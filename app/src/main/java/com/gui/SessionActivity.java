package com.gui;

import android.app.ActivityManager;
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

import com.example.visionbodyremote.R;
import com.session.SessionService;

public class SessionActivity extends AppCompatActivity {
    private TextView editView;
    private TextView sessionLinkView;

    protected void registerReciever() {
        BroadcastReceiver consoleLogReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                editView.append(intent.getStringExtra("extra"));
            }
        };

        BroadcastReceiver sessionTextReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String link="<html><a href=\""+intent.getStringExtra("extra")+"\">Session link</a></html>";

                sessionLinkView.setText(Html.fromHtml(link));

                sessionLinkView.setMovementMethod(LinkMovementMethod.getInstance());
            }
        };

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(SessionActivity.this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("devlog");
        manager.registerReceiver(consoleLogReciever, filter);

        filter = new IntentFilter();
        filter.addAction("sessions");
        manager.registerReceiver(sessionTextReciever, filter);
    }


    public void stopClick(View view) {
        this.stopService(new Intent(this, SessionService.class));
        this.startActivity(new Intent(SessionActivity.this, MainActivity.class));
        this.finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("sessionlinks", sessionLinkView.getText().toString());
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String url = savedInstanceState.getString("sessionlinks");
        sessionLinkView.setText(url);
        sessionLinkView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onBackPressed() {
    }

    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        return manager.getRunningServices(Integer.MAX_VALUE)
                .stream()
                .anyMatch(x -> SessionService.class.getName().equals(x.service.getClassName()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_session);
        this.editView = findViewById(R.id.editText);
        this.sessionLinkView = findViewById(R.id.textViewSession);

        this.registerReciever();


        Object[] devices = (Object[]) this.getIntent().getSerializableExtra("devices");
        String websiteUrl = this.getIntent().getStringExtra("url");
        String controlLinks = this.getIntent().getStringExtra("controllers");

        if (controlLinks != null) {
            this.sessionLinkView.setText(controlLinks);
            return;
        }

        sessionLinkView.setMovementMethod(LinkMovementMethod.getInstance());

        if (!isServiceRunning()) {
            Intent serviceIntent = new Intent(this, SessionService.class);
            serviceIntent.setAction("start");
            serviceIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            serviceIntent.putExtra("url", websiteUrl);
            serviceIntent.putExtra("devices", devices);
            this.startService(serviceIntent);
        }
    }
}
