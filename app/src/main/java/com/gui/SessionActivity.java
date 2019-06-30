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

import com.common.Common;
import com.common.SharedSettings;
import com.devices.et302r.ET302RInterface;
import com.example.visionbodyremote.R;
import com.session.SessionService;
import com.web.HTTPControllerFactory;

import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class SessionActivity extends AppCompatActivity {
    private TextView editView;
    private TextView sessionLinkView;
    private String sessionText;

    protected void registerReciever() {
        BroadcastReceiver consoleLogReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                editView.append(intent.getStringExtra("extra"));
            }
        };


        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(SessionActivity.this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("devlog");
        manager.registerReceiver(consoleLogReciever, filter);
    }


    public void stopClick(View view) {
        this.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("sessionlinks", sessionText);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String url = savedInstanceState.getString("sessionlinks");

        if (url != null) {
            sessionLinkView.setText(Html.fromHtml(url));
        }
        sessionLinkView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void onBackPressed() {
        SessionService.getInstance(this).removeActionTask("remotecontrol");
        this.startActivity(new Intent(SessionActivity.this, MainActivity.class));
        this.finish();
    }


    private void startControllers() {
        String websiteURL = SharedSettings.getWebsiteURL(this);
        sessionText = websiteURL + "iframe.php?links=" +

                SharedSettings.getBluetoothDeviceList(this)
                        .stream()
                        .filter(x -> x.getEnabled())
                        .map(x -> HTTPControllerFactory.get(x.getControllerName()))
                        .filter(Objects::nonNull)
                        .map(x -> {
                            String sessionKey = Common.generateString(6);
                            x.setSessionKey(sessionKey);

                            SessionService.getInstance(this).addActionTask("remotecontrol", x);

                            return x.getControlURL();
                        })
                        .collect(Collectors.joining(","));

        sessionText = "<html><a href=\"" + sessionText + "\">Session link</a></html>";

        this.sessionLinkView.setText(Html.fromHtml(sessionText));
        sessionLinkView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_session);
        this.editView = findViewById(R.id.editText);
        this.sessionLinkView = findViewById(R.id.textViewSession);

        this.registerReciever();

        startControllers();
    }
}
