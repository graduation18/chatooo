package com.example.gaber.translation_chat.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gaber.translation_chat.custom.MyDividerItemDecoration;
import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.custom.RecyclerTouchListener;
import com.example.gaber.translation_chat.activities.chat;
import com.example.gaber.translation_chat.adapters.contacts_list_adapter;
import com.example.gaber.translation_chat.custom.database_operations;
import com.example.gaber.translation_chat.models.user_data_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaber on 30/10/2018.
 */

public class contacts_recycler extends Fragment {
    View view;
    private List<user_data_model> data_model_list = new ArrayList<>();
    private RecyclerView data_recyclerView;
    private contacts_list_adapter data_adapter;
    database_operations db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.friends_chat, container, false);
        data_recyclerView = view.findViewById(R.id.chat_recycler);
        db=new database_operations(getActivity());
        data_adapter = new contacts_list_adapter(getActivity(), data_model_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        data_recyclerView.setLayoutManager(mLayoutManager);
        data_recyclerView.setItemAnimator(new DefaultItemAnimator());
        data_recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 5));
        data_recyclerView.setAdapter(data_adapter);
        data_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), data_recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Intent goto_chat=new Intent(getActivity(),chat.class);
                goto_chat.putExtra("user_token",data_model_list.get(position).token);
                startActivity(goto_chat);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        check_contacts_permission();
        get_contacts();
        return view;
    }


    private void get_contacts(){
        ArrayList<String>contacts=new ArrayList<>();
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String  phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(phoneNumber);
        }
        phones.close();
        check_contacts(contacts);
    }
    private void check_contacts(ArrayList<String> contacts){
        data_model_list.clear();
        for (String phone:contacts){
            data_model_list.add(db.getAll_users_model(phone));
        }
        data_adapter.notifyDataSetChanged();

    }
    private void check_contacts_permission(){

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},00);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

}
