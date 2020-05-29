package com.quagnitia.myposmate.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.quagnitia.myposmate.MyPOSMateApplication;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.utils.AESHelper;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AboutUs extends Fragment implements View.OnClickListener, OnTaskCompleted {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText edt_merchant_name, edt_gst_number, edt_contact_no, edt_contact_email, edt_address, edt_lane_identifier, edt_terminal_identifier, edt_pos_identifier;
    private View view;
    private CheckBox chk_branch_name, chk_branch_address, chk_branch_contact_no, chk_branch_email, chk_gst_no, chk_lane_identifier, chk_pos_identifier, chk_terminal_identifier;
    private Spinner sp_timezone;
    private Button btn_save, btn_cancel;
    private static PreferencesManager preferencesManager;
    private Context mContext;
    TreeMap<String, String> hashMapKeys;

    public AboutUs() {
        // Required empty public constructor
    }

    public static AboutUs newInstance(String param1, String param2) {
        AboutUs fragment = new AboutUs();
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
        hashMap.put("grant_type", "password");
        hashMap.put("username", preferencesManager.getterminalId());
        hashMap.put("password", preferencesManager.getuniqueId());
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.AUTH);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_about_us, container, false);
        preferencesManager = PreferencesManager.getInstance(getActivity());
        hashMapKeys = new TreeMap<>();
        mContext = getActivity();
        callAuthToken();
        initUI();
        initLIstenere();
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


    public void initUI() {


        edt_merchant_name = (EditText) view.findViewById(R.id.edt_merchant_name);
        edt_gst_number = (EditText) view.findViewById(R.id.edt_gst_number);
        //edt_gst_number.addTextChangedListener(new FourDigitCardFormatWatcher());
        edt_contact_no = (EditText) view.findViewById(R.id.edt_contact_no);
        edt_contact_email = (EditText) view.findViewById(R.id.edt_contact_email);
        edt_address = (EditText) view.findViewById(R.id.edt_address);
        btn_save = (Button) view.findViewById(R.id.btn_save);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        edt_merchant_name.setText(preferencesManager.getmerchant_name());
        edt_contact_no.setText(preferencesManager.getcontact_no());
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPattern1 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";


        edt_address.setText(preferencesManager.getaddress());
        edt_gst_number.setText(preferencesManager.getgstno());


        edt_lane_identifier = (EditText) view.findViewById(R.id.edt_lane_identifier);
        edt_terminal_identifier = (EditText) view.findViewById(R.id.edt_terminal_identifier);
        edt_pos_identifier = (EditText) view.findViewById(R.id.edt_pos_identifier);


        edt_lane_identifier.setText(preferencesManager.getLaneIdentifier());
        edt_pos_identifier.setText(preferencesManager.getPOSIdentifier());
        edt_terminal_identifier.setText(preferencesManager.getTerminalIdentifier());


        chk_branch_name = (CheckBox) view.findViewById(R.id.chk_branch_name);
        chk_branch_address = (CheckBox) view.findViewById(R.id.chk_branch_address);
        chk_branch_contact_no = (CheckBox) view.findViewById(R.id.chk_branch_contact_no);
        chk_branch_email = (CheckBox) view.findViewById(R.id.chk_branch_email);
        chk_gst_no = (CheckBox) view.findViewById(R.id.chk_gst_no);
        chk_lane_identifier = (CheckBox) view.findViewById(R.id.chk_lane_identifier);
        chk_pos_identifier = (CheckBox) view.findViewById(R.id.chk_pos_identifier);
        chk_terminal_identifier = (CheckBox) view.findViewById(R.id.chk_terminal_identifier);


        if (preferencesManager.isLaneIdentifier()) {
            chk_lane_identifier.setChecked(true);
            chk_lane_identifier.setSelected(true);
        } else {
            chk_lane_identifier.setChecked(false);
            chk_lane_identifier.setSelected(false);
        }

        if (preferencesManager.isPOSIdentifier()) {
            chk_pos_identifier.setChecked(true);
            chk_pos_identifier.setSelected(true);
        } else {
            chk_pos_identifier.setChecked(false);
            chk_pos_identifier.setSelected(false);
        }

        if (preferencesManager.isTerminalIdentifier()) {
            chk_terminal_identifier.setChecked(true);
            chk_terminal_identifier.setSelected(true);
        } else {
            chk_terminal_identifier.setChecked(false);
            chk_terminal_identifier.setSelected(false);
        }


        if (preferencesManager.getBranchName().equals("true")) {
            chk_branch_name.setChecked(true);
            chk_branch_name.setSelected(true);
        } else {
            chk_branch_name.setChecked(false);
            chk_branch_name.setSelected(false);
        }


        if (preferencesManager.getBranchAddress().equals("true")) {
            chk_branch_address.setChecked(true);
            chk_branch_address.setSelected(true);
        } else {
            chk_branch_address.setChecked(false);
            chk_branch_address.setSelected(false);
        }


        if (preferencesManager.getBranchPhoneNo().equals("true")) {
            chk_branch_contact_no.setChecked(true);
            chk_branch_contact_no.setSelected(true);
        } else {
            chk_branch_contact_no.setChecked(false);
            chk_branch_contact_no.setSelected(false);
        }


        if (preferencesManager.getBranchEmail().equals("true")) {
            chk_branch_email.setChecked(true);
            chk_branch_email.setSelected(true);
        } else {
            chk_branch_email.setChecked(false);
            chk_branch_email.setSelected(false);
        }


        if (preferencesManager.getGSTNo().equals("true")) {
            chk_gst_no.setChecked(true);
            chk_gst_no.setSelected(true);
        } else {
            chk_gst_no.setChecked(false);
            chk_gst_no.setSelected(false);
        }


        chk_gst_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (edt_gst_number.getText().toString().equals("") ||
                        (!edt_gst_number.getText().toString().equals("")
                                && (edt_gst_number.getText().toString().length() < 8
                                || edt_gst_number.getText().toString().length() > 9))) {
                    preferencesManager.setGSTNo("false");
                    chk_gst_no.setChecked(false);
                    chk_gst_no.setSelected(false);

                    if (!edt_gst_number.getText().toString().equals("")
                            && (edt_gst_number.getText().toString().length() < 8
                            || edt_gst_number.getText().toString().length() > 9)) {
                        Toast.makeText(getActivity(), "GST number should be 8 or 9 digits", Toast.LENGTH_SHORT).show();
                        edt_gst_number.setText("");
                    }

                    return;
                }

//                if (edt_gst_number.getText().toString().equals("")) {
//                    preferencesManager.setGSTNo("false");
//                    chk_gst_no.setChecked(false);
//                    return;
//                }

                if (chk_gst_no.isChecked()) {
                    chk_gst_no.setChecked(true);
                    preferencesManager.setGSTNo("true");
                } else {
                    //case 2
                    chk_gst_no.setChecked(false);
                    preferencesManager.setGSTNo("false");
                }
            }
        });


        edt_gst_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edt_gst_number.getText().toString().equals("")) {
                    preferencesManager.setGSTNo("false");
                    chk_gst_no.setChecked(false);
                    chk_gst_no.setSelected(false);
                    return;
                }
            }
        });


        chk_branch_email.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?

                if (edt_contact_email.getText().toString().equals("")) {
                    preferencesManager.setBranchEmail("false");
                    chk_branch_email.setChecked(false);
                    return;
                }


                if (chk_branch_email.isChecked()) {
                    chk_branch_email.setChecked(true);
                    preferencesManager.setBranchEmail("true");
                } else {
                    //case 2
                    chk_branch_email.setChecked(false);
                    preferencesManager.setBranchEmail("false");
                }
            }
        });


        chk_branch_contact_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?

                if (edt_contact_no.getText().toString().equals("")) {
                    preferencesManager.setBranchPhoneNo("false");
                    chk_branch_contact_no.setChecked(false);
                    return;
                }

                if (chk_branch_contact_no.isChecked()) {
                    chk_branch_contact_no.setChecked(true);
                    preferencesManager.setBranchPhoneNo("true");
                } else {
                    //case 2
                    chk_branch_contact_no.setChecked(false);
                    preferencesManager.setBranchPhoneNo("false");
                }
            }
        });
        edt_contact_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edt_contact_no.getText().toString().equals("")) {
                    preferencesManager.setBranchPhoneNo("false");
                    chk_branch_contact_no.setChecked(false);
                    chk_branch_contact_no.setSelected(false);
                    return;
                }
            }
        });

        chk_branch_address.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?

                if (edt_address.getText().toString().equals("")) {
                    preferencesManager.setBranchAddress("false");
                    chk_branch_address.setChecked(false);
                    return;
                }


                if (chk_branch_address.isChecked()) {
                    chk_branch_address.setChecked(true);
                    preferencesManager.setBranchAddress("true");
                } else {
                    //case 2
                    chk_branch_address.setChecked(false);
                    preferencesManager.setBranchAddress("false");
                }
            }
        });
        edt_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edt_address.getText().toString().equals("")) {
                    preferencesManager.setBranchAddress("false");
                    chk_branch_address.setChecked(false);
                    chk_branch_address.setSelected(false);
                    return;
                }
            }
        });

        chk_branch_name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?

                if (edt_merchant_name.getText().toString().equals("")) {
                    preferencesManager.setBranchName("false");
                    chk_branch_name.setChecked(false);
                    return;
                }


                if (chk_branch_name.isChecked()) {
                    chk_branch_name.setChecked(true);
                    preferencesManager.setBranchName("true");
                } else {
                    //case 2
                    chk_branch_name.setChecked(false);
                    preferencesManager.setBranchName("false");
                }
            }
        });
        edt_merchant_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edt_merchant_name.getText().toString().equals("")) {
                    preferencesManager.setBranchName("false");
                    chk_branch_name.setChecked(false);
                    chk_branch_name.setSelected(false);
                    return;
                }
            }
        });


        chk_terminal_identifier.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?

                if (edt_terminal_identifier.getText().toString().equals("")) {
                    preferencesManager.setisTerminalIdentifier(false);
                    chk_terminal_identifier.setChecked(false);
                    return;
                }


                if (chk_terminal_identifier.isChecked()) {
                    chk_terminal_identifier.setChecked(true);
                    preferencesManager.setisTerminalIdentifier(true);
                } else {
                    //case 2
                    chk_terminal_identifier.setChecked(false);
                    preferencesManager.setisTerminalIdentifier(false);
                }
            }
        });
        edt_terminal_identifier.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edt_terminal_identifier.getText().toString().equals("")) {
                    preferencesManager.setisTerminalIdentifier(false);
                    chk_terminal_identifier.setChecked(false);
                    chk_terminal_identifier.setSelected(false);
                    return;
                }
            }
        });

        chk_pos_identifier.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?

                if (edt_pos_identifier.getText().toString().equals("")) {
                    preferencesManager.setisPOSIdentifier(false);
                    chk_pos_identifier.setChecked(false);
                    return;
                }


                if (chk_pos_identifier.isChecked()) {
                    chk_pos_identifier.setChecked(true);
                    preferencesManager.setisPOSIdentifier(true);
                } else {
                    //case 2
                    chk_pos_identifier.setChecked(false);
                    preferencesManager.setisPOSIdentifier(false);
                }
            }
        });
        edt_pos_identifier.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edt_pos_identifier.getText().toString().equals("")) {
                    preferencesManager.setisPOSIdentifier(false);
                    chk_pos_identifier.setChecked(false);
                    chk_pos_identifier.setSelected(false);
                    return;
                }
            }
        });


        chk_lane_identifier.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?

                if (edt_lane_identifier.getText().toString().equals("")) {
                    preferencesManager.setisLaneIdentifier(false);
                    chk_lane_identifier.setChecked(false);
                    return;
                }


                if (chk_lane_identifier.isChecked()) {
                    chk_lane_identifier.setChecked(true);
                    preferencesManager.setisLaneIdentifier(true);
                } else {
                    //case 2
                    chk_lane_identifier.setChecked(false);
                    preferencesManager.setisLaneIdentifier(false);
                }
            }
        });
        edt_lane_identifier.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edt_lane_identifier.getText().toString().equals("")) {
                    preferencesManager.setisLaneIdentifier(false);
                    chk_lane_identifier.setChecked(false);
                    chk_lane_identifier.setSelected(false);
                    return;
                }
            }
        });


