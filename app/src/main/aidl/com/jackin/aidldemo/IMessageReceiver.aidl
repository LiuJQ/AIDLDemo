// IMessageReceiver.aidl
package com.jackin.aidldemo;

// Declare any non-default types here with import statements
import com.jackin.aidldemo.model.MessageModel;

interface IMessageReceiver {
    void onMessageReceived(in MessageModel msg);
}
