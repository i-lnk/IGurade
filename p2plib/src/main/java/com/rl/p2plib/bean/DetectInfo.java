package com.rl.p2plib.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.rl.p2plib.interf.IDetectTime;

/**
 * @author NickyHuang
 * @ClassName: DetectInfo
 * @Description: 移动侦测信息
 * @date 2016-6-30 下午4:34:05
 */
public class DetectInfo implements Cloneable, IDetectTime, Parcelable {

    private int enable = 0;
    private int start_hour = 0;
    private int start_mins = 0;
    private int close_hour = 0;
    private int close_mins = 0;

//    private int frequency = 0;

    private int record;
    private int audio;
    //灵敏度值
    private int enablePir=0; //pir=0时 防拆报警关
    private int removeAlarm; //防拆
    private int enableRemoveAlaram;  //防拆报警
    public DetectInfo() {
    }

    @Override
    public Object clone() {
        DetectInfo obj = null;
        try {
            obj = (DetectInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DetectInfo that = (DetectInfo) o;

        if (enable != that.enable) return false;
//        if (frequency != that.frequency) return false;
        if (start_hour != that.start_hour) return false;
        if (start_mins != that.start_mins) return false;
        if (close_hour != that.close_hour) return false;
        if (close_mins != that.close_mins) return false;
        if (record != that.record) return false;
        return audio == that.audio;

    }

    @Override
    public int hashCode() {
        int result = enable;
        result = 31 * result + start_hour;
        result = 31 * result + start_mins;
        result = 31 * result + close_hour;
        result = 31 * result + close_mins;
        result = 31 * result + record;
        result = 31 * result + audio;
        return result;
    }

//    public int getMode() {
//        return frequency;
//    }
//
//    public void setMode(int frequency) {
//        this.frequency = frequency;
//    }

    public int getRecord() {
        return record;
    }

    public void setRecord(int record) {
        this.record = record;
    }

    public int getAudio() {
        return audio;
    }

    public void setAudio(int audio) {
        this.audio = audio;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }


    public int getStart_hour() {
        return start_hour;
    }

    public void setStart_hour(int start_hour) {
        this.start_hour = start_hour;
    }

    public int getStart_mins() {
        return start_mins;
    }

    public void setStart_mins(int start_mins) {
        this.start_mins = start_mins;
    }

    public int getClose_hour() {
        return close_hour;
    }

    public void setClose_hour(int close_hour) {
        this.close_hour = close_hour;
    }

    public int getClose_mins() {
        return close_mins;
    }

    public void setClose_mins(int close_mins) {
        this.close_mins = close_mins;
    }

    public int getEnablePir() {
        return enablePir;
    }

    public void setEnablePir(int enablePir) {
        this.enablePir = enablePir;
    }

    public int getRemoveAlarm() {
        return removeAlarm;
    }

    public void setRemoveAlarm(int removeAlarm) {
        this.removeAlarm = removeAlarm;
    }

    public int getEnableRemoveAlaram() {
        return enableRemoveAlaram;
    }

    public void setEnableRemoveAlaram(int enableRemoveAlaram) {
        this.enableRemoveAlaram = enableRemoveAlaram;
    }

    /**********************************************************/
    @Override
    public boolean isOn() {
        return enable != 0;
    }

    @Override
    public String getStartTimeStr() {
        return String.format("  %02d:%02d  ", start_hour, start_mins);
    }

    @Override
    public String getEndTimeStr() {
        return String.format("  %02d:%02d  ", close_hour, close_mins);
    }

    @Override
    public String getFormatTimeStr() {
        return String.format("%02d:%02d ~ %02d:%02d", start_hour, start_mins, close_hour, close_mins);
    }

    @Override
    public boolean isNightMode() {
        return isOn() && (start_hour == 22 && close_hour == 6 && start_mins == 0 && close_mins == 0);
    }

    @Override
    public boolean isDayMode() {
        return isOn() && (start_hour == 0 && close_hour == 23 && start_mins == 0 && close_mins == 0);
    }

    @Override
    public boolean isCustomMode() {
        return isOn() && !isNightMode() && !isDayMode();
    }

    @Override
    public void setNightMode() {
        start_hour = 22;
        close_hour = 6;
        start_mins = 0;
        close_mins = 0;
    }

    @Override
    public void setDayMode() {
        start_hour = 0;
        close_hour = 23;
        start_mins = 0;
        close_mins = 59;
    }

    /**********************************************************/


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.enable);
        dest.writeInt(this.start_hour);
        dest.writeInt(this.start_mins);
        dest.writeInt(this.close_hour);
        dest.writeInt(this.close_mins);
        dest.writeInt(this.record);
        dest.writeInt(this.audio);
        dest.writeInt(this.enablePir);
        dest.writeInt(this.removeAlarm);
        dest.writeInt(this.enableRemoveAlaram);
//        dest.writeInt(this.frequency);
    }

    protected DetectInfo(Parcel in) {
        this.enable = in.readInt();
        this.start_hour = in.readInt();
        this.start_mins = in.readInt();
        this.close_hour = in.readInt();
        this.close_mins = in.readInt();
        this.record = in.readInt();
        this.audio = in.readInt();
        this.enablePir = in.readInt();
        this.removeAlarm = in.readInt();
        this.enableRemoveAlaram=in.readInt();
//        this.frequency = in.readInt();
    }

    public static final Parcelable.Creator<DetectInfo> CREATOR = new Parcelable.Creator<DetectInfo>() {
        @Override
        public DetectInfo createFromParcel(Parcel source) {
            return new DetectInfo(source);
        }

        @Override
        public DetectInfo[] newArray(int size) {
            return new DetectInfo[size];
        }
    };
}
