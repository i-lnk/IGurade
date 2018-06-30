package com.rl.geye.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.rl.p2plib.constants.P2PConstants;
import com.rl.p2plib.interf.IP2PDevice;
import com.rl.p2plib.utils.IdUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;


/**
 * Created by Nicky on 2016/9/14.
 * 设备信息
 */
@Entity
//        (indexes = {
//        @Index(value = "dev_type DESC")
//})
public class EdwinDevice implements Cloneable, Parcelable, IP2PDevice, MultiItemEntity {

    public static final Creator<EdwinDevice> CREATOR = new Creator<EdwinDevice>() {
        @Override
        public EdwinDevice createFromParcel(Parcel source) {
            return new EdwinDevice(source);
        }

        @Override
        public EdwinDevice[] newArray(int size) {
            return new EdwinDevice[size];
        }
    };
    public static final int ITEM_VIEW_ADDED = 1;
    public static final int ITEM_VIEW_NEW = 2;
    @Id(autoincrement = true)
    private Long id;
    @Unique
    @Property(nameInDb = "dev_id")
    private String devId = ""; // 设备ID
    @Property(nameInDb = "dev_name")
    private String name = ""; // 设备名称
    @Property(nameInDb = "dev_type")
    private int type = P2PConstants.DeviceType.IPC;  // 设备类型
    @Property(nameInDb = "dev_user")
    private String user = ""; //设备用户名
    @Property(nameInDb = "dev_pwd")
    private String pwd = ""; //设备密码
    @Property(nameInDb = "dev_shareable")
    private boolean shareable = false; //设备是否可被分享
    @Property(nameInDb = "dev_bg")
    private String bgPath = ""; //背景图片
    @Property(nameInDb = "dev_user_pwd_ok")
    private boolean userPwdOK = false; //设备用户名密码是否正确
    @Property(nameInDb = "time_zone_set_ok")
    private boolean timeZoneOk = false; //时区设置完成
    @Transient
    private int status = P2PConstants.P2PStatus.UNKNOWN;// 设备状态
    @Transient
    private boolean checked = false;
    @Transient
    private boolean isAdded;

    public EdwinDevice() {
    }

    protected EdwinDevice(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.devId = in.readString();
        this.name = in.readString();
        this.type = in.readInt();
        this.user = in.readString();
        this.pwd = in.readString();
        this.shareable = in.readByte() != 0;
        this.bgPath = in.readString();
        this.status = in.readInt();
        this.checked = in.readByte() != 0;
        this.userPwdOK = in.readByte() != 0;
    }

    @Generated(hash = 1689970998)
    public EdwinDevice(Long id, String devId, String name, int type, String user, String pwd, boolean shareable, String bgPath,
                       boolean userPwdOK, boolean timeZoneOk) {
        this.id = id;
        this.devId = devId;
        this.name = name;
        this.type = type;
        this.user = user;
        this.pwd = pwd;
        this.shareable = shareable;
        this.bgPath = bgPath;
        this.userPwdOK = userPwdOK;
        this.timeZoneOk = timeZoneOk;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EdwinDevice dev = (EdwinDevice) o;

        return devId != null ? IdUtil.isSameId(devId, dev.devId) : dev.devId == null;

    }

    @Override
    public Object clone() {
        EdwinDevice obj = null;
        try {
            obj = (EdwinDevice) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public boolean isShareable() {
        return shareable;
    }

    public String getBgPath() {
        return bgPath;
    }

    public void setBgPath(String bgPath) {
        this.bgPath = bgPath;
    }
//    public boolean isCamera(){
//        return  type== P2PConstants.DeviceType.IPC ;
//    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void toggleCheck() {
        this.checked = !checked;
    }

    public boolean isIpc() {
        return (type == P2PConstants.DeviceType.IPC || type == P2PConstants.DeviceType.IPFC);
    }

    public boolean isBell() {
        return (type == P2PConstants.DeviceType.BELL_UNIDIRECTIONAL || type == P2PConstants.DeviceType.BELL_BI_DIRECTIONAL
                || type == P2PConstants.DeviceType.CAT_DOUBLE_EYE || type == P2PConstants.DeviceType.CAT_SING_EYE
                || type == P2PConstants.DeviceType.BELL_LTK6112C);
    }

    public boolean isOnline() {
        return P2PConstants.P2PStatus.ON_LINE == status;
    }

    public boolean isSleep() {
        return P2PConstants.P2PStatus.SLEEP == status;
    }

    public boolean isUserPwdOK() {
        return userPwdOK;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getShareable() {
        return this.shareable;
    }

    public void setShareable(boolean shareable) {
        this.shareable = shareable;
    }

    public boolean isTimeZoneOk() {
        return timeZoneOk;
    }

    @Override
    public String toString() {
        return "EdwinDevice{" +
                "id=" + id +
                ", devId='" + devId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", user='" + user + '\'' +
                ", pwd='" + pwd + '\'' +
                ", shareable=" + shareable +
                ", bgPath='" + bgPath + '\'' +
                ", status=" + status +
                ", userPwdOK=" + userPwdOK +
                ", checked=" + checked +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.devId);
        dest.writeString(this.name);
        dest.writeInt(this.type);
        dest.writeString(this.user);
        dest.writeString(this.pwd);
        dest.writeByte(this.shareable ? (byte) 1 : (byte) 0);
        dest.writeString(this.bgPath);
        dest.writeInt(this.status);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.userPwdOK ? (byte) 1 : (byte) 0);
    }

    public boolean getUserPwdOK() {
        return this.userPwdOK;
    }

    public void setUserPwdOK(boolean userPwdOK) {
        this.userPwdOK = userPwdOK;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean isAdded) {
        this.isAdded = isAdded;
    }

    @Override
    public int getItemType() {
        return isAdded ? ITEM_VIEW_ADDED : ITEM_VIEW_NEW;//TODO
    }

    public boolean getTimeZoneOk() {
        return this.timeZoneOk;
    }

    public void setTimeZoneOk(boolean timeZoneOk) {
        this.timeZoneOk = timeZoneOk;
    }

}
