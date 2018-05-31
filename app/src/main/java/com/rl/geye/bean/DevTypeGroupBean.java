package com.rl.geye.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.rl.geye.constants.Constants;

import java.util.ArrayList;

/**
 * Created by Nicky on 2016/9/18.
 * 设备类型(添加设备)
 * 注意:此处类型通常是指一组设备  比如摇头机包含国科摇头机和神州摇头机
 */
public class DevTypeGroupBean implements Parcelable {

    public static final Creator<DevTypeGroupBean> CREATOR = new Creator<DevTypeGroupBean>() {
        @Override
        public DevTypeGroupBean createFromParcel(Parcel source) {
            return new DevTypeGroupBean(source);
        }

        @Override
        public DevTypeGroupBean[] newArray(int size) {
            return new DevTypeGroupBean[size];
        }
    };
    /**
     * @see Constants.DeviceTypeGroup 类型分组
     */
    private int typeGroup = Constants.DeviceTypeGroup.GROUP_1;
    /**
     * @see com.rl.p2plib.constants.P2PConstants.DeviceType 类型列表
     */
    private ArrayList<Integer> typeList = new ArrayList<>();
    private int imgResId = -1;

    public DevTypeGroupBean() {
    }

    public DevTypeGroupBean(int typeGroup) {
        this.typeGroup = typeGroup;
    }


    public DevTypeGroupBean(int typeGroup, ArrayList<Integer> typeList) {
        this.typeGroup = typeGroup;
        this.typeList = typeList;
    }

    protected DevTypeGroupBean(Parcel in) {
        this.typeGroup = in.readInt();
        this.typeList = new ArrayList<>();
        in.readList(this.typeList, Integer.class.getClassLoader());
        this.imgResId = in.readInt();
    }

    public ArrayList<Integer> getTypeList() {
        return typeList;
    }

    public void setTypeList(ArrayList<Integer> typeList) {
        this.typeList = typeList;
    }

    public DevTypeGroupBean addType(int type) {
        if (!typeList.contains(Integer.valueOf(type))) {
            typeList.add(type);
        }
        return this;
    }

    public int getTypeGroup() {
        return typeGroup;
    }

    public void setTypeGroup(int typeGroup) {
        this.typeGroup = typeGroup;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.typeGroup);
        dest.writeList(this.typeList);
        dest.writeInt(this.imgResId);
    }
}
