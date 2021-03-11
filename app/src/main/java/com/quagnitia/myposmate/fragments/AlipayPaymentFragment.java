package com.quagnitia.myposmate.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.quagnitia.myposmate.MyPOSMateApplication;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import static com.quagnitia.myposmate.fragments.ManualEntry.isMerchantQrDisplaySelected;
import static com.quagnitia.myposmate.fragments.ManualEntry.isTrigger;
import static com.quagnitia.myposmate.fragments.ManualEntry.requestId;


public class AlipayPaymentFragment extends Fragment implements View.OnClickListener, OnTaskCompleted {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DisplayImageOptions imageOptions2;
    private ImageLoader imageLoader;
    private String mParam1;
    private String mParam2;
    private View view;
    private Button btn_save, btn_cancel;
    private ImageView img_qrcode, img_paymentmode, img_success, img_failure ,img;
    private String qrcode = "", payment_mode = "";
    private JSONObject jsonObject;
    private TextView tv_sale_amount, tv_count_down,tv_order_badge;
    private IntentFilter intentFilter;
    RelativeLayout rel_orders;
    private String paymentdetails_result = "";
    private PaymentDetailsReceiver paymentDetailsReceiver;
    private PreferencesManager preferenceManager;
    private String amount = "";
    private CountDownTimer countDownTimer, countDownTimer1;
    private ProgressDialog progress;
    private boolean isDestroyed = false;
    private boolean isInvalideParam = false;
    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private CountDownTimer countDownTimerxmpp, countDownTimer11;
    TreeMap<String, String> hashMapKeys;

    public AlipayPaymentFragment() {
        // Required empty public constructor
    }

    public static AlipayPaymentFragment newInstance(String param1, String param2) {
        AlipayPaymentFragment fragment = new AlipayPaymentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alipay_payment, container, false);
        ((DashboardActivity) getActivity()).img_menu.setEnabled(false);
        preferenceManager = PreferencesManager.getInstance(getActivity());
        isTrigger=false;
        hashMapKeys = new TreeMap<>();
        MyPOSMateApplication.isActiveQrcode = true;
        intentFilter = new IntentFilter();
        intentFilter.addAction("PAYMENT_DETAILS");
        paymentDetailsReceiver = new PaymentDetailsReceiver();
        getActivity().registerReceiver(paymentDetailsReceiver, intentFilter);

        if (getArguments() != null) {
            try {
                jsonObject = new JSONObject(getArguments().getString(ARG_PARAM1));
            } catch (Exception e) {
                e.printStackTrace();
            }

            qrcode = jsonObject.optString("codeUrl");//+"&access_token="+preferenceManager.getauthToken();
            payment_mode = getArguments().getString(ARG_PARAM2);
        } else {
            qrcode = "";
            payment_mode = "";
        }
        initLoader();
        initUI(view);
        initListener();
        countDownTimer11 = new CountDownTimer(150000, 10000) {

            public void onTick(long millisUntilFinished) {
                callTransactionDetails();
            }

            public void onFinish() {
                try {
                    if (DashboardActivity.isExternalApp) {
                        TransactionDetailsActivity.isReturnFromTransactionDetails = false;
                        try {
                            Toast.makeText(getActivity(), "QR code has expired", Toast.LENGTH_SHORT).show();
                            //added for external apps 12/5/2019
                            int REQ_PAY_SALE = 100;
                            DashboardActivity.isExternalApp = false;
                            ((DashboardActivity) getActivity()).getIntent().putExtra("result", new JSONObject().toString());
                            ((DashboardActivity) getActivity()).setResult(REQ_PAY_SALE, ((DashboardActivity) getActivity()).getIntent());
                            ((DashboardActivity) getActivity()).finishAndRemoveTask();
                            return;
                            //added for external apps


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                    if (getActivity() != null)
                        showDialog("QR code has expired");
                    if (progress != null)
                        if (progress.isShowing())
                            progress.dismiss();
                    if (countDownTimer11 != null)
                        countDownTimer11.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };
        countDownTimer11.start();
        callAuthToken();

        return view;
    }

    public void callAuthTokenCloseTrade() {
        openProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
//        hashMap.put("username", preferenceManager.getterminalId());
//        hashMap.put("password", preferenceManager.getuniqueId());
        new OkHttpHandler(getActivity(), this, hashMap, "AuthTokenCloseTrade").execute(AppConstants.AUTH);

    }

    public void callAuthToken() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.AUTH);
    }

    public void openProgressDialog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    @Override
    public void onDestroy() {
        isDestroyed = true;
        super.onDestroy();
        ((DashboardActivity) getActivity()).img_menu.setEnabled(true);
        getActivity().unregisterReceiver(paymentDetailsReceiver);
    }

    private Dialog dialog;

    public void showDialog(String text) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater lf = (LayoutInflater) (getActivity())
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.try_again_dialog, null);
        TextView body = (TextView) dialogview
                .findViewById(R.id.dialogBody);
        body.setText(text);
        dialog.setContentView(dialogview);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        dialog.show();
        TextView tv_ok = (TextView) dialogview
                .findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (progress != null) {
                    if (progress != null)
                        if (progress.isShowing())
                            progress.dismiss();
                }


