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
        hashMap.put("grant_type", "client_credentials");
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
        edt_merchant_name.setText(preferencesManager.getmerchant_name());
        edt_contact_no.setText(preferencesManager.getcontact_no());
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPattern1 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";


        edt_address.setText(preferencesManager.getaddress());
        edt_gst_number.setText(preferencesManager.getgstno());


        edt_lane_identifier = view.findViewById(R.id.edt_lane_identifier);
        edt_terminal_identifier = view.findViewById(R.id.edt_terminal_identifier);
        edt_pos_identifier = view.findViewById(R.id.edt_pos_identifier);


        edt_lane_identifier.setText(preferencesManager.getLaneIdentifier());
        edt_pos_identifier.setText(preferencesManager.getPOSIdentifier());
        edt_terminal_identifier.setText(preferencesManager.getTerminalIdentifier());


        chk_branch_name = view.findViewById(R.id.chk_branch_name);
        chk_branch_address = view.findViewById(R.id.chk_branch_address);
        chk_branch_contact_no = view.findViewById(R.id.chk_branch_contact_no);
        chk_branch_email = view.findViewById(R.id.chk_branch_email);
        chk_gst_no = view.findViewById(R.id.chk_gst_no);
        chk_lane_identifier = view.findViewById(R.id.chk_lane_identifier);
        chk_pos_identifier = view.findViewById(R.id.chk_pos_identifier);
        chk_terminal_identifier = view.findViewById(R.id.chk_terminal_identifier);


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

            ArrayList tipList=preferencesManager.getTipPercentage("Tip");
            jsonObject.put("DefaultTip1", tipList.get(0));
            jsonObject.put("DefaultTip2", tipList.get(1));
            jsonObject.put("DefaultTip3", tipList.get(2));
            jsonObject.put("DefaultTip4", tipList.get(3));
            jsonObject.put("DefaultTip5", tipList.get(4));
            jsonObject.put("SwitchOnTip", preferencesManager.isSwitchTip());

            jsonObject.put("DefaultTip1IsEnabled", preferencesManager.isTipDefault1());
            jsonObject.put("DefaultTip2IsEnabled", preferencesManager.isTipDefault2());
            jsonObject.put("DefaultTip3IsEnabled", preferencesManager.isTipDefault3());
            jsonObject.put("DefaultTip4IsEnabled", preferencesManager.isTipDefault4());
            jsonObject.put("DefaultTip5IsEnabled", preferencesManager.isTipDefault5());
            jsonObject.put("DefaultTip5IsEnabled", preferencesManager.isTipDefault5());
            jsonObject.put("CustomTip", preferencesManager.isTipDefaultCustom());

            jsonObject.put("CentrapaySelected", preferencesManager.isCentrapayMerchantQRDisplaySelected());
            jsonObject.put("CentrapayFeeValue", preferencesManager.getcnv_centrapay());
            jsonObject.put("CnvCentrapayDisplayAndAdd", preferencesManager.is_cnv_centrapay_display_and_add());
            jsonObject.put("CnvCentrapayDisplayOnly", preferencesManager.is_cnv_centrapay_display_only());

            jsonObject.put("PoliSelected", preferencesManager.isPoliSelected());
            jsonObject.put("PoliFeeValue", preferencesManager.getcnv_poli());
            jsonObject.put("CnvPoliDisplayAndAdd", preferencesManager.is_cnv_poli_display_and_add());
            jsonObject.put("CnvPoliDisplayOnly", preferencesManager.is_cnv_poli_display_only());

            jsonObject.put("accessId",preferencesManager.getuniqueId());
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
            jsonObject.put("cnv_unimerchantqrdisplay",preferencesManager.get_cnv_unimerchantqrdisplayLower());
            jsonObject.put("UplanValue", preferencesManager.getcnv_uplan());
            jsonObject.put("CnvUnionpayDisplayAndAdd", preferencesManager.is_cnv_uni_display_and_add());
            jsonObject.put("CnvUnionpayDisplayOnly", preferencesManager.is_cnv_uni_display_only());
            jsonObject.put("Uplan", preferencesManager.isUplanSelected());
            jsonObject.put("PrintReceiptautomatically", preferencesManager.getisPrint());
            jsonObject.put("ShowReference", preferencesManager.getshowReference());
            jsonObject.put("ShowPrintQR", preferencesManager.isQR());
            jsonObject.put("DisplayStaticQR", preferencesManager.isStaticQR());
            jsonObject.put("isDisplayLoyaltyApps",preferencesManager.isDisplayLoyaltyApps());
            jsonObject.put("isExternalInputDevice",preferencesManager.isExternalScan());

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

            jsonObject.put("CnvUPIQrMPMCloudDAADD",preferencesManager.cnv_up_upi_qrscan_mpmcloud_display_and_add());
            jsonObject.put("CnvUPIQrMPMCloudDOnly",preferencesManager.cnv_up_upi_qrscan_mpmcloud_display_only());
            jsonObject.put("CnvUPIQrMPMCloudValue",preferencesManager.getcnv_up_upiqr_mpmcloud_lower());
            jsonObject.put("CnvUPIQrMPMCloudValueHigher",preferencesManager.getCnv_up_upiqr_mpmcloud_higher());
            jsonObject.put("CnvUPIQRMPMCloudAmount",preferencesManager.getCnv_up_upiqr_mpmcloud_amount());
            jsonObject.put("cnv_unimerchantqrdisplay_higher",preferencesManager.get_cnv_unimerchantqrdisplayHigher());
            jsonObject.put("isMerchantDPARDisplay",preferencesManager.isMerchantDPARDisplay());

            hashMapKeys.clear();
            hashMapKeys.put("branchAddress", edt_address.getText().toString().equals("") ? encryption("nodata") : encryption(edt_address.getText().toString()));
            hashMapKeys.put("branchContactNo", edt_contact_no.getText().toString().equals("") ? encryption("nodata") : encryption(edt_contact_no.getText().toString()));
            hashMapKeys.put("branchName", edt_merchant_name.getText().toString().equals("") ? encryption("nodata") : encryption(edt_merchant_name.getText().toString()));
            hashMapKeys.put("branchEmail", edt_contact_email.getText().toString().equals("") ? "nodata" : encryption(edt_contact_email.getText().toString()));
            hashMapKeys.put("gstNo", edt_gst_number.getText().toString().equals("") ? encryption("nodata") : encryption(edt_gst_number.getText().toString()));
            hashMapKeys.put("terminalId", encryption(preferencesManager.getterminalId()));
            hashMapKeys.put("otherData", encryption(jsonObject.toString()));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("accessId", encryption(preferencesManager.getuniqueId()));
            hashMapKeys.put("configId", encryption(preferencesManager.getConfigId()));
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferencesManager.getauthToken());

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
            new OkHttpHandler(getActivity(), this, hashMap, "UpdateBranchDetails")
                    .execute(AppConstants.BASE_URL2 + AppConstants.SAVE_TERMINAL_CONFIG);


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
            hashMapKeys.put("terminalId", encryption(preferencesManager.getterminalId()));
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

                if (preferencesManager.isManual()) {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                } else {
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                }
                break;

            case "UpdateBranchDetails":
                callAuthToken();
                PreferencesManager preferencesManager = PreferencesManager.getInstance(getActivity());
                preferencesManager.setaddress(decryption(jsonObject.optString("branchAddress")).equals("nodata") ? "" : decryption(jsonObject.optString("branchAddress")));
                preferencesManager.setcontact_email(jsonObject.optString("branchEmail").equals("nodata") ? "" : decryption(jsonObject.optString("branchEmail")));
                preferencesManager.setmerchant_name(decryption(jsonObject.optString("branchName")).equals("nodata") ? "" : decryption(jsonObject.optString("branchName")));
                preferencesManager.setgstno(decryption(jsonObject.optString("gstNo")).equals("nodata") ? "" : decryption(jsonObject.optString("gstNo")));
                preferencesManager.setcontact_no(decryption(jsonObject.optString("branchContactNo")).equals("nodata") ? "" : decryption(jsonObject.optString("branchContactNo")));
                MyPOSMateApplication.isOpen = false;
                MyPOSMateApplication.isActiveQrcode = false;

                if (jsonObject.has("otherData")) {
                    JSONObject jsonObject1 = new JSONObject(decryption(jsonObject.optString("otherData")));
                    preferencesManager.setisUnionPaySelected(jsonObject1.optBoolean("UnionPay"));


                    if (jsonObject1.has("ConfigId"))
                        preferencesManager.setConfigId(jsonObject1.optString("ConfigId"));
                    if (jsonObject1.has("MerchantId"))
                        preferencesManager.setMerchantId(jsonObject1.optString("MerchantId"));

                    ArrayList tipList=new ArrayList();
                    tipList.add(jsonObject1.optString("DefaultTip1"));
                    tipList.add(jsonObject1.optString("DefaultTip2"));
                    tipList.add(jsonObject1.optString("DefaultTip3"));
                    tipList.add(jsonObject1.optString("DefaultTip4"));
                    tipList.add(jsonObject1.optString("DefaultTip5"));
                    preferencesManager.setTipPercentage("Tip",tipList);

                    preferencesManager.setisTipDefault1(jsonObject1.optBoolean("DefaultTip1IsEnabled"));
                    preferencesManager.setisTipDefault2(jsonObject1.optBoolean("DefaultTip2IsEnabled"));
                    preferencesManager.setisTipDefault3(jsonObject1.optBoolean("DefaultTip3IsEnabled"));
                    preferencesManager.setisTipDefault4(jsonObject1.optBoolean("DefaultTip4IsEnabled"));
                    preferencesManager.setisTipDefault5(jsonObject1.optBoolean("DefaultTip5IsEnabled"));
                    preferencesManager.setisTipDefaultCustom(jsonObject1.optBoolean("CustomTip"));
                    preferencesManager.setisSwitchTip(jsonObject1.optBoolean("SwitchOnTip"));

                    preferencesManager.setisCentrapayMerchantQRDisplaySelected(jsonObject1.optBoolean("CentrapaySelected"));
                    preferencesManager.setcnv_centrapay_display_and_add(jsonObject1.optBoolean("CnvCentrapayDisplayAndAdd"));
                    preferencesManager.setcnv_centrapay_display_only(jsonObject1.optBoolean("CnvCentrapayDisplayOnly"));
                    preferencesManager.setcnv_centrapay(jsonObject1.optString("CentrapayFeeValue"));
                    preferencesManager.setisPoliSelected(jsonObject1.optBoolean("PoliSelected"));
                    preferencesManager.setcnv_poli_display_and_add(jsonObject1.optBoolean("CnvPoliDisplayAndAdd"));
                    preferencesManager.setcnv_poli_display_only(jsonObject1.optBoolean("CnvPoliDisplayOnly"));
                    preferencesManager.setcnv_poli(jsonObject1.optString("PoliFeeValue"));
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
                    preferencesManager.set_cnv_unimerchantqrdisplayLower(jsonObject1.optString("cnv_unimerchantqrdisplay"));
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
                    preferencesManager.setisDisplayLoyaltyApps(jsonObject1.optBoolean("isDisplayLoyaltyApps"));
                    preferencesManager.setisExternalScan(jsonObject1.optBoolean("isExternalInputDevice"));
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


                    preferencesManager.setcnv_up_upi_qrscan_mpmcloud_display_and_add(jsonObject1.optBoolean("CnvUPIQrMPMCloudDAADD"));
                    preferencesManager.setcnv_up_upi_qrscan_mpmcloud_display_only(jsonObject1.optBoolean("CnvUPIQrMPMCloudDOnly"));
                    preferencesManager.setcnv_up_upiqr_mpmcloud_lower(jsonObject1.optString("CnvUPIQrMPMCloudValue"));
                    preferencesManager.setCnv_up_upiqr_mpmcloud_higher(jsonObject1.optString("CnvUPIQrMPMCloudValueHigher"));
                    preferencesManager.setCnv_up_upiqr_mpmcloud_amount(jsonObject1.optString("CnvUPIQRMPMCloudAmount"));
                    preferencesManager.set_cnv_unimerchantqrdisplayHigher(jsonObject1.optString("cnv_unimerchantqrdisplay_higher"));
                    preferencesManager.setisMerchantDPARDisplay(jsonObject1.optBoolean("isMerchantDPARDisplay"));


                }


                if (preferencesManager.isManual()) {
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
            hashMapKeys.put("terminalId", encryption(preferencesManager.getterminalId()));
//            hashMapKeys.put("terminalId", edt_terminal_id.getText().toString());
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, getActivity()));
            hashMapKeys.put("access_token", preferencesManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
//            new OkHttpHandler(getActivity(), this, null, "GetBranchDetailsNew").execute(AppConstants.BASE_URL3 + AppConstants.GET_TERMINAL_CONFIG
//                    + "?terminal_id=" + encryption(edt_terminal_id.getText().toString()));//encryption("47f17c5fe8d43843"));

            new OkHttpHandler(getActivity(), this, hashMap, "GetBranchDetailsNew").execute(AppConstants.BASE_URL2 + AppConstants.GET_TERMINAL_CONFIG);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void _NewUser(JSONObject jsonObject) throws Exception {
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
                preferencesManager.setterminalId(decryption(jsonObject.optString("terminal_id")));


                JSONObject jsonObject1 = new JSONObject(decryption(jsonObject.optString("otherData")));
                if (jsonObject.has("otherData")) {

                    ArrayList tipList=new ArrayList();
                    tipList.add(jsonObject1.optString("DefaultTip1"));
                    tipList.add(jsonObject1.optString("DefaultTip2"));
                    tipList.add(jsonObject1.optString("DefaultTip3"));
                    tipList.add(jsonObject1.optString("DefaultTip4"));
                    tipList.add(jsonObject1.optString("DefaultTip5"));
                    preferencesManager.setTipPercentage("Tip",tipList);

                    preferencesManager.setisTipDefault1(jsonObject1.optBoolean("DefaultTip1IsEnabled"));
                    preferencesManager.setisTipDefault2(jsonObject1.optBoolean("DefaultTip2IsEnabled"));
                    preferencesManager.setisTipDefault3(jsonObject1.optBoolean("DefaultTip3IsEnabled"));
                    preferencesManager.setisTipDefault4(jsonObject1.optBoolean("DefaultTip4IsEnabled"));
                    preferencesManager.setisTipDefault5(jsonObject1.optBoolean("DefaultTip5IsEnabled"));
                    preferencesManager.setisTipDefaultCustom(jsonObject1.optBoolean("CustomTip"));
                    preferencesManager.setisSwitchTip(jsonObject1.optBoolean("SwitchOnTip"));



                    preferencesManager.setisCentrapayMerchantQRDisplaySelected(jsonObject1.optBoolean("CentrapaySelected"));
                    preferencesManager.setcnv_centrapay_display_and_add(jsonObject1.optBoolean("CnvCentrapayDisplayAndAdd"));
                    preferencesManager.setcnv_centrapay_display_only(jsonObject1.optBoolean("CnvCentrapayDisplayOnly"));
                    preferencesManager.setcnv_centrapay(jsonObject1.optString("CentrapayFeeValue"));
                    preferencesManager.setisPoliSelected(jsonObject1.optBoolean("PoliSelected"));
                    preferencesManager.setcnv_poli_display_and_add(jsonObject1.optBoolean("CnvPoliDisplayAndAdd"));
                    preferencesManager.setcnv_poli_display_only(jsonObject1.optBoolean("CnvPoliDisplayOnly"));
                    preferencesManager.setcnv_poli(jsonObject1.optString("PoliFeeValue"));
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
                    preferencesManager.set_cnv_unimerchantqrdisplayLower(jsonObject1.optString("cnv_unimerchantqrdisplay"));
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
                    preferencesManager.setisDisplayLoyaltyApps(jsonObject1.optBoolean("isDisplayLoyaltyApps"));
                    preferencesManager.setisExternalScan(jsonObject1.optBoolean("isExternalInputDevice"));
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
                    preferencesManager.setcnv_up_upiqr_mpmcloud_lower(jsonObject1.optString("CnvUPIQrMPMCloudValue"));
                    preferencesManager.setCnv_up_upiqr_mpmcloud_higher(jsonObject1.optString("CnvUPIQrMPMCloudValueHigher"));
                    preferencesManager.setCnv_up_upiqr_mpmcloud_amount(jsonObject1.optString("CnvUPIQRMPMCloudAmount"));
                    preferencesManager.set_cnv_unimerchantqrdisplayHigher(jsonObject1.optString("cnv_unimerchantqrdisplay_higher"));
                    preferencesManager.setisMerchantDPARDisplay(jsonObject1.optBoolean("isMerchantDPARDisplay"));
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
