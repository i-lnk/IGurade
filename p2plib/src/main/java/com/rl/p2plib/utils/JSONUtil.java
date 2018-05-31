package com.rl.p2plib.utils;


import com.alibaba.fastjson.JSON;

/**
 * Created by Nicky on 2017/3/24.
 */

public class JSONUtil {

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
