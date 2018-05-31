package com.rl.p2plib.constants;

/**
 * Created by Nicky on 2017/6/19.
 * 指令相关常量
 */

public class CmdConstant {

    private CmdConstant() {
    }

    public class CmdType {
        public static final int PTZ_CTRL = 0x1001;    //摇头机方向操控

        public static final int GET_WIFI = 0x344;        //获取设备wifi
        public static final int RESP_GET_WIFI2 = 0x345; //获取设备wifi返回
        public static final int RESP_GET_WIFI = 0x347;    //获取设备wifi返回

        public static final int SET_STREAM = 0x320;    //设置码流
        public static final int GET_STREAM = 0x322;    //获取码流

        public static final int SET_PIC_MODE = 0x370;   //设置图像模式
        public static final int GET_PIC_MODE = 0x372;   //获取图像模式(翻转、镜像)
        public static final int RESP_GET_PIC_MODE = 0x373;   //获取图像模式返回(翻转、镜像)

        public static final int SET_RECORD_TIME = 0x310; //设置录像时间
        public static final int GET_RECORD_TIME = 0x312; //获取录像时间
        public static final int RESP_GET_RECORD_TIME = 0x313; //获取录像时间返回

        public static final int USER_IPCAM_SET_PUSH_REQ = 0x802;    // 消息推送注册
        public static final int USER_IPCAM_SET_PUSH_RESP = 0x803;    // 消息推送注册应答
        public static final int USER_IPCAM_DEL_PUSH_REQ = 0x804;    // 消息推送注销
        public static final int USER_IPCAM_DEL_PUSH_RESP = 0x805;    // 消息推送注销应答

        public static final int SET_DETECT = 0x806;        //设置移动侦测
        public static final int RESP_SET_DETECT = 0x807;    //设置移动侦测返回
        public static final int GET_DETECT = 0x808;        //获取移动侦测
        public static final int RESP_GET_DETECT = 0x809;    //获取移动侦测返回
//        public static final int RESP_GET_DETECT2 = 0x327; //获取移动侦测返回2


        public static final int GET_DETECT_YTJ = 0x0882;        //获取移动侦测(摇头机)
        public static final int RESP_GET_DETECT_YTJ = 0x0883;    //获取移动侦测返回(摇头机)
        public static final int SET_DETECT_YTJ = 0x0884;        //设置移动侦测(摇头机)
        public static final int RESP_SET_DETECT_YTJ = 0x0885;    //设置移动侦测返回(摇头机)


        public static final int SET_SYS_SET = 0x852;    //重启、复位、设置语言
        public static final int SET_SYS_SET_RESP = 0x853; //重启、复位、设置语言返回

        public static final int GET_SYS_SET = 0x854; //获取设备语言
        public static final int GET_SYS_SET_RESP = 0x855; //获取设备语言返回

        public static final int UNLOCK = 0x800;        //开锁

        public static final int USER_IPCAM_GET_TIMEZONE_REQ = 0x3A0; //获取时区
        public static final int USER_IPCAM_GET_TIMEZONE_RESP = 0x3A1; //获取时区返回
        public static final int USER_IPCAM_SET_TIMEZONE_REQ = 0x3B0; //设置时区
        public static final int USER_IPCAM_SET_TIMEZONE_RESP = 0x3B1; //设置时区返回


        public static final int USER_IPCAM_SET_SDCARD_REQ = 0x380; // 设置SD卡
        public static final int USER_IPCAM_SET_SDCARD_RESP = 0x381; // 设置SD卡应答
        public static final int USER_IPCAM_GET_SDCARD_REQ = 0x856; // 获取SD卡状态
        public static final int USER_IPCAM_GET_SDCARD_RESP = 0x857; //

        public static final int USER_IPCAM_SETPASSWORD_REQ = 0x332; // 设置用户密码
        public static final int USER_IPCAM_SETPASSWORD_RESP = 0x333; // 设置用户密码应答

        public static final int USER_IPCAM_GET_OSD_REQ = 0x858;    // 获取 OSD 配置
        public static final int USER_IPCAM_GET_OSD_RESP = 0x859;    // 获取 OSD 配置应答
        public static final int USER_IPCAM_SET_OSD_REQ = 0x860;    // 设置 OSD 配置
        public static final int USER_IPCAM_SET_OSD_RESP = 0x861;    // 设置 OSD 配置应答

        public static final int USER_IPCAM_SET_433_REQ = 0x862;    // 设置 433 设备
        public static final int USER_IPCAM_SET_433_RESP = 0x863;    // 设置 433 设备应答
        public static final int USER_IPCAM_GET_433_REQ = 0x864;    // 获取 433 设备列表
        public static final int USER_IPCAM_GET_433_RESP = 0x865;    // 获取 433 设备列表应答

