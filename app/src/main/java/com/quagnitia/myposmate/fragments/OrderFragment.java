package com.quagnitia.myposmate.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

public class OrderFragment extends Fragment implements View.OnClickListener, OnTaskCompleted {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    boolean isListing=false;
    TreeMap<String, String> hashMapKeys;
    PreferencesManager preferenceManager;
    ProgressDialog progress;
    EditText edt_rejected,edt_completed,edt_accepted,edt_reviewed,edt_total_orders;
    View view;
    private RecyclerView recycler_view;

    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_orders, container, false);
        preferenceManager = PreferencesManager.getInstance(getActivity());
        hashMapKeys = new TreeMap<>();
        initUI();
        initListener();

        return view;
    }

    @Override
    public void onResume() {
        isListing=true;
        callAuthToken();
        super.onResume();
    }

    public void initUI() {
        recycler_view = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        edt_rejected=view.findViewById(R.id.edt_rejected);
        edt_completed=view.findViewById(R.id.edt_completed);
        edt_accepted=view.findViewById(R.id.edt_accepted);
        edt_reviewed=view.findViewById(R.id.edt_to_review);
        edt_total_orders=view.findViewById(R.id.edt_total_orders);

    }

    public void initListener() {

    }

    @Override
    public void onClick(View view) {

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
        openProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(preferenceManager.getBaseURL()+AppConstants.AUTH2);

    }

    public void callOrderList() {
        openProgressDialog();
        hashMapKeys.clear();
        hashMapKeys.put("branchID", preferenceManager.getMerchantId());
        hashMapKeys.put("configID", preferenceManager.getConfigId());
        hashMapKeys.put("hubID", "946");
        hashMapKeys.put("random_str", new Date().getTime() + "");
        new OkHttpHandler(getActivity(), this, null, "OrderList")
                .execute(preferenceManager.getBaseURL()+AppConstants.BASE_URL5 + AppConstants.ORDER_LIST + MD5Class.generateSignatureString(hashMapKeys, getActivity()) + "&access_token=" + preferenceManager.getauthToken());

    }


    private void _parseAuthCodeResponse(JSONObject jsonObject) {
        if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
            preferenceManager.setauthToken(jsonObject.optString("access_token"));
        }

        if(isListing)
        {
            isListing=false;
            callOrderList();
        }

    }

int cnt_completed=0,cnt_rejected=0,cnt_reviewed=0,cnt_accepted=0,total_orders=0;
    private void _parseOrderListResponse(JSONObject jsonObject)
    {
        total_orders=jsonObject.optJSONArray("data").length();
        if(jsonObject.optJSONArray("data").length()!=0)
        {
            for(int i=0;i<jsonObject.optJSONArray("data").length();i++)
            {
                JSONObject jsonObject1=jsonObject.optJSONArray("data")
                        .optJSONObject(i);
                switch (jsonObject1.optString("status"))
                {
                    case "COMPLETED":
                        cnt_completed++;
                        break;
                    case "ACCEPTED":
                        cnt_accepted++;
                        break;
                    case "REJECTED":
                        cnt_rejected++;
                        break;
                    case "REVIEWED":
                        cnt_reviewed++;
                        break;
                }
            }
            edt_total_orders.setText(total_orders+"");
            edt_rejected.setText(cnt_rejected+"");
            edt_completed.setText(cnt_completed+"");
            edt_accepted.setText(cnt_accepted+"");
            edt_reviewed.setText(cnt_reviewed+"");
            OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(), jsonObject.optJSONArray("data"));
            recycler_view.setAdapter(ordersAdapter);
        }
        else
            return;

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

            case "OrderList":
                preferenceManager.setOrderBadgeCount(0);
                _parseOrderListResponse(jsonObject);
                break;

        }

    }


}
