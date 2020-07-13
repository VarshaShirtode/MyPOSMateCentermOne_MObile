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
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.quagnitia.myposmate.printer.DeviceService;
import com.quagnitia.myposmate.printer.Printer;
import com.quagnitia.myposmate.utils.AppConstants;
import com.quagnitia.myposmate.utils.ConnectivityReceiver;
import com.quagnitia.myposmate.utils.PreferencesManager;
import com.usdk.apiservice.aidl.UDeviceService;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.json.JSONObject;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

import timber.log.Timber;

/**
 * Created by admin on 12/28/2017.
 */

public class MyPOSMateApplication extends Application implements ConnectionListener, ConnectivityReceiver.ConnectivityReceiverListener {
    public AbstractXMPPConnection asbtractConnection;
    private String serverIp = AppConstants.serverIp;
    private Handler handler;
    private PreferencesManager preferencesManager;
    private static MyPOSMateApplication mInstance;
    public static boolean isActiveQrcode = false, isOpen = false;


    private static final String TAG = "ArkeSdkDemoApplication";
    private static final String USDK_ACTION_NAME = "com.usdk.apiservice";
    private static final String USDK_PACKAGE_NAME = "com.usdk.apiservice";
    private static DeviceService deviceService;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = getApplicationContext();
        preferencesManager = PreferencesManager.getInstance(getApplicationContext());
        handler = new Handler();
        if (!preferencesManager.getUsername().equals(""))
            checkAvaliability();
        bindSdkDeviceService();
        Printer.initWebView(context);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }


        final Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if (!preferencesManager.getUsername().equals(""))
                {
                    preferencesManager.setIsConnected(false);
                    preferencesManager.setIsAuthenticated(false);
                }

                if (isNetworkAvailable()) {
                    if(asbtractConnection!=null)
                        if(asbtractConnection.isConnected() && asbtractConnection.isAuthenticated())
                        {
                            asbtractConnection.disconnect();
                        }
                    Intent i = new Intent();
                    i.setAction("NetConnectionOn");
                    sendBroadcast(i);
                    initChat(preferencesManager.getUsername(), preferencesManager.getPassword());
                } else {
                    Intent i = new Intent();
                    i.setAction("NetConnectionOff");
                    sendBroadcast(i);
                }
                handler.postDelayed(this, 60000);
            }
        };
        handler.post(run);



        preferencesManager.setIsConnected(true);
        preferencesManager.setIsAuthenticated(true);
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

                ReconnectionManager manager = ReconnectionManager.getInstanceFor(asbtractConnection);
                manager.enableAutomaticReconnection();
                manager.setEnabledPerDefault(true);


                bindSdkDeviceService();
            }, 0);
        }
    };


    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (isConnected) {
            Intent i = new Intent();
            i.setAction("NetConnectionOn");
            sendBroadcast(i);
            initChat(preferencesManager.getUsername(), preferencesManager.getPassword());
        } else {
            Intent i = new Intent();
            i.setAction("NetConnectionOff");
            sendBroadcast(i);

        }
    }

    //    MyPOSMateApplication.getInstance().setConnectivityListener();
    public void initChat(final String username, final String password) {


// Create a connection to the jabber.org server on a specific port.
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                try {
                    DomainBareJid serviceName = JidCreate.domainBareFrom(serverIp);

                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()

                            .setUsernameAndPassword(username, password)
                            .setHost(serverIp)
                            .setXmppDomain(serviceName)
                            .setPort(5222).setSecurityMode(ConnectionConfiguration.SecurityMode.disabled).allowEmptyOrNullUsernames().setResource("Android")
                            .build();

                    if (asbtractConnection != null) {
                        try
                        {
                            asbtractConnection.disconnect();
                            asbtractConnection = null;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }

                    asbtractConnection = new XMPPTCPConnection(config);


                    asbtractConnection.addConnectionListener(MyPOSMateApplication.this);
                    if (!asbtractConnection.isConnected()) {
                        try
                        {
                            asbtractConnection.connect();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    } else {
                        ReconnectionManager manager = ReconnectionManager.getInstanceFor(asbtractConnection);
                        manager.enableAutomaticReconnection();
                        manager.setEnabledPerDefault(true);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public static synchronized MyPOSMateApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    public void onTerminate() {
        Log.v("MyPOSMate®", "OnTerminate....");
//        asbtractConnection.disconnect();
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

    @Override
    public void connected(XMPPConnection connection) {
        Log.v("MyPOSMate®", "Connected....");
        re_flag = 0;


        try {
            connection.addConnectionListener(MyPOSMateApplication.this);

            preferencesManager.setIsConnected(true);
            Intent i = new Intent();
            i.setAction("Connected");
            sendBroadcast(i);
            if (connection.isConnected() && connection.isAuthenticated() || asbtractConnection.isConnected() && asbtractConnection.isAuthenticated()) {
                Log.v("AlreadyAuthenticated", "");
                re_flag = 0;
                Intent i1 = new Intent();
                i1.setAction("Authenticated");
                sendBroadcast(i1);
                preferencesManager.setIsAuthenticated(true);
            } else {
                SASLAuthentication.unBlacklistSASLMechanism("SCRAM-SHA-1");
                SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                asbtractConnection.login(Localpart.from("" + preferencesManager.getUsername()), "" + preferencesManager.getPassword());
            }


        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.v("MyPOSMate®", "Authenticated....");

        re_flag = 0;
        Intent i = new Intent();
        i.setAction("Authenticated");
        sendBroadcast(i);
        preferencesManager.setIsAuthenticated(true);
        createChatRoom(connection);


    }

    Chat newChat;
    private ChatManager chatManager;

    public void createChatRoom(XMPPConnection connection) {

        try {
            chatManager = ChatManager.getInstanceFor(asbtractConnection);
            EntityJid jid = JidCreate.entityBareFrom(JidCreate.from(preferencesManager.getUsername() + "@" + serverIp));
            newChat = chatManager.createChat(jid);
            chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addChatListener(
                    new ChatManagerListener() {


                        @Override
                        public void chatCreated(Chat chat, boolean createdLocally) {
                            chat.addMessageListener(new ChatStateListener() {
                                @Override
                                public void stateChanged(Chat chat, ChatState state, Message message) {

                                }

                                @Override
                                public void processMessage(Chat chat, final Message message) {
                                    if (message.getBody() != null) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
//                                                if(!OkHttpHandler.isWebserviceRunning)
//                                                //if none of the api is running then allow the trigger
//                                                // otherwise avoid it
//                                                {
                                                if (!isActiveQrcode) {
                                                    try {
                                                        JSONObject jsonObject1 = new JSONObject(message.getBody().toString());
                                                        if (!jsonObject1.has("channel") || jsonObject1.optString("channel").equalsIgnoreCase("DPS") || jsonObject1.optString("channel").equalsIgnoreCase("null")) {
                                                            isOpen = false;
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                    if (isOpen) {
                                                        //donot open the screen
                                                    } else {
                                                        try {AppConstants.xmppamountforscan = "";
                                                            JSONObject jsonObject = new JSONObject(message.getBody());


                                                            if(jsonObject.has("request_type"))
                                                            {
                                                                if(jsonObject.optString("request_type").equalsIgnoreCase("REFUND"))
                                                                {
                                                                    Intent i1 = new Intent();
                                                                    i1.setAction("ThirdParty");
                                                                    i1.putExtra("data", message.getBody().toString());
                                                                    sendBroadcast(i1);
                                                                    return;
                                                                }
                                                                else if(jsonObject.optString("request_type").equalsIgnoreCase("PRINT"))
                                                                {
                                                                    Intent i1 = new Intent();
                                                                    i1.setAction("PrintTrigger");
                                                                    i1.putExtra("data", message.getBody().toString());
                                                                    sendBroadcast(i1);
                                                                    return;
                                                                }
                                                            }


                                                            switch (jsonObject.optString("cmd")) {
                                                                case "PAYMENT_AMOUNT":
                                                                    if (jsonObject.optString("amount").equals("0.00")) {
                                                                        Toast.makeText(MyPOSMateApplication.this, "Amount triggered is zero.Please enter the amount greater than 0", Toast.LENGTH_LONG).show();
                                                                    } else if (!preferencesManager.isUnipaySelected() &&
                                                                            !preferencesManager.isaggregated_singleqr()) {
                                                                        Toast.makeText(MyPOSMateApplication.this, "Please select the payment option", Toast.LENGTH_LONG).show();
                                                                    }
//                                                                    else if(preferencesManager.isUnipaySelected())
//                                                                    {
//                                                                        Toast.makeText(MyPOSMateApplication.this, "Union pay payment method selected", Toast.LENGTH_LONG).show();
//                                                                    }
//                                                                    else if(!preferencesManager.isaggregated_singleqr() && !preferencesManager.isUnipaySelected())
//                                                                    {
//                                                                        Toast.makeText(MyPOSMateApplication.this, "Please select the payment method", Toast.LENGTH_LONG).show();
//                                                                    }
                                                                    else {
                                                                        Intent i1 = new Intent();
                                                                        i1.setAction("ManualEntry");
                                                                        i1.putExtra("data", message.getBody().toString());
                                                                        sendBroadcast(i1);
                                                                    }

                                                                    break;
                                                                case "PAYMENT_DETAILS":
                                                                    Intent i2 = new Intent();
                                                                    JSONObject jsonObject1 = new JSONObject(message.getBody().toString());
                                                                    if (jsonObject1.optString("channel").equalsIgnoreCase("DPS")) {
                                                                        if (jsonObject.optString("status").equals("TRADE_SUCCESS") ||
                                                                                jsonObject.optString("status").equals("0") ||
                                                                                jsonObject.optString("status").equals("TRADE_HAS_SUCCESS")
                                                                                || jsonObject.optString("status").equalsIgnoreCase("true")
                                                                        ) {
                                                                            i2.setAction("PaymentExpress");
                                                                            i2.putExtra("data", message.getBody().toString());
                                                                            sendBroadcast(i2);
                                                                        } else {
                                                                            i2.setAction("PaymentExpress");
                                                                            i2.putExtra("data", message.getBody().toString());
                                                                            sendBroadcast(i2);
                                                                        }

                                                                    } else {
                                                                        i2.setAction("PaymentProcessing");
                                                                        i2.putExtra("data", message.getBody().toString());
                                                                        sendBroadcast(i2);
                                                                    }

                                                                    break;
                                                            }






                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                    }

                                                } else {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(message.getBody());
                                                        switch (jsonObject.optString("cmd")) {

                                                            case "PAYMENT_DETAILS":
                                                                Intent i2 = new Intent();
                                                                i2.setAction("PaymentProcessing");
                                                                i2.putExtra("data", message.getBody().toString());
                                                                sendBroadcast(i2);
                                                                break;
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }


                                                }

                                                //  }

                                            }
                                        });
                                    }
                                }
                            });

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void checkAvaliability() {
        preferencesManager.setIsConnected(false);
        preferencesManager.setIsAuthenticated(false);
        if (isNetworkAvailable()) {
            Intent i = new Intent();
            i.setAction("NetConnectionOn");
            sendBroadcast(i);
//            initChat(preferencesManager.getUsername(), preferencesManager.getPassword());
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

    @Override
    public void connectionClosed() {
        Log.v("MyPOSMate®", "ConnectionClosed....");
        //   Toast.makeText(getApplicationContext(), "Connection is closed", Toast.LENGTH_LONG).show();
        Intent i = new Intent();
        i.setAction("ConnectionClosed");
        sendBroadcast(i);
        preferencesManager.setIsConnected(false);
        preferencesManager.setIsAuthenticated(false);
        if (asbtractConnection.isConnected()) {
            asbtractConnection.disconnect();
        }

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.v("MyPOSMate®", "ConnectionClosedOnError....");
        Looper.prepare();
        Toast.makeText(getApplicationContext(), "Connection is closed due to no network connection.Please make sure your net connection is on", Toast.LENGTH_LONG).show();
        Intent i = new Intent();
        i.setAction("ConnectionClosedOnError");
        sendBroadcast(i);
        preferencesManager.setIsConnected(false);
        preferencesManager.setIsAuthenticated(false);
        if (asbtractConnection.isConnected()) {
            asbtractConnection.disconnect();

        }

    }

    @Override
    public void reconnectionSuccessful() {

        Log.v("MyPOSMate®", "ReconnectionSuccessful....");
    }

    int re_flag = 0;

    @Override
    public void reconnectingIn(int seconds) {
        Log.v("MyPOSMate®", "Reconnecting....");
        if (re_flag == 0) {
            re_flag = 1;
            Looper.prepare();
            Toast.makeText(getApplicationContext(), "Trying to reconnect", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.v("MyPOSMate®", "ReconnectionFailed....");
        preferencesManager.setIsConnected(false);
        preferencesManager.setIsAuthenticated(false);
        if (asbtractConnection.isConnected()) {
            asbtractConnection.disconnect();
        }

    }


}
