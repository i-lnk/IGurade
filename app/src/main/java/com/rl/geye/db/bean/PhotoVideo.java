package com.rl.geye.db.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Checkable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.rl.commons.utils.DateUtil;
import com.rl.geye.constants.Constants;
import com.rl.p2plib.constants.P2PConstants;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by Nicky on 2016/10/7.
 * (视频、图片)
 */
@Entity
//        (indexes = {
//        @Index(value = "dev_type DESC")
//})
public class PhotoVideo implements Checkable, MultiItemEntity, Parcelable {

    public static final Creator<PhotoVideo> CREATOR = new Creator<PhotoVideo>() {
        @Override
        public PhotoVideo createFromParcel(Parcel source) {
            return new PhotoVideo(source);
        }

        @Override
        public PhotoVideo[] newArray(int size) {
            return new PhotoVideo[size];
        }
    };
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "pv_name")
    private String name = "";//名称
    @Unique
    @Property(nameInDb = "pv_path")
    private String path = ""; //本地 路径
    @Property(nameInDb = "pv_path_thumb")
    private String pathThumb = ""; //本地缩略图路径(视频)

//    @Property(nameInDb = "dev_id")
//    private String devId = "";//设备id
//
//    @Property(nameInDb = "dev_name")
//    private String devName = ""; //设备名
    @Property(nameInDb = "pv_type")
    private int type = P2PConstants.PhotoVideoType.PICTURE;//数据类型
    private Long did;//这是与设备关联的外键
    @ToOne(joinProperty = "did") //这个是注解绑定did
    private EdwinDevice device;//对象--关联设备
    @Property(nameInDb = "trigger_time")
    private long triggerTime = 0; //触发时间(秒数)
    private String date;//日期
    @Transient
    private boolean mChecked; // 是否选中
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1255233357)
    private transient PhotoVideoDao myDao;
    @Generated(hash = 708752895)
    private transient Long device__resolvedKey;

    public PhotoVideo() {
    }

    public PhotoVideo(String path) {
        this.path = path;
    }


//    public EdwinDevice getDevice() {
//        return device;
//    }
//
//    public void setDevice(EdwinDevice device) {
//        this.device = device;
//    }

    protected PhotoVideo(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.path = in.readString();
        this.pathThumb = in.readString();
        this.type = in.readInt();
        this.did = in.readLong();
        this.device = in.readParcelable(EdwinDevice.class.getClassLoader());
        this.triggerTime = in.readLong();
        this.mChecked = in.readByte() != 0;
    }

    @Generated(hash = 433461271)
    public PhotoVideo(Long id, String name, String path, String pathThumb, int type, Long did,
                      long triggerTime, String date) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.pathThumb = pathThumb;
        this.type = type;
        this.did = did;
        this.triggerTime = triggerTime;
        this.date = date;
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

    //    public String getLocalPath() {
//        return localPath;
//    }
//
//    public void setLocalPath(String localPath) {
//        this.localPath = localPath;
//    }
//
//
//    public String getAbsolutePath(){
//        String absolutePath = localPath;
//        if( localPath!=null && !localPath.endsWith("/") ){
//            absolutePath += "/";
//        }
//        absolutePath += name;
//        return absolutePath;
//    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

//    /**
//     * 日期2015-09-01，用来显示
//     */
//    public String getTriggerDate() {
//        return DateUtil.getDateStr(triggerTime * 1000);
//    }

    public String getPathThumb() {
        return pathThumb;
    }

    public void setPathThumb(String pathThumb) {
        this.pathThumb = pathThumb;
    }

    public long getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(long triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 时间09:00:00，用来显示
     */
    public String getFormatTime() {
        return DateUtil.getTimeStr(triggerTime * 1000);
    }

    /**
     * 时间2015-09-01 09:00:00，用来显示
     */
    public String getFormatDateTime() {
        return DateUtil.getCommTimeStr2(triggerTime * 1000);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotoVideo that = (PhotoVideo) o;

        return path != null ? path.equals(that.path) : that.path == null;

    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
    }

    @Override
    public int getItemType() {
        return Constants.LevelType.CHILD;
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
        dest.writeString(this.pathThumb);
        dest.writeInt(this.type);
        dest.writeLong(this.did);
        dest.writeParcelable(this.device, flags);
        dest.writeLong(this.triggerTime);
        dest.writeByte(this.mChecked ? (byte) 1 : (byte) 0);
    }

    public Long getDid() {
        return this.did;
    }

    public void setDid(Long did) {
        this.did = did;
    }

    @Override
    public String toString() {
        return "PhotoVideo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", pathThumb='" + pathThumb + '\'' +
                ", type=" + type +
                ", did=" + did +
                ", triggerTime=" + triggerTime +
                ", date='" + date + '\'' +
                ", mChecked=" + mChecked +
                '}';
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
    @Generated(hash = 1084852993)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPhotoVideoDao() : null;
    }


}
