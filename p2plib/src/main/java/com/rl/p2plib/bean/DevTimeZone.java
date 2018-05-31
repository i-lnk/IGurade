package com.rl.p2plib.bean;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.rl.commons.BaseApp;
import com.rl.commons.interf.Chooseable;
import com.rl.p2plib.R;


/**
 * @author NickyHuang
 * @ClassName: DevTimeZone
 * @Description: 时区
 * @date 2016-6-30 下午4:34:05
 */
public class DevTimeZone implements Chooseable, Parcelable {


    private int timezone = 0;

    private SparseArray<String> cityMap = new SparseArray<>();
    private SparseArray<String> descMap = new SparseArray<>();

    public DevTimeZone() {
        init();
    }


    public DevTimeZone(int timezone) {
        this.timezone = timezone;
        init();
    }


    public void init() {
        String[] zoneCities = BaseApp.context().getResources().getStringArray(R.array.time_zone_city_names);
        String[] zoneDescs = BaseApp.context().getResources().getStringArray(R.array.time_zone_descs);
        int[] zones = BaseApp.context().getResources().getIntArray(R.array.time_zone_val);
        for (int i = 0; i < zones.length; i++) {
            cityMap.put(zones[i], zoneCities[i]);
            descMap.put(zones[i], zoneDescs[i]);
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DevTimeZone other = (DevTimeZone) obj;
        if (timezone != other.timezone)
            return false;
        return true;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    @Override
    public boolean hasIcon() {
        return false;
    }

    @Override
    public int getIconResid() {
        return 0;
    }

    public String getCity() {
        String city = null;
        try {
            city = cityMap.get(timezone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return city == null ? "" : city;
    }

    public String getDesc() {
        String desc = null;
        try {
            desc = descMap.get(timezone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return desc == null ? "" : desc;
    }

    @Override
    public String getName() {
        return getCity() + "  " + getDesc();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.timezone);
        dest.writeSparseArray((SparseArray) this.cityMap);
        dest.writeSparseArray((SparseArray) this.descMap);
    }

    protected DevTimeZone(Parcel in) {
        this.timezone = in.readInt();
        this.cityMap = in.readSparseArray(String.class.getClassLoader());
        this.descMap = in.readSparseArray(String.class.getClassLoader());
    }

    public static final Creator<DevTimeZone> CREATOR = new Creator<DevTimeZone>() {
        @Override
        public DevTimeZone createFromParcel(Parcel source) {
            return new DevTimeZone(source);
        }

        @Override
        public DevTimeZone[] newArray(int size) {
            return new DevTimeZone[size];
        }
    };
}
