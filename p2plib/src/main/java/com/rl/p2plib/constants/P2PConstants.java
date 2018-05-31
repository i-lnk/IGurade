package com.rl.p2plib.constants;

/**
 * Created by Nicky on 2017/6/24.
 */
public class P2PConstants {

    private P2PConstants() {
    }


    /**
     * p2p server
     */
    public static final String P2P_SERVER = "EKPNHXIDAUAOEHLOTBSQEJSWPAARTAPKLXPGENLKLUPLHUATSVEESTPFHWIHPDIEHYAOLVEISQLNEGLPPALQHXERELIALKEHEOHZHUEKIFEEEPEJ-$$";

    public static final String P2P_TYPE = "tutk";


    /**
     * 设备类型
     */
    public class DeviceType {
        public static final int IPCC = 0;       // 卡片机
        public static final int IPC = 1;       // 摇头机
        public static final int IPFC = 2;       // 鱼眼摄像机
        public static final int BELL_UNIDIRECTIONAL = 3;  // 单向门铃3
        public static final int BELL_BI_DIRECTIONAL = 4;  // 双向门铃
        //        public static final int MZ = 5;  // 模组
        public static final int CAT_SING_EYE = 30;  //单向猫眼
        public static final int CAT_DOUBLE_EYE = 40;  //双向猫眼
    }


    /**
     * 子设备类型(433报警设备)
     */
    public class SubDevType {
        public static final int REMOTE_CONTROL = 10; // 遥控
        public static final int ALARM = 11; // 报警
        public static final int OTHER = 12; // 其他
    }

    /**
     * 消息类型
     */
    public class PPPPMsgType {
        public static final int PPPP_STATUS = 0;
        public static final int PPPP_MODE = 1;
        public static final int STREAM = 2;
        public static final int INVALID_MSG = 0xffffffff;
    }

    /**
     * 设备状态
     */
    public class P2PStatus {
        public static final int CONNECTING = 0;/* connecting */
        public static final int INITIALING = 1;/* initialing */
        public static final int ON_LINE = 2;/* online */
        public static final int CONNECT_FAILED = 3;/* connect failed */
        public static final int DISCONNECT = 4;/* connect is off */
        public static final int INVALID_ID = 5;
        public static final int DEVICE_NOT_ON_LINE = 6;
        public static final int CONNECT_TIMEOUT = 7;
        public static final int ERR_USER_PWD = 8;
        public static final int SLEEP = 9; //待机
        public static final int NOT_LOGIN = 11; //未登录
        public static final int EXCEED_SESSION = 13; //超过最大连接数

        public static final int UNKNOWN = 0xffffffff;
    }

    /**
     * SD卡状态
     */
    public class SDStatus {
        public static final int SD_STATUS_NOTINIT = 0; //sd 卡不存在
        public static final int SD_STATUS_OK = 1; //sd 状态正常
        public static final int SD_STATUS_NOTFORMAT = 2; //sd 未格式化
        public static final int SD_STATUS_FORMAT_OK = 3; //sd 格式化OK
        public static final int SD_STATUS_READONLY = 4; //sd 只读
        public static final int SD_STATUS_FORMATING = 5; //sd 正在格式化
    }

    /**
     * 设置返回结果
     */
    public class SetResVal {

        public static final int RES_OK = 0; //
        public static final int RES_CFG_433_TIMEOUT = 1;    // 超时
        public static final int RES_CFG_433_MAX = 2;        // 设备上限
        public static final int RES_CFG_433_WAITING = 3;    // 正在学习
        public static final int RES_CFG_433_EXISTS = 4;        // 设备已存在

        public static final int RES_ERR_USER = -14; //
        public static final int RES_ERR_PWD = -2; //
    }


    /**
     * 文件类型
     */
    public class FileType {
        public static final int FILE = 1; // 文件
        public static final int DIR = 2; // 文件夹

    }

    /**
     * 电源类型
     */
    public class PowerType {
        public static final int TYPE_50 = 0; //50HZ
        public static final int TYPE_60 = 1; //60HZ
    }

    /**
     * 语音类型
     */
    public class LanguageType {
        public static final int TYPE_EN = 0; //英文
        public static final int TYPE_CN = 1; //中文
    }

    /**
     * 记录数据类型(图片、视频)
     */
    public class PhotoVideoType {
        public static final int PICTURE = 0;//图片
        public static final int VIDEO = 1;//视频
    }

    /**
     * 推送类型
     */
    // ALARM_DISMANTLE,CALL,DETECTION
    public class PushType {
        public static final int CALL = 1;//呼叫
        public static final int PIR = 2;//
        public static final int DETECTION = 3;//移动侦测
        public static final int ALARM_DISMANTLE = 7;//防拆报警
        public static final int LOW_CHARGE = 8;//低电量报警
        public static final int ALARM_433 = 10000;//433报警 ( 大于10000 )

