package com.devices.relay;

import com.bluetooth.Controller;
import com.devices.lovesense.LovesenseController;
import com.rpc.RpcFunction;
import com.rpc.RpcFunctionCaller;
import com.rpc.RpcIntentHandler;

public class RelayController extends RelayConnection implements Controller {
    private RpcIntentHandler intentHandler = new RpcIntentHandler<>(RelayController.class, this);


    @Override
    public void startControlling() {
        this.connect();
        intentHandler.registerHandler(getContext(), "relay_control");

    }

    @Override
    public void stopControlling() {
        intentHandler.unregisterHandler(getContext());
    }

    @Override
    public String getTypeName() {
        return "BluetoothRelay";
    }

    @RpcFunction
    public synchronized void testConnection() {
        if ( this.connect()) {
            RpcFunctionCaller.callMethod(getContext(), "relaybox", "onConnected");
        }
    }

    @RpcFunction
    public synchronized boolean setRelay(int relay, boolean on) {
        switch (relay) {
            case 0:
                return sendCommand(on ? (byte) 4 : (byte) 6);
            case 1:
                return sendCommand(on ? (byte) 5 : (byte) 7);
            case 2:
                return sendCommand(on ? (byte) 49 : (byte) 50);
            case 3:
                return sendCommand(on ? (byte) 51 : (byte) 52);
            case 4:
                return sendCommand(on ? (byte) 53 : (byte) 54);
            case 5:
                return sendCommand(on ? (byte) 55 : (byte) 56);
            case 6:
                return sendCommand(on ? (byte) 57 : (byte) 58);
            case 7:
                return sendCommand(on ? (byte) 59 : (byte) 60);
        }
        return false;
    }
}
