package com.rl.p2plib.callback;

import com.rl.p2plib.bean.Battery;
import com.rl.p2plib.bean.CallAnswer;
import com.rl.p2plib.bean.DetectInfo;
import com.rl.p2plib.bean.DevSdCard;
import com.rl.p2plib.bean.DevSysSet;
import com.rl.p2plib.bean.DevTimeZone;
import com.rl.p2plib.bean.EdwinVideoGroup;
import com.rl.p2plib.bean.MonthRecord;
import com.rl.p2plib.bean.OsdInfo;
import com.rl.p2plib.bean.PicMode;
import com.rl.p2plib.bean.PowerData;
import com.rl.p2plib.bean.RecordTime;
import com.rl.p2plib.bean.SetResult;
import com.rl.p2plib.bean.SysInfo;
import com.rl.p2plib.bean.UpdateProgress;
import com.rl.p2plib.bean.VoiceData;
import com.rl.p2plib.bean.WakeUpData;
import com.rl.p2plib.bean.WifiData;

/**
 * p2p回调(App库与App页面间的通信)
 *
 * @author Nicky
 * created at 2017/6/16 10:44
 */
public interface P2PAppCallBack {

    /**
     * 设备状态改变
     */
    void onStatusChanged(String did, int type, int param);


    /**
     * 推送状态改变 (警报消除、呼叫已被接听等)
     */
    void onPushTypeChanged(String did, int type);


    /**
     * 搜索结果
     */
    void onSearchResult(int devType, String mac, String name, String did, String ipAddress, int port, int deviceType);


    /**
     * 视屏通话数据
     */
    void onVideoData(String did, byte[] videoBuf, int h264Data, int len, int width, int height, int time);


//    /**
//     * 设置、获取指令 结果--TODO
//     */
//    void onGetSetResult(String did, int msgType, String msgData, int len);

    /**
     * 设置结果
     */
    void onSetResult(String did, int msgType, SetResult setResult);

    /**
     * 获取子设备列表
     */
    void onGetSubList(String did, int msgType, String data);

    /**
     * 获取图像翻转模式
     */
    void onGetPicMode(String did, int msgType, PicMode picMode);


    /**
     * 获取某月的录像数据
     */
    void onGetMonthRecord(String did, int msgType, MonthRecord monthRecord);

    /**
     * 获取录像组数据
     */
    void onGetVideoGroup(String did, int msgType, EdwinVideoGroup videoGroup);

    /**
     * 获取SD卡信息
     */
    void onGetSdCard(String did, int msgType, DevSdCard sdCard);

    /**
     * SD卡设置应答
     */
    void onSdSetAck(String did, int msgType);

    /**
     * 获取OSD信息
     */
    void onGetOsdInfo(String did, int msgType, OsdInfo osdInfo);

    /**
     * 获取移动侦测信息
     */
    void onGetDetectInfo(String did, int msgType, DetectInfo detectInfo);

    /**
     * 获取自动录像时间
     */
    void onGetRecordTime(String did, int msgType, RecordTime recordTime);

    /**
     * 获取时区
     */
    void onGetTimeZone(String did, int msgType, DevTimeZone timeZone);

    /**
     * 获取设备系统设置
     */
    void onGetDevSysSet(String did, int msgType, DevSysSet sysSet);

    /**
     * 设置设备系统设置
     */
    void onSetDevSysSet(String did, int msgType, DevSysSet sysSet);

    /**
     * 获取设备信息
     */
    void onGetSysInfo(String did, int msgType, SysInfo sysInfo);

    /**
     * 获取电池电量
     */
    void onGetBattery(String did, int msgType, Battery battery);

//    /**
//     * 升级应答
//     */
//    void onUpdateAck(String did, int msgType);

    /**
     * 升级进度
     */
    void onUpdateProgress(String did, int msgType, UpdateProgress updateProgress);

    /**
     * 其它用户已接听
     */
    void onOtherUserAnswered(String did, int msgType, CallAnswer callAnswer);

    /**
     * 设置推送成功
     */
    void onPushSetOk(String did, int msgType);

    /**
     * 获取WIFI
     */
    void onGetWifiInfo(String did, int msgType, WifiData wifiInfo);

    /**
     * 设置时区
     */
    void onSetTimeZone(String did, int msgType, DevTimeZone timeZone);

    /**
     * 设置移动侦测
     */
    void onSetDetect(String did, int msgType, DetectInfo detectInfo);

    /**
     * 设置电源频率
     */
    void onSetPower(String did, int msgType, PowerData powerData);

    /**
     * 获取电源频率
     */
    void onGetPower(String did, int msgType, PowerData powerData);

    /**
     * 设置音量
     */
    void onSetVoice(String did, int msgType, VoiceData voiceData);

    /**
     * 获取音量
     */
    void onGetVoice(String did, int msgType, VoiceData voiceData);

    /**
     * 获取远程唤醒
     */
    void onGetWakeUp(String did, int msgType, WakeUpData wakeUpData);
}
