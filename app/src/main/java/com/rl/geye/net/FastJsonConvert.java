package com.rl.geye.net;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by cc on 2017/3/20.
 */
public class FastJsonConvert {


    public static <T> T fromJson(String json, Class<T> type) throws JsonIOException, JsonSyntaxException {
        return JSON.parseObject(json, type);
    }

    public static <T> T fromJson(String json, Type type) {
        return JSON.parseObject(json, type);
    }

    /*public static <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return JSON.parseObject(reader, typeOfT);
    }

    public static <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
        return JSON.parseObject(json, classOfT);
    }

    public static <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return JSON.parseObject(json, typeOfT);
    }*/


}