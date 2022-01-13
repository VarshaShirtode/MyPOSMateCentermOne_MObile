package com.quagnitia.myposmate.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.utils.AESHelper;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.apache.commons.codec.binary.Hex;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.json.JSONObject;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TreeMap;

public class Settings extends Fragment implements View.OnClickListener, ConnectionListener, OnTaskCompleted {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Button btn_save, btn_reset, btn_cancel, btn_exit;
    private View view;
    private String android_id;
    private ProgressDialog progress;
    private PreferencesManager preferenceManager;
    private EditText edt_terminal_id, edt_terminal_ip, edt_unique_id;
    private AbstractXMPPConnection asbtractConnection;
    //private String serverIp = AppConstants.serverIp;
   // private String serverIp = preferenceManager.getBaseURL().replace("https://","");
    private String username, password;
    private Handler handler;
    TreeMap<String, String> hashMapKeys;
    public boolean isRegisteredStart = false;
    RelativeLayout rel_orders;
    TextView txtServer;
    public Settings() {
        // Required empty public constructor
    }

    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
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

    boolean isGetBranchDetailsCalled = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        handler = new Handler();
        hashMapKeys = new TreeMap<>();
        preferenceManager = PreferencesManager.getInstance(getActivity());

        initUI(view);
        initListener();
        view.findViewById(R.id.activity_main).setOnTouchListener(new View.OnTouchListener() {
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

    public void initUI(View view) {
        android_id = android.provider.Settings.Secure.getString(getContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        btn_save = view.findViewById(R.id.btn_save);
        btn_exit = view.findViewById(R.id.btn_exit);
        btn_reset = view.findViewById(R.id.btn_reset);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        edt_terminal_id = view.findViewById(R.id.edt_terminal_id);
        edt_terminal_ip = view.findViewById(R.id.edt_terminal_ip);
        edt_unique_id = view.findViewById(R.id.edt_unique_id);
        edt_terminal_id.setText(android_id);//"71283458bfce2b86");
        edt_terminal_ip.setText(getLocalIpAddress());
        rel_orders=  getActivity().findViewById(R.id.rel_orders);
        rel_orders.setVisibility(View.GONE);
        txtServer=view.findViewById(R.id.txtServer);
        if (!preferenceManager.getBaseURL().equalsIgnoreCase(""))
        {
            txtServer.setText(preferenceManager.getBaseURL());
        }
//        preferenceManager.setuniqueId("eeac599d06a42e9b");
//        preferenceManager.setMerchantId("29");
//        preferenceManager.setConfigId("60");
        edt_unique_id.setText(preferenceManager.getuniqueId
                ());

        if (preferenceManager.getuniqueId().equals("")) {
            ((DashboardActivity) getActivity()).img_menu.setEnabled(false);
        } else {
            ((DashboardActivity) getActivity()).img_menu.setEnabled(true);
        }
        //added on 20th may 2019
        if (!AppConstants.isRegistered)
//            callGetBranchDetails_old();
        {

            isGetBranchDetailsCalled = true;
            callAuthToken();
        } else {
            edt_unique_id.setText(preferenceManager.getuniqueId());
            preferenceManager.setaggregated_singleqr(true);
            if (edt_terminal_id.getText().toString().equals("") && edt_unique_id.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter Terminal Id and Access Id", Toast.LENGTH_LONG).show();
            } else if (edt_terminal_id.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter Terminal Id", Toast.LENGTH_LONG).show();
            } else if (edt_unique_id.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter Access Id", Toast.LENGTH_LONG).show();
            } else {
                isRegisteredStart = true;
                callAuthToken();
            }
        }
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void initListener() {
        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
    }

    public void callValidateTerminal() {
        openProgressDialog();
        hashMapKeys.clear();
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", edt_terminal_id.getText().toString());
        hashMapKeys.put("access_id", edt_unique_id.getText().toString());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("random_str", new Date().getTime() + "");
//        hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
//        hashMapKeys.put("access_token",preferenceManager.getauthToken());
        preferenceManager.setuniqueId(edt_unique_id.getText().toString());
        new OkHttpHandler(getActivity(), this, null, "validateTerminal")
                .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.VALIDATE_TERMINAL + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.putAll(hashMapKeys);
//        new OkHttpHandler(getActivity(), this, hashMap, "validateTerminal")
//                .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.VALIDATE_TERMINAL);


    }


    public void openProgressDialog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }


    public void callAuthToken() {
        openProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(preferenceManager.getBaseURL()+AppConstants.AUTH2);
    }


    public void callDeleteTerminal() {
        openProgressDialog();
        try {
            hashMapKeys.clear();
            hashMapKeys.put("terminalId", encryption(edt_terminal_id.getText().toString()));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
            new OkHttpHandler(getActivity(), this, hashMap, "DeleteTerminal").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.DELETE_TERMINAL_CONFIG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callDeleteTerminalOld() throws Exception {
        String s = decryption(encryption(edt_terminal_id.getText().toString()));
        openProgressDialog();
        try {
            hashMapKeys.clear();
            hashMapKeys.put("terminalId", encryption_old(edt_terminal_id.getText().toString()));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

//            new OkHttpHandler(getActivity(), this, null, "DeleteTerminalOld").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL5 + AppConstants.DELETE_TERMINAL_CONFIG
//                    + "?terminal_id=" + encryption_old(edt_terminal_id.getText().toString()));

            new OkHttpHandler(getActivity(), this, hashMap, "DeleteTerminalOld").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.DELETE_TERMINAL_CONFIG);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Context mContext;
    public static boolean isSaveAndOK = false;

    @Override
    public void onClick(View view) {
        mContext = getActivity();
        if (((DashboardActivity) mContext).mPopupWindow.isShowing())
            ((DashboardActivity) mContext).mPopupWindow.dismiss();

        switch (view.getId()) {

            case R.id.btn_reset:

                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                LayoutInflater lf = (LayoutInflater) (getActivity())
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogview = lf.inflate(R.layout.reset_terminal_dialog, null);
                TextView tv_pass_text = (TextView) dialogview.findViewById(R.id.tv_pass_text);
                EditText edt_access_id = (EditText) dialogview.findViewById(R.id.edt_access_id);
                TextView body = (TextView) dialogview
                        .findViewById(R.id.dialogBody);
                dialog.setContentView(dialogview);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);
                dialog.show();

                edt_access_id.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            tv_pass_text.setText("If access id " + s +
                                    " is entered RESET can be clicked. Otherwise only CANCEL option would be available to click.");
                        } else {
                            tv_pass_text.setText("If access id is entered RESET can be clicked. Otherwise only CANCEL option would be available to click.");
                        }
                    }
                });


                TextView cancel = (TextView) dialogview
                        .findViewById(R.id.dialogCancel);
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        progress.dismiss();
                        dialog.dismiss();
                    }
                });

