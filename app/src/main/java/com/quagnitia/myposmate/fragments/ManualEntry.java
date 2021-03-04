package com.quagnitia.myposmate.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.qrscan.AidlQuickScanZbar;
import com.centerm.smartpos.aidl.qrscan.AidlScanCallback;
import com.centerm.smartpos.aidl.qrscan.CameraBeanZbar;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.util.LogUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.quagnitia.myposmate.MyPOSMateApplication;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.arke.TransactionNames;
import com.quagnitia.myposmate.arke.VASCallsArkeBusiness;
import com.quagnitia.myposmate.centrum.ThirtConst;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.CurrencyEditText;
import com.quagnitia.myposmate.utils.HomeWatcher;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnHomePressedListener;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.content.Context.INPUT_METHOD_SERVICE;


public class ManualEntry extends Fragment implements View.OnClickListener, OnTaskCompleted {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PERMISSION_REQUEST_CODE =10;
    private ProgressDialog progress, progress1;
    private String mParam1;
    private String mParam2;

    //AlternateBarcodeScanner alternateBarcodeScanner;
    public static String pass_amount = "";
    private Button btn_cancel, btn_save1, btn_cancel1;
    private View view;
    private TextView tv_alipay_disabled, tv_unionpay_qr_disabled, tv_wechat_scan_disabled, tv_wechat_disabled, tv_scanqr_disabled, tv_uplan_disabled, tv_unionpay_disabled, tv_scan_uni_disabled;
    private LinearLayout ll_amount, ll_membership_loyalty_app, ll_reference, ll_amount1, ll_reference1;
    private CurrencyEditText edt_amount, edt_amount1;
    private EditText edt_reference, edt_reference1;
    private TextView tv_status_scan, tv_noitem, tv_wechat_qr_scan_cv, tv_unionpay_qr_cv, tv_enable_payment, edt_xmpp_amount, edt_xmpp_amount1, tv_alipay, tv_wechat;
    private RelativeLayout scanqr_unionpay, scanqr,rel_orders;
    private ImageView img_alipay, img_wechat, img_unipay, img_upay, img_unionpay_qr,img;
    public static int selected_screen = 0;
    private PreferencesManager preferenceManager;
    double convenience_amount_alipay = 0.0, convenience_amount_wechat = 0.0,
            convenience_amount_alipay_scan = 0.0, convenience_amount_wechat_scan = 0.0,
            convenience_amount_unionpay = 0.0,
            convenience_amount_poli = 0.0,
            convenience_amount_centrapay_merchant_qr_display = 0.0,
            convenience_amount_uplan = 0.0,
            convenience_amount_unionpayqrscan = 0.0,
            convenience_amount_unionpayqrdisplay = 0.0,
            convenience_amount_unionpayqr_merchant_display = 0.0;
    private String payment_mode = "", qrMode = "";
    private IntentFilter intentFilter;
    private AmountReceiver amountReceiver;
    public static String xmppAmount = "";
    public static String auth_code = "";
    private CountDownTimer countDownTimer;
    public String reference_id = "";
    private LinearLayout ll_one, ll_two;
    private LinearLayout ag_v2;
    private TextView tv_start_countdown;
    private LinearLayout rel_unionpay;
    private CountDownTimer countDownTimerxmpp;
    private RelativeLayout rel_alipay_static_qr;
    String amount = "";
    private IntentIntegrator qrScan;
    static boolean shadaf = false;
    EditText tv_uni_cv, tv_uni_cv1_uplan, tv_uni_cv2_scan_qr, tv_ali_cv, tv_ali_cv1;
    LinearLayout tv_ali_cv2;
    private static String arkeAppPackageName = "com.arke.hk_dp";
    private static String sdkAppPackageName = "com.arke.sdk.demo";
    public static Context context;
    private TransactionNames currenTransaction;
    private VASCallsArkeBusiness vasCallsArkeBusiness;
    HomeWatcher mHomeWatcher;
    TreeMap<String, String> hashMapKeys;
    private Button btn_back, btn_front, tv_status_scan_button;
    private RelativeLayout rel_membership;
    TextView tv_status_scan_button1, tv_status_scan_button2;
    Button btn_back1;
    Button btn_front1;
    Button btn_loyalty_apps;
    ImageView img_poli, img_centrapay_merchant_qr_display, img_centerpay_scanqr;
    EditText edt_poli_cnv;
    TextView tv_poli_disabled;
    TextView tv_centrapay_merchant_qr_disabled;
    EditText edt_centerpay_mr_qr_cnv;
    public static boolean isLoyaltyQrSelected=false;
    public static boolean isLoyaltyFrontQrSelected=false;


    public static ManualEntry newInstance(String param1, String param2) {
        ManualEntry fragment = new ManualEntry();
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

    public void openProgressDialog() {
        progress = new ProgressDialog(mmContext == null ? getActivity() : mmContext);
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    ProgressDialog progress2;

    public void openProgressDialog1() {
        progress2 = new ProgressDialog(mmContext == null ? getActivity() : mmContext);
        progress2.setMessage("Please wait while processing transaction");
        progress2.setCancelable(false);
        progress2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress2.setIndeterminate(true);
        progress2.show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        funcOnCreateViewCall(inflater, container, savedInstanceState);

        return view;
    }




    public void funcOnCreateViewCall(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
        funcBeforeUIRender();
        view = inflater.inflate(R.layout.demo, container, false);
        funcInitials();
        funcAfterUIRender();
    }

    public void funcAfterUIRender() {
        callAuthToken();
        initUI(view);
        initListener();
        funcConditionalSwitches(view);
        funcPaymentChoicesUISwitch();
        funcDisabledUISwitch();
         if (tv_unionpay_qr_cv.getVisibility() == View.INVISIBLE && edt_poli_cnv.getVisibility() == View.INVISIBLE && edt_centerpay_mr_qr_cnv.getVisibility() == View.INVISIBLE) {
        tv_unionpay_qr_cv.setVisibility(View.GONE);
        edt_poli_cnv.setVisibility(View.GONE);
        edt_centerpay_mr_qr_cnv.setVisibility(View.GONE);
        }

//        if (preferenceManager.isAlipayScan() && preferenceManager.isWeChatScan()) {
//            scanqr.setText(getActivity().getResources().getString(R.string.click_toaw));
//        } else if (preferenceManager.isAlipayScan()) {
//            scanqr.setText("Click To Scan \nAlipay");
//        } else if (preferenceManager.isWeChatScan()) {
//            scanqr.setText("Click To Scan \nWeChat");
//        }


    }

    public void centrapay() {
        if (!preferenceManager.isCentrapayMerchantQRDisplaySelected()) {
            //centrapay is deselected
            tv_centrapay_merchant_qr_disabled.setVisibility(View.VISIBLE);
            img_centrapay_merchant_qr_display.setVisibility(View.GONE);
            edt_centerpay_mr_qr_cnv.setVisibility(View.INVISIBLE);
        } else {
            //poli is selected
            tv_centrapay_merchant_qr_disabled.setVisibility(View.GONE);
            img_centrapay_merchant_qr_display.setVisibility(View.VISIBLE);
            if (preferenceManager.is_cnv_centrapay_display_and_add() ||
                    preferenceManager.is_cnv_centrapay_display_only()) {
                edt_centerpay_mr_qr_cnv.setVisibility(View.VISIBLE);
            } else {
                edt_centerpay_mr_qr_cnv.setVisibility(View.INVISIBLE);
            }
        }

    }

    public void funcDisabledUISwitch() {
        tv_centrapay_merchant_qr_disabled = view.findViewById(R.id.tv_centrapay_merchant_qr_disabled);
        tv_uplan_disabled = view.findViewById(R.id.tv_uplan_disabled);
        tv_unionpay_disabled = view.findViewById(R.id.tv_unionpay_disabled);
        tv_scan_uni_disabled = view.findViewById(R.id.tv_scan_uni_disabled);
        tv_alipay_disabled = view.findViewById(R.id.tv_alipay_disabled);
        tv_wechat_disabled = view.findViewById(R.id.tv_wechat_disabled);
        tv_scanqr_disabled = view.findViewById(R.id.tv_scanqr_disabled);
        tv_unionpay_qr_disabled = view.findViewById(R.id.tv_unionpay_qr_disabled);
        tv_poli_disabled = view.findViewById(R.id.tv_poli_disabled);

        centrapay();
        if (!preferenceManager.isPoliSelected()) {
            //poli is deselected
            tv_poli_disabled.setVisibility(View.VISIBLE);
            img_poli.setVisibility(View.GONE);
            edt_poli_cnv.setVisibility(View.INVISIBLE);
        } else {
            //poli is selected
            tv_poli_disabled.setVisibility(View.GONE);
            img_poli.setVisibility(View.VISIBLE);
            if (preferenceManager.is_cnv_poli_display_and_add() ||
                    preferenceManager.is_cnv_poli_display_only()) {
                edt_poli_cnv.setVisibility(View.VISIBLE);
            } else {
                edt_poli_cnv.setVisibility(View.INVISIBLE);
            }
        }


        if (!preferenceManager.isUnionPaySelected()) {
            tv_unionpay_disabled.setVisibility(View.VISIBLE);
        } else {
            tv_unionpay_disabled.setVisibility(View.GONE);
        }

        if (!preferenceManager.isUplanSelected()) {
            tv_uplan_disabled.setVisibility(View.VISIBLE);
        } else {
            tv_uplan_disabled.setVisibility(View.GONE);
        }

        if (!preferenceManager.isUnionPayQrSelected() && !preferenceManager.isUnionPayQrCodeDisplaySelected()) {
            tv_scan_uni_disabled.setVisibility(View.VISIBLE);
        } else {
            tv_scan_uni_disabled.setVisibility(View.GONE);
        }

        if (!preferenceManager.isAlipaySelected()) {
            tv_alipay_disabled.setVisibility(View.VISIBLE);
        } else {
            tv_alipay_disabled.setVisibility(View.GONE);
        }

        if (!preferenceManager.isWechatSelected()) {
            tv_wechat_disabled.setVisibility(View.VISIBLE);
        } else {
            tv_wechat_disabled.setVisibility(View.GONE);
        }

        if (!preferenceManager.isAlipayScan() && !preferenceManager.isWeChatScan()) {
            tv_scanqr_disabled.setVisibility(View.VISIBLE);
//            tv_wechat_scan_disabled.setVisibility(View.VISIBLE);
        } else {
            tv_scanqr_disabled.setVisibility(View.GONE);
//            tv_wechat_scan_disabled.setVisibility(View.GONE);
        }

        if (!preferenceManager.isAlipaySelected() && !preferenceManager.isWechatSelected()) {
            tv_ali_cv.setVisibility(View.INVISIBLE);
            tv_ali_cv1.setVisibility(View.INVISIBLE);
        } else if (!preferenceManager.isAlipaySelected()) {
            tv_ali_cv.setVisibility(View.INVISIBLE);
        } else if (!preferenceManager.isWechatSelected()) {
            tv_ali_cv1.setVisibility(View.INVISIBLE);
        }


//        if (!preferenceManager.isAlipayScan()) {
//            tv_scanqr_disabled.setVisibility(View.VISIBLE);
//        } else {
//            tv_scanqr_disabled.setVisibility(View.GONE);
//        }

//        if (!preferenceManager.isWeChatScan()) {
//            tv_wechat_scan_disabled.setVisibility(View.VISIBLE);
//        } else {
//            tv_wechat_scan_disabled.setVisibility(View.GONE);
//        }

        if (!preferenceManager.isMerchantDPARDisplay()) {
            tv_unionpay_qr_disabled.setVisibility(View.VISIBLE);
        } else {
            tv_unionpay_qr_disabled.setVisibility(View.GONE);
        }

//        if (!preferenceManager.cnv_unionpayqr_display_only() &&
//                !preferenceManager.cnv_unionpayqr_display_and_add()) {
//            tv_unionpay_qr_cv.setVisibility(View.INVISIBLE);
//        }
        if (!preferenceManager.isMerchantDPARDisplay()) {
            tv_unionpay_qr_cv.setVisibility(View.INVISIBLE);
        } else {
            tv_unionpay_qr_cv.setText("0.00");
        }

        if (!preferenceManager.is_cnv_alipay_display_and_add() &&
                !preferenceManager.is_cnv_alipay_display_only() &&
                !preferenceManager.is_cnv_wechat_display_and_add() &&
                !preferenceManager.is_cnv_wechat_display_only()
        ) {
            tv_ali_cv.setVisibility(View.GONE);
            tv_ali_cv1.setVisibility(View.GONE);
            tv_ali_cv2.setVisibility(View.GONE);
//            tv_wechat_qr_scan_cv.setVisibility(View.INVISIBLE);
        }


        if ((!preferenceManager.is_cnv_uni_display_and_add() &&
                !preferenceManager.is_cnv_uni_display_only())
                && (!preferenceManager.cnv_uplan_display_and_add() && !preferenceManager.cnv_uplan_display_only())
                && ((!preferenceManager.cnv_unionpayqr_display_and_add() && !preferenceManager.cnv_unionpayqr_display_only())
                || !preferenceManager.isUnionPayQrSelected())
                && ((!preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add() && !preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only())
                || !preferenceManager.isUnionPayQrCodeDisplaySelected())
        ) {
            tv_uni_cv.setVisibility(View.GONE);
            tv_uni_cv1_uplan.setVisibility(View.GONE);
            tv_uni_cv2_scan_qr.setVisibility(View.GONE);
        }

      /*  if ((!preferenceManager.isAlipayWechatQrSelected() &&
                !preferenceManager.is_cnv_uni_display_only())
                && (!preferenceManager.cnv_uplan_display_and_add() && !preferenceManager.cnv_uplan_display_only())
                && ((!preferenceManager.cnv_unionpayqr_display_and_add() && !preferenceManager.cnv_unionpayqr_display_only())
                || !preferenceManager.isUnionPayQrSelected())
                && ((!preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add() && !preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only())
                || !preferenceManager.isUnionPayQrCodeDisplaySelected())
        ) {
            tv_uni_cv.setVisibility(View.GONE);
            tv_uni_cv1_uplan.setVisibility(View.GONE);
            tv_uni_cv2_scan_qr.setVisibility(View.GONE);
        }*/


        if ((!preferenceManager.isAlipaySelected() && preferenceManager.isAlipayScan())
                && (preferenceManager.is_cnv_alipay_display_and_add() || preferenceManager.is_cnv_alipay_display_only())) {
            img_alipay.setVisibility(View.INVISIBLE);
            scanqr.setVisibility(View.VISIBLE);
            tv_ali_cv.setVisibility(View.VISIBLE);
            tv_ali_cv2.setVisibility(View.VISIBLE);
        }

        if ((!preferenceManager.isWechatSelected() && preferenceManager.isWeChatScan())
                && (preferenceManager.is_cnv_wechat_display_and_add() || preferenceManager.is_cnv_wechat_display_only())) {
            img_wechat.setVisibility(View.INVISIBLE);
            tv_ali_cv1.setVisibility(View.VISIBLE);

            scanqr.setVisibility(View.VISIBLE);
            tv_ali_cv2.setVisibility(View.VISIBLE);
//            tv_wechat_qr_scan_cv.setVisibility(View.VISIBLE);
        }


    }


    public void funcPaymentChoicesUISwitch() {

        if (preferenceManager.isMerchantDPARDisplay()) {
            img_unionpay_qr.setVisibility(View.VISIBLE);
        } else {
            img_unionpay_qr.setVisibility(View.INVISIBLE);
        }

        if (preferenceManager.isUnionPayQrCodeDisplaySelected() || preferenceManager.isUnionPayQrSelected()) {
            scanqr_unionpay.setVisibility(View.VISIBLE);
        } else {
            scanqr_unionpay.setVisibility(View.INVISIBLE);
        }


        //This if condition is used for UnionPayQr display at merchant end
        //and the qr scan feature of the union pay app.So here the convinience fee 
        //field is common for both these options
        if (preferenceManager.cnv_unionpayqr_display_and_add() ||
                preferenceManager.cnv_unionpayqr_display_only() ||
                preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only() ||
                preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()
        ) {
            if ((preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only() ||
                    preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) && preferenceManager.isUnionPayQrCodeDisplaySelected()) {
                tv_uni_cv2_scan_qr.setVisibility(View.VISIBLE);
                tv_uni_cv2_scan_qr.setText(preferenceManager.getcnv_uniqr());
            } else if ((preferenceManager.cnv_unionpayqr_display_and_add() ||
                    preferenceManager.cnv_unionpayqr_display_only()) && preferenceManager.isUnionPayQrSelected()) {
                tv_uni_cv2_scan_qr.setVisibility(View.VISIBLE);
                tv_uni_cv2_scan_qr.setText(preferenceManager.getcnv_uniqr());
            } else {
                tv_uni_cv2_scan_qr.setVisibility(View.INVISIBLE);
                tv_uni_cv2_scan_qr.setText("0.00");
            }

            if ((preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only() ||
                    preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) && preferenceManager.isMerchantDPARDisplay()) {
                tv_unionpay_qr_cv.setVisibility(View.VISIBLE);
                tv_unionpay_qr_cv.setText(preferenceManager.get_cnv_unimerchantqrdisplayLower());
            } else if ((!preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only() ||
                    !preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) && preferenceManager.isMerchantDPARDisplay()) {
                tv_unionpay_qr_cv.setVisibility(View.INVISIBLE);
                tv_unionpay_qr_cv.setText("0.00");
            }


        } else {
            tv_uni_cv2_scan_qr.setVisibility(View.INVISIBLE);
            tv_unionpay_qr_cv.setVisibility(View.INVISIBLE);
            tv_unionpay_qr_cv.setText("0.00");
            tv_uni_cv2_scan_qr.setText("0.00");
        }


        if ((preferenceManager.cnv_uplan_display_and_add() ||
                preferenceManager.cnv_uplan_display_only()) &&
                preferenceManager.isUplanSelected()) {
            tv_uni_cv1_uplan.setVisibility(View.VISIBLE);
            tv_uni_cv1_uplan.setText(preferenceManager.getcnv_uplan());
        } else {
            tv_uni_cv1_uplan.setVisibility(View.INVISIBLE);
            tv_uni_cv1_uplan.setText("0.00");
        }


        if ((preferenceManager.is_cnv_uni_display_and_add() ||
                preferenceManager.is_cnv_uni_display_only()) &&
                preferenceManager.isUnionPaySelected()
        ) {
            tv_uni_cv.setText(preferenceManager.getcnv_uni());
            tv_uni_cv.setVisibility(View.VISIBLE);
        } else {
            tv_uni_cv.setText("0.00");
            tv_uni_cv.setVisibility(View.INVISIBLE);
        }

       // if (tv_unionpay_qr_cv.getVisibility() == View.INVISIBLE && edt_poli_cnv.getVisibility() == View.INVISIBLE && edt_centerpay_mr_qr_cnv.getVisibility() == View.INVISIBLE) {
           /* tv_unionpay_qr_cv.setVisibility(View.GONE);
            edt_poli_cnv.setVisibility(View.GONE);
            edt_centerpay_mr_qr_cnv.setVisibility(View.GONE);*/
       // }
    }


    public void funcBeforeUIRender() {
        if (mParam1.equalsIgnoreCase("Scan")) {
            payment_mode = "";
            qrMode = "False";
            open();
        }

        hashMapKeys = new TreeMap<>();
        selected_screen = 0;
    }

    public void funcInitials() {
        if (DashboardActivity.isExternalApp) {
            view.findViewById(R.id.rel_loyalty).setVisibility(View.GONE);
        }


        preferenceManager = PreferencesManager.getInstance(getActivity());
        if (!preferenceManager.getreference_id().equals("")) {
            reference_id = "";
            preferenceManager.setreference_id("");

        }
        preferenceManager.setisResetTerminal(false);
        preferenceManager.settriggerReferenceId("");
        amountReceiver = new AmountReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("AmountTrigger");
        intentFilter.addAction("ThirdPartyAppTrigger");
        intentFilter.addAction("ScannedCode");
        intentFilter.addAction("ScannedCode1");
        intentFilter.addAction("ScannedCodeUnionPayQr");
        intentFilter.addAction("PaymentExpressSuccess");
        intentFilter.addAction("PaymentExpressFailure");
        intentFilter.addAction("ScannedBackLoyaltyQr");
        getActivity().registerReceiver(amountReceiver, intentFilter);
        bindService();
    }

    public void showMainUIOnTrigger() {
        if (getArguments() != null) {
            if (getArguments().getString(ARG_PARAM1).equals("xmpp")) {
                if (preferenceManager.isaggregated_singleqr() && !preferenceManager.isUnipaySelected()) {
                    view.findViewById(R.id.mainui).setVisibility(View.VISIBLE);

                } else if (preferenceManager.isaggregated_singleqr() && preferenceManager.isUnipaySelected()) {
                    view.findViewById(R.id.mainui).setVisibility(View.VISIBLE);

                } else if (preferenceManager.isUnipaySelected() && !preferenceManager.isaggregated_singleqr()) {
//                    view.findViewById(R.id.mainui).setVisibility(View.GONE);
                    view.findViewById(R.id.mainui).setVisibility(View.VISIBLE);
                }

                if (preferenceManager.isAlipaySelected() ||
                        preferenceManager.isAlipayScan() ||
                        preferenceManager.isWechatSelected() ||
                        preferenceManager.isWeChatScan() ||
                        preferenceManager.isUnionPaySelected() ||
                        preferenceManager.isUplanSelected() ||
                        preferenceManager.isUnionPayQrSelected() ||
                        preferenceManager.isUnionPayQrCodeDisplaySelected() ||
                        preferenceManager.isCentrapayMerchantQRDisplaySelected() ||
                        preferenceManager.isPoliSelected()
                ) {
                    view.findViewById(R.id.mainui).setVisibility(View.VISIBLE);
                }
                else
                {
                    view.findViewById(R.id.mainui).setVisibility(View.GONE);
                }


            }
        }

    }


    public void funcConditionalSwitches(View view) {
        if (getArguments() != null) {
            if (getArguments().getString(ARG_PARAM1).equals("xmpp")) {
                if (preferenceManager.isaggregated_singleqr() && !preferenceManager.isUnipaySelected()) {
                    view.findViewById(R.id.mainui).setVisibility(View.VISIBLE);

                } else if (preferenceManager.isaggregated_singleqr() && preferenceManager.isUnipaySelected()) {
                    view.findViewById(R.id.mainui).setVisibility(View.VISIBLE);

                } else if (preferenceManager.isUnipaySelected() && !preferenceManager.isaggregated_singleqr()) {
//                    view.findViewById(R.id.mainui).setVisibility(View.GONE);
                    view.findViewById(R.id.mainui).setVisibility(View.VISIBLE);
                }
            }
        }
        showMainUIOnTrigger();

        if (preferenceManager.getshowReference().equals("true")) {
            view.findViewById(R.id.tv_reference).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tv_reference1).setVisibility(View.VISIBLE);
            edt_reference.setVisibility(View.VISIBLE);
            edt_reference1.setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.tv_reference).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.tv_reference1).setVisibility(View.INVISIBLE);
            edt_reference.setText("");
            edt_reference1.setText("");
            edt_reference.setVisibility(View.INVISIBLE);
            edt_reference1.setVisibility(View.INVISIBLE);
        }

