package com.devices.visionbody;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.bluetooth.Controller;
import com.devices.lovesense.LovesenseController;
import com.rpc.RpcFunction;
import com.rpc.RpcIntentHandler;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.common.Common.intStream;
import static com.common.Common.printByteArray;
import static com.common.Common.sleep;
import static com.common.Common.wrap;
import static com.common.CommonConstants.EMPTY_ARRAY;
import static com.devices.visionbody.VisionBodyConstants.INTENSITY_STEP_COUNT_MAX;
import static com.devices.visionbody.VisionBodyConstants.INTENSITY_STEP_INTERVAL;
import static com.devices.visionbody.VisionBodyConstants.bodyParts;
import static com.devices.visionbody.VisionBodyConstants.feels;
import static com.devices.visionbody.VisionBodyConstants.programs;
import static com.devices.visionbody.VisionBodyEventType.BUFFER_OVERFLOW_EVENT;
import static com.devices.visionbody.VisionBodyEventType.HIGH_RESISTANCE_BODY_PART_EVENT;
import static com.devices.visionbody.VisionBodyEventType.PANIC_BUTTON_EVENT;
import static com.devices.visionbody.VisionBodyMessageType.BEGIN_IMAGE_TRANSFER_COMMAND;
import static com.devices.visionbody.VisionBodyMessageType.END_IMAGE_TRANSFER_COMMAND;
import static com.devices.visionbody.VisionBodyMessageType.GET_BATTERY_LEVEL_COMMAND;
import static com.devices.visionbody.VisionBodyMessageType.GET_FIRMWARE_VERSION_COMMAND;
import static com.devices.visionbody.VisionBodyMessageType.GET_INTENSITY_COMMAND;
import static com.devices.visionbody.VisionBodyMessageType.IDENTIFY_COMMAND;
import static com.devices.visionbody.VisionBodyMessageType.LOAD_PROGRAM_COMMAND;
import static com.devices.visionbody.VisionBodyMessageType.LOGIN_COMMAND;
import static com.devices.visionbody.VisionBodyMessageType.SET_INTENSITY_COMMAND;
import static com.devices.visionbody.VisionBodyMessageType.START_PROGRAM_COMMAND;
import static com.devices.visionbody.VisionBodyMessageType.STOP_PROGRAM_COMMAND;
import static com.devices.visionbody.VisionBodyMessageType.SYNCHRONIZE_COMMAND;
import static com.devices.visionbody.VisionBodyMessageType.TRANSFER_IMAGE_CHUNK_RCOMMAND;
import static java.lang.Math.abs;

public class VisionBodyController extends VisionBodyConnection implements Controller {
    private byte[] intensities = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    private byte program = -1;
    private byte feel = -1;
    private byte stimduration = -1;
    private byte restduration = -1;
    private RpcIntentHandler intentHandler = new RpcIntentHandler<>(VisionBodyController.class, this);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public VisionBodyController() {
        super();
        //Schedule to read the intensities every 10 seconds to make sure we're not missing out on any settings
        scheduler.scheduleAtFixedRate(wrap(() -> getIntensity()), 1, 10, TimeUnit.SECONDS);
    }

    protected synchronized void onConnect() {
        byte[] old_intensities = intensities;
        intensities = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        byte old_program = program;
        program = -1;
        byte old_feel = feel;
        feel = -1;
        byte old_stimduration = stimduration;
        stimduration = -1;
        byte old_restduration = restduration;
        restduration = -1;


        //start out with our previous settings
        if (old_program > 0 && old_feel > 0) {
            loadProgram(programs.get(old_program), feels.get(old_feel), old_stimduration, old_restduration);
        }

        if (IntStream.rangeClosed(0, 7).anyMatch(i -> old_intensities[i] > 0)) {
            setIntensity(old_intensities[0], old_intensities[1], old_intensities[2], old_intensities[3], old_intensities[4], old_intensities[5], old_intensities[6], old_intensities[7]);
        }
    }


    @Override
    public void onDisconnect() {
        this.connect();
    }

    protected void onInvalidResponse() {
        getLogger().println("onInvalidResponse(): got invalid message from box.");
        while (this.read(1).length > 0) ;
    }

    @RpcFunction
    public synchronized boolean loadProgram(String program, String feel, byte stimDuration, byte restDuration) {
        byte programCode = programs.contains(program) ? (byte) (programs.indexOf(program)) : (byte) 0;
        byte feelCode = feels.contains(feel) ? (byte) (feels.indexOf(feel)) : (byte) 0;

        if (programCode == this.program && feelCode == this.feel && this.stimduration == stimDuration && this.restduration == restDuration) {
            return true;
        }

        getLogger().println("loadProgram(\"" + program + "\",\"" + feel + "\"," + stimDuration + "," + restDuration + "): program settings changed.");

        sendCommand(LOAD_PROGRAM_COMMAND, new byte[]{programCode, 0, feelCode, stimDuration, restDuration}, true);

        intensities = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

        this.program = programCode;
        this.feel = feelCode;
        this.stimduration = stimDuration;
        this.restduration = restDuration;

        return this.startProgram();
    }

