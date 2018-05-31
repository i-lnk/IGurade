package com.rl.geye.image;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.rl.geye.R;
import com.rl.geye.util.PhotoVideoUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nicky on 2016/10/31.
 * (手机系统数据帮助类---如相册图片获取)
 */
public class MediaStoreHelper {

    public final static int INDEX_ALL_PHOTOS = 0; // 所有图片
    public final static int INDEX_DEVICE_PHOTOS = 1; // 设备相册

    public static void getPhotoBuckets(FragmentActivity activity, Bundle args, PhotosResultCallback resultCallback) {
        activity.getSupportLoaderManager()
                .initLoader(0, args, new BucketLoaderCallbacks(activity, resultCallback));
    }

    public interface PhotosResultCallback {
        void onResultCallback(List<PhotoBucket> buckets);
    }

    static class BucketLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        private WeakReference<Context> context;
        private PhotosResultCallback resultCallback;

        public BucketLoaderCallbacks(Context context, PhotosResultCallback resultCallback) {
            this.context = new WeakReference<>(context);
            this.resultCallback = resultCallback;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new PhotoBucketLoader(context.get(), false);//暂时忽略gif格式
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (data == null)
                return;
            String deviceDir = PhotoVideoUtil.getPhotoDirPath(false);
            String croppedDir = PhotoVideoUtil.getPhotoDirPath(true);

            List<PhotoBucket> bucketList = new ArrayList<>();

            PhotoBucket bucketAll = new PhotoBucket();
            bucketAll.setName(context.get().getString(R.string.album_all));
            bucketAll.setId("ALL");

            PhotoBucket bucketDevice = new PhotoBucket();
            bucketDevice.setName(context.get().getString(R.string.album_device));
            bucketDevice.setId("DEVICE");


            while (data.moveToNext()) {

                int imageId = data.getInt(data.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                String bucketId = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
                String name = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String path = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                if (path.contains(croppedDir)) {
                    continue;
                } else if (path.contains(deviceDir)) {
                    bucketDevice.addPhoto(imageId, path);
                    continue;
                } else {
                    PhotoBucket bucket = new PhotoBucket();
                    bucket.setId(bucketId);
                    bucket.setName(name);
                    if (!bucketList.contains(bucket)) {
                        bucket.setCoverPath(path);
                        bucket.addPhoto(imageId, path);
                        bucket.setDateAdded(data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)));
                        bucketList.add(bucket);
                    } else {
                        bucketList.get(bucketList.indexOf(bucket)).addPhoto(imageId, path);
                    }
                }
                bucketAll.addPhoto(imageId, path);
            }
            if (bucketAll.getPhotoPaths().size() > 0) {
                bucketAll.setCoverPath(bucketAll.getPhotoPaths().get(0));
            }
            if (bucketDevice.getPhotoPaths().size() > 0) {
                bucketDevice.setCoverPath(bucketDevice.getPhotoPaths().get(0));
            }
            bucketList.add(INDEX_ALL_PHOTOS, bucketAll);
            if (bucketDevice.getPhotoPaths().size() > 0) {
                bucketList.add(INDEX_DEVICE_PHOTOS, bucketDevice);
            }
            if (resultCallback != null) {
                resultCallback.onResultCallback(bucketList);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