                TextView retry = (TextView) dialogview
                        .findViewById(R.id.dialogRetry);
                retry.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (edt_access_id.getText().toString().equals("")) {
                            Toast.makeText(getActivity(), "Please input the access id", Toast.LENGTH_SHORT).show();
                        } else if (!edt_access_id.getText().toString().equals("") && !edt_unique_id.getText().toString().equals("")) {
                            if (edt_access_id.getText().toString().equalsIgnoreCase(edt_unique_id.getText().toString())) {
                                callDeleteTerminal();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), "Invalid access id", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Invalid access id", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                break;

            case R.id.btn_save:
                if (edt_unique_id.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter Access Id", Toast.LENGTH_LONG).show();
                    return;
                } else if (config_id.equals("")) {
                    Toast.makeText(getActivity(), "Config ID not present, Please register again.", Toast.LENGTH_SHORT).show();
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.REGISTRATION, null);
                    return;
                }

                preferenceManager.setaggregated_singleqr(true);
                if (edt_terminal_id.getText().toString().equals("") && edt_unique_id.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter Terminal Id and Access Id", Toast.LENGTH_LONG).show();
                } else if (edt_terminal_id.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter Terminal Id", Toast.LENGTH_LONG).show();
                } else if (edt_unique_id.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter Access Id", Toast.LENGTH_LONG).show();
                } else {
//                    preferenceManager.setterminalId(edt_terminal_id.getText().toString());
//                    preferenceManager.setuniqueId(edt_unique_id.getText().toString());
                    isSaveAndOK = true;
                    callAuthToken();

                }

                break;
            case R.id.btn_cancel:
                preferenceManager.setisRegistered(false);
                if (preferenceManager.isHome()) {
                    if (!preferenceManager.getUsername().equals(""))
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                    else
                        edt_unique_id.setText("");

                } else {
                    if (!preferenceManager.getUsername().equals(""))
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                    else
                        edt_unique_id.setText("");
                }

