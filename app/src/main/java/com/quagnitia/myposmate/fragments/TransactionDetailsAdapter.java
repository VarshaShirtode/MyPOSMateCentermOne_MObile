package com.quagnitia.myposmate.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by admin on 9/17/2018.
 */

public class TransactionDetailsAdapter extends RecyclerView.Adapter<TransactionDetailsAdapter.MyViewHolder> {

    private Context mContext;
    JSONObject jsonObject;
    PreferencesManager preferencesManager;
    public static String transactionDate = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_details_key, tv_details_value;

        public MyViewHolder(View view) {
            super(view);
            tv_details_key = (TextView) view.findViewById(R.id.tv_details_key);
            tv_details_value = (TextView) view.findViewById(R.id.tv_details_value);
        }
    }


    public TransactionDetailsAdapter(Context mContext, JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        this.mContext = mContext;
        preferencesManager = PreferencesManager.getInstance(mContext);
    }

    @Override
    public TransactionDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_details_row, parent, false);

        return new TransactionDetailsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TransactionDetailsAdapter.MyViewHolder holder, int position) {
        try {
            if (jsonObject.names().getString(position).equals("Date And Time")) {
                if (jsonObject.optString("Transaction Type").equals("VOIDED")) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
                    Date d = df.parse(jsonObject.optString(jsonObject.names().getString(position)).replace("T", " "));
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df1.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));
                    holder.tv_details_key.setText(jsonObject.names().getString(position));
                    holder.tv_details_value.setText(df1.format(d));
                    transactionDate = holder.tv_details_value.getText().toString();
                } else if (jsonObject.optString("Transaction Type").equals("VOID") ||
                        jsonObject.optString("Transaction Type").equals("REFUND") ||
                        jsonObject.optString("Transaction Type").equals("COUPON_VOID")) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date d = df.parse(jsonObject.optString(jsonObject.names().getString(position)).replace("T", " "));
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df1.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));
                    holder.tv_details_key.setText(jsonObject.names().getString(position));
                    holder.tv_details_value.setText(df1.format(d));
                    transactionDate = holder.tv_details_value.getText().toString();
                } else if (jsonObject.optString("Transaction Type").equals("SALE")) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date d = df.parse(jsonObject.optString(jsonObject.names().getString(position)).replace("T", " "));
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df1.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));
                    holder.tv_details_key.setText(jsonObject.names().getString(position));
                    holder.tv_details_value.setText(df1.format(d));
                    transactionDate = holder.tv_details_value.getText().toString();
                } else {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));

                    Date d = df.parse(jsonObject.optString(jsonObject.names().getString(position)).replace("T", " "));
                    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    df1.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));
                    holder.tv_details_key.setText(jsonObject.names().getString(position));
                    holder.tv_details_value.setText(df1.format(d));
                    transactionDate = holder.tv_details_value.getText().toString();


                }


            } else if (jsonObject.names().getString(position).equals("Date (Local)")) {
                holder.tv_details_key.setText(jsonObject.names().getString(position));
                holder.tv_details_value.setText(jsonObject.optString(jsonObject.names().getString(position)));
                transactionDate = holder.tv_details_value.getText().toString();
            } else if (jsonObject.names().getString(position).equals("Refund Pay Time")) {
                holder.tv_details_key.setText(jsonObject.names().getString(position));
                holder.tv_details_value.setText(jsonObject.optString(jsonObject.names().getString(position)));
                transactionDate = holder.tv_details_value.getText().toString();
            } else {

                holder.tv_details_key.setText(jsonObject.names().getString(position));
                holder.tv_details_value.setText(jsonObject.optString(jsonObject.names().getString(position)));

            }

             if (jsonObject.names().getString(position).equals("Payment Amount"))
             {
                 holder.tv_details_value.setText(currencyFormat(jsonObject.optString(jsonObject.names().getString(position))));
             }

            if (jsonObject.names().getString(position).equals("Tip Amount"))
            {
                holder.tv_details_value.setText(currencyFormat(jsonObject.optString(jsonObject.names().getString(position))));
            }

            if (jsonObject.names().getString(position).equals("Receipt Amount"))
            {
                holder.tv_details_value.setText(currencyFormat(jsonObject.optString(jsonObject.names().getString(position))));
            }

            if (jsonObject.names().getString(position).equals("Actual Paid Amount"))
            {
                holder.tv_details_value.setText(currencyFormat(jsonObject.optString(jsonObject.names().getString(position))));
            }

            if (jsonObject.names().getString(position).equals("Amount Refunded"))
            {
                holder.tv_details_value.setText(currencyFormat(jsonObject.optString(jsonObject.names().getString(position))));
            }
            if (jsonObject.names().getString(position).equals("Remaining Amount"))
            {
                holder.tv_details_value.setText(currencyFormat(jsonObject.optString(jsonObject.names().getString(position))));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String currencyFormat(String grandTotal) {
        double number = Double.parseDouble(grandTotal);
        String COUNTRY = "US";
        String LANGUAGE = "en";
        String str = NumberFormat.getCurrencyInstance(new Locale(LANGUAGE, COUNTRY)).format(number);

        return str;
    }
    @Override
    public int getItemCount() {
        return jsonObject.length();
    }
}