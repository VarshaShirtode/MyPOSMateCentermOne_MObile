package com.quagnitia.myposmate.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.aidl.printer.PrintDataObject;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.constant.DeviceErrorCode;
import com.centerm.smartpos.util.LogUtil;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.arke.VASCallsArkeBusiness;
import com.quagnitia.myposmate.centrum.ThirtConst;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;


public class Settlement extends Fragment implements OnTaskCompleted {

    private AidlPrinterStateChangeListener callback = new PrinterCallback(); // 打印机回调
    private EditText qrCode, barCode;
    private String qrStr;
    private String barStr;
    private Spinner spinner;
    private int typeIndex;
    private String codeStr;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQ_PAY_SALE = 100;
    public AidlDeviceManager manager = null;
    ProgressDialog progress;
    AlertDialog dialog;
    TreeMap<String, String> hashMapKeys;
    JSONObject jsonObjectSale;
    Intent intentCen = new Intent();
    String packageName = "com.centerm.dynaminpayinskate";
    String activityName = "org.skate.pay.component.PayEntry";
    ComponentName comp = new ComponentName(packageName, activityName);
    boolean isSettlementReport = false;
    private VASCallsArkeBusiness vasCallsArkeBusiness;
    private String mParam1;
    private String mParam2;
    private View view;
    private EditText edt_email;
    private EditText edt_last_settlement_date;
    private LinearLayout ll_we;
    private Button btn_settlement_unionpay, btn_print_alipay, btn_settlement, btn_ok;
    private PreferencesManager preferencesManager;
    private TextView tv_payment_amount, tv_settled_on, tv_payment_count, tv_refunded_amount, tv_refund_count, tv_total_transactions;
    private TextView tv_ali_payment_amount, tv_ali_payment_count, tv_ali_refund_amount, tv_ali_refund_count,
            tv_we_payment_amount, tv_we_payment_count, tv_we_refund_amount, tv_we_refund_count, tv_union_payment_amount,
            tv_union_payment_count, tv_union_refund_amount, tv_union_refund_count;
    private AidlPrinter printDev = null;

    public static boolean isAlipayWeChatSettle = false;


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



    public Settlement() {
        // Required empty public constructor
    }

