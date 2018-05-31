package com.rl.geye.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nicky on 2016/10/25.
 * 裁剪参数(宽高比例,最大尺寸,裁剪后的名称)
 */
public class CropParam implements Parcelable {
    public static final Creator<CropParam> CREATOR = new Creator<CropParam>() {
        @Override
        public CropParam createFromParcel(Parcel source) {
            return new CropParam(source);
        }

        @Override
        public CropParam[] newArray(int size) {
            return new CropParam[size];
        }
    };
    private float ratioX = 1;
    private float ratioY = 1;
    private int maxWidth = 0;
    private int maxHeight = 0;
    private String croppedName = "SampleCropImage.jpg";

    public CropParam() {

    }

    protected CropParam(Parcel in) {
        this.ratioX = in.readFloat();
        this.ratioY = in.readFloat();
        this.maxWidth = in.readInt();
        this.maxHeight = in.readInt();
        this.croppedName = in.readString();
    }

    public static CropParam getDefaultParam() {
        CropParam obj = new CropParam();
        obj.setCroppedName("SampleCropImage.jpg");
        obj.setMaxHeight(0);
        obj.setMaxWidth(0);
        obj.setRatioX(1);
        obj.setRatioY(1);
        return obj;
    }

    public float getRatioX() {
        return ratioX;
    }

    public void setRatioX(float ratioX) {
        this.ratioX = ratioX;
    }

    public float getRatioY() {
        return ratioY;
    }

    public void setRatioY(float ratioY) {
        this.ratioY = ratioY;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public String getCroppedName() {
        return croppedName;
    }

    public void setCroppedName(String croppedName) {
        this.croppedName = croppedName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.ratioX);
        dest.writeFloat(this.ratioY);
        dest.writeInt(this.maxWidth);
        dest.writeInt(this.maxHeight);
        dest.writeString(this.croppedName);
    }
}
