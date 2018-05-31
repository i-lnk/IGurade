package com.rl.p2plib.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.rl.commons.utils.DateUtil;


/**
 * Created by Nicky on 2017/3/23.
 * 回放视频
 */
public class EdwinVideo implements Parcelable {

    private String start; //yyyy-MM-dd HH-mm-ss
    private String close;
    private int event;
    private int status;
    private int reply;
    public EdwinVideo() {
        super();
    }


    public String getName(){
        return start+" ~ "+close;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public int getTotalTime(){
        return (int)( getEndTimeMills() - getStartTimeMills() );
    }

    public long getStartTimeMills(){
        return DateUtil.getDateMills(start);
    }

    public long getEndTimeMills(){
        return DateUtil.getDateMills(close);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.start);
        dest.writeString(this.close);
        dest.writeInt(this.event);
        dest.writeInt(this.status);
        dest.writeInt(this.reply);
    }

    protected EdwinVideo(Parcel in) {
        this.start = in.readString();
        this.close = in.readString();
        this.event = in.readInt();
        this.status = in.readInt();
        this.reply = in.readInt();
    }

    public static final Creator<EdwinVideo> CREATOR = new Creator<EdwinVideo>() {
        @Override
        public EdwinVideo createFromParcel(Parcel source) {
            return new EdwinVideo(source);
        }

        @Override
        public EdwinVideo[] newArray(int size) {
            return new EdwinVideo[size];
        }
    };
}
