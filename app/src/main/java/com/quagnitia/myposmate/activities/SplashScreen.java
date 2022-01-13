package com.quagnitia.myposmate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.utils.PreferencesManager;

public class SplashScreen extends AppCompatActivity {
    private PreferencesManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        preferenceManager = PreferencesManager.getInstance(this);
        if (!preferenceManager.isAlipaySelected()
                && !preferenceManager.isWechatSelected() &&
                !preferenceManager.isUnipaySelected() &&
                !preferenceManager.isVisaSlelected()) {
            preferenceManager.setisAlipaySelected(true);
            preferenceManager.setisUnipaySelected(true);
            preferenceManager.setisVisaSlelected(true);
            preferenceManager.setisWechatSelected(true);
        }

        new CountDownTimer(2000, 1000) {
            public void onFinish() {

//                Intent intent = new Intent(SplashScreen.this, DashboardActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finishAffinity();


                Intent intent = new Intent(SplashScreen.this, WelcomeScreen.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
//                finish();
            }

            public void onTick(long millisUntilFinished) {
//                TextView t= findViewById(R.id.tv1);
//                        t.setText("Welcome MyPOSMate®");
            }

        }.start();

    }
}
