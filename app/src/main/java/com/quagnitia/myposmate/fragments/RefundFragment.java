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
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.centerm.smartpos.aidl.qrscan.AidlQuickScanZbar;
import com.centerm.smartpos.aidl.qrscan.AidlScanCallback;
import com.centerm.smartpos.aidl.qrscan.CameraBeanZbar;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.util.LogUtil;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.scanner.ScannerForBack;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;
import com.usdk.apiservice.aidl.scanner.OnScanListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import static com.quagnitia.myposmate.printer.ApiDemo.TAG;


public class RefundFragment extends Fragment implements OnTaskCompleted, View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private PreferencesManager preferencesManager;
    private Button btn_save1, btn_cancel1, btn_scan;
    private EditText edt_amount1, edt_transaction_no, edt_reference1, edt_merchant_id, edt_account_id, edt_reference_id;
    private View view;
    private ProgressDialog progress;
    TreeMap<String, String> hashMapKeys;

    public RefundFragment() {
    }

    public void openProgressDialog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    public static RefundFragment newInstance(String param1, String param2) {
        RefundFragment fragment = new RefundFragment();
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

    public void callAuthToken() {
        openProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
//        hashMap.put("username", AppConstants.CLIENT_ID);
//        hashMap.put("password",AppConstants.CLIENT_SECRET);
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.AUTH);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.refund_fragment, container, false);
        preferencesManager = PreferencesManager.getInstance(getActivity());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            getActivity().unbindService(conn);
        }
        getActivity().stopService(intentService);
    }


    public void initUI() {
        btn_scan = view.findViewById(R.id.btn_scan_reference);
        edt_amount1 =  view.findViewById(R.id.edt_amount1);
        edt_reference1 =  view.findViewById(R.id.edt_reference1);
        edt_transaction_no =  view.findViewById(R.id.edt_transaction_no);
        edt_merchant_id =  view.findViewById(R.id.edt_merchant_id);
        edt_account_id =  view.findViewById(R.id.edt_account_id);
        edt_reference_id =  view.findViewById(R.id.edt_reference_id);
        btn_save1 =  view.findViewById(R.id.btn_save1);
        btn_cancel1 =  view.findViewById(R.id.btn_cancel1);
        edt_amount1.setInputType(InputType.TYPE_CLASS_NUMBER);
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
        btn_scan.setOnClickListener(this);
        btn_cancel1.setOnClickListener(this);
    }

    private Context mContext;
    boolean isStartScan = false;
    boolean isOkClicked=false;

    @Override
    public void onClick(View v) {
        mContext = getActivity();
        if (((DashboardActivity) mContext).mPopupWindow.isShowing())
            ((DashboardActivity) mContext).mPopupWindow.dismiss();

        switch (v.getId()) {
            case R.id.btn_scan_reference:
                try {
                    isStartScan = true;
                    callAuthToken();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_save1:
                isOkClicked=true;
                callAuthToken();



                break;
            case R.id.btn_cancel1:
                edt_amount1.setText("0.00");
                edt_account_id.setText("");
                edt_merchant_id.setText("");
                edt_reference1.setText("");
                edt_reference_id.setText(preferencesManager.getreference_id());
                if (preferencesManager.isManual()) {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                } else {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                }
                break;
        }
    }


    public void callTransactionDetails2() {
        openProgressDialog();
        hashMapKeys.clear();
        hashMapKeys.put("access_id",preferencesManager.getuniqueId());
        hashMapKeys.put("branch_id", preferencesManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferencesManager.getterminalId().toString());
        hashMapKeys.put("config_id", preferencesManager.getConfigId());
        hashMapKeys.put("reference_id", edt_reference_id.getText().toString());
        hashMapKeys.put("random_str", new Date().getTime() + "");
        new OkHttpHandler(getActivity(), this, null, "TransactionDetails2")
                .execute(AppConstants.BASE_URL2 + AppConstants.GET_TRANSACTION_DETAILS + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferencesManager.getauthToken());

    }


    public void callTransactionDetails1() {
        openProgressDialog();
        hashMapKeys.clear();
        hashMapKeys.put("access_id",preferencesManager.getuniqueId());
        hashMapKeys.put("branch_id", preferencesManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferencesManager.getterminalId().toString());
        hashMapKeys.put("config_id", preferencesManager.getConfigId());
        hashMapKeys.put("reference_id", edt_reference_id.getText().toString());
        hashMapKeys.put("random_str", new Date().getTime() + "");
        new OkHttpHandler(getActivity(), this, null, "TransactionDetails1")
                .execute(AppConstants.BASE_URL2 + AppConstants.GET_TRANSACTION_DETAILS + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferencesManager.getauthToken());

    }


    public void callTransactionDetails() {
        hashMapKeys.clear();
        hashMapKeys.put("access_id",preferencesManager.getuniqueId());
        hashMapKeys.put("branch_id", preferencesManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferencesManager.getterminalId().toString());
        hashMapKeys.put("config_id", preferencesManager.getConfigId());
        hashMapKeys.put("reference_id", edt_reference_id.getText().toString());
        hashMapKeys.put("random_str", new Date().getTime() + "");
        new OkHttpHandler(getActivity(), this, null, "TransactionDetails")
                .execute(AppConstants.BASE_URL2 + AppConstants.GET_TRANSACTION_DETAILS + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferencesManager.getauthToken());

    }

    public void callRefundApi() {
        openProgressDialog();
        try {
            //v2 signature implementation
            hashMapKeys.clear();
            hashMapKeys.put("access_id",preferencesManager.getuniqueId());
            hashMapKeys.put("branch_id", preferencesManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferencesManager.getterminalId());
            hashMapKeys.put("config_id", preferencesManager.getConfigId());
            hashMapKeys.put("reference_id", edt_reference_id.getText().toString());
            hashMapKeys.put("refund_amount", edt_amount1.getText().toString());
            hashMapKeys.put("refund_password", edt_account_id.getText().toString());
            hashMapKeys.put("refund_reason", edt_reference1.getText().toString());
            hashMapKeys.put("random_str", new Date().getTime() + "");
            new OkHttpHandler(getActivity(), this, null, "refundNow")
                    .execute(AppConstants.BASE_URL2 + AppConstants.REFUND + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferencesManager.getauthToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    String roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        return twoDForm.format(d);
    }

    boolean isRefundRequestSuccess = false, isCallRefund = false;
    public static String alipaywechatamount = "0.0";
    public static JSONObject jsonObjectTransactionDetails;
    double remaining_amount=0.00;
    double refunded_amount=0.00;
    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
        if (progress != null && progress.isShowing())
            progress.dismiss();

        if (result.equals("")) {
            return;
        }

        JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {

            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferencesManager.setauthToken(jsonObject.optString("access_token"));
                }
                if (isStartScan) {
                    isStartScan = false;
                    stsartFastScan(true);
                }
                if (isRefundRequestSuccess) {
                    isRefundRequestSuccess = false;
                    callTransactionDetails();
                }
                if (isCallRefund) {
                    isCallRefund = false;
                    callRefundApi();
                }

                if(isOkClicked)
                {
                    isOkClicked=false;
                    callTransactionDetails2();
                }
                break;


            case "refundNow":
                //  callAuthToken();
                if (jsonObject.optBoolean("status")) {
                    if ((jsonObject.optString("refundStatus").equals("SUCCESS") ||
                            jsonObject.optString("refundStatus").equals("PROCESSING"))) {
                        edt_amount1.setText("0.00");
                        preferencesManager.setreference_id("");
                        Toast.makeText(getActivity(), "Refund Request Successful", Toast.LENGTH_LONG).show();
                        alipaywechatamount = "0.0";
                        isRefundRequestSuccess = true;
                        callAuthToken();
                    }
                    else if(jsonObject.optString("refundStatus").equals("GATEWAY_ERROR"))
                    {
                        Toast.makeText(getActivity(), "Gateway error occurred during transaction", Toast.LENGTH_SHORT).show();
                    }
                    else if (jsonObject.optString("code").equals("INVALID_PARAMETER")) {
                        Toast.makeText(getActivity(), "\n" +
                                "Incorrect parameters, possible causes: 1 order does not exist, 2 refund amount exceeds cumulative refund, 3 current order status does not support refund", Toast.LENGTH_LONG).show();
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.REFUND, null);
                    } else {
                        Toast.makeText(getActivity(), "Invalid Request", Toast.LENGTH_LONG).show();
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.REFUND, null);
                    }


                } else if (jsonObject.optString("code").equals("INVALID_PARAMETER")) {
                    Toast.makeText(getActivity(), "\n" +
                            "Incorrect parameters, possible causes: 1 order does not exist, 2 refund amount exceeds cumulative refund, 3 current order status does not support refund", Toast.LENGTH_LONG).show();
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.REFUND, null);
                } else {
                    Toast.makeText(getActivity(), "Refund Request UnSuccessful", Toast.LENGTH_LONG).show();
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.REFUND, null);
                }

                break;

            case "TransactionDetails":
                callAuthToken();
                edt_amount1.setText("");
                edt_transaction_no.setText("");
                edt_reference_id.setText("");
                edt_reference1.setText("");
                edt_account_id.setText("");
                if (progress.isShowing())
                    progress.dismiss();
                break;


            case "TransactionDetails2":
