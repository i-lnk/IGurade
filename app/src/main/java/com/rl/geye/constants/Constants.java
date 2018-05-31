package com.rl.geye.constants;

/**
 * Created by Nicky on 2016/9/14.
 * 常量数据
 */
public class Constants {

    public static final int UNKNOWN = -1;//未知类型
    public static final String UNKNOWN_STR = "Unknown";//未知类型


    /*** 拍照--照片存储路径 */
    public static final String PHOTO_PATH = "RL/Geye/Photos/";

    /*** 录像--视频存储路径 */
    public static final String VIDEO_PATH = "RL/Geye/Videos/";

    /*** 日志--异常路径 */
    public static final String DEVBUG_PATH = "RL/Geye/Debug/";

    /*** 裁剪照片存储路径 */
    public static final String CROPPED_PHOTO_PATH = "RL/Geye/CroppedPhotos/";

    /*** 抓拍图片名(康佳) */
    public static final String DEVICE_BG_NAME = "device_bg_kj.jpg";

    /**
     * ID 规则
     */
    public static final String ID_REGEX = "^[A-Za-z0-9]+$";

    /**
     * 分隔符(二维码)
     */
    public static final String Edwin_Str = "#_rl_#";


    /**
     * 点击事件
     */
    public final static String KEY_TAG_CLICK = "click";


    /**
     * 设备类型组
     */
    public class DeviceTypeGroup {

        public static final int GROUP_1 = 1; //门铃1
        public static final int GROUP_2 = 2; //
        public static final int GROUP_3 = 3; //
        public static final int GROUP_4 = 4; //
        public static final int GROUP_5 = 5; //

    }


    public class LevelType {

        public static final int GROUP = 0; //
        public static final int CHILD = 1; //

    }

    /**
     * 操作
     */
    public class OpAction {

        public static final int ACTION_ADD = 1;
        public static final int ACTION_DELETE = 2;
        public static final int ACTION_SET = 3;

    }


    /**
     * 添加方式
     */
    public class AddType {

        public static final int LAN = 0;// 局域网
        public static final int WIRED = 1;// 有线
        public static final int AP = 2; // AP模式
        public static final int QR = 3; // 二维码方式

    }


    /**
     * Intent 传递时的 bundle key
     */
    public class BundleKey {
        public static final String KEY_PHOTO_PATH = "key_photo_path";    // 图片路径
        public static final String KEY_RING_TYPE = "key_ring_type";    // 铃声类型
        public static final String KEY_DEV_TYPE_GROUP = "key_dev_type_group";   //设备类型组(添加设备时)
        public static final String KEY_DEV_INFO = "key_dev_info";    //设备信息
        public static final String KEY_DEV_ONLINE = "key_dev_online";    //设备是否在线
        public static final String KEY_PUSH_TYPE = "key_push_type";  //呼叫、警报类型
        public static final String KEY_IS_MONITOR = "key_is_monitor";    //是否为监视类型
        public static final String KEY_AUTO_ANSWER = "key_auto_answer";    //是否自动接听
        public static final String KEY_IS_DETECT = "key_is_detect";    //是否为移动侦测
        public static final String KEY_DETECT_INFO = "key_detect_info";    //移动侦测信息
        public static final String KEY_TIME_ZONE = "key_time_zone";    //时区
        public static final String KEY_CAL_DATE = "key_cal_date";    //
        public static final String KEY_DEV_SD_VIDEO = "key_dev_sd_video";    //设备视频
        public static final String KEY_SYS_INFO = "key_sys_info";    //设备系统信息
        public static final String KEY_RECORD_ID = "key_record_id"; //记录id

        public static final String KEY_PHOTO_GROUP = "key_photo_group";    //一组照片
        public static final String KEY_PHOTO_GROUP_POS = "key_photo_group_pos";   //组位置
        public static final String KEY_PHOTO_POS = "key_photo_pos";    //照片位置

        public static final String KEY_VIDEO_DATA = "key_video_data";    //视频数据

