package com.jackin.aidldemo.remote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jackin.aidldemo.IMessageSender;
import com.jackin.aidldemo.model.MessageModel;

public class RemoteMessageService extends Service {

    private final static String TAG = "RemoteMessageService";

    IBinder mMsgSender = new IMessageSender.Stub() {
        @Override
        public void sendMessage(MessageModel msg) throws RemoteException {
            Log.d(TAG, "send msg:\n" + msg.toString());
            // service should push this message to server.
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMsgSender;
    }

}
