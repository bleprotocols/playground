package com.bluetooth;

import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.ble.GattDeviceConnection;

import java.io.PrintStream;

public interface BluetoothDevice {
    boolean isDevice(android.bluetooth.BluetoothDevice result);

    void setLogger(PrintStream logger);

    void setContext(Context context);

    void setaddress(String deviceAddress);

}
