package com.quagnitia.myposmate.activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.MyViewHolder> {

    private Context mContext;
    private JSONArray jsonArray;
    PreferencesManager preferencesManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_id, tv_item, tv_price, tv_qty, tv_add_on;
        public LinearLayout parent;

        public MyViewHolder(View view) {
            super(view);
            tv_id = view.findViewById(R.id.tv_id);
            tv_item = view.findViewById(R.id.tv_item);
            tv_price = view.findViewById(R.id.tv_price);
            tv_qty = view.findViewById(R.id.tv_qty);
            tv_add_on = view.findViewById(R.id.tv_add_on);
        }
    }


    public OrderDetailsAdapter(Context mContext, JSONArray jsonArray) {
        this.mContext = mContext;
        this.jsonArray = jsonArray;
        String s = "[\n" +
                "{\"id\":\"(45)\",\n" +
                "\"item\":\"Mushroom Raghu\",\n" +
                "\"qty\":\"1\",\n" +
                "\"price\":\"$21.50\"\n" +
                "},\n" +
                "\n" +
                "\n" +
                "{\"id\":\"(23)\",\n" +
                "\"item\":\"Lo-Lo cheese dumplings in white sauce\",\n" +
                "\"qty\":\"2\",\n" +
                "\"price\":\"$9.00\"\n" +
                "},\n" +
                "\n" +
                "{\"id\":\"(36)\",\n" +
                "\"item\":\"Chicken & Mango Tango\",\n" +
                "\"qty\":\"1\",\n" +
                "\"price\":\"$24.50\"\n" +
                "},\n" +
                "\n" +
                "{\"id\":\"(48)\",\n" +
                "\"item\":\"Yellow & brown coconut rice with raisins and almonds\",\n" +
                "\"qty\":\"1\",\n" +
                "\"price\":\"$6.00\"\n" +
                "}\n" +
                "\n" +

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
                .inflate(R.layout.order_details_row, parent, false);

        return new MyViewHolder(itemView);
    }

    String roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        return twoDForm.format(d);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JSONObject jsonObject = jsonArray.optJSONObject(position);
//        "itemId": 952,
//                "menuID": null,
//                "quantity": "1",
//                "item": "item1",
//                "itemNotes": null,
//                "price": "100"
        holder.tv_id.setText("("+jsonObject.optString("itemId")+")");
        holder.tv_item.setText(jsonObject.optString("item"));
        holder.tv_price.setText("$"+jsonObject.optString("price"));
        holder.tv_qty.setText(jsonObject.optString("quantity"));
        holder.tv_add_on.setVisibility(View.GONE);
//        if(position==0)
//        {
//            holder.tv_add_on.setText("Extra creme, Chill level 3, No pickle");
//            holder.tv_add_on.setVisibility(View.VISIBLE);
//        }
//
//        if(position==2)
//        {
//            holder.tv_add_on.setText("Chili level 5");
//            holder.tv_add_on.setVisibility(View.VISIBLE);
//        }
//
//        if(position==3)
//        {
//            holder.tv_add_on.setText("No almonds");
//            holder.tv_add_on.setVisibility(View.VISIBLE);
//        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    @Override
    public int getItemCount() {
        int size = 4;

        return jsonArray.length();
    }

}

