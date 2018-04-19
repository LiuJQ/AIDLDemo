package com.jackin.aidldemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jackin.aidldemo.model.MessageModel;
import com.jackin.aidldemo.remote.RemoteMessageService;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private TextView mMsgTextView;

    private IMessageSender mMessageSender;
    private boolean mRemoteServiceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMsgTextView = findViewById(R.id.msg);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Send message to remote service", Snackbar.LENGTH_LONG)
                        .setAction("Send", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mRemoteServiceBound && mMessageSender != null) {
                                    // 创建消息体
                                    MessageModel msg = new MessageModel();
                                    msg.setMsgTimeStamp(System.currentTimeMillis());
                                    msg.setMsgFrom("Client");
                                    msg.setMsgTo("Service");
                                    msg.setMsgContent("What a good day today !");
                                    try {
                                        // 使用AIDL接口跨进程发送消息到Service
                                        mMessageSender.sendMessage(msg);
                                    } catch (RemoteException e) {
                                        Log.e(TAG, "send message failed");
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e(TAG, "remote service disconnected.");
                                }
                            }
                        }).show();
            }
        });

        bindRemoteService();
    }

    IMessageReceiver mMsgReceiver = new IMessageReceiver.Stub() {
        @Override
        public void onMessageReceived(final MessageModel msg) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 展示服务端推送的消息
                    mMsgTextView.setText(msg.toString());
                }
            });
        }
    };

    ServiceConnection mRemoteServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "connected to remote service");
            mRemoteServiceBound = true;
            // 通过编译器自动生成的IMessageSender.java源码Stub内部类，转换得到AIDL定义的接口
            mMessageSender = IMessageSender.Stub.asInterface(service);
            try {
                // 注册消息接收回调接口
                mMessageSender.registerMessageReceiver(mMsgReceiver);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "disconnected from remote service");
            mRemoteServiceBound = false;
        }
    };

    private void bindRemoteService() {
        Intent intent = new Intent(this, RemoteMessageService.class);
        bindService(intent, mRemoteServiceConnection, BIND_AUTO_CREATE);
        // 实现UI进程退出后依然接收服务器推送的消息
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRemoteServiceBound) {
            unbindService(mRemoteServiceConnection);
            mRemoteServiceBound = false;
        }
        // 反注册接收消息回调接口
        if (mMessageSender != null && mMessageSender.asBinder().isBinderAlive()) {
            try {
                mMessageSender.unRegisterMessageReceiver(mMsgReceiver);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
