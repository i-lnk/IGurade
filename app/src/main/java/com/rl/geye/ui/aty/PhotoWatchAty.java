package com.rl.geye.ui.aty;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nicky.framework.widget.XViewPager;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.utils.ClickUtil;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.adapter.PhotoAdapter;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.bean.ImageItem;
import com.rl.geye.bean.PhotoVideoGroup;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.PhotoVideo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Nicky on 2016/10/8.
 * 照片查看界面
 */
public class PhotoWatchAty extends BaseMyAty {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ly_all)
    View lyAll;

    @BindView(R.id.vp_photo)
    XViewPager mViewPager;

    @BindView(R.id.tv_time)
    TextView tvTime;

    private PhotoAdapter mAdapter;
    private PhotoVideoGroup mPhotoGroup;
    private int groupPos = 0;
    private int childPos = 0;

    private AsyncTask<Integer, Boolean, Boolean> mDelTask;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_photo_watch;
    }

    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {
            mPhotoGroup = fromIntent.getParcelableExtra(Constants.BundleKey.KEY_PHOTO_GROUP);
            groupPos = fromIntent.getIntExtra(Constants.BundleKey.KEY_PHOTO_GROUP_POS, 0);
            childPos = fromIntent.getIntExtra(Constants.BundleKey.KEY_PHOTO_POS, 0);
        }
        return mPhotoGroup != null;
    }

    @Override
    public View getVaryTargetView() {
        return lyAll;
    }

    @Override
    protected void initToolBar() {
        toolbar.setNavigationIcon(R.mipmap.ic_back2);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastClick(getActivity(), view))
                    return;
                onBackPressed();
            }
        });
    }

    @Override
    protected void initViewsAndEvents() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        List<ImageItem> images = new ArrayList<>();
        for (PhotoVideo photo : mPhotoGroup.getSubItems()) {
            images.add(new ImageItem(photo.getPath(), photo.getName()));
        }
        mAdapter = new PhotoAdapter(this, images);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                tvTime.setText(mPhotoGroup.getSubItem(position).getFormatDateTime());
                tvTitle.setText(String.format("%1$d/%2$d", position + 1, mPhotoGroup.getChildrenCount()));
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
        mViewPager.setCurrentItem(childPos);
        tvTime.setText(mPhotoGroup.getSubItem(childPos).getFormatDateTime());
        tvTitle.setText(String.format("%1$d/%2$d", childPos + 1, mPhotoGroup.getChildrenCount()));
    }

    @Override
    protected void onClickView(View v) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share2, menu);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
//                final View v = findViewById(R.id.item_share);
//                if (v != null) {
//                    v.setOnLongClickListener(mMenuItemLongClickListener);
//                }
                final View v2 = findViewById(R.id.item_delete);
                if (v2 != null) {
                    v2.setOnLongClickListener(mMenuItemLongClickListener);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

//            case R.id.item_share:
//                if (ClickUtil.isFastClick(getActivity(), toolbar))
//                    return super.onOptionsItemSelected(item);
//
//                if(mPhotoGroup!=null){
//                    PhotoVideo photoItem = mPhotoGroup.getSubItem(   mViewPager.getCurrentItem() );
//                    ShareUtil.shareImage(getActivity(),getString(R.string.qr_share_to),getString(R.string.qr_share),getString(R.string.qr_title), Uri.parse(photoItem.getPath()));
//                }
//                break;

            case R.id.item_delete:
                if (ClickUtil.isFastClick(getActivity(), toolbar))
                    return super.onOptionsItemSelected(item);
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.str_delete)
                        .content(R.string.tips_del_pic)
                        .positiveText(R.string.str_ok)
                        .negativeText(R.string.str_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                executeDel();
                            }
                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.item_share).setVisible(mPhotoGroup!=null);
        menu.findItem(R.id.item_delete).setVisible(mPhotoGroup != null);
        return super.onPrepareOptionsMenu(menu);
    }

    private void cancelTask() {
        if (mDelTask != null) {
            mDelTask.cancel(true);
            mDelTask = null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void executeDel() {
        cancelTask();
        mDelTask = new AsyncTask<Integer, Boolean, Boolean>() {

            private int delPos = -1;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadDialog(R.string.deleting, null, null);
            }

            @Override
            protected Boolean doInBackground(Integer... params) {
                boolean res = false;
                try {
                    delPos = params[0];
                    PhotoVideo item = mPhotoGroup.getSubItem(delPos);
                    if (deleteNorFile(item.getPath())) {
                        MyApp.getDaoSession().getPhotoVideoDao().delete(item);
                        res = true;
                    }
                } catch (Exception e) {
                    res = false;
                }
                return res;
            }


            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                hideLoadDialog();
                if (result) {
                    mPhotoGroup.removeSubItem(delPos);
//                    mAdapter.removeItem(delPos);
                    List<ImageItem> images = new ArrayList<>();
                    for (PhotoVideo photo : mPhotoGroup.getSubItems()) {
                        images.add(new ImageItem(photo.getPath(), photo.getName()));
                    }
                    mAdapter.setDatas(images);
                    mViewPager.setAdapter(mAdapter);
                    setResult(RESULT_OK, fromIntent);
                    if (mPhotoGroup.getChildrenCount() == 0) {
                        finish();
                    } else {
                        tvTime.setText(mPhotoGroup.getSubItem(mViewPager.getCurrentItem()).getFormatDateTime());
                        tvTitle.setText(String.format("%1$d/%2$d", mViewPager.getCurrentItem() + 1, mPhotoGroup.getChildrenCount()));
                    }
                }
            }
        };
        mDelTask.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool(), mViewPager.getCurrentItem());
    }

    @Override
    protected void onDestroy() {
//        fromIntent.putExtra( Constants.BundleKey.KEY_PHOTO_GROUP,mPhotoGroup);
//        fromIntent.putExtra( Constants.BundleKey.KEY_PHOTO_GROUP_POS ,groupPos);
//        fromIntent.putExtra( Constants.BundleKey.KEY_PHOTO_POS,childPos );
        super.onDestroy();
    }
}
