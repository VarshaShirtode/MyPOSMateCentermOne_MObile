package com.quagnitia.myposmate.arke;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.arke.vas.IVASInterface;
import com.arke.vas.IVASListener;
import com.arke.vas.data.VASPayload;
import com.quagnitia.myposmate.R;
import com.quagnitia.myposmate.activities.DashboardActivity;
import com.quagnitia.myposmate.utils.OnTaskCompleted;

import org.json.JSONObject;

public class VASCallsArkeBusiness {
    private static String TAG = "VASCallsArkeBusiness";

    // The Java class generated by the AIDL file
    // 由AIDL文件生成的Java类
    private static IVASInterface vasInterface = null;
    private Context context;

    // Server connection status, false for unconnected, true for connection
    // 服务端连接状况，false为未连接，true为连接中
    private boolean boundStatus = false;

    public VASCallsArkeBusiness(Context context) {
        this.context = context;
    }

    /**
     * Do transaction
     * 做交易
     *
     * @param interfaceId
     */
     OnTaskCompleted listener1;
    public void doTransaction(final String interfaceId, JSONObject jsonObject,OnTaskCompleted listener1) {
        this.listener1=listener1;
        new Thread() {
            @Override
            public void run() {
                try {

                    Log.i(TAG, "[" + interfaceId + "]");
                    bindServiceUntilConnect();

                    String sendData = jsonObject.toString();//((DashboardActivity) context).getRequestData();
                    if (TransactionNames.SIGNIN.name().equals(interfaceId)) {
                        vasInterface.signIn(listener);
                    } else if (interfaceId.equals("SETTLE")) {
                        vasInterface.settle(listener);
                    }
                    else if (interfaceId.equals("COUPON_SALE")){//(TransactionNames.SALE.name().equals(interfaceId)) {
                        vasInterface.uplanSale(new VASPayload(sendData), listener);
                    }
                    else if (interfaceId.equals("COUPON_VOID")){//(TransactionNames.SALE.name().equals(interfaceId)) {
                        vasInterface.uplanVoided(new VASPayload(sendData), listener);
                    }
                    else if (interfaceId.equals("SALE")){//(TransactionNames.SALE.name().equals(interfaceId)) {
                        vasInterface.sale(new VASPayload(sendData), listener);
                    } else if (interfaceId.equals("VOID")) {
                        vasInterface.voided(new VASPayload(sendData), listener);
                    } else if (TransactionNames.PRINT_TRANSACTION_SUMMARY.name().equals(interfaceId)) {
                        vasInterface.printTransactionSummary(listener);
                    } else if (TransactionNames.PRINT_TRANSACTION_DETAIL.name().equals(interfaceId)) {
                        vasInterface.printTransactionDetail(listener);
                    } else if (TransactionNames.TERMINAL_KEY_MANAGEMENT.name().equals(interfaceId)) {
                        vasInterface.terminalKeyManagement(listener);
                    } else if (TransactionNames.REFUND.name().equals(interfaceId)) {
                        vasInterface.refund(new VASPayload(sendData), listener);
                    } else if (TransactionNames.DOWNLOAD_PUBLIC_KEYS.name().equals(interfaceId)) {
                        vasInterface.downloadPublicKeys(listener);
                    } else if (TransactionNames.DOWNLOAD_IC_CARD_PARAMETERS.name().equals(interfaceId)) {
                        vasInterface.downloadICCardParameters(listener);
                    } else if (TransactionNames.DOWNLOAD_QPS_PARAMETER.name().equals(interfaceId)) {
                        vasInterface.downloadQPSParameter(listener);
                    } else if (TransactionNames.DOWNLOAD_CARD_BIN_B.name().equals(interfaceId)) {
                        vasInterface.downloadCardBinB(listener);
                    } else if (TransactionNames.DOWNLOAD_CARD_BIN_BLACKLIST.name().equals(interfaceId)) {
                        vasInterface.downloadCardBINBlacklist(listener);
                    } else if (TransactionNames.BALANCE_QUERY.name().equals(interfaceId)) {
                        vasInterface.balance(listener);
                    } else if (TransactionNames.BALANCE_QUERY_OF_ECASH.name().equals(interfaceId)) {
                        vasInterface.ecashBalanceQuery(listener);
                    } else if (TransactionNames.PRINT_LAST.name().equals(interfaceId)) {
                        vasInterface.printLast(listener);
                    } else if (TransactionNames.PRE_AUTHORIZATION.name().equals(interfaceId)) {
                        vasInterface.preAuthorization(new VASPayload(sendData), listener);
                    } else if (TransactionNames.PRE_AUTH_VOID.name().equals(interfaceId)) {
                        vasInterface.preAuthorizationVoid(new VASPayload(sendData), listener);
                    } else if (TransactionNames.PRE_AUTH_COMPLETION_ADVICE.name().equals(interfaceId)) {
                        vasInterface.preAuthorizationCompletionAdvice(new VASPayload(sendData), listener);
                    } else if (TransactionNames.PRE_AUTH_COMPLETION_REQUEST.name().equals(interfaceId)) {
                        vasInterface.preAuthorizationCompletionRequest(new VASPayload(sendData), listener);
                    } else if (TransactionNames.PRE_AUTH_COMPLETION_VOID.name().equals(interfaceId)) {
                        vasInterface.preAuthorizationCompletionVoid(new VASPayload(sendData), listener);
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Obtain transaction records based on value-added service order number
     * <p>
     * 根据增值服务流水获取交易记录
     *
     * @param orderNumber
     */
    public void getTransactionRecord(final String orderNumber) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "getTransactionRecord");

                    bindServiceUntilConnect();

                    vasInterface.orderNumberQuery(new VASPayload("{\"orderNumber\":\"" + (orderNumber == null ? "" : orderNumber) + "\"}"), listener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Connect to the server
     * <p>
     * 与服务端建立连接
     */
    private void bindService() {
        Intent intent = new Intent();
        intent.setAction("com.arke.vas.service");
        intent.setPackage("com.arke.hk_dp");
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    /**
     * Connect the service until the connection is successful
     * <p>
     * 连接服务，直到连接成功
     */
    private void bindServiceUntilConnect() {
        try {
            Log.i(TAG, context.getString(R.string.unbound_service_binding_service));
            bindService();

            while (vasInterface == null) {
                Thread.sleep(300);
            }
            Log.i(TAG, context.getString(R.string.connect_server_successfully));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Disconnect
     * <p>
     * 断开连接
     *
     * @param responseData
     */
    private void disconnect(String... responseData) {
        Log.i(TAG, "disconnect()");
        if (responseData != null && responseData.length > 0) {
            Log.i(TAG, "onComplete,transaction end, responseData:" + responseData[0]);
            try
            {
                Log.v("Response: Arke",responseData[0]);

                JSONObject jsonObject = new JSONObject(responseData[0]);

                        if(jsonObject.has("responseCodeThirtyNine"))
                        {
                            if(jsonObject.has("responseCodeThirtyNine") && jsonObject.optString("responseCodeThirtyNine").equals("00"))
                            {
                                if (Looper.myLooper() == null)
                                {
                                    Looper.prepare();
                                }
                                if(jsonObject.has("transactionType")&&jsonObject.optString("transactionType").equals("SALE"))
                                {
                                    Toast.makeText(context, "Transaction Successful", Toast.LENGTH_LONG).show();
                                    ((DashboardActivity)context).callSetupFragment(DashboardActivity.SCREENS.MANUALENTRY,null);
                                }

                            }
                        }

                listener1.onTaskCompleted(responseData[0],"Arke");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        context.unbindService(serviceConnection);
        vasInterface = null;
        boundStatus = false;
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "服务连接成功 onServiceConnected");
            vasInterface = IVASInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            vasInterface = null;
        }
    };

    private IVASListener listener = new IVASListener.Stub() {
        @Override
        public void onStart() throws RemoteException {
            Log.i(TAG, "onStart,begin transaction");
        }

        @Override
        public void onNext(VASPayload vasPayload) throws RemoteException {
            Log.i(TAG, " onNext,Transaction process information：" + vasPayload.toString());
        }


        @Override
        public void onComplete(VASPayload responseData) throws RemoteException {
            Log.i(TAG, " onComplete,Information returned at the end of the transaction：" + responseData);
            disconnect(responseData == null ? "" : responseData.getBody());
        }
    };
}
