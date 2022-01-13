//package com.quagnitia.myposmate.fragments;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.RemoteException;
//import android.support.v4.app.Fragment;
//import android.text.Editable;
//import android.text.InputType;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.centerm.smartpos.aidl.qrscan.AidlQuickScanZbar;
//import com.centerm.smartpos.aidl.qrscan.AidlScanCallback;
//import com.centerm.smartpos.aidl.qrscan.CameraBeanZbar;
//import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
//import com.centerm.smartpos.constant.Constant;
//import com.centerm.smartpos.util.LogUtil;
//import com.google.zxing.integration.android.IntentIntegrator;
//import com.google.zxing.integration.android.IntentResult;
//import com.quagnitia.myposmate.MyPOSMateApplication;
//import com.quagnitia.myposmate.R;
//import com.quagnitia.myposmate.activities.DashboardActivity;
//import com.quagnitia.myposmate.activities.PaymentExpressActivity;
//import com.quagnitia.myposmate.arke.TransactionNames;
//import com.quagnitia.myposmate.arke.VASCallsArkeBusiness;
//import com.quagnitia.myposmate.centrum.ThirtConst;
//import com.quagnitia.myposmate.utils.AppConstants;
//import com.quagnitia.myposmate.utils.HomeWatcher;
//import com.quagnitia.myposmate.utils.MD5Class;
//import com.quagnitia.myposmate.utils.OkHttpHandler;
//import com.quagnitia.myposmate.utils.OnHomePressedListener;
//import com.quagnitia.myposmate.utils.OnTaskCompleted;
//import com.quagnitia.myposmate.utils.PreferencesManager;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.net.URLDecoder;
//import java.net.URLEncoder;
//import java.text.DecimalFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Locale;
//import java.util.Set;
//import java.util.TreeMap;
//import java.util.UUID;
//
//import faranjit.currency.edittext.CurrencyEditText;
//
//import static android.content.Context.INPUT_METHOD_SERVICE;
//
//
//public class ManualEntry extends Fragment implements View.OnClickListener, OnTaskCompleted {
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//    private ProgressDialog progress, progress1;
//    private String mParam1;
//    private String mParam2;
//    private Button btn_save, btn_cancel, btn_save1, btn_cancel1;
//    private View view;
//    private LinearLayout ll_second, ll_amount, ll_reference, ll_amount1, ll_reference1;
//    private RelativeLayout ll_first;
//    private CurrencyEditText edt_amount, edt_amount1;
//    private EditText edt_reference, edt_reference1;
//    private TextView tv_status_scan, tv_noitem, scanqr, scanqr_unionpay, tv_enable_payment, tv_selection_type, edt_xmpp_amount, edt_xmpp_amount1, tv_alipay, tv_wechat, tv_unipay, tv_vice;
//    private ImageView img_alipay, img_wechat, img_scan, img_unipay, img_upay, img_paymentexpress;
//    public static int selected_screen = 0;
//    private PreferencesManager preferenceManager;
//    private String payment_mode = "", qrMode = "";
//    private IntentFilter intentFilter;
//    private AmountReceiver amountReceiver;
//    public static String xmppAmount = "";
//    public static String auth_code = "";
//    private CountDownTimer countDownTimer;
//    public String reference_id = "";
//    private LinearLayout ll_one, ll_two;
//    private View ag_v1, ag_v3;
//    private LinearLayout ag_v2;
//    private TextView title, tv_other, tv_qrcode, tv_start_countdown, tv_scan_code;
//    private RelativeLayout rel_unionpay, rel_wechat, rel_alipay, rel_scan, rel_paymentexpress;
//    private CountDownTimer countDownTimerxmpp;
//    private ImageView img_alipay_static_qr;
//    private View view_mid1, view_mid2;
//    String amount = "";
//    private IntentIntegrator qrScan;
//    static boolean shadaf = false;
//    TextView tv_uni_cv, tv_ali_cv;
//    private static String arkeAppPackageName = "com.arke.hk_dp";
//    private static String sdkAppPackageName = "com.arke.sdk.demo";
//    public static Context context;
//    private TransactionNames currenTransaction;
//    private VASCallsArkeBusiness vasCallsArkeBusiness;
//    HomeWatcher mHomeWatcher;
//    TreeMap<String, String> hashMapKeys;
//    private Button btn_back, btn_front, tv_status_scan_button;
//    private RelativeLayout rel_membership;
//
//    public static ManualEntry newInstance(String param1, String param2) {
//        ManualEntry fragment = new ManualEntry();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    public void openProgressDialog() {
//        progress = new ProgressDialog(getActivity());
//        progress.setMessage("Loading.......");
//        progress.setCancelable(false);
//        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progress.setIndeterminate(true);
//        progress.show();
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        if (mParam1.equalsIgnoreCase("Scan")) {
//            payment_mode = "";
//            qrMode = "False";
//            open();
//        }
//
//        hashMapKeys = new TreeMap<>();
//        selected_screen = 0;
//        view = inflater.inflate(R.layout.fragment_manual_entry, container, false);
//
//
//        if (DashboardActivity.isExternalApp) {
//            view.findViewById(R.id.rel_loyalty).setVisibility(View.GONE);
//        }
//
//
//        preferenceManager = PreferencesManager.getInstance(getActivity());
//        if (!preferenceManager.getreference_id().equals("")) {
//            reference_id = "";
//            preferenceManager.setreference_id("");
//
//        }
//        preferenceManager.setisResetTerminal(false);
//        preferenceManager.settriggerReferenceId("");
//        amountReceiver = new AmountReceiver();
//        intentFilter = new IntentFilter();
//        intentFilter.addAction("AmountTrigger");
//        intentFilter.addAction("ThirdPartyAppTrigger");
//        intentFilter.addAction("ScannedCode");
//        intentFilter.addAction("ScannedCode1");
//        intentFilter.addAction("ScannedCodeUnionPayQr");
//        intentFilter.addAction("PaymentExpressSuccess");
//        intentFilter.addAction("PaymentExpressFailure");
//        getActivity().registerReceiver(amountReceiver, intentFilter);
//        bindService();
//        callAuthToken();
//        initUI(view);
//        initListener();
//        title.setVisibility(View.VISIBLE);
//        if (getArguments() != null) {
//            if (getArguments().getString(ARG_PARAM1).equals("xmpp")) {
//                if (preferenceManager.isaggregated_singleqr() && !preferenceManager.isUnipaySelected()) {
//                    view.findViewById(R.id.mainui).setVisibility(View.GONE);
//
//                } else if (preferenceManager.isaggregated_singleqr() && preferenceManager.isUnipaySelected()) {
//                    view.findViewById(R.id.mainui).setVisibility(View.VISIBLE);
//
//                } else if (preferenceManager.isUnipaySelected() && !preferenceManager.isaggregated_singleqr()) {
//                    view.findViewById(R.id.mainui).setVisibility(View.GONE);
//                }
//            }
//        }
//
//
//        if (preferenceManager.getshowReference().equals("true")) {
//            view.findViewById(R.id.tv_reference).setVisibility(View.VISIBLE);
//            view.findViewById(R.id.tv_reference1).setVisibility(View.VISIBLE);
//            edt_reference.setVisibility(View.VISIBLE);
//            edt_reference1.setVisibility(View.VISIBLE);
//        } else {
//            view.findViewById(R.id.tv_reference).setVisibility(View.INVISIBLE);
//            view.findViewById(R.id.tv_reference1).setVisibility(View.INVISIBLE);
//            edt_reference.setText("");
//            edt_reference1.setText("");
//            edt_reference.setVisibility(View.INVISIBLE);
//            edt_reference1.setVisibility(View.INVISIBLE);
//        }
//
//
//        if (preferenceManager.isUnionPaySelected() && preferenceManager.isUplanSelected()) {
//            img_unipay.setVisibility(View.VISIBLE);
//            img_upay.setVisibility(View.VISIBLE);
//            view_mid1.setVisibility(View.VISIBLE);
//        } else if (preferenceManager.isUnionPaySelected() && !preferenceManager.isUplanSelected()) {
//            img_unipay.setVisibility(View.VISIBLE);
//            img_upay.setVisibility(View.GONE);
//            view_mid1.setVisibility(View.GONE);
//            img_unipay.setLayoutParams(new RelativeLayout.LayoutParams(350, 90));
//            ((RelativeLayout.LayoutParams) img_unipay.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
//            ((RelativeLayout.LayoutParams) img_unipay.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_TOP);
//            ((RelativeLayout.LayoutParams) img_unipay.getLayoutParams()).setMargins(0, 10, 0, 0);
//            if (!preferenceManager.isConvenienceFeeSelected()) {
//                ((RelativeLayout.LayoutParams) img_unipay.getLayoutParams()).setMargins(0, 15, 0, 15);
//            }
//            img_unipay.setLayoutParams((RelativeLayout.LayoutParams) img_unipay.getLayoutParams());
//        } else if (!preferenceManager.isUnionPaySelected() && preferenceManager.isUplanSelected()) {
//            img_unipay.setVisibility(View.GONE);
//            img_upay.setVisibility(View.VISIBLE);
//            view_mid1.setVisibility(View.GONE);
//            img_upay.setLayoutParams(new RelativeLayout.LayoutParams(350, 70));
//            img_upay.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            ((RelativeLayout.LayoutParams) img_upay.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
//            ((RelativeLayout.LayoutParams) img_upay.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_TOP);
//            ((RelativeLayout.LayoutParams) img_upay.getLayoutParams()).setMargins(0, 20, 0, 0);
//            if (!preferenceManager.isConvenienceFeeSelected()) {
//                ((RelativeLayout.LayoutParams) img_upay.getLayoutParams()).setMargins(0, 15, 0, 15);
//            }
//            img_upay.setLayoutParams((RelativeLayout.LayoutParams) img_upay.getLayoutParams());
//
//        }
//
//
//        //added on 12th march 2019
//        if (preferenceManager.isaggregated_singleqr() && preferenceManager.isAlipayWechatQrSelected()) {
//            ag_v1.setVisibility(View.VISIBLE);
//            ag_v2.setVisibility(View.VISIBLE);
//            scanqr.setVisibility(View.VISIBLE);
//        } else if (preferenceManager.isaggregated_singleqr() && !preferenceManager.isAlipayWechatQrSelected()) {
//            ag_v1.setVisibility(View.VISIBLE);
//            ag_v2.setVisibility(View.VISIBLE);
//            scanqr.setVisibility(View.GONE);
//        } else if (!preferenceManager.isaggregated_singleqr() && preferenceManager.isAlipayWechatQrSelected()) {
//            ag_v1.setVisibility(View.GONE);
//            ag_v2.setVisibility(View.GONE);
//            tv_other.setVisibility(View.GONE);
//            scanqr.setVisibility(View.VISIBLE);
//            ((LinearLayout.LayoutParams) scanqr.getLayoutParams()).setMargins(0, 15, 0, 0);
//            if (preferenceManager.isConvenienceFeeSelected()) {
//                tv_ali_cv.setVisibility(View.VISIBLE);
//            }
//        }
//
//        if (preferenceManager.isUnionPayQrSelected()) {
//            ((RelativeLayout.LayoutParams) tv_uni_cv.getLayoutParams()).setMargins(0, 0, 0, 0);
//            scanqr_unionpay.setVisibility(View.VISIBLE);
//        } else {
//            ((RelativeLayout.LayoutParams) tv_uni_cv.getLayoutParams()).setMargins(0, 20, 0, 0);
//            scanqr_unionpay.setVisibility(View.GONE);
//        }
//        displayConvieneceFee();
//
//        if (!preferenceManager.isaggregated_singleqr() && !preferenceManager.isAlipayWechatQrSelected()) {
//            //if alipay qr and alipay is not enabled and if convienece fee is enabled then donot show
//            //convienece fee
//
//            if (preferenceManager.is_cnv_alipay_display_and_add() || preferenceManager.is_cnv_alipay_display_only())
//                tv_ali_cv.setVisibility(View.GONE);
//            else
//                tv_ali_cv.setVisibility(View.GONE);
//
//        } else if (preferenceManager.isaggregated_singleqr() || preferenceManager.isAlipayWechatQrSelected()) {
//            //if alipay qr and alipay is not enabled and if convienece fee is enabled then donot show
//            //convienece fee
//
//            if (preferenceManager.is_cnv_alipay_display_and_add() || preferenceManager.is_cnv_alipay_display_only())
//                tv_ali_cv.setVisibility(View.VISIBLE);
//            else
//                tv_ali_cv.setVisibility(View.GONE);
//
//        }
//
//        if (!preferenceManager.isUnionPaySelected() && !preferenceManager.isUplanSelected() && !preferenceManager.isUnionPayQrSelected()) {
//            //if union qr and unionpay is not enabled and if convienece fee is enabled then donot show
//            //convienece fee
//
//            if (preferenceManager.is_cnv_uni_display_and_add() || preferenceManager.is_cnv_uni_display_only())
//                tv_uni_cv.setVisibility(View.GONE);
//            else
//                tv_uni_cv.setVisibility(View.GONE);
//
//        } else if (preferenceManager.isUnionPaySelected() || preferenceManager.isUplanSelected() || preferenceManager.isUnionPayQrSelected()) {
//            //if union qr and union is not enabled and if convienece fee is enabled then donot show
//            //convienece fee
//
//
//            if (preferenceManager.is_cnv_uni_display_and_add() || preferenceManager.is_cnv_uni_display_only())
//                tv_uni_cv.setVisibility(View.VISIBLE);
//            else
//                tv_uni_cv.setVisibility(View.GONE);
//
//        }
//
//
//        view.findViewById(R.id.activity_main).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if (((DashboardActivity) getActivity()).mPopupWindow.isShowing()) {
//                    ((DashboardActivity) getActivity()).mPopupWindow.dismiss();
//                }
//                return false;
//            }
//        });
//
//
//        mHomeWatcher = new HomeWatcher(getActivity());
//        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
//            @Override
//            public void onHomePressed() {
//                AppConstants.isDeviceHomePressed = true;
//                if (DashboardActivity.isExternalApp) {
//                    isTransactionDone = true;
//                    DashboardActivity.isExternalApp = false;
//                    int REQ_PAY_SALE = 100;
//                    ((DashboardActivity) getActivity()).getIntent().putExtra("result", new JSONObject().toString());
//                    ((DashboardActivity) getActivity()).setResult(REQ_PAY_SALE, ((DashboardActivity) getActivity()).getIntent());
//                    ((DashboardActivity) getActivity()).finishAndRemoveTask();
//                }
//            }
//
//            @Override
//            public void onHomeLongPressed() {
//            }
//        });
//        mHomeWatcher.startWatch();
//
//
//        if (preferenceManager.isStaticQR()) {
//            img_alipay_static_qr.setVisibility(View.VISIBLE);
//        } else {
//            img_alipay_static_qr.setVisibility(View.GONE);
//        }
//
//
//        return view;
//    }
//
//
//    public void displayConvieneceFee() {
//        if (preferenceManager.isConvenienceFeeSelected() &&
//                (preferenceManager.is_cnv_alipay_display_and_add() || preferenceManager.is_cnv_alipay_display_only())) {
//            tv_ali_cv.setVisibility(View.VISIBLE);
//        } else {
//            tv_ali_cv.setVisibility(View.GONE);
//        }
//
//        if (preferenceManager.isConvenienceFeeSelected() &&
//                (preferenceManager.is_cnv_uni_display_and_add() || preferenceManager.is_cnv_uni_display_only())) {
//            tv_uni_cv.setVisibility(View.VISIBLE);
//        } else {
//            tv_uni_cv.setVisibility(View.GONE);
//        }
//
//
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (countDownTimerxmpp != null)
//            countDownTimerxmpp.cancel();
//        if (countDownTimer != null)
//            countDownTimer.cancel();
//        getActivity().unregisterReceiver(amountReceiver);
//        mHomeWatcher.stopWatch();
//        if (conn != null) {
//            getActivity().unbindService(conn);
//        }
//        getActivity().stopService(intentService);
//    }
//
//    String roundTwoDecimals(double d) {
//        DecimalFormat twoDForm = new DecimalFormat("#0.00");
//        return twoDForm.format(d);
//    }
//
//
//    public void startCountDownTimer() {
//
//        if (!DashboardActivity.isExternalApp) {
//            countDownTimerxmpp = new CountDownTimer(30000, 1000) {
//
//                public void onTick(long millisUntilFinished) {
//                    tv_start_countdown.setVisibility(View.VISIBLE);
//                    tv_start_countdown.setText("Your session will be closed within " + millisUntilFinished / 1000 + " seconds");
//                    //here you can have your logic to set text to edittext
//                }
//
//                public void onFinish() {
//
//                    if (DashboardActivity.isExternalApp) {
//                        isTransactionDone = true;
//                        TransactionDetailsActivity.isReturnFromTransactionDetails = false;
//                        try {
//
//                            //added for external apps 12/5/2019
//                            int REQ_PAY_SALE = 100;
//                            DashboardActivity.isExternalApp = false;
//                            if (((DashboardActivity) getActivity()) != null) {
//                                if (((DashboardActivity) getActivity()).getIntent() != null) {
//                                    ((DashboardActivity) getActivity()).getIntent().putExtra("result", new JSONObject().toString());
//                                    ((DashboardActivity) getActivity()).setResult(REQ_PAY_SALE, ((DashboardActivity) getActivity()).getIntent());
//                                    ((DashboardActivity) getActivity()).finishAndRemoveTask();
//                                }
//
//                            }
//                            return;
//                            //added for external apps
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//
//                    MyPOSMateApplication.isOpen = false;
//                    tv_start_countdown.setVisibility(View.GONE);
//                    AppConstants.xmppamountforscan = "";
//                    if (isUpayselected) {
//                        isUpayselected = false;
//                    } else {
//                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
//                    }
//
//
//                }
//
//            }.start();
//        }
//    }
//
//    String original_xmpp_trigger_amount = "";
//
//    public class AmountReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String ac = intent.getAction();
//
//            switch (ac) {
//
//
//                case "AmountTrigger":
//                    try {
//                        MyPOSMateApplication.isOpen = true;
//
//                        title.setVisibility(View.GONE);
//
//                        if (preferenceManager.isaggregated_singleqr() && !preferenceManager.isUnipaySelected()) {
//                            //commented on 26 dec 2018 previously we were not showing the ui for generating qr.
//                            //but now we are showing ui to select the qr if alipay wechat is enabled and union is disabled
////                            view.findViewById(R.id.mainui).setVisibility(View.GONE);
//                            //added on 26 dec 2018
//                            view.findViewById(R.id.mainui).setVisibility(View.VISIBLE);
//                            edt_xmpp_amount.setVisibility(View.VISIBLE);
//                            ll_amount1.setVisibility(View.GONE);
//                            ll_reference1.setVisibility(View.GONE);
//                            tv_enable_payment.setVisibility(View.GONE);
//                            ll_amount.setVisibility(View.GONE);
//                            ll_reference.setVisibility(View.GONE);
//
//                        } else if (preferenceManager.isaggregated_singleqr() && preferenceManager.isUnipaySelected()) {
//                            view.findViewById(R.id.mainui).setVisibility(View.VISIBLE);
//                            edt_xmpp_amount.setVisibility(View.VISIBLE);
//                            rel_unionpay.setVisibility(View.VISIBLE);
//                            view.findViewById(R.id.uni_v1).setVisibility(View.VISIBLE);
//                            view.findViewById(R.id.uni_v2).setVisibility(View.VISIBLE);
//                            view.findViewById(R.id.union_up2).setVisibility(View.VISIBLE);
//                            view.findViewById(R.id.union_up1).setVisibility(View.VISIBLE);
//                            tv_start_countdown.setVisibility(View.VISIBLE);
//                            ll_two.setVisibility(View.GONE);
//                            ll_amount1.setVisibility(View.GONE);
//                            ll_reference1.setVisibility(View.GONE);
//                            tv_enable_payment.setVisibility(View.GONE);
//                            ll_amount.setVisibility(View.GONE);
//                            ll_reference.setVisibility(View.GONE);
//                            ll_one.setVisibility(View.VISIBLE);
//                            ag_v1.setVisibility(View.VISIBLE);
//                            ag_v2.setVisibility(View.VISIBLE);
//                            scanqr.setVisibility(View.VISIBLE);
//                            ag_v3.setVisibility(View.VISIBLE);
//                            tv_other.setVisibility(View.GONE);
//
//                        } else if (preferenceManager.isUnipaySelected() && !preferenceManager.isaggregated_singleqr()) {
//                            view.findViewById(R.id.mainui).setVisibility(View.GONE);
//                            //dec 5 2018
//                            view.findViewById(R.id.mainui).setVisibility(View.VISIBLE);
//                            edt_xmpp_amount.setVisibility(View.VISIBLE);
//                            ll_amount.setVisibility(View.GONE);
//                            ll_reference.setVisibility(View.GONE);
//                        }
//
//                        if (preferenceManager.isUnionPaySelected() && preferenceManager.isUplanSelected()) {
//                            img_unipay.setVisibility(View.VISIBLE);
//                            img_upay.setVisibility(View.VISIBLE);
//                            view_mid1.setVisibility(View.VISIBLE);
//                        } else if (preferenceManager.isUnionPaySelected() && !preferenceManager.isUplanSelected()) {
//                            img_unipay.setVisibility(View.VISIBLE);
//                            img_upay.setVisibility(View.GONE);
//                            view_mid1.setVisibility(View.GONE);
//                            img_unipay.setLayoutParams(new RelativeLayout.LayoutParams(200, 100));
//                            ((RelativeLayout.LayoutParams) img_unipay.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
//                            ((RelativeLayout.LayoutParams) img_unipay.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                            ((RelativeLayout.LayoutParams) img_unipay.getLayoutParams()).setMargins(0, 10, 0, 0);
//                            if (!preferenceManager.isConvenienceFeeSelected()) {
//                                ((RelativeLayout.LayoutParams) img_unipay.getLayoutParams()).setMargins(0, 15, 0, 15);
//                            }
//                            img_unipay.setLayoutParams((RelativeLayout.LayoutParams) img_unipay.getLayoutParams());
//                        } else if (!preferenceManager.isUnionPaySelected() && preferenceManager.isUplanSelected()) {
//                            img_unipay.setVisibility(View.GONE);
//                            img_upay.setVisibility(View.VISIBLE);
//                            view_mid1.setVisibility(View.GONE);
//                            img_upay.setLayoutParams(new RelativeLayout.LayoutParams(200, 70));
//                            img_upay.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                            ((RelativeLayout.LayoutParams) img_upay.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
//                            ((RelativeLayout.LayoutParams) img_upay.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                            ((RelativeLayout.LayoutParams) img_upay.getLayoutParams()).setMargins(0, 20, 0, 0);
//                            if (!preferenceManager.isConvenienceFeeSelected()) {
//                                ((RelativeLayout.LayoutParams) img_upay.getLayoutParams()).setMargins(0, 15, 0, 15);
//                            }
//                            img_upay.setLayoutParams((RelativeLayout.LayoutParams) img_upay.getLayoutParams());
//                        }
//
//                        //added on 12th march 2019
//                        if (preferenceManager.isaggregated_singleqr() && preferenceManager.isAlipayWechatQrSelected()) {
//                            ag_v1.setVisibility(View.VISIBLE);
//                            ag_v2.setVisibility(View.VISIBLE);
//                            scanqr.setVisibility(View.VISIBLE);
//                        } else if (preferenceManager.isaggregated_singleqr() && !preferenceManager.isAlipayWechatQrSelected()) {
//                            ag_v1.setVisibility(View.VISIBLE);
//                            ag_v2.setVisibility(View.VISIBLE);
//                            scanqr.setVisibility(View.GONE);
//                        } else if (!preferenceManager.isaggregated_singleqr() && preferenceManager.isAlipayWechatQrSelected()) {
//                            ag_v1.setVisibility(View.GONE);
//                            ag_v2.setVisibility(View.GONE);
//                            tv_other.setVisibility(View.GONE);
//                            ((LinearLayout.LayoutParams) scanqr.getLayoutParams()).setMargins(0, 15, 0, 0);
//                            scanqr.setVisibility(View.VISIBLE);
//                            if (preferenceManager.isConvenienceFeeSelected()) {
//                                tv_ali_cv.setVisibility(View.VISIBLE);
//                            }
//                        }
//
//                        if (preferenceManager.isUnionPayQrSelected()) {
//                            ((RelativeLayout.LayoutParams) tv_uni_cv.getLayoutParams()).setMargins(0, 0, 0, 0);
//                            scanqr_unionpay.setVisibility(View.VISIBLE);
//                        } else {
//                            ((RelativeLayout.LayoutParams) tv_uni_cv.getLayoutParams()).setMargins(0, 20, 0, 0);
//                            scanqr_unionpay.setVisibility(View.GONE);
//                        }
//                        displayConvieneceFee();
//
//                        if (!preferenceManager.isaggregated_singleqr() && !preferenceManager.isAlipayWechatQrSelected()) {
//                            //if alipay qr and alipay is not enabled and if convienece fee is enabled then donot show
//                            //convienece fee
//
//                            if (preferenceManager.is_cnv_alipay_display_and_add() || preferenceManager.is_cnv_alipay_display_only())
//                                tv_ali_cv.setVisibility(View.GONE);
//                            else
//                                tv_ali_cv.setVisibility(View.GONE);
//
//                        } else if (preferenceManager.isaggregated_singleqr() || preferenceManager.isAlipayWechatQrSelected()) {
//                            //if alipay qr and alipay is not enabled and if convienece fee is enabled then donot show
//                            //convienece fee
//
//                            if (preferenceManager.is_cnv_alipay_display_and_add() || preferenceManager.is_cnv_alipay_display_only())
//                                tv_ali_cv.setVisibility(View.VISIBLE);
//                            else
//                                tv_ali_cv.setVisibility(View.GONE);
//
//                        }
//
//                        if (!preferenceManager.isUnionPaySelected() && !preferenceManager.isUplanSelected() && !preferenceManager.isUnionPayQrSelected()) {
//                            //if union qr and unionpay is not enabled and if convienece fee is enabled then donot show
//                            //convienece fee
//
//                            if (preferenceManager.is_cnv_uni_display_and_add() || preferenceManager.is_cnv_uni_display_only())
//                                tv_uni_cv.setVisibility(View.GONE);
//                            else
//                                tv_uni_cv.setVisibility(View.GONE);
//
//                        } else if (preferenceManager.isUnionPaySelected() || preferenceManager.isUplanSelected() || preferenceManager.isUnionPayQrSelected()) {
//                            //if union qr and union is not enabled and if convienece fee is enabled then donot show
//                            //convienece fee
//
//
//                            if (preferenceManager.is_cnv_uni_display_and_add() || preferenceManager.is_cnv_uni_display_only())
//                                tv_uni_cv.setVisibility(View.VISIBLE);
//                            else
//                                tv_uni_cv.setVisibility(View.GONE);
//
//                        }
//
//
//                        JSONObject jsonObject = new JSONObject(intent.getStringExtra("data"));
//                        if (jsonObject.has("reference_id")) {
//                            reference_id = jsonObject.optString("reference_id");
//                            preferenceManager.setreference_id(reference_id);
//                            //5 dec 2018
//                            preferenceManager.settriggerReferenceId(reference_id);
//                            //-----------
//                        } else {
//                            reference_id = "";
//                            preferenceManager.setreference_id("");
//                            preferenceManager.settriggerReferenceId("");
//                        }
//                        if (preferenceManager.isaggregated_singleqr()) {
//                            edt_xmpp_amount.setText(preferenceManager.getcurrency() + " $" + roundTwoDecimals(Float.valueOf(jsonObject.optString("amount"))));
//                            edt_amount.setText(roundTwoDecimals(Float.valueOf(jsonObject.optString("amount"))) + "");
//                            edt_reference.setText(jsonObject.optString("reference"));
//                        } else {
//                            edt_xmpp_amount.setText(preferenceManager.getcurrency() + " $" + roundTwoDecimals(Float.valueOf(jsonObject.optString("amount"))));
//                            edt_amount.setText(roundTwoDecimals(Float.valueOf(jsonObject.optString("amount"))) + "");
//                            edt_reference.setText(jsonObject.optString("reference"));
//                        }
//                        xmppAmount = jsonObject.optString("amount");
//
//                        preferenceManager.setupay_amount(xmppAmount);
//                        original_xmpp_trigger_amount = jsonObject.optString("amount");
//                        //**********************added by ruhi 19/12/2018***********************
//                        if (!edt_amount.getText().toString().equals(""))
//
//                            if (preferenceManager.isConvenienceFeeSelected()) {
////                                tv_uni_cv.setVisibility(View.VISIBLE);
////                                tv_ali_cv.setVisibility(View.VISIBLE);
//                                if (!preferenceManager.getcnv_alipay().equals("") ||
//                                        !preferenceManager.getcnv_alipay().equals("0.0") ||
//                                        !preferenceManager.getcnv_alipay().equals("0.00")) {
//                                    //This was the old convinence fee calculation which was used before 13 march 2019 change of new formula
//                                    Log.v("AMount1", Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "");
//                                    convenience_amount_alipay = (Double.parseDouble(edt_amount.getText().toString().replace(",", "")) / 100.0f) *
//                                            (Double.parseDouble(preferenceManager.getcnv_alipay()));
//                                    convenience_amount_alipay = convenience_amount_alipay + Double.parseDouble(edt_amount.getText().toString().replace(",", ""));
//
//
//                                    //added on 13 march 2019
//                                    //This is new convienece fee calculation
////                            C/(1-r) where C is the total amount and r is convinence fee in percent.
//                                    convenience_amount_alipay = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
//                                            (1 - (Double.parseDouble(preferenceManager.getcnv_alipay()) / 100));
//
//                                    tv_ali_cv.setText("With Convenience Fee: " + roundTwoDecimals(convenience_amount_alipay));
//
//                                    if (preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_alipay_display_and_add()) {
//                                        xmppAmount = convenience_amount_alipay + "";
//                                        preferenceManager.setupay_amount(xmppAmount);
//                                    } else {
//                                        xmppAmount = edt_amount.getText().toString();
//                                        preferenceManager.setupay_amount(edt_amount.getText().toString());
//                                    }
//
//                                }
//
//                                if (!preferenceManager.getcnv_uni().equals("") ||
//                                        !preferenceManager.getcnv_uni().equals("0.0") ||
//                                        !preferenceManager.getcnv_uni().equals("0.00")) {
//                                    //This was the old convinence fee calculation which was used before 13 march 2019 change of new formula
//                                    Log.v("AMount2", Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "");
//                                    convenience_amount_unionpay = (Double.parseDouble(edt_amount.getText().toString().replace(",", "")) / 100.0f) *
//                                            (Double.parseDouble(preferenceManager.getcnv_uni()));
//                                    convenience_amount_unionpay = convenience_amount_unionpay + Double.parseDouble(edt_amount.getText().toString().replace(",", ""));
//
//
//                                    //added on 13 march 2019
//                                    //This is new convienece fee calculation
////                            C/(1-r) where C is the total amount and r is convinence fee in percent.
//                                    convenience_amount_unionpay = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
//                                            (1 - (Double.parseDouble(preferenceManager.getcnv_uni()) / 100));
//
//                                    tv_uni_cv.setText("With Convenience Fee: " + roundTwoDecimals(convenience_amount_unionpay));
//
//
//                                    if (preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_uni_display_and_add()) {
//                                        xmppAmount = convenience_amount_unionpay + "";
//                                        preferenceManager.setupay_amount(xmppAmount);
//                                    } else {
//                                        xmppAmount = edt_amount.getText().toString();
//                                        preferenceManager.setupay_amount(edt_amount.getText().toString());
//                                    }
//
//
//                                }
//
//                            } else {
//                                tv_uni_cv.setVisibility(View.GONE);
//                                tv_ali_cv.setVisibility(View.GONE);
//                            }
//
//
//                        //_____________________________________________________________________
//
//
//                        if (!jsonObject.optString("reference").equals("null") &&
//                                !jsonObject.optString("reference").equals("")
//                        ) {
//                            preferenceManager.setReference(jsonObject.optString("reference"));
//                        } else {
//                            preferenceManager.setReference("");
//                        }
//                        if (preferenceManager.isaggregated_singleqr()) {
//                            tv_start_countdown.setVisibility(View.VISIBLE);
//                            if (AppConstants.xmppamountforscan.equals(""))//added on 12th march 2019
//                                startCountDownTimer();
//
//                        } else {
//                            if (preferenceManager.isVisaSlelected() || preferenceManager.isUnipaySelected() ||
//                                    preferenceManager.isAlipaySelected() || preferenceManager.isWechatSelected()) {
//                                tv_start_countdown.setVisibility(View.VISIBLE);
//                                if (AppConstants.xmppamountforscan.equals(""))//added on 12th march 2019
//                                    startCountDownTimer();
//
//                            } else {
//                                tv_start_countdown.setVisibility(View.GONE);
//                            }
//
//                        }
//
//                        if (preferenceManager.isaggregated_singleqr() && !preferenceManager.isUnipaySelected()) {
//                            payment_mode = "ALIPAY";
//                            qrMode = "True";
//                            auth_code = "";
//
//                        } else if (preferenceManager.isaggregated_singleqr() && preferenceManager.isUnipaySelected()) {
//
//
//                        } else if (preferenceManager.isUnipaySelected() && !preferenceManager.isaggregated_singleqr()) {
//                        }
//                        AppConstants.xmppamountforscan = jsonObject.optString("amount");//added on 12th march 2019
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                case "ScannedCode":
//                    if (intent.hasExtra("identityCode")) {
//                        auth_code = intent.getStringExtra("identityCode");
//                        payment_mode = "nochannel";
//                        qrMode = "False";
//                        edt_amount.setText(preferenceManager.getupay_amount());
//                        if (MyPOSMateApplication.isOpen) {
//                            if (preferenceManager.isConvenienceFeeSelected()) {
//                                convenience_amount_unionpay = Double.parseDouble(preferenceManager.getupay_amount());
//                            } else {
//                                edt_amount.setText(preferenceManager.getupay_amount());
//                            }
//                            beginBussinessCoupon(preferenceManager.getupay_reference_id(), auth_code);
//                            //callQRCode();
//
//                        } else {
//                            if (edt_amount.getText().toString().equals("") || edt_amount.getText().toString().equals("0.00")) {
//                                Toast.makeText(getActivity(), "Please enter the amount.", Toast.LENGTH_LONG).show();
//                            } else {
//                                //    callQRCode();
//                                beginBussinessCoupon(preferenceManager.getupay_reference_id(), auth_code);
//                            }
//
//                        }
//
//                    }
//                    break;
//
//                case "ScannedCodeUnionPayQr":
//                    if (intent.hasExtra("identityCode")) {
//                        auth_code = intent.getStringExtra("identityCode");
//                        payment_mode = "nochannel";
//                        qrMode = "False";
//                        edt_amount.setText(preferenceManager.getupay_amount());
//                        if (MyPOSMateApplication.isOpen) {
//                            if (preferenceManager.isConvenienceFeeSelected()) {
//                                convenience_amount_unionpay = Double.parseDouble(preferenceManager.getupay_amount());
//                            } else {
//                                edt_amount.setText(preferenceManager.getupay_amount());
//                            }
//                            beginBussinessPreAuthorization(preferenceManager.getupay_reference_id(), auth_code);
//                            //callQRCode();
//
//                        } else {
//                            if (edt_amount.getText().toString().equals("") || edt_amount.getText().toString().equals("0.00")) {
//                                Toast.makeText(getActivity(), "Please enter the amount.", Toast.LENGTH_LONG).show();
//                            } else {
//                                //    callQRCode();
//                                beginBussinessPreAuthorization(preferenceManager.getupay_reference_id(), auth_code);
//                            }
//
//                        }
//
//                    }
//                    break;
//
//
//                case "ScannedCode1":
//                    if (intent.hasExtra("identityCode")) {
//                        auth_code = intent.getStringExtra("identityCode");
//                        payment_mode = "nochannel";
//                        qrMode = "False";
//
//                        if (MyPOSMateApplication.isOpen) {
//
//                            callPayNow();
//
//                        } else {
//                            if (edt_amount.getText().toString().equals("") || edt_amount.getText().toString().equals("0.00")) {
//                                if (!AppConstants.xmppamountforscan.equals("")) {
//                                    MyPOSMateApplication.isOpen = true;
//                                    final Handler handler = new Handler();
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            callPayNow();
//                                        }
//                                    }, 500);
//
//
//                                } else
//                                    Toast.makeText(getActivity(), "Please enter the amount.", Toast.LENGTH_LONG).show();
//                            } else {
//                                callPayNow();
//                            }
//
//                        }
//
//                    }
//                    break;
//
//
//                case "PaymentExpressSuccess":
//                case "PaymentExpressFailure":
//                    MyPOSMateApplication.isOpen = false;
//                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.PAYMENTPROCESSING, intent.getStringExtra("data"));
//                    break;
//            }
//        }
//    }
//
//
//    public void initUI(View view) {
//        qrScan = new IntentIntegrator(getActivity());
//        vasCallsArkeBusiness = new VASCallsArkeBusiness(getActivity());
//
//        view_mid1 = (View) view.findViewById(R.id.view_mid1);
//        view_mid2 = (View) view.findViewById(R.id.view_mid2);
//        btn_back = (Button) view.findViewById(R.id.btn_back);
//        tv_status_scan = (TextView) view.findViewById(R.id.tv_status_scan);
//        tv_status_scan_button = (Button) view.findViewById(R.id.tv_status_scan_button);
//        btn_front = (Button) view.findViewById(R.id.btn_front);
//        rel_membership = (RelativeLayout) view.findViewById(R.id.rel_membership);
//        btn_save = (Button) view.findViewById(R.id.btn_save);
//        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
//        btn_save1 = (Button) view.findViewById(R.id.btn_save1);
//        btn_cancel1 = (Button) view.findViewById(R.id.btn_cancel1);
//        ll_first = (RelativeLayout) view.findViewById(R.id.ll_first);
//        ll_second = (LinearLayout) view.findViewById(R.id.ll_second);
//        edt_amount = (CurrencyEditText) view.findViewById(R.id.edt_amount);
//        edt_amount.setLocale(new Locale("en", "US"));
//        edt_reference = (EditText) view.findViewById(R.id.edt_reference);
//        edt_amount1 = (CurrencyEditText) view.findViewById(R.id.edt_amount1);
//        edt_amount1.setLocale(new Locale("en", "US"));
//        edt_reference1 = (EditText) view.findViewById(R.id.edt_reference1);
//        img_alipay = (ImageView) view.findViewById(R.id.img_alipay);
//        img_wechat = (ImageView) view.findViewById(R.id.img_wechat);
//        img_scan = (ImageView) view.findViewById(R.id.img_scan);
//        img_paymentexpress = (ImageView) view.findViewById(R.id.img_paymentexpress);
//        img_unipay = (ImageView) view.findViewById(R.id.img_unipay);
//        img_upay = (ImageView) view.findViewById(R.id.img_upay);
//        tv_scan_code = (TextView) view.findViewById(R.id.tv_scan_code);
//        tv_enable_payment = (TextView) view.findViewById(R.id.tv_enable_payment);
//        tv_start_countdown = (TextView) view.findViewById(R.id.tv_start_countdown);
//        tv_start_countdown.setVisibility(View.GONE);
//        tv_selection_type = (TextView) view.findViewById(R.id.tv_selection_type);
//        edt_xmpp_amount = (TextView) view.findViewById(R.id.edt_xmpp_amount);
//        edt_xmpp_amount.setVisibility(View.GONE);
//        edt_xmpp_amount1 = (TextView) view.findViewById(R.id.edt_xmpp_amount1);
//        edt_xmpp_amount1.setVisibility(View.GONE);
//        ll_amount = (LinearLayout) view.findViewById(R.id.ll_amount);
//        ll_reference = (LinearLayout) view.findViewById(R.id.ll_reference);
//        ll_amount1 = (LinearLayout) view.findViewById(R.id.ll_amount1);
//        ll_reference1 = (LinearLayout) view.findViewById(R.id.ll_reference1);
//        title = (TextView) view.findViewById(R.id.title);
//        tv_noitem = (TextView) view.findViewById(R.id.tv_noitem);
//        rel_unionpay = (RelativeLayout) view.findViewById(R.id.rel_unionpay);
//        rel_wechat = (RelativeLayout) view.findViewById(R.id.rel_wechat);
//        rel_alipay = (RelativeLayout) view.findViewById(R.id.rel_alipay);
//        rel_scan = (RelativeLayout) view.findViewById(R.id.rel_scan);
//        rel_paymentexpress = (RelativeLayout) view.findViewById(R.id.rel_paymentexpress);
//        tv_alipay = (TextView) view.findViewById(R.id.tv_alipay);
//        tv_wechat = (TextView) view.findViewById(R.id.tv_wechat);
//        tv_unipay = (TextView) view.findViewById(R.id.tv_unipay);
//        scanqr = (TextView) view.findViewById(R.id.scanqr);
//        scanqr_unionpay = (TextView) view.findViewById(R.id.scanqr_unionpay);
//
//        tv_uni_cv = (TextView) view.findViewById(R.id.tv_uni_cv);
//        tv_ali_cv = (TextView) view.findViewById(R.id.tv_ali_cv);
//
//
//        tv_vice = (TextView) view.findViewById(R.id.tv_vice);
//        edt_amount.setSelection(edt_amount.getText().length());
//        edt_amount1.setSelection(edt_amount1.getText().length());
//        tv_qrcode = (TextView) view.findViewById(R.id.tv_qrcode);
//        ll_one = (LinearLayout) view.findViewById(R.id.ll_one);
//        ll_two = (LinearLayout) view.findViewById(R.id.ll_two);
//        ag_v1 = (View) view.findViewById(R.id.ag_v1);
//        ag_v3 = (View) view.findViewById(R.id.ag_v3);
//        ag_v2 = (LinearLayout) view.findViewById(R.id.ag_v2);
//        tv_other = (TextView) view.findViewById(R.id.tv_other);
//        img_alipay_static_qr = (ImageView) view.findViewById(R.id.img_alipay_static_qr);
//
//        if (preferenceManager.isMembershipManual()) {
//            rel_membership.setVisibility(View.VISIBLE);
//            tv_status_scan.setVisibility(View.INVISIBLE);
//            tv_status_scan_button.setVisibility(View.VISIBLE);
//            if (preferenceManager.isFront()) {
//                btn_front.setVisibility(View.VISIBLE);
//                btn_back.setVisibility(View.GONE);
//            }
//            if (preferenceManager.isBack()) {
//                btn_front.setVisibility(View.GONE);
//                btn_back.setVisibility(View.VISIBLE);
//            }
//            if (preferenceManager.isBack() && preferenceManager.isFront()) {
//                btn_front.setVisibility(View.VISIBLE);
//                btn_back.setVisibility(View.VISIBLE);
//            }
//            if (!preferenceManager.isBack() && !preferenceManager.isFront()) {
//                rel_membership.setVisibility(View.GONE);
//            }
//
//        } else {
//            rel_membership.setVisibility(View.GONE);
//            tv_status_scan.setVisibility(View.GONE);
//            tv_status_scan_button.setVisibility(View.GONE);
//        }
//
//
//        if (!edt_amount.getText().toString().equals("") && !preferenceManager.getcnv_alipay().equals("") && !preferenceManager.getcnv_uni().equals("")) {
//            if (preferenceManager.isConvenienceFeeSelected()) {
//                tv_uni_cv.setVisibility(View.VISIBLE);
//                tv_ali_cv.setVisibility(View.VISIBLE);
//                if (!preferenceManager.getcnv_alipay().equals("") ||
//                        !preferenceManager.getcnv_alipay().equals("0.0") ||
//                        !preferenceManager.getcnv_alipay().equals("0.00")) {
//                    double amount = Double.parseDouble(edt_amount.getText().toString()) * Double.parseDouble(preferenceManager.getcnv_alipay()) / 100;
//                    tv_ali_cv.setText("With Convenience Fee: " + roundTwoDecimals(amount));
//                }
//
//                if (!preferenceManager.getcnv_uni().equals("") ||
//                        !preferenceManager.getcnv_uni().equals("0.0") ||
//                        !preferenceManager.getcnv_uni().equals("0.00")) {
//                    double amount = Double.parseDouble(edt_amount.getText().toString()) * Double.parseDouble(preferenceManager.getcnv_uni()) / 100;
//                    tv_uni_cv.setText("With Convenience Fee: " + roundTwoDecimals(amount));
//                }
//
//            } else {
//                tv_uni_cv.setVisibility(View.GONE);
//                tv_ali_cv.setVisibility(View.GONE);
//            }
//        } else {
//            if (preferenceManager.isConvenienceFeeSelected()) {
//                tv_uni_cv.setVisibility(View.VISIBLE);
//                tv_ali_cv.setVisibility(View.VISIBLE);
//                tv_ali_cv.setText("With Convenience Fee: " + 0.00);
//                tv_uni_cv.setText("With Convenience Fee: " + 0.00);
//            } else {
//                tv_uni_cv.setVisibility(View.GONE);
//                tv_ali_cv.setVisibility(View.GONE);
//            }
//        }
//
//        if (preferenceManager.isaggregated_singleqr() && !preferenceManager.isUnipaySelected()) {
//
//            ll_two.setVisibility(View.GONE);
//            ll_amount1.setVisibility(View.GONE);
//            ll_reference1.setVisibility(View.GONE);
//            tv_enable_payment.setVisibility(View.GONE);
//            ll_amount.setVisibility(View.VISIBLE);
//            ll_reference.setVisibility(View.VISIBLE);
//            ll_one.setVisibility(View.VISIBLE);
//            ag_v1.setVisibility(View.VISIBLE);
//            ag_v2.setVisibility(View.VISIBLE);
//            scanqr.setVisibility(View.VISIBLE);
//            ag_v3.setVisibility(View.VISIBLE);
//            tv_other.setVisibility(View.GONE);
//            rel_unionpay.setVisibility(View.GONE);
//            view.findViewById(R.id.uni_v1).setVisibility(View.GONE);
//            view.findViewById(R.id.uni_v2).setVisibility(View.GONE);
//            view.findViewById(R.id.union_up2).setVisibility(View.GONE);
//            view.findViewById(R.id.union_up1).setVisibility(View.GONE);
//            if (preferenceManager.isConvenienceFeeSelected()) {
//                tv_ali_cv.setVisibility(View.VISIBLE);
//                tv_uni_cv.setVisibility(View.GONE);
//
//            } else {
//                tv_uni_cv.setVisibility(View.GONE);
//                tv_ali_cv.setVisibility(View.GONE);
//            }
//
//
//        } else if (preferenceManager.isaggregated_singleqr() && preferenceManager.isUnipaySelected()) {
//            rel_unionpay.setVisibility(View.VISIBLE);
//            view.findViewById(R.id.uni_v1).setVisibility(View.VISIBLE);
//            view.findViewById(R.id.uni_v2).setVisibility(View.VISIBLE);
//            view.findViewById(R.id.union_up2).setVisibility(View.VISIBLE);
//            view.findViewById(R.id.union_up1).setVisibility(View.VISIBLE);
//            ll_two.setVisibility(View.GONE);
//            ll_amount1.setVisibility(View.GONE);
//            ll_reference1.setVisibility(View.GONE);
//            tv_enable_payment.setVisibility(View.GONE);
//            ll_amount.setVisibility(View.VISIBLE);
//            ll_reference.setVisibility(View.VISIBLE);
//            ll_one.setVisibility(View.VISIBLE);
//            ag_v1.setVisibility(View.VISIBLE);
//            ag_v2.setVisibility(View.VISIBLE);
//            scanqr.setVisibility(View.VISIBLE);
//            ag_v3.setVisibility(View.VISIBLE);
//            tv_other.setVisibility(View.GONE);
//
//            if (preferenceManager.isConvenienceFeeSelected()) {
//                tv_uni_cv.setVisibility(View.VISIBLE);
//                tv_ali_cv.setVisibility(View.VISIBLE);
//
//            } else {
//                tv_uni_cv.setVisibility(View.GONE);
//                tv_ali_cv.setVisibility(View.GONE);
//            }
//
//        } else if (preferenceManager.isUnipaySelected() && !preferenceManager.isaggregated_singleqr()) {
//            ag_v1.setVisibility(View.GONE);
//            ag_v2.setVisibility(View.GONE);
//            scanqr.setVisibility(View.GONE);
//            ag_v3.setVisibility(View.GONE);
//            tv_other.setVisibility(View.GONE);
//            tv_enable_payment.setVisibility(View.GONE);
//
//            if (preferenceManager.isConvenienceFeeSelected()) {
//                tv_uni_cv.setVisibility(View.VISIBLE);
//                tv_ali_cv.setVisibility(View.GONE);
//
//            } else {
//                tv_uni_cv.setVisibility(View.GONE);
//                tv_ali_cv.setVisibility(View.GONE);
//            }
//
//
//        }
//
//
//    }
//
//    double convenience_amount_alipay = 0.0, convenience_amount_unionpay = 0.0;
//
//    public void initListener() {
//        btn_save.setOnClickListener(this);
//        btn_back.setOnClickListener(this);
//        btn_front.setOnClickListener(this);
//        btn_save1.setOnClickListener(this);
//        btn_cancel1.setOnClickListener(this);
//        tv_qrcode.setOnClickListener(this);
//        btn_cancel.setOnClickListener(this);
//        scanqr.setOnClickListener(this);
//        scanqr_unionpay.setOnClickListener(this);
//        img_alipay.setOnClickListener(this);
//        img_scan.setOnClickListener(this);
//        img_wechat.setOnClickListener(this);
//        img_unipay.setOnClickListener(this);
//        img_upay.setOnClickListener(this);
//        rel_alipay.setOnClickListener(this);
//        rel_scan.setOnClickListener(this);
//        rel_wechat.setOnClickListener(this);
//        rel_unionpay.setOnClickListener(this);
//        rel_paymentexpress.setOnClickListener(null);
//        img_paymentexpress.setOnClickListener(null);
//        tv_vice.setOnClickListener(this);
//        tv_unipay.setOnClickListener(this);
//        tv_wechat.setOnClickListener(this);
//        tv_alipay.setOnClickListener(this);
//        tv_scan_code.setOnClickListener(this);
//        img_alipay_static_qr.setOnClickListener(this);
//
//        if (!preferenceManager.isaggregated_singleqr() && !preferenceManager.isAlipaySelected() &&
//                !preferenceManager.isWechatSelected() &&
//                !preferenceManager.isUnipaySelected() &&
//                !preferenceManager.isVisaSlelected()) {
//            tv_noitem.setVisibility(View.VISIBLE);
//            btn_save.setOnClickListener(null);
//            btn_cancel.setOnClickListener(null);
//            tv_scan_code.setOnClickListener(this);
//
//            ll_amount1.setVisibility(View.GONE);
//            ll_reference1.setVisibility(View.GONE);
//            ll_reference.setVisibility(View.GONE);
//            ll_amount.setVisibility(View.GONE);
//
//
//            if (preferenceManager.isaggregated_singleqr()) {
//                ll_two.setVisibility(View.VISIBLE);
//                ll_one.setVisibility(View.GONE);
//            }
//
//        } else {
//            ll_one.setVisibility(View.VISIBLE);
//            ll_reference.setVisibility(View.VISIBLE);
//            ll_amount.setVisibility(View.VISIBLE);
//            ll_two.setVisibility(View.GONE);
//            tv_scan_code.setOnClickListener(this);
//            btn_save.setOnClickListener(this);
//            btn_cancel.setOnClickListener(this);
//            tv_noitem.setVisibility(View.GONE);
//            if (preferenceManager.isUnipaySelected()) {
//                tv_unipay.setVisibility(View.GONE);
//                rel_unionpay.setOnClickListener(this);
//                img_unipay.setOnClickListener(this);
//                img_upay.setOnClickListener(this);
//
//            } else {
//                tv_unipay.setVisibility(View.VISIBLE);
//                rel_unionpay.setOnClickListener(null);
//                img_unipay.setOnClickListener(null);
//                img_upay.setOnClickListener(null);
//            }
//
//
//            if (preferenceManager.isVisaSlelected()) {
//                tv_vice.setVisibility(View.GONE);
//                rel_scan.setOnClickListener(this);
//                img_scan.setOnClickListener(this);
//
//            } else {
//                tv_vice.setVisibility(View.VISIBLE);
//                rel_scan.setOnClickListener(null);
//                img_scan.setOnClickListener(null);
//            }
//
//            if (!preferenceManager.isUnipaySelected() && !preferenceManager.isaggregated_singleqr()) {
//                ll_one.setVisibility(View.VISIBLE);
//                ll_reference.setVisibility(View.GONE);
//                ll_amount.setVisibility(View.GONE);
//                ll_two.setVisibility(View.GONE);
//
//                tv_scan_code.setOnClickListener(this);
//                btn_save.setOnClickListener(this);
//                btn_cancel.setOnClickListener(this);
//                tv_noitem.setVisibility(View.VISIBLE);
//            }
//
//
//        }
//
//
//        calledt_amount();
//        calledt_amount1();
//
//
//        edt_reference.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (selected_screen != 0) {
//                    selected_screen = 0;
//                    rel_alipay.setBackgroundResource(0);
//                    rel_unionpay.setBackgroundResource(0);
//                    rel_wechat.setBackgroundResource(0);
//                    rel_scan.setBackgroundResource(0);
//                }
//
//            }
//        });
//        edt_reference1.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (selected_screen != 0) {
//                    selected_screen = 0;
//
//                }
//
//            }
//        });
//
//
//    }
//
//
//    public void callAuthToken() {
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("grant_type", "password");
//        hashMap.put("username", preferenceManager.getterminalId());
//        hashMap.put("password", preferenceManager.getuniqueId());
//        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.V2_AUTH);
//
//    }
//
//
//    public void callAuthToken1() {
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("grant_type", "password");
//        hashMap.put("username", preferenceManager.getterminalId());
//        hashMap.put("password", preferenceManager.getuniqueId());
//        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken1").execute(AppConstants.V2_AUTH);
//
//    }
//
//
//    public void calledt_amount() {
//        edt_amount.setInputType(InputType.TYPE_CLASS_NUMBER);
//        edt_amount.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (edt_amount.getText().toString().equals("0.00")) {
//                    edt_amount.setText(null);
//
//
//                }
//                return false;
//            }
//        });
//        edt_amount.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.v("", "");
//                try {
//                    if (s.equals("")) {
//                        return;
//                    } else {
//
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.v("", "");
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                Log.v("", "");
//                if (selected_screen != 0) {
//                    selected_screen = 0;
//                    rel_alipay.setBackgroundResource(0);
//                    rel_unionpay.setBackgroundResource(0);
//                    rel_wechat.setBackgroundResource(0);
//                    rel_scan.setBackgroundResource(0);
//                }
//                if (!edt_amount.getText().toString().equals("") &&
//                        !preferenceManager.getcnv_alipay().equals("") &&
//                        !preferenceManager.getcnv_uni().equals("")) {
//                    if (preferenceManager.isConvenienceFeeSelected()) {
//
//                        if (preferenceManager.is_cnv_alipay_display_and_add() || preferenceManager.is_cnv_alipay_display_only()) {
//
//                            if (!preferenceManager.getcnv_alipay().equals("") ||
//                                    !preferenceManager.getcnv_alipay().equals("0.0") ||
//                                    !preferenceManager.getcnv_alipay().equals("0.00")) {
//
//                                //This was the old convinence fee calculation which was used before 13 march 2019 change of new formula
//                                Log.v("AMount1", Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "");
//                                convenience_amount_alipay = (Double.parseDouble(edt_amount.getText().toString().replace(",", "")) / 100.0f) *
//                                        (Double.parseDouble(preferenceManager.getcnv_alipay()));
//                                convenience_amount_alipay = convenience_amount_alipay + Double.parseDouble(edt_amount.getText().toString().replace(",", ""));
//
//
//                                //added on 13 march 2019
//                                //This is new convienece fee calculation
////                            C/(1-r) where C is the total amount and r is convinence fee in percent.
//                                convenience_amount_alipay = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
//                                        (1 - (Double.parseDouble(preferenceManager.getcnv_alipay()) / 100));
//
//                                tv_ali_cv.setText("With Convenience Fee: " + roundTwoDecimals(convenience_amount_alipay));
//                            }
//
//                        }
//
//
//                        if (preferenceManager.is_cnv_uni_display_and_add() || preferenceManager.is_cnv_uni_display_only()) {
//
//                            if (!preferenceManager.getcnv_uni().equals("") ||
//                                    !preferenceManager.getcnv_uni().equals("0.0") ||
//                                    !preferenceManager.getcnv_uni().equals("0.00")) {
//                                //This was the old convinence fee calculation which was used before 13 march 2019 change of new formula
//                                Log.v("AMount2", Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "");
//                                convenience_amount_unionpay = (Double.parseDouble(edt_amount.getText().toString().replace(",", "")) / 100.0f) *
//                                        (Double.parseDouble(preferenceManager.getcnv_uni()));
//                                convenience_amount_unionpay = convenience_amount_unionpay + Double.parseDouble(edt_amount.getText().toString().replace(",", ""));
//
//                                //added on 13 march 2019
//                                //This is new convienece fee calculation
////                            C/(1-r) where C is the total amount and r is convinence fee in percent.
//                                convenience_amount_unionpay = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
//                                        (1 - (Double.parseDouble(preferenceManager.getcnv_uni()) / 100));
//
//                                tv_uni_cv.setText("With Convenience Fee: " + roundTwoDecimals(convenience_amount_unionpay));
//                            }
//                        }
//
//                    } else {
//                        tv_uni_cv.setVisibility(View.GONE);
//                        tv_ali_cv.setVisibility(View.GONE);
//                    }
//
//                } else {
//                    if (preferenceManager.isConvenienceFeeSelected()) {
//                        tv_ali_cv.setText("With Convenience Fee: " + 0.00);
//                        tv_uni_cv.setText("With Convenience Fee: " + 0.00);
//                    } else {
//                        tv_uni_cv.setVisibility(View.GONE);
//                        tv_ali_cv.setVisibility(View.GONE);
//                    }
//                }
//
//
//            }
//        });
//    }
//
//
//    public void calledt_amount1() {
//        edt_amount1.setInputType(InputType.TYPE_CLASS_NUMBER);
//        edt_amount1.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (edt_amount1.getText().toString().equals("0.00")) {
//                    edt_amount1.setText(null);
//
//                }
//                return false;
//            }
//        });
//        edt_amount1.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.v("", "");
//                try {
//                    if (s.equals("")) {
//                        return;
//                    } else {
//
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.v("", "");
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                Log.v("", "");
//                if (selected_screen != 0) {
//                    selected_screen = 0;
//
//                }
//
//                if (!edt_amount1.getText().toString().equals("") &&
//                        !preferenceManager.getcnv_alipay().equals("") &&
//                        !preferenceManager.getcnv_uni().equals("")) {
//                    if (preferenceManager.isConvenienceFeeSelected()) {
////                        tv_uni_cv.setVisibility(View.VISIBLE);
////                        tv_ali_cv.setVisibility(View.VISIBLE);
//                        if (preferenceManager.is_cnv_alipay_display_and_add() || preferenceManager.is_cnv_alipay_display_only()) {
//                            if (!preferenceManager.getcnv_alipay().equals("") ||
//                                    !preferenceManager.getcnv_alipay().equals("0.0") ||
//                                    !preferenceManager.getcnv_alipay().equals("0.00")) {
//                                //This was the old convinence fee calculation which was used before 13 march 2019 change of new formula
//                                Log.v("AMount1", Double.parseDouble(edt_amount1.getText().toString().replace(",", "")) + "");
//                                convenience_amount_alipay = (Double.parseDouble(edt_amount1.getText().toString().replace(",", "")) / 100.0f) *
//                                        (Double.parseDouble(preferenceManager.getcnv_alipay()));
//                                convenience_amount_alipay = convenience_amount_alipay + Double.parseDouble(edt_amount1.getText().toString().replace(",", ""));
//
//
//                                //added on 13 march 2019
//                                //This is new convienece fee calculation
////                            C/(1-r) where C is the total amount and r is convinence fee in percent.
//                                convenience_amount_alipay = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
//                                        (1 - (Double.parseDouble(preferenceManager.getcnv_alipay()) / 100));
//
//                                tv_ali_cv.setText("With Convenience Fee: " + roundTwoDecimals(convenience_amount_alipay));
//                            }
//                        }
//
//                        if (preferenceManager.is_cnv_uni_display_and_add() || preferenceManager.is_cnv_uni_display_only()) {
//                            if (!preferenceManager.getcnv_uni().equals("") ||
//                                    !preferenceManager.getcnv_uni().equals("0.0") ||
//                                    !preferenceManager.getcnv_uni().equals("0.00")) {
//                                //This was the old convinence fee calculation which was used before 13 march 2019 change of new formula
//                                Log.v("AMount2", Double.parseDouble(edt_amount1.getText().toString().replace(",", "")) + "");
//                                convenience_amount_unionpay = (Double.parseDouble(edt_amount1.getText().toString().replace(",", "")) / 100.0f) *
//                                        (Double.parseDouble(preferenceManager.getcnv_uni()));
//                                convenience_amount_unionpay = convenience_amount_unionpay + Double.parseDouble(edt_amount1.getText().toString().replace(",", ""));
//
//
//                                //added on 13 march 2019
//                                //This is new convienece fee calculation
////                            C/(1-r) where C is the total amount and r is convinence fee in percent.
//                                convenience_amount_unionpay = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
//                                        (1 - (Double.parseDouble(preferenceManager.getcnv_uni()) / 100));
//
//                                tv_uni_cv.setText("With Convenience Fee: " + roundTwoDecimals(convenience_amount_unionpay));
//                            }
//                        }
//
//                    } else {
//                        tv_uni_cv.setVisibility(View.GONE);
//                        tv_ali_cv.setVisibility(View.GONE);
//                    }
//                } else {
//                    if (preferenceManager.isConvenienceFeeSelected()) {
//                        tv_ali_cv.setText("With Convenience Fee: " + 0.00);
//                        tv_uni_cv.setText("With Convenience Fee: " + 0.00);
//                    } else {
//                        tv_uni_cv.setVisibility(View.GONE);
//                        tv_ali_cv.setVisibility(View.GONE);
//                    }
//                }
//
//
//            }
//        });
//    }
//
//
////    public void callQRCode() {
////        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
////        //payQr
////        if (countDownTimerxmpp != null) {
////            countDownTimerxmpp.cancel();
////            tv_start_countdown.setVisibility(View.GONE);
////        }
////        openProgressDialog();
////        try {
////
////
////            if (MyPOSMateApplication.isOpen) {
////                char[] ch = xmppAmount.toCharArray();
////                StringBuilder sb = new StringBuilder();
////
////                for (int i = 0; i < ch.length; i++) {
////                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {
////
////                    } else {
////                        sb.append(ch[i]);
////                    }
////                }
////                xmppAmount = sb.toString().replace(",", "");
////
////                if (edt_reference.getText().toString().equals("null"))
////                    preferenceManager.setReference("");
////                else
////                    preferenceManager.setReference(edt_reference.getText().toString());
////
////
////                if (preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_alipay_display_and_add()) {
////                    original_amount = original_xmpp_trigger_amount;
////                    fee_amount = convenience_amount_alipay -
////                            Double.parseDouble(original_xmpp_trigger_amount.replace(",", ""))
////                            + "";
////                    fee_percentage = preferenceManager.getcnv_alipay();
////                    new OkHttpHandler(getActivity(), this, null, "payQr").execute(AppConstants.BASE_URL + AppConstants.payQr
////                            + "?reference_id=" + reference_id + "&grandtotal=" + xmppAmount + "&terminal_id=" + preferenceManager.getterminalId().toString()
////                            + "&is_mobile_device=true&refData1=" + URLEncoder.encode(edt_reference.getText().toString(), "UTF-8") + "&refData2=" + "" + "&payment_channels=" + payment_mode + "&selectedChannel=" + payment_mode +
////                            "&access_id=" + preferenceManager.getuniqueId() + "&qrMode=" + qrMode + "&auth_code=" + auth_code
////                            + "&original_amount=" + original_amount + "&fee_amount=" + fee_amount + "&fee_percentage=" + fee_percentage + "&discount=0");
////                } else {
////                    new OkHttpHandler(getActivity(), this, null, "payQr").execute(AppConstants.BASE_URL + AppConstants.payQr
////                            + "?reference_id=" + reference_id + "&grandtotal=" + xmppAmount + "&terminal_id=" + preferenceManager.getterminalId().toString()
////                            + "&is_mobile_device=true&refData1=" + URLEncoder.encode(edt_reference.getText().toString(), "UTF-8") + "&refData2=" + "" + "&payment_channels=" + payment_mode + "&selectedChannel=" + payment_mode +
////                            "&access_id=" + preferenceManager.getuniqueId() + "&qrMode=" + qrMode + "&auth_code=" + auth_code);
////                }
////
////
////            } else {
////                String amount = "";
////                char[] ch = edt_amount.getText().toString().toCharArray();
////                StringBuilder sb = new StringBuilder();
////
////                for (int i = 0; i < ch.length; i++) {
////                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {
////
////                    } else {
////                        sb.append(ch[i]);
////                    }
////                }
////                amount = sb.toString().replace(",", "");
////                if (edt_reference.getText().toString().equals("null"))
////                    preferenceManager.setReference("");
////                else
////                    preferenceManager.setReference(edt_reference.getText().toString());
////
////                if (preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_alipay_display_and_add()) {
////                    amount = convenience_amount_alipay + "";
////                    original_amount = edt_amount.getText().toString().replace(",", "");
////                    fee_amount = convenience_amount_alipay -
////                            Double.parseDouble(edt_amount.getText().toString().replace(",", ""))
////                            + "";
////                    fee_percentage = preferenceManager.getcnv_alipay();
////
////                    new OkHttpHandler(getActivity(), this, null, "payQr").execute(AppConstants.BASE_URL + AppConstants.payQr
////                            + "?reference_id=" + reference_id + "&grandtotal=" + amount + "&terminal_id=" + preferenceManager.getterminalId().toString()
////                            + "&is_mobile_device=true&refData1=" + URLEncoder.encode(edt_reference.getText().toString(), "UTF-8") + "&refData2=" + "" + "&payment_channels=" + payment_mode + "&selectedChannel=" + payment_mode +
////                            "&access_id=" + preferenceManager.getuniqueId() + "&qrMode=" + qrMode + "&auth_code=" + auth_code
////                            + "&original_amount=" + original_amount + "&fee_amount=" + fee_amount + "&fee_percentage=" + fee_percentage + "&discount=0");
////                } else {
////                    new OkHttpHandler(getActivity(), this, null, "payQr").execute(AppConstants.BASE_URL + AppConstants.payQr
////                            + "?reference_id=" + reference_id + "&grandtotal=" + amount + "&terminal_id=" + preferenceManager.getterminalId().toString()
////                            + "&is_mobile_device=true&refData1=" + URLEncoder.encode(edt_reference.getText().toString(), "UTF-8") + "&refData2=" + "" + "&payment_channels=" + payment_mode + "&selectedChannel=" + payment_mode +
////                            "&access_id=" + preferenceManager.getuniqueId() + "&qrMode=" + qrMode + "&auth_code=" + auth_code);
////                }
////
////
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//
//
//    public void callPayNow() {
//        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
//        if (countDownTimerxmpp != null) {
//            countDownTimerxmpp.cancel();
//            tv_start_countdown.setVisibility(View.GONE);
//        }
//        openProgressDialog();
//        try {
//
//
//            if (MyPOSMateApplication.isOpen) {
//                char[] ch = xmppAmount.toCharArray();
//                StringBuilder sb = new StringBuilder();
//
//                for (int i = 0; i < ch.length; i++) {
//                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {
//
//                    } else {
//                        sb.append(ch[i]);
//                    }
//                }
//                xmppAmount = sb.toString().replace(",", "");
//
//
//                if (preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_alipay_display_and_add()) {
//                    original_amount = original_xmpp_trigger_amount;
//                    xmppAmount = convenience_amount_alipay + "";
//                    fee_amount = convenience_amount_alipay -
//                            Double.parseDouble(original_xmpp_trigger_amount.replace(",", ""))
//                            + "";
//                    fee_percentage = preferenceManager.getcnv_alipay();
//                    preferenceManager.setReference(edt_reference.getText().toString());
//
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", URLEncoder.encode(edt_reference.getText().toString(), "UTF-8"));
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", reference_id);
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grand_total", xmppAmount);
//                    hashMapKeys.put("original_amount", original_amount);
//                    hashMapKeys.put("fee_amount", fee_amount);
//                    hashMapKeys.put("fee_percentage", fee_percentage);
//                    hashMapKeys.put("discount", "0");
//                    hashMapKeys.put("auth_code", auth_code);
//                    hashMapKeys.put("channel", selected_channel);
//                    //   hashMapKeys.put("channel", channel);
//
//                    new OkHttpHandler(getActivity(), this, null, "paynow")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYNOW + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//
//
//                } else {
//                    preferenceManager.setReference(edt_reference.getText().toString());
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", URLEncoder.encode(edt_reference.getText().toString(), "UTF-8"));
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", reference_id);
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grand_total", xmppAmount);
//                    hashMapKeys.put("auth_code", auth_code);
//                    hashMapKeys.put("channel", selected_channel);
//                    //   hashMapKeys.put("channel", channel);
//
//                    new OkHttpHandler(getActivity(), this, null, "paynow")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYNOW + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//
//                }
//
//
//            } else {
//                String amount = "";
//                char[] ch = edt_amount.getText().toString().toCharArray();
//                StringBuilder sb = new StringBuilder();
//
//                for (int i = 0; i < ch.length; i++) {
//                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {
//
//                    } else {
//                        sb.append(ch[i]);
//                    }
//                }
//                amount = sb.toString().replace(",", "");
//                if (preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_alipay_display_and_add()) {
//                    original_amount = amount;
//                    amount = convenience_amount_alipay + "";
//                    fee_amount = convenience_amount_alipay -
//                            Double.parseDouble(edt_amount.getText().toString().replace(",", ""))
//                            + "";
//                    fee_percentage = preferenceManager.getcnv_alipay();
//
//                    preferenceManager.setReference(edt_reference.getText().toString());
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", URLEncoder.encode(edt_reference.getText().toString(), "UTF-8"));
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", reference_id);
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grand_total", amount);
//                    hashMapKeys.put("original_amount", original_amount);
//                    hashMapKeys.put("fee_amount", fee_amount);
//                    hashMapKeys.put("fee_percentage", fee_percentage);
//                    hashMapKeys.put("discount", "0");
//                    hashMapKeys.put("auth_code", auth_code);
//                    hashMapKeys.put("channel", selected_channel);
//                    //    hashMapKeys.put("channel", channel);
//
//                    new OkHttpHandler(getActivity(), this, null, "paynow")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYNOW + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//
//                } else {
//                    preferenceManager.setReference(edt_reference.getText().toString());
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", URLEncoder.encode(edt_reference.getText().toString(), "UTF-8"));
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//
//
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", reference_id);
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grand_total", amount);
//
//                    hashMapKeys.put("auth_code", auth_code);
//                    hashMapKeys.put("channel", selected_channel);
//                    // hashMapKeys.put("channel", channel);
//
//                    new OkHttpHandler(getActivity(), this, null, "paynow")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYNOW + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void callUnionPay() {
//        if (countDownTimerxmpp != null) {
//            countDownTimerxmpp.cancel();
//            tv_start_countdown.setVisibility(View.GONE);
//        }
//        openProgressDialog();
//        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
//        String amount = "";
//        if (preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_uni_display_and_add()) {
//            if (preferenceManager.getcnv_uni().equals("") || preferenceManager.getcnv_uni().equals("0.0") || preferenceManager.getcnv_uni().equals("0.00")) {
//
//                amount = convenience_amount_unionpay + "";
//                original_amount = edt_amount.getText().toString().replace(",", "");
//                fee_amount = convenience_amount_unionpay - Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "";
//                discount = "0";
//                fee_percentage = preferenceManager.getcnv_uni();
//
//            } else {
//                if (MyPOSMateApplication.isOpen) {
//                    amount = convenience_amount_unionpay + "";
//                    original_amount = original_xmpp_trigger_amount.replace(",", "");
//                    fee_amount = convenience_amount_unionpay - Double.parseDouble(original_xmpp_trigger_amount.replace(",", "")) + "";
//                    discount = "0";
//                    fee_percentage = preferenceManager.getcnv_uni();
//                } else {
//                    amount = convenience_amount_unionpay + "";
//                    original_amount = edt_amount.getText().toString().replace(",", "");
//                    fee_amount = convenience_amount_unionpay - Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "";
//                    discount = "0";
//                    fee_percentage = preferenceManager.getcnv_uni();
//                }
//
//            }
//
//            try {
//                if (!preferenceManager.gettriggerReferenceId().equals("")) {
//
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", edt_reference.getText().toString());
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("is_mobile_device", "true");
//                    hashMapKeys.put("payment_channels", "UNION_PAY");
//                    hashMapKeys.put("selectedChannel", "UNION_PAY");
//                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
//                    hashMapKeys.put("qrMode", "true");
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", preferenceManager.gettriggerReferenceId());
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grandtotal", amount.replace(",", ""));
//                    hashMapKeys.put("original_amount", original_amount);
//                    hashMapKeys.put("fee_amount", fee_amount);
//                    hashMapKeys.put("fee_percentage", fee_percentage);
//                    hashMapKeys.put("discount", "0");
//                    if (!auth_code.equals(""))
//                        hashMapKeys.put("auth_code", auth_code);
//
//                    new OkHttpHandler(getActivity(), this, null, "payUnionPay")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYUNIONPAY + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//
//                } else {
//
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", edt_reference.getText().toString());
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", reference_id);
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grandtotal", amount.replace(",", ""));
//                    hashMapKeys.put("original_amount", original_amount);
//                    hashMapKeys.put("fee_amount", fee_amount);
//                    hashMapKeys.put("fee_percentage", fee_percentage);
//                    hashMapKeys.put("discount", "0");
//                    hashMapKeys.put("is_mobile_device", "true");
//                    hashMapKeys.put("payment_channels", "UNION_PAY");
//                    hashMapKeys.put("selectedChannel", "UNION_PAY");
//                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
//                    hashMapKeys.put("qrMode", "true");
//                    if (!auth_code.equals(""))
//                        hashMapKeys.put("auth_code", auth_code);
//
//                    new OkHttpHandler(getActivity(), this, null, "payUnionPay")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYUNIONPAY + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//
//
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        } else if (preferenceManager.isConvenienceFeeSelected() && !preferenceManager.is_cnv_uni_display_and_add()) {
//            try {
//                if (!preferenceManager.gettriggerReferenceId().equals("")) {
//
//
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", edt_reference.getText().toString());
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", preferenceManager.gettriggerReferenceId());
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grandtotal", edt_amount.getText().toString().replace(",", ""));
//                    if (!auth_code.equals(""))
//                        hashMapKeys.put("auth_code", auth_code);
//                    hashMapKeys.put("is_mobile_device", "true");
//                    hashMapKeys.put("payment_channels", "UNION_PAY");
//                    hashMapKeys.put("selectedChannel", "UNION_PAY");
//                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
//                    hashMapKeys.put("qrMode", "true");
//                    new OkHttpHandler(getActivity(), this, null, "payUnionPay")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYUNIONPAY + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//
//
//                } else {
//
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", edt_reference.getText().toString());
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", reference_id);
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grandtotal", edt_amount.getText().toString().replace(",", ""));
//                    if (!auth_code.equals(""))
//                        hashMapKeys.put("auth_code", auth_code);
//                    hashMapKeys.put("is_mobile_device", "true");
//                    hashMapKeys.put("payment_channels", "UNION_PAY");
//                    hashMapKeys.put("selectedChannel", "UNION_PAY");
//                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
//                    hashMapKeys.put("qrMode", "true");
//                    new OkHttpHandler(getActivity(), this, null, "payUnionPay")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYUNIONPAY + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//
//
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        } else {
//            if (MyPOSMateApplication.isOpen) {
//                amount = original_xmpp_trigger_amount.replace(",", "");
//            } else {
//                amount = edt_amount.getText().toString().replace(",", "");
//            }
//            try {
//                if (!preferenceManager.gettriggerReferenceId().equals("")) {
//
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", edt_reference.getText().toString());
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", preferenceManager.gettriggerReferenceId());
//                    hashMapKeys.put("is_mobile_device", "true");
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grandtotal", amount.replace(",", ""));
//                    if (!auth_code.equals(""))
//                        hashMapKeys.put("auth_code", auth_code);
//                    hashMapKeys.put("payment_channels", "UNION_PAY");
//                    hashMapKeys.put("selectedChannel", "UNION_PAY");
//                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
//                    hashMapKeys.put("qrMode", "true");
//
//                    new OkHttpHandler(getActivity(), this, null, "payUnionPay")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYUNIONPAY + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//
//
//                } else {
//
//
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", edt_reference.getText().toString());
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("is_mobile_device", "true");
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", reference_id);
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grandtotal", amount.replace(",", ""));
//                    if (!auth_code.equals(""))
//                        hashMapKeys.put("auth_code", auth_code);
//
//                    hashMapKeys.put("payment_channels", "UNION_PAY");
//                    hashMapKeys.put("selectedChannel", "UNION_PAY");
//                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
//                    hashMapKeys.put("qrMode", "true");
//
//                    new OkHttpHandler(getActivity(), this, null, "payUnionPay")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYUNIONPAY + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//
//
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//
//    }
//
//
//    public void callUnionPayStatus(String json_data, String status) {
//        openProgressDialog();
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
//                    if (jsonObject.optString("transactionType").equals("SALE") ||
//                            jsonObject.optString("transactionType").equals("COUPON_SALE") ||
//                            jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_SALE")
//                    ) {
//                        status = "20";
//                    } else if (jsonObject.optString("transactionType").equals("VOID") ||
//                            jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_VOID") ||
//                            jsonObject.optString("transactionType").equals("COUPON_VOID")) {
//                        status = "19"; //set 22 to 19 in case of void on 28/02/2019
//                    }
//
//                } else {
//                    status = "23";
//                    Toast.makeText(getActivity(), jsonObject.optString("responseMessage"), Toast.LENGTH_LONG).show();
//                }
//            } else {
//                status = "23";
//                Toast.makeText(getActivity(), jsonObject.optString("responseMessage"), Toast.LENGTH_LONG).show();
//
//            }
//            preferenceManager.setreference_id(jsonObject.optString("orderNumber"));
////            new OkHttpHandler(getActivity(), this, null, "unionpaystatus").
////                    execute(AppConstants.BASE_URL + AppConstants.UNION_PAY_STATUS
////                            + "?reference_id=" + jsonObject.optString("orderNumber")
////                            + "&is_mobile_device=true"
////                            + "&terminal_id=" + preferenceManager.getterminalId() +
////                            "&access_id=" + preferenceManager.getuniqueId()
////                            + "&json_data=" + URLEncoder.encode(s + json_data + "}", "UTF-8")
////                            + "&status_id=" + status);
//
//            hashMapKeys.clear();
//            String randomStr = new Date().getTime() + "";
//
//            hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//            hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
//            hashMapKeys.put("is_mobile_device", "true");
//            hashMapKeys.put("access_id", preferenceManager.getuniqueId());
//            hashMapKeys.put("config_id", preferenceManager.getConfigId());
//            hashMapKeys.put("reference_id", jsonObject.optString("orderNumber"));
//            hashMapKeys.put("random_str", randomStr);
//            hashMapKeys.put("status_id", status);
//            hashMapKeys.put("json_data", s + json_data + "}");
//
//
//            String s2 = "", s1 = "";
//            int i1 = 0;
//            Iterator<String> iterator = hashMapKeys.keySet().iterator();
//            while (iterator.hasNext()) {
//                String key = iterator.next();
//                if (i1 != hashMapKeys.size() - 1)
//                    s2 = s2 + key + "=" + hashMapKeys.get(key) + "&";
//                else
//                    s2 = s2 + key + "=" + hashMapKeys.get(key);
//                i1++;
//            }
//            s2 = s2 + PreferencesManager.getInstance(context).getauthToken();//.getuniqueId();
//            String signature = MD5Class.MD5(s2);
//
//
//            s = "{\n" +
//                    "  \"head\": {\n" +
//                    "    \"version\": \"V1.2.0\"\n" +
//                    "  },\n" +
//                    "  \"body\":";
//
//            hashMapKeys.clear();
//            hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//            hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
//            hashMapKeys.put("is_mobile_device", "true");
//            hashMapKeys.put("access_id", preferenceManager.getuniqueId());
//            hashMapKeys.put("config_id", preferenceManager.getConfigId());
//            hashMapKeys.put("reference_id", jsonObject.optString("orderNumber"));
//            hashMapKeys.put("random_str", randomStr);
//            hashMapKeys.put("status_id", status);
//            hashMapKeys.put("json_data", URLEncoder.encode(s + json_data + "}", "UTF-8"));
//            i1 = 0;
//            Iterator<String> iterator1 = hashMapKeys.keySet().iterator();
//            while (iterator1.hasNext()) {
//                String key = iterator1.next();
//                if (i1 != hashMapKeys.size() - 1)
//                    s1 = s1 + key + "=" + hashMapKeys.get(key) + "&";
//                else
//                    s1 = s1 + key + "=" + hashMapKeys.get(key);
//                i1++;
//            }
//
//            AppConstants.EXTERNAL_APP_UNIONPAY_RESPONSE = URLDecoder.decode(s + json_data + "}", "UTF-8");
//
//            new OkHttpHandler(getActivity(), this, null, "unionpaystatus")
//                    .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_UPDATE_UNIONPAY_STATUS + "?" + s1 + "&signature=" + signature + "&access_token=" + preferenceManager.getauthToken());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static boolean isTransactionDone = false;
//
//    public void returnDataToExternalApp(TreeMap<String, String> hashMapKeys, String s) {
//        try {
//
//            //added for external apps
//            int REQ_PAY_SALE = 100;
//            if (DashboardActivity.isExternalApp) {
//                DashboardActivity.isExternalApp = false;
//                isTransactionDone = true;
//                JSONObject jsonObjectData = new JSONObject();
//                int i1 = 0;
//                Iterator<String> iterator11 = hashMapKeys.keySet().iterator();
//                while (iterator11.hasNext()) {
//                    String key = iterator11.next();
//                    if (i1 != hashMapKeys.size() - 1)
//                        jsonObjectData.put(key, hashMapKeys.get(key));
//                    else
//                        jsonObjectData.put(key, hashMapKeys.get(key));
//                    i1++;
//                }
//                jsonObjectData.remove("json_data");
//                jsonObjectData.put("json_data", s);
//                ((DashboardActivity) getActivity()).getIntent().putExtra("result", jsonObjectData.toString());
//                ((DashboardActivity) getActivity()).setResult(REQ_PAY_SALE, ((DashboardActivity) getActivity()).getIntent());
//                ((DashboardActivity) getActivity()).finishAndRemoveTask();
//                return;
//            }  //added for external apps
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void callTransactionDetails() {
//        //v2 signature implementation
//        hashMapKeys.clear();
//        hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//        hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//        hashMapKeys.put("config_id", preferenceManager.getConfigId());
//        hashMapKeys.put("reference_id", reference_id);
//        hashMapKeys.put("random_str", new Date().getTime() + "");
//        new OkHttpHandler(getActivity(), this, null, "TransactionDetails")
//                .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_GET_TRANSACTION_DETAILS + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//    }
//
//
////    public void callTransactionDetails() {
////        new OkHttpHandler(getActivity(), this, null, "TransactionDetails").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.getGatewayTransactionDetails
////                + "?reference_id=" + reference_id + "&terminal_id=" + preferenceManager.getterminalId().toString() +
////                "&access_id=" + preferenceManager.getuniqueId() + "&is_mobile_device=true");
////
////    }
//
//
//    String TransactionType = "";
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (data.hasExtra("responseCodeThirtyNine")) {
//            try {
//                Bundle bundle = new Bundle();
//                bundle.putAll(data.getExtras());
//                JSONObject json = new JSONObject();
//                Set<String> keys = bundle.keySet();
//                for (String key : keys) {
//                    // if (bundle.get(key) != null)
//                    json.put(key, JSONObject.wrap(bundle.get(key)));
//                }
//
//                switch (TransactionType) {
//
//                    case ThirtConst.TransType.SALE:
//                        json.put("orderNumber", jsonObjectSale.optString("orderNumber"));
//                        break;
//                    case ThirtConst.TransType.COUPON_SALE:
//                        json.put("orderNumber", jsonObjectCouponSale.optString("orderNumber"));
//                        break;
//                }
//
//
//                if (json.optString("responseCodeThirtyNine").equals("00")) {
//                    onTaskCompleted(json.toString(), "Arke");
//                } else {
//                    onTaskCompleted(json.toString(), "Arke");
//                    auth_code = "";
//                    isUnionPayQrSelected = false;
//                    isUpayselected = false;
//                    reference_id = "";
//                    preferenceManager.setreference_id("");
//                    callAuthToken();
//                }
//
//
//            } catch (Exception e) {
//                //Handle exception here
//
//            }
//
//        }
//
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            if (result.getContents() == null) {
//                Toast.makeText(getActivity(), "Result Not Found", Toast.LENGTH_LONG).show();
//            } else {
//                try {
//                    JSONObject obj = new JSONObject(result.getContents());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_LONG).show();
//                }
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//
//
//    public void callMembershipLoyality(String qr_data) {
//        openProgressDialog();
//        hashMapKeys.clear();
//        hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//        hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
//        hashMapKeys.put("config_id", preferenceManager.getConfigId());
//        hashMapKeys.put("device_id", UUID.randomUUID().toString().replace("-", ""));
//        hashMapKeys.put("qr_data", qr_data);
//        hashMapKeys.put("random_str", new Date().getTime() + "");
//        hashMapKeys.put("lane_id", preferenceManager.getLaneIdentifier());
//        hashMapKeys.put("pos_id", preferenceManager.getPOSIdentifier());
//        new OkHttpHandler(getActivity(), this, null, "saveLoyaltyInfo")
//                .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_SAVE_LOYALTY_INFO + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//    }
//
//
//    public void callDisplayStaticQRDialog() {
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.setCancelable(false);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        LayoutInflater lf = (LayoutInflater) (getActivity())
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogview = lf.inflate(R.layout.layout_alipay_static_qr, null);
//
//        Button btn_close = dialogview.findViewById(R.id.btn_close);
//        dialog.setContentView(dialogview);
//
//
//        btn_close.setOnClickListener((View v) ->
//                {
//                    dialog.dismiss();
//
//                }
//        );
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        lp.gravity = Gravity.CENTER;
//
//
//        dialog.getWindow().setAttributes(lp);
//        dialog.show();
//
//
//    }
//
//
//    public static String selected_channel = "";
//
//    public void dialogQR() {
//        callAuthToken();
//        Dialog dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.qrdialog);
////        dialog.setTitle("Select Channel");
//        dialog.setCancelable(false);
//
//        Button btn_alipay = (Button) dialog.findViewById(R.id.btn_alipay);
//        Button btn_wechat = (Button) dialog.findViewById(R.id.btn_wechat);
//        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
//
//
//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        btn_wechat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selected_channel = "WECHAT";
//                isUnionPayQrSelected = false;
//                isUpayselected = false;
//                if (MyPOSMateApplication.isOpen) {
//                    payment_mode = "";
//                    qrMode = "False";
//                    open();
//                } else {
//                    if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("") && edt_reference.getText().toString().equals("")) {
//                        Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                    } else {
//                        payment_mode = "";
//                        qrMode = "False";
//                        open();
//                    }
//                }
//
//                dialog.dismiss();
//            }
//        });
//
//
//        btn_alipay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selected_channel = "ALIPAY";
//                isUnionPayQrSelected = false;
//                isUpayselected = false;
//                if (MyPOSMateApplication.isOpen) {
//                    payment_mode = "";
//                    qrMode = "False";
//                    open();
//                } else {
//                    if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("") && edt_reference.getText().toString().equals("")) {
//                        Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                    } else {
//                        payment_mode = "";
//                        qrMode = "False";
//                        open();
//                    }
//                }
//                dialog.dismiss();
//            }
//        });
//
//
//        dialog.show();
//    }
//
//
//    public static boolean isUpayselected = false;
//    public static boolean isunionPayQrScanSelectedForSale = false;
//    public static boolean isUnionPayQrSelected = false;
//    public static boolean isBack = false;
//    public static boolean isFront = false;
//    private Context mContext;
//    String channel = "";
//
//    @Override
//    public void onClick(View view) {
//        mContext = getActivity();
//        if (((DashboardActivity) mContext).mPopupWindow.isShowing())
//            ((DashboardActivity) mContext).mPopupWindow.dismiss();
//
//        switch (view.getId()) {
//
//
//            case R.id.img_alipay_static_qr:
//                callDisplayStaticQRDialog();
//                break;
//
//            case R.id.btn_front:
//                edt_amount.setText("0.00");
//                edt_amount1.setText("0.00");
//                try {
//                    if (preferenceManager.getLaneIdentifier().equals("")) {
//                        Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
//                    } else if (preferenceManager.getPOSIdentifier().equals("")) {
//                        Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
//                    } else {
//                        isFront = true;
//                        callAuthToken();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.tv_status_scan_button:
//                try {
//                    if (preferenceManager.getLaneIdentifier().equals("")) {
//                        Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
//                    } else if (preferenceManager.getPOSIdentifier().equals("")) {
//                        Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
//                    } else {
//                        isFront = true;
//                        callAuthToken();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//                break;
//
//            case R.id.btn_back:
//                edt_amount.setText("0.00");
//                edt_amount1.setText("0.00");
//                try {
//                    if (preferenceManager.getLaneIdentifier().equals("")) {
//                        Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
//                    } else if (preferenceManager.getPOSIdentifier().equals("")) {
//                        Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
//                    } else {
//                        isBack = true;
//                        callAuthToken();
//
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//
//
//            case R.id.tv_unipay:
//            case R.id.tv_alipay:
//            case R.id.tv_wechat:
//            case R.id.tv_vice:
//                break;
//
//
//            case R.id.scanqr:
//            case R.id.tv_scan_code:
//
//                dialogQR();
//                break;
//
//            case R.id.tv_qrcode:
//                payment_mode = "";
//                qrMode = "False";
//                auth_code = "";
//                if (MyPOSMateApplication.isOpen) {
//                    //  callQRCode();
//                } else {
//                    if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                        Toast.makeText(getActivity(), "Please enter Amount and Reference Id.", Toast.LENGTH_LONG).show();
//                    } else {
//                        //    callQRCode();
//                    }
//                }
//
//                break;
//
//            case R.id.btn_save:
//            case R.id.btn_save1:
//            case R.id.rel_wechat:
//            case R.id.img_wechat:
//                channel = "WECHAT";
//                selected_screen = 2;
//                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//                View view1 = getActivity().getCurrentFocus();
//
//                if (view1 != null) {
//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
//                    assert imm != null;
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//
//                if (selected_screen == 0) {
//                    Toast.makeText(getActivity(), "Please select the payment type", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                if (preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_alipay_display_and_add())
//                    xmppAmount = convenience_amount_alipay + "";
//                else
//                    xmppAmount = edt_amount.getText().toString();
//
//
//                if (preferenceManager.isaggregated_singleqr()) {
//                    payment_mode = "ALIPAY";
//                    qrMode = "True";
//                    auth_code = "";
//                    if (MyPOSMateApplication.isOpen) {
//                        //  callQRCode();
//                        callTerminalPay();
//
//                    } else {
//                        if (preferenceManager.isaggregated_singleqr() && !preferenceManager.isUnipaySelected()) {
//                        }
//                        if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                            Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                        } else {
//                            // callQRCode();
//                            callTerminalPay();
//                        }
//                    }
//
//                } else
//                    switch (selected_screen) {
//                        case 1:
//                        case 2:
//                            payment_mode = "ALIPAY";
//                            qrMode = "True";
//                            auth_code = "";
//                            if (MyPOSMateApplication.isOpen) {
//                                callTerminalPay();
//                            } else {
//                                if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                                    Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                                } else {
//                                    callTerminalPay();
//                                }
//                            }
//
//                            break;
//                        case 3:
//
//                            payment_mode = "DPS";
//                            qrMode = "True";
//                            auth_code = "";
//                            if (MyPOSMateApplication.isOpen) {
//                                callTerminalPay();
//                            } else {
//                                if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                                    Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                                } else {
//                                    callTerminalPay();
//                                }
//
//                            }
//
//                            break;
//                        case 4:
//                            payment_mode = "";
//                            qrMode = "False";
//
//                            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                            } else {
//                                Toast.makeText(getActivity(), "Under Implementation", Toast.LENGTH_LONG).show();
//                            }
//
//                            break;
//                        case 5:
//                            payment_mode = "DPS";
//                            qrMode = "True";
//                            auth_code = "";
//                            if (MyPOSMateApplication.isOpen) {
//                                callTerminalPay();
//                            } else {
//                                if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                                    Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                                } else {
//                                    callTerminalPay();
//                                }
//
//                            }
//                            break;
//                        case 0:
//                            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                                Toast.makeText(getActivity(), "Please enter the amount and select payment type to proceed", Toast.LENGTH_LONG).show();
//                            }
//                            tv_selection_type.setText(getString(R.string.select_payment_type));
//                            tv_selection_type.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
//                            break;
//                    }
//
//
//                break;
//            case R.id.rel_alipay:
//            case R.id.img_alipay:
//                channel = "ALIPAY";
//                selected_screen = 2;
//                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//                View view2 = getActivity().getCurrentFocus();
//
//                if (view2 != null) {
//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
//                    assert imm != null;
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//
//                if (selected_screen == 0) {
//                    Toast.makeText(getActivity(), "Please select the payment type", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                if (preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_alipay_display_and_add())
//                    xmppAmount = convenience_amount_alipay + "";
//                else
//                    xmppAmount = edt_amount.getText().toString();
//
//
//                if (preferenceManager.isaggregated_singleqr()) {
//                    payment_mode = "ALIPAY";
//                    qrMode = "True";
//                    auth_code = "";
//                    if (MyPOSMateApplication.isOpen) {
//                        //  callQRCode();
//                        callTerminalPay();
//                    } else {
//                        if (preferenceManager.isaggregated_singleqr() && !preferenceManager.isUnipaySelected()) {
//                        }
//                        if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                            Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                        } else {
//                            // callQRCode();
//                            callTerminalPay();
//                        }
//                    }
//
//                } else
//                    switch (selected_screen) {
//                        case 1:
//                        case 2:
//                            payment_mode = "ALIPAY";
//                            qrMode = "True";
//                            auth_code = "";
//                            if (MyPOSMateApplication.isOpen) {
//                                callTerminalPay();
//                            } else {
//                                if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                                    Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                                } else {
//                                    callTerminalPay();
//                                }
//                            }
//
//                            break;
//                        case 3:
//
//                            payment_mode = "DPS";
//                            qrMode = "True";
//                            auth_code = "";
//                            if (MyPOSMateApplication.isOpen) {
//                                callTerminalPay();
//                            } else {
//                                if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                                    Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                                } else {
//                                    callTerminalPay();
//                                }
//
//                            }
//
//                            break;
//                        case 4:
//                            payment_mode = "";
//                            qrMode = "False";
//
//                            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                            } else {
//                                Toast.makeText(getActivity(), "Under Implementation", Toast.LENGTH_LONG).show();
//                            }
//
//                            break;
//                        case 5:
//                            payment_mode = "DPS";
//                            qrMode = "True";
//                            auth_code = "";
//                            if (MyPOSMateApplication.isOpen) {
//                                callTerminalPay();
//                            } else {
//                                if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                                    Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                                } else {
//                                    callTerminalPay();
//                                }
//
//                            }
//                            break;
//                        case 0:
//                            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                                Toast.makeText(getActivity(), "Please enter the amount and select payment type to proceed", Toast.LENGTH_LONG).show();
//                            }
//                            tv_selection_type.setText(getString(R.string.select_payment_type));
//                            tv_selection_type.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
//                            break;
//                    }
//
//                break;
//            case R.id.btn_cancel:
//            case R.id.btn_cancel1:
//
//                if (DashboardActivity.isExternalApp) {
//                    TransactionDetailsActivity.isReturnFromTransactionDetails = false;
//                    try {
//                        isTransactionDone = true;
//                        //added for external apps 12/5/2019
//                        int REQ_PAY_SALE = 100;
//                        DashboardActivity.isExternalApp = false;
//                        ((DashboardActivity) getActivity()).getIntent().putExtra("result", new JSONObject().toString());
//                        ((DashboardActivity) getActivity()).setResult(REQ_PAY_SALE, ((DashboardActivity) getActivity()).getIntent());
//                        ((DashboardActivity) getActivity()).finishAndRemoveTask();
//                        return;
//                        //added for external apps
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//
//                if (countDownTimerxmpp != null) {
//                    countDownTimerxmpp.cancel();
//                    tv_start_countdown.setVisibility(View.GONE);
//                    AppConstants.xmppamountforscan = "";
//                }
//                AppConstants.xmppamountforscan = "";
//                edt_amount.setText("");
//                edt_reference.setText("");
//                edt_amount1.setText("");
//                edt_reference1.setText("");
//                rel_alipay.setBackgroundResource(0);
//                rel_unionpay.setBackgroundResource(0);
//                rel_wechat.setBackgroundResource(0);
//                rel_scan.setBackgroundResource(0);
//                rel_paymentexpress.setBackgroundResource(0);
//                if (MyPOSMateApplication.isOpen) {
//                    MyPOSMateApplication.isOpen = false;
//                    MyPOSMateApplication.isActiveQrcode = false;
//                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
//                }
//                MyPOSMateApplication.isOpen = false;
//                MyPOSMateApplication.isActiveQrcode = false;
//                break;
//            case R.id.img_paymentexpress:
//            case R.id.rel_paymentexpress:
//                selected_screen = 5;
//                rel_alipay.setBackgroundResource(0);
//                rel_unionpay.setBackgroundResource(0);
//                rel_wechat.setBackgroundResource(0);
//                rel_scan.setBackgroundResource(0);
//                rel_paymentexpress.setBackgroundResource(R.drawable.edt_border_orange);
//                tv_selection_type.setText(getString(R.string.select_payment));
//                tv_selection_type.setTextColor(getResources().getColor(R.color.colorBlue));
//                break;
//            case R.id.scanqr_unionpay:
//
//                selected_screen = 4;
//                isUpayselected = false;
//                if (preferenceManager.getunion_pay_resp().equals("")) {
//                    payment_mode = "";
//                    qrMode = "False";
//                    preferenceManager.setupay_amount(edt_amount.getText().toString());
//                    xmppAmount = convenience_amount_unionpay + "";
//                    if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                        Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                    } else {
//                        isUnionPayQrSelected = true;
//                        isunionPayQrScanSelectedForSale = true;
//                        callUnionPay();
//                    }
//                } else {
//                    AppConstants.isUnionQrSelected = true;
//                    callUnionPayStatus(preferenceManager.getunion_pay_resp(), "true");
//                }
//
//
//                break;
//            case R.id.img_upay:
//
//
//                selected_screen = 4;
//                isunionPayQrScanSelectedForSale = false;
//                if (preferenceManager.getunion_pay_resp().equals("")) {
//                    payment_mode = "";
//                    qrMode = "False";
//                    preferenceManager.setupay_amount(edt_amount.getText().toString());
//                    xmppAmount = convenience_amount_unionpay + "";
//                    if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                        Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                    } else {
//                        isUpayselected = true;
//
//                        callUnionPay();
//                    }
//                } else {
//                    AppConstants.isUplanselected = true;
//                    callUnionPayStatus(preferenceManager.getunion_pay_resp(), "true");
//                }
//
//
//                break;
//
//            case R.id.img_unipay:
//                selected_screen = 4;
//
//                isUpayselected = false;
//                isUnionPayQrSelected = false;
//                if (preferenceManager.getunion_pay_resp().equals("")) {
//                    payment_mode = "";
//                    qrMode = "False";
//                    xmppAmount = convenience_amount_unionpay + "";
//                    if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                        Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                    } else {
//
//                        callUnionPay();
//                    }
//                } else {
//                    AppConstants.isUnionpayselected = true;
//                    callUnionPayStatus(preferenceManager.getunion_pay_resp(), "true");
//                }
//
//
//                break;
//            case R.id.rel_scan:
//            case R.id.img_scan:
//                selected_screen = 3;
//                rel_alipay.setBackgroundResource(0);
//                rel_unionpay.setBackgroundResource(0);
//                rel_wechat.setBackgroundResource(0);
//                rel_scan.setBackgroundResource(R.drawable.edt_border_orange);
//                rel_paymentexpress.setBackgroundResource(0);
//                tv_selection_type.setText(getString(R.string.select_payment));
//                tv_selection_type.setTextColor(getResources().getColor(R.color.colorBlue));
//
//                break;
//        }
//    }
//
//    public void open() {
//        IntentIntegrator integrator = new IntentIntegrator(getActivity());
//        integrator.setOrientationLocked(true);
//        integrator.initiateScan();
//    }
//
//    private Dialog dialog, dialog1;
//
//    public void showDialog(String text) {
//        dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        LayoutInflater lf = (LayoutInflater) (getActivity())
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogview = lf.inflate(R.layout.try_again_dialog, null);
//        TextView body = (TextView) dialogview
//                .findViewById(R.id.dialogBody);
//        body.setText(text);
//        dialog.setContentView(dialogview);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        lp.gravity = Gravity.CENTER;
//
//        dialog.getWindow().setAttributes(lp);
//        dialog.show();
//        TextView tv_ok = (TextView) dialogview
//                .findViewById(R.id.tv_ok);
//        tv_ok.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//
//    }
//
//    public void showDialog1(String text) {
//        dialog1 = new Dialog(getActivity());
//        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        LayoutInflater lf = (LayoutInflater) (getActivity())
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View dialogview = lf.inflate(R.layout.try_again_dialog, null);
//        TextView body = (TextView) dialogview
//                .findViewById(R.id.dialogBody);
//        body.setText(text);
//        dialog1.setContentView(dialogview);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog1.getWindow().getAttributes());
//        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        lp.gravity = Gravity.CENTER;
//
//        dialog1.getWindow().setAttributes(lp);
//        dialog1.show();
//        TextView tv_ok = (TextView) dialogview
//                .findViewById(R.id.tv_ok);
//        tv_ok.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                dialog1.dismiss();
//            }
//        });
//
//
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.d("", "");
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d("", "");
//        edt_amount = (CurrencyEditText) view.findViewById(R.id.edt_amount);
//        edt_amount.setLocale(new Locale("en", "US"));
//        edt_reference = (EditText) view.findViewById(R.id.edt_reference);
//        edt_amount1 = (CurrencyEditText) view.findViewById(R.id.edt_amount1);
//        edt_amount1.setLocale(new Locale("en", "US"));
//        edt_reference1 = (EditText) view.findViewById(R.id.edt_reference1);
//        edt_amount.requestFocus();
//
//
//        if (AlipayPaymentFragment.isCloseTrade) {
//            openProgressDialog();
//            callAuthToken();
//            AlipayPaymentFragment.isCloseTrade = false;
//        }
//
//        if (shadaf) {
//            shadaf = false;
//            try {
//                JSONObject jsonObject = new JSONObject(val);
//                if (jsonObject.has("responseCodeThirtyNine")) {
//                    if (jsonObject.has("responseCodeThirtyNine") && jsonObject.optString("responseCodeThirtyNine").equals("00")) {
//                        if (jsonObject.optString("transactionType").equals("SALE") ||
//                                jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_SALE") ||
//                                jsonObject.optString("transactionType").equals("COUPON_SALE")) {
//                            callUnionPayStatus(val, "true");
//                        } else if (jsonObject.optString("transactionType").equals("VOID") ||
//                                jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_VOID") ||
//                                jsonObject.optString("transactionType").equals("COUPON_VOID")) {
//                            callUnionPayStatus(val, "true");
//                        }
//
//                    } else {
//                        callUnionPayStatus(val, "false");
//                    }
//                } else if (jsonObject.has("responseMessage") &&
//
//                        jsonObject.optString("responseCode").equals("6")) {//user cancelled the transaction by closing the screen.
//                    if (jsonObject.optString("interfaceId").equals("SALE") ||
//                            jsonObject.optString("interfaceId").equals("COUPON_SALE")) {
//                        callUnionPayStatus(val, "false");
//                    } else if (jsonObject.optString("interfaceId").equals("VOID") ||
//                            jsonObject.optString("interfaceId").equals("UPI_SCAN_CODE_VOID") ||
//                            jsonObject.optString("interfaceId").equals("COUPON_VOID")) {
//                        callUnionPayStatus(val, "false");
//                    }
//                } else if (jsonObject.has("responseMessage") &&
//
//                        jsonObject.optString("responseCode").equals("2")) {//swipe failure and password incorrect
//                    if (jsonObject.optString("interfaceId").equals("SALE") ||
//                            jsonObject.optString("interfaceId").equals("UPI_SCAN_CODE_SALE") ||
//                            jsonObject.optString("interfaceId").equals("COUPON_SALE")) {
//                        callUnionPayStatus(val, "false");
//                    } else if (jsonObject.optString("interfaceId").equals("VOID") ||
//                            jsonObject.optString("interfaceId").equals("UPI_SCAN_CODE_VOID") ||
//                            jsonObject.optString("interfaceId").equals("COUPON_VOID")) {
//                        callUnionPayStatus(val, "false");
//                    }
//                } else {
//                    reference_id = "";
//                    preferenceManager.setreference_id("");
//                    preferenceManager.settriggerReferenceId("");
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            val = null;
//        } else {
//            reference_id = "";
//            preferenceManager.setreference_id("");
//            //6th dec 2018
//            if (MyPOSMateApplication.isOpen) {
//                if (AppConstants.isDeviceHomePressed) {
//                    AppConstants.isDeviceHomePressed = false;
//                } else {
//                    MyPOSMateApplication.isOpen = false;
//                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
//                }
//
//            }
//
//        }
//
//
//    }
//
//
//    JSONObject jsonObject;
//    static String val = null;
//
//    @Override
//    public void onTaskCompleted(String result, String TAG) throws Exception {
//
//        if (result.equals("")) {
//            if (progress != null && progress.isShowing())
//                progress.dismiss();
//            if (!TAG.equals("unionpaystatus"))
//                Toast.makeText(getActivity(), "No data from server.", Toast.LENGTH_LONG).show();
//            return;
//        }
//        if (!TAG.equals("Arke")) {
//            if (progress != null && progress.isShowing())
//                progress.dismiss();
//        }
//
//        jsonObject = new JSONObject(result);
//        switch (TAG) {
//
//            case "AuthToken":
//                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
//                    preferenceManager.setauthToken(jsonObject.optString("access_token"));
//                }
//                if (isBack) {
//                    isBack = false;
//                    stsartFastScan(true);//Back
//                }
//                if (isFront) {
//                    isFront = false;
//                    stsartFastScan(false);//front
//                }
//                break;
//
//            case "saveLoyaltyInfo":
//                callAuthToken();
//                progress.dismiss();
//                if (jsonObject.optBoolean("status")) {
//                    tv_status_scan.setVisibility(View.VISIBLE);
//                    tv_status_scan.setText("Thank you for using Membership/Loyality");
//                    tv_status_scan_button.setText("Rescan Membership/Loyality");
//                    Toast.makeText(getActivity(), "Loyality data uploaded successfully ", Toast.LENGTH_SHORT).show();
//                } else {
//                    tv_status_scan.setVisibility(View.VISIBLE);
//                    tv_status_scan.setText("Membership/Loyality could not be scanned");
//                    tv_status_scan_button.setText("Rescan Membership/Loyality");
//                    Toast.makeText(getActivity(), "Loyality data upload failed ", Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//
//            case "unionpaystatus":
//                callAuthToken();
//                reference_id = "";
//                preferenceManager.setreference_id("");
//                preferenceManager.settriggerReferenceId("");
//                preferenceManager.setunion_pay_resp("");
//
//
//                if (jsonObject.optBoolean("success")) {
//                    Toast.makeText((DashboardActivity) getActivity(), jsonObject.optString("response") + ".", Toast.LENGTH_LONG).show();
//
//
//                    //added for external apps 12/5/2019
//
//                    if (DashboardActivity.isExternalApp) {
//                        AppConstants.showDialog = true;
//                        isTransactionDone = true;
//                        ((DashboardActivity) getActivity()).callProgressDialogForUnionPay();
//                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
//                        returnDataToExternalApp(hashMapKeys, URLDecoder.decode(AppConstants.EXTERNAL_APP_UNIONPAY_RESPONSE));
//                    } else {
//                        AppConstants.showDialog = true;
//                        ((DashboardActivity) getActivity()).callProgressDialogForUnionPay();
//                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
//                    }
//
//
//                } else {
//                    Toast.makeText((DashboardActivity) getActivity(), "Failed to update the transaction", Toast.LENGTH_LONG).show();
//                }
//
//
//                if (AppConstants.isUnionQrSelected) {
//                    AppConstants.isUnionQrSelected = false;
//                    payment_mode = "";
//                    qrMode = "False";
//                    preferenceManager.setupay_amount(edt_amount.getText().toString());
//                    xmppAmount = convenience_amount_unionpay + "";
//                    if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                        Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                    } else {
//                        isUnionPayQrSelected = true;
//                        callUnionPay();
//                    }
//                }
//
//
//                if (AppConstants.isUplanselected) {
//                    AppConstants.isUplanselected = false;
//                    payment_mode = "";
//                    qrMode = "False";
//                    preferenceManager.setupay_amount(edt_amount.getText().toString());
//                    xmppAmount = convenience_amount_unionpay + "";
//                    if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                        Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                    } else {
//                        isUpayselected = true;
//
//                        callUnionPay();
//                    }
//                }
//
//
//                if (AppConstants.isUnionpayselected) {
//                    AppConstants.isUnionpayselected = false;
//                    payment_mode = "";
//                    qrMode = "False";
//                    xmppAmount = convenience_amount_unionpay + "";
//                    if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
//                        Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
//                    } else {
//
//                        callUnionPay();
//                    }
//                }
//                break;
//            case "payUnionPay":
//                callAuthToken();
//                if (jsonObject.optBoolean("status") == false) {
//                    preferenceManager.setreference_id("");
//                    reference_id = "";
//                    Toast.makeText(getActivity(), jsonObject.optString("message") + ".Please try again", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                if (jsonObject.optBoolean("status")) {
//                    preferenceManager.setreference_id(jsonObject.optString("reference_id"));
//
//
//                    if (isUpayselected || isUnionPayQrSelected) {
//                        preferenceManager.setupay_reference_id(jsonObject.optString("reference_id"));
//
//
//                        if (countDownTimerxmpp != null)
//                            countDownTimerxmpp.cancel();
//                        if (countDownTimer != null)
//                            countDownTimer.cancel();
//
//
//                        qrScan.initiateScan();
//                    } else
//                        beginBussiness(jsonObject.optString("reference_id"));
//                } else {
//                    Toast.makeText(context, jsonObject.optString("message"), Toast.LENGTH_LONG).show();
//                }
//
//                break;
//
//            case "Arke":
//                callAuthToken();
//
//                if (jsonObject.has("responseCodeThirtyNine")) {
//                    if (jsonObject.has("responseCodeThirtyNine") && jsonObject.optString("responseCodeThirtyNine").equals("00")) {
//                        ManualEntry.shadaf = true;
//                        if (isunionPayQrScanSelectedForSale) {
//                            isunionPayQrScanSelectedForSale = false;
//                            JSONObject jsonObject = new JSONObject(result);
//                            jsonObject.put("transactionType", "UPI_SCAN_CODE_SALE");
//                            jsonObject.put("qrcode", auth_code);
//                            preferenceManager.setunion_pay_resp(jsonObject.toString());
//                            ManualEntry.val = jsonObject.toString();
//                        } else {
//                            preferenceManager.setunion_pay_resp(jsonObject.toString());
//                            ManualEntry.val = jsonObject.toString();
//                        }
//
//                    } else {
//                        ManualEntry.shadaf = true;
//                        preferenceManager.setunion_pay_resp(jsonObject.toString());
//                        ManualEntry.val = jsonObject.toString();
//                    }
//
//                } else {
//                    ManualEntry.shadaf = true;
//                    if (isunionPayQrScanSelectedForSale) {
//                        isunionPayQrScanSelectedForSale = false;
//                        JSONObject jsonObject = new JSONObject(result);
//                        jsonObject.put("transactionType", "UPI_SCAN_CODE_SALE");
//                        jsonObject.put("qrcode", auth_code);
//                        preferenceManager.setunion_pay_resp(jsonObject.toString());
//                        ManualEntry.val = jsonObject.toString();
//                    } else {
//                        preferenceManager.setunion_pay_resp(jsonObject.toString());
//                        ManualEntry.val = jsonObject.toString();
//                    }
//
//                    Toast.makeText(getActivity(), jsonObject.optString("responseMessage"), Toast.LENGTH_LONG).show();
//
//                }
//                auth_code = "";
//                break;
//
//
//            case "TransactionDetails":
//                callAuthToken();
//
//                if (!jsonObject.optString("status_id").equals("USERPAYING") && !jsonObject.optString("status_description").equals("WAIT_BUYER_PAY")) {
//                    if (jsonObject.optString("status").equalsIgnoreCase("true") && !jsonObject.optString("status_description").equals("TRADE_NOT_PAY")) {
//                        if (progress1.isShowing())
//                            progress1.dismiss();
//                        if (MyPOSMateApplication.isOpen) {
//                            jsonObject.put("grandtotal", xmppAmount);
//                        } else
//                            jsonObject.put("grandtotal", edt_amount.getText().toString());
//                        selected_screen = 0;
//                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.PAYMENTPROCESSING, jsonObject.toString());
//                        if (progress.isShowing())
//                            progress.dismiss();
//                        countDownTimer.cancel();
//                    } else if (jsonObject.optString("status_description").equals("TRADE_REVOKED")) {
//                        if (progress1.isShowing())
//                            progress1.dismiss();
//                        if (progress.isShowing())
//                            progress.dismiss();
//                        showDialog("Transaction revoked. Try again.");
//                        countDownTimer.cancel();
//
//                    }
//                } else {
//                    if (jsonObject.optString("status_description").equals("TRADE_CLOSED")) {
//                        showDialog("Your transaction is closed.");
//                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.PAYMENTPROCESSING, jsonObject.toString());
//                        if (progress.isShowing())
//                            progress.dismiss();
//                        if (progress1.isShowing())
//                            progress1.dismiss();
//                        countDownTimer.cancel();
//                    }
//
//                }
//                break;
//
//
//            case "terminalPay":
//            case "payQr":
//            case "paynow":
//                callAuthToken();
//                AppConstants.xmppamountforscan = ""; //added on 12th march 2019
//                if (jsonObject.optBoolean("status") == false) {
//                    edt_amount.setText("");
//                    edt_amount1.setText("");
//                    preferenceManager.setreference_id("");
//                    reference_id = "";
//                    Toast.makeText(getActivity(), jsonObject.optString("message") + ".Please try again", Toast.LENGTH_LONG).show();
//                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
//                    return;
//                }
//                if (progress.isShowing())
//                    progress.dismiss();
//
//                reference_id = jsonObject.optString("reference_id");
//                preferenceManager.setreference_id(reference_id);
//                preferenceManager.setincrement_id(jsonObject.optString("increment_id"));
//                if (!payment_mode.equals("nochannel")) {
//                    if (MyPOSMateApplication.isOpen) {
//                        if (preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_alipay_display_and_add()) {
//                            jsonObject.put("amount", convenience_amount_alipay + "");
//                        } else {
//                            jsonObject.put("amount", xmppAmount);
//                        }
//
//                    } else if (preferenceManager.isaggregated_singleqr() && preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_alipay_display_and_add()) {
//                        jsonObject.put("amount", convenience_amount_alipay + "");//edt_amount.getText().toString().trim());
//                    } else if (preferenceManager.isaggregated_singleqr() && !preferenceManager.isConvenienceFeeSelected()) {
//                        jsonObject.put("amount", edt_amount.getText().toString().trim());
//                    } else
//                        jsonObject.put("amount", edt_amount.getText().toString().trim());
//                    String qrcode = jsonObject.optString("code_url");
//                    HashMap hashmap = new HashMap();
//                    hashmap.put("result", jsonObject.toString());
//                    hashmap.put("payment_mode", payment_mode);
//
//                    if (payment_mode.equalsIgnoreCase("DPS")) {
//                        if (jsonObject.optBoolean("success")) {
//                            String url = jsonObject.optString("url");
//                            Intent intent = new Intent(getActivity(), PaymentExpressActivity.class);
//                            intent.putExtra("url", url);
//                            startActivity(intent);
//                        } else {
//                            Toast.makeText(getActivity(), "Oops!! Something went wrong.", Toast.LENGTH_LONG).show();
//                        }
//                    } else {
//                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.ALIPAYPAYMENT, hashmap);
//                    }
//
//
//                    if (progress.isShowing())
//                        progress.dismiss();
//                    countDownTimer.cancel();
//                } else {
//                    if (!jsonObject.optBoolean("status")) {
//                        progress.dismiss();
//                        openProgressDialog();
//
//                        progress.setMessage("Please wait while processing the transaction..");
//                        progress1 = new ProgressDialog(getActivity());
//                        progress1.setMessage("Please wait while processing the transaction..");
//                        progress1.setCancelable(false);
//                        progress1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                        progress1.setIndeterminate(true);
//                        progress1.show();
//                        countDownTimer = new CountDownTimer(30000, 1000) { //40000 milli seconds is total time, 1000 milli seconds is time interval
//
//                            public void onTick(long millisUntilFinished) {
//                                callTransactionDetails();
//                            }
//
//                            public void onFinish() {
//                                try {
//                                    showDialog("Try again");
//                                    if (progress.isShowing())
//                                        progress.dismiss();
//                                    countDownTimer.cancel();
//
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        };
//                        countDownTimer.start();
//
//
//                    } else {
//                        if (MyPOSMateApplication.isOpen) {
//                            jsonObject.put("grandtotal", xmppAmount);
//                        } else
//                            jsonObject.put("grandtotal", edt_amount.getText().toString());
//                        if (progress.isShowing())
//                            progress.dismiss();
//                        openProgressDialog();
//
//                        progress.setMessage("Please wait while processing the transaction..");
//                        progress1 = new ProgressDialog(getActivity());
//                        progress1.setMessage("Please wait while processing the transaction..");
//                        progress1.setCancelable(false);
//                        progress1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                        progress1.setIndeterminate(true);
//                        progress1.show();
//                        countDownTimer = new CountDownTimer(30000, 1000) { //40000 milli seconds is total time, 1000 milli seconds is time interval
//
//                            public void onTick(long millisUntilFinished) {
//                                callTransactionDetails();
//                            }
//
//                            public void onFinish() {
//                                try {
//                                    showDialog("Try again");
//                                    if (progress.isShowing())
//                                        progress.dismiss();
//                                    countDownTimer.cancel();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        };
//                        countDownTimer.start();
//
//                    }
//                }
//                break;
//        }
//    }
//
//    JSONObject jsonObjectSale, jsonObjectCouponSale;
//    Intent intentCen = new Intent();
//    String packageName = "com.centerm.dynaminpayinskate";
//    String activityName = "org.skate.pay.component.PayEntry";
//    ComponentName comp = new ComponentName(packageName, activityName);
//    private static final int REQ_PAY_SALE = 100;
//
//    public void beginBussiness(String reference_id) {
//
//        hideSoftInput();
//
//        try {
//            jsonObjectSale = new JSONObject();
//            Calendar c = Calendar.getInstance();
//
//            if (preferenceManager.is_cnv_uni_display_and_add()) {
//                jsonObjectSale.put("amount", convenience_amount_unionpay + "");//Double.parseDouble(edt_amount.getText().toString().replace(",", "")));
//                jsonObjectSale.put("orderNumber", reference_id);//String.valueOf(c.getTimeInMillis()));
//                jsonObjectSale.put("needAppPrinted", false);
//            } else {
//                jsonObjectSale.put("amount", Double.parseDouble(edt_amount.getText().toString().replace(",", "")));
//                jsonObjectSale.put("orderNumber", reference_id);//String.valueOf(c.getTimeInMillis()));
//                jsonObjectSale.put("needAppPrinted", false);
//            }
//
//            //  doTransaction("SALE", jsonObject);
//
//
//            intentCen.setComponent(comp);
//            Bundle bundle = new Bundle();
//            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.SALE);
//            bundle.putDouble(ThirtConst.RequestTag.THIRD_PATH_TRANS_AMOUNT, jsonObjectSale.optDouble("amount"));
//            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObjectSale.optString("orderNumber"));
//            intentCen.putExtras(bundle);
//            TransactionType = ThirtConst.TransType.SALE;
//            startActivityForResult(intentCen, REQ_PAY_SALE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//
//    public void beginBussinessCoupon(String reference_id, String couponInformation) {
//
//        hideSoftInput();
//
//        try {
//            jsonObjectCouponSale = new JSONObject();
//            Calendar c = Calendar.getInstance();
//            if (preferenceManager.is_cnv_uni_display_and_add()) {
//                jsonObjectCouponSale.put("transactionType", "COUPON_SALE");
//                jsonObjectCouponSale.put("couponInformation", couponInformation);
//                jsonObjectCouponSale.put("amount", convenience_amount_unionpay + "");//Double.parseDouble(edt_amount.getText().toString().replace(",", "")));
//                jsonObjectCouponSale.put("orderNumber", reference_id);//String.valueOf(c.getTimeInMillis()));
//            } else {
//                jsonObjectCouponSale.put("transactionType", "COUPON_SALE");
//                jsonObjectCouponSale.put("couponInformation", couponInformation);
//                jsonObjectCouponSale.put("amount", Double.parseDouble(edt_amount.getText().toString().replace(",", "")));
//                jsonObjectCouponSale.put("orderNumber", reference_id);//String.valueOf(c.getTimeInMillis()));
//            }
//
//
//            //  doTransaction("COUPON_SALE", jsonObject);
//
//            intentCen.setComponent(comp);
//            Bundle bundle = new Bundle();
//            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.COUPON_SALE);
//            bundle.putDouble(ThirtConst.RequestTag.THIRD_PATH_TRANS_AMOUNT, jsonObjectCouponSale.optDouble("amount"));
//            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObjectCouponSale.optString("orderNumber"));
//            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_COUPON_INFO, jsonObjectCouponSale.optString("couponInformation"));
//            intentCen.putExtras(bundle);
//            TransactionType = ThirtConst.TransType.COUPON_SALE;
//            startActivityForResult(intentCen, REQ_PAY_SALE);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//
//    public void beginBussinessPreAuthorization(String reference_id, String couponInformation) {
//
//        hideSoftInput();
//
//        try {
//            jsonObjectSale = new JSONObject();
//            Calendar c = Calendar.getInstance();
//
//            // if (preferenceManager.isConvenienceFeeSelected()) {
//            if (preferenceManager.is_cnv_uni_display_and_add()) {
//                jsonObjectSale.put("transactionType", "SALE");
//                jsonObjectSale.put("qrcode", couponInformation);
//                jsonObjectSale.put("amount", convenience_amount_unionpay + "");//Double.parseDouble(edt_amount.getText().toString().replace(",", "")));
//                jsonObjectSale.put("orderNumber", reference_id);//String.valueOf(c.getTimeInMillis()));
//            } else {
//                jsonObjectSale.put("transactionType", "SALE");
//
//                jsonObjectSale.put("qrcode", couponInformation);
//                if (DashboardActivity.isExternalApp) {
//                    jsonObjectSale.put("amount", Double.parseDouble(preferenceManager.getupay_amount().replace(",", "")));
//                } else {
//                    jsonObjectSale.put("amount", Double.parseDouble(edt_amount.getText().toString().replace(",", "")));
//                }
//                jsonObjectSale.put("orderNumber", reference_id);//String.valueOf(c.getTimeInMillis()));
//            }
//
//            //  doTransaction("SALE", jsonObjectSaleQr);
//            intentCen.setComponent(comp);
//            Bundle bundle = new Bundle();
//            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.SALE);
//            bundle.putDouble(ThirtConst.RequestTag.THIRD_PATH_TRANS_AMOUNT, jsonObjectSale.optDouble("amount"));
//            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObjectSale.optString("orderNumber"));
//            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_SCAN_AUTH_PAY_CODE, jsonObjectSale.optString("qrcode"));
//            intentCen.putExtras(bundle);
//            TransactionType = ThirtConst.TransType.SALE;
//            startActivityForResult(intentCen, REQ_PAY_SALE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    private void doTransaction(String interfaceId, JSONObject jsonObject) {
//        if (TransactionNames.SALE_BY_SDK.name().equals(interfaceId)) {
//            vasCallsArkeBusiness.doTransaction(interfaceId, jsonObject, this);
//        } else {
//            vasCallsArkeBusiness.doTransaction(interfaceId, jsonObject, this);
//        }
//    }
//
//    private void hideSoftInput() {
//        if (getActivity().getCurrentFocus() != null) {
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
//        }
//    }
//
//
//    //**********************************************************************************************
//    //Start code added on 22-07-2019
//    //**********************************************************************************************
//    //Clone function of payQr only with additional signature functionality added
//    public void callTerminalPay() {
//        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
//        if (countDownTimerxmpp != null) {
//            countDownTimerxmpp.cancel();
//            tv_start_countdown.setVisibility(View.GONE);
//        }
//        openProgressDialog();
//        try {
//
//            DecimalFormat df = new DecimalFormat("#0.00");
//            if (MyPOSMateApplication.isOpen) {
//                char[] ch = xmppAmount.toCharArray();
//                StringBuilder sb = new StringBuilder();
//
//                for (int i = 0; i < ch.length; i++) {
//                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {
//
//                    } else {
//                        sb.append(ch[i]);
//                    }
//                }
//                xmppAmount = sb.toString().replace(",", "");
//
//
//                if (preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_alipay_display_and_add()) {
//                    original_amount = original_xmpp_trigger_amount;
//                    xmppAmount = convenience_amount_alipay + "";
//                    fee_amount = convenience_amount_alipay -
//                            Double.parseDouble(original_xmpp_trigger_amount.replace(",", ""))
//                            + "";
//                    fee_percentage = preferenceManager.getcnv_alipay();
//                    preferenceManager.setReference(edt_reference.getText().toString());
//
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", edt_reference.getText().toString());
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", reference_id);
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(xmppAmount)) + "");
//                    hashMapKeys.put("original_amount", original_amount);
//                    hashMapKeys.put("fee_amount", fee_amount);
//                    hashMapKeys.put("fee_percentage", fee_percentage);
//                    hashMapKeys.put("discount", "0");
////                    hashMapKeys.put("auth_code", auth_code);
//                    hashMapKeys.put("channel", channel);
//
//                    new OkHttpHandler(getActivity(), this, null, "paynow")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYNOW + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//
//
//                } else {
//                    preferenceManager.setReference(edt_reference.getText().toString());
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", edt_reference.getText().toString());
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", reference_id);
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(xmppAmount)) + "");
////                    hashMapKeys.put("auth_code", auth_code);
//                    hashMapKeys.put("channel", channel);
//
//                    new OkHttpHandler(getActivity(), this, null, "paynow")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYNOW + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//
//                }
//
//
//            } else {
//                String amount = "";
//                char[] ch = edt_amount.getText().toString().toCharArray();
//                StringBuilder sb = new StringBuilder();
//
//                for (int i = 0; i < ch.length; i++) {
//                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {
//
//                    } else {
//                        sb.append(ch[i]);
//                    }
//                }
//                amount = sb.toString().replace(",", "");
//                if (preferenceManager.isConvenienceFeeSelected() && preferenceManager.is_cnv_alipay_display_and_add()) {
//                    original_amount = amount;
//                    amount = convenience_amount_alipay + "";
//                    fee_amount = convenience_amount_alipay -
//                            Double.parseDouble(edt_amount.getText().toString().replace(",", ""))
//                            + "";
//                    fee_percentage = preferenceManager.getcnv_alipay();
//
//                    preferenceManager.setReference(edt_reference.getText().toString());
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", edt_reference.getText().toString());
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", reference_id);
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(amount)) + "");
//                    hashMapKeys.put("original_amount", original_amount);
//                    hashMapKeys.put("fee_amount", fee_amount);
//                    hashMapKeys.put("fee_percentage", fee_percentage);
//                    hashMapKeys.put("discount", "0");
////                    hashMapKeys.put("auth_code", auth_code);
//                    hashMapKeys.put("channel", channel);
//
//                    new OkHttpHandler(getActivity(), this, null, "paynow")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYNOW + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//
//                } else {
//                    preferenceManager.setReference(edt_reference.getText().toString());
//                    hashMapKeys.clear();
//                    if (!edt_reference.getText().toString().equals("")) {
//                        hashMapKeys.put("refData1", edt_reference.getText().toString());
//                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
//                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
//                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
//                    if (reference_id.isEmpty())
//                        reference_id = new Date().getTime() + "";
//                    hashMapKeys.put("reference_id", reference_id);
//                    hashMapKeys.put("random_str", new Date().getTime() + "");
//                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(amount)) + "");
////                    hashMapKeys.put("auth_code", auth_code);
//                    hashMapKeys.put("channel", channel);
//
//                    new OkHttpHandler(getActivity(), this, null, "paynow")
//                            .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.V2_PAYNOW + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private AidlQuickScanZbar aidlQuickScanService = null;
//    private int bestWidth = 640;
//    private int bestHeight = 480;
//    private int spinDegree = 90;
//    private int cameraDisplayEffect = 0;
//
//    private void switchCameraDisplayEffect(boolean cameraBack) {
//        try {
//            aidlQuickScanService.switchCameraDisplayEffect(cameraBack ? 0 : 1, cameraDisplayEffect);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void stsartFastScan(boolean cameraBack) {
//        final long startTime = System.currentTimeMillis();
//        try {
//            CameraBeanZbar cameraBean = new com.centerm.smartpos.aidl.qrscan.CameraBeanZbar(0, bestWidth, bestHeight, 4, Integer.MAX_VALUE, spinDegree, 1);
//            if (cameraBack) {
//                cameraBean.setCameraId(0);
//            } else {
//                cameraBean.setCameraId(1);
//            }
//            HashMap<String, Object> externalMap = new HashMap<String, Object>();
//            externalMap.put("ShowPreview", true);
//            cameraBean.setExternalMap(externalMap);
//            switchCameraDisplayEffect(cameraBack);//2018-03-06 增加切换摄像头显示效果 linpeita@centerm.com
//            aidlQuickScanService.scanQRCode(cameraBean, new AidlScanCallback.Stub() {
//                @Override
//                public void onFailed(int arg0) throws RemoteException {
//                    if (getActivity() != null)
//                        getActivity().runOnUiThread(new Runnable() {
//                            public void run() {
//                                tv_status_scan.setVisibility(View.VISIBLE);
//                                tv_status_scan.setText("Membership/Loyality could not be scanned");
//                                tv_status_scan_button.setText("Rescan Membership/Loyality");
//                                Toast.makeText(getActivity(), "Closed", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//
//                }
//
//                @Override
//                public void onCaptured(String arg0, int arg1) throws RemoteException {
//                    long SuccessEndTime = System.currentTimeMillis();
//                    long SuccessCostTime = SuccessEndTime - startTime;
//                    if (getActivity() != null)
//                        getActivity().runOnUiThread(new Runnable() {
//                            public void run() {
//
//                                if (preferenceManager.getLaneIdentifier().equals("")) {
//                                    Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
//                                } else if (preferenceManager.getPOSIdentifier().equals("")) {
//                                    Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
//                                } else {
//                                    callMembershipLoyality(arg0);
//                                    Toast.makeText(getActivity(), arg0 + "", Toast.LENGTH_SHORT).show();
//                                }
//
//                            }
//                        });
//
//
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public AidlDeviceManager manager = null;
//
//    Intent intentService;
//
//    public void bindService() {
//        intentService = new Intent();
//        intentService.setPackage("com.centerm.smartposservice");
//        intentService.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
//        getActivity().bindService(intentService, conn, Context.BIND_AUTO_CREATE);
//    }
//
//    /**
//     * 服务连接桥
//     */
//    public ServiceConnection conn = new ServiceConnection() {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            manager = null;
//            LogUtil.print(getResources().getString(R.string.bind_service_fail));
//            LogUtil.print("manager = " + manager);
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            manager = AidlDeviceManager.Stub.asInterface(service);
//            LogUtil.print(getResources().getString(R.string.bind_service_success));
//            LogUtil.print("manager = " + manager);
//            if (null != manager) {
//                try {
//                    onDeviceConnected(manager);
//                } catch (Exception e) {
//
//                }
//
//            }
//        }
//
//
//    };
//
//    public void onDeviceConnected(AidlDeviceManager deviceManager) {
//        try {
//            aidlQuickScanService = AidlQuickScanZbar.Stub.asInterface(deviceManager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_QUICKSCAN));
//        } catch (RemoteException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//}
