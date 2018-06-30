package com.rl.geye.ui.aty;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.rl.commons.BaseApp;
import com.rl.commons.bean.EdwinEvent;
import com.rl.commons.utils.ClickUtil;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.util.PermissionUtil;
import com.rl.p2plib.constants.P2PConstants;
import com.zxing.MessageId;
import com.zxing.activity.BaseCaptureAty;
import com.zxing.camera.CameraManager;
import com.zxing.control.AmbientLightManager;
import com.zxing.control.BeepManager;
import com.zxing.decode.CaptureActivityHandler;
import com.zxing.decode.FinishListener;
import com.zxing.decode.InactivityTimer;
import com.zxing.view.ViewfinderView;

import net.sqlcipher.database.SQLiteConstraintException;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public final class CaptureActivity extends BaseCaptureAty implements
        SurfaceHolder.Callback {


    public final static String CODE_RESULT = "CODE_RESULT";
    public final static String Capture_hint = "Capture_hint";
    private static final int REQUEST_CODE_FOR_SACN_PHOTO = 998;
    private static final int SCAN_PHOTO_FALIED = 888;
    private static final int SCAN_PHOTO_SUCCESS = 777;
    @BindView(R.id.viewfinder_view)
    ViewfinderView viewfinderView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.preview_view)
    SurfaceView surfaceView;
    @BindView(R.id.tv_photo)
    TextView tvPhoto;
    //private boolean isTorchOn = false;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private Result savedResultToShow;
    private boolean hasSurface;
    private Collection<BarcodeFormat> decodeFormats;
    //	@BindView(R.id.tv_title)
//	TextView tvTitle;
//	@BindView(R.id.scan_tips)
//	TextView tvScanTips;
//	@BindView(R.id.back)
//	TextView tvBack;
    private Map<DecodeHintType, ?> decodeHints;

    //	@BindView(R.id.btn_scan_photo)