    @RpcFunction
    public synchronized boolean setIntensity(byte upperBackIntensity, byte rearIntensity, byte legsIntesnsity, byte lowerBackIntensity, byte armsIntensity, byte neckIntensity, byte chestIntensity, byte stomachIntensity) {
        byte[] final_message = new byte[]{
                upperBackIntensity, rearIntensity, legsIntesnsity, lowerBackIntensity, armsIntensity, neckIntensity, chestIntensity, stomachIntensity
        };

        byte[] message_step = new byte[final_message.length];

        for (int i = 0; i < final_message.length; i++) {

            message_step[i] = final_message[i] > this.intensities[i] ? (byte) 1 : (byte) -1;
            message_step[i] = final_message[i] == this.intensities[i] ? 0 : message_step[i];
        }

        //check if anything changed
        if (intStream(message_step).allMatch(x -> x == 0)) {
            return true;
        }

        getLogger().println("setIntensity(" + upperBackIntensity + "," + rearIntensity + "," + legsIntesnsity + "," + lowerBackIntensity + "," + armsIntensity + "," + neckIntensity + "," + chestIntensity + "," + stomachIntensity + "): intensity changed.");

        int maxIter = IntStream.rangeClosed(0, 7)
                .map(x -> abs(final_message[x] - this.intensities[x])).max().orElseGet(() -> 0);

        //Do max X intensity steps per time
        maxIter = maxIter > INTENSITY_STEP_COUNT_MAX ? INTENSITY_STEP_COUNT_MAX : maxIter;

        for (int i = 0; i <= maxIter; i++) {

            sleep(INTENSITY_STEP_INTERVAL);
            if (sendCommand(SET_INTENSITY_COMMAND, this.intensities, true)) {
                IntStream.rangeClosed(0, 7)
                        .filter(x -> this.intensities[x] != final_message[x])
                        .forEach(x -> this.intensities[x] += message_step[x]);
            }


        }

        if (IntStream.rangeClosed(0, 7).allMatch(x -> this.intensities[x] == final_message[x])) {
            return true;
        }

        return false;
    }

    public synchronized boolean startProgram() {
        getLogger().println("startProgram(): starting program.");
        return sendCommand(START_PROGRAM_COMMAND, EMPTY_ARRAY, true);
    }

    public boolean stopProgram() {
        getLogger().println("stopProgram(): stopping program.");
        return sendCommand(STOP_PROGRAM_COMMAND, EMPTY_ARRAY, true);
    }

    public boolean getBatteryLevel() {
        getLogger().println("getBatteryLevel(): requesting battery level.");
        return sendCommand(GET_BATTERY_LEVEL_COMMAND, EMPTY_ARRAY, false);
    }

    public boolean getFirmwareVersion() {
        getLogger().println("getFirmwareVersion(): requesting firmware version.");
        return sendCommand(GET_FIRMWARE_VERSION_COMMAND, EMPTY_ARRAY, false);
    }

    public boolean getIntensity() {
        getLogger().println("getIntensity(): requesting intensities.");
        return sendCommand(GET_INTENSITY_COMMAND, EMPTY_ARRAY, false);
    }

    public boolean identify() {
        getLogger().println("identify(): requesting identify.");
        return sendCommand(IDENTIFY_COMMAND, EMPTY_ARRAY, true);
    }

    public boolean synchronize() {
        getLogger().println("synchronize(): synchronizing.");
        return sendCommand(SYNCHRONIZE_COMMAND, EMPTY_ARRAY, true);
    }

    public boolean login() {
        getLogger().println("login(): requesting key.");
        return sendCommand(LOGIN_COMMAND, EMPTY_ARRAY, false);
    }

    public boolean resetDevice() {
        getLogger().println("resetDevice(): resetting.");
        return sendCommand(Byte.MAX_VALUE, EMPTY_ARRAY, false);
    }

    @Override
    void handleEvent(int eventCode, byte[] event) {
        switch (eventCode) {
            case HIGH_RESISTANCE_BODY_PART_EVENT:
                if (event.length != 1) {
                    getLogger().println("handleEvent(HIGH_RESISTANCE_BODY_PART_EVENT," + printByteArray(event) + "): invalid data length.");
                    return;
                }

                int bit = event[0];

                for (int i = 0; i < bodyParts.size(); i++) {
                    if ((bit & 1) == 1) {
                        getLogger().println("handleEvent(HIGH_RESISTANCE_BODY_PART_EVENT," + printByteArray(event) + "): high resistance body part:" + bodyParts.get(i));
                    }
                    bit >>>= 1;
                }

                break;

            case PANIC_BUTTON_EVENT:
                if (event.length != 0) {
                    getLogger().println("handleEvent(PANIC_BUTTON_EVENT," + printByteArray(event) + "): invalid data length.");
                    return;
                }
                getLogger().println("handleEvent(PANIC_BUTTON_EVENT," + printByteArray(event) + "): recieved panic button press event.");
                break;

            case BUFFER_OVERFLOW_EVENT:
                if (event.length != 0) {
                    getLogger().println("handleEvent(BUFFER_OVERFLOW_EVENT," + printByteArray(event) + "): invalid data length.");
                    return;
                }
                getLogger().println("handleEvent(BUFFER_OVERFLOW_EVENT," + printByteArray(event) + "): recieved buffer overflow event.");
                break;
            default:
                getLogger().println("handleEvent(" + eventCode + "," + printByteArray(event) + "): unknown event.");
                break;

        }
    }


