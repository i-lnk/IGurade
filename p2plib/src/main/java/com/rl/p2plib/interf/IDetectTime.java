package com.rl.p2plib.interf;

/**
 * Created by Nicky on 2016/10/20.
 * 布防时间、自动录像时间
 */
public interface IDetectTime {

     boolean isOn();
     String getStartTimeStr();
     String getEndTimeStr();
     String getFormatTimeStr();
     boolean isNightMode();
     boolean isDayMode();
     boolean isCustomMode();

     void setNightMode();
     void setDayMode();
}
