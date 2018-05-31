package com.rl.p2plib.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nicky on 2017/3/30.
 */

public class SysInfo implements Parcelable {

    private int devType;
    private int language;
    private String version;
    private String model;
    private int odm;

    private int supportStorage;
    private int supportPTZ;
    private int supportPIR;
    private int supportRemoveAlarm;
    private int supportAudioIn;
    private int supportAudioOut;
    private int supportWakeUpControl;


    public int getDevType() {
        return devType;
    }

    public void setDevType(int devType) {
        this.devType = devType;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getOdm() {
        return odm;
    }

    public void setOdm(int odm) {
        this.odm = odm;
    }

    public int getSupportStorage() {
        return supportStorage;
    }

    public void setSupportStorage(int supportStorage) {
        this.supportStorage = supportStorage;
    }

    public int getSupportPTZ() {
        return supportPTZ;
    }

    public void setSupportPTZ(int supportPTZ) {
        this.supportPTZ = supportPTZ;
    }

    public int getSupportPIR() {
        return supportPIR;
    }

    public void setSupportPIR(int supportPIR) {
        this.supportPIR = supportPIR;
    }

    public int getSupportRemoveAlarm() {
        return supportRemoveAlarm;
    }

    public void setSupportRemoveAlarm(int supportRemoveAlarm) {
        this.supportRemoveAlarm = supportRemoveAlarm;
    }

    public int getSupportAudioIn() {
        return supportAudioIn;
    }

    public void setSupportAudioIn(int supportAudioIn) {
        this.supportAudioIn = supportAudioIn;
    }

    public int getSupportAudioOut() {
        return supportAudioOut;
    }

    public void setSupportAudioOut(int supportAudioOut) {
        this.supportAudioOut = supportAudioOut;
    }

    public int getSupportWakeUpControl() {
        return supportWakeUpControl;
    }

    public void setSupportWakeUpControl(int supportWakeUpControl) {
        this.supportWakeUpControl = supportWakeUpControl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.devType);
        dest.writeInt(this.language);
        dest.writeString(this.version);
        dest.writeString(this.model);
        dest.writeInt(this.odm);
        dest.writeInt(this.supportStorage);
        dest.writeInt(this.supportPTZ);
        dest.writeInt(this.supportPIR);
        dest.writeInt(this.supportRemoveAlarm);
        dest.writeInt(this.supportAudioIn);
        dest.writeInt(this.supportAudioOut);
        dest.writeInt(this.supportWakeUpControl);
    }

    public SysInfo() {
    }

    protected SysInfo(Parcel in) {
        this.devType = in.readInt();
        this.language = in.readInt();
        this.version = in.readString();
        this.model = in.readString();
        this.odm = in.readInt();
        this.supportStorage = in.readInt();
        this.supportPTZ = in.readInt();
        this.supportPIR = in.readInt();
        this.supportRemoveAlarm = in.readInt();
        this.supportAudioIn = in.readInt();
        this.supportAudioOut = in.readInt();
        this.supportWakeUpControl=in.readInt();
    }

    public static final Parcelable.Creator<SysInfo> CREATOR = new Parcelable.Creator<SysInfo>() {
        @Override
        public SysInfo createFromParcel(Parcel source) {
            return new SysInfo(source);
        }

        @Override
        public SysInfo[] newArray(int size) {
            return new SysInfo[size];
        }
    };
}
