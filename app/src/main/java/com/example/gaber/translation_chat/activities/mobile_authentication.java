package com.example.gaber.translation_chat.activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gaber.translation_chat.R;
import com.rilixtech.CountryCodePicker;


public class mobile_authentication extends AppCompatActivity {


    ProgressBar progress1;
    CountryCodePicker ccp;
    AppCompatEditText edtPhoneNumber;
    CountDownTimer cTimer = null;
    TextView timer;
    Button request_verify;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSharedPreferences("logged_in",MODE_PRIVATE).getBoolean("state",false)){
            Intent main=new Intent(this,main_activity.class);
            startActivity(main);
            finish();
        }
        setContentView(R.layout.activity_mobile_authentication);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        edtPhoneNumber = (AppCompatEditText) findViewById(R.id.phone_number_edt);
        progress1=(ProgressBar)findViewById(R.id.progress1);
        timer=(TextView)findViewById(R.id.timer);
        request_verify=(Button)findViewById(R.id.request_verify);
        ccp.registerPhoneNumberTextView(edtPhoneNumber);


    }


    public void verfiy(View view) {
        String phone="+"+ccp.getSelectedCountryCode()+edtPhoneNumber.getText().toString();
        if (phone.length()==13){
            Intent got_confirm_code=new Intent(mobile_authentication.this,confirm_code.class);
            got_confirm_code.putExtra("phone_number",phone);
            startActivity(got_confirm_code);
            finish();
        }
        startTimer();

    }

    private void startTimer() {
        final int time=getSharedPreferences("request_code",MODE_PRIVATE).getInt("timer",0);
        if (time>0) {
            cTimer = new CountDownTimer(time * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    timer.setText(String.valueOf(millisUntilFinished));

                }

                public void onFinish() {
                    request_verify.setEnabled(false);
                }

            };
            cTimer.start();
        }
    }

}


