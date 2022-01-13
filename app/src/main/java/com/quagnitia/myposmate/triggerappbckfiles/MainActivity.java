/*
package com.quagnitia.myposmate.triggerappbckfiles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted, View.OnClickListener {
    //live
    public static final String POSMATE_V3_BASE_URL = "https://one.myposmate.com/api/v1/pos/";
    public static final String POSMATE_POSPAY = POSMATE_V3_BASE_URL + "posPay";
    public static final String POSMATE_GET_TXN = POSMATE_V3_BASE_URL + "getTransactionDetails";
    public static final String _POS_REQUEST = POSMATE_V3_BASE_URL + "requestTerminal";
    public static final String _POS_REQUEST_STATUS=POSMATE_V3_BASE_URL+"requestStatus";
    public static final String POSMATE_POS_REQUEST_STATUS = POSMATE_V3_BASE_URL + "updateTerminalRequest";
    public static final String CREATE_ORDER = "https://one.myposmate.com/api/v1/order/create";

//test
//    public static final String POSMATE_V3_BASE_URL = "http://test.myposmate.com/api/v3/pos/";
//    public static final String POSMATE_POS_REQUEST_STATUS = POSMATE_V3_BASE_URL + "posRequestStatus";
//    public static final String POSMATE_POSPAY = POSMATE_V3_BASE_URL + "posPay";
//    public static final String POSMATE_GET_TXN = POSMATE_V3_BASE_URL + "getTransactionDetails";
//    public static final String _POS_REQUEST = POSMATE_V3_BASE_URL + "posRequest";


    private EditText edt_merchant_id, edt_terminal_id, edt_config_id, edt_access_id, edt_amount;
    private Button btn_save, btn_exit, btn_pay, btn_create_order,btn_refund,btn_print;
    private PreferencesManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferenceManager = PreferencesManager.getInstance(this);
        initUI();
        initListener();
        autoFillValues();
        TextView version =  findViewById(R.id.version);
        try {
            version.setText(getResources().getString(R.string.MyPOSMate_Version) + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void autoFillValues() {
        edt_merchant_id.setText(preferenceManager.getMerchantId());
        edt_access_id.setText(preferenceManager.getuniqueId());
        edt_config_id.setText(preferenceManager.getConfigId());
        edt_terminal_id.setText(preferenceManager.getterminalId());
    }


    public void initUI() {
        edt_merchant_id = findViewById(R.id.edt_merchant_id);
        edt_terminal_id = findViewById(R.id.edt_terminal_id);
        edt_access_id = findViewById(R.id.edt_access_id);
        edt_config_id = findViewById(R.id.edt_config_id);
        edt_amount = findViewById(R.id.edt_amount);
        btn_create_order=findViewById(R.id.btn_create_order);

        btn_save = findViewById(R.id.btn_save);
        btn_print = findViewById(R.id.btn_print);
        btn_exit = findViewById(R.id.btn_exit);
        btn_pay = findViewById(R.id.btn_pay);
        btn_refund = findViewById(R.id.btn_refund);
    }

    public void initListener() {
        btn_exit.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_pay.setOnClickListener(this);
        btn_refund.setOnClickListener(this);
        btn_print.setOnClickListener(this);
        btn_create_order.setOnClickListener(this);
    }


    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
        JSONObject jsonObject = new JSONObject(result);
        if(progress!=null)
            progress.dismiss();
        switch (TAG) {

            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferenceManager.setauthToken(jsonObject.optString("access_token"));
                }
                if (isOrderCreate)
                {
                    isOrderCreate=false;
                    callCreateOrder();
                }

                break;

            case "CreateOrder":
                if(jsonObject.optBoolean("status"))
                {
                    Toast.makeText(this, "Order Created Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Order Failed To Create", Toast.LENGTH_SHORT).show();
                }
                break;


        }

    }
    ProgressDialog progress;

    public void openProgressDialog() {
        progress = new ProgressDialog(this);
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
        new OkHttpHandler(this, this, hashMap, "AuthToken").execute(preferenceManager.getBaseURL()+AppConstants.AUTH2);

    }

    public void callCreateOrder() {

        openProgressDialog();

        new OkHttpHandler(this, this, null, "CreateOrder").
                execute(CREATE_ORDER);

    }



    boolean isOrderCreate=false;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_create_order:
                if (preferenceManager.getMerchantId().equals("")) {
                    Toast.makeText(this, "Please enter your merchant id", Toast.LENGTH_SHORT).show();
                } else if (preferenceManager.getConfigId().equals("")) {
                    Toast.makeText(this, "Please enter your config id", Toast.LENGTH_SHORT).show();
                } else if (preferenceManager.getuniqueId().equals("")) {
                    Toast.makeText(this, "Please enter your access id", Toast.LENGTH_SHORT).show();
                } else {
                    isOrderCreate=true;
                    callAuthToken();
                }

                break;

            case R.id.btn_print:
                if (preferenceManager.getMerchantId().equals("")) {
                    Toast.makeText(this, "Please enter your merchant id", Toast.LENGTH_SHORT).show();
                } else if (preferenceManager.getConfigId().equals("")) {
                    Toast.makeText(this, "Please enter your config id", Toast.LENGTH_SHORT).show();
                } else if (preferenceManager.getuniqueId().equals("")) {
                    Toast.makeText(this, "Please enter your access id", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(MainActivity.this, PrintActivity.class);
                    startActivity(i);
                }

                break;

            case R.id.btn_save:
                if (!edt_terminal_id.getText().toString().equals("")
                        || !edt_config_id.getText().toString().equals("")
                        || !edt_access_id.getText().toString().equals("")
                        || !edt_merchant_id.getText().toString().equals("")) {
                    preferenceManager.setterminalId(edt_terminal_id.getText().toString());
                    preferenceManager.setConfigId(edt_config_id.getText().toString());
                    preferenceManager.setMerchantId(edt_merchant_id.getText().toString());
                    preferenceManager.setuniqueId(edt_access_id.getText().toString());
                    Toast.makeText(this, "Settings Saved Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_exit:
                System.exit(0);
                break;

            case R.id.btn_pay:
                if (preferenceManager.getMerchantId().equals("")) {
                    Toast.makeText(this, "Please enter your merchant id", Toast.LENGTH_SHORT).show();
                } else if (preferenceManager.getConfigId().equals("")) {
                    Toast.makeText(this, "Please enter your config id", Toast.LENGTH_SHORT).show();
                } else if (preferenceManager.getuniqueId().equals("")) {
                    Toast.makeText(this, "Please enter your access id", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(MainActivity.this, PaymentActivity.class);
                    startActivity(i);
                }

                break;
            case R.id.btn_refund:
                if (preferenceManager.getMerchantId().equals("")) {
                    Toast.makeText(this, "Please enter your merchant id", Toast.LENGTH_SHORT).show();
                } else if (preferenceManager.getConfigId().equals("")) {
                    Toast.makeText(this, "Please enter your config id", Toast.LENGTH_SHORT).show();
                } else if (preferenceManager.getuniqueId().equals("")) {
                    Toast.makeText(this, "Please enter your access id", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i1 = new Intent(MainActivity.this, RefundActivity.class);
                    startActivity(i1);
                }

                break;
        }
    }


    public void openPayScreen() {

    }

    public void openRefundScreen() {

    }


}
*/
