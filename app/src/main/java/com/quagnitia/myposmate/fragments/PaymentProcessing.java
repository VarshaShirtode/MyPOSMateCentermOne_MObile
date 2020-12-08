package com.quagnitia.myposmate.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.smartpos.aidl.printer.AidlPrinter;
import com.centerm.smartpos.aidl.printer.AidlPrinterStateChangeListener;
import com.centerm.smartpos.aidl.printer.PrintDataObject;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.constant.DeviceErrorCode;
import com.centerm.smartpos.util.LogUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.quagnitia.myposmate.MyPOSMateApplication;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.utils.AESHelper;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;

import static com.quagnitia.myposmate.fragments.ManualEntry.isTrigger;
import static com.quagnitia.myposmate.fragments.ManualEntry.requestId;

public class PaymentProcessing extends Fragment implements View.OnClickListener, OnTaskCompleted {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View view;
    private ImageView img_payment, payment_image;
    private Button btn_ok, btn_email;
    private TextView tv_amount;
    private String result = "";
    private JSONObject jsonObject;
    private TextView payment_tag;
    private TextView tv_referenceid, tv_transid, tv_tradeno, tv_reference, tv_reference1;
    private PreferencesManager preferencesManager;
    private static final String TAG = "PrinterDemo";
    private static final int FONT_SIZE_SMALL = 0;
    private static final int FONT_SIZE_NORMAL = 1;
    private static final int FONT_SIZE_LARGE = 2;
    String status = "";

    private static String JSON_DATA = "";// = "{\"ACQUIRER\":\"收单行\",\"AMOUNT\":\"金额\",\"AUTH_NO\":\"授权码\",\"BATCH_NO\":\"批次号\",\"BONUS_POINTS\":\"奖励积分\",\"CARDHOLDER_SIGN\":\"持卡人签名(CARDHOLDER SIGNATURE)\",\"CARDHOLDER_SIGN_UPCASE\":\"持卡人签名(CARDHOLDER SIGNATURE)\",\"CARDHOLDER_TIP\":\"持卡人手续费\",\"CARD_NO\":\"卡号\",\"CASH_SERIAL_NUMBER\":\"收银流水号\",\"COMMODITY_CODE\":\"商品代码\",\"DATE_TIME\":\"日期时间\",\"DESKER\":\"结账数\",\"DOMESTIC_ACCOUNT_IS_BALANCED\":\"国内帐户平衡\",\"DOMESTIC_ACCOUNT_IS_UNBALANCED\":\"国内的帐户不平衡\",\"DOMESTIC_CARD\":\"内卡\",\"DOWN_PAYMENT_AMOUNT\":\"首付还款金额\",\"DUPLICATED\":\"重打印凭证/DUPLICATED\",\"ECASH_BALANCE\":\"电子现金余额\",\"EXCHANGE_INTEGRAL\":\"兑换积分数\",\"EXPIRY_DATE\":\"有效期\",\"FOREIGN_ACCOUNT_IS_BALANCED\":\"境外账户平衡\",\"FOREIGN_ACCOUNT_IS_UNBALANCED\":\"外卡不平衡\",\"FOREIGN_CARD\":\"外卡\",\"FULL_PAYMENT\":\"一次性支付\",\"INSTALLMENT\":\"分期付款\",\"ISSUER\":\"发卡行\",\"I_ACKNOWLEDGE_SATISFACTORY_OF_RELATIVE_GOODS_SERVICE\":\"本人确认以上交易,同意将其计入本卡账户\",\"MERCHANT_NAME\":\"商户名称\",\"MERCHANT_NO\":\"商户编号\",\"NUMBER\":\"笔数\",\"OLD_AUTH_NO\":\"原授权码\",\"OLD_DATE\":\"原交易日期\",\"OLD_REF_NO\":\"原参考号\",\"OLD_VOUCHER\":\"原凭证号\",\"OPERATOR_NO\":\"操作员号\",\"PAYMENT_AMOUNT\":\"自付金金额\",\"PHONE_NUMBER\":\"手机号\",\"REFERENCE\":\"备注\",\"REFERENCE_NO\":\"参考号\",\"REPAYMENT_CURRENCY\":\"还款币种\",\"RE_PRINT_THE_BILL\":\"重打印结算单\",\"RMB\":\"RMB\",\"SERVICE_CHARGE_OF_DOWN_PAYMENT\":\"首期手续费\",\"SERVICE_CHARGE_OF_EACH_PERIODS\":\"每期手续费\",\"SERVICE_CHARGE_PAYMENT_MODE\":\"手续费支付方式\",\"SERVICE_HOTLINE\":\"服务热线号码\",\"SETTLEMENT_STATISTIC\":\"结算统计\",\"STATISTIC_LIST\":\"统计单\",\"TERMINAL_NO\":\"终端编号\",\"THE_BALANCE_OF_INTEGRAL\":\"积分余额\",\"THE_NUMBER_OF_PERIODS\":\"分期期数\",\"TIP\":\"小费\",\"TOTAL\":\"总计(TOTAL)\",\"TOTAL_AMOUNT\":\"金额总计\",\"TOTAL_NUM\":\"笔数总计\",\"TRANSACTION_AMOUNT\":\"交易 AMT\",\"TRANSACTION_LINES\":\"交易笔数\",\"TRANSACTION_TYPE\":\"交易类型\",\"TRANSFER_CARD_NUMBER\":\"转入卡号\",\"TYPE\":\"类型\",\"VOUCHER_NO\":\"凭证号\",\"acquirer\":\"测试系统\",\"amtTrans\":\"0.12\",\"authCode\":\"A54321\",\"batchNo\":\"000003\",\"cardNo\":\"5187 10** **** 6333 /S\",\"copyType\":\"商户存根(MERCHANT COPY)\",\"dateTime\":\"2017/08/01 14:17:13\",\"hotline\":\"400-200-200\",\"icTagFlag\":false,\"issuer\":\"测试系统\",\"merchantName\":\"ABCDEFGHIJKLMNOPQRSTUV    12345678901234\",\"merchantNo\":\"123456789012345\",\"noSignText\":\"\",\"operatorNo\":\"01\",\"playEnglishFlag\":true,\"printTipFlag\":false,\"referenceNo\":\"220801141713\",\"reprintFlag\":false,\"shouldDeclare\":true,\"shouldSign\":true,\"signImage\":\"\",\"terminalNo\":\"12345678\",\"transTypePrint\":\"消费\",\"voucherNo\":\"000106\"}";

