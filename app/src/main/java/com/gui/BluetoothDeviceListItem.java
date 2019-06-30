package com.gui;

import java.io.Serializable;

public class BluetoothDeviceListItem implements Serializable {
    private String address;
    private String name;
    private String controllerName;
    private boolean isEnabled;

    public String getAddress() {
        return address;
    }

    public String getControllerName() {
        return controllerName;
    }

    public BluetoothDeviceListItem(String controllerName, String address, String name) {
        this.address = address;
        this.name = name;
        this.controllerName = controllerName;
    }


    public void setEnabled(boolean enabled){
        this.isEnabled = enabled;
    }

    public boolean getEnabled(){
        return this.isEnabled;
    }


    @Override
    public String toString() {
        return name;
    }

}
