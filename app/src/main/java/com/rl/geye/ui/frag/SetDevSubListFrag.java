package com.rl.geye.ui.frag;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.edwintech.vdp.jni.ApiMgrV2;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.adapter.SubDevAdapter;
import com.rl.geye.base.BaseP2PFrag;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.SubDevice;
import com.rl.geye.ui.aty.SubDevAty;
import com.rl.p2plib.utils.IdUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by Nicky on 2017/8/12.
 * 设备设置(子设备列表)
 */

public class SetDevSubListFrag extends BaseP2PFrag {


    private static final int MSG_REFRESH_LIST = 30;
    private static final int REQUEST_CODE_SUB_DEV = 31;
    @BindView(R.id.list_sub_dev)
    ListView listSubDev;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    private SubDevAdapter mAdapter;
    private List<SubDevice> mDevList = new ArrayList<>();
    private Handler mHandler;

    @Override
    protected void onP2PStatusChanged() {

    }

    @Override
    protected void onAttachToContext(Context context) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_dev_set_sublist;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViewsAndEvents() {
        mAdapter = new SubDevAdapter(getActivity(), mDevList);
        listSubDev.setAdapter(mAdapter);
        listSubDev.setEmptyView(tvEmpty);

        if (isOnline) {
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case MSG_REFRESH_LIST:
                            mAdapter.notifyDataSetChanged();
                            break;
                    }
                }
            };

            listSubDev.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Bundle bundle = new Bundle();
//                    bundle.putInt("_POSITION_",position);
                    bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, mDevice);
                    bundle.putParcelable(Constants.BundleKey.KEY_SUB_DEV, mDevList.get(position));
                    gotoActivityForResult(SubDevAty.class, REQUEST_CODE_SUB_DEV, bundle);
                }
            });
        } else {

        }
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isOnline)
            getSubDevList();
    }

    @Override
    protected MyP2PCallBack getP2PCallBack() {

        return new MyP2PCallBack() {
            @Override
            public void onGetSubList(String did, int msgType, String data) {
                super.onGetSubList(did, msgType, data);

                if (mDevice != null && IdUtil.isSameId(did, mDevice.getDevId())) {
                    mDevList.clear();
                    List<SubDevice> list = JSON.parseObject(data, new TypeReference<List<SubDevice>>() {
                    });
                    if (list != null) {
                        MyApp.getDaoSession().getSubDeviceDao().deleteAll();
                        if (!list.isEmpty()) {
                            for (SubDevice dev : list) {
                                dev.setPid(mDevice.getDevId());
                                mDevList.add(dev);
                            }
                            try {
                                MyApp.getDaoSession().getSubDeviceDao().insertInTx(mDevList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_REFRESH_LIST);
                    postEdwinEvent(Constants.EdwinEventType.EVENT_RECORD_UPDATE);//
                }

            }
        };
    }

    /**
     * 获取子设备列表
     */
    private void getSubDevList() {
        ApiMgrV2.getSubDevList(mDevice.getDevId());
    }

}
