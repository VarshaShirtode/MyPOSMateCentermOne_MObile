package com.quagnitia.myposmate.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.fragments.TransactionDetailsActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

import javax.net.ssl.HttpsURLConnection;


public class OkHttpHandler extends AsyncTask {
    private OnTaskCompleted listener;
    private HashMap<String, String> postDataParams;
    private String TAG, dup_url = "";
    private Context mContext;
    public static boolean isWebserviceRunning = false;
    PreferencesManager preferencesManager;

    public OkHttpHandler(Context context, OnTaskCompleted listener, HashMap<String, String> postDataParams, String TAG) {
        this.listener = listener;
        this.TAG = TAG;
        this.mContext = context;
        this.postDataParams = postDataParams;
        preferencesManager = PreferencesManager.getInstance(mContext);
    }

    public OkHttpHandler(OnTaskCompleted listener, String TAG, Activity baseActivity) {
        this.listener = listener;
        this.TAG = TAG;
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


    private String uploadData(String requestURL, HashMap<String, String> postDataParams, String TAG) {
        InputStream is = null;
        URL url;
        String response = "";

        try {
if(!isTimerCalled)
            startCountdownTimer();
            if (TAG.equals("unionpaystatus") || TAG.equals("UpdateBranchDetails")) {
                url = new URL(requestURL);
            } else {
                url = new URL(requestURL.replaceAll(" ", "%20"));
            }
            Log.v(TAG + ":API", url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            if (postDataParams != null) {
                String auth = new String(Base64.encode((AppConstants.CLIENT_ID + ":" + AppConstants.CLIENT_SECRET).getBytes(), Base64.URL_SAFE | Base64.NO_WRAP));
                if (TAG.equals("AuthToken") || TAG.equals("AuthTokenCloseTrade")) {
                    String formData = "username=" + preferencesManager.getMerchantId() + "&password=" + preferencesManager.getConfigId() + "&grant_type=password";
//                    String auth =new String(Base64.encode(( preferencesManager.getMerchantId() + ":" + preferencesManager.getConfigId()).getBytes(),Base64.URL_SAFE| Base64.NO_WRAP));


                    conn.setRequestMethod("POST");
                    conn.addRequestProperty("Authorization", "Basic " + auth);
                    conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    //conn.setRequestProperty("charset", "utf-8");
                    //conn.setRequestProperty("Content-Length", Integer.toString(formData.length()));
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    Log.v(TAG + " Request", conn.getOutputStream().toString());
                    writer.write(getPostDataString(postDataParams));
                    writer.flush();
                    writer.close();
                    os.close();
                } else {
                    conn.setRequestMethod("POST");
                    conn.addRequestProperty("Authorization", "Basic " + auth);
                    conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    Log.v(TAG + " Request", postDataParams.toString());
                  //  Log.v("TOKENRESPONSE",TAG + " Request "+ postDataParams.toString());
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));
                    writer.flush();
                    writer.close();
                    os.close();
                }

            } else {
                conn.setRequestMethod("GET");
                Log.v("TOKENRESPONSE",TAG + " Request GET"+requestURL);
            }
           // Log.v("TOKENRESPONSE",TAG + " Request "+ postDataParams.toString());

            String line;
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }

            }


