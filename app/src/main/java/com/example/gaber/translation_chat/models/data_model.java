package com.example.gaber.translation_chat.models;

/**
 * Created by gaber on 12/08/2018.
 */

public class data_model {
    public static final String TABLE_NAME = "chat";

    public static final String to_sql = "to_sql";
    public static final String from_sql = "from_sql";
    public static final String message_sql = "message_sql";
    public static final String type_sql = "message_type";
    public static final String time_sql = "message_time";
    public static final String storage_url_sql = "storage_url";
    public static final String lang_pair_sql = "lang_pair_sql";

    public String from;
    public String to;
    public String message;
    public String type;
    public int time;
    public String storage_url;
    public String lang_pair;



    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + from_sql + " TEXT ,"
                    + message_sql + " TEXT,"
                    + type_sql + " TEXT,"
                    + time_sql + " INTEGER,"
                    + storage_url_sql + " TEXT,"
                    + lang_pair_sql + " TEXT,"
                    +to_sql+" TEXT"
                    + ")";



    public data_model(String from,String to,String message,String type,int time,String storage_url,String lang_pair) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.type = type;
        this.time = time;
        this.storage_url = storage_url;
        this.lang_pair = lang_pair;

    }
    public data_model() {

    }

}