//
        if (!preferencesManager.getcontact_email().equals("")) {
            if (preferencesManager.getcontact_email().trim().matches(emailPattern1)) {
                edt_contact_email.setText(preferencesManager.getcontact_email());
            } else if (preferencesManager.getcontact_email().trim().matches(emailPattern)) {
                edt_contact_email.setText(preferencesManager.getcontact_email());
            } else {
                edt_contact_email.setText(decryption(preferencesManager.getcontact_email()));
            }
        }
        edt_contact_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (edt_contact_email.getText().toString().equals("")) {
                    preferencesManager.setBranchEmail("false");
                    chk_branch_email.setChecked(false);
                    chk_branch_email.setSelected(false);
                    return;
                }
            }
        });

        //edt_contact_email.setText(preferencesManager.getcontact_email());
    }

    public void initLIstenere() {
        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (((DashboardActivity) mContext).mPopupWindow.isShowing())
            ((DashboardActivity) mContext).mPopupWindow.dismiss();
        switch (v.getId()) {
            case R.id.btn_save:
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String emailPattern1 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";

                if (edt_address.getText().toString().equals("") &&
                        edt_contact_email.getText().toString().equals("") &&
                        edt_contact_no.getText().toString().equals("") &&
                        edt_merchant_name.getText().toString().equals("") &&
                        edt_gst_number.getText().toString().equals("") &&
                        edt_terminal_identifier.getText().toString().equals("") &&
                        edt_lane_identifier.getText().toString().equals("") &&
                        edt_pos_identifier.getText().toString().equals("")
                ) {
                    Toast.makeText(getActivity(), "Please fill in the details", Toast.LENGTH_LONG).show();
                } else {
                    boolean flag = true;
//
//                    if (edt_merchant_name.getText().toString().equals("")) {
//                        flag = false;
//                        Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
//                    } else if (edt_address.getText().toString().equals("")) {
//                        flag = false;
//                        Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
//                    } else if (edt_contact_no.getText().toString().equals("")) {
//                        flag = false;
//                        Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
//                    } else if (edt_contact_email.getText().toString().equals("")) {
//                        flag = false;
//                        Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
//                    } else

                    if (!edt_contact_email.getText().toString().equals("") && !edt_contact_email.getText().toString().trim().matches(emailPattern)) {
                        if (!edt_contact_email.getText().toString().equals("") && !edt_contact_email.getText().toString().trim().matches(emailPattern1)) {
                            flag = false;
                            Toast.makeText(getActivity(), "Invalid email id", Toast.LENGTH_SHORT).show();
                        } else {
                            flag = true;
                        }


                    }
//                    else if (edt_gst_number.getText().toString().equals("")) {
//                        flag = false;
//                        Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
//                    }
                    if (!edt_gst_number.getText().toString().equals("") && (edt_gst_number.getText().toString().length() < 8 || edt_gst_number.getText().toString().length() > 9)) {
                        flag = false;
                        Toast.makeText(getActivity(), "GST number should be 8 or 9 digits", Toast.LENGTH_SHORT).show();
                    }
//                    else if(edt_gst_number.getText().toString().length()==8 ||edt_gst_number.getText().toString().length()==9)
//                    {
                    if (flag) {

                        callUpdateBranchDetails();

                    } else {
                        Toast.makeText(getActivity(), "Please check if the details are entered properly", Toast.LENGTH_SHORT).show();
                    }

                }


                // }
                break;
            case R.id.btn_cancel:
                MyPOSMateApplication.isOpen = false;
                MyPOSMateApplication.isActiveQrcode = false;
                isCancel = true;
                callAuthToken();
                break;
        }
    }

    public static boolean isCancel = false;
    ProgressDialog progress;

    public void openProgressDialog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    public void callUpdateBranchDetails() {

        openProgressDialog();

        try {

            JSONObject jsonObject = new JSONObject();
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
            jsonObject.put("CnvAlipayDisplayAndAdd", preferencesManager.is_cnv_alipay_display_and_add());
            jsonObject.put("CnvAlipayDisplayOnly", preferencesManager.is_cnv_alipay_display_only());
            jsonObject.put("isUnionPayQrCodeDisplaySelected", preferencesManager.isUnionPayQrCodeDisplaySelected());
            jsonObject.put("UnionPayQrValue", preferencesManager.getcnv_uniqr());
            jsonObject.put("UplanValue", preferencesManager.getcnv_uplan());
            jsonObject.put("CnvUnionpayDisplayAndAdd", preferencesManager.is_cnv_uni_display_and_add());
            jsonObject.put("CnvUnionpayDisplayOnly", preferencesManager.is_cnv_uni_display_only());
            jsonObject.put("Uplan", preferencesManager.isUplanSelected());
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
            jsonObject.put("LaneIdentifier", edt_lane_identifier.getText().toString());
            jsonObject.put("TerminalIdentifier", edt_terminal_identifier.getText().toString());
            jsonObject.put("POSIdentifier", edt_pos_identifier.getText().toString());
            jsonObject.put("isUpdated", true);

            new OkHttpHandler(getActivity(), this, null, "UpdateBranchDetails").execute(AppConstants.BASE_URL3 + AppConstants.SAVE_TERMINAL_CONFIG
                    + "?branch_name=" + (edt_merchant_name.getText().toString().equals("") ? encryption("nodata") : encryption(edt_merchant_name.getText().toString()))
                    + "&branch_address=" + (edt_address.getText().toString().equals("") ? encryption("nodata") : encryption(edt_address.getText().toString()))
                    + "&branch_contact_no=" + (edt_contact_no.getText().toString().equals("") ? encryption("nodata") : encryption(edt_contact_no.getText().toString()))
                    + "&branch_email=" + (edt_contact_email.getText().toString().equals("") ? "nodata" : encryption(edt_contact_email.getText().toString()))
                    + "&gst_no=" + (edt_gst_number.getText().toString().equals("") ? encryption("nodata") : encryption(edt_gst_number.getText().toString()))
                    + "&terminal_id=" + encryption(preferencesManager.getterminalId())
                    + "&access_id=" + encryption(preferencesManager.getuniqueId())
                    + "&other_data=" + encryption(jsonObject.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String encryption(String strNormalText) {
        String seedValue = "YourSecKey";
        String normalTextEnc = "";
        try {
            normalTextEnc = AESHelper.encrypt(seedValue, strNormalText);
//            normalTextEnc = AESHelper.encrypt(strNormalText, seedValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return normalTextEnc;
    }

    public String decryption(String strEncryptedText) {
        String seedValue = "YourSecKey";
        String strDecryptedText = "";
        try {
            strDecryptedText = AESHelper.decrypt(seedValue, strEncryptedText);
//            strDecryptedText = AESHelper.decrypt(strEncryptedText, seedValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }

    public String decryption2(String strEncryptedText) {
        String seedValue = "YourSecKey";
        String strDecryptedText = "";
        try {
            strDecryptedText = AESHelper.decrypt2(seedValue, strEncryptedText);
//            strDecryptedText = AESHelper.decrypt(strEncryptedText, seedValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {

        if (result.equals("")) {
            if (progress.isShowing())
                progress.dismiss();
            Toast.makeText(getActivity(), "No data from server.", Toast.LENGTH_LONG).show();
            return;
        }
        if (progress.isShowing())
            progress.dismiss();
        JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {
            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferencesManager.setauthToken(jsonObject.optString("access_token"));
                }

                if (isCancel) {
                    isCancel = false;
                    callGetBranchDetails_new();
                }
                break;

            case "GetBranchDetailsNew":
                _NewUser(jsonObject);
                callAuthToken();

                if (preferencesManager.isManual()) {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                } else {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                }
                break;

            case "UpdateBranchDetails":
                callAuthToken();
                PreferencesManager preferencesManager = PreferencesManager.getInstance(getActivity());
                preferencesManager.setaddress(decryption(jsonObject.optString("branch_address")).equals("nodata") ? "" : decryption(jsonObject.optString("branch_address")));
                preferencesManager.setcontact_email(jsonObject.optString("branch_email").equals("nodata") ? "" : decryption(jsonObject.optString("branch_email")));
                preferencesManager.setmerchant_name(decryption(jsonObject.optString("branch_name")).equals("nodata") ? "" : decryption(jsonObject.optString("branch_name")));
                preferencesManager.setgstno(decryption(jsonObject.optString("gst_no")).equals("nodata") ? "" : decryption(jsonObject.optString("gst_no")));
                preferencesManager.setcontact_no(decryption(jsonObject.optString("branch_contact_no")).equals("nodata") ? "" : decryption(jsonObject.optString("branch_contact_no")));
                MyPOSMateApplication.isOpen = false;
                MyPOSMateApplication.isActiveQrcode = false;

                if (jsonObject.has("other_data")) {
                    JSONObject jsonObject1 = new JSONObject(decryption(jsonObject.optString("other_data")));
                    preferencesManager.setisUnionPaySelected(jsonObject1.optBoolean("UnionPay"));


                    if (jsonObject1.has("ConfigId"))
                        preferencesManager.setConfigId(jsonObject1.optString("ConfigId"));
                    if (jsonObject1.has("MerchantId"))
                        preferencesManager.setMerchantId(jsonObject1.optString("MerchantId"));
                    preferencesManager.setcnv_alipay_diaplay_and_add(jsonObject1.optBoolean("CnvAlipayDisplayAndAdd"));
                    preferencesManager.setcnv_alipay_diaplay_only(jsonObject1.optBoolean("CnvAlipayDisplayOnly"));
                    preferencesManager.setcnv_wechat_display_and_add(jsonObject1.optBoolean("CnvWeChatDisplayAndAdd"));
                    preferencesManager.setcnv_wechat_display_only(jsonObject1.optBoolean("CnvWeChatDisplayOnly"));
                    preferencesManager.setisAlipaySelected(jsonObject1.optBoolean("AlipaySelected"));
                    preferencesManager.setisWechatSelected(jsonObject1.optBoolean("WeChatSelected"));
                    preferencesManager.setcnv_wechat(jsonObject1.optString("WeChatValue"));

                    preferencesManager.setisWeChatScan(jsonObject1.optBoolean("WeChatScanQR"));
                    preferencesManager.setisAlipayScan(jsonObject1.optBoolean("AlipayScanQR"));
                    preferencesManager.setisUnionPayQrCodeDisplaySelected(jsonObject1.optBoolean("isUnionPayQrCodeDisplaySelected"));
                    preferencesManager.setcnv_uniqr(jsonObject1.optString("UnionPayQrValue"));
                    preferencesManager.setcnv_uplan(jsonObject1.optString("UplanValue"));


                    preferencesManager.setUnionPayQrSelected(jsonObject1.optBoolean("UnionPayQR"));
                    preferencesManager.setcnv_uni_display_and_add(jsonObject1.optBoolean("CnvUnionpayDisplayAndAdd"));
                    preferencesManager.setcnv_uni_display_only(jsonObject1.optBoolean("CnvUnionpayDisplayOnly"));
                    preferencesManager.setisUplanSelected(jsonObject1.optBoolean("Uplan"));
                    preferencesManager.setaggregated_singleqr(jsonObject1.optBoolean("AlipayWeChatPay"));
                    preferencesManager.setAlipayWechatQrSelected(jsonObject1.optBoolean("AlipayWeChatScanQR"));
                    preferencesManager.setisPrint(jsonObject1.optString("PrintReceiptautomatically"));
                    preferencesManager.setshowReference(jsonObject1.optString("ShowReference"));
                    preferencesManager.setisQR(jsonObject1.optBoolean("ShowPrintQR"));
                    preferencesManager.setisStaticQR(jsonObject1.optBoolean("DisplayStaticQR"));
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
                    preferencesManager.setisMembershipManual(jsonObject1.optBoolean("ShowMembershipManual"));
                    preferencesManager.setisMembershipHome(jsonObject1.optBoolean("ShowMembershipHome"));
                    preferencesManager.setTerminalIdentifier(jsonObject1.optString("TerminalIdentifier"));
                    preferencesManager.setPOSIdentifier(jsonObject1.optString("POSIdentifier"));
                    preferencesManager.setLaneIdentifier(jsonObject1.optString("LaneIdentifier"));
                    preferencesManager.setisLaneIdentifier(jsonObject1.optBoolean("isLaneIdentifier"));
                    preferencesManager.setisPOSIdentifier(jsonObject1.optBoolean("isPOSIdentifier"));
                    preferencesManager.setisTerminalIdentifier(jsonObject1.optBoolean("isTerminalIdentifier"));
                    preferencesManager.setTimeZoneId(jsonObject1.optString("TimeZoneId"));
                    preferencesManager.setTimeZone(jsonObject1.optString("TimeZone"));
                    preferencesManager.setisTimeZoneChecked(jsonObject1.optBoolean("isTimeZoneChecked"));
                }


                if (preferencesManager.isManual()) {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                } else {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                }

                break;

        }
    }


    public static class FourDigitCardFormatWatcher implements TextWatcher {

        // Change this to what you want... ' ', '-' etc..
        private static final char space = ' ';

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Remove spacing char
            if (s.length() > 0 && (s.length() % 5) == 0) {
                final char c = s.charAt(s.length() - 1);
                if (space == c) {
                    s.delete(s.length() - 1, s.length());
                }
            }
            // Insert char where needed.
            if (s.length() > 0 && (s.length() % 5) == 0) {
                char c = s.charAt(s.length() - 1);
                // Only if its a digit where there should be a space we insert a space
                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                    s.insert(s.length() - 1, String.valueOf(space));
                }
            }
        }
    }


    private static final int ITERATION_COUNT = 1000;
    private static final int KEY_LENGTH = 256;
    private static final String PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int PKCS5_SALT_LENGTH = 32;
    private static final String DELIMITER = "]";
    private static final SecureRandom random = new SecureRandom();
    private static final String password = "qspl123";

    public static String encryption1(String plaintext) {
        byte[] salt = generateSalt();
        SecretKey key = deriveKey(password, salt);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            byte[] iv = generateIv(cipher.getBlockSize());
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

            if (salt != null) {
                return String.format("%s%s%s%s%s",
                        toBase64(salt),
                        DELIMITER,
                        toBase64(iv),
                        DELIMITER,
                        toBase64(cipherText));
            }

            return String.format("%s%s%s",
                    toBase64(iv),
                    DELIMITER,
                    toBase64(cipherText));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryption1(String ciphertext) {
        String[] fields = ciphertext.split(DELIMITER);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Invalid encypted text format");
        }
        byte[] salt = fromBase64(fields[0]);
        byte[] iv = fromBase64(fields[1]);
        byte[] cipherBytes = fromBase64(fields[2]);
        SecretKey key = deriveKey(password, salt);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            byte[] plaintext = cipher.doFinal(cipherBytes);
            return new String(plaintext, "UTF-8");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] generateSalt() {
        byte[] b = new byte[PKCS5_SALT_LENGTH];
        random.nextBytes(b);
        return b;
    }

    private static byte[] generateIv(int length) {
        byte[] b = new byte[length];
        random.nextBytes(b);
        return b;
    }

    private static SecretKey deriveKey(String password, byte[] salt) {
        try {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM);
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toBase64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private static byte[] fromBase64(String base64) {
        return Base64.decode(base64, Base64.NO_WRAP);
    }

    public void callGetBranchDetails_new() {

        openProgressDialog();
        try {
            new OkHttpHandler(getActivity(), this, null, "GetBranchDetailsNew").execute(AppConstants.BASE_URL3 + AppConstants.GET_TERMINAL_CONFIG
                    + "?terminal_id=" + encryption(preferencesManager.getterminalId()));//encryption("47f17c5fe8d43843"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void _NewUser(JSONObject jsonObject) {
        try {
            if (jsonObject.optString("success").equals("true")) {
                preferencesManager.setaddress(decryption(jsonObject.optString("branch_address")).equals("nodata") ? "" : decryption(jsonObject.optString("branch_address")));
                if (jsonObject.optString("branch_email").equals("nodata")) {
                    preferencesManager.setcontact_email("");
                } else {
                    preferencesManager.setcontact_email(decryption(jsonObject.optString("branch_email")).equals("nodata") ? "" : decryption(jsonObject.optString("branch_email")));
                }
                preferencesManager.setcontact_no(decryption(jsonObject.optString("branch_contact_no")).equals("nodata") ? "" : decryption(jsonObject.optString("branch_contact_no")));
                preferencesManager.setmerchant_name(decryption(jsonObject.optString("branch_name")).equals("nodata") ? "" : decryption(jsonObject.optString("branch_name")));
                preferencesManager.setgstno(decryption(jsonObject.optString("gst_no")).equals("nodata") ? "" : decryption(jsonObject.optString("gst_no")));
                preferencesManager.setterminalId(decryption(jsonObject.optString("terminal_id")));


                JSONObject jsonObject1 = new JSONObject(decryption(jsonObject.optString("other_data")));
                if (jsonObject.has("other_data")) {
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

                }


            } else {

                if (jsonObject.has("config_id")) {
                    preferencesManager.setConfigId(decryption(jsonObject.optString("config_id")));
                }

                if (jsonObject.has("merchant_id")) {
                    preferencesManager.setMerchantId(decryption(jsonObject.optString("merchant_id")));
                }

                if (jsonObject.has("terminal_id")) {
                    preferencesManager.setterminalId(decryption(jsonObject.optString("terminal_id")));
                }

//                if (jsonObject.has("access_id")) {
//                    preferencesManager.setuniqueId(decryption(jsonObject.optString("access_id")));
//                }
            }


        } catch (Exception e) {
            if (jsonObject.has("config_id")) {
                preferencesManager.setConfigId(decryption(jsonObject.optString("config_id")));
            }

            if (jsonObject.has("merchant_id")) {
                preferencesManager.setMerchantId(decryption(jsonObject.optString("merchant_id")));
            }
            if (jsonObject.has("terminal_id")) {
                preferencesManager.setterminalId(decryption(jsonObject.optString("terminal_id")));
            }
//            if (jsonObject.has("access_id")) {
//                preferencesManager.setuniqueId(decryption(jsonObject.optString("access_id")));
//            }
        }
    }


}
