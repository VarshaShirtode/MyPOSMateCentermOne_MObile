package com.quagnitia.myposmate.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.TreeMap;

public class TransactionListing extends Fragment implements View.OnClickListener, OnTaskCompleted {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private View view;
    private Button btn_apply_filter, btn_reprint;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private TextView edt_end_datetime, edt_start_datetime, edt_start_time, edt_end_time;
    ProgressDialog progress;
    private PreferencesManager preferencesManager;
    private RecyclerView recycler_view;
    TreeMap<String, String> hashMapKeys;

    public TransactionListing() {
    }

    private Context mContext;

    @Override
    public void onClick(View v) {
        mContext = getActivity();
        if (((DashboardActivity) mContext).mPopupWindow.isShowing())
            ((DashboardActivity) mContext).mPopupWindow.dismiss();

        switch (v.getId()) {
            case R.id.btn_apply_filter:
                callTransactionList();
                break;
            case R.id.btn_reprint:
                beginLastPrint();
                break;
        }

    }

    public static TransactionListing newInstance(String param1, String param2) {
        TransactionListing fragment = new TransactionListing();
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

    public static boolean isInitialLaunch = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_transaction_listing, container, false);
        preferencesManager = PreferencesManager.getInstance(getActivity());
        hashMapKeys = new TreeMap<>();
        initUI();
        initListener();
        isInitialLaunch = true;
        callAuthToken();
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

