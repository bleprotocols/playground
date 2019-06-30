package com.gui;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.common.PermissionHelper;
import com.common.SharedSettings;
import com.session.SessionService;
import com.example.visionbodyremote.R;

import org.jsoup.Jsoup;

import java.util.stream.IntStream;

import static com.common.Common.*;

public class MainActivity extends AppCompatActivity {
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

    public void remoteControlClick(View view) {
        PermissionHelper.checkPermissions(this);

        BluetoothDeviceList list = SharedSettings.getBluetoothDeviceList(this);
        String websiteURL = SharedSettings.getWebsiteURL(this);

        new Thread(wrap(() -> {
            if (!checkWebsite(websiteURL)) {
                this.runOnUiThread(wrap(() -> Toast.makeText(this, "Failed to connect to webserver.", Toast.LENGTH_SHORT).show()));
                return;
            }


            Intent intent = new Intent(this, SessionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


            Object selectedDevices[] = IntStream.range(0, list.size())
                    .filter(x -> list.get(x).getEnabled())
                    .mapToObj(x -> list.get(x))
                    .toArray();

            intent.putExtra("devices", selectedDevices);
            intent.putExtra("url", websiteURL);
            startActivity(intent);
            this.finish();
        })).start();
    }


    public void settingsClick(View view) {
        navigateToActivity(this,ConfigureParametersActivity.class);
    }

    public void meditationClick(View view) {
        navigateToActivity(this,MeditationActivity.class);
    }


    public void configureDevicesClick(View view) {
        navigateToActivity(this,ConfigureDevicesActivity.class);
    }

    //retrieve user settings.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        new Thread(wrap(() -> SessionService.getInstance(this).refreshDevices())).start();

/*
    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 */

    }
}