//	Button btnScanPhoto;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;
    private String mScanCode;
    private Bitmap scanBitmap;
    private String photoPaht;
    private Handler msgHandler;

    private Unbinder unbinder;
    private boolean isScanEnable = true;

    @Override
    protected boolean useLightBarStatus() {
        return true;
    }

    protected void initCommonToolBar(Toolbar _toolbar) {
        _toolbar.setNavigationIcon(R.mipmap.ic_back);
        _toolbar.setTitle("");
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastClick(getActivity(), view))
                    return;
                onBackPressed();
            }
        });
    }

    @Override
    protected void initBind() {
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void unBind() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_code_scan;
    }

    @Override
    public View getVaryTargetView() {
        return null;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
    }

    @Override
    protected void initViewsAndEvents() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        ambientLightManager = new AmbientLightManager(this);

        String hint = getIntent().getStringExtra(Capture_hint);
        if (!StringUtils.isEmpty(hint)) {
            viewfinderView.setTips(hint);
//			tvScanTips.setText(hint);
        }
        tvPhoto.setOnClickListener(this);
        msgHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SCAN_PHOTO_FALIED:
                        BaseApp.showToast("图片格式有误");
                        break;
                    case SCAN_PHOTO_SUCCESS:
                        handleDecode((Result) msg.obj, scanBitmap, 0);
                        break;
                }
            }

        };
    }

    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {
            case R.id.tv_photo:
                if (PermissionUtil.mayRequestExternalStorage(this)) {
                    gotoActivityForResult(PhotoPickerAty.class, REQUEST_CODE_FOR_SACN_PHOTO);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (REQUEST_CODE_FOR_SACN_PHOTO == requestCode) {
                photoPaht = data.getStringExtra(Constants.BundleKey.KEY_PHOTO_PATH);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Result result = scanningImage(photoPaht);
                        // String result = decode(photo_path);
                        if (result == null) {
                            msgHandler.sendEmptyMessage(SCAN_PHOTO_FALIED);
                        } else {
                            Message message = Message.obtain();
                            message.what = SCAN_PHOTO_SUCCESS;
                            message.obj = result;
                            msgHandler.sendMessageDelayed(message, 1000);
                        }
                    }
                }).start();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.MARK_WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gotoActivityForResult(PhotoPickerAty.class, REQUEST_CODE_FOR_SACN_PHOTO);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager = new CameraManager(getApplication());
        viewfinderView.setCameraManager(cameraManager);
        handler = null;
        resetStatusView();

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        beepManager.updatePrefs();
        ambientLightManager.start(cameraManager);

        inactivityTimer.onResume();
        isScanEnable = true;
        decodeFormats = null;
        characterSet = null;
    }

    @Override
    protected void onPause() {
        closeCamera();
        ambientLightManager.stop();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        viewfinderView.recycleLineDrawable();
        if (scanBitmap != null) {
            scanBitmap.recycle();
            scanBitmap = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_CAMERA:// 拦截相机键
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
//	public byte[] BitmapToArray(Bitmap bmp){
//		ByteArrayOutputStream stream = new ByteArrayOutputStream();
//		bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
//		byte[] byteArray = stream.toByteArray();
//		return byteArray;
//	}

    /**
     * 解析二维码图片
     */
    protected Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        if (scanBitmap != null) {
            scanBitmap.recycle();
            scanBitmap = null;
        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 300);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);

        int[] intArray = new int[scanBitmap.getWidth() * scanBitmap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        scanBitmap.getPixels(intArray, 0, scanBitmap.getWidth(), 0, 0, scanBitmap.getWidth(), scanBitmap.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap.getWidth(), scanBitmap.getHeight(), intArray);
//		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        if (handler == null) {
            savedResultToShow = result;
        } else {
            if (result != null) {
                savedResultToShow = result;
            }
            if (savedResultToShow != null) {
//				Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
                Message message = Message.obtain(handler, MessageId.decode_succeeded, savedResultToShow);
                handler.sendMessage(message);
            }
            savedResultToShow = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    /**
     * 结果处理
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();
        /**
         * 自定义部分
         */
        mScanCode = rawResult.getText();
        // FIXME
        if (StringUtils.isEmpty(mScanCode)) {
            BaseApp.showToast(R.string.scan_failed);
            CaptureActivity.this.finish();
        } else {
            Log.e("NickyTag", "二维码扫描到的结果:" + mScanCode);
            if (!onScanDev()) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            restartCamera();
                        }
                    }
                }, 2000);
            } else {
                finish();
                postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_ADD_COMPLETE);
            }
        }

    }

    private boolean onScanDev() {
        closeCamera();
        try {
            String devName = "IPC";
            String regex = "[a-zA-Z0-9]{2,15}://([a-zA-Z0-9]{2,20}):([a-zA-Z0-9]{2,20}):([0-9]{1,2})@([a-zA-Z0-9]{6,20})[\\.][a-zA-Z0-9]{2,20}[\\.]com";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(mScanCode);
            if (matcher.find()) {
                Log.e("NickyTag", "User Group 1:" + matcher.group(1));
                Log.e("NickyTag", "Pwd Group 2:" + matcher.group(2));
                Log.e("NickyTag", "Type Group 3:" + matcher.group(3));
                Log.e("NickyTag", "ID Group 4:" + matcher.group(4));

                int type = StringUtils.toInt(matcher.group(3));
                String did = matcher.group(4);
                String didLast4Str;
                String didFirst2Str;
                if (did.length() > 6) {
                    didLast4Str = did.substring(did.length() - 4, did.length());
                    didFirst2Str = did.substring(0, 2);
                } else {
                    didLast4Str = did;
                    didFirst2Str = "";
                }
                switch (type) {

                    case P2PConstants.DeviceType.IPC:
                        devName = getString(R.string.type_ipc) + didFirst2Str + didLast4Str;
                        break;

                    case P2PConstants.DeviceType.IPCC:
                        devName = getString(R.string.type_ipcc) + didFirst2Str + didLast4Str;
                        break;

                    case P2PConstants.DeviceType.IPFC:
                        devName = getString(R.string.type_ipfc) + didFirst2Str + didLast4Str;
                        break;

                    case P2PConstants.DeviceType.BELL_LTK6112C:
                    case P2PConstants.DeviceType.BELL_BI_DIRECTIONAL:
                        devName = getString(R.string.type_bell_bi_directional) + didFirst2Str + didLast4Str;
                        break;

                    case P2PConstants.DeviceType.BELL_UNIDIRECTIONAL:
                        devName = getString(R.string.type_bell_unidirectional) + didFirst2Str + didLast4Str;
                        break;
                    case P2PConstants.DeviceType.CAT_SING_EYE:
                    case P2PConstants.DeviceType.CAT_DOUBLE_EYE:
                        devName = getString(R.string.type_cat_eye) + didFirst2Str + didLast4Str;
                        break;
                    default:
                        devName = "IPC" + didFirst2Str + didLast4Str;
                        break;
                }


                EdwinDevice device = new EdwinDevice();
                device.setDevId(did);
                device.setName(devName);
                device.setType(type);
                device.setShareable(true);
                // device.setUser(matcher.group(1));
                // device.setPwd(matcher.group(2));
                device.setUserPwdOK(false);
                long res;
                try {
                    res = MyApp.getDaoSession().getEdwinDeviceDao().insert(device);
                } catch (SQLiteConstraintException sqlE) {
                    sqlE.printStackTrace();
                    res = -1;
                }
                if (res != -1) {
                    List<EdwinDevice> devList = new ArrayList<>();
                    devList.add(device);
                    postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_ADD, devList);
                    postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_ADD_COMPLETE);
                } else {
                    BaseApp.showToast(R.string.device_exists);
                    return false;
                }
                return true;
            } else {
                BaseApp.showToast(R.string.invalid_qr_code);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            BaseApp.showToast(R.string.invalid_qr_code);
            return false;
        }
