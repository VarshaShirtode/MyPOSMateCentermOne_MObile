package com.quagnitia.myposmate.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.TreeMap;

public class OrderDetailsActivity extends AppCompatActivity implements OnTaskCompleted {
    RecyclerView recycler_view;
    PreferencesManager preferencesManager;
    EditText edt_order_number, edt_order_time,
            edt_name, edt_phoneno, edt_email, edt_pickup, edt_id, edt_status, edt_ready_by;

    TextView tv_subtotal,tv_total,tv_surcharge,tv_delivery,tv_discount;
    ProgressDialog progress;
    TreeMap<String, String> hashMapKeys;
    boolean isDetails = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        hashMapKeys = new TreeMap<>();
        preferencesManager = PreferencesManager.getInstance(this);
        initUI();
        isDetails = true;
        callAuthToken();

    }

    public void initUI() {
        recycler_view = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());


        edt_order_number = findViewById(R.id.edt_order_number);
        edt_order_time = findViewById(R.id.edt_order_time);
        edt_name = findViewById(R.id.edt_name);
        edt_phoneno = findViewById(R.id.edt_phoneno);
        edt_email = findViewById(R.id.edt_email);
        edt_pickup = findViewById(R.id.edt_pickup);
        edt_id = findViewById(R.id.edt_id);
        edt_status = findViewById(R.id.edt_status);
        edt_ready_by = findViewById(R.id.edt_ready_by);


        tv_subtotal=findViewById(R.id.tv_subtotal);
        tv_total=findViewById(R.id.tv_total);
        tv_surcharge=findViewById(R.id.tv_surcharge);
        tv_delivery=findViewById(R.id.tv_delivery);
        tv_discount=findViewById(R.id.tv_discount);




        findViewById(R.id.btn_status).setOnClickListener((View) -> showStatusDialog());
        findViewById(R.id.btn_cancel).setOnClickListener((View) -> finish());
    }

    int status=0;
    Button btn_complete,btn_rejected,btn_review,btn_accepted;
    TextView tv_time;
    public void showStatusDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater lf = (LayoutInflater) (this)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.status_dialog, null);
        Button btn_save_and_ok = dialogview.findViewById(R.id.btn_save);
        Button btn_cancel_and_close = dialogview.findViewById(R.id.btn_cancel);

         btn_complete = dialogview.findViewById(R.id.btn_complete);
         btn_rejected = dialogview.findViewById(R.id.btn_rejected);
         btn_review = dialogview.findViewById(R.id.btn_review);
         btn_accepted = dialogview.findViewById(R.id.btn_accept);

        tv_time=dialogview.findViewById(R.id.tv_time);
        tv_time.setText(time);

        Spinner spinner = dialogview.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.min_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btn_complete.setOnClickListener((View v) -> {
            status=3;
            resetChkDrawable(status);
        });
        btn_rejected.setOnClickListener((View v) -> {
            status=0;
            resetChkDrawable(status);
        });
        btn_review.setOnClickListener((View v) -> {
            status=1;
            resetChkDrawable(status);
        });
        btn_accepted.setOnClickListener((View v) -> {
            status=2;
            resetChkDrawable(status);
        });



        btn_cancel_and_close.setOnClickListener((View v) -> {
            dialog.dismiss();
        });

        btn_save_and_ok.setOnClickListener((View v) -> {
            isUpdate=true;
            callAuthToken();
            dialog.dismiss();
        });
        dialog.setContentView(dialogview);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

    }

    private void resetChkDrawable(int status)
    {

        btn_complete.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        btn_rejected.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        btn_review.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
        btn_accepted.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);

        switch (status)
        {
            case 0://rejected
                btn_rejected.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.chk_mark),null);
                break;
            case 1://review
                btn_review.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.chk_mark),null);
                break;
            case 2://accepted
                btn_accepted.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.chk_mark),null);
                break;
            case 3: //completed
                btn_complete.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.chk_mark),null);
                break;
        }
    }



    public void initListener() {

    }

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
        new OkHttpHandler(this, this, hashMap, "AuthToken").execute(AppConstants.AUTH);

    }
    public void callTimeStamp() {
        try {
            new OkHttpHandler(this, this, null, "TimeStamp").execute(AppConstants.BASE_URL3 + AppConstants.GET_CURRENT_DATETIME + "?access_token=" + preferencesManager.getauthToken());//"http://worldclockapi.com/api/json/NZST/now");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void callOrderDetails() {
        openProgressDialog();
        hashMapKeys.clear();
        hashMapKeys.put("branchID", preferencesManager.getMerchantId());
        hashMapKeys.put("configID", preferencesManager.getConfigId());
        hashMapKeys.put("hubID", getIntent().getStringExtra("hub_id"));
        hashMapKeys.put("myPOSMateOrderID", getIntent().getStringExtra("myPOSMateOrderID"));
        hashMapKeys.put("random_str", new Date().getTime() + "");
        new OkHttpHandler(this, this, null, "OrderDetails")
                .execute(AppConstants.BASE_URL3 + AppConstants.ORDER_DETAILS
                        + MD5Class.generateSignatureString(hashMapKeys, this) + "&access_token=" + preferencesManager.getauthToken());

    }

boolean isUpdate=false;
    public void callUpdateStatus(int status) {
        openProgressDialog();
        hashMapKeys.clear();
        hashMapKeys.put("branchID", preferencesManager.getMerchantId());
        hashMapKeys.put("configID", preferencesManager.getConfigId());
        hashMapKeys.put("hubID", "946");
        hashMapKeys.put("status",status+"");
        hashMapKeys.put("myPOSMateOrderID", getIntent().getStringExtra("myPOSMateOrderID"));
        hashMapKeys.put("random_str", new Date().getTime() + "");
        hashMapKeys.put("signature", MD5Class.generateSignatureString(hashMapKeys, this));
        hashMapKeys.put("access_token",preferencesManager.getauthToken());

        HashMap<String,String> hashMap=new HashMap();
        hashMap.putAll(hashMapKeys);
//        new OkHttpHandler(this, this, hashMap, "OrderUpdate")
//                .execute(AppConstants.BASE_URL3 + AppConstants.UPDATE_ORDER_DETAILS
//                        + MD5Class.generateSignatureString(hashMapKeys, this) + "&access_token=" + preferencesManager.getauthToken());

        new OkHttpHandler(this, this, hashMap, "OrderUpdate")
                .execute(AppConstants.BASE_URL3 + AppConstants.UPDATE_ORDER_DETAILS);


    }

    private void _parseAuthCodeResponse(JSONObject jsonObject) {
        if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
            preferencesManager.setauthToken(jsonObject.optString("access_token"));
        }

        if (isDetails) {

         callTimeStamp();
        }

        if(isUpdate)
        {
            isUpdate=false;
            callUpdateStatus(status);
        }

    }

    String time="";
    public void callTimeStampConversion(String s) {
        try {
            JSONObject jsonObjectTimeNZ = new JSONObject(s);
            String TimeStamp = jsonObjectTimeNZ.optString("time");
            String ss1[] = TimeStamp.split("T");


            SimpleDateFormat df1 = new SimpleDateFormat("hh:mm aa");
            df1.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat df2 = new SimpleDateFormat("hh:mm aa");
            df2.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));
            Date d = df1.parse(ss1[0] + " " + ss1[1]);
            String datetime = df2.format(d);
            String ss[] = datetime.split(" ");
            time=ss[1]+" "+ss[2];
