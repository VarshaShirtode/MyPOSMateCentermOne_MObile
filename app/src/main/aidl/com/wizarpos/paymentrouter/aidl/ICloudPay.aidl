package com.wizarpos.paymentrouter.aidl;
interface ICloudPay{
	String payCash(String jsonData);
	String getPOSInfo(String jsonData);
	String login(String jsonData);
	String transact(String jsonData);
	String initKey(String jsonData);
	String settle(String jsonData);
	String printLast(String jsonData);

	String doReverse(String jsonData);		// 冲正
	String consumeCancel(String jsonData);	// 消费撤销
	String balanceQuery(String jsonData);	// 余额查询
	String refund(String jsonData);			// 退货
}