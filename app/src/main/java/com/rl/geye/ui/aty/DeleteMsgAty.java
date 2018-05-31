package com.rl.geye.ui.aty;


import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.rl.commons.BaseApp;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.adapter.RecordMsgAdapter;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.bean.RecordGroup;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinRecord;
import com.rl.geye.db.bean.EdwinRecordDao;
import com.rl.geye.db.bean.SubDevice;
import com.rl.p2plib.constants.P2PConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Nicky on 2017/9/5.
 */

public class DeleteMsgAty extends BaseMyAty {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_checkall)
    ImageView ivCheckall;
    @BindView(R.id.rv_delete)
    RecyclerView rvDelete;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_del)
    Button btnDel;
    @BindView(R.id.ly_all)
    LinearLayout lyAll;

    private RecordMsgAdapter recordAdapter;
    private List<RecordGroup> mRecordList;

    private AsyncTask<Void, Void, Void> delTask;


    @Override
    protected boolean initPrepareData() {
        boolean res = false;
        if (fromIntent != null) {

            mRecordList = fromIntent.getParcelableArrayListExtra(Constants.BundleKey.KEY_DELETE_LIST);
            res = mRecordList != null && !mRecordList.isEmpty();
        }
        return res && super.initPrepareData();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.aty_delete;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.str_delete);
    }


    private void initListDatas() {

        List<String> dates = new ArrayList<>();
        for (RecordGroup group : mRecordList) {
            group.setSubItems(null);
            group.setExpanded(false);
            dates.add(group.getDate());
        }
        String startDate = mRecordList.get(mRecordList.size() - 1).getDate();
        String endDate = mRecordList.get(0).getDate();

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
                record.setChecked(false);
                mRecordList.get(index).addSubItem(record);
            }
        }

        List<MultiItemEntity> list = new ArrayList<>();
        list.addAll(mRecordList);
        recordAdapter.setNewData(list);
        if (recordAdapter.getData() != null && !recordAdapter.getData().isEmpty())
            recordAdapter.expand(0);

    }

    @Override
    protected void initViewsAndEvents() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recordAdapter = new RecordMsgAdapter(new ArrayList<MultiItemEntity>());
        recordAdapter.setEditMode(true);


        rvDelete.setLayoutManager(layoutManager);
        rvDelete.setItemAnimator(new DefaultItemAnimator());
        rvDelete.setAdapter(recordAdapter);

        btnDel.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        ivCheckall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivCheckall.setSelected(!ivCheckall.isSelected());
                recordAdapter.toggleSelectAll();
                int selectedCount = recordAdapter.getSelectedCount();
                if (selectedCount > 0)
                    btnDel.setText(getString(R.string.delete_with_count, selectedCount));
                else
                    btnDel.setText(getString(R.string.str_delete));
            }
        });
        recordAdapter.setOnCheckListener(new RecordMsgAdapter.OnCheckListener() {
            @Override
            public void OnCheckChange(boolean isSelectedAll, int selectedCount) {
                ivCheckall.setSelected(isSelectedAll);
                if (selectedCount > 0)
                    btnDel.setText(getString(R.string.delete_with_count, selectedCount));
                else
                    btnDel.setText(getString(R.string.str_delete));
            }
        });
        initListDatas();


    }

    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {

            case R.id.btn_cancel:
                onBackPressed();
                break;

            case R.id.btn_del:
                if (recordAdapter.getSelectedCount() > 0) {

                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.str_delete)
                            .content(R.string.tips_delete)
                            .positiveText(R.string.str_ok)
                            .negativeText(R.string.str_cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    executeDelete();
                                }
                            }).show();


                }
                break;
        }
    }

    private void cancelDelete() {
        if (delTask != null) {
            delTask.cancel(true);
            delTask = null;
        }
    }


    private void executeDelete() {
        cancelDelete();
        delTask = new AsyncTask<Void, Void, Void>() {
            protected void onPreExecute() {
                showLoadDialog(R.string.deleting, new EdwinTimeoutCallback(5000) {

                    @Override
                    public void onTimeOut() {
                        BaseApp.showToast(R.string.time_out);
                        hideLoadDialog();
                    }
                }, null);
            }

            @Override
            protected Void doInBackground(Void... params) {

                List<EdwinRecord> selectedDatas = recordAdapter.getSelectedDatas();
//                Logger.i("selectedDatas--> " +selectedDatas);
                MyApp.getDaoSession().getEdwinRecordDao().deleteInTx(selectedDatas);
                return null;
            }

            protected void onPostExecute(Void result) {
                hideLoadDialog();
                setResult(RESULT_OK, fromIntent);
                if (!isFinishing())
                    finish();
        /*
//                boolean isEmpty ;
                if( DeleteType.DEL_MSG==mDeleteType ){
                    recordAdapter.removeSelectedDatas();
//                    isEmpty = recordAdapter.getGroupCount()==0;
//                    fromIntent.putParcelableArrayListExtra( Constants.BundleKey.KEY_DELETE_LIST, (ArrayList<RecordGroup>) recordAdapter.getDatas() );
                }
                btnDel.setText(getString(R.string.str_delete));
                finish();*/
//                if(isEmpty){
//                    finish();
//                }
//                else{
//                    if( listView.getCount() > 0)
//                    {
//                        listView.expandGroup(0);//默认展开第一组
//                        for ( int i=1;i<listView.getCount();i++){
//                            listView.collapseGroup(i);
//                        }
//                    }
//                }
            }
        };
        delTask.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool());
    }


}
