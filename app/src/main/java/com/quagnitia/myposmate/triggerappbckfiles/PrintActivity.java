/*
package com.quagnitia.myposmate.triggerappbckfiles;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import static android.util.Base64.NO_WRAP;
import static com.example.triggerdemoapp.MainActivity.POSMATE_POS_REQUEST_STATUS;
import static com.example.triggerdemoapp.MainActivity._POS_REQUEST;

public class PrintActivity extends AppCompatActivity implements View.OnClickListener ,OnTaskCompleted{
    private Button btn_print, btn_cancel;
    private PreferencesManager preferencesManager;
    private EditText  edt_amount,edt_reference_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        preferencesManager = PreferencesManager.getInstance(this);
        callAuthToken();
        initUI();
        initListener();
    }
    public void callAuthToken() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
        new OkHttpHandler(this, this, hashMap, "AuthToken").execute(AppConstants.AUTH);

    }
    public void initUI() {
        btn_print = findViewById(R.id.btn_print);
        btn_cancel = findViewById(R.id.btn_cancel);
        edt_amount = findViewById(R.id.edt_amount);
        edt_reference_id = findViewById(R.id.edt_reference_id);
    }

    public void initListener() {
        btn_print.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
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

Dialog mDialog;
    boolean isCancelled=false;
    public void openProgressDialog1() {
        mDialog = new Dialog(this);

        // no tile for the dialog
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.progress_bar_dialog);
        ProgressBar mProgressBar = (ProgressBar) mDialog.findViewById(R.id.progress_bar);
        //  mProgressBar.getIndeterminateDrawable().setColorFilter(context.getResources()
        // .getColor(R.color.material_blue_gray_500), PorterDuff.Mode.SRC_IN);
        TextView progressText = (TextView) mDialog.findViewById(R.id.progress_text);
        Button btn_cancel=(Button)mDialog.findViewById(R.id.btn_cancel);
        progressText.setText("Please wait while printing");
        progressText.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isCancelled=true;
                mDialog.dismiss();
                finish();
            }
        });

        // you can change or add this line according to your need
        mProgressBar.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }



    String text=" MY AWESOME STORE\n" +
            "123 STORE ST\n" +
            "store@store.com\n" +
            "www.store.com\n" +
            "             \n" +
            "Order Number:XXXXXXXXXXXX\n" +
            "Date:        XX/XX/XXXX XX:XX\n" +
            "------------------------------\n" +
            "Qty   Product            Total\n" +
            "------------------------------\n" +
            "1     Product 1      10.00\n" +
            "2     Product 2      165.00\n" +
            "3     Product 3     18.00\n" +
            "------------------------------\n" +
            "                              \n" +
            "  Some extra information to add to the footer of  \n" +
            "                   this docket.                   \n" +
            "                                                  \n" +
            "GST (10.00%):                AUD XX.XX\n" +
            "Total amount (excl. GST):    AUD XX.XX\n" +
            "Total amount (incl. GST):    AUD XX.XX\n" +
            "                                                  \n" +
            "Amount Received:    AUD XX.XX\n" +
            "Amount Returned:    AUD XX.XX\n" +
            "                                                  \n" +
            "Final bits of text at the very base of a     \n" +
            "docket. This text wraps around as well!     ";

    public void callPOSRequestAPI() {
        openProgressDialog();
        TreeMap<String, String> hashMapKeys = new TreeMap<>();
        hashMapKeys.put("branch_id", preferencesManager.getMerchantId());
        hashMapKeys.put("config_id", preferencesManager.getConfigId());
//        hashMapKeys.put("terminal_id",preferencesManager.getterminalId());
        hashMapKeys.put("random_str", new Date().getTime() + "");
        hashMapKeys.put("request_type", "PRINT");
        byte[] data = new byte[0];
        try {
            data = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, NO_WRAP);
        hashMapKeys.put("body", base64);

        new OkHttpHandler(PrintActivity.this, this, null, "_POS_REQUEST").
                execute(_POS_REQUEST
                        +
                        MD5Class.generateSignatureString(hashMapKeys, this)
                        + "&access_token=" + preferencesManager.getauthToken());

    }

    public void callPOSRequestStatusAPI(String request_id) {
        TreeMap<String, String> hashMapKeys = new TreeMap<>();
//        hashMapKeys.put("terminal_id",preferencesManager.getterminalId());
        hashMapKeys.put("branch_id", preferencesManager.getMerchantId());
        hashMapKeys.put("config_id", preferencesManager.getConfigId());
//        hashMapKeys.put("terminal_id",preferencesManager.getterminalId());
        hashMapKeys.put("request_id", request_id);
        hashMapKeys.put("executed","false");
        hashMapKeys.put("random_str", new Date().getTime() + "");



        new OkHttpHandler(PrintActivity.this, this, null, POSMATE_POS_REQUEST_STATUS).
                execute(POSMATE_POS_REQUEST_STATUS
                        +
                        MD5Class.generateSignatureString(hashMapKeys, this)
                        + "&access_token=" + preferencesManager.getauthToken());

    }




boolean isPrint=false;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;

            case R.id.btn_print:
                isPrint=true;
                callAuthToken();

                break;
        }
    }


    public static String request_id="";
    boolean isUpdate=false;
    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
        JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {

            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferencesManager.setauthToken(jsonObject.optString("access_token"));
                }
                if (isPrint)
                {
                    isPrint=false;
                    callPOSRequestAPI();
                }
                if(isUpdate)
                {
                    isUpdate=false;

                    callPOSRequestStatusAPI(request_id);
                }
                break;

            case "_POS_REQUEST":
                if (progress.isShowing())
                    progress.dismiss();
                if (jsonObject.optString("status").equalsIgnoreCase("true") ||
                        jsonObject.optString("status").equalsIgnoreCase("TRADE_SUCCESS")) {
                    preferencesManager.setreference_id(jsonObject.optString("reference_id"));
                    request_id=jsonObject.optString("requestId");
                    openProgressDialog1();
                    isUpdate=true;
                    callAuthToken();

                }

                break;

            case POSMATE_POS_REQUEST_STATUS:
                if(jsonObject.optBoolean("status"))
                {
                    if (mDialog.isShowing())
                        mDialog.dismiss();
                    showAlert("Print Successful");
                    Toast.makeText(this, "Print Successful", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!isCancelled)
                    callPOSRequestStatusAPI(request_id);
                }
                break;
        }
    }

    private void showAlert(String message) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater lf = (LayoutInflater) (this)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogview = lf.inflate(R.layout.message, null);
        TextView body = (TextView) dialogview
                .findViewById(R.id.dialogBody);
        body.setText(message);
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


    }
}
*/
