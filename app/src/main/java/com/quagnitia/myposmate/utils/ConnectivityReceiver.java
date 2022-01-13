package com.quagnitia.myposmate.utils;

/**
 * Created by admin on 12/28/2017.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.quagnitia.myposmate.MyPOSMateApplication;
import com.quagnitia.myposmate.activities.DashboardActivity;

public class ConnectivityReceiver
        extends BroadcastReceiver{


    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        PreferencesManager preferenceManager=PreferencesManager.getInstance(context);
        if (isConnected) {
            MyPOSMateApplication.mStompClient=null;
            Intent i = new Intent();
            i.setAction("RECONNECT");
            i.putExtra("NetON","true");
            context.sendBroadcast(i);
            // initChat(preferenceManager.getUsername(), preferenceManager.getPassword());
        } else {
            Intent i = new Intent();
            i.setAction("NetConnectionOff");
            context.sendBroadcast(i);

        }
    }




}
