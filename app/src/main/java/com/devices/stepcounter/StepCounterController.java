package com.devices.stepcounter;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.bluetooth.Controller;
import com.rpc.RpcFunctionCaller;

public class StepCounterController implements Controller, SensorEventListener {
    private Context context;

    @Override
    public void startControlling() {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void stopControlling() {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        RpcFunctionCaller.callMethod(context, "step", "step");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

