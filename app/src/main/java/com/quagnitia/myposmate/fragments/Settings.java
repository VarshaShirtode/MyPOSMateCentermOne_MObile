package com.quagnitia.myposmate.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
    private PreferencesManager preferencesManager;
    private EditText edt_terminal_id, edt_terminal_ip, edt_unique_id;
    private AbstractXMPPConnection asbtractConnection;
    private String serverIp = AppConstants.serverIp;
    private String username, password;
    private Handler handler;
    TreeMap<String, String> hashMapKeys;
    public boolean isRegisteredStart = false;

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
        preferencesManager = PreferencesManager.getInstance(getActivity());

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
        edt_terminal_id.setText(android_id);
        edt_terminal_ip.setText(getLocalIpAddress());
//        preferencesManager.setuniqueId("eeac599d06a42e9b");
//        preferencesManager.setMerchantId("29");
//        preferencesManager.setConfigId("60");
        edt_unique_id.setText(preferencesManager.getuniqueId());

        if (preferencesManager.getuniqueId().equals("")) {
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
            edt_unique_id.setText(preferencesManager.getuniqueId());
            preferencesManager.setaggregated_singleqr(true);
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
        hashMapKeys.put("branch_id", preferencesManager.getMerchantId());
        hashMapKeys.put("terminal_id", edt_terminal_id.getText().toString());
        hashMapKeys.put("access_id", edt_unique_id.getText().toString());
        hashMapKeys.put("config_id", preferencesManager.getConfigId());
        hashMapKeys.put("random_str", new Date().getTime() + "");
//        hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
//        hashMapKeys.put("access_token",preferencesManager.getauthToken());
        preferencesManager.setuniqueId(edt_unique_id.getText().toString());
        new OkHttpHandler(getActivity(), this, null, "validateTerminal")
                .execute(AppConstants.BASE_URL2 + AppConstants.VALIDATE_TERMINAL + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferencesManager.getauthToken());

//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.putAll(hashMapKeys);
//        new OkHttpHandler(getActivity(), this, hashMap, "validateTerminal")
//                .execute(AppConstants.BASE_URL2 + AppConstants.VALIDATE_TERMINAL);


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
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.AUTH);

    }


    public void callDeleteTerminal() {
        openProgressDialog();
        try {


            hashMapKeys.clear();
            hashMapKeys.put("terminalId", encryption(edt_terminal_id.getText().toString()));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferencesManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

            new OkHttpHandler(getActivity(), this, hashMap, "DeleteTerminal").execute(AppConstants.BASE_URL2 + AppConstants.DELETE_TERMINAL_CONFIG);

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
            hashMapKeys.put("access_token", preferencesManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

//            new OkHttpHandler(getActivity(), this, null, "DeleteTerminalOld").execute(AppConstants.BASE_URL3 + AppConstants.DELETE_TERMINAL_CONFIG
//                    + "?terminal_id=" + encryption_old(edt_terminal_id.getText().toString()));

            new OkHttpHandler(getActivity(), this, hashMap, "DeleteTerminalOld").execute(AppConstants.BASE_URL2 + AppConstants.DELETE_TERMINAL_CONFIG);

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
                    Toast.makeText(getActivity(), "Config ID not present", Toast.LENGTH_SHORT).show();
                    return;
                }

                preferencesManager.setaggregated_singleqr(true);
                if (edt_terminal_id.getText().toString().equals("") && edt_unique_id.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter Terminal Id and Access Id", Toast.LENGTH_LONG).show();
                } else if (edt_terminal_id.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter Terminal Id", Toast.LENGTH_LONG).show();
                } else if (edt_unique_id.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter Access Id", Toast.LENGTH_LONG).show();
                } else {
                    preferencesManager.setterminalId(edt_terminal_id.getText().toString());
                    preferencesManager.setuniqueId(edt_unique_id.getText().toString());
                    isSaveAndOK = true;
                    callAuthToken();

                }

                break;
            case R.id.btn_cancel:
                if (preferencesManager.isHome()) {
                    if (!preferencesManager.getUsername().equals(""))
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                    else
                        edt_unique_id.setText("");

                } else {
                    if (!preferencesManager.getUsername().equals(""))
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
                    DomainBareJid serviceName = JidCreate.domainBareFrom(serverIp);

                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword(username, password)
                            .setHost(serverIp)
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
            hashMapKeys.put("access_token", preferencesManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

//            new OkHttpHandler(getActivity(), this, null, "GetBranchDetailsOld").execute(AppConstants.BASE_URL3 + AppConstants.GET_TERMINAL_CONFIG
//                    + "?terminal_id=" + encryption_old(edt_terminal_id.getText().toString()));//encryption("47f17c5fe8d43843"));

            new OkHttpHandler(getActivity(), this, hashMap, "GetBranchDetailsOld").execute(AppConstants.BASE_URL2 + AppConstants.GET_TERMINAL_CONFIG);//encryption("47f17c5fe8d43843"));

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
            hashMapKeys.put("access_token", preferencesManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
//            new OkHttpHandler(getActivity(), this, null, "GetBranchDetailsNew").execute(AppConstants.BASE_URL3 + AppConstants.GET_TERMINAL_CONFIG
//                    + "?terminal_id=" + encryption(edt_terminal_id.getText().toString()));//encryption("47f17c5fe8d43843"));

            new OkHttpHandler(getActivity(), this, hashMap, "GetBranchDetailsNew").execute(AppConstants.BASE_URL2 + AppConstants.GET_TERMINAL_CONFIG);

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
            hashMapKeys.put("configId", encryption(preferencesManager.getConfigId()));
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferencesManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);

            new OkHttpHandler(getActivity(), this, hashMap, "GetBranchDetailsNew").execute(AppConstants.BASE_URL2 + AppConstants.GET_TERMINAL_CONFIG);

//            new OkHttpHandler(getActivity(), this, hashMap, "GetBranchDetailsNew").execute(AppConstants.BASE_URL3 + AppConstants.GET_TERMINAL_CONFIG
//                    + "?terminal_id=" + encryption(edt_terminal_id.getText().toString()) + "&configId=" + encryption(configId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callUpdateBranchDetailsNew() throws Exception {

        openProgressDialog();

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("accessId", preferencesManager.getuniqueId());
            jsonObject.put("AlipaySelected", preferencesManager.isAlipaySelected());
            jsonObject.put("AlipayValue", preferencesManager.getcnv_alipay());
            jsonObject.put("CnvAlipayDisplayAndAdd", preferencesManager.is_cnv_alipay_display_and_add());
            jsonObject.put("CnvAlipayDisplayOnly", preferencesManager.is_cnv_alipay_display_only());
            jsonObject.put("WeChatSelected", preferencesManager.isWechatSelected());
            jsonObject.put("WeChatValue", preferencesManager.getcnv_wechat());
            jsonObject.put("CnvWeChatDisplayAndAdd", preferencesManager.is_cnv_wechat_display_and_add());
            jsonObject.put("CnvWeChatDisplayOnly", preferencesManager.is_cnv_wechat_display_only());
            jsonObject.put("AlipayScanQR", preferencesManager.isAlipayScan());
            jsonObject.put("WeChatScanQR", preferencesManager.isWeChatScan());
            jsonObject.put("MerchantId", preferencesManager.getMerchantId());
            jsonObject.put("ConfigId", preferencesManager.getConfigId());
            jsonObject.put("UnionPay", preferencesManager.isUnionPaySelected());
            jsonObject.put("UnionPayQR", preferencesManager.isUnionPayQrSelected());
            jsonObject.put("isUnionPayQrCodeDisplaySelected", preferencesManager.isUnionPayQrCodeDisplaySelected());
            jsonObject.put("UnionPayQrValue", preferencesManager.getcnv_uniqr());
            jsonObject.put("UplanValue", preferencesManager.getcnv_uplan());
            jsonObject.put("CnvUnionpayDisplayAndAdd", preferencesManager.is_cnv_uni_display_and_add());
            jsonObject.put("CnvUnionpayDisplayOnly", preferencesManager.is_cnv_uni_display_only());
            jsonObject.put("Uplan", preferencesManager.isUplanSelected());
            jsonObject.put("AlipayWeChatPay", preferencesManager.isaggregated_singleqr());
            jsonObject.put("AlipayWeChatScanQR", preferencesManager.isAlipayWechatQrSelected());
            jsonObject.put("PrintReceiptautomatically", preferencesManager.getisPrint());
            jsonObject.put("ShowReference", preferencesManager.getshowReference());
            jsonObject.put("ShowPrintQR", preferencesManager.isQR());
            jsonObject.put("DisplayStaticQR", preferencesManager.isStaticQR());
            jsonObject.put("Membership/Loyality", preferencesManager.isLoyality());
            jsonObject.put("Home", preferencesManager.isHome());
            jsonObject.put("ManualEntry", preferencesManager.isManual());
            jsonObject.put("Back", preferencesManager.isBack());
            jsonObject.put("Front", preferencesManager.isFront());
            jsonObject.put("ShowMembershipManual", preferencesManager.isMembershipManual());
            jsonObject.put("ShowMembershipHome", preferencesManager.isMembershipHome());
            jsonObject.put("ConvenienceFee", preferencesManager.isConvenienceFeeSelected());
            jsonObject.put("AlipayWechatvalue", preferencesManager.getcnv_alipay());
            jsonObject.put("UnionPayvalue", preferencesManager.getcnv_uni());
            jsonObject.put("EnableBranchName", preferencesManager.getBranchName());
            jsonObject.put("EnableBranchAddress", preferencesManager.getBranchAddress());
            jsonObject.put("EnableBranchEmail", preferencesManager.getBranchEmail());
            jsonObject.put("EnableBranchContactNo", preferencesManager.getBranchPhoneNo());
            jsonObject.put("EnableBranchGSTNo", preferencesManager.getGSTNo());
            jsonObject.put("TimeZoneId", preferencesManager.getTimeZoneId());
            jsonObject.put("TimeZone", preferencesManager.getTimeZone());
            jsonObject.put("isTimeZoneChecked", preferencesManager.isTimeZoneChecked());
            jsonObject.put("isTerminalIdentifier", preferencesManager.isTerminalIdentifier());
            jsonObject.put("isPOSIdentifier", preferencesManager.isPOSIdentifier());
            jsonObject.put("isLaneIdentifier", preferencesManager.isLaneIdentifier());
            jsonObject.put("LaneIdentifier", preferencesManager.getLaneIdentifier());
            jsonObject.put("TerminalIdentifier", preferencesManager.getTerminalIdentifier());
            jsonObject.put("POSIdentifier", preferencesManager.getPOSIdentifier());
            jsonObject.put("isUpdated", true);

            jsonObject.put("CnvUPIQrMPMCloudDAADD", preferencesManager.cnv_up_upi_qrscan_mpmcloud_display_and_add());
            jsonObject.put("CnvUPIQrMPMCloudDOnly", preferencesManager.cnv_up_upi_qrscan_mpmcloud_display_only());
            jsonObject.put("CnvUPIQrMPMCloudValue", preferencesManager.getcnv_up_upiqr_mpmcloud());
            jsonObject.put("isMerchantDPARDisplay",preferencesManager.isMerchantDPARDisplay());
            jsonObject.put("cnv_unimerchantqrdisplay", preferencesManager.get_cnv_unimerchantqrdisplay());
            hashMapKeys.clear();
            hashMapKeys.put("branchAddress", preferencesManager.getaddress().equals("") ? encryption("nodata") : encryption(preferencesManager.getaddress()));
            hashMapKeys.put("branchContactNo", preferencesManager.getcontact_no().equals("") ? encryption("nodata") : encryption(preferencesManager.getcontact_no()));
            hashMapKeys.put("branchName", preferencesManager.getmerchant_name().equals("") ? encryption("nodata") : encryption(preferencesManager.getmerchant_name()));
            hashMapKeys.put("branchEmail", preferencesManager.getcontact_email().equals("") ? "nodata" : encryption(preferencesManager.getcontact_email()));
            hashMapKeys.put("gstNo", preferencesManager.getgstno().equals("") ? encryption("nodata") : encryption(preferencesManager.getgstno()));
            hashMapKeys.put("terminalId", encryption(preferencesManager.getterminalId()));
            hashMapKeys.put("otherData", encryption(jsonObject.toString()));//encryption(jsonObject.toString()));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("accessId", encryption(preferencesManager.getuniqueId()));
            hashMapKeys.put("configId", encryption(preferencesManager.getConfigId()));
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferencesManager.getauthToken());

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
            new OkHttpHandler(getActivity(), this, hashMap, "UpdateBranchDetailsNew")
                    .execute(AppConstants.BASE_URL2 + AppConstants.SAVE_TERMINAL_CONFIG);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void _oldUser(JSONObject jsonObject) {
        try {
            if (jsonObject.optString("success").equals("true")) {
                preferencesManager.setaddress(decryption_old(jsonObject.optString("branchAddress")).equals("nodata") ? "" : decryption_old(jsonObject.optString("branchAddress")));
                preferencesManager.setcontact_email(jsonObject.optString("branchEmail").equals("nodata") ? "" : jsonObject.optString("branchEmail"));
                preferencesManager.setcontact_no(decryption_old(jsonObject.optString("branchContactNo")).equals("nodata") ? "" : decryption_old(jsonObject.optString("branchContactNo")));
                preferencesManager.setmerchant_name(decryption_old(jsonObject.optString("branchName")).equals("nodata") ? "" : decryption_old(jsonObject.optString("branchName")));
                preferencesManager.setgstno(decryption_old(jsonObject.optString("gstNo")).equals("nodata") ? "" : decryption_old(jsonObject.optString("gstNo")));
                preferencesManager.setterminalId(decryption_old(jsonObject.optString("terminalId")));
                edt_unique_id.setText(decryption_old(jsonObject.optString("accessId")));
                jsonObject1 = new JSONObject(decryption_old(jsonObject.optString("otherData")));
                if (jsonObject.has("otherData")) {

                    preferencesManager.setcnv_alipay_diaplay_and_add(jsonObject1.optBoolean("CnvAlipayDisplayAndAdd"));
                    preferencesManager.setcnv_alipay_diaplay_only(jsonObject1.optBoolean("CnvAlipayDisplayOnly"));
                    preferencesManager.setcnv_wechat_display_and_add(jsonObject1.optBoolean("CnvWeChatDisplayAndAdd"));
                    preferencesManager.setcnv_wechat_display_only(jsonObject1.optBoolean("CnvWeChatDisplayOnly"));
                    preferencesManager.setisAlipaySelected(jsonObject1.optBoolean("AlipaySelected"));
                    preferencesManager.setisWechatSelected(jsonObject1.optBoolean("WeChatSelected"));
                    preferencesManager.setcnv_wechat(jsonObject1.optString("WeChatValue"));

                    preferencesManager.setisWeChatScan(jsonObject1.optBoolean("WeChatScanQR"));
                    preferencesManager.setisAlipayScan(jsonObject1.optBoolean("AlipayScanQR"));

                    preferencesManager.setisUnionPaySelected(jsonObject1.optBoolean("UnionPay"));
                    preferencesManager.setUnionPayQrSelected(jsonObject1.optBoolean("UnionPayQR"));
                    preferencesManager.setisUnionPayQrCodeDisplaySelected(jsonObject1.optBoolean("isUnionPayQrCodeDisplaySelected"));
                    preferencesManager.setcnv_uniqr(jsonObject1.optString("UnionPayQrValue"));
                    preferencesManager.setcnv_uplan(jsonObject1.optString("UplanValue"));
                    preferencesManager.setcnv_uni_display_and_add(jsonObject1.optBoolean("CnvUnionpayDisplayAndAdd"));
                    preferencesManager.setcnv_uni_display_only(jsonObject1.optBoolean("CnvUnionpayDisplayOnly"));
                    preferencesManager.setisUplanSelected(jsonObject1.optBoolean("Uplan"));
                    preferencesManager.setaggregated_singleqr(jsonObject1.optBoolean("AlipayWeChatPay"));
                    preferencesManager.setAlipayWechatQrSelected(jsonObject1.optBoolean("AlipayWeChatScanQR"));
                    preferencesManager.setisPrint(jsonObject1.optString("PrintReceiptautomatically"));
                    preferencesManager.setshowReference(jsonObject1.optString("ShowReference"));
                    preferencesManager.setisQR(jsonObject1.optBoolean("ShowPrintQR"));
                    preferencesManager.setisStaticQR(jsonObject1.optBoolean("DisplayStaticQR"));
                    preferencesManager.setisMembershipManual(jsonObject1.optBoolean("ShowMembershipManual"));
                    preferencesManager.setisMembershipHome(jsonObject1.optBoolean("ShowMembershipHome"));
                    preferencesManager.setisLoyality(jsonObject1.optBoolean("Membership/Loyality"));
                    preferencesManager.setIsHome(jsonObject1.optBoolean("Home"));
                    preferencesManager.setIsManual(jsonObject1.optBoolean("ManualEntry"));
                    preferencesManager.setisConvenienceFeeSelected(jsonObject1.optBoolean("ConvenienceFee"));
                    preferencesManager.setcnv_alipay(jsonObject1.optString("AlipayWechatvalue"));
                    preferencesManager.setcnv_uni(jsonObject1.optString("UnionPayvalue"));
                    preferencesManager.setBranchName(jsonObject1.optString("EnableBranchName"));
                    preferencesManager.setBranchAddress(jsonObject1.optString("EnableBranchAddress"));
                    preferencesManager.setBranchEmail(jsonObject1.optString("EnableBranchEmail"));
                    preferencesManager.setBranchPhoneNo(jsonObject1.optString("EnableBranchContactNo"));
                    preferencesManager.setGSTNo(jsonObject1.optString("EnableBranchGSTNo"));
                    preferencesManager.setTimeZoneId(jsonObject1.optString("TimeZoneId"));
                    preferencesManager.setTimeZone(jsonObject1.optString("TimeZone"));
                    preferencesManager.setisTimeZoneChecked(jsonObject1.optBoolean("isTimeZoneChecked"));
                    preferencesManager.setIsBack(jsonObject1.optBoolean("Back"));
                    preferencesManager.setIsFront(jsonObject1.optBoolean("Front"));

                    preferencesManager.setTerminalIdentifier(jsonObject1.optString("TerminalIdentifier"));
                    preferencesManager.setPOSIdentifier(jsonObject1.optString("POSIdentifier"));
                    preferencesManager.setLaneIdentifier(jsonObject1.optString("LaneIdentifier"));
                    preferencesManager.setisLaneIdentifier(jsonObject1.optBoolean("isLaneIdentifier"));
                    preferencesManager.setisPOSIdentifier(jsonObject1.optBoolean("isPOSIdentifier"));
                    preferencesManager.setisTerminalIdentifier(jsonObject1.optBoolean("isTerminalIdentifier"));

                    preferencesManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(jsonObject1.optBoolean("CnvUPIQrMPMCloudDAADD"));
                    preferencesManager.setcnv_up_upi_qrscan_mpmcloud_display_only(jsonObject1.optBoolean("CnvUPIQrMPMCloudDOnly"));
                    preferencesManager.setcnv_up_upiqr_mpmcloud(jsonObject1.optString("CnvUPIQrMPMCloudValue"));
                    preferencesManager.setisMerchantDPARDisplay(jsonObject1.optBoolean("isMerchantDPARDisplay"));
                    preferencesManager.set_cnv_unimerchantqrdisplay(jsonObject1.optString("cnv_unimerchantqrdisplay"));
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
                preferencesManager.setaddress(decryption(jsonObject.optString("branchAddress")).equals("nodata") ? "" : decryption(jsonObject.optString("branchAddress")));
                if (jsonObject.optString("branchEmail").equals("nodata")) {
                    preferencesManager.setcontact_email("");
                } else {
                    preferencesManager.setcontact_email(decryption(jsonObject.optString("branchEmail")).equals("nodata") ? "" : decryption(jsonObject.optString("branchEmail")));
                }
                preferencesManager.setcontact_no(decryption(jsonObject.optString("branchContactNo")).equals("nodata") ? "" : decryption(jsonObject.optString("branchContactNo")));
                preferencesManager.setmerchant_name(decryption(jsonObject.optString("branchName")).equals("nodata") ? "" : decryption(jsonObject.optString("branchName")));
                preferencesManager.setgstno(decryption(jsonObject.optString("gstNo")).equals("nodata") ? "" : decryption(jsonObject.optString("gstNo")));
                preferencesManager.setterminalId(decryption(jsonObject.optString("terminalId")));

                if (preferencesManager.isResetTerminal()) {
                    edt_unique_id.setText("");
                    preferencesManager.setuniqueId("");
                } else {
                    if (!preferencesManager.getuniqueId().equals("")) {
                        edt_unique_id.setText(preferencesManager.getuniqueId());
                    } else {
                        edt_unique_id.setText(decryption(jsonObject.optString("accessId")));
                        preferencesManager.setuniqueId(decryption(jsonObject.optString("accessId")));
                    }

                }


                if (jsonObject.has("configId")) {
                    preferencesManager.setConfigId(decryption(jsonObject.optString("configId")));
                }

                if (jsonObject.has("merchant_id")) {
                    preferencesManager.setMerchantId(decryption(jsonObject.optString("merchant_id")));
                }


                jsonObject1 = new JSONObject(decryption(jsonObject.optString("otherData")));
//                jsonObject1 = new JSONObject(hextoString(jsonObject.optString("otherData")));
                if (jsonObject.has("otherData")) {


                    if (preferencesManager.isResetTerminal()) {
                        edt_unique_id.setText("");
                        preferencesManager.setuniqueId("");
                    } else {
                        if (!preferencesManager.getuniqueId().equals("")) {
                            edt_unique_id.setText(preferencesManager.getuniqueId());
                        } else {
                            edt_unique_id.setText(jsonObject1.optString("accessId"));
                            preferencesManager.setuniqueId(jsonObject1.optString("accessId"));
                        }

                    }


                    preferencesManager.setcnv_alipay_diaplay_and_add(jsonObject1.optBoolean("CnvAlipayDisplayAndAdd"));
                    preferencesManager.setcnv_alipay_diaplay_only(jsonObject1.optBoolean("CnvAlipayDisplayOnly"));
                    preferencesManager.setcnv_wechat_display_and_add(jsonObject1.optBoolean("CnvWeChatDisplayAndAdd"));
                    preferencesManager.setcnv_wechat_display_only(jsonObject1.optBoolean("CnvWeChatDisplayOnly"));
                    preferencesManager.setisAlipaySelected(jsonObject1.optBoolean("AlipaySelected"));
                    preferencesManager.setisWechatSelected(jsonObject1.optBoolean("WeChatSelected"));
                    preferencesManager.setcnv_wechat(jsonObject1.optString("WeChatValue"));

                    preferencesManager.setisWeChatScan(jsonObject1.optBoolean("WeChatScanQR"));
                    preferencesManager.setisAlipayScan(jsonObject1.optBoolean("AlipayScanQR"));

                    preferencesManager.setisUnionPaySelected(jsonObject1.optBoolean("UnionPay"));
                    preferencesManager.setUnionPayQrSelected(jsonObject1.optBoolean("UnionPayQR"));
                    preferencesManager.setisUnionPayQrCodeDisplaySelected(jsonObject1.optBoolean("isUnionPayQrCodeDisplaySelected"));
                    preferencesManager.setcnv_uniqr(jsonObject1.optString("UnionPayQrValue"));
                    preferencesManager.setcnv_uplan(jsonObject1.optString("UplanValue"));
                    preferencesManager.setcnv_uni_display_and_add(jsonObject1.optBoolean("CnvUnionpayDisplayAndAdd"));
                    preferencesManager.setcnv_uni_display_only(jsonObject1.optBoolean("CnvUnionpayDisplayOnly"));
                    preferencesManager.setisUplanSelected(jsonObject1.optBoolean("Uplan"));
                    preferencesManager.setaggregated_singleqr(jsonObject1.optBoolean("AlipayWeChatPay"));
                    preferencesManager.setAlipayWechatQrSelected(jsonObject1.optBoolean("AlipayWeChatScanQR"));
                    preferencesManager.setisPrint(jsonObject1.optString("PrintReceiptautomatically"));
                    preferencesManager.setshowReference(jsonObject1.optString("ShowReference"));
                    preferencesManager.setisQR(jsonObject1.optBoolean("ShowPrintQR"));
                    preferencesManager.setisStaticQR(jsonObject1.optBoolean("DisplayStaticQR"));
                    preferencesManager.setisMembershipManual(jsonObject1.optBoolean("ShowMembershipManual"));
                    preferencesManager.setisMembershipHome(jsonObject1.optBoolean("ShowMembershipHome"));
                    preferencesManager.setisLoyality(jsonObject1.optBoolean("Membership/Loyality"));
                    preferencesManager.setIsHome(jsonObject1.optBoolean("Home"));
                    preferencesManager.setIsManual(jsonObject1.optBoolean("ManualEntry"));
                    preferencesManager.setisConvenienceFeeSelected(jsonObject1.optBoolean("ConvenienceFee"));
                    preferencesManager.setcnv_alipay(jsonObject1.optString("AlipayWechatvalue"));
                    preferencesManager.setcnv_uni(jsonObject1.optString("UnionPayvalue"));
                    preferencesManager.setBranchName(jsonObject1.optString("EnableBranchName"));
                    preferencesManager.setBranchAddress(jsonObject1.optString("EnableBranchAddress"));
                    preferencesManager.setBranchEmail(jsonObject1.optString("EnableBranchEmail"));
                    preferencesManager.setBranchPhoneNo(jsonObject1.optString("EnableBranchContactNo"));
                    preferencesManager.setGSTNo(jsonObject1.optString("EnableBranchGSTNo"));
                    preferencesManager.setTimeZoneId(jsonObject1.optString("TimeZoneId"));
                    preferencesManager.setTimeZone(jsonObject1.optString("TimeZone"));
                    preferencesManager.setisTimeZoneChecked(jsonObject1.optBoolean("isTimeZoneChecked"));
                    preferencesManager.setIsBack(jsonObject1.optBoolean("Back"));
                    preferencesManager.setIsFront(jsonObject1.optBoolean("Front"));

                    preferencesManager.setTerminalIdentifier(jsonObject1.optString("TerminalIdentifier"));
                    preferencesManager.setPOSIdentifier(jsonObject1.optString("POSIdentifier"));
                    preferencesManager.setLaneIdentifier(jsonObject1.optString("LaneIdentifier"));
                    preferencesManager.setisLaneIdentifier(jsonObject1.optBoolean("isLaneIdentifier"));
                    preferencesManager.setisPOSIdentifier(jsonObject1.optBoolean("isPOSIdentifier"));
                    preferencesManager.setisTerminalIdentifier(jsonObject1.optBoolean("isTerminalIdentifier"));


                    preferencesManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(jsonObject1.optBoolean("CnvUPIQrMPMCloudDAADD"));
                    preferencesManager.setcnv_up_upi_qrscan_mpmcloud_display_only(jsonObject1.optBoolean("CnvUPIQrMPMCloudDOnly"));
                    preferencesManager.setcnv_up_upiqr_mpmcloud(jsonObject1.optString("CnvUPIQrMPMCloudValue"));
                    preferencesManager.set_cnv_unimerchantqrdisplay(jsonObject1.optString("cnv_unimerchantqrdisplay"));
                    preferencesManager.setisMerchantDPARDisplay(jsonObject1.optBoolean("isMerchantDPARDisplay"));

                    if (jsonObject1.has("ConfigId")) {
                        preferencesManager.setConfigId(jsonObject1.optString("ConfigId"));
                    }

                    if (jsonObject1.has("MerchantId")) {
                        preferencesManager.setMerchantId(jsonObject1.optString("MerchantId"));
                    }


                }


            } else {

                if (jsonObject.has("configId")) {
                    preferencesManager.setConfigId(decryption(jsonObject.optString("configId")));
                }

                if (jsonObject.has("merchant_id")) {
                    preferencesManager.setMerchantId(decryption(jsonObject.optString("merchant_id")));
                }

                if (jsonObject.has("terminalId")) {
                    preferencesManager.setterminalId(decryption(jsonObject.optString("terminalId")));
                }

//                if (jsonObject.has("accessId")) {
//                    preferencesManager.setuniqueId(decryption(jsonObject.optString("accessId")));
//                }
            }


        } catch (Exception e) {
            if (jsonObject.has("configId")) {
                preferencesManager.setConfigId(decryption(jsonObject.optString("configId")));
            }

            if (jsonObject.has("config_id")) {
                preferencesManager.setConfigId(decryption(jsonObject.optString("config_id")));
            }

            if (jsonObject.has("merchant_id")) {
                preferencesManager.setMerchantId(decryption(jsonObject.optString("merchant_id")));
            }

            if (jsonObject.has("terminalId")) {
                preferencesManager.setterminalId(decryption(jsonObject.optString("terminalId")));
            }

            if (jsonObject.has("terminal_id")) {
                preferencesManager.setterminalId(decryption(jsonObject.optString("terminal_id")));
            }

            if (jsonObject.has("branch_id")) {
                preferencesManager.setMerchantId(decryption(jsonObject.optString("branch_id")));
            }

            if (jsonObject.has("branchId")) {
                preferencesManager.setMerchantId(decryption(jsonObject.optString("branchId")));
            }

//            if (jsonObject.has("accessId")) {
//                preferencesManager.setuniqueId(decryption(jsonObject.optString("accessId")));
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
                    preferencesManager.setauthToken(jsonObject.optString("access_token"));
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
                    preferencesManager.setaggregated_singleqr(true);
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
                    preferencesManager.setaggregated_singleqr(true);
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
                    preferencesManager.clearPreferences();
                    Toast.makeText(getActivity(), "Terminal Config Deleted Successfully."+jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    // ((MyPOSMateApplication) getActivity().getApplicationContext()).asbtractConnection.disconnect();

                    preferencesManager.setisResetTerminal(true);
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.SETTINGS, null);
                } else {
                    Toast.makeText(getActivity(), "Terminal configuration not found on server.App data cleared successfully", Toast.LENGTH_SHORT).show();
                    preferencesManager.clearPreferences();
                    preferencesManager.setisResetTerminal(true);
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
                preferencesManager.setuniqueId("");

                if (jsonObject.has("branchInfo")) {
                    if (jsonObject.optJSONObject("branchInfo").has("fullname")) {
                        preferencesManager.setMerchantName(jsonObject.optJSONObject("branchInfo").optString("fullname"));
                    }
                }
                if (jsonObject.optBoolean("status")) {
                    preferencesManager.setisRegistered(true);
                    ((DashboardActivity) getActivity()).img_menu.setEnabled(true);
                    username = jsonObject.optString("terminal_xmpp_jid").toString();
                    password = jsonObject.optString("terminal_xmpp_password").toString();
                    preferencesManager.setterminalId(edt_terminal_id.getText().toString());
                    preferencesManager.setterminalIp(edt_terminal_ip.getText().toString());
                    preferencesManager.setuniqueId(edt_unique_id.getText().toString());
                    preferencesManager.setUsername(username);
                    preferencesManager.setPassword(password);
                    preferencesManager.setterminal_refund_password(jsonObject.optString("terminal_refund_password").toString());
                    preferencesManager.setIsAuthenticated(false);
                    preferencesManager.setIsConnected(false);
                    preferencesManager.setisUnipaySelected(true);
                    preferencesManager.setisUnionPaySelected(true);
                    preferencesManager.setisUplanSelected(false);
                    preferencesManager.setisAlipaySelected(true);
                    preferencesManager.setisWechatSelected(true);
                    preferencesManager.setisUnionPayQrCodeDisplaySelected(true);
                    preferencesManager.setIsHome(false);
                    preferencesManager.setIsBack(false);
                    preferencesManager.setIsFront(false);
                    preferencesManager.setisQR(false);
                    preferencesManager.setisStaticQR(false);
                    preferencesManager.setcurrency(jsonObject.optString("currency"));
                    preferencesManager.setisPrint("true");
                    preferencesManager.setshowReference("false");
                    preferencesManager.setisMembershipHome(false);
                    preferencesManager.setisMembershipManual(false);
                    preferencesManager.setisLoyality(false);
                    preferencesManager.setBranchEmail("false");
                    preferencesManager.setBranchPhoneNo("false");
                    preferencesManager.setBranchAddress("false");
                    preferencesManager.setBranchName("false");
                    preferencesManager.setGSTNo("false");
                    preferencesManager.setIsManual(true);
                    preferencesManager.setmerchant_info(jsonObject.optString("merchant_info"));
//                    preferencesManager.setMerchantId("");
//                    preferencesManager.setConfigId("");

                    preferencesManager.setisTimeZoneChecked(true);
                    preferencesManager.setTimeZone("New Zealand Standard Time (Pacific/Auckland)");
                    preferencesManager.setTimeZoneId("Pacific/Auckland");


                    if (preferencesManager.isResetTerminal()) {
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
                            preferencesManager.setterminalId(edt_terminal_id.getText().toString());
                            preferencesManager.setterminalIp(edt_terminal_ip.getText().toString());
                            preferencesManager.setuniqueId(edt_unique_id.getText().toString());
                            preferencesManager.setUsername(username);
                            preferencesManager.setPassword(password);
                            preferencesManager.setterminal_refund_password(jsonObject.optString("terminal_refund_password").toString());
                            preferencesManager.setIsAuthenticated(false);
                            preferencesManager.setIsConnected(false);
                            preferencesManager.setisDisplayAds(true);
                            preferencesManager.setdisplayAdsTime("5");
                            preferencesManager.setisUnipaySelected(true);
                            preferencesManager.setisUnionPaySelected(true);
                            preferencesManager.setisUplanSelected(false);
                            preferencesManager.setIsHome(false);
                            preferencesManager.setIsBack(false);
                            preferencesManager.setIsFront(false);
                            preferencesManager.setisQR(false);
                            preferencesManager.setisStaticQR(false);
                            preferencesManager.setcurrency(jsonObject.optString("currency"));
                            preferencesManager.setisPrint("true");
                            preferencesManager.setshowReference("false");
                            preferencesManager.setisMembershipHome(false);
                            preferencesManager.setisMembershipManual(false);
                            preferencesManager.setisLoyality(false);
                            preferencesManager.setBranchEmail("false");
                            preferencesManager.setBranchPhoneNo("false");
                            preferencesManager.setBranchAddress("false");
                            preferencesManager.setBranchName("false");
                            preferencesManager.setGSTNo("false");
                            preferencesManager.setIsManual(true);
                            preferencesManager.setmerchant_info(jsonObject.optString("merchant_info"));
                            preferencesManager.setisTimeZoneChecked(true);
                            preferencesManager.setTimeZone("New Zealand Standard Time (Pacific/Auckland)");
                            preferencesManager.setTimeZoneId("Pacific/Auckland");
                            preferencesManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(false);
                            preferencesManager.setcnv_up_upi_qrscan_mpmcloud_display_only(false);
                            preferencesManager.setcnv_up_upiqr_mpmcloud("");

                            if (jsonObject1.has("MerchantId") && !jsonObject1.optString("MerchantId").equals(""))
                                preferencesManager.setMerchantId(jsonObject1.optString("MerchantId"));
                            if (jsonObject1.has("ConfigId") && !jsonObject1.optString("ConfigId").equals(""))
                                preferencesManager.setConfigId(jsonObject1.optString("ConfigId"));
                            ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                            return;
                        }


                    }


                    if (jsonObject1 != null) {
                        preferencesManager.setisUnionPaySelected(jsonObject1.optBoolean("UnionPay"));
                        if (jsonObject1.has("MerchantId") && !jsonObject1.optString("MerchantId").equals(""))
                            preferencesManager.setMerchantId(jsonObject1.optString("MerchantId"));
                        if (jsonObject1.has("ConfigId") && !jsonObject1.optString("ConfigId").equals(""))
                            preferencesManager.setConfigId(jsonObject1.optString("ConfigId"));


                        preferencesManager.setcnv_alipay_diaplay_and_add(jsonObject1.optBoolean("CnvAlipayDisplayAndAdd"));
                        preferencesManager.setcnv_alipay_diaplay_only(jsonObject1.optBoolean("CnvAlipayDisplayOnly"));
                        preferencesManager.setcnv_wechat_display_and_add(jsonObject1.optBoolean("CnvWeChatDisplayAndAdd"));
                        preferencesManager.setcnv_wechat_display_only(jsonObject1.optBoolean("CnvWeChatDisplayOnly"));
                        preferencesManager.setisAlipaySelected(jsonObject1.optBoolean("AlipaySelected"));
                        preferencesManager.setisWechatSelected(jsonObject1.optBoolean("WeChatSelected"));
                        preferencesManager.setcnv_wechat(jsonObject1.optString("WeChatValue"));

                        preferencesManager.setisWeChatScan(jsonObject1.optBoolean("WeChatScanQR"));
                        preferencesManager.setisAlipayScan(jsonObject1.optBoolean("AlipayScanQR"));
                        preferencesManager.setUnionPayQrSelected(jsonObject1.optBoolean("UnionPayQR"));
                        preferencesManager.setisUnionPayQrCodeDisplaySelected(jsonObject1.optBoolean("isUnionPayQrCodeDisplaySelected"));
                        preferencesManager.setcnv_uniqr(jsonObject1.optString("UnionPayQrValue"));
                        preferencesManager.setcnv_uplan(jsonObject1.optString("UplanValue"));
                        preferencesManager.setcnv_uni_display_and_add(jsonObject1.optBoolean("CnvUnionpayDisplayAndAdd"));
                        preferencesManager.setcnv_uni_display_only(jsonObject1.optBoolean("CnvUnionpayDisplayOnly"));

                        preferencesManager.setisUplanSelected(jsonObject1.optBoolean("Uplan"));
                        preferencesManager.setaggregated_singleqr(jsonObject1.optBoolean("AlipayWeChatPay"));
                        preferencesManager.setAlipayWechatQrSelected(jsonObject1.optBoolean("AlipayWeChatScanQR"));
                        preferencesManager.setisPrint(jsonObject1.optString("PrintReceiptautomatically"));
                        preferencesManager.setshowReference(jsonObject1.optString("ShowReference"));
                        preferencesManager.setisQR(jsonObject1.optBoolean("ShowPrintQR"));
                        preferencesManager.setisStaticQR(jsonObject1.optBoolean("DisplayStaticQR"));
                        preferencesManager.setisMembershipManual(jsonObject1.optBoolean("ShowMembershipManual"));
                        preferencesManager.setisMembershipHome(jsonObject1.optBoolean("ShowMembershipHome"));
                        preferencesManager.setisLoyality(jsonObject1.optBoolean("Membership/Loyality"));
                        preferencesManager.setIsHome(jsonObject1.optBoolean("Home"));
                        preferencesManager.setIsManual(jsonObject1.optBoolean("ManualEntry"));
                        preferencesManager.setIsBack(jsonObject1.optBoolean("Back"));
                        preferencesManager.setIsFront(jsonObject1.optBoolean("Front"));
                        preferencesManager.setisConvenienceFeeSelected(jsonObject1.optBoolean("ConvenienceFee"));
                        preferencesManager.setcnv_alipay(jsonObject1.optString("AlipayWechatvalue"));
                        preferencesManager.setcnv_uni(jsonObject1.optString("UnionPayvalue"));
                        preferencesManager.setBranchName(jsonObject1.optString("EnableBranchName"));
                        preferencesManager.setBranchAddress(jsonObject1.optString("EnableBranchAddress"));
                        preferencesManager.setBranchEmail(jsonObject1.optString("EnableBranchEmail"));
                        preferencesManager.setBranchPhoneNo(jsonObject1.optString("EnableBranchContactNo"));
                        preferencesManager.setGSTNo(jsonObject1.optString("EnableBranchGSTNo"));

                        if (jsonObject1.has("TimeZoneId")) {
                            if (jsonObject1.optString("TimeZoneId").equals("")) {
                                preferencesManager.setisTimeZoneChecked(true);
                                preferencesManager.setTimeZone("New Zealand Standard Time (Pacific/Auckland)");
                                preferencesManager.setTimeZoneId("Pacific/Auckland");
                            } else {
                                preferencesManager.setTimeZoneId(jsonObject1.optString("TimeZoneId"));
                                preferencesManager.setTimeZone(jsonObject1.optString("TimeZone"));
                                preferencesManager.setisTimeZoneChecked(jsonObject1.optBoolean("isTimeZoneChecked"));
                            }
                        } else {
                            preferencesManager.setisTimeZoneChecked(true);
                            preferencesManager.setTimeZone("New Zealand Standard Time (Pacific/Auckland)");
                            preferencesManager.setTimeZoneId("Pacific/Auckland");
                        }


                        preferencesManager.setTerminalIdentifier(jsonObject1.optString("TerminalIdentifier"));
                        preferencesManager.setPOSIdentifier(jsonObject1.optString("POSIdentifier"));
                        preferencesManager.setLaneIdentifier(jsonObject1.optString("LaneIdentifier"));
                        preferencesManager.setisLaneIdentifier(jsonObject1.optBoolean("isLaneIdentifier"));
                        preferencesManager.setisPOSIdentifier(jsonObject1.optBoolean("isPOSIdentifier"));
                        preferencesManager.setisTerminalIdentifier(jsonObject1.optBoolean("isTerminalIdentifier"));

                        preferencesManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(jsonObject1.optBoolean("CnvUPIQrMPMCloudDAADD"));
                        preferencesManager.setcnv_up_upi_qrscan_mpmcloud_display_only(jsonObject1.optBoolean("CnvUPIQrMPMCloudDOnly"));
                        preferencesManager.setcnv_up_upiqr_mpmcloud(jsonObject1.optString("CnvUPIQrMPMCloudValue"));
                        preferencesManager.set_cnv_unimerchantqrdisplay(jsonObject1.optString("cnv_unimerchantqrdisplay"));
                        preferencesManager.setisMerchantDPARDisplay(jsonObject1.optBoolean("isMerchantDPARDisplay"));
                    }

                    //  initChat(jsonObject.optString("terminal_xmpp_jid").toString(),jsonObject.optString("terminal_xmpp_password").toString());
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                } else {
                    Toast.makeText(getActivity(), jsonObject.optString("message"), Toast.LENGTH_LONG).show();
                }

                if (preferencesManager.is_cnv_alipay_display_and_add() ||
                        preferencesManager.is_cnv_alipay_display_only()) {
                    preferencesManager.setisAlipaySelected(true);
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

}





