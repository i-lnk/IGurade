package com.edwintech.vdp.jni;

import android.util.Base64;

import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.log.XLog;
import com.rl.p2plib.bean.DetectInfo;
import com.rl.p2plib.bean.DevSysSet;
import com.rl.p2plib.bean.PowerData;
import com.rl.p2plib.bean.RecordTime;
import com.rl.p2plib.bean.SysVersion;
import com.rl.p2plib.bean.VoiceData;
import com.rl.p2plib.bean.WakeUpData;
import com.rl.p2plib.constants.CmdConstant;
import com.rl.p2plib.constants.PushConfig;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nicky on 2017/1/4.
 * jni api 管理
 */
@SuppressWarnings(value = {"WeakerAccess"})
public class ApiMgrV2 {
    private final static String TAG = "ApiMgr";

    private ApiMgrV2() {
    }

//    private final static int gwChannel = 0xff;

    /**
     * 发送指令执行结果回调
     */
    public interface SendCmdCallBack {
        void onSuccess();

        void onFailed();
    }


    /**
     * JNI接口参数拼接
     */
    private static String getParamsStr(Map<String, String> params) {
        String paramsStr = "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramsStr += entry.getKey() + "=";
            String value = entry.getValue();
            String encrypt = new String(Base64.encode(value.getBytes(), Base64.NO_WRAP));
            paramsStr += encrypt + "&";
        }
        return paramsStr;
    }

    /**
     * 设置镜头(IPBOX)
     */
    public static void setLens(String devId, int lens) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("index", String.valueOf(lens));
        String msg = "setLens.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.SET_LENS, msg, null, 0);
    }

    /**
     * 回放暂停或播放
     */
    public static void palybackPausePlay(String devId) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("channel", String.valueOf(0));
        params.put("command", String.valueOf(0));
        params.put("param", String.valueOf(6));
        params.put("eventtime", "");
        String msg = "setPlaybackProgress.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.PLAYBACK_PROGRESS_SET, msg, null, 0);
    }

    /**
     * 设置回放进度
     */
    public static void setPlaybackProgress(String devId, String time) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("channel", String.valueOf(0));
        params.put("command", String.valueOf(6));
        params.put("param", String.valueOf(6));
        params.put("eventtime", time);
        String msg = "setPlaybackProgress.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.PLAYBACK_PROGRESS_SET, msg, null, 0);
    }

    /**
     * 通过月份获取播放记录
     *
     * @param devId
     * @param month YY-MM
     */
    public static void getMonthRecord(String devId, String month) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("time", month);
        XLog.i(TAG, "----------getMonthRecord:" + month);
        String msg = "getMonthRecord.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.GET_MONTH_RECORD, msg, null, 0);
    }

    /**
     * 获取SD卡录像列表
     *
     * @param devId
     * @param startTime yyyy-MM-dd HH:mm:ss
     * @param endTime   yyyy-MM-dd HH:mm:ss
     */
    public static void getVideoList(String devId, String startTime, String endTime) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("channel", String.valueOf(0));
        params.put("command", String.valueOf(4));
        params.put("param", String.valueOf(0));
        params.put("starttime", startTime);
        params.put("closetime", endTime);
        String msg = "getVideoList.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.VIDEOLIST_GET, msg, null, 0);
    }

    /**
     * 设置预置位
     */
    public static void setPresetPos(String devId, int index) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("index", String.valueOf(index));
        String msg = "setPresetPos.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.PRESET_POSITION_SET, msg, null, 0);
        XLog.i(TAG, "---------------------------setPresetPos：" + index);
    }

    /**
     * 转到预置位
     */
    public static void gotoPresetPos(String devId, int index) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("index", String.valueOf(index));
        String msg = "setPresetPos.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.PRESET_POSITION_GOTO, msg, null, 0);
    }

    /**
     * 获取设备系统
     */
    public static void getDevSys(String devId) {
        String msg = "";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.DEV_SYS_GET, msg, null, 0);
    }

    /**
     * 升级设备系统
     */
    public static void updateDevSys(String devId, SysVersion sysVer) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("type", "1");
        params.put("url", sysVer.getUrl());
        params.put("md5", sysVer.getMd5());
        String msg = "updateDevSys.cgi?" + getParamsStr(params);
        XLog.i(TAG, " updateDevSys: " + msg);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.DEV_SYS_UPDATE, msg, null, 0);
    }

    public static void getUpdateProgress(String devId) {
        String msg = "";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.DEV_SYS_UPDATE_PROGRESS, msg, null, 0);
    }

    /**
     * 获取图像模式(翻转、镜像)
     */
    public static void getPicMode(String devId, SendCmdCallBack callBack, int retryTimes) {
        String msg = "getvideo.cgi";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.GET_PIC_MODE, msg, callBack, retryTimes);
    }

    /**
     * 图像控制
     */
    public static void setPicMode(String devId, int flip, int mirror) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("flip", String.valueOf(flip));
        params.put("mirror", String.valueOf(mirror));
        String msg = "setvideo.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.SET_PIC_MODE, msg, null, 0);
    }


    /**
     * PTZ控制
     */
    public static void setPtzCtrl(String devId, int ptzCmd) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("control", String.valueOf(ptzCmd));
        params.put("speed", String.valueOf(1));
        String msg = "setptz.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.PTZ_CTRL, msg, null, 0);
    }

    /**
     * 开锁
     */
    public static void setUnlock(String devId, int doorNum) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("doornumb", String.valueOf(doorNum));
        params.put("opentime", String.valueOf(1));
        params.put("doorpass", String.valueOf(123456));
        String msg = "setlock.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.UNLOCK, msg, null, 0);
    }

    /**
     * 码流控制
     *
     * @param channel 0：主码流  1：次码流
     * @param quality 0：高清  1：均衡 2：流畅
     */
    public static void setStream(String devId, int channel, int quality) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("channel", String.valueOf(channel));
        params.put("quality", String.valueOf(quality));
        String msg = "setstreamctrl.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.SET_STREAM, msg, null, 0);
    }

    /**
     * 设置推送
     */
    public static void setPush(String devId) {
        String alias = devId.replace("-", "");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("AppKey", PushConfig.jAppkey);
        params.put("Master", PushConfig.jMasterkey);
        params.put("Alias", alias);
        params.put("Type", String.valueOf(PushConfig.jType));
        params.put("FCMKey", PushConfig.FCM_Key);
        String msg = "setpush.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_SET_PUSH_REQ, msg, null, 0);
    }

    /**
     * 删除推送
     */
    public static void delPush(String devId) {
        String alias = devId.replace("-", "");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("AppKey", PushConfig.jAppkey);
        params.put("Master", PushConfig.jMasterkey);
        params.put("Alias", alias);
        params.put("Type", String.valueOf(PushConfig.jType));
        params.put("FCMKey", PushConfig.FCM_Key);
        String msg = "delpush.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_DEL_PUSH_REQ, msg, null, 0);
    }

    /**
     * 开始配对
     */
    public static void startConfig433(String devId, String name, int type) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("type", String.valueOf(type));
        params.put("name", name);
        String msg = "cfg433.cgi?" + getParamsStr(params);
        XLog.i(TAG, "--------------------------startConfig433 ");
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_CFG_433_REQ, msg, null, 0);
    }

    /**
     * 停止配对
     */
    public static void stopConfig433(String devId) {
        String msg = "cfg433exit.cgi";
        XLog.i(TAG, "--------------------------stopConfig433 ");
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_CFG_433_EXIT_REQ, msg, null, 0);
    }

    /**
     * 保存移动侦测设置
     */
    public static void saveDetect(String devId, DetectInfo detect, boolean isIpc) {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("enable", String.valueOf(detect.getEnable()));
        params.put("level", String.valueOf(5));
        params.put("delay", String.valueOf(30));
        params.put("startHour", String.valueOf(detect.getStart_hour()));
        params.put("startMins", String.valueOf(detect.getStart_mins()));
        params.put("closeHour", String.valueOf(detect.getClose_hour()));
        params.put("closeMins", String.valueOf(detect.getClose_mins()));
        if (isIpc) {
            params.put("notify", String.valueOf(1));
            params.put("record", String.valueOf(detect.getRecord()));
            params.put("audio", String.valueOf(detect.getAudio()));
        }
        params.put("enablePir", String.valueOf(detect.getEnablePir()));
        params.put("removeAlarm", String.valueOf(detect.getRemoveAlarm()));
//        params.put("frequency",String.valueOf(detect.getMode()));

        XLog.i(TAG, "saveDetect--->params:" + params);

        String msg = "setmotion.cgi?" + getParamsStr(params);

        sendCMDInThreadPool(devId, CmdConstant.CmdType.SET_DETECT_YTJ, msg, null, 0);

    }

    /**
     * 保存自动录像
     */
    public static void saveAutoRecord(String devId, RecordTime recordTime) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("channel", String.valueOf(0));
        params.put("type", String.valueOf(recordTime.getType()));
        params.put("startHour", String.valueOf(recordTime.getStart_hour()));
        params.put("startMins", String.valueOf(recordTime.getStart_mins()));
        params.put("closeHour", String.valueOf(recordTime.getClose_hour()));
        params.put("closeMins", String.valueOf(recordTime.getClose_mins()));
        params.put("videoLens", String.valueOf(recordTime.getVideo_lens()));
        String msg = "setrecordschedule.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.SET_RECORD_TIME, msg, null, 0);
    }

    /**
     * 保存电源
     */
    public static void savePower(String devId, PowerData mPowerData) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("channel", String.valueOf(0));
        params.put("mode", String.valueOf(mPowerData.getMode()));
        String msg = getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.IOTYPE_USER_IPCAM_SET_POWER_FREQUENCY_REQ, msg, null, 0);
    }

    /**
     * 保存音量
     */
    public static void saveVoice(String devId, VoiceData mVoiceData) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("audioVolume", String.valueOf(mVoiceData.getAudioVolume()));
        String msg = getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.IOTYPE_USER_IPCAM_SET_AUDIO_VOLUME_REQ, msg, null, 0);

    }

    /**
     * 获取设备移动侦测
     */
    public static void getDetect(String devId) {
        String msg = "";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.GET_DETECT_YTJ, msg, null, 0);

    }

    /**
     * 获取设备Wifi
     */
    public static void getDevWifi(String devId) {
        String msg = "";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.GET_WIFI, msg, null, 0);
    }

    /**
     * 修改密码
     */
    public static void updatePwd(String devId, String devUser, String devPwd, String newPwd) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("user", devUser);
        params.put("oldpass", devPwd);
        params.put("newpass", newPwd);
        String msg = "setpassword.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_SETPASSWORD_REQ, msg, null, 0);
    }

    /**
     * 格式化SD卡
     */
    public static void formatSdCard(String devId) {
        String msg = "formatsdcard.cgi";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_SET_SDCARD_REQ, msg, null, 0);
    }


    /**
     * 获取设备SD卡状态
     */
    public static void getSdCard(String devId) {
        String msg = "getsdcardstatus.cgi";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_GET_SDCARD_REQ, msg, null, 0);
    }

    /**
     * 删除子设备
     */
    public static void deleteSubDev(String devId, String subId) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", subId);
        String msg = "del433.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_DEL_433_REQ, msg, null, 0);
    }


    /**
     * 修改子设备
     */
    public static void updateSubDev(String devId, String subId, String name, int type) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("uuid", subId);
        params.put("type", String.valueOf(type));
        String nameEncoder;
        try {
            nameEncoder = URLEncoder.encode(name, "UTF-8");
        } catch (Exception e) {
            nameEncoder = name;
        }
        params.put("name", nameEncoder);
        String msg = "set433.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_SET_433_REQ, msg, null, 0);
    }


    /**
     * 获取设备名
     */
    public static void getDevName(String devId) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("channel", String.valueOf(0));
        String msg = "getosd.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_GET_OSD_REQ, msg, null, 0);
    }

    /**
     * 设置设备名
     */
    public static void setDevName(String devId, String name) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("channel", String.valueOf(0));
        params.put("channel_name_text", name);
        String msg = "setosd.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_SET_OSD_REQ, msg, null, 0);
    }

    /**
     * 获取录像时间
     */
    public static void getAutoRecord(String devId) {
        String msg = "";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.GET_RECORD_TIME, msg, null, 0);
    }

    /**
     * 获取电源
     */
    public static void getPower(String devId) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("channel", String.valueOf(0));
        String msg = "getEvn.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.IOTYPE_USER_IPCAM_GET_POWER_FREQUENCY_REQ, msg, null, 0);
    }

    /**
     * 获取音量
     */
    public static void getVoice(String devId) {
        String msg = "";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.IOTYPE_USER_IPCAM_GET_AUDIO_VOLUME_REQ, msg, null, 0);
    }

    /**
     * 获取设备时区
     */
    public static void getDevTimeZone(String devId) {
        String msg = "gettimezone.cgi";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_GET_TIMEZONE_REQ, msg, null, 0);
    }


    /**
     * 设置时区
     */
    public static void setTimeZone(String devId, int timeZone) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("timezone", String.valueOf(timeZone));
        String msg = "settimezone.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_SET_TIMEZONE_REQ, msg, null, 0);
    }

    /**
     * 获取设备系统设置
     */
    public static void getSysSet(String devId) {
        String msg = "";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.GET_SYS_SET, msg, null, 0);
    }


    /**
     * 设置设备系统设置
     */
    public static void setSysSet(String devId, DevSysSet sysSet) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("power", String.valueOf(1));
        params.put("language", String.valueOf(sysSet.getLanguage()));
        params.put("enableAutomicUpdate", String.valueOf(sysSet.getEnableAutomicUpdate())); //自动升级
        params.put("enablePreviewUnlock", String.valueOf(sysSet.getEnablePreviewUnlock())); //监视开锁
        params.put("enableRingingButton", String.valueOf(sysSet.getEnableRingingButton())); //撞击门铃
        params.put("datetime", "");
        String msg = "setsystem.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.SET_SYS_SET, msg, null, 0);
    }

    /**
     * 设置设备时间
     */
    public static void setSysTime(String devId, String time) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("power", String.valueOf(1));
        params.put("language", String.valueOf(-1));
        params.put("enableAutomicUpdate", String.valueOf(-1));
        params.put("enablePreviewUnlock", String.valueOf(-1));
        params.put("enableRingingButton", String.valueOf(-1));
        params.put("datetime", time);
        String msg = "setsystem.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.SET_SYS_SET, msg, null, 0);
    }

    /**
     * 重启控制
     */
    public static void rebootReset(String devId, boolean isReboot, DevSysSet sysSet) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("power", String.valueOf(isReboot ? 0 : 3));
        params.put("language", String.valueOf(sysSet.getLanguage()));
        String msg = "setsystem.cgi?" + getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.SET_SYS_SET, msg, null, 0);
    }

    /**
     * 获取子设备列表
     */
    public static void getSubDevList(String devId) {
        String msg = String.format("get433.cgi");
        sendCMDInThreadPool(devId, CmdConstant.CmdType.USER_IPCAM_GET_433_REQ, msg, null, 0);
    }


    /**
     * 挂断或者接听
     */
    public static void hangUpOrAnswer(String devId, boolean isHangup, SendCmdCallBack callBack, int retryTimes) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("index", "0");
        params.put("ack", String.valueOf(isHangup ? 0 : 1));
        String msg = "hangup.cgi?" + getParamsStr(params);
        XLog.i(TAG, "hangUpOrAnswer------->isHangup: " + isHangup);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.IOTYPE_XM_CALL_RESP, msg, callBack, retryTimes);
    }


    /**
     * 获取设备电池电量
     */
    public static void getBattery(String devId, SendCmdCallBack callBack, int retryTimes) {
        String msg = "";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.IOTYPE_USER_IPCAM_GET_BATTERY_REQ, msg, callBack, retryTimes);
    }

    /**
     * 获取远程唤醒
     */
    public static void getWakeUpReq(String devId) {
        String msg = "";
        sendCMDInThreadPool(devId, CmdConstant.CmdType.IOTYPE_USER_IPCAM_GET_WAKEUP_FUN_REQ, msg, null, 0);
    }

    /**
     * 保存远程唤醒
     */
    public static void saveWakeUp(String devId, WakeUpData mWakeUpData) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("enable", String.valueOf(mWakeUpData.getEnable()));
        String msg = getParamsStr(params);
        sendCMDInThreadPool(devId, CmdConstant.CmdType.IOTYPE_USER_IPCAM_SET_WAKEUP_FUN_REQ, msg, null, 0);

    }

