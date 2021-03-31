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
import com.quagnitia.myposmate.activities.OrderDetailsActivity;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    private Context mContext;
    private JSONArray jsonArray;
    PreferencesManager preferencesManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_id, tv_status, tv_order_number, tv_ready_by, tv_from;
        public LinearLayout parent;

        public MyViewHolder(View view) {
            super(view);
            tv_id = (TextView) view.findViewById(R.id.tv_id);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_order_number = (TextView) view.findViewById(R.id.tv_order_number);
            tv_ready_by = (TextView) view.findViewById(R.id.tv_ready_by);
            tv_from = (TextView) view.findViewById(R.id.tv_from);
            parent = (LinearLayout) view.findViewById(R.id.parent);
        }
    }


    public OrdersAdapter(Context mContext, JSONArray jsonArray) {
        this.mContext = mContext;
        this.jsonArray = jsonArray;
        String s = "[\n" +
                "{\"id\":\"123124\",\n" +
                "\"from\":\"HungryPanda\",\n" +
                "\"order_no\":\"TBLK127\",\n" +
                "\"status\":\"To Review\",\n" +
                "\"ready_by\":\"\"},\n" +
                "\n" +
                "\n" +
                "{\"id\":\"123123\",\n" +
                "\"from\":\"GoMenu\",\n" +
                "\"order_no\":\"12342655\",\n" +
                "\"status\":\"To Review\",\n" +
                "\"ready_by\":\"\"},\n" +
                "\n" +
                "{\"id\":\"123117\",\n" +
                "\"from\":\"GoMenu\",\n" +
                "\"order_no\":\"12343622\",\n" +
                "\"status\":\"Rejected\",\n" +
                "\"ready_by\":\"\"},\n" +
                "\n" +
                "{\"id\":\"123106\",\n" +
                "\"from\":\"HungryPanda\",\n" +
                "\"order_no\":\"STRT132\",\n" +
                "\"status\":\"Accepted\",\n" +
                "\"ready_by\":\"7:45pm\"},\n" +
                "\n" +
                "{\"id\":\"123101\",\n" +
                "\"from\":\"HungryPanda\",\n" +
                "\"order_no\":\"TBLK126\",\n" +
                "\"status\":\"Accepted\",\n" +
                "\"ready_by\":\"7:40pm\"},\n" +
                "\n" +
                "{\"id\":\"122089\",\n" +
                "\"from\":\"GoMenu\",\n" +
                "\"order_no\":\"123456778\",\n" +
                "\"status\":\"Completed\",\n" +
                "\"ready_by\":\"7:20pm\"},\n" +
                "\n" +
                "{\"id\":\"122087\",\n" +
                "\"from\":\"GoMenu\",\n" +
                "\"order_no\":\"123456346\",\n" +
                "\"status\":\"Completed\",\n" +
                "\"ready_by\":\"7:20pm\"},\n" +
                "\n" +
                "\n" +
                "{\"id\":\"122070\",\n" +
                "\"from\":\"GoMenu\",\n" +
                "\"order_no\":\"12345743\",\n" +
                "\"status\":\"Rejected\",\n" +
                "\"ready_by\":\"\"},\n" +
                "\n" +
                "{\"id\":\"122066\",\n" +
                "\"from\":\"GoMenu\",\n" +
                "\"order_no\":\"12356367\",\n" +
                "\"status\":\"Completed\",\n" +
                "\"ready_by\":\"7:00pm\"}\n" +
                "\n" +
                "]";

//        try {
//            this.jsonArray = new JSONArray(s);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        preferencesManager = PreferencesManager.getInstance(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    String roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        return twoDForm.format(d);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JSONObject jsonObject = jsonArray.optJSONObject(position);

        holder.tv_id.setTextColor(Color.parseColor("#000000"));
        holder.tv_order_number.setTextColor(Color.parseColor("#000000"));
        holder.tv_status.setTextColor(Color.parseColor("#000000"));
        holder.tv_ready_by.setTextColor(Color.parseColor("#000000"));
        holder.tv_from.setTextColor(Color.parseColor("#000000"));

        if (jsonObject.optString("status").equalsIgnoreCase("Reviewed")) {
            holder.parent.setBackgroundColor(Color.parseColor("#5797CD"));
            holder.tv_id.setTextColor(Color.parseColor("#ffffff"));
            holder.tv_order_number.setTextColor(Color.parseColor("#ffffff"));
            holder.tv_status.setTextColor(Color.parseColor("#ffffff"));
            holder.tv_ready_by.setTextColor(Color.parseColor("#ffffff"));
            holder.tv_from.setTextColor(Color.parseColor("#ffffff"));
        } else if (jsonObject.optString("status").equalsIgnoreCase("Accepted")) {
            holder.parent.setBackgroundColor(Color.parseColor("#F4C200"));
        } else if (jsonObject.optString("status").equalsIgnoreCase("Completed")) {
            holder.parent.setBackgroundColor(Color.parseColor("#198E14"));
        } else if (jsonObject.optString("status").equalsIgnoreCase("Rejected")) {
            holder.parent.setBackgroundColor(Color.parseColor("#E92F1C"));
        }
        holder.tv_id.setText(jsonObject.optString("myPOSMateOrderID"));
        holder.tv_order_number.setText(jsonObject.optString("orderNumber"));
        holder.tv_status.setText(jsonObject.optString("status"));
//        holder.tv_ready_by.setText(jsonObject.optString("ready_by"));
        holder.tv_from.setText(jsonObject.optString("pickupFrom"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent i = new Intent(mContext, OrderDetailsActivity.class);
                    i.putExtra("hub_id",jsonObject.optString("hubID"));
                    i.putExtra("myPOSMateOrderID",jsonObject.optString("myPOSMateOrderID"));
                    mContext.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
      //  int size = 9;

        return jsonArray.length();
    }

}