//                isCallRefund = true;
//                callAuthToken();
                refunded_amount=0.0;
                JSONObject paymentJSONObject=jsonObject.optJSONObject("payment");
if(alipaywechatamount.equals(""))alipaywechatamount="0.0";
                if(jsonObject.has("refunds"))
                {
                    JSONArray jsonArrayRefund=jsonObject.optJSONArray("refunds");
                    for(int i=0;i<jsonArrayRefund.length();i++)
                    {
                        refunded_amount = refunded_amount + Double.parseDouble(jsonArrayRefund.optJSONObject(i).optString("refundFee"));
                        refunded_amount=Double.parseDouble(roundTwoDecimals(refunded_amount));

                    }
                    remaining_amount=Double.parseDouble(paymentJSONObject.optString("grandTotal"))-refunded_amount;
                    remaining_amount=Double.parseDouble(roundTwoDecimals(remaining_amount));
                }
                if (Double.parseDouble(edt_amount1.getText().toString()) > remaining_amount && remaining_amount>0.0){
                    Toast.makeText(getActivity(), "Amount entered is greater than the original amount used in the transaction.", Toast.LENGTH_SHORT).show();
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.REFUND, null);
                } else {
                    if (edt_amount1.getText().toString().equals("") || edt_amount1.getText().toString().equals("0.00")) {
                        Toast.makeText(getActivity(), "Enter amount", Toast.LENGTH_SHORT).show();
                    } else if (edt_reference_id.getText().toString().equals("")) {

                        Toast.makeText(getActivity(), "Enter reference id", Toast.LENGTH_SHORT).show();
                    } else if (edt_reference1.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Enter refund reason", Toast.LENGTH_SHORT).show();
                    } else if (edt_account_id.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Enter refund password", Toast.LENGTH_SHORT).show();
                    } else if (!alipaywechatamount.equals("0.0") && Double.parseDouble(edt_amount1.getText().toString()) > Double.parseDouble(alipaywechatamount)) {
                        Toast.makeText(getActivity(), "Amount entered is greater than the original amount used in the transaction.", Toast.LENGTH_SHORT).show();
                    } /*else if (jsonObjectTransactionDetails != null && Double.parseDouble(edt_amount1.getText().toString()) >
                            Double.parseDouble(jsonObjectTransactionDetails.optString("remaining_amount"))) {
                        Toast.makeText(getActivity(), "Entered amount is greater than remaining amount.", Toast.LENGTH_SHORT).show();
                    }*/ else {
                        isCallRefund = true;
                        callAuthToken();
                    }
                }


                break;

            case "TransactionDetails1":
                callAuthToken();
                refunded_amount=0.0;
                jsonObjectTransactionDetails = jsonObject.optJSONObject("payment");
                JSONArray jsonArrayRefund=null;
                if(jsonObject.has("refunds"))
                {
                     jsonArrayRefund=jsonObject.optJSONArray("refunds");
                    for(int i=0;i<jsonArrayRefund.length();i++)
                    {
                        refunded_amount = refunded_amount + Double.parseDouble(jsonArrayRefund.optJSONObject(i).optString("refundFee"));
                        refunded_amount=Double.parseDouble(roundTwoDecimals(refunded_amount));
                    }
                    remaining_amount=Double.parseDouble(jsonObjectTransactionDetails.optString("grandTotal"))-refunded_amount;
                    remaining_amount=Double.parseDouble(roundTwoDecimals(remaining_amount));
                }
                if (jsonObjectTransactionDetails.optString("paymentStatus").equals("REFUND")) {
                    if (Double.parseDouble(jsonArrayRefund.optJSONObject(0).optString("receiptAmount")) >
                            refunded_amount) {
                        btn_save1.setEnabled(true);
                        edt_amount1.setText(remaining_amount + "");
                        if (!jsonArrayRefund.optJSONObject(jsonArrayRefund.length()-1).optString("refundReason").equals("") &&
                                !jsonArrayRefund.optJSONObject(jsonArrayRefund.length()-1).optString("refundReason").equals("null")) {
                            edt_reference1.setText(jsonArrayRefund.optJSONObject(jsonArrayRefund.length()-1).optString("refundReason"));
                        }
                    } else if (Double.parseDouble(jsonArrayRefund.optJSONObject(0).optString("receiptAmount")) ==
                            refunded_amount) {
                        btn_save1.setEnabled(false);

                        Toast.makeText(getActivity(), "This Transaction is already refunded", Toast.LENGTH_SHORT).show();
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.REFUND, null);
                    }
                } else if (jsonObjectTransactionDetails.optString("paymentStatus").equals("SUCCESS")) {
                    btn_save1.setEnabled(true);
                    edt_amount1.setText(Double.parseDouble(jsonObjectTransactionDetails.optString("receiptAmount")) + "");
//                    if (!jsonObject.optString("ref1").equals("") &&
//                            !jsonObject.optString("ref1").equals("null")) {
//                        edt_reference1.setText(jsonObject.optString("ref1"));
//                    }
                }

                alipaywechatamount = edt_amount1.getText().toString();

                break;
        }


    }


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

                                callTransactionDetails1();

                            }
                        });


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

