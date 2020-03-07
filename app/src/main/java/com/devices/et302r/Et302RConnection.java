package com.devices.et302r;

import android.bluetooth.BluetoothDevice;

import com.ble.GattDeviceConnection;
import com.common.Common;

import static com.common.Common.sleep;

public abstract class Et302RConnection extends GattDeviceConnection {

    public Et302RConnection() {
        this.setServiceUUID(ET302RUUID.SERIAL_SERVICE_UUID)
                .setRxUUID(ET302RUUID.RTX_UUID)
                .setTxUUID(ET302RUUID.RTX_UUID);
    }

    @Override
    public boolean isDevice(BluetoothDevice result) {
        return result != null && result.getName().startsWith("ET302R");
    }

    protected synchronized void sendCommand(byte button, int duration) {
        byte command[] = {(byte) 0xFF, button, (byte) (duration >> 8), (byte) (duration & 0xff), (byte) 0xFF};
        byte response[] = {(byte) 0xFE, button, (byte) (duration >> 8), (byte) (duration & 0xff), (byte) 0xFE};
        this.write(command);
    }
}
