package com.quagnitia.myposmate.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Test {
    public static void main(String[] args) {
        String utcTime = "2019-10-14T09:54:09";
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df1.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df2.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
        try {

            String format = df2.format(df1.parse(utcTime));
            System.out.print("Main: "+format);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}