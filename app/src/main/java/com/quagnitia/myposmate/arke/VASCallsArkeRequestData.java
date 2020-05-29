package com.quagnitia.myposmate.arke;


/**
 * Value added service request data
 *
 * @author hongqy
 */
public class VASCallsArkeRequestData {

    /**
     * Transaction id
     * <p>
     * 交易类型
     */
    String interfaceId;

    /**
     * Value added service order number
     * <p>
     * 增值服务订单号
     */
    String orderNumber;

    /**
     * Transaction amount
     * <p>
     * 交易金额
     */
    Double amount;

    /**
     * When the value is true that POS does not play a single, when the value is false or does not exist, said by the POS hit
     * <p>
     * 当该值为true表示POS不打单，当该值为false或者不存在时表示由POS打
     */
    Boolean needAppPrinted;

    /**
     * Original transaction voucher number
     * <p>
     * 原交易凭证号
     */
    String originalVoucherNumber;

    /**
     * Value added service incoming notes that need to be printed
     * <p>
     * 第三方传入的需要打印的备注信息
     */
    String inputRemarkInfo;

    /**
     * Original transaction reference number
     * <p>
     * 原交易参考号
     */
    private String originalReferenceNumber;

    /**
     * Card number
     * <p>
     * 卡号
     */
    private String cardNumber;

    /**
     * Expiry date
     * <p>
     * 有效期
     */
    private String expiryDate;

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getNeedAppPrinted() {
        return needAppPrinted;
    }

    public void setNeedAppPrinted(Boolean needAppPrinted) {
        this.needAppPrinted = needAppPrinted;
    }

    public String getOriginalVoucherNumber() {
        return originalVoucherNumber;
    }

    public void setOriginalVoucherNumber(String originalVoucherNumber) {
        this.originalVoucherNumber = originalVoucherNumber;
    }

    public String getInputRemarkInfo() {
        return inputRemarkInfo;
    }

    public void setInputRemarkInfo(String inputRemarkInfo) {
        this.inputRemarkInfo = inputRemarkInfo;
    }

    public String getOriginalReferenceNumber() {
        return originalReferenceNumber;
    }

    public void setOriginalReferenceNumber(String originalReferenceNumber) {
        this.originalReferenceNumber = originalReferenceNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}

