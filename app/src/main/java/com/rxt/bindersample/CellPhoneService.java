package com.rxt.bindersample;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.rxt.bindersample.aidl.CellPhone;
import com.rxt.bindersample.aidl.ICellPhoneManager;
import com.rxt.bindersample.aidl.IOnNewDataReceivedListner;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class CellPhoneService extends Service {

    public static final String TAG = "CellPhoneService";

    private CopyOnWriteArrayList<CellPhone> cellPhoneList;
    private AtomicBoolean mIsServiceAlive = new AtomicBoolean(true);
    private RemoteCallbackList<IOnNewDataReceivedListner> mListnerList = new RemoteCallbackList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("服务创建");
        cellPhoneList = new CopyOnWriteArrayList<>();
        cellPhoneList.add(new CellPhone("华为", 2000));
        cellPhoneList.add(new CellPhone("小米", 1499));
        cellPhoneList.add(new CellPhone("vivo", 2499));
        Executors.newSingleThreadExecutor().execute(new UpdateDataInService());
    }

    private IBinder mBinder = new ICellPhoneManager.Stub() {

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            //客户端权限验证
            int permissionVal = checkCallingOrSelfPermission("com.rxt.binder");
            if (permissionVal == PackageManager.PERMISSION_DENIED) {
                //客户端没有声明权限
                Log.i(TAG, "onTransact: " + "客户端没有权限");
                return false;
            }

            //客户端包名验证
            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
            }
            if (packageName != null && !packageName.startsWith("com.rxt")) {
                Log.i(TAG, "onTransact: " + "客户端包名不对");
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public List<CellPhone> getCellPhoneList() throws RemoteException {
            return cellPhoneList;
        }

        @Override
        public void addCellPhone(CellPhone phone) throws RemoteException {
            cellPhoneList.add(phone);
        }

        @Override
        public void registerOnNewDataReceivedListner(IOnNewDataReceivedListner listner) throws RemoteException {
            mListnerList.register(listner);
        }

        @Override
        public void unRegisterOnNewDataReceivedListner(IOnNewDataReceivedListner listner) throws RemoteException {
            mListnerList.unregister(listner);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private class UpdateDataInService implements Runnable {

        @Override
        public void run() {
            while (mIsServiceAlive.get()) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendNewDataToClient();
            }
        }
    }

    private void sendNewDataToClient() {
        CellPhone phone = new CellPhone("iPhone" + (cellPhoneList.size() + 1), 8999);
        cellPhoneList.add(phone);
        int size = mListnerList.beginBroadcast();
        for (int i = 0; i < size; i++) {
            IOnNewDataReceivedListner listner = mListnerList.getBroadcastItem(i);
            try {
                listner.onNewDataReceived(phone);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mListnerList.finishBroadcast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsServiceAlive.set(false);
    }
}
