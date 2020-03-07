package com.gui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ListView;

import com.ble.GattDeviceScanner;
import com.bluetooth.Controller;
import com.common.PermissionHelper;
import com.common.SharedSettings;
import com.example.visionbodyremote.R;
import com.rpc.Reflection;
import com.session.DeviceControllerFactory;
import com.session.SessionService;

import java.util.List;
import java.util.stream.IntStream;

import static com.common.Common.wrap;

public class ConfigureDevicesActivity extends AppCompatActivity {
    private ListView listview;
    private BluetoothDeviceList bluetoothDeviceList;
    private GattDeviceScanner gattDeviceScanner;

    private List<Controller> webControllerList = DeviceControllerFactory.all();


    private void onDeviceFound(ScanResult x) {
        if (x.getDevice() != null && x.getDevice().getName() != null) {

            //
            Controller controller = webControllerList.stream()
                    .filter(y -> y instanceof com.bluetooth.BluetoothDevice)
                    .filter(c -> ((com.bluetooth.BluetoothDevice) c).isDevice(x.getDevice())).findAny().orElse(null);

            if (controller != null) {
                bluetoothDeviceList.add(new BluetoothDeviceListItem(controller.getTypeName(), x.getDevice().getAddress(), x.getDevice().getName()));
            }
        }
    }

    public void scanForDevices(View view) {
        PermissionHelper.checkPermissions(this);

        gattDeviceScanner.scan();

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (null == adapter) {
            return;
        }
        for (BluetoothDevice device : adapter.getBondedDevices()) {

            Controller controller = webControllerList.stream()
                    .filter(y -> y instanceof com.bluetooth.BluetoothDevice)
                    .filter(c -> ((com.bluetooth.BluetoothDevice) c).isDevice(device)).findAny().orElse(null);

            if (controller != null) {
                bluetoothDeviceList.add(new BluetoothDeviceListItem(controller.getTypeName(), device.getAddress(), device.getName()));
            }
        }

        new Handler().postDelayed(wrap(() -> this.gattDeviceScanner.stop()), 15000);
    }

    public void clearDevices(View view) {
        bluetoothDeviceList.clear();
    }

    public void saveToSettings() {
        SparseBooleanArray checkedItems = this.listview.getCheckedItemPositions();
        IntStream.range(0, this.bluetoothDeviceList.size())
                .forEach(x -> this.bluetoothDeviceList.get(x).setEnabled(checkedItems.get(x)));

        SharedSettings.saveBluetoothDeviceList(this, this.bluetoothDeviceList);
        new Thread(wrap(() -> SessionService.getInstance(this).refreshDevices())).start();
    }

    public void bindNewDeviceList() {
        bluetoothDeviceList.setContext(this);
        listview.setAdapter(bluetoothDeviceList.adapter());

        for (int i = 0; i < bluetoothDeviceList.size(); i++) {
            listview.setItemChecked(i, bluetoothDeviceList.get(i).getEnabled());
        }
    }

    public void loadFromSettings() {
        bluetoothDeviceList = SharedSettings.getBluetoothDeviceList(this);
        bindNewDeviceList();
        SessionService.getInstance(this).clearDevices();
    }


    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("deviceList", Reflection.objectToString(bluetoothDeviceList));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String deviceList = savedInstanceState.getString("deviceList");

        if (deviceList == null) {
            this.bluetoothDeviceList = new BluetoothDeviceList().setContext(this);
        }

        bluetoothDeviceList = Reflection.stringToType(deviceList, new BluetoothDeviceList());
        bindNewDeviceList();
    }


    public void onBackPressed() {
        saveToSettings();
        this.startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    //retrieve user settings.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configuredevices);
        listview = findViewById(R.id.listView);

        gattDeviceScanner = new GattDeviceScanner(this::onDeviceFound, this.getBaseContext());
        loadFromSettings();

    }
}
