package com.quagnitia.myposmate.gridpayment;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quagnitia.myposmate.R;

import java.util.ArrayList;

public class MyHoverAdapter extends RecyclerView.Adapter<MyHoverAdapter.ViewHolder> {
    public static ArrayList<MyObject> mData;
    public ArrayList<MyObject> mDataChanged;
    private Context mCtx;
    int selected_option;
    private static long mLastClickTime = 0;


    public final String TAG = this.getClass().getSimpleName();
    private MyClickListener mMyClickListener;
  //  private ArrayList<MyObject> mData;
    private View.OnDragListener mItemDragListener;

    public MyHoverAdapter(Context mCtx) {

    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final ImageView image;
        private final RelativeLayout root,main_layout,rlTop;
         RelativeLayout rlTop1,rlTop2,rlTop3,rlTop4;
        public MyClickListener mClickListener;
        public TextView textfield,tv_cnv;

        public ViewHolder(View v, View.OnDragListener mItemDragListener) {
            super(v);

            root = (RelativeLayout)v;
           // textfield = (TextView) v.findViewById(R.id.form_fieldlabel);
            tv_cnv = (TextView) v.findViewById(R.id.tv_cnv );
            image = (ImageView) v.findViewById(R.id.form_image);
            main_layout=v.findViewById(R.id.main_layout);
            /*image.setOnLongClickListener(this);
            image.setOnClickListener(this);*/
            main_layout.setOnDragListener(mItemDragListener);
            rlTop=v.findViewById(R.id.rlTop);
            rlTop.setOnLongClickListener(this);
            rlTop.setOnClickListener(this);
            rlTop1=v.findViewById(R.id.rlTop1);
            rlTop2=v.findViewById(R.id.rlTop2);
            rlTop3=v.findViewById(R.id.rlTop3);
            rlTop4=v.findViewById(R.id.rlTop4);
        }


        @Override
        public void onClick(View v) {
            if (mData.get(getAdapterPosition()).isDisplay()) {
                preventDoubleCLick(v);
                mClickListener.onClick(v, getAdapterPosition(), mData);
            }
        }
        long TIME = 1 * 1000;
        private void preventDoubleCLick(View v) {
            v.setEnabled(false);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    v.setEnabled(true);
                }
            }, TIME);
        }


        @Override
        public boolean onLongClick(View v) {
            mClickListener.onLongClick(v, getAdapterPosition(),true);
            return true;
        }
    }

    public MyHoverAdapter(Context ctx, MyClickListener clickListener, ArrayList<MyObject> data, View.OnDragListener itemDragListener,int selected_option) {
        mCtx = ctx;
        mMyClickListener = clickListener;
        mData = data;
        mItemDragListener = itemDragListener;
        this.selected_option=selected_option;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);
        ViewHolder vh = new ViewHolder(v, mItemDragListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mClickListener = mMyClickListener;


        if (mData.get(position).isDisplay())
        {
          //  holder.textfield.setText("" + mData.get(position).getName().replace(" disabled",""));


// Visibility on off according number of icons
            if (mData.get(position).getSelected_option()==5)
            {
                holder.rlTop1.setVisibility(View.GONE);
                holder.rlTop2.setVisibility(View.VISIBLE);
                holder.rlTop3.setVisibility(View.GONE);
                holder.rlTop4.setVisibility(View.GONE);

            }else if (mData.get(position).getSelected_option()==3)
            {
                holder.rlTop1.setVisibility(View.GONE);
                holder.rlTop2.setVisibility(View.GONE);
                holder.rlTop3.setVisibility(View.VISIBLE);
                holder.rlTop4.setVisibility(View.GONE);

            }else if (mData.get(position).getSelected_option()==9)
            {
                holder.rlTop1.setVisibility(View.GONE);
                holder.rlTop2.setVisibility(View.GONE);
                holder.rlTop3.setVisibility(View.GONE);
                holder.rlTop4.setVisibility(View.VISIBLE);

            }else{
                holder.rlTop1.setVisibility(View.VISIBLE);
                holder.rlTop2.setVisibility(View.GONE);
                holder.rlTop3.setVisibility(View.GONE);
                holder.rlTop4.setVisibility(View.GONE);
                holder.image.setImageResource(mData.get(position).getLogo());

            }
            if (mData.get(position).isConv()==true) {
                holder.tv_cnv.setVisibility(View.VISIBLE);
                holder.tv_cnv.setText(mData.get(position).getCnv_amt());
                Log.v("CONVE","in adapter "+mData.get(position).getName()+" "+ mData.get(position).isConv());
            } else{
                holder.tv_cnv.setVisibility(View.GONE);             ;
            }
        }
       /* holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx,"clicked "+mData.get(position).getSelected_option(),Toast.LENGTH_SHORT).show();
            }
        });*/
        holder.main_layout.setTag(position);
       mDataChanged=mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}