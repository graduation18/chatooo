package com.example.gaber.translation_chat.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.custom.TranslatorBackgroundTask;
import com.example.gaber.translation_chat.models.data_model;
import com.example.gaber.translation_chat.models.user_data_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by gaber on 12/08/2018.
 */

public class data_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

private Context context;
private List<data_model> datalist;
private static MediaPlayer mediaPlayer;
public boolean seen=false;



    public class MyViewHolder_message extends RecyclerView.ViewHolder {
        public TextView name,message_data,message_status;
        public LinearLayout message_layout;
        public MyViewHolder_message(View view) {
        super(view);
            name=(TextView) view.findViewById(R.id.name);
            message_data =(TextView) view.findViewById(R.id.message_context);
            message_status=(TextView)view.findViewById(R.id.message_status);
            message_layout =(LinearLayout) view.findViewById(R.id.message_data);


    }
}
    public class MyViewHolder_file extends RecyclerView.ViewHolder {
        public TextView name,message_status;
        public ImageView file_down;
        public LinearLayout message_layout;
        public MyViewHolder_file(View view) {
            super(view);
            name=(TextView) view.findViewById(R.id.name);
            file_down =(ImageView) view.findViewById(R.id.file_down);
            message_layout =(LinearLayout) view.findViewById(R.id.file_data);
            message_status=(TextView)view.findViewById(R.id.message_status);


        }
    }
    public class MyViewHolder_record extends RecyclerView.ViewHolder {
        public TextView name,message_status;
        public SeekBar progressBar;
        public ImageView play;
        public LinearLayout record_layout;
        public MyViewHolder_record(View view) {
            super(view);
            name=(TextView) view.findViewById(R.id.name);
            message_status=(TextView)view.findViewById(R.id.message_status);
            progressBar =(SeekBar) view.findViewById(R.id.progressBar);
            play =(ImageView) view.findViewById(R.id.play);
            record_layout =(LinearLayout) view.findViewById(R.id.record_data);



        }
    }
    public class MyViewHolder_image extends RecyclerView.ViewHolder {
        public TextView name,message_status;
        public ImageView imageView;
        public LinearLayout image_layout;
        public MyViewHolder_image(View view) {
            super(view);
            name=(TextView) view.findViewById(R.id.name);
            message_status=(TextView)view.findViewById(R.id.message_status);
            imageView =(ImageView) view.findViewById(R.id.sended_image);
            image_layout =(LinearLayout) view.findViewById(R.id.image_data);


        }
    }

    public data_adapter(Context context, List<data_model> datalist,boolean seen) {
        this.context = context;
        this.datalist = datalist;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==0) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item, parent, false);
            return new MyViewHolder_message(itemView);
        }else if (viewType==1){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_item_right, parent, false);
            return new MyViewHolder_message(itemView);
        }else if (viewType==2){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.record_item, parent, false);
            return new MyViewHolder_record(itemView);
        }else if (viewType==3){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.record_item_righ, parent, false);
            return new MyViewHolder_record(itemView);
        }else if (viewType==4){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_item, parent, false);
            return new MyViewHolder_image(itemView);
        }else if (viewType==5){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_item_right, parent, false);
            return new MyViewHolder_image(itemView);
        }else if (viewType==6){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.file_item, parent, false);
            return new MyViewHolder_file(itemView);
        }else if (viewType==7){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.file_item_righ, parent, false);
            return new MyViewHolder_file(itemView);
        }
        return null;

    }



    @Override
    public int getItemViewType(int position) {
        if (datalist.get(position).type.contains("text")){
             if (datalist.get(position).from.equals(FirebaseInstanceId.getInstance().getToken())) {
                 return 0;

            }else {
                return 1;
            }
        }else if (datalist.get(position).type.contains("record")){
            if (datalist.get(position).from.equals(FirebaseInstanceId.getInstance().getToken())) {

                return 2;

            }else {

                return 3;
            }
        }else if (datalist.get(position).type.contains("image")){
            if (datalist.get(position).from.equals(FirebaseInstanceId.getInstance().getToken())) {
                  return 4;

            }else {
                return 5;
            }

        }
        else if (datalist.get(position).type.contains("file")){
            if (datalist.get(position).from.equals(FirebaseInstanceId.getInstance().getToken())) {
                return 6;

            }else {
                return 7;
            }

        }
        else {
            return 0;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final data_model data = datalist.get(position);
        find_user(data.from,holder,data);


    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }




    private void find_user(String s, final RecyclerView.ViewHolder holder, final data_model data){
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query query = mFirebaseDatabaseReference.orderByChild("token").equalTo(s.toString());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String from_name=dataSnapshot1.getValue(user_data_model.class).name;
                    set_view(holder,data,from_name);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void set_view(final RecyclerView.ViewHolder holder, final data_model data,String from_name){
        if (holder.getItemViewType()==0||holder.getItemViewType()==1){
            MyViewHolder_message message=(MyViewHolder_message)holder;
            message.name.setText(from_name);
            message.message_data.setText(data.message);
            if (seen==true){
                message.message_status.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.seen1, 0);
            }

        }else if(holder.getItemViewType()==2||holder.getItemViewType()==3){
            final MyViewHolder_record message=(MyViewHolder_record)holder;
            message.name.setText(from_name);
            if (seen==true){
                message.message_status.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.seen1, 0);
            }
            message.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(data.storage_url);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        message.progressBar.setMax(mediaPlayer.getDuration());
                        message.play.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);


                        Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                               message.progressBar.setProgress(mediaPlayer.getCurrentPosition());


                            }
                        },0,1000);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mediaPlayer.isPlaying()){
                                    message.play.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                                }else {
                                    message.play.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                                }
                            }
                        }, mediaPlayer.getDuration());

                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
            });
            message.progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(mediaPlayer != null && fromUser){
                        mediaPlayer.seekTo(progress * 1000);
                    }
                }
            });
        }else if (holder.getItemViewType()==4||holder.getItemViewType()==5){
            MyViewHolder_image message=(MyViewHolder_image)holder;
            message.name.setText(from_name);
            if (seen==true){
                message.message_status.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.seen1, 0);
            }
            Picasso.with(context)
                    .load(data.storage_url)
                    .placeholder(R.mipmap.ic_launcher).fit().into(message.imageView, new Callback() {
                @Override
                public void onSuccess() {}
                @Override public void onError() {
                    Toast.makeText(context,"error loading image",Toast.LENGTH_LONG).show();
                }
            });
        }else if (holder.getItemViewType()==6||holder.getItemViewType()==7){
            MyViewHolder_file message=(MyViewHolder_file)holder;
            message.name.setText(from_name);
            message.file_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DownloadManager downloadManager=(DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri=Uri.parse(data.storage_url);
                    DownloadManager.Request request=new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    downloadManager.enqueue(request);
                }
            });

        }

    }


}