    public static Settlement newInstance(String param1, String param2) {
        Settlement fragment = new Settlement();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settlement, container, false);
        initials();
        return view;
    }

    public void initials() {
        preferencesManager = PreferencesManager.getInstance(getActivity());
        hashMapKeys = new TreeMap<>();
        initUI();
        initListener();
        callAuthToken();
        if(!preferencesManager.getcontact_email().equals(""))
        {
            isSettlementReport = true;
            edt_email.setText(preferencesManager.getcontact_email());
        }

    }

    public void openProgressDialog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    public void initUI() {
        ll_we = view.findViewById(R.id.ll_we);
        ll_we.setVisibility(View.GONE);
        tv_settled_on = view.findViewById(R.id.tv_settled_on);
        tv_settled_on.setVisibility(View.INVISIBLE);
        edt_email = view.findViewById(R.id.edt_email);
        edt_last_settlement_date = view.findViewById(R.id.edt_last_settlement_date);
        btn_settlement_unionpay = view.findViewById(R.id.btn_settlement_unionpay);
        btn_settlement = view.findViewById(R.id.btn_settlement);
        btn_ok = view.findViewById(R.id.btn_ok);
        btn_print_alipay = view.findViewById(R.id.btn_print_alipay);
        btn_print_alipay.setVisibility(View.GONE);
        tv_payment_amount = view.findViewById(R.id.tv_payment_amount);
        tv_payment_count = view.findViewById(R.id.tv_payment_count);
        tv_refunded_amount = view.findViewById(R.id.tv_refunded_amount);
        tv_refund_count = view.findViewById(R.id.tv_refund_count);
        tv_total_transactions = view.findViewById(R.id.tv_total_transactions);
        tv_ali_payment_amount = view.findViewById(R.id.tv_ali_payment_amount);
        tv_ali_payment_count = view.findViewById(R.id.tv_ali_payment_count);
        tv_ali_refund_amount = view.findViewById(R.id.tv_ali_refund_amount);
        tv_ali_refund_count = view.findViewById(R.id.tv_ali_refund_count);
        tv_we_payment_amount = view.findViewById(R.id.tv_we_payment_amount);
        tv_we_payment_count = view.findViewById(R.id.tv_we_payment_count);
        tv_we_refund_amount = view.findViewById(R.id.tv_we_refund_amount);
        tv_we_refund_count = view.findViewById(R.id.tv_we_refund_count);
        tv_union_payment_amount = view.findViewById(R.id.tv_union_payment_amount);
        tv_union_payment_count = view.findViewById(R.id.tv_union_payment_count);
        tv_union_refund_amount = view.findViewById(R.id.tv_union_refund_amount);
        tv_union_refund_count = view.findViewById(R.id.tv_union_refund_count);

    }

    public void initListener() {
        btn_settlement_unionpay.setOnClickListener((View v) -> funcUnionPaySettlement());
        btn_settlement.setOnClickListener((View v) -> funcAlipayWeChatSettlement());
        btn_ok.setOnClickListener((View v) -> funcOK());
        btn_print_alipay.setOnClickListener((View v) -> {
            try {
                print(jsonObjectSettlement);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }


    public void funcUnionPaySettlement() {
        beginSettlement();
    }

    public void funcAlipayWeChatSettlement() {
        callTimeStamp();

    }

    public void funcOK() {
        if (preferencesManager.isManual()) {
            ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
        } else {
            ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
        }
    }


    public void callSettlementReport() {

        openProgressDialog();
        try {
            hashMapKeys.clear();
            if(edt_email.getText().toString().equals(""))
            {
                Toast.makeText(getActivity(), "Please enter the email id", Toast.LENGTH_SHORT).show();
                return;
            }
            hashMapKeys.put("email",edt_email.getText().toString());
            hashMapKeys.put("terminal_id",preferencesManager.getterminalId());
            hashMapKeys.put("access_id",preferencesManager.getuniqueId());
            hashMapKeys.put("branch_id", preferencesManager.getMerchantId());
            hashMapKeys.put("config_id", preferencesManager.getConfigId());
            hashMapKeys.put("random_str", new Date().getTime() + "");
            new OkHttpHandler(getActivity(), this, null, "SettlementReports")
                    .execute(AppConstants.BASE_URL2 + AppConstants.SETTLEMENT_REPORT + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferencesManager.getauthToken());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callTimeStamp() {
        try {
            new OkHttpHandler(getActivity(), this, null, "TimeStamp").execute(AppConstants.BASE_URL3 + AppConstants.GET_CURRENT_DATETIME + "?access_token=" + preferencesManager.getauthToken());//"http://worldclockapi.com/api/json/NZST/now");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callSettle() {

        openProgressDialog();
        try {
            hashMapKeys.clear();
            if (edt_email.getText().toString().equals("")) {
                progress.dismiss();
                Toast.makeText(getActivity(), "Please id not received from server.Please enter the email id.", Toast.LENGTH_SHORT).show();
                return;
            }


            if (preferencesManager.getLaneIdentifier().equals("")) {
                Toast.makeText(getActivity(), "Please fill in the lane identifier in branch info", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                return;
            }
            if (preferencesManager.getPOSIdentifier().equals("")) {
                progress.dismiss();
                Toast.makeText(getActivity(), "Please fill in the pos identifier in branch info", Toast.LENGTH_SHORT).show();
                return;
            }

            if( preferencesManager.getmerchant_name().equals(""))
            {
                progress.dismiss();
                Toast.makeText(getActivity(), "Please fill in the branch name in branch info", Toast.LENGTH_SHORT).show();
                return;
            }
            if( preferencesManager.getTerminalIdentifier().equals(""))
            {
                progress.dismiss();
                Toast.makeText(getActivity(), "Please fill in the terminal identifier in branch info", Toast.LENGTH_SHORT).show();
                return;
            }

            hashMapKeys.put("access_id",preferencesManager.getuniqueId());
            hashMapKeys.put("branch_name", preferencesManager.getmerchant_name());
          //  if(!datetime.equals(""))
          //  hashMapKeys.put("timezone", datetime.replace(" ","T"));
            hashMapKeys.put("lane_id", preferencesManager.getLaneIdentifier());
            hashMapKeys.put("pos_id", preferencesManager.getPOSIdentifier());
            if(edt_email.getText().toString().equals(""))
            {
                Toast.makeText(getActivity(), "Please enter the email id", Toast.LENGTH_SHORT).show();
                return;
            }
            hashMapKeys.put("email", edt_email.getText().toString());
            hashMapKeys.put("terminal_id", preferencesManager.getterminalId());
            hashMapKeys.put("branch_id", preferencesManager.getMerchantId());
            hashMapKeys.put("config_id", preferencesManager.getConfigId());
            hashMapKeys.put("random_str", new Date().getTime() + "");
//            hashMapKeys.put("signature",MD5Class.generateSignatureString(hashMapKeys, getActivity()));
//            hashMapKeys.put("access_token",preferencesManager.getauthToken());
            HashMap hashMap=new HashMap();
            hashMap.putAll(hashMapKeys);
            new OkHttpHandler(getActivity(), this, null, "Settle")
                    .execute(AppConstants.BASE_URL2 + AppConstants.SETTLE + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferencesManager.getauthToken());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void beginSettlement() {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.SETTLE);
            intentCen.setComponent(comp);
            intentCen.putExtras(bundle);
            startActivityForResult(intentCen, REQ_PAY_SALE);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void callAuthToken() {
        openProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
//        hashMap.put("username", AppConstants.CLIENT_ID);
//        hashMap.put("password",AppConstants.CLIENT_SECRET);
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.AUTH);

    }

    String datetime="";

    public void callTimeStampConversion(String s) {
        try {
            String ss1[] = s.split("T");
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df1.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df2.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));
            Date d = df1.parse(ss1[0] + " " + ss1[1]);
            datetime = df2.format(d);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    String roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        return twoDForm.format(d);
    }

    JSONObject jsonObject = null, jsonObjectSettlement = null;
    public static String resultTime = "";

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

        if (!TAG.equals("SettlementReports")) {
            jsonObject = new JSONObject(result);

        }
        if (TAG.equals("Settle")) {
            jsonObjectSettlement = new JSONObject(result);
        }


        switch (TAG) {
            case "TimeStamp":
                resultTime = jsonObject.optString("time");
                isAlipayWeChatSettle = true;
                callAuthToken();

                break;
            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferencesManager.setauthToken(jsonObject.optString("access_token"));
                }
                if (isSettlementReport) {
                    isSettlementReport = false;
                    callSettlementReport();
                }
                if (isAlipayWeChatSettle) {
                    isAlipayWeChatSettle = false;
                    callTimeStampConversion(resultTime);
                    callSettle();
                }
                break;
            case "Settle":
                if (jsonObjectSettlement.optBoolean("status")) {
                    callAuthToken();
                    ll_we.setVisibility(View.VISIBLE);

                    Double paymentamount = Double.parseDouble(jsonObjectSettlement.optString("alipayPaymentAmount")) +
                            Double.parseDouble(jsonObjectSettlement.optString("wechatPaymentAmount")) +
                            Double.parseDouble(jsonObjectSettlement.optString("upPaymentAmount"));

                    Double refundedAmount=Double.parseDouble(jsonObjectSettlement.optString("alipayRefundAmount"))+
                            Double.parseDouble(jsonObjectSettlement.optString("wechatRefundAmount"))+
                            Double.parseDouble(jsonObjectSettlement.optString("upRefundAmount"));

                    Integer paymentcount = Integer.parseInt(jsonObjectSettlement.optString("alipayPaymentCount")) +
                            Integer.parseInt(jsonObjectSettlement.optString("wechatPaymentCount")) +
                            Integer.parseInt(jsonObjectSettlement.optString("upPaymentCount"));//+

                    Integer refundcount = Integer.parseInt(jsonObjectSettlement.optString("alipayRefundCount")) +
                            Integer.parseInt(jsonObjectSettlement.optString("wechatRefundCount")) +
                            Integer.parseInt(jsonObjectSettlement.optString("upRefundCount"));//+

                    Integer totalTransactions = Integer.parseInt(jsonObjectSettlement.optString("alipayRefundCount")) +
                            Integer.parseInt(jsonObjectSettlement.optString("wechatRefundCount")) +
                            Integer.parseInt(jsonObjectSettlement.optString("alipayPaymentCount")) +
                            Integer.parseInt(jsonObjectSettlement.optString("wechatPaymentCount")) +
                            Integer.parseInt(jsonObjectSettlement.optString("upPaymentCount")) +
                            Integer.parseInt(jsonObjectSettlement.optString("upRefundCount"));

                    tv_payment_amount.setText("$" + roundTwoDecimals(paymentamount));
                    tv_payment_count.setText(paymentcount + "");
                    tv_refunded_amount.setText("$" + roundTwoDecimals(refundedAmount));
                    tv_refund_count.setText(refundcount+"");
                    tv_total_transactions.setText(totalTransactions + "");
                    tv_ali_payment_amount.setText("$" + jsonObjectSettlement.optString("alipayPaymentAmount"));
                    tv_ali_payment_count.setText(jsonObjectSettlement.optString("alipayPaymentCount"));
                    tv_ali_refund_amount.setText("$" + jsonObjectSettlement.optString("alipayRefundAmount"));
                    tv_ali_refund_count.setText(jsonObjectSettlement.optString("alipayRefundCount"));
                    tv_we_payment_amount.setText("$" + jsonObjectSettlement.optString("wechatPaymentAmount"));
                    tv_we_payment_count.setText(jsonObjectSettlement.optString("wechatPaymentCount"));
                    tv_we_refund_amount.setText("$" + jsonObjectSettlement.optString("wechatRefundAmount"));
                    tv_we_refund_count.setText(jsonObjectSettlement.optString("wechatRefundCount"));
                    tv_union_payment_amount.setText("$" + jsonObjectSettlement.optString("upPaymentAmount"));
                    tv_union_payment_count.setText(jsonObjectSettlement.optString("upPaymentCount"));
                    tv_union_refund_amount.setText("$" + jsonObjectSettlement.optString("upRefundAmount"));
                    tv_union_refund_count.setText(jsonObjectSettlement.optString("upRefundCount"));
                    // print(jsonObject);
                    btn_print_alipay.setVisibility(View.VISIBLE);
                    tv_settled_on.setVisibility(View.VISIBLE);

                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    df1.setTimeZone(TimeZone.getTimeZone("UTC"));
                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    df2.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));
                    tv_settled_on.setText("Settled On: " + df2.format(df1.parse(jsonObjectSettlement.optString("settlementDate"))).replace("T", " "));

                } else {
                    Toast.makeText(getActivity(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                }
                break;

            case "SettlementReports":
                callAuthToken();
                JSONArray jsonArray = new JSONArray(result);
                if (jsonArray.length() != 0) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(jsonArray.length() - 1);
                    edt_email.setText(jsonObject1.optString("email"));
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    df1.setTimeZone(TimeZone.getTimeZone("UTC"));
                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    df2.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));
                    edt_last_settlement_date.setText(df2.format(df1.parse(jsonObject1.optString("settlementDate"))).replace("T", " "));
                }
                break;

        }
    }

    /**
     * Print.
     */
    public void print(JSONObject jsonObjectSettlement) throws RemoteException {

        try {
//            JSONObject jsonObject = new JSONObject(preferencesManager.getmerchant_info());
            final List<PrintDataObject> list = new ArrayList<PrintDataObject>();

            int fontSize = 24;
            list.add(new PrintDataObject("Merchant Name: " + jsonObject.optString("company"),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));

            list.add(new PrintDataObject("Branch Name:",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject(preferencesManager.getbranchName(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            if (!preferencesManager.getmerchant_name().equals("")) {
                list.add(new PrintDataObject("Branch Info: " + preferencesManager.getmerchant_name(),
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }

            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df1.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df2.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));
            list.add(new PrintDataObject("Settlement Date: " + df2.format(df1.parse(jsonObjectSettlement.optString("settlementDate" +
                    ""))).replace("T", " "),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
//            list.add(new PrintDataObject("End: " + edt_end_datetime.getText().toString() + " " + edt_end_time.getText().toString(),
//                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
//                    true));
            list.add(new PrintDataObject("------------------------------",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("Payment Amount: " + tv_payment_amount.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("Payment Count: " + tv_payment_count.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("Refunded Amount: " + tv_refunded_amount.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("Refund Count: " + tv_refund_count.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("Total Transactions: " + tv_total_transactions.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("------------------------------",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));

            list.add(new PrintDataObject("Alipay Payment Amount: " + tv_ali_payment_amount.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("Alipay Payment Count: " + tv_ali_payment_count.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("Alipay Refund Amount: " + tv_ali_refund_amount.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("Alipay Refund Count: " + tv_ali_refund_count.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("------------------------------",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));

            list.add(new PrintDataObject("WeChat Payment Amount: " + tv_we_payment_amount.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("WeChat Payment Count: " + tv_we_payment_count.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("WeChat Refund Amount: " + tv_we_refund_amount.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("WeChat Refund Count: " + tv_we_refund_count.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("------------------------------",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));


//            list.add(new PrintDataObject("UnionPay Payment Amount: " + tv_union_payment_amount.getText().toString(),
//                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
//                    true));
//
//            list.add(new PrintDataObject("UnionPay Payment Count: " + tv_union_payment_count.getText().toString(),
//                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
//                    true));
//
//            list.add(new PrintDataObject("UnionPay Refund Amount: " + tv_union_refund_amount.getText().toString(),
//                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
//                    true));
//
//            list.add(new PrintDataObject("UnionPay Refund Count: " + tv_union_refund_count.getText().toString(),
//                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
//                    true));
//
//            list.add(new PrintDataObject("------------------------------",
//                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
//                    true));


            list.add(new PrintDataObject("NOTE",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));

            String ss = "Numbers shown are based on transactions  recorded on terminal and 100 percent accuracy is not guaranteed . Accuracy of can be confirmed on the payment providers web portal.";

            char[] c = ss.toCharArray();
            String s = "";
            int j = 0;
            for (int i = 0; i < c.length; i++) {
                s = s + c[i];
                j++;
                if (j == 32) {
                    j = 0;
                    list.add(new PrintDataObject(s,
                            4, false, PrintDataObject.ALIGN.LEFT));

                    s = "";
                } else if (i == c.length - 1) {
                    list.add(new PrintDataObject(s,
                            4, false, PrintDataObject.ALIGN.LEFT));
                }
            }

            printDev.spitPaper(50);
            int ret = printDev.printTextEffect(list);
            printDev.spitPaper(50);
            Log.e("test", "返回码：" + ret);
            getMessStr(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bindService();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            getActivity().unbindService(conn);
        }
        getActivity().stopService(intentService);
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

    private void getMessStr(int ret) {
        switch (ret) {
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_BUSY:
                Toast.makeText(getActivity(), getString(R.string.printer_device_busy), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_OK:
                Toast.makeText(getActivity(), getString(R.string.printer_success), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_OUT_OF_PAPER:
                Toast.makeText(getActivity(), getString(R.string.printer_lack_paper), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_HEAD_OVER_HEIGH:
                Toast.makeText(getActivity(), getString(R.string.printer_over_heigh), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_OVER_HEATER:
                Toast.makeText(getActivity(), getString(R.string.printer_over_heat), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_LOW_POWER:
                Toast.makeText(getActivity(), getString(R.string.printer_low_power), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), getString(R.string.printer_other_exception_code) + ret, Toast.LENGTH_SHORT).show();
                break;
        }

    }

    Intent intentService;

    public void bindService() {
        intentService = new Intent();
        intentService.setPackage("com.centerm.smartposservice");
        intentService.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        getActivity().bindService(intentService, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 打印机回调类
     */
    private class PrinterCallback extends AidlPrinterStateChangeListener.Stub {

        @Override
        public void onPrintError(int arg0) throws RemoteException {
            // showMessage("打印机异常" + arg0, Color.RED);
            Toast.makeText(getActivity(), arg0, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrintFinish() throws RemoteException {
            Toast.makeText(getActivity(), getString(R.string.printer_finish), Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onPrintOutOfPaper() throws RemoteException {
            Toast.makeText(getActivity(), getString(R.string.printer_need_paper), Toast.LENGTH_SHORT).show();
        }
    }


}
