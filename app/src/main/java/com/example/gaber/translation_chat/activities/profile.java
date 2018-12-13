package com.example.gaber.translation_chat.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.custom.PicassoCircleTransformation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * Created by gaber on 13/08/2018.
 */

public class profile extends AppCompatActivity {
    TextView name_t,country_t,bio_t,add_friend_t;
    ImageView cover,profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name_t=(TextView)findViewById(R.id.name);
        country_t=(TextView)findViewById(R.id.country);
        bio_t=(TextView)findViewById(R.id.bio);
        add_friend_t=(TextView)findViewById(R.id.add_friend);
        cover=(ImageView)findViewById(R.id.cover);
        profile=(ImageView)findViewById(R.id.profile);
        final String name=getIntent().getStringExtra("name");
        final String image_url=getIntent().getStringExtra("image_url");
        final String token=getIntent().getStringExtra("token");
        final String language=getIntent().getStringExtra("language");
        String country=getIntent().getStringExtra("country");
        name_t.setText(name);
        country_t.setText(country);
        Picasso.with(this)
                .load(image_url)
                .placeholder(R.mipmap.ic_launcher)
                .transform(new PicassoCircleTransformation())
                .into(profile, new Callback() {
                    @Override
                    public void onSuccess() {}
                    @Override public void onError() {
                        Toast.makeText(profile.this,"error loading image",Toast.LENGTH_LONG).show();
                    }
                });
        Picasso.with(this)
                .load(image_url)
                .placeholder(R.mipmap.ic_launcher)
                .into(cover, new Callback() {
                    @Override
                    public void onSuccess() {}
                    @Override public void onError() {
                        Toast.makeText(profile.this,"error loading image",Toast.LENGTH_LONG).show();
                    }
                });
        add_friend_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(profile.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(profile.this);
                }
                builder.setTitle("add friend")
                        .setMessage("you are about to add this friend, you sure?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                add_friend(name,image_url,token,language);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });

    }
    private void add_friend(String name,String image,String token,String language){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("friends_list")
                .child(getSharedPreferences("logged_in",MODE_PRIVATE).getString("name","")).child(name);
        myRef.child("name").setValue(name);
        myRef.child("image_url").setValue(image);
        myRef.child("token").setValue(token);
        myRef.child("language").setValue(language);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,add_friend.class));
    }
}
