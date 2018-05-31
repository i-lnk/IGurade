package com.rl.commons.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.rl.commons.BaseApp;
import com.rl.commons.log.XLog;

import java.util.List;
import java.util.UUID;

/**
 * 
 * @ClassName: AppDevice 
 * @Description: 硬件设备管理类
 * @author NickyHuang
 * @date 2016-3-30 下午8:35:03 
 *
 */

public class AppDevice {
	
	private AppDevice(){}
	
	public static String getProcessName(Context cxt, int pid) {
		ActivityManager am = (ActivityManager) cxt
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
		if (runningApps == null) {
			return null;
		}
		for (RunningAppProcessInfo procInfo : runningApps) {
			if (procInfo.pid == pid) {
				return procInfo.processName;
			}
		}
		return null;
	}
	
	
	// 手机网络类型
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

	// 关闭软键盘
	public static void hideSoftKeyboard(View view) {
		if (view == null)
			return;
		if ( BaseApp.context() != null) {
			((InputMethodManager) BaseApp.context().getSystemService(
					Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					view.getWindowToken(), 0);
		}
	}

	private static Boolean _isTablet = null;

	public static boolean isTablet() {
		if (_isTablet == null) {
			boolean flag;
			if ((0xf & BaseApp.context().getResources().getConfiguration().screenLayout) >= 3)
				flag = true;
			else
				flag = false;
			_isTablet = Boolean.valueOf(flag);
		}
		return _isTablet.booleanValue();
	}

	public static float dpToPixel(float dp) {
		return dp * (getDisplayMetrics().densityDpi / 160F);
	}
	
	/**
     * 描述：sp转换为px.
     * @param spValue the sp value
     * @return sp值
     */
    public static float sp2px(float spValue) {
        return spValue * getDisplayMetrics().scaledDensity;
    }

	public static DisplayMetrics getDisplayMetrics() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		((WindowManager) BaseApp.context().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
				displaymetrics);
		return displaymetrics;
	}

	public static float getScreenHeight() {
		return getDisplayMetrics().heightPixels;
	}

	public static float getScreenWidth() {
		return getDisplayMetrics().widthPixels;
	}

//	public static boolean hasInternet(BaseApp cwApplication) {
//		boolean flag;
//		if (((ConnectivityManager) BaseApp.context().getSystemService(
//				"connectivity")).getActiveNetworkInfo() != null)
//			flag = true;
//		else
//			flag = false;
//		return flag;
//	}
	
	
	/**
     * 获取当前网络类型
     * 
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
	public static int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) BaseApp
				.context().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
	
	/** 
     * 将ip的整数形式转换成ip形式 
     * @param ipInt 
     * @return 
     */  
    public static String int2ip(int ipInt) {  
        StringBuilder sb = new StringBuilder();  
        sb.append(ipInt & 0xFF).append(".");  
        sb.append((ipInt >> 8) & 0xFF).append(".");  
        sb.append((ipInt >> 16) & 0xFF).append(".");  
        sb.append((ipInt >> 24) & 0xFF);  
        return sb.toString();  
    }  
    
    static String long2ip(long ip){  
        StringBuffer sb=new StringBuffer();  
        sb.append(String.valueOf((int)(ip&0xff)));  
        sb.append('.');  
        sb.append(String.valueOf((int)((ip>>8)&0xff)));  
        sb.append('.');  
        sb.append(String.valueOf((int)((ip>>16)&0xff)));  
        sb.append('.');  
        sb.append(String.valueOf((int)((ip>>24)&0xff)));  
        return sb.toString();  
    }  
  
    /** 
     * 获取当前ip地址
     * @return 
     */  
    public static String getLocalIpAddress() {  
        try {  
            WifiManager wifiManager = (WifiManager) BaseApp.context().getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);  
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();  
            int i = wifiInfo.getIpAddress();  
            if(i==0)return "0";  
            return int2ip(i);  
        } catch (Exception ex) {  
            return "0";  
        }  
    } 
    
    /**
     *  获取设备uuid
     * @return
     */
    @SuppressWarnings("static-access")
	public static String getDevUUID() {
		TelephonyManager tm = (TelephonyManager) BaseApp.context()
				.getSystemService(BaseApp.context().TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
				BaseApp.context().getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}
	

	

	
	public static PackageInfo getPackageInfo(String pckName) {
		try {
			return BaseApp.context().getPackageManager()
					.getPackageInfo(pckName, 0);
		} catch (NameNotFoundException e) {
			XLog.e(e.getMessage());
		}
		return null;

	}

	public static int getVersionCode() {
		int versionCode = 0;
		try {
			versionCode = BaseApp
					.context()
					.getPackageManager()
					.getPackageInfo(BaseApp.context().getPackageName(), 0).versionCode;
		} catch (NameNotFoundException ex) {
			versionCode = 0;
		}
		return versionCode;
	}

	public static String getVersionName() {
		String name = "";
		try {
			name = BaseApp
					.context()
					.getPackageManager()
					.getPackageInfo(BaseApp.context().getPackageName(), 0).versionName;
		} catch (NameNotFoundException ex) {
			name = "";
		}
		return name;
	}
	
	
	public static String getNativePhoneNumber(){
		SIMCardMgr simInfo = new SIMCardMgr(BaseApp.context());
		return  simInfo.getNativePhoneNumber();
	}

	public static int getAppUid(){
		int uid;
		try {
			uid = BaseApp
					.context()
					.getPackageManager()
					.getPackageInfo(BaseApp.context().getPackageName(), 0).applicationInfo.uid;
		} catch (NameNotFoundException ex) {
			uid = 0;
		}
		return uid;
	}

	

}
