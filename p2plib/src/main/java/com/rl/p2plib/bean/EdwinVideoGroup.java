package com.rl.p2plib.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 2017/4/1.
 */
public class EdwinVideoGroup implements Parcelable {


    private int total;
    private int count;
    private int index;
    private int end;
    private List<EdwinVideo> record = new ArrayList<>();


    public boolean isEnd(){
        return end == 1;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public List<EdwinVideo> getRecord() {
        return record;
    }

    public void setRecord(List<EdwinVideo> record) {
        this.record = record;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.total);
        dest.writeInt(this.count);
        dest.writeInt(this.index);
        dest.writeInt(this.end);
        dest.writeTypedList(this.record);
    }

    public EdwinVideoGroup() {
    }

    protected EdwinVideoGroup(Parcel in) {
        this.total = in.readInt();
        this.count = in.readInt();
        this.index = in.readInt();
        this.end = in.readInt();
        this.record = in.createTypedArrayList(EdwinVideo.CREATOR);
    }

    public static final Creator<EdwinVideoGroup> CREATOR = new Creator<EdwinVideoGroup>() {
        @Override
        public EdwinVideoGroup createFromParcel(Parcel source) {
            return new EdwinVideoGroup(source);
        }

        @Override
        public EdwinVideoGroup[] newArray(int size) {
            return new EdwinVideoGroup[size];
        }
    };
}
