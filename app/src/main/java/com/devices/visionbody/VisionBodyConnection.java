package com.devices.visionbody;

import android.bluetooth.BluetoothDevice;

import com.ble.GattDeviceConnection;

import static com.common.Common.*;
import static com.devices.visionbody.VisionBodyConstants.*;
import static com.devices.visionbody.VisionBodyMessageKind.*;


public abstract class VisionBodyConnection extends GattDeviceConnection {

    public VisionBodyConnection() {
        super();
        this.setServiceUUID(VisionBodyUUID.VISION_BODY_BOX_SERVICE)
                .setTxUUID(VisionBodyUUID.VISION_BODY_TX_CHANNEL)
                .setRxUUID(VisionBodyUUID.VISION_BODY_RX_CHANNEL)
                .setAutoReconnect(false)
        .setConnectionTimeout(5000)
        .setTxRxTimeout(2000);
    }

    public boolean sendCommand(byte commandNo, byte[] body, boolean isAck) {
        byte[] message = new byte[body.length + MESSAGE_HEADER_LENGTH];

        if (body.length > MAX_MESSAGE_LENGTH && body.length != TRANSFER_IMAGE_CHUNK_MESSAGE_LENGTH) {
            throw new RuntimeException("Command payload has to be 130 bytes or less in length, or it has to be an image transfer command");
        }

        message[0] = commandNo;
        message[1] = MESSAGE_KIND_CMD;
        message[2] = Byte.MAX_VALUE;
        message[3] = body.length == TRANSFER_IMAGE_CHUNK_MESSAGE_LENGTH ? -1 : (byte) body.length;

        System.arraycopy(body, 0, message, 4, body.length);

        for (int i = 0; i < MESSAGE_RETRIES; i++) {
            if (this.write(message)) {
                if (isAck) {
                    if (this.tryReadResponse(MESSAGE_KIND_ACK, commandNo)) {
                        return true;
                    }
                } else {
                    if (this.tryReadResponse(MESSAGE_KIND_RSP, commandNo)) {
                        return true;
                    }
                }
            }
            sleep(10);
        }
        return false;
    }

    protected abstract void onInvalidResponse();

    private boolean tryReadResponse(int responseKind, int responseNumber) {
        byte[] responseHeader = this.read(MESSAGE_HEADER_LENGTH);


        //if it begins with 0 - just drop the first byte
        if (responseHeader.length > 0 && responseHeader[0] == 0) {
            responseHeader[0] = responseHeader[1];
            responseHeader[1] = responseHeader[2];
            responseHeader[2] = responseHeader[3];
            byte[] nextByte = this.read(1);
            if (nextByte.length > 0) {
                responseHeader[3] = nextByte[0];
            }
        }

        if (responseHeader.length == 0) {
            return false;
        }

        if (responseHeader[1] < 0 || responseHeader[1] > 3) {
            getLogger().println("tryReadResponse(): read invalid header, invalid response type: " + printByteArray(responseHeader));
            this.onInvalidResponse();
            return false;
        }

        if (responseHeader[3] < 0 || responseHeader[3] > MAX_RESPONSE_LENGTH) {
            getLogger().println("tryReadResponse(): read invalid header, invalid response length: " + printByteArray(responseHeader));
            this.onInvalidResponse();
            return false;
        }

        //Handle events
        if (responseHeader[1] == MESSAGE_KIND_EVT) {
            this.handleEvent(responseHeader[0], this.read(responseHeader[3]));
        }

        //Handle responses
        if (responseHeader[1] == MESSAGE_KIND_RSP) {
            this.handleResponse(responseHeader[0], this.read(responseHeader[3]));
        }

        //Handle responses
        if (responseHeader[1] == MESSAGE_KIND_ACK) {
            this.handleAck(responseHeader[0], this.read(responseHeader[3]));
        }
        //Did we recieve the message we want to recieve?
        return (responseHeader[1] == responseKind && responseHeader[0] == responseNumber);
    }


    abstract void handleEvent(int eventCode, byte[] event);

    abstract void handleResponse(int responseCode, byte[] response);

    abstract void handleAck(int responseCode, byte[] response);


    @Override
    public boolean isDevice(BluetoothDevice result) {
        return result.getName().startsWith("AAA-VB-02");
    }
}
