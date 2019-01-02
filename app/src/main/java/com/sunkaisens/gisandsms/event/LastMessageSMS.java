package com.sunkaisens.gisandsms.event;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * @author:sun
 * @date:2018/12/27
 * @email:sunjianyun@sunkaisens.com
 * @Description:
 */
public class LastMessageSMS extends DataSupport implements Parcelable {
    /**
     * 本地号码
     */
    private String localNumber;
    /**
     * 远端号码
     */
    private String remoteNumber;
    /**
     * 最后一条消息
     */
    private String lastSMS;
    /**
     * 时间
     */
    private long data;

    public String getLocalNumber() {
        return localNumber;
    }

    public void setLocalNumber(String localNumber) {
        this.localNumber = localNumber;
    }

    public String getRemoteNumber() {
        return remoteNumber;
    }

    public void setRemoteNumber(String remoteNumber) {
        this.remoteNumber = remoteNumber;
    }

    public String getLastSMS() {
        return lastSMS;
    }

    public void setLastSMS(String lastSMS) {
        this.lastSMS = lastSMS;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LastMessageSMS{" +
                "localNumber='" + localNumber + '\'' +
                ", remoteNumber='" + remoteNumber + '\'' +
                ", lastSMS='" + lastSMS + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.localNumber);
        dest.writeString(this.remoteNumber);
        dest.writeString(this.lastSMS);
        dest.writeLong(this.data);
    }

    public LastMessageSMS() {
    }

    protected LastMessageSMS(Parcel in) {
        this.localNumber = in.readString();
        this.remoteNumber = in.readString();
        this.lastSMS = in.readString();
        this.data = in.readLong();
    }

    public static final Parcelable.Creator<LastMessageSMS> CREATOR = new Parcelable.Creator<LastMessageSMS>() {
        @Override
        public LastMessageSMS createFromParcel(Parcel source) {
            return new LastMessageSMS(source);
        }

        @Override
        public LastMessageSMS[] newArray(int size) {
            return new LastMessageSMS[size];
        }
    };
}
