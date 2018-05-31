package com.rl.geye.ui.frag;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.bean.EdwinEvent;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.adapter.RecordMsgAdapter;
import com.rl.geye.base.BaseRecyclerViewFrag;
import com.rl.geye.bean.RecordGroup;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.db.bean.EdwinRecord;
import com.rl.geye.db.bean.EdwinRecordDao;
import com.rl.geye.db.bean.SubDevice;
import com.rl.geye.ui.aty.DeleteMsgAty;
import com.rl.p2plib.constants.P2PConstants;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nicky on 2016/9/13.
 * 首页 -- 消息记录
 */
public class HomeMsgFrag extends BaseRecyclerViewFrag {

    private final static int REQUEST_CODE_FOR_DELETE = 32; //删除页面

    private RecordMsgAdapter mAdapter;


    private AsyncTask<Void, Void, Void> refreshTask;
    private AsyncTask<Void, Void, Void> loadmoreTask;

    private int groupSize = 0;

    private OnMsgListener mListener;

    @Override
    protected void onAttachToContext(Context context) {
        if (context instanceof OnMsgListener) {
            mListener = (OnMsgListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMsgListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected void initEmptyView() {
        tvEmptyTips.setText(R.string.tips_empty_record);
        ivEmpty.setImageResource(R.mipmap.ic_empty_msg);
    }

    @Override
    protected BaseMultiItemQuickAdapter getAdapter() {
        if (mAdapter == null) {
            List<MultiItemEntity> list = new ArrayList<>();
            mAdapter = new RecordMsgAdapter(list);
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

                List<RecordGroup> recordGroupList = getGroupList();

                list = new ArrayList<>();
                if (recordGroupList != null && !recordGroupList.isEmpty()) {
                    list.addAll(recordGroupList);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                mAdapter.setNewData(list);
                if (mAdapter.getData() != null && !mAdapter.getData().isEmpty())
                    mAdapter.expand(0);

                mListener.onMsgListSizeChanged(mAdapter.getData().isEmpty());

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
                List<RecordGroup> recordGroupList = getGroupList();
                needLoadMore = (recordGroupList != null && recordGroupList.size() == PAGE_COUNT);
                list = new ArrayList<>();
                if (recordGroupList != null && !recordGroupList.isEmpty()) {
                    list.addAll(recordGroupList);
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

    private List<RecordGroup> getGroupList() {
        List<RecordGroup> recordGroupList = new ArrayList<>();
        List<EdwinRecord> groupList = MyApp.getDaoSession().getEdwinRecordDao()
                .queryBuilder()
                .where(new WhereCondition.StringCondition(" 1 GROUP BY " + EdwinRecordDao.Properties.Date.name))
                .orderDesc(EdwinRecordDao.Properties.TriggerTime)
                .limit(PAGE_COUNT)
                .offset(groupSize)
                .list();

        if (groupList == null || groupList.isEmpty())
            return recordGroupList;
        groupSize += groupList.size();

        List<String> dates = new ArrayList<>();
        String startDate = groupList.get(groupList.size() - 1).getDate();
        String endDate = groupList.get(0).getDate();

        for (EdwinRecord record : groupList) {
            RecordGroup group = new RecordGroup();
            group.setDate(record.getDate());
            dates.add(record.getDate());
            recordGroupList.add(group);
        }

        List<EdwinRecord> childList = MyApp.getDaoSession().getEdwinRecordDao().queryBuilder()
                .where(EdwinRecordDao.Properties.Date.ge(startDate), EdwinRecordDao.Properties.Date.le(endDate))
                .orderDesc(EdwinRecordDao.Properties.TriggerTime).list();

        for (EdwinRecord record : childList) {

            int index = dates.indexOf(record.getDate());
            if (index != -1) {
                if (P2PConstants.PushType.ALARM_433 == record.getType()) {
                    List<SubDevice> subList = MyApp.getDaoSession().getSubDeviceDao().loadAll();
                    if (subList != null && !subList.isEmpty()) {
                        for (SubDevice subDev : subList) {
                            if (subDev.getId().equals(record.getSubDevId())) {
                                record.setSubDev(subDev);
                            }
                        }
                    } else {
                        continue;
                    }
                }
                recordGroupList.get(index).addSubItem(record);
            }
        }

        for (int i = recordGroupList.size() - 1; i >= 0; i--) {
            RecordGroup group = recordGroupList.get(i);
            if (group.getSubItems() == null || group.getSubItems().isEmpty()) {
                recordGroupList.remove(i);
            }
        }
        return recordGroupList;
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
            case Constants.EdwinEventType.EVENT_RECORD_ADD:
            case Constants.EdwinEventType.EVENT_RECORD_UPDATE:
                //TODO
                prepareRefresh();
                executeRefresh();
                break;

            case Constants.EdwinEventType.EVENT_DEV_UPDATE_NAME:
                EdwinDevice updateDev = (EdwinDevice) event.getData();
                if (updateDev != null) {
                    for (MultiItemEntity item : mAdapter.getData()) {
                        if (item instanceof EdwinRecord) {
                            EdwinRecord child = (EdwinRecord) item;
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

    public void gotoDelete() {
        if (isFragVisible() && !swipe.isRefreshing()) {
            ArrayList<RecordGroup> groupList = mAdapter.getGroupList();
            if (!groupList.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(Constants.BundleKey.KEY_DELETE_LIST, groupList);
                gotoActivityForResult(DeleteMsgAty.class, REQUEST_CODE_FOR_DELETE, bundle);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_DELETE && resultCode == Activity.RESULT_OK) {
            prepareRefresh();
            executeRefresh();
        }
    }


    /**
     * 列表个数发生改变
     */
    public interface OnMsgListener {
        void onMsgListSizeChanged(boolean isEmpty);
    }

}
