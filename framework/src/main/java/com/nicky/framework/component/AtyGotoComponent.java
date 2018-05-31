package com.nicky.framework.component;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Nicky on 2017/8/28.
 * Activity页面跳转组件
 */

public class AtyGotoComponent implements GotoComponent{

    private Activity mActivity;

    public AtyGotoComponent(Activity  activity){
        this.mActivity = activity;
    }

    @Override
    public void gotoActivity(Class<? extends Activity> clz) {
        gotoActivity(clz,null);
    }

    @Override
    public void gotoActivity(Class<? extends Activity> clz, Bundle data) {
        Intent intent = new Intent(mActivity, clz);
        if(data!=null)
            intent.putExtras(data);
        mActivity.startActivity(intent);
    }

    @Override
    public void gotoActivityForResult(Class<? extends Activity> clz, int requestCode) {
        gotoActivityForResult( clz,requestCode,null);
    }

    @Override
    public void gotoActivityForResult(Class<? extends Activity> clz, int requestCode, Bundle data) {
        Intent intent = new Intent(mActivity, clz);
        if(data!=null)
            intent.putExtras(data);
        mActivity.startActivityForResult(intent, requestCode);
    }


}
