package com.rl.commons.utils;


import android.hardware.Camera;

/**
 * Created by Nicky on 2017/5/31.
 */

public class CameraUtil {

    private CameraUtil(){}

    public static boolean isCameraUseable() {

        boolean canUse =true;

        Camera mCamera =null;

        try{

            mCamera = Camera.open();

            // setParameters 是针对魅族MX5。MX5通过Camera.open()拿到的Camera对象不为null

            Camera.Parameters mParameters = mCamera.getParameters();

            mCamera.setParameters(mParameters);

        }catch(Exception e) {

            canUse =false;

        }

        if(mCamera !=null) {

            mCamera.release();

        }
        return canUse;

    }
}
