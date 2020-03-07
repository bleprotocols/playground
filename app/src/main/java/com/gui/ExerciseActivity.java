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
import com.games.exercise.ExerciseGameController;
import com.games.meditation.MeditationGameController;
import com.session.SessionService;

public class ExerciseActivity extends AppCompatActivity {
    private TextView editView;
    private String sessionText;

    protected void registerReciever() {
        BroadcastReceiver consoleLogReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                editView.append(intent.getStringExtra("extra"));
            }
        };


        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(ExerciseActivity.this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("devlog");
        manager.registerReceiver(consoleLogReciever, filter);
    }


    public void startClick(View view) {
        SessionService.getInstance(this).addActionTask("exercise",new TextToSpeechController());
        SessionService.getInstance(this).addActionTask("exercise",new GyroscopeController());
        SessionService.getInstance(this).addActionTask("exercise",new ExerciseGameController());
    }


    public void onBackPressed() {
        SessionService.getInstance(this).removeActionTask("exercise");
        this.startActivity(new Intent(ExerciseActivity.this, MainActivity.class));
        this.finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exercise);
        this.editView = findViewById(R.id.editText);

        this.registerReciever();

    }
}