////		String devName = "DoorBell";
//		boolean isMatch;
//		String idRegex = Constants.ID_REGEX;
//		Pattern p = Pattern.compile(idRegex);
//		Matcher m = p.matcher(mScanCode.replace("-","").replace("_",""));
//		isMatch = m.matches();
//		if (!isMatch) {
//			BaseApp.showToast(getString(R.string.invalid_qr_code));
//			return false;
//		}
//
//		EdwinDevice device = new EdwinDevice();
//		device.setDevId(mScanCode);
//		device.setName(mScanCode);
////		device.setName(devName);
//		device.setType(0);//TODO
//		device.setShareable(false);
//		device.setUser("admin");//TODO
//		device.setPwd("admin");//TODO
//
//		long res = TbDevice.getInstance().addDevice(device);
//		if (res != -1) {
//			List<EdwinDevice> devList = new ArrayList<>();
//			devList.add(device);
//			postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_ADD, devList);
//		} else {
//			BaseApp.showToast(getString(R.string.device_exists));
//			return false;
//		}
//		return true;

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            return;
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats,
                        decodeHints, characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tips_warning);
        builder.setMessage(R.string.tips_sry_restart);
        builder.setPositiveButton(R.string.str_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
//			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
            handler.sendEmptyMessageDelayed(MessageId.restart_preview, delayMS);
        }
        resetStatusView();
    }

    private void resetStatusView() {
        viewfinderView.setVisibility(View.VISIBLE);
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * 重新扫描
     */
    private void restartCamera() {
        if (isFinishing()) {
            return;
        }
        viewfinderView.setVisibility(View.VISIBLE);

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        initCamera(surfaceHolder);

        // 恢复活动监控器
        inactivityTimer.onResume();
        isScanEnable = true;
    }

    private void closeCamera() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        if (isScanEnable) {
            inactivityTimer.onPause();
            // 关闭摄像头
            cameraManager.closeDriver();
            isScanEnable = false;
        }
    }


    protected void postEdwinEvent(EdwinEvent<?> event) {
        if (null != event) {
            EventBus.getDefault().post(event);
        }
    }

    protected void postEdwinEvent(int eventCode) {
        postEdwinEvent(new EdwinEvent<>(eventCode, getXClass()));
    }


    protected void postEdwinEvent(int eventCode, Object data) {
        postEdwinEvent(new EdwinEvent<>(eventCode, data, getXClass()));
    }


}
