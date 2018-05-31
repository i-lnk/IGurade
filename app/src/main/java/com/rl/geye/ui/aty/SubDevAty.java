package com.rl.geye.ui.aty;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.edwintech.vdp.jni.ApiMgrV2;
import com.nicky.framework.tableview.CustomTableItem;
import com.nicky.framework.tableview.UITableView;
import com.nicky.framework.tableview.ViewItem;
import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.commons.utils.ClickUtil;
import com.rl.geye.R;
import com.rl.geye.base.BaseP2PAty;
import com.rl.geye.bean.SubType;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.SubDevice;
import com.rl.geye.ui.dlg.InputDialog;
import com.rl.p2plib.constants.P2PConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/11/7.
 * 433子设备修改
 */
public class SubDevAty extends BaseP2PAty implements UITableView.TableClickListener {

    private static final int ID_TYPE = 1001;
    private static final int ID_NAME = 1002;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.table_01)
    UITableView table1;
    @BindView(R.id.ly_all)
    View lyAll;
    private CustomTableItem itemName;
    private CustomTableItem itemType;
//    private CustomTableItem itemBlock;

    private SubDevice mCurDev; //当前子设备


    private SubDevice mOrigDev;

    //    private int position = -1;
    private SubType mSubDevType;
    private List<SubType> subTypes = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.aty_sub_dev;
    }

    @Override
    protected View getVaryTargetView() {
        return lyAll;
    }

    @Override
    protected void onP2PStatusChanged() {

    }

    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {

//            position = fromIntent.getIntExtra("_POSITION_",-1);
            mOrigDev = fromIntent.getParcelableExtra(Constants.BundleKey.KEY_SUB_DEV);
            if (mOrigDev != null)
                mCurDev = (SubDevice) mOrigDev.clone();
//            XLog.i(TAG," mCurDev--->"+mCurDev);
        }
        return mOrigDev != null && super.initPrepareData();
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.str_device);
    }

    @Override
    protected void initViewsAndEvents() {
//        if( mOrigDev==null || mCurDev ==null) {
//            finish();
//            return;
//        }

        tvTitle.setText(mCurDev.getName());

        itemName = new CustomTableItem(this, CustomTableItem.ITEM_TYPE_COMMON);
        itemType = new CustomTableItem(this, CustomTableItem.ITEM_TYPE_COMMON);
//        itemBlock = new CustomTableItem( this, CustomTableItem.ITEM_TYPE_SWITCH );

        itemName.setName(getString(R.string.dev_name));
        int iconRid = R.mipmap.ic_433_remote_3;
        switch (mCurDev.getType()) {
            case P2PConstants.SubDevType.REMOTE_CONTROL:
                iconRid = R.mipmap.ic_433_remote_3;
                break;
            case P2PConstants.SubDevType.ALARM:
                iconRid = R.mipmap.ic_433_alarm_3;
                break;
            case P2PConstants.SubDevType.OTHER:
                iconRid = R.mipmap.ic_433_other_3;
                break;
        }
        itemName.setIconImageResource(iconRid);
        itemName.setValue(mCurDev.getName());


        mSubDevType = new SubType(mCurDev.getType());
        subTypes.clear();
        subTypes.add(new SubType(P2PConstants.SubDevType.REMOTE_CONTROL));
        subTypes.add(new SubType(P2PConstants.SubDevType.ALARM));
        subTypes.add(new SubType(P2PConstants.SubDevType.OTHER));

        itemType.setName(getString(R.string.dev_type));
        itemType.setIconImageResource(R.mipmap.ic_dev_type);
        itemType.setIconRightVisibility(View.INVISIBLE);
        itemType.setValue(mSubDevType.getName());

//        itemBlock.setName(getString(R.string.dev_block));
//        itemBlock.setIconImageResource(R.drawable.ic_block);
//        itemBlock.setOnXCheckedChangeListener(new CustomTableItem.OnXCheckedChangeListener() {
//
//            @Override
//            public void onXCheckedChanged(boolean isChecked) {
//                mCurDev.setBlock(isChecked);
//            }
//        });
//        itemBlock.setChecked(mCurDev.isBlock());

        table1.clear();
        table1.addViewItem(new ViewItem(itemName, ID_NAME));
        table1.addViewItem(new ViewItem(itemType, ID_TYPE));
//        table1.addViewItem(new ViewItem(itemBlock));
        table1.commit();

//        table1.setClickable( itemBlock,false );
        table1.setClickable(itemType, false);//设备类型更改无效

        table1.setTableClickListener(this);

    }

    @Override
    protected void onClickView(View v) {

    }


    @Override
    public void onTableClick(ViewItem view) {
        switch (view.getViewId()) {
            case ID_NAME:
                onClickDevName();
                break;
            case ID_TYPE:
//                ChooseDialog<SubType> dialog = new ChooseDialog<>();
//                dialog.setTitle(getString(R.string.choose_dev_type))
//                        .setListDatas(subTypes).setChoosedItem(mSubDevType)
//                        .setOnItemChooseListener(new ChooseDialog.OnItemChooseListener<SubType>() {
//
//                            @Override
//                            public void onItemChoose(int position,SubType data) {
//                                mSubDevType = data;
//                                mCurDev.setType( mSubDevType.getType() );
//                                itemType.setValue( mSubDevType.getName() );
//
//                                int iconRid = R.mipmap.ic_433_remote_3;
//                                switch( mCurDev.getType() ){
//                                    case Constants.SubDevType.REMOTE_CONTROL:
//                                        iconRid = R.mipmap.ic_433_remote_3;
//                                        break;
//                                    case Constants.SubDevType.ALARM:
//                                        iconRid = R.mipmap.ic_433_alarm_3;
//                                        break;
//                                    case Constants.SubDevType.OTHER:
//                                        iconRid = R.mipmap.ic_433_other_3;
//                                        break;
//                                }
//                                itemName.setIconImageResource(iconRid);
//                                updateSubDev();
//                            }
//                        }).show( getSupportFragmentManager(),"__SUB_DEV_TYPE_DLG__");
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View v = findViewById(R.id.item_delete);
                if (v != null) {
                    v.setOnLongClickListener(mMenuItemLongClickListener);
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
            case R.id.item_delete:
                if (ClickUtil.isFastClick(getActivity(), toolbar))
                    return super.onOptionsItemSelected(item);

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.str_delete)
                        .content(R.string.tips_del_dev)
                        .positiveText(R.string.str_ok)
                        .negativeText(R.string.str_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                executeDelete();
                            }
                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.item_delete).setVisible(mOrigDev != null && mDevice != null);// && position!=-1
        return super.onPrepareOptionsMenu(menu);
    }


    private void onClickDevName() {
        InputDialog dialog = new InputDialog();
        dialog.setOkStr(getString(R.string.str_ok)).setCancelStr(getString(R.string.str_cancel));
        dialog.setTitle(getString(R.string.dev_name)).setHint(getString(R.string.dev_name));
        dialog.setShowEt(true);
        dialog.setMaxLength(22);
        dialog.setContent(itemName.getValue().getName());
        dialog.setOnEditClick(new InputDialog.OnEditClickListener() {

            @Override
            public void onResult(String result) {
                if (!mCurDev.getName().equals(result)) {
                    mCurDev.setName(result);
                    itemName.setValue(result);
                    updateSubDev();
                }
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show(getSupportFragmentManager(), "__sub_dev_name_dlg__");
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

    @Override
    public void onBackPressed() {
        if (mOrigDev != null && mCurDev != null) {
            if (!mOrigDev.getName().equals(mCurDev.getName())
                    || mOrigDev.getType() != mCurDev.getType()) {
                //|| mOrigDev.isBlock() != mCurDev.isBlock()
                fromIntent.putExtra(Constants.BundleKey.KEY_SUB_DEV, mCurDev);
//                fromIntent.putExtra("_POSITION_", position);
                setResult(RESULT_OK, fromIntent);
            }
        }
        super.onBackPressed();
    }


    /**
     * 删除子设备
     */
    private void executeDelete() {
        showLoadDialog(R.string.deleting, new EdwinTimeoutCallback(3000) {
            @Override
            public void onTimeOut() {
                finish();
            }
        }, null);
        ApiMgrV2.deleteSubDev(mDevice.getDevId(), mCurDev.getId());
//        TbSubDevice.getInstance().deleteDeviceById(mCurDev.getId(), mDevice.getDevId());
    }


    /**
     * 修改子设备
     */
    private void updateSubDev() {
        ApiMgrV2.updateSubDev(mDevice.getDevId(), mCurDev.getId(), mCurDev.getName(), mCurDev.getType());
    }


}
