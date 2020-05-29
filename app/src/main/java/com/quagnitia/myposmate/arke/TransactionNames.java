package com.quagnitia.myposmate.arke;

import com.quagnitia.myposmate.R;


/**
 * Transaction names
 * <p>
 * 交易名称
 */
public enum TransactionNames {
    SIGNIN(R.string.sign_in),
    SALE(R.string.sale, new RequestParameters[]{RequestParameters.amount, RequestParameters.orderNumber, RequestParameters.needAppPrinted}),
    VOID(R.string.void_play, new RequestParameters[]{RequestParameters.originalVoucherNumber, RequestParameters.orderNumber, RequestParameters.needAppPrinted}),
    REFUND(R.string.refund, new RequestParameters[]{RequestParameters.amount, RequestParameters.originalReferenceNumber, RequestParameters.originalTransactionDate, RequestParameters.needAppPrinted}),
    SETTLE(R.string.settle),
   // ORDER_NUMBER_INQUIRY(order_number_inquiry, new RequestParameters[]{RequestParameters.orderNumber}),
    BALANCE_QUERY(R.string.balance_query),
    BALANCE_QUERY_OF_ECASH(R.string.balance_query_of_ecash),
    PRINT_LAST(R.string.print_last),
    PRINT_TRANSACTION_SUMMARY(R.string.print_transaction_summary),
    PRINT_TRANSACTION_DETAIL(R.string.print_transaction_detail),
    TERMINAL_KEY_MANAGEMENT(R.string.terminal_key_management),
    DOWNLOAD_PUBLIC_KEYS(R.string.download_public_keys),
    DOWNLOAD_IC_CARD_PARAMETERS(R.string.download_ic_card_Parameters),
    DOWNLOAD_QPS_PARAMETER(R.string.download_qps_parameter),
    DOWNLOAD_CARD_BIN_B(R.string.download_card_bin_b),
    DOWNLOAD_CARD_BIN_BLACKLIST(R.string.download_card_bin_blacklist),

    PRE_AUTHORIZATION(R.string.pre_authorization, new RequestParameters[]{RequestParameters.amount, RequestParameters.orderNumber, RequestParameters.needAppPrinted}),
    PRE_AUTH_VOID(R.string.pre_auth_void, new RequestParameters[]{RequestParameters.amount, RequestParameters.originalAuthorizationCode, RequestParameters.originalTransactionDate, RequestParameters.orderNumber, RequestParameters.needAppPrinted}),
    PRE_AUTH_COMPLETION_ADVICE(R.string.pre_auth_completion_advice, new RequestParameters[]{RequestParameters.amount, RequestParameters.originalAuthorizationCode, RequestParameters.originalTransactionDate, RequestParameters.orderNumber, RequestParameters.needAppPrinted}),
    PRE_AUTH_COMPLETION_VOID(R.string.pre_auth_completion_void, new RequestParameters[]{RequestParameters.originalVoucherNumber, RequestParameters.orderNumber, RequestParameters.needAppPrinted}),
    PRE_AUTH_COMPLETION_REQUEST(R.string.pre_auth_completion_request, new RequestParameters[]{RequestParameters.amount, RequestParameters.originalAuthorizationCode, RequestParameters.originalTransactionDate, RequestParameters.orderNumber, RequestParameters.needAppPrinted}),
    SALE_BY_SDK(R.string.sale_by_sdk, new RequestParameters[]{RequestParameters.amount, RequestParameters.orderNumber, RequestParameters.needAppPrinted});

    /**
     * Display name
     */
    private int displayNameRes;

    private RequestParameters[] parameters;

    TransactionNames(int displayNameRes, RequestParameters[] parameters) {
        this.displayNameRes = displayNameRes;
        this.parameters = parameters;
    }

    TransactionNames(int displayNameRes) {
        this.displayNameRes = displayNameRes;
    }

//    public String getDisplayName() {
//        return VASCallsArkeActivity.getContext().getString(this.displayNameRes);
//    }

    public RequestParameters[] getParameters() {
        return parameters;
    }

//    @Override
//    public String toString() {
//        return this.getDisplayName();
//    }
}
