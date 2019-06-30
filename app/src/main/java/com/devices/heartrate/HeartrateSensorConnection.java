package com.devices.heartrate;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.ble.GattDeviceConnection;
import com.rpc.RpcFunctionCaller;

import static com.devices.heartrate.HeartrateUUID.HEARTRATE_RX_SERVICE;
import static com.devices.heartrate.HeartrateUUID.HEARTRATE_SERVICE;

public abstract class HeartrateSensorConnection extends GattDeviceConnection implements com.bluetooth.BluetoothDevice {
    public HeartrateSensorConnection() {
        super();
        this.setServiceUUID(HEARTRATE_SERVICE)
                .setRxUUID(HEARTRATE_RX_SERVICE);
    }

    //Broadcast a onHeartrate event
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic c) {
        RpcFunctionCaller.callMethod(getContext(),"heartrate","onHeartrate",extractHeartrate(c));
    }


    private static int extractHeartrate(BluetoothGattCharacteristic characteristic) {
        int heartrate=0;

        if ((characteristic.getProperties() & 0x01) != 0) {
            heartrate= characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
        } else {
            heartrate= characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
        }


        return heartrate;
    }

    @Override
    public void onDisconnect() {
        this.connect();
        RpcFunctionCaller.callMethod(getContext(),"heartrate","onHeartrateDisconnected");
    }

    @Override
    public boolean isDevice(BluetoothDevice result) {
        return result != null && result.getName().startsWith("HRM_Sensor");
    }

    @Override
    protected void onConnect() {
        this.setSubscribeToRxChannel();
        RpcFunctionCaller.callMethod(getContext(),"heartrate","onHeartrateConnected");

    }

    //
    //UUID
}
