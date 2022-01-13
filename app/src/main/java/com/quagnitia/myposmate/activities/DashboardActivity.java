package com.quagnitia.myposmate.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.aidl.printer.PrintDataObject;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.constant.DeviceErrorCode;
import com.centerm.smartpos.util.LogUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.quagnitia.myposmate.BuildConfig;
import com.quagnitia.myposmate.MyPOSMateApplication;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.fragments.AboutUs;
import com.quagnitia.myposmate.fragments.AlipayPaymentFragment;
import com.quagnitia.myposmate.fragments.EODFragment;
import com.quagnitia.myposmate.fragments.FragmentLoyaltyApps;
import com.quagnitia.myposmate.fragments.ManualEntry;
import com.quagnitia.myposmate.fragments.ManualEntry1;
import com.quagnitia.myposmate.fragments.OrderFragment;
import com.quagnitia.myposmate.fragments.PaymentProcessing;
import com.quagnitia.myposmate.fragments.PosMateConnectioFrag;
import com.quagnitia.myposmate.fragments.RefundFragment;
import com.quagnitia.myposmate.fragments.RefundFragmentUnionPay;

import com.quagnitia.myposmate.fragments.Settings;
import com.quagnitia.myposmate.fragments.Settlement;
import com.quagnitia.myposmate.fragments.TipSetting;
import com.quagnitia.myposmate.fragments.TransactionDetailsActivity;
import com.quagnitia.myposmate.fragments.TransactionListing;
import com.quagnitia.myposmate.fragments.TriggerFragment;
import com.quagnitia.myposmate.utils.AESHelper;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.MySoundPlayer;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.apache.commons.codec.binary.Hex;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static com.quagnitia.myposmate.MyPOSMateApplication.isOpen;
import static com.quagnitia.myposmate.MyPOSMateApplication.mStompClient;
import static com.quagnitia.myposmate.utils.AppConstants.SAVE_LOYALTY_INFO;
import static com.quagnitia.myposmate.utils.AppConstants.isTerminalInfoDeleted;

//import com.quagnitia.myposmate.fragments.DemoFragment;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, OnTaskCompleted {
    private static final int PERMISSION_REQUEST_CODE =100 ;
    public ImageView img_menu;
    public TextView tv_order_badge;
    private RelativeLayout rel_un;
    private TextView tv_settlement;
    private TextView tv_loyalty_apps;
    private Context mContext;
    public ImageView img;
    public PopupWindow mPopupWindow;
    private TextView tv_home;
    private TextView tv_timezone;
    private TextView tv_manual_entry;
    private TextView tv_refund;
    private TextView tv_refund1;
    private TextView tv_refund_unipay;
    private TextView tv_scan;
    private TextView tv_settings;
    private TextView tv_about;
    private TextView tv_close;
    private TextView tv_display_choices;
    private TextView tv_payment_choices;
    private FrameLayout inner_frame;
    private String CURRENTFRAGMENT = "";
    private Fragment fragment;
    private PreferencesManager preferenceManager;
    private IntentFilter intentFilter;
    private OpenFragmentsReceiver openFragmentsReceiver;
    private ProgressDialog progress;
    //private PreferencesManager preferenceManager;
    private TextView tv_transaction_log, tv_orders;
    private TextView tv_eod;
    public static boolean isLaunch = false;
    public static boolean isExternalApp = false;
    private EditText edt_zip_cv;

    //payment choices variables
    private CheckBox chk_centrapay_merchant_qr;
    private CheckBox chk_centrapay_qr_scan;
    private CheckBox chk_centrapay_display_and_add;
    private CheckBox chk_centrapay_display_only;
    private EditText edt_centrapay_cv;
    private CheckBox chk_poli;
    private CheckBox chk_poli_display_and_add;
    private CheckBox chk_poli_display_only;
    private EditText edt_poli_cv;
    private CheckBox chk_unionpay_card;
    private CheckBox chk_zip;
    private CheckBox chk_alipay;
    private CheckBox chk_wechat;
    private CheckBox chk_zip_qr_scan;
    private CheckBox chk_unionpay_qr;
    private CheckBox chk_up_upi_qr_display_and_add;
    private CheckBox chk_upi_qr_display_only;
    private CheckBox chk_uplan_qr;
    private CheckBox chk_alipay_qr_scan;
    private CheckBox chk_wechat_qr_scan;
    private CheckBox chk_unionpay_qr_code;
    private CheckBox chk_zip_display_and_add;
    private CheckBox chk_ali_display_and_add;
    private CheckBox chk_ali_display_only;
    private CheckBox chk_wechat_display_and_add;
    private CheckBox chk_wechat_display_only;
    private CheckBox chk_unionpay_card_display_and_add;
    private CheckBox chk_unionpay_card_display_only;
    private CheckBox chk_unionpay_qr_display_and_add;
    private CheckBox chk_unionpay_qr_display_only;
    private CheckBox chk_uplan_display_and_add;
    private CheckBox chk_uplan_display_only;
    private CheckBox chk_upi_qr_merchant_display;
    private EditText edt_ali_cv;
    private CheckBox chk_zip_display_only;
    private EditText edt_wechat_cv;
    private EditText edt_unionpay_card_cv;
    private EditText edt_unionpay_qr_cv;
    private EditText edt_uplan_cv;
    private EditText edt_up_upi_qr_cv;
    private EditText edt_up_upi_qr_cv1;
    private EditText edt_up_upi_qr_amount;
    private TextView upi_note;
    JSONObject jsonObjectLoyalty;
    public RelativeLayout rel_orders;
    MySoundPlayer mySoundPlayer;
    private TextView tv_settingMenu;
    private PopupWindow mPopupWindows;
    private TextView tv_tip;
    TextView tv_Connections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("Test").equals("Manual")) {

                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                /*if (!android.provider.Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 0);
                }*/
           //   preventStatusBarExpansion(DashboardActivity.this);
            }

        }


        setContentView(R.layout.activity_dashboard);
        hashMapKeys = new TreeMap<>();
        funcInitialSetup();
        tv_close_ext.setVisibility(View.GONE);

        //added for external apps 12/5/2019
        launchThroughExternalApps();

        if (preferenceManager.getTimezoneAbrev().equals("")) {
//           String s= TimeZone.getTimeZone("Pacific/Auckland")
//                    .getDisplayName(false, TimeZone.SHORT);
            preferenceManager.setTimezoneAbrev("NZST");
        }
        requestCameraPermission();

    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PERMISSION_REQUEST_CODE);
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    // boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted) {

                    }
                    // Snackbar.make(LanguageSelectionActivity.this, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
                    //  Toast.makeText(context,"Permission Granted, Now you can access location data and call.",Toast.LENGTH_SHORT).show();
                    else {
                       // requestCameraPermission();
                      //  Toast.makeText(DashboardActivity.this, "Permission Denied, You cannot access Camera.", Toast.LENGTH_SHORT).show();

                        //   Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();

                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow Permission access to Camera");
                                return;
                            }else{
                                showMessageOK("You need to allow Permission access to Camera from Application Setting, Do you want to continue?");
                                return;

                            }
                        }

                    }
                }


                break;
        }

    }*/

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // now, you have permission go ahead
            // TODO: something

        } else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(DashboardActivity.this,
                    CAMERA)) {
                // now, user has denied permission (but not permanently!)

            } else {
showMessageOK("You have previously declined this permission.\n" +
                "You must approve this permission in \"Permissions\" in the app settings on your device.");
                // now, user has denied permission permanently!
/*

                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "You have previously declined this permission.\n" +
                        "You must approve this permission in \"Permissions\" in the app settings on your device.", Snackbar.LENGTH_LONG).setAction("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));

                    }
                });
                View snackbarView = snackbar.getView();
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setMaxLines(5);  //Or as much as you need
                snackbar.show();
*/

            }

        }
        return;
    }


    private void showMessageOKCancel(String message) {
    /*new AlertDialog.Builder(DashboardActivity.this)
                    .setMessage(message)
                    .setPositiveButton("OK", okListener)
                  //  .setNegativeButton("Cancel", null)
                    .create()
                    .show();*/

        AlertDialog.Builder alert=new AlertDialog.Builder(DashboardActivity.this);
        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{CAMERA},
                            PERMISSION_REQUEST_CODE);
                }
            }
        });
        alert.setCancelable(false);
        alert.show();
    }

    private void showMessageOK(String message) {
          AlertDialog.Builder alert=new AlertDialog.Builder(DashboardActivity.this);
          alert.setMessage(message);
          alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                      startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                  }
              }
          });
          alert.setCancelable(false);
          alert.show();

        }

    public void showOrderReceivedToast(String text) {
        LayoutInflater li = getLayoutInflater();
        View layout = li.inflate(R.layout.custome_toast_layout, findViewById(R.id.custom_toast_layout));
        TextView tv_message = layout.findViewById(R.id.tv_message);
        tv_message.setText(text);
        Animation animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move);
        layout.startAnimation(animBlink);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
        toast.setView(layout);//setting the view of custom toast layout
        toast.show();
    }


    public void callTaskPlay() {
        TimerTask uploadCheckerTimerTask = new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (preferenceManager.getOrderBadgeCount() != 0) {
                            mySoundPlayer.playSound(1);
                            showOrderReceivedToast(" Orders in Que");
                            mySoundPlayer.stopCurrentSound(1);
                        }

                    }
                });

            }
        };
        Timer uploadCheckerTimer = new Timer(true);
        uploadCheckerTimer.scheduleAtFixedRate(uploadCheckerTimerTask, 0, 90 * 1000);
    }


    //added for external apps 12/5/2019
    public void launchThroughExternalApps() {

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("Test").equals("Manual")) {

                img_menu.setVisibility(View.GONE);
                try {
                    isExternalApp = true;
                    tv_close_ext.setVisibility(View.VISIBLE);
                    JSONObject jsonObject = new JSONObject(getIntent().getExtras().getString("TestData"));
                    Intent i1 = new Intent();
                    i1.setAction("ManualEntry");
                    i1.putExtra("data", jsonObject.toString());
                    sendBroadcast(i1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (getIntent().getExtras().getString("Test").equals("TransactionDetails")) {
                img_menu.setVisibility(View.GONE);
                try {
                    isExternalApp = true;
                    //JSONObject jsonObject = new JSONObject(getIntent().getExtras().getString("TestData"));
                    Intent i = new Intent(mContext, TransactionDetailsActivity.class);
                    i.putExtra("reference_id", getIntent().getExtras().getString("ReferenceId"));
                    // i.putExtra("amount",jsonObject.optString("amount"));
                    mContext.startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void preventStatusBarExpansion(Context context) {
        WindowManager manager = ((WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            LAYOUT_FLAG=WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        else
            LAYOUT_FLAG=WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        Activity activity = (Activity) context;
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = LAYOUT_FLAG;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = activity.getResources().getDimensionPixelSize(resId);
        }

        localLayoutParams.height = result;

        localLayoutParams.format = PixelFormat.TRANSPARENT;

        customViewGroup view = new customViewGroup(context);

        if (manager != null) {
            manager.addView(view, localLayoutParams);
        }
    }


    public static class customViewGroup extends ViewGroup {

        public customViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            Log.v("customViewGroup", "**********Intercepted");
            return true;
        }
    }

    public TreeMap<String, String> getHashMapKeysUniversal() {
        return hashMapKeysUniversal;
    }

    public void setHashMapKeysUniversal(TreeMap<String, String> hashMapKeysUniversal) {
        this.hashMapKeysUniversal.putAll(hashMapKeysUniversal);
    }

    private TreeMap<String, String> hashMapKeysUniversal = null;

    public void funcInitialSetup() {
        mContext = DashboardActivity.this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferenceManager = PreferencesManager.getInstance(this);
        hashMapKeysUniversal = new TreeMap<>();
        applyIntentFilter();
        openFragmentsReceiver = new OpenFragmentsReceiver();
        registerReceiver(openFragmentsReceiver, intentFilter);
        preferenceManager = PreferencesManager.getInstance(DashboardActivity.this);
        jsonObjectLoyalty = new JSONObject();
        mySoundPlayer = new MySoundPlayer();
        mySoundPlayer.initSounds(this);
        mySoundPlayer.addSound(1, R.raw.audio_orders_up);
        // isLaunch = true;
        callAuthToken();
        initUI();
        initListener();
        img_menu.setEnabled(true);


        if (mStompClient != null) {
            if (mStompClient.isConnected()) {
                callSetupFragment(SCREENS.POSMATECONNECTION, null);
            } else {
                if (preferenceManager.isRegistered()) {
                    preferenceManager.setIsConnected(false);
                    preferenceManager.setIsAuthenticated(false);
                    callSetupFragment(SCREENS.POSMATECONNECTION, null);
                } else {
                    callSetupFragment(SCREENS.REGISTRATION, null);
                }
            }

        } else {

            if (preferenceManager.isRegistered()) {
                preferenceManager.setIsConnected(false);
                preferenceManager.setIsAuthenticated(false);
                callSetupFragment(SCREENS.POSMATECONNECTION, null);
            } else {
                callSetupFragment(SCREENS.REGISTRATION, null);
            }

        }

        findViewById(R.id.activity_main).setOnTouchListener((View v, MotionEvent event) -> {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
            if (mPopupWindows.isShowing())
                mPopupWindows.dismiss();
            v.performClick();
            return false;
        });
    }

    public void applyIntentFilter() {
        intentFilter = new IntentFilter();

        intentFilter.addAction("ManualEntry");
        intentFilter.addAction("PrintTrigger");
        intentFilter.addAction("OrderDetails");
        intentFilter.addAction("ThirdParty");
        intentFilter.addAction("PaymentProcessing");
        intentFilter.addAction("RECONNECT");
        intentFilter.addAction("RECONNECT1");
    }

    public enum SCREENS {
        POSMATECONNECTION, ORDERS, LOYALTY_APPS, SETTINGS, THIRD_PARTY, SETTLEMEMT, MANUALENTRY, TRANSACTION_LIST, EOD, REGISTRATION, REFUND, REFUND_UNIONPAY, ALIPAYPAYMENT, PAYMENTPROCESSING, ABOUT, HELP,TIP
    }


    public void connectStomp() {
        isNetConnectionOn = true;
        callAuthToken();
    }


    @Override
    protected void onDestroy() {
        try {
            if (mStompClient != null) {
                if (mStompClient.isConnected()) {
                    mStompClient.disconnect();
                    preferenceManager.setIsAuthenticated(false);
                    preferenceManager.setIsConnected(false);
                }
            }
            if (openFragmentsReceiver != null)
                unregisterReceiver(openFragmentsReceiver);
            if (intentService != null)
                stopService(intentService);
            if (printDev != null)
                printDev = null;

            finishAffinity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindService();
        if (TransactionDetailsActivity.isReturnFromTransactionDetails) {
            TransactionDetailsActivity.isReturnFromTransactionDetails = false;
            try {
                //added for external apps 12/5/2019
                int REQ_PAY_SALE = 100;
                DashboardActivity.isExternalApp = false;
                getIntent().putExtra("result", TransactionDetailsActivity.jsonObjectReturnResult.toString());
                setResult(REQ_PAY_SALE, getIntent());
                finishAndRemoveTask();
                return;
                //added for external apps
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (UnionPayScreen.isUnionPaySelected) {
            UnionPayScreen.isUnionPaySelected = false;
            callSetupFragment(SCREENS.MANUALENTRY, null);
        }

    }

    @Override
    protected void onStop() {

        int REQ_PAY_SALE = 100;
        if (ManualEntry.isTransactionDone) {
            DashboardActivity.isExternalApp = false;
            ManualEntry.isTransactionDone = false;
            getIntent().putExtra("result", new JSONObject().toString());
            setResult(REQ_PAY_SALE, getIntent());
            finishAndRemoveTask();
            super.onStop();
            return;
        } else
            super.onStop();
    }

    public void openProgressDialog() {
        progress = new ProgressDialog(DashboardActivity.this);
        progress.setMessage(getResources().getString(R.string.loading));
      //  progress.setMessage("DashLoading");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progress.dismiss();//dismiss dialog
            }
        });
        progress.show();
    }

    TextView tv_close_ext;

    public void initUI() {
        img_menu = findViewById(R.id.img_menu);
        tv_order_badge = findViewById(R.id.tv_order_badge);
        img=findViewById(R.id.img);
        if (preferenceManager.getOrderBadgeCount() != 0) {
            Animation animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.blink);
            tv_order_badge.startAnimation(animBlink);
            tv_order_badge.setVisibility(View.VISIBLE);
            tv_order_badge.setText(preferenceManager.getOrderBadgeCount() + "");
        } else {
            tv_order_badge.setVisibility(View.GONE);
        }
        callTaskPlay();
        rel_orders = findViewById(R.id.rel_orders);
        rel_un = findViewById(R.id.rel_un);
        tv_close_ext = findViewById(R.id.tv_close);
        TextView version = findViewById(R.id.version);
        try {
            version.setText(getResources().getString(R.string.MyPOSMate_Version) + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayMenu();
        displaySubMenu();
    }

    public void initListener() {
        img_menu.setOnClickListener(this);
        tv_close_ext.setOnClickListener(this);
        rel_orders.setOnClickListener(this);

    }

    public static boolean isDisplayChoicesSelected = false;

    public void displayMenu() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.pop_window_layout, null);
        final float scale = getResources().getDisplayMetrics().density;
        int width = (int) (150 * scale + 0.5f);
        mPopupWindow = new PopupWindow(
                customView,
                width,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        funcInitiateDisplayMenuViews(customView);

        tv_refund1.setOnClickListener((View v) -> {
            callAuthToken();
            if (!isRefundOptionOpen) {
                isRefundOptionOpen = true;
                tv_refund.setVisibility(View.VISIBLE);
                tv_refund_unipay.setVisibility(View.VISIBLE);
            } else {
                isRefundOptionOpen = false;
                tv_refund.setVisibility(View.GONE);
                tv_refund_unipay.setVisibility(View.GONE);
            }

        });
        tv_settlement.setOnClickListener((View v) -> funcMenuSettlement());
        tv_loyalty_apps.setOnClickListener((View v) -> funcLoyaltyApps());
        tv_refund_unipay.setOnClickListener((View v) -> funcMenuRefundUnionPay());
        tv_timezone.setOnClickListener((View v) -> funcMenuTimezone());
        tv_eod.setOnClickListener((View v) -> funcMenuEOD());
        tv_transaction_log.setOnClickListener((View v) -> functionMenuTransactionLog());
        tv_home.setOnClickListener((View v) -> funcMenuHome());
        tv_refund.setOnClickListener((View v) -> funcRefundAlipayWeChat());
        tv_scan.setOnClickListener((View v) -> funcMenuScan());
        tv_manual_entry.setOnClickListener((View v) -> funcMenuManualEntry());
        tv_settings.setOnClickListener((View v) -> funcMenuSettings());
        tv_about.setOnClickListener((View v) -> funcMenuAbout());
        tv_close.setOnClickListener((View v) -> funcMenuClose());
        tv_display_choices.setOnClickListener((View v) -> funcMenuDisplayChoices());
        tv_payment_choices.setOnClickListener((View v) -> funcMenuPaymentChoices());
        tv_orders.setOnClickListener((View v) -> funcOrders());
        tv_settingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_settingMenu.setTextColor(Color.parseColor("#1565C0"));

                final float scale = getResources().getDisplayMetrics().density;
                int width = (int) (150 * scale + 0.5f);
                int height = (int) (75 * scale + 0.5f);
                mPopupWindows.showAtLocation(inner_frame, Gravity.LEFT, width, height);
            }
        });
    }

    public void displaySubMenu() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.pop_window_layout_sub, null);
        final float scale = getResources().getDisplayMetrics().density;
        int width = (int) (150 * scale + 0.5f);
        mPopupWindows = new PopupWindow(
                customView,
                width,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        funcInitiateDisplaySubMenuViews(customView);

        tv_timezone.setOnClickListener((View v) -> funcMenuTimezone());
        tv_settings.setOnClickListener((View v) -> funcMenuSettings());
        tv_about.setOnClickListener((View v) -> funcMenuAbout());
        tv_display_choices.setOnClickListener((View v) -> funcMenuDisplayChoices());
        tv_payment_choices.setOnClickListener((View v) -> funcMenuPaymentChoices());
        tv_tip.setOnClickListener((View v) -> funcMenuTipSetting());
        tv_Connections.setOnClickListener((View v) -> validateAccessIdForSwitchConnection());

    }

    private void validateAccessIdForSwitchConnection() {
        Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater lf = (LayoutInflater) (DashboardActivity.this)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.dialog_validate_connection, null);
        EditText etAccessId=dialogview.findViewById(R.id.etAccessId);
        Button btn_ok = dialogview.findViewById(R.id.btn_ok);
        Button btn_cancel = dialogview.findViewById(R.id.btn_cancel);

        dialog.setContentView(dialogview);

        btn_cancel.setOnClickListener((View v) ->
                {

                    dialog.dismiss();
                }
        );

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        btn_ok.setOnClickListener((View v) -> {
            if (etAccessId.getText().toString().equals("")) {
                Toast.makeText(DashboardActivity.this, "Please enter Access Id", Toast.LENGTH_LONG).show();
            }else if (etAccessId.getText().toString().trim().equals(preferenceManager.getuniqueId
                    ()))
            {
                preferenceManager.clearPreferences();
                funcChangeConnection();
                dialog.dismiss();
            }else{
                Toast.makeText(DashboardActivity.this, "Please enter valid Access Id", Toast.LENGTH_LONG).show();

            }

            //dialog.dismiss();
        });

        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }
    private void funcChangeConnection() {
        Log.v("SelectedPos","on dialog from Dashboard "+preferenceManager.getBaseURL());

        callSetupFragment(SCREENS.REGISTRATION, null);
        preferenceManager.setisRegistered(false);
        preferenceManager.setEditConnection(true);
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }
    private void funcInitiateDisplaySubMenuViews(View customView) {
        tv_timezone = customView.findViewById(R.id.tv_timezone);
        tv_settings = customView.findViewById(R.id.tv_settings);
        tv_about = customView.findViewById(R.id.tv_about);
        tv_display_choices = customView.findViewById(R.id.tv_display_choices);
        tv_payment_choices = customView.findViewById(R.id.tv_payment_choices);
        tv_tip=customView.findViewById(R.id.tv_tip);
        tv_Connections=customView.findViewById(R.id.tv_Connections);
        inner_frame = (FrameLayout) findViewById(R.id.inner_frame);
    }

    public void funcOrders() {
        callAuthToken();
        preferenceManager.setOrderBadgeCount(0);
        tv_order_badge = findViewById(R.id.tv_order_badge);
        tv_order_badge.setVisibility(View.GONE);
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
        //  if (preferenceManager.isAuthenticated()) {
        callSetupFragment(SCREENS.ORDERS, null);
//        } else {
//            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
//        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    public void funcInitiateDisplayMenuViews(View customView) {
        tv_manual_entry = customView.findViewById(R.id.tv_manual_entry);
        tv_settlement = customView.findViewById(R.id.tv_settlement);
        tv_loyalty_apps = customView.findViewById(R.id.tv_loyalty_apps);
        tv_transaction_log = customView.findViewById(R.id.tv_transaction_log);
        tv_orders = customView.findViewById(R.id.tv_orders);
        tv_eod = customView.findViewById(R.id.tv_eod);
        tv_home = customView.findViewById(R.id.tv_home);
        tv_timezone = customView.findViewById(R.id.tv_timezone);
        tv_refund = customView.findViewById(R.id.tv_refund);
        tv_refund1 = customView.findViewById(R.id.tv_refund1);
        tv_refund_unipay = customView.findViewById(R.id.tv_refund_unipay);
        tv_scan = customView.findViewById(R.id.tv_scan);
        tv_settings = customView.findViewById(R.id.tv_settings);
        tv_about = customView.findViewById(R.id.tv_about);
        tv_close = customView.findViewById(R.id.tv_close);
        tv_display_choices = customView.findViewById(R.id.tv_display_choices);
        tv_payment_choices = customView.findViewById(R.id.tv_payment_choices);
        tv_settingMenu = customView.findViewById(R.id.tv_settingMenu);

        inner_frame = (FrameLayout) findViewById(R.id.inner_frame);
        tv_refund_unipay.setVisibility(View.GONE);
        tv_refund.setVisibility(View.GONE);
    }

    public void funcLoyaltyApps() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
        //   if (preferenceManager.isAuthenticated()) {


        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater lf = (LayoutInflater) (DashboardActivity.this)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.loyalty_apps_dialog, null);
        final CheckBox chk_best_deals = dialogview.findViewById(R.id.chk_best_deals);
        final CheckBox chk_entertainment_book = dialogview.findViewById(R.id.chk_entertainment_book);
        final CheckBox chk_mopanion = dialogview.findViewById(R.id.chk_mopanion);
        final CheckBox chk_wine_club = dialogview.findViewById(R.id.chk_wine_club);
        final ImageView img_best_deals = dialogview.findViewById(R.id.img_best_deals);
        final ImageView img_entertainment_book = dialogview.findViewById(R.id.img_entertainment_book);
        final ImageView img_mopanion = dialogview.findViewById(R.id.img_mopanion);
        final ImageView img_wine_club = dialogview.findViewById(R.id.img_wine_club);

        try {
            if (!preferenceManager.getLoyaltyData().equals("")) {
                jsonObjectLoyalty = new JSONObject(preferenceManager.getLoyaltyData());
                chk_best_deals.setChecked(jsonObjectLoyalty.optBoolean("best_deals"));
                chk_entertainment_book.setChecked(jsonObjectLoyalty.optBoolean("entertainment_book"));
                chk_mopanion.setChecked(jsonObjectLoyalty.optBoolean("mopanion"));
                chk_wine_club.setChecked(jsonObjectLoyalty.optBoolean("wine_club"));
            }
        } catch (Exception e) {
        }

        img_best_deals.setOnClickListener((View v) -> {
            funcBestDealsDialog();
        });
        img_entertainment_book.setOnClickListener((View v) -> {
            funcEntertainmentBook();
        });
        img_mopanion.setOnClickListener((View v) -> {
        });
        img_wine_club.setOnClickListener((View v) -> {
        });


        Button btn_ok = dialogview.findViewById(R.id.btn_ok);
        Button btn_cancel = dialogview.findViewById(R.id.btn_cancel);
        dialog.setContentView(dialogview);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        btn_ok.setOnClickListener((View v) -> {
            try {
                callAuthToken();

                if (chk_best_deals.isChecked())
                    jsonObjectLoyalty.put("best_deals", true);
                else
                    jsonObjectLoyalty.put("best_deals", false);

                if (chk_entertainment_book.isChecked())
                    jsonObjectLoyalty.put("entertainment_book", true);
                else
                    jsonObjectLoyalty.put("entertainment_book", false);

                if (chk_mopanion.isChecked())
                    jsonObjectLoyalty.put("mopanion", true);
                else
                    jsonObjectLoyalty.put("mopanion", false);

                if (chk_wine_club.isChecked())
                    jsonObjectLoyalty.put("wine_club", true);
                else
                    jsonObjectLoyalty.put("wine_club", false);
                preferenceManager.setLoyaltyData(jsonObjectLoyalty.toString());
                dialog.dismiss();
            } catch (Exception e) {
            }

        });
        btn_cancel.setOnClickListener((View v) ->
                {
                    dialog.dismiss();
                }
        );

        dialog.getWindow().setAttributes(lp);
        dialog.show();

        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            if (mPopupWindows.isShowing())
                mPopupWindows.dismiss();
            return;
        }
    }


    public void funcBestDealsDialog() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();

        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater lf = (LayoutInflater) (DashboardActivity.this)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.best_deals_layout, null);
        final CheckBox chk_front = dialogview.findViewById(R.id.chk_front);
        final CheckBox chk_back = dialogview.findViewById(R.id.chk_back);
        final CheckBox chk_amount = dialogview.findViewById(R.id.chk_amount);
        final CheckBox chk_terminal_id = dialogview.findViewById(R.id.chk_terminal_id);
        final EditText edt_execute_url = dialogview.findViewById(R.id.edt_execute_url);
        final EditText edt_merchant_id = dialogview.findViewById(R.id.edt_merchant_id);
        final EditText edt_store_id = dialogview.findViewById(R.id.edt_store_id);

        try {
            if (!preferenceManager.getLoyaltyData().equals("")) {
                jsonObjectLoyalty = new JSONObject(preferenceManager.getLoyaltyData());
                JSONObject jsonObject = jsonObjectLoyalty.optJSONObject("best_deals_json");
                chk_front.setChecked(jsonObject.optBoolean("front"));
                chk_back.setChecked(jsonObject.optBoolean("back"));
                chk_amount.setChecked(jsonObject.optBoolean("amount"));
                chk_terminal_id.setChecked(jsonObject.optBoolean("terminal_id"));
                edt_execute_url.setText(jsonObject.optString("execute_url"));
                edt_merchant_id.setText(jsonObject.optString("merchant_id"));
                edt_store_id.setText(jsonObject.optString("store_id"));
            }
        } catch (Exception e) {
        }


        Button btn_ok = dialogview.findViewById(R.id.btn_ok);
        Button btn_cancel = dialogview.findViewById(R.id.btn_cancel);
        dialog.setContentView(dialogview);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        btn_ok.setOnClickListener((View v) -> {
            try {
                callAuthToken();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("execute_url", edt_execute_url.getText().toString());
                jsonObject.put("merchant_id", edt_merchant_id.getText().toString());
                jsonObject.put("store_id", edt_store_id.getText().toString());
                jsonObject.put("front", chk_front.isChecked());
                jsonObject.put("back", chk_back.isChecked());
                jsonObject.put("amount", chk_amount.isChecked());
                jsonObject.put("terminal_id", chk_terminal_id.isChecked());
                jsonObjectLoyalty.put("best_deals_json", jsonObject);
                preferenceManager.setLoyaltyData(jsonObjectLoyalty.toString());
                dialog.dismiss();
            } catch (Exception e) {
            }

        });
        btn_cancel.setOnClickListener((View v) ->
                {
                    dialog.dismiss();

                }
        );

        dialog.getWindow().setAttributes(lp);
        dialog.show();

        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            if (mPopupWindows.isShowing())
                mPopupWindows.dismiss();
            return;
        }
    }

    public void funcEntertainmentBook() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();

        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater lf = (LayoutInflater) (DashboardActivity.this)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.entertainment_book, null);
        final CheckBox chk_front = dialogview.findViewById(R.id.chk_front);
        final CheckBox chk_back = dialogview.findViewById(R.id.chk_back);
        final EditText edt_execute_apps = dialogview.findViewById(R.id.edt_execute_apps);
        final EditText edt_key = dialogview.findViewById(R.id.edt_key);
        try {
            if (!preferenceManager.getLoyaltyData().equals("")) {
                jsonObjectLoyalty = new JSONObject(preferenceManager.getLoyaltyData());
                JSONObject jsonObject = jsonObjectLoyalty.optJSONObject("entertainment_book_json");
                chk_front.setChecked(jsonObject.optBoolean("front"));
                chk_back.setChecked(jsonObject.optBoolean("back"));
                edt_key.setText(jsonObject.optString("key"));
                edt_execute_apps.setText(jsonObject.optString("execute_app"));
            }
        } catch (Exception e) {
        }


        Button btn_ok = dialogview.findViewById(R.id.btn_ok);
        Button btn_cancel = dialogview.findViewById(R.id.btn_cancel);
        dialog.setContentView(dialogview);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        btn_ok.setOnClickListener((View v) -> {
            try {
                callAuthToken();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("execute_app", edt_execute_apps.getText().toString());
                jsonObject.put("key", edt_key.getText().toString());
                jsonObject.put("front", chk_front.isChecked());
                jsonObject.put("back", chk_back.isChecked());
                jsonObjectLoyalty.put("entertainment_book_json", jsonObject);
                preferenceManager.setLoyaltyData(jsonObjectLoyalty.toString());
                dialog.dismiss();
            } catch (Exception e) {
            }

        });
        btn_cancel.setOnClickListener((View v) ->
                {
                    dialog.dismiss();
                }
        );

        dialog.getWindow().setAttributes(lp);
        dialog.show();

        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            if (mPopupWindows.isShowing())
                mPopupWindows.dismiss();
            return;
        }
    }


    public void funcMenuSettlement() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
        // if (preferenceManager.isAuthenticated()) {
        callSetupFragment(SCREENS.SETTLEMEMT, null);
