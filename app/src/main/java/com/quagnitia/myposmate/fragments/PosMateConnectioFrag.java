package com.quagnitia.myposmate.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.qrscan.AidlQuickScanZbar;
import com.centerm.smartpos.aidl.qrscan.AidlScanCallback;
import com.centerm.smartpos.aidl.qrscan.CameraBeanZbar;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.util.LogUtil;
import com.quagnitia.myposmate.MyPOSMateApplication;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.scanner.ScannerForBack;
import com.quagnitia.myposmate.scanner.ScannerForFront;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;
import com.usdk.apiservice.aidl.scanner.OnScanListener;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.UUID;

import pl.droidsonroids.gif.GifTextView;

import static com.quagnitia.myposmate.printer.ApiDemo.TAG;

public class PosMateConnectioFrag extends Fragment implements View.OnClickListener, OnTaskCompleted {

    private View view;
    private AbstractXMPPConnection asbtractConnection;
    private String serverIp = AppConstants.serverIp;
    private Handler handler;
    private PreferencesManager preferencesManager;
    private TextView tv_status, tv_status_scan;
    private IntentFilter intentFilter;
    private ProgressBar progressbar;
    private MyReceiver myReceiver;
    private Button btn_reconnect,btn_back,btn_front, tv_status_scan_button;
    private RelativeLayout rel_membership;
    private ArrayList<Integer> imgarr = null;
    private GifTextView gifTextView;
    private ImageView close_btn;
    private Timer timer;
    TreeMap<String, String> hashMapKeys;

    public static PosMateConnectioFrag newInstance(String param1, String param2) {
        PosMateConnectioFrag fragment = new PosMateConnectioFrag();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pos_mate_connectio_frag, null);
        myReceiver = new MyReceiver();
        hashMapKeys = new TreeMap<>();
        intentFilter = new IntentFilter();
        intentFilter.addAction("Connected");
        intentFilter.addAction("Authenticated");
        intentFilter.addAction("ConnectionClosed");
        intentFilter.addAction("ConnectionClosedOnError");
        intentFilter.addAction("NetConnectionOn");
        intentFilter.addAction("NetConnectionOff");
        intentFilter.addAction("Reconnect");

        getActivity().registerReceiver(myReceiver, intentFilter);
        preferencesManager = PreferencesManager.getInstance(getActivity());
        preferencesManager.setisResetTerminal(false);
        handler = new Handler();
        initUI();
        bindService();
        if (preferencesManager.isAuthenticated()) {
            ((DashboardActivity) getActivity()).img_menu.setEnabled(true);
            tv_status.setText("MyPOSMate® is authenticated successfully.");
            tv_status.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            progressbar.setVisibility(View.GONE);

            if (preferencesManager.isDisplayAds()) {
                int seconds = Integer.parseInt(preferencesManager.getdisplayAdsTime()) * 1000;

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        gif_frame.setVisibility(View.VISIBLE);
//                        if (imgarr != null && imgarr.size() > 0) {
//                            timer = new Timer();
//    /*
//    Schedule a task for repeated fixed-rate execution after a specific delay has passed.
//    Parameters
//    task  the task to schedule.
//    delay  amount of time in milliseconds before first execution.
//    period  amount of time in milliseconds between subsequent executions.
//    */
//                            timer.scheduleAtFixedRate(new customTimerTask(getActivity()), 4000, 4000);
//                        }
//                    }
//                }, seconds);
            }


        } else {
            checkAvaliability();
            ((MyPOSMateApplication) getActivity().getApplicationContext()).initChat(preferencesManager.getUsername(), preferencesManager.getPassword());
        }
        imgarr = new ArrayList<>();
        imgarr.add(R.drawable.unionpay_ad);
//        imgarr.add(R.drawable.img_one);
//        imgarr.add(R.drawable.img_two);
//        imgarr.add(R.drawable.img_three);
//        imgarr.add(R.drawable.img_four);
//        imgarr.add(R.drawable.img_five);

