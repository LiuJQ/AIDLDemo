// IMessageSender.aidl
package com.jackin.aidldemo;

// Declare any non-default types here with import statements
import com.jackin.aidldemo.model.MessageModel;

interface IMessageSender {

    void sendMessage(in MessageModel msg);

}
