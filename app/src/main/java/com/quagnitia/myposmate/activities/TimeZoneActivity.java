package com.quagnitia.myposmate.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.EditText;
import android.widget.Toast;

import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.utils.AESHelper;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.MD5Class;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static com.quagnitia.myposmate.activities.TimeZoneAdapter.isUpdateDetails;
import static com.quagnitia.myposmate.utils.AppConstants.isTerminalInfoDeleted;

public class TimeZoneActivity extends AppCompatActivity implements OnTaskCompleted {
    RecyclerView recycler_view;
    EditText editTextSearch;
    ArrayList<String> arrayList;
    TimeZoneAdapter timeZoneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_zone);
        preferenceManager = PreferencesManager.getInstance(this);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        hashMapKeys = new TreeMap<>();
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        arrayList = new ArrayList<>();
        String[] timezones = TimeZone.getAvailableIDs();
        for (String tzId : timezones) {

            TimeZone tz = TimeZone.getTimeZone(tzId);
            String name = tz.getDisplayName();
            if (tzId.equals("Australia/Perth") ||
                    tzId.equals("Australia/Eucla") ||
                    tzId.equals("Australia/Darwin") ||
                    tzId.equals("Australia/Brisbane") ||
                    tzId.equals("Australia/Adelaide") ||
                    tzId.equals("Australia/Sydney") ||
                    tzId.equals("Australia/Lord_Howe") ||
                    tzId.equals("Pacific/Fiji") ||
                    tzId.equals("Pacific/Auckland") ||
                    tzId.equals("Pacific/Chatham")
            ) {
                arrayList.add(tzId);
            }

            int offset = tz.getRawOffset();
            // ...
        }


        timeZoneAdapter = new TimeZoneAdapter(this, arrayList);
        recycler_view.setAdapter(timeZoneAdapter);


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });

    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<String> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (String s : arrayList) {
            //if the existing elements contains the search input
            if (s.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        timeZoneAdapter.filterList(filterdNames);
    }

    ProgressDialog progress;

    public void openProgressDialog() {
        progress = new ProgressDialog(TimeZoneActivity.this);
        progress.setMessage("Loading.......");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
    }

    public String encryption(String strNormalText) {
        String seedValue = "YourSecKey";
        String normalTextEnc = "";
        try {
            normalTextEnc = AESHelper.encrypt(seedValue, strNormalText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return normalTextEnc;
    }

    public String decryption(String strEncryptedText) {
        String seedValue = "YourSecKey";
        String strDecryptedText = "";
        try {
            strDecryptedText = AESHelper.decrypt(seedValue, strEncryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }
    public void callAuthToken() {
        openProgressDialog();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
        new OkHttpHandler(TimeZoneActivity.this, this, hashMap, "AuthToken").execute(AppConstants.AUTH);

    }
    private PreferencesManager preferenceManager;
    TreeMap<String, String> hashMapKeys;
    JSONObject updateDetailsJson=null;

    public void callUpdateBranchDetails(JSONObject jsonObject) {

        if(isUpdateDetails)
        {
            updateDetailsJson=jsonObject;
            callAuthToken();
            return;
        }



        openProgressDialog();
        try {
            hashMapKeys.clear();
            hashMapKeys.put("branchAddress",  encryption(preferenceManager.getaddress()));
            hashMapKeys.put("branchContactNo", encryption(preferenceManager.getcontact_no()));
            hashMapKeys.put("branchName",  encryption(preferenceManager.getmerchant_name()));
            hashMapKeys.put("branchEmail", encryption(preferenceManager.getcontact_email()));
            hashMapKeys.put("gstNo",  encryption(preferenceManager.getgstno()));
            hashMapKeys.put("terminalId", encryption(preferenceManager.getterminalId()));
            hashMapKeys.put("otherData", encryption(jsonObject.toString()));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("accessId", encryption(preferenceManager.getuniqueId()));
            hashMapKeys.put("configId", preferenceManager.getConfigId());
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, TimeZoneActivity.this));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
            new OkHttpHandler(TimeZoneActivity.this, this, hashMap, "UpdateBranchDetails")
                    .execute(AppConstants.BASE_URL2 + AppConstants.SAVE_TERMINAL_CONFIG);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callDeleteTerminal() {
        openProgressDialog();
        try {
            hashMapKeys.clear();
            hashMapKeys.put("terminalId", encryption(preferenceManager.getterminalId()));
            hashMapKeys.put("random_str", new Date().getTime() + "");
            hashMapKeys.put("signature", MD5Class.generateSignatureStringOne(hashMapKeys, TimeZoneActivity.this));
            hashMapKeys.put("access_token", preferenceManager.getauthToken());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.putAll(hashMapKeys);
            new OkHttpHandler(TimeZoneActivity.this, this, hashMap, "DeleteTerminal").execute(AppConstants.BASE_URL2 + AppConstants.DELETE_TERMINAL_CONFIG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {

        if (result.equals("")) {
            if (progress.isShowing())
                progress.dismiss();
            Toast.makeText(TimeZoneActivity.this, "No data from server.", Toast.LENGTH_LONG).show();
            return;
        }
        if (progress.isShowing())
            progress.dismiss();
        JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {
            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferenceManager.setauthToken(jsonObject.optString("access_token"));
                }

                if(isUpdateDetails)
                {
                    isUpdateDetails=false;
                    callDeleteTerminal();
                }
                if(isTerminalInfoDeleted)
                {
                    isTerminalInfoDeleted=false;
                    callUpdateBranchDetails(updateDetailsJson);
                }
                break;
            case "DeleteTerminal":
                if (jsonObject.optBoolean("success")) {
                    isTerminalInfoDeleted = true;
                    callAuthToken();
                }
                break;
            case "UpdateBranchDetails":
                if (jsonObject.has("otherData")) {
                    JSONObject jsonObject1 = new JSONObject(decryption(jsonObject.optString("otherData")));


                    if (jsonObject1.has("ConfigId"))
                        preferenceManager.setConfigId(jsonObject1.optString("ConfigId"));
                    if (jsonObject1.has("MerchantId"))
                        preferenceManager.setMerchantId(jsonObject1.optString("MerchantId"));

                    preferenceManager.setcnv_alipay_diaplay_and_add(jsonObject1.optBoolean("CnvAlipayDisplayAndAdd"));
                    preferenceManager.setcnv_alipay_diaplay_only(jsonObject1.optBoolean("CnvAlipayDisplayOnly"));
                    preferenceManager.setcnv_wechat_display_and_add(jsonObject1.optBoolean("CnvWeChatDisplayAndAdd"));
                    preferenceManager.setcnv_wechat_display_only(jsonObject1.optBoolean("CnvWeChatDisplayOnly"));
                    preferenceManager.setisAlipaySelected(jsonObject1.optBoolean("AlipaySelected"));
                    preferenceManager.setisWechatSelected(jsonObject1.optBoolean("WeChatSelected"));
                    preferenceManager.setcnv_wechat(jsonObject1.optString("WeChatValue"));

                    preferenceManager.setisWeChatScan(jsonObject1.optBoolean("WeChatScanQR"));
                    preferenceManager.setisAlipayScan(jsonObject1.optBoolean("AlipayScanQR"));

                    preferenceManager.setisUnionPaySelected(jsonObject1.optBoolean("UnionPay"));
                    preferenceManager.setUnionPayQrSelected(jsonObject1.optBoolean("UnionPayQR"));
                    preferenceManager.setisUnionPayQrCodeDisplaySelected(jsonObject1.optBoolean("isUnionPayQrCodeDisplaySelected"));
                    preferenceManager.setcnv_uniqr(jsonObject1.optString("UnionPayQrValue"));
                    preferenceManager.setcnv_uplan(jsonObject1.optString("UplanValue"));
                    preferenceManager.setcnv_uni_display_and_add(jsonObject1.optBoolean("CnvUnionpayDisplayAndAdd"));
                    preferenceManager.setcnv_uni_display_only(jsonObject1.optBoolean("CnvUnionpayDisplayOnly"));
                    preferenceManager.setisUplanSelected(jsonObject1.optBoolean("Uplan"));
                    preferenceManager.setaggregated_singleqr(jsonObject1.optBoolean("AlipayWeChatPay"));
                    preferenceManager.setAlipayWechatQrSelected(jsonObject1.optBoolean("AlipayWeChatScanQR"));
                    preferenceManager.setisPrint(jsonObject1.optString("PrintReceiptautomatically"));
                    preferenceManager.setshowReference(jsonObject1.optString("ShowReference"));
                    preferenceManager.setisQR(jsonObject1.optBoolean("ShowPrintQR"));
                    preferenceManager.setisLoyality(jsonObject1.optBoolean("Membership/Loyality"));
                    preferenceManager.setisMembershipManual(jsonObject1.optBoolean("ShowMembershipManual"));
                    preferenceManager.setisMembershipHome(jsonObject1.optBoolean("ShowMembershipHome"));
                    preferenceManager.setIsHome(jsonObject1.optBoolean("Home"));
                    preferenceManager.setIsManual(jsonObject1.optBoolean("ManualEntry"));
                    preferenceManager.setIsBack(jsonObject1.optBoolean("Back"));
                    preferenceManager.setIsFront(jsonObject1.optBoolean("Front"));
                    preferenceManager.setisConvenienceFeeSelected(jsonObject1.optBoolean("ConvenienceFee"));
                    preferenceManager.setcnv_alipay(jsonObject1.optString("AlipayWechatvalue"));
                    preferenceManager.setcnv_uni(jsonObject1.optString("UnionPayvalue"));
                    preferenceManager.setBranchName(jsonObject1.optString("EnableBranchName"));
                    preferenceManager.setBranchAddress(jsonObject1.optString("EnableBranchAddress"));
                    preferenceManager.setBranchEmail(jsonObject1.optString("EnableBranchEmail"));
                    preferenceManager.setBranchPhoneNo(jsonObject1.optString("EnableBranchContactNo"));
                    preferenceManager.setGSTNo(jsonObject1.optString("EnableBranchGSTNo"));
                    preferenceManager.setTimeZoneId(jsonObject1.optString("TimeZoneId"));
                    preferenceManager.setTimeZone(jsonObject1.optString("TimeZone"));
                    preferenceManager.setisTimeZoneChecked(jsonObject1.optBoolean("isTimeZoneChecked"));

                    preferenceManager.setTerminalIdentifier(jsonObject1.optString("TerminalIdentifier"));
                    preferenceManager.setPOSIdentifier(jsonObject1.optString("POSIdentifier"));
                    preferenceManager.setLaneIdentifier(jsonObject1.optString("LaneIdentifier"));
                    preferenceManager.setisLaneIdentifier(jsonObject1.optBoolean("isLaneIdentifier"));
                    preferenceManager.setisPOSIdentifier(jsonObject1.optBoolean("isPOSIdentifier"));
                    preferenceManager.setisTerminalIdentifier(jsonObject1.optBoolean("isTerminalIdentifier"));
                }

                break;

        }
    }

    private static final int ITERATION_COUNT = 1000;
    private static final int KEY_LENGTH = 256;
    private static final String PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int PKCS5_SALT_LENGTH = 32;
    private static final String DELIMITER = "]";
    private static final SecureRandom random = new SecureRandom();
    private static final String password = "qspl123";

    public static String encryption1(String plaintext) {
        byte[] salt = generateSalt();
        SecretKey key = deriveKey(password, salt);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            byte[] iv = generateIv(cipher.getBlockSize());
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

            if (salt != null) {
                return String.format("%s%s%s%s%s",
                        toBase64(salt),
                        DELIMITER,
                        toBase64(iv),
                        DELIMITER,
                        toBase64(cipherText));
            }

            return String.format("%s%s%s",
                    toBase64(iv),
                    DELIMITER,
                    toBase64(cipherText));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryption1(String ciphertext) {
        String[] fields = ciphertext.split(DELIMITER);
        if (fields.length != 3) {
            throw new IllegalArgumentException("Invalid encypted text format");
        }
        byte[] salt = fromBase64(fields[0]);
        byte[] iv = fromBase64(fields[1]);
        byte[] cipherBytes = fromBase64(fields[2]);
        SecretKey key = deriveKey(password, salt);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            byte[] plaintext = cipher.doFinal(cipherBytes);
            return new String(plaintext, "UTF-8");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] generateSalt() {
        byte[] b = new byte[PKCS5_SALT_LENGTH];
        random.nextBytes(b);
        return b;
    }

    private static byte[] generateIv(int length) {
        byte[] b = new byte[length];
        random.nextBytes(b);
        return b;
    }

    private static SecretKey deriveKey(String password, byte[] salt) {
        try {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM);
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toBase64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private static byte[] fromBase64(String base64) {
        return Base64.decode(base64, Base64.NO_WRAP);
    }

}
