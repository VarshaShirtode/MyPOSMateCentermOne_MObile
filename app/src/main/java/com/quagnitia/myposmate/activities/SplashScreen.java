package com.quagnitia.myposmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.utils.PreferencesManager;

public class SplashScreen extends AppCompatActivity {
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        preferencesManager = PreferencesManager.getInstance(this);
        if (!preferencesManager.isAlipaySelected()
                && !preferencesManager.isWechatSelected() &&
                !preferencesManager.isUnipaySelected() &&
                !!preferencesManager.isVisaSlelected()) {
            preferencesManager.setisAlipaySelected(true);
            preferencesManager.setisUnipaySelected(true);
            preferencesManager.setisVisaSlelected(true);
            preferencesManager.setisWechatSelected(true);
        }


        new CountDownTimer(2000, 1000) {
            public void onFinish() {

                Intent intent = new Intent(SplashScreen.this, WelcomeScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

            public void onTick(long millisUntilFinished) {
            }

        }.start();

    }
}