    @Override
    synchronized void handleResponse(int responseCode, byte[] response) {
        switch (responseCode) {

            case BEGIN_IMAGE_TRANSFER_COMMAND:
                if (response.length != 1) {
                    getLogger().println("handleResponse(BEGIN_IMAGE_TRANSFER," + printByteArray(response) + "): invalid response length.");
                    return;
                }

                getLogger().println("handleResponse(BEGIN_IMAGE_TRANSFER," + printByteArray(response) + "): return code: " + response[0]);
                break;

            case GET_BATTERY_LEVEL_COMMAND:
                getLogger().println("handleResponse(GET_BATTERY_LEVEL_RESPONSE," + printByteArray(response) + "): battery level: " + response[0] + "%");
                break;

            case GET_FIRMWARE_VERSION_COMMAND:
                if (response.length != 4) {
                    getLogger().println("handleResponse(GET_FIRMWARE_VERSION_RESPONSE," + printByteArray(response) + "): invalid response length.");
                    return;
                }
                getLogger().println("handleResponse(GET_FIRMWARE_VERSION_RESPONSE," + printByteArray(response) + "): firmware version: " + new String(response, StandardCharsets.US_ASCII));
                break;

            case GET_INTENSITY_COMMAND:
                if (response.length != bodyParts.size()) {
                    getLogger().println("handleResponse(GET_INTENSITY_RESPONSE," + printByteArray(response) + "): invalid response length.");
                    return;
                }

                if (IntStream.rangeClosed(0, 7)
                        .anyMatch(i -> response[i] < 0 || response[i] > 99)) {
                    getLogger().println("handleResponse(GET_INTENSITY_RESPONSE," + printByteArray(response) + "): invalid intensity.");
                    return;
                }

                this.intensities = response;
                break;
            case LOGIN_COMMAND:
                if (response.length != 32) {
                    getLogger().println("handleResponse(LOGIN_RESPONSE," + printByteArray(response) + "): invalid response length.");
                    return;
                }
                getLogger().println("handleResponse(LOGIN_RESPONSE," + printByteArray(response) + "): recieved key vector: " + printByteArray(response));
                break;

            case SET_INTENSITY_COMMAND:
                if (response.length != 1) {
                    getLogger().println("handleResponse(SET_INTENSITY_RESPONSE," + printByteArray(response) + "): invalid response length.");
                    return;
                }

                getLogger().println("handleResponse(SET_INTENSITY_RESPONSE," + printByteArray(response) + "): recieved return value: " + response[0]);
                break;

            case TRANSFER_IMAGE_CHUNK_RCOMMAND:
                if (response.length != 1) {
                    getLogger().println("handleResponse(TRANSFER_IMAGE_CHUNK_RESPONSE," + printByteArray(response) + "): invalid response length.");
                    return;
                }

                getLogger().println("handleResponse(TRANSFER_IMAGE_CHUNK_RESPONSE," + printByteArray(response) + "): recieved return value: " + response[0]);
                break;

            case END_IMAGE_TRANSFER_COMMAND:
                if (response.length != 1) {
                    getLogger().println("handleResponse(END_IMAGE_TRANSFER_RESPONSE," + printByteArray(response) + "): invalid response length.");
                    return;
                }

                getLogger().println("handleResponse(END_IMAGE_TRANSFER_RESPONSE," + printByteArray(response) + "): recieved return value: " + response[0]);
                break;
            //
            case LOAD_PROGRAM_COMMAND:
                if (response.length != 1) {
                    getLogger().println("handleResponse(LOAD_PROGRAM_RESPONSE," + printByteArray(response) + "): invalid response length.");
                    return;
                }

                getLogger().println("handleResponse(LOAD_PROGRAM_RESPONSE," + printByteArray(response) + "): recieved return value: " + response[0]);
                break;

            case START_PROGRAM_COMMAND:
                if (response.length != 1) {
                    getLogger().println("handleResponse(START_PROGRAM_RESPONSE," + printByteArray(response) + "): invalid response length.");
                    return;
                }

                getLogger().println("handleResponse(START_PROGRAM_RESPONSE," + printByteArray(response) + "): recieved return value: " + response[0]);
                break;

            default:
                getLogger().println("handleResponse(" + responseCode + "," + printByteArray(response) + "): unknown response");
                break;
        }
    }

    @Override
    void handleAck(int responseCode, byte[] response) {
    }

    @Override
    public synchronized void startControlling() {
        intentHandler.registerHandler(getContext(), "visionbody_control");
        this.connect();
    }

    @Override
    public synchronized void stopControlling() {
        intentHandler.unregisterHandler(getContext());
        this.close();
    }


    @Override
    public String getTypeName() {
        return "VisionBody";
    }

}
