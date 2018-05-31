package com.rl.geye.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by Nicky on 2016/10/7.
 * (设备背景图片)
 */
@Entity
//        (indexes = {
//        @Index(value = "dev_type DESC")
//})
public class DeviceBg implements Parcelable {

    public static final Creator<DeviceBg> CREATOR = new Creator<DeviceBg>() {
        @Override
        public DeviceBg createFromParcel(Parcel source) {
            return new DeviceBg(source);
        }

        @Override
        public DeviceBg[] newArray(int size) {
            return new DeviceBg[size];
        }
    };
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "pv_name")
    private String name = "";//名称
    @Unique
    @Property(nameInDb = "pv_path")
    private String path = ""; //本地 路径
    private Long did;//这是与设备关联的外键
    @ToOne(joinProperty = "did") //这个是注解绑定did
    private EdwinDevice device;//对象--关联设备
    @Property(nameInDb = "trigger_time")
    private long triggerTime = 0; //触发时间(秒数)
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 539210695)
    private transient DeviceBgDao myDao;
    @Generated(hash = 708752895)
    private transient Long device__resolvedKey;

    public DeviceBg() {
    }

    public DeviceBg(String path) {
        this.path = path;
    }


    protected DeviceBg(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.path = in.readString();
        this.did = (Long) in.readValue(Long.class.getClassLoader());
        this.device = in.readParcelable(EdwinDevice.class.getClassLoader());
        this.triggerTime = in.readLong();
    }

    @Generated(hash = 1736221273)
    public DeviceBg(Long id, String name, String path, Long did, long triggerTime) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.did = did;
        this.triggerTime = triggerTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(long triggerTime) {
        this.triggerTime = triggerTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceBg that = (DeviceBg) o;
        return path != null ? path.equals(that.path) : that.path == null;

    }

    public Long getDid() {
        return this.did;
    }

    public void setDid(Long did) {
        this.did = did;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeValue(this.did);
        dest.writeParcelable(this.device, flags);
        dest.writeLong(this.triggerTime);
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 859389465)
    public EdwinDevice getDevice() {
        Long __key = this.did;
        if (device__resolvedKey == null || !device__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EdwinDeviceDao targetDao = daoSession.getEdwinDeviceDao();
            EdwinDevice deviceNew = targetDao.load(__key);
            synchronized (this) {
                device = deviceNew;
                device__resolvedKey = __key;
            }
        }
        return device;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 530623700)
    public void setDevice(EdwinDevice device) {
        synchronized (this) {
            this.device = device;
            did = device == null ? null : device.getId();
            device__resolvedKey = did;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1423487308)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDeviceBgDao() : null;
    }


}
