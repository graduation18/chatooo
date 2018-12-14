package com.example.gaber.translation_chat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaber.translation_chat.R;
import com.example.gaber.translation_chat.models.user_data_model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.poovam.pinedittextfield.LinePinField;
import com.poovam.pinedittextfield.PinField;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Created by gaber on 11/30/2018.
 */

public class confirm_code extends AppCompatActivity {
    FirebaseAuth auth;
    String vervication_id,phone;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    LinePinField linePinField;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_code);
        auth=FirebaseAuth.getInstance();
        linePinField = findViewById(R.id.lineField);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                String code=credential.getSmsCode();
                linePinField.setText(code);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String mverificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                                vervication_id = mverificationId;

            }
        };
        linePinField.setOnTextCompleteListener(new PinField.OnTextCompleteListener() {
            @Override
            public boolean onTextComplete (@NotNull String enteredText) {
                Log.w("khgj",linePinField.getText().toString()+"   "+vervication_id);
                sign_in(vervication_id,linePinField.getText().toString());
                return true; // Return true to keep the keyboard open else return false to close the keyboard
            }
        });
        phone=getIntent().getStringExtra("phone_number");
        get_verfiy_code(phone);


    }
    private void get_verfiy_code(String phone_number)
    {
        int number_of_tries=getSharedPreferences("request_code",MODE_PRIVATE).getInt("number_of_tries",0);
        getSharedPreferences("request_code",MODE_PRIVATE).edit()
                .putInt("number_of_tries",number_of_tries+1)
                .apply();
        if (number_of_tries>3){
            getSharedPreferences("request_code",MODE_PRIVATE).edit()
                    .putInt("timer",1)
                    .apply();
        }else if (number_of_tries>4){
            getSharedPreferences("request_code",MODE_PRIVATE).edit()
                    .putInt("timer",3)
                    .apply();
        }else if (number_of_tries>5){
            getSharedPreferences("request_code",MODE_PRIVATE).edit()
                    .putInt("timer",60)
                    .apply();
        }else if (number_of_tries>7){
            getSharedPreferences("request_code",MODE_PRIVATE).edit()
                    .putInt("timer",20*60)
                    .apply();
        }
        int timer=getSharedPreferences("request_code",MODE_PRIVATE).getInt("timer",0);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone_number,        // Phone number to verify
                timer,                 // Timeout duration
                TimeUnit.MINUTES,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);// OnVerificationStateChangedCallbacks

    }
    private void sign_in(String vervication_id,String code)
    {
        PhoneAuthCredential authCredential= PhoneAuthProvider.getCredential(vervication_id,code);
        signInWithPhoneAuthCredential(authCredential);
    }
    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential)
    {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String refreshedtoken= FirebaseInstanceId.getInstance().getToken();
                            getSharedPreferences("request_code",MODE_PRIVATE).edit()
                                    .putInt("number_of_tries",0)
                                    .putInt("timer",0)
                                    .apply();

                            check_user(refreshedtoken,phone,credential.getSmsCode());


                        }else {
                            Log.w("khgj",task.getException());

                            Toast.makeText(confirm_code.this,"Login unsuccessful ", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
    private void sign_up(String user_token,String phone)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        user_data_model user=new user_data_model("",user_token,"","Online","","",00,"en",phone);
        myRef.push().setValue(user);
        Intent main=new Intent(confirm_code.this,login.class);
        main.putExtra("phone_number",phone);
        startActivity(main);
        finish();

    }
    private void check_user(final String user_token, final String phone, final String pass)
    {
        Log.w("khgj","check_user");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("phone").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot sub_type : dataSnapshot.getChildren()) {
                        if (!FirebaseInstanceId.getInstance().getToken().equals(sub_type.child("token").getValue())){
                            DatabaseReference myRef = sub_type.getRef();
                            myRef.child("token").setValue(FirebaseInstanceId.getInstance().getToken());

                        }
                        SharedPreferences.Editor editor = getSharedPreferences("logged_in", MODE_PRIVATE).edit();
                        editor.putBoolean("state",true);
                        editor.apply();
                        Intent main=new Intent(confirm_code.this,main_activity.class);
                        startActivity(main);
                        finish();
                        Toast.makeText(getApplicationContext(),"welcome",Toast.LENGTH_LONG).show();


                    }
                }else {
                    sign_up(user_token,phone);
                    Toast.makeText(getApplicationContext(),"welcome",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("khgj",databaseError.getMessage());


            }
        });

    }


}
