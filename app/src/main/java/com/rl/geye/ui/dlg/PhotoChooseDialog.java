package com.rl.geye.ui.dlg;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nicky.framework.base.BaseActivity;
import com.nicky.framework.base.BaseDialog;
import com.orhanobut.logger.Logger;
import com.rl.commons.BaseApp;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.compatibility.Version;
import com.rl.commons.interf.PermissionResultCallback;
import com.rl.commons.utils.CameraUtil;
import com.rl.commons.utils.ClickUtil;
import com.rl.geye.R;
import com.rl.geye.bean.CropParam;
import com.rl.geye.image.ImageCompressTask;
import com.rl.geye.ui.aty.PhotoPickerAty;
import com.rl.geye.util.CropUtil;
import com.rl.geye.util.PhotoVideoUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Nicky on 2016/10/22.
 * 选择照片弹框
 */
public class PhotoChooseDialog extends BaseDialog {

    public static final String EXTRA_CROPPED_PARAM = "extra_cropped_param";
    public static final String EXTRA_BUCKET = "extra_bucket_id";
    private final static String TAG = "PhotoChooseDialog";
    private final static int PICK_PHOTO = 6001;
    private final static int TAKE_PHOTO = 6002;
    private static final int REQUEST_CODE_FOR_CAMERA = 5005;
    @BindView(R.id.btn_take_photo)
    Button btnTakePhoto;
    @BindView(R.id.btn_choose_photo)
    Button btnChoosePhoto;
    @BindView(R.id.btn_reset)
    Button btnReset;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
//    private float ratioX = 1;
//    private float ratioY = 1;
//    private int maxWidth = 0;
//    private int maxHeight = 0;
    /**
     * 裁剪图片的宽高比及最大尺寸
     */
    private CropParam mCropParam = CropParam.getDefaultParam();
//    private static final String CROPPED_JPEG_NAME = "SampleCropImage.jpg"; //裁剪后的图片名
    private File photoFile;//存储拍照图片
    private OnPhotoListener onPhotoListener;

    public void setOnPhotoListener(OnPhotoListener listener) {
        this.onPhotoListener = listener;
    }

    /**
     * Set an aspect ratio for crop bounds.
     * User won't see the menu with other ratios options.
     *
     * @param x aspect ratio X
     * @param y aspect ratio Y
     */
    public PhotoChooseDialog withAspectRatio(float x, float y) {
        if (mCropParam == null) {
            mCropParam = CropParam.getDefaultParam();
        }
        mCropParam.setRatioX(x);
        mCropParam.setRatioY(y);
        return this;
    }

    /**
     * Set maximum size for result cropped image.
     *
     * @param width  max cropped image width
     * @param height max cropped image height
     */
    public PhotoChooseDialog withMaxResultSize(@IntRange(from = 100) int width, @IntRange(from = 100) int height) {
        if (mCropParam == null) {
            mCropParam = CropParam.getDefaultParam();
        }
        mCropParam.setMaxWidth(width);
        mCropParam.setMaxHeight(height);
        return this;
    }

    @Override
    protected void initDialog(Dialog dialog) {
        setDialogGravity(Gravity.BOTTOM);
        View view = inflateContentView(R.layout.dialog_choose_photo);
        ButterKnife.bind(this, view);
        dialog.setContentView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (photoFile != null && photoFile.exists()) {
            photoFile.delete();
//            photoFile = null;
        }
    }

