package com.rl.geye.ui.frag;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.bean.EdwinEvent;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.adapter.PhotoVideoAdapter;
import com.rl.geye.base.BaseRecyclerViewFrag;
import com.rl.geye.bean.PhotoVideoGroup;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.db.bean.PhotoVideo;
import com.rl.geye.db.bean.PhotoVideoDao;
import com.rl.geye.ui.aty.DeletePhotoVideoAty;
import com.rl.geye.ui.aty.PhotoWatchAty;
import com.rl.p2plib.constants.P2PConstants;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Nicky on 2016/9/13.
 * 首页 -- 照片
 */
public class TabPhotoFrag extends BaseRecyclerViewFrag implements PhotoVideoAdapter.OnPhotoClickListener {

    private final static int REQUEST_CODE_FOR_PHOTO_WATCH = 1; //全屏查看图片
    private final static int REQUEST_CODE_FOR_DELETE = 2; //删除页面

    private PhotoVideoAdapter mAdapter;


    private AsyncTask<Void, Void, Void> refreshTask;
    private AsyncTask<Void, Void, Void> loadmoreTask;

    private int groupSize = 0;

    private OnPhotoListener mListener;

    @Override
    protected void onAttachToContext(Context context) {
        if (context instanceof OnPhotoListener) {
            mListener = (OnPhotoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPhotoListener");
        }
    }

    @Override
    protected void initEmptyView() {
        tvEmptyTips.setText(R.string.tips_empty_photo);
        ivEmpty.setImageResource(R.mipmap.ic_empty_photo);
    }

    @Override
    protected BaseMultiItemQuickAdapter getAdapter() {
        if (mAdapter == null) {
            List<MultiItemEntity> list = new ArrayList<>();
            mAdapter = new PhotoVideoAdapter(list, P2PConstants.PhotoVideoType.PICTURE, Glide.with(this));
            mAdapter.setPhotoListener(this);
        }
        return mAdapter;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return layoutManager;
    }

    @Override
    protected void executeRefresh() {
        mAdapter.setEnableLoadMore(false);
        cancelRefresh();
        groupSize = 0;
        refreshTask = new AsyncTask<Void, Void, Void>() {

            private List<MultiItemEntity> list;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                List<PhotoVideoGroup> photoGroupList = getGroupList();

                list = new ArrayList<>();
                if (photoGroupList != null && !photoGroupList.isEmpty()) {
                    list.addAll(photoGroupList);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                mAdapter.setNewData(list);
                if (mAdapter.getData() != null && !mAdapter.getData().isEmpty())
                    mAdapter.expand(0);

                mListener.onPhotoListSizeChanged(mAdapter.getData().isEmpty());

                if (swipe != null) {
                    swipe.setRefreshing(false);
                }
                mAdapter.setEnableLoadMore(true);
                mState = STATE_NONE;
            }
        };
        refreshTask.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool());
    }

