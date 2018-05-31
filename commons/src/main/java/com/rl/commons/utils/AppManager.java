package com.rl.commons.utils;

import android.app.Activity;
import android.os.Process;
import java.util.ArrayList;
import java.util.List;


//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//                           _oo0oo_
//                          o8888888o
//                          88" . "88
//                          (| -_- |)
//                          0\  =  /0
//                        ___/`---‘\___
//                     .' \\\|     |// '.
//                    / \\\|||  :  |||// \\
//                   / _ ||||| -:- |||||- \\
//                   | |  \\\\  -  /// |   |
//                   | \_|  ''\---/''  |_/ |
//                   \  .-\__  '-'  __/-.  /
//                 ___'. .'  /--.--\  '. .'___
//              ."" '<  '.___\_<|>_/___.' >'  "".
//            | | : '-  \'.;'\ _ /';.'/ - ' : | |
//            \  \ '_.   \_ __\ /__ _/   .-' /  /
//        ====='-.____'.___ \_____/___.-'____.-'=====
//                          '=---='
//
//
//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//
//                     佛祖保佑                 永无BUG

/**
 * 
 * @ClassName: AppManager 
 * @Description: 应用程序Activity管理类：用于Activity管理和应用程序退出
 * @author NickyHuang
 * @date 2016-4-11 上午11:39:48 
 *
 */
public class AppManager<T extends Activity> {
	
	private List< T > activitys = new ArrayList<>();
	private static AppManager instance;



    /**
     * 获取单一实例
     */
    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(T activity) {
        if (activitys == null) {
            activitys = new ArrayList<>();
        }
        activitys.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public T currentActivity() {
        return activitys == null || activitys.size() == 0 ? null : activitys.get(activitys.size() - 1);
    }

    /**
     * 移除指定的Activity
     */
    public void removeActivity(T activity) {
        if (activity != null) {
            activitys.remove(activity);
            activity = null;
        }
    }


    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        finishActivity(currentActivity());
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(T activity) {
        if (activity != null) {
            activity.finish();
            activitys.remove(activity);
//            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (T activity : activitys) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = activitys.size() - 1; i >= 0; i--) {
            if (null != activitys.get(i)) {
                T activity = activitys.get(i);
                activity.finish();
                activity = null;
            }
        }
        activitys.clear();
    }

    /**
     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
     *
     */
    public void finishOthersActivity(Class<?> cls) {
        for (T activity : activitys) {
            if (!(activity.getClass().equals(cls))) {
                finishActivity((T) activity);
            }
        }
    }

    /**
     * 获取指定的Activity
     */
    public  T getActivity(Class<?> cls) {
        for (T activity : activitys) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }


    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            Process.killProcess(Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            System.exit(-1);
        }
    }

}
