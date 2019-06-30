package com.gui;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Stream;

public class BluetoothDeviceList implements Serializable{
    private ArrayList<BluetoothDeviceListItem> gattDeviceList = new ArrayList<>();
    private HashSet<String> deviceAddressSet = new HashSet<>();
    private transient ArrayAdapter<BluetoothDeviceListItem> adapter;
    private transient Context context; // field is not serialized

    public BluetoothDeviceListItem get(int index) {
        return gattDeviceList.get(index);
    }

    public BluetoothDeviceList setContext(Context context) {
        this.context = context;
        return this;
    }

    public synchronized void clear() {
        deviceAddressSet.clear();
        gattDeviceList.clear();
    }

    public synchronized boolean add(BluetoothDeviceListItem bluetoothDeviceListItem) {
        if (!deviceAddressSet.contains(bluetoothDeviceListItem.getAddress())) {
            gattDeviceList.add(bluetoothDeviceListItem);
            deviceAddressSet.add(bluetoothDeviceListItem.getAddress());
            if (null != adapter) {
                adapter.notifyDataSetChanged();
            }
            return true;
        }
        return false;
    }


    public synchronized ArrayAdapter<BluetoothDeviceListItem> adapter() {
        if (null == adapter) {
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, gattDeviceList);
        }

        return adapter;
    }

    public Stream<BluetoothDeviceListItem> stream(){
        return gattDeviceList.stream();
    }

    public int size() {
        return gattDeviceList.size();
    }

}
