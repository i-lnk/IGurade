package com.rl.commons.interf;

/**
 * Created by Nicky on 2017/5/8.
 * 权限申请回调
 */
public interface PermissionResultCallback {

    void onPermissionGranted();
    void onPermissionDenied();

}
