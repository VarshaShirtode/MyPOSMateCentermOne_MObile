package com.quagnitia.myposmate.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.centrum.ThirtConst;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class TriggerFragment extends Fragment implements View.OnClickListener, OnTaskCompleted {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    IntentFilter intentFilter;
    ProgressDialog progress;
    TriggerReceiver triggerReceiver;
    TreeMap<String, String> hashMapKeys;
    View view;
    LinearLayout ll_unionpay, ll_epayments, ll_refund_password,ll_cup_reference_id,ll_epayments_trade_no;
    JSONObject triggerjsonObject = null;
    EditText edt_scheme, edt_refund_password, edt_amount, edt_mpm_reference, edt_cup_reference_id, edt_transaction_reference, edt_reason;
    Button btn_cancel1, btn_cancel2, btn_void, btn_refund1, btn_refund2;
    PreferencesManager preferenceManager;
    JSONObject jsonObjectSale = null;
    Intent intentCen = new Intent();
    String packageName = "com.centerm.dynaminpayinskate";
    String activityName = "org.skate.pay.component.PayEntry";
    ComponentName comp = new ComponentName(packageName, activityName);
    private static final int REQ_PAY_SALE = 100;
    boolean isStart = false;
    boolean isTriggerReceived=false;


    public TriggerFragment() {
    }

    public static TriggerFragment newInstance(String param1, String param2) {
        TriggerFragment fragment = new TriggerFragment();
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
        view = inflater.inflate(R.layout.fragment_trigger, container, false);
        hashMapKeys = new TreeMap<>();
        preferenceManager = PreferencesManager.getInstance(getActivity());
        triggerReceiver = new TriggerReceiver();
        addIntentFilter();
        initUI();
        initListener();
        isStart = true;
        callAuthToken();
        return view;
    }


    public void initUI() {
        edt_scheme = (EditText) view.findViewById(R.id.edt_scheme);
        edt_amount = (EditText) view.findViewById(R.id.edt_amount);
        edt_mpm_reference = (EditText) view.findViewById(R.id.edt_mpm_reference);
        edt_cup_reference_id = (EditText) view.findViewById(R.id.edt_cup_reference_id);
        edt_transaction_reference = (EditText) view.findViewById(R.id.edt_transaction_reference);
        edt_reason = (EditText) view.findViewById(R.id.edt_reason);
        edt_refund_password = (EditText) view.findViewById(R.id.edt_refund_password);
        btn_void = (Button) view.findViewById(R.id.btn_void);
        btn_refund1 = (Button) view.findViewById(R.id.btn_refund1);
        btn_refund2 = (Button) view.findViewById(R.id.btn_refund2);
        btn_cancel1 = (Button) view.findViewById(R.id.btn_cancel1);
        btn_cancel2 = (Button) view.findViewById(R.id.btn_cancel2);
        ll_unionpay = (LinearLayout) view.findViewById(R.id.ll_unionpay);
        ll_epayments = (LinearLayout) view.findViewById(R.id.ll_epayments);
        ll_refund_password = (LinearLayout) view.findViewById(R.id.ll_refund_password);
        ll_cup_reference_id = (LinearLayout) view.findViewById(R.id.ll_cup_reference_id);
        ll_epayments_trade_no = (LinearLayout) view.findViewById(R.id.ll_epayments_trade_no);
        ll_refund_password.setVisibility(View.GONE);
        ll_epayments_trade_no.setVisibility(View.GONE);
        ll_cup_reference_id.setVisibility(View.GONE);
        ll_unionpay.setVisibility(View.GONE);
        ll_epayments.setVisibility(View.GONE);
        getActivity().registerReceiver(triggerReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (triggerReceiver != null)
            getActivity().unregisterReceiver(triggerReceiver);
    }

    public void initListener() {
        btn_void.setOnClickListener(this);
        btn_refund1.setOnClickListener(this);
        btn_refund2.setOnClickListener(this);
        btn_cancel1.setOnClickListener(this);
        btn_cancel2.setOnClickListener(this);
    }

    public void addIntentFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("TriggerReceiver");
        getActivity().registerReceiver(triggerReceiver, intentFilter);
    }

    public void callRefundApi(JSONObject jsonObject) {
        openProgressDialog();
        try {
            //v2 signature implementation
            hashMapKeys.clear();
            hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
            hashMapKeys.put("config_id", preferenceManager.getConfigId());
            hashMapKeys.put("reference_id", jsonObject.optString("reference_id"));
            hashMapKeys.put("refund_amount", edt_amount.getText().toString());
            hashMapKeys.put("refund_password", edt_refund_password.getText().toString());
            hashMapKeys.put("refund_reason", "test");
            hashMapKeys.put("random_str", new Date().getTime() + "");

            new OkHttpHandler(getActivity(), this, null, "refundNow")
                    .execute(AppConstants.BASE_URL2 + AppConstants.V2_REFUND +
                            MD5Class.generateSignatureString(hashMapKeys, getActivity())
                            + "&access_token=" + preferenceManager.getauthToken());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callUpdateRequestAPI(String request_id,boolean executed) {
        openProgressDialog();
        try {
            //v2 signature implementation

            hashMapKeys.clear();
            hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
            hashMapKeys.put("config_id", preferenceManager.getConfigId());
            hashMapKeys.put("request_id", request_id);
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("executed", executed+"");

            new OkHttpHandler(getActivity(), this, null, "updateRequest")
                    .execute(AppConstants.BASE_URL2 + AppConstants.V2_UPDATE_REQUEST +
                            MD5Class.generateSignatureString(hashMapKeys, getActivity())
                            + "&access_token=" + preferenceManager.getauthToken());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_refund2:
                if (triggerjsonObject.optString("channel")
                        .equalsIgnoreCase("ALIPAY") ||
                        triggerjsonObject.optString("channel")
                                .equalsIgnoreCase("WECHAT")) {
                    if (triggerjsonObject.optString("request_type").equalsIgnoreCase("REFUND")) {
                        if (!edt_refund_password.getText().toString().equals(""))
                            callRefundApi(triggerjsonObject);
                        else
                            Toast.makeText(getActivity(), "Please enter refund password.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;


            case R.id.btn_refund1:
                if (triggerjsonObject.optString("channel")
                        .equalsIgnoreCase("UNION_PAY")) {
                    if (triggerjsonObject.optString("request_type").equalsIgnoreCase("REFUND")) {
                        beginRefund(jsonObjectGatewayResponse);
                    }
                }
                break;


            case R.id.btn_void:
                if (triggerjsonObject.optString("channel")
                        .equalsIgnoreCase("UNION_PAY")) {
                    if (triggerjsonObject.optString("request_type").equalsIgnoreCase("REFUND")) {
                        beginVoid(jsonObjectGatewayResponse);
                    }
                }
                break;

            case R.id.btn_cancel1:
            case R.id.btn_cancel2:
                if (preferenceManager.isManual()) {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                } else {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                }
                break;
        }
    }


    public void beginRefund(JSONObject jsonObject1) {
        try {
            jsonObjectSale = new JSONObject();
            jsonObjectSale.put("transactionType", "REFUND");
            jsonObjectSale.put("amount", edt_amount.getText().toString());
            jsonObjectSale.put("originalReferenceNumber", jsonObject1.optString("referenceNumber"));
            jsonObjectSale.put("orderNumber", jsonObject1.optString("orderNumber"));
            String s[] = TransactionDetailsAdapter.transactionDate.split(" ");
            String s1[] = s[0].split("-");
            jsonObjectSale.put("originalTransactionDate", jsonObject1.optString("transactionDate"));


            intentCen.setComponent(comp);
            Bundle bundle = new Bundle();
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.REFUND);
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_REFERENCE_NO, jsonObjectSale.optString("originalReferenceNumber"));
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_TRANS_DATE, jsonObjectSale.optString("originalTransactionDate"));
            bundle.putDouble(ThirtConst.RequestTag.THIRD_PATH_TRANS_AMOUNT, jsonObjectSale.optDouble("amount"));
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObjectSale.optString("orderNumber"));

            if (jsonObjectGatewayResponse.optString("transactionType").equals("UPI_SCAN_CODE_SALE")) {
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_SCAN_AUTH_PAY_CODE, jsonObjectGatewayResponse.optString("qrcode"));
            }


            intentCen.putExtras(bundle);
            startActivityForResult(intentCen, REQ_PAY_SALE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void beginVoid(JSONObject jsonObject1) {

        hideSoftInput();

        try {
            jsonObjectSale = new JSONObject();
            jsonObjectSale.put("originalVoucherNumber", jsonObject1.optString("voucherNumber"));
            jsonObjectSale.put("orderNumber", jsonObject1.optString("orderNumber"));//String.valueOf(c.getTimeInMillis()));
            jsonObjectSale.put("needAppPrinted", false);
            Toast.makeText(getActivity(), jsonObject1.optString("transactionType"), Toast.LENGTH_SHORT).show();
            if (jsonObject1.optString("transactionType").equals(ThirtConst.TransType.COUPON_SALE)) {
                doTransaction(ThirtConst.TransType.COUPON_VOID, jsonObjectSale);
            } else if (jsonObject1.optString("transactionType").equals("UPI_SCAN_CODE_SALE")) {
                jsonObjectSale.put("transactionType", ThirtConst.TransType.VOID);
                jsonObjectSale.put("qrcode", jsonObject1.optString("voucherNumber"));
                doTransaction(ThirtConst.TransType.SCAN_VOID, jsonObjectSale);
            } else
                doTransaction(ThirtConst.TransType.VOID, jsonObjectSale);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void hideSoftInput() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }


    private void doTransaction(String interfaceId, JSONObject jsonObject) {

        intentCen.setComponent(comp);
        Bundle bundle = new Bundle();
        switch (interfaceId) {
            case ThirtConst.TransType.VOID:
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.VOID);
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_VOUCHER_NO, jsonObject.optString("originalVoucherNumber"));
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObject.optString("orderNumber"));
                break;
            case ThirtConst.TransType.SCAN_VOID:
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.VOID);
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_VOUCHER_NO, jsonObject.optString("originalVoucherNumber"));
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObject.optString("orderNumber"));
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_SCAN_AUTH_PAY_CODE, jsonObject.optString("originalVoucherNumber"));
                break;
            case ThirtConst.TransType.COUPON_VOID:
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.COUPON_VOID);
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_VOUCHER_NO, jsonObject.optString("originalVoucherNumber"));
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObject.optString("orderNumber"));
                break;
        }

        intentCen.putExtras(bundle);
        startActivityForResult(intentCen, REQ_PAY_SALE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (data.hasExtra("responseCodeThirtyNine")) {
            try {
                Bundle bundle = new Bundle();
                bundle.putAll(data.getExtras());
                JSONObject json = new JSONObject();
                Set<String> keys = bundle.keySet();
                for (String key : keys) {
                    if (bundle.get(key) != null)
                        json.put(key, JSONObject.wrap(bundle.get(key)));
                }
                json.put("orderNumber", jsonObjectSale.optString("orderNumber"));

                onTaskCompleted(json.toString(), "Arke");
            } catch (Exception e) {
                //Handle exception here
            }

        }


    }


    public void openProgressDialog() {
        if(progress!=null)
        {
            if(progress.isShowing())
            {
                progress.dismiss();
            }
            progress=null;
        }
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }
    @Override
    public void onDetach() {
        if (progress != null && progress.isShowing())
            progress.dismiss();
        super.onDetach();
    }

    public void callTransactionDetails(JSONObject jsonObject) {
        openProgressDialog();
        //v2 signature implementation
        hashMapKeys.clear();
        hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("reference_id", jsonObject.optString("reference_id"));
        hashMapKeys.put("random_str", new Date().getTime() + "");
        new OkHttpHandler(getActivity(), this, null, "TransactionDetails")
                .execute(AppConstants.BASE_URL2 + AppConstants.V2_GET_TRANSACTION_DETAILS
                        + MD5Class.generateSignatureString(hashMapKeys, getActivity())
                        + "&access_token=" + preferenceManager.getauthToken());
    }

    public static String unionpay = "";
    public static boolean isRefundUnionPaySuccess = false;
    JSONObject jsonObjectResponse = null, jsonObjectGatewayResponse = null;


    public void callAuthToken() {
        openProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "password");
        hashMap.put("username", preferenceManager.getterminalId());
        hashMap.put("password", preferenceManager.getuniqueId());
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.V2_AUTH);

    }


    public void callSwitchFragment()
    {
        if (preferenceManager.isManual()) {
            ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
        } else {
            ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
        }
    }

    boolean isEpaymentsRefund = false;
    boolean isEndOfProcedure=false;

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
        if (progress.isShowing())
            progress.dismiss();
        jsonObjectResponse = new JSONObject(result);
        JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {

            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferenceManager.setauthToken(jsonObject.optString("access_token"));
                }