//    /** 获取子设备列表 */
//    public static void getVideoList(String devId,String startTime,String endTime){
//        String msg = String.format("get433.cgi");
////        channel=%d&command=0x4&param=%d&starttime=%d-%d-%d %d:%d:%d&closetime=%d-%d-%d %d:%d:%d
//        Avapi.SendCtrlCommand(devId, CmdConstant.CmdType.USER_IPCAM_GET_433_REQ, msg, msg.length());
//    }


    /*********************************** 指令在线程池中发送防止UI层卡住**********************************************/

    /**
     * retryTimes   -1:  一直重发直至成功
     * 0: 不重发
     * >0: 重发次数
     */
    private static void sendCMDInThreadPool(String did, int msgType, String msg, SendCmdCallBack callBack, int retryTimes) {
        ThreadPoolMgr.getCustomThreadPool2().submit(new SendCMDThread(did, msgType, msg, callBack, retryTimes));
//        Callable sendCmdTask = new SendCMDThread(did, msgType, msg, msgLens);
//        Future<?> sendCmdFuture =  ThreadPoolMgr.getCustomThreadPool2().submit(sendCmdTask);
//        new TimeOutThread( sendCmdTask ,sendCmdFuture ).start();

    }


    /**
     * retryTimes   -1:  一直重发直至成功
     * 0: 不重发
     * >0: 重发次数
     */
    public static class SendCMDThread implements Runnable {

        private String did;
        private String msg;
        private int msgType;
        private SendCmdCallBack mCallBack;
        private int mRetryTimes;


        public SendCMDThread(String did, int msgType, String msg, SendCmdCallBack callBack, int retryTimes) {
            this.did = did;
            this.msgType = msgType;
            this.msg = msg;
            this.mCallBack = callBack;
            this.mRetryTimes = retryTimes;
        }

        @Override
        public void run() {
            int ret = -1;
            int retry = 0;
            if (mRetryTimes == 0) {
                ret = Avapi.SendCtrlCommand(did, msgType, msg, msg.length());
                if (mCallBack != null) {
//                    XLog.w(TAG, "********** SendCtrlCommand res: " + ret);
                    if (ret == 0)
                        mCallBack.onSuccess();
                    else
                        mCallBack.onFailed();
                }
            } else if (mRetryTimes == -1) {
                while (ret != 0) {
                    if (retry > 0) {
                        XLog.w(TAG, String.format("********** CMD:%x -- retry time:%d ", msgType, retry));
                    }
                    ret = Avapi.SendCtrlCommand(did, msgType, msg, msg.length());
//                    XLog.w(TAG, "********** SendCtrlCommand res: " + ret);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    retry++;
                }
                if (mCallBack != null) {
                    mCallBack.onSuccess();
                }
            } else if (mRetryTimes > 0) {
                //TODO
                ret = Avapi.SendCtrlCommand(did, msgType, msg, msg.length());
            }


        }
    }

