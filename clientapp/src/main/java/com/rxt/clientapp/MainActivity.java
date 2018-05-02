package com.rxt.clientapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rxt.bindersample.aidl.CellPhone;
import com.rxt.bindersample.aidl.ICellPhoneManager;
import com.rxt.bindersample.aidl.IOnNewDataReceivedListner;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "ClientApp.MainActivity";

    @BindView(R.id.btn_bind_service)
    Button btnBindService;
    @BindView(R.id.btn_get_book_list)
    Button btnGetBookList;
    @BindView(R.id.btn_add_book)
    Button btnAddBook;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private CellPhoneAdapter mAdapter;
    private ICellPhoneManager iCellPhoneManager;
    private ClientHandler mHandler;

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iCellPhoneManager = ICellPhoneManager.Stub.asInterface(service);
            try {
                iCellPhoneManager.asBinder().linkToDeath(mDeathRecipient, 0);
                iCellPhoneManager.registerOnNewDataReceivedListner(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Toast.makeText(MainActivity.this, "服务已连接", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iCellPhoneManager = null;
            Toast.makeText(MainActivity.this, "服务已断开", Toast.LENGTH_SHORT).show();
        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            //服务断开
            Log.i(TAG, "binderDied: " + Thread.currentThread().getName());
            if (iCellPhoneManager == null) {
                return;
            }
            iCellPhoneManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            iCellPhoneManager = null;
            //重连
            bindCellPhoneService();
        }
    };

    private IOnNewDataReceivedListner mListener = new IOnNewDataReceivedListner.Stub() {
        @Override
        public void onNewDataReceived(CellPhone phone) throws RemoteException {
            List<CellPhone> dataList = mAdapter.getDataList();
            Message message;
            if (dataList == null) {
                message = mHandler.obtainMessage(0);
            } else {
                dataList.add(phone);
                message = mHandler.obtainMessage(1);
            }
            mHandler.sendMessage(message);
            Log.i(TAG, "onNewDataReceived: " + Thread.currentThread().getName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mHandler = new ClientHandler(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new CellPhoneAdapter();
        recyclerView.setAdapter(mAdapter);

    }

    @OnClick({R.id.btn_bind_service, R.id.btn_get_book_list, R.id.btn_add_book})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_bind_service:
                bindCellPhoneService();
                break;
            case R.id.btn_get_book_list:
                if (iCellPhoneManager != null) {
                    try {
                        mAdapter.setDataList(iCellPhoneManager.getCellPhoneList());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_add_book:
                if (iCellPhoneManager != null) {
                    try {
                        iCellPhoneManager.addCellPhone(new CellPhone("OPPO", 1799));
                        mAdapter.setDataList(iCellPhoneManager.getCellPhoneList());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void bindCellPhoneService() {
        Intent intent = new Intent();
        intent.setAction("com.rxt.bindersample.CellPhoneService");
        intent.setComponent(new ComponentName("com.rxt.bindersample",
                "com.rxt.bindersample.CellPhoneService"));
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (iCellPhoneManager != null && iCellPhoneManager.asBinder().isBinderAlive()) {
            try {
                iCellPhoneManager.unRegisterOnNewDataReceivedListner(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConn);
    }

    private static class ClientHandler extends Handler {
        private WeakReference<MainActivity> weakReference;

        public ClientHandler(MainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity mainActivity = weakReference.get();
            if (msg.what == 0) {
                try {
                    mainActivity.mAdapter.setDataList(mainActivity.iCellPhoneManager.getCellPhoneList());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                mainActivity.mAdapter.notifyItemInserted(mainActivity.mAdapter
                        .getDataList().size() - 1);
                mainActivity.recyclerView.smoothScrollToPosition(mainActivity.mAdapter.getDataList().size() - 1);
            }
        }
    }
}
