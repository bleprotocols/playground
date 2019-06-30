package com.devices.et302r;

import com.bluetooth.Controller;
import com.devices.lovesense.LovesenseController;
import com.rpc.RpcFunction;
import com.rpc.RpcIntentHandler;

import static com.common.Common.sleep;

public class ET302RController extends Et302RConnection implements Controller {


    private RpcIntentHandler intentHandler = new RpcIntentHandler<>(ET302RController.class, this);

    @Override
    protected void onConnect() {
    }

    @Override
    protected void onDisconnect() {

    }


    @RpcFunction
    public synchronized void sendButton(int button, int duration) {
        sendCommand((byte) button, duration);
    }


    @Override
    public synchronized void startControlling() {
        intentHandler.registerHandler(getContext(), "etremote_control");
        this.connect();

    }

    @Override
    public synchronized void stopControlling() {
        intentHandler.unregisterHandler(getContext());
    }

    @Override
    public String getTypeName() {
        return "ET302R";
    }

    //When connected and sent an update, just send it directly to the device, it'll just match it's state to the commands'

}
