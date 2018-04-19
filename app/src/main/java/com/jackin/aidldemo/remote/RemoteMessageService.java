package com.jackin.aidldemo.remote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jackin.aidldemo.IMessageReceiver;
import com.jackin.aidldemo.IMessageSender;
import com.jackin.aidldemo.model.MessageModel;

import java.util.concurrent.atomic.AtomicBoolean;

public class RemoteMessageService extends Service {

    private final static String TAG = "RemoteMessageService";
    private AtomicBoolean isServiceRunning = new AtomicBoolean(true);
    // RemoteCallbackList管理多进程回调接口
    private RemoteCallbackList<IMessageReceiver> mMessageReceivers = new RemoteCallbackList<>();

    IBinder mMsgSender = new IMessageSender.Stub() {
        @Override
        public void sendMessage(MessageModel msg) throws RemoteException {
            // 私有进程Service接收到UI进程的消息
            Log.d(TAG, "send msg:\n" + msg.toString());

            // service should push this message to server
            // TODO 把UI进程发送的消息推送到远程服务器
        }

        @Override
        public void registerMessageReceiver(IMessageReceiver msgReceiver) throws RemoteException {
            mMessageReceivers.register(msgReceiver);
        }

        @Override
        public void unRegisterMessageReceiver(IMessageReceiver msgReceiver) throws RemoteException {
            mMessageReceivers.unregister(msgReceiver);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMsgSender;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning.set(true);

        // 启动模拟与服务器的长连接线程
        new Thread(new FakeMsgTCPTask()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMessageReceivers.kill();
        isServiceRunning.set(false);
    }

    private class FakeMsgTCPTask implements Runnable {
        @Override
        public void run() {
            // 模拟长连接接收服务器推送的消息
            while (isServiceRunning.get()) {
                // 5s唤醒一次
                try {
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // TODO 从服务端拉取最新消息

                // 创建消息体
                MessageModel msg = new MessageModel();
                msg.setMsgFrom("Service");
                msg.setMsgTo("Client");
                msg.setMsgTimeStamp(System.currentTimeMillis());
                msg.setMsgContent("Someone say hello to you !");
                // 遍历所有跨进程连接
                final int receiverCount = mMessageReceivers.beginBroadcast();
                Log.d(TAG, receiverCount + " connection totally now");
                for (int index=0; index<receiverCount; index++) {
                    IMessageReceiver receiver = mMessageReceivers.getBroadcastItem(index);
                    if (receiver != null && receiver.asBinder().isBinderAlive()) {
                        try {
                            // 通过跨进程回调接口发送到UI进程
                            receiver.onMessageReceived(msg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "binder died");
                    }
                }
                mMessageReceivers.finishBroadcast();
            }
        }
    }
}
