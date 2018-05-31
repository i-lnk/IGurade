package com.rl.geye.ui.aty;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.orhanobut.logger.Logger;
import com.rl.commons.BaseApp;
import com.rl.commons.ThreadPoolMgr;
import com.rl.geye.R;
import com.rl.geye.adapter.BucketV2Adapter;
import com.rl.geye.adapter.PhotoGridV2Adapter;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.bean.CropParam;
import com.rl.geye.constants.Constants;
import com.rl.geye.image.ImageCompressTask;
import com.rl.geye.image.MediaStoreHelper;
import com.rl.geye.image.PhotoBucket;
import com.rl.geye.image.PhotoItem;
import com.rl.geye.ui.dlg.PhotoChooseDialog;
import com.rl.geye.util.CropUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/10/31.
 * 选择相片并裁剪
 */
public class PhotoPickerAty extends BaseMyAty implements PhotoGridV2Adapter.OnPhotoClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_photos)
    RecyclerView recyclerView;
    @BindView(R.id.tv_bucket)
    Button tvBucket;
    @BindView(R.id.ly_all)
    View lyAll;
    @BindView(R.id.ly_bucket)
    View lyBucket;


    @BindView(R.id.lv_album)
    ListView lvAlbum;
    @BindView(R.id.ly_list)
    View lyList;

    @BindView(R.id.ly_bg)
    View lyBg;

    private int SCROLL_THRESHOLD = 30;

    private CropParam mCropParam; //裁剪参数

    private boolean isListShow = false;
    private Handler mHandler;

    private RequestManager mGlideRequestManager;
    private PhotoGridV2Adapter photoGridAdapter;

    //所有photos的路径
    private List<PhotoBucket> photoBuckets = new ArrayList<>();

    private BucketV2Adapter listAdapter;

    private int bucketTanslationY = 0;
    private int bucketMenuHeight = 0;


    @Override
    protected int getLayoutId() {
        return R.layout.aty_photo_picker;
    }


    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {
            mCropParam = fromIntent.getParcelableExtra(PhotoChooseDialog.EXTRA_CROPPED_PARAM);
        }
//        return mCropParam!=null;
        return true;
    }

    @Override
    public View getVaryTargetView() {
        return lyAll;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.str_album);
    }

    @Override
    protected void initViewsAndEvents() {

        SCROLL_THRESHOLD = dp2px(20);
        bucketTanslationY = getResources().getDimensionPixelSize(R.dimen.item_bucket_translation_y_height);
        bucketMenuHeight = getResources().getDimensionPixelSize(R.dimen.item_bucket_menu_height);

        mGlideRequestManager = Glide.with(this);

        photoGridAdapter = new PhotoGridV2Adapter(this, mGlideRequestManager, photoBuckets);

        listAdapter = new BucketV2Adapter(this, mGlideRequestManager, photoBuckets);


        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photoGridAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        MediaStoreHelper.getPhotoBuckets(getActivity(), new Bundle(),
                new MediaStoreHelper.PhotosResultCallback() {
                    @Override
                    public void onResultCallback(List<PhotoBucket> dirs) {
                        photoBuckets.clear();
                        photoBuckets.addAll(dirs);
                        photoGridAdapter.notifyDataSetChanged();
                        listAdapter.notifyDataSetChanged();
                    }
                });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Log.d(">>> Picker >>>", "dy = " + dy);
                if (Math.abs(dy) > SCROLL_THRESHOLD) {
                    mGlideRequestManager.pauseRequests();
                } else {
                    mGlideRequestManager.resumeRequests();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mGlideRequestManager.resumeRequests();
                }
            }
        });
        photoGridAdapter.setOnPhotoClickListener(this);

        lvAlbum.setAdapter(listAdapter);
        lvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                hideList();
                PhotoBucket bucket = photoBuckets.get(position);
                tvBucket.setText(bucket.getName());
                photoGridAdapter.setCurrentBucketIndex(position);
                photoGridAdapter.notifyDataSetChanged();
            }
        });

        lyBg.setOnClickListener(this);
        lyBucket.setOnClickListener(this);
        tvBucket.setOnClickListener(this);
        mHandler = new Handler(Looper.getMainLooper());


    }

    @Override
    protected void onClickView(View v) {

        switch (v.getId()) {

            case R.id.ly_bucket:
            case R.id.tv_bucket:
                if (isListShow) {
                    hideList();
                } else if (!getActivity().isFinishing()) {
                    showList();
                }
                break;
            case R.id.ly_bg:
                hideList();
                break;
        }
    }


    @Override
    public void onPhotoClick(View v, int position) {
        PhotoItem item = photoGridAdapter.getItem(position);
//        ImageItem item = mAdapter.getItem(position);
        if (item != null) {

            if (mCropParam != null) {

                File origFile = new File(item.getPath());
                if (origFile.exists()) {
                    Map<String, File> files = new HashMap<>();
                    files.put("orig_file", origFile);
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
                                            CropUtil.startCropActivity(getActivity(), selectedUri2, mCropParam);
                                        }
                                    }

                                }
                            });
                    task.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool2(), files);
                }

            } else {
                fromIntent.putExtra(Constants.BundleKey.KEY_PHOTO_PATH, item.getPath());
                setResult(RESULT_OK, fromIntent);
                finish();
            }
        }
    }


    private void showList() {
        isListShow = true;
        lyBg.setVisibility(View.VISIBLE);
        mHandler.post(new ShowViewRunnable(lyList, isListShow));
    }

    private void hideList() {
        isListShow = false;
        lyBg.setVisibility(View.GONE);
        mHandler.post(new ShowViewRunnable(lyList, isListShow));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        } else if (resultCode == Activity.RESULT_OK && data != null) {
            if (UCrop.REQUEST_CROP == requestCode) {
                handleCropResult(data);
            }
        }

    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            setResult(RESULT_OK, result);
            finish();
        } else {
            BaseApp.showToast(R.string.toast_cannot_retrieve_cropped_image);
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            BaseApp.showToast(cropError.getMessage());
        } else {
            BaseApp.showToast(R.string.toast_unexpected_error);
        }
    }

    /**
     * child 显示隐藏动画
     */
    public class ShowViewRunnable implements Runnable {
        private View child;
        private boolean isShow = true;

        public ShowViewRunnable(View child, boolean isShow) {
            this.child = child;
            this.isShow = isShow;
        }

        @Override
        public void run() {
            if (child != null) {
                if (isShow) {
                    //显示List
                    float curTranslationY = lyList.getTranslationY();
                    ValueAnimator fadeAnim = null;
                    fadeAnim = ObjectAnimator.ofFloat(child, "translationY", curTranslationY, -bucketMenuHeight);
                    fadeAnim.setDuration(300);
                    fadeAnim.start();

                } else {
                    //隐藏List
                    float curTranslationY = lyList.getTranslationY();
                    ValueAnimator fadeAnim = null;
                    fadeAnim = ObjectAnimator.ofFloat(child, "translationY", curTranslationY, bucketTanslationY);
                    fadeAnim.setDuration(300);
                    fadeAnim.start();

                }
            }
        }
    }

}
