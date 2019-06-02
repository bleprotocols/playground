package com.web;

import com.bluetooth.BluetoothDevice;

public interface WebController extends BluetoothDevice {
    String getTypeName();
    String getControlURL(String sessionKey);
    boolean acceptCommand(String command);
}