        funcUIEnablingDisabling();

    }


    public void funcUIEnablingDisabling() {
        if (preferenceManager.isUnionPaySelected()
                && preferenceManager.isUplanSelected()
                && preferenceManager.isUnionPayQrSelected()) {
            img_unipay.setVisibility(View.VISIBLE);
            img_upay.setVisibility(View.VISIBLE);
            scanqr_unionpay.setVisibility(View.VISIBLE);
        } else if (preferenceManager.isUnionPaySelected() && !preferenceManager.isUplanSelected() && preferenceManager.isUnionPayQrSelected()) {
            img_unipay.setVisibility(View.VISIBLE);
            img_upay.setVisibility(View.INVISIBLE);
            scanqr_unionpay.setVisibility(View.VISIBLE);
        } else if (preferenceManager.isUnionPaySelected() && !preferenceManager.isUplanSelected() && !preferenceManager.isUnionPayQrSelected()) {
            img_unipay.setVisibility(View.VISIBLE);
            img_upay.setVisibility(View.INVISIBLE);
            scanqr_unionpay.setVisibility(View.INVISIBLE);
        } else if (!preferenceManager.isUnionPaySelected() && preferenceManager.isUplanSelected() && preferenceManager.isUnionPayQrSelected()) {
            img_unipay.setVisibility(View.INVISIBLE);
            img_upay.setVisibility(View.VISIBLE);
            scanqr_unionpay.setVisibility(View.VISIBLE);
        } else if (!preferenceManager.isUnionPaySelected() && !preferenceManager.isUplanSelected() && preferenceManager.isUnionPayQrSelected()) {
            img_unipay.setVisibility(View.INVISIBLE);
            img_upay.setVisibility(View.INVISIBLE);
            scanqr_unionpay.setVisibility(View.VISIBLE);
        } else if (preferenceManager.isUnionPaySelected() && preferenceManager.isUplanSelected() && !preferenceManager.isUnionPayQrSelected()) {
            img_unipay.setVisibility(View.VISIBLE);
            img_upay.setVisibility(View.VISIBLE);
            scanqr_unionpay.setVisibility(View.INVISIBLE);
        } else if (!preferenceManager.isUnionPaySelected() && preferenceManager.isUplanSelected() && !preferenceManager.isUnionPayQrSelected()) {
            img_unipay.setVisibility(View.INVISIBLE);
            img_upay.setVisibility(View.VISIBLE);
            scanqr_unionpay.setVisibility(View.INVISIBLE);
        }


        if ((preferenceManager.isAlipaySelected() && preferenceManager.isAlipayScan())
                && (preferenceManager.is_cnv_alipay_display_and_add() || preferenceManager.is_cnv_alipay_display_only())) {
            img_alipay.setVisibility(View.VISIBLE);
            scanqr.setVisibility(View.VISIBLE);
            tv_ali_cv.setVisibility(View.VISIBLE);
            tv_ali_cv2.setVisibility(View.VISIBLE);
        }


        if ((preferenceManager.isWechatSelected() && preferenceManager.isWeChatScan())
                && (preferenceManager.is_cnv_wechat_display_and_add() || preferenceManager.is_cnv_wechat_display_only())) {
            img_wechat.setVisibility(View.VISIBLE);
            tv_ali_cv1.setVisibility(View.VISIBLE);

            scanqr.setVisibility(View.VISIBLE);
            tv_ali_cv2.setVisibility(View.VISIBLE);
//            tv_wechat_qr_scan_cv.setVisibility(View.VISIBLE);
        }


        view.findViewById(R.id.activity_main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (((DashboardActivity) getActivity()).mPopupWindow.isShowing()) {
                    ((DashboardActivity) getActivity()).mPopupWindow.dismiss();
                }
                return false;
            }
        });


        mHomeWatcher = new HomeWatcher(getActivity());
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                AppConstants.isDeviceHomePressed = true;
                if (DashboardActivity.isExternalApp) {
                    isTransactionDone = true;
                    DashboardActivity.isExternalApp = false;
                    int REQ_PAY_SALE = 100;
                    getActivity().getIntent().putExtra("result", new JSONObject().toString());
                    getActivity().setResult(REQ_PAY_SALE, getActivity().getIntent());
                    getActivity().finishAndRemoveTask();
                }
            }

            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();


        if (preferenceManager.isStaticQR()) {
            rel_alipay_static_qr.setVisibility(View.VISIBLE);
        } else {
            rel_alipay_static_qr.setVisibility(View.GONE);
        }

    }


    @Override
    public void onDestroy() {
        if (!edt_amount.getText().toString().equals(""))
            pass_amount = edt_amount.getText().toString();
        super.onDestroy();
        if (countDownTimerxmpp != null)
            countDownTimerxmpp.cancel();
        if (countDownTimer != null)
            countDownTimer.cancel();
        getActivity().unregisterReceiver(amountReceiver);
        mHomeWatcher.stopWatch();
        if (conn != null) {
            getActivity().unbindService(conn);
        }
        getActivity().stopService(intentService);
    }

    String roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        return twoDForm.format(d);
    }


    public void startCountDownTimer() {

        if (!DashboardActivity.isExternalApp) {
            countDownTimerxmpp = new CountDownTimer(75000, 1000) {

                public void onTick(long millisUntilFinished) {
                    tv_start_countdown.setVisibility(View.VISIBLE);
                    tv_start_countdown.setText("Your session will be closed within " + millisUntilFinished / 1000 + " seconds");
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {

                    if (DashboardActivity.isExternalApp) {
                        isTransactionDone = true;
                        TransactionDetailsActivity.isReturnFromTransactionDetails = false;
                        try {

                            //added for external apps 12/5/2019
                            int REQ_PAY_SALE = 100;
                            DashboardActivity.isExternalApp = false;
                            if (getActivity() != null) {
                                if (getActivity().getIntent() != null) {
                                    getActivity().getIntent().putExtra("result", new JSONObject().toString());
                                    getActivity().setResult(REQ_PAY_SALE, getActivity().getIntent());
                                    getActivity().finishAndRemoveTask();
                                }

                            }
                            return;
                            //added for external apps
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    tv_start_countdown.setVisibility(View.GONE);
                    MyPOSMateApplication.isOpen = false;
                    AppConstants.xmppamountforscan = "";
                    if (isUpayselected) {
                        isUpayselected = false;
                    } else {
//                        if (preferenceManager.isManual()) {
//                            ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
//                        } else {
//                            ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
//                        }
                    }
                    isTriggerCancelled = true;
                    if (isTriggerCancelled) {
                        isTrigger = false;
                        callAuthToken();
                    }

                }

            }.start();

        }
    }

    String original_xmpp_trigger_amount = "";
    Context mmContext = null;
    public static String requestId = "";
    public static boolean isTrigger = false;

    public class AmountReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ac = intent.getAction();
            mmContext = context;
            switch (ac) {


                case "AmountTrigger":
                    try {
                        isTrigger = true;
                        //   callAuthToken();

                        MyPOSMateApplication.isOpen = true;
                        funcAfterUIRender();
                        edt_xmpp_amount.setVisibility(View.VISIBLE);
                        ll_amount.setVisibility(View.GONE);
                        ll_reference.setVisibility(View.GONE);
                        ll_amount1.setVisibility(View.GONE);
                        ll_reference1.setVisibility(View.GONE);
                        rel_alipay_static_qr.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(intent.getStringExtra("data"));
                        if (jsonObject.has("referenceId")) {
                            reference_id = jsonObject.optString("referenceId");
                            preferenceManager.setreference_id(reference_id);
                            //5 dec 2018
                            preferenceManager.settriggerReferenceId(reference_id);
                            //-----------
                        } else {
                            reference_id = "";
                            preferenceManager.setreference_id("");
                            preferenceManager.settriggerReferenceId("");
                        }
                        edt_xmpp_amount.setText(preferenceManager.getcurrency() + " $" + roundTwoDecimals(Float.valueOf(jsonObject.optString("amount"))));
                        edt_amount.setText(roundTwoDecimals(Float.valueOf(jsonObject.optString("amount"))) + "");
                        edt_reference.setText(jsonObject.optString("reference"));
                        xmppAmount = jsonObject.optString("amount");
                        requestId = jsonObject.optString("request_id");

                        preferenceManager.setupay_amount(xmppAmount);
                        original_xmpp_trigger_amount = jsonObject.optString("amount");


                        callAllConvinenceFeeCalculations();


                        if (!jsonObject.optString("reference").equals("null") &&
                                !jsonObject.optString("reference").equals("")
                        ) {
                            preferenceManager.setReference(jsonObject.optString("reference"));
                        } else {
                            preferenceManager.setReference("");
                        }

                        if (!preferenceManager.isAlipaySelected() &&
                                !preferenceManager.isWechatSelected() &&
                                !preferenceManager.isUnionPaySelected() &&
                                !preferenceManager.isUnionPayQrCodeDisplaySelected() &&
                                !preferenceManager.isUnionPayQrSelected() &&
                                !preferenceManager.isUplanSelected()
                        ) {
                            tv_start_countdown.setVisibility(View.GONE);
                        } else {
                            tv_start_countdown.setVisibility(View.VISIBLE);
                            if (AppConstants.xmppamountforscan.equals(""))
                                startCountDownTimer();
                        }


                        AppConstants.xmppamountforscan = jsonObject.optString("amount");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "ScannedCode":
                    if (intent.hasExtra("identityCode")) {
                        auth_code = intent.getStringExtra("identityCode");
                        payment_mode = "nochannel";
                        qrMode = "False";
                        edt_amount.setText(preferenceManager.getupay_amount());
                        if (MyPOSMateApplication.isOpen) {
                            if (preferenceManager.isConvenienceFeeSelected()) {
                                if (ManualEntry.isUpayselected)
                                    convenience_amount_uplan = Double.parseDouble(preferenceManager.getupay_amount());
                                else
                                    convenience_amount_unionpay = Double.parseDouble(preferenceManager.getupay_amount());
                            } else {
                                edt_amount.setText(preferenceManager.getupay_amount());
                            }
                            beginBussinessCoupon(preferenceManager.getupay_reference_id(), auth_code);

                        } else {
                            if (edt_amount.getText().toString().equals("") || edt_amount.getText().toString().equals("0.00")) {
                                Toast.makeText(getActivity(), "Please enter the amount.", Toast.LENGTH_LONG).show();
                            } else {
                                beginBussinessCoupon(preferenceManager.getupay_reference_id(), auth_code);
                            }

                        }

                    }
                    break;

                case "ScannedCodeUnionPayQr":
                    if (intent.hasExtra("identityCode")) {
                        auth_code = intent.getStringExtra("identityCode");
                        payment_mode = "nochannel";
                        qrMode = "False";
                        edt_amount.setText(preferenceManager.getupay_amount());

                        if (unionpay_payment_option.equals("UPI-QRScan")) {
                            if (MyPOSMateApplication.isOpen) {
                                if (preferenceManager.isConvenienceFeeSelected()) {
                                    convenience_amount_unionpayqrdisplay = Double.parseDouble(preferenceManager.getupay_amount());
                                } else {
                                    edt_amount.setText(preferenceManager.getupay_amount());
                                }
                                callUnionPayUPIQRScan();

                            } else {
                                if (edt_amount.getText().toString().equals("") || edt_amount.getText().toString().equals("0.00")) {
                                    Toast.makeText(getActivity(), "Please enter the amount.", Toast.LENGTH_LONG).show();
                                } else {
                                    callUnionPayUPIQRScan();
                                }

                            }
                        } else {
                            if (MyPOSMateApplication.isOpen) {
                                if (preferenceManager.isConvenienceFeeSelected()) {
                                    convenience_amount_unionpayqrdisplay = Double.parseDouble(preferenceManager.getupay_amount());
                                } else {
                                    edt_amount.setText(preferenceManager.getupay_amount());
                                }
                                beginBussinessPreAuthorization(preferenceManager.getupay_reference_id(), auth_code);
                                //callQRCode();

                            } else {
                                if (edt_amount.getText().toString().equals("") || edt_amount.getText().toString().equals("0.00")) {
                                    Toast.makeText(getActivity(), "Please enter the amount.", Toast.LENGTH_LONG).show();
                                } else {
                                    //    callQRCode();
                                    beginBussinessPreAuthorization(preferenceManager.getupay_reference_id(), auth_code);
                                }

                            }
                        }


                    }
                    break;


                case "ScannedCode1":

                    if (intent.hasExtra("identityCode")) {
                        auth_code = intent.getStringExtra("identityCode");
                        char c[] = auth_code.toCharArray();
                        String str_authcode = String.valueOf(c[0]) + String.valueOf(c[1]) + "";
                        Log.v("AUTHCODE","aurCode "+auth_code+" Code"+str_authcode);
                        payment_mode = "nochannel";
                        qrMode = "False";
                        openProgressDialog1();
                        if (MyPOSMateApplication.isOpen) {
                            if (str_authcode.equals("10") ||
                                    str_authcode.equals("11") ||
                                    str_authcode.equals("12") ||
                                    str_authcode.equals("13") ||
                                    str_authcode.equals("14") ||
                                    str_authcode.equals("15")) {
                                selected_channel = "WECHAT";
                                callPayNowWeChat();
                            } else if (str_authcode.equals("25") ||
                                    str_authcode.equals("26") ||
                                    str_authcode.equals("27") ||
                                    str_authcode.equals("28") ||
                                    str_authcode.equals("29") ||
                                    str_authcode.equals("30")) {
                                selected_channel = "ALIPAY";
                                callPayNowAlipay();

                            } else {
                                callRetryScanDialog(auth_code);
                            }
//                            if (selected_channel.equals("ALIPAY"))
//                                callPayNowAlipay();
//                            else if (selected_channel.equals("WECHAT"))
//                                callPayNowWeChat();

                        } else {
                            if (edt_amount.getText().toString().equals("") || edt_amount.getText().toString().equals("0.00")) {
                                if (!AppConstants.xmppamountforscan.equals("")) {
                                    MyPOSMateApplication.isOpen = true;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (str_authcode.equals("10") ||
                                                    str_authcode.equals("11") ||
                                                    str_authcode.equals("12") ||
                                                    str_authcode.equals("13") ||
                                                    str_authcode.equals("14") ||
                                                    str_authcode.equals("15")) {
                                                selected_channel = "WECHAT";
                                                callPayNowWeChat();
                                            } else if (str_authcode.equals("25") ||
                                                    str_authcode.equals("26") ||
                                                    str_authcode.equals("27") ||
                                                    str_authcode.equals("28") ||
                                                    str_authcode.equals("29") ||
                                                    str_authcode.equals("30")) {
                                                selected_channel = "ALIPAY";
                                                callPayNowAlipay();

                                            } else {
                                                callRetryScanDialog(auth_code);
                                            }

//                                            if (selected_channel.equals("ALIPAY"))
//                                                callPayNowAlipay();
//                                            else if (selected_channel.equals("WECHAT"))
//                                                callPayNowWeChat();
                                        }
                                    }, 500);


                                } else
                                    Toast.makeText(getActivity(), "Please enter the amount.", Toast.LENGTH_LONG).show();
                            } else {


                                if (str_authcode.equals("10") ||
                                        str_authcode.equals("11") ||
                                        str_authcode.equals("12") ||
                                        str_authcode.equals("13") ||
                                        str_authcode.equals("14") ||
                                        str_authcode.equals("15")) {
                                    selected_channel = "WECHAT";
                                    callPayNowWeChat();
                                } else if (str_authcode.equals("25") ||
                                        str_authcode.equals("26") ||
                                        str_authcode.equals("27") ||
                                        str_authcode.equals("28") ||
                                        str_authcode.equals("29") ||
                                        str_authcode.equals("30")) {
                                    selected_channel = "ALIPAY";
                                    callPayNowAlipay();

                                } else {
                                    callRetryScanDialog(auth_code);
                                }

//                                if (selected_channel.equals("ALIPAY"))
//                                    callPayNowAlipay();
//                                else if (selected_channel.equals("WECHAT"))
//                                    callPayNowWeChat();
                            }

                        }

                    }
                    break;


                case "PaymentExpressSuccess":
                case "PaymentExpressFailure":
                    MyPOSMateApplication.isOpen = false;
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.PAYMENTPROCESSING, intent.getStringExtra("data"));
                    break;

                case "ScannedBackLoyaltyQr":
                    //Toast.makeText(context, "inside receiver", Toast.LENGTH_SHORT).show();
                    if (intent.hasExtra("identityCode")) {
                        String auth_code = intent.getStringExtra("identityCode");

                            long SuccessEndTime = System.currentTimeMillis();
                           // long SuccessCostTime = SuccessEndTime - startTime;
                            if (getActivity() != null)
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {

                                        if (preferenceManager.getLaneIdentifier().equals("")) {
                                            Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
                                        } else if (preferenceManager.getPOSIdentifier().equals("")) {
                                            Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
                                        } else {
                                            callMembershipLoyality(auth_code);
                                            Toast.makeText(getActivity(), auth_code + "", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });




                    }break;

                case "ScannedFrontLoyaltyQr":
                    //Toast.makeText(context, "inside receiver", Toast.LENGTH_SHORT).show();
                    if (intent.hasExtra("identityCode")) {
                        String auth_code = intent.getStringExtra("identityCode");

                        long SuccessEndTime = System.currentTimeMillis();
                        // long SuccessCostTime = SuccessEndTime - startTime;
                        if (getActivity() != null)
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {

                                    if (preferenceManager.getLaneIdentifier().equals("")) {
                                        Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
                                    } else if (preferenceManager.getPOSIdentifier().equals("")) {
                                        Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
                                    } else {
                                        callMembershipLoyality(auth_code);
                                        Toast.makeText(getActivity(), auth_code + "", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });




                    }break;
            }
        }
    }


    public void callRetryScanDialog(String auth_code) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater lf = (LayoutInflater) (mContext)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.qr_not_recognized_dialog, null);
        TextView title = (TextView) dialogview.findViewById(R.id.title);
        TextView body = (TextView) dialogview
                .findViewById(R.id.dialogBody);
        body.setText("QR not recognised");
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
//        cancel.setText("OK");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progress2 != null) {
                    if (progress2.isShowing())
                        progress2.dismiss();
                }
                dialog.dismiss();
            }
        });

        TextView retry = (TextView) dialogview
                .findViewById(R.id.dialogRetry);
