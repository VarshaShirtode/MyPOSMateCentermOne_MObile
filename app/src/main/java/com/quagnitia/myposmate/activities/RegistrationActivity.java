package com.quagnitia.myposmate.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class RegistrationActivity extends Fragment implements View.OnClickListener, OnTaskCompleted {
    EditText edt_terminal_id, edt_config_id, edt_merchant_id, edt_access_id;
    Button btn_save, btn_already_registered, btn_exit;
    PreferencesManager preferencesManager;
    private String android_id;
    TreeMap<String, String> hashMapKeys;

    public RegistrationActivity() {
        // Required empty public constructor
    }

    public static RegistrationActivity newInstance() {
        return new RegistrationActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        View view = inflater.inflate(R.layout.activity_registration, container, false);
        hashMapKeys = new TreeMap<>();
        ((DashboardActivity) getActivity()).img_menu.setEnabled(false);
        preferencesManager = PreferencesManager.getInstance(getActivity());
        initUI(view);
        initListener();
        view.findViewById(R.id.activity_main).setOnTouchListener((View v, MotionEvent event) -> {

            if (((DashboardActivity) getActivity()).mPopupWindow.isShowing()) {
                ((DashboardActivity) getActivity()).mPopupWindow.dismiss();
            }
            v.performClick();
            return false;
        });
        return view;
    }


    public void initUI(View view) {
        android_id = android.provider.Settings.Secure.getString(getContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        edt_terminal_id = view.findViewById(R.id.edt_terminal_id);
        edt_config_id = view.findViewById(R.id.edt_config_id);
        edt_merchant_id = view.findViewById(R.id.edt_merchant_id);
        edt_access_id = view.findViewById(R.id.edt_access_id);
        btn_save = view.findViewById(R.id.btn_save);
        btn_already_registered = view.findViewById(R.id.btn_already_registered);
        btn_exit = view.findViewById(R.id.btn_exit);
    }

    public void initListener() {
        btn_save.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_already_registered.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_exit:
               getActivity().finishAffinity();
                break;

            case R.id.btn_already_registered:
                AppConstants.isRegistered = false;
                preferencesManager.setuniqueId("");
                ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.SETTINGS, null);
                break;


            case R.id.btn_save:

                if (edt_config_id.getText().toString().equals("")
                        || edt_access_id.getText().toString().equals("")
                        || edt_merchant_id.getText().toString().equals("")

                ) {

                    if (edt_config_id.getText().toString().equals(""))
                        edt_config_id.setError("Please enter config id");

                    if (edt_access_id.getText().toString().equals(""))
                        edt_access_id.setError("Please enter access id");

                    if (edt_merchant_id.getText().toString().equals(""))
                        edt_merchant_id.setError("Please enter merchant id");

//                    if (edt_terminal_id.getText().toString().equals(""))
//                        edt_terminal_id.setError("Please enter terminal id");

                } else {

                    callAuthToken();
//                    callRegistartionAPI();

                }

                break;

        }
    }


    public void callAuthToken() {
        openProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
//        hashMap.put("username", AppConstants.CLIENT_ID);
//        hashMap.put("password",AppConstants.CLIENT_SECRET);
        preferencesManager.setConfigId(edt_config_id.getText().toString());
        preferencesManager.setMerchantId(edt_merchant_id.getText().toString());
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.AUTH);

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

    public void callRegistartionAPI() {
        openProgressDialog();
        hashMapKeys.put("terminal_id", android_id);
        hashMapKeys.put("access_id", edt_access_id.getText().toString());
        hashMapKeys.put("config_id", edt_config_id.getText().toString());
        hashMapKeys.put("branch_id", edt_merchant_id.getText().toString());
        hashMapKeys.put("random_str", new Date().getTime() + "");
        preferencesManager.setuniqueId(edt_access_id.getText().toString());
        new OkHttpHandler(getActivity(), this, null, "Registration")
                .execute(AppConstants.BASE_URL2 + AppConstants.REGISTRATION + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferencesManager.getauthToken());
    }

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
        if (progress.isShowing())
            progress.dismiss();
        if (result.equals("")) {
            Toast.makeText(getActivity(), "No data from server.", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {

            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferencesManager.setauthToken(jsonObject.optString("access_token"));
                    callRegistartionAPI();
                }
                break;
            case "Registration":
                if (jsonObject.optBoolean("status")||
                        jsonObject.optBoolean("success")) {
                    ((DashboardActivity) getActivity()).img_menu.setEnabled(true);
                    AppConstants.isRegistered = true;
                    preferencesManager.setMerchantId(edt_merchant_id.getText().toString());
                    preferencesManager.setConfigId(edt_config_id.getText().toString());
                    preferencesManager.setuniqueId(edt_access_id.getText().toString());
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.SETTINGS, null);
                } else {
                    Toast.makeText(getActivity(), jsonObject.optString("message"), Toast.LENGTH_LONG).show();

                }

                break;
        }
    }
}
