package com.quagnitia.myposmate.activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by admin on 9/17/2018.
 */

public class TimeZoneAdapter extends RecyclerView.Adapter<TimeZoneAdapter.MyViewHolder> {

    private Context mContext;
    JSONObject jsonObject;
    ArrayList<String> arrayList;
    PreferencesManager preferencesManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_timezone;
        private CheckBox chkBox;

        public MyViewHolder(View view) {
            super(view);
            tv_timezone = (TextView) view.findViewById(R.id.tv_timezone);
            chkBox = (CheckBox) view.findViewById(R.id.chk_timezone);
        }
    }


    public TimeZoneAdapter(Context mContext, ArrayList<String> arrayList) {
        this.arrayList = arrayList;
        this.mContext = mContext;
        preferencesManager = PreferencesManager.getInstance(mContext);
    }

    @Override
    public TimeZoneAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timezone_row, parent, false);

        return new TimeZoneAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TimeZoneAdapter.MyViewHolder holder, int position) {
        try {
            TimeZone tz = TimeZone.getTimeZone(arrayList.get(position));
            String s = "";
            if (arrayList.get(position).equals("Australia/Perth")) {
                s = "Perth (AWST UTC+8)";
            }
            if (arrayList.get(position).equals("Australia/Eucla")) {
                s = "Eucla (ACWST UTC+8:45)";
            }
            if (arrayList.get(position).equals("Australia/Darwin")) {
                s = "Darwin (ACST UTC+9:30)";
            }
            if (arrayList.get(position).equals("Australia/Brisbane")) {
                s = "Brisbane (AEST UTC+10)";
            }
            if (arrayList.get(position).equals("Australia/Adelaide")) {
                s = "Adelaide (ACDT UTC+10:30)";
            }
            if (arrayList.get(position).equals("Australia/Sydney")) {
                s = "Sydney (AEDT UTC+11)";
            }
            if (arrayList.get(position).equals("Australia/Lord_Howe")) {
                s = "Lord Howe Island (LHDT UTC+11)";
            }
            if (arrayList.get(position).equals("Pacific/Fiji")) {
                s = "Suva (FJT UTC+12)";
            }
            if (arrayList.get(position).equals("Pacific/Auckland")) {
                s = "Auckland (NZDT UTC+13)";
            }
            if (arrayList.get(position).equals("Pacific/Chatham")) {
                s = "Chatham Islands (CHADT UTC+13:45)";
            }


            holder.tv_timezone.setText(tz.getDisplayName() + " (" + arrayList.get(position) + ")");
            if (preferencesManager.isTimeZoneChecked() &&
                    !preferencesManager.getTimeZone().equals("") &&
                    preferencesManager.getTimeZone().
                            equals(holder.tv_timezone.getText().toString())) {
                holder.chkBox.setSelected(true);
                holder.chkBox.setChecked(true);
            } else {
                holder.chkBox.setSelected(false);
                holder.chkBox.setChecked(false);
            }
            holder.chkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.chkBox.isChecked()) {
                        holder.chkBox.setSelected(true);
                        holder.chkBox.setChecked(true);
                        preferencesManager.setisTimeZoneChecked(true);
                        preferencesManager.setTimeZone(holder.tv_timezone.getText().toString());
                        preferencesManager.setTimeZoneId(arrayList.get(position));
                        if (arrayList.get(position).equals("Australia/Perth")) {
                            preferencesManager.setTimezoneAbrev("AWST");
                        }
                        else
                        if (arrayList.get(position).equals("Australia/Eucla")) {
                            preferencesManager.setTimezoneAbrev("ACWST");
                        }
                        else
                        if (arrayList.get(position).equals("Australia/Darwin")) {
                            preferencesManager.setTimezoneAbrev("ACST");
                        }
                        else
                        if (arrayList.get(position).equals("Australia/Brisbane")) {
                            preferencesManager.setTimezoneAbrev("AEST");
                        }
                        else
                        if (arrayList.get(position).equals("Australia/Adelaide")) {
                            preferencesManager.setTimezoneAbrev("ACDT");
                        }
                        else
                        if (arrayList.get(position).equals("Australia/Sydney")) {
                            preferencesManager.setTimezoneAbrev("AEDT");
                        }
                        else
                        if (arrayList.get(position).equals("Australia/Lord_Howe")) {
                            preferencesManager.setTimezoneAbrev("LHDT");
                        }
                        else
                        if (arrayList.get(position).equals("Pacific/Fiji")) {
                            preferencesManager.setTimezoneAbrev("FJT");
                        }
                        else
                        if (arrayList.get(position).equals("Pacific/Auckland")) {
                            preferencesManager.setTimezoneAbrev("NZDT");
                        }
                        else
                        if (arrayList.get(position).equals("Pacific/Chatham")) {
                            preferencesManager.setTimezoneAbrev("CHADT");
                        }


                        notifyDataSetChanged();

                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("AlipaySelected", preferencesManager.isAlipaySelected());
                            jsonObject.put("AlipayValue", preferencesManager.getcnv_alipay());
                            jsonObject.put("CnvAlipayDisplayAndAdd", preferencesManager.is_cnv_alipay_display_and_add());
                            jsonObject.put("CnvAlipayDisplayOnly", preferencesManager.is_cnv_alipay_display_only());

                            jsonObject.put("WeChatSelected", preferencesManager.isWechatSelected());
                            jsonObject.put("WeChatValue", preferencesManager.getcnv_wechat());
                            jsonObject.put("CnvWeChatDisplayAndAdd", preferencesManager.is_cnv_wechat_display_and_add());
                            jsonObject.put("CnvWeChatDisplayOnly", preferencesManager.is_cnv_wechat_display_only());

                            jsonObject.put("AlipayScanQR", preferencesManager.isAlipayScan());
                            jsonObject.put("WeChatScanQR", preferencesManager.isWeChatScan());

                            jsonObject.put("MerchantId", preferencesManager.getMerchantId());
                            jsonObject.put("ConfigId", preferencesManager.getConfigId());
                            jsonObject.put("UnionPay", preferencesManager.isUnionPaySelected());
                            jsonObject.put("UnionPayQR", preferencesManager.isUnionPayQrSelected());
                            jsonObject.put("isUnionPayQrCodeDisplaySelected", preferencesManager.isUnionPayQrCodeDisplaySelected());
                            jsonObject.put("UnionPayQrValue", preferencesManager.getcnv_uniqr());
                            jsonObject.put("UplanValue", preferencesManager.getcnv_uplan());
                            jsonObject.put("CnvUnionpayDisplayAndAdd", preferencesManager.is_cnv_uni_display_and_add());
                            jsonObject.put("CnvUnionpayDisplayOnly", preferencesManager.is_cnv_uni_display_only());
                            jsonObject.put("Uplan", preferencesManager.isUplanSelected());
                            jsonObject.put("AlipayWeChatPay", preferencesManager.isaggregated_singleqr());
                            jsonObject.put("AlipayWeChatScanQR", preferencesManager.isAlipayWechatQrSelected());
                            jsonObject.put("PrintReceiptautomatically", preferencesManager.getisPrint());
                            jsonObject.put("ShowReference", preferencesManager.getshowReference());
                            jsonObject.put("ShowPrintQR", preferencesManager.isQR());
                            jsonObject.put("DisplayStaticQR", preferencesManager.isStaticQR());
                            jsonObject.put("Home", preferencesManager.isHome());
                            jsonObject.put("ManualEntry", preferencesManager.isManual());
                            jsonObject.put("Back", preferencesManager.isBack());
                            jsonObject.put("Front", preferencesManager.isFront());
                            jsonObject.put("ShowMembershipManual", preferencesManager.isMembershipManual());
                            jsonObject.put("ShowMembershipHome", preferencesManager.isMembershipHome());
                            jsonObject.put("ConvenienceFee", preferencesManager.isConvenienceFeeSelected());
                            jsonObject.put("AlipayWechatvalue", preferencesManager.getcnv_alipay());
                            jsonObject.put("UnionPayvalue", preferencesManager.getcnv_uni());
                            jsonObject.put("EnableBranchName", preferencesManager.getBranchName());
                            jsonObject.put("EnableBranchAddress", preferencesManager.getBranchAddress());
                            jsonObject.put("EnableBranchEmail", preferencesManager.getBranchEmail());
                            jsonObject.put("EnableBranchContactNo", preferencesManager.getBranchPhoneNo());
                            jsonObject.put("EnableBranchGSTNo", preferencesManager.getGSTNo());
                            jsonObject.put("TimeZoneId", preferencesManager.getTimeZoneId());
                            jsonObject.put("TimeZone", preferencesManager.getTimeZone());
                            jsonObject.put("isTimeZoneChecked", preferencesManager.isTimeZoneChecked());

                            jsonObject.put("Membership/Loyality", preferencesManager.isLoyality());
                            jsonObject.put("isTerminalIdentifier", preferencesManager.isTerminalIdentifier());
                            jsonObject.put("isPOSIdentifier", preferencesManager.isPOSIdentifier());
                            jsonObject.put("isLaneIdentifier", preferencesManager.isLaneIdentifier());
                            jsonObject.put("LaneIdentifier", preferencesManager.getLaneIdentifier());
                            jsonObject.put("TerminalIdentifier", preferencesManager.getTerminalIdentifier());
                            jsonObject.put("POSIdentifier", preferencesManager.getPOSIdentifier());

                            jsonObject.put("isUpdated", true);

                            ((TimeZoneActivity) mContext).callUpdateBranchDetails(jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        holder.chkBox.setSelected(false);
                        holder.chkBox.setChecked(false);
                        preferencesManager.setisTimeZoneChecked(false);
                        preferencesManager.setTimeZone("");
                        preferencesManager.setTimeZoneId("");
                        notifyDataSetChanged();

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void filterList(ArrayList<String> filterdNames) {
        this.arrayList = filterdNames;
        notifyDataSetChanged();
    }
}

