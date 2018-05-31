package com.rl.geye.bean;


import com.rl.commons.interf.Chooseable;
import com.rl.geye.R;

/**
 * @author NickyHuang
 * 铃声数据
 */
public class RingBean implements Chooseable {

    private long id;//用于比较(自定义)
    private String name = "";
    private String ringUrl = "";
    private int remindType = RingRemindType.DEFAULT; //提醒方式
    private int ringType = RingType.CALL; //铃声类型(门铃、警报)

    public int getRingType() {
        return ringType;
    }

    public void setRingType(int ringType) {
        this.ringType = ringType;
    }

    public int getRemindType() {
        return remindType;
    }

    public void setRemindType(int remindType) {
        this.remindType = remindType;
    }

    public String getRingUrl() {
        return ringUrl;
    }

    public void setRingUrl(String ringUrl) {
        this.ringUrl = ringUrl;
    }

    public int getRingDefaultResId() {
        if (remindType == RingRemindType.DEFAULT) {
            if (ringType == RingType.CALL)
                return R.raw.ring_call_default;
            else if (ringType == RingType.ALARM)
                return R.raw.ring_alarm_default;
        }
        return -1;
    }

    public String getRingDefaultName() {
        if (remindType == RingRemindType.DEFAULT) {
            if (ringType == RingType.CALL)
                return "ring_call_default";
            else if (ringType == RingType.ALARM)
                return "ring_alarm_default";
        }
        return "";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RingBean other = (RingBean) o;
        if (other.ringType != ringType)
            return false;
        if (other.remindType != remindType)
            return false;
        if (remindType != RingRemindType.CUSTOM)//静音或者默认
        {
            return true;
        }
        return name != null ? name.equals(other.name) : other.name == null;

    }

    @Override
    public boolean hasIcon() {
        return false;
    }

    @Override
    public int getIconResid() {
        return -1;
    }

    @Override
    public String getName() {
        return name;
    }

    //	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//
//		RingBean other = (RingBean) o;
//		if(  other.ringType!= ringType )
//			return false;
//		if(  other.remindType!= remindType )
//			return false;
//		if( remindType !=  RingRemindType.CUSTOM)//静音或者默认
//		{
//			return true;
//		}
//		if (name != null ? !name.equals(other.name) : other.name != null) return false;
//		return ringUrl != null ? ringUrl.equals(other.ringUrl) : other.ringUrl == null;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		RingBean other = (RingBean) obj;
//		if(  other.ringType!= ringType )
//			return false;
//		if(  other.remindType!= remindType )
//			return false;
//		if( remindType !=  RingRemindType.CUSTOM)//静音或者默认
//		{
//			return true;
//		}
//		if (id != other.id)
//			return false;
//		return true;
//	}

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RingBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ringUrl='" + ringUrl + '\'' +
                '}';
    }

    /**
     * 铃声类型
     */
    public class RingType {
        public static final int CALL = 0; //门铃
        public static final int ALARM = 1; //警报
    }

    /**
     * 提醒方式
     */
    public class RingRemindType {
        public static final int DEFAULT = 0;//默认
        public static final int MUTE = 1;//静音
        public static final int CUSTOM = 2; //自定义
    }
}
