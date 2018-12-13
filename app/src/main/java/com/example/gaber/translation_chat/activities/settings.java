package com.example.gaber.translation_chat.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.gaber.translation_chat.R;

public class settings extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back=new Intent(this,MainActivity.class);
        finish();
        startActivity(back);
    }
}
