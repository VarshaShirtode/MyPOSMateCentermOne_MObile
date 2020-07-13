package com.quagnitia.myposmate.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.aidl.printer.PrintDataObject;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.constant.DeviceErrorCode;
import com.centerm.smartpos.util.LogUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.arke.TransactionNames;
import com.quagnitia.myposmate.arke.VASCallsArkeBusiness;
import com.quagnitia.myposmate.centrum.ThirtConst;
import com.quagnitia.myposmate.utils.AESHelper;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

public class TransactionDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnTaskCompleted {

    private Button btn_refund, btn_close, btn_refund_uni, btn_print, btn_void;
    private ProgressDialog progress;
    private EditText edt_amount, edt_description, edt_password;
    private PreferencesManager preferenceManager;
    private RecyclerView recycler_view;
    private VASCallsArkeBusiness vasCallsArkeBusiness;
    TreeMap<String, String> hashMapKeys;
    private String JSON_DATA = "";
    JSONObject jsonObjectSale;
    Intent intentCen = new Intent();
    String packageName = "com.centerm.dynaminpayinskate";
    String activityName = "org.skate.pay.component.PayEntry";
    ComponentName comp = new ComponentName(packageName, activityName);
    private static final int REQ_PAY_SALE = 100;
    boolean isStart = false;
    public String refund_time = "", refund_trade_no = "";
    public String referenecno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_transaction_details);

        hashMapKeys = new TreeMap<>();
        preferenceManager = PreferencesManager.getInstance(this);
        dialog = new AlertDialog.Builder(TransactionDetailsActivity.this)
                .setNegativeButton(getString(R.string.cancel), null)
                .setCancelable(false)
                .create();
        initUI();
        initListener();
        isStart = true;
        callAuthToken();

    }


    @Override
    protected void onResume() {
        super.onResume();
        bindService();
    }

    public void openProgressDialog() {
        progress = new ProgressDialog(TransactionDetailsActivity.this);
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }


    public void callUnionPayStatus(String json_data, String status) {
        openProgressDialog();
        try {
            String s = "{\n" +
                    "  \"head\": {\n" +
                    "    \"version\": \"V1.2.0\"\n" +
                    "  },\n" +
                    "  \"body\":";

            JSONObject jsonObject = new JSONObject(json_data);
            if (jsonObject.has("responseCodeThirtyNine")) {
                if (jsonObject.has("responseCodeThirtyNine") && jsonObject.optString("responseCodeThirtyNine").equals("00")) {
                    if (jsonObject.optString("transactionType").equals("SALE") ||
                            jsonObject.optString("transactionType").equals("COUPON_SALE") ||
                            jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_SALE")
                    ) {
                        status = "20";
                    } else if (jsonObject.optString("transactionType").equals("VOID") ||
                            jsonObject.optString("transactionType").equals("REFUND") ||
                            jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_VOID") ||
                            jsonObject.optString("transactionType").equals("COUPON_VOID")) {
                        status = "19"; //set 22 to 19 in case of void on 28/02/2019
                    }

                }
            } else {
                status = "23";
                Toast.makeText(this, jsonObject.optString("responseMessage"), Toast.LENGTH_LONG).show();

            }
            jsonObject.put("status_id", status);
            json_data = jsonObject.toString();
            preferenceManager.setreference_id(jsonObject.optString("orderNumber"));
            hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferenceManager.getterminalId());
            hashMapKeys.put("system", preferenceManager.getterminalId());
            hashMapKeys.put("channel", "UNION_PAY");
            hashMapKeys.put("access_id", preferenceManager.getuniqueId());
            hashMapKeys.put("config_id", preferenceManager.getConfigId());
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("rate", "0");
            hashMapKeys.put("currency", "NZD");
            hashMapKeys.put("transaction_id", jsonObjectTransactionDetails.optJSONObject("payment").optString("id"));

            if (jsonObject.optString("transactionType").equals("SALE") ||
                    jsonObject.optString("transactionType").equals("COUPON_SALE") ||
                    jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_SALE")
            ) {
                hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(jsonObject.optString("amount"))));
                hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(jsonObject.optString("amount"))));
            } else if (jsonObjectTransactionDetails.optJSONObject("payment").has("serverResponse")) {
                if (!jsonObjectGatewayResponse.equals("")) {
                    hashMapKeys.put("grand_total", roundTwoDecimals(Double.parseDouble(jsonObjectGatewayResponse.optString("amount"))));
                    hashMapKeys.put("receiptAmount", roundTwoDecimals(Double.parseDouble(jsonObjectGatewayResponse.optString("amount"))));
                    hashMapKeys.put("refund_amount", refund_amount + "");
                    hashMapKeys.put("refund_trade_no", jsonObject.optString("referenceNumber") + "");
                }
            }

            hashMapKeys.put("reference_id", jsonObject.optString("referenceNumber"));
            hashMapKeys.put("server_response", android.util.Base64.encodeToString((s + json_data + "}").getBytes(), Base64.NO_WRAP));
            hashMapKeys.put("trade_no", jsonObject.optString("referenceNumber"));
            hashMapKeys.put("is_success", true + "");
            hashMapKeys.put("is_payment", false + "");

            String s2 = "", s1 = "";
            int i1 = 0;
            Iterator<String> iterator = hashMapKeys.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (i1 != hashMapKeys.size() - 1) {
                    s2 = s2 + key + "=" + hashMapKeys.get(key) + "&";
                    s1 = s1 + key + "=" + hashMapKeys.get(key) + "&";
                } else {
                    s2 = s2 + key + "=" + hashMapKeys.get(key);
                    s1 = s1 + key + "=" + hashMapKeys.get(key);
                }
                i1++;
            }
            s2 = s2 + AppConstants.CLIENT_ID + PreferencesManager.getInstance(this).getauthToken();//.getuniqueId();
            String signature = MD5Class.MD5(s2);
            new OkHttpHandler(TransactionDetailsActivity.this, this, null, "saveTransaction")
                    .execute(AppConstants.BASE_URL2 + AppConstants.SAVETRANSACTIONUNIONPAY + "?" + s1 + "&signature=" + signature + "&access_token=" + preferenceManager.getauthToken());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callTransactionDetails() {
        openProgressDialog();
        hashMapKeys.clear();
        hashMapKeys.put("access_id", preferenceManager.getuniqueId());
        hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
        hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
        hashMapKeys.put("config_id", preferenceManager.getConfigId());
        hashMapKeys.put("reference_id", getIntent().getStringExtra("reference_id"));
        hashMapKeys.put("random_str", new Date().getTime() + "");
        new OkHttpHandler(TransactionDetailsActivity.this, this, null, "TransactionDetails")
                .execute(AppConstants.BASE_URL2 + AppConstants.GET_TRANSACTION_DETAILS + MD5Class.generateSignatureString(hashMapKeys, this) + "&access_token=" + preferenceManager.getauthToken());
    }

    public void callRefundApi() {
        openProgressDialog();
        try {
            hashMapKeys.clear();
            hashMapKeys.put("access_id", preferenceManager.getuniqueId());
            hashMapKeys.put("branch_id", preferenceManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferenceManager.getterminalId().toString());
            hashMapKeys.put("config_id", preferenceManager.getConfigId());
            hashMapKeys.put("reference_id", getIntent().getStringExtra("reference_id"));
            hashMapKeys.put("refund_amount", edt_amount.getText().toString());
            hashMapKeys.put("refund_password", edt_password.getText().toString());
            hashMapKeys.put("refund_reason", edt_description.getText().toString());
            hashMapKeys.put("random_str", new Date().getTime() + "");

            new OkHttpHandler(TransactionDetailsActivity.this, this, null, "refundNow")
                    .execute(AppConstants.BASE_URL2 + AppConstants.REFUND + MD5Class.generateSignatureString(hashMapKeys, this) + "&access_token=" + preferenceManager.getauthToken());


            callAuthToken();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LinearLayout ll;
    Button print1, btn_close1;

    public void initUI() {
        vasCallsArkeBusiness = new VASCallsArkeBusiness(TransactionDetailsActivity.this);
        btn_print = findViewById(R.id.btn_print);
        btn_void = findViewById(R.id.btn_void);
        btn_refund_uni = findViewById(R.id.btn_refund_uni);
        btn_void.setVisibility(View.GONE);
        ll = findViewById(R.id.ll);
        print1 = findViewById(R.id.btn_print1);
        btn_close1 = findViewById(R.id.btn_close1);
        btn_close1.setVisibility(View.GONE);
        print1.setVisibility(View.GONE);
        ll.setVisibility(View.GONE);
        btn_refund = findViewById(R.id.btn_refund);
        btn_close = findViewById(R.id.btn_close);
        edt_amount = findViewById(R.id.edt_amount);
        edt_description = findViewById(R.id.edt_description);
        edt_password = findViewById(R.id.edt_password);
        recycler_view = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(TransactionDetailsActivity.this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

    }


    public void initListener() {
        btn_close.setOnClickListener(this);
        btn_refund.setOnClickListener(this);
        btn_print.setOnClickListener(this);
        print1.setOnClickListener(this);
        btn_close1.setOnClickListener(this);
        btn_void.setOnClickListener(this);
        btn_refund_uni.setOnClickListener(this);
    }

    public void beginVoid(JSONObject jsonObject1) {

        hideSoftInput();

        try {
            jsonObjectSale = new JSONObject();
            jsonObjectSale.put("originalVoucherNumber", jsonObject1.optString("voucherNumber"));
            jsonObjectSale.put("orderNumber", jsonObject1.optString("orderNumber"));//String.valueOf(c.getTimeInMillis()));
            jsonObjectSale.put("needAppPrinted", false);
            Toast.makeText(this, jsonObject1.optString("transactionType"), Toast.LENGTH_SHORT).show();
            if (jsonObject1.optString("transactionType").equals(ThirtConst.TransType.COUPON_SALE)) {
                doTransaction(ThirtConst.TransType.COUPON_VOID, jsonObjectSale);
            } else if (jsonObject1.optString("transactionType").equals("UPI_SCAN_CODE_SALE")) {
                jsonObjectSale.put("transactionType", ThirtConst.TransType.VOID);
                jsonObjectSale.put("qrcode", jsonObject1.optString("voucherNumber"));
                doTransaction(ThirtConst.TransType.SCAN_VOID, jsonObjectSale);
            } else
                doTransaction(ThirtConst.TransType.VOID, jsonObjectSale);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void doTransaction(String interfaceId, JSONObject jsonObject) {

        intentCen.setComponent(comp);
        Bundle bundle = new Bundle();
        switch (interfaceId) {
            case ThirtConst.TransType.VOID:
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.VOID);
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_VOUCHER_NO, jsonObject.optString("originalVoucherNumber"));
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObject.optString("orderNumber"));
                break;
            case ThirtConst.TransType.SCAN_VOID:
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.VOID);
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_VOUCHER_NO, jsonObject.optString("originalVoucherNumber"));
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObject.optString("orderNumber"));
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_SCAN_AUTH_PAY_CODE, jsonObject.optString("originalVoucherNumber"));
                break;
            case ThirtConst.TransType.COUPON_VOID:
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.COUPON_VOID);
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_VOUCHER_NO, jsonObject.optString("originalVoucherNumber"));
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObject.optString("orderNumber"));
                break;
        }

        intentCen.putExtras(bundle);
        startActivityForResult(intentCen, REQ_PAY_SALE);
    }

    /**
     * Hide soft keyboard
     * <p>
     * 隐藏软键盘
     */
    private void hideSoftInput() {
        if (TransactionDetailsActivity.this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) TransactionDetailsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(TransactionDetailsActivity.this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    String refund_amount = "";

    @Override
    public void onClick(View v) {
        //   callAuthToken();
        switch (v.getId()) {

            case R.id.btn_void:
                if(newjson.has("Remaining Amount"))
                {
                    Double voidAmount=Double.parseDouble(newjson.optString("Remaining Amount"));
                    Double originalAmount=Double.parseDouble(newjson.optString("Original Amount"));
                    if(voidAmount!=0.00&&voidAmount<originalAmount)
                    {
                        refund_amount=voidAmount+"";
                        Toast.makeText(TransactionDetailsActivity.this, "Void cannot be performed on partial amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        refund_amount=newjson.optString("Payment Amount")+"";
                    }

                }
                else
                    refund_amount=newjson.optString("Payment Amount")+"";
                beginVoid(jsonObjectGatewayResponse);
                break;

            case R.id.btn_print:
            case R.id.btn_print1:
                try {
                    print();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_refund_uni:
                final Dialog dialog = new Dialog(TransactionDetailsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                LayoutInflater lf = (LayoutInflater) (TransactionDetailsActivity.this)
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogview = lf.inflate(R.layout.refund_layout_unionpay, null);
                dialog.setContentView(dialogview);


                final EditText editAmount = dialogview.findViewById(R.id.edt_amount_unionpay);
                Button btn_close = dialogview.findViewById(R.id.btn_close);
                Button btn_refund =  dialogview.findViewById(R.id.btn_refund);
                Double originalAmount=Double.parseDouble(newjson.optString("Receipt Amount"));
                if(newjson.has("Remaining Amount"))
                {
                    Double remainingAmount=Double.parseDouble(newjson.optString("Remaining Amount"));
                    if(remainingAmount!=0.00&&remainingAmount<originalAmount)
                    {
                       editAmount.setText(roundTwoDecimals(remainingAmount));
                    }
                }

                btn_close.setOnClickListener(View-> {
                        dialog.dismiss();
                });

                btn_refund.setOnClickListener(View-> {

                        try {
                            refund_amount = editAmount.getText().toString();

                            if(Double.parseDouble(refund_amount)>originalAmount)
                            {
                                Toast.makeText(TransactionDetailsActivity.this, "Amount enter is greater than the receipt amount", Toast.LENGTH_SHORT).show();
                                return;
                            }


                            jsonObjectSale = new JSONObject();
                            jsonObjectSale.put("transactionType", "REFUND");
                            jsonObjectSale.put("amount", editAmount.getText().toString());
                            jsonObjectSale.put("originalReferenceNumber", jsonObjectGatewayResponse.optString("referenceNumber"));
                            jsonObjectSale.put("orderNumber", jsonObjectGatewayResponse.optString("orderNumber"));
                            String s[] = TransactionDetailsAdapter.transactionDate.split(" ");
                            String s1[] = s[0].split("-");
                            jsonObjectSale.put("originalTransactionDate", s1[1] + s1[2]);


                            intentCen.setComponent(comp);
                            Bundle bundle = new Bundle();
                            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.REFUND);
                            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_REFERENCE_NO, jsonObjectSale.optString("originalReferenceNumber"));
                            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_TRANS_DATE, jsonObjectSale.optString("originalTransactionDate"));
                            bundle.putDouble(ThirtConst.RequestTag.THIRD_PATH_TRANS_AMOUNT, jsonObjectSale.optDouble("amount"));
                            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORDER_NO, jsonObjectSale.optString("orderNumber"));

                            if (jsonObjectGatewayResponse.optString("transactionType").equals("UPI_SCAN_CODE_SALE")) {
                                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_SCAN_AUTH_PAY_CODE, jsonObjectGatewayResponse.optString("qrcode"));
                            }


                            intentCen.putExtras(bundle);
                            startActivityForResult(intentCen, REQ_PAY_SALE);
                            callAuthToken();
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                });


                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;
                dialog.getWindow().setAttributes(lp);
                dialog.show();

                break;
            case R.id.btn_refund:
                if (!edt_amount.getText().toString().equals("") && Double.parseDouble(edt_amount.getText().toString()) > Double.parseDouble(amountTotal)) {
                    Toast.makeText(TransactionDetailsActivity.this, "Please enter the amount less than  or equal to your grand total", Toast.LENGTH_SHORT).show();
                } else if (edt_amount.getText().toString().equals("")) {
                    Toast.makeText(TransactionDetailsActivity.this, "Please enter the refund amount", Toast.LENGTH_SHORT).show();
                } else if (edt_description.getText().toString().equals("")) {
                    Toast.makeText(TransactionDetailsActivity.this, "Please enter the refund reason", Toast.LENGTH_SHORT).show();
                } else if (edt_password.getText().toString().equals("")) {
                    Toast.makeText(TransactionDetailsActivity.this, "Please enter the refund password", Toast.LENGTH_SHORT).show();
                } else if ((newjson.has("Remaining Amount") && !newjson.optString("Remaining Amount").equals("0.00")) && Double.parseDouble(edt_amount.getText().toString()) >
//                        Double.parseDouble(jsonObjectTransactionDetails.optString("remaining_amount"))) {
                        Double.parseDouble(newjson.optString("Remaining Amount"))
                        && newjson.optString("Transaction Type").equals("REFUND")
                ) {
                    Toast.makeText(TransactionDetailsActivity.this, "Entered amount is greater than remaining amount.", Toast.LENGTH_SHORT).show();
                } else {
                    callRefundApi();
                }
                break;
            case R.id.btn_close:
            case R.id.btn_close1:
                if (DashboardActivity.isExternalApp) {
                    DashboardActivity.isExternalApp = false;
                    TransactionDetailsActivity.isReturnFromTransactionDetails = true;
                    jsonObjectReturnResult = new JSONObject();
                }
                finish();
                break;
        }
    }

    public void callAuthToken() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
        new OkHttpHandler(TransactionDetailsActivity.this, this, hashMap, "AuthToken").execute(AppConstants.AUTH);

    }

    public void beginRefund(JSONObject jsonObject) {

        hideSoftInput();

        try {
            doTransactionRefund(TransactionNames.REFUND.name(), jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * According to the choice, began to do the transaction
     * <p>
     * 根据选择，开始做交易
     *
     * @param interfaceId
     */
    private void doTransactionRefund(String interfaceId, JSONObject jsonObject) {
        if (TransactionNames.SALE_BY_SDK.name().equals(interfaceId)) {
            vasCallsArkeBusiness.doTransaction(interfaceId, jsonObject, this);
        } else {
            vasCallsArkeBusiness.doTransaction(interfaceId, jsonObject, this);
        }
    }


    public void returnDataToExternalApp() {
        try {

            //added for external apps 12/5/2019
            int REQ_PAY_SALE = 100;
            if (DashboardActivity.isExternalApp) {
                DashboardActivity.isExternalApp = false;
                getIntent().putExtra("result", jsonObject.toString());
                setResult(REQ_PAY_SALE, getIntent());
                finish();
                return;
            }  //added for external apps


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    JSONObject newjson = new JSONObject();
    JSONObject jsonObject, jsonObjectTransactionDetails, jsonO, jsonObjectGatewayResponse;
    public static String unionpay = "";
    public static boolean isRefundUnionPaySuccess = false;


    public static boolean isReturnFromTransactionDetails = false;
    public static JSONObject jsonObjectReturnResult = null;


    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {

        if (result.equals("")) {
            if (progress != null && progress.isShowing())
                progress.dismiss();
            Toast.makeText(TransactionDetailsActivity.this, "No data from server.", Toast.LENGTH_LONG).show();
            return;
        }
        if (progress != null && progress.isShowing())
            progress.dismiss();


        JSONObject jsonObject11 = new JSONObject(result);
        if ((jsonObject11.optString("interfaceId").equals("VOID") ||
                jsonObject11.optString("interfaceId").equals("REFUND")
                || jsonObject11.optString("interfaceId").equals("UPI_SCAN_CODE_VOID"))
                && jsonObject11.optString("responseMessage").equals("User Cancelled") && jsonObject11.optString("responseCode").equals("6")) {
            return;
        }

        if (TAG.equals("TransactionDetails"))
            jsonObject = new JSONObject(result);
        else if (TAG.equals("AuthToken"))
            jsonO = new JSONObject(result);
        else
            jsonObject = new JSONObject(result);


        switch (TAG) {


            case "AuthToken":
                if (jsonO.has("access_token") && !jsonO.optString("access_token").equals("")) {
                    preferenceManager.setauthToken(jsonO.optString("access_token"));
                }

                if (isStart) {
                    isStart = false;
                    callTransactionDetails();
                }
                break;

            case "saveTransaction":
                callAuthToken();
                if (progress != null && progress.isShowing())
                    progress.dismiss();


                if (jsonObject.optBoolean("status")) {
                    unionpay = "";
                    refund_time = "";
                    refund_trade_no = "";
                    preferenceManager.setunion_pay_resp("");
                    Toast.makeText(TransactionDetailsActivity.this, "Transaction status updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(TransactionDetailsActivity.this, "Transaction status not updated", Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;
            case "Arke":
                callAuthToken();
                try {

                    if (jsonObject.has("responseCodeThirtyNine")) {
                        if (jsonObject.has("responseCodeThirtyNine") && jsonObject.optString("responseCodeThirtyNine").equals("00")) {
                            if (jsonObject.optString("transactionType").equals("SALE") ||
                                    jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_SALE") ||
                                    jsonObject.optString("transactionType").equals("COUPON_SALE")) {
                                //added on 28/02/2019
                                unionpay = result;
                                callUnionPayStatus(result, "true");
                            } else if (jsonObject.optString("transactionType").equals("VOID") ||
                                    jsonObject.optString("transactionType").equals("REFUND") ||
                                    jsonObject.optString("transactionType").equals("UPI_SCAN_CODE_VOID") ||
                                    jsonObject.optString("transactionType").equals("COUPON_VOID")) {
                                //added on 28/02/2019
                                unionpay = result;
                                callUnionPayStatus(result, "true");

                            }

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {

                    //added for external apps 12/5/2019
                    if (DashboardActivity.isExternalApp) {
                        isReturnFromTransactionDetails = true;
                        DashboardActivity.isExternalApp = false;
                        jsonObjectReturnResult = jsonObject;
                        finish();
                        return;
                    }  //added for external apps


                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;


            case "TransactionDetails":

                if (jsonObject.optBoolean("status")) {
                    JSON_DATA = jsonObject.toString();
                    jsonObjectTransactionDetails = jsonObject;
                    parseTransactionDetailsResponse(jsonObject);

                } else {
                    Toast.makeText(TransactionDetailsActivity.this, "Transaction Details Not Found", Toast.LENGTH_SHORT).show();
                }
                break;

          /*  case "TransactionDetails1":

                if (jsonObject.has("error") && jsonObject.optString("error").equals("invalid_token")) {
                    callTransactionDetails();
                    return;
                }

                openProgressDialog();
                callAuthToken();

                JSON_DATA = jsonObject.toString();
                jsonObjectTransactionDetails = jsonObject;
                try {
                    JSONObject jsonObject1 = new JSONObject(JSON_DATA);

                    String s = "";
                    for (int i = 0; i < jsonObject1.length(); i++) {
                        if (!jsonObject1.names().optString(i).equals("merchant_info")) {
                            if (!jsonObject1.names().optString(i).equals("code_url")) {
                                if (!jsonObject1.names().optString(i).equals("refunds")) {
                                    if (!jsonObject1.names().optString(i).equals("code")) {
                                        if (!jsonObject1.names().optString(i).equals("status_id")) {
                                            newjson.put(jsonObject1.names().optString(i), jsonObject1.optString(jsonObject1.names().optString(i)));
                                        }
                                    }
                                }

                            }
                        }

                    }

                    JSONObject newjson1 = newjson;
                    JSONObject json = new JSONObject();
                    String jsonString[] = {"status", "channel", "rmb_amount",
                            "currency", "grandtotal", "receipt_amount", "gmt_payment", "gmt_payment", "remaining_amount",
                            "refunded_amount", "config_id", "reference_id", "trade_no", "increment_id", "merchant_id", "message", "status_description"};

                    for (int i = 0; i < jsonString.length; i++) {

                        for (int j = 0; j < newjson1.length(); j++) {
                            if (newjson1.names().getString(j).equals(jsonString[i])) {
                                switch (newjson1.names().getString(j)) {
                                    case "status":
                                        if (newjson1.optString("channel").equals("UNION_PAY")) {
                                            if (newjson1.has("server_response")) {
                                                JSONObject js = new JSONObject(newjson1.optString("server_response"));
                                                if (js.optJSONObject("body").optString("transactionType").equals("SALE") ||
                                                        js.optJSONObject("body").optString("transactionType").equals("UPI_SCAN_CODE_SALE") ||

                                                        js.optJSONObject("body").optString("transactionType").equals("COUPON_SALE")) {
                                                    json.put("Transaction Successful", newjson1.optString("status"));
                                                    json.put("Transaction Type", js.optJSONObject("body").optString("transactionType"));
                                                    json.put("Date And Time", newjson1.optString("created_on"));
                                                    //added on 28/02/2019
                                                    refund_time = newjson1.optString("created_on");

                                                } else if (js.optJSONObject("body").optString("transactionType").equals("VOID") ||
                                                        js.optJSONObject("body").optString("transactionType").equals("REFUND") ||
                                                        js.optJSONObject("body").optString("transactionType").equals("UPI_SCAN_CODE_VOID") ||
                                                        js.optJSONObject("body").optString("transactionType").equals("COUPON_VOID")) {
                                                    json.put("Transaction Successful", "true");
                                                    json.put("Transaction Type", js.optJSONObject("body").optString("transactionType"));

                                                    char date[] = js.optJSONObject("body")
                                                            .optString("transactionDate").toCharArray();
                                                    char time[] = js.optJSONObject("body")
                                                            .optString("transactionTime").toCharArray();
                                                    String datestr = "", timestr = "";
                                                    for (int j1 = 0; j1 < date.length; j1++) {
                                                        String dash = "";
                                                        if (j1 == 4 || j1 == 6) {
                                                            dash = "-";
                                                        }
                                                        datestr = datestr + dash + date[j1];
                                                    }

                                                    for (int j1 = 0; j1 < time.length; j1++) {
                                                        String dash = "";
                                                        if (j1 == 2 || j1 == 4) {
                                                            dash = ":";
                                                        }
                                                        timestr = timestr + dash + time[j1];
                                                    }

//                                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
//                                                    Date d=df.parse(datestr + " " + timestr);
//                                                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                                    df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));
                                                    json.put("Date And Time", datestr + " " + timestr);
                                                    //added on 28/02/2019
                                                    refund_time = datestr + " " + timestr;
                                                } else {
                                                    json.put("Transaction Successful", newjson1.optString("status"));
                                                    json.put("Response Message", js.optJSONObject("body").optString("responseMessage"));
                                                }
                                            } else {
                                                json.put("Transaction Successful", newjson1.optString("status"));
                                            }

                                        } else {
                                            json.put("Transaction Successful", newjson1.optString("status"));
                                        }

                                        break;
                                    case "channel":
                                        json.put("Payment by", newjson1.optString("channel"));
                                        if (newjson1.optString("channel").equals("UNION_PAY")) {
                                            print1.setVisibility(View.VISIBLE);
                                            btn_close1.setVisibility(View.VISIBLE);
                                            btn_void.setVisibility(View.VISIBLE);
                                            ll.setVisibility(View.GONE);
                                        } else {
                                            print1.setVisibility(View.GONE);
                                            btn_close1.setVisibility(View.GONE);
                                            btn_void.setVisibility(View.GONE);
                                            ll.setVisibility(View.VISIBLE);
                                        }
                                        break;
                                    case "rmb_amount":
                                        json.put("Amount RMB", newjson1.optString("rmb_amount"));
                                        break;
                                    case "currency":
                                        json.put("Payment Currency", newjson1.optString("currency"));
                                        break;
                                    case "grandtotal":
                                        json.put("Payment Amount", roundTwoDecimals(Float.valueOf(newjson1.optString("grandtotal"))));
                                        break;
                                    case "receipt_amount":
                                        json.put("Receipt Amount", newjson1.optString("receipt_amount"));
                                        break;
                                    //commented on 2/2/2019
//                                    case "gmt_payment":
//                                        json.put("Date (GMT)", newjson1.optString("gmt_payment"));
//                                        break;
                                    case "gmt_payment":
                                        if (newjson1.has("channel") && newjson1.optString("channel").equals("UNION_PAY")) {
                                            char date[] = newjson1.optJSONObject("server_response").optJSONObject("body")
                                                    .optString("transactionDate").toCharArray();
                                            char time[] = newjson1.optJSONObject("server_response").optJSONObject("body")
                                                    .optString("transactionTime").toCharArray();
                                            String datestr = "", timestr = "";
                                            for (int j1 = 0; j1 < date.length; j1++) {
                                                String dash = "";
                                                if (j1 == 4 || j1 == 6) {
                                                    dash = "-";
                                                }
                                                datestr = datestr + dash + date[j1];
                                            }

                                            for (int j1 = 0; j1 < time.length; j1++) {
                                                String dash = "";
                                                if (j1 == 2 || j1 == 4) {
                                                    dash = ":";
                                                }
                                                timestr = timestr + dash + time[j1];
                                            }

//                                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
//                                            Date d=df.parse(datestr + " " + timestr);
//                                            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                            df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));

//                                            json.put("Date And Time", datestr + " " + timestr);
                                            json.put("Date And Time", datestr + " " + timestr);
                                            //added on 28/02/2019
                                            refund_time = datestr + " " + timestr;
                                        } else {
                                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                            Date d = df.parse(newjson1.optString("gmt_payment").replace("T", " "));
                                            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));

//                                            json.put("Date (Local)", newjson1.optString("gmt_payment"));
                                            json.put("Date (Local)", df1.format(d));
                                            //added on 28/02/2019
                                            refund_time = df1.format(d);
                                        }

                                        break;
                                    case "remaining_amount":
                                        json.put("Amount Available", newjson1.optString("remaining_amount"));
                                        break;
                                    case "refunded_amount":
                                        json.put("Amount Refunded", newjson1.optString("refunded_amount"));
                                        break;
                                    case "config_id":
                                        json.put("System Config", newjson1.optString("config_id"));
                                        break;
                                    case "reference_id":
                                        json.put("Reference Number", newjson1.optString("reference_id"));
                                        referenecno = newjson1.optString("reference_id");
                                        if (newjson1.has("server_response")) {
                                            JSONObject js = new JSONObject(newjson1.optString("server_response"));
                                            if (js.optJSONObject("body").optString("transactionType").equals("SALE") ||
                                                    js.optJSONObject("body").optString("transactionType").equals("UPI_SCAN_CODE_SALE") ||
                                                    js.optJSONObject("body").optString("transactionType").equals("COUPON_SALE") ||
                                                    js.optJSONObject("body").optString("transactionType").equals("VOID") ||
                                                    js.optJSONObject("body").optString("transactionType").equals("REFUND") ||
                                                    js.optJSONObject("body").optString("transactionType").equals("UPI_SCAN_CODE_VOID") ||
                                                    js.optJSONObject("body").optString("transactionType").equals("COUPON_VOID")) {
                                                json.put("CUP Reference No", js.optJSONObject("body").optString("referenceNumber"));
                                            }
                                        }


                                        break;
                                    case "trade_no":
                                        json.put("Payment Reference", newjson1.optString("trade_no"));
                                        break;
                                    case "increment_id":
                                        json.put("Transaction Number", newjson1.optString("increment_id"));
                                        break;
                                    case "merchant_id":
                                        json.put("Merchant Number", newjson1.optString("merchant_id"));
                                        break;
                                    case "message":
                                        json.put("Message Description", newjson1.optString("message"));
                                        break;
                                    case "status_description":

                                        if (jsonObject1.has("refunds")) {
                                            if (jsonObject1.optJSONArray("refunds").length() == 0) {
                                                json.put("Message Status", newjson1.optString("status_description"));
                                            } else {
                                                json.put("Message Status", newjson1.optString("status_description"));
                                                if (jsonObject1.optJSONArray("refunds").length() > 1) {
                                                    if (jsonObject1.optJSONArray("refunds")
                                                            .optJSONObject(jsonObject1.optJSONArray("refunds").length() - 1).optString("refund_pay_time").equals("")) {
                                                        if (jsonObject1.optJSONArray("refunds").optJSONObject(jsonObject1.optJSONArray("refunds").length() - 1).optString("code").equals("ACCESS_FORBIDDEN")) {
                                                            break;
                                                        }
                                                        if (jsonObject1.optJSONArray("refunds")
                                                                .optJSONObject(0).optString("refund_pay_time").equals("")) {

                                                        } else {

                                                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                            Date d = df.parse(jsonObject1.optJSONArray("refunds")
                                                                    .optJSONObject(0).optString("refund_pay_time").replace("T", " "));
                                                            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                            df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));

                                                            json.put("Refund Pay Time", df1.format(d));
                                                            //added on 28/02/2019
                                                            refund_time = df1.format(d);
                                                        }
                                                    } else {
                                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                        Date d = df.parse(jsonObject1.optJSONArray("refunds")
                                                                .optJSONObject(jsonObject1.optJSONArray("refunds").length() - 1).optString("refund_pay_time").replace("T", " "));
                                                        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));
                                                        json.put("Refund Pay Time", df1.format(d));
                                                        //added on 28/02/2019
                                                        refund_time = df1.format(d);
                                                    }

                                                } else {
                                                    if (jsonObject1.optJSONArray("refunds").optJSONObject(0).optString("code").equals("ACCESS_FORBIDDEN")) {
                                                        break;
                                                    } else {
                                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                        Date d = df.parse(jsonObject1.optJSONArray("refunds")
                                                                .optJSONObject(0).optString("refund_pay_time").replace("T", " "));
                                                        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));
                                                        json.put("Refund Pay Time", df1.format(d));
                                                        //added on 28/02/2019
                                                        refund_time = df1.format(d);
                                                    }

                                                }

                                            }
                                        } else {
                                            json.put("Message Status", newjson1.optString("status_description"));
                                        }


                                        break;

                                }

                            }
                        }


                    }


                    if (!newjson1.optString("original_amount").equals("0.0") &&
                            !newjson1.optString("original_amount").equals("0.00")) {
                        json.put("Original Amount", newjson1.optString("original_amount"));
                    }
                    if (!newjson1.optString("fee_amount").equals("0.0") &&
                            !newjson1.optString("fee_amount").equals("0.00")) {
                        json.put("Fee Amount", newjson1.optString("fee_amount"));
                    }
                    if (!newjson1.optString("fee_percentage").equals("0.0") &&
                            !newjson1.optString("fee_percentage").equals("0.00")) {
                        json.put("Fee Percentage", newjson1.optString("fee_percentage"));
                    }
                    if (!newjson1.optString("discount").equals("0.0") &&
                            !newjson1.optString("discount").equals("0.00")) {
                        json.put("Discount", newjson1.optString("discount"));
                    }

                    try {
                        if (jsonObject.has("server_response") && jsonObject.optJSONObject("server_response") != null) {
                            jsonObjectGatewayResponse = jsonObject.optJSONObject("server_response").optJSONObject("body");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    newjson = json;
                    if (json.optString("Transaction Successful").equals("false")) {
                        print1.setEnabled(false);
                        btn_void.setEnabled(false);
                        btn_refund_uni.setEnabled(false);
                    } else {
                        print1.setEnabled(true);
                        btn_void.setEnabled(true);
                        btn_refund_uni.setEnabled(true);
                    }

                    if (newjson.optString("Payment Amount").equals(newjson.optString("Amount Refunded"))) {
                        btn_refund.setVisibility(View.GONE);
                        findViewById(R.id.ll1).setVisibility(View.GONE);
                        findViewById(R.id.ll2).setVisibility(View.GONE);
                        findViewById(R.id.ll3).setVisibility(View.GONE);
                    } else if (newjson.optString("Message Status").equals("TRADE_CLOSED")) {
                        btn_refund.setVisibility(View.GONE);
                        findViewById(R.id.ll1).setVisibility(View.GONE);
                        findViewById(R.id.ll2).setVisibility(View.GONE);
                        findViewById(R.id.ll3).setVisibility(View.GONE);
                    } else {
                        btn_refund.setVisibility(View.VISIBLE);
                        findViewById(R.id.ll1).setVisibility(View.VISIBLE);
                        findViewById(R.id.ll2).setVisibility(View.VISIBLE);
                        findViewById(R.id.ll3).setVisibility(View.VISIBLE);
                    }


                    if (newjson.optString("Transaction Type").equals("VOIDED") ||
                            newjson.optString("Transaction Type").equals("REFUND") ||
                            newjson.optString("Transaction Type").equals("TRADE_REFUND") ||
                            newjson.optString("Transaction Type").equals("UPI_SCAN_CODE_VOID") ||
                            newjson.optString("Transaction Type").equals("VOID") ||
                            newjson.optString("Transaction Type").equals("COUPON_VOID")) {
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_void);
                        linearLayout.setWeightSum(2);
                        btn_refund_uni.setVisibility(View.GONE);
                        btn_void.setVisibility(View.GONE);
                    } else if (newjson.optString("Transaction Type").equals("SALE") ||
                            newjson.optString("Transaction Type").equals("UPI_SCAN_CODE_SALE")
                    ) {
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_void);
                        linearLayout.setWeightSum(4);
                        btn_void.setVisibility(View.VISIBLE);
                        btn_refund_uni.setVisibility(View.VISIBLE);
                    } else if (newjson.optString("Transaction Type").equals("COUPON_SALE")) {
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_void);
                        linearLayout.setWeightSum(3);
                        btn_void.setVisibility(View.VISIBLE);
                        btn_refund_uni.setVisibility(View.GONE);
                    } else {
                        btn_void.setVisibility(View.GONE);
                        btn_refund_uni.setVisibility(View.GONE);
                    }

                    // amountTotal = newjson.optString("Payment Amount");
                    amountTotal = newjson1.optString("receipt_amount");

                    TransactionDetailsAdapter transactionDetailsAdapter = new TransactionDetailsAdapter(TransactionDetailsActivity.this, newjson);
                    recycler_view.setAdapter(transactionDetailsAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;*/


            case "refundNow":

                if (progress != null && progress.isShowing())
                    progress.dismiss();

                if (jsonObject.optBoolean("status")) {
                    Toast.makeText(TransactionDetailsActivity.this, "Refund Request Successful.", Toast.LENGTH_LONG).show();

                    try {

                        //added for external apps 12/5/2019
                        int REQ_PAY_SALE = 100;
                        if (DashboardActivity.isExternalApp) {
                            isReturnFromTransactionDetails = true;
                            DashboardActivity.isExternalApp = false;
                            jsonObjectReturnResult = jsonObject;
                            finish();
                            return;
//                            getIntent().putExtra("result", jsonObject.toString());
//                            setResult(REQ_PAY_SALE, getIntent());
//                            finish();
//                            return;
                        }  //added for external apps


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else
                    Toast.makeText(TransactionDetailsActivity.this, "Refund Request UnSuccessful." + jsonObject.optString("message"), Toast.LENGTH_LONG).show();


                Intent i = getIntent();
                i.putExtra("reference_id", getIntent().getStringExtra("reference_id"));
//                i.putExtra("increment_id", getIntent().getStringExtra("increment_id"));
                startActivity(i);
                finish();


                break;


        }
    }

    double remaining_amount = 0.00;
    double refunded_amount = 0.00;

    public void parseTransactionDetailsResponse(JSONObject jsonObject) throws Exception {

        JSONObject jsonObjectPayment = jsonObject.optJSONObject("payment");
        String serverResponse = "";

        //validation for unionpay
      JSONObject refundJsonObject=null;


        if (jsonObjectPayment.has("serverResponse")) {
            if (jsonObjectPayment.optString("serverResponse") != null) {
                byte data[] = android.util.Base64.decode(android.util.Base64.decode(jsonObjectPayment.optString("serverResponse"), Base64.NO_WRAP), Base64.NO_WRAP);
                serverResponse = new String(data, "UTF-8");
                JSONObject jsonObjectServerResponse = new JSONObject(serverResponse);
                jsonObjectGatewayResponse = jsonObjectServerResponse.optJSONObject("body");
            }
        }


        JSONObject json = new JSONObject();
//        if (jsonObjectPayment.optString("paymentStatus").equals("SUCCESS") ||
//                jsonObjectPayment.optString("paymentStatus").equals("REFUND")) {
            for (int i = 0; i < jsonObjectPayment.length(); i++) {
                for (int j = 0; j < jsonObjectPayment.length(); j++) {
                    String value = jsonObjectPayment.optString(jsonObjectPayment.names().optString(j));
                    switch (jsonObjectPayment.names().optString(j)) {
                        case "id":
                            json.put("Transaction Number", value);
                            break;
                        case "channel":
                            json.put("Payment By", value);
                            if (value.equals("UNION_PAY")) {
                                print1.setVisibility(View.VISIBLE);
                                btn_close1.setVisibility(View.VISIBLE);
                                btn_void.setVisibility(View.VISIBLE);
                                ll.setVisibility(View.GONE);
                            } else {
                                print1.setVisibility(View.GONE);
                                btn_close1.setVisibility(View.GONE);
                                btn_void.setVisibility(View.GONE);
                                ll.setVisibility(View.VISIBLE);
                            }
                            break;
                        case "createDate":
                            json.put("Date And Time", value);
                            break;
                        case "currency":
                            json.put("Payment Currency", value);
                            break;
                        case "grandTotal":
                            json.put("Payment Amount", value);
                            break;
                        case "status":
                            json.put("Transaction Successful", value);
                            break;
                        case "message":
                            json.put("Message Description", value);
                            break;
                        case "paymentStatus":
                            json.put("Message Status", value);
                            break;
                        case "qrCode":
                            json.put("QRCode", value);
                            break;
                        case "rate":
                            Double receipt_amount = Double.parseDouble(jsonObjectPayment.optString("receiptAmount"));
                            Double rate = Double.parseDouble(jsonObjectPayment.optString("rate"));
                            Double rmb_amount = receipt_amount * rate;
                            json.put("Amount RMB", roundTwoDecimals(Float.valueOf(rmb_amount + "")));
                            break;
                        case "receiptAmount":
                            json.put("Receipt Amount", value);
                            break;
                        case "referenceId":
                            json.put("Reference Number", value);
                            break;
                        case "terminalId":
                            json.put("Terminal Id", value);
                            break;
                        case "tradeNo":
                            json.put("Payment Reference", value);
                            break;
                        case "type":
                                if (jsonObjectPayment.has("serverResponse")) {
                                    if(getIntent().getStringExtra("increment_id").equals(jsonObjectPayment.optString("id")))
                                        json.put("CUP Reference No", jsonObjectGatewayResponse.optString("referenceNumber"));
                                    json.put("Transaction Type", jsonObjectGatewayResponse.optString("transactionType"));
                                } else
                                    json.put("Transaction Type", value);
                            break;
                    }
                }
            }


            if (jsonObject.has("refunds")) {
                JSONArray jsonArrayRefund = jsonObject.optJSONArray("refunds");
                for (int i = 0; i < jsonArrayRefund.length(); i++) {

                    if(jsonObject.has("refunds")&&jsonObjectPayment.optString("channel").equals("UNION_PAY"))
                    {
                        byte data[] = android.util.Base64.decode(android.util.Base64.decode(jsonArrayRefund.optJSONObject(i).optString("serverResponse"), Base64.NO_WRAP), Base64.NO_WRAP);
                        String s = new String(data, "UTF-8");
                        refundJsonObject=new JSONObject(s);
                        refundJsonObject=refundJsonObject.optJSONObject("body");

                        if(getIntent().getStringExtra("increment_id").equals(jsonArrayRefund.optJSONObject(i).optString("id")))
                        json.put("CUP Reference No", refundJsonObject.optString("referenceNumber"));
                        json.put("Transaction Type", refundJsonObject.optString("transactionType"));
                    }

                    if (json.optString("Payment By").equals("UNION_PAY")) {
                        refunded_amount = refunded_amount + Double.parseDouble(jsonArrayRefund.optJSONObject(i).optString("refundedAmount"));
                    } else {
                        refunded_amount = refunded_amount + Double.parseDouble(jsonArrayRefund.optJSONObject(i).optString("refundFee"));
                    }

                    refunded_amount = Double.parseDouble(roundTwoDecimals(refunded_amount));

                }
                remaining_amount = Double.parseDouble(jsonObject.optJSONObject("payment").optString("receiptAmount")) - refunded_amount;
                remaining_amount = Double.parseDouble(roundTwoDecimals(remaining_amount));
            }

            json.put("Amount Refunded", roundTwoDecimals(refunded_amount));
            json.put("Remaining Amount", roundTwoDecimals(remaining_amount));

            if (!jsonObjectPayment.optString("receiptAmount").equals("0.0") &&
                    !jsonObjectPayment.optString("receiptAmount").equals("0.00")) {
                json.put("Original Amount", jsonObjectPayment.optString("receiptAmount"));
            }
            if (jsonObjectPayment.has("feeAmount")&&!jsonObjectPayment.optString("feeAmount").equals("0.0") &&
                    !jsonObjectPayment.optString("feeAmount").equals("0.00")) {
                json.put("Fee Amount", jsonObjectPayment.optString("feeAmount"));
            }
            if (jsonObjectPayment.has("feePercentage")&&!jsonObjectPayment.optString("feePercentage").equals("0.0") &&
                    !jsonObjectPayment.optString("feePercentage").equals("0.00")) {
                json.put("Fee Percentage", jsonObjectPayment.optString("feePercentage"));
            }
            if (jsonObjectPayment.has("discount")&&!jsonObjectPayment.optString("discount").equals("0.0") &&
                    !jsonObjectPayment.optString("discount").equals("0.00")) {
                json.put("Discount", jsonObjectPayment.optString("discount"));
            }

            newjson = json;

            if (json.optString("Transaction Successful").equals("false")) {
                print1.setEnabled(false);
                btn_void.setEnabled(false);
                btn_refund_uni.setEnabled(false);
            } else {
                print1.setEnabled(true);
                btn_void.setEnabled(true);
                btn_refund_uni.setEnabled(true);
            }

            if (Double.parseDouble(newjson.optString("Payment Amount"))
                    == Double.parseDouble((newjson.optString("Amount Refunded")))) {
                btn_refund.setVisibility(View.GONE);
                findViewById(R.id.ll1).setVisibility(View.GONE);
                findViewById(R.id.ll2).setVisibility(View.GONE);
                findViewById(R.id.ll3).setVisibility(View.GONE);
            } else if (newjson.optString("Message Status").equals("CLOSED")) { //closed
                btn_refund.setVisibility(View.GONE);
                findViewById(R.id.ll1).setVisibility(View.GONE);
                findViewById(R.id.ll2).setVisibility(View.GONE);
                findViewById(R.id.ll3).setVisibility(View.GONE);
            } else {
                btn_refund.setVisibility(View.VISIBLE);
                findViewById(R.id.ll1).setVisibility(View.VISIBLE);
                findViewById(R.id.ll2).setVisibility(View.VISIBLE);
                findViewById(R.id.ll3).setVisibility(View.VISIBLE);
            }


            if (newjson.optString("Transaction Type").equals("VOIDED") ||
                    newjson.optString("Transaction Type").equals("REFUND") ||
                    newjson.optString("Transaction Type").equals("TRADE_REFUND") ||
                    newjson.optString("Transaction Type").equals("UPI_SCAN_CODE_VOID") ||
                    newjson.optString("Transaction Type").equals("VOID") ||
                    newjson.optString("Transaction Type").equals("COUPON_VOID")) {
                LinearLayout linearLayout =  findViewById(R.id.ll_void);

                if (Double.parseDouble(newjson.optString("Payment Amount"))
                        == Double.parseDouble((newjson.optString("Amount Refunded")))
                &&newjson.optString("Transaction Type").equals("REFUND")
                ) {
                    btn_refund.setVisibility(View.GONE);

                    findViewById(R.id.ll1).setVisibility(View.GONE);
                    findViewById(R.id.ll2).setVisibility(View.GONE);
                    findViewById(R.id.ll3).setVisibility(View.GONE);
                    linearLayout.setWeightSum(2);
                    btn_refund_uni.setVisibility(View.GONE);
                    btn_void.setVisibility(View.GONE);
                }
                if (Double.parseDouble(newjson.optString("Payment Amount"))
                        == Double.parseDouble((newjson.optString("Amount Refunded")))
                        &&newjson.optString("Transaction Type").equals("VOID")||
                        newjson.optString("Transaction Type").equals("COUPON_VOID")
                ||newjson.optString("Transaction Type").equals("UPI_SCAN_CODE_VOID"))
                {
                    linearLayout.setWeightSum(2);
                    btn_refund_uni.setVisibility(View.GONE);
                    btn_void.setVisibility(View.GONE);
                }

            } else if (newjson.optString("Transaction Type").equals("SALE") ||
                    newjson.optString("Transaction Type").equals("UPI_SCAN_CODE_SALE")
            ) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_void);
                linearLayout.setWeightSum(4);
                btn_void.setVisibility(View.VISIBLE);
                btn_refund_uni.setVisibility(View.VISIBLE);
                if (Double.parseDouble(newjson.optString("Payment Amount"))
                        == Double.parseDouble((newjson.optString("Amount Refunded")))) {
                    btn_refund.setVisibility(View.GONE);
                    findViewById(R.id.ll1).setVisibility(View.GONE);
                    findViewById(R.id.ll2).setVisibility(View.GONE);
                    findViewById(R.id.ll3).setVisibility(View.GONE);
                    linearLayout.setWeightSum(2);
                    btn_refund_uni.setVisibility(View.GONE);
                    btn_void.setVisibility(View.GONE);
                }
            } else if (newjson.optString("Transaction Type").equals("COUPON_SALE")) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_void);
                linearLayout.setWeightSum(3);
                btn_void.setVisibility(View.VISIBLE);
                btn_refund_uni.setVisibility(View.GONE);
                if (Double.parseDouble(newjson.optString("Payment Amount"))
                        == Double.parseDouble((newjson.optString("Amount Refunded")))) {
                    btn_refund.setVisibility(View.GONE);
                    findViewById(R.id.ll1).setVisibility(View.GONE);
                    findViewById(R.id.ll2).setVisibility(View.GONE);
                    findViewById(R.id.ll3).setVisibility(View.GONE);
                    linearLayout.setWeightSum(2);
                    btn_refund_uni.setVisibility(View.GONE);
                    btn_void.setVisibility(View.GONE);
                }
            } else {
                btn_void.setVisibility(View.GONE);
                btn_refund_uni.setVisibility(View.GONE);
            }

            amountTotal = jsonObjectPayment.optString("receiptAmount");

            TransactionDetailsAdapter transactionDetailsAdapter = new TransactionDetailsAdapter(TransactionDetailsActivity.this, newjson);
            recycler_view.setAdapter(transactionDetailsAdapter);


//        } else {
//            Toast.makeText(TransactionDetailsActivity.this, "Transaction Details Not Found", Toast.LENGTH_SHORT).show();
//        }


    }


    String roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        return twoDForm.format(d);
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

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String emailPattern1 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";
    public static String amountTotal = "";
    AlertDialog dialog;
    private static final String TAG = "TransactionDetails";


    private AidlPrinter printDev = null;
    // 打印机回调对象
    private AidlPrinterStateChangeListener callback = new PrinterCallback(); // 打印机回调

    /**
     * 打印机回调类
     */
    private class PrinterCallback extends AidlPrinterStateChangeListener.Stub {

        @Override
        public void onPrintError(int arg0) throws RemoteException {
            // showMessage("打印机异常" + arg0, Color.RED);
            Looper.prepare();
            Toast.makeText(TransactionDetailsActivity.this, arg0, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrintFinish() throws RemoteException {
            Looper.prepare();
            Toast.makeText(TransactionDetailsActivity.this, getString(R.string.printer_finish), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrintOutOfPaper() throws RemoteException {
            Looper.prepare();
            Toast.makeText(TransactionDetailsActivity.this, getString(R.string.printer_need_paper), Toast.LENGTH_SHORT).show();
        }
    }

    public void onDeviceConnected(AidlDeviceManager deviceManager) {
        try {
            printDev = AidlPrinter.Stub.asInterface(deviceManager
                    .getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PRINTERDEV));
            printDev.setPrinterGray(0x02);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    Bitmap bitmap = null;
    boolean isUnionPay = false;

    public void print() throws Exception {


        final List<PrintDataObject> list = new ArrayList<PrintDataObject>();

        int fontSize = 24;
        if (jsonObject.has("payment")) {
            if (jsonObject.optJSONObject("payment").has("serverResponse")) {
                if (jsonObject.optJSONObject("payment").optString("serverResponse") != null) {
                    byte data[] = android.util.Base64.decode(android.util.Base64.decode(jsonObject.optJSONObject("payment").optString("serverResponse"), Base64.NO_WRAP), Base64.NO_WRAP);
                    String serverResponse = new String(data, "UTF-8");
                    jsonObject = new JSONObject(serverResponse).optJSONObject("body");

                    //printing unionpay receipt
                    if (jsonObject.has("responseCodeThirtyNine")) {
                        isUnionPay = true;
                        intentCen.setComponent(comp);
                        Bundle bundle = new Bundle();
                        bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.PRINT_ANY);
                        bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_VOUCHER_NO, jsonObject.optString("voucherNumber"));
                        intentCen.putExtras(bundle);
                        startActivityForResult(intentCen, REQ_PAY_SALE);
                        return;
                    }

                }
            } else {
                jsonObject = jsonObject.optJSONObject("payment");
            }

        }
        else if(jsonObject.has("transactionType"))
        {
            if (jsonObject.has("responseCodeThirtyNine")) {
                isUnionPay = true;
                intentCen.setComponent(comp);
                Bundle bundle = new Bundle();
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.PRINT_ANY);
                bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_ORI_VOUCHER_NO, jsonObject.optString("voucherNumber"));
                intentCen.putExtras(bundle);
                startActivityForResult(intentCen, REQ_PAY_SALE);
                return;
            }
        }
        try {

            if (newjson.optString("Transaction Type").equals("VOIDED") ||
                    newjson.optString("Transaction Type").equals("UPI_SCAN_CODE_VOID") ||
                    newjson.optString("Transaction Type").equals("VOID") ||
                    newjson.optString("Transaction Type").equals("COUPON_VOID")) {
                list.add(new PrintDataObject("--- " + "UnionPay Voided Receipt" + " ---",
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));
            } else if (newjson.optString("Transaction Type").equals("REFUND")) {
                list.add(new PrintDataObject("--- " + "UnionPay Refunded Receipt" + " ---",
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));
            } else if ((newjson.optString("Transaction Type").equals("SALE") ||
                    newjson.optString("transactionType").equals("UPI_SCAN_CODE_SALE") ||
                    newjson.optString("Transaction Type").equals("COUPON_SALE")) && newjson.optString("Transaction Successful").equals("true")) {
                list.add(new PrintDataObject("------- " + "Successful" + " -------",
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));

            } else if (jsonObject.optString("paymentStatus").equals("SUCCESS")) {
                list.add(new PrintDataObject("------- " + "Successful" + " -------",
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));
            } else if (jsonObject.optString("paymentStatus").equals("REFUND")) {
                list.add(new PrintDataObject("------- " + "Refund Receipt" + " -------",
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));
            } else if (jsonObject.optString("paymentStatus").equals("VOIDED") ||
                    jsonObject.optString("status_description").equals("UPI_SCAN_CODE_VOID") ||
                    jsonObject.optString("status_description").equals("VOID")) {
                list.add(new PrintDataObject("------- " + "UnionPay Voided Receipt" + " -------",
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));
            } else if (jsonObject.optString("paymentStatus").equals("REFUND") ||
                    jsonObject.optString("status_description").equals("TRADE_REFUND")) {
                list.add(new PrintDataObject("------- " + "UnionPay Refunded Receipt" + " -------",
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));
            } else {
                list.add(new PrintDataObject("------- " + "Unsuccessful" + " -------",
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }


//            list.add(new PrintDataObject("Merchant Name:",
//                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
//                    true));
//
//            list.add(new PrintDataObject(new JSONObject(preferenceManager.getmerchant_info()).optString("company"),
//                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
//                    true));


            if (preferenceManager.getBranchName().equals("true")) {

                list.add(new PrintDataObject("Branch Name:",
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));

                list.add(new PrintDataObject(preferenceManager.getmerchant_name(),
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }

            if (preferenceManager.getGSTNo().equals("true")) {
                list.add(new PrintDataObject("GST:",
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));


                ArrayList<String> arrayList1 = new ArrayList<>();
                char cc[] = preferenceManager.getgstno().toCharArray();
                for (int i = 0; i < cc.length; i++) {
                    if (cc.length == 8) {
                        if (i == 1) {
                            arrayList1.add(cc[i] + "-");
                        } else if (i == 4) {
                            arrayList1.add(cc[i] + "-");
                        } else {
                            arrayList1.add(cc[i] + "");
                        }

                    } else if (cc.length == 9) {
                        if (i == 2) {
                            arrayList1.add(cc[i] + "-");
                        } else if (i == 5) {
                            arrayList1.add(cc[i] + "-");
                        } else {
                            arrayList1.add(cc[i] + "");
                        }
                    }
                }
                String sss = "";
                for (int i = 0; i < arrayList1.size(); i++) {
                    sss = sss + arrayList1.get(i);
                }
                list.add(new PrintDataObject(sss,
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }


            if (preferenceManager.getBranchAddress().equals("true")) {
                list.add(new PrintDataObject("Address:",
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
                char c[] = preferenceManager.getaddress().toCharArray();
                String s = "";
                int j = 0;
                for (int i = 0; i < c.length; i++) {
                    s = s + c[i];
                    j++;
                    if (j == 31) {
                        j = 0;
                        list.add(new PrintDataObject(s,
                                fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                                true));
                        s = "";
                    } else if (i == c.length - 1) {
                        list.add(new PrintDataObject(s,
                                fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                                true));
                    }
                }


            }


            if (preferenceManager.getBranchPhoneNo().equals("true")) {
                list.add(new PrintDataObject("Contact Number:",
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
                list.add(new PrintDataObject(preferenceManager.getcontact_no(),
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }

            if (preferenceManager.getBranchEmail().equals("true")) {
                list.add(new PrintDataObject("Contact Email:",
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));

                // Printer.getInstance().addText(AlignMode.LEFT, "Contact Email:");
                if (!preferenceManager.getcontact_email().equals("")) {
                    if (preferenceManager.getcontact_email().trim().matches(emailPattern1)) {
                        list.add(new PrintDataObject(preferenceManager.getcontact_email(),
                                fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                                true));
                    } else if (preferenceManager.getcontact_email().trim().matches(emailPattern)) {
                        list.add(new PrintDataObject(preferenceManager.getcontact_email(),
                                fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                                true));
                    } else {
                        list.add(new PrintDataObject(preferenceManager.getcontact_email(),
                                fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                                true));
                    }
                }

            }


            list.add(new PrintDataObject("Transaction Number:",
                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject(jsonObject.optString("id"),
                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("Reference Number:",
                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject(jsonObject.optString("referenceId"),
                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                    true));

            if (newjson.has("CUP Reference No")) {
                list.add(new PrintDataObject("CUP Reference No:",
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
                list.add(new PrintDataObject(newjson.optString("CUP Reference No"),
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }

            if (jsonObject.has("trade_no")) {
                list.add(new PrintDataObject("Trade Number:",
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
                list.add(new PrintDataObject(jsonObject.optString("tradeNo"),
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }


            list.add(new PrintDataObject("Date (Local):",
                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                    true));

            try {
                Date c1 = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c1);


                Date d = null;
                if (newjson.has("Transaction Type") && (newjson.optString("Transaction Type").equals("VOIDED") ||


                        newjson.optString("Transaction Type").equals("UPI_SCAN_CODE_VOID") ||
                        newjson.optString("Transaction Type").equals("COUPON_VOID"))) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
                    d = df.parse(newjson.optString("Date And Time").replace("T", " "));
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));
                    list.add(new PrintDataObject(df1.format(d),
                            fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                            true));
                } else if (newjson.optString("Transaction Type").equals("REFUND") ||
                        newjson.optString("Transaction Type").equals("VOID")) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
                    d = df.parse(newjson.optString("Date And Time").replace("T", " "));
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));
                    list.add(new PrintDataObject(df1.format(d),
                            fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                            true));
                } else if (newjson.has("Transaction Type") && (newjson.optString("Transaction Type").equals("SALE")
                        || newjson.optString("transactionType").equals("UPI_SCAN_CODE_SALE") ||
                        newjson.optString("Transaction Type").equals("COUPON_SALE"))) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    d = df.parse(jsonObject.optString("created_on").replace("T", " "));
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));
                    list.add(new PrintDataObject(df1.format(d),
                            fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                            true));
                } else {
                    SimpleDateFormat df = null, df1 = null;

                    if (jsonObject.has("gmt_payment")) {
                        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        df.setTimeZone(TimeZone.getTimeZone("UTC"));
                        d = df.parse(jsonObject.optString("gmt_payment").replace("T", " "));
                        df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));
                    } else if (jsonObject.has("created_on")) {
                        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        df.setTimeZone(TimeZone.getTimeZone("UTC"));
                        d = df.parse(jsonObject.optString("created_on").replace("T", " "));
                        df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));
                    } else if (jsonObject.has("createDate")) {
                        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        df.setTimeZone(TimeZone.getTimeZone("UTC"));
                        d = df.parse(jsonObject.optString("createDate"));
                        df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));
                    }
                    list.add(new PrintDataObject(df1.format(d).replace("T", " "),
                            fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                            true));

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            if (jsonObject.has("ref1") && !jsonObject.optString("ref1").equals("") && !jsonObject.optString("ref1").equals("null")) {
                list.add(new PrintDataObject("Reference:",
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
                list.add(new PrintDataObject(jsonObject.optString("ref1"),
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }

            list.add(new PrintDataObject("Paid Amount:",
                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                    true));
            if (jsonObject.has("receiptAmount")) {
                amountTotal = jsonObject.optString("receiptAmount");
                if (jsonObject.optString("channel").equals("UNION_PAY")) {
                    list.add(new PrintDataObject(preferenceManager.getcurrency() + " " + roundTwoDecimals(Float.valueOf(jsonObject.optString("receiptAmount"))),
                            fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                            true));
                } else
                    list.add(new PrintDataObject(preferenceManager.getcurrency() + " " + roundTwoDecimals(Float.valueOf(jsonObject.optString("receiptAmount"))) + " RMB " + newjson.optString("Amount RMB"),
                            fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                            true));
            } else if (jsonObject.has("grandTotal")) {
                amountTotal = jsonObject.optString("grandTotal");
                if (jsonObject.optString("channel").equals("UNION_PAY")) {
                    list.add(new PrintDataObject(preferenceManager.getcurrency() + " " + roundTwoDecimals(Float.valueOf(jsonObject.optString("grandTotal"))),
                            fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                            true));
                } else
                    list.add(new PrintDataObject(preferenceManager.getcurrency() + " " + roundTwoDecimals(Float.valueOf(jsonObject.optString("grandTotal"))) + " RMB " + newjson.optString("Amount RMB"),
                            fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                            true));
            }


            if (jsonObject.has("receiptAmount") && !jsonObject.optString("receiptAmount").equals("0.0") &&
                    !jsonObject.optString("receiptAmount").equals("0.00")) {
                list.add(new PrintDataObject("Original Amount:" + preferenceManager.getcurrency() + " " + jsonObject.optString("receiptAmount"),
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }

            if (jsonObject.has("feeAmount") && !jsonObject.optString("feeAmount").equals("0.0") &&
                    !jsonObject.optString("feeAmount").equals("0.00")) {
                list.add(new PrintDataObject("Fee Amount:" + preferenceManager.getcurrency() + " " + jsonObject.optString("feeAmount"),
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }

            if (jsonObject.has("feePercentage") && !jsonObject.optString("feePercentage").equals("0.0") &&
                    !jsonObject.optString("feePercentage").equals("0.00")) {
                list.add(new PrintDataObject("Fee Percentage: " + jsonObject.optString("feePercentage"),
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }

            if (jsonObject.has("discount") && !jsonObject.optString("discount").equals("0.0") &&
                    !jsonObject.optString("discount").equals("0.00")) {
                list.add(new PrintDataObject("Discount:" + preferenceManager.getcurrency() + " " + jsonObject.optString("discount"),
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }


            if (jsonObjectTransactionDetails.has("refunds") && jsonObjectTransactionDetails.optJSONArray("refunds") != null)
                if (jsonObjectTransactionDetails.optJSONArray("refunds").length() != 0) {

                    JSONArray jsonArrayRefund = jsonObjectTransactionDetails.optJSONArray("refunds");
                    for (int i = 0; i < jsonArrayRefund.length(); i++) {
                        refunded_amount = refunded_amount + Double.parseDouble(jsonArrayRefund.optJSONObject(i).optString("refundFee"));
                        refunded_amount = Double.parseDouble(roundTwoDecimals(refunded_amount));

                    }
                    remaining_amount = Double.parseDouble(jsonObject.optString("grandTotal")) - refunded_amount;
                    remaining_amount = Double.parseDouble(roundTwoDecimals(remaining_amount));

                    list.add(new PrintDataObject("-----------------------------",
                            fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                            true));
                    list.add(new PrintDataObject("Refund Transaction Details",
                            fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                            true));
                    list.add(new PrintDataObject("-----------------------------",
                            fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                            true));
                    Double amt = Double.parseDouble(amountTotal);
                    for (int i = 0; i < jsonObjectTransactionDetails.optJSONArray("refunds").length(); i++) {
                        JSONObject jsonObject1 = jsonObjectTransactionDetails.optJSONArray("refunds").optJSONObject(i);
                        if (!jsonObject1.optString("refundTradeNo").equals("")
                                && !jsonObject1.optString("createDate").equals("")
                                && !jsonObject1.optString("refundFee").equals("0.0")
                        ) {

                            list.add(new PrintDataObject("Refund Trade No:",
                                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                                    true));
                            list.add(new PrintDataObject(jsonObject1.optString("refundTradeNo"),
                                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                                    true));
                            list.add(new PrintDataObject("Refund Pay Time:",
                                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                                    true));


                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            df.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date d = df.parse(jsonObject1.optString("createDate").replace("T", " "));
                            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            df1.setTimeZone(TimeZone.getTimeZone(preferenceManager.getTimeZoneId()));

                            list.add(new PrintDataObject(df1.format(d),
                                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                                    true));
                            list.add(new PrintDataObject("Amount Refunded:" + preferenceManager.getcurrency() + " " + jsonObject1.optString("refundFee"),
                                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                                    true));
                            amt = (Double.parseDouble(roundTwoDecimals(Float.valueOf(amt + "")))) - Double.parseDouble(jsonObject1.optString("refundFee"));
                            list.add(new PrintDataObject("Amount Available:" + preferenceManager.getcurrency() + " " + roundTwoDecimals(Float.valueOf(amt + "")),
                                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                                    true));
                            list.add(new PrintDataObject("-----------------------------",
                                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                                    true));

                        }

                    }

                }

            list.add(new PrintDataObject("Payment via: " + jsonObject.optString("channel"),
                    fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                    true));
            String text = "";
            if (jsonObject.optString("channel").equals("UNION_PAY")) {
                text = newjson.optString("CUP Reference No");
            } else {
                text = jsonObject.optString("referenceId");
            }


            if (preferenceManager.isQR()) {
                if (!text.equals("")) {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        //bitmap.recycle();
                        //  Printer.getInstance().addImage(byteArray);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                }

            }

            list.add(new PrintDataObject("----Duplicate Receipt------",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            if (!preferenceManager.isQR()) {
                list.add(new PrintDataObject("\n\n",
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }


        } catch (Exception e) {
        }


        try {
            int ret = printDev.printTextEffect(list);
            if (preferenceManager.isQR()) {
                printDev.printBmpFast(bitmap, Constant.ALIGN.LEFT, callback);
                bitmap.recycle();

            }
            if (preferenceManager.isQR()) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            printDev.spitPaper(50);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, 1000);
            }
            Log.e("test", "返回码：" + ret);
            getMessStr(ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void getMessStr(int ret) {
        switch (ret) {
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_BUSY:
                Toast.makeText(TransactionDetailsActivity.this, getString(R.string.printer_device_busy), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_OK:
                Toast.makeText(TransactionDetailsActivity.this, getString(R.string.printer_success), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_OUT_OF_PAPER:
                Toast.makeText(TransactionDetailsActivity.this, getString(R.string.printer_lack_paper), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_HEAD_OVER_HEIGH:
                Toast.makeText(TransactionDetailsActivity.this, getString(R.string.printer_over_heigh), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_OVER_HEATER:
                Toast.makeText(TransactionDetailsActivity.this, getString(R.string.printer_over_heat), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_LOW_POWER:
                Toast.makeText(TransactionDetailsActivity.this, getString(R.string.printer_low_power), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(TransactionDetailsActivity.this, getString(R.string.printer_other_exception_code) + ret, Toast.LENGTH_SHORT).show();
                break;
        }

    }


    public AidlDeviceManager manager = null;
    Intent intentService;

    public void bindService() {
        intentService = new Intent();
        intentService.setPackage("com.centerm.smartposservice");
        intentService.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        bindService(intentService, conn, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn);
        }
        stopService(intentService);
    }

    /**
     * 服务连接桥
     */
    public ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            manager = null;
            LogUtil.print(getResources().getString(R.string.bind_service_fail));
            LogUtil.print("manager = " + manager);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            manager = AidlDeviceManager.Stub.asInterface(service);
            LogUtil.print(getResources().getString(R.string.bind_service_success));
            LogUtil.print("manager = " + manager);
            if (null != manager) {
                try {
                    onDeviceConnected(manager);
                } catch (Exception e) {

                }

            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callAuthToken();
        if (isUnionPay) {
            isUnionPay = false;
            try {
                String text = "";
                if (jsonObjectTransactionDetails.optJSONObject("payment").optString("channel").equals("UNION_PAY")) {
                    text = newjson.optString("CUP Reference No");
                } else {
                    text = jsonObject.optString("reference_id");
                }


                if (preferenceManager.isQR()) {
                    if (!text.equals("")) {
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        try {
                            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            bitmap = barcodeEncoder.createBitmap(bitMatrix);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            //bitmap.recycle();
                            //  Printer.getInstance().addImage(byteArray);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                    }

                }
                if (preferenceManager.isQR()) {
                    printDev.printBmpFast(bitmap, Constant.ALIGN.LEFT, callback);
                    bitmap.recycle();

                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            printDev.spitPaper(50);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        if (data.hasExtra("responseCodeThirtyNine")) {
            try {
                Bundle bundle = new Bundle();
                bundle.putAll(data.getExtras());
                JSONObject json = new JSONObject();
                Set<String> keys = bundle.keySet();
                for (String key : keys) {
                    if (bundle.get(key) != null)
                        json.put(key, JSONObject.wrap(bundle.get(key)));
                }
                json.put("orderNumber", jsonObjectSale.optString("orderNumber"));
                if (!json.optString("responseCodeThirtyNine").equals("00"))
                    return;
                onTaskCompleted(json.toString(), "Arke");
            } catch (Exception e) {
                //Handle exception here
            }

        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (DashboardActivity.isExternalApp) {
            DashboardActivity.isExternalApp = false;
            TransactionDetailsActivity.isReturnFromTransactionDetails = true;
        }
        finish();
    }
}