    @OnClick({R.id.btn_take_photo, R.id.btn_choose_photo, R.id.btn_reset, R.id.btn_cancel})
    void clickEvent(View view) {
        if (ClickUtil.isFastClick(getActivity(), view))
            return;
        switch (view.getId()) {
            case R.id.btn_take_photo:
                BaseActivity aty = (BaseActivity) getActivity();
                if (aty != null) {
                    aty.checkPermission(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_FOR_CAMERA,
                            new PermissionResultCallback() {
                                @Override
                                public void onPermissionGranted() {

                                    if (Build.VERSION.SDK_INT >= Version.API23_M) {
                                        onCameraUseable();
                                    } else {
                                        if (CameraUtil.isCameraUseable()) {
                                            onCameraUseable();
                                        } else {
                                            new MaterialDialog.Builder(getActivity())
                                                    .title(R.string.permission_title_rationale)
                                                    .content(R.string.permission_camera_rationale)
                                                    .positiveText(R.string.str_ok)
                                                    .negativeText(R.string.str_cancel)
                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                                            startActivity(intent);
                                                        }
                                                    }).show();
                                        }
                                    }

                                }

                                @Override
                                public void onPermissionDenied() {

                                }
                            });
                }

                break;
            case R.id.btn_choose_photo:
                mCropParam.setCroppedName(PhotoVideoUtil.getRandomPhotoName(true));
//                Intent albumIntent = new Intent(getActivity(),ChooseAlbumAty.class);
                Intent albumIntent = new Intent(getActivity(), PhotoPickerAty.class);
                albumIntent.putExtra(EXTRA_CROPPED_PARAM, mCropParam);
                startActivityForResult(albumIntent, PICK_PHOTO);
                break;
            case R.id.btn_reset:
                if (onPhotoListener != null)
                    onPhotoListener.onRestore();
                dismiss();
                break;
            case R.id.btn_cancel:
                if (onPhotoListener != null)
                    onPhotoListener.onCancel();
                dismiss();
                break;
        }
    }


    private void onCameraUseable() {
        if (photoFile == null) {
            photoFile = new File(Environment.getExternalStorageDirectory(), "camera.jpg");
        }

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        Uri uri;
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
        uri = Uri.fromFile(photoFile);
//        } else {
//            /**
//             * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
//             * 并且这样可以解决MIUI系统上拍照返回size为0的情况
//             */
//            uri = FileProvider.getUriForFile(getActivity(), ProviderUtil.getFileProviderName(getActivity()), photoFile);
//        }
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(captureIntent, TAKE_PHOTO);
    }

    /**
     * dialog的样式，主要是动画，返回0的话，将默认没有动画效果
     */
    protected int getAnimStyle() {
        return R.style.BottomDialogStyle;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICK_PHOTO:
                    if (data != null) {
                        final Uri selectedUri = UCrop.getOutput(data);
                        if (onPhotoListener != null && selectedUri != null) {
                            onPhotoListener.onChoosePhoto(selectedUri);
                        }
                        dismiss();
                    }
                    break;
                case TAKE_PHOTO:
                    Map<String, File> files = new HashMap<>();
                    files.put("orig_file", photoFile);
                    ImageCompressTask task = new ImageCompressTask(
                            new ImageCompressTask.TaskHandleListener() {
                                @Override
                                public void onPreExecute() {
                                    Logger.i("正在压缩图片...");
//                                    showLoadDialog("正在压缩图片...");
                                }

                                @Override
                                public void onPostExecute(Map<String, File> result) {

                                    if (result != null && result.containsKey("orig_file")) {
                                        final Uri selectedUri2 = Uri.fromFile(result.get("orig_file"));
                                        if (selectedUri2 != null) {
                                            mCropParam.setCroppedName(PhotoVideoUtil.getRandomPhotoName(true));
                                            CropUtil.startCropActivity(getActivity(), PhotoChooseDialog.this, selectedUri2, mCropParam);
                                        }
                                    }

                                }
                            });
                    task.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool2(), files);

                    break;
                case UCrop.REQUEST_CROP:
                    handleCropResult(data);
                    break;
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        } else {
            dismiss();
        }

    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            if (onPhotoListener != null && resultUri != null) {
                onPhotoListener.onTakePhoto(resultUri);
            }
            dismiss();
        } else {
            BaseApp.context().showToast(R.string.toast_cannot_retrieve_cropped_image);
            dismiss();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            BaseApp.context().showToast(cropError.getMessage());
        } else {
            BaseApp.context().showToast(R.string.toast_unexpected_error);
        }
        dismiss();
    }

    public interface OnPhotoListener {
        void onTakePhoto(Uri uri);

        void onChoosePhoto(Uri uri);

        void onRestore();

        void onCancel();
    }


}
