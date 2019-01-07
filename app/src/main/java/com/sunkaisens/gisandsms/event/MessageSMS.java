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
public class MessageSMS extends DataSupport implements Parcelable {
    /**
     * 对端用户的账号
     */
    private String remoteAccount;
    /**
     * 对端用户昵称
     */
    private String remoteDisplayname;
    /**
     * 本地用户账号
     */
    private String localAccount;
    /**
     * 本地用户昵称
     */
    private String localDiaplayname;
    /**
     * 接收开始时间
     */
    private long startTime;
    /**
     * 接收结束时间
     */
    private long endTime;
    /**
     * 文本消息内容
     */
    private String msg;
    /**
     * 是否已读
     */
    private int isRead;
    /**
     * 文件下载链接
     */
    private String fileUri;

    /**
     * 标记文件的类型
     */
    private int msgType;

    /**
     * 文件的发送进度
     */

    private int fileProgress;
    /**
     * 消息标识id
     */
    private String localMsgID;

    /**
     * 组号
     */
    private String groupNumber;

    /**
     * 是否是群组消息
     */
    private boolean isGroup;

    public String getRemoteAccount() {
        return remoteAccount;
    }

    public void setRemoteAccount(String remoteAccount) {
        this.remoteAccount = remoteAccount;
    }

    public String getRemoteDisplayname() {
        return remoteDisplayname;
    }

    public void setRemoteDisplayname(String remoteDisplayname) {
        this.remoteDisplayname = remoteDisplayname;
    }

    public String getLocalAccount() {
        return localAccount;
    }

    public void setLocalAccount(String localAccount) {
        this.localAccount = localAccount;
    }

    public String getLocalDiaplayname() {
        return localDiaplayname;
    }

    public void setLocalDiaplayname(String localDiaplayname) {
        this.localDiaplayname = localDiaplayname;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getFileProgress() {
        return fileProgress;
    }

    public void setFileProgress(int fileProgress) {
        this.fileProgress = fileProgress;
    }

    public String getLocalMsgID() {
        return localMsgID;
    }

    public void setLocalMsgID(String localMsgID) {
        this.localMsgID = localMsgID;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    @Override
    public String toString() {
        return "MessageSMS{" +
                "remoteAccount='" + remoteAccount + '\'' +
                ", remoteDisplayname='" + remoteDisplayname + '\'' +
                ", localAccount='" + localAccount + '\'' +
                ", localDiaplayname='" + localDiaplayname + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", msg='" + msg + '\'' +
                ", isRead=" + isRead +
                ", fileUri='" + fileUri + '\'' +
                ", msgType=" + msgType +
                ", fileProgress=" + fileProgress +
                ", localMsgID='" + localMsgID + '\'' +
                ", groupNumber='" + groupNumber + '\'' +
                ", isGroup=" + isGroup +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.remoteAccount);
        dest.writeString(this.remoteDisplayname);
        dest.writeString(this.localAccount);
        dest.writeString(this.localDiaplayname);
        dest.writeLong(this.startTime);
        dest.writeLong(this.endTime);
        dest.writeString(this.msg);
        dest.writeInt(this.isRead);
        dest.writeString(this.fileUri);
        dest.writeInt(this.msgType);
        dest.writeInt(this.fileProgress);
        dest.writeString(this.localMsgID);
        dest.writeString(this.groupNumber);
        dest.writeByte(this.isGroup ? (byte) 1 : (byte) 0);
    }

    public MessageSMS() {
    }

    protected MessageSMS(Parcel in) {
        this.remoteAccount = in.readString();
        this.remoteDisplayname = in.readString();
        this.localAccount = in.readString();
        this.localDiaplayname = in.readString();
        this.startTime = in.readLong();
        this.endTime = in.readLong();
        this.msg = in.readString();
        this.isRead = in.readInt();
        this.fileUri = in.readString();
        this.msgType = in.readInt();
        this.fileProgress = in.readInt();
        this.localMsgID = in.readString();
        this.groupNumber = in.readString();
        this.isGroup = in.readByte() != 0;
    }

    public static final Creator<MessageSMS> CREATOR = new Creator<MessageSMS>() {
        @Override
        public MessageSMS createFromParcel(Parcel source) {
            return new MessageSMS(source);
        }

        @Override
        public MessageSMS[] newArray(int size) {
            return new MessageSMS[size];
        }
    };
}
