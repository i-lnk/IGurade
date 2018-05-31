package com.nicky.framework.component;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Nicky on 2017/8/28.
 * 页面跳转组件
 */
public interface GotoComponent {

     void gotoActivity(Class<? extends Activity> clz);

     void gotoActivity(Class<? extends Activity> clz, Bundle data);

     void gotoActivityForResult(Class<? extends Activity> clz,int requestCode);

     void gotoActivityForResult(Class<? extends Activity> clz,int requestCode, Bundle data);
}