        public static final String KEY_ADD_TYPE = "key_add_type";   //添加方式


        public static final String KEY_DELETE_TYPE = "KEY_DELETE_TYPE";    // 删除类型
        public static final String KEY_DELETE_LIST = "KEY_DELETE_LIST";    // 删除列表数据

        public static final String KEY_SUB_DEV = "key_sub_dev";    // 433子设备
        public static final String KEY_SUB_NAME = "key_sub_name";    // 433子设备名
        public static final String KEY_SUB_TYPE = "key_sub_type";    // 433子设备类型


//        public static final String KEY_START_DATE = "key_start_date";    // 起始日期
//        public static final String KEY_END_DATE = "key_end_date";    // 结束日期
//        public static final String KEY_LIST_SIZE = "key_list_size";    //
//
//
//        public static final String KEY_TRIGGER_TIME = "key_trigger_time";  //触发时间
//
//        public static final String KEY_DEV_ID = "key_dev_id";       //设备id
//        public static final String KEY_DEV_NAME = "key_dev_name";   //设备名
//        public static final String KEY_DEV_TYPE = "key_dev_type";   //设备类型
//        public static final String KEY_DEV_USER = "key_dev_user";   //设备用户
//        public static final String KEY_DEV_PWD = "key_dev_pwd";     //设备密码
//        public static final String KEY_DEV_MAC = "key_dev_mac";    //设备Mac地址
//
//        public static final String KEY_SUB_DEV_INFO = "key_sub_dev_info";    //433子设备信息
//
//        public static final String KEY_DELETE_TYPE = "KEY_DELETE_TYPE";    // 删除类型
//        public static final String KEY_DELETE_LIST = "KEY_DELETE_LIST";    // 删除列表数据
//
//
//
//        public static final String KEY_HELP_GUIDES = "key_help_guides";    // 帮助提示
//        public static final String KEY_HELP_BG = "key_help_bg";    // 帮助图片
//


    }

    /**
     * App内事件传递类型
     */
    public class EdwinEventType {
        private static final int EVENT_BEGIN = 100;

        public static final int EVENT_TAKE_PHOTO = EVENT_BEGIN + 10;  // 拍照
        public static final int EVENT_TAKE_VIDEO = EVENT_BEGIN + 20;  // 录像
        public static final int EVENT_RECORD_ADD = EVENT_BEGIN + 30;  // 新增记录
        public static final int EVENT_RECORD_UPDATE = EVENT_BEGIN + 40;  // 更新记录
        public static final int EVENT_PHOTO_VIDEO_UPDATE = EVENT_BEGIN + 50;  // 更新记录


        public static final int EVENT_P2P_NET_DISCONNECT = EVENT_BEGIN + 60;
        public static final int EVENT_P2P_INIT_FAILED = EVENT_BEGIN + 70;  // 设备新增完成

        public static final int EVENT_DEV_ADD = EVENT_BEGIN + 80;  // 设备更新(增)
        public static final int EVENT_DEV_ADD_COMPLETE = EVENT_BEGIN + 90;  // 设备新增完成
        public static final int EVENT_DEV_ADD_CANCEL = EVENT_BEGIN + 100;  // 设备新增操作取消
        public static final int EVENT_DEV_DELETE = EVENT_BEGIN + 110;  // 设备更新(删)
        public static final int EVENT_DEV_UPDATE_NAME = EVENT_BEGIN + 120;  // 设备名称更新(改)
        public static final int EVENT_DEV_UPDATE_PWD = EVENT_BEGIN + 130;  // 设备密码更新(改)
        public static final int EVENT_DEV_UPDATE_USER_PWD = EVENT_BEGIN + 140;  // 设备用户密码更新(改)
        public static final int EVENT_DEV_UPDATE_BG = EVENT_BEGIN + 150;  // 设备背景更新(改)
        public static final int EVENT_DEV_REBOOT = EVENT_BEGIN + 160;  // 设备重启
        public static final int EVENT_FINISH_P2P_PAGE = EVENT_BEGIN + 170;  // 关闭P2P页

