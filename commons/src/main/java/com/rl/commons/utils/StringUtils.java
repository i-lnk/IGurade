package com.rl.commons.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.rl.commons.BaseApp;
import com.rl.commons.log.XLog;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * @ClassName: StringUtils 
 * @Description: 字符串工具类
 * @author NickyHuang
 * @date 2016-3-18 上午1:05:54 
 *
 */
public class StringUtils {
	private StringUtils(){
	}

	@SuppressLint("SimpleDateFormat")
	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	public static boolean isBlank(String paramString) {
		if (paramString != null) {
			if (paramString.length() != 0) {
				for (int i = 0; i < paramString.length(); i++) {
					if (!Character.isWhitespace(paramString.charAt(i))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/** 判断字符串是否为空 */
	public static boolean isEmpty(String paramString) {

		if (paramString == null || "".equals(paramString)
				|| "null".equals(paramString))
			return true;

		for (int i = 0; i < paramString.length(); i++) {
			char c = paramString.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/** 判断字符串的是否为全是空格 */
	public static boolean isAllBank(String paramString) {

		if (StringUtils.isEmpty(paramString.replaceAll("", "")))
			return true;

		return false;
	}

	private static final Pattern URL_PATTERN = Pattern
			.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

	/** 判断是否为网页 */
	public static boolean isURL(String url) {
		if (url == null || url.trim().length() == 0)
			return false;
		return StringUtils.URL_PATTERN.matcher(url).matches();
	}

	/** 判断手机格式是否正确 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^1[3|4|5|6|7|8|][0-9]{9}$");

		Matcher m = p.matcher(mobiles);

		return m.matches();
	}
	
	/** 判断是否为合法IP */
	public static boolean isIP(String addr) {
        if ( isEmpty(addr) || addr.length() < 7 || addr.length() > 15 ) {
            return false;
        }
        String rexp = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";

        // return addr.matches(rexp);
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.matches();
        return ipAddress;
    }
	

	/** 字符串转Long */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/** 字符串转整数 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}
	
	public static int toInt(String str){
		return toInt(str, 0);
	}

//	/** 对象转整数 */
//	public static int toInt(Object obj) {
//		if (obj == null)
//			return -1;
//		return toInt(obj.toString(), -1);
//	}

	/** 字符串转浮点型 */
	public static float toFloat(String str, float defValue) {
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	public static boolean hasSpecialCharacter(String str) {
		String regEx = "^[\u4E00-\u9FA5A-Za-z0-9_]+$";
		return str.matches(regEx);
	}

	@SuppressLint("SimpleDateFormat")
	private final static ThreadLocal<SimpleDateFormat> dateDay = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	/**
	 * 将时间戳转为代表"距现在多久之前"的字符串
	 * 
	 * @param timeStr
	 *            时间戳
	 * @return
	 */
	public static String getFriendlyDate(String timeStr) {

		long t = toLong(timeStr);

		if (isEmpty(timeStr) || t == 0)
			return "null";

		StringBuffer sb = new StringBuffer();

		long time = System.currentTimeMillis() - (t * 1000);
		long mill = (long) Math.ceil(time / 1000);// 秒前

		long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

		long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

		long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

		day = day - 1;
		if (day > 7) {
			sb.append(dateDay.get().format(t * 1000));
		} else if (day > 2 && day <= 7) {
			sb.append(day + "天前");
		} else if (day == 2) {
			sb.append("前天");
		} else if (day == 1) {
			sb.append("昨天");
		} else if (hour - 1 > 0) {

			if (hour >= 24) {
				sb.append("昨天");
			} else {
				sb.append(hour + "小时前");
			}
		} else if (minute - 1 > 0) {
			if (minute == 60) {
				sb.append("1小时前");
			} else {
				sb.append(minute + "分钟前");
			}
		} else if (mill - 1 > 0) {
			if (mill == 60) {
				sb.append("1分钟前");
			} else {
				sb.append(mill + "秒前");
			}
		} else {
			sb.append("刚刚");
		}
		return sb.toString();
	}

	@SuppressLint("SimpleDateFormat")
	private final static ThreadLocal<SimpleDateFormat> dateMouth = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("MM-dd HH:mm");
		}
	};

	/**
	 * 将时间戳转为(MM-DD HH:MM)格式的字符串
	 * 
	 * @param timeStr
	 *            时间戳
	 * @return
	 */
	public static String getDate(String timeStr) {
		long t = toLong(timeStr);

		if (isEmpty(timeStr) || t == 0)
			return "null";

		StringBuffer sb = new StringBuffer();

		sb.append(dateMouth.get().format(t * 1000));

		return sb.toString();
	}

	/** 获取当前时间 */
	public static String getCurTimeStr() {
		Calendar cal = Calendar.getInstance();
		String curDate = dateFormater.get().format(cal.getTime());
		return curDate;
	}

	/** 计算两个时间差，返回的是的秒s */
	public static long calDateDifferent(String dete1, String date2) {

		long diff = 0;
		Date d1 = null;
		Date d2 = null;

		try {
			d1 = dateFormater.get().parse(dete1);
			d2 = dateFormater.get().parse(date2);

			// 毫秒ms
			diff = d2.getTime() - d1.getTime();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return diff / 1000;
	}
	
	/**
	 * 将数字转化为一个字节的表示方式
	 */
	public static String formatNum(int num) {
		return num < 10 ? "0" + num : "" + num;
	}

	/** 生成uuid */
	public static String newUuid()
	{		 
		return UUID.randomUUID().toString();
	}
	
	/** 生成session(16位) */
	public static String newSession()
	{		
		//26个字母
		String[] letterArray = new String[]{
				"A","B","C","D","E","F","G","H","I","J",
				"K","L","M","N","O","P","Q","R","S","T",
				"U","V","W","X","Y","Z"
		};
		String millsStr = String.format("%013d", System.currentTimeMillis());								
		//String.valueOf(System.currentTimeMillis());
		//XLog.e("######################## millsStr is :"+millsStr);						
		int millsIndex = 0;//记录下标
		
		Random random = new Random();
		String extraStr1 = letterArray[ random.nextInt(26) ];
		String extraStr2 = letterArray[ random.nextInt(26) ];
		String extraStr3 = letterArray[ random.nextInt(26) ];
		
		int extraIndex1  = random.nextInt(16);
		
		int extraIndex2  = random.nextInt(16);
		while( extraIndex1==extraIndex2 )
		{
			extraIndex2 = random.nextInt(16);
		}
		
		int extraIndex3  = random.nextInt(16);
		while( extraIndex2==extraIndex3 || extraIndex1==extraIndex3 )
		{
			extraIndex3 = random.nextInt(16);
		}
		String session = "";
		for(int i=0;i<16;i++ ){
			if( i== extraIndex1 ){
				session += extraStr1;
			}else if( i== extraIndex2 ){
				session += extraStr2;
			}else if( i== extraIndex3 ){
				session += extraStr3;
			}else{
				session += millsStr.charAt(millsIndex);
				millsIndex++;
			}
		}
		XLog.i("######################## session is :"+session);
		return session;
	}
	

	/** 获取手机Ip(WIFI或3G) */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
					.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					// 这里需要注意：这里增加了一个限定条件( inetAddress instanceof Inet4Address
					// ),主要是在Android4.0高版本中可能优先得到的是IPv6的地址。参考：http://blog.csdn.net/stormwy/article/details/8832164
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			XLog.e("getLocalIpAddress", ex.toString());
		}
		return "";
	}
	
	
	/** 获取手机Ip(WIFI) */
	public static String getLocalIpAddressWifi() {
		String ipString = "0.0.0.0";
		WifiManager wifimanage = (WifiManager) BaseApp.context().getApplicationContext()
				.getSystemService(Context.WIFI_SERVICE);// 获取WifiManager
		// 检查wifi是否开启 没开启wifi时,ip地址为0.0.0.0
		if (wifimanage.isWifiEnabled()) {
			WifiInfo wifiinfo = wifimanage.getConnectionInfo();
			int ipAddress = wifiinfo.getIpAddress();
			// 获得IP地址的方法一：
			if (ipAddress != 0) {
				ipString = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff)
						+ "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
			}
		}
		return ipString;
	}
	
	
	 /**
     * 得到一个格式化的时间
     *
     * @param time
     *            时间 秒
     * @return 时：分：秒
     */
    public static  String getFormatTime(long time) {
        // long millisecond = time % 1000;
        long second = time % 60;
        long minute = (time % 3600) / 60;
        long hour = time / 3600;

//        // 毫秒秒显示两位
//        // String strMillisecond = "" + (millisecond / 10);
//        // 秒显示两位
//        String strSecond = ("00" + second)
//                .substring(("00" + second).length() - 2);
//        // 分显示两位
//        String strMinute = ("00" + minute)
//                .substring(("00" + minute).length() - 2);
//        // 时显示两位
//        String strHour = ("00" + hour).substring(("00" + hour).length() - 2);
//        return strHour + ":" + strMinute + ":" + strSecond;
        
        return String.format("%02d:%02d:%02d", hour,minute,second);
        // + strMillisecond;
    }
	
    
    /**
     *  判断字符是否为中文(算法1)
     */
    public static boolean isChinese(char a) { 
    	int v = (int)a; 
    	return (v >=19968 && v <= 171941); 
    }
	
    /**
     *  判断字符是否为中文(算法2)
     */
    public static boolean isChineseChar(char c) { 
    	Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
    	
		if ( ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
			|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
			|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
			|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
			|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
			|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
		) {
			return true;
		}
		return false;
    }


	public static String replaceBlank(String str){
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

}
