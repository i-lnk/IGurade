package com.zxing.activity;

import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;
import com.nicky.framework.base.BaseActivity;
import com.zxing.camera.CameraManager;
import com.zxing.view.ViewfinderView;

/**
 * Created by Nicky on 2017/6/22.
 */

public abstract class BaseCaptureAty extends BaseActivity {

    public abstract ViewfinderView getViewfinderView();

	public abstract Handler getHandler();

	public abstract CameraManager getCameraManager();

    public abstract void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor);

	public void drawViewfinder() {
        if(getViewfinderView()!=null)
            getViewfinderView().drawViewfinder();
	}

}
