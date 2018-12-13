package com.example.gaber.translation_chat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.translation_chat.custom.PicassoCircleTransformation;
import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.models.friend_data_model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by gaber on 12/08/2018.
 */

public class friends_list_adapter extends RecyclerView.Adapter<friends_list_adapter.MyViewHolder> {

private Context context;
private List<friend_data_model> datalist;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,status;
        public ImageView image;
        public MyViewHolder(View view) {
        super(view);
        name=(TextView) view.findViewById(R.id.name);
        status=(TextView) view.findViewById(R.id.status);
        image=(ImageView)view.findViewById(R.id.image);

    }
}


    public friends_list_adapter(Context context, List<friend_data_model> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_item, parent, false);
            return new MyViewHolder(itemView);


    }




    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        friend_data_model data = datalist.get(position);
         holder.name.setText(data.name);
         get_status(data.name,holder.status);
         Picasso.with(context)
                .load(data.image)
                .placeholder(R.mipmap.ic_launcher)
                .transform(new PicassoCircleTransformation())
                .into(holder.image, new Callback() {
            @Override
            public void onSuccess() {}
            @Override public void onError() {
                Toast.makeText(context,"error loading image",Toast.LENGTH_LONG).show();
            }
        });



    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }



    private void get_status(final String name, final TextView status)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot sub_type : dataSnapshot.getChildren()) {

                        if (sub_type.child("status").getValue(String.class).contains("Online")){
                                status.setVisibility(View.VISIBLE);
                        }else {
                            status.setVisibility(View.GONE);
                        }


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}