//
//            edt_start_datetime.setText(ss[0]);
//            edt_end_datetime.setText(ss[0]);
//            edt_start_time.setText("00:00:00");
//            edt_end_time.setText(ss[1]);
//            isListingCalled = true;
            callAuthToken();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void _parseOrderDetailsResponse(JSONObject jsonObject) {
        JSONObject jsonObjectData=jsonObject.optJSONObject("data");
        edt_order_number.setText(jsonObjectData.optString("orderNumber"));
        edt_order_time.setText(jsonObjectData.optString("myPOSMateTime"));
        edt_name.setText(jsonObjectData.optString("customerName"));
        edt_phoneno.setText(jsonObjectData.optString("customerPhone"));
        edt_email.setText(jsonObjectData.optString("customerMail"));
        edt_pickup.setText(jsonObjectData.optString("pickupFrom"));
        edt_id.setText(jsonObjectData.optString("myPOSMateOrderID"));
        edt_status.setText(jsonObjectData.optString("status"));
        edt_ready_by.setText(jsonObjectData.optString("modifiedOn"));

        tv_subtotal.setText(jsonObjectData.optString("subtotal").equals("null")?"0.0":
                "$ "+jsonObjectData.optString("subtotal"));
        tv_total.setText(jsonObjectData.optString("total").equals("null")?"0.0":
                "$ "+jsonObjectData.optString("total"));
        tv_surcharge.setText(jsonObjectData.optString("surchargeAmount").equals("null")?"0.0":
                "$ "+jsonObjectData.optString("surchargeAmount"));
        tv_delivery.setText(jsonObjectData.optString("deliveryAmount").equals("null")?"0.0":
                "$ "+jsonObjectData.optString("deliveryAmount"));
        tv_discount.setText(jsonObjectData.optString("discountAmount").equals("null")?"0.0":
                "$ "+jsonObjectData.optString("discountAmount"));

        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(this, jsonObjectData.optJSONArray("orderItems"));
        recycler_view.setAdapter(orderDetailsAdapter);
    }


    JSONObject jsonObject;

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
        if (progress != null)
            progress.dismiss();

        jsonObject = new JSONObject(result);
        switch (TAG) {
            case "AuthToken":
                _parseAuthCodeResponse(jsonObject);
                break;

            case "OrderDetails":
                _parseOrderDetailsResponse(jsonObject);
                break;

            case "TimeStamp":
                if (isDetails) {
                    isDetails = false;
                    callOrderDetails();
                }
                callTimeStampConversion(result);
                break;

            case "OrderUpdate":
                if(jsonObject.optBoolean("status"))
                {
                    Toast.makeText(this, "Order Updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent i=getIntent();
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(this, "Failed to update order", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
}
