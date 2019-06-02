package com.gui;

import android.os.Parcel;
import android.os.Parcelable;

public class BluetoothDeviceListItem implements Parcelable {
    private String address;
    private String name;
    private String controllerName;

    public static final Creator<BluetoothDeviceListItem> CREATOR = new Creator<BluetoothDeviceListItem>() {
        @Override
        public BluetoothDeviceListItem createFromParcel(Parcel in) {
            return new BluetoothDeviceListItem(in);
        }

        @Override
        public BluetoothDeviceListItem[] newArray(int size) {
            return new BluetoothDeviceListItem[size];
        }
    };


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

    private BluetoothDeviceListItem(Parcel in) {
        address = in.readString();
        name = in.readString();
        controllerName = in.readString();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(name);
        dest.writeString(controllerName);
    }
}
