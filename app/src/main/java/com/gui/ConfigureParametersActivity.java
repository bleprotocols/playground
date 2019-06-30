package com.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.common.SharedSettings;
import com.example.visionbodyremote.R;

public class ConfigureParametersActivity extends AppCompatActivity {
    private TextView urlView;


    private void restoreSettings() {
        this.urlView.setText(SharedSettings.getWebsiteURL(this));
    }

    private void saveSettings() {
        SharedSettings.saveWebsiteURL(this, urlView.getText().toString());
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


    public void onBackPressed() {
        saveSettings();
        this.startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    //retrieve user settings.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("Test:" + ((byte) -86 ^ (byte) 65));
        setContentView(R.layout.activity_settings);
        this.urlView = findViewById(R.id.websiteUrl);

        restoreSettings();
    }
}
