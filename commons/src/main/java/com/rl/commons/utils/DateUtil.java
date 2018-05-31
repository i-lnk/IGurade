package com.rl.commons.utils;

import android.annotation.SuppressLint;

import com.rl.commons.BaseApp;
import com.rl.commons.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtil {
	
	private DateUtil(){
	}
	
	public static String getCommTimeStr(long milliseconds) {
		return getFormatStr(new SimpleDateFormat("yyyy-MM-dd HH:mm"), milliseconds);
	}


	public static String getCommTimeStr2(long milliseconds) {
		return getFormatStr(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), milliseconds);
	}

	public static String getCommTimeStr3(long milliseconds) {
		return getFormatStr(new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss"), milliseconds);
	}

	@SuppressWarnings("deprecation")
	public static long timestamp(int year, int month, int day) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		date.setYear(year - 1900);
		date.setMonth(month);
		date.setDate(day);
		try {
			System.out.println((simpleDateFormat.parse(simpleDateFormat
					.format(date))).getTime());
			return (simpleDateFormat.parse(simpleDateFormat.format(date)))
					.getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static long timestamp(long milliseconds) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		date.setTime(milliseconds);
		try {
			return (simpleDateFormat.parse(simpleDateFormat.format(date)))
					.getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 当前时间戳
	 */
	public static long nowTimestamp() {
		return timestamp(System.currentTimeMillis());
	}

	public static long getTimestamp(String birthday) {
		String[] strs = birthday.split("-");
		int[] date = new int[3];
		for (int i = 0; i < strs.length; i++) {
			date[i] = StringUtils.toInt(strs[i]);
		}
		return timestamp(date[0], date[1], date[2]);
	}

	
	public static long getDateMills(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return getDateMills(dateStr, sdf);
	}
	
	
	/**
	 * @Description: 将日期转为毫秒数 ,
	 * @param dateStr
	 *            日期格式为("yyyy-MM-dd")
	 * @param isDayStart
	 *            是否返回当天00:00:00 的时间戳,如果不是则返回当天23:59:59的时间戳
	 */
	public static long getDateMills(String dateStr, boolean isDayStart) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return getDateMills(dateStr, sdf, isDayStart);
	}

	/**
	 * 将日期转为毫秒数 ,日期格式为("yyyy.MM.dd")
	 */
	public static long getDateMillsV2(String dateStr, boolean isDayStart) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		return getDateMills(dateStr, sdf, isDayStart);
	}

	/**
	 * 将日期转为毫秒数 ,日期格式为("yyyyMMdd")
	 */
	public static long getDateMillsV3(String dateStr, boolean isDayStart) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return getDateMills(dateStr, sdf, isDayStart);
	}
	
	
	public static long getDateMillsV4(String dateStr, boolean isDayStart) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return getDateMills(dateStr, sdf, isDayStart);
	}



	/**
	 * 将日期转为毫秒数
	 * 
	 * @param isDayStart
	 *            是否返回当天00:00:00 的时间戳,如果不是则返回当天23:59:59的时间戳
	 */
	public static long getDateMills(String dateStr, SimpleDateFormat sdf,
			boolean isDayStart) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		try {
			cal.setTime(sdf.parse(dateStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (isDayStart)
			return getTimeStampDayStart(cal);
		else
			return getTimeStampDayEnd(cal);
		// return cal.getTimeInMillis();
	}
	
	public static long getDateMills(String dateStr, SimpleDateFormat sdf) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		try {
			cal.setTime(sdf.parse(dateStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cal.getTimeInMillis();
	}
	

	/** 获取当天 00:00:00 时间戳 **/
	public static long getTodayStart() {
		Calendar cal = Calendar.getInstance();
		return getTimeStampDayStart(cal);
	}

	/** 获取当天 23:59:59 时间戳 **/
	public static long getTodayEnd() {
		Calendar cal = Calendar.getInstance();
		return getTimeStampDayEnd(cal);
	}

	/** 获取当天 日期yyyyMMdd **/
	public static String getTodayStr() {
		return getDateStr3(System.currentTimeMillis());
	}

	/** 获取 00:00:00 时间戳 **/
	public static long getTimeStampDayStart(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		// System.out.println(cal.getTime().getTime());
		return cal.getTimeInMillis();
	}

	/** 获取 23:59:59 时间戳 **/
	public static long getTimeStampDayEnd(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 0);
		// System.out.println(cal.getTime().getTime());
		return cal.getTimeInMillis();
	}

	/** 获取 时间(时:分:秒) **/
	public static String getTimeStr(long milliseconds) {
		return getFormatStr(new SimpleDateFormat("HH:mm:ss"), milliseconds);
	}

	/** 获取 时间(小时:分钟) **/
	public static String getTimeStr2(long milliseconds) {
		return getFormatStr(new SimpleDateFormat("HH:mm"), milliseconds);
	}
	
	/** 获取 时间(分:秒) **/
	public static String getTimeStr3(long milliseconds) {
		return getFormatStr(new SimpleDateFormat("mm:ss"), milliseconds);
	}

	/** 获取 日期(年-月-日) **/
	public static String getDateStr(long milliseconds) {
		return getFormatStr(new SimpleDateFormat("yyyy-MM-dd"), milliseconds);
	}
	

	/** 获取 日期(年.月.日) **/
	public static String getDateStr2(long milliseconds) {
		return getFormatStr(new SimpleDateFormat("yyyy.MM.dd"), milliseconds);
	}

	public static String getDateStr3(long milliseconds) {
		return getFormatStr(new SimpleDateFormat("yyyyMMdd"), milliseconds);
	}
	
	public static String getDateStr4(long milliseconds) {
		return getFormatStr(new SimpleDateFormat("yyyy年MM月dd日"), milliseconds);
	}

	/** 获取 日期(年-月) **/
	public static String getDateStr5(long milliseconds) {
		return getFormatStr(new SimpleDateFormat("yy-MM"), milliseconds);
	}

	/** 毫秒数转日期字符串 **/
	public static String getFormatStr(SimpleDateFormat format, long milliseconds) {
		Date date = new Date();
		date.setTime(milliseconds);
		return format.format(date);
	}

	/**
	 * 获取两个日期之间的间隔天数
	 * 
	 * @return
	 */
	public static int getGapCount(Date startDate, Date endDate) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime()
				.getTime()) / (1000 * 60 * 60 * 24));
	}
	
	
	/**
	 * 获取目标日期到当天的间隔天数
	 * @param mills 目标日期毫秒数
	 * @return
	 */
	public static int getGapCountToToday(long mills) {

		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTimeInMillis(mills);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);
		
		
		long gapMills = Math.abs( toCalendar.getTimeInMillis() -  fromCalendar.getTimeInMillis() );
		return (int) ( gapMills/(1000 * 60 * 60 * 24)  );

	}
	
	/**
	 * @param mills 毫秒数
	 * @return 记录时间(今天以时间显示,昨天,上周时间已星期显示,更久之前以日期显示)
	 */
	public static String getFormatRecordTime(long mills,boolean withTime){
    	if ( mills < 0  )//|| mills>System.currentTimeMillis()
			return "";
    	else if ( mills>System.currentTimeMillis() ){
    		return getDateStr(mills);
    	}

    	Calendar timeCal = Calendar.getInstance();
    	timeCal.setTimeInMillis(mills);
    	
    	String timeStr = "";
    	int day = getGapCountToToday(mills);
    	switch( day ){
    	
    	case 0: //当天
    		int amPm = timeCal.get(Calendar.AM_PM);
    		timeStr = BaseApp.context().getString( amPm==Calendar.AM ? R.string.am : R.string.pm);
    		if(withTime)
    			timeStr +=   " "+getTimeStr2(mills);
    		break;
    		
    	case 1: //昨天
    		timeStr = BaseApp.context().getString(R.string.yesterday);
    		break;
    			
    	case 2: 
    	case 3:
    	case 4:
    	case 5:
    	case 6://一周内
    		int dayInWeek = timeCal.get(Calendar.DAY_OF_WEEK );
    		if( Calendar.SUNDAY== dayInWeek){
    			timeStr = BaseApp.context().getString(R.string.sunday_full);
    		}else if( Calendar.MONDAY== dayInWeek ){
    			timeStr = BaseApp.context().getString(R.string.monday_full);
    		}else if( Calendar.TUESDAY== dayInWeek ){
    			timeStr = BaseApp.context().getString(R.string.tuesday_full);
    		}else if( Calendar.WEDNESDAY== dayInWeek ){
    			timeStr = BaseApp.context().getString(R.string.wednesday_full);
    		}else if( Calendar.THURSDAY== dayInWeek ){
    			timeStr = BaseApp.context().getString(R.string.thursday_full);
    		}else if( Calendar.FRIDAY== dayInWeek ){
    			timeStr = BaseApp.context().getString(R.string.friday_full);
    		}else if( Calendar.SATURDAY== dayInWeek ){
    			timeStr = BaseApp.context().getString(R.string.saturday_full);
    		}
    		break;
    	default:
    		timeStr = getDateStr(mills);
    		break;
    	}
    	
    	return timeStr;
    }

}
