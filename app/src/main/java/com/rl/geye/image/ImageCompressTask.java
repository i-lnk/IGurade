package com.rl.geye.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;

import com.rl.commons.utils.AppDevice;
import com.rl.geye.MyApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 图片压缩任务
 *
 * @author NickyHuang
 */
public class ImageCompressTask extends
        AsyncTask<Map<String, File>, Void, Map<String, File>> {
    // private Context mContext;
    private TaskHandleListener mTaskHandleListener;

    public ImageCompressTask(TaskHandleListener mTaskHandleListener) {
        super();
        this.mTaskHandleListener = mTaskHandleListener;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        int screenW = (int) AppDevice.getScreenWidth();
        int screenH = (int) AppDevice.getScreenHeight();
        while (screenW > 1024 || screenH > 1024)// TODO
        {
            screenW = screenW / 2;
            screenH = screenH / 2;
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, screenW, screenH);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    // 计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 判断照片角度
     **/
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转照片
     **/
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    public void setTaskHandleListener(TaskHandleListener listener) {
        mTaskHandleListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mTaskHandleListener != null)
            mTaskHandleListener.onPreExecute();
    }

    @Override
    protected Map<String, File> doInBackground(Map<String, File>... params) {
        Map<String, File> compressedFile = new HashMap<String, File>();
        Iterator<Entry<String, File>> entries = params[0].entrySet().iterator();
        while (entries.hasNext()) {
            Entry<String, File> entry = (Entry<String, File>) entries
                    .next();
            String key = (String) entry.getKey();
            File file = (File) entry.getValue();

            String compressedPath = compressImageFile(file.getAbsolutePath());
            // FCLog.d(this, "compressedPath:" + compressedPath);
            compressedFile.put(key, new File(compressedPath));
        }
        return compressedFile;
    }

    @Override
    protected void onPostExecute(Map<String, File> result) {
        super.onPostExecute(result);
        if (mTaskHandleListener != null)
            mTaskHandleListener.onPostExecute(result);
    }

    /**
     * 压缩图片
     **/
    private String compressImageFile(String path) {
        Bitmap bm = getSmallBitmap(path);
        int degree = readPictureDegree(path);
        if (degree != 0) {// 旋转照片角度
            bm = rotateBitmap(bm, degree);
        }
        String strMyImagePath = null;
        File tmpFile = null;
        try {

            // Store to tmp file
            String extr = MyApp.context().getCacheDir().getPath();
            File mFolder = new File(extr + "/tmpDir");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            String fileName = OfflineUtils.offlineFileName(path) + ".jpg";
            tmpFile = new File(mFolder.getAbsolutePath(), fileName);
            strMyImagePath = tmpFile.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tmpFile);
                bm.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bm.recycle();
        } catch (Throwable e) {
        }
        if (strMyImagePath == null) {
            return path;
        }
        if (tmpFile != null && tmpFile.exists()) {
            long s = 0;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(tmpFile);
                s = fis.available();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return strMyImagePath;
    }

    public interface TaskHandleListener {
        public void onPreExecute();

        public void onPostExecute(Map<String, File> result);
    }

    // private static final int DESIREDWIDTH =
    // (int)(AppDevice.getScreenWidth()/2);
    // private static final int DESIREDHEIGHT =
    // (int)(AppDevice.getScreenHeight()/2);
    //
    // private String compressImageFile(String path) {
    // String strMyImagePath = null;
    // Bitmap scaledBitmap = null;
    // File tmpFile = null;
    // try {
    // // Part 1: Decode image
    // Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH,
    // DESIREDHEIGHT, ScalingLogic.FIT);
    //
    // if (!(unscaledBitmap.getWidth() <= 800 && unscaledBitmap.getHeight() <=
    // 800)) {
    // // Part 2: Scale image
    // scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap,
    // DESIREDWIDTH, DESIREDHEIGHT, ScalingLogic.FIT);
    // } else {
    // unscaledBitmap.recycle();
    // return path;
    // }
    //
    // // Store to tmp file
    //
    // String extr = AppContext.getInstance().getCacheDir().getPath();
    // File mFolder = new File(extr + "/tmpDir");
    // if (!mFolder.exists()) {
    // mFolder.mkdir();
    // }
    //
    // String s = OfflineUtils.offlineFileName(path)+".jpg";
    //
    // tmpFile = new File(mFolder.getAbsolutePath(), s);
    //
    // strMyImagePath = tmpFile.getAbsolutePath();
    // FileOutputStream fos = null;
    // try {
    // fos = new FileOutputStream(tmpFile);
    // scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
    // fos.flush();
    // fos.close();
    // } catch (FileNotFoundException e) {
    //
    // e.printStackTrace();
    // } catch (Exception e) {
    //
    // e.printStackTrace();
    // }
    //
    // scaledBitmap.recycle();
    // } catch (Throwable e) {
    // }
    //
    // if (strMyImagePath == null) {
    // return path;
    // }
    //
    // if (tmpFile!=null && tmpFile.exists()) {
    // long s = 0;
    // FileInputStream fis = null;
    // try {
    // fis = new FileInputStream(tmpFile);
    // s = fis.available();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // Log.i("NickyTag", "compressed file size:" + s);
    // }
    // Log.i("NickyTag", "compressed file path:" + strMyImagePath);
    // return strMyImagePath;
    //
    // }
}
