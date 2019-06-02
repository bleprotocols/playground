package com.devices.lovesense;

import android.bluetooth.BluetoothDevice;


public class LovesenseController extends LovesenseConnection implements com.bluetooth.BluetoothDevice {
    public void setIntensity(int intensity) {
        if (intensity < 0 || intensity > 20) {
            getLogger().println("LovesenseController:setIntensity(" + intensity + "): invalid intensity. ");
        }

        getLogger().println("LovesenseController:setIntensity(" + intensity + "): setting intensity. ");

        sendCommand("Vibrate:" + intensity + ";");
    }

    @Override
    public boolean isDevice(BluetoothDevice result) {
        return result != null && result.getName().startsWith("LVS-");
    }

}
