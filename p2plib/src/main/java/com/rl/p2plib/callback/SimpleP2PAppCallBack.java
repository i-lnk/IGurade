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
 * Created by Nicky on 2017/6/16.
 */
public class SimpleP2PAppCallBack implements P2PAppCallBack {

    @Override
    public void onStatusChanged(String did, int type, int param) {

    }

    @Override
    public void onPushTypeChanged(String did, int type) {

    }

    @Override
    public void onSearchResult(int cameraType, String mac, String name, String strDevId, String ipAddress, int port, int deviceType) {

    }

    @Override
    public void onVideoData(String did, byte[] videoBuf, int h264Data, int len, int width, int height, int time) {

    }

    @Override
    public void onSetResult(String did, int msgType, SetResult setResult) {

    }

    @Override
    public void onGetSubList(String did, int msgType, String data) {

    }

    @Override
    public void onGetPicMode(String did, int msgType, PicMode picMode) {

    }

    @Override
    public void onGetMonthRecord(String did, int msgType, MonthRecord monthRecord) {

    }

    @Override
    public void onGetVideoGroup(String did, int msgType, EdwinVideoGroup videoGroup) {

    }

    @Override
    public void onGetSdCard(String did, int msgType, DevSdCard sdCard) {

    }

    @Override
    public void onSdSetAck(String did, int msgType) {

    }

    @Override
    public void onGetOsdInfo(String did, int msgType, OsdInfo osdInfo) {

    }

    @Override
    public void onGetDetectInfo(String did, int msgType, DetectInfo detectInfo) {

    }

    @Override
    public void onGetRecordTime(String did, int msgType, RecordTime recordTime) {

    }

    @Override
    public void onGetTimeZone(String did, int msgType, DevTimeZone timeZone) {

    }

    @Override
    public void onGetDevSysSet(String did, int msgType, DevSysSet sysSet) {

    }

    @Override
    public void onSetDevSysSet(String did, int msgType, DevSysSet sysSet) {

    }

    @Override
    public void onGetSysInfo(String did, int msgType, SysInfo sysInfo) {

    }

    @Override
    public void onGetBattery(String did, int msgType, Battery battery) {

    }

    @Override
    public void onUpdateProgress(String did, int msgType, UpdateProgress updateProgress) {

    }

//    @Override
//    public void onUpdateAck(String did, int msgType) {
//
//    }

    @Override
    public void onOtherUserAnswered(String did, int msgType, CallAnswer callAnswer) {

    }


    @Override
    public void onPushSetOk(String did, int msgType) {

    }

    @Override
    public void onGetWifiInfo(String did, int msgType, WifiData wifiInfo) {

    }

    @Override
    public void onSetTimeZone(String did, int msgType, DevTimeZone timeZone) {

    }

    @Override
    public void onSetDetect(String did, int msgType, DetectInfo detectInfo) {

    }

    @Override
    public void onSetPower(String did, int msgType, PowerData powerData) {
        
    }

    @Override
    public void onGetPower(String did, int msgType, PowerData powerData) {

    }

    @Override
    public void onSetVoice(String did, int msgType, VoiceData voiceData) {

    }

    @Override
    public void onGetVoice(String did, int msgType, VoiceData voiceData) {

    }

    @Override
    public void onGetWakeUp(String did, int msgType, WakeUpData wakeUpData) {

    }


}
