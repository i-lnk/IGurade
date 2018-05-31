package com.rl.geye.util;

import android.os.Environment;

import com.rl.geye.constants.Constants;

import java.io.File;

/**
 * Created by Nicky on 2016/10/25.
 * 图片视频工具类
 */
public class PhotoVideoUtil {


    private PhotoVideoUtil() {
    }

    /**
     * 获取一个随机照片名
     */
    public static String getRandomPhotoName(boolean isCropped) {
        return String.format("%1$s_%2$d.jpg", isCropped ? "Cropped" : "Img", System.currentTimeMillis() / 1000);
    }

    /**
     * 返回照片文件
     */
    public static File getPhotoFile(String fileName, boolean isCropped) {
        String path = getPhotoDirPath(isCropped);
        return new File(path, fileName);
    }

    /**
     * 返回一个随机名称的照片文件
     */
    public static File getPhotoFile(boolean isCropped) {
        return getPhotoFile(getRandomPhotoName(isCropped), isCropped);
    }


    /**
     * 返回照片文件夹
     */
    public static File getPhotoDir(boolean isCropped) {
        if (isCropped) {
            return new File(Environment.getExternalStorageDirectory(), Constants.CROPPED_PHOTO_PATH);
        } else {
            return new File(Environment.getExternalStorageDirectory(), Constants.PHOTO_PATH);
        }
    }

    /**
     * 返回照片文件夹路径
     */
    public static String getPhotoDirPath(boolean isCropped) {
        String path = Environment.getExternalStorageDirectory() + "/";
        path += getPhotoDirPath2(isCropped);
        return path;
    }

    /**
     * 返回照片文件夹路径(不带SD卡路径)
     */
    public static String getPhotoDirPath2(boolean isCropped) {
        return isCropped ? Constants.CROPPED_PHOTO_PATH : Constants.PHOTO_PATH;
    }


    /**
     * 返回视频文件夹
     */
    public static File getVideoDir() {
        return new File(Environment.getExternalStorageDirectory(), Constants.VIDEO_PATH);
    }

    /**
     * 返回视频文件夹路径
     */
    public static String getVideoDirPath() {
        return Environment.getExternalStorageDirectory() + "/" + Constants.VIDEO_PATH;
    }

}
