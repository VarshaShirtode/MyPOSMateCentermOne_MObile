package com.quagnitia.myposmate.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import java.util.TreeMap;

public class OrderFragment extends Fragment implements View.OnClickListener, OnTaskCompleted {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    TreeMap<String, String> hashMapKeys;
    PreferencesManager preferencesManager;
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
        preferencesManager = PreferencesManager.getInstance(getActivity());
        hashMapKeys = new TreeMap<>();
        initUI();
        initListener();
        return view;
    }


    public void initUI() {
        recycler_view = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(), null);
        recycler_view.setAdapter(ordersAdapter);


    }

    public void initListener() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {

    }
}
