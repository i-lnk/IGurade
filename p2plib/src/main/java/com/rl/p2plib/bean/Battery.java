package com.rl.p2plib.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nicky on 2017/8/15.
 */

public class Battery implements Parcelable {

    private int battery;

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.battery);
    }

    public Battery() {
    }

    protected Battery(Parcel in) {
        this.battery = in.readInt();
    }

    public static final Parcelable.Creator<Battery> CREATOR = new Parcelable.Creator<Battery>() {
        @Override
        public Battery createFromParcel(Parcel source) {
            return new Battery(source);
        }

        @Override
        public Battery[] newArray(int size) {
            return new Battery[size];
        }
    };
}
