package com.rl.p2plib.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.rl.p2plib.interf.IDetectTime;

/**
 * 
 * @ClassName: RecordTime
 * @Description: 自动录像时间
 * @author NickyHuang
 * @date 2016-6-30 下午4:34:05 
 *
 */
public class RecordTime implements Parcelable,Cloneable,IDetectTime {
	
	private int channel = 0;
	private int type = 0;//0--->都没开启 1---》手动录像
	private int start_hour = 0;
	private int start_mins = 0;
	private int close_hour = 0;
	private int close_mins = 0;
	private int video_lens = 0;
	public RecordTime() {
	}

	@Override
	public Object clone(){
		RecordTime obj = null;
		try {
			obj = (RecordTime) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}


	
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

    public int getVideo_lens() {
        return video_lens;
    }

    public void setVideo_lens(int video_lens) {
        this.video_lens = video_lens;
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

	/**********************************************************/
	@Override
	public boolean isOn(){
		return type!=0;
	}

	@Override
	public String getStartTimeStr(){
		return String.format("  %02d:%02d  ",start_hour,start_mins);
	}

	@Override
	public String getEndTimeStr(){
		return String.format("  %02d:%02d  ",close_hour,close_mins);
	}

	@Override
	public String getFormatTimeStr(){
		return String.format("%02d:%02d ~ %02d:%02d",start_hour,start_mins,close_hour,close_mins);
	}

	@Override
	public boolean isNightMode(){
		return isOn() && ( start_hour == 22 && close_hour == 6 && start_mins==0 && close_mins==0 );
	}

	@Override
	public boolean isDayMode(){
		return isOn() && ( start_hour == 0 && close_hour == 23 && start_mins==0 && close_mins==0 );
	}

	@Override
	public boolean isCustomMode(){
		return isOn() && !isNightMode() && !isDayMode();
	}

	@Override
	public void setNightMode(){
		start_hour = 22 ;
		close_hour = 6 ;
		start_mins = 0 ;
		close_mins = 0;
	}

	@Override
	public void setDayMode(){
		start_hour = 0 ;
		close_hour = 23 ;
		start_mins = 0 ;
		close_mins = 0;
	}
	/**********************************************************/

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.channel);
		dest.writeInt(this.type);
		dest.writeInt(this.start_hour);
		dest.writeInt(this.start_mins);
		dest.writeInt(this.close_hour);
		dest.writeInt(this.close_mins);
		dest.writeInt(this.video_lens);
	}



	protected RecordTime(Parcel in) {
		this.channel = in.readInt();
		this.type = in.readInt();
		this.start_hour = in.readInt();
		this.start_mins = in.readInt();
		this.close_hour = in.readInt();
		this.close_mins = in.readInt();
		this.video_lens = in.readInt();
	}

	public static final Creator<RecordTime> CREATOR = new Creator<RecordTime>() {
		@Override
		public RecordTime createFromParcel(Parcel source) {
			return new RecordTime(source);
		}

		@Override
		public RecordTime[] newArray(int size) {
			return new RecordTime[size];
		}
	};
}
