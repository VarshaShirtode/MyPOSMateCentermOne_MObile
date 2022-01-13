//package com.quagnitia.myposmate.activities;
//
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.net.http.SslError;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.webkit.SslErrorHandler;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.TextView;
//
//import com.quagnitia.myposmate.MyPOSMateApplication;
//import com.quagnitia.myposmate.R;
//import com.quagnitia.myposmate.utils.AppConstants;
//import com.quagnitia.myposmate.utils.OkHttpHandler;
//import com.quagnitia.myposmate.utils.OnTaskCompleted;
//import com.quagnitia.myposmate.utils.PreferencesManager;
//
//import org.json.JSONObject;
//
//public class PaymentExpressActivity extends AppCompatActivity implements OnTaskCompleted {
//    private WebView webView;
//    private PreferencesManager preferenceManager;
//    private TransactionReceiver transactionReceiver;
//    private IntentFilter intentFilter;
//    private ProgressDialog progress;
//    private TextView tv_cancel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_payment_express);
//        preferenceManager = PreferencesManager.getInstance(this);
//        transactionReceiver = new TransactionReceiver();
//        intentFilter = new IntentFilter();
//        intentFilter.addAction("PaymentExpress");
//        registerReceiver(transactionReceiver, intentFilter);
//        webView = (WebView) findViewById(R.id.webview);
////        webView.setWebViewClient(new WebViewClient());
//        webView.setWebViewClient(new WebViewClient() {
//
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                handler.proceed();
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                if(progress!=null)
//                {
//                    if(!progress.isShowing())
//                        openProgressDialog();
//                }
//                else
//                {
//                    openProgressDialog();
//                }
//
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                if(progress!=null)
//                {
//                    if(progress.isShowing())
//                        progress.dismiss();
//                }
//
//            }
//        });
//
//        webView.getSettings().setJavaScriptEnabled(true);
//        if (getIntent() != null) {
//            webView.loadUrl(getIntent().getStringExtra("url"));
//        }
//
//        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }
//
//    public void openProgressDialog() {
//        progress = new ProgressDialog(this);
//        progress.setMessage("Loading.......");
//        progress.setCancelable(false);
//        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progress.setIndeterminate(true);
//        progress.show();
//    }
//
////    public void callTransactionDetails() {
////        openProgressDialog();
////        new OkHttpHandler(this, this, null, "TransactionDetails").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_getGatewayTransactionDetails
////                + "?reference_id=" + jsonObject.optString("reference_id") + "&terminal_id=" + preferenceManager.getterminalId().toString() +
////                "&access_id=" + preferenceManager.getuniqueId() + "&is_mobile_device=true");
////    }
//
//    @Override
//    public void onTaskCompleted(String result, String TAG) throws Exception {
//
//        if (result.equals("")) {
//            if (progress.isShowing())
//                progress.dismiss();
//        }
//        JSONObject jsonObject = new JSONObject(result);
//        switch (TAG) {
//            case "TransactionDetails":
//                if (!jsonObject.optString("status_id").equals("USERPAYING")) {
//                    if (jsonObject.optString("status").equalsIgnoreCase("true")) {
//                        if (MyPOSMateApplication.isOpen) {
//                            jsonObject.put("grandtotal", jsonObject.optString("amount"));
//                        } else
//                            jsonObject.put("grandtotal", jsonObject.optString("amount"));
//
//                        Intent intent1 = new Intent();
//                        intent1.setAction("PaymentExpressSuccess");
//                        intent1.putExtra("data", jsonObject.toString());
//                        sendBroadcast(intent1);
//                        if (progress.isShowing())
//                            progress.dismiss();
//                        finish();
//
//                    } else if (jsonObject.optString("status_description").equals("TRADE_REVOKED")) {
//
//                        Intent intent1 = new Intent();
//                        intent1.setAction("PaymentExpressFailure");
//                        intent1.putExtra("data", jsonObject.toString());
//                        sendBroadcast(intent1);
//                        if (progress.isShowing())
//                            progress.dismiss();
//
//
//                        finish();
//
//                    }
//                } else {
//                    if (jsonObject.optString("status_description").equals("TRADE_CLOSED")) {
//                        Intent intent1 = new Intent();
//                        intent1.setAction("PaymentExpressFailure");
//                        intent1.putExtra("data", jsonObject.toString());
//                        sendBroadcast(intent1);
//                        if (progress.isShowing())
//                            progress.dismiss();
//
//                        finish();
//
//                    }
//                }
//                break;
//
//
//        }
//    }
//
//    JSONObject jsonObject;
//
//    public class TransactionReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String ac = intent.getAction();
//            switch (ac) {
//                case "PaymentExpress":
//
//                    try {
//                        jsonObject = new JSONObject(intent.getStringExtra("data"));
//                        if (jsonObject.optString("status").equals("TRADE_SUCCESS") ||
//                                jsonObject.optString("status").equals("0") ||
//                                jsonObject.optString("status").equals("TRADE_HAS_SUCCESS")
//                                || jsonObject.optString("status").equalsIgnoreCase("true")
//                                ) {
//                            Intent intent1 = new Intent();
//                            intent1.setAction("PaymentExpressSuccess");
//                            intent1.putExtra("data", jsonObject.toString());
//                            sendBroadcast(intent1);
//                            finish();
//                        } else {
//                            Intent intent1 = new Intent();
//                            intent1.setAction("PaymentExpressFailure");
//                            intent1.putExtra("data", jsonObject.toString());
//                            sendBroadcast(intent1);
//                            finish();
//                        }
//                    } catch (Exception e) {
//
//                    }
//
//
//                    break;
//            }
//        }
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(transactionReceiver);
//    }
//}
