package com.example.gaber.translation_chat.activities;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.example.gaber.translation_chat.custom.MyDividerItemDecoration;
import com.example.gaber.translation_chat.custom.PicassoCircleTransformation;
import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.custom.RecyclerTouchListener;
import com.example.gaber.translation_chat.custom.TranslatorBackgroundTask;
import com.example.gaber.translation_chat.adapters.data_adapter;
import com.example.gaber.translation_chat.models.data_model;
import com.example.gaber.translation_chat.custom.database_operations;
import com.example.gaber.translation_chat.models.user_data_model;
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
import org.json.JSONObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class chat extends AppCompatActivity {
    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    private static String audioFilePath;
    private boolean isRecording = false;
    private TextView user_id,last_seen,back ;
    private EditText textmessage ;
    private ImageView choose_images,Choose_files,user_image,translate ;
    private com.example.gaber.translation_chat.adapters.data_adapter data_adapter;
    private List<data_model> data_model_list = new ArrayList<>();
    private RecyclerView data_recyclerView;
    private StorageReference mStorageRef;
    private RequestQueue queue;
    String my_user_id,to_user_id,to_lang,my_user_lang,lang_pair;
    FirebaseDatabase database;
    database_operations db;
    int PICK_FILE = 1;
    int PICK_IMAGE_MULTIPLE = 2;
    boolean isTranslating=false,seen=false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        user_id=(TextView)findViewById(R.id.user_id);
        textmessage=(EditText)findViewById(R.id.textmessage);
        last_seen=(TextView)findViewById(R.id.last_seen);
        back=(TextView)findViewById(R.id.back);
        choose_images=(ImageView) findViewById(R.id.camera);
        Choose_files=(ImageView)findViewById(R.id.attachfiles);
        user_image=(ImageView)findViewById(R.id.user_image);
        translate=(ImageView)findViewById(R.id.translate);
        final RecordView recordView = (RecordView) findViewById(R.id.record_view);
        final RecordButton recordButton = (RecordButton) findViewById(R.id.record_button);
        db=new database_operations(getApplicationContext());
        my_user_id= FirebaseInstanceId.getInstance().getToken();
        my_user_lang=getSharedPreferences("logged_in",MODE_PRIVATE).getString("language","");
        data_recyclerView = findViewById(R.id.main_recycler);
        data_adapter = new data_adapter(this, data_model_list,seen);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        data_recyclerView.setLayoutManager(mLayoutManager);
        data_recyclerView.setItemAnimator(new DefaultItemAnimator());
        data_recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 5));
        data_recyclerView.setAdapter(data_adapter);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        queue = Volley.newRequestQueue(this);
        to_user_id=getIntent().getStringExtra("user_token");
        user_data_model user=db.getAll_users_model(to_user_id);
        String from_name=user.name;
        String image_url=user.image_url;
        lang_pair=getSharedPreferences("logged_in",MODE_PRIVATE).getString("language","ar")+"-"+user.language;
        user_id.setText(from_name);
        Picasso.with(chat.this)
                .load(image_url)
                .fit().into(user_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            user_image.setVisibility(View.VISIBLE);
                        }
                        @Override public void onError() {
                            Toast.makeText(chat.this,"error loading image",Toast.LENGTH_LONG).show();
                        }
                    });
        check_last_seen(user.phone);
        status_online();
        refresh();



        //IMPORTANT
        recordButton.setRecordView(recordView);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                textmessage.setVisibility(View.GONE);
                choose_images.setVisibility(View.GONE);
                Choose_files.setVisibility(View.GONE);
                translate.setVisibility(View.GONE);
                recordView.setVisibility(View.VISIBLE);
                audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/"+rand_file_name();
                record();
                Log.d("RecordView", "onStart");
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                stopAudio();
                delete_record();
                textmessage.setVisibility(View.VISIBLE);
                choose_images.setVisibility(View.VISIBLE);
                Choose_files.setVisibility(View.VISIBLE);
                translate.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.GONE);
                Log.d("RecordView", "onCancel");

            }

            @Override
            public void onFinish(long recordTime) {
                //Stop Recording..
                stopAudio();
                textmessage.setVisibility(View.VISIBLE);
                choose_images.setVisibility(View.VISIBLE);
                Choose_files.setVisibility(View.VISIBLE);
                translate.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.GONE);
                upload_record(audioFilePath);
                Log.d("RecordView", "onFinish");

            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                stopAudio();
                delete_record();
                textmessage.setVisibility(View.VISIBLE);
                choose_images.setVisibility(View.VISIBLE);
                Choose_files.setVisibility(View.VISIBLE);
                translate.setVisibility(View.VISIBLE);
                recordView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Record is too short",Toast.LENGTH_LONG).show();
                Log.d("RecordView", "onLessThanSecond");
            }
        });

        //ListenForRecord must be false ,otherwise onClick will not be called
        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {

                    send_message(my_user_id, to_user_id, textmessage.getText().toString(), "", "text",lang_pair);

                textmessage.setText("");

            }
        });




        textmessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length()>0) {
                    recordButton.setListenForRecord(false);
                    recordButton.setImageResource(R.drawable.ic_send_black_24dp);

                }else {
                    recordButton.setListenForRecord(true);
                    recordButton.setImageResource(R.drawable.recv_ic_mic_white);
                }
            }
        });
        choose_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_images();
            }
        });
        Choose_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_Documents();
            }
        });
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTranslating==false){
                    translate.setImageResource(R.drawable.ic_translate_green_24dp);
                    isTranslating=true;
                }else{
                    translate.setImageResource(R.drawable.ic_translate_white_24dp);
                    isTranslating=false;
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(chat.this,main_activity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void record()
    {
        try {
            isRecording = true;
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }
    private void stopAudio ( )
    {


        if (isRecording)
        {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } else {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void delete_record()
    {
        File file = new File(audioFilePath);
        boolean deleted = file.delete();
        if (deleted)
            getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

    }
    private String rand_file_name()
    {
        String alphabet= "abcdefghijklmnopqrstuvwxyz";
        String s = "";
        Random random = new Random();
        int randomLen = 1+random.nextInt(9);
        for (int i = 0; i < randomLen; i++) {
            char c = alphabet.charAt(random.nextInt(26));
            s+=c;
        }
        return s+".mp3";
    }
    private void upload_record(String audioFilePath)
    {
        Uri file = Uri.fromFile(new File(audioFilePath));
        final StorageReference ref = mStorageRef.child("record"+my_user_id+"/"+audioFilePath);
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
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
                    progressDialog.dismiss();
                    send_message(my_user_id,to_user_id,"", String.valueOf(downloadUri),"record",lang_pair);

                } else {
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
    private void upload_image(String audioFilePath)
    {
        Uri file = Uri.fromFile(new File(audioFilePath));
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        final StorageReference ref = mStorageRef.child("image/"+my_user_id+"/"+audioFilePath);
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
                    progressDialog.dismiss();
                    send_message(my_user_id,to_user_id,"", String.valueOf(downloadUri),"image",lang_pair);

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
    private void upload_file(String audioFilePath)
    {
        Uri file = Uri.fromFile(new File(audioFilePath));
        final StorageReference ref = mStorageRef.child("file"+my_user_id+"/"+audioFilePath);
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
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
                    progressDialog.dismiss();
                    send_message(my_user_id,to_user_id,"", String.valueOf(downloadUri),"file",lang_pair);

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
    private void send_message(final String from_user_token, final String to_user_token, String message, final String storage_url, final String type,String lang_pair)
    {

            final String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss aa", Locale.getDefault()).format(new Date());

            db.insert_data_model(from_user_token, to_user_token, message, type, (int) System.currentTimeMillis(), storage_url,lang_pair);
            refresh();
        if (isTranslating){
            if (message.length()>0) {
                try {
                    String languagePair = lang_pair;
                    message = Translate(message, languagePair);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
            try {
                JSONObject main = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("from_user_token", from_user_token);
                data.put("message", message);
                data.put("storage_url", storage_url);
                data.put("type", type);
                data.put("time", date);
                data.put("lang_pair", lang_pair);
                main.put("data", data);
                main.put("to", to_user_token);
                String url = "https://fcm.googleapis.com/fcm/send";
                if (queue == null) {
                    queue = Volley.newRequestQueue(this);
                }
                // Request a string response from the provided URL.
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, main,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "key=AAAAgt2WRhE:APA91bGPyXD_fxtAEmZyE18zcq37bD4H0WD3gl1oWzJEgUzaeyho3YCVA8tgKvdasBzKSdWTbNZkRsxFBPyMN8gu2uNKHT0GgJxuuaszkT49hGGpwHdScpn2GrqZ9PHvzBzswirSBK6W");

                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            } catch (Exception e) {

            }


    }
    private void select_Documents()
    {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent,PICK_FILE);
    }
    private void select_images()
    {
        Pix.start(this,
                PICK_IMAGE_MULTIPLE,20);
    }
    private void reload()
    {
        data_model_list.clear();
        data_model_list.addAll(db.getAll_notification_model(my_user_id,to_user_id));
        data_adapter.notifyDataSetChanged();
        data_recyclerView.smoothScrollToPosition(data_model_list.size() - 1);
    }
    private void refresh()
    {
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){

                if (user_id.getText().toString().length()>0){
                    int size=db.getmessagesCount(my_user_id,to_user_id);
                    if (data_model_list.size()!=size) {
                        reload();
                    }
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

    }
    String Translate(String textToBeTranslated,String languagePair) throws ExecutionException, InterruptedException
    {
        TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(this);
        String translationResult = translatorBackgroundTask.execute(textToBeTranslated,languagePair).get().toString();// Returns the translated text as a String
        return translationResult;// Logs the result in Android Monitor
    }
    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_MULTIPLE) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            for (String uri:returnValue){
                upload_image(uri);
            }
        }else if (resultCode == RESULT_OK && requestCode ==PICK_FILE){
            upload_file( data.getData().getPath());
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("token").equalTo(FirebaseInstanceId.getInstance().getToken());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                        DatabaseReference myRef = user.getRef();
                        myRef.child("status").setValue("Offline");
                        if (getSharedPreferences("last_seen",MODE_PRIVATE).getBoolean("state",false)){
                            final String date = new SimpleDateFormat("yyyy/MM/dd HH:mm aa", Locale.getDefault()).format(new Date());
                            myRef.child("last_seen").setValue(date);
                        }else {
                            myRef.child("last_seen").setValue("hidden");

                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    protected void onResume()
    {
        super.onResume();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("token").equalTo(FirebaseInstanceId.getInstance().getToken());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                        DatabaseReference myRef = user.getRef();
                        myRef.child("status").setValue("Online");

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void  check_last_seen(String phone)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("phone").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot sub_type : dataSnapshot.getChildren()) {
                        if (!sub_type.child("last_seen").getValue(String.class).contains("hidden"))
                        last_seen.setText(sub_type.child("last_seen").getValue(String.class));
                        last_seen.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("khgj",databaseError.getMessage());


            }
        });
    }
    private void status_online()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("token").equalTo(FirebaseInstanceId.getInstance().getToken());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot user : dataSnapshot.getChildren()) {

                        DatabaseReference myRef = user.getRef();
                        myRef.child("status").setValue("Online");
                        myRef.child("last_seen").setValue("Online");

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}


}