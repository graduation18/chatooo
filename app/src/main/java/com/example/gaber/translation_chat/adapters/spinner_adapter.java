package com.example.gaber.translation_chat.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.models.Language;

import java.util.List;

/**
 * Created by gaber on 12/10/2018.
 */

public class spinner_adapter extends ArrayAdapter<String> {

private final LayoutInflater mInflater;
private final Context mContext;

private final List<Language> items;
private final int mResource;

public spinner_adapter(@NonNull Context context, @LayoutRes int resource,
                          @NonNull List objects) {
        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
        }
@Override
public View getDropDownView(int position, @Nullable View convertView,
                            @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
        }

@Override
public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
        }

private View createItemView(int position, View convertView, ViewGroup parent){
final View view = mInflater.inflate(mResource, parent, false);

        TextView language = (TextView) view.findViewById(R.id.language);

        Language offerData = items.get(position);

        language.setText(offerData.Name);


        return view;
        }
        }
