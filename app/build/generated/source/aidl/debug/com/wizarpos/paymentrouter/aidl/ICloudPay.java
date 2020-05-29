/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.wizarpos.paymentrouter.aidl;
public interface ICloudPay extends android.os.IInterface
{
  /** Default implementation for ICloudPay. */
  public static class Default implements com.wizarpos.paymentrouter.aidl.ICloudPay
  {
    @Override public java.lang.String payCash(java.lang.String jsonData) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String getPOSInfo(java.lang.String jsonData) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String login(java.lang.String jsonData) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String transact(java.lang.String jsonData) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String initKey(java.lang.String jsonData) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String settle(java.lang.String jsonData) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String printLast(java.lang.String jsonData) throws android.os.RemoteException
    {
      return null;
    }
    @Override public java.lang.String doReverse(java.lang.String jsonData) throws android.os.RemoteException
    {
      return null;
    }
    // 冲正

    @Override public java.lang.String consumeCancel(java.lang.String jsonData) throws android.os.RemoteException
    {
      return null;
    }
    // 消费撤销

    @Override public java.lang.String balanceQuery(java.lang.String jsonData) throws android.os.RemoteException
    {
      return null;
    }
    // 余额查询

    @Override public java.lang.String refund(java.lang.String jsonData) throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.wizarpos.paymentrouter.aidl.ICloudPay
  {
    private static final java.lang.String DESCRIPTOR = "com.wizarpos.paymentrouter.aidl.ICloudPay";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.wizarpos.paymentrouter.aidl.ICloudPay interface,
     * generating a proxy if needed.
     */
    public static com.wizarpos.paymentrouter.aidl.ICloudPay asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.wizarpos.paymentrouter.aidl.ICloudPay))) {
        return ((com.wizarpos.paymentrouter.aidl.ICloudPay)iin);
      }
      return new com.wizarpos.paymentrouter.aidl.ICloudPay.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_payCash:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.payCash(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_getPOSInfo:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.getPOSInfo(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_login:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.login(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_transact:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.transact(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_initKey:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.initKey(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_settle:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.settle(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_printLast:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.printLast(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_doReverse:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.doReverse(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_consumeCancel:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.consumeCancel(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_balanceQuery:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.balanceQuery(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_refund:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _result = this.refund(_arg0);
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.wizarpos.paymentrouter.aidl.ICloudPay
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public java.lang.String payCash(java.lang.String jsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(jsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_payCash, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().payCash(jsonData);
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String getPOSInfo(java.lang.String jsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(jsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPOSInfo, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getPOSInfo(jsonData);
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String login(java.lang.String jsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(jsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_login, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().login(jsonData);
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String transact(java.lang.String jsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(jsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_transact, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().transact(jsonData);
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String initKey(java.lang.String jsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(jsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_initKey, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().initKey(jsonData);
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String settle(java.lang.String jsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(jsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_settle, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().settle(jsonData);
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String printLast(java.lang.String jsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(jsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_printLast, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().printLast(jsonData);
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public java.lang.String doReverse(java.lang.String jsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(jsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_doReverse, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().doReverse(jsonData);
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // 冲正

      @Override public java.lang.String consumeCancel(java.lang.String jsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(jsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_consumeCancel, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().consumeCancel(jsonData);
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // 消费撤销

      @Override public java.lang.String balanceQuery(java.lang.String jsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(jsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_balanceQuery, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().balanceQuery(jsonData);
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      // 余额查询

      @Override public java.lang.String refund(java.lang.String jsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(jsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_refund, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().refund(jsonData);
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static com.wizarpos.paymentrouter.aidl.ICloudPay sDefaultImpl;
    }
    static final int TRANSACTION_payCash = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getPOSInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_login = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_transact = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_initKey = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_settle = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_printLast = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_doReverse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_consumeCancel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_balanceQuery = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_refund = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    public static boolean setDefaultImpl(com.wizarpos.paymentrouter.aidl.ICloudPay impl) {
      if (Stub.Proxy.sDefaultImpl == null && impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.wizarpos.paymentrouter.aidl.ICloudPay getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public java.lang.String payCash(java.lang.String jsonData) throws android.os.RemoteException;
  public java.lang.String getPOSInfo(java.lang.String jsonData) throws android.os.RemoteException;
  public java.lang.String login(java.lang.String jsonData) throws android.os.RemoteException;
  public java.lang.String transact(java.lang.String jsonData) throws android.os.RemoteException;
  public java.lang.String initKey(java.lang.String jsonData) throws android.os.RemoteException;
  public java.lang.String settle(java.lang.String jsonData) throws android.os.RemoteException;
  public java.lang.String printLast(java.lang.String jsonData) throws android.os.RemoteException;
  public java.lang.String doReverse(java.lang.String jsonData) throws android.os.RemoteException;
  // 冲正

  public java.lang.String consumeCancel(java.lang.String jsonData) throws android.os.RemoteException;
  // 消费撤销

  public java.lang.String balanceQuery(java.lang.String jsonData) throws android.os.RemoteException;
  // 余额查询

  public java.lang.String refund(java.lang.String jsonData) throws android.os.RemoteException;
}
