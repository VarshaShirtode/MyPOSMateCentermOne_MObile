package com.quagnitia.myposmate.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.fragments.RecyclerItemClickListener;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.prefs.Preferences;

public class RegistrationActivity extends Fragment implements View.OnClickListener, OnTaskCompleted {

    EditText edt_terminal_id, edt_config_id, edt_merchant_id, edt_access_id;
    Button btn_save, btn_already_registered, btn_exit,btn_connect;
    PreferencesManager preferenceManager;
    private String android_id;
    TreeMap<String, String> hashMapKeys;
    RelativeLayout rel_orders;
    TextView txtServer;
    private boolean isConnections=false;
    ArrayList connectionList;
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
        preferenceManager = PreferencesManager.getInstance(getActivity());

        initUI(view);
        initListener();
        view.findViewById(R.id.activity_main).setOnTouchListener((View v, MotionEvent event) -> {
            if (((DashboardActivity) getActivity()).mPopupWindow.isShowing()) {
                ((DashboardActivity) getActivity()).mPopupWindow.dismiss();
            }
            v.performClick();
            return false;
        });
        isConnections=true;
        callAuthToken();
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
        rel_orders=  getActivity().findViewById(R.id.rel_orders);
        rel_orders.setVisibility(View.GONE);
        txtServer=view.findViewById(R.id.txtServer);
        btn_connect=view.findViewById(R.id.btn_connect);

        txtServer.setText(preferenceManager.getBaseURL());
    }

     void showConnectionDialog() {
         Dialog dialog = new Dialog(getActivity());
         dialog.setCancelable(false);
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

         LayoutInflater lf = (LayoutInflater) (getActivity())
                 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View dialogview = lf.inflate(R.layout.dialog_connection, null);
         final RadioButton rbLive = dialogview.findViewById(R.id.rbLive);
         final RadioButton rbTest = dialogview.findViewById(R.id.rbTest);
         final RadioButton rb3 = dialogview.findViewById(R.id.rb3);
         final RadioButton rb4 = dialogview.findViewById(R.id.rb4);

         final String[] selectedValue = {""};
         LinearLayout linearLayout = dialogview.findViewById(R.id.linearLayout);
         RadioGroup radioGroup = new RadioGroup(getActivity());
         if(connectionList==null||connectionList.size()==0)
         {
             connectionList=new ArrayList();
             connectionList.add(0,preferenceManager.getBaseURL());
         }

         for (int i = 0; i < connectionList.size(); i++){
             final RadioButton[] radioButton = {new RadioButton(getActivity())};
             int j=i+1;
             radioButton[0].setText("Connection "+j);
             radioGroup.addView(radioButton[0]);

             int keyI = i;
             Log.v("SelectedPos","on dialog from preference "+preferenceManager.getBaseURL());

             if (preferenceManager.getBaseURL().equals(connectionList.get(i)))
             {
                 radioButton[0].setChecked(true);
             }
             int finalI = i;
             radioButton[0].setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (radioButton[0].isChecked()){
                         selectedValue[0] = ""+connectionList.get(keyI);
                         preferenceManager.setSelectedPosition(finalI);
                         // Toast.makeText(getActivity(), selectedValue[0], Toast.LENGTH_SHORT).show();
                         Log.v("SelectedPos","on Select "+preferenceManager.getSelectedPosition()+"  "+ selectedValue[0]);
                     }
                 }
             });
         }
         linearLayout.addView(radioGroup);

        /*if (preferenceManager.getBaseURL().equalsIgnoreCase("https://liveone.myposmate.com"))
        {
            rbLive.setChecked(true);
        } else if (preferenceManager.getBaseURL().equalsIgnoreCase("https://one.myposmate.com"))
         {
             rbTest.setChecked(true);
         }*//*else{
            rbTest.setChecked(true);
        }*/
      /*   Log.v("SelectedPos","Start"+preferenceManager.getSelectedPosition());
         ListView listConnection=dialogview.findViewById(R.id.listConnection);
         ConnectionAdapter connectionAdapter=new ConnectionAdapter(getActivity(),connectionList,preferenceManager.getSelectedPosition());
         listConnection.setAdapter(connectionAdapter);*/

