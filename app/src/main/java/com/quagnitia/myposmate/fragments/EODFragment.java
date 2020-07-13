package com.quagnitia.myposmate.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.quagnitia.myposmate.arke.TransactionNames;
import com.quagnitia.myposmate.arke.VASCallsArkeBusiness;
import com.quagnitia.myposmate.centrum.ThirtConst;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Created by admin on 9/18/2018.
 */

public class EODFragment extends Fragment implements View.OnClickListener, OnTaskCompleted {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PrinterDemo";
    private static final int FONT_SIZE_SMALL = 0;
    private static final int FONT_SIZE_NORMAL = 1;
    private static final int FONT_SIZE_LARGE = 2;
    private VASCallsArkeBusiness vasCallsArkeBusiness;
    private String mParam1;
    private String mParam2;
    private View view;
    ProgressDialog progress;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button btn_print, btn_settlement, btn_apply_filter;
    private PreferencesManager preferencesManager;
    AlertDialog dialog;
    private TextView tv_payment_amount, tv_payment_count, tv_refunded_amount, tv_refund_count, tv_total_transactions;
    private TextView edt_end_datetime, edt_start_datetime, edt_start_time, edt_end_time;
    private TextView tv_ali_payment_amount, tv_ali_payment_count, tv_ali_refund_amount, tv_ali_refund_count,
            tv_we_payment_amount, tv_we_payment_count, tv_we_refund_amount, tv_we_refund_count, tv_union_payment_amount,
            tv_union_payment_count, tv_union_refund_amount, tv_union_refund_count;
    TreeMap<String, String> hashMapKeys;
    JSONObject jsonObjectSale;
    Intent intentCen = new Intent();
    String packageName = "com.centerm.dynaminpayinskate";
    String activityName = "org.skate.pay.component.PayEntry";
    ComponentName comp = new ComponentName(packageName, activityName);
    private static final int REQ_PAY_SALE = 100;

    public EODFragment() {
        // Required empty public constructor
    }

