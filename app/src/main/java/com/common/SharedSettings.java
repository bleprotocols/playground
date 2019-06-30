package com.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.bluetooth.BluetoothDevice;
import com.gui.BluetoothDeviceList;
import com.rpc.Reflection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SharedSettings {
    public static void saveBluetoothDeviceList(Context context, BluetoothDeviceList deviceList) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("deviceList", Reflection.objectToString(deviceList));
        editor.apply();
    }

    public static BluetoothDeviceList getBluetoothDeviceList(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String obj = preferences.getString("deviceList", null);
        return Reflection.stringToType(obj, new BluetoothDeviceList());

    }

    public static void saveWebsiteURL(Context context, String websiteurl) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("websiteurl", websiteurl).apply();
    }

    public static String getWebsiteURL(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("websiteurl", "");
    }


}
