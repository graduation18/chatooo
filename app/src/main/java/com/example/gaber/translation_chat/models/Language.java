package com.example.gaber.translation_chat.models;

/**
 * Created by gaber on 10/10/2018.
 */




    public class Language
    {
        public String Name;
        public String Code;
        public String NativeName;

        public Language(String code, String name, String nativeName)
        {
            Code = code;
            Name = name;
            NativeName = nativeName;
        }

        public Language()
        {
        }
    }

