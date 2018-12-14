package com.example.gaber.translation_chat.activities;


import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;


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
        final String phone=getIntent().getStringExtra("phone");
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
                check_write_contacts_permission();
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
                               writeContact(name,phone);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,add_friend.class));
    }
    private void check_write_contacts_permission()
    {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_CONTACTS},00);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }
    private void writeContact(String displayName, String number) {
        ArrayList contentProviderOperations = new ArrayList();
        //insert raw contact using RawContacts.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        //insert contact display name using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName).build());
        //insert mobile number using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number).withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {
            getApplicationContext().getContentResolver().
                    applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

}
