package com.quagnitia.myposmate.arke;

import com.quagnitia.myposmate.R;

import java.util.List;

/**
 * Request parameters
 * <p>
 * 请求参数
 */
public enum RequestParameters {
    amount(R.string.amount, Double.class),
    needAppPrinted(R.string.needAppPrinted, Boolean.class),
    originalVoucherNumber(R.string.originalVoucherNumber),
    orderNumber(R.string.orderNumber),
    originalReferenceNumber(R.string.original_reference_number),
    originalTransactionDate(R.string.original_transaction_date),
    originalAuthorizationCode(R.string.original_authorization_code);

    /**
     * Parameter value
     * <p>
     * 参数值
     */
    private String value;

    /**
     * The class of the parameter value
     * <p>
     * 数据类型
     */
    private Class dataType;

    /**
     * The resource id of the displays name
     * <p>
     * 显示名称资源id
     */
    private int displayNameRes;
    private List<TransactionNames> transactions;

    RequestParameters(int displayNameRes, Class dataType) {
        this.displayNameRes = displayNameRes;
        this.dataType = dataType;

    }

    RequestParameters(int displayNameRes) {
        this.displayNameRes = displayNameRes;
        this.dataType = String.class;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

//    public String getDisplayName() {
//        return VASCallsArkeActivity.getContext().getString(displayNameRes);
//    }


    public String getName() {
        return super.name();
    }

    public Class getDataType() {
        return dataType;
    }
}
