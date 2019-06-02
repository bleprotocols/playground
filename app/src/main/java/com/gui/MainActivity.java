package com.gui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ble.GattDeviceScanner;
import com.web.WebController;
import com.example.visionbodyremote.R;
import com.web.WebControllerFactory;

import org.jsoup.Jsoup;

import java.util.List;
import java.util.stream.IntStream;

import static com.common.Common.wrap;

public class MainActivity extends AppCompatActivity {
    private TextView urlView;
    private ListView listview;
    private BluetoothDeviceList bluetoothDeviceList;
    private GattDeviceScanner gattDeviceScanner;
    private SharedPreferences preferences;

    private List<WebController> webControllerList = WebControllerFactory.all();

    private void onDeviceFound(ScanResult x) {
        if (x.getDevice() != null && x.getDevice().getName() != null) {
            WebController controller = webControllerList.stream().filter(c -> c.isDevice(x.getDevice())).findAny().orElse(null);

            if (controller != null) {
                bluetoothDeviceList.add(new BluetoothDeviceListItem(controller.getTypeName(), x.getDevice().getAddress(), x.getDevice().getName()));
            }
        }
    }

    public void scanForDevices(View view) {

        gattDeviceScanner.scan();

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (null == adapter) {
            return;
        }
        for (BluetoothDevice device : adapter.getBondedDevices()) {

            WebController controller = webControllerList.stream().filter(c -> c.isDevice(device)).findAny().orElse(null);
            if (controller != null) {
                bluetoothDeviceList.add(new BluetoothDeviceListItem(controller.getTypeName(), device.getAddress(), device.getName()));
            }
        }

        new Handler().postDelayed(wrap(() -> this.gattDeviceScanner.stop()), 15000);
    }


    public void requestPermission(String permission) {
        if (this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission}, 1);
        }
    }

    public boolean checkPermissions() {
        if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth low-energy not supported", Toast.LENGTH_SHORT).show();
            return false;
        }
        requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        requestPermission(Manifest.permission.BLUETOOTH);
        requestPermission(Manifest.permission.BLUETOOTH_ADMIN);
        requestPermission(Manifest.permission.INTERNET);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        return true;
    }

    public boolean checkWebsite(String website) {
        String connectionPhp = website + "/visionbody.php?session=testtest";
        String sessionPhp = website + "/get_session.php?session=testtest";

        try {
            if (Jsoup.connect(connectionPhp).get().body() == null) {
                throw new RuntimeException();
            }
            if (Jsoup.connect(sessionPhp).get().body() == null) {
                throw new RuntimeException();
            }
            return true;
        } catch (Exception ex) {
            System.out.println("Test");
        }

        return false;
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("url", urlView.getText().toString());
        editor.apply();
    }

    public void scanClick(View view) {
        SparseBooleanArray checkedItems = this.listview.getCheckedItemPositions();

        if (checkedItems.size() == 0) {
            Toast.makeText(this, "Please select at least one device to connect to.", Toast.LENGTH_SHORT).show();
            return;
        }


        if (gattDeviceScanner != null) {
            gattDeviceScanner.stop();
        }

        String urlText = this.urlView.getText().toString();

        new Thread(wrap(() -> {
            if (!checkWebsite(urlText)) {
                this.runOnUiThread(wrap(() -> Toast.makeText(this, "Failed to connect to webserver.", Toast.LENGTH_SHORT).show()));
                return;
            }

            runOnUiThread(wrap(this::saveSettings));

            Intent intent = new Intent(this, SessionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Object selectedDevices[] = IntStream.range(0, this.bluetoothDeviceList.size())
                    .filter(x -> checkedItems.get(x))
                    .mapToObj(x -> this.bluetoothDeviceList.get((int) x))
                    .toArray();

            intent.putExtra("devices", selectedDevices);
            intent.putExtra("url", urlText);
            startActivity(intent);
            this.finish();
        })).start();
    }


    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("url", urlView.getText().toString());
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String url = savedInstanceState.getString("url");
        this.urlView.setText(url);
    }


    //retrieve user settings.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("Test:" + ((byte) -86 ^ (byte) 65));
        setContentView(R.layout.activity_main);
        this.urlView = findViewById(R.id.websiteUrl);

        listview = (ListView) findViewById(R.id.listView);
        bluetoothDeviceList = new BluetoothDeviceList(this);
        listview.setAdapter(bluetoothDeviceList.adapter());
        gattDeviceScanner = new GattDeviceScanner(this::onDeviceFound, this.getBaseContext());

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.urlView.setText(preferences.getString("url", ""));
        this.checkPermissions();
        this.scanForDevices(null);
    }
}
