package com.quagnitia.myposmate;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quagnitia.myposmate.printer.DeviceService;
import com.quagnitia.myposmate.printer.Printer;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.ConnectivityReceiver;
import com.quagnitia.myposmate.utils.OkHttpHandler;
import com.quagnitia.myposmate.utils.OnTaskCompleted;
import com.quagnitia.myposmate.utils.PreferencesManager;
import com.usdk.apiservice.aidl.UDeviceService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

/**
 * Created by admin on 12/28/2017.
 */

public class MyPOSMateApplication extends Application implements OnTaskCompleted {//ConnectionListener
    private Handler handler;
    private PreferencesManager preferencesManager;
    private static MyPOSMateApplication mInstance;
    public static boolean isActiveQrcode = false, isOpen = false;


    private static final String TAG = "ArkeSdkDemoApplication";
    private static final String USDK_ACTION_NAME = "com.usdk.apiservice";
    private static final String USDK_PACKAGE_NAME = "com.usdk.apiservice";
    private static DeviceService deviceService;
    private static Context context;

    public static StompClient mStompClient;
    private Disposable mRestPingDisposable;
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private Gson mGson = new GsonBuilder().create();

    private CompositeDisposable compositeDisposable;
    public static final String LOGIN = "login";
    public static final String PASSCODE = "passcode";


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = getApplicationContext();
        preferencesManager = PreferencesManager.getInstance(getApplicationContext());
        handler = new Handler();
//        if (!preferencesManager.getUsername().equals(""))
//            checkAvaliability();
        bindSdkDeviceService();
        Printer.initWebView(context);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

