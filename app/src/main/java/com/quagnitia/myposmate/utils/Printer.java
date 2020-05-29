package com.quagnitia.myposmate.utils;


import com.centerm.smartpos.aidl.sys.AidlDeviceManager;

public interface Printer {

     void onDeviceConnected(AidlDeviceManager deviceManager)  throws Exception;
}