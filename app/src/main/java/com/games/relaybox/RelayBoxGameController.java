package com.games.relaybox;

import android.content.Context;

import com.bluetooth.Controller;
import com.devices.relay.RelayInterface;
import com.devices.textToSpeech.TextToSpeechInterface;
import com.rpc.RpcFunction;
import com.rpc.RpcIntentHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.common.Common.doUntilTrue;
import static com.common.Common.sleep;
import static com.common.Common.wrap;

public class RelayBoxGameController implements Controller {
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private RpcIntentHandler intentHandler = new RpcIntentHandler<>(RelayBoxGameController.class, this);

    private Context context;

    boolean isConnected = false;

    void refreshValues() {
        //  Et312BInterface.setPower(context, rewardLevel, punishmentLevel);
    }

    @Override
    public void startControlling() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        intentHandler.registerHandler(context, "relaybox");

        scheduler.schedule(wrap(this::main), 10, TimeUnit.SECONDS);
    }

    public void main() {
        TextToSpeechInterface.speak(context, "Welcome to the relay box module. Please enable your relay box.");
        while (!isConnected) {
            RelayInterface.testConnection(context);
            sleep(5000);
        }

        TextToSpeechInterface.speak(context, "Relay box connected. Relay 0 on for 30 seconds.");
        doUntilTrue(this, x -> x.openRelay(), 30000);
        RelayInterface.setRelay(context, 0, false);
        TextToSpeechInterface.speak(context, "Delay of 90 seconds.");
        sleep(90000);
        RelayInterface.setRelay(context, 2, true);
        sleep(15000);
        RelayInterface.setRelay(context, 2, false);
        TextToSpeechInterface.speak(context, "Relay 0 on for random time.");
        RelayInterface.setRelay(context, 0, true);
        doUntilTrue(this, x -> x.openRelay(), (long) ((60000 * 6) + ((7 * 60000) * Math.random())));

        RelayInterface.setRelay(context, 0, false);
        TextToSpeechInterface.speak(context, "Relays disabled.");
    }

    public boolean openRelay() {
        if ((int) (Math.random() * 50) == 5) {
            RelayInterface.setRelay(context, 2, true);
            sleep(3500);
            RelayInterface.setRelay(context, 2, false);
        }

        RelayInterface.setRelay(context, 0, true);
        sleep(1000);
        return false;
    }

    @RpcFunction
    public void onConnected() {
        isConnected = true;
    }


    @Override
    public void stopControlling() {
        intentHandler.unregisterHandler(context);
        this.scheduler.shutdown();
    }

    @Override
    public String getTypeName() {
        return "Relay box game";
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
}
