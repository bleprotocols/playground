package com.common;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class PermissionHelper {


    private static void requestPermission(Activity context, String permission) {
        if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{permission}, 1);
        }
    }

    public static boolean checkPermissions(Activity context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "Bluetooth low-energy not supported", Toast.LENGTH_SHORT).show();
            return false;
        }
        requestPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        requestPermission(context, Manifest.permission.BLUETOOTH);
        requestPermission(context, Manifest.permission.BLUETOOTH_ADMIN);
        requestPermission(context, Manifest.permission.INTERNET);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivityForResult(enableBluetooth, 0);
        }

        return true;
    }

}
