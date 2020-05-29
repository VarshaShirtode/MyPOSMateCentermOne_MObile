package com.quagnitia.myposmate.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.quagnitia.myposmate.R;

public class UnionPayScreen extends AppCompatActivity {
    private Button btn_void;
    public static boolean isUnionPaySelected=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_union_pay_screen);
        btn_void = (Button)findViewById(R.id.btn_void);
        btn_void.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        isUnionPaySelected=true;
        super.onBackPressed();
    }
}
