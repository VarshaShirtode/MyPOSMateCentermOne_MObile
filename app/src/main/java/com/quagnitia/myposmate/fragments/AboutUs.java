package com.quagnitia.myposmate.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import static com.quagnitia.myposmate.utils.AppConstants.isTerminalInfoDeleted;

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
    private static PreferencesManager preferenceManager;
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
        hashMap.put("grant_type", "client_credentials");
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(preferenceManager.getBaseURL()+AppConstants.AUTH2);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_about_us, container, false);
        preferenceManager = PreferencesManager.getInstance(getActivity());
        hashMapKeys = new TreeMap<>();
        mContext = getActivity();
        callAuthToken();
        try
        {
            initUI();
        }
        catch (Exception e)
        { }

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


    public void initUI() throws Exception{


        edt_merchant_name = view.findViewById(R.id.edt_merchant_name);
        edt_gst_number = view.findViewById(R.id.edt_gst_number);
        edt_contact_no = view.findViewById(R.id.edt_contact_no);
        edt_contact_email = view.findViewById(R.id.edt_contact_email);
        edt_address = view.findViewById(R.id.edt_address);
        btn_save = view.findViewById(R.id.btn_save);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        edt_merchant_name.setText(preferenceManager.getmerchant_name());
        edt_contact_no.setText(preferenceManager.getcontact_no());
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPattern1 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";


        edt_address.setText(preferenceManager.getaddress());
        edt_gst_number.setText(preferenceManager.getgstno());


        edt_lane_identifier = view.findViewById(R.id.edt_lane_identifier);
        edt_terminal_identifier = view.findViewById(R.id.edt_terminal_identifier);
        edt_pos_identifier = view.findViewById(R.id.edt_pos_identifier);


        edt_lane_identifier.setText(preferenceManager.getLaneIdentifier());
        edt_pos_identifier.setText(preferenceManager.getPOSIdentifier());
        edt_terminal_identifier.setText(preferenceManager.getTerminalIdentifier());


        chk_branch_name = view.findViewById(R.id.chk_branch_name);
        chk_branch_address = view.findViewById(R.id.chk_branch_address);
        chk_branch_contact_no = view.findViewById(R.id.chk_branch_contact_no);
        chk_branch_email = view.findViewById(R.id.chk_branch_email);
        chk_gst_no = view.findViewById(R.id.chk_gst_no);
        chk_lane_identifier = view.findViewById(R.id.chk_lane_identifier);
        chk_pos_identifier = view.findViewById(R.id.chk_pos_identifier);
        chk_terminal_identifier = view.findViewById(R.id.chk_terminal_identifier);


        if (preferenceManager.isLaneIdentifier()) {
            chk_lane_identifier.setChecked(true);
            chk_lane_identifier.setSelected(true);
        } else {
            chk_lane_identifier.setChecked(false);
            chk_lane_identifier.setSelected(false);
        }

        if (preferenceManager.isPOSIdentifier()) {
            chk_pos_identifier.setChecked(true);
            chk_pos_identifier.setSelected(true);
        } else {
            chk_pos_identifier.setChecked(false);
            chk_pos_identifier.setSelected(false);
        }

        if (preferenceManager.isTerminalIdentifier()) {
            chk_terminal_identifier.setChecked(true);
            chk_terminal_identifier.setSelected(true);
        } else {
            chk_terminal_identifier.setChecked(false);
            chk_terminal_identifier.setSelected(false);
        }


        if (preferenceManager.getBranchName().equals("true")) {
            chk_branch_name.setChecked(true);
            chk_branch_name.setSelected(true);
        } else {
            chk_branch_name.setChecked(false);
            chk_branch_name.setSelected(false);
        }


        if (preferenceManager.getBranchAddress().equals("true")) {
            chk_branch_address.setChecked(true);
            chk_branch_address.setSelected(true);
        } else {
            chk_branch_address.setChecked(false);
            chk_branch_address.setSelected(false);
        }


        if (preferenceManager.getBranchPhoneNo().equals("true")) {
            chk_branch_contact_no.setChecked(true);
            chk_branch_contact_no.setSelected(true);
        } else {
            chk_branch_contact_no.setChecked(false);
            chk_branch_contact_no.setSelected(false);
        }


        if (preferenceManager.getBranchEmail().equals("true")) {
            chk_branch_email.setChecked(true);
            chk_branch_email.setSelected(true);
        } else {
            chk_branch_email.setChecked(false);
            chk_branch_email.setSelected(false);
        }


        if (preferenceManager.getGSTNo().equals("true")) {
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
                    preferenceManager.setGSTNo("false");
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
//                    preferenceManager.setGSTNo("false");
//                    chk_gst_no.setChecked(false);
//                    return;
//                }

                if (chk_gst_no.isChecked()) {
                    chk_gst_no.setChecked(true);
                    preferenceManager.setGSTNo("true");
                } else {
                    //case 2
                    chk_gst_no.setChecked(false);
                    preferenceManager.setGSTNo("false");
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
                    preferenceManager.setGSTNo("false");
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
                    preferenceManager.setBranchEmail("false");
                    chk_branch_email.setChecked(false);
                    return;
                }


                if (chk_branch_email.isChecked()) {
                    chk_branch_email.setChecked(true);
                    preferenceManager.setBranchEmail("true");
                } else {
                    //case 2
                    chk_branch_email.setChecked(false);
                    preferenceManager.setBranchEmail("false");
                }
            }
        });


        chk_branch_contact_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?

                if (edt_contact_no.getText().toString().equals("")) {
                    preferenceManager.setBranchPhoneNo("false");
                    chk_branch_contact_no.setChecked(false);
                    return;
                }

                if (chk_branch_contact_no.isChecked()) {
                    chk_branch_contact_no.setChecked(true);
                    preferenceManager.setBranchPhoneNo("true");
                } else {
                    //case 2
                    chk_branch_contact_no.setChecked(false);
                    preferenceManager.setBranchPhoneNo("false");
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
                    preferenceManager.setBranchPhoneNo("false");
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
                    preferenceManager.setBranchAddress("false");
                    chk_branch_address.setChecked(false);
                    return;
                }


                if (chk_branch_address.isChecked()) {
                    chk_branch_address.setChecked(true);
                    preferenceManager.setBranchAddress("true");
                } else {
                    //case 2
                    chk_branch_address.setChecked(false);
                    preferenceManager.setBranchAddress("false");
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
                    preferenceManager.setBranchAddress("false");
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
                    preferenceManager.setBranchName("false");
                    chk_branch_name.setChecked(false);
                    return;
                }


                if (chk_branch_name.isChecked()) {
                    chk_branch_name.setChecked(true);
                    preferenceManager.setBranchName("true");
                } else {
                    //case 2
                    chk_branch_name.setChecked(false);
                    preferenceManager.setBranchName("false");
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
                    preferenceManager.setBranchName("false");
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
                    preferenceManager.setisTerminalIdentifier(false);
                    chk_terminal_identifier.setChecked(false);
                    return;
                }


                if (chk_terminal_identifier.isChecked()) {
                    chk_terminal_identifier.setChecked(true);
                    preferenceManager.setisTerminalIdentifier(true);
                } else {
                    //case 2
                    chk_terminal_identifier.setChecked(false);
                    preferenceManager.setisTerminalIdentifier(false);
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
                    preferenceManager.setisTerminalIdentifier(false);
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
                    preferenceManager.setisPOSIdentifier(false);
                    chk_pos_identifier.setChecked(false);
                    return;
                }


                if (chk_pos_identifier.isChecked()) {
                    chk_pos_identifier.setChecked(true);
                    preferenceManager.setisPOSIdentifier(true);
                } else {
                    //case 2
                    chk_pos_identifier.setChecked(false);
                    preferenceManager.setisPOSIdentifier(false);
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
                    preferenceManager.setisPOSIdentifier(false);
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
                    preferenceManager.setisLaneIdentifier(false);
                    chk_lane_identifier.setChecked(false);
                    return;
                }


                if (chk_lane_identifier.isChecked()) {
                    chk_lane_identifier.setChecked(true);
                    preferenceManager.setisLaneIdentifier(true);
                } else {
                    //case 2
                    chk_lane_identifier.setChecked(false);
                    preferenceManager.setisLaneIdentifier(false);
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
                    preferenceManager.setisLaneIdentifier(false);
                    chk_lane_identifier.setChecked(false);
                    chk_lane_identifier.setSelected(false);
                    return;
                }
            }
        });


