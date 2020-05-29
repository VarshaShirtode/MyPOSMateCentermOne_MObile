package com.quagnitia.myposmate.centrum;

/**
 * Created by daizy
 * date 2019/9/2.
 */
public class ThirtConst {

    public class RequestTag {
        /**
         * 交易类型
         */
        public static final String THIRD_PATH_TRANS_TYPE = "transactionType";
        /**
         * 交易金额
         */
        public static final String THIRD_PATH_TRANS_AMOUNT = "amount";
        /**
         * 交易单号
         */
        public static final String THIRD_PATH_TRANS_ORDER_NO = "orderNumber";
        /**
         * 预留信息
         */
        public static final String THIRD_PATH_TRANS_REMARK_INFO = "inputRemarkInfo";
        /**
         * 扫码支付付款二维码
         */
        public static final String THIRD_PATH_TRANS_SCAN_AUTH_PAY_CODE = "qrcode";
        /**
         * 原交易流水号-撤销用
         */
        public static final String THIRD_PATH_TRANS_ORI_VOUCHER_NO = "originalVoucherNumber";
        /**
         * 原交易参考号-退货用
         */
        public static final String THIRD_PATH_TRANS_ORI_REFERENCE_NO = "originalReferenceNumber";
        /**
         * 元交易日期-退货用
         */
        public static final String THIRD_PATH_TRANS_ORI_TRANS_DATE = "originalTransactionDate";
        /**
         * 优惠券信息
         */
        public static final String THIRD_PATH_TRANS_COUPON_INFO = "couponInformation";
        /**
         * 原交易授权码-预授权类交易使用
         */
        public static final String THIRD_PATH_TRANS_ORI_AUTH_CODE = "originalAuthorizationCode";
    }

    public class RespondTag {
        /**
         * 交易类型
         */
        public static final String THIRD_PATH_TRANS_TYPE = "transactionType";
        /**
         * 交易接口id？？？做啥子用
         */
        public static final String THIRD_PATH_TRANS_INTERFACE_ID = "interfaceId";
        /**
         * 收单包名
         */
        public static final String THIRD_PATH_TRANS_PACKAGE_NAME = "packageName";
        /**
         * 商户名称
         */
        public static final String THIRD_PATH_TRANS_MERCHANT_NAME = "merchantName";
        /**
         * 商户号
         */
        public static final String THIRD_PATH_TRANS_MERCHANT_NO = "merchantNumber";
        /**
         * 终端号
         */
        public static final String THIRD_PATH_TRANS_TERMINAL_NO = "terminalNumber";
        /**
         * 操作员号
         */
        public static final String THIRD_PATH_TRANS_OPERATOR_ID = "operatorNumber";
        /**
         * 交易金额
         */
        public static final String THIRD_PATH_TRANS_AMOUNT = "amount";
        /**
         * 交易卡号
         */
        public static final String THIRD_PATH_TRANS_CARD_NO = "cardNumber";
        /**
         * 交易卡有效期
         */
        public static final String THIRD_PATH_TRANS_CARD_EXPIRATE_DATE = "expirationDate";
        /**
         * 交易授权码
         */
        public static final String THIRD_PATH_TRANS_AUTH_CODE = "authCode";
        /**
         * 交易批次号
         */
        public static final String THIRD_PATH_TRANS_BATCH_NO = "batchNumber";
        /**
         * 交易流水号
         */
        public static final String THIRD_PATH_TRANS_VOUCHER_NO = "voucherNumber";
        /**
         * 参考号
         */
        public static final String THIRD_PATH_TRANS_REFERENCE_NO = "referenceNumber";
        /**
         * 交易结果码
         */
        public static final String THIRD_PATH_TRANS_RESPONSE_CODE = "responseCode";
        /**
         * 39域返回码
         */
        public static final String THIRD_PATH_TRANS_RETURN_CODE_FIELD39 = "responseCodeThirtyNine";
        /**
         * 交易结果描述
         */
        public static final String THIRD_PATH_TRANS_RESPONSE_MSG = "responseMessage";
        /**
         * 39域返回码描述
         */
        public static final String THIRD_PATH_TRANS_RETURN_MSG_FIELD39 = "responseMessageThirtyNine";

        /**
         * 交易日期
         */
        public static final String THIRD_PATH_TRANS_DATE = "transactionDate";
        /**
         * 交易时间
         */
        public static final String THIRD_PATH_TRANS_TIME = "transactionTime";

        /**
         * 交易是否已撤销
         */
        public static final String THIRD_PATH_TRANS_IS_TRANS_VOIDED = "voided";

        /**
         * 交易流水金额？？？做啥用
         */
        public static final String THIRD_PATH_TRANS_VOUCHER_AMOUNT = "voucherAmount";
        /**
         * 交易单号
         */
        public static final String THIRD_PATH_TRANS_ORDER_NO = "orderNumber";
        /**
         * 优惠券信息
         */
        public static final String THIRD_PATH_TRANS_COUPON_INFO = "couponInformation";
        /**
         * 交易渠道 Wechat / Alipay / UnionPay
         */
        public static final String THIRD_PATH_TRANS_PAY_CHANNEL = "channelType";
    }

    public class TransType {
        public static final String SCAN_VOID = "SCAN_VOID";
        public static final String SALE = "SALE";
        public static final String VOID = "VOID";
        public static final String REFUND = "REFUND";
        public static final String COUPON_SALE = "COUPON_SALE";
        public static final String COUPON_VOID = "COUPON_VOID";
        public static final String PRE_AUTH = "PRE_AUTHORIZATION";
        public static final String PRE_AUTH_CANCEL = "PRE_AUTH_VOID";
        public static final String AUTH_COMPLETE = "PRE_AUTH_COMPLETION_REQUEST";
        public static final String AUTH_SETTLEMENT = "PRE_AUTH_COMPLETION_ADVICE";
        public static final String AUTH_COMPLETE_VOID = "PRE_AUTH_COMPLETION_VOID";
        public static final String SETTLE = "SETTLE";
        public static final String TRANS_QUERY = "ORDER_NUMBER_QUERY";
        public static final String PRINT_LAST = "PRINT_LAST";
        public static final String PRINT_ANY = "PRINT_ANY";
    }

    public class PayChannel {
        public static final String WECHAT = "Wechat";
        public static final String ALIPAY = "Alipay";
        public static final String UNION_PAY = "UnionPay";
    }

    public class ExtendParams {

    }
}
