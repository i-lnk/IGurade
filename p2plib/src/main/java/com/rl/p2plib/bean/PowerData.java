package com.rl.p2plib.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: SXF
 * E-mail: xue.com.fei@outlook.com
 * CreatedTime: 2018/3/31 20:17
 * <p>
 * PowerData
 */
public class PowerData implements Parcelable ,Cloneable{
    private int channel = 0;
    private int mode = 0;

    protected PowerData(Parcel in) {
        channel = in.readInt();
        mode = in.readInt();
    }

    public PowerData() {
    }

    public int getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "PowerData{" +
                "channel=" + channel +
                ", mode=" + mode +
                '}';
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(channel);
        dest.writeInt(mode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PowerData> CREATOR = new Creator<PowerData>() {
        @Override
        public PowerData createFromParcel(Parcel in) {
            return new PowerData(in);
        }

        @Override
        public PowerData[] newArray(int size) {
            return new PowerData[size];
        }
    };
}
