/*
package com.quagnitia.myposmate.triggerappbckfiles;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import static com.example.triggerdemoapp.MainActivity.POSMATE_GET_TXN;
import static com.example.triggerdemoapp.MainActivity._POS_REQUEST;
import static com.example.triggerdemoapp.MainActivity._POS_REQUEST_STATUS;

public class RefundActivity extends AppCompatActivity implements View.OnClickListener, OnTaskCompleted {

    private Button btn_send, btn_cancel;
    private PreferencesManager preferenceManager;
    private EditText edt_amount, edt_reference_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);
        preferenceManager = PreferencesManager.getInstance(this);
        initUI();
        initListener();
        callAuthToken();
    }

    public void initUI() {
        btn_send = findViewById(R.id.btn_send);
        btn_cancel = findViewById(R.id.btn_cancel);
        edt_amount = findViewById(R.id.edt_amount);
        edt_reference_id = findViewById(R.id.edt_reference_id);
    }

    public void initListener() {
        btn_send.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    ProgressDialog progress;

    public void openProgressDialog() {
        progress = new ProgressDialog(this);
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }
    public void callAuthToken() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
        new OkHttpHandler(this, this, hashMap, "AuthToken").execute(preferenceManager.getBaseURL()+AppConstants.AUTH2);

    }

    public void callRefundAPI() {
        openProgressDialog();
        TreeMap<String, String> hashMapKeys = new TreeMap<>();
//        hashMapKeys.put("terminal_id",preferenceManager.getterminalId());
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("reference_id", edt_reference_id.getText().toString());
        hashMapKeys.put("random_str", new Date().getTime() + "");
        hashMapKeys.put("grand_total", edt_amount.getText().toString());
        hashMapKeys.put("request_type", "REFUND");

        new OkHttpHandler(RefundActivity.this, this, null, "_POS_REQUEST").
                execute(_POS_REQUEST
                        + MD5Class.generateSignatureString(hashMapKeys,this)+"&access_token="+preferenceManager.getauthToken());

    }


boolean isRefund=false;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;

            case R.id.btn_send:
                if(edt_reference_id.getText().toString().equals(""))
                    Toast.makeText(this, "Please enter the reference_id", Toast.LENGTH_SHORT).show();
                else if(edt_amount.getText().toString().equals(""))
                    Toast.makeText(this, "Please enter the amount", Toast.LENGTH_SHORT).show();
                else
                {
                    isRefund=true;
                    callAuthToken();
                }

                break;
        }
    }

    private void callPosmateLandiTxnAPI() {

        final OkHttpHandler handler = new OkHttpHandler(this, this,
                null, POSMATE_GET_TXN);


        TreeMap<String, String> hashMapKeys = new TreeMap<>();
//        hashMapKeys.put("terminal_id",preferenceManager.getterminalId());
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("access_id",preferenceManager.getuniqueId());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("reference_id", preferenceManager.getreference_id());
        hashMapKeys.put("random_str", new Date().getTime() + "");


        String url = POSMATE_GET_TXN//"https://myposmate.com/api/v3/pos/getTransactionDetails"
                + MD5Class.generateSignatureString(hashMapKeys,this)+"&access_token="+preferenceManager.getauthToken();
        handler.execute(url);


    }
    public void callRequestStatus(String request_id) {
        TreeMap<String, String> hashMapKeys = new TreeMap<>();
        hashMapKeys.put("request_id",request_id);
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("random_str", new Date().getTime() + "");
        String url = _POS_REQUEST_STATUS
                + MD5Class.generateSignatureString(hashMapKeys,this)
                +"&access_token="+preferenceManager.getauthToken();
        new OkHttpHandler(RefundActivity.this, this,
                null, "requestStatus").execute(url);
    }
    String requestId="";
boolean isTransaction=false,isRefundInitiated=false;
    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
       final JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {
            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferenceManager.setauthToken(jsonObject.optString("access_token"));
                }
                if(isRefund)
                {
                    isRefund=false;
                    callRefundAPI();
                }
                if(isTransaction)
                {
                    callPosmateLandiTxnAPI();
                }


                break;

            case "requestStatus":
                if(jsonObject.optBoolean("status"))
                {
                    if(!jsonObject.optBoolean("executed"))
                    {
                        new CountDownTimer(5000, 1000) {
                            public void onFinish() {
                                if(jsonObject.optString("requestInfo").equals("DEVICE_BUSY"))
                                {
                                    if (progress.isShowing())
                                        progress.dismiss();
                                    Toast.makeText(RefundActivity.this, "Device is busy,try after sometime", Toast.LENGTH_LONG).show();
                                    finish();
                                    this.cancel();

                                }
                                else
                                {
                                    progress.setMessage(jsonObject.optString("requestInfo"));
                                    callRequestStatus(requestId);
                                }
                            }

                            public void onTick(long millisUntilFinished) {
                            }

                        }.start();
                    }
                    else
                    {
                        new CountDownTimer(3000, 1000) {
                            public void onFinish() {
                                isRefundInitiated=true;
                                isTransaction=true;
                                callAuthToken();
                            }

                            public void onTick(long millisUntilFinished) {
                            }

                        }.start();

                    }
                }
                else
                {
                    new CountDownTimer(5000, 1000) {
                        public void onFinish() {

                            callRequestStatus(requestId);
                        }

                        public void onTick(long millisUntilFinished) {
                        }

                    }.start();
                }


                break;




            case "_POS_REQUEST":
                if (jsonObject.optBoolean("status")) {
                    openProgressDialog();
                    preferenceManager.setreference_id(edt_reference_id.getText().toString());
                    new CountDownTimer(2000, 1000) {
                        public void onFinish() {
                            requestId=jsonObject.optString("requestId");
                            callRequestStatus(requestId);
                        }

                        public void onTick(long millisUntilFinished) {
                        }

                    }.start();

                }

                break;

            case POSMATE_GET_TXN:
//                if(progress.isShowing())
//                    progress.dismiss();
                if (jsonObject.optBoolean("status")) {
                    if(jsonObject.has("refunds"))
                    {
                        isTransaction=false;
                        JSONObject refundObject=jsonObject.optJSONArray("refunds").optJSONObject(0);
                        maketransactionDetailsCalls(refundObject.optString("refundStatus"), refundObject);
                    }

                    else
                    {
                        isTransaction=true;
                        callAuthToken();
                    }

                } else {
                    isTransaction=true;
                    callAuthToken();
                }
                break;
        }
    }

    private void showAlert(String message) {

                        final Dialog dialog = new Dialog(this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        LayoutInflater lf = (LayoutInflater) (this)
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View dialogview = lf.inflate(R.layout.message, null);
                        TextView body = (TextView) dialogview
                                .findViewById(R.id.dialogBody);
                        body.setText(message);
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
                                finish();
                                dialog.dismiss();
                            }
                        });


    }



    public void maketransactionDetailsCalls(String status_id, JSONObject jsonObject) {
        try {


            switch (status_id) {

                case "SUCCESS"://TRADE_SUCCESS
                    edt_amount.setText("");
                    edt_reference_id.setText("");
                    showAlert("Refund Successful ");
                    Toast.makeText(this,"Refund Successful", Toast.LENGTH_SHORT).show();
                    if (progress != null)
                        if (progress.isShowing())
                            progress.dismiss();

                    break;

                case "FAILED":
                case "PENDING":
                    Toast.makeText(RefundActivity.this, "Transaction failed", Toast.LENGTH_LONG).show();
                    if (progress != null)
                        if (progress.isShowing())
                            progress.dismiss();

                    break;
                case "CLOSED"://TRADE_CLOSED
                    showAlert("Transaction Closed");
                    if (progress != null)
                        if (progress.isShowing())
                            progress.dismiss();
                    break;
                case "GATEWAY_ERROR"://TRADE_ERROR
                    showAlert("Gateway Error");
                    if (progress != null)
                        if (progress.isShowing())
                            progress.dismiss();
                    break;




            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
*/
