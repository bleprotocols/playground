package com.ble;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.widget.Toast;

import java.util.function.Consumer;


/**
 * Class that wraps functionality BLE device scanning.
 */
public class GattDeviceScanner extends ScanCallback {
    private Context context;
    private BluetoothLeScanner bluetoothleScanner;
    private Consumer<ScanResult> deviceFoundFunction;

    public GattDeviceScanner(Consumer<ScanResult> deviceFoundFunction, Context context) {
        this.context = context;
        this.deviceFoundFunction = deviceFoundFunction;
    }

    public void stop() {
        bluetoothleScanner.stopScan(this);
    }

    public void scan() {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

        if (null == mBluetoothAdapter) {
            Toast.makeText(context, "Bluetooth not supported.", Toast.LENGTH_SHORT).show();
            return;
        }

        bluetoothleScanner = mBluetoothAdapter.getBluetoothLeScanner();
        bluetoothleScanner.startScan(this);
    }


    public void onScanResult(int callbackType, ScanResult result) {
        processResult(result);
    }

    private void processResult(ScanResult result) {
        deviceFoundFunction.accept(result);
    }
}
