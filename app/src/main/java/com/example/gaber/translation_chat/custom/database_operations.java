package com.example.gaber.translation_chat.custom;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.gaber.translation_chat.models.data_model;
import com.example.gaber.translation_chat.models.user_data_model;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by gaber on 21/07/2018.
 */

public class database_operations extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "agent_database";

    Context context;


    public database_operations(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create  table
        db.execSQL(data_model.CREATE_TABLE);
        db.execSQL(user_data_model.CREATE_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + data_model.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + user_data_model.TABLE_NAME);


        // Create tables again
        onCreate(db);
    }


    //data_model
    public void insert_data_model(String from,String to,String message,String type,int time,String storage_url,String lang_pair)
    {
        database_operations mDbHelper = new database_operations(context);
        // get writable database as we want to write data
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(data_model.to_sql, to);
        values.put(data_model.from_sql, from);
        values.put(data_model.message_sql, message);
        values.put(data_model.type_sql, type);
        values.put(data_model.time_sql, time);
        values.put(data_model.storage_url_sql, storage_url);
        values.put(data_model.lang_pair_sql,lang_pair);

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(data_model.TABLE_NAME, null, values);


        // return newly inserted row id

    }
    public List<data_model> getAll_notification_model(String from,String to)
    {
        List<data_model> data_modelList = new ArrayList<>();
        // Select All Query
        String countQuery = "SELECT  * FROM " + data_model.TABLE_NAME+" WHERE "
                +"("+data_model.to_sql+"=? AND "+data_model.from_sql+"=? )"+
                "OR ("+data_model.from_sql+"=? AND "+data_model.to_sql+"=? )"
                +" ORDER BY "+data_model.time_sql;



        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, new String[]{to,from,to,from});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                data_model data = new data_model();
                data.from=cursor.getString(cursor.getColumnIndex(data_model.from_sql));
                data.to=cursor.getString(cursor.getColumnIndex(data_model.to_sql));
                data.message=cursor.getString(cursor.getColumnIndex(data_model.message_sql));
                data.time=cursor.getInt(cursor.getColumnIndex(data_model.time_sql));
                data.type=cursor.getString(cursor.getColumnIndex(data_model.type_sql));
                data.storage_url=cursor.getString(cursor.getColumnIndex(data_model.storage_url_sql));
                data.lang_pair=cursor.getString(cursor.getColumnIndex(data_model.lang_pair_sql));
                data_modelList.add(data);

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return data_modelList;
    }
    public data_model getlast_message(String from,String to)
    {
        data_model data = new data_model();
        // Select All Query
        String countQuery = "SELECT  * FROM " + data_model.TABLE_NAME+" WHERE "
                +"("+data_model.to_sql+"=? AND "+data_model.from_sql+"=? )"+
                "OR ("+data_model.from_sql+"=? AND "+data_model.to_sql+"=? )"
                +" ORDER BY "+data_model.time_sql+" DESC LIMIT 1";



        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, new String[]{to,from,to,from});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

                data.from=cursor.getString(cursor.getColumnIndex(data_model.from_sql));
                data.to=cursor.getString(cursor.getColumnIndex(data_model.to_sql));
                data.message=cursor.getString(cursor.getColumnIndex(data_model.message_sql));
                data.time=cursor.getInt(cursor.getColumnIndex(data_model.time_sql));
                data.type=cursor.getString(cursor.getColumnIndex(data_model.type_sql));
                data.storage_url=cursor.getString(cursor.getColumnIndex(data_model.storage_url_sql));
                data.lang_pair=cursor.getString(cursor.getColumnIndex(data_model.lang_pair_sql));
            db.close();
            // return notes list
            return data;

        }else {
            return null;
        }

        // close db connection

    }
    public List<data_model> getAll_notification_model()
    {
        List<data_model> data_modelList = new ArrayList<>();

        // Select All Query
        String countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                data_model data=new data_model();
                data= getlast_message(cursor.getString(cursor.getColumnIndex(user_data_model.token_sql)),FirebaseInstanceId.getInstance().getToken());
                if (data!=null){
                    data_modelList.add(data);
                }


            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return data_modelList;
    }
    public int getmessagesCount(String from,String to)
    {

        String countQuery = "SELECT  * FROM " + data_model.TABLE_NAME+" WHERE "
                +"("+data_model.to_sql+"=? AND "+data_model.from_sql+"=? )"+
                "OR ("+data_model.from_sql+"=? AND "+data_model.to_sql+"=? )";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, new String[]{to,from,to,from});


        int count = cursor.getCount();

        cursor.close();



        // return count

        return count;
    }
    public boolean getmessagesCount()
    {
        Boolean result;
        String countQuery = "SELECT  * FROM " + data_model.TABLE_NAME
                +" WHERE "+data_model.time_sql+"=?";
        SQLiteDatabase db = this.getReadableDatabase();
        final String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss aa", Locale.getDefault()).format(new Date());

        Cursor cursor = db.rawQuery(countQuery, new String []{date});

        if (cursor.moveToNext()){
         result=true;
        }else {
            result=false;
        }
        cursor.close();



        // return count

        return result;
    }


    //users_model
    public void insert_user_model(String name, String token
            ,String image_url,String status,String country
            ,String gender,int age,String language,String phone)
    {
        database_operations mDbHelper = new database_operations(context);
        // get writable database as we want to write data
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SQLiteDatabase db2 = mDbHelper.getReadableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(user_data_model.name_sql, name);
        values.put(user_data_model.token_sql, token);
        values.put(user_data_model.image_url_sql, image_url);
        values.put(user_data_model.status_sql, status);
        values.put(user_data_model.country_sql, country);
        values.put(user_data_model.gender_sql, gender);
        values.put(user_data_model.age_sql, age);
        values.put(user_data_model.language_sql, language);
        values.put(user_data_model.phone_sql, phone);
// Insert the new row, returning the primary key value of the new row
        String countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.phone_sql+"=?";
        Cursor cursor = db2.rawQuery(countQuery, new String[]{phone});

        if (!cursor.moveToNext()) {
            db.insert(user_data_model.TABLE_NAME, null, values);
        }else {
            update_user_model(name,token,image_url,status,country,gender,age,language,phone);
        }
        db2.close();




        // return newly inserted row id

    }
    public List<user_data_model> getAll_users_model(String name,int age,String gender,String country)
    {
        List<user_data_model> user_data_models = new ArrayList<>();
        String countQuery;
        SQLiteDatabase db = this.getReadableDatabase();


        // Select All Query
        if (name.length()>0&&age==0&&gender.length()==0&&country.length()==0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.name_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{name});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else if (gender.length()>0&&age==0&&name.length()==0&&country.length()==0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.gender_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{gender});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);


                } while (cursor.moveToNext());
            }

        }else if (country.length()>0&&age==0&&name.length()==0&&gender.length()==0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.country_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{country});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }

        }else if (gender.length()==0&&age>0&&name.length()==0&&country.length()==0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.age_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{String.valueOf(age)});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else if (name.length()>0&&age>0&&gender.length()==0&&country.length()==0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.name_sql+"=?"+" AND "+user_data_model.age_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{name, String.valueOf(age)});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else if (name.length()>0&&age==0&&gender.length()>0&&country.length()==0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.name_sql+"=?"+" AND "+user_data_model.gender_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{name, gender});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else if (name.length()>0&&age==0&&gender.length()==0&&country.length()>0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.name_sql+"=?"+" AND "+user_data_model.country_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{name,country});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else if (name.length()==0&&age>0&&gender.length()>0&&country.length()==0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.age_sql+"=?"+" AND "+user_data_model.gender_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{String.valueOf(age),gender});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else if (name.length()==0&&age>0&&gender.length()==0&&country.length()>0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.age_sql+"=?"+" AND "+user_data_model.country_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{String.valueOf(age),country});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else if (name.length()==0&&age==0&&gender.length()>0&&country.length()>0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.gender_sql+"=?"+" AND "+user_data_model.country_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{gender,country});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else if (name.length()>0&&age>0&&gender.length()>0&&country.length()==0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.name_sql+"=?"+" AND "+user_data_model.age_sql+"=?"
            +" AND "+user_data_model.gender_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{name, String.valueOf(age),gender});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else if (name.length()>0&&age>0&&gender.length()==0&&country.length()>0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.name_sql+"=?"+" AND "+user_data_model.age_sql+"=?"
                    +" AND "+user_data_model.gender_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{name, String.valueOf(age),country});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else if (name.length()>0&&age==0&&gender.length()>0&&country.length()>0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.name_sql+"=?"+" AND "+user_data_model.country_sql+"=?"
                    +" AND "+user_data_model.gender_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{name, country,gender});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else if (name.length()==0&&age>0&&gender.length()>0&&country.length()>0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.age_sql+"=?"+" AND "+user_data_model.country_sql+"=?"
                    +" AND "+user_data_model.gender_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{String.valueOf(age), country,gender});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else if (name.length()>0&&age>0&&gender.length()>0&&country.length()>0){
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.age_sql+"=?"+" AND "+user_data_model.country_sql+"=?"
                    +" AND "+user_data_model.gender_sql+"=?"+" AND "+user_data_model.name_sql+"=?";
            Cursor cursor = db.rawQuery(countQuery, new String[]{String.valueOf(age), country,gender,name});
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }else {
            countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME;
            Cursor cursor = db.rawQuery(countQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    user_data_model User_data_model=new user_data_model();
                    User_data_model.name=cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
                    User_data_model.age=cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
                    User_data_model.gender=cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
                    User_data_model.country=cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
                    User_data_model.image_url=cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
                    User_data_model.token=cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
                    User_data_model.status=cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
                    User_data_model.language=cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
                    User_data_model.phone=cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
                    user_data_models.add(User_data_model);
                } while (cursor.moveToNext());
            }
        }


        Log.w("kjhsajdaj",countQuery);

        // looping through all rows and adding to list

        // close db connection

        db.close();

        // return notes list
        return user_data_models;
    }

    public user_data_model getAll_users_model(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();

        String countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME+" WHERE "+user_data_model.phone_sql+"=?"+" OR "+user_data_model.token_sql+"=?";
        Cursor cursor = db.rawQuery(countQuery, new String[]{phone,phone});
        user_data_model User_data_model = new user_data_model();

        if (cursor.moveToFirst()) {


            User_data_model.name = cursor.getString(cursor.getColumnIndex(user_data_model.name_sql));
            User_data_model.age = cursor.getInt(cursor.getColumnIndex(user_data_model.age_sql));
            User_data_model.gender = cursor.getString(cursor.getColumnIndex(user_data_model.gender_sql));
            User_data_model.country = cursor.getString(cursor.getColumnIndex(user_data_model.country_sql));
            User_data_model.image_url = cursor.getString(cursor.getColumnIndex(user_data_model.image_url_sql));
            User_data_model.token = cursor.getString(cursor.getColumnIndex(user_data_model.token_sql));
            User_data_model.status = cursor.getString(cursor.getColumnIndex(user_data_model.status_sql));
            User_data_model.language = cursor.getString(cursor.getColumnIndex(user_data_model.language_sql));
            User_data_model.phone = cursor.getString(cursor.getColumnIndex(user_data_model.phone_sql));
            return User_data_model;
        }else {
            return null;
        }
        // return contact

    }
    public int getusersCount()
    {

        String countQuery = "SELECT  * FROM " + user_data_model.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);


        int count = cursor.getCount();

        cursor.close();



        // return count

        return count;
    }
    public void delete_all_search()
    {

        String countQuery = "delete FROM " + user_data_model.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.close();



    }

    public void update_user_model(String name, String token
            ,String image_url,String status,String country
            ,String gender,int age,String language,String phone)
    {
        database_operations mDbHelper = new database_operations(context);
        // get writable database as we want to write data
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(user_data_model.name_sql, name);
        values.put(user_data_model.token_sql, token);
        values.put(user_data_model.image_url_sql, image_url);
        values.put(user_data_model.status_sql, status);
        values.put(user_data_model.country_sql, country);
        values.put(user_data_model.gender_sql, gender);
        values.put(user_data_model.age_sql, age);
        values.put(user_data_model.language_sql, language);
        values.put(user_data_model.phone_sql, phone);
        db.update(user_data_model.TABLE_NAME,values, user_data_model.phone_sql+ " = ?", new String[]{phone});

    }





}
