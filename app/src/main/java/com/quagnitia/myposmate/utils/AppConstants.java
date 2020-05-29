package com.quagnitia.myposmate.utils;


public class AppConstants {


    //Test
//    public static String BASE_URL3 = "http://test.myposmate.com/api/v3/";
//    public static String BASE_URL2 = "http://test.myposmate.com/api/v3/terminal/";
//    public static String V2_AUTH = "http://test.myposmate.com/oauth/token";
//    public static String serverIp = "myposmate.com";

    //Live
//    public static String BASE_URL3 = "https://myposmate.com/api/v3/";
//    public static String BASE_URL2 = "https://myposmate.com/api/v3/terminal/";
//    public static String V2_AUTH = "https://myposmate.com/oauth/token";
//    public static String serverIp = "myposmate.com";



    //Live
    public static String BASE_URL3 = "https://one.myposmate.com/api/v1/";
    public static String BASE_URL2 = "https://one.myposmate.com/api/v1/terminal/";
    public static String V2_AUTH = "https://one.myposmate.com/oauth/token";
    public static String serverIp = "one.myposmate.com";
//one.myposmate.com commit


    public static String CLIENT_ID="b7ae46e37ba373dce7bd";
    public static String CLIENT_SECRET="5ed7ed3cc894247a60d7ff75382c9";

    public static String V2_GET_TRANSACTION_DETAILS = "getTransactionDetails";
    public static String V2_CANCEL_TRANSACTION = "cancelTransaction";
    public static String V2_REFUND = "refund";
    public static String V2_SETTLEMENT_REPORT = "settlementReport";
    public static String V2_SETTLE = "settle";
    public static String V2_GET_RECENT_TRANSACTIONS = "getRecentTransactions";
    public static String V2_GET_CHANNEL_SUMMARY = "getChannelSummary";
    public static String V2_PAYNOW = "payNow";
    public static String V2_VALIDATE_TERMINAL = "validateTerminal";
    public static String V2_PAYUNIONPAY = "payUnionPay";
    public static String V2_UPDATE_UNIONPAY_STATUS = "updateUnionPayStatus";
    public static String V2_SAVE_LOYALTY_INFO = "loyalty/saveLoyaltyInfo";
    public static String V2_SAVE_TERMINAL_CONFIG = "saveTerminalConfig";
    public static String V2_GET_TERMINAL_CONFIG = "getTerminalConfig";
    public static String V2_DELETE_TERMINAL_CONFIG = "deleteTerminalConfig";
    public static String V2_GET_CURRENT_DATETIME = "getCurrentDateTime";
    public static String V2_REFUND_UNION_PAY = "refundUnionPay";
    public static String V2_getDetailsByRef = "getDetailsByRef";
    public static String V2_REGISTRATION = "registerTerminal";
    public static String V2_UPDATE_REQUEST = "updateRequest";
    public static String EXTERNAL_APP_UNIONPAY_RESPONSE="EXTERNAL_APP_UNIONPAY_RESPONSE";



    public static String configIdMatch = "";
    public static String xmppamountforscan = "";
    public static boolean isRefundUnionpayDone = false;
    public static boolean isScannedCode1 = false;
    public static boolean showDialog = false;
    public static boolean isUplanselected = false, isUnionpayselected = false, isUnionQrSelected = false;
    public static boolean isPostReceived = false;
    public static boolean isRegistered = false;
    public static boolean isNetOff = false;
    public static boolean isDeviceHomePressed = false;

}//app contants end