            Log.v("Response:" + TAG, response);
            Log.v("TOKENRESPONSE",TAG + " Request "+ postDataParams.toString());

        } catch (SocketTimeoutException s) {

//            if (TAG.equals("unionpaystatus")) {
//                showAlert2();
//            } else {
//                showAlert();
//            }
            if(mContext!=null)
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showAlert3();
                }
            });

            isWebserviceRunning = false;
            s.printStackTrace();
            return response;


        } catch (Exception e) {
//            if (TAG.equals("unionpaystatus")) {
//                showAlert2();
//            } else {
//                showAlert();
//            }
            try
            {
                if(mContext!=null)
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            showAlert3();
                        }
                    });
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }


            isWebserviceRunning = false;
            e.printStackTrace();
            return response;
        }
        //    Log.v(TAG, "Response: " + response);
        return response;
    }

    @Override
    protected String doInBackground(Object[] objects) {
        dup_url = objects[0].toString();
        isWebserviceRunning = true;
        return uploadData(objects[0].toString(), postDataParams, TAG);
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            isWebserviceRunning = false;
            countDownTimer11.cancel();
            Log.v("TOKENRESPONSE",TAG + " response = "+o.toString());
            listener.onTaskCompleted(o.toString(), TAG);
            AppConstants.isPostReceived = true;
            cancel(true);

        } catch (Exception e) {
            AppConstants.isPostReceived = true;
            cancel(true);
            Log.e("ReadJSONFeedTask", e.getLocalizedMessage() + "");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(isTimerCalled)
        {
            isTimerCalled=false;
            openProgressDialog();
        }


    }
    public void openProgressDialog() {
        progress = new ProgressDialog(mContext );
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }
    CountDownTimer countDownTimer11;
    ProgressDialog progress;
    public static boolean isTimerCalled=false;

    public void startCountdownTimer() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countDownTimer11 = new CountDownTimer(60000, 60000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        try {
                            listener.onTaskCompleted("{}", TAG);

                            showAlert3();

                            if (countDownTimer11 != null)
                                countDownTimer11.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                };
                countDownTimer11.start();
            }

        });

    }

    private void showAlert3() {


        try {
            if (AppConstants.isPostReceived) {
                AppConstants.isPostReceived = false;
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }



                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                LayoutInflater lf = (LayoutInflater) (mContext)
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogview = lf.inflate(R.layout.retry_dialog, null);
                TextView title = (TextView) dialogview.findViewById(R.id.title);
                title.setText("Note");
                TextView body = (TextView) dialogview
                        .findViewById(R.id.dialogBody);
                body.setText("No response received from server.\nPlease check your network connection\n.Do you want to retry?");
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
                cancel.setText("CANCEL");
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            if(progress!=null)
                            {
                                if(progress.isShowing())
                                    progress.dismiss();
                            }
                            countDownTimer11.cancel();
                            listener.onTaskCompleted("", TAG);
                            isWebserviceRunning = false;
                            cancel(true);
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        dialog.dismiss();

                    }
                });

                TextView retry = (TextView) dialogview
                        .findViewById(R.id.dialogRetry);
                retry.setVisibility(View.VISIBLE);
                retry.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        isTimerCalled=true;
                        if(progress!=null)
                        {
                            if(progress.isShowing())
                                progress.dismiss();
                        }
                        new OkHttpHandler(mContext, listener, postDataParams, TAG).execute(dup_url);
                        dialog.dismiss();
                        countDownTimer11.cancel();
                    }
                });


    }


    private void showAlert() {


        try {
            if (AppConstants.isPostReceived) {
                AppConstants.isPostReceived = false;
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                LayoutInflater lf = (LayoutInflater) (mContext)
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
                cancel.setText("OK");
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            listener.onTaskCompleted("", TAG);
                            isWebserviceRunning = false;
                            cancel(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        dialog.dismiss();

                    }
                });

                TextView retry = (TextView) dialogview
                        .findViewById(R.id.dialogRetry);
                retry.setVisibility(View.GONE);
                retry.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new OkHttpHandler(mContext, listener, postDataParams, TAG).execute(dup_url);
                        dialog.dismiss();
                    }
                });

            }

        });
    }


    private void showAlert2() {


        try {
//            if(isConnected())
//            {
//            if(AppConstants.isPostReceived)
//            {
//                AppConstants.isPostReceived=false;

            if (TAG.equals("unionpaystatus")) {

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final Dialog dialog = new Dialog(mContext);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        LayoutInflater lf = (LayoutInflater) (mContext)
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View dialogview = lf.inflate(R.layout.retry_dialog, null);
                        TextView title = (TextView) dialogview.findViewById(R.id.title);
                        title.setText("Warning");
                        TextView body = (TextView) dialogview
                                .findViewById(R.id.dialogBody);
                        body.setText("Could not update the data.Please retry.");
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
                        cancel.setText("EXIT");
                        cancel.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
//                                    try {
//                                        listener.onTaskCompleted("", TAG);
//                                        isWebserviceRunning = false;
//                                        cancel(true);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }


                                dialog.dismiss();
                                showExitDialog();

                            }
                        });

                        TextView retry = (TextView) dialogview
                                .findViewById(R.id.dialogRetry);
                        // retry.setVisibility(View.GONE);
                        retry.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                new OkHttpHandler(mContext, listener, postDataParams, TAG).execute(dup_url);
                                dialog.dismiss();
                            }
                        });

                    }

                });
            }
//                else
//                {
//                    return;
//                }


//            }

            // }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void showExitDialog() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                LayoutInflater lf = (LayoutInflater) (mContext)
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogview = lf.inflate(R.layout.retry_dialog, null);
                TextView title = (TextView) dialogview.findViewById(R.id.title);
                title.setText("Warning");
                TextView body = (TextView) dialogview
                        .findViewById(R.id.dialogBody);
                body.setText("Are you sure you want to exit? The record would not be updated.");
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
                cancel.setText("YES");
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            listener.onTaskCompleted("", TAG);
                            isWebserviceRunning = false;
                            cancel(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        dialog.dismiss();

                    }
                });

                TextView retry = (TextView) dialogview
                        .findViewById(R.id.dialogRetry);
                retry.setText("NO");
                // retry.setVisibility(View.GONE);
                retry.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //new OkHttpHandler(mContext, listener, postDataParams, TAG).execute(dup_url);
                        AppConstants.isPostReceived = true;
                        showAlert2();
                        dialog.dismiss();
                    }
                });

            }

        });
    }


}
