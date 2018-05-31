package com.rl.geye.image;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nicky on 2016/10/31.
 */
public class PhotoItem implements Parcelable {

    public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel source) {
            return new PhotoItem(source);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };
    private int id;
    private String path;

    public PhotoItem() {
    }

    public PhotoItem(int id, String path) {
        this.id = id;
        this.path = path;
    }

    protected PhotoItem(Parcel in) {
        this.id = in.readInt();
        this.path = in.readString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotoItem photoItem = (PhotoItem) o;

        return id == photoItem.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.path);
    }
}
