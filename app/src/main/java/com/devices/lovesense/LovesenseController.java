package com.devices.lovesense;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.bluetooth.Controller;
import com.devices.lock.LockController;
import com.rpc.RpcFunction;
import com.rpc.RpcIntentHandler;


public class LovesenseController extends LovesenseConnection implements Controller {
    private RpcIntentHandler intentHandler = new RpcIntentHandler<>(LovesenseController.class, this);
    int intensity = 0;

    @Override
    protected void onConnect() {
        setIntensity(intensity);
    }

    @RpcFunction
    public synchronized void setIntensity(int intensity) {
        if (intensity < 0 || intensity > 20) {
            getLogger().println("LovesenseController:setIntensity(" + intensity + "): invalid intensity. ");
        }

        if (intensity == this.intensity) {
            return;
        }

        getLogger().println("LovesenseController:setIntensity(" + intensity + "): setting intensity. ");

        sendCommand("Vibrate:" + intensity + ";");
        this.intensity = intensity;
    }

    @Override
    public synchronized void startControlling() {
        intentHandler.registerHandler(getContext(), "lovesense_control");
        this.connect();
    }

    @Override
    public synchronized void stopControlling() {
        intentHandler.unregisterHandler(getContext());
    }

    @Override
    public String getTypeName() {
        return "LoveSense";
    }

}