        public static final int EVENT_GOTO_BACKGROUND = EVENT_BEGIN + 300;  // app 转至后台
        public static final int EVENT_GOTO_FOREGROUND = EVENT_BEGIN + 310;  // app 转至前台


//        public static final int EVENT_DEV_ADD = EVENT_BEGIN + 10;  // 设备更新(增)
//        public static final int EVENT_DEV_ADD_COMPLETE = EVENT_BEGIN + 20;  // 设备新增完成
//        public static final int EVENT_DEV_ADD_CANCEL = EVENT_BEGIN + 30;  // 设备新增操作取消
//        public static final int EVENT_DEV_DELETE = EVENT_BEGIN + 40;  // 设备更新(删)
//        public static final int EVENT_DEV_UPDATE_NAME = EVENT_BEGIN + 50;  // 设备名称更新(改)
//        public static final int EVENT_DEV_UPDATE_PWD = EVENT_BEGIN + 60;  // 设备密码更新(改)
//        public static final int EVENT_DEV_UPDATE_USER_PWD = EVENT_BEGIN + 70;  // 设备用户密码更新(改)
//        public static final int EVENT_DEV_UPDATE_BG = EVENT_BEGIN + 80;  // 设备密码更新(改)
//        public static final int EVENT_TAKE_PHOTO = EVENT_BEGIN + 90;  // 拍照
//        public static final int EVENT_TAKE_VIDEO = EVENT_BEGIN + 100;  // 录像
//        public static final int EVENT_RECORD_ADD = EVENT_BEGIN + 110;  // 新增记录
//        public static final int EVENT_RECORD_UPDATE = EVENT_BEGIN + 120;  // 更新记录
//        public static final int EVENT_PHOTO_VIDEO_DEL_REFRESH = EVENT_BEGIN + 130;  // 主页--视频图片页--删除按钮刷新


    }

//    public static String getEventDesc( int code ){
//
//        if( EventDescs.indexOfKey(code) !=-1 ){
//            return EventDescs.get( code );
//        }
//        return UNKNOWN_STR;
//    }
//
//    public static SparseArray<String> EventDescs = new SparseArray<>();
//    static {
//        EventDescs.clear();
//        EventDescs.put( EdwinEventType.EVENT_DEV_ADD,"event_dev_add");
//        EventDescs.put( EdwinEventType.EVENT_DEV_ADD_COMPLETE,"event_dev_add_complete");
//        EventDescs.put( EdwinEventType.EVENT_DEV_ADD_CANCEL,"event_dev_add_cancel");
////        EventDescs.put( EdwinEventType.EVENT_DEV_DELETE,"event_dev_delete");
////        EventDescs.put( EdwinEventType.EVENT_DEV_UPDATE_NAME,"event_dev_update_name");
////        EventDescs.put( EdwinEventType.EVENT_DEV_UPDATE_PWD,"event_dev_update_pwd");
////        EventDescs.put( EdwinEventType.EVENT_DEV_UPDATE_USER_PWD,"event_dev_update_user_pwd");
////        EventDescs.put( EdwinEventType.EVENT_DEV_UPDATE_BG,"event_dev_update_bg");
////        EventDescs.put( EdwinEventType.EVENT_TAKE_PHOTO,"event_take_photo");
////        EventDescs.put( EdwinEventType.EVENT_TAKE_VIDEO,"event_take_video");
////        EventDescs.put( EdwinEventType.EVENT_RECORD_ADD,"event_record_add");
////        EventDescs.put( EdwinEventType.EVENT_RECORD_UPDATE,"event_record_update");
////        EventDescs.put( EdwinEventType.EVENT_PHOTO_VIDEO_DEL_REFRESH,"event_photo_video_del_refresh");
//        EventDescs.put( EdwinEventType.EVENT_GOTO_BACKGROUND,"event_goto_background");
//        EventDescs.put( EdwinEventType.EVENT_GOTO_FOREGROUND,"event_goto_foreground");
//    }

}
