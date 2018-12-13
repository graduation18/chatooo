package com.example.gaber.translation_chat.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.adapters.ViewPagerAdapter_with_titles;
import com.example.gaber.translation_chat.fragments.chat_recycler;
import com.example.gaber.translation_chat.fragments.contacts_recycler;

/**
 * Created by gaber on 01/11/2018.
 */

public class main_activity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tabLayout=(TabLayout)findViewById(R.id.zyamat7by);
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        ViewPagerAdapter_with_titles adapter=new ViewPagerAdapter_with_titles( getSupportFragmentManager());
        adapter.addFragment(new chat_recycler(),"Chats");
        adapter.addFragment(new contacts_recycler(),"Contacts");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}
