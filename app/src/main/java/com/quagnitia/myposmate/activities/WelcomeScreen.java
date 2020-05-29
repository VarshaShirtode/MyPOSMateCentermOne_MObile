package com.quagnitia.myposmate.activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.quagnitia.myposmate.R;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        new CountDownTimer(2000, 1000) {
            public void onFinish() {
                Intent intent = new Intent(WelcomeScreen.this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            public void onTick(long millisUntilFinished) {
            }

        }.start();
    }
}