    public void callAuthToken() {
        // openProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
//        hashMap.put("username", AppConstants.CLIENT_ID);
//        hashMap.put("password",AppConstants.CLIENT_SECRET);
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.AUTH);

    }

    private VASCallsArkeBusiness vasCallsArkeBusiness;

    public void initUI() {
        vasCallsArkeBusiness = new VASCallsArkeBusiness(getActivity());
        btn_apply_filter = (Button) view.findViewById(R.id.btn_apply_filter);
        btn_reprint = (Button) view.findViewById(R.id.btn_reprint);
        edt_end_datetime = (TextView) view.findViewById(R.id.edt_end_datetime);
        edt_start_datetime = (TextView) view.findViewById(R.id.edt_start_datetime);
        edt_start_time = (TextView) view.findViewById(R.id.edt_start_time);
        edt_end_time = (TextView) view.findViewById(R.id.edt_end_time);

        //  callTimeStamp();

        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());


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

    Intent intentCen = new Intent();
    String packageName = "com.centerm.dynaminpayinskate";
    String activityName = "org.skate.pay.component.PayEntry";
    ComponentName comp = new ComponentName(packageName, activityName);
    private static final int REQ_PAY_SALE = 100;

    public void beginLastPrint() {

        try {
            intentCen.setComponent(comp);
            Bundle bundle = new Bundle();
            bundle.putString(ThirtConst.RequestTag.THIRD_PATH_TRANS_TYPE, ThirtConst.TransType.PRINT_LAST);
            intentCen.putExtras(bundle);
            startActivityForResult(intentCen, REQ_PAY_SALE);
            //  doTransaction(TransactionNames.PRINT_LAST.name());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void doTransaction(String interfaceId) {
        vasCallsArkeBusiness.doTransaction(interfaceId, new JSONObject(), this);
    }

boolean isListingCalled=false;
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
            isListingCalled=true;
callAuthToken();


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


    @Override
    public void onResume() {
        super.onResume();


        if (TransactionDetailsActivity.isReturnFromTransactionDetails) {
            TransactionDetailsActivity.isReturnFromTransactionDetails=false;
            try {

                //added for external apps 12/5/2019
                int REQ_PAY_SALE = 100;
                DashboardActivity.isExternalApp = false;
                ((DashboardActivity) getActivity()).getIntent().putExtra("result",TransactionDetailsActivity.jsonObjectReturnResult.toString());
                ((DashboardActivity) getActivity()).setResult(REQ_PAY_SALE, ((DashboardActivity) getActivity()).getIntent());
                ((DashboardActivity) getActivity()).finish();
                return;
                //added for external apps


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (TransactionDetailsActivity.isRefundUnionPaySuccess) {

            callAuthToken();
        } else
            callTransactionList();


    }

    public void initListener() {
        btn_apply_filter.setOnClickListener(this);
        btn_reprint.setOnClickListener(this);
    }

    public void callTransactionList() {

        openProgressDialog();
        try {

            SimpleDateFormat mainConv = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS");
            mainConv.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df1.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df2.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));

            String startTime = edt_start_datetime.getText().toString() + "T" + edt_start_time.getText().toString();

            String endTime = df1.format(df2.parse(edt_end_datetime.getText().toString() + "T" + edt_end_time.getText().toString()));

            startTime = df1.format(df2.parse(startTime));
            hashMapKeys.clear();
            hashMapKeys.put("access_id",preferencesManager.getuniqueId());
            hashMapKeys.put("branch_id", preferencesManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferencesManager.getterminalId());
            hashMapKeys.put("config_id", preferencesManager.getConfigId());
            hashMapKeys.put("end_date", URLEncoder.encode(mainConv.format(df2.parse(endTime))+preferencesManager.getTimezoneAbrev(),"UTF-8"));
            hashMapKeys.put("start_date", URLEncoder.encode(mainConv.format(df2.parse(startTime))+preferencesManager.getTimezoneAbrev(),"UTF-8"));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("limit","1000");

            new OkHttpHandler(getActivity(), this, null, "TransactionListing")
                    .execute(AppConstants.BASE_URL2 + AppConstants.GET_RECENT_TRANSACTIONS + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferencesManager.getauthToken());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {

        if (result.equals("")) {
            if (progress != null && progress.isShowing())
                progress.dismiss();
            Toast.makeText(getActivity(), "No data from server.", Toast.LENGTH_LONG).show();
            return;
        }
        if (progress != null && progress.isShowing())
            progress.dismiss();


        JSONObject jsonObject = new JSONObject(result);

        switch (TAG) {
            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferencesManager.setauthToken(jsonObject.optString("access_token"));
                    if (TransactionDetailsActivity.isRefundUnionPaySuccess) {
                        TransactionDetailsActivity.isRefundUnionPaySuccess = false;
                        callTransactionList();
                    } else if (TransactionListing.isInitialLaunch) {
                        isInitialLaunch = false;
                        callTimeStamp();
                    }
                    else if(isListingCalled)
                    {
                        isListingCalled=false;
                        callTransactionList();
                    }
                }
                break;

            case "TimeStamp":
                if (!jsonObject.optBoolean("success")) {
                    //   Toast.makeText(getActivity(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                }
                callTimeStampConversion(result);
                break;


            case "TransactionListing":
                if (!jsonObject.optBoolean("success")) {
                    Toast.makeText(getActivity(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                }
                callAuthToken();
                TransactionListingAdapter transactionListingAdapter1 = new TransactionListingAdapter(getActivity(), null);
                recycler_view.setAdapter(transactionListingAdapter1);
                if (jsonObject.has("transactions")) {
                    JSONArray jsonArray = jsonObject.optJSONArray("transactions");
                    JSONArray jsonArray1 = new JSONArray();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        if (!jsonObject1.optString("paymentStatus").equals("REQUEST_RECEIVED")
                                && !jsonObject1.optString("paymentStatus").equals("WAIT_BUYER_PAY")
                                && !jsonObject1.optString("paymentStatus").equals("TRADE_NOT_PAY")
                                && !jsonObject1.optString("paymentStatus").equals("TRADE_NOT_ISEXIST")
                                && !jsonObject1.optString("paymentStatus").equals("INVALID_PARAMETER")
                                && !jsonObject1.optString("paymentStatus").equals("SYSTEM_ERROR")
                                && !jsonObject1.optString("paymentStatus").equals("USERPAYING")) {
                            jsonArray1.put(jsonObject1);
                        } else if (jsonObject1.optString("paymentStatus").equals("REQUEST_RECEIVED") &&
                                jsonObject1.optString("channel").equals("UNION_PAY")) {
                            jsonArray1.put(jsonObject1);
                        }


                    }


                    if (jsonArray.length() != 0) {
                        TransactionListingAdapter transactionListingAdapter = new TransactionListingAdapter(getActivity(), jsonArray1);
                        recycler_view.setAdapter(transactionListingAdapter);
                    } else {
                        Toast.makeText(getActivity(), "No items found", Toast.LENGTH_LONG).show();
                    }
                }

                break;


        }
    }
}
