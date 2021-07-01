package com.quagnitia.myposmate.gridpayment;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by varsha on 5/12/21.
 */
public interface MyClickListener {
    public void onLongClick(View v, int pos,boolean onClick );
    public void onClick(View v, int pos, ArrayList<MyObject> mData);
}