                if (DashboardActivity.isExternalApp) {
                    TransactionDetailsActivity.isReturnFromTransactionDetails = false;
                    try {

                        //added for external apps 12/5/2019
                        int REQ_PAY_SALE = 100;
                        DashboardActivity.isExternalApp = false;
                        if (((DashboardActivity) getActivity()) != null) {
                            if (((DashboardActivity) getActivity()).getIntent() != null) {
                                ((DashboardActivity) getActivity()).getIntent().putExtra("result", new JSONObject().toString());
                                ((DashboardActivity) getActivity()).setResult(REQ_PAY_SALE, ((DashboardActivity) getActivity()).getIntent());
                                ((DashboardActivity) getActivity()).finishAndRemoveTask();
                            }

                        }

                        dialog.dismiss();
                        return;
                        //added for external apps


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                dialog.dismiss();
            }
        });


    }

    public void callUpdateRequestAPI1(String request_id, boolean executed) {
       // openProgressDialog();
        try {
            //v2 signature implementation

            hashMapKeys.clear();
            hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
            hashMapKeys.put("config_id", preferenceManager.getConfigId());
            hashMapKeys.put("access_id", preferenceManager.getuniqueId());
            hashMapKeys.put("request_id", request_id);
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("executed", executed + "");

            new OkHttpHandler(getActivity(), this, null, "updateRequest")
                    .execute(AppConstants.BASE_URL2 + AppConstants.UPDATE_REQUEST +
                            MD5Class.generateSignatureString(hashMapKeys, getActivity())
                            + "&access_token=" + preferenceManager.getauthToken());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _parseUpdateRequest(JSONObject jsonObject)
    {
        if(progress!=null)
        if(progress.isShowing())
            progress.dismiss();

        if(isMerchantQrDisplaySelected==false && MyPOSMateApplication.isOpen)
        {
            if(jsonObject.optBoolean("status"))
            {
                Toast.makeText(getActivity(), "Cancel Trigger Request Is Successful", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity(), "Error cancelling request", Toast.LENGTH_SHORT).show();
            }
            MyPOSMateApplication.isOpen = false;
            MyPOSMateApplication.isActiveQrcode = false;
            callAuthTokenCloseTrade();
        }
        else
        {
            if(jsonObject.optBoolean("status"))
            {
                Toast.makeText(getActivity(), "Cancel Trigger Request Is Successful", Toast.LENGTH_SHORT).show();
                if (preferenceManager.isManual()) {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                } else {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                }
            }
            else
            {
                Toast.makeText(getActivity(), "Error cancelling request", Toast.LENGTH_SHORT).show();
            }
        }


    }


    public static boolean isCloseTrade = false;
    boolean isCloseTradeFinished = false;

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {

        JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {
            case "updateRequest":
                _parseUpdateRequest(jsonObject);
                break;
            case "AuthToken":
                _parseAuthTokenResponse(jsonObject);
                break;
            case "CloseTrade":
                _parseCloseTradeResponse(jsonObject);
                break;
            case "AuthTokenCloseTrade":
                _parseCloseTradeAuthToken(jsonObject);
                break;
            case "TransactionDetails":
                _parseTransactionDetailsResponse(jsonObject);
                break;
            case "TransactionDetails1":
                _parseTransactionDetailsResponse1(jsonObject);
                break;

        }
    }

    private void _parseCloseTradeAuthToken(JSONObject jsonObject)
    {
        if (progress.isShowing())
            progress.dismiss();
        if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
            isCloseTrade = true;
            preferenceManager.setauthTokenCloseTrade(jsonObject.optString("access_token"));
            callCancelTransaction();
        }
    }
    
    private void _parseTransactionDetailsResponse1(JSONObject jsonObject)
    {

        if (progress != null)
            if (progress.isShowing())
                progress.dismiss();
        if (countDownTimerxmpp != null)
            countDownTimerxmpp.cancel();

        MyPOSMateApplication.isActiveQrcode = false;
        _callDefaultFragSwitch();
    }

    private void _callDefaultFragSwitch()
    {
        if(preferenceManager.isManual())
            ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
        else
            ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
    }

    private void _parseTransactionDetailsResponse(JSONObject jsonObject)
    {
        callAuthToken();
        if (jsonObject.optString("message").equals("Invalid Reference ID")) {
            if (progress != null)
                if (progress.isShowing())
                    progress.dismiss();
            return;
        }

        maketransactionDetailsCalls(jsonObject.optJSONObject("payment").optString("paymentStatus"), jsonObject);
    }

    private void _parseAuthTokenResponse(JSONObject jsonObject)
    {

        if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
            preferenceManager.setauthToken(jsonObject.optString("access_token"));
        }

        if (isCloseTradeFinished) {
            isCloseTradeFinished = false;
            callTransactionDetails1();
        }
        if(isTrigger)
        {
            isTrigger=false;
            callUpdateRequestAPI1(requestId,true);

        }

    }

    private void _parseCloseTradeResponse(JSONObject jsonObject)
    {
        callAuthToken();
        if (progress != null)
            progress.dismiss();
        if (jsonObject.optBoolean("status")) {
            countDownTimer11.cancel();
            isCloseTradeFinished = true;
            Toast.makeText(getActivity(), "Your transaction has been cancelled successfully", Toast.LENGTH_LONG).show();
            _callDefaultFragSwitch();

        } else {
            Toast.makeText(getActivity(), "Error occurred during cancelling", Toast.LENGTH_LONG).show();
        }


        if (DashboardActivity.isExternalApp) {
            TransactionDetailsActivity.isReturnFromTransactionDetails = false;
            try {

                //added for external apps 12/5/2019
                int REQ_PAY_SALE = 100;
                DashboardActivity.isExternalApp = false;
                ((DashboardActivity) getActivity()).getIntent().putExtra("result", new JSONObject().toString());
                ((DashboardActivity) getActivity()).setResult(REQ_PAY_SALE, ((DashboardActivity) getActivity()).getIntent());
                ((DashboardActivity) getActivity()).finishAndRemoveTask();
                return;
                //added for external apps


            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    public void maketransactionDetailsCalls(String status_id, JSONObject jsonObject) {
        try {


            switch (status_id) {

                case "SUCCESS"://TRADE_SUCCESS
                    MyPOSMateApplication.isOpen = false;
                    if (okHttpHandler != null) {
                        okHttpHandler.cancel(true);
                    }
                   jsonObject.put("grandTotal",jsonObject.optString("receiptAmount"));

                    if (countDownTimerxmpp != null)
                        countDownTimerxmpp.cancel();
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.PAYMENTPROCESSING, jsonObject.toString());
                    if (progress != null)
                        if (progress.isShowing())
                            progress.dismiss();
                    if (countDownTimer != null)
                        countDownTimer.cancel();

                    if (countDownTimer1 != null)
                        countDownTimer1.cancel();

                    if (countDownTimer11 != null)
                        countDownTimer11.cancel();

                    break;

                case "PENDING"://WAIT_BUYER_PAY
                    if (progress != null)
                        if (progress.isShowing())
                            progress.dismiss();
                    if (okHttpHandler != null) {
                        okHttpHandler.cancel(true);
                    }
                    if (countDownTimer != null)
                        countDownTimer.cancel();
                    if (countDownTimer1 != null)
                        countDownTimer1.cancel();

                    break;
                case "CLOSED"://TRADE_CLOSED
                    Toast.makeText(getActivity(), jsonObject.optString("status_description"), Toast.LENGTH_SHORT).show();
                    if (getActivity() != null)
                        showDialog("Your transaction is closed");
                    if (okHttpHandler != null) {
                        okHttpHandler.cancel(true);
                    }
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.PAYMENTPROCESSING, jsonObject.toString());
                    if (progress != null)
                        if (progress.isShowing())
                            progress.dismiss();
                    if (countDownTimer != null)
                        countDownTimer.cancel();
                    if (countDownTimer1 != null)
                        countDownTimer1.cancel();

                    if (countDownTimer11 != null)
                        countDownTimer11.cancel();
                    break;
                case "GATEWAY_ERROR"://TRADE_ERROR
                    Toast.makeText(getActivity(), jsonObject.optString("status_description"), Toast.LENGTH_SHORT).show();
                    break;




            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isTimeOut = false;

    public class PaymentDetailsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String ac = intent.getAction();
            switch (ac) {
                case "PAYMENT_DETAILS":
                    try {
                        JSONObject jsonObject = new JSONObject(intent.getStringExtra("data"));
                        paymentdetails_result = intent.getStringExtra("data");
                        if (jsonObject.optString("status").equals("TRADE_SUCCESS") ||
                                jsonObject.optString("status").equals("0") ||
                                jsonObject.optString("status").equals("TRADE_HAS_SUCCESS")
                                || jsonObject.optString("status").equalsIgnoreCase("true")
                        ) {
                            img_success.setVisibility(View.VISIBLE);
                            img_failure.setVisibility(View.GONE);
                            if (progress != null) {
                                if (progress.isShowing())
                                    progress.dismiss();
                            }
                            if (countDownTimerxmpp != null)
                                countDownTimerxmpp.cancel();
                            tv_count_down.setVisibility(View.GONE);
                            openProgressDialog();
                            progress.setMessage("Please wait while processing the transaction..");
                            countDownTimer = new CountDownTimer(30000, 5000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                                public void onTick(long millisUntilFinished) {
                                    callTransactionDetails();
                                }

                                public void onFinish() {
                                    try {
                                        dialog = new Dialog(getActivity());
                                        if (getActivity() != null)
                                            showDialog("Try again");
                                        if (progress != null)
                                            if (progress.isShowing())
                                                progress.dismiss();
                                        if (countDownTimer != null)
                                            countDownTimer.cancel();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            };
                            countDownTimer.start();
                        } else {
                            img_success.setVisibility(View.GONE);
                            img_failure.setVisibility(View.VISIBLE);
                            if (jsonObject.optString("status").equals("TRADE_REVOKED")) {
                                if (countDownTimerxmpp != null)
                                    countDownTimerxmpp.cancel();
                                tv_count_down.setVisibility(View.GONE);
                                isTimeOut = true;
                                if (getActivity() != null)
                                    showDialog("Transaction Timeout. Re-Initiate the transaction.");
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    break;
            }
        }
    }

    OkHttpHandler okHttpHandler;


    public void callTransactionDetails() {
        //v2 signature implementation
        hashMapKeys.clear();
        hashMapKeys.put("access_id",preferenceManager.getuniqueId());
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("reference_id", jsonObject.optString("referenceId"));
        hashMapKeys.put("random_str", new Date().getTime() + "");
        new OkHttpHandler(getActivity(), this, null, "TransactionDetails")
                .execute(AppConstants.BASE_URL2 + AppConstants.GET_TRANSACTION_DETAILS + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
    }


//    public void callTransactionDetails() {
//        okHttpHandler = new OkHttpHandler(getActivity(), this, null, "TransactionDetails");
//        okHttpHandler.execute(AppConstants.BASE_URL + AppConstants.getGatewayTransactionDetails
//                + "?reference_id=" + jsonObject.optString("reference_id") + "&terminal_id=" + preferenceManager.getterminalId().toString() +
//                "&access_id=" + preferenceManager.getuniqueId() + "&is_mobile_device=true");
//
//    }


    public void callTransactionDetails1() {
        openProgressDialog();
        //v2 signature implementation
        hashMapKeys.clear();
        hashMapKeys.put("access_id",preferenceManager.getuniqueId());
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("reference_id", reference_id);
        hashMapKeys.put("random_str", new Date().getTime() + "");
        new OkHttpHandler(getActivity(), this, null, "TransactionDetails1")
                .execute(AppConstants.BASE_URL2 + AppConstants.GET_TRANSACTION_DETAILS + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
    }

    String reference_id = "";

    public void callCancelTransaction() {
        openProgressDialog();
        if (okHttpHandler != null) {
            okHttpHandler.cancel(true);
        }


        //v2 signature implementation
        hashMapKeys.clear();
        hashMapKeys.put("access_id", preferenceManager.getuniqueId());
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("reference_id", jsonObject.optString("referenceId"));
        reference_id = jsonObject.optString("referenceId");
        hashMapKeys.put("random_str", new Date().getTime() + "");

        new OkHttpHandler(getActivity(), this, null, "CloseTrade")
                .execute(AppConstants.BASE_URL2 + AppConstants.CANCEL_TRANSACTION + MD5Class.generateSignatureStringCloseTrade(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthTokenCloseTrade());


    }

    public void initLoader() {
        imageOptions2 = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .cacheOnDisk(true).displayer(new SimpleBitmapDisplayer()).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity()).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
    }

    String roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        return twoDForm.format(d);
    }

    public void initUI(View view) {
        btn_save = view.findViewById(R.id.btn_save);
        tv_count_down =  view.findViewById(R.id.tv_count_down);
        btn_cancel =  view.findViewById(R.id.btn_cancel);
        img_qrcode = view.findViewById(R.id.img_qrcode);
        img_paymentmode =  view.findViewById(R.id.img_paymentmode);
        tv_sale_amount = view.findViewById(R.id.tv_sale_amount);
        tv_order_badge=getActivity().findViewById(R.id.tv_order_badge);
        img_success =  view.findViewById(R.id.img_success);
        img_failure =  view.findViewById(R.id.img_failure);
        img_failure =  view.findViewById(R.id.img_failure);
        img =  getActivity().findViewById(R.id.img);
        rel_orders=  getActivity().findViewById(R.id.rel_orders);
        rel_orders.setVisibility(View.GONE);
        /*img.setVisibility(View.GONE);
        img.setClickable(false);
        img.setEnabled(false);
        tv_order_badge.setVisibility(View.GONE);
        tv_order_badge.setClickable(false);
        tv_order_badge.setEnabled(false);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"clicked",Toast.LENGTH_SHORT).show();
            }
        });*/
        img_success.setVisibility(View.GONE);
        img_failure.setVisibility(View.GONE);
        if (!qrcode.equals("")) {
            imageLoader.displayImage(qrcode, img_qrcode, imageOptions2);

            amount = jsonObject.optString("amount");
            char[] ch = amount.toCharArray();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < ch.length; i++) {
                if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                } else {
                    sb.append(ch[i]);
                }
            }
            amount = sb.toString();

            tv_sale_amount.setText("Sale amount in " + preferenceManager.getcurrency() + " " + "$" + roundTwoDecimals(Float.valueOf(amount.replace(",", ""))));
            if (payment_mode.equals("ALIPAY")) {
                // img_paymentmode.setImageResource(R.drawable.ic_smallali);
            } else if (payment_mode.equals("WECHAT")) {
                //img_paymentmode.setImageResource(R.drawable.ic_smalwechat);
            }
        }


        countDownTimerxmpp = new CountDownTimer(150000, 1000) {

            public void onTick(long millisUntilFinished) {
                long time = millisUntilFinished / 1000;
                tv_count_down.setText("This QR Code will expire in " + time + " seconds.");
                Log.d("mili :", "" + millisUntilFinished);
                Log.d("sec :", time + "");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                isTimeOut = true;
                if (progress != null)
                    progress.dismiss();
                img_success.setVisibility(View.GONE);
                img_failure.setVisibility(View.VISIBLE);
                MyPOSMateApplication.isActiveQrcode = false;
                if (getActivity() != null)
                    showDialog("QR code has expired");
                tv_count_down.setVisibility(View.GONE);
//                if (getActivity() != null) {
//                    Intent i = new Intent();
//                    i.setAction("RECONNECT");
//                    getActivity().sendBroadcast(i);
//                }

            }

        }.start();


    }

    public void initListener() {
        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                if (isTimeOut) {
                    isTimeOut = false;
                    if (countDownTimerxmpp != null)
                        countDownTimerxmpp.cancel();
                    MyPOSMateApplication.isActiveQrcode = false;
                    if (preferenceManager.isManual()) {
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);

                    } else if (preferenceManager.isHome()) {
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);

                    }
                  //  ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                    return;
                }
                openProgressDialog();

                progress.setMessage("Please wait while processing the transaction..");
                countDownTimer1 = new CountDownTimer(30000, 5000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                    public void onTick(long millisUntilFinished) {
                        callTransactionDetails();
                    }

                    public void onFinish() {
                        try {
                            dialog = new Dialog(getActivity());
                            if (getActivity() != null)
                                showDialog("Try again");
                            if (progress != null)
                                if (progress.isShowing())
                                    progress.dismiss();
                            if (countDownTimer1 != null)
                                countDownTimer1.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                };
                countDownTimer1.start();

                break;
            case R.id.btn_cancel:
                if (progress != null)
                    if (progress != null && progress.isShowing())
                        progress.dismiss();

                preferenceManager.setreference_id("");
                preferenceManager.settriggerReferenceId("");

                if (countDownTimer != null)
                    countDownTimer.cancel();
                if (countDownTimer1 != null)
                    countDownTimer1.cancel();

                if (countDownTimer11 != null)
                    countDownTimer11.cancel();

                if(isMerchantQrDisplaySelected)
                {
                    isMerchantQrDisplaySelected=false;
                    if( MyPOSMateApplication.isOpen)
                    {
                        MyPOSMateApplication.isOpen = false;
                        MyPOSMateApplication.isActiveQrcode = false;
                        isTrigger=true;
                        openProgressDialog();
                        callAuthToken();
                    }
                    else
                    {
                        MyPOSMateApplication.isOpen = false;
                        MyPOSMateApplication.isActiveQrcode = false;
                        if(preferenceManager.isManual())
                        {
                            ((DashboardActivity)getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY,null);
                        }
                        else
                        {
                            ((DashboardActivity)getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION,null);
                        }
                    }
                }
                else
                {
                    if( MyPOSMateApplication.isOpen)
                    {
                        isTrigger=true;
                        openProgressDialog();
                        callAuthToken();
                    }
                    else
                    {
                        MyPOSMateApplication.isOpen = false;
                        MyPOSMateApplication.isActiveQrcode = false;
                        callAuthTokenCloseTrade();
                    }
                }
                break;
            default:
                Toast.makeText(getActivity(), "Under Implementation", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyPOSMateApplication.isActiveQrcode = false;
        MyPOSMateApplication.isOpen = false;
        if (okHttpHandler != null) {
            okHttpHandler.cancel(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (okHttpHandler != null) {
            okHttpHandler.cancel(true);
        }
    }
}