    public static EODFragment newInstance(String param1, String param2) {
        EODFragment fragment = new EODFragment();
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
        view = inflater.inflate(R.layout.fragment_eod, container, false);
        preferencesManager = PreferencesManager.getInstance(getActivity());
        hashMapKeys = new TreeMap<>();
        dialog = new AlertDialog.Builder(getActivity())
                .setNegativeButton(getString(R.string.cancel), null)
                .setCancelable(false)
                .create();
        initUI();
        initListener();
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

    public void openProgressDialog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    boolean isStart = false;

    public void initUI() {

        vasCallsArkeBusiness = new VASCallsArkeBusiness(getActivity());
        edt_end_datetime = view.findViewById(R.id.edt_end_datetime);
        edt_start_datetime = view.findViewById(R.id.edt_start_datetime);
        edt_start_time = view.findViewById(R.id.edt_start_time);
        edt_end_time = view.findViewById(R.id.edt_end_time);

        isStart = true;
        callAuthToken();
        btn_print = view.findViewById(R.id.btn_print);
        btn_settlement = view.findViewById(R.id.btn_settlement);
        btn_apply_filter = view.findViewById(R.id.btn_apply_filter);
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

        edt_end_datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                String s[] = edt_end_datetime.getText().toString().split("-");
                mYear = Integer.parseInt(s[0]);
                mMonth = Integer.parseInt(s[1]);
                mDay = Integer.parseInt(s[2]);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                try {
                                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                                    Date d = df1.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                    edt_end_datetime.setText(df1.format(d));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }, mYear, mMonth - 1, mDay);
                datePickerDialog.show();


            }
        });
        edt_start_datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                String s[] = edt_start_datetime.getText().toString().split("-");
                mYear = Integer.parseInt(s[0]);
                mMonth = Integer.parseInt(s[1]);
                mDay = Integer.parseInt(s[2]);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                try {
                                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                                    Date d = df1.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                    edt_start_datetime.setText(df1.format(d));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }, mYear, mMonth - 1, mDay);
                datePickerDialog.show();

            }
        });
        edt_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                String s[] = edt_start_time.getText().toString().split(":");
                mHour = Integer.parseInt(s[0]);//c.get(Calendar.HOUR_OF_DAY);
                mMinute = Integer.parseInt(s[1]);//c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                try {
                                    SimpleDateFormat df1 = new SimpleDateFormat("HH:mm:ss");
                                    Date d = df1.parse(hourOfDay + ":" + minute + ":" + s[2]);
                                    edt_start_time.setText(df1.format(d));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });
        edt_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                String s[] = edt_end_time.getText().toString().split(":");
                mHour = Integer.parseInt(s[0]);
                mMinute = Integer.parseInt(s[1]);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                try {
                                    SimpleDateFormat df1 = new SimpleDateFormat("HH:mm:ss");
                                    Date d = df1.parse(hourOfDay + ":" + minute + ":" + s[2]);
                                    edt_end_time.setText(df1.format(d));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });
    }


    public void callTimeStampConversion(String s) {
        try {

            JSONObject jsonObjectTimeNZ = new JSONObject(s);
            String TimeStamp = jsonObjectTimeNZ.optString("time");
            String ss1[] = TimeStamp.split("T");
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df1.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df2.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));
            Date d = df1.parse(ss1[0] + " " + ss1[1]);
            String datetime = df2.format(d);
            String ss[] = datetime.split(" ");

            edt_start_datetime.setText(ss[0]);
            edt_end_datetime.setText(ss[0]);
            edt_start_time.setText("00:00:00");
            edt_end_time.setText(ss[1]);

            callGetReports();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void callTimeStamp() {

        openProgressDialog();
        try {
            HashMap<String, String> jsonObject = new HashMap<>();
            jsonObject.put("zone_id", preferencesManager.getTimeZoneId());
            new OkHttpHandler(getActivity(), this, null, "TimeStamp").execute(AppConstants.BASE_URL3 + AppConstants.GET_CURRENT_DATETIME + "?access_token=" + preferencesManager.getauthToken());//"http://worldclockapi.com/api/json/NZST/now");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initListener() {
        btn_print.setOnClickListener(this);
        btn_apply_filter.setOnClickListener(this);
        btn_settlement.setOnClickListener(this);
    }

    public void callGetReports() {

        openProgressDialog();
        try {

            SimpleDateFormat mainConv = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS");
            mainConv.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df1.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df2.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));

            String startTime = edt_start_datetime.getText().toString() + "T" + edt_start_time.getText().toString();
            Date d = df2.parse(edt_end_datetime.getText().toString() + "T" + edt_end_time.getText().toString());
            String endTime = df1.format(d);

            startTime = df1.format(df2.parse(startTime));


            hashMapKeys.clear();
            hashMapKeys.put("access_id", preferencesManager.getuniqueId());
            hashMapKeys.put("branch_id", preferencesManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferencesManager.getterminalId());
            hashMapKeys.put("config_id", preferencesManager.getConfigId());
            hashMapKeys.put("start_date", URLEncoder.encode(mainConv.format(df2.parse(startTime)) + preferencesManager.getTimezoneAbrev(), "UTF-8"));
            hashMapKeys.put("end_date", URLEncoder.encode(mainConv.format(df2.parse(endTime)) + preferencesManager.getTimezoneAbrev(), "UTF-8"));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            new OkHttpHandler(getActivity(), this, null, "GetReports")
                    .execute(AppConstants.BASE_URL2 + AppConstants.GET_CHANNEL_SUMMARY + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferencesManager.getauthToken());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Context mContext;

    boolean isApplyfilter = false;

    @Override
    public void onClick(View v) {
        mContext = getActivity();
        if (((DashboardActivity) mContext).mPopupWindow.isShowing())
            ((DashboardActivity) mContext).mPopupWindow.dismiss();

        switch (v.getId()) {
            case R.id.btn_settlement:
                beginSettlement();

                break;
            case R.id.btn_print:
                try {
                    print();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_apply_filter:
                isApplyfilter = true;
                callAuthToken();

                break;
        }
    }

    public void beginSettlement() {

        hideSoftInput();


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

    private void doTransaction(String interfaceId, JSONObject jsonObject) {
        if (TransactionNames.SALE_BY_SDK.name().equals(interfaceId)) {
            vasCallsArkeBusiness.doTransaction(interfaceId, jsonObject, this);
        } else {
            vasCallsArkeBusiness.doTransaction(interfaceId, jsonObject, this);
        }
    }

    private void hideSoftInput() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }


    String roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        return twoDForm.format(d);
    }

    public void callAuthToken() {
        openProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.AUTH);

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
                if (isStart) {
                    isStart = false;
                    callTimeStamp();
                }
                if (isApplyfilter) {
                    isApplyfilter = false;
                    callGetReports();
                }
                break;
            case "TimeStamp":
                callTimeStampConversion(result);
                break;
            case "GetReports":
                callAuthToken();
                JSONObject alipaySummary = jsonObject.optJSONObject("alipaySummary");
                JSONObject wechatSummary = jsonObject.optJSONObject("wechatSummary");
                JSONObject unionpaySummary = jsonObject.optJSONObject("unionpaySummary");

                Double paymentamount = Double.parseDouble(alipaySummary.optString("paymentAmount")) +
                        Double.parseDouble(wechatSummary.optString("paymentAmount")) +
                        Double.parseDouble(unionpaySummary.optString("paymentAmount"));

                Integer paymentcount = Integer.parseInt(alipaySummary.optString("paymentCount")) +
                        Integer.parseInt(wechatSummary.optString("paymentCount")) +
                        Integer.parseInt(unionpaySummary.optString("paymentCount"));//+

                Integer totalTransactions = Integer.parseInt(alipaySummary.optString("paymentCount")) +
                        Integer.parseInt(alipaySummary.optString("refundCount")) +
                        Integer.parseInt(wechatSummary.optString("paymentCount")) +
                        Integer.parseInt(wechatSummary.optString("refundCount")) +
                        Integer.parseInt(unionpaySummary.optString("paymentCount")) +
                        Integer.parseInt(unionpaySummary.optString("refundCount"));

                tv_payment_amount.setText("$" + roundTwoDecimals(paymentamount));
                tv_payment_count.setText(paymentcount + "");
                tv_refunded_amount.setText("$" + roundTwoDecimals(Double.parseDouble(jsonObject.optJSONObject("totalSummary").optString("refundAmount"))));
                tv_refund_count.setText(jsonObject.optJSONObject("totalSummary").optString("refundCount"));
                tv_total_transactions.setText(totalTransactions + "");

                tv_ali_payment_amount.setText("$" + roundTwoDecimals(Double.parseDouble(alipaySummary.optString("paymentAmount"))));
                tv_ali_payment_count.setText(alipaySummary.optString("paymentCount"));
                tv_ali_refund_amount.setText("$" + roundTwoDecimals(Double.parseDouble(alipaySummary.optString("refundAmount"))));
                tv_ali_refund_count.setText(alipaySummary.optString("refundCount"));

                tv_we_payment_amount.setText("$" + roundTwoDecimals(Double.parseDouble(wechatSummary.optString("paymentAmount"))));
                tv_we_payment_count.setText(wechatSummary.optString("paymentCount"));
                tv_we_refund_amount.setText("$" + roundTwoDecimals(Double.parseDouble(wechatSummary.optString("refundAmount"))));
                tv_we_refund_count.setText(wechatSummary.optString("refundCount"));

                tv_union_payment_amount.setText("$" + roundTwoDecimals(Double.parseDouble(unionpaySummary.optString("paymentAmount"))));
                tv_union_payment_count.setText(unionpaySummary.optString("paymentCount"));
                tv_union_refund_amount.setText("$" + roundTwoDecimals(Double.parseDouble(unionpaySummary.optString("refundAmount"))));
                tv_union_refund_count.setText(unionpaySummary.optString("refundCount"));

                break;

        }
    }

    /**
     * Print.
     */
    public void print() throws RemoteException {

        try {
//            JSONObject jsonObject = new JSONObject(preferencesManager.getmerchant_info().toString());
            final List<PrintDataObject> list = new ArrayList<PrintDataObject>();

            int fontSize = 24;
//            list.add(new PrintDataObject("Merchant Name: " + jsonObject.optString("company"),
//                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
//                    true));
            if (!preferencesManager.getmerchant_name().equals("")) {
                list.add(new PrintDataObject("Branch Name: " + preferencesManager.getmerchant_name(),
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }
            list.add(new PrintDataObject("Start: " + edt_start_datetime.getText().toString() + " " + edt_start_time.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject("End: " + edt_end_datetime.getText().toString() + " " + edt_end_time.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
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


            list.add(new PrintDataObject("UnionPay Payment Amount: " + tv_union_payment_amount.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));

            list.add(new PrintDataObject("UnionPay Payment Count: " + tv_union_payment_count.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));

            list.add(new PrintDataObject("UnionPay Refund Amount: " + tv_union_refund_amount.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));

            list.add(new PrintDataObject("UnionPay Refund Count: " + tv_union_refund_count.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));

            list.add(new PrintDataObject("------------------------------",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));


            list.add(new PrintDataObject("NOTE",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));

            String ss = "Numbers shown are based on transactions  recorded on terminal and 100 percent accuracy is not guaranteed . Accuracy of can be confirmed on the payment providers web portal.";

            char c[] = ss.toCharArray();
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
            list.add(new PrintDataObject("\n",
                    4, false, PrintDataObject.ALIGN.LEFT));

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

    private AidlPrinter printDev = null;
    // 打印机回调对象
    private AidlPrinterStateChangeListener callback = new PrinterCallback(); // 打印机回调
    private EditText qrCode, barCode;
    private String qrStr;
    private String barStr;
    private Spinner spinner;
    private int typeIndex;
    private String codeStr;


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

    public AidlDeviceManager manager = null;

    public void bindService() {
        Intent intent = new Intent();
        intent.setPackage("com.centerm.smartposservice");
        intent.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);
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


}
