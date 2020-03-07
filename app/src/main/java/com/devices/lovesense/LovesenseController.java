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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.common.Common.wrap;


public class LovesenseController extends LovesenseConnection implements Controller {
    private RpcIntentHandler intentHandler = new RpcIntentHandler<>(LovesenseController.class, this);
    int intensity = 0;
    private  ScheduledExecutorService scheduler;

    @Override
    protected void onConnect() {
        setIntensity(intensity);
    }

    @RpcFunction
    public synchronized void setIntensity(int intensity) {
        if (intensity < 0 || intensity > 20) {
            getLogger().println("LovesenseController:setIntensity(" + intensity + "): invalid intensity. ");
        }

        this.intensity=intensity;
    }

    public void syncIntensity(){
        sendCommand("Vibrate:" + intensity + ";");
    }

    @Override
    public synchronized void startControlling() {
        intentHandler.registerHandler(getContext(), "lovesense_control");
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(wrap(() -> syncIntensity()), 1, 1, TimeUnit.SECONDS);

        this.connect();
    }

    @Override
    public synchronized void stopControlling() {
        intentHandler.unregisterHandler(getContext());
        scheduler.shutdownNow();
    }

    @Override
    public String getTypeName() {
        return "LoveSense";
    }

}
