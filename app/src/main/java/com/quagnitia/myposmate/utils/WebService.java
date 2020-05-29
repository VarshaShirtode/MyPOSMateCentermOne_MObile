package com.quagnitia.myposmate.utils;
/*@Author: Ruhi Bhandari
* @Created Date: 6/8/2016
* @Used for interacting with server.
* */

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.quagnitia.myposmate.R;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class WebService extends AsyncTask<String, String, String> {

    private Activity baseActivity;
    OnTaskCompleted listener;
    Context c;
    HashMap<String, String> postDataParams;
    String TAG, dup_url = "";
    ProgressDialog progress;
    int pd;

    JSONObject postDataParamsJsonObj;

    public WebService(OnTaskCompleted listener, HashMap<String, String> postDataParams, String TAG) {
        this.listener = listener;
        this.TAG = TAG;

        this.postDataParams = postDataParams;

    }


    public WebService(OnTaskCompleted listener, JSONObject postDataParams, String TAG, Activity baseActivity, int pd) {
        this.listener = listener;
        this.TAG = TAG;
        this.pd = pd;
        postDataParamsJsonObj = postDataParams;
        this.baseActivity = baseActivity;
    }


    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }


    public String uploadData(String requestURL, HashMap<String, String> postDataParams, JSONObject postDataParamsJsonObj, String TAG) {
        InputStream is = null;
        OutputStream os = null;
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);


            if (TAG.equalsIgnoreCase("UploadFileWS")) {
//                final MultipartUtility http = new MultipartUtility(url,"POST");
//                http.addFilePartData(postDataParams);
//                final byte[] bytes = http.finish();
//                InputStream myInputStream = new ByteArrayInputStream(bytes);
//                response = decodeResponse(myInputStream);
            } else {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(60000);
                conn.setConnectTimeout(60000);
                if (postDataParams != null) {
                    if (TAG.equalsIgnoreCase("SIGNUPWS_JSON")) {

                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        os = conn.getOutputStream();
                        JSONObject object = new JSONObject();
                        Set<String> keys = postDataParams.keySet();
                        for (String key : keys) {
                            object.put(key, postDataParams.get(key));
                        }
                        os.write(object.toString().getBytes("UTF-8"));
                        os.close();
                    } else {
                        conn.setRequestMethod("POST");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        os = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        writer.write(getPostDataString(postDataParams));
                        writer.flush();
                        writer.close();
                        os.close();
                    }

                } else if (postDataParamsJsonObj != null) {
                    PackageInfo pInfo = baseActivity.getPackageManager().getPackageInfo(baseActivity.getPackageName(), 0);
                    String version = pInfo.versionName;
                    postDataParamsJsonObj.put("deviceSdkVersion", "" + Build.VERSION.SDK);
                    postDataParamsJsonObj.put("device", "" + Build.DEVICE + "_" + Build.MANUFACTURER);
                    postDataParamsJsonObj.put("deviceModel", Build.MANUFACTURER.toString().toUpperCase() + " " + Build.MODEL);
                    postDataParamsJsonObj.put("deviceProduct", "" + Build.PRODUCT);
                    postDataParamsJsonObj.put("appVersion", "" + version);

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    os = conn.getOutputStream();
                    os.write(postDataParamsJsonObj.toString().getBytes("UTF-8"));

                } else {
                    conn.setRequestMethod("GET");
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line = "";
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";

                }

                if (os != null) {
//                    writer.flush();
//                    writer.close();
                    os.close();
                }

            }

        } catch (SocketTimeoutException s) {
            s.printStackTrace();
            cancel(true);
            progress.dismiss();
            showAlert();

        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            cancel(true);
            progress.dismiss();
            showAlert();
        } catch (IOException e) {
            e.printStackTrace();
            cancel(true);
            progress.dismiss();
            showAlert();

        } catch (Exception e) {
            cancel(true);
            e.printStackTrace();
            progress.dismiss();
            showAlert();

        }
     //   Log.v(TAG, "Response: " + response);
        return response;
    }


    private static String decodeResponse(InputStream is) {
        String result = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            result = sb.toString();

        } catch (Exception EXCEPTION) {
            EXCEPTION.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


        if (pd != 1) {
            progress = new ProgressDialog((Activity) listener);
            progress.setMessage("Loading.......");
            progress.setCancelable(false);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
        }

    }

    @Override
    protected String doInBackground(String... url) {
        dup_url = url[0];
        return uploadData(url[0], postDataParams, postDataParamsJsonObj, TAG);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {

            if (pd != 1) {
                if (progress.isShowing()) {
                    progress.dismiss();

                }

            }


            listener.onTaskCompleted(result, TAG);


        } catch (Exception e) {
            Log.e("ReadJSONFeedTask", e.getLocalizedMessage() + "");

        }

    }

    public void showAlert() {

        ((Activity) listener).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final Dialog dialog = new Dialog((Context) listener);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                LayoutInflater lf = (LayoutInflater) ((Context) listener)
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogview = lf.inflate(R.layout.retry_dialog, null);
                TextView title = (TextView) dialogview.findViewById(R.id.title);
                title.setText("Note");
                TextView body = (TextView) dialogview
                        .findViewById(R.id.dialogBody);
                body.setText("Please check your network connection");
                dialog.setContentView(dialogview);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);
                dialog.show();
                TextView cancel = (TextView) dialogview
                        .findViewById(R.id.dialogCancel);
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        progress.dismiss();
                        dialog.dismiss();
                    }
                });

                TextView retry = (TextView) dialogview
                        .findViewById(R.id.dialogRetry);
                retry.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new WebService(listener, postDataParams, TAG).execute(dup_url);
                        dialog.dismiss();
                    }
                });

            }

        });
    }

}//end of class
