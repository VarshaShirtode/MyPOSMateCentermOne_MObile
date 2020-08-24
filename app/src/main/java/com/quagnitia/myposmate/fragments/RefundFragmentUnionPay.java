package com.quagnitia.myposmate.fragments;

/**
 * Created by admin on 7/20/2018.
 */

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.centerm.smartpos.aidl.qrscan.AidlQuickScanZbar;
import com.centerm.smartpos.aidl.qrscan.AidlScanCallback;
import com.centerm.smartpos.aidl.qrscan.CameraBeanZbar;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.util.LogUtil;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.arke.VASCallsArkeBusiness;
import com.quagnitia.myposmate.centrum.ThirtConst;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import static com.quagnitia.myposmate.fragments.ManualEntry.shadaf;


public class RefundFragmentUnionPay extends Fragment implements OnTaskCompleted, View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private PreferencesManager preferenceManager;
    private Button btn_save1, btn_cancel1;
    private EditText edt_amount1, edt_description, edt_password, edt_transaction_no, edt_order_no, edt_reference_id;
    private View view;
    private ProgressDialog progress;
    private LinearLayout ll_se, ll_first1, ll_refund_reason, ll_refund_password, ll_amount1, ll_reference_id, ll_order_no, ll_transaction_no;
    private Button btn_save2, btn_cancel2;

    private Button btn_scan_reference;
    private VASCallsArkeBusiness vasCallsArkeBusiness;


    public RefundFragmentUnionPay() {
        // Required empty public constructor
    }

    public void openProgressDialog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    public static RefundFragmentUnionPay newInstance(String param1, String param2) {
        RefundFragmentUnionPay fragment = new RefundFragmentUnionPay();
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
        }
    }

    public void callAuthToken() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.AUTH);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.refund_fragment_unionpay, container, false);
        preferenceManager = PreferencesManager.getInstance(getActivity());
        hashMapKeys = new TreeMap<>();
        bindService();
        callAuthToken();
        initUI();
        initListener();
        view.findViewById(R.id.ll_two).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (((DashboardActivity) getActivity()).mPopupWindow.isShowing()) {
                    ((DashboardActivity) getActivity()).mPopupWindow.dismiss();
                }
                return false;
            }
        });
        return view;
    }

    public void initUI() {
        vasCallsArkeBusiness = new VASCallsArkeBusiness(getActivity());
        edt_amount1 = view.findViewById(R.id.edt_amount1);
        edt_order_no = view.findViewById(R.id.edt_order_no);
        edt_transaction_no = view.findViewById(R.id.edt_transaction_date);
        edt_reference_id = view.findViewById(R.id.edt_reference_no);
        btn_save1 = view.findViewById(R.id.btn_save1);
        btn_cancel1 = view.findViewById(R.id.btn_cancel1);
        edt_amount1.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_description = view.findViewById(R.id.edt_description);
        edt_password = view.findViewById(R.id.edt_password);
        btn_scan_reference = view.findViewById(R.id.btn_scan_reference);
        ll_amount1 = view.findViewById(R.id.ll_amount1);
        ll_reference_id = view.findViewById(R.id.ll_reference_id);
        ll_transaction_no = view.findViewById(R.id.ll_transaction_no);
        ll_order_no = view.findViewById(R.id.ll_order_no);
        ll_refund_password = view.findViewById(R.id.ll_refund_password);
        ll_refund_reason = view.findViewById(R.id.ll_refund_desc);
        ll_refund_reason.setVisibility(View.GONE);
        ll_refund_password.setVisibility(View.GONE);

        ll_se = view.findViewById(R.id.ll_se);
        ll_first1 = view.findViewById(R.id.ll_first1);

        ll_reference_id.setVisibility(View.VISIBLE);
        ll_amount1.setVisibility(View.GONE);
        ll_transaction_no.setVisibility(View.GONE);
        ll_order_no.setVisibility(View.GONE);
        ll_first1.setVisibility(View.GONE);
        ll_se.setVisibility(View.VISIBLE);

        btn_save2 = view.findViewById(R.id.btn_save2);
        btn_cancel2 = view.findViewById(R.id.btn_cancel2);

        edt_amount1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edt_amount1.getText().toString().equals("0.00")) {
                    edt_amount1.setText("");
                }
                return false;
            }
        });
    }

    public void initListener() {
        btn_save1.setOnClickListener(this);
        btn_scan_reference.setOnClickListener(this);
        btn_cancel1.setOnClickListener(this);
        btn_save2.setOnClickListener(this);
        btn_cancel2.setOnClickListener(this);
    }

    public static boolean isScanned = false;


    private AidlQuickScanZbar aidlQuickScanService = null;
    private int bestWidth = 640;
    private int bestHeight = 480;
    private int spinDegree = 90;
    private int cameraDisplayEffect = 0;

    private void switchCameraDisplayEffect(boolean cameraBack) {
        try {
            aidlQuickScanService.switchCameraDisplayEffect(cameraBack ? 0 : 1, cameraDisplayEffect);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stsartFastScan(boolean cameraBack) {
        final long startTime = System.currentTimeMillis();
        try {
            CameraBeanZbar cameraBean = new com.centerm.smartpos.aidl.qrscan.CameraBeanZbar(0, bestWidth, bestHeight, 4, Integer.MAX_VALUE, spinDegree, 1);
            if (cameraBack) {
                cameraBean.setCameraId(0);
            } else {
                cameraBean.setCameraId(1);
            }
            HashMap<String, Object> externalMap = new HashMap<String, Object>();
            externalMap.put("ShowPreview", true);
            cameraBean.setExternalMap(externalMap);
            switchCameraDisplayEffect(cameraBack);//2018-03-06 增加切换摄像头显示效果 linpeita@centerm.com
            aidlQuickScanService.scanQRCode(cameraBean, new AidlScanCallback.Stub() {
                @Override
                public void onFailed(int arg0) throws RemoteException {

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), getString(R.string.scan_fail), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onCaptured(String arg0, int arg1) throws RemoteException {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), getString(R.string.scan_success) + "\n" + getString(R.string.scan_info) + "\n" + arg0, Toast.LENGTH_SHORT).show();
                        }
                    });

                    long SuccessEndTime = System.currentTimeMillis();
                    long SuccessCostTime = SuccessEndTime - startTime;
                    if (getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {

                                edt_reference_id.setText(arg0 + "");
                                edt_reference_id.setEnabled(false);
                                edt_order_no.setEnabled(false);
                                edt_transaction_no.setEnabled(true);
                                isScanned = true;
                                callAuthToken();


                            }
                        });


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callRefundApi() {
        openProgressDialog();
        try {
            hashMapKeys.clear();
            hashMapKeys.put("access_id", preferenceManager.getuniqueId());
            hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
            hashMapKeys.put("config_id", preferenceManager.getConfigId());
            hashMapKeys.put("reference_id", referenecno);
            hashMapKeys.put("refund_amount", edt_amount1.getText().toString());
            hashMapKeys.put("refund_password", edt_password.getText().toString());
            hashMapKeys.put("refund_reason", edt_description.getText().toString());
            hashMapKeys.put("random_str", new Date().getTime() + "");

            new OkHttpHandler(getActivity(), this, null, "refundNow")
                    .execute(AppConstants.BASE_URL2 + AppConstants.REFUND + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());


            callAuthToken();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Context mContext;

    @Override
    public void onClick(View v) {
        mContext = getActivity();
        if (((DashboardActivity) mContext).mPopupWindow.isShowing())
            ((DashboardActivity) mContext).mPopupWindow.dismiss();

        switch (v.getId()) {

            case R.id.btn_scan_reference:
                try {
                    stsartFastScan(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


            case R.id.btn_save2:
                if (edt_reference_id.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter reference no", Toast.LENGTH_SHORT).show();
                } else {
                    callTransactionDetails();
                }
                break;

            case R.id.btn_cancel2:
            case R.id.btn_cancel1:
                edt_reference_id.setEnabled(true);
                edt_order_no.setEnabled(true);
                edt_transaction_no.setEnabled(true);
                if (preferenceManager.isManual()) {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                } else {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                }
                break;

            case R.id.btn_save1:
                if (edt_amount1.getText().toString().equals("") || edt_amount1.getText().toString().equals("0.00")) {
                    Toast.makeText(getActivity(), "Enter amount", Toast.LENGTH_SHORT).show();
                } else if (edt_reference_id.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter reference no", Toast.LENGTH_SHORT).show();
                } else if (isThirdParty && edt_transaction_no.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter transaction date(MMDD)", Toast.LENGTH_SHORT).show();
                } else if (isThirdParty && edt_order_no.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter order no", Toast.LENGTH_SHORT).show();
                } else if (!isThirdParty && edt_password.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter refund password", Toast.LENGTH_SHORT).show();
                } else if (!isThirdParty && edt_description.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter refund description", Toast.LENGTH_SHORT).show();
                } else if (alipaywechatamount != 0.0 && Double.parseDouble(edt_amount1.getText().toString()) > alipaywechatamount) {
                    Toast.makeText(getActivity(), "Amount entered is greater than the original amount used in the transaction.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        refund_amount = edt_amount1.getText().toString();
                        if (isThirdParty) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("transactionType", "REFUND");
                            jsonObject.put("amount", edt_amount1.getText().toString());
                            jsonObject.put("originalReferenceNumber", referenecno);//edt_reference_id.getText().toString());
                            jsonObject.put("orderNumber", edt_order_no.getText().toString());
                            jsonObject.put("originalTransactionDate", edt_transaction_no.getText().toString());
                            beginRefund(jsonObject);
                        } else {
                            callRefundApi();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                break;

        }
    }

    String roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        return twoDForm.format(d);
    }

    TreeMap<String, String> hashMapKeys;
    String refund_amount = "";

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
            jsonObject.put("status_id", status);
            json_data = jsonObject.toString();
            preferenceManager.setreference_id(jsonObject.optString("orderNumber"));
            hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
            hashMapKeys.put("system", preferenceManager.getterminalId());
            hashMapKeys.put("channel", "UNION_PAY");
            hashMapKeys.put("access_id", preferenceManager.getuniqueId());
            hashMapKeys.put("config_id", preferenceManager.getConfigId());
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("rate", "0");
            hashMapKeys.put("currency", "NZD");
            hashMapKeys.put("transaction_id", jsonObjectTransactionDetails.optJSONObject("payment").optString("id"));

            if (jsonObject.optString("transactionType").equals("SALE") ||
                    jsonObject.optString("transactionType").equals("COUPON_SALE") ||
                    jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_SALE")
            ) {
                hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(jsonObject.optString("amount"))));
                hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(jsonObject.optString("amount"))));
            } else if (jsonObjectTransactionDetails.optJSONObject("payment").has("serverResponse")) {
                if (!jsonObjectGatewayResponse.equals("")) {
                    hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(jsonObjectGatewayResponse.optString("amount"))));
                    hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(jsonObjectGatewayResponse.optString("amount"))));
                    hashMapKeys.put("refund_amount", refund_amount + "");
                    hashMapKeys.put("refund_trade_no", jsonObject.optString("referenceNumber") + "");
                }
            }

            hashMapKeys.put("reference_id", edt_reference_id.getText().toString());//jsonObject.optString("referenceNumber"));
            hashMapKeys.put("server_response", android.util.Base64.encodeToString((s + json_data + "}").getBytes(), Base64.NO_WRAP));
            hashMapKeys.put("trade_no", jsonObject.optString("referenceNumber"));
            hashMapKeys.put("is_success", true + "");
            hashMapKeys.put("is_payment", false + "");
            hashMapKeys.put("thirdParty", true + "");
            String s2 = "", s1 = "";
            int i1 = 0;
            Iterator<String> iterator = hashMapKeys.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (i1 != hashMapKeys.size() - 1) {
                    s2 = s2 + key + "=" + hashMapKeys.get(key) + "&";
                    s1 = s1 + key + "=" + hashMapKeys.get(key) + "&";
                } else {
                    s2 = s2 + key + "=" + hashMapKeys.get(key);
                    s1 = s1 + key + "=" + hashMapKeys.get(key);
                }
                i1++;
            }
            s2 = s2 + AppConstants.CLIENT_ID + PreferencesManager.getInstance(getActivity()).getauthToken();//.getuniqueId();
            String signature = MD5Class.MD5(s2);
            new OkHttpHandler(getActivity(), this, null, "saveTransaction")
                    .execute(AppConstants.BASE_URL2 + AppConstants.SAVETRANSACTIONUNIONPAY + "?" + s1 + "&signature=" + signature + "&access_token=" + preferenceManager.getauthToken());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callTransactionDetails() {
        openProgressDialog();
        hashMapKeys.clear();
        hashMapKeys.put("access_id", preferenceManager.getuniqueId());
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        if (!edt_reference_id.getText().toString().equals(""))
            hashMapKeys.put("reference_id", edt_reference_id.getText().toString());
        else
            return;
        hashMapKeys.put("random_str", new Date().getTime() + "");
        new OkHttpHandler(getActivity(), this, null, "TransactionDetails")
                .execute(AppConstants.BASE_URL2 + AppConstants.GET_TRANSACTION_DETAILS + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            getActivity().unbindService(conn);
        }
        getActivity().stopService(intentService);
    }

    boolean isUnionPayStatusUpdate = false;

    @Override
    public void onResume() {
        super.onResume();
    }

    JSONObject jsonObjectSale;
    Intent intentCen = new Intent();
    String packageName = "com.centerm.dynaminpayinskate";
    String activityName = "org.skate.pay.component.PayEntry";
    ComponentName comp = new ComponentName(packageName, activityName);
    private static final int REQ_PAY_SALE = 100;

    public void beginRefund(JSONObject jsonObjectSale) {
        this.jsonObjectSale = jsonObjectSale;
        hideSoftInput();

        try {
            intentCen.setComponent(comp);
            Bundle bundle = new Bundle();
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.REFUND);
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_REFERENCE_NO, jsonObjectSale.optString("originalReferenceNumber"));
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_TRANS_DATE, jsonObjectSale.optString("originalTransactionDate"));
            bundle.putDouble(ThirtConst.RequestTag.THIRD_PATH_TRANS_AMOUNT, jsonObjectSale.optDouble("amount"));
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObjectSale.optString("orderNumber"));

            if (isScannedUnionPayQr) {
                isScannedUnionPayQr = false;
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_SCAN_AUTH_PAY_CODE, qrcode);
            }

            intentCen.putExtras(bundle);
            startActivityForResult(intentCen, REQ_PAY_SALE);

        } catch (Exception e) {
            e.printStackTrace();
        }


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
            }

        }

    }


    /**
     * Hide soft keyboard
     * <p>
     * 隐藏软键盘
     */
    private void hideSoftInput() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    JSONObject jsonObject;
    public static String qrcode = "";
    public static boolean isScannedUnionPayQr = false;
    public static double alipaywechatamount = 0.0;
    private static boolean isThirdParty = false;
    JSONObject jsonObjectTransactionDetails;
    JSONObject jsonObjectGatewayResponse;
    double remaining_amount = 0.00;
    double refunded_amount = 0.00;

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {

        if (result.equals("")) {
            if (progress != null && progress.isShowing())
                progress.dismiss();
            if (!TAG.equals("unionpaystatus"))
                Toast.makeText(getActivity(), "No data from server.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!TAG.equals("Arke")) {
            if (progress != null && progress.isShowing())
                progress.dismiss();
        }

        jsonObject = new JSONObject(result);
        switch (TAG) {
            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferenceManager.setauthToken(jsonObject.optString("access_token"));

                    if (isScanned) {
                        isScanned = false;
                        callTransactionDetails();
                    }

                    if (isUnionPayStatusUpdate) {
                        isUnionPayStatusUpdate = false;
                        callUnionPayStatus(ManualEntry.val, "true");
                    }

                    if (AppConstants.isRefundUnionpayDone) {
                        AppConstants.isRefundUnionpayDone = false;
                        callUnionPayStatus(ManualEntry.val, true + "");
                    }

                }
                break;
            case "Arke":
                if (jsonObject.has("responseCodeThirtyNine")) {
                    if (jsonObject.has("responseCodeThirtyNine") && jsonObject.optString("responseCodeThirtyNine").equals("00")) {
                        preferenceManager.setunion_pay_resp(jsonObject.toString());
                        shadaf = true;
                        ManualEntry.val = jsonObject.toString();
                    }
                }
                if (shadaf) {
                    isUnionPayStatusUpdate = true;
                    callAuthToken();

                }

                break;


            case "TransactionDetails":
                callAuthToken();
                jsonObjectTransactionDetails = jsonObject;
                if (jsonObject.optJSONObject("payment").has("serverResponse")) {

                    if (jsonObject.optJSONObject("payment").has("thirdParty") &&
                            jsonObject.optJSONObject("payment").optBoolean("thirdParty")) {
                        isThirdParty = true;

                        _parseDpAppResponse(jsonObject);


                    } else {
                        isThirdParty = false;

                        _parseMPMCloudResponse(jsonObject);
                    }


                } else {
                    if (jsonObject.optString("message").equals("Invalid Trade ID")) {
                        Toast.makeText(getActivity(), "Invalid Reference no", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                }

                break;
            case "refundNow":
                callAuthToken();
                if (progress != null && progress.isShowing())
                    progress.dismiss();

                if (jsonObject.optBoolean("status")) {
                    Toast.makeText(getActivity(), "Refund Request Successful.", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(getActivity(), "Refund Request UnSuccessful." + jsonObject.optString("message"), Toast.LENGTH_LONG).show();


                preferenceManager.setunion_pay_resp("");
                shadaf = false;
                if (preferenceManager.isManual()) {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                } else {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                }

                break;

            case "saveTransaction":
                callAuthToken();
                if (progress != null && progress.isShowing())
                    progress.dismiss();

                if (jsonObject.optBoolean("status")) {
                    refund_time = "";
                    refund_trade_no = "";
                    shadaf = false;
                    AppConstants.isRefundUnionpayDone = true;
                    preferenceManager.setunion_pay_resp("");
                    Toast.makeText(getActivity(), "Transaction status updated successfully", Toast.LENGTH_SHORT).show();
                    shadaf = false;
                    if (preferenceManager.isManual()) {
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                    } else {
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                    }
                } else {
                    Toast.makeText(getActivity(), "Transaction status not updated", Toast.LENGTH_SHORT).show();
                }


                break;

        }
    }

    public void _parseMPMCloudResponse(JSONObject jsonObject) throws Exception {
        JSONObject jsonObjectPayment = jsonObject.optJSONObject("payment");
        ll_amount1.setVisibility(View.VISIBLE);
        ll_se.setVisibility(View.GONE);
        ll_first1.setVisibility(View.VISIBLE);

        ll_order_no.setVisibility(View.GONE);
        ll_transaction_no.setVisibility(View.GONE);
        ll_refund_reason.setVisibility(View.VISIBLE);
        ll_refund_password.setVisibility(View.VISIBLE);
        refund_time = jsonObjectPayment.optString("createDate");
        referenecno = jsonObjectPayment.optString("referenceId");
        if (jsonObject.has("refunds")) {
            JSONArray jsonArrayRefund = jsonObject.optJSONArray("refunds");
            for (int i = 0; i < jsonArrayRefund.length(); i++) {

                if (jsonObjectPayment.optString("channel").equals("UNION_PAY")) {
                    refunded_amount = refunded_amount + Double.parseDouble(jsonArrayRefund.optJSONObject(i).optString("refundFee"));
                } else {
                    refunded_amount = refunded_amount + Double.parseDouble(jsonArrayRefund.optJSONObject(i).optString("refundFee"));
                }

                refunded_amount = Double.parseDouble(roundTwoDecimals(refunded_amount));

            }
            remaining_amount = Double.parseDouble(jsonObjectPayment.optString("receiptAmount")) - refunded_amount;
            remaining_amount = Double.parseDouble(roundTwoDecimals(remaining_amount));
            if (remaining_amount != 0.00)
                edt_amount1.setText(roundTwoDecimals(Double.parseDouble(remaining_amount + "")));
        } else {
            edt_amount1.setText(roundTwoDecimals(Double.parseDouble(jsonObjectPayment.optString("receiptAmount"))));
        }

        edt_order_no.setText(jsonObjectPayment.optString("referenceId"));

        alipaywechatamount = Double.parseDouble(jsonObjectPayment.optString("grandTotal"));
        edt_transaction_no.setText(refund_time);
    }


    public void _parseDpAppResponse(JSONObject jsonObject) throws Exception {
        {
            byte data[] = android.util.Base64.decode(android.util.Base64.decode(jsonObject.optJSONObject("payment").optString("serverResponse"), Base64.NO_WRAP), Base64.NO_WRAP);
            String serverResponse = new String(data, "UTF-8");
            JSONObject jsonObjectServerResponse = new JSONObject(serverResponse);
            jsonObjectGatewayResponse = jsonObjectServerResponse.optJSONObject("body");
            if (jsonObjectGatewayResponse.optString("transactionType").equals("COUPON_SALE") ||
                    jsonObjectGatewayResponse.optString("transactionType").equals("COUPON_VOID")) {
                Toast.makeText(getActivity(), "Refund of coupon transaction cannot be done.", Toast.LENGTH_SHORT).show();
                return;
            }

            ll_amount1.setVisibility(View.VISIBLE);
            ll_se.setVisibility(View.GONE);
            ll_first1.setVisibility(View.VISIBLE);

            ll_order_no.setVisibility(View.VISIBLE);
            ll_transaction_no.setVisibility(View.VISIBLE);
            ll_refund_reason.setVisibility(View.GONE);
            ll_refund_password.setVisibility(View.GONE);

            refund_time = jsonObjectGatewayResponse.optString("createdDate");
            referenecno = jsonObjectGatewayResponse.optString("referenceNumber");
            if (jsonObject.has("refunds")) {
                JSONArray jsonArrayRefund = jsonObject.optJSONArray("refunds");
                for (int i = 0; i < jsonArrayRefund.length(); i++) {

                    if (jsonObject.optJSONObject("payment").optString("channel").equals("UNION_PAY")) {
                        refunded_amount = refunded_amount + Double.parseDouble(jsonArrayRefund.optJSONObject(i).optString("refundFee"));
                    } else {
                        refunded_amount = refunded_amount + Double.parseDouble(jsonArrayRefund.optJSONObject(i).optString("refundFee"));
                    }

                    refunded_amount = Double.parseDouble(roundTwoDecimals(refunded_amount));

                }
                remaining_amount = Double.parseDouble(jsonObject.optJSONObject("payment").optString("receiptAmount")) - refunded_amount;
                remaining_amount = Double.parseDouble(roundTwoDecimals(remaining_amount));
                if (remaining_amount != 0.00)
                    edt_amount1.setText(roundTwoDecimals(Double.parseDouble(remaining_amount + "")));
            } else {
                edt_amount1.setText(roundTwoDecimals(Double.parseDouble(jsonObject.optJSONObject("payment").optString("receiptAmount"))));
            }

            edt_order_no.setText(jsonObjectGatewayResponse.optString("orderNumber"));
            if (jsonObjectGatewayResponse.optString("transactionType").equals("UPI_SCAN_CODE_SALE")
                    && jsonObjectGatewayResponse.has("qrcode")) {
                isScannedUnionPayQr = true;
                qrcode = jsonObjectGatewayResponse.optString("qrcode");
            }
            alipaywechatamount = Double.parseDouble(jsonObject.optJSONObject("payment").optString("grandTotal"));
//                    alipaywechatamount=Double.parseDouble(jsonObject.optJSONObject("server_response").optJSONObject("body").optString("amount"));
            char formats1[] = jsonObjectGatewayResponse.optString("transactionDate").toCharArray();//formatter5.format(date).toString().split("-");
            edt_transaction_no.setText(formats1[4] + "" + formats1[5] + "" + formats1[6] + "" + formats1[7] + "");
        }
    }


    public String refund_time = "", refund_trade_no = "";
    public String referenecno = "";


    public AidlDeviceManager manager = null;

    Intent intentService;

    public void bindService() {
        intentService = new Intent();
        intentService.setPackage("com.centerm.smartposservice");
        intentService.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        getActivity().bindService(intentService, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 服务连接桥
     */
    public ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            manager = null;
            LogUtil.print(getResources().getString(R.string.bind_service_fail));
            LogUtil.print("manager = " + manager);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            manager = AidlDeviceManager.Stub.asInterface(service);
            LogUtil.print(getResources().getString(R.string.bind_service_success));
            LogUtil.print("manager = " + manager);
            if (null != manager) {
                try {
                    onDeviceConnected(manager);
                } catch (Exception e) {

                }

            }
        }


    };

    public void onDeviceConnected(AidlDeviceManager deviceManager) {
        try {
            aidlQuickScanService = AidlQuickScanZbar.Stub.asInterface(deviceManager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_QUICKSCAN));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}

