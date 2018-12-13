package com.example.gaber.translation_chat.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.adapters.spinner_adapter;
import com.example.gaber.translation_chat.fragments.chat_recycler;
import com.example.gaber.translation_chat.models.Language;
import com.example.gaber.translation_chat.models.user_data_model;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by gaber on 13/08/2018.
 */

public class login extends AppCompatActivity {
    EditText user_name,country,age;
    SharedPreferences prefs ;
    private Button btnSignup,btnipload;
    private StorageReference mStorageRef;
    int PICK_IMAGE_MULTIPLE = 00;
    boolean image_uploaded=false;
    Spinner spinner;
    String image_url,lang_code;
    public List<Language> LanguagesList = new ArrayList<>();
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        btnSignup=(Button) findViewById(R.id.btnSignup);
        user_name=(EditText)findViewById(R.id.user_name);
        country=(EditText)findViewById(R.id.country);
        age=(EditText)findViewById(R.id.age);
         btnipload=(Button) findViewById(R.id.btnipload);
        radioGroup = (RadioGroup) findViewById(R.id.radio);
        radioButton = (RadioButton) findViewById(R.id.male);
        Initialize();
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner_adapter adapter = new spinner_adapter(this,
                R.layout.customspinneritem, LanguagesList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                lang_code=LanguagesList.get(pos).Code;
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        country.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                    picker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                            country.setText(name);
                            picker.dismiss();
                        }
                    });
                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                }
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference();


        //check if user logged in before
        prefs = getSharedPreferences("logged_in", MODE_PRIVATE);
        boolean state = prefs.getBoolean("state", false);
          if (state!=false){

           sign_in(prefs.getString("name", ""),prefs.getString("pass", ""));
        }
        //check user
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=user_name.getText().toString();
                String refreshedtoken= FirebaseInstanceId.getInstance().getToken();
                String countryy= country.getText().toString();
                int agee= Integer.parseInt(age.getText().toString());
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId==-1)
                    selectedId=R.id.male;
                radioButton = (RadioButton) findViewById(selectedId);
                String gender_string=radioButton.getText().toString();
                if (name.length()>0)
                {
                    check_user(refreshedtoken,name,countryy,agee,gender_string,lang_code);
                }else {
                    Toast.makeText(getApplicationContext(),"please fill fields",Toast.LENGTH_LONG).show();

                }

            }
        });











        btnipload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_images();
            }
        });
    }
    private void sign_up(String user_token,String name,String image_url,String country,int age,String gender,String language)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        user_data_model user=new user_data_model(name,user_token,image_url,"Online",country,gender,age,language);
        myRef.push().setValue(user);
        SharedPreferences.Editor editor = getSharedPreferences("logged_in", MODE_PRIVATE).edit();
        editor.putBoolean("state",true);
        editor.putString("name",name);
        editor.putString("language",language);
        editor.apply();
        Intent main=new Intent(login.this,chat_recycler.class);
        startActivity(main);
        finish();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_MULTIPLE) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            for (String uri:returnValue){
                upload_image(uri);
            }
        }
    }

   private void check_user(final String user_token, final String name, final String country, final int age, final String gender, final String language)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("phone").equalTo(getIntent().getStringExtra("phone_number"));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot sub_type : dataSnapshot.getChildren()) {
                        DatabaseReference myRef = sub_type.getRef();
                        myRef.child("name").setValue(name);
                        myRef.child("country").setValue(country);
                        myRef.child("age").setValue(age);
                        myRef.child("gender").setValue(gender);
                        myRef.child("language").setValue(language);
                    }
                    sign_in(name,language);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
    private void sign_in(final String name,String language)
    {
        SharedPreferences.Editor editor = getSharedPreferences("logged_in", MODE_PRIVATE).edit();
        editor.putBoolean("state",true);
        editor.putString("name",name);
        editor.putString("language",language);
        editor.apply();
        Intent main=new Intent(login.this,main_activity.class);
        startActivity(main);
        finish();
        Toast.makeText(getApplicationContext(),"welcome",Toast.LENGTH_LONG).show();

    }
    private void upload_image(String audioFilePath)
    {
        Uri file = Uri.fromFile(new File(audioFilePath));
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        final StorageReference ref = mStorageRef.child("profile image").child(audioFilePath);
        UploadTask uploadTask = ref.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    image_uploaded=true;
                    image_url=downloadUri.toString();
                    progressDialog.dismiss();

                } else {
                    // Handle failures
                    // ...
                    progressDialog.dismiss();

                }
            }

        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                progressDialog.setMessage(String.valueOf(progress)+"% Uploaded");
            }
        });



    }
    private void select_images()
    {
        Pix.start(this,
                PICK_IMAGE_MULTIPLE,1);
    }
    protected void AddLang(String code, String name, String nativeName)
    {
        Language lang = new Language(code, name, nativeName);
        LanguagesList.add(lang);
    }
    public void Initialize()
    {
        AddLang("ab", "Abkhaz", "аҧсуа");

        AddLang(
                "aa",
                "Afar",
                "Afaraf"
        );
        AddLang(
                "af",
                "Afrikaans",
                "Afrikaans"
        );
        AddLang(
                "ak",
                "Akan",
                "Akan"
        );
        AddLang(
                "sq",
                "Albanian",
                "Shqip"
        );
        AddLang(
                "am",
                "Amharic",
                "አማርኛ"
        );
        AddLang(
                "ar",
                "Arabic",
                "العربية"
        );
        AddLang(
                "an",
                "Aragonese",
                "Aragonés"
        );
        AddLang(
                "hy",
                "Armenian",
                "Հայերեն"
        );
        AddLang(
                "as",
                "Assamese",
                "অসমীয়া"
        );
        AddLang(
                "av",
                "Avaric",
                "авар мацӀ, магӀарул мацӀ"
        );
        AddLang(
                "ae",
                "Avestan",
                "avesta"
        );
        AddLang(
                "ay",
                "Aymara",
                "aymar aru"
        );
        AddLang(
                "az",
                "Azerbaijani",
                "azərbaycan dili"
        );
        AddLang(
                "bm",
                "Bambara",
                "bamanankan"
        );
        AddLang(
                "ba",
                "Bashkir",
                "башҡорт теле"
        );
        AddLang(
                "eu",
                "Basque",
                "euskara, euskera"
        );
        AddLang(
                "be",
                "Belarusian",
                "Беларуская"
        );
        AddLang(
                "bn",
                "Bengali",
                "বাংলা"
        );
        AddLang(
                "bh",
                "Bihari",
                "भोजपुरी"
        );
        AddLang(
                "bi",
                "Bislama",
                "Bislama"
        );
        AddLang(
                "bs",
                "Bosnian",
                "bosanski jezik"
        );
        AddLang(
                "br",
                "Breton",
                "brezhoneg"
        );
        AddLang(
                "bg",
                "Bulgarian",
                "български език"
        );
        AddLang(
                "my",
                "Burmese",
                "ဗမာစာ"
        );
        AddLang(
                "ca",
                "Catalan; Valencian",
                "Català"
        );
        AddLang(
                "ch",
                "Chamorro",
                "Chamoru"
        );
        AddLang(
                "ce",
                "Chechen",
                "нохчийн мотт"
        );
        AddLang(
                "ny",
                "Chichewa; Chewa; Nyanja",
                "chiCheŵa, chinyanja"
        );
        AddLang(
                "zh",
                "Chinese",
                "中文 (Zhōngwén), 汉语, 漢語"
        );
        AddLang(
                "cv",
                "Chuvash",
                "чӑваш чӗлхи"
        );
        AddLang(
                "kw",
                "Cornish",
                "Kernewek"
        );
        AddLang(
                "co",
                "Corsican",
                "corsu, lingua corsa"
        );
        AddLang(
                "cr",
                "Cree",
                "ᓀᐦᐃᔭᐍᐏᐣ"
        );
        AddLang(
                "hr",
                "Croatian",
                "hrvatski"
        );
        AddLang(
                "cs",
                "Czech",
                "česky, čeština"
        );
        AddLang(
                "da",
                "Danish",
                "dansk"
        );
        AddLang(
                "dv",
                "Divehi; Dhivehi; Maldivian;",
                "ދިވެހި"
        );
        AddLang(
                "nl",
                "Dutch",
                "Nederlands, Vlaams"
        );
        AddLang(
                "en",
                "English",
                "English"
        );
        AddLang(
                "eo",
                "Esperanto",
                "Esperanto"
        );
        AddLang(
                "et",
                "Estonian",
                "eesti, eesti keel"
        );
        AddLang(
                "ee",
                "Ewe",
                "Eʋegbe"
        );
        AddLang(
                "fo",
                "Faroese",
                "føroyskt"
        );
        AddLang(
                "fj",
                "Fijian",
                "vosa Vakaviti"
        );
        AddLang(
                "fi",
                "Finnish",
                "suomi, suomen kieli"
        );
        AddLang(
                "fr",
                "French",
                "français, langue française"
        );
        AddLang(
                "ff",
                "Fula; Fulah; Pulaar; Pular",
                "Fulfulde, Pulaar, Pular"
        );
        AddLang(
                "gl",
                "Galician",
                "Galego"
        );
        AddLang(
                "ka",
                "Georgian",
                "ქართული"
        );
        AddLang(
                "de",
                "German",
                "Deutsch"
        );
        AddLang(
                "el",
                "Greek, Modern",
                "Ελληνικά"
        );
        AddLang(
                "gn",
                "Guaraní",
                "Avañeẽ"
        );
        AddLang(
                "gu",
                "Gujarati",
                "ગુજરાતી"
        );
        AddLang(
                "ht",
                "Haitian; Haitian Creole",
                "Kreyòl ayisyen"
        );
        AddLang(
                "ha",
                "Hausa",
                "Hausa, هَوُسَ"
        );
        AddLang(
                "he",
                "Hebrew (modern)",
                "עברית"
        );
        AddLang(
                "hz",
                "Herero",
                "Otjiherero"
        );
        AddLang(
                "hi",
                "Hindi",
                "हिन्दी, हिंदी"
        );
        AddLang(
                "ho",
                "Hiri Motu",
                "Hiri Motu"
        );
        AddLang(
                "hu",
                "Hungarian",
                "Magyar"
        );
        AddLang(
                "ia",
                "Interlingua",
                "Interlingua"
        );
        AddLang(
                "id",
                "Indonesian",
                "Bahasa Indonesia"
        );
        AddLang(
                "ie",
                "Interlingue",
                "Originally called Occidental; then Interlingue after WWII"
        );
        AddLang(
                "ga",
                "Irish",
                "Gaeilge"
        );
        AddLang(
                "ig",
                "Igbo",
                "Asụsụ Igbo"
        );
        AddLang(
                "ik",
                "Inupiaq",
                "Iñupiaq, Iñupiatun"
        );
        AddLang(
                "io",
                "Ido",
                "Ido"
        );
        AddLang(
                "is",
                "Icelandic",
                "Íslenska"
        );
        AddLang(
                "it",
                "Italian",
                "Italiano"
        );
        AddLang(
                "iu",
                "Inuktitut",
                "ᐃᓄᒃᑎᑐᑦ"
        );
        AddLang(
                "ja",
                "Japanese",
                "日本語 (にほんご／にっぽんご)"
        );
        AddLang(
                "jv",
                "Javanese",
                "basa Jawa"
        );
        AddLang(
                "kl",
                "Kalaallisut, Greenlandic",
                "kalaallisut, kalaallit oqaasii"
        );
        AddLang(
                "kn",
                "Kannada",
                "ಕನ್ನಡ"
        );
        AddLang(
                "kr",
                "Kanuri",
                "Kanuri"
        );
        AddLang(
                "ks",
                "Kashmiri",
                "कश्मीरी, كشميري‎"
        );
        AddLang(
                "kk",
                "Kazakh",
                "Қазақ тілі"
        );
        AddLang(
                "km",
                "Khmer",
                "ភាសាខ្មែរ"
        );
        AddLang(
                "ki",
                "Kikuyu, Gikuyu",
                "Gĩkũyũ"
        );
        AddLang(
                "rw",
                "Kinyarwanda",
                "Ikinyarwanda"
        );
        AddLang(
                "ky",
                "Kirghiz, Kyrgyz",
                "кыргыз тили"
        );
        AddLang(
                "kv",
                "Komi",
                "коми кыв"
        );
        AddLang(
                "kg",
                "Kongo",
                "KiKongo"
        );
        AddLang(
                "ko",
                "Korean",
                "한국어 (韓國語), 조선말 (朝鮮語)"
        );
        AddLang(
                "ku",
                "Kurdish",
                "Kurdî, كوردی‎"
        );
        AddLang(
                "kj",
                "Kwanyama, Kuanyama",
                "Kuanyama"
        );
        AddLang(
                "la",
                "Latin",
                "latine, lingua latina"
        );
        AddLang(
                "lb",
                "Luxembourgish, Letzeburgesch",
                "Lëtzebuergesch"
        );
        AddLang(
                "lg",
                "Luganda",
                "Luganda"
        );
        AddLang(
                "li",
                "Limburgish, Limburgan, Limburger",
                "Limburgs"
        );
        AddLang(
                "ln",
                "Lingala",
                "Lingála"
        );
        AddLang(
                "lo",
                "Lao",
                "ພາສາລາວ"
        );
        AddLang(
                "lt",
                "Lithuanian",
                "lietuvių kalba"
        );
        AddLang(
                "lu",
                "Luba-Katanga",
                ""
        );
        AddLang(
                "lv",
                "Latvian",
                "latviešu valoda"
        );
        AddLang(
                "gv",
                "Manx",
                "Gaelg, Gailck"
        );
        AddLang(
                "mk",
                "Macedonian",
                "македонски јазик"
        );
        AddLang(
                "mg",
                "Malagasy",
                "Malagasy fiteny"
        );
        AddLang(
                "ms",
                "Malay",
                "bahasa Melayu, بهاس ملايو‎"
        );
        AddLang(
                "ml",
                "Malayalam",
                "മലയാളം"
        );
        AddLang(
                "mt",
                "Maltese",
                "Malti"
        );
        AddLang(
                "mi",
                "Māori",
                "te reo Māori"
        );
        AddLang(
                "mr",
                "Marathi (Marāṭhī)",
                "मराठी"
        );
        AddLang(
                "mh",
                "Marshallese",
                "Kajin M̧ajeļ"
        );
        AddLang(
                "mn",
                "Mongolian",
                "монгол"
        );
        AddLang(
                "na",
                "Nauru",
                "Ekakairũ Naoero"
        );
        AddLang(
                "nv",
                "Navajo, Navaho",
                "Diné bizaad, Dinékʼehǰí"
        );
        AddLang(
                "nb",
                "Norwegian Bokmål",
                "Norsk bokmål"
        );
        AddLang(
                "nd",
                "North Ndebele",
                "isiNdebele"
        );
        AddLang(
                "ne",
                "Nepali",
                "नेपाली"
        );
        AddLang(
                "ng",
                "Ndonga",
                "Owambo"
        );
        AddLang(
                "nn",
                "Norwegian Nynorsk",
                "Norsk nynorsk"
        );
        AddLang(
                "no",
                "Norwegian",
                "Norsk"
        );
        AddLang(
                "ii",
                "Nuosu",
                "ꆈꌠ꒿ Nuosuhxop"
        );
        AddLang(
                "nr",
                "South Ndebele",
                "isiNdebele"
        );
        AddLang(
                "oc",
                "Occitan",
                "Occitan"
        );
        AddLang(
                "oj",
                "Ojibwe, Ojibwa",
                "ᐊᓂᔑᓈᐯᒧᐎᓐ"
        );
        AddLang(
                "cu",
                "Old Church Slavonic, Church Slavic, Church Slavonic, Old Bulgarian, Old Slavonic",
                "ѩзыкъ словѣньскъ"
        );
        AddLang(
                "om",
                "Oromo",
                "Afaan Oromoo"
        );
        AddLang(
                "or",
                "Oriya",
                "ଓଡ଼ିଆ"
        );
        AddLang(
                "os",
                "Ossetian, Ossetic",
                "ирон æвзаг"
        );
        AddLang(
                "pa",
                "Panjabi, Punjabi",
                "ਪੰਜਾਬੀ, پنجابی‎"
        );
        AddLang(
                "pi",
                "Pāli",
                "पाऴि"
        );
        AddLang(
                "fa",
                "Persian",
                "فارسی"
        );
        AddLang(
                "pl",
                "Polish",
                "Polski"
        );
        AddLang(
                "ps",
                "Pashto, Pushto",
                "پښتو"
        );
        AddLang(
                "pt",
                "Portuguese",
                "Português"
        );
        AddLang(
                "qu",
                "Quechua",
                "Runa Simi, Kichwa"
        );
        AddLang(
                "rm",
                "Romansh",
                "rumantsch grischun"
        );
        AddLang(
                "rn",
                "Kirundi",
                "kiRundi"
        );
        AddLang(
                "ro",
                "Romanian, Moldavian, Moldovan",
                "română"
        );
        AddLang(
                "ru",
                "Russian",
                "русский язык"
        );
        AddLang(
                "sa",
                "Sanskrit (Saṁskṛta)",
                "संस्कृतम्"
        );
        AddLang(
                "sc",
                "Sardinian",
                "sardu"
        );
        AddLang(
                "sd",
                "Sindhi",
                "सिन्धी, سنڌي، سندھی‎"
        );
        AddLang(
                "se",
                "Northern Sami",
                "Davvisámegiella"
        );
        AddLang(
                "sm",
                "Samoan",
                "gagana faa Samoa"
        );
        AddLang(
                "sg",
                "Sango",
                "yângâ tî sängö"
        );
        AddLang(
                "sr",
                "Serbian",
                "српски језик"
        );
        AddLang(
                "gd",
                "Scottish Gaelic; Gaelic",
                "Gàidhlig"
        );
        AddLang(
                "sn",
                "Shona",
                "chiShona"
        );
        AddLang(
                "si",
                "Sinhala, Sinhalese",
                "සිංහල"
        );
        AddLang(
                "sk",
                "Slovak",
                "slovenčina"
        );
        AddLang(
                "sl",
                "Slovene",
                "slovenščina"
        );
        AddLang(
                "so",
                "Somali",
                "Soomaaliga, af Soomaali"
        );
        AddLang(
                "st",
                "Southern Sotho",
                "Sesotho"
        );
        AddLang(
                "es",
                "Spanish; Castilian",
                "español, castellano"
        );
        AddLang(
                "su",
                "Sundanese",
                "Basa Sunda"
        );
        AddLang(
                "sw",
                "Swahili",
                "Kiswahili"
        );
        AddLang(
                "ss",
                "Swati",
                "SiSwati"
        );
        AddLang(
                "sv",
                "Swedish",
                "svenska"
        );
        AddLang(
                "ta",
                "Tamil",
                "தமிழ்"
        );
        AddLang(
                "te",
                "Telugu",
                "తెలుగు"
        );
        AddLang(
                "tg",
                "Tajik",
                "тоҷикӣ, toğikī, تاجیکی‎"
        );
        AddLang(
                "th",
                "Thai",
                "ไทย"
        );
        AddLang(
                "ti",
                "Tigrinya",
                "ትግርኛ"
        );
        AddLang(
                "bo",
                "Tibetan Standard, Tibetan, Central",
                "བོད་ཡིག"
        );
        AddLang(
                "tk",
                "Turkmen",
                "Türkmen, Түркмен"
        );
        AddLang(
                "tl",
                "Tagalog",
                "Wikang Tagalog, ᜏᜒᜃᜅ᜔ ᜆᜄᜎᜓᜄ᜔"
        );
        AddLang(
                "tn",
                "Tswana",
                "Setswana"
        );
        AddLang(
                "to",
                "Tonga (Tonga Islands)",
                "faka Tonga"
        );
        AddLang(
                "tr",
                "Turkish",
                "Türkçe"
        );
        AddLang(
                "ts",
                "Tsonga",
                "Xitsonga"
        );
        AddLang(
                "tt",
                "Tatar",
                "татарча, tatarça, تاتارچا‎"
        );
        AddLang(
                "tw",
                "Twi",
                "Twi"
        );
        AddLang(
                "ty",
                "Tahitian",
                "Reo Tahiti"
        );
        AddLang(
                "ug",
                "Uighur, Uyghur",
                "Uyƣurqə, ئۇيغۇرچە‎"
        );
        AddLang(
                "uk",
                "Ukrainian",
                "українська"
        );
        AddLang(
                "ur",
                "Urdu",
                "اردو"
        );
        AddLang(
                "uz",
                "Uzbek",
                "zbek, Ўзбек, أۇزبېك‎"
        );
        AddLang(
                "ve",
                "Venda",
                "Tshivenḓa"
        );
        AddLang(
                "vi",
                "Vietnamese",
                "Tiếng Việt"
        );
        AddLang(
                "vo",
                "Volapük",
                "Volapük"
        );
        AddLang(
                "wa",
                "Walloon",
                "Walon"
        );
        AddLang(
                "cy",
                "Welsh",
                "Cymraeg"
        );
        AddLang(
                "wo",
                "Wolof",
                "Wollof"
        );
        AddLang(
                "fy",
                "Western Frisian",
                "Frysk"
        );
        AddLang(
                "xh",
                "Xhosa",
                "isiXhosa"
        );
        AddLang(
                "yi",
                "Yiddish",
                "ייִדיש"
        );
        AddLang(
                "yo",
                "Yoruba",
                "Yorùbá"
        );
        AddLang(
                "za",
                "Zhuang, Chuang",
                "Saɯ cueŋƅ, Saw cuengh");
    }



}
