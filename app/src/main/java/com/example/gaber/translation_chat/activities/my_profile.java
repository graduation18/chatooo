package com.example.gaber.translation_chat.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.translation_chat.custom.PicassoCircleTransformation;
import com.example.gaber.translation_chat.R;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by gaber on 11/10/2018.
 */

public class my_profile extends AppCompatActivity {
    TextView name_t,edit_profile_t,status_t;
    EditText country_t,bio_t,language_t,age_t;
    ImageView cover_im,profile_im;
    int PICK_IMAGE_MULTIPLE = 3;
    private StorageReference mStorageRef;
    boolean cover=false;
    String profile_url="",cover_url="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        name_t=(TextView)findViewById(R.id.name);
        country_t=(EditText)findViewById(R.id.country);
        bio_t=(EditText)findViewById(R.id.bio);
        edit_profile_t=(TextView)findViewById(R.id.edit_profile);
        language_t=(EditText)findViewById(R.id.language);
        age_t=(EditText)findViewById(R.id.age);
        status_t=(TextView)findViewById(R.id.status);
        cover_im=(ImageView)findViewById(R.id.cover);
        profile_im=(ImageView)findViewById(R.id.profile);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        get_my_profile_data();
        country_t.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                    picker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                            country_t.setText(name);
                            picker.dismiss();
                        }
                    });
                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                }
            }
        });
        profile_im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_images();
                cover=false;

            }
        });
        cover_im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_images();
                cover=true;
            }
        });
        edit_profile_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(my_profile.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(my_profile.this);
                }
                builder.setTitle("change data")
                        .setMessage("you are about to update your profile, you sure?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                edit_profile(Integer.parseInt(age_t.getText().toString()),language_t.getText().toString()
                                ,country_t.getText().toString()
                                ,bio_t.getText().toString()
                                ,profile_url,cover_url);
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
    private void get_my_profile_data()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("token").equalTo(FirebaseInstanceId.getInstance().getToken());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot sub_type : dataSnapshot.getChildren()) {
                         String name=sub_type.child("name").getValue(String.class);
                         String image_url=sub_type.child("image_url").getValue(String.class);
                         String token=sub_type.child("token").getValue(String.class);
                         String language=sub_type.child("language").getValue(String.class);
                         String country=sub_type.child("country").getValue(String.class);
                         String bio=sub_type.child("bio").getValue(String.class);
                         int age=sub_type.child("age").getValue(Integer.class);
                         String cover=sub_type.child("cover").getValue(String.class);
                         String gender=sub_type.child("gender").getValue(String.class);
                         String status=sub_type.child("status").getValue(String.class);
                         name_t.setText(name);
                         country_t.setText(country);
                         bio_t.setText(bio);
                         age_t.setText(String.valueOf(age));
                         language_t.setText(language);
                         if (status.contains("ONLINE")){
                             status_t.setVisibility(View.VISIBLE);
                         }
                         Picasso.with(my_profile.this)
                                .load(image_url)
                                .placeholder(R.mipmap.ic_launcher)
                                .transform(new PicassoCircleTransformation())
                                .into(profile_im, new Callback() {
                                    @Override
                                    public void onSuccess() {}
                                    @Override public void onError() {
                                        Toast.makeText(my_profile.this,"error loading image",Toast.LENGTH_LONG).show();
                                    }
                                });
                         Picasso.with(my_profile.this)
                                .load(cover)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(cover_im, new Callback() {
                                    @Override
                                    public void onSuccess() {}
                                    @Override public void onError() {
                                        Toast.makeText(my_profile.this,"error loading image",Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void edit_profile(final int age_t, final String language_, final String country_, final String bio_, final String profile_url, final String cover_url)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("token").equalTo(FirebaseInstanceId.getInstance().getToken());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot sub_type : dataSnapshot.getChildren()) {
                        String name=sub_type.child("name").getValue(String.class);
                        String image_url=sub_type.child("image_url").getValue(String.class);
                        String token=sub_type.child("token").getValue(String.class);
                        String language=sub_type.child("language").getValue(String.class);
                        String country=sub_type.child("country").getValue(String.class);
                        String bio=sub_type.child("bio").getValue(String.class);
                        int age=sub_type.child("age").getValue(Integer.class);
                        String cover=sub_type.child("cover").getValue(String.class);
                        String gender=sub_type.child("gender").getValue(String.class);
                        String status=sub_type.child("status").getValue(String.class);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = sub_type.getRef();
                        if (cover_url.length()>0){
                            myRef.child("cover").setValue(cover_url);
                        }
                        if (profile_url.length()>0){
                            myRef.child("image_url").setValue(profile_url);
                        }
                        if (language_.length()>0){
                            myRef.child("language").setValue(language_);
                        }
                        myRef.child("bio").setValue(bio_);
                        myRef.child("country").setValue(country_);
                        myRef.child("age").setValue(age_t);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void select_images()
    {
        Pix.start(this,
                PICK_IMAGE_MULTIPLE,20);
    }
    private void upload_image(String audioFilePath)
    {
        Uri file = Uri.fromFile(new File(audioFilePath));
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        final StorageReference ref = mStorageRef.child("image/"+FirebaseInstanceId.getInstance().getToken()+"/"+audioFilePath);
        UploadTask uploadTask = ref.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (cover){
                        cover_url=downloadUri.toString();
                        Picasso.with(my_profile.this)
                                .load(downloadUri)
                                .placeholder(R.mipmap.ic_launcher)
                                .into(cover_im, new Callback() {
                                    @Override
                                    public void onSuccess() {}
                                    @Override public void onError() {
                                        Toast.makeText(my_profile.this,"error loading image",Toast.LENGTH_LONG).show();
                                    }
                                });

                    }else {
                        profile_url=downloadUri.toString();
                        Picasso.with(my_profile.this)
                                .load(downloadUri)
                                .placeholder(R.mipmap.ic_launcher)
                                .transform(new PicassoCircleTransformation())
                                .into(profile_im, new Callback() {
                                    @Override
                                    public void onSuccess() {}
                                    @Override public void onError() {
                                        Toast.makeText(my_profile.this,"error loading image",Toast.LENGTH_LONG).show();
                                    }
                                });
                    }

                    progressDialog.dismiss();
                } else {
                    // Handle failures
                    // ...
                    progressDialog.dismiss();

                }
            }

        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progressDialog.setMessage(String.valueOf(progress)+"% Uploaded");
            }
        });



    }
    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_MULTIPLE) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            for (String uri:returnValue){
                upload_image(uri);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this,chat.class));
    }
}
