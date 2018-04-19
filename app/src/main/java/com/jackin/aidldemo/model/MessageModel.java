package com.jackin.aidldemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * 消息实体
 * @author LiuJQ
 */
public class MessageModel implements Parcelable {

    private final static String MSG_FORMAT = "%s\nfrom:%s\nto:%s\nmessage:%s";

    private int msgId;
    private long msgTimeStamp;
    private String msgFrom;
    private String msgTo;
    private String msgContent;

    public MessageModel () {

    }

    public int getMsgId() {
        return msgId;
    }

    public long getMsgTimeStamp() {
        return msgTimeStamp;
    }

    public void setMsgTimeStamp(long msgTimeStamp) {
        this.msgTimeStamp = msgTimeStamp;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    public String getMsgTo() {
        return msgTo;
    }

    public void setMsgTo(String msgTo) {
        this.msgTo = msgTo;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    protected MessageModel(Parcel in) {
        msgId = in.readInt();
        msgTimeStamp = in.readLong();
        msgFrom = in.readString();
        msgTo = in.readString();
        msgContent = in.readString();
    }

    public static final Creator<MessageModel> CREATOR = new Creator<MessageModel>() {
        @Override
        public MessageModel createFromParcel(Parcel in) {
            return new MessageModel(in);
        }

        @Override
        public MessageModel[] newArray(int size) {
            return new MessageModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(msgId);
        dest.writeLong(msgTimeStamp);
        dest.writeString(msgFrom);
        dest.writeString(msgTo);
        dest.writeString(msgContent);
    }

    @Override
    public String toString() {
        Date time = new Date(msgTimeStamp);
        return String.format(MSG_FORMAT, time.toString(), msgFrom, msgTo, msgContent);
    }
}
