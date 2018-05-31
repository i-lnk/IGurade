package com.rl.geye.net;


/**
 * Created by cc on 2017/3/20.
 */

public class NetUrl {


    private static String getBaseUrl() {
        return "http://goke.lpwei.com/";
    }


    /**
     * 获取
     */
    public static String checkVersion() {
        return getBaseUrl() + "update/update.php";
    }

//    /** 获取  */
//    public static String checkVersionOld() {
//        return "http://ts.vs98.com:8080/api/v1/update";
//    }


    /**   */
    public static String checkAppUpdate() {
        return "http://lpwei.com/update/updateapp.php";
    }

}
