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
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.orhanobut.logger.Logger;
import com.rl.commons.BaseApp;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.adapter.PhotoVideoAdapter;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.bean.PhotoVideoGroup;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.PhotoVideo;
import com.rl.geye.db.bean.PhotoVideoDao;
import com.rl.p2plib.constants.P2PConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Nicky on 2017/9/5.
 */

public class DeletePhotoVideoAty extends BaseMyAty {

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

    private PhotoVideoAdapter mAdapter;
    private List<PhotoVideoGroup> mPhotoVideoList;
    private int photoVideoType = P2PConstants.PhotoVideoType.PICTURE;//

    private AsyncTask<Void, Void, Void> delTask;


    @Override
    protected boolean initPrepareData() {
        boolean res = false;
        if (fromIntent != null) {
            photoVideoType = fromIntent.getIntExtra(Constants.BundleKey.KEY_DELETE_TYPE, P2PConstants.PhotoVideoType.PICTURE);
            mPhotoVideoList = fromIntent.getParcelableArrayListExtra(Constants.BundleKey.KEY_DELETE_LIST);
            res = mPhotoVideoList != null && !mPhotoVideoList.isEmpty();
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
        for (PhotoVideoGroup group : mPhotoVideoList) {
            group.setSubItems(null);
            group.setExpanded(false);
            dates.add(group.getDate());
        }
        String startDate = mPhotoVideoList.get(mPhotoVideoList.size() - 1).getDate();
        String endDate = mPhotoVideoList.get(0).getDate();

        List<PhotoVideo> childList = MyApp.getDaoSession().getPhotoVideoDao().queryBuilder()
                .where(PhotoVideoDao.Properties.Type.eq(photoVideoType),
                        PhotoVideoDao.Properties.Date.ge(startDate), PhotoVideoDao.Properties.Date.le(endDate))
                .orderDesc(PhotoVideoDao.Properties.TriggerTime).list();


        for (PhotoVideo photoVideo : childList) {
            int index = dates.indexOf(photoVideo.getDate());
            if (index != -1) {
                photoVideo.setChecked(false);
                mPhotoVideoList.get(index).addSubItem(photoVideo);
            }
        }

        List<MultiItemEntity> list = new ArrayList<>();
        list.addAll(mPhotoVideoList);
        mAdapter.setNewData(list);
        if (mAdapter.getData() != null && !mAdapter.getData().isEmpty())
            mAdapter.expand(0);

    }

    @Override
    protected void initViewsAndEvents() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mAdapter = new PhotoVideoAdapter(new ArrayList<MultiItemEntity>(), photoVideoType, Glide.with(this));
        mAdapter.setEditMode(true);


        rvDelete.setLayoutManager(layoutManager);
        rvDelete.setItemAnimator(new DefaultItemAnimator());
        rvDelete.setAdapter(mAdapter);

        btnDel.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        ivCheckall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivCheckall.setSelected(!ivCheckall.isSelected());
                mAdapter.toggleSelectAll();
                int selectedCount = mAdapter.getSelectedCount();
                if (selectedCount > 0)
                    btnDel.setText(getString(R.string.delete_with_count, selectedCount));
                else
                    btnDel.setText(getString(R.string.str_delete));
            }
        });
        mAdapter.setOnCheckListener(new PhotoVideoAdapter.OnCheckListener() {
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
                if (mAdapter.getSelectedCount() > 0) {

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
                showLoadDialog(R.string.deleting, new EdwinTimeoutCallback(10 * 1000) {

                    @Override
                    public void onTimeOut() {
                        BaseApp.showToast(R.string.time_out);
                        hideLoadDialog();
                    }
                }, null);
            }

            @Override
            protected Void doInBackground(Void... params) {

                List<PhotoVideo> selectedDatas = mAdapter.getSelectedDatas();
                //TODO
                Logger.i("selectedDatas--> " + selectedDatas);

                for (PhotoVideo data : selectedDatas) {
                    deleteNorFile(data.getPath());
                    deleteNorFile(data.getPathThumb());
                }
                MyApp.getDaoSession().getPhotoVideoDao().deleteInTx(selectedDatas);
                return null;
            }

            protected void onPostExecute(Void result) {
                hideLoadDialog();
                setResult(RESULT_OK, fromIntent);
                if (!isFinishing())
                    finish();

            }
        };
        delTask.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool());
    }


}
