package com.rl.geye.image;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;

import com.rl.geye.BuildConfig;
import com.rl.geye.R;
import com.rl.geye.util.PhotoVideoUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {
//	/**
//	 * 照片相关
//	 */
//	//拍照--照片存储路径
//	public static final String PHOTO_PATH = "EdwinTech/VDP/Photos/";

    public static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";

    private ImageUtil() {
    }

    /**
     * 扫描文件(不支持文件夹)
     */
    public static void scanFile(Context context, String filePath) {

        File file = new File(filePath);

        /**
         * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
         * 并且这样可以解决MIUI系统上拍照返回size为0的情况
         */
        Uri uri;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
//            uri = FileProvider.getUriForFile(context, ProviderUtil.getFileProviderName(context), file );
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
        }


        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * 扫描文件夹(不支持文件)
     */
    public static void scanDirAsync(Context ctx, String dir) {
        File dirFile = new File(dir);

        /**
         * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
         * 并且这样可以解决MIUI系统上拍照返回size为0的情况
         */
        Uri uri;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(dirFile);
        } else {
            uri = FileProvider.getUriForFile(ctx, ProviderUtil.getFileProviderName(ctx), dirFile);
        }

        Intent scanIntent = new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR);
        scanIntent.setData(uri);
        ctx.sendBroadcast(scanIntent);
    }

//	public static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";
//	public static void scanDir(Context ctx, String dir) {
//		Intent scanIntent = new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR);
//		scanIntent.setData(Uri.fromFile(new File(dir)));
//		ctx.sendBroadcast(scanIntent);
//	}


    /**
     * @param pic 通过图片名 获取图片id
     */
    public static int getImageId(String pic) {

        if (pic == null || pic.trim().equals("")) {
            return R.mipmap.ic_empty_photo;
        }
        Class<R.drawable> draw = R.drawable.class;
        try {
            return draw.getDeclaredField(pic).getInt(pic);
        } catch (Exception e) {
            return R.mipmap.ic_empty_photo;
        }
    }

    /**
     * @param bitmap  将bitmap保存到本地
     * @param picName
     */
    public static void saveBitmapFile(Bitmap bitmap, String picName) {
        File picDir = PhotoVideoUtil.getPhotoDir(false);
        if (!picDir.exists()) {
            picDir.mkdirs();
        }
        File photo = new File(picDir, picName);//"a.jpg"
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(photo));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param picName 通过图片名转成bitmap显示
     */
    public static Bitmap getBitMap(String picName) {
        File picDir = PhotoVideoUtil.getPhotoDir(false);
        if (!picDir.exists()) {
            picDir.mkdirs();
        }
        File photo = new File(picDir, picName);

        return fileToBitmap(photo);

    }

    /**
     * 位图转文件
     */
    public static File bitmapToFile(Bitmap bm, String fileName) {
        try {
            File picDir = PhotoVideoUtil.getPhotoDir(false);
            if (!picDir.exists()) {
                picDir.mkdirs();
            }
            File myCaptureFile = new File(picDir, fileName);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return myCaptureFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件转位图
     */
    public static Bitmap fileToBitmap(File file) {
        int IMAGE_MAX_SIZE = 600;
        Bitmap b = null;
        try {
            //Decode image size
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(file);
            BitmapFactory.decodeStream(fis, null, option);
            fis.close();

            int scale = 1;
            if (option.outHeight > IMAGE_MAX_SIZE || option.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(option.outHeight, option.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(file);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return b;
    }

    /**
     * Uri转bitmap
     */
    public static Bitmap uriToBitmap(Uri uri, Context context) {
        ContentResolver resolver = context.getContentResolver();
        String[] columns = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = resolver.query(uri, columns, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(columns[0]);
        String path = cursor.getString(index);
        cursor.close();
        File file = new File(path);
        return fileToBitmap(file);
    }

    /**
     * Uri 转 文件路径
     */
    public static String uriToFilePath(Uri uri, Context context) {

        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String path = null;
        if (scheme == null)
            path = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            path = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        path = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return path;
//		ContentResolver resolver = context.getContentResolver();
//		String[] columns = new String[]{MediaStore.Images.Media.DATA};
//		Cursor cursor = resolver.query(uri, columns, null, null, null);
//		cursor.moveToFirst();
//		int index = cursor.getColumnIndex(columns[0]);
//		String path = cursor.getString(index);
//		return path;
    }

    /**
     * 文件路径 转 Uri
     *
     * @param filePath
     * @return
     */
    public static Uri filePathToUri(String filePath) {
        return Uri.fromFile(new File(filePath));
    }


    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    @SuppressLint("NewApi")
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 获取视频第一帧的缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }


}