        public static final int BELL_ON_LINE = 20;//双向门铃唤醒上线
//        public static final int OTHER_USER_ANSWERED = 3;//其他用户已接听
//        public static final int TIME_OUT = 4;//通话超时
    }


    /**
     * 音频编码类型
     */
    public class AudioCodec {

        public static final int AAC = 0x88;   // 2014-07-02 add AAC audio codec definition
        public static final int G711U = 0x89;   //g711 u-law
        public static final int G711A = 0x8A;   //g711 a-law
        public static final int ADPCM = 0X8B;
        public static final int PCM = 0x8C;
        public static final int SPEEX = 0x8D;
        public static final int MP3 = 0x8E;
        public static final int G726 = 0x8F;
        public static final int OPUS = 0xE1;

    }

    /**
     * 码流类型
     */
    public class StreamType {
        public static final int MAIN = 0; //主
        public static final int SECONDARY = 1; //次

    }

    public class Resolution {
        public static final int HIGH = 0; //高清
        public static final int MIDDLE = 1; //标清
        public static final int LOW = 2; //流畅
    }


    /**
     * PTZ控制指令
     */
    public class PtzCmd {

        public static final int AVIOCTRL_PTZ_STOP = 0;
        public static final int AVIOCTRL_PTZ_UP = 1;
        public static final int AVIOCTRL_PTZ_DOWN = 2;
        public static final int AVIOCTRL_PTZ_LEFT = 3;
        public static final int AVIOCTRL_PTZ_LEFT_UP = 4;
        public static final int AVIOCTRL_PTZ_LEFT_DOWN = 5;
        public static final int AVIOCTRL_PTZ_RIGHT = 6;
        public static final int AVIOCTRL_PTZ_RIGHT_UP = 7;
        public static final int AVIOCTRL_PTZ_RIGHT_DOWN = 8;
        public static final int AVIOCTRL_PTZ_AUTO = 9;
        public static final int AVIOCTRL_PTZ_SET_POINT = 10;
        public static final int AVIOCTRL_PTZ_CLEAR_POINT = 11;
        public static final int AVIOCTRL_PTZ_GOTO_POINT = 12;

        public static final int AVIOCTRL_PTZ_SET_MODE_START = 13;
        public static final int AVIOCTRL_PTZ_SET_MODE_STOP = 14;
        public static final int AVIOCTRL_PTZ_MODE_RUN = 15;

        public static final int AVIOCTRL_PTZ_MENU_OPEN = 16;
        public static final int AVIOCTRL_PTZ_MENU_EXIT = 17;
        public static final int AVIOCTRL_PTZ_MENU_ENTER = 18;

        public static final int AVIOCTRL_PTZ_FLIP = 19;
        public static final int AVIOCTRL_PTZ_START = 20;


    }


    /**
     *  App内事件传递类型
     */
//    public class P2PEventType
//    {
//        private static final int EVENT_BEGIN = 9900;
//
//        public static final int EVENT_P2P_NET_DISCONNECT = EVENT_BEGIN + 10;
//        public static final int EVENT_P2P_INIT_FAILED =  EVENT_BEGIN + 20;  // 设备新增完成
//
//        public static final int EVENT_DEV_ADD = EVENT_BEGIN + 30;  // 设备更新(增)
//        public static final int EVENT_DEV_ADD_COMPLETE = EVENT_BEGIN + 40;  // 设备新增完成
//        public static final int EVENT_DEV_ADD_CANCEL = EVENT_BEGIN + 50;  // 设备新增操作取消
//        public static final int EVENT_DEV_DELETE = EVENT_BEGIN + 60;  // 设备更新(删)
//        public static final int EVENT_DEV_UPDATE_NAME = EVENT_BEGIN + 70;  // 设备名称更新(改)
//        public static final int EVENT_DEV_UPDATE_PWD = EVENT_BEGIN + 80;  // 设备密码更新(改)
//        public static final int EVENT_DEV_UPDATE_USER_PWD = EVENT_BEGIN + 90;  // 设备用户密码更新(改)
//        public static final int EVENT_DEV_UPDATE_BG = EVENT_BEGIN + 100;  // 设备背景更新(改)
//        public static final int EVENT_DEV_REBOOT = EVENT_BEGIN + 110;  // 设备重启
//
//        public static final int EVENT_FINISH_P2P_PAGE = EVENT_BEGIN + 120;  // 关闭P2P页
//
//        public static final int EVENT_GOTO_BACKGROUND = EVENT_BEGIN + 300;  // app 转至后台
//        public static final int EVENT_GOTO_FOREGROUND = EVENT_BEGIN + 310;  // app 转至前台
//    }


}
