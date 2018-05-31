package com.rl.geye.ui.aty;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nicky.framework.widget.AspectRatio;
import com.nicky.framework.widget.XRelativeLayout;
import com.rl.commons.BaseApp;
import com.rl.commons.utils.AppDevice;
import com.rl.geye.BuildConfig;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.base.BaseMyAty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Nicky on 2016/11/7.
 * 关于页面
 */
public class AboutAty extends BaseMyAty {
    @BindView(R.id.ll_version)
    LinearLayout ll_version;
    @BindView(R.id.text_version)
    TextView text_version;
    @BindView(R.id.iv_about)
    ImageView ivAbout;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.layout_about_top)
    XRelativeLayout layoutAboutTop;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //    private boolean useLightStatus = true;
//    private int statusColorId = R.color.colorPrimaryDark;
//
//
//    @Override
//    protected boolean useLightBarStatus() {
//        return useLightStatus;
//    }
//
//    protected int getBarStatusColor(){
//        return statusColorId;
//    }
//
//
//    @Override
//    protected boolean initPrepareData() {
//        statusColorId = R.color.bg_about;
//        useLightStatus = false;
//        setTheme( R.style.AppTheme_About );
//        return super.initPrepareData();
//    }
//
//
    @Override
    protected int getLayoutId() {
        return R.layout.aty_about;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.str_about);
        ll_version.setOnClickListener(this);
    }

    @Override
    protected void initViewsAndEvents() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        JPushInterface.getAllTags(this,10010);
        layoutAboutTop.setRatio(AspectRatio.makeAspectRatio(295.0f / 481.0f, true));
        tvVersion.setText("Ver  " + AppDevice.getVersionName());
        if (MyApp.IS_NEW_VERSION) {
            text_version.setText(R.string.version_latest);
        } else {
            text_version.setText(R.string.no_version_latest);
        }
//        getResources().updateConfiguration();
//        createConfigurationContext(null);

    }

    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {
            case R.id.ll_version:
                if (!MyApp.IS_NEW_VERSION) {
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.version_check)
                            .content(R.string.version_find)
                            .positiveText(R.string.update)
                            .negativeText(R.string.str_cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    downloadApk(MyApp.url);
                                }
                            }).show();
                }
                break;
        }
    }

    private void downloadApk(String url) {
        //显示下载进度
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();

        //访问网络下载apk
        new Thread(new DownloadApk(url, dialog)).start();
    }

    /**
     * 下载完成,提示用户安装
     */
    private void installApk(File file) {
        //调用系统安装程序
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileProvider", file);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

        } else {

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

        }

        getApplicationContext().startActivity(intent);
    }

    private class DownloadApk implements Runnable {

        InputStream is;
        FileOutputStream fos;
        private ProgressDialog dialog;
        private String url;

        public DownloadApk(String url, ProgressDialog dialog) {
            this.dialog = dialog;
            this.url = url.replaceAll(" ", "");
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();


            try {
                Request request = new Request.Builder().get().url(url).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.d("开始下载apk的url=", url);
                    //获取内容总长度
                    long contentLength = response.body().contentLength();
                    //设置最大值
                    dialog.setMax((int) contentLength);
                    //保存到sd卡
                    File apkFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".apk");
                    fos = new FileOutputStream(apkFile);
                    //获得输入流
                    is = response.body().byteStream();
                    //定义缓冲区大小
                    byte[] bys = new byte[1024];
                    int progress = 0;
                    int len = -1;
                    while ((len = is.read(bys)) != -1) {
                        try {
                            Thread.sleep(1);
                            fos.write(bys, 0, len);
                            fos.flush();
                            progress += len;
                            //设置进度
                            dialog.setProgress(progress);
                        } catch (InterruptedException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    BaseApp.showToast("Error:10002");
                                }
                            });
                        }
                    }
                    //下载完成,提示用户安装
                    installApk(apkFile);
                }
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BaseApp.showToast("Error:10003");
                    }
                });
            } finally {
                //关闭io流
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    is = null;
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fos = null;
                }
            }
            dialog.dismiss();
        }
    }

}
