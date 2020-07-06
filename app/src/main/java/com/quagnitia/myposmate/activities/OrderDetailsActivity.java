package com.quagnitia.myposmate.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.quagnitia.myposmate.R;

public class OrderDetailsActivity extends AppCompatActivity {
    RecyclerView recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        initUI();
    }

    public void initUI() {
        recycler_view =  findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(this, null);
        recycler_view.setAdapter(orderDetailsAdapter);

        findViewById(R.id.btn_status).setOnClickListener((View) -> showStatusDialog());
    }


    public void showStatusDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater lf = (LayoutInflater) (this)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.status_dialog, null);
        Button btn_save_and_ok = dialogview.findViewById(R.id.btn_save);
        Button btn_cancel_and_close = dialogview.findViewById(R.id.btn_cancel);
        Spinner spinner=dialogview.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.min_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btn_cancel_and_close.setOnClickListener((View v) -> {
            dialog.dismiss();
        });

        btn_save_and_ok.setOnClickListener((View v) -> {
            dialog.dismiss();
        });
        dialog.setContentView(dialogview);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

    }


    public void initListener() {

    }
}
