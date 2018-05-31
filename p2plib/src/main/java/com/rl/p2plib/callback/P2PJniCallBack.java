package com.rl.p2plib.callback;

/**
 * Created by Nicky on 2017/4/5.
 * p2p回调(JNI库与APP端通信接口----JNI调用APP端)
 */
public interface P2PJniCallBack {

    /**
     * 视屏通话回调
     */
    void VideoData(String did, byte[] videoBuf, int h264Data, int len, int width, int height, int time);


    /**
     * 设备连接状态 回调
     */
    void PPPPMsgNotify(String did, int type, int param);


    /**
     * 搜索设备 回调
     */
    void SearchResult(int cameraType, String mac, String name, String did, String ipAddress, int port, int deviceType);


    /**
     * 推送消息(报警、来电) 回调
     */
    void CallBack_AlarmNotifyDoorBell(String did, String dbDid, String dbType, String dbTime);

    /**
     * get、set回调(新)
     */
    void UILayerNotify(String did, int msgType, byte[] data ,int len);



}
