package com.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.devices.gyroscope.GyroscopeController;
import com.devices.textToSpeech.TextToSpeechController;
import com.example.visionbodyremote.R;
import com.games.relaybox.RelayBoxGameController;
import com.session.SessionService;

public class RelayActivity extends AppCompatActivity {
    private TextView editView;
    private String sessionText;

    protected void registerReciever() {
        BroadcastReceiver consoleLogReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                editView.append(intent.getStringExtra("extra"));
            }
        };


        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(RelayActivity.this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("devlog");
        manager.registerReceiver(consoleLogReciever, filter);
    }


    public void startClick(View view) {
        SessionService.getInstance(this).addActionTask("relay", new TextToSpeechController());
        SessionService.getInstance(this).addActionTask("relay", new GyroscopeController());
        SessionService.getInstance(this).addActionTask("relay", new RelayBoxGameController());
    }


    public void onBackPressed() {
        SessionService.getInstance(this).removeActionTask("relay");
        this.startActivity(new Intent(RelayActivity.this, MainActivity.class));
        this.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_relay);
        this.editView = findViewById(R.id.editText);

        this.registerReciever();

    }
}
