package com.nicky.framework.component;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Nicky on 2017/8/28.
 * Activity页面跳转组件
 */

public class FragGotoComponent implements GotoComponent{


    private Fragment mFragment;

    public FragGotoComponent(Fragment  fragment){
        this.mFragment = fragment;
    }

    @Override
    public void gotoActivity(Class<? extends Activity> clz) {
        gotoActivity(clz,null);
    }

    @Override
    public void gotoActivity(Class<? extends Activity> clz, Bundle data) {
        Intent intent = new Intent(mFragment.getContext(), clz);
        if(data!=null)
            intent.putExtras(data);
        mFragment.startActivity(intent);
    }

    @Override
    public void gotoActivityForResult(Class<? extends Activity> clz, int requestCode) {
        gotoActivityForResult( clz,requestCode,null);
    }

    @Override
    public void gotoActivityForResult(Class<? extends Activity> clz, int requestCode, Bundle data) {
        Intent intent = new Intent(mFragment.getContext(), clz);
        if(data!=null)
            intent.putExtras(data);
        mFragment.startActivityForResult(intent, requestCode);
    }


}
