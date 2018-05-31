package com.rl.geye.logic;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.rl.commons.BaseApp;
import com.rl.geye.R;
import com.rl.geye.bean.RingBean;


/**
 * @author NickyHuang
 * @ClassName: DataLogic
 * @Description: 数据信息读写
 * @date 2016-1-11
 */
public class DataLogic {

    private static final String PREFERENCE_RING_BELL = "preference.ring.bell"; //呼叫铃声
    private static final String PREFERENCE_RING_ALARM = "preference.ring.alarm"; //报警铃声
    private static final String PREFERENCE_VIBRATE = "preference.vibrate"; //振动设置
    private static final String PREFERENCE_NO_DISTURB = "preference.no_disturb"; //振动设置
    private static final String PREFERENCE_GUIDE = "preference.guide"; //摇头机导航显示
    private static final String PREFERENCE_JPUSH = "preference.jpush"; //极光推送部分参数( deviceToken 和 alias)
    private static final String PREFERENCE_WIFI = "preference.wifi"; //wifi密码
    private static final String PREFERENCE_TEST = "preference.test"; //
    private static final String PREFERENCE_APP_INSTALL = "preference.app.install"; //

    private static final String RING_NAME = "ring_name"; //铃声名
    //	private static final String RING_TYPE = "ring_type"; //铃声类型
    private static final String RING_REMIND = "ring_remind"; //铃声提示方式
    private static final String RING_URL = "ring_url"; //铃声路径
    private static final String RING_ID = "ring_id"; //铃声id

    private static final String VIBRATE_ENABLE = "vibrate_enable"; //是否振动
    private static final String NO_DISTURB_ENABLE = "no_disturb_enable"; //勿扰

    private static final String SHOW_GUIDE = "show_guide"; //摇头机是否显示引导

    private static final String JPUSH_DEVICE_TOKEN = "jpush_device_token"; //极光推送 deviceToken
    private static final String JPUSH_ALIAS = "jpush_alias"; //极光推送 alias
    private static final String JPUSH_TAG_OK = "jpush_tag_ok"; //极光推送 TAG设置是否OK


    private static final String NEED_TEST_DATA = "need_test_data"; //是否需要测试数据
    private static final String APP_INSTALL = "app_install"; //APP是否安装

