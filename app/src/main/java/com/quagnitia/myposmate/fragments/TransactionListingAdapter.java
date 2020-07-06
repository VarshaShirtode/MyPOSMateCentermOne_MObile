package com.quagnitia.myposmate.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by admin on 9/17/2018.
 */

public class TransactionListingAdapter extends RecyclerView.Adapter<TransactionListingAdapter.MyViewHolder> {

    private Context mContext;
    private JSONArray jsonArray;
PreferencesManager preferencesManager;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_reference_id, tv_date_time, tv_amount, tv_type, tv_result, tv_scheme;
public LinearLayout parent;
        public MyViewHolder(View view) {
            super(view);
            tv_reference_id = view.findViewById(R.id.tv_reference_id);
            tv_date_time =  view.findViewById(R.id.tv_date_time);
            tv_amount =  view.findViewById(R.id.tv_amount);
            tv_type = view.findViewById(R.id.tv_type);
            tv_result =  view.findViewById(R.id.tv_result);
            tv_scheme =  view.findViewById(R.id.tv_scheme);
            parent =  view.findViewById(R.id.parent);
        }
    }


    public TransactionListingAdapter(Context mContext,JSONArray jsonArray) {
        this.mContext = mContext;
        this.jsonArray=jsonArray;
        preferencesManager=PreferencesManager.getInstance(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_listing_row, parent, false);

        return new MyViewHolder(itemView);
    }
    String roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        return twoDForm.format(d);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JSONObject jsonObject=jsonArray.optJSONObject(position);
        if(jsonObject.optString("paymentStatus").equals("SUCCESS"))
        {
            holder.parent.setBackgroundColor(Color.parseColor("#82e0aa"));
        }
        else
        {
            holder.parent.setBackgroundColor(Color.parseColor("#EEEEEE"));
        }
        holder.tv_reference_id.setText(jsonObject.optString("referenceId"));
        try
        {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d=df.parse(jsonObject.optString("createDate"));

            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df1.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));
            holder.tv_date_time.setText(df1.format(d).replace("T"," "));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        holder.tv_amount.setText(roundTwoDecimals(Float.valueOf(jsonObject.optString("grandTotal"))));
        holder.tv_type.setText(jsonObject.optString("type"));
        if(jsonObject.optString("type").equals("REFUND"))
        {
            holder.tv_amount.setText(roundTwoDecimals(Float.valueOf(jsonObject.optString("refundFee"))));
        }
        else
        {
            holder.tv_amount.setText(roundTwoDecimals(Float.valueOf(jsonObject.optString("grandTotal"))));
        }

        holder.tv_scheme.setText(jsonObject.optString("channel"));
        if(jsonObject.optString("paymentStatus").equals("CLOSED")||
                jsonObject.optString("paymentStatus").equals("REFUND")
        )
        {
            if(jsonObject.optString("channel").equals("UNION_PAY"))
            {
                holder.tv_result.setText(jsonObject.optString("paymentStatus"));//("VOIDED");
            }
            else
            {
                holder.tv_result.setText(jsonObject.optString("paymentStatus"));
            }
        }
        else
        {
            holder.tv_result.setText(jsonObject.optString("paymentStatus"));
        }


        if(jsonObject.optString("type").equals("REFUND"))
        {
            holder.tv_result.setText(jsonObject.optString("refundStatus"));
        }
        else
        {
            holder.tv_result.setText(jsonObject.optString("paymentStatus"));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(jsonObject.optString("channel").equals("UNION_PAY") && jsonObject.optString("paymentStatus").equals("REQUEST_RECEIVED"))
                {
                    Toast.makeText(mContext, "Transaction incomplete. Details not available.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent i = new Intent(mContext, TransactionDetailsActivity.class);
                    i.putExtra("reference_id",jsonObject.optString("referenceId"));
                   // i.putExtra("increment_id",jsonObject.optString("increment_id"));
                    mContext.startActivity(i);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        int size=0;
        if(jsonArray!=null && jsonArray.length()>0)
        {
            size=jsonArray.length();
        }
        else
        {
            size=0;
        }
        return size;
    }
}