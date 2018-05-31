package com.rl.p2plib.p2pconnect;

import com.edwintech.vdp.jni.Avapi;
import com.rl.commons.log.XLog;
import com.rl.commons.utils.StringUtils;
import com.rl.p2plib.constants.P2PConstants;
import com.rl.p2plib.interf.IP2PDevice;

/**
 * Created by Nicky on 2017/6/19.
 * P2P 连接、断开 线程
 */
public class P2PConnectThreads {
    private final static String TAG = "P2PConnectThreads";

    /**
     * 设备刷新线程
     */
    public static class RefreshDeviceThread<T extends IP2PDevice> implements Runnable {
        private T mDevice; //更新设备
        private boolean needClose;

        public RefreshDeviceThread(T device, boolean needClose) {
            this.mDevice = device;
            this.needClose = needClose;
        }

        @Override
        public void run() {
            if (mDevice == null)
                return;
            XLog.i(TAG, "###############DevRefreshThread : ");
//            /**---主动设为正在连接---*/
//            if(BridgeService.isReady()){
//                BridgeService.getInstance().PPPPMsgNotify(mDevice.getDevId(), P2PConstants.PPPPMsgType.PPPP_STATUS, P2PConstants.P2PStatus.CONNECTING);
//            }
            if (needClose) {
                closePPPP(mDevice);
                mySleep(200);
            }
            startPPPP(mDevice);
        }
    }

//    /** 设备刷新[多个设备 主线程] */
//    public static class RefreshDevicesMainThread<T extends IP2PDevice> {
//
//        private List<T> devList; //设备
//
//
//        public RefreshDevicesMainThread(List<T> devList){
//            this.devList = devList;
//        }
//
//        public void start(){
//            if( devList==null||devList.isEmpty() )
//                return;
//            for( IP2PDevice device:devList ){
////            if ( device.getStatus() != Constants.DeviceStatus.PPPP_STATUS_ON_LINE
////                    && device.getStatus() != Constants.DeviceStatus.PPPP_STATUS_NOT_LOGIN ) {
//                if ( device.getStatus() != P2PConstants.P2PStatus.ON_LINE
//                        && device.getStatus() != P2PConstants.P2PStatus.NOT_LOGIN ) {
//                    mySleep(100);
//                    XLog.i(TAG, "###############restartPPPP : " + device);
////                    /**---主动设为正在连接---*/
////                    if(BridgeService.isReady()){
////                        BridgeService.getInstance().PPPPMsgNotify(device.getDevId(), P2PConstants.PPPPMsgType.PPPP_STATUS, P2PConstants.P2PStatus.CONNECTING);
////                    }
//                    closePPPP( device );
//                    mySleep(200);
//                    startPPPP( device );
//                }
//            }
//        }
//
//    }


//    /** 设备连接线程(多个设备) */
//    public static class ConnectDevicesThread<T extends IP2PDevice> implements Runnable{
//
//        private List<T> devList; //设备
//
//        public ConnectDevicesThread(List<T> devList){
//            this.devList = devList;
//        }
//
//        @Override
//        public void run() {
//            if( devList==null)
//                return;
//            for ( T device : devList ){
////                /**---主动设为正在连接---*/
////                if(BridgeService.isReady()){
////                    BridgeService.getInstance().PPPPMsgNotify(device.getDevId(), P2PConstants.PPPPMsgType.PPPP_STATUS, P2PConstants.P2PStatus.CONNECTING);
////                }
//                startPPPP(device);
//                mySleep(50);
//            }
//        }
//    }

    /**
     * 设备连接线程(单个设备)
     */
    public static class ConnectDeviceThread<T extends IP2PDevice> implements Runnable {

        private IP2PDevice mDevice; //设备

        public ConnectDeviceThread(T dev) {
            this.mDevice = dev;
        }

        @Override
        public void run() {
            if (mDevice == null)
                return;
//            /**---主动设为正在连接---*/
//            if(BridgeService.isReady()){
//                BridgeService.getInstance().PPPPMsgNotify(mDevice.getDevId(), P2PConstants.PPPPMsgType.PPPP_STATUS, P2PConstants.P2PStatus.CONNECTING);
//            }
            startPPPP(mDevice);
        }
    }


    /**
     * 设备中断线程(单个设备)
     */
    public static class StopDeviceThread implements Runnable {

        private String mDevId; //

        public StopDeviceThread(String devId) {
            this.mDevId = devId;
        }

        @Override
        public void run() {
            if (StringUtils.isEmpty(mDevId))
                return;
            XLog.i(TAG, "###############DevStopThread : ");
//            /**---主动设为未知状态---*/
//            if(BridgeService.isReady()){
//                int  status = BridgeService.getInstance().getDeviceStatus(mDevId);
//                if( status == P2PConstants.P2PStatus.CONNECTED ){
//                    BridgeService.getInstance().PPPPMsgNotify(mDevId, P2PConstants.PPPPMsgType.PPPP_STATUS, P2PConstants.P2PStatus.UNKNOWN);
//                }
//            }
            closePPPP(mDevId);
        }
    }

//    /** 设备中断线程(多个个设备)  */
//    public static class StopDevicesThread<T extends IP2PDevice> implements Runnable{
//
//        private List<T> devList; //设备
//
//        public StopDevicesThread(List<T> devList){
//            this.devList = devList;
//        }
//
//        @Override
//        public void run() {
//
//            if( devList==null)
//                return;
//            for ( T device : devList ){
////                /**---主动设为未知状态---*/
////                if(BridgeService.isReady()){
////                    int  status = BridgeService.getInstance().getDeviceStatus(device.getDevId());
////                    if( status == P2PConstants.P2PStatus.CONNECTED ){
////                        BridgeService.getInstance().PPPPMsgNotify(device.getDevId(), P2PConstants.PPPPMsgType.PPPP_STATUS, P2PConstants.P2PStatus.UNKNOWN);
////                    }
////                }
//                closePPPP(device);
//                mySleep(50);
//            }
//
//        }
//    }

    /**********************************************************************************************************************************
     * ********************************************************************************************************************************
     * ********************************************************************************************************************************/

    private static void startPPPP(IP2PDevice device) {
        if (device != null) {
            startPPPP(device.getDevId(), device.getUser(), device.getPwd());
        }
    }

    private static void startPPPP(String did, String user, String pwd) {
        XLog.i(TAG, "------------------->startPPPP [ " + did + "]");
        Avapi.StartPPPP(did, user, pwd, P2PConstants.P2P_SERVER, P2PConstants.P2P_TYPE);
        XLog.i(TAG, "------------------->startPPPP [ " + did + "] OVER");
    }

    private static void closePPPP(IP2PDevice device) {
        if (device != null) {
            closePPPP(device.getDevId());
        }
    }

    private static void closePPPP(String did) {
        if (!StringUtils.isEmpty(did)) {
            XLog.i(TAG, "------------------->closePPPP [ " + did + "]");
            Avapi.ClosePPPP(did);
            XLog.i(TAG, "------------------->closePPPP [ " + did + "] OVER");
        }
    }


    public static void mySleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
