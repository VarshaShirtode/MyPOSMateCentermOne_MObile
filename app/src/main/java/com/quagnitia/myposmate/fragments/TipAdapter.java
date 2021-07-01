package com.quagnitia.myposmate.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quagnitia.myposmate.R;

import java.util.ArrayList;

public class TipAdapter extends RecyclerView.Adapter<TipAdapter.ViewHolder> {

    public ArrayList<TipData> enabledTipList;
    private Context context;
    public final String TAG = this.getClass().getSimpleName();
    int selected_option;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtPrice,txtAmt,txtPercent,txtAmt2;
        RelativeLayout main_layout;
        LinearLayout ll_default,ll_defaultCustom;
        public ViewHolder(View v) {
            super(v);
            txtPrice = (TextView) v.findViewById(R.id.txtPrice);
            txtAmt = (TextView) v.findViewById(R.id.txtAmt );
            txtAmt2= (TextView) v.findViewById(R.id.txtAmt2 );
            txtPercent = (TextView) v.findViewById(R.id.txtPercent);
            main_layout= (RelativeLayout) v.findViewById(R.id.main_layout);
            ll_default= (LinearLayout) v.findViewById(R.id.ll_default);
            ll_defaultCustom= (LinearLayout) v.findViewById(R.id.ll_defaultCustom);
        }
/*

        @Override
        public void onClick(View v) {
            if (mData.get(getAdapterPosition()).isDisplay()) {
                mClickListener.onClick(v, getAdapterPosition(), mData);
            }
        }


        @Override
        public boolean onLongClick(View v) {
            mClickListener.onLongClick(v, getAdapterPosition(),true);
            return true;
        }*/
    }

    public TipAdapter(Context ctx, ArrayList<TipData> enabledTipList, int selected_option) {
        context = ctx;
        this.enabledTipList = enabledTipList;
        this.selected_option=selected_option;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_tip, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (enabledTipList.get(position).getAmount().equals("custom"))
        {
            holder.ll_default.setVisibility(View.GONE);
            holder.ll_defaultCustom.setVisibility(View.VISIBLE);
        }else{
            holder.ll_default.setVisibility(View.VISIBLE);
            holder.ll_defaultCustom.setVisibility(View.GONE);
            if (selected_option==5)
            {
                String amt[]=enabledTipList.get(position).getAmount().split("/");
                holder.txtAmt.setText("A: "+amt[0]);
                if (amt.length>1) {
                    holder.txtAmt2.setVisibility(View.VISIBLE);
                    holder.txtAmt2.setText("W: "+amt[1]);
                }
            }else{
                holder.txtAmt.setText(enabledTipList.get(position).getAmount());
            }

            holder.txtPrice.setText(enabledTipList.get(position).getPrice());
            holder.txtPercent.setText(enabledTipList.get(position).getPercent());
        }

        holder.main_layout.setTag(position);

    }

    @Override
    public int getItemCount() {
        return enabledTipList.size();
    }
}