/*
       listConnection.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                 @Override
                                                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                     RadioButton r = (RadioButton) view.findViewById(R.id.rbLive);
                                                     r.setText("" + connectionList.get(position));
                                                     r.setChecked(position == preferenceManager.getSelectedPosition());
                                                     r.setTag(position);
                                                     r.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View view) {
                                                             //selectedPosition = (Integer)view.getTag();
                                                             preferenceManager.setSelectedPosition((Integer) view.getTag());
                                                             Log.v("SelectedPos", "on Select " + preferenceManager.getSelectedPosition() + "  " + (Integer) view.getTag());
                                                             connectionAdapter.notifyDataSetChanged();
                                                         }
                                                     });
                                                 }
                                             });*/



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
             Log.v("SelectedPos","okk "+preferenceManager.getSelectedPosition());
             preferenceManager.setBaseURL(""+connectionList.get(preferenceManager.getSelectedPosition()));
             txtServer.setText(""+connectionList.get(preferenceManager.getSelectedPosition()));
             dialog.dismiss();
         });

         dialog.getWindow().setAttributes(lp);
         dialog.show();
     }

    public void callGetConnections() {
        openProgressDialog();
        try {
            new OkHttpHandler(getActivity(), this, null, "GetConnections").execute( AppConstants.GET_CONNECTIONS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void initListener() {
        btn_save.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_already_registered.setOnClickListener(this);
        btn_connect.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_exit:
                getActivity().finishAffinity();
                break;
            case R.id.btn_connect:
                /*isConnections=true;
                callAuthToken();*/
                showConnectionDialog();
                break;
            case R.id.btn_already_registered:
                AppConstants.isRegistered = false;
                preferenceManager.setuniqueId("");

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
                        edt_merchant_id.setError("Please enter branch id");

//                    if (edt_terminal_id.getText().toString().equals(""))
//                        edt_terminal_id.setError("Please enter terminal id");

                } else {
                    isConnections=false;
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
        preferenceManager.setConfigId(edt_config_id.getText().toString());
        preferenceManager.setMerchantId(edt_merchant_id.getText().toString());
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(preferenceManager.getBaseURL()+AppConstants.AUTH2);

    }


    ProgressDialog progress;

    public void openProgressDialog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading.......");
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

    public void callRegistartionAPI() {
        openProgressDialog();
        hashMapKeys.put("terminal_id",android_id);
        hashMapKeys.put("access_id", edt_access_id.getText().toString());
        hashMapKeys.put("config_id", edt_config_id.getText().toString());
        hashMapKeys.put("branch_id", edt_merchant_id.getText().toString());
        hashMapKeys.put("random_str", new Date().getTime() + "");
        preferenceManager.setuniqueId(edt_access_id.getText().toString());
        String url=preferenceManager.getBaseURL()+AppConstants.BASE_URL4;
        /*new OkHttpHandler(getActivity(), this, null, "Registration")
                .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.REGISTRATION + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());*/
        new OkHttpHandler(getActivity(), this, null, "Registration")
                .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL4 + AppConstants.REGISTRATION + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());
        Log.v("TOKENRESPONSE","URL Registration "+url);
    }

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
        if (progress.isShowing())
            progress.dismiss();
        if (result.equals("")) {
            Toast.makeText(getActivity(), "No data from server. Please check your network connection", Toast.LENGTH_LONG).show();
            if (TAG.equals("GetConnections"))
            {
                // isConnections=false;
                showConnectionDialog();
            }
            return;
        }
        //JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {

            case "AuthToken":
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferenceManager.setauthToken(jsonObject.optString("access_token"));
                    // callRegistartionAPI();
                    //Register to server
                    if (isConnections)
                    {
                        callGetConnections();
                        isConnections=false;
                    }else {
                        callRegistartionAPI();
                    }
                }
                break;
            case "Registration":
                JSONObject jsonObject1 = new JSONObject(result);
                if (jsonObject1.optBoolean("status")||
                        jsonObject1.optBoolean("success")) {
                    ((DashboardActivity) getActivity()).img_menu.setEnabled(true);
                    AppConstants.isRegistered = true;
                    preferenceManager.setMerchantId(edt_merchant_id.getText().toString());
                    preferenceManager.setConfigId(edt_config_id.getText().toString());
                    preferenceManager.setuniqueId(edt_access_id.getText().toString());
                    preferenceManager.setisRegistered(true);
                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.SETTINGS, null);
                } else {
                    Toast.makeText(getActivity(), jsonObject1.optString("message"), Toast.LENGTH_LONG).show();
                }

                break;

            case "GetConnections":
                JSONArray jsonarray = new JSONArray(result);
                connectionList=new ArrayList();
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String name = jsonobject.getString("connection");
                    Log.v("TOKENRESPONSE","List "+name);
                    connectionList.add(name);
                }
                Log.v("TOKENRESPONSE","List "+connectionList.size());
                if (!preferenceManager.isRegistered()) {
            /*if (preferenceManager.isEditConnection()==true)
            {
                ((DashboardActivity) getActivity()).img_menu.setEnabled(true);
            }*/
                    showConnectionDialog();

                }
                break;
        }
    }
}