//        retry.setVisibility(View.GONE);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    selected_channel = "";
                    callPayNowAlipay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
    }

    public void initUI(View view) {
        qrScan = new IntentIntegrator(getActivity());
        vasCallsArkeBusiness = new VASCallsArkeBusiness(getActivity());

        img_poli = view.findViewById(R.id.img_poli);
        img_centrapay_merchant_qr_display = view.findViewById(R.id.img_centrapay_merchant_qr_display);
        img_centerpay_scanqr = view.findViewById(R.id.img_centerpay_scanqr);
        btn_back = view.findViewById(R.id.btn_back);
        edt_poli_cnv = view.findViewById(R.id.edt_poli_cnv);
        edt_centerpay_mr_qr_cnv = view.findViewById(R.id.edt_centerpay_mr_qr_cnv);
        tv_status_scan = view.findViewById(R.id.tv_status_scan);
        tv_status_scan_button = view.findViewById(R.id.tv_status_scan_button);
        btn_front = view.findViewById(R.id.btn_front);
        rel_membership = view.findViewById(R.id.rel_membership);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        rel_orders=getActivity().findViewById(R.id.rel_orders);
        rel_orders.setVisibility(View.VISIBLE);
        btn_save1 = view.findViewById(R.id.btn_save1);
        btn_cancel1 = view.findViewById(R.id.btn_cancel1);
        edt_amount = view.findViewById(R.id.edt_amount);
        edt_amount.setLocale(new Locale("en", "US"));
        edt_reference = view.findViewById(R.id.edt_reference);
        edt_amount1 = view.findViewById(R.id.edt_amount1);
        edt_amount1.setLocale(new Locale("en", "US"));
        edt_reference1 = view.findViewById(R.id.edt_reference1);
        img_alipay = view.findViewById(R.id.img_alipay);
        img_wechat = view.findViewById(R.id.img_wechat);
        img_unipay = view.findViewById(R.id.img_unipay);

        img_upay = view.findViewById(R.id.img_upay);
        img_unionpay_qr = view.findViewById(R.id.img_unionpay_qr);
        tv_enable_payment = view.findViewById(R.id.tv_enable_payment);
        tv_start_countdown = view.findViewById(R.id.tv_start_countdown);
        tv_start_countdown.setVisibility(View.GONE);
        edt_xmpp_amount = view.findViewById(R.id.edt_xmpp_amount);
        edt_xmpp_amount.setVisibility(View.GONE);
        edt_xmpp_amount1 = view.findViewById(R.id.edt_xmpp_amount1);
        edt_xmpp_amount1.setVisibility(View.GONE);
        ll_amount = view.findViewById(R.id.ll_amount);
        ll_membership_loyalty_app = view.findViewById(R.id.ll_membership_loyalty_app);
        ll_reference = view.findViewById(R.id.ll_reference);
        ll_amount1 = view.findViewById(R.id.ll_amount1);
        ll_reference1 = view.findViewById(R.id.ll_reference1);
        tv_noitem = view.findViewById(R.id.tv_noitem);
        rel_unionpay = view.findViewById(R.id.rel_unionpay);
        tv_alipay = view.findViewById(R.id.tv_alipay);
        tv_wechat = view.findViewById(R.id.tv_wechat);
        scanqr = view.findViewById(R.id.scanqr);
        scanqr_unionpay = view.findViewById(R.id.scanqr_unionpay);
        tv_unionpay_qr_cv = view.findViewById(R.id.tv_unionpay_qr_cv);

        tv_uni_cv = view.findViewById(R.id.tv_uni_cv);
        tv_uni_cv1_uplan = view.findViewById(R.id.tv_uni_cv1_uplan);
        tv_uni_cv2_scan_qr = view.findViewById(R.id.tv_uni_cv2_scan_qr);

        tv_ali_cv = view.findViewById(R.id.tv_ali_cv);
        tv_ali_cv1 = view.findViewById(R.id.tv_ali_cv1);
        tv_ali_cv2 = view.findViewById(R.id.tv_ali_cv2);


        edt_amount.setSelection(edt_amount.getText().length());
        edt_amount1.setSelection(edt_amount1.getText().length());
        ll_one = view.findViewById(R.id.ll_one);
        ll_two = view.findViewById(R.id.ll_two);
        ag_v2 = view.findViewById(R.id.ag_v2);
        rel_alipay_static_qr = view.findViewById(R.id.rel_alipay_static_qr);
        rel_alipay_static_qr.setVisibility(View.VISIBLE);

        tv_uplan_disabled = view.findViewById(R.id.tv_uplan_disabled);
        tv_unionpay_disabled = view.findViewById(R.id.tv_unionpay_disabled);
        tv_scan_uni_disabled = view.findViewById(R.id.tv_scan_uni_disabled);


        tv_status_scan_button1 = view.findViewById(R.id.tv_status_scan_button1);
        tv_status_scan_button2 = view.findViewById(R.id.tv_status_scan_button2);
        btn_back1 = view.findViewById(R.id.btn_back1);
        btn_front1 = view.findViewById(R.id.btn_front1);
        btn_loyalty_apps = view.findViewById(R.id.btn_loyalty_apps);

        funcLoyaltyAppSwitches();
        funcNewUISwitchBasedOnPref();

    }


    public void funcLoyaltyAppSwitches() {
        if (preferenceManager.isDisplayLoyaltyApps()) {
            rel_membership.setVisibility(View.GONE);
            ll_membership_loyalty_app.setVisibility(View.VISIBLE);
            tv_status_scan.setVisibility(View.GONE);
            if (preferenceManager.isMembershipManual()) {
                if (preferenceManager.isFront() &&
                        preferenceManager.isBack() &&
                        preferenceManager.isDisplayLoyaltyApps()) {
                    view.findViewById(R.id.ll_back).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.ll_front).setVisibility(View.VISIBLE);
                    ll_membership_loyalty_app.setWeightSum(3);
                    tv_status_scan_button1.setVisibility(View.VISIBLE);
                    tv_status_scan_button2.setVisibility(View.VISIBLE);
                    btn_loyalty_apps.setVisibility(View.VISIBLE);
                    btn_back1.setVisibility(View.VISIBLE);
                    btn_front1.setVisibility(View.VISIBLE);
                } else if (!preferenceManager.isFront() &&
                        preferenceManager.isBack() &&
                        preferenceManager.isDisplayLoyaltyApps()) {
                    view.findViewById(R.id.ll_back).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.ll_front).setVisibility(View.GONE);
                    ll_membership_loyalty_app.setWeightSum(2);
                    tv_status_scan_button1.setVisibility(View.VISIBLE);
                    tv_status_scan_button2.setVisibility(View.GONE);
                    btn_loyalty_apps.setVisibility(View.VISIBLE);
                    btn_back1.setVisibility(View.VISIBLE);
                    btn_front1.setVisibility(View.GONE);
                } else if (preferenceManager.isFront() &&
                        !preferenceManager.isBack() &&
                        preferenceManager.isDisplayLoyaltyApps()) {
                    view.findViewById(R.id.ll_back).setVisibility(View.GONE);
                    view.findViewById(R.id.ll_front).setVisibility(View.VISIBLE);
                    ll_membership_loyalty_app.setWeightSum(2);
                    tv_status_scan_button1.setVisibility(View.GONE);
                    tv_status_scan_button2.setVisibility(View.VISIBLE);
                    btn_loyalty_apps.setVisibility(View.VISIBLE);
                    btn_back1.setVisibility(View.GONE);
                    btn_front1.setVisibility(View.VISIBLE);
                } else if (!preferenceManager.isFront() &&
                        !preferenceManager.isBack() &&
                        preferenceManager.isDisplayLoyaltyApps()) {
                    view.findViewById(R.id.ll_back).setVisibility(View.GONE);
                    view.findViewById(R.id.ll_front).setVisibility(View.GONE);
                    ll_membership_loyalty_app.setWeightSum(1);
                    tv_status_scan_button1.setVisibility(View.GONE);
                    tv_status_scan_button2.setVisibility(View.GONE);
                    btn_loyalty_apps.setVisibility(View.VISIBLE);
                    btn_back1.setVisibility(View.GONE);
                    btn_front1.setVisibility(View.GONE);
                }

            }

        } else {
            funcMembershipLoyalityUISwitch();
        }
    }


    public void funcMembershipLoyalityUISwitch() {
        if (preferenceManager.isMembershipManual()) {
            rel_membership.setVisibility(View.VISIBLE);
            tv_status_scan.setVisibility(View.INVISIBLE);
            tv_status_scan_button.setVisibility(View.VISIBLE);
            if (preferenceManager.isFront()) {
                btn_front.setVisibility(View.VISIBLE);
                btn_back.setVisibility(View.GONE);
            }
            if (preferenceManager.isBack()) {
                btn_front.setVisibility(View.GONE);
                btn_back.setVisibility(View.VISIBLE);
            }
            if (preferenceManager.isBack() && preferenceManager.isFront()) {
                btn_front.setVisibility(View.VISIBLE);
                btn_back.setVisibility(View.VISIBLE);
            }
            if (!preferenceManager.isBack() && !preferenceManager.isFront()) {
                rel_membership.setVisibility(View.GONE);
            }

        } else {
            rel_membership.setVisibility(View.GONE);
            tv_status_scan.setVisibility(View.GONE);
            tv_status_scan_button.setVisibility(View.GONE);
        }

    }


    public void cnvdisplay() {

        if (!preferenceManager.getcnv_centrapay().equals("") &&
                !preferenceManager.getcnv_centrapay().equals("0.0") &&
                !preferenceManager.getcnv_centrapay().equals("0.00")) {
            double amount = Double.parseDouble(edt_amount.getText().toString()) * Double.parseDouble(preferenceManager.getcnv_centrapay()) / 100;
            edt_centerpay_mr_qr_cnv.setText("" + roundTwoDecimals(amount));
        }

        if (!preferenceManager.getcnv_poli().equals("") &&
                !preferenceManager.getcnv_poli().equals("0.0") &&
                !preferenceManager.getcnv_poli().equals("0.00")) {
            double amount = Double.parseDouble(edt_amount.getText().toString()) * Double.parseDouble(preferenceManager.getcnv_poli()) / 100;
            edt_poli_cnv.setText("" + roundTwoDecimals(amount));
        }


        if (!preferenceManager.getcnv_alipay().equals("") &&
                !preferenceManager.getcnv_alipay().equals("0.0") &&
                !preferenceManager.getcnv_alipay().equals("0.00")) {
            double amount = Double.parseDouble(edt_amount.getText().toString()) * Double.parseDouble(preferenceManager.getcnv_alipay()) / 100;
            tv_ali_cv.setText("" + roundTwoDecimals(amount));
        }

        if (!preferenceManager.getcnv_wechat().equals("") &&
                !preferenceManager.getcnv_wechat().equals("0.0") &&
                !preferenceManager.getcnv_wechat().equals("0.00")) {
            double amount = Double.parseDouble(edt_amount.getText().toString())
                    * Double.parseDouble(preferenceManager.getcnv_wechat()) / 100;
            tv_ali_cv1.setText("" + roundTwoDecimals(amount));
        }

        if (!preferenceManager.getcnv_uni().equals("") &&
                !preferenceManager.getcnv_uni().equals("0.0") &&
                !preferenceManager.getcnv_uni().equals("0.00")) {
            double amount = Double.parseDouble(edt_amount.getText().toString()) * Double.parseDouble(preferenceManager.getcnv_uni()) / 100;
            tv_uni_cv.setText("" + roundTwoDecimals(amount));
        }

        if (!preferenceManager.getcnv_uplan().equals("") &&
                !preferenceManager.getcnv_uplan().equals("0.0") &&
                !preferenceManager.getcnv_uplan().equals("0.00")) {
            double amount = Double.parseDouble(edt_amount.getText().toString())
                    * Double.parseDouble(preferenceManager.getcnv_uplan()) / 100;
            tv_uni_cv1_uplan.setText("" + roundTwoDecimals(amount));
        }


        if (!preferenceManager.getcnv_uniqr().equals("") &&
                !preferenceManager.getcnv_uniqr().equals("0.0") &&
                !preferenceManager.getcnv_uniqr().equals("0.00")) {
            double amount = Double.parseDouble(edt_amount.getText().toString())
                    * Double.parseDouble(preferenceManager.getcnv_uniqr()) / 100;
            tv_uni_cv2_scan_qr.setText("" + roundTwoDecimals(amount));
            //  tv_unionpay_qr_cv.setText("" + roundTwoDecimals(amount));
        }


        if (!preferenceManager.get_cnv_unimerchantqrdisplayLower().equals("") &&
                !preferenceManager.get_cnv_unimerchantqrdisplayLower().equals("0.0") &&
                !preferenceManager.get_cnv_unimerchantqrdisplayLower().equals("0.00")) {
            double amount = Double.parseDouble(edt_amount.getText().toString())
                    * Double.parseDouble(preferenceManager.get_cnv_unimerchantqrdisplayLower()) / 100;
            tv_unionpay_qr_cv.setText("" + roundTwoDecimals(amount));
            //  tv_unionpay_qr_cv.setText("" + roundTwoDecimals(amount));
        }

    }


    public void funcNewUISwitchBasedOnPref() {

        cnvdisplay();

        if (preferenceManager.isAlipaySelected() &&
                preferenceManager.isWechatSelected() &&
                preferenceManager.isAlipayScan() &&
                preferenceManager.isWeChatScan()) {
            img_alipay.setVisibility(View.VISIBLE);
            img_wechat.setVisibility(View.VISIBLE);
            scanqr.setVisibility(View.VISIBLE);
        } else if (!preferenceManager.isAlipaySelected() &&
                preferenceManager.isWechatSelected() &&
                preferenceManager.isAlipayScan() &&
                preferenceManager.isWeChatScan()) {
            img_alipay.setVisibility(View.INVISIBLE);
            img_wechat.setVisibility(View.VISIBLE);
            scanqr.setVisibility(View.VISIBLE);
        } else if (preferenceManager.isAlipaySelected() &&
                !preferenceManager.isWechatSelected() &&
                preferenceManager.isAlipayScan() &&
                preferenceManager.isWeChatScan()) {
            img_alipay.setVisibility(View.VISIBLE);
            img_wechat.setVisibility(View.INVISIBLE);
            scanqr.setVisibility(View.VISIBLE);
        } else if (preferenceManager.isAlipaySelected() &&
                preferenceManager.isWechatSelected() &&
                !preferenceManager.isAlipayScan() &&
                !preferenceManager.isWeChatScan()) {
            img_alipay.setVisibility(View.VISIBLE);
            img_wechat.setVisibility(View.VISIBLE);
            scanqr.setVisibility(View.INVISIBLE);
        } else if (!preferenceManager.isAlipaySelected() &&
                !preferenceManager.isWechatSelected() &&
                preferenceManager.isAlipayScan() &&
                preferenceManager.isWeChatScan()) {
            img_alipay.setVisibility(View.INVISIBLE);
            img_wechat.setVisibility(View.INVISIBLE);
            scanqr.setVisibility(View.VISIBLE);
        } else if (preferenceManager.isAlipaySelected() &&
                !preferenceManager.isWechatSelected() &&
                !preferenceManager.isAlipayScan() &&
                !preferenceManager.isWeChatScan()) {
            img_alipay.setVisibility(View.VISIBLE);
            img_wechat.setVisibility(View.INVISIBLE);
            scanqr.setVisibility(View.INVISIBLE);
        } else if (!preferenceManager.isAlipaySelected() &&
                preferenceManager.isWechatSelected() &&
                !preferenceManager.isAlipayScan() &&
                !preferenceManager.isWeChatScan()) {
            img_alipay.setVisibility(View.INVISIBLE);
            img_wechat.setVisibility(View.VISIBLE);
            scanqr.setVisibility(View.INVISIBLE);
        } else if (!preferenceManager.isAlipaySelected() &&
                !preferenceManager.isWechatSelected() &&
                !preferenceManager.isAlipayScan() &&
                !preferenceManager.isWeChatScan()) {
            img_alipay.setVisibility(View.INVISIBLE);
            img_wechat.setVisibility(View.INVISIBLE);
            scanqr.setVisibility(View.INVISIBLE);
            tv_ali_cv.setVisibility(View.INVISIBLE);
            tv_ali_cv1.setVisibility(View.INVISIBLE);
            tv_ali_cv2.setVisibility(View.INVISIBLE);
//            tv_wechat_qr_scan_cv.setVisibility(View.INVISIBLE);
        }

        if (preferenceManager.isAlipayScan() || preferenceManager.isWeChatScan()) {
            scanqr.setVisibility(View.VISIBLE);
        } else if (!preferenceManager.isAlipayScan() && !preferenceManager.isWeChatScan()) {
            scanqr.setVisibility(View.INVISIBLE);
        }


//        if (preferenceManager.isWeChatScan()) {
//            scanqr.setVisibility(View.VISIBLE);
//        } else if (!preferenceManager.isWeChatScan()) {
//            scanqr.setVisibility(View.INVISIBLE);
//        }


        if (preferenceManager.is_cnv_alipay_display_and_add() ||
                preferenceManager.is_cnv_alipay_display_only()) {
            tv_ali_cv.setVisibility(View.VISIBLE);
        } else {
            tv_ali_cv.setVisibility(View.INVISIBLE);
        }

        if (preferenceManager.is_cnv_wechat_display_and_add() ||
                preferenceManager.is_cnv_wechat_display_only()) {
            tv_ali_cv1.setVisibility(View.VISIBLE);
        } else {
            tv_ali_cv1.setVisibility(View.INVISIBLE);
        }

        if (preferenceManager.isAlipayScan()
                && ((preferenceManager.is_cnv_wechat_display_and_add() ||
                preferenceManager.is_cnv_wechat_display_only()))) {
            tv_ali_cv2.setVisibility(View.INVISIBLE);
        } else {
            tv_ali_cv2.setVisibility(View.INVISIBLE);
        }


//        if (preferenceManager.isWeChatScan()
//                && ((preferenceManager.is_cnv_wechat_display_and_add() ||
//                preferenceManager.is_cnv_wechat_display_only()))) {
//            tv_wechat_qr_scan_cv.setVisibility(View.VISIBLE);
//        } else {
//            tv_wechat_qr_scan_cv.setVisibility(View.INVISIBLE);
//        }


        if (!preferenceManager.isAlipaySelected() &&
                !preferenceManager.isWechatSelected() &&
                !preferenceManager.isUnionPaySelected() &&
                !preferenceManager.isUnionPayQrCodeDisplaySelected() &&
                !preferenceManager.isUnionPayQrSelected() &&
                !preferenceManager.isUplanSelected()&&
                !preferenceManager.isPoliSelected()&&
                !preferenceManager.isCentrapayMerchantQRDisplaySelected()
        ) {
            tv_noitem.setVisibility(View.VISIBLE);
            btn_cancel.setOnClickListener(null);
            btn_cancel.setVisibility(View.GONE);
            ll_amount1.setVisibility(View.GONE);
            ll_reference1.setVisibility(View.GONE);
            ll_reference.setVisibility(View.GONE);
            ll_amount.setVisibility(View.GONE);
        } else {
            ll_one.setVisibility(View.VISIBLE);
            ll_reference.setVisibility(View.VISIBLE);
            ll_amount.setVisibility(View.VISIBLE);
            ll_two.setVisibility(View.GONE);
            btn_cancel.setOnClickListener(this);
            btn_cancel.setVisibility(View.VISIBLE);
            tv_noitem.setVisibility(View.GONE);

        }


        calledt_amount();
        calledt_amount1();


    }


    public void initListener() {
        img_centrapay_merchant_qr_display.setOnClickListener(this);
        img_centerpay_scanqr.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_front.setOnClickListener(this);
        btn_back1.setOnClickListener(this);
        btn_front1.setOnClickListener(this);
        btn_loyalty_apps.setOnClickListener(this);
        btn_save1.setOnClickListener(this);
        btn_cancel1.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        scanqr.setOnClickListener(this);
        scanqr_unionpay.setOnClickListener(this);
        img_alipay.setOnClickListener(this);
        img_wechat.setOnClickListener(this);
        img_unipay.setOnClickListener(this);
        img_upay.setOnClickListener(this);
        rel_unionpay.setOnClickListener(this);
        img_unionpay_qr.setOnClickListener(this);
        rel_alipay_static_qr.setOnClickListener(this);
        img_poli.setOnClickListener(this);
        img_centerpay_scanqr.setOnClickListener(this);
        img_centrapay_merchant_qr_display.setOnClickListener(this);

    }


    public void callAuthToken() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
//        hashMap.put("username", AppConstants.CLIENT_ID);
//        hashMap.put("password",AppConstants.CLIENT_SECRET);
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.AUTH);

    }


    public void callAuthToken1() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
