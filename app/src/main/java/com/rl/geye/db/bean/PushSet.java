package com.rl.geye.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by Nicky on 2016/10/7.
 * (推送设置--处理结果)
 */
@Entity
//        (indexes = {
//        @Index(value = "dev_type DESC")
//})
public class PushSet {

    @Id(autoincrement = true)
    private Long id;

    @Unique
    @Property(nameInDb = "push_did")
    private String devId = "";//设备ID

    @Property(nameInDb = "push_dev_push_ok")
    private boolean devPushOk = false;//设备推送 是否已设置完成

    @Property(nameInDb = "push_fcm_topic_ok")
    private boolean fcmTopicOK = false;//FCM主题 是否已设置完成

    @Property(nameInDb = "push_is_delete")
    private boolean isDeleted = false;//是否已删除


    @Generated(hash = 382263373)
    public PushSet(Long id, String devId, boolean devPushOk, boolean fcmTopicOK,
                   boolean isDeleted) {
        this.id = id;
        this.devId = devId;
        this.devPushOk = devPushOk;
        this.fcmTopicOK = fcmTopicOK;
        this.isDeleted = isDeleted;
    }

    @Generated(hash = 751908008)
    public PushSet() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }


    public boolean isFcmTopicOK() {
        return fcmTopicOK;
    }

    public boolean getFcmTopicOK() {
        return this.fcmTopicOK;
    }

    public void setFcmTopicOK(boolean fcmTopicOK) {
        this.fcmTopicOK = fcmTopicOK;
    }

    public boolean isDevPushOk() {
        return devPushOk;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean getIsDeleted() {
        return this.isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean getDevPushOk() {
        return this.devPushOk;
    }

    public void setDevPushOk(boolean devPushOk) {
        this.devPushOk = devPushOk;
    }

    @Override
    public String toString() {
        return "PushSet{" +
                "id=" + id +
                ", devId='" + devId + '\'' +
                ", devPushOk=" + devPushOk +
                ", fcmTopicOK=" + fcmTopicOK +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
