package com.rl.geye.image;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 2016/10/31.
 */
public class PhotoBucket implements Parcelable {

    public static final Creator<PhotoBucket> CREATOR = new Creator<PhotoBucket>() {
        @Override
        public PhotoBucket createFromParcel(Parcel source) {
            return new PhotoBucket(source);
        }

        @Override
        public PhotoBucket[] newArray(int size) {
            return new PhotoBucket[size];
        }
    };
    private String id;
    private String coverPath;
    private String name;
    private long dateAdded;
    private List<PhotoItem> photos = new ArrayList<>();

    public PhotoBucket() {
    }

    protected PhotoBucket(Parcel in) {
        this.id = in.readString();
        this.coverPath = in.readString();
        this.name = in.readString();
        this.dateAdded = in.readLong();
        this.photos = in.createTypedArrayList(PhotoItem.CREATOR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotoBucket that = (PhotoBucket) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public List<PhotoItem> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoItem> photos) {
        this.photos = photos;
    }

    public List<String> getPhotoPaths() {
        List<String> paths = new ArrayList<>(photos.size());
        for (PhotoItem photo : photos) {
            paths.add(photo.getPath());
        }
        return paths;
    }

    public void addPhoto(int id, String path) {
        photos.add(new PhotoItem(id, path));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.coverPath);
        dest.writeString(this.name);
        dest.writeLong(this.dateAdded);
        dest.writeTypedList(this.photos);
    }
}