//        } else {
//            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
//        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            if (mPopupWindows.isShowing())
                mPopupWindows.dismiss();
            return;
        }
    }

    public void funcMenuRefundUnionPay() {
        callAuthToken();
        tv_refund.setVisibility(View.GONE);
        tv_refund_unipay.setVisibility(View.GONE);

        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
        // if (preferenceManager.isAuthenticated()) {
        callSetupFragment(SCREENS.REFUND_UNIONPAY, null);
//        } else {
//            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
//        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    public void funcMenuTimezone() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();

        Intent i = new Intent(DashboardActivity.this, TimeZoneActivity.class);
        startActivity(i);

        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    public void funcMenuEOD() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
        //  if (preferenceManager.isAuthenticated()) {
        callSetupFragment(SCREENS.EOD, null);
//        } else {
//            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
//        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    public void functionMenuTransactionLog() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
        // if (preferenceManager.isAuthenticated()) {
        callSetupFragment(SCREENS.TRANSACTION_LIST, null);
//        } else {
//            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
//        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    public void funcMenuHome() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
        // if (preferenceManager.isAuthenticated()) {
        callSetupFragment(SCREENS.POSMATECONNECTION, null);
//        } else {
//            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
//        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    public static boolean isRefundOptionOpen = false;

    public void funcRefundAlipayWeChat() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
        // if (preferenceManager.isAuthenticated()) {
        callSetupFragment(SCREENS.REFUND, null);
//        } else {
//            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
//        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    public void funcMenuScan() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();

        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
        if (preferenceManager.isAuthenticated()) {
            callSetupFragment(SCREENS.MANUALENTRY, "Scan");
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    public void funcMenuManualEntry() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
        //  if (preferenceManager.isAuthenticated()) {
        callSetupFragment(SCREENS.MANUALENTRY, null);
//        } else {
//            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
//        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    public void funcMenuSettings() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();

        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
        callSetupFragment(SCREENS.SETTINGS, null);
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    public void funcMenuHelp() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
        if (preferenceManager.isAuthenticated()) {
            callSetupFragment(SCREENS.HELP, null);
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();

    }

    public void funcMenuAbout() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
//        if (preferenceManager.isAuthenticated()) {
        callSetupFragment(SCREENS.ABOUT, null);
//        } else {
//            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
//        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    public void funcMenuTipSetting() {
        callAuthToken();
        //  if (preferenceManager.isAuthenticated()) {
        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater lf = (LayoutInflater) (DashboardActivity.this)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.fragment_tipsetting, null);
        CheckBox chk_switchTip,chk_default1,chk_default2,chk_default3,chk_default4,chk_default5,chk_custom;
        chk_switchTip= dialogview.findViewById(R.id.chk_switchTip);
        chk_default1= dialogview.findViewById(R.id.chk_default1);
        chk_default2= dialogview.findViewById(R.id.chk_default2);
        chk_default3= dialogview.findViewById(R.id.chk_default3);
        chk_default4= dialogview.findViewById(R.id.chk_default4);
        chk_default5= dialogview.findViewById(R.id.chk_default5);
        chk_custom= dialogview.findViewById(R.id.chk_custom);
        EditText edt_default1,edt_default2,edt_default3,edt_default4,edt_default5;
        edt_default1= dialogview.findViewById(R.id.edt_default1);
        edt_default2= dialogview.findViewById(R.id.edt_default2);
        edt_default3= dialogview.findViewById(R.id.edt_default3);
        edt_default4= dialogview.findViewById(R.id.edt_default4);
        edt_default5= dialogview.findViewById(R.id.edt_default5);



        if (preferenceManager.isSwitchTip())
        {
            chk_switchTip.setChecked(true);
        }else{
            chk_switchTip.setChecked(false);
        }

        if (preferenceManager.isTipDefault1())
        {
            chk_default1.setChecked(true);
        }else{
            chk_default1.setChecked(false);
        }

        if (preferenceManager.isTipDefault2())
        {
            chk_default2.setChecked(true);
        }else{
            chk_default2.setChecked(false);
        }

        if (preferenceManager.isTipDefault3())
        {
            chk_default3.setChecked(true);
        }else{
            chk_default3.setChecked(false);
        }

        if (preferenceManager.isTipDefault4())
        {
            chk_default4.setChecked(true);
        }else{
            chk_default4.setChecked(false);
        }

        if (preferenceManager.isTipDefault5())
        {
            chk_default5.setChecked(true);
        }else{
            chk_default5.setChecked(false);
        }

        if (preferenceManager.isTipDefaultCustom())
        {
            chk_custom.setChecked(true);
        }else{
            chk_custom.setChecked(false);
        }

        ArrayList<String> tipListSaved = preferenceManager.getTipPercentage("Tip");

        if(tipListSaved.size()!=0) {
            edt_default1.setText(tipListSaved.get(0));
            edt_default2.setText(tipListSaved.get(1));
            edt_default3.setText(tipListSaved.get(2));
            edt_default4.setText(tipListSaved.get(3));
            edt_default5.setText(tipListSaved.get(4));
        }

        Button btn_save_and_ok = dialogview.findViewById(R.id.btn_save_and_ok);
        Button btn_cancel_and_close = dialogview.findViewById(R.id.btn_cancel_and_close);

        btn_cancel_and_close.setOnClickListener((View v) -> {
            dialog.dismiss();
            // callGetBranchDetails_new();
        });

        btn_save_and_ok.setOnClickListener((View v) -> {
            String def1=edt_default1.getText().toString().trim();
            String def2=edt_default2.getText().toString().trim();
            String def3=edt_default3.getText().toString().trim();
            String def4=edt_default4.getText().toString().trim();
            String def5=edt_default5.getText().toString().trim();



           /* preferenceManager.setTipDefault1(def1);
            preferenceManager.setTipDefault2(def2);
            preferenceManager.setTipDefault3(def3);
            preferenceManager.setTipDefault4(def4);
            preferenceManager.setTipDefault5(def5);*/
            ArrayList tipList=new ArrayList();
            tipList.add(def1);
            tipList.add(def2);
            tipList.add(def3);
            tipList.add(def4);
            tipList.add(def5);
            preferenceManager.setTipPercentage("Tip",tipList);
            if (chk_switchTip.isChecked())
            {
                preferenceManager.setisSwitchTip(true);
            }else{
                preferenceManager.setisSwitchTip(false);
            }

            if (chk_default1.isChecked())
            {
                preferenceManager.setisTipDefault1(true);
            }else{
                preferenceManager.setisTipDefault1(false);
            }

            if (chk_default2.isChecked())
            {
                preferenceManager.setisTipDefault2(true);
            }else{
                preferenceManager.setisTipDefault2(false);
            }

            if (chk_default3.isChecked())
            {
                preferenceManager.setisTipDefault3(true);
            }else{
                preferenceManager.setisTipDefault3(false);
            }

            if (chk_default4.isChecked())
            {
                preferenceManager.setisTipDefault4(true);
            }else{
                preferenceManager.setisTipDefault4(false);
            }

            if (chk_default5.isChecked())
            {
                preferenceManager.setisTipDefault5(true);
            }else{
                preferenceManager.setisTipDefault5(false);
            }

            if (chk_custom.isChecked())
            {
                preferenceManager.setisTipDefaultCustom(true);
            }else{
                preferenceManager.setisTipDefaultCustom(false);
            }

            callUpdateBranchDetails(funcPrepareDisplayChoicesJSONObject());
            dialog.dismiss();
        });
        dialog.setContentView(dialogview);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    public void funcMenuClose() {
        callAuthToken();
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
//        finish();
        if (openFragmentsReceiver != null)
            unregisterReceiver(openFragmentsReceiver);
        if (intentService != null)
            stopService(intentService);
        if (printDev != null)
            printDev = null;

        finishAffinity();
    }

    boolean isDisplayChoicesDataSaved = false;


    //Display Choices Dialog
    public void funcMenuDisplayChoices() {
        callAuthToken();
        //   if (preferenceManager.isAuthenticated()) {


        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater lf = (LayoutInflater) (DashboardActivity.this)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.diaplay_choice_row, null);
        final RadioButton chk_home = dialogview.findViewById(R.id.chk_home);
        final RadioButton chk_manual = dialogview.findViewById(R.id.chk_manual);
        final CheckBox chk_back = dialogview.findViewById(R.id.chk_back);
        final CheckBox chk_print_qr = dialogview.findViewById(R.id.chk_print_qr);
        final CheckBox chk_display_static_qr = dialogview.findViewById(R.id.chk_display_static_qr);
        final CheckBox chk_ExternalScan = dialogview.findViewById(R.id.chk_ExternalScan);


        final CheckBox chk_display_loyalty_apps = dialogview.findViewById(R.id.chk_display_loyalty_apps);
        final CheckBox chk_front = dialogview.findViewById(R.id.chk_front);
        final CheckBox chk_membership_manual = dialogview.findViewById(R.id.chk_membership_manual);
        final CheckBox chk_membership_home = dialogview.findViewById(R.id.chk_membership_home);
        final CheckBox chk_alipay = dialogview.findViewById(R.id.chk_alipay);
//            final CheckBox chk_scanqr = dialogview.findViewById(R.id.chk_scanqr);
        final CheckBox chk_print_receipt = dialogview.findViewById(R.id.chk_print_receipt);
        final CheckBox chk_wechat = dialogview.findViewById(R.id.chk_wechat);
        final CheckBox chk_vise = dialogview.findViewById(R.id.chk_vise);
        final CheckBox chk_reference = dialogview.findViewById(R.id.chk_reference);
        final CheckBox chk_loyality = dialogview.findViewById(R.id.chk_loyality);
//            final CheckBox chk_aggregated_singleqr = dialogview.findViewById(R.id.chk_aggregated_singleqr);
        final CheckBox chk_ali_display_and_add = dialogview.findViewById(R.id.chk_ali_display_and_add);
        final CheckBox chk_ali_display_only = dialogview.findViewById(R.id.chk_ali_display_only);

        final CheckBox chk_drag_drop = dialogview.findViewById(R.id.chk_drag_drop);

        if (preferenceManager.getshowReference().equals("true")) {
            chk_reference.setChecked(true);
            chk_reference.setSelected(true);
        } else {
            chk_reference.setChecked(false);
            chk_reference.setSelected(false);
        }

        if (preferenceManager.isQR()) {
            chk_print_qr.setChecked(true);
            chk_print_qr.setSelected(true);
        } else {
            chk_print_qr.setChecked(false);
            chk_print_qr.setSelected(false);
        }

        if (preferenceManager.isStaticQR()) {
            chk_display_static_qr.setChecked(true);
            chk_display_static_qr.setSelected(true);
        } else {
            chk_display_static_qr.setChecked(false);
            chk_display_static_qr.setSelected(false);
        }

        if (preferenceManager.isExternalScan()) {
            chk_ExternalScan.setChecked(true);
            chk_ExternalScan.setSelected(true);
        } else {
            chk_ExternalScan.setChecked(false);
            chk_ExternalScan.setSelected(false);
        }

        if (preferenceManager.isDragDrop()) {
            chk_drag_drop.setChecked(true);
            chk_drag_drop.setSelected(true);
        } else {
            chk_drag_drop.setChecked(false);
            chk_drag_drop.setSelected(false);
        }

        if (preferenceManager.isDisplayLoyaltyApps()) {
            chk_display_loyalty_apps.setChecked(true);
            chk_display_loyalty_apps.setSelected(true);
        } else {
            chk_display_loyalty_apps.setChecked(false);
            chk_display_loyalty_apps.setSelected(false);
        }

        if (preferenceManager.isMembershipManual()) {
            chk_membership_manual.setChecked(true);
            chk_membership_manual.setSelected(true);
        } else {
            chk_membership_manual.setChecked(false);
            chk_membership_manual.setSelected(false);
        }


        if (preferenceManager.isMembershipHome()) {
            chk_membership_home.setChecked(true);
            chk_membership_home.setSelected(true);
        } else {
            chk_membership_home.setChecked(false);
            chk_membership_home.setSelected(false);
        }


        if (preferenceManager.isHome()) {
            chk_home.setChecked(true);
            chk_home.setSelected(true);
        } else {
            chk_home.setChecked(false);
            chk_home.setSelected(false);
        }


        if (preferenceManager.isManual()) {
            chk_manual.setChecked(true);
            chk_manual.setSelected(true);
        } else {
            chk_manual.setChecked(false);
            chk_manual.setSelected(false);
        }

        if (preferenceManager.getisPrint().equals("true")) {
            chk_print_receipt.setChecked(true);
            chk_print_receipt.setSelected(true);
        }


        if (preferenceManager.isAlipaySelected()) {
            chk_alipay.setChecked(true);
            chk_alipay.setSelected(true);
        }

        if (preferenceManager.isWechatSelected()) {
            chk_wechat.setChecked(true);
            chk_wechat.setSelected(true);
        }

//            if (preferenceManager.isAlipayWechatQrSelected()) {
//                chk_scanqr.setChecked(true);
//                chk_scanqr.setSelected(true);
//            }
        if (preferenceManager.isLoyality()) {
            chk_loyality.setChecked(true);
            chk_loyality.setSelected(true);
        }

        if (preferenceManager.isBack()) {
            chk_back.setChecked(true);
            chk_back.setSelected(true);
        }

        if (preferenceManager.isFront()) {
            chk_front.setChecked(true);
            chk_front.setSelected(true);
        }


//            if (preferenceManager.isaggregated_singleqr()) {
//                chk_aggregated_singleqr.setChecked(true);
//                chk_aggregated_singleqr.setSelected(true);
//            }


        chk_home.setOnClickListener((View v) -> {
            if (chk_manual.isChecked()) {
                chk_manual.setChecked(false);
                preferenceManager.setIsManual(false);
                preferenceManager.setIsHome(true);
            } else {
                //case 2
                chk_manual.setChecked(false);
                preferenceManager.setIsManual(false);
                preferenceManager.setIsHome(true);
            }
        });


        chk_manual.setOnClickListener((View v) -> {
            if (chk_home.isChecked()) {
                chk_home.setChecked(false);
                preferenceManager.setIsManual(true);
                preferenceManager.setIsHome(false);
            } else {
                //case 2
                chk_home.setChecked(false);
                preferenceManager.setIsManual(true);
                preferenceManager.setIsHome(false);
            }
        });


        chk_membership_home.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_membership_home.setChecked(true);
                preferenceManager.setshowReference("true");
            } else {
                //case 2
                chk_membership_home.setChecked(false);
                preferenceManager.setshowReference("false");
            }
        });


        chk_membership_manual.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_membership_manual.setChecked(true);
                preferenceManager.setisPrint("true");
            } else {
                //case 2
                chk_membership_manual.setChecked(false);
                preferenceManager.setisPrint("false");
            }
        });


        chk_membership_manual.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_membership_manual.setChecked(true);
                preferenceManager.setisMembershipManual(true);
            } else {
                //case 2
                chk_membership_manual.setChecked(false);
                preferenceManager.setisMembershipManual(false);
            }
        });


        chk_membership_home.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_membership_home.setChecked(true);
                preferenceManager.setisMembershipHome(true);
            } else {
                //case 2
                chk_membership_home.setChecked(false);
                preferenceManager.setisMembershipHome(false);
            }
        });


        chk_reference.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_reference.setChecked(true);
                preferenceManager.setshowReference("true");
            } else {
                //case 2
                chk_reference.setChecked(false);
                preferenceManager.setshowReference("false");
            }
        });


        chk_display_static_qr.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_display_static_qr.setChecked(true);
                preferenceManager.setisStaticQR(true);
            } else {
                //case 2
                chk_display_static_qr.setChecked(false);
                preferenceManager.setisStaticQR(false);
            }
        });

        chk_ExternalScan.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_ExternalScan.setChecked(true);
                preferenceManager.setisExternalScan(true);
            } else {
                //case 2
                chk_ExternalScan.setChecked(false);
                preferenceManager.setisExternalScan(false);
            }
        });

        chk_drag_drop.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_drag_drop.setChecked(true);
                preferenceManager.setDragDrop(true);
            } else {
                //case 2
                chk_drag_drop.setChecked(false);
                preferenceManager.setDragDrop(false);
            }
        });

        chk_display_loyalty_apps.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_display_loyalty_apps.setChecked(true);
                preferenceManager.setisDisplayLoyaltyApps(true);
            } else {
                //case 2
                chk_display_loyalty_apps.setChecked(false);
                preferenceManager.setisDisplayLoyaltyApps(false);
            }
        });

        chk_print_qr.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_print_qr.setChecked(true);
                preferenceManager.setisQR(true);
            } else {
                //case 2
                chk_print_qr.setChecked(false);
                preferenceManager.setisQR(false);
            }
        });


        chk_print_receipt.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_print_receipt.setChecked(true);
                preferenceManager.setisPrint("true");
            } else {
                //case 2
                chk_print_receipt.setChecked(false);
                preferenceManager.setisPrint("false");
            }
        });


//            chk_alipay.setOnClickListener((View v) -> {
//                if (((CheckBox) v).isChecked()) {
//                    chk_aggregated_singleqr.setChecked(false);
//                }
//            });

//            chk_wechat.setOnClickListener((View v) -> {
//                if (((CheckBox) v).isChecked()) {
//                    chk_aggregated_singleqr.setChecked(false);
//                }
//            });


//            chk_scanqr.setOnClickListener((View v) -> {
//                if (((CheckBox) v).isChecked()) {
//                    chk_scanqr.setChecked(true);
//                    preferenceManager.setAlipayWechatQrSelected(true);
//
//
//                } else {
//                    //case 2
//                    chk_scanqr.setChecked(false);
//                    preferenceManager.setAlipayWechatQrSelected(false);
//                }
//            });
        chk_loyality.setOnClickListener((View v) -> {
            if (chk_loyality.isChecked()) {
                chk_loyality.setChecked(true);
                preferenceManager.setisLoyality(true);
            } else {
                //case 2
                chk_loyality.setChecked(false);
                preferenceManager.setisLoyality(false);
            }
        });


        chk_back.setOnClickListener((View v) -> {
            if (chk_back.isChecked()) {
                chk_back.setChecked(true);
                preferenceManager.setIsBack(true);
            } else {
                //case 2
                chk_back.setChecked(false);
                preferenceManager.setIsBack(false);
            }
        });


        chk_front.setOnClickListener((View v) -> {
            if (chk_front.isChecked()) {
                chk_front.setChecked(true);
                preferenceManager.setIsFront(true);
            } else {
                //case 2
                chk_front.setChecked(false);
                preferenceManager.setIsFront(false);
            }
        });


        Button btn_ok = dialogview.findViewById(R.id.btn_ok);
        Button btn_cancel = dialogview.findViewById(R.id.btn_cancel);

        dialog.setContentView(dialogview);


        btn_cancel.setOnClickListener((View v) ->
                {
                    dialog.dismiss();
                    callGetBranchDetails_new();

                }
        );

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        btn_ok.setOnClickListener((View v) -> {
            if (chk_alipay.isChecked()) {
                preferenceManager.setisAlipaySelected(true);
            } else {
                preferenceManager.setisAlipaySelected(false);
            }

            if (chk_wechat.isChecked()) {
                preferenceManager.setisWechatSelected(true);
            } else {
                preferenceManager.setisWechatSelected(false);
            }

//                if (chk_aggregated_singleqr.isChecked()) {
//                    preferenceManager.setaggregated_singleqr(true);
//                } else {
//                    preferenceManager.setaggregated_singleqr(false);
//                }

            if (chk_print_receipt.isChecked()) {
                preferenceManager.setisPrint("true");
            } else {
                preferenceManager.setisPrint("false");
            }

            if (chk_home.isChecked()) {
                callSetupFragment(SCREENS.POSMATECONNECTION, null);
            } else {
                callSetupFragment(SCREENS.MANUALENTRY, null);
            }

            isLaunch = true;
            isDisplayChoicesDataSaved = true;
            callAuthToken();
            dialog.dismiss();
        });

        dialog.getWindow().setAttributes(lp);
        dialog.show();

