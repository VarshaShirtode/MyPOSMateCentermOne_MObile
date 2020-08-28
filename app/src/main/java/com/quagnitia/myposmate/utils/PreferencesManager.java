package com.quagnitia.myposmate.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.quagnitia.myposmate.activities.TimeZoneActivity;

public class PreferencesManager {

    public static final String SHARED_PREFERENCES_NAME = "MyPOasMatePref";

    /**
     * Instance
     */
    private static PreferencesManager preferencesManager = null;

    /**
     * Shared Preferences
     */
    private static SharedPreferences sharedPreferences;

    /**
     * Preferences variables
     */

    private String terminalId = "terminalId";
    private String terminalIp = "terminalIp";
    private String uniqueId = "uniqueId";
    private String username = "username";
    private String reference = "reference";
    private String password = "password";
    private String isConnected = "isConnected";
    private String isAuthenticated="isAuthenticated";
    private String isAlipaySelected="isAlipaySelected";
    private String isManual="isManual";
    private String isHome="isHome";
    private String isBack="isBack";
    private String isFront="isFront";
    private String isWechatSelected="isWechatSelected";
    private String isMerchantDPARDisplay="isMerchantDPARDisplay";
    private String isUnipaySelected="isUnipaySelected";
    private String isUplanSelected="isUplanSelected";
    private String isAlipayWechatQrSelected="isAlipayWechatQrSelected";
    private String isUnionPayQrSelected="isUnionPayQrSelected";
    private String isUnionPayQrCodeDisplaySelected="isUnionPayQrCodeDisplaySelected";
    private String isUnionPaySelected="isUnionPaySelected";
    private String isVisaSlelected="isVisaSlelected";
    private String isaggregated_singleqr="isaggregated_singleqr";
    private String reference_id="reference_id";
    private String terminal_refund_password="terminal_refund_password";
    private String isDisplayAds="isDisplayAds";
    private String displayAdsTime="displayAdsTime";
    private String increment_id="increment_id";
    private String merchant_name="merchant_name";
    private String MerchantName="MerchantName";
    private String contact_no="contact_no";
    private String contact_email="contact_email";
    private String address="address";
    private String gstno="gstno";
    private String merchant_info="merchant_info";
    private String union_pay_resp="union_pay_resp";
    private String isPrint="isPrint";
    private String BranchName="BranchName";
    private String BranchAddress="BranchAddress";
    private String BranchPhoneNo="BranchPhoneNo";
    private String BranchEmail="BranchEmail";
    private String GSTNo="GSTNo";
    private String showReference="showReference";
    private String triggerReferenceId="triggerReferenceId";
    private String isConvenienceFeeSelected="isConvenienceFeeSelected";
    private String cnv_alipay="cnv_alipay";
    private String cnv_wechat="cnv_wechat";
    private String cnv_alipay_display_and_add="cnv_alipay_display_and_add";
    private String cnv_alipay_display_only="cnv_alipay_display_only";
    private String cnv_uni="cnv_uni";
    private String cnv_uniqr="cnv_uniqr";
    private String cnv_unimerchantqrdisplay="cnv_unimerchantqrdisplay";
    private String cnv_uplan="cnv_uplan";
    private String cnv_up_upiqr_mpmcloud="cnv_up_upiqr_mpmcloud";
    private String cnv_uni_display_and_add="cnv_uni_display_and_add";
    private String cnv_uni_display_only="cnv_uni_display_only";
    private String timezone="timezone";
    private String upay_reference_id="upay_reference_id";
    private String upay_amount="upay_amount";
    private String currency="currency";
    private String isTimeZoneChecked="isTimeZoneChecked";
    private String TimeZone="TimeZone";
    private String TimeZoneId="TimeZoneId";
    private String amountdata="amountdata";
    private String isLoyality="isLoyality";
    private String isMembershipManual="isMembershipManual";
    private String isMembershipHome="isMembershipHome";
    private String isLaneIdentifier="isLaneIdentifier";
    private String isPOSIdentifier="isPOSIdentifier";
    private String isTerminalIdentifier="isTerminalIdentifier";

    private String LaneIdentifier="LaneIdentifier";
    private String POSIdentifier="POSIdentifier";
    private String TerminalIdentifier="TerminalIdentifier";
    private String isQR="isQR";
    private String isStaticQR="isStaticQR";

