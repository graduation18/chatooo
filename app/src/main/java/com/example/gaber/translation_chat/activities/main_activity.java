package com.example.gaber.translation_chat.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.adapters.ViewPagerAdapter_with_titles;
import com.example.gaber.translation_chat.custom.database_operations;
import com.example.gaber.translation_chat.fragments.chat_recycler;
import com.example.gaber.translation_chat.fragments.contacts_recycler;
import com.example.gaber.translation_chat.models.user_data_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by gaber on 01/11/2018.
 */

public class main_activity extends MainActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private database_operations db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tabLayout=(TabLayout)findViewById(R.id.zyamat7by);
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        db=new database_operations(this);
        ViewPagerAdapter_with_titles adapter=new ViewPagerAdapter_with_titles( getSupportFragmentManager());
        adapter.addFragment(new chat_recycler(),"Chats");
        adapter.addFragment(new contacts_recycler(),"Contacts");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        get_users();

    }

    private void get_users(){
        FirebaseDatabase.getInstance().getReference().child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {

                            for (DataSnapshot sub_type : dataSnapshot.getChildren()) {
                                String name=sub_type.child("name").getValue().toString();
                                String token=sub_type.child("token").getValue().toString();
                                String image_url=sub_type.child("image_url").getValue().toString();
                                String status=sub_type.child("status").getValue().toString();
                                String country=sub_type.child("country").getValue().toString();
                                String gender=sub_type.child("gender").getValue().toString();
                                int age=sub_type.child("age").getValue(Integer.class);
                                String language=sub_type.child("language").getValue().toString();
                                String phone=sub_type.child("phone").getValue().toString();
                                db.insert_user_model(
                                        name,token,image_url,status,country,gender,age,language,phone);

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