//        } else {
//            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
//        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }


    //Payment Choices Dialog
    public void funcMenuPaymentChoices() {
        callAuthToken();
        //  if (preferenceManager.isAuthenticated()) {
        final Dialog dialog = new Dialog(DashboardActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater lf = (LayoutInflater) (DashboardActivity.this)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.payment_choices, null);
        Button btn_save_and_ok = dialogview.findViewById(R.id.btn_save_and_ok);
        Button btn_cancel_and_close = dialogview.findViewById(R.id.btn_cancel_and_close);


        //functions sequence matters
        funcPCViewInitialize(dialogview);
        funcEdtLengthCalPC();
        funcCVTextChangeListener();
        funcSetPCChkListeners();
        funcPCPrefChecks();


        btn_cancel_and_close.setOnClickListener((View v) -> {
            dialog.dismiss();
            callGetBranchDetails_new();
        });

        btn_save_and_ok.setOnClickListener((View v) -> {
            funcPCSaveAndOK();
            dialog.dismiss();
        });
        dialog.setContentView(dialogview);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

//        } else {
//            Toast.makeText(mContext, getResources().getString(R.string.please_wait_for_authentication), Toast.LENGTH_LONG).show();
//        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mPopupWindows.isShowing())
            mPopupWindows.dismiss();
    }

    //Method for initializing all the views of payment choices option.
    public void funcPCViewInitialize(View dialogView) {
        pcAlipay(dialogView);
        pcWeChat(dialogView);
        pcUPCardDPApp(dialogView);
        pcUPQRScanDPApp(dialogView);
        pcUPUplanScanDPApp(dialogView);
        pcUP_UPIQRScan_MPMCloud(dialogView);
        pcUP_Merchant_QRDisplay(dialogView);
        pcPoli(dialogView);
        pcCentrapay(dialogView);
        //ZIP_V
        pcZip(dialogView);
    }

    private void pcPoli(View dialogView) {
        //POLI
        chk_poli = dialogView.findViewById(R.id.chk_poli);
        chk_poli_display_and_add = dialogView.findViewById(R.id.chk_poli_display_and_add);
        chk_poli_display_only = dialogView.findViewById(R.id.chk_poli_display_only);
        edt_poli_cv = dialogView.findViewById(R.id.edt_poli_cv);
    }

    private void pcCentrapay(View dialogView) {
        //CENTRAPAY
        chk_centrapay_merchant_qr = dialogView.findViewById(R.id.chk_centrapay_merchant_qr);
        chk_centrapay_qr_scan = dialogView.findViewById(R.id.chk_centrapay_qr_scan);
        chk_centrapay_display_and_add = dialogView.findViewById(R.id.chk_centrapay_display_and_add);
        chk_centrapay_display_only = dialogView.findViewById(R.id.chk_centrapay_display_only);
        edt_centrapay_cv = dialogView.findViewById(R.id.edt_centrapay_cv);
    }
    private void pcZip(View dialogView) {
        //ZIP_V
        chk_zip = dialogView.findViewById(R.id.chk_zip);
        chk_zip_qr_scan = dialogView.findViewById(R.id.chk_zip_qr_scan);
        chk_zip_display_and_add = dialogView.findViewById(R.id.chk_zip_display_and_add);
        chk_zip_display_only = dialogView.findViewById(R.id.chk_zip_display_only);
        edt_zip_cv = dialogView.findViewById(R.id.edt_zip_cv);
    }
    private void pcAlipay(View dialogView) {
        //ALIPAY
        chk_alipay = dialogView.findViewById(R.id.chk_alipay);
        chk_alipay_qr_scan = dialogView.findViewById(R.id.chk_alipay_qr_scan);
        chk_ali_display_and_add = dialogView.findViewById(R.id.chk_ali_display_and_add);
        chk_ali_display_only = dialogView.findViewById(R.id.chk_ali_display_only);
        edt_ali_cv = dialogView.findViewById(R.id.edt_ali_cv);
    }

    private void pcWeChat(View dialogView) {
        //WECHAT
        chk_wechat = dialogView.findViewById(R.id.chk_wechat);
        chk_wechat_qr_scan = dialogView.findViewById(R.id.chk_wechat_qr_scan);
        chk_wechat_display_and_add = dialogView.findViewById(R.id.chk_wechat_display_and_add);
        chk_wechat_display_only = dialogView.findViewById(R.id.chk_wechat_display_only);
        edt_wechat_cv = dialogView.findViewById(R.id.edt_wechat_cv);
    }

    private void pcUPCardDPApp(View dialogView) {
        //UNION_PAY CARD DP APP INVOKING PAYMENT CHOICES IDS
        chk_unionpay_card = dialogView.findViewById(R.id.chk_unionpay_card);
        chk_unionpay_card_display_and_add = dialogView.findViewById(R.id.chk_unionpay_card_display_and_add);
        chk_unionpay_card_display_only = dialogView.findViewById(R.id.chk_unionpay_card_display_only);
        edt_unionpay_card_cv = dialogView.findViewById(R.id.edt_unionpay_card_cv);
    }

    private void pcUPQRScanDPApp(View dialogView) {
        //UNION_PAY CUSTOMER WALLET QR SCAN AND DP APP INVOKING FOR PAYMENT ITS PAYMENT CHOICES IDS
        chk_unionpay_qr_code = dialogView.findViewById(R.id.chk_unionpay_qr_code);
        chk_unionpay_qr_display_and_add = dialogView.findViewById(R.id.chk_unionpay_qr_display_and_add);
        chk_unionpay_qr_display_only = dialogView.findViewById(R.id.chk_unionpay_qr_display_only);
        edt_unionpay_qr_cv = dialogView.findViewById(R.id.edt_unionpay_qr_cv);
    }

    private void pcUPUplanScanDPApp(View dialogView) {
        //U-PLAN SCANNING COUPON OPTION AND PAYMENT THROUGH DP APP AND ITS PAYMENT CHOICES IDS
        chk_uplan_qr = dialogView.findViewById(R.id.chk_uplan_qr);
        chk_uplan_display_and_add = dialogView.findViewById(R.id.chk_uplan_display_and_add);
        chk_uplan_display_only = dialogView.findViewById(R.id.chk_uplan_display_only);
        edt_uplan_cv = dialogView.findViewById(R.id.edt_uplan_cv);
    }

    private void pcUP_UPIQRScan_MPMCloud(View dialogView) {
        //DP CUSTOMER WALLET QR SCAN AND PAYMENT THROUGH MPM CLOUD AND ITS PAYMENT CHOICES IDS
        chk_unionpay_qr = dialogView.findViewById(R.id.chk_unionpay_qr);
        chk_up_upi_qr_display_and_add = dialogView.findViewById(R.id.chk_up_upi_qr_display_and_add);
        chk_upi_qr_display_only = dialogView.findViewById(R.id.chk_upi_qr_display_only);
        edt_up_upi_qr_cv = dialogView.findViewById(R.id.edt_up_upi_qr_cv);
        edt_up_upi_qr_cv1 = dialogView.findViewById(R.id.edt_up_upi_qr_cv1);
        edt_up_upi_qr_amount = dialogView.findViewById(R.id.edt_up_upi_qr_amount);

        // upi_note = dialogView.findViewById(R.id.upi_note);
    }


    private void pcUP_Merchant_QRDisplay(View dialogView) {
        chk_upi_qr_merchant_display = dialogView.findViewById(R.id.chk_upi_qr_merchant_display);
    }


    public void funcSetPCChkListeners() {
        //Alipay PC Listeners
        _alipay_chkListener();
        _alipay_QRScan_chkListener();
        _alipay_DAADD_DONLY_chkListener();

        //Zip PC Listeners
        _zip_chkListener();
        _zip_QRScan_chkListener();
        _zip_DAADD_DONLY_chkListener();

        //WeChat PC Listeners
        _weChat_chkListener();
        _weChat_QRScan_chkListener();
        _weChat_DAADD_DONLY_chkListener();

        //UnionPay Card DP App PC Listeners
        _upCard_chkListener();
        _upCard_DPAPP_DAADD_DONLY_chkListener();

        //UnionPay QR Scan DP App PC Listeners
        _upQrScan_chkListener();
        _upQrScan_DPAPP_DAADD_DONLY_chkListener();

        //Uplan Coupon SCan DP App PC Listeners
        _uplan_chkListener();
        _uplan_DAADD_DONLY_chkListener();

        //UP UPI QR Scan using MPMCloud PC Listener
        _up_UPIQRScan_MPMCloud_chkListener();
        _up_UPIQRScan_MPMCloud_DPAPP_DAADD_DONLY_chkListener();


        //POLI PC Listeners
        _poli_chkListener();
        _poli_DAADD_DONLY_chkListener();


        //CENTRAPAY PC Listeners
        _centrapay_chkListener();
        _centrapay_DAADD_DONLY_chkListener();


    }


    private void _centrapay_chkListener()
    {
        chk_centrapay_merchant_qr.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                preferenceManager.setisCentrapayMerchantQRDisplaySelected(true);
            } else {
                preferenceManager.setisCentrapayMerchantQRDisplaySelected(false);
                chk_centrapay_merchant_qr.setChecked(false);
                chk_centrapay_display_and_add.setChecked(false);
                chk_centrapay_display_only.setChecked(false);
                preferenceManager.setcnv_centrapay_display_and_add(false);
                preferenceManager.setcnv_centrapay_display_only(false);
                preferenceManager.setcnv_centrapay("0.00");
                edt_centrapay_cv.setText("0.00");
            }
        });
    }
    //    chk_centrapay_merchant_qr,chk_centrapay_qr_scan,