    public PaymentProcessing() {
        // Required empty public constructor
    }

    public static PaymentProcessing newInstance(String param1, String param2) {
        PaymentProcessing fragment = new PaymentProcessing();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    String roundTwoDecimals(Double d) {
        DecimalFormat twoDForm = new DecimalFormat("#0.00");
        return twoDForm.format(d);
    }

    AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment_processing, container, false);
        preferencesManager = PreferencesManager.getInstance(getActivity());
        dialog = new AlertDialog.Builder(getActivity())
                .setNegativeButton(getString(R.string.cancel), null)
                .setCancelable(false)
                .create();
        hashMapKeys = new TreeMap<>();
        initUI(view);
        initListener();
        if (getArguments() != null) {
            try {
                result = getArguments().getString(ARG_PARAM1);
                if (result != null && !result.equals("")) {
                    jsonObject = new JSONObject(result);

                    if (jsonObject.optString("channel").equals("WECHAT")) {
                        img_payment.setImageResource(R.drawable.ic_smalwechat);
                    } else if (jsonObject.optString("channel").equals("ALIPAY")) {
                        img_payment.setImageResource(R.drawable.ic_smallali);
                    }
//                    if(isTrigger)
//                    {
//                        isTrigger=false;
                        callUpdateRequestAPI1(requestId,true);
//                    }

                    if (jsonObject.optString("status").equals("true")
                            || jsonObject.optString("responseCodeThirtyNine").equals("00")

                    ) {
                        if (jsonObject.optJSONObject("payment").optString("paymentStatus").equals("FAILED")||
                                jsonObject.optJSONObject("payment").optString("paymentStatus").equals("CLOSED")
                        ) {
                            payment_tag.setText("Payment Unsuccessful");
                            status = "Unsuccessful";
                            payment_image.setImageResource(R.drawable.unsuccessful_icon);
                            if (jsonObject.optJSONObject("payment").has("receiptAmount")) {
                                tv_amount.setText("$" + roundTwoDecimals(Double.valueOf(jsonObject.optJSONObject("payment").optString("receiptAmount"))));
                            } else if (jsonObject.optJSONObject("payment").has("grandTotal")) {
                                tv_amount.setText("$" + roundTwoDecimals(Double.valueOf(jsonObject.optJSONObject("payment").optString("grandTotal"))));
                            }
                        } else {
                            payment_tag.setText("Payment Successful");
                            status = "Successful";
                            if (jsonObject.optJSONObject("payment").has("receiptAmount")) {
                                tv_amount.setText("$" + roundTwoDecimals(Double.valueOf(jsonObject.optJSONObject("payment").optString("receiptAmount"))));
                            } else if (jsonObject.optJSONObject("payment").has("amount")) {
                                tv_amount.setText("$" + roundTwoDecimals(Double.valueOf(jsonObject.optJSONObject("payment").optString("amount"))));
                            } else if (jsonObject.optJSONObject("payment").has("grandTotal")) {
                                tv_amount.setText("$" + roundTwoDecimals(Double.valueOf(jsonObject.optJSONObject("payment").optString("grandTotal"))));
                            }

                            payment_image.setImageResource(R.drawable.successful_icon);
                        }

                    } else {
                        status = "Unsuccessful";
                        payment_tag.setText("Payment Unsuccessful");
                        tv_amount.setText("$00.00");
                        payment_image.setImageResource(R.drawable.unsuccessful_icon);
                    }

                    if (preferencesManager.getReference().equals("")) {
                        tv_reference.setVisibility(View.GONE);
                        tv_reference1.setVisibility(View.GONE);
                    } else {
                        tv_reference.setText(preferencesManager.getReference());
                        tv_reference.setVisibility(View.VISIBLE);
                        tv_reference1.setVisibility(View.VISIBLE);
                    }
                    tv_tradeno.setText(jsonObject.optJSONObject("payment").optString("tradeNo"));
                    if (jsonObject.optJSONObject("payment").has("orderNumber")) {
                        tv_referenceid.setText(jsonObject.optJSONObject("payment").optString("orderNumber"));
                    } else
                        tv_referenceid.setText(jsonObject.optJSONObject("payment").optString("referenceId"));
                    tv_transid.setText(jsonObject.optJSONObject("payment").optString("id"));//increment_id

                }
                if (preferencesManager.getisPrint().equals("true")) {
                    print();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        callAuthToken();
        return view;
    }
    TreeMap<String, String> hashMapKeys;
    public void callUpdateRequestAPI1(String request_id, boolean executed) {
        openProgressDialog();
        try {
            //v2 signature implementation

            hashMapKeys.clear();
            hashMapKeys.put("branch_id", preferencesManager.getMerchantId());
            hashMapKeys.put("terminal_id", preferencesManager.getterminalId());
            hashMapKeys.put("config_id", preferencesManager.getConfigId());
            hashMapKeys.put("access_id", preferencesManager.getuniqueId());
            hashMapKeys.put("request_id", request_id);
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("executed", executed + "");

            new OkHttpHandler(getActivity(), this, null, "updateRequest")
                    .execute(AppConstants.BASE_URL2 + AppConstants.UPDATE_REQUEST +
                            MD5Class.generateSignatureString(hashMapKeys, getActivity())
                            + "&access_token=" + preferencesManager.getauthToken());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ProgressDialog progress;

    public void openProgressDialog() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    public void callAuthToken() {
        // openProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "password");
        hashMap.put("username", preferencesManager.getterminalId());
        hashMap.put("password", preferencesManager.getuniqueId());
        new OkHttpHandler(getActivity(), this, hashMap, "AuthToken").execute(AppConstants.AUTH);

    }

    public static PaymentProcessing getInstance() {
        return new PaymentProcessing();
    }

    /**
     * Read the file in the assets directory.
     */
    private byte[] readAssetsFile(String fileName) throws RemoteException {
        InputStream input = null;
        try {
            input = getContext().getAssets().open(fileName);
            byte[] buffer = new byte[input.available()];
            int size = input.read(buffer);
            if (size == -1) {
                throw new RemoteException(getContext().getString(R.string.read_fail));
            }
            return buffer;
        } catch (IOException e) {
            throw new RemoteException(e.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    //showToast(e.getLocalizedMessage());
                }
            }
        }
    }

    public String decryption(String strEncryptedText) {
        String seedValue = "YourSecKey";
        String strDecryptedText = "";
        try {
            strDecryptedText = AESHelper.decrypt(seedValue, strEncryptedText);
//            strDecryptedText = AESHelper.decrypt(strEncryptedText, seedValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String emailPattern1 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";

    private void print() throws RemoteException {
        JSONObject jsonObject=this.jsonObject.optJSONObject("payment");

        btn_ok.setEnabled(false);
        btn_email.setEnabled(false);
        btn_ok.setOnClickListener(null);

        btn_ok.setClickable(false);
        btn_email.setClickable(false);
        final List<PrintDataObject> list = new ArrayList<PrintDataObject>();
        int fontSize = 24;
        list.add(new PrintDataObject("------- " + status + " -------",
                fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                true));
        list.add(new PrintDataObject("Merchant Name:",
                fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                true));
            list.add(new PrintDataObject(preferencesManager.getMerchantName(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));

        list.add(new PrintDataObject("Branch Name:",
                fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                true));
        list.add(new PrintDataObject(preferencesManager.getbranchName(),
                fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                true));


        if (preferencesManager.getBranchName().equals("true")) {
            list.add(new PrintDataObject("Branch Info:",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject(preferencesManager.getmerchant_name(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
        }


        if (preferencesManager.getGSTNo().equals("true")) {
            list.add(new PrintDataObject("GST:",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));

            ArrayList<String> arrayList1 = new ArrayList<>();
            char cc[] = preferencesManager.getgstno().toCharArray();
            for (int i = 0; i < cc.length; i++) {
                if (cc.length == 8) {
                    if (i == 1) {
                        arrayList1.add(cc[i] + "-");
                    } else if (i == 4) {
                        arrayList1.add(cc[i] + "-");
                    } else {
                        arrayList1.add(cc[i] + "");
                    }

                } else if (cc.length == 9) {
                    if (i == 2) {
                        arrayList1.add(cc[i] + "-");
                    } else if (i == 5) {
                        arrayList1.add(cc[i] + "-");
                    } else {
                        arrayList1.add(cc[i] + "");
                    }
                }
            }
            String sss = "";
            for (int i = 0; i < arrayList1.size(); i++) {
                sss = sss + arrayList1.get(i);
            }
            list.add(new PrintDataObject(sss,
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
        }

        if (preferencesManager.getBranchAddress().equals("true")) {
            char c[] = preferencesManager.getaddress().toCharArray();
            ArrayList<String> arrayList = new ArrayList<>();
            for (int i = 0; i < c.length; i++) {
                arrayList.add(c[i] + "");

            }
            String s = "";
            int j = 31;
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.size() > 31) {
                    if (i == j) {
                        arrayList.add(i, arrayList.get(i) + "\n");
                        j = j * 2;
                    }
                }
                s = s + arrayList.get(i);
            }

            list.add(new PrintDataObject("Address:",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject(s,
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
        }

        if (preferencesManager.getBranchPhoneNo().equals("true")) {
            list.add(new PrintDataObject("Contact Number:",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject(preferencesManager.getcontact_no(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
        }

        if (preferencesManager.getBranchEmail().equals("true")) {
            list.add(new PrintDataObject("Contact Email:",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            if (!preferencesManager.getcontact_email().equals("")) {
                if (preferencesManager.getcontact_email().trim().matches(emailPattern1)) {
                    list.add(new PrintDataObject(preferencesManager.getcontact_email(),
                            fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                            true));
                } else if (preferencesManager.getcontact_email().trim().matches(emailPattern)) {
                    list.add(new PrintDataObject(preferencesManager.getcontact_email(),
                            fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                            true));
                } else {
                    list.add(new PrintDataObject(decryption(preferencesManager.getcontact_email()),
                            fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                            true));
                }
            }

        }


        list.add(new PrintDataObject("Transaction Number:",
                fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                true));
        list.add(new PrintDataObject(tv_transid.getText().toString(),
                fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                true));
        list.add(new PrintDataObject("Reference Number:",
                fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                true));
        list.add(new PrintDataObject(tv_referenceid.getText().toString(),
                fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                true));


        if (jsonObject.has("tradeNo")) {
            list.add(new PrintDataObject("Trade Number:",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
            list.add(new PrintDataObject(tv_tradeno.getText().toString(),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
        }

        list.add(new PrintDataObject("Date (Local):",
                fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                true));

        try {

            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df1.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df2.setTimeZone(TimeZone.getTimeZone(preferencesManager.getTimeZoneId()));


            Date d = df1.parse(jsonObject.optString("createDate"));
            list.add(new PrintDataObject(df2.format(d).replace("T"," "),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));


        } catch (Exception e) {
            e.printStackTrace();
        }


        if (jsonObject.has("ref1") && !jsonObject.optString("ref1").equals("") && !jsonObject.optString("ref1").equals("null")) {
            if (!preferencesManager.getReference().equals("")) {
                list.add(new PrintDataObject("Reference:",
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));

                list.add(new PrintDataObject(preferencesManager.getReference(),
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));

            } else {
                list.add(new PrintDataObject("Reference:",
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));

                list.add(new PrintDataObject(jsonObject.optString("ref1"),
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));
            }

        }

        try
        {
            if (jsonObject.has("discountDetails")) {
                list.add(new PrintDataObject("Amount:",
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
                list.add(new PrintDataObject(jsonObject.optString("currency") + " " + roundTwoDecimals(Double.valueOf(jsonObject.optString("grandTotal"))),
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
                JSONArray jsonArray=new JSONArray(jsonObject.optString("discountDetails"));
                if(jsonArray.length()==1)
                {
                    list.add(new PrintDataObject("Discount:",
                            fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                            true));
                    list.add(new PrintDataObject(jsonObject.optString("currency") + " "+jsonArray.optJSONObject(0).optString("discountAmt"),
                            fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                            true));
                    return;
                }
                list.add(new PrintDataObject("Uplan Discount:",
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
                list.add(new PrintDataObject(jsonObject.optString("currency") + " "+jsonArray.optJSONObject(0).optString("discountAmt"),
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
                list.add(new PrintDataObject("Discount:",
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));
                list.add(new PrintDataObject(jsonObject.optString("currency") + " "+jsonArray.optJSONObject(1).optString("discountAmt"),
                        fontSize, false, PrintDataObject.ALIGN.LEFT, false,
                        true));

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        list.add(new PrintDataObject("Paid Amount:",
                fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                true));

        if (jsonObject.has("receiptAmount")) {
            if (jsonObject.optString("channel").equals("UNION_PAY")) {
                list.add(new PrintDataObject(jsonObject.optString("currency") + " " + roundTwoDecimals(Double.valueOf(jsonObject.optString("receiptAmount"))),
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));

            } else {



                if (jsonObject.has("rate")) {
                    Double receipt_amount = Double.parseDouble(jsonObject.optString("receiptAmount"));
                    Double rate = Double.parseDouble(jsonObject.optString("rate"));
                    Double rmb_amount = receipt_amount * rate;
                    list.add(new PrintDataObject(jsonObject.optString("currency") + " " + roundTwoDecimals(Double.valueOf(jsonObject.optString("receiptAmount"))) + " RMB " + roundTwoDecimals(rmb_amount),
                            fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                            true));
                } else {
                    list.add(new PrintDataObject(jsonObject.optString("currency") + " " + roundTwoDecimals(Double.valueOf(jsonObject.optString("receiptAmount"))) + " RMB 0.00",
                            fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                            true));
                }
            }


        } else if (jsonObject.has("grandTotal")) {
            if (jsonObject.optString("channel").equals("UNION_PAY")) {
                list.add(new PrintDataObject(jsonObject.optString("currency") + " " + roundTwoDecimals(Double.valueOf(jsonObject.optString("grandTotal"))),
                        fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                        true));

            } else {
                if (jsonObject.has("rate")) {

                    Double receipt_amount = Double.parseDouble(jsonObject.optString("receiptAmount"));
                    Double rate = Double.parseDouble(jsonObject.optString("rate"));
                    Double rmb_amount = receipt_amount * rate;
                    list.add(new PrintDataObject(jsonObject.optString("currency") + " " + roundTwoDecimals(Double.valueOf(jsonObject.optString("grandTotal"))) + " RMB " + roundTwoDecimals(rmb_amount),
                            fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                            true));
                } else {
                    list.add(new PrintDataObject(jsonObject.optString("currency") + " " + roundTwoDecimals(Double.valueOf(jsonObject.optString("grandTotal"))) + " RMB 0.00",
                            fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                            true));
                }
            }
        }




        if (jsonObject.has("feeAmount") && !jsonObject.optString("feeAmount").equals("0.0") &&
                !jsonObject.optString("feeAmount").equals("0.00")) {

            Double originalAmount=Double.parseDouble(jsonObject.optString("receiptAmount"))-
                    Double.parseDouble(jsonObject.optString("feeAmount"));
            list.add(new PrintDataObject("Original Amount: "
                    + jsonObject.optString("currency") + " " +
                    originalAmount, fontSize, true,
                    PrintDataObject.ALIGN.LEFT, false, true));


            list.add(new PrintDataObject("Fee Amount: " + jsonObject.optString("currency") + " " + jsonObject.optString("feeAmount"),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
        }

        if (jsonObject.has("feePercentage") && !jsonObject.optString("feePercentage").equals("0.0") &&
                !jsonObject.optString("feePercentage").equals("0.00")) {
            list.add(new PrintDataObject("Fee Percentage: " + jsonObject.optString("feePercentage"),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
        }

        if (jsonObject.has("discount") && !jsonObject.optString("discount").equals("0.0") &&
                !jsonObject.optString("discount").equals("0.00")) {
            list.add(new PrintDataObject("Discount: " + jsonObject.optString("currency") + " " + jsonObject.optString("discount"),
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
        }

        list.add(new PrintDataObject("Payment via: " + jsonObject.optString("channel"),
                fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                true));
        if (!preferencesManager.isQR()) {
            list.add(new PrintDataObject("\n\n",
                    fontSize, true, PrintDataObject.ALIGN.LEFT, false,
                    true));
        }
        String text = tv_referenceid.getText().toString();
        Bitmap bitmap = null;
        if (preferencesManager.isQR()) {
            if (!text.equals("")) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        }


        try {
            int ret = printDev.printTextEffect(list);
            if (preferencesManager.isQR()) {

                printDev.printBmpFast(bitmap, Constant.ALIGN.LEFT, callback);
                bitmap.recycle();
                btn_ok.setClickable(true);
                btn_email.setClickable(true);
                btn_ok.setEnabled(true);
                btn_email.setEnabled(true);
                btn_ok.setOnClickListener(this);
                btn_email.setOnClickListener(this);
            }
            if (preferencesManager.isQR()) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            printDev.spitPaper(50);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, 1000);
            } else {
                btn_ok.setClickable(true);
                btn_email.setClickable(true);
                btn_ok.setEnabled(true);
                btn_email.setEnabled(true);
                btn_ok.setOnClickListener(this);
                btn_email.setOnClickListener(this);
            }
            Log.e("test", "返回码：" + ret);
            getMessStr(ret);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void initUI(View view) {
        img_payment = view.findViewById(R.id.img_payment);
        payment_tag =  view.findViewById(R.id.payment_tag);
        btn_ok =  view.findViewById(R.id.btn_ok);
        btn_email =  view.findViewById(R.id.btn_email);
        payment_image =  view.findViewById(R.id.payment_image);
        tv_amount =  view.findViewById(R.id.tv_amount);
        tv_referenceid =  view.findViewById(R.id.tv_referenceid);
        tv_transid =  view.findViewById(R.id.tv_transid);
        tv_tradeno =  view.findViewById(R.id.tv_tradeno);
        tv_reference =  view.findViewById(R.id.tv_reference);
        tv_reference1 =  view.findViewById(R.id.tv_reference1);
        if (ManualEntry.selected_screen == 1) {
            img_payment.setImageResource(R.drawable.ic_smallali);
        } else if (ManualEntry.selected_screen == 2) {
            img_payment.setImageResource(R.drawable.ic_smalwechat);
        } else if (ManualEntry.selected_screen == 4) {
            img_payment.setImageResource(R.drawable.ic_smalluni);
        }

        img_payment.setVisibility(View.INVISIBLE);
    }

    public void initListener() {
        btn_ok.setOnClickListener(this);
        btn_email.setOnClickListener(this);
    }

    private Context mContext;
    boolean isNetConnectionOn=false;
    @Override
    public void onClick(View view) {
        mContext = getActivity();
        if (((DashboardActivity) mContext).mPopupWindow.isShowing())
            ((DashboardActivity) mContext).mPopupWindow.dismiss();
        switch (view.getId()) {
            case R.id.btn_ok:
                MyPOSMateApplication.isOpen = false;
                MyPOSMateApplication.isActiveQrcode = false;

                //added for external apps 12/5/2019
                returnDataToExternalApp();

//                if (((MyPOSMateApplication) getActivity().getApplicationContext()).mStompClient.isConnected()) {
                    if (preferencesManager.isManual()) {
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
                    } else {
                        ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
                    }
//                } else {
//                    preferencesManager.setIsAuthenticated(false);
//                    preferencesManager.setIsConnected(false);
//                    isNetConnectionOn=true;
//                    callAuthToken();
//
//                    ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
//                }

                break;

            case R.id.btn_email:
                try {
                    JSON_DATA = "{\"Merchant Name\":\"" + preferencesManager.getmerchant_name() + "\",\"Address\":\"" + preferencesManager.getaddress() + "\"," +
                            "\"Contact Number\":\"" + preferencesManager.getcontact_no() + "\",\"Contact Email\":" +
                            "\"" + preferencesManager.getcontact_email() + "\",\"Transaction Number\":\"" + tv_transid.getText().toString() +
                            "\",\"Reference Number\":\"" + tv_referenceid.getText().toString() +
                            "\",\"Trade Number\":\"" + tv_tradeno.getText().toString() + "\",\"Date (Local)\":\"持卡人手续费\"," +
                            "\"Amount\":\"" + tv_amount.getText().toString()
                            + "\",\"Payment via\":\"" + jsonObject.optString("channel") + "\"}";
                    print();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }



    public void returnDataToExternalApp()
    {
        try
        {

            //added for external apps 12/5/2019
            int REQ_PAY_SALE = 100;
            if (DashboardActivity.isExternalApp) {
                DashboardActivity.isExternalApp = false;
                ((DashboardActivity) getActivity()).getIntent().putExtra("result",jsonObject.toString());
                ((DashboardActivity) getActivity()).setResult(REQ_PAY_SALE, ((DashboardActivity) getActivity()).getIntent());
                ((DashboardActivity) getActivity()).finishAndRemoveTask();
                System.exit(0);
                return;
            }  //added for external apps


        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void _parseUpdateRequest(JSONObject jsonObject)
    {
        if(jsonObject.optBoolean("status"))
        {
            Toast.makeText(getActivity(), "Cancel Trigger Request Is Successful", Toast.LENGTH_SHORT).show();
            if (preferencesManager.isManual()) {
                ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY, null);
            } else {
                ((DashboardActivity) getActivity()).callSetupFragment(DashboardActivity.SCREENS.POSMATECONNECTION, null);
            }
        }
        else
        {
            Toast.makeText(getActivity(), "Error cancelling request", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
        JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {
            case "updateRequest":
                if(progress.isShowing())
                    progress.dismiss();
                callAuthToken();
             //   _parseUpdateRequest(jsonObject);
                break;
            case "AuthToken":
//                if(progress.isShowing())
//                    progress.dismiss();
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferencesManager.setauthToken(jsonObject.optString("access_token"));
                }
                if(isNetConnectionOn)
                {
                    isNetConnectionOn=false;
                    Log.v("PaymentProcessing","PaymentProcessing Called connection");
//                    if(MyPOSMateApplication.mStompClient==null)
//                    {
                            ((MyPOSMateApplication)getActivity().getApplicationContext()).initiateStompConnection(preferencesManager.getauthToken());
//                    }
                }
                break;

        }
    }


    private AidlPrinter printDev = null;
    // 打印机回调对象
    private AidlPrinterStateChangeListener callback = new PrinterCallback(); // 打印机回调
    private EditText qrCode, barCode;
    private String qrStr;
    private String barStr;
    private Spinner spinner;
    private int typeIndex;
    private String codeStr;


    /**
     * 打印机回调类
     */
    private class PrinterCallback extends AidlPrinterStateChangeListener.Stub {

        @Override
        public void onPrintError(int arg0) throws RemoteException {
            // showMessage("打印机异常" + arg0, Color.RED);
            Toast.makeText(getActivity(), arg0, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrintFinish() throws RemoteException {
           // Toast.makeText(getActivity(), getString(R.string.printer_finish), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPrintOutOfPaper() throws RemoteException {
            Looper.prepare();
            Toast.makeText(getActivity(), getString(R.string.printer_need_paper), Toast.LENGTH_SHORT).show();
        }
    }

    public void onDeviceConnected(AidlDeviceManager deviceManager) {
        try {
            printDev = AidlPrinter.Stub.asInterface(deviceManager
                    .getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_PRINTERDEV));
            printDev.setPrinterGray(0x02);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void getMessStr(int ret) {
        switch (ret) {
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_BUSY:
                Toast.makeText(getActivity(), getString(R.string.printer_device_busy), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_OK:
                Toast.makeText(getActivity(), getString(R.string.printer_success), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_OUT_OF_PAPER:
                Toast.makeText(getActivity(), getString(R.string.printer_lack_paper), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_HEAD_OVER_HEIGH:
                Toast.makeText(getActivity(), getString(R.string.printer_over_heigh), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_OVER_HEATER:
                Toast.makeText(getActivity(), getString(R.string.printer_over_heat), Toast.LENGTH_SHORT).show();
                break;
            case DeviceErrorCode.DEVICE_PRINTER.DEVICE_PRINTER_LOW_POWER:
                Toast.makeText(getActivity(), getString(R.string.printer_low_power), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), getString(R.string.printer_other_exception_code) + ret, Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        bindService();
        if (preferencesManager.getisPrint().equals("true")) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        print();
                    } catch (Exception e) {
                    }
                }
            }, 100);

        }

    }


    public AidlDeviceManager manager = null;
    Intent intentService;

    public void bindService() {
        intentService = new Intent();
        intentService.setPackage("com.centerm.smartposservice");
        intentService.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        getActivity().bindService(intentService, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 服务连接桥
     */
    public ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            manager = null;
            try {
                LogUtil.print(getResources().getString(R.string.bind_service_fail));
                LogUtil.print("manager = " + manager);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            manager = AidlDeviceManager.Stub.asInterface(service);

            if (null != manager) {
                try {
                    LogUtil.print(getResources().getString(R.string.bind_service_success));
                    LogUtil.print("manager = " + manager);
                    onDeviceConnected(manager);
                } catch (Exception e) {

                }

            }
        }


    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            getActivity().unbindService(conn);
        }
        getActivity().stopService(intentService);
    }
}
