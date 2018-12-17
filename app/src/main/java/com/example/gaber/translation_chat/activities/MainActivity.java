package com.example.gaber.translation_chat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.gaber.translation_chat.custom.MyDividerItemDecoration;
import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.adapters.friends_list_adapter;
import com.example.gaber.translation_chat.models.friend_data_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    DrawerLayout fullView;
    Toolbar toolbarTop ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }
    @Override
    public void setContentView(int layoutResID) {

        fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        toolbarTop = (Toolbar) fullView.findViewById(R.id.toolbar_top);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, fullView, toolbarTop, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        fullView.addDrawerListener(toggle);
        toggle.syncState();

        super.setContentView(fullView);







    }


    private void log_out()
    {
        SharedPreferences.Editor editor = getSharedPreferences("logged_in", MODE_PRIVATE).edit();
        editor.putBoolean("state",false);
        editor.apply();
        finish();
    }


    public void logout(View view) {
        log_out();
    }

    public void gotosettings(View view) {
        Intent settings=new Intent(this, com.example.gaber.translation_chat.activities.settings.class);
        finish();
        startActivity(settings);
    }

    public void goto_add_friend(View view) {
        Intent add_friend=new Intent(this, com.example.gaber.translation_chat.activities.add_friend.class);
        finish();
        startActivity(add_friend);
    }

    public void goto_my_profile(View view) {
        Intent add_friend=new Intent(this, my_profile.class);
        finish();
        startActivity(add_friend);
    }

}
