package com.rl.geye.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.PhotoVideo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 2016/9/12.
 * 图片组、视频组(按日期分组)
 */

public class PhotoVideoGroup extends AbstractExpandableItem<PhotoVideo> implements MultiItemEntity, Parcelable {

    public static final Creator<PhotoVideoGroup> CREATOR = new Creator<PhotoVideoGroup>() {
        @Override
        public PhotoVideoGroup createFromParcel(Parcel source) {
            return new PhotoVideoGroup(source);
        }

        @Override
        public PhotoVideoGroup[] newArray(int size) {
            return new PhotoVideoGroup[size];
        }
    };
//    private String desc;
    private String date;

    public PhotoVideoGroup() {
    }

    protected PhotoVideoGroup(Parcel in) {
        this.date = in.readString();
        this.mExpandable = in.readByte() != 0;
        this.mSubItems = new ArrayList<PhotoVideo>();
        in.readList(this.mSubItems, PhotoVideo.class.getClassLoader());
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

    public List<PhotoVideo> getSelectedDatas() {
        List<PhotoVideo> list = new ArrayList<>();
        if (mSubItems != null) {
            for (PhotoVideo child : mSubItems) {
                if (child.isChecked())
                    list.add(child);
            }
        }
        return list;
    }

    public List<PhotoVideo> getUSelectedDatas() {
        List<PhotoVideo> list = new ArrayList<>();
        if (mSubItems != null) {
            for (PhotoVideo child : mSubItems) {
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
        for (PhotoVideo child : mSubItems) {
            if (child.isChecked())
                count++;
        }
        return count;
    }

    public boolean isSelectAll() {
        if (mSubItems == null)
            return false;
        for (PhotoVideo child : mSubItems) {
            if (!child.isChecked())
                return false;
        }
        return true;
    }

    public void selectAll() {
        if (mSubItems == null)
            return;
        for (PhotoVideo child : mSubItems) {
            child.setChecked(true);
        }
    }

    public void deselectAll() {
        if (mSubItems == null)
            return;
        for (PhotoVideo child : mSubItems) {
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
}