        return view;
    }

    public void checkAvaliability() {
        if (isNetworkAvailable()) {
            tv_status.setText("MyPOSMate® is online. Please wait while connecting.");
        } else {
            tv_status.setText("MyPOSMate® offline. Please check your network connection.");
        }
    }

    RelativeLayout gif_frame;

    public void initUI() {
        tv_status = (TextView) view.findViewById(R.id.tv_status);
        tv_status_scan = (TextView) view.findViewById(R.id.tv_status_scan);
        tv_status_scan_button = (Button) view.findViewById(R.id.tv_status_scan_button);
        btn_back = (Button) view.findViewById(R.id.btn_back);
        btn_front = (Button) view.findViewById(R.id.btn_front);
        rel_membership=(RelativeLayout)view.findViewById(R.id.rel_membership);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        progressbar.setVisibility(View.VISIBLE);
        btn_reconnect = (Button) view.findViewById(R.id.btn_reconnect);
        btn_reconnect.setOnClickListener(this);
        gifTextView = (GifTextView) view.findViewById(R.id.img_gif);
        gif_frame = (RelativeLayout) view.findViewById(R.id.gif_frame);
        close_btn = (ImageView) view.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(this);
        //tv_status_scan_button.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_front.setOnClickListener(this);


//        if (!preferencesManager.isLoyality()) {
//            tv_status_scan.setVisibility(View.INVISIBLE);
//            tv_status_scan_button.setVisibility(View.GONE);
//        } else {
//            tv_status_scan.setVisibility(View.INVISIBLE);
//            tv_status_scan_button.setVisibility(View.VISIBLE);
//        }


        if(preferencesManager.isMembershipHome())
        {
            rel_membership.setVisibility(View.VISIBLE);
            tv_status_scan.setVisibility(View.INVISIBLE);
            tv_status_scan_button.setVisibility(View.VISIBLE);
            if(preferencesManager.isFront())
            {
                btn_front.setVisibility(View.VISIBLE);
                btn_back.setVisibility(View.GONE);
            }
             if(preferencesManager.isBack())
            {
                btn_front.setVisibility(View.GONE);
                btn_back.setVisibility(View.VISIBLE);
            }
             if(preferencesManager.isBack() && preferencesManager.isFront())
            {
                btn_front.setVisibility(View.VISIBLE);
                btn_back.setVisibility(View.VISIBLE);
            }
             if(!preferencesManager.isBack() && !preferencesManager.isFront())
            {
                rel_membership.setVisibility(View.GONE);
            }

        }
        else
        {
            rel_membership.setVisibility(View.GONE);
            tv_status_scan.setVisibility(View.GONE);
            tv_status_scan_button.setVisibility(View.GONE);
        }




    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onDestroyView() {
        gif_frame.setVisibility(View.GONE);
//        if (timer != null)
//            timer.cancel();
        super.onDestroyView();
    }

    private Context mContext;

    @Override
    public void onClick(View view) {
        mContext = getActivity();
        if (((DashboardActivity) mContext).mPopupWindow.isShowing())
            ((DashboardActivity) mContext).mPopupWindow.dismiss();

        switch (view.getId()) {
            case R.id.btn_front:
            case R.id.tv_status_scan_button:
                try {
                    if (preferencesManager.getLaneIdentifier().equals("")) {
                        Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
                    } else if (preferencesManager.getPOSIdentifier().equals("")) {
                        Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
                    } else {
                        isFront=true;
                        callAuthToken();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;



            case R.id.btn_back:
                try {
                    if (preferencesManager.getLaneIdentifier().equals("")) {
                        Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
                    } else if (preferencesManager.getPOSIdentifier().equals("")) {
                        Toast.makeText(getActivity(), "Please update pos identifier in branch details option.", Toast.LENGTH_LONG).show();
                    } else {
                        isBack=true;
                        callAuthToken();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_reconnect:
                if (isNetworkAvailable()) {
                    progressbar.setVisibility(View.VISIBLE);
                    tv_status.setText("MyPOSMate® is online. Please wait while connecting.");
                    ((MyPOSMateApplication) getActivity().getApplicationContext()).initChat(preferencesManager.getUsername(), preferencesManager.getPassword());
                } else {
                    progressbar.setVisibility(View.GONE);
                    tv_status.setText("MyPOSMate® is offline. Please make sure your internet connection is ON.");
                }
                break;

            case R.id.close_btn:
                gif_frame.setVisibility(View.GONE);
                ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
//                if (timer != null)
//                    timer.cancel();
                break;
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(myReceiver);
        gif_frame.setVisibility(View.GONE);
        if (conn != null) {
            getActivity().unbindService(conn);
        }
        getActivity().stopService(intentService);
//        if (timer != null)
//            timer.cancel();
        super.onDestroy();


    }

    @Override
    public void onResume() {
        super.onResume();
        // register connection status listener

    }


    public class customTimerTask extends TimerTask {
        Activity mycontext;
        int i = 0;

        public customTimerTask(Activity c) {
            mycontext = c;
        }

        @Override
        public void run() {
            if (mycontext != null)
                mycontext.runOnUiThread(new Runnable() {
                    public void run() {
                        // Display the images
                        gifTextView.setBackgroundResource(imgarr.get(i));
                        i++;
                        if (i >= imgarr.size()) {
                            i = 0;
                        }
                    }
                });
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ac = intent.getAction();
            switch (ac) {
                case "NetConnectionOn":
                    tv_status.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    tv_status.setText("MyPOSMate® is online.\nAuthentication is in progress.");
                    btn_reconnect.setVisibility(View.GONE);
                    break;
                case "NetConnectionOff":
                    tv_status.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    tv_status.setText("Please check your network connection.");
                    progressbar.setVisibility(View.GONE);
                    btn_reconnect.setVisibility(View.VISIBLE);
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                    break;
                case "Connected":
                    tv_status.setText("MyPOSMate® is connected successfully. Please wait while authenticating.");
                    tv_status.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                    btn_reconnect.setVisibility(View.GONE);
                    break;
                case "Authenticated":
                    ((DashboardActivity) getActivity()).img_menu.setEnabled(true);
                    tv_status.setText("MyPOSMate® is authenticated successfully.");
                    tv_status.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    progressbar.setVisibility(View.GONE);
                    btn_reconnect.setVisibility(View.GONE);
                    //  preferencesManager.setIsManual(true);

                    if (preferencesManager.isManual()) {
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                    } else {

                        if (AppConstants.isNetOff) {
                            AppConstants.isNetOff = false;
                            tv_status.setText("Connection is closed.Please reconnect.");
                            tv_status.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            progressbar.setVisibility(View.GONE);
                            btn_reconnect.setVisibility(View.VISIBLE);

                        }



                    }


                    if (preferencesManager.isDisplayAds()) {
                    }


                    break;
                case "ConnectionClosed":
                    tv_status.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    tv_status.setText("Connection is closed.Please reconnect.");
                    progressbar.setVisibility(View.GONE);
                    btn_reconnect.setVisibility(View.VISIBLE);
                    break;
                case "ConnectionClosedOnError":
                    tv_status.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    tv_status.setText("Connection is closed.Please reconnect.");
                    progressbar.setVisibility(View.GONE);
                    btn_reconnect.setVisibility(View.VISIBLE);
                    break;
                case "Reconnect":
                    if (isNetworkAvailable()) {
                        progressbar.setVisibility(View.VISIBLE);
                        tv_status.setText("MyPOSMate® is online. Please wait while connecting.");
                        ((MyPOSMateApplication) getActivity().getApplicationContext()).initChat(preferencesManager.getUsername(), preferencesManager.getPassword());
                    } else {
                        progressbar.setVisibility(View.GONE);
                        tv_status.setText("MyPOSMate® is offline. Please make sure your internet connection is ON.");
                    }
                    break;
            }
        }
    }

    ProgressDialog progress;

    public void openProgressDialog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }


    public void callMembershipLoyality(String qr_data) {
        openProgressDialog();
        hashMapKeys.clear();
        hashMapKeys.put("merchant_id", preferencesManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferencesManager.getterminalId());
        hashMapKeys.put("config_id", preferencesManager.getConfigId());
        hashMapKeys.put("device_id", UUID.randomUUID().toString().replace("-",""));
        hashMapKeys.put("qr_data", qr_data);
        hashMapKeys.put("random_str", new Date().getTime() + "");
        hashMapKeys.put("lane_id", preferencesManager.getLaneIdentifier());
        hashMapKeys.put("pos_id", preferencesManager.getPOSIdentifier());
        new OkHttpHandler(getActivity(), this, null, "saveLoyaltyInfo")
                .execute(AppConstants.BASE_URL2 + AppConstants.V2_SAVE_LOYALTY_INFO + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferencesManager.getauthToken());
    }
    public void callAuthToken() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
//        hashMap.put("username", AppConstants.CLIENT_ID);
//        hashMap.put("password",AppConstants.CLIENT_SECRET);
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.V2_AUTH);

    }
    public static boolean isBack = false;
    public static boolean isFront = false;
    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {

        JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {
            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferencesManager.setauthToken(jsonObject.optString("access_token"));
                }
                if(isBack)
                {
                    isBack=false;
                    stsartFastScan(true);//Back
                }
                if(isFront)
                {
                    isFront=false;
                    stsartFastScan(false);//front
                }
                break;

            case "saveLoyaltyInfo":
                progress.dismiss();
                if (jsonObject.optBoolean("status")) {
                    tv_status_scan.setVisibility(View.VISIBLE);
                    tv_status_scan.setText("Thank you for using Membership/Loyality");
                    tv_status_scan_button.setText("Rescan Membership/Loyality");
                    Toast.makeText(getActivity(), "Loyality data uploaded successfully ", Toast.LENGTH_SHORT).show();
                } else {
                    tv_status_scan.setVisibility(View.VISIBLE);
                    tv_status_scan.setText("Membership/Loyality could not be scanned");
                    tv_status_scan_button.setText("Rescan Membership/Loyality");
                    Toast.makeText(getActivity(), "Loyality data upload failed ", Toast.LENGTH_SHORT).show();
                }
                break;
        }


    }




    private AidlQuickScanZbar aidlQuickScanService = null;
    private int bestWidth = 640;
    private int bestHeight = 480;
    private int spinDegree = 90;
    private int cameraDisplayEffect = 0;
    private void switchCameraDisplayEffect(boolean cameraBack) {
        try {
            aidlQuickScanService.switchCameraDisplayEffect(cameraBack?0:1, cameraDisplayEffect);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stsartFastScan(boolean cameraBack) {
        final long startTime = System.currentTimeMillis();
        try {
            CameraBeanZbar cameraBean = new com.centerm.smartpos.aidl.qrscan.CameraBeanZbar(0, bestWidth, bestHeight, 4, Integer.MAX_VALUE, spinDegree, 1);
            if(cameraBack){
                cameraBean.setCameraId(0);
            }else{
                cameraBean.setCameraId(1);
            }
            HashMap<String,Object> externalMap = new HashMap<String,Object>();
            externalMap.put("ShowPreview", true);
            cameraBean.setExternalMap(externalMap);
            switchCameraDisplayEffect(cameraBack);//2018-03-06 增加切换摄像头显示效果 linpeita@centerm.com
            aidlQuickScanService.scanQRCode(cameraBean, new AidlScanCallback.Stub() {
                @Override
                public void onFailed(int arg0) throws RemoteException {
                    if (getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                tv_status_scan.setVisibility(View.VISIBLE);
                                tv_status_scan.setText("Membership/Loyality could not be scanned");
                                tv_status_scan_button.setText("Rescan Membership/Loyality");
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

                                if (preferencesManager.getLaneIdentifier().equals("")) {
                                    Toast.makeText(getActivity(), "Please update lane identifier in branch details option.", Toast.LENGTH_LONG).show();
                                } else if (preferencesManager.getPOSIdentifier().equals("")) {
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
                }
                catch (Exception e)
                {

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
