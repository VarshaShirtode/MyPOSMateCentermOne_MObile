package com.quagnitia.myposmate.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.qrscan.AidlQuickScanZbar;
import com.centerm.smartpos.aidl.qrscan.AidlScanCallback;
import com.centerm.smartpos.aidl.qrscan.CameraBeanZbar;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.util.LogUtil;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.utils.PreferencesManager;

import java.util.HashMap;

import static com.quagnitia.myposmate.fragments.ManualEntry.pass_amount;

public class FragmentLoyaltyApps extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    View view;
    Button btn_cancel;
    ImageView img_one,img_two,img_three,img_fly_buys,img_air_points,img_smart_fuel,img_goody;
    PreferencesManager preferencesManager;



    public FragmentLoyaltyApps() {
        // Required empty public constructor
    }

    public static FragmentLoyaltyApps newInstance(String param1, String param2) {
        FragmentLoyaltyApps fragment = new FragmentLoyaltyApps();
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
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_loyalty_apps, container, false);
        EditText edt_amount=view.findViewById(R.id.edt_amount);
        edt_amount.setText(pass_amount);
        initUI();
        initListener();
        bindService();
        return view;
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(intentService);
        super.onDestroy();
    }

    public void initUI()
    {
        preferencesManager=PreferencesManager.getInstance(getActivity());
        btn_cancel=view.findViewById(R.id.btn_cancel);
        img_one=view.findViewById(R.id.img_one);
        img_two=view.findViewById(R.id.img_two);
        img_three=view.findViewById(R.id.img_three);
        img_fly_buys=view.findViewById(R.id.img_fly_buys);
        img_air_points=view.findViewById(R.id.img_air_points);
        img_smart_fuel=view.findViewById(R.id.img_smart_fuel);
        img_goody=view.findViewById(R.id.img_goody);
    }

    public void initListener()
    {
        btn_cancel.setOnClickListener(this);
        img_one.setOnClickListener(this);
        img_two.setOnClickListener(this);
        img_three.setOnClickListener(this);
        img_fly_buys.setOnClickListener(this);
        img_air_points.setOnClickListener(this);
        img_smart_fuel.setOnClickListener(this);
        img_goody.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_cancel:
                if(preferencesManager.isManual())
                {
                    ((DashboardActivity)getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY,null);
                }
                else
                {
                    ((DashboardActivity)getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION,null);
                }
                break;

            case R.id.img_one:
                Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.goodealnz.merchant");
                if (intent == null) {
                    // Bring user to the market or let them choose an app?
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + "com.goodealnz.merchant"));
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                break;
          //  case R.id.img_two:
            case R.id.img_three:
//            case R.id.img_fly_buys:
//            case R.id.img_goody:
//            case R.id.img_smart_fuel:
//            case R.id.img_air_points:
                callCam();
                break;

        }
    }


    public void callCam()
    {
//        if(preferencesManager.isFront())
//        {
//            stsartFastScan(false);//front
//        }
//        else if(preferencesManager.isBack())
//        {
            stsartFastScan(true);//Back
//        }
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
                    if (getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
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

                                    Toast.makeText(getActivity(), arg0 + "", Toast.LENGTH_SHORT).show();

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

