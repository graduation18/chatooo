package com.example.gaber.translation_chat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.translation_chat.R;
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

/**
 * Created by gaber on 12/08/2018.
 */

public class friends_chat_adapter extends RecyclerView.Adapter<friends_chat_adapter.MyViewHolder_message> {

private Context context;
private List<data_model> datalist;



    public class MyViewHolder_message extends RecyclerView.ViewHolder {
        public TextView user_id,message;
        public ImageView user_image;
        public MyViewHolder_message(View view) {
        super(view);
            user_id=(TextView) view.findViewById(R.id.user_id);
            message =(TextView) view.findViewById(R.id.message);
            user_image =(ImageView) view.findViewById(R.id.user_image);


    }
}

    public friends_chat_adapter(Context context, List<data_model> datalist) {
        this.context = context;
        this.datalist = datalist;

    }

    @Override
    public MyViewHolder_message onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_chat_item, parent, false);
            return new MyViewHolder_message(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder_message holder, int position) {
        final data_model data = datalist.get(position);
        final String message=data.message;
        String from_user_name=data.from;
        if (from_user_name.contains(FirebaseInstanceId.getInstance().getToken())){
            from_user_name=data.to;
        }
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        Query query = mFirebaseDatabaseReference.orderByChild("token").equalTo(from_user_name);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String from_name=dataSnapshot1.getValue(user_data_model.class).name;
                    String image_url=dataSnapshot1.getValue(user_data_model.class).image_url;
                    holder.message.setText(message);
                    holder.user_id.setText(from_name);
                    Picasso.with(context)
                            .load(image_url)
                            .fit().into(holder.user_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.user_image.setVisibility(View.VISIBLE);
                        }
                        @Override public void onError() {
                            Toast.makeText(context,"error loading image",Toast.LENGTH_LONG).show();
                        }
                    });


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return datalist.size();
    }



}


