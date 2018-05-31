package com.rl.geye.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.rl.commons.interf.Chooseable;

/**
 * Created by Nicky on 2016/9/28.
 * Wifi信息
 */
public class EdwinWifiInfo implements Parcelable, Chooseable {

    public static final Creator<EdwinWifiInfo> CREATOR = new Creator<EdwinWifiInfo>() {
        @Override
        public EdwinWifiInfo createFromParcel(Parcel source) {
            return new EdwinWifiInfo(source);
        }

        @Override
        public EdwinWifiInfo[] newArray(int size) {
            return new EdwinWifiInfo[size];
        }
    };
    private String wifiName = "";
    private byte authMode;
    private String capabilities = "";
    private int localIp;
    private String wifiPwd = "";

    private String bssid = "";
    private int level = 0;

    public EdwinWifiInfo() {
    }

    protected EdwinWifiInfo(Parcel in) {
        this.wifiName = in.readString();
        this.authMode = in.readByte();
        this.capabilities = in.readString();
        this.localIp = in.readInt();
        this.wifiPwd = in.readString();
        this.bssid = in.readString();
        this.level = in.readInt();
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public byte getAuthMode() {
        return authMode;
    }

    public void setAuthMode(byte authMode) {
        this.authMode = authMode;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getLocalIp() {
        return localIp;
    }

    public void setLocalIp(int localIp) {
        this.localIp = localIp;
    }

    public String getWifiPwd() {
        return wifiPwd;
    }

    public void setWifiPwd(String wifiPwd) {
        this.wifiPwd = wifiPwd;
    }


    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.wifiName);
        dest.writeByte(this.authMode);
        dest.writeString(this.capabilities);
        dest.writeInt(this.localIp);
        dest.writeString(this.wifiPwd);
        dest.writeString(this.bssid);
        dest.writeInt(this.level);
    }

    @Override
    public String toString() {
        return "EdwinWifiInfo{" +
                "wifiPwd='" + wifiPwd + '\'' +
                ", wifiName='" + wifiName + '\'' +
                ", authMode=" + authMode +
                ", capabilities='" + capabilities + '\'' +
                ", localIp=" + localIp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EdwinWifiInfo wifiInfo = (EdwinWifiInfo) o;

        if (wifiName != null ? !wifiName.equals(wifiInfo.wifiName) : wifiInfo.wifiName != null)
            return false;
        return bssid != null ? bssid.equals(wifiInfo.bssid) : wifiInfo.bssid == null;

    }

    @Override
    public int hashCode() {
        int result = wifiName != null ? wifiName.hashCode() : 0;
        result = 31 * result + (bssid != null ? bssid.hashCode() : 0);
        return result;
    }

    @Override
    public boolean hasIcon() {
        return false;
    }

    @Override
    public int getIconResid() {
        return -1;
    }

    @Override
    public String getName() {
        return wifiName;
    }
}