//        hashMap.put("username", AppConstants.CLIENT_ID);
//        hashMap.put("password",AppConstants.CLIENT_SECRET);
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken1").execute(AppConstants.AUTH);

    }


    public void calledt_amount() {
        edt_amount.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_amount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edt_amount.getText().toString().equals("0.00")) {
                    edt_amount.setText("");


                }
                return false;
            }
        });
        edt_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.v("", "");
                try {
                    if (s.equals("")) {
                        return;
                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v("", "");

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.v("", "");
                if (selected_screen != 0) {
                    selected_screen = 0;
                }


                // C/(1-r) where C is the total amount and r is
//                            convinence fee in percent.
                if (!edt_amount.getText().toString().equals("")) {
//                    Toast.makeText(getActivity(), "Called"+edt_amount.getText().toString(), Toast.LENGTH_SHORT).show();
                    if (edt_amount.getText().toString().length() < 12) {
                        tv_uni_cv.setTextSize(14);
                        tv_uni_cv1_uplan.setTextSize(14);
                        tv_uni_cv2_scan_qr.setTextSize(14);
                        tv_ali_cv.setTextSize(14);
                        tv_ali_cv1.setTextSize(14);
                        tv_unionpay_qr_cv.setTextSize(14);
                        edt_poli_cnv.setTextSize(14);
                        edt_centerpay_mr_qr_cnv.setTextSize(14);
                    } else {
                        tv_uni_cv.setTextSize(10);
                        tv_uni_cv1_uplan.setTextSize(10);
                        tv_uni_cv2_scan_qr.setTextSize(10);
                        tv_ali_cv.setTextSize(10);
                        tv_ali_cv1.setTextSize(10);
                        tv_unionpay_qr_cv.setTextSize(10);
                        edt_poli_cnv.setTextSize(10);
                        edt_centerpay_mr_qr_cnv.setTextSize(10);
                    }
                    callAllConvinenceFeeCalculations();
                }


            }
        });
    }

    public void callAllConvinenceFeeCalculations() {
        if (edt_amount.getText().toString().equals(""))
            return;
        calculateConvFeeCentrapay();
        calculateConvFeePoli();
        calculateConvAlipay();
        calculateConvWeChat();
        calculateConvAlipayWeChatScan();
        calculateConvFeeUnionPay();
        calculateConvFeeUplan();
        calculateUPQRScan();
        calculateConvFeeUnionPayMerchantQRDisplay();
    }

    private void calculateUPQRScan() {
        if (preferenceManager.isUnionPayQrSelected())
            calculateConvFeeUnionPayQRScan();
        else if (preferenceManager.isUnionPayQrCodeDisplaySelected())
            calculateConvFeeUnionPayQRDisplay();
    }


    public void calculateConvAlipay() {
        if (preferenceManager.isAlipaySelected() && (preferenceManager.is_cnv_alipay_display_and_add() ||
                preferenceManager.is_cnv_alipay_display_only())) {

            if (!preferenceManager.getcnv_alipay().equals("") ||
                    !preferenceManager.getcnv_alipay().equals("0.0") ||
                    !preferenceManager.getcnv_alipay().equals("0.00")) {
                convenience_amount_alipay = Double.parseDouble(edt_amount.getText().toString().
                        replace(",", "")) /
                        (1 - (Double.parseDouble(preferenceManager.getcnv_alipay()) / 100));
                if (roundTwoDecimals(convenience_amount_alipay).length() > 12) {
                    tv_ali_cv.setTextSize(10);
                }
                tv_ali_cv.setText("" + roundTwoDecimals(convenience_amount_alipay));
            }

        }
    }

    public void calculateConvWeChat() {
        if (preferenceManager.isWechatSelected() && (preferenceManager.is_cnv_wechat_display_and_add() ||
                preferenceManager.is_cnv_wechat_display_only())) {

            if (!preferenceManager.getcnv_wechat().equals("") ||
                    !preferenceManager.getcnv_wechat().equals("0.0") ||
                    !preferenceManager.getcnv_wechat().equals("0.00")) {
                convenience_amount_wechat = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
                        (1 - (Double.parseDouble(preferenceManager.getcnv_wechat()) / 100));
                if (roundTwoDecimals(convenience_amount_wechat).length() > 12) {
                    tv_ali_cv1.setTextSize(10);
                }

                tv_ali_cv1.setText("" + roundTwoDecimals(convenience_amount_wechat));
            }

        }
    }


    public void calculateConvAlipayWeChatScan() {
        if (preferenceManager.isAlipayScan() && (preferenceManager.is_cnv_alipay_display_and_add() ||
                preferenceManager.is_cnv_alipay_display_only())) {

            if (!preferenceManager.getcnv_alipay().equals("") ||
                    !preferenceManager.getcnv_alipay().equals("0.0") ||
                    !preferenceManager.getcnv_alipay().equals("0.00")) {
                convenience_amount_alipay_scan = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
                        (1 - (Double.parseDouble(preferenceManager.getcnv_alipay()) / 100));

                if (roundTwoDecimals(convenience_amount_alipay_scan).length() > 12) {
                    tv_ali_cv.setTextSize(10);
                }
                tv_ali_cv.setText("" + roundTwoDecimals(convenience_amount_alipay_scan));
                // tv_ali_cv2.setText("" + roundTwoDecimals(convenience_amount_alipay_scan));

            }

        }


        if (preferenceManager.isWeChatScan() && (preferenceManager.is_cnv_wechat_display_and_add() ||
                preferenceManager.is_cnv_wechat_display_only())) {

            if (!preferenceManager.getcnv_wechat().equals("") ||
                    !preferenceManager.getcnv_wechat().equals("0.0") ||
                    !preferenceManager.getcnv_wechat().equals("0.00")) {
                convenience_amount_wechat_scan = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
                        (1 - (Double.parseDouble(preferenceManager.getcnv_wechat()) / 100));

                if (roundTwoDecimals(convenience_amount_wechat_scan).length() > 12) {
                    tv_ali_cv1.setTextSize(10);
                }

                tv_ali_cv1.setText("" + roundTwoDecimals(convenience_amount_wechat_scan));
                //   tv_ali_cv2.setText("" + roundTwoDecimals(convenience_amount_wechat_scan));
            }

        }

        if (preferenceManager.isAlipayScan() ||
                preferenceManager.isWeChatScan()) {
            if (convenience_amount_alipay_scan > convenience_amount_wechat_scan) {
                if (roundTwoDecimals(convenience_amount_alipay_scan).length() > 12) {
                    tv_ali_cv.setTextSize(10);
                }

                tv_ali_cv.setText("" + roundTwoDecimals(convenience_amount_alipay_scan));
                //  tv_ali_cv2.setText("" + roundTwoDecimals(convenience_amount_alipay_scan));
            } else if (convenience_amount_alipay_scan < convenience_amount_wechat_scan) {
                if (roundTwoDecimals(convenience_amount_wechat_scan).length() > 12) {
                    tv_ali_cv1.setTextSize(10);
                }

                tv_ali_cv1.setText("" + roundTwoDecimals(convenience_amount_wechat_scan));
                //  tv_ali_cv2.setText("" + roundTwoDecimals(convenience_amount_wechat_scan));
            } else {
                if (roundTwoDecimals(convenience_amount_alipay_scan).length() > 12) {
                    tv_ali_cv.setTextSize(10);
                }
                tv_ali_cv.setText("" + roundTwoDecimals(convenience_amount_alipay_scan));
                // tv_ali_cv2.setText("" + roundTwoDecimals(convenience_amount_alipay_scan));
            }
        }


    }


    public void calculateConvFeeUnionPay() {
        if (preferenceManager.is_cnv_uni_display_and_add()
                || preferenceManager.is_cnv_uni_display_only()) {

            if (!preferenceManager.getcnv_uni().equals("") ||
                    !preferenceManager.getcnv_uni().equals("0.0") ||
                    !preferenceManager.getcnv_uni().equals("0.00")) {
                convenience_amount_unionpay = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
                        (1 - (Double.parseDouble(preferenceManager.getcnv_uni()) / 100));
                if (roundTwoDecimals(convenience_amount_unionpay).length() > 12) {
                    tv_uni_cv.setTextSize(10);
                    tv_uni_cv1_uplan.setTextSize(10);
                    tv_uni_cv2_scan_qr.setTextSize(10);
                }
                tv_uni_cv.setText("" + roundTwoDecimals(convenience_amount_unionpay));
                tv_uni_cv1_uplan.setText("" + roundTwoDecimals(convenience_amount_unionpay));
                tv_uni_cv2_scan_qr.setText("" + roundTwoDecimals(convenience_amount_unionpay));
            }
        }
    }


    public void calculateConvFeeCentrapay() {
        if (preferenceManager.is_cnv_centrapay_display_and_add()
                || preferenceManager.is_cnv_centrapay_display_only()) {

            if (!preferenceManager.getcnv_centrapay().equals("") ||
                    !preferenceManager.getcnv_centrapay().equals("0.0") ||
                    !preferenceManager.getcnv_centrapay().equals("0.00")) {
                convenience_amount_centrapay_merchant_qr_display = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
                        (1 - (Double.parseDouble(preferenceManager.getcnv_centrapay()) / 100));
                if (roundTwoDecimals(convenience_amount_centrapay_merchant_qr_display).length() > 12) {
                    edt_centerpay_mr_qr_cnv.setTextSize(10);
                }
                edt_centerpay_mr_qr_cnv.setText("" + roundTwoDecimals(convenience_amount_centrapay_merchant_qr_display));
            }
        }
    }


    public void calculateConvFeePoli() {
        if (preferenceManager.is_cnv_poli_display_and_add()
                || preferenceManager.is_cnv_poli_display_only()) {

            if (!preferenceManager.getcnv_poli().equals("") ||
                    !preferenceManager.getcnv_poli().equals("0.0") ||
                    !preferenceManager.getcnv_poli().equals("0.00")) {
                convenience_amount_poli = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
                        (1 - (Double.parseDouble(preferenceManager.getcnv_poli()) / 100));
                if (roundTwoDecimals(convenience_amount_poli).length() > 12) {
                    edt_poli_cnv.setTextSize(10);
                }
                edt_poli_cnv.setText("" + roundTwoDecimals(convenience_amount_poli));
            }
        }
    }


    public void calculateConvFeeUplan() {
        if (preferenceManager.cnv_uplan_display_and_add()
                || preferenceManager.cnv_uplan_display_only()) {

            if (!preferenceManager.getcnv_uplan().equals("") ||
                    !preferenceManager.getcnv_uplan().equals("0.0") ||
                    !preferenceManager.getcnv_uplan().equals("0.00")) {

                convenience_amount_uplan = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
                        (1 - (Double.parseDouble(preferenceManager.getcnv_uplan()) / 100));
                if (roundTwoDecimals(convenience_amount_uplan).length() > 12) {
                    tv_uni_cv1_uplan.setTextSize(10);
                }
                tv_uni_cv1_uplan.setText("" + roundTwoDecimals(convenience_amount_uplan));
            }
        }
    }


    public void calculateConvFeeUnionPayQRScan() {
        if (preferenceManager.cnv_unionpayqr_display_and_add()
                || preferenceManager.cnv_unionpayqr_display_only()) {

            if (!preferenceManager.getcnv_uniqr().equals("") ||
                    !preferenceManager.getcnv_uniqr().equals("0.0") ||
                    !preferenceManager.getcnv_uniqr().equals("0.00")) {

                convenience_amount_unionpayqrscan = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
                        (1 - (Double.parseDouble(preferenceManager.getcnv_uniqr()) / 100));
                if (roundTwoDecimals(convenience_amount_unionpayqrscan).length() > 12) {
                    tv_uni_cv2_scan_qr.setTextSize(10);
                }
                tv_uni_cv2_scan_qr.setText("" + roundTwoDecimals(convenience_amount_unionpayqrscan));
            }
        }
    }


    public void calculateConvFeeUnionPayMerchantQRDisplay() {
        if (preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()
                || preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only()) {

            if ((preferenceManager.get_cnv_unimerchantqrdisplayLower().equals("") ||
                    preferenceManager.get_cnv_unimerchantqrdisplayLower().equals("0.0") ||
                    preferenceManager.get_cnv_unimerchantqrdisplayLower().equals("0.00")) &&
                    (preferenceManager.get_cnv_unimerchantqrdisplayHigher().equals("") ||
                            preferenceManager.get_cnv_unimerchantqrdisplayHigher().equals("0.0") ||
                            preferenceManager.get_cnv_unimerchantqrdisplayHigher().equals("0.00"))) {


            } else {

                Double o_amt = edt_amount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edt_amount.getText().toString().replace(",", ""));
                Double upi_amt = preferenceManager.getCnv_up_upiqr_mpmcloud_amount().isEmpty() ? 0 : Double.parseDouble(preferenceManager.getCnv_up_upiqr_mpmcloud_amount());

                if (o_amt < upi_amt) {
                    convenience_amount_unionpayqr_merchant_display = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
                            (1 - (Double.parseDouble(preferenceManager.get_cnv_unimerchantqrdisplayLower()) / 100));
                } else {
                    convenience_amount_unionpayqr_merchant_display = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
                            (1 - (Double.parseDouble(preferenceManager.get_cnv_unimerchantqrdisplayHigher()) / 100));
                }

                if (roundTwoDecimals(convenience_amount_unionpayqr_merchant_display).length() > 12) {
                    tv_unionpay_qr_cv.setTextSize(10);
                }
                tv_unionpay_qr_cv.setText("" + roundTwoDecimals(convenience_amount_unionpayqr_merchant_display));
            }
        }
    }


    public void calculateConvFeeUnionPayQRDisplay() {
        if (preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()
                || preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only()) {

            if ((!preferenceManager.getcnv_up_upiqr_mpmcloud_lower().equals("") ||
                    !preferenceManager.getcnv_up_upiqr_mpmcloud_lower().equals("0.0") ||
                    !preferenceManager.getcnv_up_upiqr_mpmcloud_lower().equals("0.00")) &&
                    (!preferenceManager.getCnv_up_upiqr_mpmcloud_higher().equals("") ||
                            !preferenceManager.getCnv_up_upiqr_mpmcloud_higher().equals("0.0") ||
                            !preferenceManager.getCnv_up_upiqr_mpmcloud_higher().equals("0.00"))) {

                Double o_amt = edt_amount.getText().toString().isEmpty() ? 0 : Double.parseDouble(edt_amount.getText().toString().replace(",", ""));
                Double upi_amt = preferenceManager.getCnv_up_upiqr_mpmcloud_amount().isEmpty() ? 0 : Double.parseDouble(preferenceManager.getCnv_up_upiqr_mpmcloud_amount());
                if (o_amt < upi_amt) {
                    convenience_amount_unionpayqrdisplay = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
                            (1 - (Double.parseDouble(preferenceManager.getcnv_up_upiqr_mpmcloud_lower()) / 100));
                } else {
                    convenience_amount_unionpayqrdisplay = Double.parseDouble(edt_amount.getText().toString().replace(",", "")) /
                            (1 - (Double.parseDouble(preferenceManager.getCnv_up_upiqr_mpmcloud_higher()) / 100));
                }


                if (roundTwoDecimals(convenience_amount_unionpayqrdisplay).length() > 12) {
                    tv_uni_cv2_scan_qr.setTextSize(10);
                }
                tv_uni_cv2_scan_qr.setText("" + roundTwoDecimals(convenience_amount_unionpayqrdisplay));
            }
        }
    }


    public void calledt_amount1() {
        edt_amount1.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_amount1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edt_amount1.getText().toString().equals("0.00")) {
                    edt_amount1.setText(null);

                }
                return false;
            }
        });
        edt_amount1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.v("", "");
                try {
                    if (s.equals("")) {
                        return;
                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v("", "");

            }

            @Override
            public void afterTextChanged(Editable s) {

                Log.v("", "");
                if (selected_screen != 0) {
                    selected_screen = 0;
                }
                // C/(1-r) where C is the total amount and r is
//                            convinence fee in percent.
//                if (!edt_amount1.getText().toString().equals("")) {
//                    Toast.makeText(getActivity(), "Called"+edt_amount1.getText().toString(), Toast.LENGTH_SHORT).show();
//                    callAllConvinenceFeeCalculations();
//                }

            }
        });
    }

    public boolean isPayNowScanCalled = false;

    public void callPayNowAlipay() {
        isPayNowScanCalled = true;
        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
        if (countDownTimerxmpp != null) {
            countDownTimerxmpp.cancel();
            tv_start_countdown.setVisibility(View.GONE);
        }
        openProgressDialog();
        try {


            if (MyPOSMateApplication.isOpen) {
                char[] ch = xmppAmount.toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ch.length; i++) {
                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                    } else {
                        sb.append(ch[i]);
                    }
                }
                xmppAmount = sb.toString().replace(",", "");

                if (preferenceManager.is_cnv_alipay_display_and_add()) {
                  //  Log.v("TOKENRESPONSE","1 "+xmppAmount+" "+auth_code);
                    Toast.makeText(getActivity(),"1",Toast.LENGTH_SHORT).show();
                    original_amount = original_xmpp_trigger_amount;
                    xmppAmount = convenience_amount_alipay_scan + "";
                    fee_amount = convenience_amount_alipay_scan -
                            Double.parseDouble(original_xmpp_trigger_amount.replace(",", ""))
                            + "";
                    fee_percentage = preferenceManager.getcnv_alipay();
                    preferenceManager.setReference(edt_reference.getText().toString());

                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", URLEncoder.encode(edt_reference.getText().toString(), "UTF-8"));
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                  if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";

                    if (preferenceManager.isExternalScan())
                    {
                        reference_id = new Date().getTime() + "";
                    }

                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", xmppAmount);
                    hashMapKeys.put("original_amount", original_amount);
                    hashMapKeys.put("fee_amount", roundTwoDecimals(Float.valueOf(fee_amount + "")));
                    hashMapKeys.put("fee_percentage", fee_percentage);
                    hashMapKeys.put("discount", "0");
                    if (!auth_code.equals(""))
                        hashMapKeys.put("qr_mode", false + "");
                    else
                        hashMapKeys.put("qr_mode", true + "");

                    hashMapKeys.put("auth_code", auth_code);
                    if (!selected_channel.equals(""))
                        hashMapKeys.put("channel", selected_channel);
                    //   hashMapKeys.put("channel", channel);

                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

                } else {
                    Log.v("TOKENRESPONSE","2 "+xmppAmount+" "+auth_code);
                   Log.v("TOKENRESPONSE","2"+xmppAmount+" "+auth_code);
                  //  Toast.makeText(getActivity(),"2",Toast.LENGTH_SHORT).show();
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", URLEncoder.encode(edt_reference.getText().toString(), "UTF-8"));
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                 if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";

                    if (preferenceManager.isExternalScan())
                    {
                        reference_id = new Date().getTime() + "";
                    }

                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", xmppAmount);
                    if (!auth_code.equals(""))
                        hashMapKeys.put("qr_mode", false + "");
                    else
                        hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("auth_code", auth_code);
                    if (!selected_channel.equals(""))
                        hashMapKeys.put("channel", selected_channel);
                    //   hashMapKeys.put("channel", channel);

                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                }


            } else {
                Log.v("TOKENRESPONSE","3 "+xmppAmount+" "+auth_code);
              //  Toast.makeText(getActivity(),"3 Display and Add",Toast.LENGTH_SHORT).show();
                String amount = "";
                char[] ch = edt_amount.getText().toString().toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ch.length; i++) {
                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                    } else {
                        sb.append(ch[i]);
                    }
                }
                amount = sb.toString().replace(",", "");

                if (preferenceManager.is_cnv_alipay_display_and_add()) {
                    original_amount = amount;
                    amount = convenience_amount_alipay_scan + "";
                    fee_amount = convenience_amount_alipay_scan -
                            Double.parseDouble(edt_amount.getText().toString().replace(",", ""))
                            + "";
                    fee_percentage = preferenceManager.getcnv_alipay();
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", URLEncoder.encode(edt_reference.getText().toString(), "UTF-8"));
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    if (preferenceManager.isExternalScan())
                    {
                        reference_id = new Date().getTime() + "";
                    }
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", amount);
                    hashMapKeys.put("original_amount", original_amount);
                    hashMapKeys.put("fee_amount", roundTwoDecimals(Float.valueOf(fee_amount + "")));
                    hashMapKeys.put("fee_percentage", fee_percentage);
                    hashMapKeys.put("discount", "0");
                    if (!auth_code.equals(""))
                        hashMapKeys.put("qr_mode", false + "");
                    else
                        hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("auth_code", auth_code);
                    if (!selected_channel.equals(""))
                        hashMapKeys.put("channel", selected_channel);
                    //    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

                } else {
                    Log.v("TOKENRESPONSE","4 "+xmppAmount+" "+auth_code);

                  //  Toast.makeText(getActivity(),"4 Display only",Toast.LENGTH_SHORT).show();
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", URLEncoder.encode(edt_reference.getText().toString(), "UTF-8"));
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";

                    if (preferenceManager.isExternalScan())
                    {
                        reference_id = new Date().getTime() + "";
                    }
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", amount);
                    if (!auth_code.equals(""))
                        hashMapKeys.put("qr_mode", false + "");
                    else
                        hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("auth_code", auth_code);
                    if (!selected_channel.equals(""))
                        hashMapKeys.put("channel", selected_channel);
                    // hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callPayNowWeChat() {
        isPayNowScanCalled = true;
        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
        if (countDownTimerxmpp != null) {
            countDownTimerxmpp.cancel();
            tv_start_countdown.setVisibility(View.GONE);
        }
        openProgressDialog();
        try {


            if (MyPOSMateApplication.isOpen) {
                char[] ch = xmppAmount.toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ch.length; i++) {
                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                    } else {
                        sb.append(ch[i]);
                    }
                }
                xmppAmount = sb.toString().replace(",", "");


                if (preferenceManager.is_cnv_wechat_display_and_add()) {
                    original_amount = original_xmpp_trigger_amount;
                    xmppAmount = convenience_amount_wechat_scan + "";
                    fee_amount = convenience_amount_wechat_scan -
                            Double.parseDouble(original_xmpp_trigger_amount.replace(",", ""))
                            + "";
                    fee_percentage = preferenceManager.getcnv_wechat();
                    preferenceManager.setReference(edt_reference.getText().toString());

                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", URLEncoder.encode(edt_reference.getText().toString(), "UTF-8"));
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", xmppAmount);
                    hashMapKeys.put("original_amount", original_amount);
                    hashMapKeys.put("fee_amount", roundTwoDecimals(Float.valueOf(fee_amount + "")));
                    hashMapKeys.put("fee_percentage", fee_percentage);
                    hashMapKeys.put("discount", "0");
                    if (!auth_code.equals(""))
                        hashMapKeys.put("qr_mode", false + "");
                    else
                        hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("auth_code", auth_code);
                    if (!selected_channel.equals(""))
                        hashMapKeys.put("channel", selected_channel);
                    //   hashMapKeys.put("channel", channel);

                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

                } else {
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", URLEncoder.encode(edt_reference.getText().toString(), "UTF-8"));
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", xmppAmount);
                    if (!auth_code.equals(""))
                        hashMapKeys.put("qr_mode", false + "");
                    else
                        hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("auth_code", auth_code);
                    if (!selected_channel.equals(""))
                        hashMapKeys.put("channel", selected_channel);
                    //   hashMapKeys.put("channel", channel);

                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                }


            } else {
                String amount = "";
                char[] ch = edt_amount.getText().toString().toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ch.length; i++) {
                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                    } else {
                        sb.append(ch[i]);
                    }
                }
                amount = sb.toString().replace(",", "");
                if (preferenceManager.is_cnv_wechat_display_and_add()) {
                    original_amount = amount;
                    amount = convenience_amount_wechat_scan + "";
                    fee_amount = convenience_amount_wechat_scan -
                            Double.parseDouble(edt_amount.getText().toString().replace(",", ""))
                            + "";
                    fee_percentage = preferenceManager.getcnv_wechat();

                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", URLEncoder.encode(edt_reference.getText().toString(), "UTF-8"));
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", amount);
                    hashMapKeys.put("original_amount", original_amount);
                    hashMapKeys.put("fee_amount", roundTwoDecimals(Float.valueOf(fee_amount + "")));
                    hashMapKeys.put("fee_percentage", fee_percentage);
                    hashMapKeys.put("discount", "0");
                    if (!auth_code.equals(""))
                        hashMapKeys.put("qr_mode", false + "");
                    else
                        hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("auth_code", auth_code);
                    if (!selected_channel.equals(""))
                        hashMapKeys.put("channel", selected_channel);
                    //    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

                } else {
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", URLEncoder.encode(edt_reference.getText().toString(), "UTF-8"));
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())


                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", amount);
                    if (!auth_code.equals(""))
                        hashMapKeys.put("qr_mode", false + "");
                    else
                        hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("auth_code", auth_code);
                    if (!selected_channel.equals(""))
                        hashMapKeys.put("channel", selected_channel);
                    // hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callDP(String reference_id) {
        preferenceManager.setreference_id(reference_id);
        if (isUpayselected || isUnionPayQrSelected) {
            preferenceManager.setupay_reference_id(reference_id);
            if (countDownTimerxmpp != null)
                countDownTimerxmpp.cancel();
            if (countDownTimer != null)
                countDownTimer.cancel();
            // qrScan.initiateScan();
           // startQuickScan(true);
        } else
            beginBussiness(reference_id);
    }


    public static String server_response = "";
    public static boolean is_success = false;
    public static boolean is_payment = false;
    public static String trade_no = "";

    public void callUnionPay() {
        trade_no = "";
        is_success = false;
        is_payment = false;
        server_response = "";
        if (countDownTimerxmpp != null) {
            countDownTimerxmpp.cancel();
            tv_start_countdown.setVisibility(View.GONE);
        }
        callAuthToken();
        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
        String amount = "";


        hashMapKeys.clear();
        if (!edt_reference.getText().toString().equals("")) {
            hashMapKeys.put("ref_data1", edt_reference.getText().toString());
        }
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
        hashMapKeys.put("system", preferenceManager.getterminalId());
        hashMapKeys.put("channel", "UNION_PAY");
        hashMapKeys.put("access_id", preferenceManager.getuniqueId());
        hashMapKeys.put("is_success", is_success + "");
        hashMapKeys.put("is_payment", is_payment + "");
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("random_str", new Date().getTime() + "");
        hashMapKeys.put("trade_no", trade_no);
        hashMapKeys.put("server_response", server_response);
        hashMapKeys.put("rate", "0");
        hashMapKeys.put("currency", "NZD");


        if (preferenceManager.isConvenienceFeeSelected()
                && preferenceManager.is_cnv_uni_display_and_add()) {
            if (preferenceManager.getcnv_uni().equals("") || preferenceManager.getcnv_uni().equals("0.0") || preferenceManager.getcnv_uni().equals("0.00")) {

                amount = convenience_amount_unionpay + "";
                original_amount = edt_amount.getText().toString().replace(",", "");
                fee_amount = convenience_amount_unionpay - Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "";
                discount = "0";
                fee_percentage = preferenceManager.getcnv_uni();

            } else {
                if (MyPOSMateApplication.isOpen) {
                    amount = convenience_amount_unionpay + "";
                    original_amount = original_xmpp_trigger_amount.replace(",", "");
                    fee_amount = convenience_amount_unionpay - Double.parseDouble(original_xmpp_trigger_amount.replace(",", "")) + "";
                    discount = "0";
                    fee_percentage = preferenceManager.getcnv_uni();
                } else {
                    amount = convenience_amount_unionpay + "";
                    original_amount = edt_amount.getText().toString().replace(",", "");
                    fee_amount = convenience_amount_unionpay - Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "";
                    discount = "0";
                    fee_percentage = preferenceManager.getcnv_uni();
                }

            }

            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";
            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("original_amount", roundTwoDecimals(Double.parseDouble(original_amount)));
            hashMapKeys.put("fee_amount", roundTwoDecimals(Double.parseDouble(fee_amount)));
            hashMapKeys.put("fee_percentage", roundTwoDecimals(Double.parseDouble(fee_percentage)));
            hashMapKeys.put("discount", discount);
            callDP(reference_id);


        } else if (preferenceManager.isConvenienceFeeSelected()
                && !preferenceManager.is_cnv_uni_display_and_add()) {
            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";
            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(edt_amount.getText().toString().replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(edt_amount.getText().toString().replace(",", ""))));
            callDP(reference_id);

        } else {
            if (MyPOSMateApplication.isOpen) {
                amount = original_xmpp_trigger_amount.replace(",", "");
            } else {
                amount = edt_amount.getText().toString().replace(",", "");
            }
            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";
            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            callDP(reference_id);

        }

    }


    public void callUnionPayQRScan() {
        trade_no = "";
        is_success = false;
        is_payment = false;
        server_response = "";

        if (countDownTimerxmpp != null) {
            countDownTimerxmpp.cancel();
            tv_start_countdown.setVisibility(View.GONE);
        }
        callAuthToken();
        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
        String amount = "";

        hashMapKeys.clear();
        if (!edt_reference.getText().toString().equals("")) {
            hashMapKeys.put("ref_data1", edt_reference.getText().toString());
        }
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
        hashMapKeys.put("system", preferenceManager.getterminalId());
        hashMapKeys.put("channel", "UNION_PAY");
        hashMapKeys.put("access_id", preferenceManager.getuniqueId());
        hashMapKeys.put("is_success", is_success + "");
        hashMapKeys.put("is_payment", is_payment + "");
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("random_str", new Date().getTime() + "");
        hashMapKeys.put("trade_no", trade_no);
        hashMapKeys.put("server_response", server_response);
        hashMapKeys.put("rate", "0");
        hashMapKeys.put("currency", "NZD");


        if (preferenceManager.isConvenienceFeeSelected()
                && preferenceManager.cnv_unionpayqr_display_and_add()) {
            if (preferenceManager.getcnv_uniqr().equals("") || preferenceManager.getcnv_uniqr().equals("0.0") || preferenceManager.getcnv_uniqr().equals("0.00")) {

                amount = convenience_amount_unionpayqrscan + "";
                original_amount = edt_amount.getText().toString().replace(",", "");
                fee_amount = convenience_amount_unionpayqrscan - Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "";
                discount = "0";
                fee_percentage = preferenceManager.getcnv_uniqr();

            } else {
                if (MyPOSMateApplication.isOpen) {
                    amount = convenience_amount_unionpayqrscan + "";
                    original_amount = original_xmpp_trigger_amount.replace(",", "");
                    fee_amount = convenience_amount_unionpayqrscan - Double.parseDouble(original_xmpp_trigger_amount.replace(",", "")) + "";
                    discount = "0";
                    fee_percentage = preferenceManager.getcnv_uniqr();
                } else {
                    amount = convenience_amount_unionpayqrscan + "";
                    original_amount = edt_amount.getText().toString().replace(",", "");
                    fee_amount = convenience_amount_unionpayqrscan - Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "";
                    discount = "0";
                    fee_percentage = preferenceManager.getcnv_uniqr();
                }

            }


            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";

            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("original_amount", roundTwoDecimals(Double.parseDouble(original_amount)));
            hashMapKeys.put("fee_amount", roundTwoDecimals(Double.parseDouble(fee_amount)));
            hashMapKeys.put("fee_percentage", roundTwoDecimals(Double.parseDouble(fee_percentage)));
            hashMapKeys.put("discount", discount);
            callDP(reference_id);


        } else if (preferenceManager.isConvenienceFeeSelected()
                && !preferenceManager.cnv_unionpayqr_display_and_add()) {

            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";
            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(edt_amount.getText().toString().replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(edt_amount.getText().toString().replace(",", ""))));
            callDP(reference_id);

        } else {
            if (MyPOSMateApplication.isOpen) {
                amount = original_xmpp_trigger_amount.replace(",", "");
            } else {
                amount = edt_amount.getText().toString().replace(",", "");
            }
            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";
            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            callDP(reference_id);
        }
        if (((DashboardActivity) getActivity()).getHashMapKeysUniversal() != null)
            ((DashboardActivity) getActivity()).getHashMapKeysUniversal().clear();
        if (((DashboardActivity) getActivity()).getHashMapKeysUniversal() != null)
            ((DashboardActivity) getActivity()).setHashMapKeysUniversal(hashMapKeys);
    }


    public void callUnionPayUPIQRScan() {
        openProgressDialog();
        if (countDownTimerxmpp != null) {
            countDownTimerxmpp.cancel();
            tv_start_countdown.setVisibility(View.GONE);
        }
        callAuthToken();
        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
        String amount = "";

        hashMapKeys.clear();
        if (!edt_reference.getText().toString().equals("")) {
            hashMapKeys.put("ref_data1", edt_reference.getText().toString());
        }
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
        hashMapKeys.put("system", preferenceManager.getterminalId());
        hashMapKeys.put("channel", "UNION_PAY");
        hashMapKeys.put("access_id", preferenceManager.getuniqueId());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("random_str", new Date().getTime() + "");


        if (preferenceManager.isConvenienceFeeSelected()
                && preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) {
            if (preferenceManager.getcnv_up_upiqr_mpmcloud_lower().equals("") || preferenceManager.getcnv_up_upiqr_mpmcloud_lower().equals("0.0") || preferenceManager.getcnv_up_upiqr_mpmcloud_lower().equals("0.00")) {

                amount = convenience_amount_unionpayqrdisplay + "";
                original_amount = edt_amount.getText().toString().replace(",", "");
                fee_amount = convenience_amount_unionpayqrdisplay - Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "";
                discount = "0";
                fee_percentage = preferenceManager.getcnv_up_upiqr_mpmcloud_lower();

            } else {
                if (MyPOSMateApplication.isOpen) {
                    amount = convenience_amount_unionpayqrdisplay + "";
                    original_amount = original_xmpp_trigger_amount.replace(",", "");
                    fee_amount = convenience_amount_unionpayqrdisplay - Double.parseDouble(original_xmpp_trigger_amount.replace(",", "")) + "";
                    discount = "0";
                    fee_percentage = preferenceManager.getcnv_up_upiqr_mpmcloud_lower();
                } else {
                    amount = convenience_amount_unionpayqrdisplay + "";
                    original_amount = edt_amount.getText().toString().replace(",", "");
                    fee_amount = convenience_amount_unionpayqrdisplay - Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "";
                    discount = "0";
                    fee_percentage = preferenceManager.getcnv_up_upiqr_mpmcloud_lower();
                }

            }


            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";

            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("original_amount", roundTwoDecimals(Double.parseDouble(original_amount)));
            hashMapKeys.put("fee_amount", roundTwoDecimals(Double.parseDouble(fee_amount)));
            hashMapKeys.put("fee_percentage", roundTwoDecimals(Double.parseDouble(fee_percentage)));
            hashMapKeys.put("discount", discount);
            hashMapKeys.put("qr_mode", false + "");
            hashMapKeys.put("auth_code", auth_code);

            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

            new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                    .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

        } else if (preferenceManager.isConvenienceFeeSelected()
                && !preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) {

            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";
            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(edt_amount.getText().toString().replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(edt_amount.getText().toString().replace(",", ""))));
            hashMapKeys.put("qr_mode", false + "");
            hashMapKeys.put("auth_code", auth_code);
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

            new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                    .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

        } else {
            if (MyPOSMateApplication.isOpen) {
                amount = original_xmpp_trigger_amount.replace(",", "");
            } else {
                amount = edt_amount.getText().toString().replace(",", "");
            }
            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";
            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("qr_mode", false + "");
            hashMapKeys.put("auth_code", auth_code);
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

            new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                    .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
        }

    }


    public void callUnionPayQRMerchantDisplay() {
        openProgressDialog();
        if (countDownTimerxmpp != null) {
            countDownTimerxmpp.cancel();
            tv_start_countdown.setVisibility(View.GONE);
        }
        callAuthToken();
        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
        String amount = "";

        hashMapKeys.clear();
        if (!edt_reference.getText().toString().equals("")) {
            hashMapKeys.put("ref_data1", edt_reference.getText().toString());
        }
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
        hashMapKeys.put("system", preferenceManager.getterminalId());
        hashMapKeys.put("channel", "UNION_PAY");
        hashMapKeys.put("access_id", preferenceManager.getuniqueId());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("random_str", new Date().getTime() + "");


        if (preferenceManager.isConvenienceFeeSelected()
                && preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) {
            if (preferenceManager.get_cnv_unimerchantqrdisplayLower().equals("") || preferenceManager.getcnv_up_upiqr_mpmcloud_lower().equals("0.0") || preferenceManager.get_cnv_unimerchantqrdisplayLower().equals("0.00")) {

                amount = convenience_amount_unionpayqr_merchant_display + "";
                original_amount = edt_amount.getText().toString().replace(",", "");
                fee_amount = convenience_amount_unionpayqr_merchant_display - Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "";
                discount = "0";
                fee_percentage = preferenceManager.get_cnv_unimerchantqrdisplayLower();

            } else {
                if (MyPOSMateApplication.isOpen) {
                    amount = convenience_amount_unionpayqr_merchant_display + "";
                    original_amount = original_xmpp_trigger_amount.replace(",", "");
                    fee_amount = convenience_amount_unionpayqr_merchant_display - Double.parseDouble(original_xmpp_trigger_amount.replace(",", "")) + "";
                    discount = "0";
                    fee_percentage = preferenceManager.get_cnv_unimerchantqrdisplayLower();
                } else {
                    amount = convenience_amount_unionpayqr_merchant_display + "";
                    original_amount = edt_amount.getText().toString().replace(",", "");
                    fee_amount = convenience_amount_unionpayqr_merchant_display - Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "";
                    discount = "0";
                    fee_percentage = preferenceManager.get_cnv_unimerchantqrdisplayLower();
                }

            }


            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";

            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("original_amount", roundTwoDecimals(Double.parseDouble(original_amount)));
            hashMapKeys.put("fee_amount", roundTwoDecimals(Double.parseDouble(fee_amount)));
            hashMapKeys.put("fee_percentage", roundTwoDecimals(Double.parseDouble(fee_percentage)));
            hashMapKeys.put("discount", discount);
            hashMapKeys.put("qr_mode", true + "");
//            hashMapKeys.put("auth_code", auth_code);

            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

            new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                    .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

        } else if (preferenceManager.isConvenienceFeeSelected()
                && !preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) {

            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";
            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(edt_amount.getText().toString().replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(edt_amount.getText().toString().replace(",", ""))));
            hashMapKeys.put("qr_mode", true + "");
//            hashMapKeys.put("auth_code", auth_code);
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

            new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                    .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

        } else {
            if (MyPOSMateApplication.isOpen) {
                amount = original_xmpp_trigger_amount.replace(",", "");
            } else {
                amount = edt_amount.getText().toString().replace(",", "");
            }
            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";
            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("qr_mode", true + "");
//            hashMapKeys.put("auth_code", auth_code);
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

            new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                    .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
        }

    }


    public void callUplan() {
        trade_no = "";
        is_success = false;
        is_payment = false;
        server_response = "";
        if (countDownTimerxmpp != null) {
            countDownTimerxmpp.cancel();
            tv_start_countdown.setVisibility(View.GONE);
        }
        callAuthToken();
        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
        String amount = "";

        hashMapKeys.clear();
        if (!edt_reference.getText().toString().equals("")) {
            hashMapKeys.put("ref_data1", edt_reference.getText().toString());
        }
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
        hashMapKeys.put("system", preferenceManager.getterminalId());
        hashMapKeys.put("channel", "UNION_PAY");
        hashMapKeys.put("access_id", preferenceManager.getuniqueId());
        hashMapKeys.put("is_success", is_success + "");
        hashMapKeys.put("is_payment", is_payment + "");
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("random_str", new Date().getTime() + "");
        hashMapKeys.put("trade_no", trade_no);
        hashMapKeys.put("server_response", server_response);
        hashMapKeys.put("rate", "0");
        hashMapKeys.put("currency", "NZD");


        if (preferenceManager.isConvenienceFeeSelected()
                && preferenceManager.cnv_uplan_display_and_add()) {
            if (preferenceManager.getcnv_uplan().equals("") || preferenceManager.getcnv_uplan().equals("0.0") || preferenceManager.getcnv_uplan().equals("0.00")) {

                amount = convenience_amount_uplan + "";
                original_amount = edt_amount.getText().toString().replace(",", "");
                fee_amount = convenience_amount_uplan - Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "";
                discount = "0";
                fee_percentage = preferenceManager.getcnv_uplan();

            } else {
                if (MyPOSMateApplication.isOpen) {
                    amount = convenience_amount_uplan + "";
                    original_amount = original_xmpp_trigger_amount.replace(",", "");
                    fee_amount = convenience_amount_uplan - Double.parseDouble(original_xmpp_trigger_amount.replace(",", "")) + "";
                    discount = "0";
                    fee_percentage = preferenceManager.getcnv_uplan();
                } else {
                    amount = convenience_amount_uplan + "";
                    original_amount = edt_amount.getText().toString().replace(",", "");
                    fee_amount = convenience_amount_uplan - Double.parseDouble(edt_amount.getText().toString().replace(",", "")) + "";
                    discount = "0";
                    fee_percentage = preferenceManager.getcnv_uplan();
                }

            }


            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";

            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("original_amount", roundTwoDecimals(Double.parseDouble(original_amount)));
            hashMapKeys.put("fee_amount", roundTwoDecimals(Double.parseDouble(fee_amount)));
            hashMapKeys.put("fee_percentage", roundTwoDecimals(Double.parseDouble(fee_percentage)));
            hashMapKeys.put("discount", discount);
            callDP(reference_id);

        } else if (preferenceManager.isConvenienceFeeSelected()
                && !preferenceManager.cnv_uplan_display_and_add()) {
            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";
            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(edt_amount.getText().toString().replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(edt_amount.getText().toString().replace(",", ""))));
            callDP(reference_id);


        } else {
            if (MyPOSMateApplication.isOpen) {
                amount = original_xmpp_trigger_amount.replace(",", "");
            } else {
                amount = edt_amount.getText().toString().replace(",", "");
            }
            if (!preferenceManager.gettriggerReferenceId().equals(""))
                reference_id = preferenceManager.gettriggerReferenceId();
            else
                reference_id = new Date().getTime() + "";
            hashMapKeys.put("reference_id", reference_id);
            hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(amount.replace(",", ""))));
            callDP(reference_id);
        }


    }


    public void callUnionPayStatus(String json_data, String status) {
        openProgressDialog();
        if (((DashboardActivity) getActivity()).getHashMapKeysUniversal() != null) {
            if (((DashboardActivity) getActivity()).getHashMapKeysUniversal().size() != 0) {
                hashMapKeys.putAll(((DashboardActivity) getActivity()).getHashMapKeysUniversal());
            }
        }


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
                        is_success = true;
                    } else if (jsonObject.optString("transactionType").equals("VOID") ||
                            jsonObject.optString("transactionType").equals("REFUND") ||
                            jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_VOID") ||
                            jsonObject.optString("transactionType").equals("COUPON_VOID")) {
                        status = "19"; //set 22 to 19 in case of void on 28/02/2019
                        is_success = true;
                    }

                } else {
                    status = "23";
                    is_success = false;
                    Toast.makeText(getActivity(), jsonObject.optString("responseMessage"), Toast.LENGTH_LONG).show();
                }
            } else {
                status = "23";
                is_success = false;
                Toast.makeText(getActivity(), jsonObject.optString("responseMessage"), Toast.LENGTH_LONG).show();

            }
            jsonObject.put("status_id", status);
            json_data = jsonObject.toString();
            preferenceManager.setreference_id(jsonObject.optString("orderNumber"));

            if (!preferenceManager.gettriggerReferenceId().equals(""))
                trade_no = preferenceManager.gettriggerReferenceId();
            else
                trade_no = jsonObject.optString("referenceNumber");


            if (trade_no.equals("null")) {
                trade_no = new Date().getTime() + "";
            }
            hashMapKeys.put("reference_id", trade_no);
            hashMapKeys.put("server_response", android.util.Base64.encodeToString((s + json_data + "}").getBytes(), Base64.NO_WRAP));
            hashMapKeys.put("trade_no", trade_no);
            hashMapKeys.put("is_success", is_success + "");
            hashMapKeys.put("is_payment", true + "");
            hashMapKeys.put("thirdParty", true + "");

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
            s2 = s2 + AppConstants.CLIENT_ID + PreferencesManager.getInstance(context).getauthToken();//.getuniqueId();
            String signature = MD5Class.MD5(s2);

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

            AppConstants.EXTERNAL_APP_UNIONPAY_RESPONSE = URLDecoder.decode(s + json_data + "}", "UTF-8");

            new OkHttpHandler(getActivity(), this, null, "unionpaystatus")
                    .execute(AppConstants.BASE_URL2 + AppConstants.SAVETRANSACTIONUNIONPAY + "?" + s1 + "&signature=" + signature + "&access_token=" + preferenceManager.getauthToken());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isTransactionDone = false;

    public void returnDataToExternalApp(TreeMap<String, String> hashMapKeys, String s) {
        try {

            //added for external apps
            int REQ_PAY_SALE = 100;
            if (DashboardActivity.isExternalApp) {
                DashboardActivity.isExternalApp = false;
                isTransactionDone = true;
                JSONObject jsonObjectData = new JSONObject();
                int i1 = 0;
                Iterator<String> iterator11 = hashMapKeys.keySet().iterator();
                while (iterator11.hasNext()) {
                    String key = iterator11.next();
                    if (i1 != hashMapKeys.size() - 1)
                        jsonObjectData.put(key, hashMapKeys.get(key));
                    else
                        jsonObjectData.put(key, hashMapKeys.get(key));
                    i1++;
                }
                jsonObjectData.remove("json_data");
                jsonObjectData.put("json_data", s);
                getActivity().getIntent().putExtra("result", jsonObjectData.toString());
                getActivity().setResult(REQ_PAY_SALE, getActivity().getIntent());
                getActivity().finishAndRemoveTask();
                return;
            }  //added for external apps

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callUpdateRequestAPI1(String request_id, boolean executed) {
        openProgressDialog();
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


    public void callTransactionDetails() {
        hashMapKeys.clear();
        hashMapKeys.put("access_id", preferenceManager.getuniqueId());
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("reference_id", reference_id);
        hashMapKeys.put("random_str", new Date().getTime() + "");
        new OkHttpHandler(getActivity(), this, null, "TransactionDetails")
                .execute(AppConstants.BASE_URL2 + AppConstants.GET_TRANSACTION_DETAILS + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
    }


    String TransactionType = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      /* if (IntentIntegrator.REQUEST_CODE == requestCode) {

           IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
           if (result != null) {
               if (result.getContents() == null) {
                   Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
               } else {
                   String buyerIdentityCode = data != null ? data.getStringExtra("SCAN_RESULT") : null;
                   if (buyerIdentityCode != null && !buyerIdentityCode.equalsIgnoreCase("")) {
                       Toast.makeText(getActivity(), "code " + buyerIdentityCode, Toast.LENGTH_SHORT).show();
*//*
                        Intent intent = new Intent();
                        intent.setAction(ConstantValue.FILTER_PRODUCT_SCAN);
                        intent.putExtra("Barcode", buyerIdentityCode);
                        sendBroadcast(intent);*//*
//                        }

                   } else {
                       Toast.makeText(getActivity(), "Invalid barcode/UID", Toast.LENGTH_SHORT).show();
                   }
               }
           }
       }
*/
        if (data!=null) {

            if (data.hasExtra("responseCodeThirtyNine")) {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putAll(data.getExtras());
                    JSONObject json = new JSONObject();
                    Set<String> keys = bundle.keySet();
                    for (String key : keys) {
                        // if (bundle.get(key) != null)
                        json.put(key, JSONObject.wrap(bundle.get(key)));
                    }

                    switch (TransactionType) {

                        case ThirtConst.TransType.SALE:
                            json.put("orderNumber", jsonObjectSale.optString("orderNumber"));
                            break;
                        case ThirtConst.TransType.COUPON_SALE:
                            json.put("orderNumber", jsonObjectCouponSale.optString("orderNumber"));
                            break;
                    }


                    if (json.optString("responseCodeThirtyNine").equals("00")) {
                        onTaskCompleted(json.toString(), "Arke");
                    } else {
                        onTaskCompleted(json.toString(), "Arke");
                        auth_code = "";
                        isUnionPayQrSelected = false;
                        isUpayselected = false;
                        reference_id = "";
                        preferenceManager.setreference_id("");
                        callAuthToken();
                    }


                } catch (Exception e) {
                    //Handle exception here

                }

            }

            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(getActivity(), "Result Not Found", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject obj = new JSONObject(result.getContents());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }else{
            Toast.makeText(getActivity(), "Application is not installed", Toast.LENGTH_LONG).show();
        }

    }


    public void callMembershipLoyality(String qr_data) {
        openProgressDialog();
        hashMapKeys.clear();
        hashMapKeys.put("access_id", preferenceManager.getuniqueId());
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("device_id", UUID.randomUUID().toString().replace("-", ""));
        hashMapKeys.put("qr_data", qr_data);
        hashMapKeys.put("random_str", new Date().getTime() + "");
        hashMapKeys.put("lane_id", preferenceManager.getLaneIdentifier());
        hashMapKeys.put("pos_id", preferenceManager.getPOSIdentifier());
        new OkHttpHandler(getActivity(), this, null, "saveLoyaltyInfo")
                .execute(AppConstants.BASE_URL2 + AppConstants.SAVE_LOYALTY_INFO + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
    }


    public void callDisplayStaticQRDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater lf = (LayoutInflater) (getActivity())
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.layout_alipay_static_qr, null);

        Button btn_close = dialogview.findViewById(R.id.btn_close);
        dialog.setContentView(dialogview);


        btn_close.setOnClickListener((View v) ->
                {
                    dialog.dismiss();

                }
        );

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;


        dialog.getWindow().setAttributes(lp);
        dialog.show();


    }


    public static String selected_channel = "";

    public void dialogQR() {
        callAuthToken();
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.qrdialog);
//        dialog.setTitle("Select Channel");
        dialog.setCancelable(false);

        Button btn_alipay = (Button) dialog.findViewById(R.id.btn_alipay);
        Button btn_wechat = (Button) dialog.findViewById(R.id.btn_wechat);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_channel = "WECHAT";
                isUnionPayQrSelected = false;
                isUpayselected = false;
                if (MyPOSMateApplication.isOpen) {
                    payment_mode = "";
                    qrMode = "False";
                    open();
                } else {
                    if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("") && edt_reference.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
                    } else {
                        payment_mode = "";
                        qrMode = "False";
                        open();
                    }
                }

                dialog.dismiss();
            }
        });


        btn_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_channel = "ALIPAY";
                isUnionPayQrSelected = false;
                isUpayselected = false;
                if (MyPOSMateApplication.isOpen) {
                    payment_mode = "";
                    qrMode = "False";
                    open();
                } else {
                    if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("") && edt_reference.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
                    } else {
                        payment_mode = "";
                        qrMode = "False";
                        open();
                    }
                }
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    public static boolean isUpayselected = false;
    public static boolean isunionPayQrScanSelectedForSale = false;
    public static boolean isUnionPayQrSelected = false;
    public static boolean isBack = false;
    public static boolean isFront = false;
    public static String unionpay_payment_option = "";
    private Context mContext;
    String channel = "";
    boolean isPoliSelected = false;
    boolean isCentrapayMerchantQRDisplaySelected = false;

    @Override
    public void onClick(View view) {
        mContext = getActivity();

        if (((DashboardActivity) mContext).mPopupWindow.isShowing())
            ((DashboardActivity) mContext).mPopupWindow.dismiss();


        switch (view.getId()) {


            case R.id.img_centrapay_merchant_qr_display:
                _funcCentrapayMerchantQrDisplay();
                break;

            case R.id.img_centerpay_scanqr:
                _funcCentraPayScanQR();
                break;

            case R.id.rel_alipay_static_qr:
                callDisplayStaticQRDialog();
                break;

            case R.id.btn_front:
            case R.id.btn_front1:
                _funcFrontCameraScan();
                break;

            case R.id.btn_loyalty_apps:
                ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.LOYALTY_APPS, null);
                break;

            case R.id.tv_status_scan_button:
                try {
                    if (preferenceManager.getLaneIdentifier().equals("")) {
                        Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
                    } else if (preferenceManager.getPOSIdentifier().equals("")) {
                        Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
                    } else {
                        isFront = true;
                        callAuthToken();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;

            case R.id.btn_back:
            case R.id.btn_back1:
                _funcBackButton();
                break;


            case R.id.scanqr:
                if (preferenceManager.isExternalScan())
                {
                    _funcAlipayWeChatQRScanExternal();
                  /*  showScanDialog();
                    Toast.makeText(getActivity(),"External Input Device is enabled",Toast.LENGTH_SHORT).show();
              */  }else{
                    _funcAlipayWeChatQRScan();
                }

               /* TelephonyManager manager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                if (Objects.requireNonNull(manager).getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
                    Toast.makeText(getActivity(),"Scanner is not available",Toast.LENGTH_SHORT).show();
                }else if (Objects.requireNonNull(manager).getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
                        Toast.makeText(getActivity(),"Scanner is not available",Toast.LENGTH_SHORT).show();
                    }else {
                        _funcAlipayWeChatQRScan();
                }*/
               /* if (preferenceManager.isExternalScan())
                {

                    Toast.makeText(getActivity(),"External Input Device is enabled",Toast.LENGTH_SHORT).show();
                }else{
                // _funcAlipayWeChatQRScan();
                    _funcAlipayWeChatQRScanQR();
                 //performBarcodeScan();
                    Toast.makeText(getActivity(),"External Input Device is disabled",Toast.LENGTH_SHORT).show();
                }*/

                break;

            case R.id.btn_save1:
            case R.id.img_wechat:
                _funcWeChat();
                break;

            case R.id.img_alipay:
                _funcAlipay();
                break;

            case R.id.img_poli:
                _funcPoli();
                break;


            case R.id.btn_cancel:
            case R.id.btn_cancel1:
                _funcCancelButton();
                break;

            case R.id.scanqr_unionpay:
               /* TelephonyManager managers = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                if (Objects.requireNonNull(managers).getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
                  Toast.makeText(getActivity(),"Scanner is not available",Toast.LENGTH_SHORT).show();
                  }else if (Objects.requireNonNull(managers).getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
                    Toast.makeText(getActivity(),"Scanner is not available",Toast.LENGTH_SHORT).show();
                }else {
*/
                if (preferenceManager.isExternalScan())
                {
                    if (preferenceManager.isUnionPayQrCodeDisplaySelected())
                        _funcUPIQRScanExternal();
                   /* else if (preferenceManager.isUnionPayQrSelected())
                        _funcDPQRScanExternal();*/
                  /*  showScanUnionPayDialog();
                    Toast.makeText(getActivity(),"External Input Device is enabled",Toast.LENGTH_SHORT).show();
              */  }else{
                    if (preferenceManager.isUnionPayQrCodeDisplaySelected())
                        _funcUPIQRScan();
                  /*  else if (preferenceManager.isUnionPayQrSelected())
                        _funcDPQRScan();*/
                }

              //  }
                break;

            case R.id.img_upay:
                _funcDPUplanForCoupon();
                break;

            case R.id.img_unipay:
                _funcDPCard();
                break;

            case R.id.img_unionpay_qr:
                isMerchantQrDisplaySelected = true;
                _funcUnionPayQRMerchantDisplay();
                break;

        }
    }

    private void _funcDPQRScanExternal() {
        //Scan customer wallet qr code and perfom
        //transaction through Dynamic Pay App
        unionpay_payment_option = "DP-QR";
        selected_screen = 4;
        isUpayselected = false;
        if (preferenceManager.getunion_pay_resp().equals("")) {
            payment_mode = "";
            qrMode = "False";
            preferenceManager.setupay_amount(edt_amount.getText().toString());
            xmppAmount = convenience_amount_unionpayqrscan + "";
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {
                isUnionPayQrSelected = true;
                isunionPayQrScanSelectedForSale = true;
                auth_code = "";
                callUnionPayQRScan();
            }
        } else {
            AppConstants.isUnionQrSelected = true;
            callUnionPayStatus(preferenceManager.getunion_pay_resp(), "true");
        }

    }

    private void _funcUPIQRScanExternal() {
        //Scan customer wallet qr code and perfom
        //transaction through MyPOSMate cloud
        unionpay_payment_option = "UPI-QRScan";
        selected_screen = 4;
        isUpayselected = false;
        isUnionPayQrSelected = true;
        if (preferenceManager.getunion_pay_resp().equals("")) {
            payment_mode = "";
            qrMode = "False";
            preferenceManager.setupay_amount(edt_amount.getText().toString());
            xmppAmount = convenience_amount_unionpayqrdisplay + "";

            if (MyPOSMateApplication.isOpen) {
                payment_mode = "UPI-QRScan";
                qrMode = "False";
                showScanUnionPayDialog();
            } else {
                if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("") && edt_reference.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
                } else {
                    payment_mode = "UPI-QRScan";
                    qrMode = "False";
                   showScanUnionPayDialog();
                }
            }

        } else {
            AppConstants.isUnionQrSelected = true;
            callUnionPayStatus(preferenceManager.getunion_pay_resp(), "true");
        }
    }

    private void showScanAlipayDialog() {//ScanAlipay
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            LayoutInflater lf = (LayoutInflater) (getActivity())
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogview = lf.inflate(R.layout.scanner_dialog, null);
            TextView title = (TextView) dialogview.findViewById(R.id.title);
           // title.setText("Please Enter Your ConfigId");
            EditText body = (EditText) dialogview
                    .findViewById(R.id.dialogBody);
            dialog.setContentView(dialogview);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);
            dialog.show();
            TextView cancel = (TextView) dialogview
                .findViewById(R.id.tv_cancel);

            TextView ok = (TextView) dialogview
                .findViewById(R.id.tv_ok);

           body.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                String barcode = body.getText().toString().trim();
                // Toast.makeText(getActivity(),""+barcode,Toast.LENGTH_SHORT).show();

                if (keyCode == KeyEvent.KEYCODE_ENTER && barcode.length() > 0) {
                    Log.v("SCANNES",barcode);
                    body.setText(barcode);

                            ok.performClick();

                    return true;
                }

                return false;
            }
        });

            ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String barcode= body.getText().toString().trim();
                    if (barcode.equalsIgnoreCase("")||barcode.isEmpty())
                    {
                        Toast.makeText(getActivity(),"Please enter the code in input box",Toast.LENGTH_SHORT).show();
                    }else {
                        ProgressDialog p = new ProgressDialog(mmContext == null ? getActivity() : mmContext);
                        p.setMessage("Sending request.......");
                        p.setCancelable(false);
                        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        p.setIndeterminate(true);
                        p.show();
                        final Handler handlers = new Handler();
                        handlers.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!AppConstants.xmppamountforscan.equals("")) {

//                    when user get the amount xmpp trigger and if the scan qr button is pressed
//                    of alipay and wechat then on launch of qr scan screen the xmpp trigger state
//                    of manual entry screen gets reset.So the further procedure gets stuck as all the values are reset to zero.
//                    To avoid the value reset we would again initiate the xmpp amount json data on manual entry screen
//                    so that all the values will be calculated once the qr is scanned from wallet.And
//                    once you reach here we will fire a amount trigger broadcast and then send the scanned code so that the payNow procedure works properly

                                    final Handler handler1 = new Handler();
                                    handler1.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent ia = new Intent();
                                            ia.setAction("AmountTrigger");
                                            ia.putExtra("data", preferenceManager.getamountdata());
                                            getActivity().sendBroadcast(ia);

                                        }
                                    }, 400);

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            Toast.makeText(getActivity(), "Scanned: " + body.getText().toString(), Toast.LENGTH_LONG).show();
                                            String identityCode = body.getText().toString();
                                            Intent i = new Intent();
                                            if (ManualEntry.isUpayselected) {
                                                AppConstants.isScannedCode1 = false;
                                                i.setAction("ScannedCode");
                                            } else if (ManualEntry.isUnionPayQrSelected) {
                                                AppConstants.isScannedCode1 = false;
                                                i.setAction("ScannedCodeUnionPayQr");
                                            } else {
                                                i.setAction("ScannedCode1");
                                                AppConstants.isScannedCode1 = true;
                                            }

                                            i.putExtra("identityCode", identityCode);
                                            Log.v("AUTHCODE", "Authcodeac " + i.getAction());
                                            getActivity().sendBroadcast(i);
                                            if (p != null && p.isShowing()) {
                                                p.dismiss();
                                            }
                                            dialog.dismiss();
                                        }
                                    }, 800);

                                } else {
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), "Scanned : " + body.getText().toString(), Toast.LENGTH_LONG).show();
                                            String identityCode = body.getText().toString();
                                            Intent i = new Intent();
                                            if (ManualEntry.isUpayselected) {
                                                AppConstants.isScannedCode1 = false;
                                                i.setAction("ScannedCode");
                                            } else if (ManualEntry.isUnionPayQrSelected) {
                                                AppConstants.isScannedCode1 = false;
                                                i.setAction("ScannedCodeUnionPayQr");
                                            } else {
                                                i.setAction("ScannedCode1");
                                                AppConstants.isScannedCode1 = true;
                                            }
                                            Log.v("AUTHCODE", "Authcodeac2 " + i.getAction());
                                            i.putExtra("identityCode", identityCode);
                                            getActivity().sendBroadcast(i);
                                            if (p != null && p.isShowing()) {
                                                p.dismiss();
                                            }
                                            dialog.dismiss();
                                        }
                                    }, 500);

                                }

                            }

                        }, 1000);
                        dialog.dismiss();
                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

    }

    private void showScanUnionPayDialog() {//ScanUnionpay
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater lf = (LayoutInflater) (getActivity())
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.scanner_dialog, null);
        TextView title = (TextView) dialogview.findViewById(R.id.title);
        // title.setText("Please Enter Your ConfigId");
        EditText body = (EditText) dialogview
                .findViewById(R.id.dialogBody);
        dialog.setContentView(dialogview);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        dialog.show();
        TextView ok = (TextView) dialogview
                .findViewById(R.id.tv_ok);
        body.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                String barcode = body.getText().toString();
                // Toast.makeText(getActivity(),""+barcode,Toast.LENGTH_SHORT).show();

                if (keyCode == KeyEvent.KEYCODE_ENTER && barcode.length() > 0) {
                    body.setText(barcode);
                       ok.performClick();
                    return true;
                }

                return false;
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String barcode= body.getText().toString().trim();
                if (barcode.equalsIgnoreCase("")||barcode.isEmpty())
                {
                    Toast.makeText(getActivity(),"Please enter the code in input box",Toast.LENGTH_SHORT).show();
                }else {
                    final Handler handlers = new Handler();
                    handlers.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!AppConstants.xmppamountforscan.equals("")) {

                                final Handler handler1 = new Handler();
                                handler1.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent ia = new Intent();
                                        ia.setAction("AmountTrigger");
                                        ia.putExtra("data", preferenceManager.getamountdata());
                                        getActivity().sendBroadcast(ia);

                                    }
                                }, 400);

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Scanned: " + body.getText().toString(), Toast.LENGTH_LONG).show();
                                        String identityCode = body.getText().toString();
                                        Intent i = new Intent();
                                        if (ManualEntry.isUpayselected) {
                                            AppConstants.isScannedCode1 = false;
                                            i.setAction("ScannedCode");
                                        } else if (ManualEntry.isUnionPayQrSelected) {
                                            AppConstants.isScannedCode1 = false;
                                            i.setAction("ScannedCodeUnionPayQr");
                                        } else {
                                            i.setAction("ScannedCode1");
                                            AppConstants.isScannedCode1 = true;
                                        }

                                        i.putExtra("identityCode", identityCode);
                                        Log.v("AUTHCODE", "Authcodeac " + i.getAction());
                                        getActivity().sendBroadcast(i);
                                    }
                                }, 800);

                            } else {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Scanned else : " + body.getText().toString(), Toast.LENGTH_LONG).show();
                                        String identityCode = body.getText().toString();
                                        Intent i = new Intent();
                                        if (ManualEntry.isUpayselected) {
                                            AppConstants.isScannedCode1 = false;
                                            i.setAction("ScannedCode");
                                        } else if (ManualEntry.isUnionPayQrSelected) {
                                            AppConstants.isScannedCode1 = false;
                                            i.setAction("ScannedCodeUnionPayQr");
                                        } else {
                                            i.setAction("ScannedCode1");
                                            AppConstants.isScannedCode1 = true;
                                        }
                                        Log.v("AUTHCODE", "Authcodeac2 " + i.getAction());
                                        i.putExtra("identityCode", identityCode);
                                        getActivity().sendBroadcast(i);
                                    }
                                }, 500);

                            }
                        }
                    }, 1000);
                    dialog.dismiss();
                }
            }
        });
        TextView cancel = (TextView) dialogview
                .findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

    }
    private void showScanBackCameraDialog() {//ScanBack Camera
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater lf = (LayoutInflater) (getActivity())
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.scanner_dialog, null);
        TextView title = (TextView) dialogview.findViewById(R.id.title);
        // title.setText("Please Enter Your ConfigId");
        EditText body = (EditText) dialogview
                .findViewById(R.id.dialogBody);
        dialog.setContentView(dialogview);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        TextView cancel = (TextView) dialogview
                .findViewById(R.id.tv_cancel);

        TextView ok = (TextView) dialogview
                .findViewById(R.id.tv_ok);

        body.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                String barcode = body.getText().toString().trim();
                // Toast.makeText(getActivity(),""+barcode,Toast.LENGTH_SHORT).show();

                if (keyCode == KeyEvent.KEYCODE_ENTER && barcode.length() > 0) {
                    Log.v("SCANNES",barcode);
                    body.setText(barcode);
                    ok.performClick();
                    return true;
                }

                return false;
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String auth_code = body.getText().toString();
                if (auth_code.equalsIgnoreCase("")||auth_code.isEmpty())
                {
                Toast.makeText(getActivity(),"Please enter the code in input box",Toast.LENGTH_SHORT).show();
                }else {
                    ProgressDialog p = new ProgressDialog(mmContext == null ? getActivity() : mmContext);
                    p.setMessage("Sending request.......");
                    p.setCancelable(false);
                    p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    p.setIndeterminate(true);
                    p.show();
                    final Handler handlers = new Handler();
                    handlers.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(p!=null&&p.isShowing()) {
                                p.dismiss();
                            }
                            long SuccessEndTime = System.currentTimeMillis();
                            // long SuccessCostTime = SuccessEndTime - startTime;
                            if (getActivity() != null)
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {

                                        if (preferenceManager.getLaneIdentifier().equals("")) {
                                            Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
                                        } else if (preferenceManager.getPOSIdentifier().equals("")) {
                                            Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
                                        } else {
                                            callMembershipLoyality(auth_code);
                                            Toast.makeText(getActivity(), auth_code + "", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });


                        }

                    }, 1000);
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
    private void _funcAlipayWeChatQRScanQR() {
        //Scanning qr from customer wallet and performing transaction
        //through MyPOSMate cloud
        isUnionPayQrSelected = false;
        isUpayselected = false;
        if (MyPOSMateApplication.isOpen) {
            payment_mode = "";
            qrMode = "False";
            open();
        } else {
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("") && edt_reference.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {
                payment_mode = "";
                qrMode = "False";
                open();
            }
        }
    }


    FrameLayout surfaceViewContainer;
    private void switchBarcodeUI(boolean b) {
        try {
            if (b) {
                getActivity().findViewById(R.id.frameSurfaceView).setVisibility(View.VISIBLE);
                surfaceViewContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("","");
                    }
                });
            } else{
                getActivity().findViewById(R.id.frameSurfaceView).setVisibility(View.GONE);
                surfaceViewContainer.removeAllViews();
//               alternateBarcodeScanner.removeCallBack();
                surfaceViewContainer.setOnClickListener(null);
             //  alternateBarcodeScanner = null;
                surfaceViewContainer = null;
            }
        }catch (Exception e){
            e.printStackTrace();
          //  alternateBarcodeScanner = null;
            surfaceViewContainer = null;
        }
    }


    private void performBarcodeScan() {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
      /*  surfaceViewContainer = (FrameLayout) getActivity().findViewById(R.id.surfaceView);
        if(surfaceViewContainer != null){
            SurfaceView cameraSurface = new SurfaceView(getActivity());
            surfaceViewContainer.addView(cameraSurface);
            switchBarcodeUI(true);
            getActivity().findViewById(R.id.imgCloseCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchBarcodeUI(false);
                }
            });*/
          /*alternateBarcodeScanner = new AlternateBarcodeScanner(getActivity(), cameraSurface,
                    new AlternateBarcodeScanner.BarcodeListener() {
                        @Override
                        public void onBarcodeReceived(@NotNull String barcodeValue) {

                           *//* Intent intent = new Intent();
                            intent.setAction(ConstantValue.FILTER_PRODUCT_SCAN);
                            intent.putExtra("Barcode", barcodeValue);
                            getActivity().sendBroadcast(intent);*//*
                        }
                    });
            alternateBarcodeScanner.initialiseDetectorsAndSources();
*/
      //  }else{
           /* String selected=mPreferences.getString(ConstantValue.SCANNEROPTION);
            if(selected.equals(""))
            {
                mPreferences.putString(ConstantValue.SCANNEROPTION,"CAMERA");
            }
            if(mPreferences.getString(ConstantValue.SCANNEROPTION).equals("CAMERA"))
            {*/
              /*  IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setOrientationLocked(false);
                integrator.initiateScan();*/
           /* }else{
                Toast.makeText(getActivity(),"Barcode Scanner.. In Progress",Toast.LENGTH_SHORT).show();
            }*/

            /*IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.setOrientationLocked(false);
            integrator.initiateScan();*/
     //   }
    }

    public static boolean isMerchantQrDisplaySelected = false;


    private void _funcUnionPayQRMerchantDisplay() {
        //Scan merchant qr code and perfom
        //transaction through MyPOSMate cloud
        unionpay_payment_option = "DPMerchantQRScan";
        selected_screen = 4;
        AppConstants.isUnionPayMerchantQrDisplaySelected = true;
        isUpayselected = false;
        if (preferenceManager.getunion_pay_resp().equals("")) {
            preferenceManager.setupay_amount(edt_amount.getText().toString());
            if (preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add())
                xmppAmount = convenience_amount_unionpayqr_merchant_display + "";
            else
                xmppAmount = edt_amount.getText().toString();


            channel = "UNION_PAY";
            if (MyPOSMateApplication.isOpen) {

                payment_mode = "UNION_PAY";
                qrMode = "True";
                callUnionPayQRMerchantDisplay();
            } else {
                if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("") && edt_reference.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
                } else {
                    payment_mode = "UNION_PAY";
                    qrMode = "True";
                    callUnionPayQRMerchantDisplay();
                }
            }

        } else {
            AppConstants.isUnionPayMerchantQrDisplaySelected = true;
            callUnionPayStatus(preferenceManager.getunion_pay_resp(), "true");
        }

    }


    private void _funcUPIQRScan() {
        //Scan customer wallet qr code and perfom
        //transaction through MyPOSMate cloud
        unionpay_payment_option = "UPI-QRScan";
        selected_screen = 4;
        isUpayselected = false;
        isUnionPayQrSelected = true;
        if (preferenceManager.getunion_pay_resp().equals("")) {
            payment_mode = "";
            qrMode = "False";
            preferenceManager.setupay_amount(edt_amount.getText().toString());
            xmppAmount = convenience_amount_unionpayqrdisplay + "";

            if (MyPOSMateApplication.isOpen) {
                payment_mode = "UPI-QRScan";
                qrMode = "False";
                open();
            } else {
                if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("") && edt_reference.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
                } else {
                    payment_mode = "UPI-QRScan";
                    qrMode = "False";
                    open();
                }
            }

        } else {
            AppConstants.isUnionQrSelected = true;
            callUnionPayStatus(preferenceManager.getunion_pay_resp(), "true");
        }
    }


    private void _funcDPCard() {
        //Perform UnionPay Transaction using
        //Dynamic Pay appplication
        selected_screen = 4;
        unionpay_payment_option = "DP-Card";
        isUpayselected = false;
        isUnionPayQrSelected = false;
        if (preferenceManager.getunion_pay_resp().equals("")) {
            payment_mode = "";
            qrMode = "False";
            xmppAmount = convenience_amount_unionpay + "";
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {

                callUnionPay();
            }
        } else {
            AppConstants.isUnionpayselected = true;
            callUnionPayStatus(preferenceManager.getunion_pay_resp(), "true");
        }
    }


    private void _funcDPUplanForCoupon() {
        //Scan coupon and perform transaction using
        //Dynamic Pay app
        unionpay_payment_option = "DP-Uplan";
        selected_screen = 4;
        isunionPayQrScanSelectedForSale = false;
        if (preferenceManager.getunion_pay_resp().equals("")) {
            payment_mode = "";
            qrMode = "False";
            preferenceManager.setupay_amount(edt_amount.getText().toString());
            xmppAmount = convenience_amount_uplan + "";
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {
                isUpayselected = true;
                auth_code = "";
                callUplan();
            }
        } else {
            AppConstants.isUplanselected = true;
            callUnionPayStatus(preferenceManager.getunion_pay_resp(), "true");
        }

    }

    private void _funcDPQRScan() {
        //Scan customer wallet qr code and perfom
        //transaction through Dynamic Pay App
        unionpay_payment_option = "DP-QR";
        selected_screen = 4;
        isUpayselected = false;
        if (preferenceManager.getunion_pay_resp().equals("")) {
            payment_mode = "";
            qrMode = "False";
            preferenceManager.setupay_amount(edt_amount.getText().toString());
            xmppAmount = convenience_amount_unionpayqrscan + "";
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {
                isUnionPayQrSelected = true;
                isunionPayQrScanSelectedForSale = true;
                auth_code = "";
                callUnionPayQRScan();
            }
        } else {
            AppConstants.isUnionQrSelected = true;
            callUnionPayStatus(preferenceManager.getunion_pay_resp(), "true");
        }

    }

    public void _funcCentraPayScanQR() {

    }


    private void _funcCentrapayMerchantQrDisplay() {
        //Perform alipay transaction by generating QR code on terminal
        isCentrapayMerchantQRDisplaySelected = true;
        channel = "CENTRA_PAY";
        selected_screen = 2;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        View view2 = getActivity().getCurrentFocus();

        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (selected_screen == 0) {
            Toast.makeText(getActivity(), "Please select the payment type", Toast.LENGTH_LONG).show();
            return;
        }

        if (preferenceManager.is_cnv_centrapay_display_and_add())
            xmppAmount = convenience_amount_centrapay_merchant_qr_display + "";
        else
            xmppAmount = edt_amount.getText().toString();


        payment_mode = "CENTRA_PAY";
        qrMode = "True";
        auth_code = "";
        if (MyPOSMateApplication.isOpen) {
            callTerminalPayCentrapayMerchantQrDisplay();
        } else {
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {
                callTerminalPayCentrapayMerchantQrDisplay();
            }
        }

    }


    private void _funcPoli() {
        //Perform alipay transaction by generating QR code on terminal
        isPoliSelected = true;
        channel = "POLI";
        selected_screen = 2;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        View view2 = getActivity().getCurrentFocus();

        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (selected_screen == 0) {
            Toast.makeText(getActivity(), "Please select the payment type", Toast.LENGTH_LONG).show();
            return;
        }

        if (preferenceManager.is_cnv_poli_display_and_add())
            xmppAmount = convenience_amount_poli + "";
        else
            xmppAmount = edt_amount.getText().toString();


        payment_mode = "POLI";
        qrMode = "True";
        auth_code = "";
        if (MyPOSMateApplication.isOpen) {
            callTerminalPayPoli();
        } else {
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {
                callTerminalPayPoli();
            }
        }

    }


    private void _funcAlipay() {
        //Perform alipay transaction by generating QR code on terminal
        channel = "ALIPAY";
        selected_screen = 2;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        View view2 = getActivity().getCurrentFocus();

        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (selected_screen == 0) {
            Toast.makeText(getActivity(), "Please select the payment type", Toast.LENGTH_LONG).show();
            return;
        }

        if (preferenceManager.is_cnv_alipay_display_and_add())
            xmppAmount = convenience_amount_alipay + "";
        else
            xmppAmount = edt_amount.getText().toString();


        payment_mode = "ALIPAY";
        qrMode = "True";
        auth_code = "";
        if (MyPOSMateApplication.isOpen) {
            callTerminalPayAlipay();
        } else {
            if (preferenceManager.isaggregated_singleqr() && !preferenceManager.isUnipaySelected()) {
            }
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {
                callTerminalPayAlipay();
            }
        }

    }

    private void _funcWeChat() {
        //Perform wechat transaction by generating QR code on terminal
        channel = "WECHAT";
        selected_screen = 2;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        View view1 = getActivity().getCurrentFocus();

        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (selected_screen == 0) {
            Toast.makeText(getActivity(), "Please select the payment type", Toast.LENGTH_LONG).show();
            return;
        }

        if (preferenceManager.is_cnv_wechat_display_and_add())
            xmppAmount = convenience_amount_wechat + "";
        else
            xmppAmount = edt_amount.getText().toString();


        payment_mode = "WECHAT";
        qrMode = "True";
        auth_code = "";
        if (MyPOSMateApplication.isOpen) {
            callTerminalPayWeChat();

        } else {
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {
                callTerminalPayWeChat();
            }
        }
    }


    private void _funcAlipayWeChatQRScan() {
        //Scanning qr from customer wallet and performing transaction
        //through MyPOSMate cloud
        isUnionPayQrSelected = false;
        isUpayselected = false;
        if (MyPOSMateApplication.isOpen) {
            payment_mode = "";
            qrMode = "False";
            open();
        } else {
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("") && edt_reference.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {
                payment_mode = "";
                qrMode = "False";
                open();
            }
        }
    }


    private void _funcAlipayWeChatQRScanExternal() {
        //Scanning qr from customer wallet and performing transaction
        //through MyPOSMate cloud
        isUnionPayQrSelected = false;
        isUpayselected = false;
        if (MyPOSMateApplication.isOpen) {
            payment_mode = "";
            qrMode = "False";
         showScanAlipayDialog();
            Toast.makeText(getActivity(),"External Input Device is enabled",Toast.LENGTH_SHORT).show();

        } else {
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("") && edt_reference.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {
                payment_mode = "";
                qrMode = "False";
                showScanAlipayDialog();
                Toast.makeText(getActivity(),"External Input Device is enabled",Toast.LENGTH_SHORT).show();

            }
        }
    }


    boolean isTriggerCancelled = false;

    private void _funcCancelButton() {
        if (DashboardActivity.isExternalApp) {
            TransactionDetailsActivity.isReturnFromTransactionDetails = false;
            try {
                isTransactionDone = true;
                //added for external apps 12/5/2019
                int REQ_PAY_SALE = 100;
                DashboardActivity.isExternalApp = false;
                getActivity().getIntent().putExtra("result", new JSONObject().toString());
                getActivity().setResult(REQ_PAY_SALE, getActivity().getIntent());
                getActivity().finishAndRemoveTask();
                return;
                //added for external apps


            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        if (countDownTimerxmpp != null) {
            countDownTimerxmpp.cancel();
            tv_start_countdown.setVisibility(View.GONE);
            AppConstants.xmppamountforscan = "";
        }
        AppConstants.xmppamountforscan = "";
        edt_amount.setText("");
        edt_reference.setText("");
        edt_amount1.setText("");
        edt_reference1.setText("");
        if (MyPOSMateApplication.isOpen) {
            MyPOSMateApplication.isOpen = false;
            MyPOSMateApplication.isActiveQrcode = false;
            isTriggerCancelled = true;
            if (isTriggerCancelled) {
                isTrigger = false;
                callAuthToken();
            }

        }
        MyPOSMateApplication.isOpen = false;
        MyPOSMateApplication.isActiveQrcode = false;
    }

    private void _funcBackButton() {
        edt_amount.setText("0.00");
        edt_amount1.setText("0.00");
        try {
            if (preferenceManager.getLaneIdentifier().equals("")) {
                Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
            } else if (preferenceManager.getPOSIdentifier().equals("")) {
                Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
            } else {
                isBack = true;
                callAuthToken();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void _funcFrontCameraScan() {
        edt_amount.setText("0.00");
        edt_amount1.setText("0.00");
        try {
            if (preferenceManager.getLaneIdentifier().equals("")) {
                Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
            } else if (preferenceManager.getPOSIdentifier().equals("")) {
                Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
            } else {
                isFront = true;
                callAuthToken();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open() {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setOrientationLocked(true);
        integrator.initiateScan();


    }

    private Dialog dialog, dialog1;

    public void showDialog(String text) {
        dialog = new Dialog(getActivity());
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

                if (progress1 != null) {
                    if (progress1.isShowing())
                        progress1.dismiss();
                }

                dialog.dismiss();
            }
        });


    }

    public void showDialog1(String text) {
        dialog1 = new Dialog(getActivity());
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater lf = (LayoutInflater) (getActivity())
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.try_again_dialog, null);
        TextView body = (TextView) dialogview
                .findViewById(R.id.dialogBody);
        body.setText(text);
        dialog1.setContentView(dialogview);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog1.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog1.getWindow().setAttributes(lp);
        dialog1.show();
        TextView tv_ok = (TextView) dialogview
                .findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("", "");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("", "");
        edt_amount = view.findViewById(R.id.edt_amount);
        if (edt_amount.getText().toString().equals(""))
            edt_amount.setText("0.00");

        edt_amount.setLocale(new Locale("en", "US"));
        edt_reference = view.findViewById(R.id.edt_reference);
        edt_amount1 = view.findViewById(R.id.edt_amount1);
        edt_amount1.setLocale(new Locale("en", "US"));
        edt_reference1 = view.findViewById(R.id.edt_reference1);
        edt_amount.requestFocus();


        if (AlipayPaymentFragment.isCloseTrade) {
            openProgressDialog();
            callAuthToken();
            AlipayPaymentFragment.isCloseTrade = false;
        }

        if (shadaf) {
            shadaf = false;
            try {
                JSONObject jsonObject = new JSONObject(val);
                if (jsonObject.has("responseCodeThirtyNine")) {
                    if (jsonObject.has("responseCodeThirtyNine") && jsonObject.optString("responseCodeThirtyNine").equals("00")) {
                        if (jsonObject.optString("transactionType").equals("SALE") ||
                                jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_SALE") ||
                                jsonObject.optString("transactionType").equals("COUPON_SALE")) {
                            callUnionPayStatus(val, "true");
                        } else if (jsonObject.optString("transactionType").equals("VOID") ||
                                jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_VOID") ||
                                jsonObject.optString("transactionType").equals("COUPON_VOID")) {
                            callUnionPayStatus(val, "true");
                        }

                    } else {
                        callUnionPayStatus(val, "false");
                    }
                } else if (jsonObject.has("responseMessage") &&

                        jsonObject.optString("responseCode").equals("6")) {//user cancelled the transaction by closing the screen.
                    if (jsonObject.optString("interfaceId").equals("SALE") ||
                            jsonObject.optString("interfaceId").equals("COUPON_SALE")) {
                        callUnionPayStatus(val, "false");
                    } else if (jsonObject.optString("interfaceId").equals("VOID") ||
                            jsonObject.optString("interfaceId").equals("UPI_SCAN_CODE_VOID") ||
                            jsonObject.optString("interfaceId").equals("COUPON_VOID")) {
                        callUnionPayStatus(val, "false");
                    }
                } else if (jsonObject.has("responseMessage") &&

                        jsonObject.optString("responseCode").equals("2")) {//swipe failure and password incorrect
                    if (jsonObject.optString("interfaceId").equals("SALE") ||
                            jsonObject.optString("interfaceId").equals("UPI_SCAN_CODE_SALE") ||
                            jsonObject.optString("interfaceId").equals("COUPON_SALE")) {
                        callUnionPayStatus(val, "false");
                    } else if (jsonObject.optString("interfaceId").equals("VOID") ||
                            jsonObject.optString("interfaceId").equals("UPI_SCAN_CODE_VOID") ||
                            jsonObject.optString("interfaceId").equals("COUPON_VOID")) {
                        callUnionPayStatus(val, "false");
                    }
                } else {
                    reference_id = "";
                    preferenceManager.setreference_id("");
                    preferenceManager.settriggerReferenceId("");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            val = null;
        } else {
            reference_id = "";
            preferenceManager.setreference_id("");
            //6th dec 2018
            if (MyPOSMateApplication.isOpen) {
                if (AppConstants.isDeviceHomePressed) {
                    AppConstants.isDeviceHomePressed = false;
                } else {
                    MyPOSMateApplication.isOpen = false;
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                }

            }

        }


    }


    JSONObject jsonObject;
    static String val = null;
    public static boolean isTransactionDetails = false;

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
                _parseAuthCodeResponse(jsonObject);
                break;

            case "saveLoyaltyInfo":
                _parseSaveLoyalityInfoResponse(jsonObject);
                break;

            case "unionpaystatus":
                _parseUnionPayStatusResponse(jsonObject);
                break;

            case "Arke":
                _parseArkeResponse(result);
                break;

            case "TransactionDetails":
                _parseTransactionDetailsResponse(jsonObject);
                break;

            case "paynow":
                Log.v("PAY_RESPONSE","respay "+result);
                _parsePayNowResponse(jsonObject);
                break;


            case "updateRequest":
                _parseUpdateRequest(jsonObject);
                break;
        }
    }


    private void _parseUpdateRequest(JSONObject jsonObject) {
        if (jsonObject.optBoolean("status")) {
            Toast.makeText(getActivity(), "Cancel Trigger Request Is Successful", Toast.LENGTH_SHORT).show();
            if (preferenceManager.isManual()) {
                ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
            } else {
                ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
            }
        } else {
            Toast.makeText(getActivity(), "Error cancelling request", Toast.LENGTH_SHORT).show();
        }
    }

    private void _parseAuthCodeResponse(JSONObject jsonObject) {
        if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
            preferenceManager.setauthToken(jsonObject.optString("access_token"));
        }
        if (isBack) {
//            isBack = false;
          //  stsartFastScan(true);//Back
            if (preferenceManager.isExternalScan())
            {
                //edt_reference_id.requestFocus();
                //scanner

                showScanBackCameraDialog();
            }else{
                //camera
                isLoyaltyQrSelected=true;
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        }
        if (isFront) {
//            isFront = false;
           // stsartFastScan(false);//front

            if (preferenceManager.isExternalScan())
            {
                //edt_reference_id.requestFocus();
                //scanner
                showScanBackCameraDialog();
            }else{
                //camera
                isLoyaltyFrontQrSelected=true;
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setOrientationLocked(false);
                integrator.setCameraId(1);
                integrator.initiateScan();
            }
        }
        if (isTransactionDetails) {
            isTransactionDetails = false;
            callTransactionDetails();
        }

        if (isTriggerCancelled) {
            isTriggerCancelled = false;
            callUpdateRequestAPI1(requestId, true);
        }
    }

    private void _parseSaveLoyalityInfoResponse(JSONObject jsonObject) {
        callAuthToken();
        progress.dismiss();
        if (jsonObject.optBoolean("status")) {
            tv_status_scan.setVisibility(View.VISIBLE);
            tv_status_scan.setText("Thank you for using Membership/Loyality");
            tv_status_scan_button.setText("Rescan Membership/Loyality");
            if (isFront) {
                isFront = false;
                tv_status_scan.setVisibility(View.VISIBLE);
                tv_status_scan_button2.setText("Rescan Membership/Loyality");
                tv_status_scan_button2.setGravity(Gravity.CENTER | Gravity.TOP);
            } else {
                tv_status_scan.setVisibility(View.GONE);
                tv_status_scan_button2.setGravity(Gravity.CENTER);
            }
            if (isBack) {
                isBack = false;
                tv_status_scan.setVisibility(View.VISIBLE);
                tv_status_scan_button1.setText("Rescan Membership/Loyality");
                tv_status_scan_button1.setGravity(Gravity.CENTER | Gravity.TOP);
            } else {
                tv_status_scan.setVisibility(View.GONE);
                tv_status_scan_button2.setGravity(Gravity.CENTER);
            }

            Toast.makeText(getActivity(), "Loyality data uploaded successfully ", Toast.LENGTH_SHORT).show();
        } else {
            tv_status_scan.setVisibility(View.VISIBLE);
            tv_status_scan.setText("Membership/Loyality could not be scanned");
            tv_status_scan_button.setText("Rescan Membership/Loyality");
            if (isFront) {
                isFront = false;
                tv_status_scan_button2.setText("Rescan Membership/Loyality");
            }
            if (isBack) {
                isBack = false;
                tv_status_scan_button1.setText("Rescan Membership/Loyality");
            }
            Toast.makeText(getActivity(), "Loyality data upload failed ", Toast.LENGTH_SHORT).show();
        }
    }

    private void _parseUnionPayStatusResponse(JSONObject jsonObject) {
        callAuthToken();
        reference_id = "";
        preferenceManager.setreference_id("");
        preferenceManager.settriggerReferenceId("");
        preferenceManager.setunion_pay_resp("");
        AppConstants.xmppamountforscan = "";
        if (isTrigger) {
            isTrigger = false;
            callUpdateRequestAPI1(requestId, true);
        }
        if (jsonObject.optBoolean("status")) {
            Toast.makeText(getActivity(), jsonObject.optString("message") + ".", Toast.LENGTH_LONG).show();
            //added for external apps 12/5/2019
            if (DashboardActivity.isExternalApp) {
                AppConstants.showDialog = true;
                isTransactionDone = true;
                ((DashboardActivity) getActivity()).callProgressDialogForUnionPay();
                ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                returnDataToExternalApp(hashMapKeys, URLDecoder.decode(AppConstants.EXTERNAL_APP_UNIONPAY_RESPONSE));
            } else {
                AppConstants.showDialog = true;
                ((DashboardActivity) getActivity()).callProgressDialogForUnionPay();
                if (!isTrigger)
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
            }


        } else {
            Toast.makeText(getActivity(), "Failed to update the transaction", Toast.LENGTH_LONG).show();
        }


        if (AppConstants.isUnionQrSelected) {
            AppConstants.isUnionQrSelected = false;
            payment_mode = "";
            qrMode = "False";
            preferenceManager.setupay_amount(edt_amount.getText().toString());
            xmppAmount = convenience_amount_unionpayqrscan + "";
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {
                isUnionPayQrSelected = true;
                callUnionPayQRScan();
            }
        }


        if (AppConstants.isUplanselected) {
            AppConstants.isUplanselected = false;
            payment_mode = "";
            qrMode = "False";
            preferenceManager.setupay_amount(edt_amount.getText().toString());
            xmppAmount = convenience_amount_uplan + "";
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {
                isUpayselected = true;

                callUplan();
            }
        }


        if (AppConstants.isUnionpayselected) {
            AppConstants.isUnionpayselected = false;
            payment_mode = "";
            qrMode = "False";
            xmppAmount = convenience_amount_unionpay + "";
            if (edt_amount.getText().toString().equals("0.00") || edt_amount.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter the amount", Toast.LENGTH_LONG).show();
            } else {

                callUnionPay();
            }
        }
    }

    private void _parseArkeResponse(String result) throws JSONException {
        callAuthToken();

        if (jsonObject.has("responseCodeThirtyNine")) {
            if (jsonObject.has("responseCodeThirtyNine") && jsonObject.optString("responseCodeThirtyNine").equals("00")) {
                ManualEntry.shadaf = true;
                if (isunionPayQrScanSelectedForSale) {
                    isunionPayQrScanSelectedForSale = false;
                    JSONObject jsonObject = new JSONObject(result);
                    jsonObject.put("transactionType", "UPI_SCAN_CODE_SALE");
                    jsonObject.put("qrcode", auth_code);
                    preferenceManager.setunion_pay_resp(jsonObject.toString());
                    ManualEntry.val = jsonObject.toString();
                } else {
                    preferenceManager.setunion_pay_resp(jsonObject.toString());
                    ManualEntry.val = jsonObject.toString();
                }

            } else {
                ManualEntry.shadaf = true;
                preferenceManager.setunion_pay_resp(jsonObject.toString());
                ManualEntry.val = jsonObject.toString();
            }

        } else {
            ManualEntry.shadaf = true;
            if (isunionPayQrScanSelectedForSale) {
                isunionPayQrScanSelectedForSale = false;
                JSONObject jsonObject = new JSONObject(result);
                jsonObject.put("transactionType", "UPI_SCAN_CODE_SALE");
                jsonObject.put("qrcode", auth_code);
                preferenceManager.setunion_pay_resp(jsonObject.toString());
                ManualEntry.val = jsonObject.toString();
            } else {
                preferenceManager.setunion_pay_resp(jsonObject.toString());
                ManualEntry.val = jsonObject.toString();
            }

            Toast.makeText(getActivity(), jsonObject.optString("responseMessage"), Toast.LENGTH_LONG).show();

        }
        auth_code = "";
    }

    private void _parseTransactionDetailsResponse(JSONObject jsonObject) throws JSONException {
        callAuthToken();

        if (!jsonObject.optString("status_id").equals("USERPAYING") && !jsonObject.optString("status_description").equals("WAIT_BUYER_PAY")) {
            if (jsonObject.optString("status").equalsIgnoreCase("true") && !jsonObject.optString("status_description").equals("TRADE_NOT_PAY")) {
                if (progress1.isShowing())
                    progress1.dismiss();
                if (MyPOSMateApplication.isOpen) {
                    jsonObject.put("grandtotal", xmppAmount);
                } else
                    jsonObject.put("grandtotal", edt_amount.getText().toString());
                selected_screen = 0;
                ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.PAYMENTPROCESSING, jsonObject.toString());
                if (progress.isShowing())
                    progress.dismiss();
                countDownTimer.cancel();
            } else if (jsonObject.optString("status_description").equals("TRADE_REVOKED")) {
                if (progress1.isShowing())
                    progress1.dismiss();
                if (progress.isShowing())
                    progress.dismiss();
                showDialog("Transaction revoked. Try again.");
                countDownTimer.cancel();

            }
        } else {
            if (jsonObject.optString("status_description").equals("TRADE_CLOSED")) {
                showDialog("Your transaction is closed.");
                callTransactionDetails();
                ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.PAYMENTPROCESSING, jsonObject.toString());
                if (progress.isShowing())
                    progress.dismiss();
                if (progress1.isShowing())
                    progress1.dismiss();
                countDownTimer.cancel();
            }

        }

    }


    private void _parsePayNowResponse(JSONObject jsonObject) throws JSONException, Exception {
        callAuthToken();
        AppConstants.xmppamountforscan = ""; //added on 12th march 2019
        if (!jsonObject.has("reference_id")) {
            jsonObject.putOpt("referenceId", reference_id);
        }

        if (jsonObject.optBoolean("status") == false) {
            String res=String.valueOf(jsonObject);
           // Toast.makeText(getActivity(), "false"+ res, Toast.LENGTH_LONG).show();

            if (progress != null) {
                if (progress.isShowing())
                    progress.dismiss();
            }

            edt_amount.setText("");
            edt_amount1.setText("");
            preferenceManager.setreference_id("");
            reference_id = "";

               if (progress != null) {
                    if (progress.isShowing())
                        progress.dismiss();
                }

                if (progress2 != null) {
                    if (progress2.isShowing())
                        progress2.dismiss();
                }


           // callTransactionDetails();
            Toast.makeText(getActivity(), jsonObject.optString("message") + ".Please try again", Toast.LENGTH_LONG).show();
            ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);

            return;
        }
        else if (jsonObject.optString("error").equalsIgnoreCase("invalid_token")){
            if (progress != null) {
                if (progress.isShowing())
                    progress.dismiss();
            }

            if (progress2 != null) {
                if (progress2.isShowing())
                    progress2.dismiss();
            }
            Toast.makeText(getActivity(), jsonObject.optString("error") + ".Please try again", Toast.LENGTH_LONG).show();

        }
        if (progress.isShowing())
            progress.dismiss();

        reference_id = jsonObject.optString("referenceId");
        preferenceManager.setreference_id(reference_id);
        preferenceManager.setincrement_id(jsonObject.optString("incrementId"));
        if (!payment_mode.equals("nochannel")) {
            if (MyPOSMateApplication.isOpen) {

                if (isMerchantQrDisplaySelected && preferenceManager.isMerchantDPARDisplay() &&
                        preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) {
                    jsonObject.put("amount", convenience_amount_unionpayqr_merchant_display + "");
                } else if (isMerchantQrDisplaySelected && preferenceManager.isMerchantDPARDisplay() &&
                        !preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) {
                    jsonObject.put("amount", xmppAmount);
                }


                if (preferenceManager.is_cnv_alipay_display_and_add() &&
                        preferenceManager.is_cnv_wechat_display_and_add()) {
                    if (isPayNowScanCalled) {
                        if (selected_channel.equals("ALIPAY"))
                            jsonObject.put("amount", convenience_amount_alipay_scan + "");
                        else if (selected_channel.equals("WECHAT"))
                            jsonObject.put("amount", convenience_amount_wechat_scan + "");
                        else
                            jsonObject.put("amount", xmppAmount);
                    } else {
                        if (selected_channel.equals("ALIPAY"))
                            jsonObject.put("amount", convenience_amount_alipay + "");
                        else if (selected_channel.equals("WECHAT"))
                            jsonObject.put("amount", convenience_amount_wechat + "");
                        else
                            jsonObject.put("amount", xmppAmount);
                    }
                } else if (preferenceManager.is_cnv_alipay_display_and_add() &&
                        !preferenceManager.is_cnv_wechat_display_and_add()) {

                    if (isPayNowScanCalled) {
                        if (selected_channel.equals("ALIPAY"))
                            jsonObject.put("amount", convenience_amount_alipay_scan + "");
                        else
                            jsonObject.put("amount", xmppAmount);
                    } else {
                        if (selected_channel.equals("ALIPAY"))
                            jsonObject.put("amount", convenience_amount_alipay + "");
                        else
                            jsonObject.put("amount", xmppAmount);
                    }
                } else if (!preferenceManager.is_cnv_alipay_display_and_add() &&
                        preferenceManager.is_cnv_wechat_display_and_add()) {

                    if (isPayNowScanCalled) {
                        if (selected_channel.equals("WECHAT"))
                            jsonObject.put("amount", convenience_amount_wechat_scan + "");
                        else
                            jsonObject.put("amount", xmppAmount);
                    } else {
                        if (selected_channel.equals("WECHAT"))
                            jsonObject.put("amount", convenience_amount_wechat + "");
                        else
                            jsonObject.put("amount", xmppAmount);
                    }
                } else if (preferenceManager.is_cnv_poli_display_and_add() &&
                        preferenceManager.isPoliSelected()) {
                    jsonObject.put("amount", convenience_amount_poli);
                } else if (!preferenceManager.is_cnv_poli_display_and_add() &&
                        preferenceManager.isPoliSelected()) {
                    jsonObject.put("amount", xmppAmount);
                } else if (preferenceManager.is_cnv_centrapay_display_and_add() &&
                        preferenceManager.isCentrapayMerchantQRDisplaySelected()) {
                    jsonObject.put("amount", convenience_amount_centrapay_merchant_qr_display);
                } else if (!preferenceManager.is_cnv_centrapay_display_and_add() &&
                        preferenceManager.isCentrapayMerchantQRDisplaySelected()) {
                    jsonObject.put("amount", xmppAmount);
                } else {
                    jsonObject.put("amount", xmppAmount);
                }

            } else if (isCentrapayMerchantQRDisplaySelected &&
                    preferenceManager.is_cnv_centrapay_display_and_add() &&
                    preferenceManager.isCentrapayMerchantQRDisplaySelected()) {
                isCentrapayMerchantQRDisplaySelected = false;
                jsonObject.put("amount", convenience_amount_centrapay_merchant_qr_display);
            } else if (isCentrapayMerchantQRDisplaySelected && !preferenceManager.is_cnv_centrapay_display_and_add() &&
                    preferenceManager.isCentrapayMerchantQRDisplaySelected()) {
                isCentrapayMerchantQRDisplaySelected = false;
                jsonObject.put("amount", edt_amount.getText().toString().trim());
            } else if (isPoliSelected && preferenceManager.is_cnv_poli_display_and_add() &&
                    preferenceManager.isPoliSelected()) {
                isPoliSelected = false;
                jsonObject.put("amount", convenience_amount_poli);
            } else if (isPoliSelected && !preferenceManager.is_cnv_poli_display_and_add() &&
                    preferenceManager.isPoliSelected()) {
                isPoliSelected = false;
                jsonObject.put("amount", edt_amount.getText().toString().trim());
            } else if (isMerchantQrDisplaySelected && preferenceManager.isMerchantDPARDisplay() &&
                    preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) {
                jsonObject.put("amount", convenience_amount_unionpayqr_merchant_display + "");
            } else if (isMerchantQrDisplaySelected && preferenceManager.isMerchantDPARDisplay() &&
                    !preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) {
                jsonObject.put("amount", edt_amount.getText().toString());
            } else if (!preferenceManager.is_cnv_alipay_display_and_add() &&
                    !preferenceManager.is_cnv_wechat_display_and_add()) {
                jsonObject.put("amount", edt_amount.getText().toString().trim());
            } else if (preferenceManager.is_cnv_alipay_display_and_add() &&
                    preferenceManager.is_cnv_wechat_display_and_add()) {

                if (isPayNowScanCalled) {
                    if (selected_channel.equals("ALIPAY"))
                        jsonObject.put("amount", convenience_amount_alipay_scan + "");
                    else if (selected_channel.equals("WECHAT"))
                        jsonObject.put("amount", convenience_amount_wechat_scan + "");
                    else
                        jsonObject.put("amount", xmppAmount);
                } else {
                    if (payment_mode.equals("ALIPAY"))
                        jsonObject.put("amount", convenience_amount_alipay + "");
                    else if (payment_mode.equals("WECHAT"))
                        jsonObject.put("amount", convenience_amount_wechat + "");
                    else
                        jsonObject.put("amount", xmppAmount);
                }
            } else if (preferenceManager.is_cnv_alipay_display_and_add() &&
                    !preferenceManager.is_cnv_wechat_display_and_add()) {

                if (isPayNowScanCalled) {
                    if (selected_channel.equals("ALIPAY"))
                        jsonObject.put("amount", convenience_amount_alipay_scan + "");
                    else
                        jsonObject.put("amount", xmppAmount);
                } else {
                    if (payment_mode.equals("ALIPAY"))
                        jsonObject.put("amount", convenience_amount_alipay + "");
                    else
                        jsonObject.put("amount", xmppAmount);
                }
            } else if (!preferenceManager.is_cnv_alipay_display_and_add() &&
                    preferenceManager.is_cnv_wechat_display_and_add()) {

                if (isPayNowScanCalled) {
                    if (selected_channel.equals("WECHAT"))
                        jsonObject.put("amount", convenience_amount_wechat_scan + "");
                    else
                        jsonObject.put("amount", xmppAmount);
                } else {
                    if (payment_mode.equals("WECHAT"))
                        jsonObject.put("amount", convenience_amount_wechat + "");
                    else
                        jsonObject.put("amount", xmppAmount);
                }
            } else
                jsonObject.put("amount", edt_amount.getText().toString().trim());
            String qrcode = jsonObject.optString("codeUrl");

            HashMap hashmap = new HashMap();
            hashmap.put("result", jsonObject.toString());
            hashmap.put("payment_mode", payment_mode);

            if (payment_mode.equalsIgnoreCase("UNION_PAY")) {
                ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.ALIPAYPAYMENT, hashmap);
            } else {
                ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.ALIPAYPAYMENT, hashmap);
            }


            if (progress.isShowing())
                progress.dismiss();
            countDownTimer.cancel();
        } else {
            if (!jsonObject.optBoolean("status")) {
                progress.dismiss();
                Toast.makeText(getActivity(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
               /* openProgressDialog();

                progress.setMessage("Please wait while processing the transaction..");
                progress1 = new ProgressDialog(getActivity());
                progress1.setMessage("Please wait while processing the transaction..");
                progress1.setCancelable(false);
                progress1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress1.setIndeterminate(true);
                progress1.show();
                countDownTimer = new CountDownTimer(30000, 1000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                    public void onTick(long millisUntilFinished) {
//                                isTransactionDetails=true;
//                                callAuthToken();
                        if (progress2.isShowing())
                            progress2.dismiss();
                        callTransactionDetails();
                    }

                    public void onFinish() {
                        try {
                            showDialog("Try again");
                            if (progress.isShowing())
                                progress.dismiss();
//                                    if(progresdismiss();
                            countDownTimer.cancel();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };
                countDownTimer.start();*/


            } else {
                if (MyPOSMateApplication.isOpen) {
                    jsonObject.put("grandtotal", xmppAmount);
                } else
                    jsonObject.put("grandtotal", edt_amount.getText().toString());
                if (progress.isShowing())
                    progress.dismiss();
                openProgressDialog();

                progress.setMessage("Please wait while processing the transaction..");
                progress1 = new ProgressDialog(getActivity());
                progress1.setMessage("Please wait while processing the transaction..");
                progress1.setCancelable(false);
                progress1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress1.setIndeterminate(true);
                progress1.show();
                countDownTimer = new CountDownTimer(30000, 1000) { //40000 milli seconds is total time, 1000 milli seconds is time interval

                    public void onTick(long millisUntilFinished) {
                        if (progress2 != null) {
                            if (progress2.isShowing())
                                progress2.dismiss();
                        }

                        callTransactionDetails();
                    }

                    public void onFinish() {
                        try {
                            showRetryDialog();
                            // showDialog("Try again");
                            if (progress != null) {
                                if (progress.isShowing())
                                    progress.dismiss();
                            }
                            countDownTimer.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                };
                countDownTimer.start();

            }
        }
    }


    public void showRetryDialog() {
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
                    if (progress1 != null) {
                        if (progress1.isShowing())
                            progress1.dismiss();
                    }
                    ((DashboardActivity) mContext).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
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
                callTransactionDetails();
                dialog.dismiss();
            }
        });
    }


    JSONObject jsonObjectSale, jsonObjectCouponSale;
    Intent intentCen = new Intent();
    String packageName = "com.centerm.dynaminpayinskate";
    String activityName = "org.skate.pay.component.PayEntry";
    ComponentName comp = new ComponentName(packageName, activityName);
    private static final int REQ_PAY_SALE = 100;

    public void beginBussiness(String reference_id) {

        hideSoftInput();

        try {
            jsonObjectSale = new JSONObject();
            Calendar c = Calendar.getInstance();

            if (preferenceManager.is_cnv_uni_display_and_add()) {
                jsonObjectSale.put("amount", convenience_amount_unionpay + "");//Double.parseDouble(edt_amount.getText().toString().replace(",", "")));
                jsonObjectSale.put("orderNumber", reference_id);//String.valueOf(c.getTimeInMillis()));
                jsonObjectSale.put("needAppPrinted", false);
            } else {
                jsonObjectSale.put("amount", Double.parseDouble(edt_amount.getText().toString().replace(",", "")));
                jsonObjectSale.put("orderNumber", reference_id);//String.valueOf(c.getTimeInMillis()));
                jsonObjectSale.put("needAppPrinted", false);
            }

            //  doTransaction("SALE", jsonObject);


            intentCen.setComponent(comp);
            Bundle bundle = new Bundle();
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.SALE);
            bundle.putDouble(ThirtConst.RequestTag.THIRD_PATH_TRANS_AMOUNT, jsonObjectSale.optDouble("amount"));
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObjectSale.optString("orderNumber"));
            intentCen.putExtras(bundle);
            TransactionType = ThirtConst.TransType.SALE;
            startActivityForResult(intentCen, REQ_PAY_SALE);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void beginBussinessCoupon(String reference_id, String couponInformation) {

        hideSoftInput();

        try {
            jsonObjectCouponSale = new JSONObject();
            Calendar c = Calendar.getInstance();
            if (preferenceManager.cnv_uplan_display_and_add() && unionpay_payment_option.equals("DP-Uplan")) {
                jsonObjectCouponSale.put("transactionType", "COUPON_SALE");
                jsonObjectCouponSale.put("couponInformation", couponInformation);
                jsonObjectCouponSale.put("amount", convenience_amount_uplan + "");
                jsonObjectCouponSale.put("orderNumber", reference_id);
            } else if (preferenceManager.cnv_unionpayqr_display_and_add() && unionpay_payment_option.equals("DP-QR")) {
                jsonObjectCouponSale.put("transactionType", "COUPON_SALE");
                jsonObjectCouponSale.put("couponInformation", couponInformation);
                jsonObjectCouponSale.put("amount", convenience_amount_unionpayqrscan + "");
                jsonObjectCouponSale.put("orderNumber", reference_id);
            } else {
                jsonObjectCouponSale.put("transactionType", "COUPON_SALE");
                jsonObjectCouponSale.put("couponInformation", couponInformation);
                jsonObjectCouponSale.put("amount", Double.parseDouble(edt_amount.getText().toString().replace(",", "")));
                jsonObjectCouponSale.put("orderNumber", reference_id);
            }

            intentCen.setComponent(comp);
            Bundle bundle = new Bundle();
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.COUPON_SALE);
            bundle.putDouble(ThirtConst.RequestTag.THIRD_PATH_TRANS_AMOUNT, jsonObjectCouponSale.optDouble("amount"));
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObjectCouponSale.optString("orderNumber"));
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_COUPON_INFO, jsonObjectCouponSale.optString("couponInformation"));
            intentCen.putExtras(bundle);
            TransactionType = ThirtConst.TransType.COUPON_SALE;
            startActivityForResult(intentCen, REQ_PAY_SALE);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void beginBussinessPreAuthorization(String reference_id, String couponInformation) {

        hideSoftInput();

        try {
            jsonObjectSale = new JSONObject();
            Calendar c = Calendar.getInstance();

            // if (preferenceManager.isConvenienceFeeSelected()) {
            if (preferenceManager.cnv_unionpayqr_display_and_add()) {
                jsonObjectSale.put("transactionType", "SALE");
                jsonObjectSale.put("qrcode", couponInformation);
                jsonObjectSale.put("amount", convenience_amount_unionpayqrscan + "");//Double.parseDouble(edt_amount.getText().toString().replace(",", "")));
                jsonObjectSale.put("orderNumber", reference_id);//String.valueOf(c.getTimeInMillis()));
            } else {
                jsonObjectSale.put("transactionType", "SALE");

                jsonObjectSale.put("qrcode", couponInformation);
                if (DashboardActivity.isExternalApp) {
                    jsonObjectSale.put("amount", Double.parseDouble(preferenceManager.getupay_amount().replace(",", "")));
                } else {
                    jsonObjectSale.put("amount", Double.parseDouble(edt_amount.getText().toString().replace(",", "")));
                }
                jsonObjectSale.put("orderNumber", reference_id);//String.valueOf(c.getTimeInMillis()));
            }

            //  doTransaction("SALE", jsonObjectSaleQr);
            intentCen.setComponent(comp);
            Bundle bundle = new Bundle();
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.SALE);
            bundle.putDouble(ThirtConst.RequestTag.THIRD_PATH_TRANS_AMOUNT, jsonObjectSale.optDouble("amount"));
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObjectSale.optString("orderNumber"));
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_SCAN_AUTH_PAY_CODE, jsonObjectSale.optString("qrcode"));
            intentCen.putExtras(bundle);
            TransactionType = ThirtConst.TransType.SALE;
            startActivityForResult(intentCen, REQ_PAY_SALE);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void doTransaction(String interfaceId, JSONObject jsonObject) {
        if (TransactionNames.SALE_BY_SDK.name().equals(interfaceId)) {
            vasCallsArkeBusiness.doTransaction(interfaceId, jsonObject, this);
        } else {
            vasCallsArkeBusiness.doTransaction(interfaceId, jsonObject, this);
        }
    }

    private void hideSoftInput() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }


    public void callTerminalPayCentrapayMerchantQrDisplay() {
        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
        if (countDownTimerxmpp != null) {
            countDownTimerxmpp.cancel();
            tv_start_countdown.setVisibility(View.GONE);
        }
        openProgressDialog();
        try {

            DecimalFormat df = new DecimalFormat("#0.00");
            if (MyPOSMateApplication.isOpen) {
                char[] ch = xmppAmount.toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ch.length; i++) {
                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                    } else {
                        sb.append(ch[i]);
                    }
                }
                xmppAmount = sb.toString().replace(",", "");


                if (preferenceManager.is_cnv_centrapay_display_and_add()) {
                    original_amount = original_xmpp_trigger_amount;
                    xmppAmount = convenience_amount_centrapay_merchant_qr_display + "";
                    fee_amount = convenience_amount_centrapay_merchant_qr_display -
                            Double.parseDouble(original_xmpp_trigger_amount.replace(",", ""))
                            + "";
                    fee_percentage = preferenceManager.getcnv_centrapay();
                    preferenceManager.setReference(edt_reference.getText().toString());

                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(xmppAmount)) + "");
                    hashMapKeys.put("original_amount", original_amount);
                    hashMapKeys.put("fee_amount", roundTwoDecimals(Float.valueOf(fee_amount + "")));
                    hashMapKeys.put("fee_percentage", fee_percentage);
                    hashMapKeys.put("discount", "0");
                    hashMapKeys.put("qr_mode", true + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());


                } else {
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(xmppAmount)) + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                }


            } else {
                String amount = "";
                char[] ch = edt_amount.getText().toString().toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ch.length; i++) {
                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                    } else {
                        sb.append(ch[i]);
                    }
                }
                amount = sb.toString().replace(",", "");
                if (preferenceManager.is_cnv_centrapay_display_and_add()) {
                    original_amount = amount;
                    amount = convenience_amount_centrapay_merchant_qr_display + "";
                    fee_amount = convenience_amount_centrapay_merchant_qr_display -
                            Double.parseDouble(edt_amount.getText().toString().replace(",", ""))
                            + "";
                    fee_percentage = preferenceManager.getcnv_centrapay();

                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(amount)) + "");
                    hashMapKeys.put("original_amount", original_amount);
                    hashMapKeys.put("fee_amount", roundTwoDecimals(Float.valueOf(fee_amount + "")));
                    hashMapKeys.put("fee_percentage", fee_percentage);
                    hashMapKeys.put("discount", "0");
                    hashMapKeys.put("qr_mode", true + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("channel", channel);

                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                } else {
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(amount)) + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callTerminalPayPoli() {
        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
        if (countDownTimerxmpp != null) {
            countDownTimerxmpp.cancel();
            tv_start_countdown.setVisibility(View.GONE);
        }
        openProgressDialog();
        try {

            DecimalFormat df = new DecimalFormat("#0.00");
            if (MyPOSMateApplication.isOpen) {
                char[] ch = xmppAmount.toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ch.length; i++) {
                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                    } else {
                        sb.append(ch[i]);
                    }
                }
                xmppAmount = sb.toString().replace(",", "");


                if (preferenceManager.is_cnv_poli_display_and_add()) {
                    original_amount = original_xmpp_trigger_amount;
                    xmppAmount = convenience_amount_poli + "";
                    fee_amount = convenience_amount_poli -
                            Double.parseDouble(original_xmpp_trigger_amount.replace(",", ""))
                            + "";
                    fee_percentage = preferenceManager.getcnv_poli();
                    preferenceManager.setReference(edt_reference.getText().toString());

                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(xmppAmount)) + "");
                    hashMapKeys.put("original_amount", original_amount);
                    hashMapKeys.put("fee_amount", roundTwoDecimals(Float.valueOf(fee_amount + "")));
                    hashMapKeys.put("fee_percentage", fee_percentage);
                    hashMapKeys.put("discount", "0");
                    hashMapKeys.put("qr_mode", true + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());


                } else {
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(xmppAmount)) + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                }


            } else {
                String amount = "";
                char[] ch = edt_amount.getText().toString().toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ch.length; i++) {
                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                    } else {
                        sb.append(ch[i]);
                    }
                }
                amount = sb.toString().replace(",", "");
                if (preferenceManager.is_cnv_poli_display_and_add()) {
                    original_amount = amount;
                    amount = convenience_amount_poli + "";
                    fee_amount = convenience_amount_poli -
                            Double.parseDouble(edt_amount.getText().toString().replace(",", ""))
                            + "";
                    fee_percentage = preferenceManager.getcnv_poli();

                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(amount)) + "");
                    hashMapKeys.put("original_amount", original_amount);
                    hashMapKeys.put("fee_amount", roundTwoDecimals(Float.valueOf(fee_amount + "")));
                    hashMapKeys.put("fee_percentage", fee_percentage);
                    hashMapKeys.put("discount", "0");
                    hashMapKeys.put("qr_mode", true + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("channel", channel);

                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                } else {
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(amount)) + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callTerminalPayAlipay() {
        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
        if (countDownTimerxmpp != null) {
            countDownTimerxmpp.cancel();
            tv_start_countdown.setVisibility(View.GONE);
        }
        openProgressDialog();
        try {

            DecimalFormat df = new DecimalFormat("#0.00");
            if (MyPOSMateApplication.isOpen) {
                char[] ch = xmppAmount.toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ch.length; i++) {
                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                    } else {
                        sb.append(ch[i]);
                    }
                }
                xmppAmount = sb.toString().replace(",", "");


                if (preferenceManager.is_cnv_alipay_display_and_add()) {
                    original_amount = original_xmpp_trigger_amount;
                    xmppAmount = convenience_amount_alipay + "";
                    fee_amount = convenience_amount_alipay -
                            Double.parseDouble(original_xmpp_trigger_amount.replace(",", ""))
                            + "";
                    fee_percentage = preferenceManager.getcnv_alipay();
                    preferenceManager.setReference(edt_reference.getText().toString());

                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(xmppAmount)) + "");
                    hashMapKeys.put("original_amount", original_amount);
                    hashMapKeys.put("fee_amount", roundTwoDecimals(Float.valueOf(fee_amount + "")));
                    hashMapKeys.put("fee_percentage", fee_percentage);
                    hashMapKeys.put("discount", "0");
                    hashMapKeys.put("qr_mode", true + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());


                } else {
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(xmppAmount)) + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                }


            } else {
                String amount = "";
                char[] ch = edt_amount.getText().toString().toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ch.length; i++) {
                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                    } else {
                        sb.append(ch[i]);
                    }
                }
                amount = sb.toString().replace(",", "");
                if (preferenceManager.is_cnv_alipay_display_and_add()) {
                    original_amount = amount;
                    amount = convenience_amount_alipay + "";
                    fee_amount = convenience_amount_alipay -
                            Double.parseDouble(edt_amount.getText().toString().replace(",", ""))
                            + "";
                    fee_percentage = preferenceManager.getcnv_alipay();

                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(amount)) + "");
                    hashMapKeys.put("original_amount", original_amount);
                    hashMapKeys.put("fee_amount", roundTwoDecimals(Float.valueOf(fee_amount + "")));
                    hashMapKeys.put("fee_percentage", fee_percentage);
                    hashMapKeys.put("discount", "0");
                    hashMapKeys.put("qr_mode", true + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("channel", channel);

                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                } else {
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(amount)) + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        return twoDForm.format(d);
    }

    public void callTerminalPayWeChat() {
        String original_amount = "", fee_amount = "", discount = "", fee_percentage = "";
        if (countDownTimerxmpp != null) {
            countDownTimerxmpp.cancel();
            tv_start_countdown.setVisibility(View.GONE);
        }
        openProgressDialog();
        try {

            DecimalFormat df = new DecimalFormat("#0.00");
            if (MyPOSMateApplication.isOpen) {
                char[] ch = xmppAmount.toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ch.length; i++) {
                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                    } else {
                        sb.append(ch[i]);
                    }
                }
                xmppAmount = sb.toString().replace(",", "");


                if (preferenceManager.is_cnv_wechat_display_and_add()) {
                    original_amount = original_xmpp_trigger_amount;
                    xmppAmount = convenience_amount_wechat + "";
                    fee_amount = convenience_amount_wechat -
                            Double.parseDouble(original_xmpp_trigger_amount.replace(",", ""))
                            + "";
                    fee_percentage = preferenceManager.getcnv_wechat();
                    preferenceManager.setReference(edt_reference.getText().toString());

                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(xmppAmount)) + "");
                    hashMapKeys.put("original_amount", original_amount);
                    hashMapKeys.put("fee_amount", roundTwoDecimals(Float.valueOf(fee_amount + "")));
                    hashMapKeys.put("fee_percentage", fee_percentage);
                    hashMapKeys.put("discount", "0");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("channel", channel);

                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

                } else {
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(xmppAmount)) + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

                }


            } else {
                String amount = "";
                char[] ch = edt_amount.getText().toString().toCharArray();
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < ch.length; i++) {
                    if (((int) ch[i] == 160) || ((int) ch[i] == 32)) {

                    } else {
                        sb.append(ch[i]);
                    }
                }
                amount = sb.toString().replace(",", "");
                if (preferenceManager.is_cnv_wechat_display_and_add()) {
                    original_amount = amount;
                    amount = convenience_amount_wechat + "";
                    fee_amount = convenience_amount_wechat -
                            Double.parseDouble(edt_amount.getText().toString().replace(",", ""))
                            + "";
                    fee_percentage = preferenceManager.getcnv_wechat();

                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(amount)) + "");
                    hashMapKeys.put("original_amount", original_amount);
                    hashMapKeys.put("fee_amount", roundTwoDecimals(Float.valueOf(fee_amount + "")));
                    hashMapKeys.put("fee_percentage", fee_percentage);
                    hashMapKeys.put("discount", "0");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

                } else {
                    preferenceManager.setReference(edt_reference.getText().toString());
                    hashMapKeys.clear();
                    if (!edt_reference.getText().toString().equals("")) {
                        hashMapKeys.put("refData1", edt_reference.getText().toString());
                    }
//                    hashMapKeys.put("merchant_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
                    hashMapKeys.put("access_id", preferenceManager.getuniqueId());
                    hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
                    hashMapKeys.put("config_id", preferenceManager.getConfigId());
                    if (reference_id.isEmpty())
                        reference_id = new Date().getTime() + "";
                    hashMapKeys.put("reference_id", reference_id);
                    hashMapKeys.put("random_str", new Date().getTime() + "");
                    hashMapKeys.put("grand_total", df.format(Double.parseDouble(amount)) + "");
//                    hashMapKeys.put("auth_code", auth_code);
                    hashMapKeys.put("qr_mode", true + "");
                    hashMapKeys.put("channel", channel);
                    hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
                    hashMapKeys.put("access_token", preferenceManager.getauthToken());

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.putAll(hashMapKeys);

                    new OkHttpHandler(getActivity(), this, hashMap, "paynow")
                            .execute(AppConstants.BASE_URL2 + AppConstants.PAYNOW);//+ MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
                }
            }
        } catch (Exception e) {
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
                    isBack = false;
                    isFront = false;
                    if (getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                tv_status_scan.setVisibility(View.VISIBLE);
                                tv_status_scan.setText("Membership/Loyality could not be scanned");
                                tv_status_scan_button.setText("Rescan Membership/Loyality");
                                Toast.makeText(getActivity(), "Closed", Toast.LENGTH_SHORT).show();
                            }
                        });


                }

                @Override
                public void onCaptured(String arg0, int arg1) throws RemoteException {
                    long SuccessEndTime = System.currentTimeMillis();
                    long SuccessCostTime = SuccessEndTime - startTime;
                    if (getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {

                                if (preferenceManager.getLaneIdentifier().equals("")) {
                                    Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
                                } else if (preferenceManager.getPOSIdentifier().equals("")) {
                                    Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
                                } else {
                                    callMembershipLoyality(arg0);
                                    Toast.makeText(getActivity(), arg0 + "", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startQuickScan(boolean cameraBack) {
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
                    isBack = false;
                    isFront = false;
                    if (getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {

                                if (MyPOSMateApplication.isOpen) {
                                    MyPOSMateApplication.isOpen = false;
                                    MyPOSMateApplication.isActiveQrcode = false;
                                    preferenceManager.settriggerReferenceId("");
                                    AppConstants.xmppamountforscan = "";
                                    isTriggerCancelled = true;
                                    ManualEntry.isUpayselected = false;
                                    if (isTriggerCancelled) {
                                        isTrigger = false;
                                        callAuthToken();
                                    }

                                } else {
                                    AppConstants.xmppamountforscan = "";
                                    MyPOSMateApplication.isOpen = false;
                                    preferenceManager.settriggerReferenceId("");
                                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                                    ManualEntry.isUpayselected = false;
                                }


                                Toast.makeText(getActivity(), "Closed", Toast.LENGTH_SHORT).show();
                            }
                        });


                }

                @Override
                public void onCaptured(String arg0, int arg1) throws RemoteException{} /*{
                    long SuccessEndTime = System.currentTimeMillis();
                    long SuccessCostTime = SuccessEndTime - startTime;
                    if (getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {


                                if (arg0.equals("")) {
                                    AppConstants.xmppamountforscan = "";
                                    MyPOSMateApplication.isOpen = false;
                                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
                                    preferenceManager.settriggerReferenceId("");
                                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                                    ManualEntry.isUpayselected = false;
                                } else {

                                    if (!AppConstants.xmppamountforscan.equals("")) {

                                        final Handler handler1 = new Handler();
                                        handler1.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent ia = new Intent();
                                                ia.setAction("AmountTrigger");
                                                ia.putExtra("data", preferenceManager.getamountdata());
                                                getActivity().sendBroadcast(ia);

                                            }
                                        }, 400);

                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "Scanned: " + arg0, Toast.LENGTH_LONG).show();
                                                String identityCode = arg0;//data.getStringExtra("SCAN_RESULT");
                                                Intent i = new Intent();
                                                if (                                                                                                                                                                        ManualEntry.isUpayselected) {
                                                    AppConstants.isScannedCode1 = false;
                                                    i.setAction("ScannedCode");
                                                } else if (ManualEntry.isUnionPayQrSelected) {
                                                    AppConstants.isScannedCode1 = false;
                                                    i.setAction("ScannedCodeUnionPayQr");
                                                } else {
                                                    i.setAction("ScannedCode1");
                                                    AppConstants.isScannedCode1 = true;
                                                }

                                                i.putExtra("identityCode", identityCode);
                                                getActivity().sendBroadcast(i);
                                            }
                                        }, 800);

                                    } else {
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "Scanned : "  + arg0, Toast.LENGTH_LONG).show();
                                                String identityCode = arg0;//data.getStringExtra("SCAN_RESULT");
                                                Intent i = new Intent();
                                                if (ManualEntry.isUpayselected) {
                                                    AppConstants.isScannedCode1 = false;
                                                    i.setAction("ScannedCode");
                                                } else if (ManualEntry.isUnionPayQrSelected) {
                                                    AppConstants.isScannedCode1 = false;
                                                    i.setAction("ScannedCodeUnionPayQr");
                                                } else {
                                                    i.setAction("ScannedCode1");
                                                    AppConstants.isScannedCode1 = true;
                                                }

                                                i.putExtra("identityCode", identityCode);
                                                getActivity().sendBroadcast(i);
                                            }
                                        }, 500);

                                    }


                                }

                            }
                        });


                }*/
            });
        } catch (Exception e) {
            e.printStackTrace();
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


}
