package com.rl.geye.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.rl.geye.R;
import com.rl.geye.bean.CropParam;
import com.yalantis.ucrop.UCrop;


/**
 * Created by Nicky on 2016/10/25.
 * 裁剪帮助类
 */
public class CropUtil {

    private CropUtil() {
    }


    /**
     * 跳转至裁剪页
     */
    public static void startCropActivity(@NonNull Activity activity, @NonNull Uri uri, @NonNull CropParam param) {
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(PhotoVideoUtil.getPhotoFile(param.getCroppedName(), true)));
        uCrop = basisConfig(uCrop, param);
        uCrop = advancedConfig(activity, uCrop);
        uCrop.start(activity);
    }

    /**
     * 跳转至裁剪页
     */
    public static void startCropActivity(@NonNull Context context, @NonNull Fragment fragment,
                                         @NonNull Uri uri, @NonNull CropParam param) {
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(PhotoVideoUtil.getPhotoFile(param.getCroppedName(), true)));
        uCrop = basisConfig(uCrop, param);
        uCrop = advancedConfig(context, uCrop);
        uCrop.start(context, fragment);
    }

    /**
     * 跳转至裁剪页
     */
    public static void startCropActivity(@NonNull Context context, @NonNull android.support.v4.app.Fragment fragment,
                                         @NonNull Uri uri, @NonNull CropParam param) {
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(PhotoVideoUtil.getPhotoFile(param.getCroppedName(), true)));
        uCrop = CropUtil.basisConfig(uCrop, param);
        uCrop = CropUtil.advancedConfig(context, uCrop);
        uCrop.start(context, fragment);
    }

    /**
     * In most cases you need only to set crop aspect ration and max size for resulting image.
     */
    public static UCrop basisConfig(@NonNull UCrop uCrop, @NonNull CropParam param) {
        uCrop = uCrop.withAspectRatio(param.getRatioX(), param.getRatioY());
        if (param.getMaxWidth() >= 100 && param.getMaxHeight() >= 100) {
            uCrop = uCrop.withMaxResultSize(param.getMaxWidth(), param.getMaxHeight());
        }
        return uCrop;
    }

    /**
     * Sometimes you want to adjust more options, it's done via {@link UCrop.Options} class.
     */
    public static UCrop advancedConfig(@NonNull Context context, @NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);// or PNG
        options.setStatusBarColor(ContextCompat.getColor(context, R.color.black));
        options.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.text_black));
        options.setLogoColor(ContextCompat.getColor(context, R.color.text_black));
        options.setCompressionQuality(80);//图片质量
        options.setHideBottomControls(true); //是否隐藏底部控制栏
        options.setFreeStyleCropEnabled(false); //是否可以自由改变裁剪区位置和比例
        options.setShowCropGrid(false);
//        options.setCropGridColor(Color.GREEN);
//        options.setCropGridColumnCount(2);
//        options.setCropGridRowCount(1);
        return uCrop.withOptions(options);
    }

}
