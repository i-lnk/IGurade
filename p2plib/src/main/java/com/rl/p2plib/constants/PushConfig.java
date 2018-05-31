package com.rl.p2plib.constants;

/**
 * Created by Nicky on 2017/6/10.
 * 推送设置
 */
public class PushConfig {

    private PushConfig(){}

    public static final int pushType = 1;// 1 is jpush 2 is bpush 4 is xpush 8为FCM

    public static final int setType = 1;
    public static final int evironment = 2;
    public static final int validity = 120;

    /** 极光推送部分 */
//    public static final String jAppkey = "6ccd194ff81baf4f271113df";
//    public static final String jMasterkey = "3dc3a2f014d028bb8ea60118";
    public static final String jAppkey = "74bb64a1d36437eea2541594";
    public static final String jMasterkey = "51afd151900c1c9c6a4469cf";
    public static final int jType = 2; // 3是按别名推送 2是按标签推送

    /** 百度推送部分 */
    public static final String bAppkey = "";
    public static final String bSecretkey = "";
    public static final String bChannelid = "";//在百度服务器上注册成功并返回的数据

    /** 信鸽推送部分 */
    public static final int xAccessid = 0;
    public static final String xSecretkey = "";

    public static final String FCM_Key = "AIzaSyAG1u9x0KZRL5a3na9CTZYFUtlkH46CMoI";

}
