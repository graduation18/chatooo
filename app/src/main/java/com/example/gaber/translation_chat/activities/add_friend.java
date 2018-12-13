package com.example.gaber.translation_chat.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.gaber.translation_chat.custom.MyDividerItemDecoration;
import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.custom.RecyclerTouchListener;
import com.example.gaber.translation_chat.custom.database_operations;
import com.example.gaber.translation_chat.models.user_data_model;
import com.example.gaber.translation_chat.adapters.user_list_adapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.util.ArrayList;
import java.util.List;

public class add_friend extends MainActivity {

    EditText text,country,age;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private database_operations db;
    private List<user_data_model> data_model_list = new ArrayList<>();
    private RecyclerView data_recyclerView;
    private user_list_adapter data_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);


        text=(EditText)findViewById(R.id.name);
        age=(EditText)findViewById(R.id.age);
        country=(EditText)findViewById(R.id.country);
        radioGroup = (RadioGroup) findViewById(R.id.radio);
        radioButton = (RadioButton) findViewById(R.id.male);
        db=new database_operations(add_friend.this);
        data_recyclerView = findViewById(R.id.main_recycler);
        data_adapter = new user_list_adapter(this, data_model_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        data_recyclerView.setLayoutManager(mLayoutManager);
        data_recyclerView.setItemAnimator(new DefaultItemAnimator());
        data_recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 5));
        data_recyclerView.setAdapter(data_adapter);
        data_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, data_recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                String name=data_model_list.get(position).name;
                String image_url=data_model_list.get(position).image_url;
                String token=data_model_list.get(position).token;
                String language=data_model_list.get(position).language;
                String country=data_model_list.get(position).country;
                Intent gotoprofile=new Intent(add_friend.this,profile.class);
                gotoprofile.putExtra("name",name);
                gotoprofile.putExtra("image_url",image_url);
                gotoprofile.putExtra("token",token);
                gotoprofile.putExtra("language",language);
                gotoprofile.putExtra("country",country);
                finish();
                startActivity(gotoprofile);

            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        country.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                    picker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                            country.setText(name);
                            picker.dismiss();
                        }
                    });
                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                }
            }
        });
        db.delete_all_search();
        FirebaseDatabase.getInstance().getReference().child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {

                            for (DataSnapshot sub_type : dataSnapshot.getChildren()) {
                                String name=sub_type.child("name").getValue().toString();
                                String token=sub_type.child("token").getValue().toString();
                                String pass=sub_type.child("pass").getValue().toString();
                                String image_url=sub_type.child("image_url").getValue().toString();
                                String status=sub_type.child("status").getValue().toString();
                                String country=sub_type.child("country").getValue().toString();
                                String gender=sub_type.child("gender").getValue().toString();
                                int age=sub_type.child("age").getValue(Integer.class);
                                String language=sub_type.child("language").getValue().toString();
                                db.insert_user_model(
                                        name,token,image_url,status,country,gender,age,language
                                );
                                data_model_list.add(new user_data_model(name,token
                                ,image_url,status,country,gender,age,language));
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
    private void find_user(final String name,int age,String gender,String country)
    {
        data_model_list.clear();
        data_model_list.addAll(db.getAll_users_model(name,age,gender,country));
        data_adapter.notifyDataSetChanged();

    }
    public void find_friend(View view)
    {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId==-1)
            selectedId=R.id.male;
        radioButton = (RadioButton) findViewById(selectedId);
        String gender_string=radioButton.getText().toString();
        String age_string=age.getText().toString();
        if (age_string.length()==0){
            age_string="0";
        }
        String country_string=country.getText().toString();
        String name_string=text.getText().toString();

        find_user(name_string, Integer.parseInt(age_string),gender_string,country_string);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent back=new Intent(this,chat.class);
        finish();
        startActivity(back);
    }

}
