// IMessageSender.aidl
package com.jackin.aidldemo;

// Declare any non-default types here with import statements
import com.jackin.aidldemo.model.MessageModel;
import com.jackin.aidldemo.IMessageReceiver;

interface IMessageSender {
    void sendMessage(in MessageModel msg);

    void registerMessageReceiver(IMessageReceiver msgReceiver);

    void unRegisterMessageReceiver(IMessageReceiver msgReceiver);
}
