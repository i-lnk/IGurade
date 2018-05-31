package com.rl.geye.ui.aty;


import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.rl.commons.interf.PermissionResultCallback;
import com.rl.commons.utils.AppDevice;
import com.rl.commons.utils.AppManager;
import com.rl.geye.R;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.constants.SystemValue;
import com.rl.p2plib.BridgeService;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/10/19.
 * 启动页
 */
public class LauncherAty extends BaseMyAty {

    private static final int REQUEST_CODE_FOR_READ_WRITE = 6006;
    //    private NotificationManager mCustomMgr;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    private Handler mHandler;
    private ServiceWaitThread mThread;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_launcher;
    }

    @Override
    public View getVaryTargetView() {
        return null;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViewsAndEvents() {
//        mCustomMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        mCustomMgr.cancelAll();
        SystemValue.appIsStarting = true;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tvVersion.setText("Ver  " + AppDevice.getVersionName());

        mHandler = new Handler();

////        DataLogic.saveNeedTestData(true);
//        if( DataLogic.needTestData() )
//        {
//            DataLogic.saveNeedTestData(false);
//            EdwinDevice device = new EdwinDevice();
//            device.setDevId( "E1PAA14WUBZMSHPGY1XJ" );
//            device.setName("Test Device");
//            device.setType( P2PConstants.DeviceType.IPC );
//            device.setShareable(true);
//            device.setUser(device.isIpc()? "":"admin");
//            device.setPwd(device.isIpc()? "":"admin");
//
//            MyApp.getDaoSession().getEdwinDeviceDao().insert(device);
//
//        }

        checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_FOR_READ_WRITE, new PermissionResultCallback() {
                    @Override
                    public void onPermissionGranted() {

                        if (BridgeService.isReady()) {
                            onServiceReady(true);
                        } else {
                            startService(new Intent(Intent.ACTION_MAIN).setClass(LauncherAty.this, BridgeService.class));
                            mThread = new ServiceWaitThread();
                            mThread.start();
                        }


                    }

                    @Override
                    public void onPermissionDenied() {
                        onMyPermissionDenied();
                    }
                });


    }

    @Override
    protected void onClickView(View v) {

    }

    private void onMyPermissionDenied() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), BridgeService.class);
        getActivity().stopService(intent);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AppManager.getInstance().AppExit();
            }
        }.start();
    }


    protected void onServiceReady(boolean needDelay) {
//        XLog.e(TAG,"------------------------onServiceReady----------------------------------");
        if (needDelay) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent().setClass(LauncherAty.this, MainAty.class));
                    finish();
                }
            }, 500);
        } else {
            startActivity(new Intent().setClass(LauncherAty.this, MainAty.class));
            finish();
        }

    }


    private class ServiceWaitThread extends Thread {
        public void run() {
            while (!BridgeService.isReady()) {
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
//            XLog.e(TAG,"------------------------Service Start OK----------------------------------");
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onServiceReady(false);
            mThread = null;
        }
    }


}