    private String MerchantId="MerchantId";
    private String ConfigId="ConfigId";
    private String authToken="authToken";
    private String authTokenCloseTrade="authTokenCloseTrade";
    private String isResetTerminal="isResetTerminal";
    private String timezoneabrev="timezoneabrev";


    //added preference fields on 3/3/2020

    private String isAlipay="isAlipay";
    private String isWeChat="isWeChat";
    private String isAlipayScan="isAlipayScan";
    private String isWeChatScan="isWeChatScan";
    private String isUnionPayCard="isUnionPayCard";
    private String isUnionPayQR="isUnionPayQR";
    private String isUnionPayQRScan="isUnionPayQRScan";
    private String isUplan="isUplan";
    private String isStaticAlipay="isStaticAlipay";
    private String isStaticWeChat="isStaticWeChat";
    private String isStaticUnionPay="isStaticUnionPay";
    private String isAlipayDisplayAndAdd="isAlipayDisplayAndAdd";
    private String isAlipayDisplayOnly="isAlipayDisplayOnly";
    private String isWeChatDisplayAndAdd="isWeChatDisplayAndAdd";
    private String isWeChatDisplayOnly="isWeChatDisplayOnly";
    private String isUnionPayCardDisplayAndAdd="isUnionPayCardDisplayAndAdd";
    private String isUnionPayCardDisplayOnly="isUnionPayCardDisplayOnly";
    private String isUnionPayQRDisplayAndAdd="isUnionPayQRDisplayAndAdd";
    private String isUnionPayQRDisplayOnly="isUnionPayQRDisplayOnly";
    private String isUplanDisplayAndAdd="isUplanDisplayAndAdd";
    private String isUplanDisplayOnly="isUplanDisplayOnly";
    private String isStaticAlipayDisplayAndAdd="isStaticAlipayDisplayAndAdd";
    private String isStaticAlipayDisplayOnly="isStaticAlipayDisplayOnly";
    private String isStaticWeChatDisplayAndAdd="isStaticWeChatDisplayAndAdd";
    private String isStaticWeChatDisplayOnly="isStaticWeChatDisplayOnly";
    private String isStaticUnionPayDisplayAndAdd="isStaticUnionPayDisplayAndAdd";
    private String isStaticUnionPayDisplayOnly="isStaticUnionPayDisplayOnly";

    //convience fee for qr code
    private String cnv_unionpayqr_display_and_add="cnv_unionpayqr_display_and_add";
    private String cnv_unionpayqr_display_only="cnv_unionpayqr_display_only";

    //convience fee for uplan
    private String cnv_uplan_display_and_add="cnv_uplan_display_and_add";
    private String cnv_uplan_display_only="cnv_uplan_display_only";

    //convience fee for wechat
    private String cnv_wechat_display_and_add="cnv_wechat_display_and_add";
    private String cnv_wechat_display_only="cnv_wechat_display_only";



    //convience fee for mpm upi qr code scan
    private String cnv_up_upi_qrscan_mpmcloud_display_and_add="cnv_up_upi_qrscan_mpmcloud_display_and_add";
    private String cnv_up_upi_qrscan_mpmcloud_display_only="cnv_up_upi_qrscan_mpmcloud_display_only";


    //already registered flag
    private String isRegistered="isRegistered";



    public boolean isRegistered() {
        return sharedPreferences.getBoolean(isRegistered, false);
    }

