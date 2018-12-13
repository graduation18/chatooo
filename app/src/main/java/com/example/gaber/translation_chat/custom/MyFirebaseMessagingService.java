package com.example.gaber.translation_chat.custom;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.activities.MainActivity;
import com.example.gaber.translation_chat.models.user_data_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by gaber on 26/08/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private NotificationManager notifManager;
    private NotificationChannel mChannel;
    private database_operations db;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            db = new database_operations(getApplicationContext());

            try {
                find_user(remoteMessage);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
    }


    private void notification(String message,String from_user_token){

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);
        }

        intent = new Intent (this, MainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mChannel == null) {
                NotificationChannel mChannel = new NotificationChannel
                        ("0",from_user_token,importance);
                mChannel.setDescription (message);
                mChannel.enableVibration (true);
                mChannel.setVibrationPattern (new long[]
                        {100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel (mChannel);
            }
            builder = new NotificationCompat.Builder (this,"0");

            intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity (this, 0, intent, 0);
            builder.setContentTitle (from_user_token)  // flare_icon_30
                    .setSmallIcon (R.drawable.common_google_signin_btn_icon_light) // required
                    .setContentText (message)  // required
                    .setDefaults (Notification.DEFAULT_ALL)
                    .setAutoCancel (true)
                    .setContentIntent (pendingIntent)
                    .setSound (RingtoneManager.getDefaultUri
                            (RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate (new long[]{100, 200, 300, 400,
                            500, 400, 300, 200, 400});
        } else {

            builder = new NotificationCompat.Builder (this);


            Uri sound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_light);
            builder.setContentTitle(from_user_token);
            builder.setContentText(message);
            builder.setColor((getResources().getColor(R.color.white)));
            builder.setSound(sound);
            builder.setVibrate (new long[]{100, 200, 300, 400,
                    500, 400, 300, 200, 400});
            Intent resultIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);

// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

// notificationID allows you to update the notification later on.


        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification notification = builder.build ();
        int id = (int) System.currentTimeMillis();
        notifManager.notify (id, notification);

    }
    private void find_user(RemoteMessage remoteMessage) throws ParseException {
        Map<String,String> data=remoteMessage.getData();
        final String from_user_token =  data.get("from_user_token");
        final String message =  data.get("message");
        final String storage_url =  data.get("storage_url");
        final String type =  data.get("type");
        final String time =  data.get("time");
        final String lang_pair =  data.get("lang_pair");
        final Date date1=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss aa", Locale.getDefault()).parse(time);
        Log.w("hvh",message);
        final String refreshedtoken= FirebaseInstanceId.getInstance().getToken();

        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query query = mFirebaseDatabaseReference.orderByChild("token").equalTo(from_user_token);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String from_name=dataSnapshot1.getValue(user_data_model.class).name;
                    notification(message,from_name);
                    db.insert_data_model(from_user_token,refreshedtoken,message,type, (int) (date1.getTime()/1000),storage_url,lang_pair);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

