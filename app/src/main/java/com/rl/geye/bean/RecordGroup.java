package com.rl.geye.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 2016/9/12.
 * 图片组、视频组(按日期分组)
 */

public class RecordGroup extends AbstractExpandableItem<EdwinRecord> implements MultiItemEntity, Parcelable {

    public static final Creator<RecordGroup> CREATOR = new Creator<RecordGroup>() {
        @Override
        public RecordGroup createFromParcel(Parcel source) {
            return new RecordGroup(source);
        }

        @Override
        public RecordGroup[] newArray(int size) {
            return new RecordGroup[size];
        }
    };
//    private String desc;
    private String date;

    public RecordGroup() {
    }

    protected RecordGroup(Parcel in) {
        this.date = in.readString();
        this.mExpandable = in.readByte() != 0;
        this.mSubItems = new ArrayList<EdwinRecord>();
        in.readList(this.mSubItems, EdwinRecord.class.getClassLoader());
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return Constants.LevelType.GROUP;
    }

    public List<EdwinRecord> getSelectedDatas() {
        List<EdwinRecord> list = new ArrayList<>();
        if (mSubItems != null) {
            for (EdwinRecord child : mSubItems) {
                if (child.isChecked())
                    list.add(child);
            }
        }
        return list;
    }

    public List<EdwinRecord> getUSelectedDatas() {
        List<EdwinRecord> list = new ArrayList<>();
        if (mSubItems != null) {
            for (EdwinRecord child : mSubItems) {
                if (!child.isChecked())
                    list.add(child);
            }
        }
        return list;
    }

    public int getSelectedCount() {
        if (mSubItems == null)
            return 0;
        int count = 0;
        for (EdwinRecord child : mSubItems) {
            if (child.isChecked())
                count++;
        }
        return count;
    }

    public boolean isSelectAll() {
        if (mSubItems == null)
            return false;
        for (EdwinRecord child : mSubItems) {
            if (!child.isChecked())
                return false;
        }
        return true;
    }

    public void selectAll() {
        if (mSubItems == null)
            return;
        for (EdwinRecord child : mSubItems) {
            child.setChecked(true);
        }
    }

    public void deselectAll() {
        if (mSubItems == null)
            return;
        for (EdwinRecord child : mSubItems) {
            child.setChecked(false);
        }
    }

    public void toggleSelectAll() {
        if (mSubItems == null)
            return;
        if (isSelectAll())
            deselectAll();
        else
            selectAll();
    }

    public int getChildrenCount() {
        return mSubItems == null ? 0 : mSubItems.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeByte(this.mExpandable ? (byte) 1 : (byte) 0);
        dest.writeList(this.mSubItems);
    }

    @Override
    public String toString() {
        return "RecordGroup{" +
                "date='" + date + '\'' +
                "} " + super.toString();
    }


}
