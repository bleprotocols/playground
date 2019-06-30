package com.devices.heartrate;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.bluetooth.Controller;
import com.devices.lovesense.LovesenseConnection;

import static android.content.ContentValues.TAG;

//update: heartrate and time when tured off
//update when put on
//update log with value every 10 minutes
public class HeartrateSensorController extends HeartrateSensorConnection implements Controller {


    @Override
    public void startControlling() {
        this.connect();
    }

    @Override
    public void stopControlling() {
        this.close();
    }

    @Override
    public String getTypeName() {
        return "HeartrateSensor";
    }


}