    private static SharedPreferences getBellPreference() {
        return BaseApp.context().getSharedPreferences(PREFERENCE_RING_BELL, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getAlarmPreference() {
        return BaseApp.context().getSharedPreferences(PREFERENCE_RING_ALARM, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getVibratePreference() {
        return BaseApp.context().getSharedPreferences(PREFERENCE_VIBRATE, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getNoDisturbPreference() {
        return BaseApp.context().getSharedPreferences(PREFERENCE_NO_DISTURB, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getGuidePreference() {
        return BaseApp.context().getSharedPreferences(PREFERENCE_GUIDE, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getJpushPreference() {
        return BaseApp.context().getSharedPreferences(PREFERENCE_JPUSH, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getWifiPreference() {
        return BaseApp.context().getSharedPreferences(PREFERENCE_WIFI, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getTestPreference() {
        return BaseApp.context().getSharedPreferences(PREFERENCE_TEST, Context.MODE_PRIVATE);
    }

    private static SharedPreferences getAppInstallPreference() {
        return BaseApp.context().getSharedPreferences(PREFERENCE_APP_INSTALL, Context.MODE_PRIVATE);
    }


    public static void saveRing(RingBean ring) {
        SharedPreferences preferences = null;
        switch (ring.getRingType()) {
            case RingBean.RingType.CALL:
                preferences = getBellPreference();
                break;
            case RingBean.RingType.ALARM:
                preferences = getAlarmPreference();
                break;
        }
        if (preferences != null) {
            Editor editor = preferences.edit();//获取编辑器
            editor.putString(RING_NAME, ring.getName());
//			editor.putInt(RING_TYPE, ring.getRingType());
            editor.putInt(RING_REMIND, ring.getRemindType());
            editor.putString(RING_URL, ring.getRingUrl());
            editor.putLong(RING_ID, ring.getId());
            editor.commit();
        }
    }


    public static RingBean getRingBell() {
        SharedPreferences pref = getBellPreference();
        RingBean ring = new RingBean();
//		ring.setRingType(pref.getInt(RING_TYPE, RingBean.RingType.CALL));
        ring.setRingType(RingBean.RingType.CALL);
        ring.setRemindType(pref.getInt(RING_REMIND, RingBean.RingRemindType.DEFAULT));
        ring.setName(pref.getString(RING_NAME, BaseApp.context().getString(R.string.default_ring_2)));
        ring.setRingUrl(pref.getString(RING_URL, ""));
        ring.setId(pref.getLong(RING_ID, 0));
        return ring;
    }

    public static RingBean getRingAlarm() {
        SharedPreferences pref = getAlarmPreference();
        RingBean ring = new RingBean();
//		ring.setRingType(pref.getInt(RING_TYPE, RingBean.RingType.ALARM));
        ring.setRingType(RingBean.RingType.ALARM);
        ring.setRemindType(pref.getInt(RING_REMIND, RingBean.RingRemindType.DEFAULT));
        ring.setName(pref.getString(RING_NAME, BaseApp.context().getString(R.string.default_ring_2)));
        ring.setRingUrl(pref.getString(RING_URL, ""));
        ring.setId(pref.getLong(RING_ID, 0));
        return ring;
    }


    public static void saveVibrateEnable(boolean enable) {
        SharedPreferences pref = getVibratePreference();
        Editor editor = pref.edit();//获取编辑器
        editor.putBoolean(VIBRATE_ENABLE, enable);
        editor.commit();
    }


    public static boolean isVibrateEnable() {
        SharedPreferences pref = getVibratePreference();
        return pref.getBoolean(VIBRATE_ENABLE, true);
    }


    public static void saveNoDisturb(boolean enable) {
        SharedPreferences pref = getNoDisturbPreference();
        Editor editor = pref.edit();//获取编辑器
        editor.putBoolean(NO_DISTURB_ENABLE, enable);
        editor.commit();
    }


    public static boolean isNoDisturb() {
        SharedPreferences pref = getNoDisturbPreference();
        return pref.getBoolean(NO_DISTURB_ENABLE, false);
    }


    public static void saveShowGuide(boolean show) {
        SharedPreferences pref = getGuidePreference();
        Editor editor = pref.edit();//获取编辑器
        editor.putBoolean(SHOW_GUIDE, show);
        editor.commit();
    }


    public static boolean isShowGuide() {
        SharedPreferences pref = getGuidePreference();
        return pref.getBoolean(SHOW_GUIDE, true);
    }


//	public static void saveJpushDeviceToken(String deviceToken){
//		SharedPreferences pref = getJpushPreference();
//		Editor editor = pref.edit();//获取编辑器
//		editor.putString(JPUSH_DEVICE_TOKEN, deviceToken);
//		editor.commit();
//	}
//
//	public static String getJpushDeviceToken(){
//		SharedPreferences pref = getJpushPreference();
//		return pref.getString(JPUSH_DEVICE_TOKEN, "");
//	}
//
//	public static void saveJpushAlias(String alias){
//		SharedPreferences pref = getJpushPreference();
//		Editor editor = pref.edit();//获取编辑器
//		editor.putString(JPUSH_ALIAS, alias);
//		editor.commit();
//	}
//
//	public static String getJpushAlias(){
//		SharedPreferences pref = getJpushPreference();
//		return pref.getString(JPUSH_ALIAS, "");
//	}


    public static void saveJpushTagOk(boolean isOk) {
        SharedPreferences pref = getJpushPreference();
        Editor editor = pref.edit();//获取编辑器
        editor.putBoolean(JPUSH_TAG_OK, isOk);
        editor.commit();
    }

    public static boolean getJpushTagOk() {
        SharedPreferences pref = getJpushPreference();
        return pref.getBoolean(JPUSH_TAG_OK, false);
    }


    public static void saveWifiPwd(String ssid, String pwd) {
        SharedPreferences pref = getWifiPreference();
        Editor editor = pref.edit();//获取编辑器
        editor.putString(ssid, pwd);
//		XLog.i("NickyTag","DataLogic saveWifiPwd : ssid: "+ssid+ " , pwd: "+pwd);
        editor.commit();
    }

    public static String getWifiPwd(String ssid) {
        SharedPreferences pref = getWifiPreference();
        String pwd = pref.getString(ssid, "");
//		XLog.i("NickyTag","DataLogic getWifiPwd : ssid: "+ssid + " , pwd: "+pwd);
        return pwd;
    }


    public static void saveNeedTestData(boolean needTestData) {
        SharedPreferences preferences = getTestPreference();
        Editor editor = preferences.edit();//获取编辑器
        editor.putBoolean(NEED_TEST_DATA, needTestData);
        editor.commit();
    }

    public static boolean needTestData() {
        return getTestPreference().getBoolean(NEED_TEST_DATA, true);
    }

    public static void saveAppInstalled(boolean appInstalled) {
        SharedPreferences preferences = getAppInstallPreference();
        Editor editor = preferences.edit();//获取编辑器
        editor.putBoolean(APP_INSTALL, appInstalled);
        editor.commit();
    }

    public static boolean isAppInstalled() {
        return getAppInstallPreference().getBoolean(APP_INSTALL, false);
    }


}


