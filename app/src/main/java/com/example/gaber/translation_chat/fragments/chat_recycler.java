package com.example.gaber.translation_chat.fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gaber.translation_chat.custom.MyDividerItemDecoration;
import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.custom.RecyclerTouchListener;
import com.example.gaber.translation_chat.activities.chat;
import com.example.gaber.translation_chat.models.data_model;
import com.example.gaber.translation_chat.custom.database_operations;
import com.example.gaber.translation_chat.adapters.friends_chat_adapter;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaber on 30/10/2018.
 */

public class chat_recycler extends Fragment {
    View view;
    private database_operations db;
    private List<data_model> data_model_list = new ArrayList<>();
    private RecyclerView data_recyclerView;
    private friends_chat_adapter data_adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.friends_chat, container, false);
        db=new database_operations(getActivity());
        data_recyclerView = view.findViewById(R.id.chat_recycler);
        data_adapter = new friends_chat_adapter(getActivity(), data_model_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        data_recyclerView.setLayoutManager(mLayoutManager);
        data_recyclerView.setItemAnimator(new DefaultItemAnimator());
        data_recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 5));
        data_recyclerView.setAdapter(data_adapter);
        data_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), data_recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Intent goto_chat=new Intent(getActivity(),chat.class);
                if (data_model_list.get(position).from.contains(FirebaseInstanceId.getInstance().getToken())){
                    goto_chat.putExtra("user_token",data_model_list.get(position).to);
                }else {
                    goto_chat.putExtra("user_token",data_model_list.get(position).from);
                }

                startActivity(goto_chat);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        reload();
        refresh();

        return view;
    }
    private void reload()
    {
        data_model_list.clear();
        data_model_list.addAll(db.getAll_notification_model());
        data_adapter.notifyDataSetChanged();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        reload();
    }
    private void refresh()
    {
        final Handler handler = new Handler();//دا هاندلر زي تايمر لفانكشن ممكن تشتغل كل شوية او ع طول ع حسب التايمر ب اد اية
        final int delay = 100; //milliseconds معناه ان الفانكشن هتشتغل كل 1 ثانية حلو؟
        handler.postDelayed(new Runnable(){
            public void run(){

                Boolean res=db.getmessagesCount();
                if (res) {
                    reload();
                    Log.w("ndkkjnakn",data_model_list.get(0).message);
                }
                handler.postDelayed(this, delay);
            }
        }, delay);

    }
}