//    chk_centrapay_display_and_add,,chk_centrapay_display_only
//            edt_centrapay_cv
    private void _centrapay_DAADD_DONLY_chkListener() {
        chk_centrapay_display_and_add.setOnClickListener((View v) -> {

            if (chk_centrapay_merchant_qr.isChecked()) {
                if (edt_centrapay_cv.getText().toString().equals("0.0") ||
                        edt_centrapay_cv.getText().toString().equals("0.00") ||
                        edt_centrapay_cv.getText().toString().equals("")
                        || edt_centrapay_cv.getText().toString().equals(" ")) {
                    chk_centrapay_display_and_add.setChecked(false);
                    chk_centrapay_display_and_add.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String s = edt_centrapay_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_centrapay_display_and_add.setChecked(false);
                        chk_centrapay_display_and_add.setSelected(false);
                        return;
                    }
                    if (chk_centrapay_display_only.isChecked()) {
                        chk_centrapay_display_only.setChecked(false);
                        preferenceManager.setcnv_centrapay_display_only(false);
                        preferenceManager.setcnv_centrapay_display_and_add(true);
                    } else {
                        //case 2
                        chk_centrapay_display_only.setChecked(false);
                        preferenceManager.setcnv_centrapay_display_only(false);
                        preferenceManager.setcnv_centrapay_display_and_add(true);
                    }
                }
                preferenceManager.setcnv_centrapay(edt_centrapay_cv.getText().toString());
            } else {
                chk_centrapay_display_and_add.setChecked(false);
                chk_centrapay_display_and_add.setSelected(false);
                preferenceManager.setcnv_centrapay("0.00");
                Toast.makeText(DashboardActivity.this, "Please select unionpay card", Toast.LENGTH_SHORT).show();
                return;
            }

        });


        chk_centrapay_display_only.setOnClickListener((View v) -> {

            if (chk_centrapay_merchant_qr.isChecked()) {
                if (edt_centrapay_cv.getText().toString().equals("0.0") ||
                        edt_centrapay_cv.getText().toString().equals("0.00") ||
                        edt_centrapay_cv.getText().toString().equals("")
                        || edt_centrapay_cv.getText().toString().equals(" ")) {
                    chk_centrapay_display_only.setChecked(false);
                    chk_centrapay_display_only.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String s = edt_centrapay_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_centrapay_display_only.setChecked(false);
                        chk_centrapay_display_only.setSelected(false);
                        return;
                    }
                    if (chk_centrapay_display_and_add.isChecked()) {
                        chk_centrapay_display_and_add.setChecked(false);
                        preferenceManager.setcnv_centrapay_display_only(true);
                        preferenceManager.setcnv_centrapay_display_and_add(false);
                    } else {
                        //case 2
                        chk_centrapay_display_and_add.setChecked(false);
                        preferenceManager.setcnv_centrapay_display_only(true);
                        preferenceManager.setcnv_centrapay_display_and_add(false);
                    }
                }
                preferenceManager.setcnv_centrapay(edt_centrapay_cv.getText().toString());
            } else {
                chk_centrapay_display_only.setChecked(false);
                chk_centrapay_display_only.setSelected(false);
                preferenceManager.setcnv_centrapay("0.00");
                Toast.makeText(DashboardActivity.this, "Please select unionpay card", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }


    private void _poli_chkListener() {
        chk_poli.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                preferenceManager.setisPoliSelected(true);
            } else {
                preferenceManager.setisPoliSelected(false);
                chk_poli.setChecked(false);
                chk_poli_display_and_add.setChecked(false);
                chk_poli_display_only.setChecked(false);
                preferenceManager.setcnv_poli_display_and_add(false);
                preferenceManager.setcnv_poli_display_only(false);
                preferenceManager.setcnv_poli("0.00");
                edt_poli_cv.setText("0.00");
            }
        });
    }

    private void _poli_DAADD_DONLY_chkListener() {
        chk_poli_display_and_add.setOnClickListener((View v) -> {

            if (chk_poli.isChecked()) {
                if (edt_poli_cv.getText().toString().equals("0.0") ||
                        edt_poli_cv.getText().toString().equals("0.00") ||
                        edt_poli_cv.getText().toString().equals("")
                        || edt_poli_cv.getText().toString().equals(" ")) {
                    chk_poli_display_and_add.setChecked(false);
                    chk_poli_display_and_add.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String s = edt_poli_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_poli_display_and_add.setChecked(false);
                        chk_poli_display_and_add.setSelected(false);
                        return;
                    }
                    if (chk_poli_display_only.isChecked()) {
                        chk_poli_display_only.setChecked(false);
                        preferenceManager.setcnv_poli_display_only(false);
                        preferenceManager.setcnv_poli_display_and_add(true);
                    } else {
                        //case 2
                        chk_poli_display_only.setChecked(false);
                        preferenceManager.setcnv_poli_display_only(false);
                        preferenceManager.setcnv_poli_display_and_add(true);
                    }
                }
                preferenceManager.setcnv_poli(edt_poli_cv.getText().toString());
            } else {
                chk_poli_display_and_add.setChecked(false);
                chk_poli_display_and_add.setSelected(false);
                preferenceManager.setcnv_poli("0.00");
                Toast.makeText(DashboardActivity.this, "Please select unionpay card", Toast.LENGTH_SHORT).show();
                return;
            }

        });


        chk_poli_display_only.setOnClickListener((View v) -> {

            if (chk_poli.isChecked()) {
                if (edt_poli_cv.getText().toString().equals("0.0") ||
                        edt_poli_cv.getText().toString().equals("0.00") ||
                        edt_poli_cv.getText().toString().equals("")
                        || edt_poli_cv.getText().toString().equals(" ")) {
                    chk_poli_display_only.setChecked(false);
                    chk_poli_display_only.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String s = edt_poli_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_poli_display_only.setChecked(false);
                        chk_poli_display_only.setSelected(false);
                        return;
                    }
                    if (chk_poli_display_and_add.isChecked()) {
                        chk_poli_display_and_add.setChecked(false);
                        preferenceManager.setcnv_poli_display_only(true);
                        preferenceManager.setcnv_poli_display_and_add(false);
                    } else {
                        //case 2
                        chk_poli_display_and_add.setChecked(false);
                        preferenceManager.setcnv_poli_display_only(true);
                        preferenceManager.setcnv_poli_display_and_add(false);
                    }
                }
                preferenceManager.setcnv_poli(edt_poli_cv.getText().toString());
            } else {
                chk_poli_display_only.setChecked(false);
                chk_poli_display_only.setSelected(false);
                preferenceManager.setcnv_poli("0.00");
                Toast.makeText(DashboardActivity.this, "Please select unionpay card", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }


    //WeChat PC Listeners
    private void _weChat_chkListener() {
        chk_wechat.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_wechat.setChecked(true);
                preferenceManager.setisWechatSelected(true);


            } else {
                //case 2
                chk_wechat.setChecked(false);
                preferenceManager.setisWechatSelected(false);

                if (!chk_wechat_qr_scan.isChecked()) {
                    chk_wechat_display_and_add.setChecked(false);
                    chk_wechat_display_only.setChecked(false);
                    preferenceManager.setcnv_wechat_display_only(false);
                    preferenceManager.setcnv_wechat_display_and_add(false);
                    preferenceManager.setcnv_wechat("0.00");
                    edt_wechat_cv.setText("0.00");
                }
            }
        });
    }

    private void _weChat_QRScan_chkListener() {
        chk_wechat_qr_scan.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_wechat_qr_scan.setChecked(true);
                preferenceManager.setisWeChatScan(true);
//                chk_alipay_qr_scan.setChecked(false);
//                preferenceManager.setisAlipayScan(false);

            } else {
                //case 2
                chk_wechat_qr_scan.setChecked(false);
                preferenceManager.setisWeChatScan(false);

                if (!chk_wechat.isChecked()) {
                    chk_wechat_display_and_add.setChecked(false);
                    chk_wechat_display_only.setChecked(false);
                    preferenceManager.setcnv_wechat_display_only(false);
                    preferenceManager.setcnv_wechat_display_and_add(false);
                    preferenceManager.setcnv_wechat("0.00");
                    edt_wechat_cv.setText("0.00");
                }


            }
        });

    }

    private void _weChat_DAADD_DONLY_chkListener() {
        chk_wechat_display_and_add.setOnClickListener((View v) -> {
            if ((chk_wechat.isChecked() || chk_wechat_qr_scan.isChecked()) ||
                    (chk_wechat.isChecked() && chk_wechat_qr_scan.isChecked())) {
                if (edt_wechat_cv.getText().toString().equals("0.0") ||
                        edt_wechat_cv.getText().toString().equals("0.00") ||
                        edt_wechat_cv.getText().toString().equals("")
                        || edt_wechat_cv.getText().toString().equals(" ")) {
                    chk_wechat_display_and_add.setChecked(false);
                    chk_wechat_display_and_add.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //is chkIos checked?
                    String s = edt_wechat_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_wechat_display_and_add.setChecked(false);
                        chk_wechat_display_and_add.setSelected(false);
                        return;
                    }
                    if (chk_ali_display_only.isChecked()) {
                        chk_ali_display_only.setChecked(false);
                        preferenceManager.setcnv_wechat_display_only(false);
                        preferenceManager.setcnv_wechat_display_and_add(true);
                    } else {
                        //case 2
                        chk_ali_display_only.setChecked(false);
                        preferenceManager.setcnv_wechat_display_only(false);
                        preferenceManager.setcnv_wechat_display_and_add(true);
                    }
                }
                preferenceManager.setcnv_wechat(edt_wechat_cv.getText().toString());
            } else {
                chk_wechat_display_and_add.setChecked(false);
                chk_wechat_display_and_add.setSelected(false);
                preferenceManager.setcnv_wechat("0.00");
                Toast.makeText(DashboardActivity.this, "Please select wechat", Toast.LENGTH_SHORT).show();
                return;
            }

        });

        chk_wechat_display_only.setOnClickListener((View v) -> {

            if ((chk_wechat.isChecked() || chk_wechat_qr_scan.isChecked()) ||
                    (chk_wechat.isChecked() && chk_wechat_qr_scan.isChecked())) {
                if (edt_wechat_cv.getText().toString().equals("0.0") ||
                        edt_wechat_cv.getText().toString().equals("0.00") ||
                        edt_wechat_cv.getText().toString().equals("")
                        || edt_wechat_cv.getText().toString().equals(" ")) {
                    chk_wechat_display_only.setChecked(false);
                    chk_wechat_display_only.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    //is chkIos checked?
                    String s = edt_wechat_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_wechat_display_only.setChecked(false);
                        chk_wechat_display_only.setSelected(false);
                        return;
                    }
                    if (chk_wechat_display_and_add.isChecked()) {
                        chk_wechat_display_and_add.setChecked(false);
                        preferenceManager.setcnv_wechat_display_only(true);
                        preferenceManager.setcnv_wechat_display_and_add(false);
                    } else {
                        //case 2
                        chk_wechat_display_and_add.setChecked(false);
                        preferenceManager.setcnv_wechat_display_only(true);
                        preferenceManager.setcnv_wechat_display_and_add(false);
                    }

                }
                preferenceManager.setcnv_wechat(edt_wechat_cv.getText().toString());
            } else {
                chk_wechat_display_only.setChecked(false);
                chk_wechat_display_only.setSelected(false);
                preferenceManager.setcnv_wechat("0.00");
                Toast.makeText(DashboardActivity.this, "Please select wechat", Toast.LENGTH_SHORT).show();
                return;
            }

        });
    }
    //Zip PC Listeners
    private void _zip_chkListener() {
        chk_zip.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_zip.setChecked(true);
                preferenceManager.setisZipSelected(true);


            } else {
                //case 2
                chk_zip.setChecked(false);
                preferenceManager.setisZipSelected(false);

                if (!chk_zip_qr_scan.isChecked()) {
                    chk_zip_display_and_add.setChecked(false);
                    chk_zip_display_only.setChecked(false);
                    preferenceManager.setcnv_zip_diaplay_and_add(false);
                    preferenceManager.setcnv_zip_diaplay_only(false);
                    preferenceManager.setcnv_alipay("0.00");
                    edt_zip_cv.setText("0.00");
                }
            }
        });
    }

    private void _zip_QRScan_chkListener() {
        chk_zip_qr_scan.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_zip_qr_scan.setChecked(true);
                preferenceManager.setisZipScan(true);
//                chk_wechat_qr_scan.setChecked(false);
//                preferenceManager.setisWeChatScan(false);

            } else {
                //case 2
                chk_zip_qr_scan.setChecked(false);
                preferenceManager.setisZipScan(false);

                if (!chk_zip.isChecked()) {
                    chk_zip_display_and_add.setChecked(false);
                    chk_zip_display_only.setChecked(false);
                    preferenceManager.setcnv_zip_diaplay_and_add(false);
                    preferenceManager.setcnv_zip_diaplay_only(false);
                    preferenceManager.setcnv_zip("0.00");
                    edt_zip_cv.setText("0.00");
                }
            }
        });

    }
    private void _zip_DAADD_DONLY_chkListener() {
        chk_zip_display_and_add.setOnClickListener((View v) -> {

            if ((chk_zip.isChecked() || chk_zip_qr_scan.isChecked()) ||
                    (chk_zip.isChecked() && chk_zip_qr_scan.isChecked())) {
                if (edt_zip_cv.getText().toString().equals("0.0") ||
                        edt_zip_cv.getText().toString().equals("0.00") ||
                        edt_zip_cv.getText().toString().equals("")
                        || edt_zip_cv.getText().toString().equals(" ")) {
                    chk_zip_display_and_add.setChecked(false);
                    chk_zip_display_and_add.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //is chkIos checked?
                    String s = edt_zip_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_zip_display_and_add.setChecked(false);
                        chk_zip_display_and_add.setSelected(false);
                        return;
                    }
                    if (chk_zip_display_only.isChecked()) {
                        chk_zip_display_only.setChecked(false);
                        preferenceManager.setcnv_zip_diaplay_only(false);
                        preferenceManager.setcnv_zip_diaplay_and_add(true);
                    } else {
                        //case 2
                        chk_zip_display_only.setChecked(false);
                        preferenceManager.setcnv_zip_diaplay_only(false);
                        preferenceManager.setcnv_zip_diaplay_and_add(true);
                    }
                }
                preferenceManager.setcnv_zip(edt_zip_cv.getText().toString());
            } else {
                chk_zip_display_and_add.setChecked(false);
                chk_zip_display_and_add.setSelected(false);
                preferenceManager.setcnv_zip("0.00");
                Toast.makeText(DashboardActivity.this, "Please select the alipay", Toast.LENGTH_SHORT).show();
                return;
            }

        });

        chk_zip_display_only.setOnClickListener((View v) -> {

            if ((chk_zip.isChecked() || chk_zip_qr_scan.isChecked()) ||
                    (chk_zip.isChecked() && chk_zip_qr_scan.isChecked())) {
                if (edt_zip_cv.getText().toString().equals("0.0") ||
                        edt_zip_cv.getText().toString().equals("0.00") ||
                        edt_zip_cv.getText().toString().equals("")
                        || edt_zip_cv.getText().toString().equals(" ")) {
                    chk_zip_display_only.setChecked(false);
                    chk_zip_display_only.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    //is chkIos checked?
                    String s = edt_zip_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_zip_display_only.setChecked(false);
                        chk_zip_display_only.setSelected(false);
                        return;
                    }
                    if (chk_zip_display_and_add.isChecked()) {
                        chk_zip_display_and_add.setChecked(false);
                        preferenceManager.setcnv_zip_diaplay_only(true);
                        preferenceManager.setcnv_zip_diaplay_and_add(false);
                    } else {
                        //case 2
                        chk_zip_display_and_add.setChecked(false);
                        preferenceManager.setcnv_zip_diaplay_only(true);
                        preferenceManager.setcnv_zip_diaplay_and_add(false);
                    }

                }
                preferenceManager.setcnv_zip(edt_zip_cv.getText().toString());
            } else {
                chk_zip_display_only.setChecked(false);
                chk_zip_display_only.setSelected(false);
                preferenceManager.setcnv_zip("0.00");
                Toast.makeText(DashboardActivity.this, "Please select zip", Toast.LENGTH_SHORT).show();
                return;
            }

        });
    }
    //Alipay PC Listeners
    private void _alipay_chkListener() {
        chk_alipay.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_alipay.setChecked(true);
                preferenceManager.setisAlipaySelected(true);


            } else {
                //case 2
                chk_alipay.setChecked(false);
                preferenceManager.setisAlipaySelected(false);

                if (!chk_alipay_qr_scan.isChecked()) {
                    chk_ali_display_and_add.setChecked(false);
                    chk_ali_display_only.setChecked(false);
                    preferenceManager.setcnv_alipay_diaplay_and_add(false);
                    preferenceManager.setcnv_alipay_diaplay_only(false);
                    preferenceManager.setcnv_alipay("0.00");
                    edt_ali_cv.setText("0.00");
                }


            }
        });
    }

    private void _alipay_QRScan_chkListener() {
        chk_alipay_qr_scan.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_alipay_qr_scan.setChecked(true);
                preferenceManager.setisAlipayScan(true);
//                chk_wechat_qr_scan.setChecked(false);
//                preferenceManager.setisWeChatScan(false);

            } else {
                //case 2
                chk_alipay_qr_scan.setChecked(false);
                preferenceManager.setisAlipayScan(false);

                if (!chk_alipay.isChecked()) {
                    chk_ali_display_and_add.setChecked(false);
                    chk_ali_display_only.setChecked(false);
                    preferenceManager.setcnv_alipay_diaplay_and_add(false);
                    preferenceManager.setcnv_alipay_diaplay_only(false);
                    preferenceManager.setcnv_alipay("0.00");
                    edt_ali_cv.setText("0.00");
                }
            }
        });

    }

    private void _alipay_DAADD_DONLY_chkListener() {
        chk_ali_display_and_add.setOnClickListener((View v) -> {

            if ((chk_alipay.isChecked() || chk_alipay_qr_scan.isChecked()) ||
                    (chk_alipay.isChecked() && chk_alipay_qr_scan.isChecked())) {
                if (edt_ali_cv.getText().toString().equals("0.0") ||
                        edt_ali_cv.getText().toString().equals("0.00") ||
                        edt_ali_cv.getText().toString().equals("")
                        || edt_ali_cv.getText().toString().equals(" ")) {
                    chk_ali_display_and_add.setChecked(false);
                    chk_ali_display_and_add.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //is chkIos checked?
                    String s = edt_ali_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_ali_display_and_add.setChecked(false);
                        chk_ali_display_and_add.setSelected(false);
                        return;
                    }
                    if (chk_ali_display_only.isChecked()) {
                        chk_ali_display_only.setChecked(false);
                        preferenceManager.setcnv_alipay_diaplay_only(false);
                        preferenceManager.setcnv_alipay_diaplay_and_add(true);
                    } else {
                        //case 2
                        chk_ali_display_only.setChecked(false);
                        preferenceManager.setcnv_alipay_diaplay_only(false);
                        preferenceManager.setcnv_alipay_diaplay_and_add(true);
                    }
                }
                preferenceManager.setcnv_alipay(edt_ali_cv.getText().toString());
            } else {
                chk_ali_display_and_add.setChecked(false);
                chk_ali_display_and_add.setSelected(false);
                preferenceManager.setcnv_alipay("0.00");
                Toast.makeText(DashboardActivity.this, "Please select the alipay", Toast.LENGTH_SHORT).show();
                return;
            }

        });

        chk_ali_display_only.setOnClickListener((View v) -> {

            if ((chk_alipay.isChecked() || chk_alipay_qr_scan.isChecked()) ||
                    (chk_alipay.isChecked() && chk_alipay_qr_scan.isChecked())) {
                if (edt_ali_cv.getText().toString().equals("0.0") ||
                        edt_ali_cv.getText().toString().equals("0.00") ||
                        edt_ali_cv.getText().toString().equals("")
                        || edt_ali_cv.getText().toString().equals(" ")) {
                    chk_ali_display_only.setChecked(false);
                    chk_ali_display_only.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    //is chkIos checked?
                    String s = edt_ali_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_ali_display_only.setChecked(false);
                        chk_ali_display_only.setSelected(false);
                        return;
                    }
                    if (chk_ali_display_and_add.isChecked()) {
                        chk_ali_display_and_add.setChecked(false);
                        preferenceManager.setcnv_alipay_diaplay_only(true);
                        preferenceManager.setcnv_alipay_diaplay_and_add(false);
                    } else {
                        //case 2
                        chk_ali_display_and_add.setChecked(false);
                        preferenceManager.setcnv_alipay_diaplay_only(true);
                        preferenceManager.setcnv_alipay_diaplay_and_add(false);
                    }

                }
                preferenceManager.setcnv_alipay(edt_ali_cv.getText().toString());
            } else {
                chk_ali_display_only.setChecked(false);
                chk_ali_display_only.setSelected(false);
                preferenceManager.setcnv_alipay("0.00");
                Toast.makeText(DashboardActivity.this, "Please select alipay", Toast.LENGTH_SHORT).show();
                return;
            }

        });
    }

    //UnionPay Card DP App PC Listeners
    private void _upCard_chkListener() {
        chk_unionpay_card.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                preferenceManager.setisUnionPaySelected(true);
            } else {
                preferenceManager.setisUnionPaySelected(false);
                chk_unionpay_card.setChecked(false);
                chk_unionpay_card_display_and_add.setChecked(false);
                chk_unionpay_card_display_only.setChecked(false);
                preferenceManager.setcnv_uni_display_and_add(false);
                preferenceManager.setcnv_uni_display_only(false);
                preferenceManager.setcnv_uni("0.00");
                edt_unionpay_card_cv.setText("0.00");
            }
        });
    }

    private void _upCard_DPAPP_DAADD_DONLY_chkListener() {
        chk_unionpay_card_display_and_add.setOnClickListener((View v) -> {

            if (chk_unionpay_card.isChecked()) {
                if (edt_unionpay_card_cv.getText().toString().equals("0.0") ||
                        edt_unionpay_card_cv.getText().toString().equals("0.00") ||
                        edt_unionpay_card_cv.getText().toString().equals("")
                        || edt_unionpay_card_cv.getText().toString().equals(" ")) {
                    chk_unionpay_card_display_and_add.setChecked(false);
                    chk_unionpay_card_display_and_add.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String s = edt_unionpay_card_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_unionpay_card_display_and_add.setChecked(false);
                        chk_unionpay_card_display_and_add.setSelected(false);
                        return;
                    }
                    if (chk_unionpay_card_display_only.isChecked()) {
                        chk_unionpay_card_display_only.setChecked(false);
                        preferenceManager.setcnv_uni_display_only(false);
                        preferenceManager.setcnv_uni_display_and_add(true);
                    } else {
                        //case 2
                        chk_unionpay_card_display_only.setChecked(false);
                        preferenceManager.setcnv_uni_display_only(false);
                        preferenceManager.setcnv_uni_display_and_add(true);
                    }
                }
                preferenceManager.setcnv_uni(edt_unionpay_card_cv.getText().toString());
            } else {
                chk_unionpay_card_display_and_add.setChecked(false);
                chk_unionpay_card_display_and_add.setSelected(false);
                preferenceManager.setcnv_uni("0.00");
                Toast.makeText(DashboardActivity.this, "Please select unionpay card", Toast.LENGTH_SHORT).show();
                return;
            }

        });


        chk_unionpay_card_display_only.setOnClickListener((View v) -> {

            if (chk_unionpay_card.isChecked()) {
                if (edt_unionpay_card_cv.getText().toString().equals("0.0") ||
                        edt_unionpay_card_cv.getText().toString().equals("0.00") ||
                        edt_unionpay_card_cv.getText().toString().equals("")
                        || edt_unionpay_card_cv.getText().toString().equals(" ")) {
                    chk_unionpay_card_display_only.setChecked(false);
                    chk_unionpay_card_display_only.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String s = edt_unionpay_card_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_unionpay_card_display_only.setChecked(false);
                        chk_unionpay_card_display_only.setSelected(false);
                        return;
                    }
                    if (chk_unionpay_card_display_and_add.isChecked()) {
                        chk_unionpay_card_display_and_add.setChecked(false);
                        preferenceManager.setcnv_uni_display_only(true);
                        preferenceManager.setcnv_uni_display_and_add(false);
                    } else {
                        //case 2
                        chk_unionpay_card_display_and_add.setChecked(false);
                        preferenceManager.setcnv_uni_display_only(true);
                        preferenceManager.setcnv_uni_display_and_add(false);
                    }
                }
                preferenceManager.setcnv_uni(edt_unionpay_card_cv.getText().toString());
            } else {
                chk_unionpay_card_display_only.setChecked(false);
                chk_unionpay_card_display_only.setSelected(false);
                preferenceManager.setcnv_uni("0.00");
                Toast.makeText(DashboardActivity.this, "Please select unionpay card", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    //UnionPay QR Scan DP App PC Listeners
    private void _upQrScan_chkListener() {

        chk_unionpay_qr.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_unionpay_qr.setChecked(true);
                preferenceManager.setUnionPayQrSelected(true);

                //if unionpay qr is selected then disable the upi qr
                chk_unionpay_qr_code.setChecked(false);
                preferenceManager.setisUnionPayQrCodeDisplaySelected(false);

                if (!chk_upi_qr_merchant_display.isChecked()) {
                    chk_upi_qr_display_only.setChecked(false);
                    chk_up_upi_qr_display_and_add.setChecked(false);
                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(false);
                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(false);
                    preferenceManager.setcnv_up_upiqr_mpmcloud_lower("0.00");
                    edt_up_upi_qr_cv.setText("0.00");
                    edt_up_upi_qr_cv1.setText("0.00");
                    edt_up_upi_qr_amount.setText("0.00");
                }


//                chk_upi_qr_display_only.setChecked(false);
//                chk_up_upi_qr_display_and_add.setChecked(false);
//                preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(false);
//                preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(false);
//                preferenceManager.setcnv_up_upiqr_mpmcloud_lower("0.00");
//                edt_up_upi_qr_cv.setText("0.00");


            } else {
                //case 2
                chk_unionpay_qr.setChecked(false);
                preferenceManager.setUnionPayQrSelected(false);
                chk_unionpay_qr_display_and_add.setChecked(false);
                chk_unionpay_qr_display_only.setChecked(false);
                preferenceManager.setcnv_unionpayqr_display_only(false);
                preferenceManager.setcnv_unionpayqr_display_and_add(false);
                preferenceManager.setcnv_uniqr("0.00");
                edt_unionpay_qr_cv.setText("0.00");

//                if (!chk_upi_qr_merchant_display.isChecked()) {
//                    chk_unionpay_qr_display_and_add.setChecked(false);
//                    chk_unionpay_qr_display_only.setChecked(false);
//                    preferenceManager.setcnv_unionpayqr_display_only(false);
//                    preferenceManager.setcnv_unionpayqr_display_and_add(false);
//                    preferenceManager.setcnv_uniqr("0.00");
//                    edt_unionpay_qr_cv.setText("0.00");
//                }

                /*if (!chk_unionpay_qr_code.isChecked()) {
                    chk_unionpay_qr_display_and_add.setChecked(false);
                    chk_unionpay_qr_display_only.setChecked(false);
                    preferenceManager.setcnv_unionpayqr_display_only(false);
                    preferenceManager.setcnv_unionpayqr_display_and_add(false);
                    preferenceManager.setcnv_uniqr("0.00");
                    edt_unionpay_qr_cv.setText("0.00");

                }*/
            }
        });
    }

    private void _upQrScan_DPAPP_DAADD_DONLY_chkListener() {
        chk_unionpay_qr_display_and_add.setOnClickListener((View v) -> {

            if (!chk_unionpay_qr.isChecked()) {
                chk_unionpay_qr_display_and_add.setChecked(false);
                return;
            }

            if (chk_unionpay_qr.isChecked() || chk_unionpay_qr_code.isChecked()) {
                if (edt_unionpay_qr_cv.getText().toString().equals("0.0") ||
                        edt_unionpay_qr_cv.getText().toString().equals("0.00") ||
                        edt_unionpay_qr_cv.getText().toString().equals("")
                        || edt_unionpay_qr_cv.getText().toString().equals(" ")) {
                    chk_unionpay_qr_display_and_add.setChecked(false);
                    chk_unionpay_qr_display_and_add.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //is chkIos checked?
                    String s = edt_unionpay_qr_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_unionpay_qr_display_and_add.setChecked(false);
                        chk_unionpay_qr_display_and_add.setSelected(false);
                        return;
                    }
                    if (chk_unionpay_qr_display_only.isChecked()) {
                        chk_unionpay_qr_display_only.setChecked(false);
                        preferenceManager.setcnv_unionpayqr_display_only(false);
                        preferenceManager.setcnv_unionpayqr_display_and_add(true);
                    } else {
                        //case 2
                        chk_unionpay_qr_display_only.setChecked(false);
                        preferenceManager.setcnv_unionpayqr_display_only(false);
                        preferenceManager.setcnv_unionpayqr_display_and_add(true);
                    }
                }
                preferenceManager.setcnv_uniqr(edt_unionpay_qr_cv.getText().toString());
            } else {
                chk_unionpay_qr_display_and_add.setChecked(false);
                chk_unionpay_qr_display_and_add.setSelected(false);
                preferenceManager.setcnv_uniqr("0.00");
                Toast.makeText(DashboardActivity.this, "Please select the union pay qr or union pay scan", Toast.LENGTH_SHORT).show();
                return;
            }


        });


        chk_unionpay_qr_display_only.setOnClickListener((View v) -> {

            if (chk_unionpay_qr.isChecked() || chk_unionpay_qr_code.isChecked()) {
                if (edt_unionpay_qr_cv.getText().toString().equals("0.0") ||
                        edt_unionpay_qr_cv.getText().toString().equals("0.00") ||
                        edt_unionpay_qr_cv.getText().toString().equals("")
                        || edt_unionpay_qr_cv.getText().toString().equals(" ")) {
                    chk_unionpay_qr_display_only.setChecked(false);
                    chk_unionpay_qr_display_only.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    //is chkIos checked?
                    String s = edt_unionpay_qr_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_unionpay_qr_display_only.setChecked(false);
                        chk_unionpay_qr_display_only.setSelected(false);
                        return;
                    }
                    if (chk_unionpay_qr_display_and_add.isChecked()) {
                        chk_unionpay_qr_display_and_add.setChecked(false);
                        preferenceManager.setcnv_unionpayqr_display_only(true);
                        preferenceManager.setcnv_unionpayqr_display_and_add(false);
                    } else {
                        //case 2
                        chk_unionpay_qr_display_and_add.setChecked(false);
                        preferenceManager.setcnv_unionpayqr_display_only(true);
                        preferenceManager.setcnv_unionpayqr_display_and_add(false);
                    }

                }
                preferenceManager.setcnv_uniqr(edt_unionpay_qr_cv.getText().toString());
            } else {
                chk_unionpay_qr_display_only.setChecked(false);
                chk_unionpay_qr_display_only.setSelected(false);
                preferenceManager.setcnv_uniqr("0.00");
                Toast.makeText(DashboardActivity.this, "Please select the union pay qr or union pay scan", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    //Uplan Coupon SCan DP App PC Listeners
    private void _uplan_chkListener() {
        chk_uplan_qr.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_uplan_qr.setChecked(true);
                preferenceManager.setisUplanSelected(true);
            } else {
                //case 2
                chk_uplan_qr.setChecked(false);
                preferenceManager.setisUplanSelected(false);
                chk_uplan_qr.setChecked(false);
                chk_uplan_display_only.setChecked(false);
                chk_uplan_display_and_add.setChecked(false);
                preferenceManager.setcnv_uplan_display_only(false);
                preferenceManager.setcnv_uplan_display_and_add(false);
                preferenceManager.setcnv_uplan("0.00");
                edt_uplan_cv.setText("0.00");
            }
        });
    }

    private void _uplan_DAADD_DONLY_chkListener() {
        chk_uplan_display_and_add.setOnClickListener((View v) -> {

            if (chk_uplan_qr.isChecked()) {
                if (edt_uplan_cv.getText().toString().equals("0.0") ||
                        edt_uplan_cv.getText().toString().equals("0.00") ||
                        edt_uplan_cv.getText().toString().equals("")
                        || edt_uplan_cv.getText().toString().equals(" ")) {
                    chk_uplan_display_and_add.setChecked(false);
                    chk_uplan_display_and_add.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //is chkIos checked?
                    String s = edt_uplan_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_uplan_display_and_add.setChecked(false);
                        chk_uplan_display_and_add.setSelected(false);
                        return;
                    }
                    if (chk_uplan_display_only.isChecked()) {
                        chk_uplan_display_only.setChecked(false);
                        preferenceManager.setcnv_uplan_display_only(false);
                        preferenceManager.setcnv_uplan_display_and_add(true);
                    } else {
                        //case 2
                        chk_uplan_display_only.setChecked(false);
                        preferenceManager.setcnv_uplan_display_only(false);
                        preferenceManager.setcnv_uplan_display_and_add(true);
                    }
                }
                preferenceManager.setcnv_uplan(edt_uplan_cv.getText().toString());
            } else {
                chk_uplan_display_and_add.setChecked(false);
                chk_uplan_display_and_add.setSelected(false);
                preferenceManager.setcnv_uplan("0.00");
                Toast.makeText(DashboardActivity.this, "Please select the uplan", Toast.LENGTH_SHORT).show();
                return;
            }

        });


        chk_uplan_display_only.setOnClickListener((View v) -> {

            if (chk_uplan_qr.isChecked()) {
                if (edt_uplan_cv.getText().toString().equals("0.0") ||
                        edt_uplan_cv.getText().toString().equals("0.00") ||
                        edt_uplan_cv.getText().toString().equals("")
                        || edt_uplan_cv.getText().toString().equals(" ")) {
                    chk_uplan_display_only.setChecked(false);
                    chk_uplan_display_only.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    //is chkIos checked?
                    String s = edt_uplan_cv.getText().toString();
                    if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                        chk_uplan_display_only.setChecked(false);
                        chk_uplan_display_only.setSelected(false);
                        return;
                    }
                    if (chk_uplan_display_and_add.isChecked()) {
                        chk_uplan_display_and_add.setChecked(false);
                        preferenceManager.setcnv_uplan_display_only(true);
                        preferenceManager.setcnv_uplan_display_and_add(false);
                    } else {
                        //case 2
                        chk_uplan_display_and_add.setChecked(false);
                        preferenceManager.setcnv_uplan_display_only(true);
                        preferenceManager.setcnv_uplan_display_and_add(false);
                    }

                }
                preferenceManager.setcnv_uplan(edt_uplan_cv.getText().toString());
            } else {
                chk_uplan_display_only.setChecked(false);
                chk_uplan_display_only.setSelected(false);
                preferenceManager.setcnv_uplan("0.00");
                Toast.makeText(DashboardActivity.this, "Please select uplan", Toast.LENGTH_SHORT).show();
                return;
            }

        });

    }

    //UP UPI QR Scan using MPMCloud PC Listener
    private void _up_UPIQRScan_MPMCloud_chkListener() {

        chk_upi_qr_merchant_display.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_upi_qr_merchant_display.setChecked(true);
                preferenceManager.setisMerchantDPARDisplay(true);


            } else {
                //case 2
                chk_upi_qr_merchant_display.setChecked(false);
                preferenceManager.setisMerchantDPARDisplay(false);

                if (!chk_unionpay_qr_code.isChecked()) {
                    chk_up_upi_qr_display_and_add.setChecked(false);
                    chk_upi_qr_display_only.setChecked(false);
                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(false);
                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(false);
                    preferenceManager.setcnv_up_upiqr_mpmcloud_lower("0.00");
                    edt_up_upi_qr_cv.setText("0.00");
                    edt_up_upi_qr_cv1.setText("0.00");
                    edt_up_upi_qr_amount.setText("0.00");
                }
            }
        });


        chk_unionpay_qr_code.setOnClickListener((View v) -> {
            if (((CheckBox) v).isChecked()) {
                chk_unionpay_qr_code.setChecked(true);
                preferenceManager.setisUnionPayQrCodeDisplaySelected(true);

                //if upi qr is selected then disable the unionpay qr
                chk_unionpay_qr.setChecked(false);
                preferenceManager.setUnionPayQrSelected(false);
                chk_unionpay_qr_display_and_add.setChecked(false);
                chk_unionpay_qr_display_only.setChecked(false);
                preferenceManager.setcnv_unionpayqr_display_only(false);
                preferenceManager.setcnv_unionpayqr_display_and_add(false);
                preferenceManager.setcnv_uniqr("0.00");
                edt_unionpay_qr_cv.setText("0.00");

            } else {
                //case 2
                chk_unionpay_qr_code.setChecked(false);
                preferenceManager.setisUnionPayQrCodeDisplaySelected(false);

                if (!chk_upi_qr_merchant_display.isChecked()) {
                    chk_upi_qr_display_only.setChecked(false);
                    chk_up_upi_qr_display_and_add.setChecked(false);
                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(false);
                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(false);
                    preferenceManager.setcnv_up_upiqr_mpmcloud_lower("0.00");
                    edt_up_upi_qr_cv.setText("0.00");
                    edt_up_upi_qr_cv1.setText("0.00");
                    edt_up_upi_qr_amount.setText("0.00");
                }


                if (!chk_upi_qr_merchant_display.isChecked()) {
                    chk_up_upi_qr_display_and_add.setChecked(false);
                    chk_upi_qr_display_only.setChecked(false);
                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(false);
                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(false);
                    preferenceManager.setcnv_up_upiqr_mpmcloud_lower("0.00");
                    edt_up_upi_qr_cv.setText("0.00");
                    edt_up_upi_qr_cv1.setText("0.00");
                    edt_up_upi_qr_amount.setText("0.00");
                }


               /* if (!chk_unionpay_qr.isChecked()) {
                    chk_unionpay_qr_display_and_add.setChecked(false);
                    chk_unionpay_qr_display_only.setChecked(false);
                    preferenceManager.setcnv_unionpayqr_display_only(false);
                    preferenceManager.setcnv_unionpayqr_display_and_add(false);
                    preferenceManager.setcnv_uniqr("0.00");
                    edt_unionpay_qr_cv.setText("0.00");

                }*/

            }
        });

    }

    private void _up_UPIQRScan_MPMCloud_DPAPP_DAADD_DONLY_chkListener() {
        chk_up_upi_qr_display_and_add.setOnClickListener((View v) -> {


            if (chk_unionpay_qr_code.isChecked() || chk_upi_qr_merchant_display.isChecked()) {
                if ((edt_up_upi_qr_cv.getText().toString().equals("0.0") ||
                        edt_up_upi_qr_cv.getText().toString().equals("0.00") ||
                        edt_up_upi_qr_cv.getText().toString().equals("")
                        || edt_up_upi_qr_cv.getText().toString().equals(" ")) &&
                        (edt_up_upi_qr_cv1.getText().toString().equals("0.0") ||
                                edt_up_upi_qr_cv1.getText().toString().equals("0.00") ||
                                edt_up_upi_qr_cv1.getText().toString().equals("")
                                || edt_up_upi_qr_cv1.getText().toString().equals(" ")) &&
                        (edt_up_upi_qr_amount.getText().toString().equals("0.0") ||
                                edt_up_upi_qr_amount.getText().toString().equals("0.00") ||
                                edt_up_upi_qr_amount.getText().toString().equals("")
                                || edt_up_upi_qr_amount.getText().toString().equals(" "))) {
                    chk_up_upi_qr_display_and_add.setChecked(false);
                    chk_up_upi_qr_display_and_add.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (chk_upi_qr_display_only.isChecked()) {
                        chk_upi_qr_display_only.setChecked(false);
                        preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(false);
                        preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(true);
                    } else {
                        //case 2
                        chk_upi_qr_display_only.setChecked(false);
                        preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(false);
                        preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(true);
                    }

                }
                preferenceManager.setcnv_up_upiqr_mpmcloud_lower(edt_up_upi_qr_cv.getText().toString());
                preferenceManager.setCnv_up_upiqr_mpmcloud_higher(edt_up_upi_qr_cv1.getText().toString());

                preferenceManager.set_cnv_unimerchantqrdisplayLower(edt_up_upi_qr_cv.getText().toString());
                preferenceManager.set_cnv_unimerchantqrdisplayHigher(edt_up_upi_qr_cv1.getText().toString());

                preferenceManager.setCnv_up_upiqr_mpmcloud_amount(edt_up_upi_qr_amount.getText().toString());

            } else {
                chk_up_upi_qr_display_and_add.setChecked(false);
                chk_up_upi_qr_display_and_add.setSelected(false);
                preferenceManager.setcnv_up_upiqr_mpmcloud_lower("0.00");
                preferenceManager.setCnv_up_upiqr_mpmcloud_higher("0.00");

                preferenceManager.set_cnv_unimerchantqrdisplayLower("0.00");
                preferenceManager.set_cnv_unimerchantqrdisplayHigher("0.00");

                preferenceManager.setCnv_up_upiqr_mpmcloud_amount("0.00");
                Toast.makeText(DashboardActivity.this, "Please select the union pay qr or union pay scan", Toast.LENGTH_SHORT).show();
                return;
            }

        });


        chk_upi_qr_display_only.setOnClickListener((View v) -> {

            if (chk_unionpay_qr_code.isChecked() || chk_upi_qr_merchant_display.isChecked()) {
                if ((edt_up_upi_qr_cv.getText().toString().equals("0.0") ||
                        edt_up_upi_qr_cv.getText().toString().equals("0.00") ||
                        edt_up_upi_qr_cv.getText().toString().equals("")
                        || edt_up_upi_qr_cv.getText().toString().equals(" ")) &&
                        (edt_up_upi_qr_cv1.getText().toString().equals("0.0") ||
                                edt_up_upi_qr_cv1.getText().toString().equals("0.00") ||
                                edt_up_upi_qr_cv1.getText().toString().equals("")
                                || edt_up_upi_qr_cv1.getText().toString().equals(" ")) &&
                        (edt_up_upi_qr_amount.getText().toString().equals("0.0") ||
                                edt_up_upi_qr_amount.getText().toString().equals("0.00") ||
                                edt_up_upi_qr_amount.getText().toString().equals("")
                                || edt_up_upi_qr_amount.getText().toString().equals(" "))) {
                    chk_upi_qr_display_only.setChecked(false);
                    chk_upi_qr_display_only.setSelected(false);
                    Toast.makeText(DashboardActivity.this, "Please enter the fee amount.", Toast.LENGTH_SHORT).show();
                    return;
                } else {


                    if (chk_up_upi_qr_display_and_add.isChecked()) {
                        chk_up_upi_qr_display_and_add.setChecked(false);
                        preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(true);
                        preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(false);
                    } else {
                        //case 2
                        chk_up_upi_qr_display_and_add.setChecked(false);
                        preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(true);
                        preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(false);
                    }

                }
                preferenceManager.setcnv_up_upiqr_mpmcloud_lower(edt_up_upi_qr_cv.getText().toString());
                preferenceManager.setCnv_up_upiqr_mpmcloud_higher(edt_up_upi_qr_cv1.getText().toString());

                preferenceManager.set_cnv_unimerchantqrdisplayLower(edt_up_upi_qr_cv.getText().toString());
                preferenceManager.set_cnv_unimerchantqrdisplayHigher(edt_up_upi_qr_cv1.getText().toString());

                preferenceManager.setCnv_up_upiqr_mpmcloud_amount(edt_up_upi_qr_amount.getText().toString());
            } else {
                chk_upi_qr_display_only.setChecked(false);
                chk_upi_qr_display_only.setSelected(false);
                preferenceManager.setcnv_up_upiqr_mpmcloud_lower("0.00");
                preferenceManager.setCnv_up_upiqr_mpmcloud_higher("0.00");

                preferenceManager.set_cnv_unimerchantqrdisplayLower("0.00");
                preferenceManager.set_cnv_unimerchantqrdisplayHigher("0.00");

                preferenceManager.setCnv_up_upiqr_mpmcloud_amount("0.00");
                Toast.makeText(DashboardActivity.this, "Please select the union pay qr or union pay scan", Toast.LENGTH_SHORT).show();
                return;
            }

        });
    }


    public void funcPCPrefChecks() {
        if (preferenceManager.is_cnv_centrapay_display_and_add() ||
                preferenceManager.is_cnv_centrapay_display_only() ||
                preferenceManager.is_cnv_poli_display_and_add() ||
                preferenceManager.is_cnv_poli_display_only() ||
                preferenceManager.is_cnv_uni_display_only() ||
                preferenceManager.is_cnv_uni_display_and_add() ||
                preferenceManager.cnv_unionpayqr_display_and_add() ||
                preferenceManager.cnv_unionpayqr_display_only() ||
                preferenceManager.cnv_uplan_display_and_add() ||
                preferenceManager.cnv_uplan_display_only() ||
                preferenceManager.is_cnv_wechat_display_and_add() ||
                preferenceManager.is_cnv_wechat_display_only() ||
                preferenceManager.is_cnv_alipay_display_only() ||
                preferenceManager.is_cnv_alipay_display_and_add() ||
                preferenceManager.is_cnv_zip_display_only() ||
                preferenceManager.is_cnv_zip_display_and_add() ||
                preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add() ||
                preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only()
        ) {

            if (preferenceManager.is_cnv_centrapay_display_and_add()) {
                chk_centrapay_display_and_add.setChecked(true);
                chk_centrapay_display_and_add.setSelected(true);
            }
            if (preferenceManager.is_cnv_centrapay_display_only()) {
                chk_centrapay_display_only.setChecked(true);
                chk_centrapay_display_only.setSelected(true);
            }


            if (preferenceManager.is_cnv_poli_display_and_add()) {
                chk_poli_display_and_add.setChecked(true);
                chk_poli_display_and_add.setSelected(true);
            }
            if (preferenceManager.is_cnv_poli_display_only()) {
                chk_poli_display_only.setChecked(true);
                chk_poli_display_only.setSelected(true);
            }

            if (preferenceManager.cnv_uplan_display_and_add()) {
                chk_uplan_display_and_add.setChecked(true);
                chk_uplan_display_and_add.setSelected(true);
            }

            if (preferenceManager.cnv_uplan_display_only()) {
                chk_uplan_display_only.setChecked(true);
                chk_uplan_display_only.setSelected(true);
            }

            if (preferenceManager.cnv_unionpayqr_display_and_add()) {
                chk_unionpay_qr_display_and_add.setChecked(true);
                chk_unionpay_qr_display_and_add.setSelected(true);
            }

            if (preferenceManager.cnv_unionpayqr_display_only()) {
                chk_unionpay_qr_display_only.setChecked(true);
                chk_unionpay_qr_display_only.setSelected(true);
            }


            if (preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) {
                chk_up_upi_qr_display_and_add.setChecked(true);
                chk_up_upi_qr_display_and_add.setSelected(true);
            }

            if (preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only()) {
                chk_upi_qr_display_only.setChecked(true);
                chk_upi_qr_display_only.setSelected(true);
            }


            if (preferenceManager.is_cnv_alipay_display_and_add()) {
                chk_ali_display_and_add.setChecked(true);
                chk_ali_display_and_add.setSelected(true);
            }


            if (preferenceManager.is_cnv_alipay_display_only()) {
                chk_ali_display_only.setChecked(true);
                chk_ali_display_only.setChecked(true);
            }

            if (preferenceManager.is_cnv_zip_display_and_add()) {
                chk_zip_display_and_add.setChecked(true);
                chk_zip_display_and_add.setSelected(true);
            }

            if (preferenceManager.is_cnv_zip_display_only()) {
                chk_zip_display_only.setChecked(true);
                chk_zip_display_only.setChecked(true);
            }


            if (preferenceManager.is_cnv_wechat_display_only()) {
                chk_wechat_display_only.setChecked(true);
                chk_wechat_display_only.setSelected(true);
            }

            if (preferenceManager.is_cnv_wechat_display_and_add()) {
                chk_wechat_display_and_add.setChecked(true);
                chk_wechat_display_and_add.setSelected(true);
            }


            if (preferenceManager.is_cnv_uni_display_and_add()) {
                chk_unionpay_card_display_and_add.setChecked(true);
                chk_unionpay_card_display_and_add.setSelected(true);
            }


            if (preferenceManager.is_cnv_uni_display_only()) {
                chk_unionpay_card_display_only.setChecked(true);
                chk_unionpay_card_display_only.setSelected(true);
            }

            if (preferenceManager.isWeChatScan()) {
                chk_wechat_qr_scan.setChecked(true);
                chk_wechat_qr_scan.setSelected(true);
            }

            if (preferenceManager.isAlipayScan()) {
                chk_alipay_qr_scan.setChecked(true);
                chk_alipay_qr_scan.setSelected(true);
            }

            if (preferenceManager.isZipScan()) {
                chk_zip_qr_scan.setChecked(true);
                chk_zip_qr_scan.setSelected(true);
            }


            edt_centrapay_cv.setText(preferenceManager.getcnv_centrapay());
            edt_poli_cv.setText(preferenceManager.getcnv_poli());
            edt_unionpay_card_cv.setText(preferenceManager.getcnv_uni());
            edt_unionpay_qr_cv.setText(preferenceManager.getcnv_uniqr());
            edt_uplan_cv.setText(preferenceManager.getcnv_uplan());
            edt_ali_cv.setText(preferenceManager.getcnv_alipay());
            edt_zip_cv.setText(preferenceManager.getcnv_zip());
            edt_wechat_cv.setText(preferenceManager.getcnv_wechat());
            if (!preferenceManager.getcnv_up_upiqr_mpmcloud_lower().equals("0.00") &&
                    !preferenceManager.getcnv_up_upiqr_mpmcloud_lower().equals(""))
                edt_up_upi_qr_cv.setText(preferenceManager.getcnv_up_upiqr_mpmcloud_lower());
            else {
                if(!preferenceManager.get_cnv_unimerchantqrdisplayLower().equalsIgnoreCase(""))
                edt_up_upi_qr_cv.setText(preferenceManager.get_cnv_unimerchantqrdisplayLower());
            }

            if (!preferenceManager.getCnv_up_upiqr_mpmcloud_higher().equals("0.00") &&
                    !preferenceManager.getCnv_up_upiqr_mpmcloud_higher().equals(""))
                edt_up_upi_qr_cv1.setText(preferenceManager.getCnv_up_upiqr_mpmcloud_higher());
            else {
                if(!preferenceManager.get_cnv_unimerchantqrdisplayHigher().equalsIgnoreCase(""))
                edt_up_upi_qr_cv1.setText(preferenceManager.get_cnv_unimerchantqrdisplayHigher());

            }
            if(!preferenceManager.getCnv_up_upiqr_mpmcloud_amount().equalsIgnoreCase(""))
            edt_up_upi_qr_amount.setText(preferenceManager.getCnv_up_upiqr_mpmcloud_amount());



        }


        if (preferenceManager.is_cnv_alipay_display_and_add()) {
            chk_ali_display_and_add.setChecked(true);
            chk_ali_display_and_add.setSelected(true);
        }


        if (preferenceManager.is_cnv_alipay_display_only()) {
            chk_ali_display_only.setChecked(true);
            chk_ali_display_only.setChecked(true);
        }

        if (preferenceManager.is_cnv_wechat_display_only()) {
            chk_wechat_display_only.setChecked(true);
            chk_wechat_display_only.setSelected(true);
        }
        if (preferenceManager.is_cnv_zip_display_and_add()) {
            chk_zip_display_and_add.setChecked(true);
            chk_zip_display_and_add.setSelected(true);
        }

        if (preferenceManager.is_cnv_zip_display_only()) {
            chk_zip_display_only.setChecked(true);
            chk_zip_display_only.setChecked(true);
        }
        if (preferenceManager.is_cnv_wechat_display_and_add()) {
            chk_wechat_display_and_add.setChecked(true);
            chk_wechat_display_and_add.setSelected(true);
        }

        if (preferenceManager.isZipScan()) {
            chk_zip_qr_scan.setSelected(true);
            chk_zip_qr_scan.setChecked(true);
        }
        if (preferenceManager.is_cnv_uni_display_and_add()) {
            chk_unionpay_card_display_and_add.setChecked(true);
            chk_unionpay_card_display_and_add.setSelected(true);
        }


        if (preferenceManager.is_cnv_uni_display_only()) {
            chk_unionpay_card_display_only.setChecked(true);
            chk_unionpay_card_display_only.setSelected(true);
        }


        if (preferenceManager.cnv_unionpayqr_display_and_add()) {
            chk_unionpay_qr_display_and_add.setChecked(true);
            chk_unionpay_qr_display_and_add.setSelected(true);
        }
        if (preferenceManager.cnv_unionpayqr_display_only()) {
            chk_unionpay_qr_display_only.setChecked(true);
            chk_unionpay_qr_display_only.setSelected(true);
        }


        if (preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add()) {
            chk_up_upi_qr_display_and_add.setChecked(true);
            chk_up_upi_qr_display_and_add.setSelected(true);
        }

        if (preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only()) {
            chk_upi_qr_display_only.setChecked(true);
            chk_upi_qr_display_only.setSelected(true);
        }


        if (preferenceManager.isUnipaySelected() &&
                preferenceManager.isUnionPaySelected()) {
            chk_unionpay_card.setChecked(true);
            chk_unionpay_card.setSelected(true);
        } else {
            chk_unionpay_card.setChecked(false);
            chk_unionpay_card.setSelected(false);
        }
        if (preferenceManager.isUplanSelected()) {
            chk_uplan_qr.setChecked(true);
            chk_uplan_qr.setSelected(true);
        }

        if (preferenceManager.isUnionPayQrSelected()) {
            chk_unionpay_qr.setChecked(true);
            chk_unionpay_qr.setSelected(true);
        }
        if (preferenceManager.isMerchantDPARDisplay()) {
            chk_upi_qr_merchant_display.setChecked(true);
            chk_upi_qr_merchant_display.setSelected(true);
        }

        if (preferenceManager.isUnionPayQrCodeDisplaySelected()) {
            chk_unionpay_qr_code.setChecked(true);
            chk_unionpay_qr_code.setSelected(true);
        }

        if (preferenceManager.isAlipaySelected()) {
            chk_alipay.setChecked(true);
            chk_alipay.setSelected(true);
        }
        if (preferenceManager.isZipSelected()) {
            chk_zip.setChecked(true);
            chk_zip.setSelected(true);
        }
        if (preferenceManager.isWechatSelected()) {
            chk_wechat.setChecked(true);
            chk_wechat.setSelected(true);
        }
        if (preferenceManager.isCentrapayMerchantQRDisplaySelected()) {
            chk_centrapay_merchant_qr.setChecked(true);
            chk_centrapay_merchant_qr.setSelected(true);
        }
        if (preferenceManager.isPoliSelected()) {
            chk_poli.setChecked(true);
            chk_poli.setSelected(true);
        }

        if (preferenceManager.isAlipayScan()) {
            chk_alipay_qr_scan.setSelected(true);
            chk_alipay_qr_scan.setChecked(true);
        }


        if (preferenceManager.isWeChatScan()) {
            chk_wechat_qr_scan.setSelected(true);
            chk_wechat_qr_scan.setChecked(true);
        }

        if (preferenceManager.isUplanSelected()) {
            chk_uplan_qr.setSelected(true);
            chk_uplan_qr.setChecked(true);
        }

        //start added on 11/14/2019
        if (edt_ali_cv.getText().toString().equals("0.00") ||
                edt_ali_cv.getText().toString().equals("")
                || edt_ali_cv.getText().toString().equals("0.0")) {
            preferenceManager.setcnv_alipay("0.00");
            preferenceManager.setcnv_alipay_diaplay_and_add(false);
            preferenceManager.setcnv_alipay_diaplay_only(false);
            chk_ali_display_and_add.setSelected(false);
            chk_ali_display_only.setSelected(false);
            chk_ali_display_and_add.setChecked(false);
            chk_ali_display_only.setChecked(false);
        }
        if (edt_zip_cv.getText().toString().equals("0.00") ||
                edt_zip_cv.getText().toString().equals("")
                || edt_zip_cv.getText().toString().equals("0.0")) {
            preferenceManager.setcnv_zip("0.00");
            preferenceManager.setcnv_zip_diaplay_and_add(false);
            preferenceManager.setcnv_zip_diaplay_only(false);
            chk_zip_display_and_add.setSelected(false);
            chk_zip_display_only.setSelected(false);
            chk_zip_display_and_add.setChecked(false);
            chk_zip_display_only.setChecked(false);
        }
        if (edt_wechat_cv.getText().toString().equals("0.00") ||
                edt_wechat_cv.getText().toString().equals("")
                || edt_wechat_cv.getText().toString().equals("0.0")) {
            preferenceManager.setcnv_wechat("0.00");
            preferenceManager.setcnv_wechat_display_and_add(false);
            preferenceManager.setcnv_wechat_display_only(false);
            chk_wechat_display_and_add.setSelected(false);
            chk_wechat_display_only.setSelected(false);
            chk_wechat_display_and_add.setChecked(false);
            chk_wechat_display_only.setChecked(false);
        }

        if (edt_unionpay_card_cv.getText().toString().equals("") ||
                edt_unionpay_card_cv.getText().toString().equals("0.00") ||
                edt_unionpay_card_cv.getText().toString().equals("0.0")) {
            preferenceManager.setcnv_uni("0.00");
            preferenceManager.setcnv_uni_display_and_add(false);
            preferenceManager.setcnv_uni_display_only(false);
            chk_unionpay_card_display_and_add.setSelected(false);
            chk_unionpay_card_display_only.setSelected(false);
            chk_unionpay_card_display_and_add.setChecked(false);
            chk_unionpay_card_display_only.setChecked(false);
        }
        if (edt_centrapay_cv.getText().toString().equals("") ||
                edt_centrapay_cv.getText().toString().equals("0.00") ||
                edt_centrapay_cv.getText().toString().equals("0.0")) {
            preferenceManager.setcnv_centrapay("0.00");
            preferenceManager.setcnv_centrapay_display_and_add(false);
            preferenceManager.setcnv_centrapay_display_only(false);
            chk_centrapay_display_and_add.setSelected(false);
            chk_centrapay_display_only.setSelected(false);
            chk_centrapay_display_and_add.setChecked(false);
            chk_centrapay_display_only.setChecked(false);
        }
        if (edt_poli_cv.getText().toString().equals("") ||
                edt_poli_cv.getText().toString().equals("0.00") ||
                edt_poli_cv.getText().toString().equals("0.0")) {
            preferenceManager.setcnv_poli("0.00");
            preferenceManager.setcnv_poli_display_and_add(false);
            preferenceManager.setcnv_poli_display_only(false);
            chk_poli_display_and_add.setSelected(false);
            chk_poli_display_only.setSelected(false);
            chk_poli_display_and_add.setChecked(false);
            chk_poli_display_only.setChecked(false);
        }
        if(!chk_upi_qr_merchant_display.isChecked() && !chk_unionpay_qr_code.isChecked() )
        {
            preferenceManager.setCnv_up_upiqr_mpmcloud_higher("0.00");
            preferenceManager.setcnv_up_upiqr_mpmcloud_lower("0.00");
            preferenceManager.set_cnv_unimerchantqrdisplayLower("0.00");
            preferenceManager.set_cnv_unimerchantqrdisplayHigher("0.00");
            preferenceManager.setCnv_up_upiqr_mpmcloud_amount("0.00");
            edt_up_upi_qr_cv1.setText("0.00");
            edt_up_upi_qr_cv.setText("0.00");
            edt_up_upi_qr_amount.setText("0.00");
        }

        //end added on 11/14/2019
    }

    public void funcPCSaveAndOK() {
        if (chk_ali_display_and_add.isChecked() ||
                chk_ali_display_only.isChecked() ||
                chk_zip_display_and_add.isChecked() ||
                chk_zip_display_only.isChecked() ||
                chk_wechat_display_and_add.isChecked() ||
                chk_wechat_display_only.isChecked() ||
                chk_unionpay_card_display_and_add.isChecked() ||
                chk_unionpay_card_display_only.isChecked() ||
                chk_unionpay_qr_display_and_add.isChecked() ||
                chk_unionpay_qr_display_only.isChecked() ||
                chk_uplan_display_only.isChecked() ||
                chk_uplan_display_and_add.isChecked() ||
                chk_up_upi_qr_display_and_add.isChecked() ||
                chk_upi_qr_display_only.isChecked() ||
                chk_poli_display_and_add.isChecked() ||
                chk_poli_display_only.isChecked()||
                chk_centrapay_display_and_add.isChecked() ||
                chk_centrapay_display_only.isChecked()
        ) {
            preferenceManager.setisConvenienceFeeSelected(true);

            if (chk_ali_display_and_add.isChecked())
                preferenceManager.setcnv_alipay_diaplay_and_add(true);
            else
                preferenceManager.setcnv_alipay_diaplay_and_add(false);

            if (chk_ali_display_only.isChecked())
                preferenceManager.setcnv_alipay_diaplay_only(true);
            else
                preferenceManager.setcnv_alipay_diaplay_only(false);

            if (chk_zip_display_and_add.isChecked())
                preferenceManager.setcnv_zip_diaplay_and_add(true);
            else
                preferenceManager.setcnv_zip_diaplay_and_add(false);

            if (chk_zip_display_only.isChecked())
                preferenceManager.setcnv_zip_diaplay_only(true);
            else
                preferenceManager.setcnv_zip_diaplay_only(false);


            if (chk_wechat_display_and_add.isChecked())
                preferenceManager.setcnv_wechat_display_and_add(true);
            else
                preferenceManager.setcnv_wechat_display_and_add(false);

            if (chk_wechat_display_only.isChecked())
                preferenceManager.setcnv_wechat_display_only(true);
            else
                preferenceManager.setcnv_wechat_display_only(false);


            if (chk_unionpay_card_display_and_add.isChecked())
                preferenceManager.setcnv_uni_display_and_add(true);
            else
                preferenceManager.setcnv_uni_display_and_add(false);

            if (chk_unionpay_card_display_only.isChecked())
                preferenceManager.setcnv_uni_display_only(true);
            else
                preferenceManager.setcnv_uni_display_only(false);


            if (chk_poli_display_and_add.isChecked()) {
                preferenceManager.setcnv_poli_display_and_add(true);
            } else {
                preferenceManager.setcnv_poli_display_and_add(false);
            }

            if (chk_poli_display_only.isChecked()) {
                preferenceManager.setcnv_poli_display_only(true);
            } else {
                preferenceManager.setcnv_poli_display_only(false);
            }

            if (chk_centrapay_display_and_add.isChecked()) {
                preferenceManager.setcnv_centrapay_display_and_add(true);
            } else {
                preferenceManager.setcnv_centrapay_display_and_add(false);
            }

            if (chk_centrapay_display_only.isChecked()) {
                preferenceManager.setcnv_centrapay_display_only(true);
            } else {
                preferenceManager.setcnv_centrapay_display_only(false);
            }

            if (chk_centrapay_merchant_qr.isChecked()) {
                if (!edt_centrapay_cv.getText().toString().equals("0.0") ||
                        !edt_centrapay_cv.getText().toString().equals("0.00")
                        || !edt_centrapay_cv.getText().toString().equals("")) {
                    if (chk_centrapay_display_and_add.isChecked())
                        preferenceManager.setcnv_centrapay_display_and_add(true);
                    else
                        preferenceManager.setcnv_centrapay_display_and_add(false);
                    if (chk_centrapay_display_only.isChecked())
                        preferenceManager.setcnv_centrapay_display_only(true);
                    else
                        preferenceManager.setcnv_centrapay_display_only(false);

                }
            }


            if (chk_poli.isChecked()) {
                if (!edt_poli_cv.getText().toString().equals("0.0") ||
                        !edt_poli_cv.getText().toString().equals("0.00")
                        || !edt_poli_cv.getText().toString().equals("")) {
                    if (chk_poli_display_and_add.isChecked())
                        preferenceManager.setcnv_poli_display_and_add(true);
                    else
                        preferenceManager.setcnv_poli_display_and_add(false);
                    if (chk_poli_display_only.isChecked())
                        preferenceManager.setcnv_poli_display_only(true);
                    else
                        preferenceManager.setcnv_poli_display_only(false);

                }
            }


            //if unionpayqr or unionpay qr scan is selected then only
            // display and add And display only option will be checked
            // based on the convinence fee value
            if (chk_unionpay_qr.isChecked()) {
                if (!edt_unionpay_qr_cv.getText().toString().equals("0.0") ||
                        !edt_unionpay_qr_cv.getText().toString().equals("0.00")
                        || !edt_unionpay_qr_cv.getText().toString().equals("")) {
                    //display and add of union pay qr
                    if (chk_unionpay_qr_display_and_add.isChecked())
                        preferenceManager.setcnv_unionpayqr_display_and_add(true);
                    else
                        preferenceManager.setcnv_unionpayqr_display_and_add(false);
                    //display only of union pay qr
                    if (chk_unionpay_qr_display_only.isChecked())
                        preferenceManager.setcnv_unionpayqr_display_only(true);
                    else
                        preferenceManager.setcnv_unionpayqr_display_only(false);

                }
            }


            if (chk_unionpay_qr_code.isChecked() || chk_upi_qr_merchant_display.isChecked()) {
                if ((!edt_up_upi_qr_cv.getText().toString().equals("0.0") ||
                        !edt_up_upi_qr_cv.getText().toString().equals("0.00")
                        || !edt_up_upi_qr_cv.getText().toString().equals("")) &&
                        (!edt_up_upi_qr_cv1.getText().toString().equals("0.0") ||
                                !edt_up_upi_qr_cv1.getText().toString().equals("0.00")
                                || !edt_up_upi_qr_cv1.getText().toString().equals("")) &&
                        (!edt_up_upi_qr_amount.getText().toString().equals("0.0") ||
                                !edt_up_upi_qr_amount.getText().toString().equals("0.00")
                                || !edt_up_upi_qr_amount.getText().toString().equals(""))) {
                    //display and add of union pay qr
                    if (chk_up_upi_qr_display_and_add.isChecked())
                        preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(true);
                    else
                        preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(false);
                    //display only of union pay qr
                    if (chk_upi_qr_display_only.isChecked())
                        preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(true);
                    else
                        preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(false);

                    if (chk_up_upi_qr_display_and_add.isChecked() || chk_upi_qr_display_only.isChecked()) {
                        if (chk_unionpay_qr_code.isChecked()) {
                            Double a = edt_up_upi_qr_amount.getText().toString().isEmpty() ? 0.00 : calculatecnv(edt_up_upi_qr_amount.getText().toString());
                            if (a != 0.00) {
                                preferenceManager.setcnv_up_upiqr_mpmcloud_lower(edt_up_upi_qr_cv.getText().toString());
                                preferenceManager.setCnv_up_upiqr_mpmcloud_higher(edt_up_upi_qr_cv1.getText().toString());
                                preferenceManager.setCnv_up_upiqr_mpmcloud_amount(edt_up_upi_qr_amount.getText().toString());
                            } else {
                                preferenceManager.setcnv_up_upiqr_mpmcloud_lower("0.00");
                                preferenceManager.setCnv_up_upiqr_mpmcloud_higher("0.00");
                                preferenceManager.setCnv_up_upiqr_mpmcloud_amount("0.00");
                            }

                        } else if (!chk_unionpay_qr_code.isChecked()) {
                            preferenceManager.setcnv_up_upiqr_mpmcloud_lower("0.00");
                            preferenceManager.setCnv_up_upiqr_mpmcloud_higher("0.00");
                            if (!chk_upi_qr_merchant_display.isChecked())
                                preferenceManager.setCnv_up_upiqr_mpmcloud_amount("0.00");
                        }

                        if (chk_upi_qr_merchant_display.isChecked()) {
                            Double a = edt_up_upi_qr_amount.getText().toString().isEmpty() ? 0.00 : calculatecnv(edt_up_upi_qr_amount.getText().toString());
                            if (a != 0.00) {
                                preferenceManager.set_cnv_unimerchantqrdisplayLower(edt_up_upi_qr_cv.getText().toString());
                                preferenceManager.set_cnv_unimerchantqrdisplayHigher(edt_up_upi_qr_cv1.getText().toString());
                                preferenceManager.setCnv_up_upiqr_mpmcloud_amount(edt_up_upi_qr_amount.getText().toString());
                            } else {
                                preferenceManager.set_cnv_unimerchantqrdisplayLower("0.00");
                                preferenceManager.set_cnv_unimerchantqrdisplayHigher("0.00");
                                preferenceManager.setCnv_up_upiqr_mpmcloud_amount("0.00");
                            }

                        } else if (!chk_upi_qr_merchant_display.isChecked()) {
                            preferenceManager.set_cnv_unimerchantqrdisplayLower("0.00");
                            preferenceManager.set_cnv_unimerchantqrdisplayHigher("0.00");
                            if (!chk_unionpay_qr_code.isChecked())
                            preferenceManager.setCnv_up_upiqr_mpmcloud_amount("0.00");
                        }

                    }


                }
            }


            if (chk_uplan_qr.isChecked()) {
                if (!edt_uplan_cv.getText().toString().equals("0.0") ||
                        !edt_uplan_cv.getText().toString().equals("0.00")
                        || !edt_uplan_cv.getText().toString().equals("")) {
                    if (chk_uplan_display_and_add.isChecked())
                        preferenceManager.setcnv_uplan_display_and_add(true);
                    else
                        preferenceManager.setcnv_uplan_display_and_add(false);
                    if (chk_uplan_display_only.isChecked())
                        preferenceManager.setcnv_uplan_display_only(true);
                    else
                        preferenceManager.setcnv_uplan_display_only(false);

                }
            }
            if (chk_zip.isChecked()) {
                if (!edt_zip_cv.getText().toString().equals("0.0") ||
                        !edt_zip_cv.getText().toString().equals("0.00")
                        || !edt_zip_cv.getText().toString().equals("")) {
                    if (chk_zip_display_and_add.isChecked())
                        preferenceManager.setcnv_zip_diaplay_and_add(true);
                    else
                        preferenceManager.setcnv_zip_diaplay_and_add(false);

                    if (chk_zip_display_only.isChecked())
                        preferenceManager.setcnv_zip_diaplay_only(true);
                    else
                        preferenceManager.setcnv_zip_diaplay_only(false);
                }
            }
            if (chk_alipay.isChecked()) {
                if (!edt_ali_cv.getText().toString().equals("0.0") ||
                        !edt_ali_cv.getText().toString().equals("0.00")
                        || !edt_ali_cv.getText().toString().equals("")) {
                    if (chk_ali_display_and_add.isChecked())
                        preferenceManager.setcnv_alipay_diaplay_and_add(true);
                    else
                        preferenceManager.setcnv_alipay_diaplay_and_add(false);

                    if (chk_ali_display_only.isChecked())
                        preferenceManager.setcnv_alipay_diaplay_only(true);
                    else
                        preferenceManager.setcnv_alipay_diaplay_only(false);

                }
            }


            if (chk_wechat.isChecked()) {
                if (!edt_wechat_cv.getText().toString().equals("0.0") ||
                        !edt_wechat_cv.getText().toString().equals("0.00")
                        || !edt_wechat_cv.getText().toString().equals("")) {
                    if (chk_wechat_display_and_add.isChecked())
                        preferenceManager.setcnv_wechat_display_and_add(true);
                    else
                        preferenceManager.setcnv_wechat_display_and_add(false);

                    if (chk_wechat_display_only.isChecked())
                        preferenceManager.setcnv_wechat_display_only(true);
                    else
                        preferenceManager.setcnv_wechat_display_only(false);

                }
            }


            String s = edt_ali_cv.getText().toString();
            if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                chk_ali_display_only.setChecked(false);
                chk_ali_display_only.setSelected(false);
                chk_ali_display_and_add.setChecked(false);
                chk_ali_display_and_add.setSelected(false);
                preferenceManager.setcnv_alipay_diaplay_only(false);
                preferenceManager.setcnv_alipay_diaplay_and_add(false);

            }

            String ss = edt_zip_cv.getText().toString();
            if (ss.equals("") || ss.equals("0.00") || ss.equals("0.0") || ss.equals("0")) {
                chk_zip_display_only.setChecked(false);
                chk_zip_display_only.setSelected(false);
                chk_zip_display_and_add.setChecked(false);
                chk_zip_display_and_add.setSelected(false);
                preferenceManager.setcnv_zip_diaplay_only(false);
                preferenceManager.setcnv_zip_diaplay_and_add(false);
            }

            s = edt_wechat_cv.getText().toString();
            if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                chk_wechat_display_only.setChecked(false);
                chk_wechat_display_only.setSelected(false);
                chk_wechat_display_and_add.setChecked(false);
                chk_wechat_display_and_add.setSelected(false);
                preferenceManager.setcnv_wechat_display_only(false);
                preferenceManager.setcnv_wechat_display_and_add(false);

            }

            s = edt_unionpay_card_cv.getText().toString();
            if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                chk_unionpay_card_display_only.setChecked(false);
                chk_unionpay_card_display_only.setSelected(false);
                chk_unionpay_card_display_and_add.setChecked(false);
                chk_unionpay_card_display_and_add.setSelected(false);
                preferenceManager.setcnv_uni_display_only(false);
                preferenceManager.setcnv_uni_display_and_add(false);

            }
            s = edt_poli_cv.getText().toString();
            if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                chk_poli_display_only.setChecked(false);
                chk_poli_display_only.setSelected(false);
                chk_poli_display_and_add.setChecked(false);
                chk_poli_display_and_add.setSelected(false);
                preferenceManager.setcnv_poli_display_only(false);
                preferenceManager.setcnv_poli_display_and_add(false);

            }

            s = edt_centrapay_cv.getText().toString();
            if (s.equals("") || s.equals("0.00") || s.equals("0.0") || s.equals("0")) {
                chk_centrapay_display_only.setChecked(false);
                chk_centrapay_display_only.setSelected(false);
                chk_centrapay_display_and_add.setChecked(false);
                chk_centrapay_display_and_add.setSelected(false);
                preferenceManager.setcnv_centrapay_display_only(false);
                preferenceManager.setcnv_centrapay_display_and_add(false);
            }

            if ((edt_ali_cv.getText().toString().equals("0.00") ||
                    edt_ali_cv.getText().toString().equals("0.0") ||
                    edt_ali_cv.getText().toString().equals("")) &&
                    (edt_zip_cv.getText().toString().equals("0.00") ||
                            edt_zip_cv.getText().toString().equals("0.0") ||
                            edt_zip_cv.getText().toString().equals("")) &&
                    (edt_wechat_cv.getText().toString().equals("0.00") ||
                            edt_wechat_cv.getText().toString().equals("0.0") ||
                            edt_wechat_cv.getText().toString().equals("")) &&
                    (edt_unionpay_card_cv.getText().toString().equals("0.00") ||
                            edt_unionpay_card_cv.getText().toString().equals("0.0") ||
                            edt_unionpay_card_cv.getText().toString().equals("")) &&
                    (edt_unionpay_qr_cv.getText().toString().equals("") &&
                            (edt_unionpay_qr_cv.getText().toString().equals("0.0") ||
                                    edt_unionpay_qr_cv.getText().toString().equals("0.00"))) &&
                    (edt_up_upi_qr_cv.getText().toString().equals("") &&
                            (edt_up_upi_qr_cv.getText().toString().equals("0.0") ||
                                    edt_up_upi_qr_cv.getText().toString().equals("0.00"))) &&
                    (edt_up_upi_qr_cv1.getText().toString().equals("") &&
                            (edt_up_upi_qr_cv1.getText().toString().equals("0.0") ||
                                    edt_up_upi_qr_cv1.getText().toString().equals("0.00"))) &&
                    (edt_up_upi_qr_amount.getText().toString().equals("") &&
                            (edt_up_upi_qr_amount.getText().toString().equals("0.0") ||
                                    edt_up_upi_qr_amount.getText().toString().equals("0.00"))) &&
                    (edt_uplan_cv.getText().toString().equals("0.00") ||
                            edt_uplan_cv.getText().toString().equals("0.0") ||
                            edt_uplan_cv.getText().toString().equals("")) &&
                    (edt_poli_cv.getText().toString().equals("0.00") ||
                            edt_poli_cv.getText().toString().equals("0.0") ||
                            edt_poli_cv.getText().toString().equals(""))&&
                    (edt_centrapay_cv.getText().toString().equals("0.00") ||
                            edt_centrapay_cv.getText().toString().equals("0.0") ||
                            edt_centrapay_cv.getText().toString().equals(""))
            ) {
                preferenceManager.setisConvenienceFeeSelected(false);
                preferenceManager.setcnv_alipay("0.00");
                preferenceManager.setcnv_zip("0.00");
                preferenceManager.setcnv_wechat("0.00");
                preferenceManager.setcnv_uplan("0.00");
                preferenceManager.setcnv_uni("0.00");
                preferenceManager.setcnv_uniqr("0.00");
                preferenceManager.setcnv_poli("0.00");
                preferenceManager.setcnv_centrapay("0.00");
                preferenceManager.setCnv_up_upiqr_mpmcloud_higher("0.00");
                preferenceManager.setcnv_up_upiqr_mpmcloud_lower("0.00");
                preferenceManager.set_cnv_unimerchantqrdisplayHigher("0.00");
                preferenceManager.set_cnv_unimerchantqrdisplayLower("0.00");
                preferenceManager.setCnv_up_upiqr_mpmcloud_amount("0.00");
            } else {
                if (!edt_ali_cv.getText().toString().equals("") &&
                        (!edt_ali_cv.getText().toString().equals("0.0") ||
                                !edt_ali_cv.getText().toString().equals("0.00"))) {
                    preferenceManager.setcnv_alipay(edt_ali_cv.getText().toString().replace(",", ""));
                } else {
                    preferenceManager.setcnv_alipay("0.00");
                }

                if (!edt_zip_cv.getText().toString().equals("") &&
                        (!edt_zip_cv.getText().toString().equals("0.0") ||
                                !edt_zip_cv.getText().toString().equals("0.00"))) {
                    preferenceManager.setcnv_zip(edt_zip_cv.getText().toString().replace(",", ""));
                } else {
                    preferenceManager.setcnv_zip("0.00");
                }

                if (!edt_wechat_cv.getText().toString().equals("") &&
                        (!edt_wechat_cv.getText().toString().equals("0.0") ||
                                !edt_wechat_cv.getText().toString().equals("0.00"))) {
                    preferenceManager.setcnv_wechat(edt_wechat_cv.getText().toString().replace(",", ""));
                } else {
                    preferenceManager.setcnv_wechat("0.00");
                }


                if (!edt_unionpay_card_cv.getText().toString().equals("") &&
                        (!edt_unionpay_card_cv.getText().toString().equals("0.0") || !edt_unionpay_card_cv.getText().toString().equals("0.00"))) {
                    preferenceManager.setcnv_uni(edt_unionpay_card_cv.getText().toString().replace(",", ""));
                } else {
                    preferenceManager.setcnv_uni("0.00");
                }

                if (!edt_poli_cv.getText().toString().equals("") &&
                        (!edt_poli_cv.getText().toString().equals("0.0") || !edt_poli_cv.getText().toString().equals("0.00"))) {
                    preferenceManager.setcnv_poli(edt_poli_cv.getText().toString().replace(",", ""));
                } else {
                    preferenceManager.setcnv_poli("0.00");
                }

                if (!edt_centrapay_cv.getText().toString().equals("") &&
                        (!edt_centrapay_cv.getText().toString().equals("0.0") || !edt_centrapay_cv.getText().toString().equals("0.00"))) {
                    preferenceManager.setcnv_centrapay(edt_centrapay_cv.getText().toString().replace(",", ""));
                } else {
                    preferenceManager.setcnv_centrapay("0.00");
                }

                if (!edt_unionpay_qr_cv.getText().toString().equals("") &&
                        (!edt_unionpay_qr_cv.getText().toString().equals("0.0") ||
                                !edt_unionpay_qr_cv.getText().toString().equals("0.00"))) {
                    preferenceManager.setcnv_uniqr(edt_unionpay_qr_cv.getText().toString().replace(",", ""));
                } else {
                    preferenceManager.setcnv_uniqr("0.00");
                }

                if (!edt_uplan_cv.getText().toString().equals("") &&
                        (!edt_uplan_cv.getText().toString().equals("0.0")
                                || !edt_uplan_cv.getText().toString().equals("0.00")) &&
                        (chk_uplan_display_and_add.isChecked() || chk_uplan_display_only.isChecked())
                ) {
                    preferenceManager.setcnv_uplan(edt_uplan_cv.getText().toString().replace(",", ""));
                } else {
                    preferenceManager.setcnv_uplan("0.00");
                }

            }


        } else {
            preferenceManager.setisConvenienceFeeSelected(false);
            if (!chk_ali_display_and_add.isChecked())
                preferenceManager.setcnv_alipay_diaplay_and_add(false);
            if (!chk_ali_display_only.isChecked())
                preferenceManager.setcnv_alipay_diaplay_only(false);

            if (!chk_zip_display_and_add.isChecked())
                preferenceManager.setcnv_zip_diaplay_and_add(false);
            if (!chk_zip_display_only.isChecked())
                preferenceManager.setcnv_zip_diaplay_only(false);

            if (!chk_wechat_display_and_add.isChecked())
                preferenceManager.setcnv_wechat_display_and_add(false);
            if (!chk_wechat_display_only.isChecked())
                preferenceManager.setcnv_wechat_display_only(false);

            if (!chk_unionpay_card_display_and_add.isChecked())
                preferenceManager.setcnv_uni_display_and_add(false);
            if (!chk_unionpay_card_display_only.isChecked())
                preferenceManager.setcnv_uni_display_only(false);

            if (!chk_centrapay_display_and_add.isChecked())
                preferenceManager.setcnv_centrapay_display_and_add(false);
            if (!chk_centrapay_display_only.isChecked())
                preferenceManager.setcnv_centrapay_display_only(false);


            if (!chk_poli_display_and_add.isChecked())
                preferenceManager.setcnv_poli_display_and_add(false);
            if (!chk_poli_display_only.isChecked())
                preferenceManager.setcnv_poli_display_only(false);

            if (!chk_unionpay_qr_display_and_add.isChecked())
                preferenceManager.setcnv_unionpayqr_display_and_add(false);
            if (!chk_unionpay_qr_display_only.isChecked())
                preferenceManager.setcnv_unionpayqr_display_only(false);


            if (!chk_up_upi_qr_display_and_add.isChecked())
                preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(false);
            if (!chk_upi_qr_display_only.isChecked())
                preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(false);


            if (!chk_uplan_display_and_add.isChecked())
                preferenceManager.setcnv_uplan_display_and_add(false);
            if (!chk_uplan_display_only.isChecked())
                preferenceManager.setcnv_uplan_display_only(false);

        }

        if (chk_alipay.isChecked()) {
            preferenceManager.setisAlipaySelected(true);
        } else {
            preferenceManager.setisAlipaySelected(false);
        }


        if (chk_zip.isChecked()) {
            preferenceManager.setisZipSelected(true);
        } else {
            preferenceManager.setisZipSelected(false);
        }


        if (chk_wechat.isChecked()) {
            preferenceManager.setisWechatSelected(true);
        } else {
            preferenceManager.setisWechatSelected(false);
        }
        if (chk_uplan_qr.isChecked()) {
            preferenceManager.setisUnipaySelected(true);
        } else {
            preferenceManager.setisUnipaySelected(false);
        }

        if (chk_unionpay_card.isChecked()) {
            preferenceManager.setisUnipaySelected(true);
        } else {
            preferenceManager.setisUnipaySelected(false);
            if (chk_uplan_qr.isChecked()) {
                preferenceManager.setisUnipaySelected(true);
            } else {
                preferenceManager.setisUnipaySelected(false);
            }
        }


        if (!preferenceManager.is_cnv_alipay_display_and_add() && !preferenceManager.is_cnv_alipay_display_only()) {
            edt_ali_cv.setText("0.00");
            preferenceManager.setcnv_alipay("0.00");
        }

        if (!preferenceManager.is_cnv_zip_display_and_add() && !preferenceManager.is_cnv_zip_display_only()) {
            edt_zip_cv.setText("0.00");
            preferenceManager.setcnv_zip("0.00");
        }

        if (!preferenceManager.is_cnv_wechat_display_and_add() && !preferenceManager.is_cnv_wechat_display_only()) {
            edt_wechat_cv.setText("0.00");
            preferenceManager.setcnv_wechat("0.00");
        }

        if (!preferenceManager.is_cnv_uni_display_and_add() && !preferenceManager.is_cnv_uni_display_only()) {
            edt_unionpay_card_cv.setText("0.00");
            preferenceManager.setcnv_uni("0.00");
        }
        if (!preferenceManager.is_cnv_poli_display_and_add() && !preferenceManager.is_cnv_poli_display_only()) {
            edt_poli_cv.setText("0.00");
            preferenceManager.setcnv_poli("0.00");
        }

        if (!preferenceManager.is_cnv_centrapay_display_and_add() && !preferenceManager.is_cnv_centrapay_display_only()) {
            edt_centrapay_cv.setText("0.00");
            preferenceManager.setcnv_centrapay("0.00");
        }

        if ((!edt_unionpay_qr_cv.getText().toString().equals("") &&
                (!edt_unionpay_qr_cv.getText().toString().equals("0.0") ||
                        !edt_unionpay_qr_cv.getText().toString().equals("0.00"))) &&
                !chk_unionpay_qr_display_and_add.isChecked() && !chk_unionpay_qr_display_only.isChecked()) {
            preferenceManager.setcnv_uniqr("0.00");
            edt_unionpay_qr_cv.setText("0.00");
        }

        callUpdateBranchDetails(funcPrepareDisplayChoicesJSONObject());
    }

    public void funcEdtLengthCalPC() {
        edt_centrapay_cv.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_poli_cv.setInputType(InputType.TYPE_CLASS_NUMBER);
        //ZIP_V
        edt_zip_cv.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_ali_cv.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_wechat_cv.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_unionpay_card_cv.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_unionpay_qr_cv.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_uplan_cv.setInputType(InputType.TYPE_CLASS_NUMBER);

        edt_centrapay_cv.setSelection(edt_centrapay_cv.getText().length());
        edt_poli_cv.setSelection(edt_poli_cv.getText().length());
        edt_ali_cv.setSelection(edt_ali_cv.getText().length());
        edt_zip_cv.setSelection(edt_zip_cv.getText().length());
        edt_unionpay_card_cv.setSelection(edt_unionpay_card_cv.getText().length());
        edt_wechat_cv.setSelection(edt_wechat_cv.getText().length());
        edt_unionpay_qr_cv.setSelection(edt_unionpay_qr_cv.getText().length());
        edt_uplan_cv.setSelection(edt_uplan_cv.getText().length());


        edt_centrapay_cv.setOnTouchListener((View v, MotionEvent event) -> {
            if (edt_centrapay_cv.getText().toString().equals("0.0")) {
                edt_centrapay_cv.setText("");
            }
            return false;
        });


        edt_poli_cv.setOnTouchListener((View v, MotionEvent event) -> {
            if (edt_poli_cv.getText().toString().equals("0.0")) {
                edt_poli_cv.setText("");
            }
            return false;
        });

        edt_ali_cv.setOnTouchListener((View v, MotionEvent event) -> {
            if (edt_ali_cv.getText().toString().equals("0.0")) {
                edt_ali_cv.setText("");
            }
            return false;
        });

        edt_zip_cv.setOnTouchListener((View v, MotionEvent event) -> {
            if (edt_zip_cv.getText().toString().equals("0.0")) {
                edt_zip_cv.setText("");
            }
            return false;
        });

        edt_unionpay_card_cv.setOnTouchListener((View v, MotionEvent event) -> {
            if (edt_unionpay_card_cv.getText().toString().equals("0.0")) {
                edt_unionpay_card_cv.setText("");
            }
            return false;
        });

        edt_unionpay_qr_cv.setOnTouchListener((View v, MotionEvent event) -> {
            if (edt_unionpay_qr_cv.getText().toString().equals("0.0")) {
                edt_unionpay_qr_cv.setText("");
            }
            return false;
        });

        edt_uplan_cv.setOnTouchListener((View v, MotionEvent event) -> {
            if (edt_uplan_cv.getText().toString().equals("0.0")) {
                edt_uplan_cv.setText("");
            }
            return false;
        });
    }

    public void funcCVTextChangeListener() {

        edt_up_upi_qr_cv.addTextChangedListener(new EditTextListenerClass(edt_up_upi_qr_cv));
        edt_up_upi_qr_cv1.addTextChangedListener(new EditTextListenerClass(edt_up_upi_qr_cv1));
        edt_ali_cv.addTextChangedListener(new EditTextListenerClass(edt_ali_cv));
        //ZIP_V
        edt_zip_cv.addTextChangedListener(new EditTextListenerClass(edt_zip_cv));
        edt_poli_cv.addTextChangedListener(new EditTextListenerClass(edt_poli_cv));
        edt_centrapay_cv.addTextChangedListener(new EditTextListenerClass(edt_centrapay_cv));
        edt_unionpay_card_cv.addTextChangedListener(new EditTextListenerClass(edt_unionpay_card_cv));
        edt_unionpay_qr_cv.addTextChangedListener(new EditTextListenerClass(edt_unionpay_qr_cv));
        edt_uplan_cv.addTextChangedListener(new EditTextListenerClass(edt_uplan_cv));


    }


    private class EditTextListenerClass implements TextWatcher {
        private EditText editText;

        private EditTextListenerClass(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
           /* Double upilowerFee = edt_up_upi_qr_cv.getText().toString().isEmpty() ? 0.00 :
                    Double.parseDouble(edt_up_upi_qr_cv.getText().toString());
            Double upihigherFee = edt_up_upi_qr_cv1.getText().toString().isEmpty() ? 0.00 :
                    Double.parseDouble(edt_up_upi_qr_cv1.getText().toString());*/
            switch (editText.getId()) {
                case R.id.edt_centrapay_cv:
                case R.id.edt_poli_cv:
                case R.id.edt_ali_cv:
                case R.id.edt_unionpay_card_cv:
                case R.id.edt_unionpay_qr_cv:
                case R.id.edt_uplan_cv:
                    if (s.equals("")) {
                        return;
                    } else {
                    }

                    break;

                case R.id.edt_up_upi_qr_cv:
//                    if(upilowerFee>=upihigherFee)
//                    {
//                        upi_note.setText("Entered fee percent should be less than the greater fee percent");
//                    }
//                    else
//                    {
//                        upi_note.setText("");
//                    }
                    break;

                case R.id.edt_up_upi_qr_cv1:
//                    if(upihigherFee<=upilowerFee)
//                    {
//                        upi_note.setText("Entered fee percent should be greater than the lesser fee percent");
//                    }
//                    else
//                    {
//                        upi_note.setText("");
//                    }
                    break;

            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


    public JSONObject funcPrepareDisplayChoicesJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("accessId", preferenceManager.getuniqueId());
            jsonObject.put("AlipaySelected", preferenceManager.isAlipaySelected());
            jsonObject.put("AlipayValue", preferenceManager.getcnv_alipay());
            jsonObject.put("CnvAlipayDisplayAndAdd", preferenceManager.is_cnv_alipay_display_and_add());
            jsonObject.put("CnvAlipayDisplayOnly", preferenceManager.is_cnv_alipay_display_only());

            jsonObject.put("ZipSelected", preferenceManager.isZipSelected());
            jsonObject.put("ZipValue", preferenceManager.getcnv_zip());
            jsonObject.put("CnvZipDisplayAndAdd", preferenceManager.is_cnv_zip_display_and_add());
            jsonObject.put("CnvZipDisplayOnly", preferenceManager.is_cnv_zip_display_only());

            jsonObject.put("WeChatSelected", preferenceManager.isWechatSelected());
            jsonObject.put("WeChatValue", preferenceManager.getcnv_wechat());
            jsonObject.put("CnvWeChatDisplayAndAdd", preferenceManager.is_cnv_wechat_display_and_add());
            jsonObject.put("CnvWeChatDisplayOnly", preferenceManager.is_cnv_wechat_display_only());

            jsonObject.put("AlipayScanQR", preferenceManager.isAlipayScan());
            jsonObject.put("WeChatScanQR", preferenceManager.isWeChatScan());
            jsonObject.put("ZipScanQR", preferenceManager.isZipScan());

            jsonObject.put("UnionPay", preferenceManager.isUnionPaySelected());
            jsonObject.put("Uplan", preferenceManager.isUplanSelected());
            jsonObject.put("UnionPayQR", preferenceManager.isUnionPayQrSelected());
            jsonObject.put("isUnionPayQrCodeDisplaySelected", preferenceManager.isUnionPayQrCodeDisplaySelected());
            jsonObject.put("UnionPayQrValue", preferenceManager.getcnv_uniqr());
            jsonObject.put("cnv_unimerchantqrdisplay", preferenceManager.get_cnv_unimerchantqrdisplayLower());
            jsonObject.put("UplanValue", preferenceManager.getcnv_uplan());
            jsonObject.put("CnvUnionpayDisplayAndAdd", preferenceManager.is_cnv_uni_display_and_add());
            jsonObject.put("CnvUnionpayDisplayOnly", preferenceManager.is_cnv_uni_display_only());
            jsonObject.put("MerchantId", preferenceManager.getMerchantId());
            jsonObject.put("ConfigId", preferenceManager.getConfigId());
            jsonObject.put("AlipayWeChatPay", preferenceManager.isaggregated_singleqr());
            jsonObject.put("AlipayWeChatScanQR", preferenceManager.isAlipayWechatQrSelected());
            jsonObject.put("PrintReceiptautomatically", preferenceManager.getisPrint());
            jsonObject.put("ShowReference", preferenceManager.getshowReference());
            jsonObject.put("ShowPrintQR", preferenceManager.isQR());
            jsonObject.put("DisplayStaticQR", preferenceManager.isStaticQR());
            jsonObject.put("isDisplayLoyaltyApps", preferenceManager.isDisplayLoyaltyApps());
            jsonObject.put("isExternalInputDevice", preferenceManager.isExternalScan());
            jsonObject.put("isDragDrop", preferenceManager.isDragDrop());
            jsonObject.put("ShowMembershipManual", preferenceManager.isMembershipManual());
            jsonObject.put("ShowMembershipHome", preferenceManager.isMembershipHome());
            jsonObject.put("Membership/Loyality", preferenceManager.isLoyality());
            jsonObject.put("Home", preferenceManager.isHome());
            jsonObject.put("ManualEntry", preferenceManager.isManual());
            jsonObject.put("Back", preferenceManager.isBack());
            jsonObject.put("Front", preferenceManager.isFront());
            jsonObject.put("ConvenienceFee", preferenceManager.isConvenienceFeeSelected());
            jsonObject.put("AlipayWechatvalue", preferenceManager.getcnv_alipay());
            jsonObject.put("UnionPayvalue", preferenceManager.getcnv_uni());
            jsonObject.put("UnionPayQRValue", preferenceManager.getcnv_uniqr());
            jsonObject.put("EnableBranchName", preferenceManager.getBranchName());
            jsonObject.put("EnableBranchAddress", preferenceManager.getBranchAddress());
            jsonObject.put("EnableBranchEmail", preferenceManager.getBranchEmail());
            jsonObject.put("EnableBranchContactNo", preferenceManager.getBranchPhoneNo());
            jsonObject.put("EnableBranchGSTNo", preferenceManager.getGSTNo());
            jsonObject.put("TimeZoneId", preferenceManager.getTimeZoneId());
            jsonObject.put("TimeZone", preferenceManager.getTimeZone());
            jsonObject.put("isTimeZoneChecked", preferenceManager.isTimeZoneChecked());
            jsonObject.put("isTerminalIdentifier", preferenceManager.isTerminalIdentifier());
            jsonObject.put("isPOSIdentifier", preferenceManager.isPOSIdentifier());
            jsonObject.put("isLaneIdentifier", preferenceManager.isLaneIdentifier());
            jsonObject.put("LaneIdentifier", preferenceManager.getLaneIdentifier());
            jsonObject.put("TerminalIdentifier", preferenceManager.getTerminalIdentifier());
            jsonObject.put("POSIdentifier", preferenceManager.getPOSIdentifier());
            jsonObject.put("isUpdated", true);

            jsonObject.put("CnvUPIQrMPMCloudDAADD", preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add());
            jsonObject.put("CnvUPIQrMPMCloudDOnly", preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only());
            jsonObject.put("CnvUPIQrMPMCloudValue", preferenceManager.getcnv_up_upiqr_mpmcloud_lower());
            jsonObject.put("CnvUPIQrMPMCloudValueHigher", preferenceManager.getCnv_up_upiqr_mpmcloud_higher());
            jsonObject.put("CnvUPIQRMPMCloudAmount", preferenceManager.getCnv_up_upiqr_mpmcloud_amount());
            jsonObject.put("cnv_unimerchantqrdisplay_higher", preferenceManager.get_cnv_unimerchantqrdisplayHigher());
            jsonObject.put("isMerchantDPARDisplay", preferenceManager.isMerchantDPARDisplay());

            jsonObject.put("PoliSelected", preferenceManager.isPoliSelected());
            jsonObject.put("PoliFeeValue", preferenceManager.getcnv_poli());
            jsonObject.put("CnvPoliDisplayAndAdd", preferenceManager.is_cnv_poli_display_and_add());
            jsonObject.put("CnvPoliDisplayOnly", preferenceManager.is_cnv_poli_display_only());

            jsonObject.put("CentrapaySelected", preferenceManager.isCentrapayMerchantQRDisplaySelected());
            jsonObject.put("CentrapayFeeValue", preferenceManager.getcnv_centrapay());
            jsonObject.put("CnvCentrapayDisplayAndAdd", preferenceManager.is_cnv_centrapay_display_and_add());
            jsonObject.put("CnvCentrapayDisplayOnly", preferenceManager.is_cnv_centrapay_display_only());

            ArrayList tipList=preferenceManager.getTipPercentage("Tip");
            jsonObject.put("DefaultTip1", tipList.get(0));
            jsonObject.put("DefaultTip2", tipList.get(1));
            jsonObject.put("DefaultTip3", tipList.get(2));
            jsonObject.put("DefaultTip4", tipList.get(3));
            jsonObject.put("DefaultTip5", tipList.get(4));
            jsonObject.put("SwitchOnTip", preferenceManager.isSwitchTip());
            jsonObject.put("DefaultTip1IsEnabled", preferenceManager.isTipDefault1());
            jsonObject.put("DefaultTip2IsEnabled", preferenceManager.isTipDefault2());
            jsonObject.put("DefaultTip3IsEnabled", preferenceManager.isTipDefault3());
            jsonObject.put("DefaultTip4IsEnabled", preferenceManager.isTipDefault4());
            jsonObject.put("DefaultTip5IsEnabled", preferenceManager.isTipDefault5());
            jsonObject.put("DefaultTip5IsEnabled", preferenceManager.isTipDefault5());
            jsonObject.put("CustomTip", preferenceManager.isTipDefaultCustom());
            jsonObject.put("PaymentModePosition", preferenceManager.getString("DATA"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public String encryption(String strNormalText) throws Exception {
        String seedValue = "YourSecKey";
        String normalTextEnc = "";
        try {
            normalTextEnc = AESHelper.encrypt(seedValue, strNormalText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toHex(normalTextEnc);
    }

    public String decryption(String strEncryptedText) throws Exception {
        String seedValue = "YourSecKey";
        String strDecryptedText = hextoString(strEncryptedText);
        try {
            strDecryptedText = AESHelper.decrypt(seedValue, strDecryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                if (ManualEntry.isLoyaltyFrontQrSelected || ManualEntry.isLoyaltyQrSelected) {
                    Toast.makeText(this, "Closed", Toast.LENGTH_SHORT).show();

                } else
                {
                    AppConstants.xmppamountforscan = "";
                MyPOSMateApplication.isOpen = false;
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                preferenceManager.settriggerReferenceId("");
                callSetupFragment(SCREENS.MANUALENTRY, null);

                ManualEntry.isUpayselected = false;
            }
            } else {

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
                            sendBroadcast(ia);

                        }
                    }, 400);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DashboardActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                            String identityCode = data.getStringExtra("SCAN_RESULT");
                            Intent i = new Intent();
                            if (ManualEntry.isUpayselected) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedCode");
                            } else if (ManualEntry.isUnionPayQrSelected) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedCodeUnionPayQr");
                            } else if (RefundFragmentUnionPay.isUnionQrSelected) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedCodeUnionPayRefundQr");
                                RefundFragmentUnionPay.isUnionQrSelected=false;
                            }else if (ManualEntry.isLoyaltyQrSelected) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedBackLoyaltyQr");
                                ManualEntry.isLoyaltyQrSelected=false;
                            }else if (ManualEntry.isLoyaltyFrontQrSelected) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedFrontLoyaltyQr");
                                ManualEntry.isLoyaltyFrontQrSelected=false;
                            }
                            else if (PosMateConnectioFrag.isLoyaltyQrSelectedPos) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedPosBack");
                                PosMateConnectioFrag.isLoyaltyQrSelectedPos=false;
                            }else if (PosMateConnectioFrag.isLoyaltyFrontQrSelectedPos) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedPosFront");
                                PosMateConnectioFrag.isLoyaltyFrontQrSelectedPos=false;
                            }else if (ManualEntry.isZipQrSelected) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ZIPCODE");
                                ManualEntry.isZipQrSelected=false;
                            }else {
                                i.setAction("ScannedCode1");
                                AppConstants.isScannedCode1 = true;
                            }

                            i.putExtra("identityCode", identityCode);
                            Log.v("AUTHCODE","Authcodeac "+i.getAction());
                            sendBroadcast(i);
                        }
                    }, 800);

                } else {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DashboardActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                            String identityCode = data.getStringExtra("SCAN_RESULT");
                            Intent i = new Intent();
                            if (ManualEntry.isUpayselected) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedCode");
                            } else if (ManualEntry.isUnionPayQrSelected) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedCodeUnionPayQr");
                            } else if (RefundFragment.isAlipayQrSelected) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedCodeAlipayRefundQr");
                                RefundFragment.isAlipayQrSelected=false;
                            }else if (RefundFragmentUnionPay.isUnionQrSelected) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedCodeUnionPayRefundQr");
                                RefundFragmentUnionPay.isUnionQrSelected=false;
                            }else if (ManualEntry.isLoyaltyQrSelected) {

                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedBackLoyaltyQr");
                                ManualEntry.isLoyaltyQrSelected=false;
                            }else if (ManualEntry.isLoyaltyFrontQrSelected) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedFrontLoyaltyQr");
                                ManualEntry.isLoyaltyFrontQrSelected=false;
                            }else if (PosMateConnectioFrag.isLoyaltyQrSelectedPos) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedPosBack");
                                PosMateConnectioFrag.isLoyaltyQrSelectedPos=false;
                            }else if (PosMateConnectioFrag.isLoyaltyFrontQrSelectedPos) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ScannedPosFront");
                                PosMateConnectioFrag.isLoyaltyFrontQrSelectedPos=false;
                            }else if (ManualEntry.isZipQrSelected) {
                                AppConstants.isScannedCode1 = false;
                                i.setAction("ZIPCODE");
                                ManualEntry.isZipQrSelected=false;
                            }else {
                                i.setAction("ScannedCode1");
                                AppConstants.isScannedCode1 = true;
                            }
                            Log.v("AUTHCODE","Authcodeac2 "+i.getAction());
                            i.putExtra("identityCode", identityCode);
                            sendBroadcast(i);
                        }
                    }, 500);

                }


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void callDeleteTerminal() {
        openProgressDialog();
        try {
            hashMapKeys.clear();
            hashMapKeys.put("terminalId", encryption(preferenceManager.getterminalId()));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, DashboardActivity.this));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
            new OkHttpHandler(DashboardActivity.this, this, hashMap, "DeleteTerminal").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.DELETE_TERMINAL_CONFIG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isEndOfProcedure = false;

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {

        if (result.equals("")) {
            if (progress.isShowing())
                progress.dismiss();
            Toast.makeText(DashboardActivity.this, "No data from server.", Toast.LENGTH_LONG).show();
            return;
        }
        if (progress.isShowing())
            progress.dismiss();


        JSONObject jsonObject = new JSONObject(result);
        Log.v("MYRESULT",result+TAG);
        switch (TAG) {
            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferenceManager.setauthToken(jsonObject.optString("access_token"));
                }

                if (isNetConnectionOn) {
                    isNetConnectionOn = false;
                    Log.v("Dashboard", "Dashboard Called connection");
//                    if(mStompClient==null)
//                    {
                    ((MyPOSMateApplication) this.getApplicationContext()).initiateStompConnection(preferenceManager.getauthToken());
//                    }

                }

                if (isTriggerReceived) {

                    callUpdateRequestAPI(request_id, false);
                }
                if (isEndOfProcedure) {
                    isEndOfProcedure = false;
                    callUpdateRequestAPI(request_id, true);
                }
                if (isLaunch) {
                    isLaunch = false;
                    if (isTerminalInfoDeleted) {
                        isTerminalInfoDeleted = false;
                        if (isDisplayChoicesDataSaved) {
                            isDisplayChoicesDataSaved = false;
                            callUpdateBranchDetails(funcPrepareDisplayChoicesJSONObject());
                            Log.v("MYRESULT",result);
                        } else {
                            callUpdateBranchDetailsNew();
                            Log.v("MYRESULT","newupdate1 "+result);
                        }
                    } else {
                        Log.v("MYRESULT","elase delted "+result);
                        if (isDisplayChoicesDataSaved) {
                            isDisplayChoicesDataSaved = false;
                            callUpdateBranchDetails(funcPrepareDisplayChoicesJSONObject());
                            Log.v("MYRESULT","newupdate3 "+result);
                        } else
                        {
                            if(progress!=null)
                            {
                                Log.v("MYRESULT","newupdate8 "+result);
                                if (progress.isShowing())
                                {
                                    Log.v("MYRESULT","newupdate9 "+result);
                                    progress.dismiss();
                                }
                            }
                            callUpdateBranchDetailsNew();
                        Log.v("MYRESULT","newupdate2 "+result);
                    }
//                        callDeleteTerminal();
                    }


                }

                break;

            case "DeleteTerminal":
                //    if (jsonObject.optBoolean("success")) {
                isTerminalInfoDeleted = true;
                callAuthToken();
                //    }
                break;

            case "updateRequest":
                if (jsonObject.optBoolean("status")) {
                    //callAuthToken();
                    if (isTriggerReceived) {
                        isTriggerReceived = false;
                        final Handler handler_print = new Handler();
                        handler_print.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    print(triggerjsonObject.optString("body"));
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 500);

                    }


                }

                break;

            case "GetBranchDetailsNew":
                _NewUser(jsonObject);
                callAuthToken();
                if (preferenceManager.isManual())
                    callSetupFragment(SCREENS.MANUALENTRY, null);
                else
                    callSetupFragment(SCREENS.POSMATECONNECTION, null);
                break;

            case "UpdateBranchDetailsNew":
                Log.v("MYRESULT","New "+result);
                callAuthToken();

                break;

            case "UpdateBranchDetails":
                callAuthToken();
                if (jsonObject.has("otherData")) {
                    JSONObject jsonObject1 = new JSONObject(decryption(jsonObject.optString("otherData")));

                    if (jsonObject1.has("ConfigId"))
                        preferenceManager.setConfigId(jsonObject1.optString("ConfigId"));
                    if (jsonObject1.has("MerchantId"))
                        preferenceManager.setMerchantId(jsonObject1.optString("MerchantId"));

                    preferenceManager.setisCentrapayMerchantQRDisplaySelected(jsonObject1.optBoolean("CentrapaySelected"));
                    preferenceManager.setcnv_centrapay_display_and_add(jsonObject1.optBoolean("CnvCentrapayDisplayAndAdd"));
                    preferenceManager.setcnv_centrapay_display_only(jsonObject1.optBoolean("CnvCentrapayDisplayOnly"));
                    preferenceManager.setcnv_centrapay(jsonObject1.optString("CentrapayFeeValue"));

                    ArrayList tipList=new ArrayList();
                    tipList.add(jsonObject1.optString("DefaultTip1"));
                    tipList.add(jsonObject1.optString("DefaultTip2"));
                    tipList.add(jsonObject1.optString("DefaultTip3"));
                    tipList.add(jsonObject1.optString("DefaultTip4"));
                    tipList.add(jsonObject1.optString("DefaultTip5"));
                    preferenceManager.setTipPercentage("Tip",tipList);

                    preferenceManager.setisSwitchTip(jsonObject1.optBoolean("SwitchOnTip"));

                    preferenceManager.setisTipDefault1(jsonObject1.optBoolean("DefaultTip1IsEnabled"));
                    preferenceManager.setisTipDefault2(jsonObject1.optBoolean("DefaultTip2IsEnabled"));
                    preferenceManager.setisTipDefault3(jsonObject1.optBoolean("DefaultTip3IsEnabled"));
                    preferenceManager.setisTipDefault4(jsonObject1.optBoolean("DefaultTip4IsEnabled"));
                    preferenceManager.setisTipDefault5(jsonObject1.optBoolean("DefaultTip5IsEnabled"));
                    preferenceManager.setisTipDefaultCustom(jsonObject1.optBoolean("CustomTip"));
                    preferenceManager.putString("DATA",jsonObject1.optString("PaymentModePosition"));

                    preferenceManager.setisPoliSelected(jsonObject1.optBoolean("PoliSelected"));
                    preferenceManager.setcnv_poli_display_and_add(jsonObject1.optBoolean("CnvPoliDisplayAndAdd"));
                    preferenceManager.setcnv_poli_display_only(jsonObject1.optBoolean("CnvPoliDisplayOnly"));
                    preferenceManager.setcnv_poli(jsonObject1.optString("PoliFeeValue"));

                    preferenceManager.setcnv_alipay_diaplay_and_add(jsonObject1.optBoolean("CnvAlipayDisplayAndAdd"));
                    preferenceManager.setcnv_alipay_diaplay_only(jsonObject1.optBoolean("CnvAlipayDisplayOnly"));
                    preferenceManager.setcnv_wechat_display_and_add(jsonObject1.optBoolean("CnvWeChatDisplayAndAdd"));
                    preferenceManager.setcnv_wechat_display_only(jsonObject1.optBoolean("CnvWeChatDisplayOnly"));
                    preferenceManager.setisAlipaySelected(jsonObject1.optBoolean("AlipaySelected"));
                    preferenceManager.setisWechatSelected(jsonObject1.optBoolean("WeChatSelected"));
                    preferenceManager.setcnv_wechat(jsonObject1.optString("WeChatValue"));

                    preferenceManager.setisWeChatScan(jsonObject1.optBoolean("WeChatScanQR"));
                    preferenceManager.setisAlipayScan(jsonObject1.optBoolean("AlipayScanQR"));

                    preferenceManager.setisUnionPaySelected(jsonObject1.optBoolean("UnionPay"));
                    preferenceManager.setUnionPayQrSelected(jsonObject1.optBoolean("UnionPayQR"));
                    preferenceManager.setisUnionPayQrCodeDisplaySelected(jsonObject1.optBoolean("isUnionPayQrCodeDisplaySelected"));
                    preferenceManager.setcnv_uniqr(jsonObject1.optString("UnionPayQrValue"));
                    preferenceManager.set_cnv_unimerchantqrdisplayLower(jsonObject1.optString("cnv_unimerchantqrdisplay"));
                    preferenceManager.setcnv_uplan(jsonObject1.optString("UplanValue"));
                    preferenceManager.setcnv_uni_display_and_add(jsonObject1.optBoolean("CnvUnionpayDisplayAndAdd"));
                    preferenceManager.setcnv_uni_display_only(jsonObject1.optBoolean("CnvUnionpayDisplayOnly"));
                    preferenceManager.setisUplanSelected(jsonObject1.optBoolean("Uplan"));
                    preferenceManager.setaggregated_singleqr(jsonObject1.optBoolean("AlipayWeChatPay"));
                    preferenceManager.setAlipayWechatQrSelected(jsonObject1.optBoolean("AlipayWeChatScanQR"));
                    preferenceManager.setisPrint(jsonObject1.optString("PrintReceiptautomatically"));
                    preferenceManager.setshowReference(jsonObject1.optString("ShowReference"));
                    preferenceManager.setisQR(jsonObject1.optBoolean("ShowPrintQR"));
                    preferenceManager.setisStaticQR(jsonObject1.optBoolean("DisplayStaticQR"));
                    preferenceManager.setisDisplayLoyaltyApps(jsonObject1.optBoolean("isDisplayLoyaltyApps"));
                    preferenceManager.setisExternalScan(jsonObject1.optBoolean("isExternalInputDevice"));
                    preferenceManager.setDragDrop(jsonObject1.optBoolean("isDragDrop"));
                    preferenceManager.setisMembershipManual(jsonObject1.optBoolean("ShowMembershipManual"));
                    preferenceManager.setisMembershipHome(jsonObject1.optBoolean("ShowMembershipHome"));
                    preferenceManager.setisLoyality(jsonObject1.optBoolean("Membership/Loyality"));
                    preferenceManager.setIsHome(jsonObject1.optBoolean("Home"));
                    preferenceManager.setIsManual(jsonObject1.optBoolean("ManualEntry"));
                    preferenceManager.setIsBack(jsonObject1.optBoolean("Back"));
                    preferenceManager.setIsFront(jsonObject1.optBoolean("Front"));
                    preferenceManager.setisConvenienceFeeSelected(jsonObject1.optBoolean("ConvenienceFee"));
                    preferenceManager.setcnv_alipay(jsonObject1.optString("AlipayWechatvalue"));
                    preferenceManager.setcnv_uni(jsonObject1.optString("UnionPayvalue"));
                    preferenceManager.setcnv_uniqr(jsonObject1.optString("UnionPayQRValue"));
                    preferenceManager.set_cnv_unimerchantqrdisplayLower(jsonObject1.optString("cnv_unimerchantqrdisplay"));
                    preferenceManager.set_cnv_unimerchantqrdisplayHigher(jsonObject1.optString("cnv_unimerchantqrdisplay_higher"));
                    preferenceManager.setcnv_uplan(jsonObject1.optString("UplanValue"));
                    preferenceManager.setBranchName(jsonObject1.optString("EnableBranchName"));
                    preferenceManager.setBranchAddress(jsonObject1.optString("EnableBranchAddress"));
                    preferenceManager.setBranchEmail(jsonObject1.optString("EnableBranchEmail"));
                    preferenceManager.setBranchPhoneNo(jsonObject1.optString("EnableBranchContactNo"));
                    preferenceManager.setGSTNo(jsonObject1.optString("EnableBranchGSTNo"));
                    preferenceManager.setTimeZoneId(jsonObject1.optString("TimeZoneId"));
                    preferenceManager.setTimeZone(jsonObject1.optString("TimeZone"));
                    preferenceManager.setisTimeZoneChecked(jsonObject1.optBoolean("isTimeZoneChecked"));
                    preferenceManager.setTerminalIdentifier(jsonObject1.optString("TerminalIdentifier"));
                    preferenceManager.setPOSIdentifier(jsonObject1.optString("POSIdentifier"));
                    preferenceManager.setLaneIdentifier(jsonObject1.optString("LaneIdentifier"));
                    preferenceManager.setisLaneIdentifier(jsonObject1.optBoolean("isLaneIdentifier"));
                    preferenceManager.setisPOSIdentifier(jsonObject1.optBoolean("isPOSIdentifier"));
                    preferenceManager.setisTerminalIdentifier(jsonObject1.optBoolean("isTerminalIdentifier"));

                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(jsonObject1.optBoolean("CnvUPIQrMPMCloudDAADD"));
                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(jsonObject1.optBoolean("CnvUPIQrMPMCloudDOnly"));
                    preferenceManager.setcnv_up_upiqr_mpmcloud_lower(jsonObject1.optString("CnvUPIQrMPMCloudValue"));
                    preferenceManager.setCnv_up_upiqr_mpmcloud_higher(jsonObject1.optString("CnvUPIQrMPMCloudValueHigher"));
                    preferenceManager.setCnv_up_upiqr_mpmcloud_amount(jsonObject1.optString("CnvUPIQRMPMCloudAmount"));
                    preferenceManager.set_cnv_unimerchantqrdisplayHigher(jsonObject1.optString("cnv_unimerchantqrdisplay_higher"));
                    preferenceManager.setisMerchantDPARDisplay(jsonObject1.optBoolean("isMerchantDPARDisplay"));


                    if (preferenceManager.isManual()) {
                        callSetupFragment(SCREENS.MANUALENTRY, null);
                    } else {
                        callSetupFragment(SCREENS.POSMATECONNECTION, null);
                    }


                }

                break;


        }
    }


    private void print(String body) throws RemoteException {

        try {
            final List<PrintDataObject> list = new ArrayList<PrintDataObject>();
            int fontSize = 24;
            byte[] data = Base64.decode(body, Base64.NO_WRAP);
            try {
                String text = new String(data, "UTF-8");
                list.add(new PrintDataObject(text,
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            printDev.spitPaper(50);
            int ret = printDev.printTextEffect(list);
            printDev.spitPaper(50);
            printDev.spitPaper(50);
            Log.e("test", "返回码：" + ret);
            getMessStr(ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public AidlDeviceManager manager = null;
    Intent intentService;

    public void bindService() {
        intentService = new Intent();
        intentService.setPackage("com.centerm.smartposservice");
        intentService.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        bindService(intentService, conn, Context.BIND_AUTO_CREATE);
    }


    /**
     * 服务连接桥
     */
    public ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            manager = null;
            try {
                LogUtil.print(getResources().getString(R.string.bind_service_fail));
                LogUtil.print("manager = " + manager);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            manager = AidlDeviceManager.Stub.asInterface(service);

            if (null != manager) {
                try {
                    LogUtil.print(getResources().getString(R.string.bind_service_success));
                    LogUtil.print("manager = " + manager);
                    onDeviceConnected(manager);
                } catch (Exception e) {

                }

            }
        }


    };


    private AidlPrinter printDev = null;
    // 打印机回调对象
    private AidlPrinterStateChangeListener callback = new PrinterCallback(); // 打印机回调
    private EditText qrCode, barCode;
    private String qrStr;
    private String barStr;
    private Spinner spinner;
    private int typeIndex;
    private String codeStr;

    /**
     * 打印机回调类
     */
    private class PrinterCallback extends AidlPrinterStateChangeListener.Stub {

        @Override
        public void onPrintError(int arg0) throws RemoteException {
            // showMessage("打印机异常" + arg0, Color.RED);
            Toast.makeText(DashboardActivity.this, arg0, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrintFinish() throws RemoteException {
            // Toast.makeText(getActivity(), getString(R.string.printer_finish), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrintOutOfPaper() throws RemoteException {
            Looper.prepare();
            Toast.makeText(DashboardActivity.this, getString(R.string.printer_need_paper), Toast.LENGTH_SHORT).show();
        }
    }

    public void onDeviceConnected(AidlDeviceManager deviceManager) {
        try {
            printDev = AidlPrinter.Stub.asInterface(deviceManager
                    .getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PRINTERDEV));
            printDev.setPrinterGray(0x02);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void getMessStr(int ret) {
        switch (ret) {
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_BUSY:
                Toast.makeText(DashboardActivity.this, getString(R.string.printer_device_busy), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_OK:
                isEndOfProcedure = true;
                callAuthToken();
                Toast.makeText(DashboardActivity.this, getString(R.string.printer_success), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_OUT_OF_PAPER:
                Toast.makeText(DashboardActivity.this, getString(R.string.printer_lack_paper), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_HEAD_OVER_HEIGH:
                Toast.makeText(DashboardActivity.this, getString(R.string.printer_over_heigh), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_OVER_HEATER:
                Toast.makeText(DashboardActivity.this, getString(R.string.printer_over_heat), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_LOW_POWER:
                Toast.makeText(DashboardActivity.this, getString(R.string.printer_low_power), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(DashboardActivity.this, getString(R.string.printer_other_exception_code) + ret, Toast.LENGTH_SHORT).show();
                break;
        }

    }

    TreeMap<String, String> hashMapKeys;

    public void callUpdateRequestAPI(String request_id, boolean executed) {
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

            new OkHttpHandler(this, this, null, "updateRequest")
                    .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.UPDATE_REQUEST +
                            MD5Class.generateSignatureString(hashMapKeys, this)
                            + "&access_token=" + preferenceManager.getauthToken());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disableBadge() {
        preferenceManager.setOrderBadgeCount(0);
        tv_order_badge.setVisibility(View.GONE);
    }

    boolean isNetConnectionOn = false;
    JSONObject triggerjsonObject;
    public static boolean isTriggerReceived = false;
    public static String request_id = "";

    public class OpenFragmentsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String ac = intent.getAction();
            switch (ac) {

                case "ThirdParty":
                    callSetupFragment(SCREENS.THIRD_PARTY, "xmpp");
                    final Handler handler11 = new Handler();
                    handler11.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent in = new Intent();
                            in.setAction("TriggerReceiver");
                            preferenceManager.setamountdata(intent.getStringExtra("data"));
                            in.putExtra("data", intent.getStringExtra("data"));
                            sendBroadcast(in);
                        }
                    }, 500);
                    break;

                case "PrintTrigger":
                    try {
                        triggerjsonObject = new JSONObject(intent.getStringExtra("data"));
                        request_id = triggerjsonObject.optString("request_id");
                        isTriggerReceived = true;
                        callAuthToken();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case "OrderDetails":
                    try {
                        JSONObject jsonObject = new JSONObject(intent.getStringExtra("data"));
                        if (isOpen) {

                            mySoundPlayer.playSound(1);
                            showOrderReceivedToast("Order Arrived: " + jsonObject.optString("order_id"));
                            tv_order_badge.setText(preferenceManager.getOrderBadgeCount() + "");
                            tv_order_badge.setVisibility(View.VISIBLE);
                            mySoundPlayer.stopCurrentSound(1);
                        } else {
                            callAuthToken();
                            Intent orderIntent = new Intent(DashboardActivity.this, OrderDetailsActivity.class);
                            orderIntent.putExtra("hub_id", jsonObject.optString("hub_id"));
                            orderIntent.putExtra("myPOSMateOrderID", jsonObject.optString("order_id"));
                            startActivity(orderIntent);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case "ManualEntry":
                    callSetupFragment(SCREENS.MANUALENTRY, "xmpp");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent();
                            i.setAction("AmountTrigger");
                            preferenceManager.setamountdata(intent.getStringExtra("data"));
                            i.putExtra("data", intent.getStringExtra("data"));
                            sendBroadcast(i);
                        }
                    }, 500);


                    break;

                case "PaymentProcessing":
                    final Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent();
                            i.setAction("PAYMENT_DETAILS");
                            i.putExtra("data", intent.getStringExtra("data"));
                            sendBroadcast(i);
                        }
                    }, 500);

                    break;


                case "RECONNECT":
                    if (mStompClient == null) {
                        preferenceManager.setIsAuthenticated(false);
                        preferenceManager.setIsConnected(false);
                        connectStomp();
                    } else {
                        if (((MyPOSMateApplication) getApplicationContext()).mStompClient.isConnected()) {
                            if (preferenceManager.isManual()) {
                                callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                            } else {
                                callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                            }
                        } else {
                            preferenceManager.setIsAuthenticated(false);
                            preferenceManager.setIsConnected(false);
                            callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                        }
                    }
                    break;

                case "RECONNECT1":
                    preferenceManager.setIsAuthenticated(false);
                    preferenceManager.setIsConnected(false);
                    AppConstants.isNetOff = true;
                    callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                    break;

            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rel_orders:
                if (mPopupWindow.isShowing())
                    mPopupWindow.dismiss();
                if (mPopupWindows.isShowing())
                    mPopupWindows.dismiss();
                disableBadge();
                callSetupFragment(SCREENS.ORDERS, null);
                break;
            case R.id.img_menu:

                if (mPopupWindows.isShowing()) {
                    mPopupWindows.dismiss();
                }

                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                else {
                    final float scale = getResources().getDisplayMetrics().density;
                    int width = (int) (150 * scale + 0.5f);
                    int height = (int) (75 * scale + 0.5f);
                    mPopupWindow.showAtLocation(inner_frame, Gravity.LEFT | Gravity.TOP, 0, height);
                    tv_settingMenu.setTextColor(Color.parseColor("#000000"));
                }

                break;

            case R.id.tv_close:
                int REQ_PAY_SALE = 100;
                DashboardActivity.isExternalApp = false;
                getIntent().putExtra("result", new JSONObject().toString());
                setResult(REQ_PAY_SALE, getIntent());
                finishAffinity();
                //  finish();
                break;

            default:
                if (mPopupWindow.isShowing())
                    mPopupWindow.dismiss();
                if (mPopupWindows.isShowing())
                    mPopupWindows.dismiss();
        }
    }


    //On device back pressed
    @Override
    public void onBackPressed() {

        if (isExternalApp) {
            int REQ_PAY_SALE = 100;
            DashboardActivity.isExternalApp = false;
            getIntent().putExtra("result", new JSONObject().toString());
            setResult(REQ_PAY_SALE, getIntent());
            finish();
            return;
        }


        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            if (mPopupWindows.isShowing())
                mPopupWindows.dismiss();
            return;
        }

        if (CURRENTFRAGMENT.equals(SCREENS.SETTINGS.toString())) {
            return;
        }


        if (preferenceManager.isManual()) {
            callSetupFragment(SCREENS.MANUALENTRY, null);
        } else if (preferenceManager.isHome()) {
            callSetupFragment(SCREENS.POSMATECONNECTION, null);
        } else {
            super.onBackPressed();
        }


    }

    //Fragments Screens
    public void callSetupFragment(SCREENS screens, Object data) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (screens) {

            case THIRD_PARTY:
                fragment = TriggerFragment.newInstance("", "");
                CURRENTFRAGMENT = SCREENS.THIRD_PARTY.toString();
                break;

            case POSMATECONNECTION:
                fragment = PosMateConnectioFrag.newInstance("", "");
                CURRENTFRAGMENT = SCREENS.POSMATECONNECTION.toString();

//                if (data != null) {
//                    fragment = ManualEntry.newInstance(data.toString(), "");
//                } else
//                    fragment = ManualEntry.newInstance("", "");
//
//
//                CURRENTFRAGMENT = SCREENS.MANUALENTRY.toString();

                break;

            case SETTINGS:
                fragment = Settings.newInstance("", "");
                CURRENTFRAGMENT = SCREENS.SETTINGS.toString();
                break;

           /* case TIP:
                fragment = TipSetting.newInstance("", "");
                CURRENTFRAGMENT = SCREENS.TIP.toString();
                break;*/


            case SETTLEMEMT:
                fragment = Settlement.newInstance("", "");
                CURRENTFRAGMENT = SCREENS.SETTLEMEMT.toString();
                break;


            case MANUALENTRY:

//                if (data != null) {
//                    fragment = DemoFragment.newInstance(data.toString(), "");
//                } else
//                    fragment = DemoFragment.newInstance("", "");
//
//                CURRENTFRAGMENT = SCREENS.MANUALENTRY.toString();

                if (data != null) {
                    fragment = ManualEntry.newInstance(data.toString(), "");
                } else
                    fragment = ManualEntry.newInstance("", "");

                CURRENTFRAGMENT = SCREENS.MANUALENTRY.toString();
                break;

            case ALIPAYPAYMENT:
                if (data != null) {
                    HashMap hashMap = (HashMap) data;
                    fragment = AlipayPaymentFragment.newInstance(hashMap.get("result").toString(), hashMap.get("payment_mode").toString());
                } else {
                    fragment = AlipayPaymentFragment.newInstance("", "");
                }

                CURRENTFRAGMENT = SCREENS.ALIPAYPAYMENT.toString();
                break;

            case PAYMENTPROCESSING:
                fragment = PaymentProcessing.newInstance(data.toString(), "");
                CURRENTFRAGMENT = SCREENS.PAYMENTPROCESSING.toString();
                break;

            case ABOUT:
                fragment = AboutUs.newInstance("", "");
                CURRENTFRAGMENT = SCREENS.ABOUT.toString();
                break;

            case TRANSACTION_LIST:
                fragment = TransactionListing.newInstance("", "");
                CURRENTFRAGMENT = SCREENS.TRANSACTION_LIST.toString();
                break;

            case ORDERS:
                disableBadge();
                fragment = OrderFragment.newInstance("", "");
                CURRENTFRAGMENT = SCREENS.ORDERS.toString();
                break;

            case LOYALTY_APPS:
                fragment = FragmentLoyaltyApps.newInstance("", "");
                CURRENTFRAGMENT = SCREENS.LOYALTY_APPS.toString();
                break;

            case EOD:
                fragment = EODFragment.newInstance("", "");
                CURRENTFRAGMENT = SCREENS.EOD.toString();
                break;

            case REFUND:
                fragment = RefundFragment.newInstance("", "");
                CURRENTFRAGMENT = SCREENS.REFUND.toString();
                break;

            case REFUND_UNIONPAY:
                fragment = RefundFragmentUnionPay.newInstance("", "");
                CURRENTFRAGMENT = SCREENS.REFUND_UNIONPAY.toString();
                break;

            case REGISTRATION:
                fragment = RegistrationActivity.newInstance();
                CURRENTFRAGMENT = SCREENS.REFUND.toString();
                break;

        }
        fragmentTransaction.replace(R.id.inner_frame, fragment, CURRENTFRAGMENT);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void callAuthToken() {
        openProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
        new OkHttpHandler(DashboardActivity.this, this, hashMap, "AuthToken").execute(preferenceManager.getBaseURL()+AppConstants.AUTH2);
    }


    //Update Branch details from Preference to server
    public void callUpdateBranchDetailsNew() {

        openProgressDialog();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accessId", preferenceManager.getuniqueId());
            jsonObject.put("AlipaySelected", preferenceManager.isAlipaySelected());
            jsonObject.put("AlipayValue", preferenceManager.getcnv_alipay());
            jsonObject.put("CnvAlipayDisplayAndAdd", preferenceManager.is_cnv_alipay_display_and_add());
            jsonObject.put("CnvAlipayDisplayOnly", preferenceManager.is_cnv_alipay_display_only());
            jsonObject.put("WeChatSelected", preferenceManager.isWechatSelected());
            jsonObject.put("WeChatValue", preferenceManager.getcnv_wechat());
            jsonObject.put("CnvWeChatDisplayAndAdd", preferenceManager.is_cnv_wechat_display_and_add());
            jsonObject.put("CnvWeChatDisplayOnly", preferenceManager.is_cnv_wechat_display_only());
            jsonObject.put("AlipayScanQR", preferenceManager.isAlipayScan());
            jsonObject.put("WeChatScanQR", preferenceManager.isWeChatScan());
            jsonObject.put("MerchantId", preferenceManager.getMerchantId());
            jsonObject.put("ConfigId", preferenceManager.getConfigId());
            jsonObject.put("UnionPay", preferenceManager.isUnionPaySelected());
            jsonObject.put("UnionPayQR", preferenceManager.isUnionPayQrSelected());
            jsonObject.put("isUnionPayQrCodeDisplaySelected", preferenceManager.isUnionPayQrCodeDisplaySelected());
            jsonObject.put("UnionPayQrValue", preferenceManager.getcnv_uniqr());
            jsonObject.put("cnv_unimerchantqrdisplay", preferenceManager.get_cnv_unimerchantqrdisplayLower());
            jsonObject.put("cnv_unimerchantqrdisplay_higher", preferenceManager.get_cnv_unimerchantqrdisplayHigher());
            jsonObject.put("UplanValue", preferenceManager.getcnv_uplan());
            jsonObject.put("CnvUnionpayDisplayAndAdd", preferenceManager.is_cnv_uni_display_and_add());
            jsonObject.put("CnvUnionpayDisplayOnly", preferenceManager.is_cnv_uni_display_only());
            jsonObject.put("Uplan", preferenceManager.isUplanSelected());
            jsonObject.put("AlipayWeChatPay", preferenceManager.isaggregated_singleqr());
            jsonObject.put("AlipayWeChatScanQR", preferenceManager.isAlipayWechatQrSelected());
            jsonObject.put("PrintReceiptautomatically", preferenceManager.getisPrint());
            jsonObject.put("ShowReference", preferenceManager.getshowReference());
            jsonObject.put("ShowPrintQR", preferenceManager.isQR());
            jsonObject.put("DisplayStaticQR", preferenceManager.isStaticQR());
            jsonObject.put("isDisplayLoyaltyApps", preferenceManager.isDisplayLoyaltyApps());
           jsonObject.put("isExternalInputDevice", preferenceManager.isExternalScan());
            jsonObject.put("isDragDrop", preferenceManager.isDragDrop());
           jsonObject.put("Membership/Loyality", preferenceManager.isLoyality());
            jsonObject.put("Home", preferenceManager.isHome());
            jsonObject.put("ManualEntry", preferenceManager.isManual());
            jsonObject.put("Back", preferenceManager.isBack());
            jsonObject.put("Front", preferenceManager.isFront());
            jsonObject.put("ShowMembershipManual", preferenceManager.isMembershipManual());
            jsonObject.put("ShowMembershipHome", preferenceManager.isMembershipHome());
            jsonObject.put("ConvenienceFee", preferenceManager.isConvenienceFeeSelected());
            jsonObject.put("AlipayWechatvalue", preferenceManager.getcnv_alipay());
            jsonObject.put("UnionPayvalue", preferenceManager.getcnv_uni());
            jsonObject.put("UnionPayQRValue", preferenceManager.getcnv_uniqr());
            jsonObject.put("cnv_unimerchantqrdisplay", preferenceManager.get_cnv_unimerchantqrdisplayLower());
            jsonObject.put("cnv_unimerchantqrdisplay_higher", preferenceManager.get_cnv_unimerchantqrdisplayHigher());
            jsonObject.put("EnableBranchName", preferenceManager.getBranchName());
            jsonObject.put("EnableBranchAddress", preferenceManager.getBranchAddress());
            jsonObject.put("EnableBranchEmail", preferenceManager.getBranchEmail());
            jsonObject.put("EnableBranchContactNo", preferenceManager.getBranchPhoneNo());
            jsonObject.put("EnableBranchGSTNo", preferenceManager.getGSTNo());
            jsonObject.put("TimeZoneId", preferenceManager.getTimeZoneId());
            jsonObject.put("TimeZone", preferenceManager.getTimeZone());
            jsonObject.put("isTimeZoneChecked", preferenceManager.isTimeZoneChecked());
            jsonObject.put("isTerminalIdentifier", preferenceManager.isTerminalIdentifier());
            jsonObject.put("isPOSIdentifier", preferenceManager.isPOSIdentifier());
            jsonObject.put("isLaneIdentifier", preferenceManager.isLaneIdentifier());
            jsonObject.put("LaneIdentifier", preferenceManager.getLaneIdentifier());
            jsonObject.put("TerminalIdentifier", preferenceManager.getTerminalIdentifier());
            jsonObject.put("POSIdentifier", preferenceManager.getPOSIdentifier());
            jsonObject.put("isUpdated", true);
            jsonObject.put("CnvUPIQrMPMCloudDAADD", preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add());
            jsonObject.put("CnvUPIQrMPMCloudDOnly", preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only());
            jsonObject.put("CnvUPIQrMPMCloudValue", preferenceManager.getcnv_up_upiqr_mpmcloud_lower());
            jsonObject.put("CnvUPIQrMPMCloudValueHigher", preferenceManager.getCnv_up_upiqr_mpmcloud_higher());
            jsonObject.put("CnvUPIQRMPMCloudAmount", preferenceManager.getCnv_up_upiqr_mpmcloud_amount());
            jsonObject.put("isMerchantDPARDisplay", preferenceManager.isMerchantDPARDisplay());
            jsonObject.put("PoliSelected", preferenceManager.isPoliSelected());
            jsonObject.put("PoliFeeValue", preferenceManager.getcnv_poli());
            jsonObject.put("CnvPoliDisplayAndAdd", preferenceManager.is_cnv_poli_display_and_add());
            jsonObject.put("CnvPoliDisplayOnly", preferenceManager.is_cnv_poli_display_only());


            jsonObject.put("CentrapaySelected", preferenceManager.isCentrapayMerchantQRDisplaySelected());
            jsonObject.put("CentrapayFeeValue", preferenceManager.getcnv_centrapay());
            jsonObject.put("CnvCentrapayDisplayAndAdd", preferenceManager.is_cnv_centrapay_display_and_add());
            jsonObject.put("CnvCentrapayDisplayOnly", preferenceManager.is_cnv_centrapay_display_only());

            ArrayList tipList=preferenceManager.getTipPercentage("Tip");
            jsonObject.put("DefaultTip1", tipList.get(0));
            jsonObject.put("DefaultTip2", tipList.get(1));
            jsonObject.put("DefaultTip3", tipList.get(2));
            jsonObject.put("DefaultTip4", tipList.get(3));
            jsonObject.put("DefaultTip5", tipList.get(4));
            jsonObject.put("SwitchOnTip", preferenceManager.isSwitchTip());
            jsonObject.put("DefaultTip1IsEnabled", preferenceManager.isTipDefault1());
            jsonObject.put("DefaultTip2IsEnabled", preferenceManager.isTipDefault2());
            jsonObject.put("DefaultTip3IsEnabled", preferenceManager.isTipDefault3());
            jsonObject.put("DefaultTip4IsEnabled", preferenceManager.isTipDefault4());
            jsonObject.put("DefaultTip5IsEnabled", preferenceManager.isTipDefault5());
            jsonObject.put("DefaultTip5IsEnabled", preferenceManager.isTipDefault5());
            jsonObject.put("CustomTip", preferenceManager.isTipDefaultCustom());
            jsonObject.put("PaymentModePosition", preferenceManager.getString("DATA"));


            hashMapKeys.clear();
            hashMapKeys.put("branchAddress", preferenceManager.getaddress().equals("") ? encryption("nodata") : encryption(preferenceManager.getaddress()));
            hashMapKeys.put("branchContactNo", preferenceManager.getcontact_no().equals("") ? encryption("nodata") : encryption(preferenceManager.getcontact_no()));
            hashMapKeys.put("branchName", preferenceManager.getmerchant_name().equals("") ? encryption("nodata") : encryption(preferenceManager.getmerchant_name()));
            hashMapKeys.put("branchEmail", preferenceManager.getcontact_email().equals("") ? "nodata" : encryption(preferenceManager.getcontact_email()));
            hashMapKeys.put("gstNo", preferenceManager.getgstno().equals("") ? encryption("nodata") : encryption(preferenceManager.getgstno()));
            hashMapKeys.put("terminalId", encryption(preferenceManager.getterminalId()));
            hashMapKeys.put("otherData", encryption(jsonObject.toString()));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("accessId", encryption(preferenceManager.getuniqueId()));
            hashMapKeys.put("configId", encryption(preferenceManager.getConfigId()));
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, DashboardActivity.this));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
            new OkHttpHandler(DashboardActivity.this, this, hashMap, "UpdateBranchDetailsNew")
                    .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.SAVE_TERMINAL_CONFIG);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callProgressDialogForUnionPay() {
        rel_un.setVisibility(View.VISIBLE);
        CountDownTimer timer = new CountDownTimer(5000, 5000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                rel_un.setOnClickListener(null);
                rel_un.setVisibility(View.GONE);

                this.cancel();
            }
        };
        timer.start();
    }

    // Update from preference
    public void callUpdateBranchDetails(JSONObject jsonObject) {

        openProgressDialog();
        try {
            hashMapKeys.clear();
            hashMapKeys.put("branchAddress", preferenceManager.getaddress().equals("") ? encryption("nodata") : encryption(preferenceManager.getaddress()));
            hashMapKeys.put("branchContactNo", preferenceManager.getcontact_no().equals("") ? encryption("nodata") : encryption(preferenceManager.getcontact_no()));
            hashMapKeys.put("branchName", preferenceManager.getmerchant_name().equals("") ? encryption("nodata") : encryption(preferenceManager.getmerchant_name()));
            hashMapKeys.put("branchEmail", preferenceManager.getcontact_email().equals("") ? "nodata" : encryption(preferenceManager.getcontact_email()));
            hashMapKeys.put("gstNo", preferenceManager.getgstno().equals("") ? encryption("nodata") : encryption(preferenceManager.getgstno()));
            hashMapKeys.put("terminalId", encryption(preferenceManager.getterminalId()));
            hashMapKeys.put("otherData", encryption(jsonObject.toString()));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("accessId", encryption(preferenceManager.getuniqueId()));
            hashMapKeys.put("configId", encryption(preferenceManager.getConfigId()));
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, DashboardActivity.this));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
            new OkHttpHandler(DashboardActivity.this, this, hashMap, "UpdateBranchDetails")
                    .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.SAVE_TERMINAL_CONFIG);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes()));
    }

    public String hextoString(String hexString) throws Exception {
        byte[] bytes = null;
        try {
            bytes = Hex.decodeHex(hexString.toCharArray());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(bytes, "UTF-8");
    }


    //get branch details from server
    public void callGetBranchDetails_new() {

        openProgressDialog();
        try {
            hashMapKeys.clear();
            hashMapKeys.put("terminalId", encryption(preferenceManager.getterminalId()));
//            hashMapKeys.put("terminalId", edt_terminal_id.getText().toString());
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, this));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
//            new OkHttpHandler(getActivity(), this, null, "GetBranchDetailsNew").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL5 + AppConstants.GET_TERMINAL_CONFIG
//                    + "?terminal_id=" + encryption(edt_terminal_id.getText().toString()));//encryption("47f17c5fe8d43843"));

            new OkHttpHandler(DashboardActivity.this, this, hashMap, "GetBranchDetailsNew").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.GET_TERMINAL_CONFIG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save data to preference from server if user is new.
    public void _NewUser(JSONObject jsonObject) throws Exception {
        try {
            if (jsonObject.optString("success").equals("true")) {

                 // Add data in Preferences
//                JSONObject jsonObject1 = new JSONObject(decryption(jsonObject.optString("otherData")));
                JSONObject jsonObject1 = new JSONObject(decryption(jsonObject.optString("otherData")));
                if (jsonObject.has("otherData")) {
                    ArrayList tipList=new ArrayList();
                    tipList.add(jsonObject1.optString("DefaultTip1"));
                    tipList.add(jsonObject1.optString("DefaultTip2"));
                    tipList.add(jsonObject1.optString("DefaultTip3"));
                    tipList.add(jsonObject1.optString("DefaultTip4"));
                    tipList.add(jsonObject1.optString("DefaultTip5"));
                    preferenceManager.setTipPercentage("Tip",tipList);

                    preferenceManager.setisSwitchTip(jsonObject1.optBoolean("SwitchOnTip"));

                    preferenceManager.setisTipDefault1(jsonObject1.optBoolean("DefaultTip1IsEnabled"));
                    preferenceManager.setisTipDefault2(jsonObject1.optBoolean("DefaultTip2IsEnabled"));
                    preferenceManager.setisTipDefault3(jsonObject1.optBoolean("DefaultTip3IsEnabled"));
                    preferenceManager.setisTipDefault4(jsonObject1.optBoolean("DefaultTip4IsEnabled"));
                    preferenceManager.setisTipDefault5(jsonObject1.optBoolean("DefaultTip5IsEnabled"));
                    preferenceManager.setisTipDefaultCustom(jsonObject1.optBoolean("CustomTip"));
                    preferenceManager.putString("DATA",jsonObject1.optString("PaymentModePosition"));

                    preferenceManager.setisCentrapayMerchantQRDisplaySelected(jsonObject1.optBoolean("CentrapaySelected"));
                    preferenceManager.setcnv_centrapay_display_and_add(jsonObject1.optBoolean("CnvCentrapayDisplayAndAdd"));
                    preferenceManager.setcnv_centrapay_display_only(jsonObject1.optBoolean("CnvCentrapayDisplayOnly"));
                    preferenceManager.setcnv_centrapay(jsonObject1.optString("CentrapayFeeValue"));
                    preferenceManager.setisPoliSelected(jsonObject1.optBoolean("PoliSelected"));
                    preferenceManager.setcnv_poli_display_and_add(jsonObject1.optBoolean("CnvPoliDisplayAndAdd"));
                    preferenceManager.setcnv_poli_display_only(jsonObject1.optBoolean("CnvPoliDisplayOnly"));
                    preferenceManager.setcnv_poli(jsonObject1.optString("PoliFeeValue"));
                    preferenceManager.setcnv_alipay_diaplay_and_add(jsonObject1.optBoolean("CnvAlipayDisplayAndAdd"));
                    preferenceManager.setcnv_alipay_diaplay_only(jsonObject1.optBoolean("CnvAlipayDisplayOnly"));
                    preferenceManager.setcnv_wechat_display_and_add(jsonObject1.optBoolean("CnvWeChatDisplayAndAdd"));
                    preferenceManager.setcnv_wechat_display_only(jsonObject1.optBoolean("CnvWeChatDisplayOnly"));
                    preferenceManager.setisAlipaySelected(jsonObject1.optBoolean("AlipaySelected"));
                    preferenceManager.setisWechatSelected(jsonObject1.optBoolean("WeChatSelected"));
                    preferenceManager.setcnv_wechat(jsonObject1.optString("WeChatValue"));

                    preferenceManager.setisWeChatScan(jsonObject1.optBoolean("WeChatScanQR"));
                    preferenceManager.setisAlipayScan(jsonObject1.optBoolean("AlipayScanQR"));

                    preferenceManager.setisUnionPaySelected(jsonObject1.optBoolean("UnionPay"));
                    preferenceManager.setUnionPayQrSelected(jsonObject1.optBoolean("UnionPayQR"));
                    preferenceManager.setisUnionPayQrCodeDisplaySelected(jsonObject1.optBoolean("isUnionPayQrCodeDisplaySelected"));
                    preferenceManager.setcnv_uniqr(jsonObject1.optString("UnionPayQrValue"));
                    preferenceManager.set_cnv_unimerchantqrdisplayLower(jsonObject1.optString("cnv_unimerchantqrdisplay"));
                    preferenceManager.set_cnv_unimerchantqrdisplayHigher(jsonObject1.optString("cnv_unimerchantqrdisplay_higher"));
                    preferenceManager.setcnv_uplan(jsonObject1.optString("UplanValue"));
                    preferenceManager.setcnv_uni_display_and_add(jsonObject1.optBoolean("CnvUnionpayDisplayAndAdd"));
                    preferenceManager.setcnv_uni_display_only(jsonObject1.optBoolean("CnvUnionpayDisplayOnly"));
                    preferenceManager.setisUplanSelected(jsonObject1.optBoolean("Uplan"));
                    preferenceManager.setaggregated_singleqr(jsonObject1.optBoolean("AlipayWeChatPay"));
                    preferenceManager.setAlipayWechatQrSelected(jsonObject1.optBoolean("AlipayWeChatScanQR"));
                    preferenceManager.setisPrint(jsonObject1.optString("PrintReceiptautomatically"));
                    preferenceManager.setshowReference(jsonObject1.optString("ShowReference"));
                    preferenceManager.setisQR(jsonObject1.optBoolean("ShowPrintQR"));
                    preferenceManager.setisStaticQR(jsonObject1.optBoolean("DisplayStaticQR"));
                    preferenceManager.setisDisplayLoyaltyApps(jsonObject1.optBoolean("isDisplayLoyaltyApps"));
                    preferenceManager.setisExternalScan(jsonObject1.optBoolean("isExternalInputDevice"));
                    preferenceManager.setDragDrop(jsonObject1.optBoolean("isDragDrop"));
                    preferenceManager.setisMembershipManual(jsonObject1.optBoolean("ShowMembershipManual"));
                    preferenceManager.setisMembershipHome(jsonObject1.optBoolean("ShowMembershipHome"));
                    preferenceManager.setisLoyality(jsonObject1.optBoolean("Membership/Loyality"));
                    preferenceManager.setIsHome(jsonObject1.optBoolean("Home"));
                    preferenceManager.setIsManual(jsonObject1.optBoolean("ManualEntry"));
                    preferenceManager.setisConvenienceFeeSelected(jsonObject1.optBoolean("ConvenienceFee"));
                    preferenceManager.setcnv_alipay(jsonObject1.optString("AlipayWechatvalue"));
                    preferenceManager.setcnv_uni(jsonObject1.optString("UnionPayvalue"));
                    preferenceManager.setcnv_uniqr(jsonObject1.optString("UnionPayQrValue"));
                    preferenceManager.set_cnv_unimerchantqrdisplayLower(jsonObject1.optString("cnv_unimerchantqrdisplay"));
                    preferenceManager.set_cnv_unimerchantqrdisplayHigher(jsonObject1.optString("cnv_unimerchantqrdisplay_higher"));
                    preferenceManager.setcnv_uplan(jsonObject1.optString("UplanValue"));
                    preferenceManager.setBranchName(jsonObject1.optString("EnableBranchName"));
                    preferenceManager.setBranchAddress(jsonObject1.optString("EnableBranchAddress"));
                    preferenceManager.setBranchEmail(jsonObject1.optString("EnableBranchEmail"));
                    preferenceManager.setBranchPhoneNo(jsonObject1.optString("EnableBranchContactNo"));
                    preferenceManager.setGSTNo(jsonObject1.optString("EnableBranchGSTNo"));
                    preferenceManager.setTimeZoneId(jsonObject1.optString("TimeZoneId"));
                    preferenceManager.setTimeZone(jsonObject1.optString("TimeZone"));
                    preferenceManager.setisTimeZoneChecked(jsonObject1.optBoolean("isTimeZoneChecked"));
                    preferenceManager.setIsBack(jsonObject1.optBoolean("Back"));
                    preferenceManager.setIsFront(jsonObject1.optBoolean("Front"));

                    preferenceManager.setTerminalIdentifier(jsonObject1.optString("TerminalIdentifier"));
                    preferenceManager.setPOSIdentifier(jsonObject1.optString("POSIdentifier"));
                    preferenceManager.setLaneIdentifier(jsonObject1.optString("LaneIdentifier"));
                    preferenceManager.setisLaneIdentifier(jsonObject1.optBoolean("isLaneIdentifier"));
                    preferenceManager.setisPOSIdentifier(jsonObject1.optBoolean("isPOSIdentifier"));
                    preferenceManager.setisTerminalIdentifier(jsonObject1.optBoolean("isTerminalIdentifier"));

                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(jsonObject1.optBoolean("CnvUPIQrMPMCloudDAADD"));
                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(jsonObject1.optBoolean("CnvUPIQrMPMCloudDOnly"));
                    preferenceManager.setcnv_up_upiqr_mpmcloud_lower(jsonObject1.optString("CnvUPIQrMPMCloudValue"));
                    preferenceManager.setCnv_up_upiqr_mpmcloud_higher(jsonObject1.optString("CnvUPIQrMPMCloudValueHigher"));
                    preferenceManager.setCnv_up_upiqr_mpmcloud_amount(jsonObject1.optString("CnvUPIQRMPMCloudAmount"));
                    preferenceManager.setisMerchantDPARDisplay(jsonObject1.optBoolean("isMerchantDPARDisplay"));

                }


            } else {

                if (jsonObject.has("config_id")) {
                    preferenceManager.setConfigId(decryption(jsonObject.optString("config_id")));
                }

                if (jsonObject.has("merchant_id")) {
                    preferenceManager.setMerchantId(decryption(jsonObject.optString("merchant_id")));
                }

                if (jsonObject.has("terminal_id")) {
                    preferenceManager.setterminalId(decryption(jsonObject.optString("terminal_id")));
                }

            }


        } catch (Exception e) {
            if (jsonObject.has("config_id")) {
                preferenceManager.setConfigId(decryption(jsonObject.optString("config_id")));
            }

            if (jsonObject.has("merchant_id")) {
                preferenceManager.setMerchantId(decryption(jsonObject.optString("merchant_id")));
            }
            if (jsonObject.has("terminal_id")) {
                preferenceManager.setterminalId(decryption(jsonObject.optString("terminal_id")));
            }
        }
    }
    private double calculatecnv(String per) {
        NumberFormat f = NumberFormat.getInstance(); // Gets a NumberFormat with the default locale, you can specify a Locale as first parameter (like Locale.FRENCH)
        double cent=0.0;
        try {
            cent = f.parse(per).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cent;
    }
}
