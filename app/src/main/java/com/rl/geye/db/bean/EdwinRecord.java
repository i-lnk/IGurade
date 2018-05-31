package com.rl.geye.db.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Checkable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.rl.commons.utils.DateUtil;
import com.rl.geye.constants.Constants;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by Nicky on 2016/10/7.
 * 记录
 */
@Entity
        (indexes = {
                @Index(value = "did ,type,triggerTime", unique = true)
        })
public class EdwinRecord implements Checkable, Parcelable, MultiItemEntity {

    public static final Creator<EdwinRecord> CREATOR = new Creator<EdwinRecord>() {
        @Override
        public EdwinRecord createFromParcel(Parcel source) {
            return new EdwinRecord(source);
        }

        @Override
        public EdwinRecord[] newArray(int size) {
            return new EdwinRecord[size];
        }
    };
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "rec_type")
    private int type = 0; //记录类型
    private Long did;//这是与设备关联的外键
    @ToOne(joinProperty = "did") //这个是注解绑定did
    private EdwinDevice device;//对象--关联设备

//    private String sdid = ""; //这是与433子设备关联的外键
//
//    @ToOne(joinProperty = "sdid") //这个是注解绑定did
//    private SubDevice subDevice;//对象--关联子设备
    @Property(nameInDb = "rec_is_answered")
    private boolean isAnswered; //是否一接听
    @Property(nameInDb = "trigger_time")
    private long triggerTime = 0; //触发时间(秒数)

//    private String desc = "";  //记录描述
//    private boolean isRead;   //是否已读
    private String date;//日期
    /**
     * 子设备不做关联
     */
    @Property(nameInDb = "sub_id")
    private String subDevId = ""; //433ID
    @Transient
    private SubDevice subDev; //
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
    @Generated(hash = 1781641966)
    private transient EdwinRecordDao myDao;
    @Generated(hash = 708752895)
    private transient Long device__resolvedKey;

    public EdwinRecord() {
    }

    protected EdwinRecord(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.type = in.readInt();
        this.did = (Long) in.readValue(Long.class.getClassLoader());
        this.device = in.readParcelable(EdwinDevice.class.getClassLoader());
        this.isAnswered = in.readByte() != 0;
        this.triggerTime = in.readLong();
        this.date = in.readString();
        this.mChecked = in.readByte() != 0;
        this.subDevId = in.readString();
        this.subDev = in.readParcelable(SubDevice.class.getClassLoader());
    }

    @Generated(hash = 822752363)
    public EdwinRecord(Long id, int type, Long did, boolean isAnswered, long triggerTime, String date,
                       String subDevId) {
        this.id = id;
        this.type = type;
        this.did = did;
        this.isAnswered = isAnswered;
        this.triggerTime = triggerTime;
        this.date = date;
        this.subDevId = subDevId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getDid() {
        return did;
    }

    public void setDid(Long did) {
        this.did = did;
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

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }

    /**
     * 时间09:00:00，用来显示
     */
    public String getFormatTime() {
        return DateUtil.getTimeStr(triggerTime * 1000);
    }

    public String getSubDevId() {
        return subDevId;
    }

    public void setSubDevId(String subDevId) {
        this.subDevId = subDevId;
    }

    public SubDevice getSubDev() {
        return subDev;
    }

    public void setSubDev(SubDevice subDev) {
        this.subDev = subDev;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.type);
        dest.writeValue(this.did);
        dest.writeParcelable(this.device, flags);
        dest.writeByte(this.isAnswered ? (byte) 1 : (byte) 0);
        dest.writeLong(this.triggerTime);
        dest.writeString(this.date);
        dest.writeByte(this.mChecked ? (byte) 1 : (byte) 0);
        dest.writeString(this.subDevId);
        dest.writeParcelable(this.subDev, flags);
    }

    public boolean getIsAnswered() {
        return this.isAnswered;
    }

    public void setIsAnswered(boolean isAnswered) {
        this.isAnswered = isAnswered;
    }

    @Override
    public String toString() {
        return "EdwinRecord{" +
                "id=" + id +
                ", type=" + type +
                ", did=" + did +
                ", device=" + device +
                ", isAnswered=" + isAnswered +
                ", triggerTime=" + triggerTime +
                ", date='" + date + '\'' +
                ", mChecked=" + mChecked +
                '}';
    }

//    @Override
//    public String toString() {
//        return "EdwinRecord{" +
//                "id=" + id +
//                ", type=" + type +
//                ", did=" + did +
//                ", device=" + device +
//                ", isAnswered=" + isAnswered +
//                ", triggerTime=" + triggerTime +
//                ", date='" + date + '\'' +
//                ", subDevId='" + subDevId + '\'' +
//                ", subDev=" + subDev +
//                ", mChecked=" + mChecked +
//                ", daoSession=" + daoSession +
//                ", myDao=" + myDao +
//                ", device__resolvedKey=" + device__resolvedKey +
//                '}';
//    }

    @Override
    public int getItemType() {
        return Constants.LevelType.CHILD;
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
    @Generated(hash = 204031646)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getEdwinRecordDao() : null;
    }

}