                break;
            case R.id.btn_exit:
                // getActivity().finish();
                getActivity().finishAffinity();
                //    System.exit(0);
                break;
        }
    }

    void initChat(final String username, final String password) {


// Create a connection to the jabber.org server on a specific port.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   // DomainBareJid serviceName = JidCreate.domainBareFrom(serverIp);
                    DomainBareJid serviceName = JidCreate.domainBareFrom(preferenceManager.getBaseURL().replace("https://",""));

                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword(username, password)
                            //.setHost(serverIp)
                            .setHost(preferenceManager.getBaseURL().replace("https://",""))
                            .setXmppDomain(serviceName)
                            .setPort(5222).setSecurityMode(ConnectionConfiguration.SecurityMode.disabled).allowEmptyOrNullUsernames().setResource("Android")
                            .build();

                    asbtractConnection = new XMPPTCPConnection(config);
                    asbtractConnection.addConnectionListener(Settings.this);
                    asbtractConnection.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    @Override
    public void connected(XMPPConnection connection) {

        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Connected", Toast.LENGTH_LONG).show();
                }
            });


            asbtractConnection.login(Localpart.from("" + username), "" + password);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Toast.makeText(getActivity(), "Successfully Authenticated", Toast.LENGTH_LONG).show();
    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectionClosedOnError(Exception e) {
    }

    @Override
    public void reconnectionSuccessful() {
    }

    @Override
    public void reconnectingIn(int seconds) {
    }

    @Override
    public void reconnectionFailed(Exception e) {
    }


    public String encryption_old(String strNormalText) throws Exception {
        String seedValue = "YourSecKey";
        String normalTextEnc = "";
        try {
            normalTextEnc = AESHelper.encrypt2(seedValue, strNormalText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toHex(normalTextEnc);
    }
    public String encryption1(String strNormalText) throws Exception {
        String seedValue = "YourSecKey";
        String normalTextEnc = "";
        try {
            normalTextEnc = AESHelper.encrypt(seedValue, strNormalText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return normalTextEnc;
    }

    public String decryption1(String strEncryptedText) throws Exception {
        String seedValue = "YourSecKey";
        String strDecryptedText ="";
        try {
            strDecryptedText = AESHelper.decrypt(seedValue, strDecryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
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

    public String decryption_old(String strEncryptedText) throws Exception {
        String seedValue = "YourSecKey";
        String strDecryptedText = hextoString(strEncryptedText);
        try {
            strDecryptedText = AESHelper.decrypt2(seedValue, strDecryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }

    public void callConfigDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater lf = (LayoutInflater) (getActivity())
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.config_dialog, null);
        TextView title = (TextView) dialogview.findViewById(R.id.title);
        title.setText("Please Enter Your ConfigId");
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
                .findViewById(R.id.dialogCancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                dialog.dismiss();

            }
        });

        TextView retry = (TextView) dialogview
                .findViewById(R.id.dialogRetry);
        retry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!body.getText().toString().equals("")) {
                    callGetBranchDetails_newwithconfig(body.getText().toString());
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please enter config id.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    public void callGetBranchDetails_old() {

        openProgressDialog();
        try {

            hashMapKeys.clear();
            hashMapKeys.put("terminalId", encryption_old(edt_terminal_id.getText().toString()));
//            hashMapKeys.put("terminalId", edt_terminal_id.getText().toString());
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

//            new OkHttpHandler(getActivity(), this, null, "GetBranchDetailsOld").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL5 + AppConstants.GET_TERMINAL_CONFIG
//                    + "?terminal_id=" + encryption_old(edt_terminal_id.getText().toString()));//encryption("47f17c5fe8d43843"));

            new OkHttpHandler(getActivity(), this, hashMap, "GetBranchDetailsOld").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.GET_TERMINAL_CONFIG);//encryption("47f17c5fe8d43843"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callGetBranchDetails_new() {

        openProgressDialog();
        try {
            hashMapKeys.clear();
            hashMapKeys.put("terminalId", encryption(edt_terminal_id.getText().toString()));
//            hashMapKeys.put("terminalId", edt_terminal_id.getText().toString());
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
//            new OkHttpHandler(getActivity(), this, null, "GetBranchDetailsNew").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL5 + AppConstants.GET_TERMINAL_CONFIG
//                    + "?terminal_id=" + encryption(edt_terminal_id.getText().toString()));//encryption("47f17c5fe8d43843"));

            new OkHttpHandler(getActivity(), this, hashMap, "GetBranchDetailsNew").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.GET_TERMINAL_CONFIG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callGetBranchDetails_newwithconfig(String configId) {
        openProgressDialog();
        try {

            hashMapKeys.clear();
            hashMapKeys.put("terminalId", encryption(edt_terminal_id.getText().toString()));
//            hashMapKeys.put("terminalId", edt_terminal_id.getText().toString());
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("configId", encryption(preferenceManager.getConfigId()));
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

            new OkHttpHandler(getActivity(), this, hashMap, "GetBranchDetailsNew").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.GET_TERMINAL_CONFIG);

//            new OkHttpHandler(getActivity(), this, hashMap, "GetBranchDetailsNew").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL5 + AppConstants.GET_TERMINAL_CONFIG
//                    + "?terminal_id=" + encryption(edt_terminal_id.getText().toString()) + "&configId=" + encryption(configId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callUpdateBranchDetailsNew() throws Exception {

        openProgressDialog();

        try {

            JSONObject jsonObject = new JSONObject();
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


            jsonObject.put("CentrapaySelected", preferenceManager.isCentrapayMerchantQRDisplaySelected());
            jsonObject.put("CentrapayFeeValue", preferenceManager.getcnv_centrapay());
            jsonObject.put("CnvCentrapayDisplayAndAdd", preferenceManager.is_cnv_centrapay_display_and_add());
            jsonObject.put("CnvCentrapayDisplayOnly", preferenceManager.is_cnv_centrapay_display_only());

            jsonObject.put("PoliSelected", preferenceManager.isPoliSelected());
            jsonObject.put("PoliFeeValue", preferenceManager.getcnv_poli());
            jsonObject.put("CnvPoliDisplayAndAdd", preferenceManager.is_cnv_poli_display_and_add());
            jsonObject.put("CnvPoliDisplayOnly", preferenceManager.is_cnv_poli_display_only());

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
            jsonObject.put("isDisplayLoyaltyApps",preferenceManager.isDisplayLoyaltyApps());
            jsonObject.put("isExternalInputDevice",preferenceManager.isExternalScan());
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
            jsonObject.put("CnvUPIQrMPMCloudValueHigher",preferenceManager.getCnv_up_upiqr_mpmcloud_higher());
            jsonObject.put("CnvUPIQRMPMCloudAmount",preferenceManager.getCnv_up_upiqr_mpmcloud_amount());
            jsonObject.put("isMerchantDPARDisplay",preferenceManager.isMerchantDPARDisplay());
            jsonObject.put("cnv_unimerchantqrdisplay", preferenceManager.get_cnv_unimerchantqrdisplayLower());
            jsonObject.put("cnv_unimerchantqrdisplay_higher",preferenceManager.get_cnv_unimerchantqrdisplayHigher());
            hashMapKeys.clear();
            hashMapKeys.put("branchAddress", preferenceManager.getaddress().equals("") ? encryption("nodata") : encryption(preferenceManager.getaddress()));
            hashMapKeys.put("branchContactNo", preferenceManager.getcontact_no().equals("") ? encryption("nodata") : encryption(preferenceManager.getcontact_no()));
            hashMapKeys.put("branchName", preferenceManager.getmerchant_name().equals("") ? encryption("nodata") : encryption(preferenceManager.getmerchant_name()));
            hashMapKeys.put("branchEmail", preferenceManager.getcontact_email().equals("") ? "nodata" : encryption(preferenceManager.getcontact_email()));
            hashMapKeys.put("gstNo", preferenceManager.getgstno().equals("") ? encryption("nodata") : encryption(preferenceManager.getgstno()));
            hashMapKeys.put("terminalId", encryption(preferenceManager.getterminalId()));
            hashMapKeys.put("otherData", encryption(jsonObject.toString()));//encryption(jsonObject.toString()));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("accessId", encryption(preferenceManager.getuniqueId()));
            hashMapKeys.put("configId", encryption(preferenceManager.getConfigId()));
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
            new OkHttpHandler(getActivity(), this, hashMap, "UpdateBranchDetailsNew")
                    .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.SAVE_TERMINAL_CONFIG);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void _oldUser(JSONObject jsonObject) {
        try {
            if (jsonObject.optString("success").equals("true")) {
                preferenceManager.setaddress(decryption_old(jsonObject.optString("branchAddress")).equals("nodata") ? "" : decryption_old(jsonObject.optString("branchAddress")));
                preferenceManager.setcontact_email(jsonObject.optString("branchEmail").equals("nodata") ? "" : jsonObject.optString("branchEmail"));
                preferenceManager.setcontact_no(decryption_old(jsonObject.optString("branchContactNo")).equals("nodata") ? "" : decryption_old(jsonObject.optString("branchContactNo")));
                preferenceManager.setmerchant_name(decryption_old(jsonObject.optString("branchName")).equals("nodata") ? "" : decryption_old(jsonObject.optString("branchName")));
                preferenceManager.setgstno(decryption_old(jsonObject.optString("gstNo")).equals("nodata") ? "" : decryption_old(jsonObject.optString("gstNo")));
                preferenceManager.setterminalId(decryption_old(jsonObject.optString("terminalId")));
                edt_unique_id.setText(decryption_old(jsonObject.optString("accessId")));
                jsonObject1 = new JSONObject(decryption_old(jsonObject.optString("otherData")));
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
                    preferenceManager.set_cnv_unimerchantqrdisplayLower(jsonObject1.optString("cnv_unimerchantqrdisplay"));
                    preferenceManager.set_cnv_unimerchantqrdisplayHigher(jsonObject1.optString("cnv_unimerchantqrdisplay_higher"));
                }


            } else {
                // Toast.makeText(getActivity(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
            }

            callDeleteTerminalOld();


        } catch (Exception e) {

        }
    }

    String config_id = "";

    public void _NewUser(JSONObject jsonObject) throws Exception {

        if (jsonObject.has("configId")) {
            config_id = decryption(jsonObject.optString("configId"));
            if (config_id.equals("")) {
                Toast.makeText(getActivity(), "ConfigID not found", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        try {
            if (jsonObject.optString("success").equals("true")) {
                preferenceManager.setaddress(decryption(jsonObject.optString("branchAddress")).equals("nodata") ? "" : decryption(jsonObject.optString("branchAddress")));
                if (jsonObject.optString("branchEmail").equals("nodata")) {
                    preferenceManager.setcontact_email("");
                } else {
                    preferenceManager.setcontact_email(decryption(jsonObject.optString("branchEmail")).equals("nodata") ? "" : decryption(jsonObject.optString("branchEmail")));
                }
                preferenceManager.setcontact_no(decryption(jsonObject.optString("branchContactNo")).equals("nodata") ? "" : decryption(jsonObject.optString("branchContactNo")));
                preferenceManager.setmerchant_name(decryption(jsonObject.optString("branchName")).equals("nodata") ? "" : decryption(jsonObject.optString("branchName")));
                preferenceManager.setgstno(decryption(jsonObject.optString("gstNo")).equals("nodata") ? "" : decryption(jsonObject.optString("gstNo")));
                preferenceManager.setterminalId(decryption(jsonObject.optString("terminalId")));

                if (preferenceManager.isResetTerminal()) {
                    edt_unique_id.setText("");
                    preferenceManager.setuniqueId("");
                } else {
                    if (!preferenceManager.getuniqueId().equals("")) {
                        edt_unique_id.setText(preferenceManager.getuniqueId());
                    } else {
                        edt_unique_id.setText(decryption(jsonObject.optString("accessId")));
                        preferenceManager.setuniqueId(decryption(jsonObject.optString("accessId")));
                    }

                }


                if (jsonObject.has("configId")) {
                    preferenceManager.setConfigId(decryption(jsonObject.optString("configId")));
                }

                if (jsonObject.has("merchant_id")) {
                    preferenceManager.setMerchantId(decryption(jsonObject.optString("merchant_id")));
                }


                jsonObject1 = new JSONObject(decryption(jsonObject.optString("otherData")));
//                jsonObject1 = new JSONObject(hextoString(jsonObject.optString("otherData")));
                if (jsonObject.has("otherData")) {
                    if (preferenceManager.isResetTerminal()) {
                        edt_unique_id.setText("");
                        preferenceManager.setuniqueId("");
                    } else {
                        if (!preferenceManager.getuniqueId().equals("")) {
                            edt_unique_id.setText(preferenceManager.getuniqueId());
                        } else {
                            edt_unique_id.setText(jsonObject1.optString("accessId"));
                            preferenceManager.setuniqueId(jsonObject1.optString("accessId"));
                        }

                    }

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
                    preferenceManager.set_cnv_unimerchantqrdisplayLower(jsonObject1.optString("cnv_unimerchantqrdisplay"));
                    preferenceManager.set_cnv_unimerchantqrdisplayHigher(jsonObject1.optString("cnv_unimerchantqrdisplay_higher"));
                    preferenceManager.setisMerchantDPARDisplay(jsonObject1.optBoolean("isMerchantDPARDisplay"));

                    if (jsonObject1.has("ConfigId")) {
                        preferenceManager.setConfigId(jsonObject1.optString("ConfigId"));
                    }

                    if (jsonObject1.has("MerchantId")) {
                        preferenceManager.setMerchantId(jsonObject1.optString("MerchantId"));
                    }
                }
            } else {
                if (jsonObject.has("configId")) {
                    preferenceManager.setConfigId(decryption(jsonObject.optString("configId")));
                }

                if (jsonObject.has("merchant_id")) {
                    preferenceManager.setMerchantId(decryption(jsonObject.optString("merchant_id")));
                }

                if (jsonObject.has("terminalId")) {
                    preferenceManager.setterminalId(decryption(jsonObject.optString("terminalId")));
                }
//                if (jsonObject.has("accessId")) {
//                    preferenceManager.setuniqueId(decryption(jsonObject.optString("accessId")));
//                }
            }
        } catch (Exception e) {
            if (jsonObject.has("configId")) {
                preferenceManager.setConfigId(decryption(jsonObject.optString("configId")));
            }

            if (jsonObject.has("config_id")) {
                preferenceManager.setConfigId(decryption(jsonObject.optString("config_id")));
            }

            if (jsonObject.has("merchant_id")) {
                preferenceManager.setMerchantId(decryption(jsonObject.optString("merchant_id")));
            }

            if (jsonObject.has("terminalId")) {
                preferenceManager.setterminalId(decryption(jsonObject.optString("terminalId")));
            }

            if (jsonObject.has("terminal_id")) {
                preferenceManager.setterminalId(decryption(jsonObject.optString("terminal_id")));
            }

            if (jsonObject.has("branch_id")) {
                preferenceManager.setMerchantId(decryption(jsonObject.optString("branch_id")));
            }

            if (jsonObject.has("branchId")) {
                preferenceManager.setMerchantId(decryption(jsonObject.optString("branchId")));
            }

//            if (jsonObject.has("accessId")) {
//                preferenceManager.setuniqueId(decryption(jsonObject.optString("accessId")));
//            }
        }
    }


    JSONObject jsonObject1 = null, UpdateBranchDetailsNewJsonObject = null;
    boolean isUpdateNewDetails = false;

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
        if (progress != null && progress.isShowing())
            progress.dismiss();
        if (result.equals("")) {
            Toast.makeText(getActivity(), "No data from server.", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {

//            case "GetBranchDetails":
//                _oldUser_(jsonObject);
//                break;


            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferenceManager.setauthToken(jsonObject.optString("access_token"));
                }
                if (isGetBranchDetailsCalled) {
                    isGetBranchDetailsCalled = false;
                    callGetBranchDetails_new();
                }
                if (isSaveAndOK) {
                    isSaveAndOK = false;
                    callValidateTerminal();
                }
                if (isRegisteredStart) {
                    isRegisteredStart = false;
                    callValidateTerminal();
                }

                if (isUpdateNewDetails) {
                    isUpdateNewDetails = false;
                    callUpdateBranchDetailsNew();
                }
                break;


            case "UpdateBranchDetailsNew":
                if (!jsonObject.optBoolean("success")) {
                    // Toast.makeText(getActivity(), "Failed to update terminal configuration."+jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    return;
                }
                UpdateBranchDetailsNewJsonObject = jsonObject;
                if (AppConstants.isRegistered) {
                    preferenceManager.setaggregated_singleqr(true);
                    if (edt_terminal_id.getText().toString().equals("") && edt_unique_id.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Please enter Terminal Id and Access Id", Toast.LENGTH_LONG).show();
                    } else if (edt_terminal_id.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Please enter Terminal Id", Toast.LENGTH_LONG).show();
                    } else if (edt_unique_id.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Please enter Access Id", Toast.LENGTH_LONG).show();
                    } else {
                        callValidateTerminal();
                    }
                } else {
                    _NewUser(UpdateBranchDetailsNewJsonObject);
                    // callGetBranchDetails_new();
                }

                break;
            case "UpdateBranchDetails":
                //added on 20th may 2019
                if (AppConstants.isRegistered) {
                    preferenceManager.setaggregated_singleqr(true);
                    if (edt_terminal_id.getText().toString().equals("") && edt_unique_id.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Please enter Terminal Id and Access Id", Toast.LENGTH_LONG).show();
                    } else if (edt_terminal_id.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Please enter Terminal Id", Toast.LENGTH_LONG).show();
                    } else if (edt_unique_id.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Please enter Access Id", Toast.LENGTH_LONG).show();
                    } else {
                        callValidateTerminal();
                    }
                }
                break;


            case "DeleteTerminal":
                callAuthToken();
                if (jsonObject.optBoolean("success")) {
                    preferenceManager.clearPreferences();
                    Toast.makeText(getActivity(), "Terminal Config Deleted Successfully."+jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    // ((MyPOSMateApplication) getActivity().getApplicationContext()).asbtractConnection.disconnect();

                    preferenceManager.setisResetTerminal(true);
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.SETTINGS, null);
                } else {
                    Toast.makeText(getActivity(), "Terminal configuration not found on server.App data cleared successfully", Toast.LENGTH_SHORT).show();
                    preferenceManager.clearPreferences();
                    preferenceManager.setisResetTerminal(true);
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.SETTINGS, null);
                }

                break;

            case "DeleteTerminalOld":
                isUpdateNewDetails = true;
                callAuthToken();
                break;

            case "GetBranchDetailsOld":
//                isUpdateNewDetails = true;
//                callAuthToken();
                _oldUser(jsonObject);
                break;

            case "GetBranchDetailsNew":
                if (jsonObject.optBoolean("success")) {
                    if (jsonObject.has("multi_terminals")) {
                        if (jsonObject.optBoolean("multi_terminals")) {
                            AppConstants.configIdMatch = decryption(jsonObject.optString("configId"));
                            callConfigDialog();
                        } else
                            _NewUser(jsonObject);
                    } else
                        _NewUser(jsonObject);

                    // _NewUser(UpdateBranchDetailsNewJsonObject);
                    isUpdateNewDetails = true;
                    callAuthToken();
                } else {
                    callAuthToken();
                    Toast.makeText(getActivity(), "Terminal Configuration not found", Toast.LENGTH_SHORT).show();
                }

                break;

            case "validateTerminal":
                DashboardActivity.isLaunch = true;
                callAuthToken();
                AppConstants.isRegistered = false;
//                preferenceManager.setuniqueId("");

                if (jsonObject.has("branchInfo")) {
                    if (jsonObject.optJSONObject("branchInfo").has("company")) {
                        preferenceManager.setMerchantName(jsonObject.optJSONObject("branchInfo").optString("company"));
                        preferenceManager.setbranchName(jsonObject.optJSONObject("branchInfo").optString("branchName"));
//                        preferenceManager.setTerminalIdentifier(jsonObject.optJSONObject("terminal").optString("terminalTag"));
//                        preferenceManager.setPOSIdentifier(jsonObject.optJSONObject("terminal").optString("posTag"));
//                        preferenceManager.setLaneIdentifier(jsonObject.optJSONObject("terminal").optString("laneTag"));
                    }

                }
                if (jsonObject.optBoolean("status")) {
                    preferenceManager.setcurrency(jsonObject.optString("currency"));
                    preferenceManager.setisRegistered(true);
                    ((DashboardActivity) getActivity()).img_menu.setEnabled(true);
                    username = jsonObject.optString("terminal_xmpp_jid").toString();
                    password = jsonObject.optString("terminal_xmpp_password").toString();
                    preferenceManager.setterminalId(edt_terminal_id.getText().toString());
                    preferenceManager.setterminalIp(edt_terminal_ip.getText().toString());
                    preferenceManager.setuniqueId(edt_unique_id.getText().toString());
                    preferenceManager.setUsername(username);
                    preferenceManager.setPassword(password);
                    preferenceManager.setterminal_refund_password(jsonObject.optString("terminal_refund_password").toString());
                    preferenceManager.setIsAuthenticated(false);
                    preferenceManager.setIsConnected(false);
                    preferenceManager.setisUnipaySelected(true);
                    preferenceManager.setisUnionPaySelected(true);
                    preferenceManager.setisUplanSelected(false);
                    preferenceManager.setisAlipaySelected(true);
                    preferenceManager.setisWechatSelected(true);
                    preferenceManager.setisUnionPayQrCodeDisplaySelected(true);
                    preferenceManager.setIsHome(false);
                    preferenceManager.setIsBack(false);
                    preferenceManager.setIsFront(false);
                    preferenceManager.setisQR(false);
                    preferenceManager.setisStaticQR(false);
                    preferenceManager.setcurrency(jsonObject.optString("currency"));
                    preferenceManager.setisPrint("true");
                    preferenceManager.setshowReference("false");
                    preferenceManager.setisMembershipHome(false);
                    preferenceManager.setisMembershipManual(false);
                    preferenceManager.setisLoyality(false);
                    preferenceManager.setBranchEmail("false");
                    preferenceManager.setBranchPhoneNo("false");
                    preferenceManager.setBranchAddress("false");
                    preferenceManager.setBranchName("false");
                    preferenceManager.setGSTNo("false");
                    preferenceManager.setIsManual(true);
                    preferenceManager.setmerchant_info(jsonObject.optString("merchant_info"));
//                    preferenceManager.setMerchantId("");
//                    preferenceManager.setConfigId("");

                    preferenceManager.setisTimeZoneChecked(true);
                    preferenceManager.setTimeZone("New Zealand Standard Time (Pacific/Auckland)");
                    preferenceManager.setTimeZoneId("Pacific/Auckland");


                    if (preferenceManager.isResetTerminal()) {
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                        return;
                    }


                    if (jsonObject1 != null) {
                        if (!jsonObject1.optBoolean("UnionPay") &&
                                !jsonObject1.optBoolean("UnionPayQR") &&
                                !jsonObject1.optBoolean("isUnionPayQrCodeDisplaySelected") &&
                                jsonObject1.optString("UnionPayQrValue").equals("") &&
                                jsonObject1.optString("UplanValue").equals("") &&
                                !jsonObject1.optBoolean("CnvAlipayDisplayAndAdd") &&
                                !jsonObject1.optBoolean("CnvAlipayDisplayOnly") &&
                                !jsonObject1.optBoolean("CnvWeChatDisplayOnly") &&
                                !jsonObject1.optBoolean("CnvWeChatDisplayAndAdd") &&
                                !jsonObject1.optBoolean("CnvUnionpayDisplayAndAdd") &&
                                !jsonObject1.optBoolean("CnvUnionpayDisplayOnly") &&
                                !jsonObject1.optBoolean("Uplan") &&
                                !jsonObject1.optBoolean("AlipayWeChatPay") &&
                                !jsonObject1.optBoolean("AlipayWeChatScanQR") &&
                                jsonObject1.optString("PrintReceiptautomatically").equals("false") &&
                                jsonObject1.optString("ShowReference").equals("") &&
                                !jsonObject1.optBoolean("ShowPrintQR") &&
                                !jsonObject1.optBoolean("ShowMembershipManual") &&
                                !jsonObject1.optBoolean("ShowMembershipHome") &&
                                !jsonObject1.optBoolean("Membership/Loyality") &&
                                !jsonObject1.optBoolean("Home") &&
                                !jsonObject1.optBoolean("ManualEntry") &&
                                !jsonObject1.optBoolean("Back") &&
                                !jsonObject1.optBoolean("Front") &&
                                !jsonObject1.optBoolean("ConvenienceFee") &&
                                !jsonObject1.optBoolean("CnvUPIQrMPMCloudDAADD") &&
                                !jsonObject1.optBoolean("CnvUPIQrMPMCloudDOnly") &&
                                !jsonObject1.optBoolean("isMerchantDPARDisplay")&&
                                jsonObject1.optString("CnvUPIQrMPMCloudValue").equals("") &&
                                jsonObject1.optString("cnv_unimerchantqrdisplay").equals("")&&
                                jsonObject1.optString("AlipayWechatvalue").equals("") &&
                                jsonObject1.optString("UnionPayvalue").equals("") &&
                                jsonObject1.optString("EnableBranchName").equals("") &&
                                jsonObject1.optString("EnableBranchAddress").equals("") &&
                                jsonObject1.optString("EnableBranchEmail").equals("") &&
                                jsonObject1.optString("EnableBranchContactNo").equals("") &&
                                jsonObject1.optString("EnableBranchGSTNo").equals("") &&
                                jsonObject1.optString("TerminalIdentifier").equals("") &&
                                jsonObject1.optString("POSIdentifier").equals("") &&
                                jsonObject1.optString("LaneIdentifier").equals("") &&
                                !jsonObject1.optBoolean("isLaneIdentifier") &&
                                !jsonObject1.optBoolean("isPOSIdentifier") &&
                                !jsonObject1.optBoolean("isTerminalIdentifier")) {
                            ((DashboardActivity) getActivity()).img_menu.setEnabled(true);
                            username = jsonObject.optString("terminal_xmpp_jid").toString();
                            password = jsonObject.optString("terminal_xmpp_password").toString();
                            preferenceManager.setterminalId(edt_terminal_id.getText().toString());
                            preferenceManager.setterminalIp(edt_terminal_ip.getText().toString());
                            preferenceManager.setuniqueId(edt_unique_id.getText().toString());
                            preferenceManager.setUsername(username);
                            preferenceManager.setPassword(password);
                            preferenceManager.setterminal_refund_password(jsonObject.optString("terminal_refund_password").toString());
                            preferenceManager.setIsAuthenticated(false);
                            preferenceManager.setIsConnected(false);
                            preferenceManager.setisDisplayAds(true);
                            preferenceManager.setdisplayAdsTime("5");
                            preferenceManager.setisUnipaySelected(true);
                            preferenceManager.setisUnionPaySelected(true);
                            preferenceManager.setisUplanSelected(false);
                            preferenceManager.setIsHome(false);
                            preferenceManager.setIsBack(false);
                            preferenceManager.setIsFront(false);
                            preferenceManager.setisQR(false);
                            preferenceManager.setisStaticQR(false);
                            preferenceManager.setcurrency(jsonObject.optString("currency"));
                            preferenceManager.setisPrint("true");
                            preferenceManager.setshowReference("false");
                            preferenceManager.setisMembershipHome(false);
                            preferenceManager.setisMembershipManual(false);
                            preferenceManager.setisLoyality(false);
                            preferenceManager.setBranchEmail("false");
                            preferenceManager.setBranchPhoneNo("false");
                            preferenceManager.setBranchAddress("false");
                            preferenceManager.setBranchName("false");
                            preferenceManager.setGSTNo("false");
                            preferenceManager.setIsManual(true);
                            preferenceManager.setmerchant_info(jsonObject.optString("merchant_info"));
                            preferenceManager.setisTimeZoneChecked(true);
                            preferenceManager.setTimeZone("New Zealand Standard Time (Pacific/Auckland)");
                            preferenceManager.setTimeZoneId("Pacific/Auckland");
                            preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(false);
                            preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(false);
                            preferenceManager.setcnv_up_upiqr_mpmcloud_lower("");

                            if (jsonObject1.has("MerchantId") && !jsonObject1.optString("MerchantId").equals(""))
                                preferenceManager.setMerchantId(jsonObject1.optString("MerchantId"));
                            if (jsonObject1.has("ConfigId") && !jsonObject1.optString("ConfigId").equals(""))
                                preferenceManager.setConfigId(jsonObject1.optString("ConfigId"));
                            ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                            return;
                        }


                    }


                    if (jsonObject1 != null) {
                        preferenceManager.setisUnionPaySelected(jsonObject1.optBoolean("UnionPay"));
                        if (jsonObject1.has("MerchantId") && !jsonObject1.optString("MerchantId").equals(""))
                            preferenceManager.setMerchantId(jsonObject1.optString("MerchantId"));
                        if (jsonObject1.has("ConfigId") && !jsonObject1.optString("ConfigId").equals(""))
                            preferenceManager.setConfigId(jsonObject1.optString("ConfigId"));

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
                        preferenceManager.setUnionPayQrSelected(jsonObject1.optBoolean("UnionPayQR"));
                        preferenceManager.setisUnionPayQrCodeDisplaySelected(jsonObject1.optBoolean("isUnionPayQrCodeDisplaySelected"));
                        preferenceManager.setcnv_uniqr(jsonObject1.optString("UnionPayQrValue"));
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
                        preferenceManager.setBranchName(jsonObject1.optString("EnableBranchName"));
                        preferenceManager.setBranchAddress(jsonObject1.optString("EnableBranchAddress"));
                        preferenceManager.setBranchEmail(jsonObject1.optString("EnableBranchEmail"));
                        preferenceManager.setBranchPhoneNo(jsonObject1.optString("EnableBranchContactNo"));
                        preferenceManager.setGSTNo(jsonObject1.optString("EnableBranchGSTNo"));

                        if (jsonObject1.has("TimeZoneId")) {
                            if (jsonObject1.optString("TimeZoneId").equals("")) {
                                preferenceManager.setisTimeZoneChecked(true);
                                preferenceManager.setTimeZone("New Zealand Standard Time (Pacific/Auckland)");
                                preferenceManager.setTimeZoneId("Pacific/Auckland");
                            } else {
                                preferenceManager.setTimeZoneId(jsonObject1.optString("TimeZoneId"));
                                preferenceManager.setTimeZone(jsonObject1.optString("TimeZone"));
                                preferenceManager.setisTimeZoneChecked(jsonObject1.optBoolean("isTimeZoneChecked"));
                            }
                        } else {
                            preferenceManager.setisTimeZoneChecked(true);
                            preferenceManager.setTimeZone("New Zealand Standard Time (Pacific/Auckland)");
                            preferenceManager.setTimeZoneId("Pacific/Auckland");
                        }


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
                        preferenceManager.set_cnv_unimerchantqrdisplayLower(jsonObject1.optString("cnv_unimerchantqrdisplay"));
                        preferenceManager.set_cnv_unimerchantqrdisplayHigher(jsonObject1.optString("cnv_unimerchantqrdisplay_higher"));
                        preferenceManager.setisMerchantDPARDisplay(jsonObject1.optBoolean("isMerchantDPARDisplay"));
                    }
                    ((DashboardActivity) getActivity()).img_menu.setEnabled(true);
                    //  initChat(jsonObject.optString("terminal_xmpp_jid").toString(),jsonObject.optString("terminal_xmpp_password").toString());
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                } else {
                    preferenceManager.setuniqueId("");
                    preferenceManager.clearPreferences();
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.SETTINGS,null);
                    ((DashboardActivity) getActivity()).img_menu.setEnabled(false);
                    Toast.makeText(getActivity(), jsonObject.optString("message"), Toast.LENGTH_LONG).show();
                  }

                if (preferenceManager.is_cnv_alipay_display_and_add() ||
                        preferenceManager.is_cnv_alipay_display_only()) {
                    preferenceManager.setisAlipaySelected(true);
                }


                break;

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((DashboardActivity) getActivity()).rel_orders.setVisibility(View.VISIBLE);
    }

}





