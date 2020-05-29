package com.quagnitia.myposmate.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.quagnitia.myposmate.MyPOSMateApplication;
import com.quagnitia.myposmate.activities.DashboardActivity;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

public class NetworkChangeReceiver extends BroadcastReceiver implements OnTaskCompleted {
    private PreferencesManager preferenceManager;
    private Context context;
    TreeMap<String, String> hashMapKeys;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        preferenceManager = PreferencesManager.getInstance(context);
        this.context = context;
        hashMapKeys = new TreeMap<>();
        if (checkInternet(context)) {


            if (!preferenceManager.getunion_pay_resp().equals("")) {
                Toast.makeText(context, "Network Available data sync started", Toast.LENGTH_LONG).show();
                callUnionPayStatus(preferenceManager.getunion_pay_resp(), "true");
                if (((MyPOSMateApplication) context.getApplicationContext()).asbtractConnection.isConnected()) {
//                    ((MyPOSMateApplication) context.getApplicationContext()).asbtractConnection.disconnect();

                }

//                MyPOSMateApplication.isActiveQrcode = false;
//                preferenceManager.setIsAuthenticated(false);
//                preferenceManager.setIsConnected(false);
                Intent i=new Intent();
                i.setAction("RECONNECT1");
                context.sendBroadcast(i);

            }

        }

    }



    public void callUnionPayStatus(String json_data, String status) {
        try {
            String s = "{\n" +
                    "  \"head\": {\n" +
                    "    \"version\": \"V1.2.0\"\n" +
                    "  },\n" +
                    "  \"body\":";

            JSONObject jsonObject = new JSONObject(json_data);
            if (jsonObject.has("responseCodeThirtyNine")) {
                if (jsonObject.has("responseCodeThirtyNine") && jsonObject.optString("responseCodeThirtyNine").equals("00")) {
                    if (jsonObject.optString("transactionType").equals("SALE") ||
                            jsonObject.optString("transactionType").equals("COUPON_SALE") ||
                            jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_SALE")
                    ) {
                        status = "20";
                    } else if (jsonObject.optString("transactionType").equals("VOID") ||
                            jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_VOID") ||
                            jsonObject.optString("transactionType").equals("COUPON_VOID")) {
                        status = "19"; //set 22 to 19 in case of void on 28/02/2019
                    }

                }
            } else {
                status = "23";
                Toast.makeText(context, jsonObject.optString("responseMessage"), Toast.LENGTH_LONG).show();

            }
            preferenceManager.setreference_id(jsonObject.optString("orderNumber"));

            hashMapKeys.clear();
            String randomStr = new Date().getTime() + "";

            hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
            hashMapKeys.put("is_mobile_device", "true");
            hashMapKeys.put("access_id", preferenceManager.getuniqueId());
            hashMapKeys.put("config_id", preferenceManager.getConfigId());
            hashMapKeys.put("reference_id", jsonObject.optString("orderNumber"));
            hashMapKeys.put("random_str", randomStr);
            hashMapKeys.put("status_id", status);
            hashMapKeys.put("json_data", s + json_data + "}");


            String s2 = "", s1 = "";
            int i1 = 0;
            Iterator<String> iterator = hashMapKeys.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (i1 != hashMapKeys.size() - 1)
                    s2 = s2 + key + "=" + hashMapKeys.get(key) + "&";
                else
                    s2 = s2 + key + "=" + hashMapKeys.get(key);
                i1++;
            }
            s2 = s2 + PreferencesManager.getInstance(context).getuniqueId();
            String signature = MD5Class.MD5(s2);


            s = "{\n" +
                    "  \"head\": {\n" +
                    "    \"version\": \"V1.2.0\"\n" +
                    "  },\n" +
                    "  \"body\":";

            hashMapKeys.clear();
            hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
            hashMapKeys.put("is_mobile_device", "true");
            hashMapKeys.put("access_id", preferenceManager.getuniqueId());
            hashMapKeys.put("config_id", preferenceManager.getConfigId());
            hashMapKeys.put("reference_id", jsonObject.optString("orderNumber"));
            hashMapKeys.put("random_str", randomStr);
            hashMapKeys.put("status_id", status);
            hashMapKeys.put("json_data", URLEncoder.encode(s + json_data + "}", "UTF-8"));
            i1 = 0;
            Iterator<String> iterator1 = hashMapKeys.keySet().iterator();
            while (iterator1.hasNext()) {
                String key = iterator1.next();
                if (i1 != hashMapKeys.size() - 1)
                    s1 = s1 + key + "=" + hashMapKeys.get(key) + "&";
                else
                    s1 = s1 + key + "=" + hashMapKeys.get(key);
                i1++;
            }


            new OkHttpHandler(context, this, null, "unionpaystatus")
                    .execute(AppConstants.BASE_URL2 + AppConstants.UPDATE_UNIONPAY_STATUS + "?" + s1 + "&signature=" + signature);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void callUnionPayStatus(String json_data, String status) {
//
//        try {
//            String s = "{\n" +
//                    "  \"head\": {\n" +
//                    "    \"version\": \"V1.2.0\"\n" +
//                    "  },\n" +
//                    "  \"body\":";
//
//            JSONObject jsonObject = new JSONObject(json_data);
//            if (jsonObject.has("responseCodeThirtyNine")) {
//                if (jsonObject.has("responseCodeThirtyNine") && jsonObject.optString("responseCodeThirtyNine").equals("00")) {
//                    if (jsonObject.optString("transactionType").equals("SALE")) {
//                        status = "20";
//                    } else if (jsonObject.optString("transactionType").equals("VOID")) {
//                        status = "22";
//                    }
//
//                }
//            } else {
//                status = "23";
//                Toast.makeText(context, jsonObject.optString("responseMessage"), Toast.LENGTH_LONG).show();
//
//            }
//            preferenceManager.setreference_id(jsonObject.optString("orderNumber"));
//            new OkHttpHandler(context, this, null, "unionpaystatus").
//                    execute(AppConstants.BASE_URL + AppConstants.UNION_PAY_STATUS
//                            + "?reference_id=" + jsonObject.optString("orderNumber") + "&is_mobile_device=true"
//                            + "&terminal_id=" + preferenceManager.getterminalId() +
//                            "&access_id=" + preferenceManager.getuniqueId() + "&json_data=" + URLEncoder.encode(s + json_data + "}", "UTF-8") + "&status_id=" + status);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
        JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {
            case "unionpaystatus":

                preferenceManager.setreference_id("");
                preferenceManager.settriggerReferenceId("");


                if (jsonObject.optBoolean("success")) {
                    Toast.makeText(context, "Data synced successfully", Toast.LENGTH_LONG).show();
                    Log.v("Data Synced:", "Success");
                    AppConstants.showDialog = true;
                    preferenceManager.setunion_pay_resp("");
                } else {
                    Toast.makeText(context, "Data sync failed", Toast.LENGTH_LONG).show();
                    Log.v("Data Synced:", "Failed");
                }
                //context.img_menu.setOnClickListener((DashboardActivity)getActivity());

                break;
        }
    }
}
