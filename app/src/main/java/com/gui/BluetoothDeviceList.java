package com.gui;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashSet;

public class BluetoothDeviceList {
    private ArrayList<BluetoothDeviceListItem> gattDeviceList = new ArrayList<>();
    private HashSet<String> deviceAddressSet = new HashSet<>();
    private ArrayAdapter<BluetoothDeviceListItem> adapter;
    private Context context;

    public BluetoothDeviceList(Context context) {
        this.context = context;
    }

    public BluetoothDeviceListItem get(int index) {
        return gattDeviceList.get(index);
    }

    public void clear() {
        deviceAddressSet.clear();
        gattDeviceList.clear();
    }

    public boolean add(BluetoothDeviceListItem bluetoothDeviceListItem) {
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


    public ArrayAdapter<BluetoothDeviceListItem> adapter() {
        if (null == adapter) {
            adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, gattDeviceList);
        }

        return adapter;
    }

    public int size() {
        return gattDeviceList.size();
    }
}