      /*  final Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
//                if (!preferencesManager.getUsername().equals(""))
//                {
//                    preferencesManager.setIsConnected(false);
//                    preferencesManager.setIsAuthenticated(false);
//                }

                if (isNetworkAvailable()) {
                    Intent i = new Intent();
                    i.setAction("NetConnectionOn");
                    sendBroadcast(i);
                    isNetConnectionOn = true;
                    callAuthToken();
//                    initiateStompConnection();

                  //  initChat(preferencesManager.getUsername(), preferencesManager.getPassword());
                } else {
                    Intent i = new Intent();
                    i.setAction("NetConnectionOff");
                    sendBroadcast(i);
                }
                handler.postDelayed(this, 60000);
            }
        };
        handler.post(run);*/

    }

    public void callAuthToken() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("grant_type", "client_credentials");
        new OkHttpHandler(getApplicationContext(), this, hashMap, "AuthToken").execute(AppConstants.AUTH);
    }


    public void initiateStompConnection(String access_token) {
        if (mStompClient == null) {
            mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "wss://" + AppConstants.serverIp
                    + "/websocket?access_token=" + access_token);

            resetSubscriptions();
            connectStomp();
        } else {
            if (mStompClient.isConnected()) {
                Log.v("MyPOSMate", "Connection is already established");
                Intent i1 = new Intent();
                i1.setAction("Authenticated");
                sendBroadcast(i1);
                preferencesManager.setIsAuthenticated(true);
            }
        }

    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    public void connectStomp() {
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader(LOGIN, "guest"));
        headers.add(new StompHeader(PASSCODE, "guest"));
        mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);
        resetSubscriptions();

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            preferencesManager.setIsConnected(true);
                            preferencesManager.setIsAuthenticated(true);
                            Log.v("MyPOSMate®", "Connected....");
                            toast("Stomp connection opened");
                            Intent i = new Intent();
                            i.setAction("Connected");
                            sendBroadcast(i);
                            onSubscribe();
                            break;
                        case ERROR:
                            preferencesManager.setIsConnected(false);
                            preferencesManager.setIsAuthenticated(false);
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            toast("Stomp connection error");
                            Log.v("MyPOSMate®", "ConnectionClosedOnError....");
                            toast("Connection is closed due to no network connection.Please make sure your net connection is on");
                            Intent i2 = new Intent();
                            i2.setAction("ConnectionClosedOnError");
                            sendBroadcast(i2);
                            break;
                        case CLOSED:
                            preferencesManager.setIsConnected(false);
                            preferencesManager.setIsAuthenticated(false);
                            toast("Stomp connection closed");
                            Log.v("MyPOSMate®", "ConnectionClosed....");
                            Intent i1 = new Intent();
                            i1.setAction("ConnectionClosed");
                            sendBroadcast(i1);
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            preferencesManager.setIsConnected(false);
                            preferencesManager.setIsAuthenticated(false);
                            toast("Stomp failed server heartbeat");
                            break;

                    }
                });

        compositeDisposable.add(dispLifecycle);
        mStompClient.connect(headers);
    }

    private String generateTopic(String branch_id, String config_id, String terminal_id, String access_id) {
        return "/queue/" + branch_id + config_id + terminal_id + access_id;
    }

    void onSubscribe() {

        Disposable dispTopic = mStompClient.topic(
                generateTopic(preferencesManager.getMerchantId(),
                        preferencesManager.getConfigId(),
                        preferencesManager.getterminalId(),
                        preferencesManager.getuniqueId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                    callAuthToken();
                    funcTrigger(topicMessage);
                    // addItem(mGson.fromJson(topicMessage.getPayload(), EchoModel.class));
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe top" +
                            "ic", throwable);
                });

        compositeDisposable.add(dispTopic);

    }

    private void toast(String text) {
        Log.i(TAG, text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public void funcTrigger(StompMessage message) {
        Runnable runnable = () -> {
            Intent intent = new Intent();
            if (!isActiveQrcode) {
                try {
                    JSONObject jsonObject1 = new JSONObject(message.getPayload());
                    if (!jsonObject1.has("channel") || jsonObject1.optString("channel").equalsIgnoreCase("DPS") || jsonObject1.optString("channel").equalsIgnoreCase("null")) {
                        isOpen = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (isOpen) {
                    //donot open the screen
                } else {
                    try {
                        AppConstants.xmppamountforscan = "";
                        JSONObject jsonObject = new JSONObject(message.getPayload());


                        if (jsonObject.has("request_type")) {

                            switch (jsonObject.optString("request_type")) {
                                case "PAY":
                                    if (jsonObject.optString("amount").equals("0.00")) {
                                        Toast.makeText(MyPOSMateApplication.this, "Amount triggered is zero.Please enter the amount greater than 0", Toast.LENGTH_LONG).show();
                                    } else if (!preferencesManager.isUnipaySelected() &&
                                            !preferencesManager.isaggregated_singleqr()) {
                                        Toast.makeText(MyPOSMateApplication.this, "Please select the payment option", Toast.LENGTH_LONG).show();
                                    } else {
                                        intent.setAction("ManualEntry");
                                        intent.putExtra("data", message.getPayload().toString());
                                        sendBroadcast(intent);
                                    }

                                    break;
                                case "PAY_DETAILS":
                                    JSONObject jsonObject1 = new JSONObject(message.getPayload().toString());
                                    if (jsonObject1.optString("channel").equalsIgnoreCase("DPS")) {
                                        if (jsonObject.optString("status").equals("TRADE_SUCCESS") ||
                                                jsonObject.optString("status").equals("0") ||
                                                jsonObject.optString("status").equals("TRADE_HAS_SUCCESS")
                                                || jsonObject.optString("status").equalsIgnoreCase("true")
                                        ) {
                                            intent.setAction("PaymentExpress");
                                            intent.putExtra("data", message.getPayload());
                                            sendBroadcast(intent);
                                        } else {
                                            intent.setAction("PaymentExpress");
                                            intent.putExtra("data", message.getPayload());
                                            sendBroadcast(intent);
                                        }

                                    } else {
                                        intent.setAction("PaymentProcessing");
                                        intent.putExtra("data", message.getPayload());
                                        sendBroadcast(intent);
                                    }

                                    break;

                                case "REFUND":
                                    intent.setAction("ThirdParty");
                                    intent.putExtra("data", message.getPayload());
                                    sendBroadcast(intent);
                                    break;

                                case "PRINT":
                                    intent.setAction("PrintTrigger");
                                    intent.putExtra("data", message.getPayload());
                                    sendBroadcast(intent);
                                    break;
                                case "NEW_ORDER":
                                    intent.setAction("OrderDetails");
                                    intent.putExtra("data", message.getPayload());
                                    sendBroadcast(intent);
                                    break;


                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            } else {
                try {
                    JSONObject jsonObject = new JSONObject(message.getPayload());
                    switch (jsonObject.optString("request_type")) {

                        case "PAY_DETAILS":
                            intent.setAction("PaymentProcessing");
                            intent.putExtra("data", message.getPayload());
                            sendBroadcast(intent);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        };
        if (message.getPayload() != null) {
            handler.post(runnable);
        }
    }


    /**
     * Get context.
     */
    public static Context getContext() {
        if (context == null) {
            throw new RuntimeException("Initiate context failed");
        }

        return context;
    }

    public void onDestroyApp() {
        mStompClient.disconnect();

        if (mRestPingDisposable != null) mRestPingDisposable.dispose();
        if (compositeDisposable != null) compositeDisposable.dispose();
    }


    /**
     * Get device service instance.
     */
    public static DeviceService getDeviceService() {
        if (deviceService == null) {
            throw new RuntimeException("SDK service is still not connected.");
        }

        return deviceService;
    }

    /**
     * Bind sdk service.
     */
    private void bindSdkDeviceService() {
        Intent intent = new Intent();
        intent.setAction(USDK_ACTION_NAME);
        intent.setPackage(USDK_PACKAGE_NAME);

        Log.d(TAG, "binding sdk device service...");
        boolean flag = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!flag) {
            Log.d(TAG, "SDK service binding failed.");
            return;
        }

        Log.d(TAG, "SDK service binding successfully.");
    }

    /**
     * Service connection.
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "SDK service disconnected.");
            deviceService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "SDK service connected.");

            try {
                deviceService = new DeviceService(UDeviceService.Stub.asInterface(service));
                deviceService.register();
                deviceService.debugLog(true, true);
                Log.d(TAG, "SDK deviceService initiated version:" + deviceService.getVersion() + ".");
            } catch (RemoteException e) {
                throw new RuntimeException("SDK deviceService initiating failed.", e);
            }

            try {
                linkToDeath(service);
            } catch (RemoteException e) {
                throw new RuntimeException("SDK service link to death error.", e);
            }
        }

        private void linkToDeath(IBinder service) throws RemoteException {
            service.linkToDeath(() -> {
                Log.d(TAG, "SDK service is dead. Reconnecting...");

//                ReconnectionManager manager = ReconnectionManager.getInstanceFor(asbtractConnection);
//                manager.enableAutomaticReconnection();
//                manager.setEnabledPerDefault(true);


                bindSdkDeviceService();
            }, 0);
        }
    };


    /**
     * Callback will be triggered when there is change in
     * network connection
     */



    public static synchronized MyPOSMateApplication getInstance() {

        return mInstance;
    }


    @Override
    public void onTerminate() {
        Log.v("MyPOSMate®", "OnTerminate....");
        onDestroyApp();
        preferencesManager.setIsConnected(false);
        preferencesManager.setIsAuthenticated(false);
        try {
            deviceService.unregister();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(serviceConnection);
        System.exit(0);
        super.onTerminate();
    }


    public void checkAvaliability() {
        preferencesManager.setIsConnected(false);
        preferencesManager.setIsAuthenticated(false);
        if (isNetworkAvailable()) {
            Intent i = new Intent();
            i.setAction("NetConnectionOn");
            sendBroadcast(i);
        } else {
            Intent i = new Intent();
            i.setAction("NetConnectionOff");
            sendBroadcast(i);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    boolean isNetConnectionOn;

    @Override
    public void onTaskCompleted(String result, String TAG) throws Exception {
        if (result.equals("")) {
            // Toast.makeText(DashboardActivity.this, "No data from server.", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject jsonObject = new JSONObject(result);
        switch (TAG) {
            case "AuthToken":
                if (jsonObject.has("access_token") && !jsonObject.optString("access_token").equals("")) {
                    preferencesManager.setauthToken(jsonObject.optString("access_token"));
                }

                if (isNetConnectionOn) {
                    isNetConnectionOn = false;
                    Log.v("Dashboard", "MyPOSMateApplication Called connection");
                    ((MyPOSMateApplication) this.getApplicationContext()).initiateStompConnection(preferencesManager.getauthToken());

                }

                break;
        }
    }
}