//
        if (!preferenceManager.getcontact_email().equals("")) {
            if (preferenceManager.getcontact_email().trim().matches(emailPattern1)) {
                edt_contact_email.setText(preferenceManager.getcontact_email());
            } else if (preferenceManager.getcontact_email().trim().matches(emailPattern)) {
                edt_contact_email.setText(preferenceManager.getcontact_email());
            } else {
                edt_contact_email.setText(decryption(preferenceManager.getcontact_email()));
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
                    preferenceManager.setBranchEmail("false");
                    chk_branch_email.setChecked(false);
                    chk_branch_email.setSelected(false);
                    return;
                }
            }
        });

        //edt_contact_email.setText(preferenceManager.getcontact_email());
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
                    if (!edt_gst_number.getText().toString().equals("") && (edt_gst_number.getText().toString().trim().length() < 8 || edt_gst_number.getText().toString().trim().length() > 9)) {
                        flag = false;
                        Toast.makeText(getActivity(), "GST number should be 8 or 9 digits", Toast.LENGTH_SHORT).show();
                    }
//                    else if(edt_gst_number.getText().toString().length()==8 ||edt_gst_number.getText().toString().length()==9)
//                    {
                    if (flag) {

                        isUpdateDetails = true;
                        callAuthToken();

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
    public String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes()));
    }

    public String hextoString(String hexString) throws Exception
    {
        byte[] bytes=null;
        try
        {
            bytes = Hex.decodeHex(hexString.toCharArray());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new String(bytes, "UTF-8");
    }
    boolean isUpdateDetails = false;

    public void callUpdateBranchDetails() {

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

            jsonObject.put("accessId",preferenceManager.getuniqueId());
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
            jsonObject.put("CnvAlipayDisplayAndAdd", preferenceManager.is_cnv_alipay_display_and_add());
            jsonObject.put("CnvAlipayDisplayOnly", preferenceManager.is_cnv_alipay_display_only());
            jsonObject.put("isUnionPayQrCodeDisplaySelected", preferenceManager.isUnionPayQrCodeDisplaySelected());
            jsonObject.put("UnionPayQrValue", preferenceManager.getcnv_uniqr());
            jsonObject.put("cnv_unimerchantqrdisplay",preferenceManager.get_cnv_unimerchantqrdisplayLower());
            jsonObject.put("UplanValue", preferenceManager.getcnv_uplan());
            jsonObject.put("CnvUnionpayDisplayAndAdd", preferenceManager.is_cnv_uni_display_and_add());
            jsonObject.put("CnvUnionpayDisplayOnly", preferenceManager.is_cnv_uni_display_only());
            jsonObject.put("Uplan", preferenceManager.isUplanSelected());
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
            jsonObject.put("LaneIdentifier", edt_lane_identifier.getText().toString());
            jsonObject.put("TerminalIdentifier", edt_terminal_identifier.getText().toString());
            jsonObject.put("POSIdentifier", edt_pos_identifier.getText().toString());
            jsonObject.put("isUpdated", true);

            jsonObject.put("CnvUPIQrMPMCloudDAADD",preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add());
            jsonObject.put("CnvUPIQrMPMCloudDOnly",preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only());
            jsonObject.put("CnvUPIQrMPMCloudValue",preferenceManager.getcnv_up_upiqr_mpmcloud_lower());
            jsonObject.put("CnvUPIQrMPMCloudValueHigher",preferenceManager.getCnv_up_upiqr_mpmcloud_higher());
            jsonObject.put("CnvUPIQRMPMCloudAmount",preferenceManager.getCnv_up_upiqr_mpmcloud_amount());
            jsonObject.put("cnv_unimerchantqrdisplay_higher",preferenceManager.get_cnv_unimerchantqrdisplayHigher());
            jsonObject.put("isMerchantDPARDisplay",preferenceManager.isMerchantDPARDisplay());

            hashMapKeys.clear();
            hashMapKeys.put("branchAddress", edt_address.getText().toString().equals("") ? encryption("nodata") : encryption(edt_address.getText().toString()));
            hashMapKeys.put("branchContactNo", edt_contact_no.getText().toString().equals("") ? encryption("nodata") : encryption(edt_contact_no.getText().toString()));
            hashMapKeys.put("branchName", edt_merchant_name.getText().toString().equals("") ? encryption("nodata") : encryption(edt_merchant_name.getText().toString()));
            hashMapKeys.put("branchEmail", edt_contact_email.getText().toString().equals("") ? "nodata" : encryption(edt_contact_email.getText().toString()));
            hashMapKeys.put("gstNo", edt_gst_number.getText().toString().equals("") ? encryption("nodata") : encryption(edt_gst_number.getText().toString()));
            hashMapKeys.put("terminalId", encryption(preferenceManager.getterminalId()));
            hashMapKeys.put("otherData", encryption(jsonObject.toString()));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("accessId", encryption(preferenceManager.getuniqueId()));
            hashMapKeys.put("configId", encryption(preferenceManager.getConfigId()));
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
            new OkHttpHandler(getActivity(), this, hashMap, "UpdateBranchDetails")
                    .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.SAVE_TERMINAL_CONFIG);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String encryption(String strNormalText) throws Exception{
        String seedValue = "YourSecKey";
        String normalTextEnc = "";
        try {
            normalTextEnc = AESHelper.encrypt(seedValue, strNormalText);
//            normalTextEnc = AESHelper.encrypt(strNormalText, seedValue);
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
//            strDecryptedText = AESHelper.decrypt(strEncryptedText, seedValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }

    public void callDeleteTerminal() {
        openProgressDialog();
        try {
            hashMapKeys.clear();
            hashMapKeys.put("terminalId", encryption(preferenceManager.getterminalId()));
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
                    preferenceManager.setauthToken(jsonObject.optString("access_token"));
                }

                if (isCancel) {
                    isCancel = false;
                    callGetBranchDetails_new();
                }
                if (isUpdateDetails) {
                    isUpdateDetails = false;
                    if (isTerminalInfoDeleted)
                    {
                        isTerminalInfoDeleted=false;
                        callUpdateBranchDetails();
                    }

                    else
                    {callUpdateBranchDetails();
//                        callDeleteTerminal();
                    }

                }
                break;


            case "DeleteTerminal":
              //  if (jsonObject.optBoolean("success")) {
                    isUpdateDetails = true;
                    isTerminalInfoDeleted = true;
                    callAuthToken();
                //}
                break;


            case "GetBranchDetailsNew":
                _NewUser(jsonObject);
                callAuthToken();

                if (preferenceManager.isManual()) {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                } else {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                }
                break;

            case "UpdateBranchDetails":
                callAuthToken();
                PreferencesManager preferenceManager = PreferencesManager.getInstance(getActivity());
                preferenceManager.setaddress(decryption(jsonObject.optString("branchAddress")).equals("nodata") ? "" : decryption(jsonObject.optString("branchAddress")));
                preferenceManager.setcontact_email(jsonObject.optString("branchEmail").equals("nodata") ? "" : decryption(jsonObject.optString("branchEmail")));
                preferenceManager.setmerchant_name(decryption(jsonObject.optString("branchName")).equals("nodata") ? "" : decryption(jsonObject.optString("branchName")));
                preferenceManager.setgstno(decryption(jsonObject.optString("gstNo")).equals("nodata") ? "" : decryption(jsonObject.optString("gstNo")));
                preferenceManager.setcontact_no(decryption(jsonObject.optString("branchContactNo")).equals("nodata") ? "" : decryption(jsonObject.optString("branchContactNo")));
                MyPOSMateApplication.isOpen = false;
                MyPOSMateApplication.isActiveQrcode = false;

                if (jsonObject.has("otherData")) {
                    JSONObject jsonObject1 = new JSONObject(decryption(jsonObject.optString("otherData")));
                    preferenceManager.setisUnionPaySelected(jsonObject1.optBoolean("UnionPay"));


                    if (jsonObject1.has("ConfigId"))
                        preferenceManager.setConfigId(jsonObject1.optString("ConfigId"));
                    if (jsonObject1.has("MerchantId"))
                        preferenceManager.setMerchantId(jsonObject1.optString("MerchantId"));

                    ArrayList tipList=new ArrayList();
                    tipList.add(jsonObject1.optString("DefaultTip1"));
                    tipList.add(jsonObject1.optString("DefaultTip2"));
                    tipList.add(jsonObject1.optString("DefaultTip3"));
                    tipList.add(jsonObject1.optString("DefaultTip4"));
                    tipList.add(jsonObject1.optString("DefaultTip5"));
                    preferenceManager.setTipPercentage("Tip",tipList);

                    preferenceManager.setisTipDefault1(jsonObject1.optBoolean("DefaultTip1IsEnabled"));
                    preferenceManager.setisTipDefault2(jsonObject1.optBoolean("DefaultTip2IsEnabled"));
                    preferenceManager.setisTipDefault3(jsonObject1.optBoolean("DefaultTip3IsEnabled"));
                    preferenceManager.setisTipDefault4(jsonObject1.optBoolean("DefaultTip4IsEnabled"));
                    preferenceManager.setisTipDefault5(jsonObject1.optBoolean("DefaultTip5IsEnabled"));
                    preferenceManager.setisTipDefaultCustom(jsonObject1.optBoolean("CustomTip"));
                    preferenceManager.setisSwitchTip(jsonObject1.optBoolean("SwitchOnTip"));
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
                    preferenceManager.setisUnionPayQrCodeDisplaySelected(jsonObject1.optBoolean("isUnionPayQrCodeDisplaySelected"));
                    preferenceManager.setcnv_uniqr(jsonObject1.optString("UnionPayQrValue"));
                    preferenceManager.set_cnv_unimerchantqrdisplayLower(jsonObject1.optString("cnv_unimerchantqrdisplay"));
                    preferenceManager.setcnv_uplan(jsonObject1.optString("UplanValue"));


                    preferenceManager.setUnionPayQrSelected(jsonObject1.optBoolean("UnionPayQR"));
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
                    preferenceManager.setisMembershipManual(jsonObject1.optBoolean("ShowMembershipManual"));
                    preferenceManager.setisMembershipHome(jsonObject1.optBoolean("ShowMembershipHome"));
                    preferenceManager.setTerminalIdentifier(jsonObject1.optString("TerminalIdentifier"));
                    preferenceManager.setPOSIdentifier(jsonObject1.optString("POSIdentifier"));
                    preferenceManager.setLaneIdentifier(jsonObject1.optString("LaneIdentifier"));
                    preferenceManager.setisLaneIdentifier(jsonObject1.optBoolean("isLaneIdentifier"));
                    preferenceManager.setisPOSIdentifier(jsonObject1.optBoolean("isPOSIdentifier"));
                    preferenceManager.setisTerminalIdentifier(jsonObject1.optBoolean("isTerminalIdentifier"));
                    preferenceManager.setTimeZoneId(jsonObject1.optString("TimeZoneId"));
                    preferenceManager.setTimeZone(jsonObject1.optString("TimeZone"));
                    preferenceManager.setisTimeZoneChecked(jsonObject1.optBoolean("isTimeZoneChecked"));


                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(jsonObject1.optBoolean("CnvUPIQrMPMCloudDAADD"));
                    preferenceManager.setcnv_up_upi_qrscan_mpmcloud_display_only(jsonObject1.optBoolean("CnvUPIQrMPMCloudDOnly"));
                    preferenceManager.setcnv_up_upiqr_mpmcloud_lower(jsonObject1.optString("CnvUPIQrMPMCloudValue"));
                    preferenceManager.setCnv_up_upiqr_mpmcloud_higher(jsonObject1.optString("CnvUPIQrMPMCloudValueHigher"));
                    preferenceManager.setCnv_up_upiqr_mpmcloud_amount(jsonObject1.optString("CnvUPIQRMPMCloudAmount"));
                    preferenceManager.set_cnv_unimerchantqrdisplayHigher(jsonObject1.optString("cnv_unimerchantqrdisplay_higher"));
                    preferenceManager.setisMerchantDPARDisplay(jsonObject1.optBoolean("isMerchantDPARDisplay"));


                }


                if (preferenceManager.isManual()) {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                } else {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                }

                break;

        }
    }


    public void callGetBranchDetails_new() {

        openProgressDialog();
        try {
            hashMapKeys.clear();
            hashMapKeys.put("terminalId", encryption(preferenceManager.getterminalId()));
//            hashMapKeys.put("terminalId", edt_terminal_id.getText().toString());
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
//            new OkHttpHandler(getActivity(), this, null, "GetBranchDetailsNew").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL5 + AppConstants.GET_TERMINAL_CONFIG
//                    + "?terminal_id=" + encryption(edt_terminal_id.getText().toString()));//encryption("47f17c5fe8d43843"));

            new OkHttpHandler(getActivity(), this, hashMap, "GetBranchDetailsNew").execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.GET_TERMINAL_CONFIG);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void _NewUser(JSONObject jsonObject) throws Exception {
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
                preferenceManager.setterminalId(decryption(jsonObject.optString("terminal_id")));


                JSONObject jsonObject1 = new JSONObject(decryption(jsonObject.optString("otherData")));
                if (jsonObject.has("otherData")) {

                    ArrayList tipList=new ArrayList();
                    tipList.add(jsonObject1.optString("DefaultTip1"));
                    tipList.add(jsonObject1.optString("DefaultTip2"));
                    tipList.add(jsonObject1.optString("DefaultTip3"));
                    tipList.add(jsonObject1.optString("DefaultTip4"));
                    tipList.add(jsonObject1.optString("DefaultTip5"));
                    preferenceManager.setTipPercentage("Tip",tipList);

                    preferenceManager.setisTipDefault1(jsonObject1.optBoolean("DefaultTip1IsEnabled"));
                    preferenceManager.setisTipDefault2(jsonObject1.optBoolean("DefaultTip2IsEnabled"));
                    preferenceManager.setisTipDefault3(jsonObject1.optBoolean("DefaultTip3IsEnabled"));
                    preferenceManager.setisTipDefault4(jsonObject1.optBoolean("DefaultTip4IsEnabled"));
                    preferenceManager.setisTipDefault5(jsonObject1.optBoolean("DefaultTip5IsEnabled"));
                    preferenceManager.setisTipDefaultCustom(jsonObject1.optBoolean("CustomTip"));
                    preferenceManager.setisSwitchTip(jsonObject1.optBoolean("SwitchOnTip"));
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
                    preferenceManager.set_cnv_unimerchantqrdisplayHigher(jsonObject1.optString("cnv_unimerchantqrdisplay_higher"));
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

//                if (jsonObject.has("access_id")) {
//                    preferenceManager.setuniqueId(decryption(jsonObject.optString("access_id")));
//                }
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
//            if (jsonObject.has("access_id")) {
//                preferenceManager.setuniqueId(decryption(jsonObject.optString("access_id")));
//            }
        }
    }


}