//    public static class SendCMDThread implements Callable {
//
//        private String did;
//        private String msg;
//        private int msgType;
//        private int msgLens;
//
//        public SendCMDThread(String did, int msgType, String msg, int msgLens){
//            this.did = did;
//            this.msgType = msgType;
//            this.msg = msg;
//            this.msgLens = msgLens;
//        }
//
////        @Override
////        public void run() {
////            XLog.i("********************SendCtrlCommand--->msgType: "+msgType +" ,msg:" +msg );
////            Avapi.SendCtrlCommand(did, msgType, msg, msg.length());
////            XLog.i("******************** SendCtrlCommand--->end " );
////        }
//
//        @Override
//        public Object call() throws Exception {
//            XLog.i("********************SendCtrlCommand--->msgType: "+msgType +" ,msg:" +msg );
//            Avapi.SendCtrlCommand(did, msgType, msg, msg.length());
//            XLog.i("******************** SendCtrlCommand--->end " );
//            return null;
//        }
//    }
//
//    public static class TimeOutThread extends Thread{
//
//        Callable task;
//        Future<?> future;
//
//
//        public TimeOutThread(Callable task,Future<?> future){
//            this.task = task;
//            this.future = future;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            XLog.i("********************SendCtrlCommand--->TimeOutThread: " );
//            try {
//                future.get( 5000, TimeUnit.MILLISECONDS );
//    //            task.throwException()
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (TimeoutException e) {
//                e.printStackTrace();
//            }finally {
//    //            task.setStop(true);
//                future.cancel(true);
//            }
//            XLog.i("********************SendCtrlCommand--->TimeOutThread: END " );
//        }
//    }


}
