package com.rl.geye.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;


/**
 * Created by Nicky on 2016/8/25.
 * 433 子设备
 */
@Entity
        (indexes = {
                @Index(value = "id,pid", unique = true)
        })
public class SubDevice implements Cloneable, Parcelable {


    public static final Creator<SubDevice> CREATOR = new Creator<SubDevice>() {
        @Override
        public SubDevice createFromParcel(Parcel source) {
            return new SubDevice(source);
        }

        @Override
        public SubDevice[] newArray(int size) {
            return new SubDevice[size];
        }
    };
    @Id(autoincrement = true)
    private Long tid;
    @Property(nameInDb = "sub_name")
    private String name;
    @Property(nameInDb = "sub_type")
    private int type = 0; //类型
    @Property(nameInDb = "sub_sid")
    private String id;//子设备ID
    @Property(nameInDb = "sub_pid")
    private String pid;//关联主设备ID


    public SubDevice() {
    }


    public SubDevice(String name, int type) {
        this.name = name;
        this.type = type;
//        this.isBlock = isBlock;
    }

    protected SubDevice(Parcel in) {
        this.tid = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.type = in.readInt();
        this.id = in.readString();
        this.pid = in.readString();
    }

    @Generated(hash = 326540063)
    public SubDevice(Long tid, String name, int type, String id, String pid) {
        this.tid = tid;
        this.name = name;
        this.type = type;
        this.id = id;
        this.pid = pid;
    }

    @Override
    public Object clone() {
        SubDevice sd = null;
        try {
            sd = (SubDevice) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return sd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubDevice subDevice = (SubDevice) o;
        if (id != null ? !id.equals(subDevice.id) : subDevice.id != null) return false;
        return pid != null ? pid.equals(subDevice.pid) : subDevice.pid == null;
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.tid);
        dest.writeString(this.name);
        dest.writeInt(this.type);
        dest.writeString(this.id);
        dest.writeString(this.pid);
    }

    @Override
    public String toString() {
        return "SubDevice{" +
                "tid=" + tid +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", id='" + id + '\'' +
                ", pid='" + pid + '\'' +
                '}';
    }
}
