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
    private friends_list_adapter data_adapter;
    public List<friend_data_model> friends_list = new ArrayList<>();
    public RecyclerView friends_recyclerView;


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


        friends_recyclerView = findViewById(R.id.friends_recycler);
        data_adapter = new friends_list_adapter(this, friends_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        friends_recyclerView.setLayoutManager(mLayoutManager);
        friends_recyclerView.setItemAnimator(new DefaultItemAnimator());
        friends_recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 5));
        friends_recyclerView.setAdapter(data_adapter);


        find_friends(getSharedPreferences("logged_in",MODE_PRIVATE).getString("name",""));

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            data_adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
        edit_friends_tokens();




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
    private void find_friends(String s)
    {

         FirebaseDatabase.getInstance().getReference().child("friends_list").child(s)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        friends_list.clear();
                        if (dataSnapshot.hasChildren()) {

                            for (DataSnapshot sub_type : dataSnapshot.getChildren()) {
                                String name=sub_type.child("name").getValue(String.class);
                                String image=sub_type.child("image_url").getValue(String.class);
                                String token=sub_type.child("token").getValue(String.class);
                                String language=sub_type.child("language").getValue(String.class);
                                friends_list.add(new friend_data_model(name,image,token,language));

                            }
                            data_adapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getApplicationContext(),"no such user",Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


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
    private void edit_friends_tokens()
    {
        FirebaseDatabase.getInstance().getReference().child("friends_list")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {

                            for (DataSnapshot sub_type : dataSnapshot.getChildren()) {
                                Query query = sub_type.getRef().orderByKey()
                                        .equalTo(getSharedPreferences("logged_in",MODE_PRIVATE).getString("name",""));
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            for (DataSnapshot sub_type : dataSnapshot.getChildren()) {
                                                DatabaseReference myRef = sub_type.getRef();
                                                myRef.child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"no such user",Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
