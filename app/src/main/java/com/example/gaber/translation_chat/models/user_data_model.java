package com.example.gaber.translation_chat.models;

/**
 * Created by gaber on 12/08/2018.
 */

public class user_data_model {

    public static final String TABLE_NAME = "users";


    public static final String name_sql = "name_sql";
    public static final String token_sql = "token_sql";
    public static final String image_url_sql = "image_url_sql";
    public static final String status_sql = "status_sql";
    public static final String country_sql = "country_sql";
    public static final String gender_sql = "gender_sql";
    public static final String age_sql = "age_sql";
    public static final String language_sql = "language_sql";
    public static final String phone_sql = "phone_sql";


    public String name;
    public String token;
    public String image_url;
    public String status;
    public String country;
    public String gender;
    public int age;
    public String language;
    public String phone;


    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + name_sql + " TEXT ,"
                    + token_sql + " TEXT,"
                    + image_url_sql + " TEXT,"
                    + status_sql + " TEXT,"
                    + country_sql + " TEXT,"
                    + gender_sql + " TEXT,"
                    + language_sql + " TEXT,"
                    + phone_sql + " TEXT,"
                    +age_sql+" Integer"
                    + ")";
public user_data_model(){

}


    public user_data_model(String name, String token
            ,String image_url,String status,String country
            ,String gender,int age,String language,String phone) {
        this.name = name;
        this.token = token;
        this.image_url=image_url;
        this.status=status;
        this.country=country;
        this.gender=gender;
        this.age=age;
        this.language=language;
        this.phone=phone;
    }

}
