package com.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.common.Common;
import com.common.SharedSettings;
import com.devices.accelerometer.AccelerometerController;
import com.devices.gyroscope.GyroscopeController;
import com.devices.lock.LockController;
import com.devices.textToSpeech.TextToSpeechController;
import com.example.visionbodyremote.R;
import com.games.meditation.MeditationGameController;
import com.session.SessionService;
import com.web.HTTPControllerFactory;

import java.util.Objects;
import java.util.stream.Collectors;

public class MeditationActivity extends AppCompatActivity {
    private TextView editView;
    private String sessionText;

    protected void registerReciever() {
        BroadcastReceiver consoleLogReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                editView.append(intent.getStringExtra("extra"));
            }
        };


        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(MeditationActivity.this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("devlog");
        manager.registerReceiver(consoleLogReciever, filter);
    }


    public void startClick(View view) {
        SessionService.getInstance(this).addActionTask("meditation",new TextToSpeechController());
        SessionService.getInstance(this).addActionTask("meditation",new GyroscopeController());
        SessionService.getInstance(this).addActionTask("meditation",new MeditationGameController());
    }


    public void onBackPressed() {
        SessionService.getInstance(this).removeActionTask("meditation");
        this.startActivity(new Intent(MeditationActivity.this, MainActivity.class));
        this.finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meditation);
        this.editView = findViewById(R.id.editText);

        this.registerReciever();

    }
}