//                if (isStart) {
//                    isStart = false;
//                    callTransactionDetails(triggerjsonObject);
//                }
                if (isEpaymentsRefund) {
                    callTransactionDetails(triggerjsonObject);
                }

                if(isTriggerReceived)
                {
                    isTriggerReceived=false;
                    callUpdateRequestAPI(request_id,false);
                }

                if(isEndOfProcedure)
                {
                    callUpdateRequestAPI(request_id,true);
                }
                break;


            case "updateRequest":
                if(jsonObject.optBoolean("status"))
                {
                    callAuthToken();
                    if(isEndOfProcedure)
                    {
                        isEndOfProcedure=false;
                        callSwitchFragment();
                    }
                }

                break;


            case "refundNow":
                callAuthToken();
                if (jsonObject.optBoolean("status")) {

                    isEpaymentsRefund = true;
                    Toast.makeText(getActivity(), "Refund Request Successful.", Toast.LENGTH_LONG).show();


                } else
                    Toast.makeText(getActivity(), "Refund Request UnSuccessful." + jsonObject.optString("message"), Toast.LENGTH_LONG).show();
                break;

            case "unionpaystatus":
                AppConstants.isRefundUnionpayDone = true;
                if (progress != null && progress.isShowing())
                    progress.dismiss();
                if (jsonObject.optBoolean("success")) {
                    isEndOfProcedure=true;
                    Toast.makeText(getActivity(), "Transaction details updated successfully", Toast.LENGTH_SHORT).show();
                    callAuthToken();


                } else
                    Toast.makeText(getActivity(), "Transaction update failed", Toast.LENGTH_SHORT).show();
                break;

            case "TransactionDetails":
                callAuthToken();
                jsonObjectResponse = new JSONObject(result);
                if (jsonObjectResponse.has("server_response") &&
                        jsonObjectResponse.optJSONObject("server_response") != null) {
                    jsonObjectGatewayResponse = jsonObjectResponse.optJSONObject("server_response").optJSONObject("body");
                }

                if (triggerjsonObject.optString("channel").equalsIgnoreCase("UNION_PAY")) {
                    edt_cup_reference_id.setText(jsonObjectGatewayResponse.optString("referenceNumber"));
                } else {
                    edt_transaction_reference.setText(jsonObject.optString("trade_no"));
                }


                if (isEpaymentsRefund) {
                    isEpaymentsRefund = false;
                    isEndOfProcedure=true;
                    callAuthToken();
                }
                break;


            case "Arke":

                try {

                    if (jsonObject.has("responseCodeThirtyNine")) {
                        if (jsonObject.has("responseCodeThirtyNine") && jsonObject.optString("responseCodeThirtyNine").equals("00")) {
                            if (jsonObject.optString("transactionType").equals("SALE") ||
                                    jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_SALE") ||
                                    jsonObject.optString("transactionType").equals("COUPON_SALE")) {
                                //added on 28/02/2019
                                unionpay = result;
                                callUnionPayStatus(result, "true");
                            } else if (jsonObject.optString("transactionType").equals("VOID") ||
                                    jsonObject.optString("transactionType").equals("REFUND") ||
                                    jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_VOID") ||
                                    jsonObject.optString("transactionType").equals("COUPON_VOID")) {
                                //added on 28/02/2019
                                unionpay = result;

                                callUnionPayStatus(result, "true");
                            }

                        }
                    } else {
//                        callUnionPayStatus(result, "false");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;


        }
    }


    public void callUnionPayStatus(String json_data, String status) {
        openProgressDialog();
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
                            jsonObject.optString("transactionType").equals("REFUND") ||
                            jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_VOID") ||
                            jsonObject.optString("transactionType").equals("COUPON_VOID")) {
                        status = "19"; //set 22 to 19 in case of void on 28/02/2019
                    }

                }
            } else {
                status = "23";
                Toast.makeText(getActivity(), jsonObject.optString("responseMessage"), Toast.LENGTH_LONG).show();

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
            s2 = s2 + PreferencesManager.getInstance(getActivity()).getauthToken();//.getuniqueId();
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


            new OkHttpHandler(getActivity(), this, null, "unionpaystatus")
                    .execute(AppConstants.BASE_URL2 + AppConstants.V2_UPDATE_UNIONPAY_STATUS + "?" + s1 + "&signature=" + signature + "&access_token=" + preferenceManager.getauthToken());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

public static String request_id="";
    public class TriggerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ac = intent.getAction();

            switch (ac) {
                case "TriggerReceiver":
                    try {

                        triggerjsonObject = new JSONObject(intent.getStringExtra("data"));
                        request_id=triggerjsonObject.optString("request_id");
                        edt_scheme.setText(triggerjsonObject.optString("channel"));
                        edt_amount.setText(triggerjsonObject.optString("amount"));
                        edt_mpm_reference.setText(triggerjsonObject.optString("reference_id"));
                        edt_cup_reference_id.setText(triggerjsonObject.optString(""));
                        edt_transaction_reference.setText(triggerjsonObject.optString(""));
                        edt_reason.setText(triggerjsonObject.optString(""));

                        if (triggerjsonObject.optString("channel").equalsIgnoreCase("UNION_PAY")) {
                            ll_unionpay.setVisibility(View.VISIBLE);
                            ll_cup_reference_id.setVisibility(View.VISIBLE);
                            ll_epayments_trade_no.setVisibility(View.GONE);
                            ll_epayments.setVisibility(View.GONE);
                            edt_cup_reference_id.setText("");
                        } else {
                            ll_cup_reference_id.setVisibility(View.GONE);
                            ll_epayments_trade_no.setVisibility(View.VISIBLE);
                            ll_unionpay.setVisibility(View.GONE);
                            ll_epayments.setVisibility(View.VISIBLE);
                            edt_transaction_reference.setText("");
                        }

                        if (triggerjsonObject.optString("channel").equalsIgnoreCase("ALIPAY") ||
                                triggerjsonObject.optString("channel").equalsIgnoreCase("WECHAT")) {
                            ll_refund_password.setVisibility(View.VISIBLE);
                        } else {
                            ll_refund_password.setVisibility(View.GONE);
                        }
                        callTransactionDetails(triggerjsonObject);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isTriggerReceived=true;
                                callAuthToken();
                            }
                        }, 1000);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
