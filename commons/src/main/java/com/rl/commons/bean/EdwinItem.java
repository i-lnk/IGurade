package com.rl.commons.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nicky on 2017/3/29.
 * 通用 实体类( 名称 值)
 */

public class EdwinItem implements Parcelable {

    private String name;
    private int val;

    public EdwinItem(String name, int val) {
        this.name = name;
        this.val = val;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EdwinItem edwinItem = (EdwinItem) o;

        return val == edwinItem.val;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.val);
    }

    public EdwinItem() {
    }

    protected EdwinItem(Parcel in) {
        this.name = in.readString();
        this.val = in.readInt();
    }

    public static final Creator<EdwinItem> CREATOR = new Creator<EdwinItem>() {
        @Override
        public EdwinItem createFromParcel(Parcel source) {
            return new EdwinItem(source);
        }

        @Override
        public EdwinItem[] newArray(int size) {
            return new EdwinItem[size];
        }
    };

    /**
     * 注意此方法不可更改，否则选择值的地方可能显示出错
     */
    @Override
    public String toString() {
        return name;
    }
}