        public static final int USER_IPCAM_CFG_433_REQ = 0x866;    // 开始 433 配对
        public static final int USER_IPCAM_CFG_433_RESP = 0x867;    // 开始 433 配对应答
        public static final int USER_IPCAM_DEL_433_REQ = 0x868;    // 删除 433 设备
        public static final int USER_IPCAM_DEL_433_RESP = 0x869;    // 删除 433 设备应答
        public static final int USER_IPCAM_CFG_433_EXIT_REQ = 0x870;    // 退出 433 设备配对
        public static final int USER_IPCAM_CFG_433_EXIT_RESP = 0x871;    // 退出 433 设备配对应答

        public static final int DEV_SYS_GET = 0x0880;    // 获取设备系统
        public static final int DEV_SYS_GET_RESP = 0x0881;    // 获取设备系统 应答

        public static final int DEV_SYS_UPDATE = 0x088a;    // 获取设备升级
        //        public static final int DEV_SYS_UPDATE_RESP = 0x088b;	// 获取设备升级返回
        public static final int DEV_SYS_UPDATE_PROGRESS = 0x088c;    // 获取设备升级进度
        public static final int DEV_SYS_UPDATE_PROGRESS_RESP = 0x088d;    // 获取设备升级进度返回


        public static final int PRESET_POSITION_SET = 0x0440;    // 设置预置位
        public static final int PRESET_POSITION_GOTO = 0x0442;    // 转到预置位


        public static final int VIDEOLIST_GET = 0x0318; //获取Sd卡录像列表
        public static final int VIDEOLIST_GET_RESP = 0x0319; //获取Sd卡录像列表 返回
        public static final int GET_MONTH_RECORD = 0x031C; //通过月份获取播放记录
        public static final int GET_MONTH_RECORD_RESP = 0x031D; //通过月份获取播放记录 返回

        public static final int PLAYBACK_PROGRESS_SET = 0x031a; //回放进度设置


        public static final int SET_LENS = 0X0901; //设置镜头


        public static final int IOTYPE_XM_CALL_RESP = 0x701;//挂断接听

        public static final int IOTYPE_USER_IPCAM_GET_BATTERY_REQ = 0x60A;//获取电池电量
        public static final int IOTYPE_USER_IPCAM_GET_BATTERY_RESP = 0x60B;//

        public static final int CALL_OTHER_ANSWERED = 0x702; //其它用户已接听

        public static final int IOTYPE_USER_IPCAM_GET_POWER_FREQUENCY_REQ = 0x362;//获取电源频率请求
        public static final int IOTYPE_USER_IPCAM_GET_POWER_FREQUENCY_RESP = 0x363;//获取电源频率返回

        public static final int IOTYPE_USER_IPCAM_SET_POWER_FREQUENCY_REQ = 0x360;//设置电源频率请求
        public static final int IOTYPE_USER_IPCAM_SET_POWER_FREQUENCY_RESP = 0x361;//设置电源频率返回

        public static final int IOTYPE_USER_IPCAM_GET_AUDIO_VOLUME_REQ = 0x8a0;//开始请求音量
        public static final int IOTYPE_USER_IPCAM_GET_AUDIO_VOLUME_RESP = 0x8a1;//获取音量 返回
        public static final int IOTYPE_USER_IPCAM_SET_AUDIO_VOLUME_REQ = 0x8a2;//开始设置音量请求
        public static final int IOTYPE_USER_IPCAM_SET_AUDIO_VOLUME_RESP = 0x8a3;//设置音量响应

        public static final int AVIOCTRL_EVENT_ALL = 0x00;  //所有录像
        public static final int AVIOCTRL_EVENT_RINGBELL = 0x14; //访客
        public static final int AVIOCTRL_EVENT_PIR = 0x13; //红外报警
        public static final int AVIOCTRL_EVENT_MOTIONDECT = 0x01;  //移动侦测

        public static final int IOTYPE_USER_IPCAM_GET_WAKEUP_FUN_REQ = 0x1802;//获取远程唤醒
        public static final int IOTYPE_USER_IPCAM_GET_WAKEUP_FUN_RESP = 0x1803;//请求远程唤醒返回
        public static final int IOTYPE_USER_IPCAM_SET_WAKEUP_FUN_REQ = 0x1804;//设置远程唤醒请求
        public static final int IOTYPE_USER_IPCAM_SET_WAKEUP_FUN_RESP = 0x1805;//设置远程唤醒响应


    }


}
