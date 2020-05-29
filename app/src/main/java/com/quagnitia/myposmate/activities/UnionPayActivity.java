package com.quagnitia.myposmate.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.quagnitia.myposmate.R;
import com.wizarpos.paymentrouter.aidl.IWizarPayment;
import org.json.JSONException;
import org.json.JSONObject;

public class UnionPayActivity extends AppCompatActivity implements View.OnClickListener {


    private String param, response;
    TextView textViewStat, tv_description;
    private IWizarPayment mWizarPayment;
    final ServiceConnection mConnPayment = new PaymentConnection();
    private String amount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_union_pay);
        textViewStat = (TextView) findViewById(R.id.tv_status);
        tv_description = (TextView) findViewById(R.id.tv_description);
        int[] btnIds = {R.id.bind, R.id.unbind
                , R.id.login, R.id.getPOSInfo, R.id.getPayInfo
                , R.id.payCash, R.id.doReverse, R.id.consumeCancel, R.id.queryBalance, R.id.settle
        };
        for (int id : btnIds) {
            Log.e("btnId", "" + id);
            findViewById(id).setOnClickListener(this);
        }
        if (getIntent().hasExtra("AMOUNT")) {
            amount = getIntent().getStringExtra("AMOUNT").replace(".","");
            textViewStat.setText("Transaction initiated for amount : " + getIntent().getStringExtra("AMOUNT"));
            tv_description.setText("Connecting to server...");
            bindPaymentRouter();
        }
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindPaymentRouter();
    }

    class PaymentConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName compName, IBinder binder) {
            Log.d("onServiceConnected", "compName: " + compName);
            mWizarPayment = IWizarPayment.Stub.asInterface(binder);
            //showResponse("Connect Success!");
            tv_description.setText("Connect Success!");
            if (mWizarPayment == null) {
                response = "Failed to connect!";

            } else if (null == (param = getParam(R.id.payCash))) {
                response = "Call parameter failed!";
            }
            tv_description.setText(response);

            createAsyncTask().execute(R.id.payCash);

        }

        @Override
        public void onServiceDisconnected(ComponentName compName) {
            Log.d("onServiceDisconnected", "compName: " + compName);
            mWizarPayment = null;
            //showResponse("Disconnect Success!");
            tv_description.setText("Disconnect Success!");

        }
    }

    ;

    private void bindPaymentRouter() {
        if (mWizarPayment == null) {
            Intent intent = new Intent("com.wizarpos.paymentrouter.aidl.IWizarPayment");
            intent.setPackage("com.wizarpos.paymentrouter.aidl");
            bindService(intent, mConnPayment, BIND_AUTO_CREATE);
        }
    }

    private void unbindPaymentRouter() {
        if (mWizarPayment != null) {
            unbindService(mConnPayment);
            mWizarPayment = null;
        }
    }

    public void showResponse(String response) {
        this.response = response;
        showResponse();
    }

    public void showResponse() {
        setTextById(R.id.param, param);
        setTextById(R.id.result, response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("RespCode").equalsIgnoreCase("00")) {
                tv_description.setText("Payment was successful!" + "/nResult :" + jsonObject.optString("RespDesc"));
            } else {
                tv_description.setText(jsonObject.optString("RespDesc"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setTextById(int id, CharSequence text) {
        ((TextView) findViewById(id)).setText(text);
    }

    @Override
    public void onClick(final View view) {
        final int btnId = view.getId();
        setTextById(R.id.method, ((TextView) view).getText());

        param = "";
        response = "";
        switch (btnId) {
            case R.id.bind:
                bindPaymentRouter();
                break;
            case R.id.unbind:
                unbindPaymentRouter();
                break;
            default:
                if (mWizarPayment == null) {
                    response = "Please click [ConnectPaymentRouter First]!";
                } else if (null == (param = getParam(btnId))) {
                    response = "Call parameter failed!";
                }
                if (response == "") {
                    createAsyncTask().execute(btnId);
                    return;
                }
                break;
        }
        showResponse();
    }

    private String getParam(int btnId) {
        JSONObject jsonObject = new JSONObject();
        try {
            switch (btnId) {
                case R.id.payCash:
                    setParam4payCash(jsonObject);
                    break;
                case R.id.doReverse:
                    setParam4doReverse(jsonObject);
                    break;
                case R.id.getPOSInfo:
                    setParam4getPOSInfo(jsonObject);
                    break;
                case R.id.getPayInfo:
                    setParam4getPayInfo(jsonObject);
                    break;
                case R.id.login:
                    setParam4login(jsonObject);
                    break;
                case R.id.settle:
                    setParam4settle(jsonObject);
                    break;
                case R.id.queryBalance:
                    setParam4queryBalance(jsonObject);
                    break;
                case R.id.consumeCancel:
                    setParam4consumeCancel(jsonObject);
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject.toString();
    }

    private AsyncTask<Integer, Void, String> createAsyncTask() {
        return new AsyncTask<Integer, Void, String>() {
            protected void onPreExecute() {
                showResponse("...");
            }

            protected String doInBackground(Integer... btnIds) {
                Log.d("doInBackground", "Request: " + param + " mWizarPayment: " + mWizarPayment);

                String result = "Skipped";
                try {
                    switch (btnIds[0]) {
                        case R.id.payCash:
                            result = mWizarPayment.payCash(param);
                            break;
                        case R.id.doReverse:
                            result = mWizarPayment.doReverse(param);
                            break;
                        case R.id.getPOSInfo:
                            result = mWizarPayment.getPOSInfo(param);
                            break;
                        case R.id.getPayInfo:
                            result = mWizarPayment.getPayInfo(param);
                            break;
                        case R.id.login:
                            result = mWizarPayment.login(param);
                            break;
                        case R.id.settle:
                            result = mWizarPayment.settle(param);
                            break;
                        case R.id.queryBalance:
                            result = mWizarPayment.balanceQuery(param);
                            break;
                        case R.id.consumeCancel:
                            result = mWizarPayment.consumeCancel(param);
                            break;
                    }
                } catch (RemoteException e) {
                    result = e.getMessage();
                }

                Log.d("doInBackground", "Response: " + result);

                return result;
            }

            protected void onPostExecute(String result) {
                showResponse(result);
            }
        };
    }

    private void setParam4payCash(JSONObject jsonObject) throws JSONException {
        jsonObject.put("AppID", "12498423");
        jsonObject.put("AppName", "TestAccount");
        jsonObject.put("TrxID", "12498423");
//		jsonObject.put("TransType", 1);
        jsonObject.put("TransAmount", amount);
//		jsonObject.put("TransIndexCode", "14526855");
//		jsonObject.put("ReqTransDate", "140421");
//		jsonObject.put("ReqTransTime", "100445");
//		jsonObject.put("cardInfo2", "6225000000000170=301010100");
//		jsonObject.put("cardInfo3", "6225000000000170=30101010000=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=0=00");
//		jsonObject.put("TerminalID", "100");
//		jsonObject.put("MerchantID", "100");
//		jsonObject.put("KeyIndex", "100");
    }

    private void setParam4doReverse(JSONObject jsonObject) throws JSONException {
        jsonObject.put("AppID", "12498423");
        jsonObject.put("AppName", "TestAccount");
        jsonObject.put("OriginTransType", 1);
        jsonObject.put("OriginTransAmount", "00000010000");
        jsonObject.put("OriginTransIndexCode", "14526855");
        jsonObject.put("ReqTransDate", "140421");
        jsonObject.put("ReqTransTime", "100445");
//		jsonObject.put("cardInfo2", "100");
//		jsonObject.put("cardInfo3", "100");
//		jsonObject.put("TerminalID", "100");
//		jsonObject.put("MerchantID", "100");
//		jsonObject.put("KeyIndex", "100");
    }

    private void setParam4getPOSInfo(JSONObject jsonObject) throws JSONException {
        jsonObject.put("AppID", "50006");
//		jsonObject.put("TrxID", "12498423");
        jsonObject.put("AppName", "TestAccount");
        jsonObject.put("TransType", 3);
        jsonObject.put("ReqTransDate", "140421");
    }

    private void setParam4getPayInfo(JSONObject jsonObject) throws JSONException {
        jsonObject.put("appID", "12498423");
        jsonObject.put("TrxID", "12498423");
        jsonObject.put("appName", "TestAccount");
        jsonObject.put("TransType", 3);
        jsonObject.put("transIndexCode", "14526855");
        jsonObject.put("reqTransDate", "140421");
    }

    private void setParam4login(JSONObject jsonObject) throws JSONException {
        jsonObject.put("AppID", "50006");
        jsonObject.put("AppName", "TestAccount");
        jsonObject.put("OptCode", "01");
        jsonObject.put("OptPass", "0000");
    }

    private void setParam4settle(JSONObject jsonObject) throws JSONException {
        jsonObject.put("AppID", "12498423");
        jsonObject.put("TrxID", "12498423");
        jsonObject.put("AppName", "TestAccount");
        jsonObject.put("TransType", 23);
        jsonObject.put("ReqTransDate", "140421");
    }

    private void setParam4queryBalance(JSONObject jsonObject) throws JSONException {
        jsonObject.put("AppID", "12498423");
        jsonObject.put("TrxID", "12498423");
        jsonObject.put("AppName", "TestAccount");
        jsonObject.put("TransType", 23);
        jsonObject.put("ReqTransDate", "140421");
    }

    private void setParam4consumeCancel(JSONObject jsonObject) throws JSONException {
        jsonObject.put("AppID", "12498423");
        jsonObject.put("TrxID", "12498423");
        jsonObject.put("AppName", "TestAccount");
        jsonObject.put("TransType", 23);
        jsonObject.put("ReqTransDate", "140421");
    }
}
