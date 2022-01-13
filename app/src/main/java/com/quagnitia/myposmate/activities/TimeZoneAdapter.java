package com.quagnitia.myposmate.activities;

import android.annotation.SuppressLint;
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
    PreferencesManager preferenceManager;

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
        preferenceManager = PreferencesManager.getInstance(mContext);
    }

    @Override
    public TimeZoneAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timezone_row, parent, false);

        return new TimeZoneAdapter.MyViewHolder(itemView);
    }
public static boolean isUpdateDetails=false;
    @Override
    public void onBindViewHolder(TimeZoneAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
           //Checked TimeZone
            if (preferenceManager.isTimeZoneChecked() &&
                    !preferenceManager.getTimeZone().equals("") &&
                    preferenceManager.getTimeZone().
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
                        preferenceManager.setisTimeZoneChecked(true);
                        preferenceManager.setTimeZone(holder.tv_timezone.getText().toString());
                        preferenceManager.setTimeZoneId(arrayList.get(position));
                        String s=TimeZone.getTimeZone(arrayList.get(position))
                                .getDisplayName(false, TimeZone.SHORT);
                        if (arrayList.get(position).equals("Australia/Perth")) {
                            preferenceManager.setTimezoneAbrev("AWST");
//                            preferenceManager.setTimezoneAbrev(s);
                        }
                        else
                        if (arrayList.get(position).equals("Australia/Eucla")) {
                            preferenceManager.setTimezoneAbrev("ACWST");
//                            preferenceManager.setTimezoneAbrev(s);
                        }
                        else
                        if (arrayList.get(position).equals("Australia/Darwin")) {
                            preferenceManager.setTimezoneAbrev("ACST");
//                            preferenceManager.setTimezoneAbrev(s);
                        }
                        else
                        if (arrayList.get(position).equals("Australia/Brisbane")) {
                            preferenceManager.setTimezoneAbrev("AEST");
//                            preferenceManager.setTimezoneAbrev(s);
                        }
                        else
                        if (arrayList.get(position).equals("Australia/Adelaide")) {
                            preferenceManager.setTimezoneAbrev("ACDT");
//                            preferenceManager.setTimezoneAbrev(s);
                        }
                        else
                        if (arrayList.get(position).equals("Australia/Sydney")) {
                            preferenceManager.setTimezoneAbrev("AEDT");
//                            preferenceManager.setTimezoneAbrev(s);
                        }
                        else
                        if (arrayList.get(position).equals("Australia/Lord_Howe")) {
                            preferenceManager.setTimezoneAbrev("LHDT");
//                            preferenceManager.setTimezoneAbrev(s);
                        }
                        else
                        if (arrayList.get(position).equals("Pacific/Fiji")) {
                            preferenceManager.setTimezoneAbrev("FJT");
//                            preferenceManager.setTimezoneAbrev(s);
                        }
                        else
                        if (arrayList.get(position).equals("Pacific/Auckland")) {
//                            preferenceManager.setTimezoneAbrev(s);
                            preferenceManager.setTimezoneAbrev("NZST");
                        }
                        else
                        if (arrayList.get(position).equals("Pacific/Chatham")) {
                            preferenceManager.setTimezoneAbrev("CHADT");
//                            preferenceManager.setTimezoneAbrev(s);
                        }


                        notifyDataSetChanged();

                        try {
                            JSONObject jsonObject = new JSONObject();
                            ArrayList tipList=preferenceManager.getTipPercentage("Tip");
                            jsonObject.put("DefaultTip1", tipList.get(0));
                            jsonObject.put("DefaultTip2", tipList.get(1));
                            jsonObject.put("DefaultTip3", tipList.get(2));
                            jsonObject.put("DefaultTip4", tipList.get(3));
                            jsonObject.put("DefaultTip5", tipList.get(4));
                            jsonObject.put("SwitchOnTip", preferenceManager.isSwitchTip());
                            jsonObject.put("DefaultTip1IsEnabled", preferenceManager.isTipDefault1());
                            jsonObject.put("DefaultTip2IsEnabled", preferenceManager.isTipDefault2());
                            jsonObject.put("DefaultTip3IsEnabled", preferenceManager.isTipDefault3());
                            jsonObject.put("DefaultTip4IsEnabled", preferenceManager.isTipDefault4());
                            jsonObject.put("DefaultTip5IsEnabled", preferenceManager.isTipDefault5());
                            jsonObject.put("DefaultTip5IsEnabled", preferenceManager.isTipDefault5());
                            jsonObject.put("CustomTip", preferenceManager.isTipDefaultCustom());
                            jsonObject.put("PaymentModePosition", preferenceManager.getString("DATA"));

                            jsonObject.put("CentrapaySelected", preferenceManager.isCentrapayMerchantQRDisplaySelected());
                            jsonObject.put("CentrapayFeeValue", preferenceManager.getcnv_centrapay());
                            jsonObject.put("CnvCentrapayDisplayAndAdd", preferenceManager.is_cnv_centrapay_display_and_add());
                            jsonObject.put("CnvCentrapayDisplayOnly", preferenceManager.is_cnv_centrapay_display_only());

                            jsonObject.put("PoliSelected", preferenceManager.isPoliSelected());
                            jsonObject.put("PoliFeeValue", preferenceManager.getcnv_poli());
                            jsonObject.put("CnvPoliDisplayAndAdd", preferenceManager.is_cnv_poli_display_and_add());
                            jsonObject.put("CnvPoliDisplayOnly", preferenceManager.is_cnv_poli_display_only());

                            jsonObject.put("accessId",preferenceManager.getuniqueId());
                            jsonObject.put("AlipaySelected", preferenceManager.isAlipaySelected());
                            jsonObject.put("AlipayValue", preferenceManager.getcnv_alipay());
                            jsonObject.put("CnvAlipayDisplayAndAdd", preferenceManager.is_cnv_alipay_display_and_add());
                            jsonObject.put("CnvAlipayDisplayOnly", preferenceManager.is_cnv_alipay_display_only());

                            jsonObject.put("WeChatSelected", preferenceManager.isWechatSelected());
                            jsonObject.put("WeChatValue", preferenceManager.getcnv_wechat());
                            jsonObject.put("CnvWeChatDisplayAndAdd", preferenceManager.is_cnv_wechat_display_and_add());
                            jsonObject.put("CnvWeChatDisplayOnly", preferenceManager.is_cnv_wechat_display_only());

                            jsonObject.put("AlipayScanQR", preferenceManager.isAlipayScan());
                            jsonObject.put("WeChatScanQR", preferenceManager.isWeChatScan());

                            jsonObject.put("MerchantId", preferenceManager.getMerchantId());
                            jsonObject.put("ConfigId", preferenceManager.getConfigId());
                            jsonObject.put("UnionPay", preferenceManager.isUnionPaySelected());
                            jsonObject.put("UnionPayQR", preferenceManager.isUnionPayQrSelected());
                            jsonObject.put("isUnionPayQrCodeDisplaySelected", preferenceManager.isUnionPayQrCodeDisplaySelected());
                            jsonObject.put("UnionPayQrValue", preferenceManager.getcnv_uniqr());
                            jsonObject.put("UplanValue", preferenceManager.getcnv_uplan());
                            jsonObject.put("CnvUnionpayDisplayAndAdd", preferenceManager.is_cnv_uni_display_and_add());
                            jsonObject.put("CnvUnionpayDisplayOnly", preferenceManager.is_cnv_uni_display_only());
                            jsonObject.put("Uplan", preferenceManager.isUplanSelected());
                            jsonObject.put("AlipayWeChatPay", preferenceManager.isaggregated_singleqr());
                            jsonObject.put("AlipayWeChatScanQR", preferenceManager.isAlipayWechatQrSelected());
                            jsonObject.put("PrintReceiptautomatically", preferenceManager.getisPrint());
                            jsonObject.put("ShowReference", preferenceManager.getshowReference());
                            jsonObject.put("ShowPrintQR", preferenceManager.isQR());
                            jsonObject.put("DisplayStaticQR", preferenceManager.isStaticQR());
                            jsonObject.put("isDisplayLoyaltyApps",preferenceManager.isDisplayLoyaltyApps());
                            jsonObject.put("isExternalInputDevice",preferenceManager.isExternalScan());

                            jsonObject.put("isDragDrop", preferenceManager.isDragDrop());

                            jsonObject.put("Home", preferenceManager.isHome());
                            jsonObject.put("ManualEntry", preferenceManager.isManual());
                            jsonObject.put("Back", preferenceManager.isBack());
                            jsonObject.put("Front", preferenceManager.isFront());
                            jsonObject.put("ShowMembershipManual", preferenceManager.isMembershipManual());
                            jsonObject.put("ShowMembershipHome", preferenceManager.isMembershipHome());
                            jsonObject.put("ConvenienceFee", preferenceManager.isConvenienceFeeSelected());
                            jsonObject.put("AlipayWechatvalue", preferenceManager.getcnv_alipay());
                            jsonObject.put("UnionPayvalue", preferenceManager.getcnv_uni());
                            jsonObject.put("EnableBranchName", preferenceManager.getBranchName());
                            jsonObject.put("EnableBranchAddress", preferenceManager.getBranchAddress());
                            jsonObject.put("EnableBranchEmail", preferenceManager.getBranchEmail());
                            jsonObject.put("EnableBranchContactNo", preferenceManager.getBranchPhoneNo());
                            jsonObject.put("EnableBranchGSTNo", preferenceManager.getGSTNo());
                            jsonObject.put("TimeZoneId", preferenceManager.getTimeZoneId());
                            jsonObject.put("TimeZone", preferenceManager.getTimeZone());
                            jsonObject.put("isTimeZoneChecked", preferenceManager.isTimeZoneChecked());

                            jsonObject.put("Membership/Loyality", preferenceManager.isLoyality());
                            jsonObject.put("isTerminalIdentifier", preferenceManager.isTerminalIdentifier());
                            jsonObject.put("isPOSIdentifier", preferenceManager.isPOSIdentifier());
                            jsonObject.put("isLaneIdentifier", preferenceManager.isLaneIdentifier());
                            jsonObject.put("LaneIdentifier", preferenceManager.getLaneIdentifier());
                            jsonObject.put("TerminalIdentifier", preferenceManager.getTerminalIdentifier());
                            jsonObject.put("POSIdentifier", preferenceManager.getPOSIdentifier());

                            jsonObject.put("isUpdated", true);
                            jsonObject.put("CnvUPIQrMPMCloudDAADD",preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_and_add());
                            jsonObject.put("CnvUPIQrMPMCloudDOnly",preferenceManager.cnv_up_upi_qrscan_mpmcloud_display_only());
                            jsonObject.put("CnvUPIQrMPMCloudValue",preferenceManager.getcnv_up_upiqr_mpmcloud_lower());
                            jsonObject.put("CnvUPIQrMPMCloudValueHigher",preferenceManager.getCnv_up_upiqr_mpmcloud_higher());
                            jsonObject.put("CnvUPIQRMPMCloudAmount",preferenceManager.getCnv_up_upiqr_mpmcloud_amount());
                            jsonObject.put("cnv_unimerchantqrdisplay_higher",preferenceManager.get_cnv_unimerchantqrdisplayHigher());
                            jsonObject.put("isMerchantDPARDisplay",preferenceManager.isMerchantDPARDisplay());
                            jsonObject.put("cnv_unimerchantqrdisplay",preferenceManager.get_cnv_unimerchantqrdisplayLower());

                            isUpdateDetails=true;
                            ((TimeZoneActivity) mContext).callUpdateBranchDetails(jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        holder.chkBox.setSelected(false);
                        holder.chkBox.setChecked(false);
                        preferenceManager.setisTimeZoneChecked(false);
                        preferenceManager.setTimeZone("");
                        preferenceManager.setTimeZoneId("");
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