    public void setisRegistered(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isRegistered, text);
        editor.apply();

    }




    public boolean isAlipayScan() {
        return sharedPreferences.getBoolean(isAlipayScan, false);
    }

    public void setisAlipayScan(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isAlipayScan, text);
        editor.apply();

    }


    public String getTimezoneAbrev() {
        return sharedPreferences.getString(timezoneabrev, "");
    }

    public void setTimezoneAbrev(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(timezoneabrev, text);
        editor.apply();

    }



    public boolean isWeChatScan() {
        return sharedPreferences.getBoolean(isWeChatScan, false);
    }

    public void setisWeChatScan(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isWeChatScan, text);
        editor.apply();

    }


    public boolean cnv_uplan_display_and_add() {
        return sharedPreferences.getBoolean(cnv_uplan_display_and_add, false);
    }

    public void setcnv_uplan_display_and_add(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cnv_uplan_display_and_add, text);
        editor.apply();

    }

    public boolean cnv_uplan_display_only() {
        return sharedPreferences.getBoolean(cnv_uplan_display_only, false);
    }

    public void setcnv_uplan_display_only(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cnv_uplan_display_only, text);
        editor.apply();

    }




    //MPM UPI QR SCAN DISPLAY ONLY
    public boolean cnv_up_upi_qrscan_mpmcloud_display_only() {
        return sharedPreferences.getBoolean(cnv_up_upi_qrscan_mpmcloud_display_only, false);
    }

    public void setcnv_up_upi_qrscan_mpmcloud_display_only(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cnv_up_upi_qrscan_mpmcloud_display_only, text);
        editor.apply();

    }

    //MPM UPI QR SCAN DISPLAY AND ADD
    public boolean cnv_up_upi_qrscan_mpmcloud_display_and_add() {
        return sharedPreferences.getBoolean(cnv_up_upi_qrscan_mpmcloud_display_and_add, false);
    }

    public void setcnv_up_upi_qrscan_mpmcloud_display_and_add(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cnv_up_upi_qrscan_mpmcloud_display_and_add, text);
        editor.apply();

    }



    public boolean cnv_unionpayqr_display_only() {
        return sharedPreferences.getBoolean(cnv_unionpayqr_display_only, false);
    }

    public void setcnv_unionpayqr_display_only(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cnv_unionpayqr_display_only, text);
        editor.apply();

    }


    public boolean cnv_unionpayqr_display_and_add() {
        return sharedPreferences.getBoolean(cnv_unionpayqr_display_and_add, false);
    }

    public void setcnv_unionpayqr_display_and_add(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cnv_unionpayqr_display_and_add, text);
        editor.apply();

    }


    public boolean isUnionPayQRDisplayAndAdd() {
        return sharedPreferences.getBoolean(isUnionPayQRDisplayAndAdd, false);
    }

    public void setisUnionPayQRDisplayAndAdd(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isUnionPayQRDisplayAndAdd, text);
        editor.apply();

    }


    public boolean isUnionPayQRDisplayOnly() {
        return sharedPreferences.getBoolean(isUnionPayQRDisplayOnly, false);
    }

    public void setisUnionPayQRDisplayOnly(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isUnionPayQRDisplayOnly, text);
        editor.apply();

    }


    public boolean isUplanDisplayAndAdd() {
        return sharedPreferences.getBoolean(isUplanDisplayAndAdd, false);
    }

    public void setisUplanDisplayAndAdd(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isUplanDisplayAndAdd, text);
        editor.apply();

    }


    public boolean isUplanDisplayOnly() {
        return sharedPreferences.getBoolean(isUplanDisplayOnly, false);
    }

    public void setisUplanDisplayOnly(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isUplanDisplayOnly, text);
        editor.apply();

    }








    public boolean isResetTerminal() {
        return sharedPreferences.getBoolean(isResetTerminal, false);
    }

    public void setisResetTerminal(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isResetTerminal, text);
        editor.apply();

    }






    public boolean isQR() {
        return sharedPreferences.getBoolean(isQR, false);
    }

    public void setisQR(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isQR, text);
        editor.apply();

    }


    public boolean isStaticQR() {
        return sharedPreferences.getBoolean(isStaticQR, false);
    }

    public void setisStaticQR(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isStaticQR, text);
        editor.apply();

    }


    public String  getauthTokenCloseTrade() {
        return sharedPreferences.getString(authTokenCloseTrade, "");
    }

    public void setauthTokenCloseTrade(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(authTokenCloseTrade, text);
        editor.apply();

    }

    public String  getauthToken() {
        return sharedPreferences.getString(authToken, "");
    }

    public void setauthToken(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(authToken, text);
        editor.apply();

    }

    public String getMerchantId() {
        return sharedPreferences.getString(MerchantId, "");
    }

    public void setMerchantId(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MerchantId, text);
        editor.apply();
    }

    public String getConfigId() {
        return sharedPreferences.getString(ConfigId, "");
    }

    public void setConfigId(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ConfigId, text);
        editor.apply();
    }

    public String getLaneIdentifier() {
        return sharedPreferences.getString(LaneIdentifier, "");
    }

    public void setLaneIdentifier(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LaneIdentifier, text);
        editor.apply();
    }


    public String getPOSIdentifier() {
        return sharedPreferences.getString(POSIdentifier, "");
    }

    public void setPOSIdentifier(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(POSIdentifier, text);
        editor.apply();
    }


    public String getTerminalIdentifier() {
        return sharedPreferences.getString(TerminalIdentifier, "");
    }

    public void setTerminalIdentifier(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TerminalIdentifier, text);
        editor.apply();
    }







    public boolean isLaneIdentifier() {
        return sharedPreferences.getBoolean(isLaneIdentifier, false);
    }

    public void setisLaneIdentifier(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isLaneIdentifier, text);
        editor.apply();

    }


    public boolean isPOSIdentifier() {
        return sharedPreferences.getBoolean(isPOSIdentifier, false);
    }

    public void setisPOSIdentifier(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isPOSIdentifier, text);
        editor.apply();

    }



    public boolean isTerminalIdentifier() {
        return sharedPreferences.getBoolean(isTerminalIdentifier, false);
    }

    public void setisTerminalIdentifier(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isTerminalIdentifier, text);
        editor.apply();

    }


    public boolean isLoyality() {
        return sharedPreferences.getBoolean(isLoyality, false);
    }

    public void setisLoyality(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isLoyality, text);
        editor.apply();

    }





    public String getamountdata() {
        return sharedPreferences.getString(amountdata, "");
    }

    public void setamountdata(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(amountdata, text);
        editor.apply();
    }


    public String getTimeZone() {
        return sharedPreferences.getString(TimeZone, "");
    }

    public void setTimeZone(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TimeZone, text);
        editor.apply();
    }

    public String getTimeZoneId() {
        return sharedPreferences.getString(TimeZoneId, "");
    }

    public void setTimeZoneId(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TimeZoneId, text);
        editor.apply();
    }


    public String getcurrency() {
        return sharedPreferences.getString(currency, "");
    }

    public void setcurrency(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(currency, text);
        editor.apply();
    }


    public String getupay_amount() {
        return sharedPreferences.getString(upay_amount, "");
    }

    public void setupay_amount(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(upay_amount, text);
        editor.apply();
    }

    public String getupay_reference_id() {
        return sharedPreferences.getString(upay_reference_id, "");
    }

    public void setupay_reference_id(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(upay_reference_id, text);
        editor.apply();
    }



    public String gettimezone() {
        return sharedPreferences.getString(timezone, "");
    }

    public void settimezone(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(timezone, text);
        editor.apply();
    }


    public boolean is_cnv_alipay_display_and_add() {
        return sharedPreferences.getBoolean(cnv_alipay_display_and_add, false);
    }

    public void setcnv_alipay_diaplay_and_add(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cnv_alipay_display_and_add, text);
        editor.apply();

    }


    public boolean is_cnv_alipay_display_only() {
        return sharedPreferences.getBoolean(cnv_alipay_display_only, false);
    }

    public void setcnv_alipay_diaplay_only(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cnv_alipay_display_only, text);
        editor.apply();

    }



    public boolean is_cnv_wechat_display_and_add() {
        return sharedPreferences.getBoolean(cnv_wechat_display_and_add, false);
    }

    public void setcnv_wechat_display_and_add(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cnv_wechat_display_and_add, text);
        editor.apply();

    }


    public boolean isAlipay() {
        return sharedPreferences.getBoolean(isAlipay, false);
    }

    public void setAlipay(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isAlipay, text);
        editor.apply();

    }


    public boolean isWeChat() {
        return sharedPreferences.getBoolean(isWeChat, false);
    }

    public void setWeChat(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isWeChat, text);
        editor.apply();

    }


    public boolean is_cnv_wechat_display_only() {
        return sharedPreferences.getBoolean(cnv_wechat_display_only, false);
    }

    public void setcnv_wechat_display_only(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cnv_wechat_display_only, text);
        editor.apply();

    }







    public boolean is_cnv_uni_display_and_add() {
        return sharedPreferences.getBoolean(cnv_uni_display_and_add, false);
    }

    public void setcnv_uni_display_and_add(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cnv_uni_display_and_add, text);
        editor.apply();

    }

    public boolean is_cnv_uni_display_only() {
        return sharedPreferences.getBoolean(cnv_uni_display_only, false);
    }

    public void setcnv_uni_display_only(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cnv_uni_display_only, text);
        editor.apply();

    }




    public String getcnv_alipay() {
        return sharedPreferences.getString(cnv_alipay, "");
    }

    public void setcnv_alipay(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(cnv_alipay, text);
        editor.apply();

    }
    public String getcnv_wechat() {
        return sharedPreferences.getString(cnv_wechat, "");
    }

    public void setcnv_wechat(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(cnv_wechat, text);
        editor.apply();

    }


    public String getcnv_uplan() {
        return sharedPreferences.getString(cnv_uplan, "");
    }

    public void setcnv_uplan(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(cnv_uplan, text);
        editor.apply();

    }



    public String getcnv_uniqr() {
        return sharedPreferences.getString(cnv_uniqr, "");
    }

    public void setcnv_uniqr(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(cnv_uniqr, text);
        editor.apply();

    }


    public String get_cnv_unimerchantqrdisplay() {
        return sharedPreferences.getString(cnv_unimerchantqrdisplay, "");
    }

    public void set_cnv_unimerchantqrdisplay(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(cnv_unimerchantqrdisplay, text);
        editor.apply();

    }


    //MPM CLOUD SCAN UPI QR PREF
    public String getcnv_up_upiqr_mpmcloud() {
        return sharedPreferences.getString(cnv_up_upiqr_mpmcloud, "");
    }

    public void setcnv_up_upiqr_mpmcloud(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(cnv_up_upiqr_mpmcloud, text);
        editor.apply();

    }


    public String getcnv_uni() {
        return sharedPreferences.getString(cnv_uni, "");
    }

    public void setcnv_uni(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(cnv_uni, text);
        editor.apply();

    }



    public boolean isTimeZoneChecked() {
        return sharedPreferences.getBoolean(isTimeZoneChecked, false);
    }

    public void setisTimeZoneChecked(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isTimeZoneChecked, text);
        editor.apply();

    }




    public boolean isConvenienceFeeSelected() {
        return sharedPreferences.getBoolean(isConvenienceFeeSelected, false);
    }

    public void setisConvenienceFeeSelected(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isConvenienceFeeSelected, text);
        editor.apply();

    }






    public String gettriggerReferenceId() {
        return sharedPreferences.getString(triggerReferenceId, "");
    }

    public void settriggerReferenceId(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(triggerReferenceId, text);
        editor.apply();

    }

    public Boolean isMembershipHome() {
        return sharedPreferences.getBoolean(isMembershipHome, false);
    }

    public void setisMembershipHome(Boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isMembershipHome, text);
        editor.apply();

    }

    public Boolean isMembershipManual() {
        return sharedPreferences.getBoolean(isMembershipManual, false);
    }

    public void setisMembershipManual(Boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isMembershipManual, text);
        editor.apply();

    }











    public String getshowReference() {
        return sharedPreferences.getString(showReference, "");
    }

    public void setshowReference(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(showReference, text);
        editor.apply();

    }


    public String getGSTNo() {
        return sharedPreferences.getString(GSTNo, "");
    }

    public void setGSTNo(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(GSTNo, text);
        editor.apply();

    }


    public String getBranchEmail() {
        return sharedPreferences.getString(BranchEmail, "");
    }

    public void setBranchEmail(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BranchEmail, text);
        editor.apply();

    }


    public String getBranchPhoneNo() {
        return sharedPreferences.getString(BranchPhoneNo, "");
    }

    public void setBranchPhoneNo(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BranchPhoneNo, text);
        editor.apply();

    }

    public String getBranchAddress() {
        return sharedPreferences.getString(BranchAddress, "");
    }

    public void setBranchAddress(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BranchAddress, text);
        editor.apply();

    }




    public String getBranchName() {
        return sharedPreferences.getString(BranchName, "");
    }

    public void setBranchName(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BranchName, text);
        editor.apply();

    }







    public String getisPrint() {
        return sharedPreferences.getString(isPrint, "false");
    }

    public void setisPrint(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(isPrint, text);
        editor.apply();

    }

    public String getunion_pay_resp() {
        return sharedPreferences.getString(union_pay_resp, "");
    }

    public void setunion_pay_resp(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(union_pay_resp, text);
        editor.apply();

    }


    public String getmerchant_info() {
        return sharedPreferences.getString(merchant_info, "");
    }

    public void setmerchant_info(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(merchant_info, text);
        editor.apply();

    }


    public String getgstno() {
        return sharedPreferences.getString(gstno, "");
    }

    public void setgstno(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(gstno, text);
        editor.apply();

    }


    public String getaddress() {
        return sharedPreferences.getString(address, "");
    }

    public void setaddress(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(address, text);
        editor.apply();

    }


    public String getcontact_email() {
        return sharedPreferences.getString(contact_email, "");
    }

    public void setcontact_email(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(contact_email, text);
        editor.apply();

    }


    public String getcontact_no() {
        return sharedPreferences.getString(contact_no, "");
    }

    public void setcontact_no(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(contact_no, text);
        editor.apply();

    }

    public String getmerchant_name() {
        return sharedPreferences.getString(merchant_name, "");
    }

    public void setmerchant_name(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(merchant_name, text);
        editor.apply();

    }

    public String getMerchantName() {
        return sharedPreferences.getString(MerchantName, "");
    }

    public void setMerchantName(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MerchantName, text);
        editor.apply();

    }

















    public String getReference() {
        return sharedPreferences.getString(reference, "");
    }

    public void setReference(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(reference, text);
        editor.apply();

    }


    public String getincrement_id() {
        return sharedPreferences.getString(increment_id, "");
    }

    public void setincrement_id(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(increment_id, text);
        editor.apply();

    }


    public String getdisplayAdsTime() {
        return sharedPreferences.getString(displayAdsTime, "");
    }

    public void setdisplayAdsTime(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(displayAdsTime, text);
        editor.apply();

    }


    public boolean isDisplayAds() {
        return sharedPreferences.getBoolean(isDisplayAds, false);
    }

    public void setisDisplayAds(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isDisplayAds, text);
        editor.apply();

    }




    public String getterminal_refund_password() {
        return sharedPreferences.getString(terminal_refund_password, "");
    }

    public void setterminal_refund_password(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(terminal_refund_password, text);
        editor.apply();

    }




    public String getreference_id() {
        return sharedPreferences.getString(reference_id, "");
    }

    public void setreference_id(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(reference_id, text);
        editor.apply();

    }



    private PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static PreferencesManager getInstance(Context context) {

        if (preferencesManager == null) {
            Log.v("Preference status", "new object of " + context);
            preferencesManager = new PreferencesManager(context);
        } else {
            Log.v("Preference status", "old object of " + context);
        }

        return preferencesManager;
    }


    public boolean isBack() {
        return sharedPreferences.getBoolean(isBack, false);
    }

    public void setIsBack(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isBack, text);
        editor.apply();

    }
    public boolean isFront() {
        return sharedPreferences.getBoolean(isFront, false);
    }

    public void setIsFront(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isFront, text);
        editor.apply();

    }








    public boolean isHome() {
        return sharedPreferences.getBoolean(isHome, false);
    }

    public void setIsHome(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isHome, text);
        editor.apply();

    }
    public boolean isManual() {
        return sharedPreferences.getBoolean(isManual, false);
    }

    public void setIsManual(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isManual, text);
        editor.apply();

    }

    public boolean isAlipaySelected() {
        return sharedPreferences.getBoolean(isAlipaySelected, false);
    }

    public void setisAlipaySelected(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isAlipaySelected, text);
        editor.apply();

    }

    public boolean isWechatSelected() {
        return sharedPreferences.getBoolean(isWechatSelected, false);
    }

    public void setisWechatSelected(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isWechatSelected, text);
        editor.apply();

    }
    public boolean isMerchantDPARDisplay() {
        return sharedPreferences.getBoolean(isMerchantDPARDisplay, false);
    }

    public void setisMerchantDPARDisplay(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isMerchantDPARDisplay, text);
        editor.apply();

    }

    public boolean isUnionPayQrSelected() {
        return sharedPreferences.getBoolean(isUnionPayQrSelected, false);
    }

    public void setUnionPayQrSelected(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isUnionPayQrSelected, text);
        editor.apply();

    }


    public boolean isUnionPayQrCodeDisplaySelected() {
        return sharedPreferences.getBoolean(isUnionPayQrCodeDisplaySelected, false);
    }

    public void setisUnionPayQrCodeDisplaySelected(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isUnionPayQrCodeDisplaySelected, text);
        editor.apply();

    }

    public boolean isAlipayWechatQrSelected() {
        return sharedPreferences.getBoolean(isAlipayWechatQrSelected, false);
    }

    public void setAlipayWechatQrSelected(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isAlipayWechatQrSelected, text);
        editor.apply();

    }


    public boolean isUplanSelected() {
        return sharedPreferences.getBoolean(isUplanSelected, false);
    }

    public void setisUplanSelected(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isUplanSelected, text);
        editor.apply();

    }

    public boolean isUnionPaySelected() {
        return sharedPreferences.getBoolean(isUnionPaySelected, false);
    }

    public void setisUnionPaySelected(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isUnionPaySelected, text);
        editor.apply();

    }




    public boolean isUnipaySelected() {
        return sharedPreferences.getBoolean(isUnipaySelected, false);
    }

    public void setisUnipaySelected(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isUnipaySelected, text);
        editor.apply();

    }



    public boolean isVisaSlelected() {
        return sharedPreferences.getBoolean(isVisaSlelected, false);
    }

    public void setisVisaSlelected(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isVisaSlelected, text);
        editor.apply();

    }


    public boolean isaggregated_singleqr() {
        return sharedPreferences.getBoolean(isaggregated_singleqr, false);
    }

    public void setaggregated_singleqr(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isaggregated_singleqr, text);
        editor.apply();

    }


    public boolean isConnected() {
        return sharedPreferences.getBoolean(isConnected, false);
    }

    public void setIsConnected(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isConnected, text);
        editor.apply();

    }
    public boolean isAuthenticated() {
        return sharedPreferences.getBoolean(isAuthenticated, false);
    }

    public void setIsAuthenticated(boolean text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(isAuthenticated, text);
        editor.apply();

    }

    public String getterminalId() {
        return sharedPreferences.getString(terminalId, "");
    }

    public void setterminalId(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(terminalId, text);
        editor.apply();

    }

    public String getterminalIp() {
        return sharedPreferences.getString(terminalIp, "");
    }

    public void setterminalIp(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(terminalIp, text);
        editor.apply();

    }

    public String getuniqueId() {
        return sharedPreferences.getString(uniqueId, "");
    }

    public void setuniqueId(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(uniqueId, text);
        editor.apply();

    }

    public String getUsername() {
        return sharedPreferences.getString(username, "");
    }

    public void setUsername(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(username, text);
        editor.apply();

    }

    public String getPassword() {
        return sharedPreferences.getString(password, "");
    }

    public void setPassword(String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(password, text);
        editor.apply();

    }
    public void clearPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ACCESS_TOKEN", "");
        editor.putString("ACCESS_TOKEN_SECRET", "");
        editor.putString("terminalId", "");
        editor.putString("terminalIp", "");
        editor.putString("uniqueId", "");
        editor.putString("username", "");
        editor.putString("reference", "");
        editor.putString("password", "");
        editor.putBoolean("isConnected", false);
        editor.putBoolean("isAuthenticated", false);
        editor.putBoolean("isAlipaySelected", false);
        editor.putBoolean("isManual", false);
        editor.putBoolean("isHome", false);
        editor.putBoolean("isBack", false);
        editor.putBoolean("isFront", false);
        editor.putBoolean("isWechatSelected", false);
        editor.putBoolean("isUnipaySelected", false);
        editor.putBoolean("isAlipayWechatQrSelected",false);
        editor.putBoolean("isVisaSlelected", false);
        editor.putBoolean("isaggregated_singleqr", false);
        editor.putString("reference_id", "");
        editor.putString("terminal_refund_password", "");
        editor.putBoolean("isDisplayAds", false);
        editor.putString("displayAdsTime", "");
        editor.putString("increment_id", "");
        editor.putString("merchant_name", "");
        editor.putString("contact_no", "");
        editor.putString("contact_email", "");
        editor.putString("address", "");
        editor.putString("gstno", "");
        editor.putString("merchant_info", "");
        editor.putString("union_pay_resp", "");
        editor.putString("isPrint", "false");
        editor.putString("BranchName", "");
        editor.putString("BranchAddress", "");
        editor.putString("BranchPhoneNo", "");
        editor.putString("BranchEmail", "");
        editor.putString("GSTNo", "");
        editor.putString("showReference", "");
        editor.putString("triggerReferenceId", "");
        editor.putBoolean("isConvenienceFeeSelected", false);
        editor.putString("cnv_alipay", "");
        editor.putString("cnv_uni", "");
        editor.putString("timezone", "");



        editor.clear();
        editor.apply();
    }



}
