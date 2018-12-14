package com.example.gaber.translation_chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.gaber.translation_chat.R;

public class settings extends MainActivity {
    Switch last_seen,mute_notifications;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mute_notifications=(Switch)findViewById(R.id.mute_notifications);
        last_seen=(Switch)findViewById(R.id.last_seen);
        mute_notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getSharedPreferences("notifications_mute", MODE_PRIVATE)
                            .edit()
                            .putBoolean("state", true)
                            .apply();
                }else {
                    getSharedPreferences("notifications_mute", MODE_PRIVATE)
                            .edit()
                            .putBoolean("state", false)
                            .apply();
                }
            }
        });
        last_seen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getSharedPreferences("last_seen", MODE_PRIVATE)
                            .edit()
                            .putBoolean("state", true)
                            .apply();
                }else {
                    getSharedPreferences("last_seen", MODE_PRIVATE)
                            .edit()
                            .putBoolean("state", false)
                            .apply();
                }
            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back=new Intent(this,MainActivity.class);
        finish();
        startActivity(back);
    }
}