    @Override
    protected void executeLoadMore() {
        swipe.setEnabled(false);
//        mAdapter.setEnableLoadMore(false);
        cancelLoadMore();

        loadmoreTask = new AsyncTask<Void, Void, Void>() {

            private List<MultiItemEntity> list;
            private boolean needLoadMore = false;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                needLoadMore = false;
                List<PhotoVideoGroup> photoGroupList = getGroupList();
                needLoadMore = (photoGroupList != null && photoGroupList.size() == PAGE_COUNT);
                list = new ArrayList<>();
                if (photoGroupList != null && !photoGroupList.isEmpty()) {
                    list.addAll(photoGroupList);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                if (list != null && !list.isEmpty())
                    mAdapter.addData(list);
                swipe.setEnabled(true);

                if (needLoadMore) {
                    mAdapter.loadMoreComplete();
                } else {
                    mAdapter.loadMoreEnd();
                }
                mState = STATE_NONE;
            }
        };
        loadmoreTask.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool());
    }

    private void cancelRefresh() {
        if (refreshTask != null) {
            refreshTask.cancel(true);
            refreshTask = null;
        }
    }

    private void cancelLoadMore() {
        if (loadmoreTask != null) {
            loadmoreTask.cancel(true);
            loadmoreTask = null;
        }
    }

    private List<PhotoVideoGroup> getGroupList() {
        List<PhotoVideoGroup> photoGroupList = new ArrayList<>();
        List<PhotoVideo> groupList = MyApp.getDaoSession().getPhotoVideoDao()
                .queryBuilder()
                .where(PhotoVideoDao.Properties.Type.eq(P2PConstants.PhotoVideoType.PICTURE),
                        new WhereCondition.StringCondition(" 1 GROUP BY " + PhotoVideoDao.Properties.Date.name))
                .limit(PAGE_COUNT)
                .offset(groupSize)
                .orderDesc(PhotoVideoDao.Properties.TriggerTime).list();

        if (groupList == null || groupList.isEmpty())
            return photoGroupList;
        groupSize += groupList.size();

        List<String> dates = new ArrayList<>();
        String startDate = groupList.get(groupList.size() - 1).getDate();
        String endDate = groupList.get(0).getDate();

        for (PhotoVideo photoVideo : groupList) {
            PhotoVideoGroup group = new PhotoVideoGroup();
            group.setDate(photoVideo.getDate());
            dates.add(photoVideo.getDate());
            photoGroupList.add(group);
        }

        List<PhotoVideo> childList = MyApp.getDaoSession().getPhotoVideoDao().queryBuilder()
                .where(PhotoVideoDao.Properties.Type.eq(P2PConstants.PhotoVideoType.PICTURE),
                        PhotoVideoDao.Properties.Date.ge(startDate), PhotoVideoDao.Properties.Date.le(endDate))
                .orderDesc(PhotoVideoDao.Properties.TriggerTime).list();

        for (PhotoVideo photoVideo : childList) {

            int index = dates.indexOf(photoVideo.getDate());
            if (index != -1) {
                photoGroupList.get(index).addSubItem(photoVideo);
            }
        }

        for (int i = photoGroupList.size() - 1; i >= 0; i--) {
            PhotoVideoGroup group = photoGroupList.get(i);
            if (group.getSubItems() == null || group.getSubItems().isEmpty()) {
                photoGroupList.remove(i);
            }
        }

        return photoGroupList;
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Override
    protected boolean ignoreEventSelf() {
        return true;
    }

    @Override
    protected void onXEventRecv(EdwinEvent<?> event) {
        super.onXEventRecv(event);
        switch (event.getEventCode()) {
            case Constants.EdwinEventType.EVENT_TAKE_PHOTO:
            case Constants.EdwinEventType.EVENT_PHOTO_VIDEO_UPDATE:
                //TODO---新增时也可不必全部刷新--暂作刷新处理
                prepareRefresh();
                executeRefresh();
                break;

            case Constants.EdwinEventType.EVENT_DEV_UPDATE_NAME:
                EdwinDevice updateDev = (EdwinDevice) event.getData();
                if (updateDev != null) {
                    for (MultiItemEntity item : mAdapter.getData()) {
                        if (item instanceof PhotoVideo) {
                            PhotoVideo child = (PhotoVideo) item;
                            child.setDevice(updateDev);
                        }

                    }
                    mAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void OnPhotoClick(PhotoVideoGroup group, int groupPosition, PhotoVideo child, int childPosition) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.KEY_PHOTO_GROUP, group);
        bundle.putInt(Constants.BundleKey.KEY_PHOTO_GROUP_POS, groupPosition);
        bundle.putInt(Constants.BundleKey.KEY_PHOTO_POS, childPosition);
        gotoActivityForResult(PhotoWatchAty.class, REQUEST_CODE_FOR_PHOTO_WATCH, bundle);
    }

    public void gotoDelete() {
        if (isFragVisible() && !swipe.isRefreshing()) {
            ArrayList<PhotoVideoGroup> groupList = mAdapter.getGroupList();
            if (!groupList.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(Constants.BundleKey.KEY_DELETE_LIST, groupList);
                bundle.putInt(Constants.BundleKey.KEY_DELETE_TYPE, P2PConstants.PhotoVideoType.PICTURE);
                gotoActivityForResult(DeletePhotoVideoAty.class, REQUEST_CODE_FOR_DELETE, bundle);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_FOR_DELETE || requestCode == REQUEST_CODE_FOR_PHOTO_WATCH) && resultCode == RESULT_OK) {
            prepareRefresh();
            executeRefresh();
        }
    }

    /**
     * 列表个数发生改变
     */
    public interface OnPhotoListener {
        void onPhotoListSizeChanged(boolean isEmpty);
    }

}
