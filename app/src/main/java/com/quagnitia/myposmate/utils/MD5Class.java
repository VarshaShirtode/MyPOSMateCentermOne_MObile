package com.quagnitia.myposmate.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.TreeMap;

public class MD5Class {


    public static final String MD5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static final String generateSignatureStringCloseTrade(TreeMap<String, String> hashMapKeys, Context context) {
        String s = "", s1 = "";
        int i1 = 0;
        Iterator<String> iterator = hashMapKeys.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (i1 != hashMapKeys.size() - 1)
                s = s + key + "=" + hashMapKeys.get(key) + "&";
            else
                s = s + key + "=" + hashMapKeys.get(key);
            i1++;
        }
        s1 = s;
        s = s +AppConstants.CLIENT_ID+ PreferencesManager.getInstance(context).getauthTokenCloseTrade();//.getuniqueId();
        String signature = MD5Class.MD5(s);
        return "?" + s1 + "&signature=" + signature;

    }

    public static final String generateSignatureString(TreeMap<String, String> hashMapKeys, Context context) {
        String s = "", s1 = "";
        int i1 = 0;
        Iterator<String> iterator = hashMapKeys.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (i1 != hashMapKeys.size() - 1)
                s = s + key + "=" + hashMapKeys.get(key) + "&";
            else
                s = s + key + "=" + hashMapKeys.get(key);
            i1++;
        }
        s1 = s;
        s = s +AppConstants.CLIENT_ID+ PreferencesManager.getInstance(context).getauthToken();//.getuniqueId();
        String signature = MD5Class.MD5(s);

        String sss=MD5Class.MD5("access_id=89b7a2d73b1b905f&branch_id=227&config_id=231&end_date=20220306T212500.000NZDT&limit=5&random_str=TEST_TERMINAL&start_date=20200106T212400.000NZDT&terminal_id=Test1b7ae46e37ba373dce7bde84095b9-eb76-4624-94dc-938d410145f3");
        Log.v("Signature",sss);

        return "?" + s1 + "&signature=" + signature;
    }

    public static final String generateSignatureStringOne(TreeMap<String, String> hashMapKeys, Context context) {
        String s = "", s1 = "";
        int i1 = 0;
        Iterator<String> iterator = hashMapKeys.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (i1 != hashMapKeys.size() - 1)
                s = s + key + "=" + hashMapKeys.get(key) + "&";
            else
                s = s + key + "=" + hashMapKeys.get(key);
            i1++;
        }
        s1 = s;
        s = s + AppConstants.CLIENT_ID+
                PreferencesManager.getInstance(context).getauthToken();//.getuniqueId();
        String signature = MD5Class.MD5(s);
        return signature;
    }



    public static final String generateSignatureStringV2(TreeMap<String, String> hashMapKeys, Context context) {
        String s = "", s1 = "";
        int i1 = 0;
        Iterator<String> iterator = hashMapKeys.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (i1 != hashMapKeys.size() - 1)
                s = s + key + "=" + hashMapKeys.get(key) + "&";
            else
                s = s + key + "=" + hashMapKeys.get(key);
            i1++;
        }
        s1 = s;
        s = s + PreferencesManager.getInstance(context).getuniqueId();
        String signature = MD5Class.MD5(s);
        return "?" + s1 + "&signature=" + signature;
    }

}
