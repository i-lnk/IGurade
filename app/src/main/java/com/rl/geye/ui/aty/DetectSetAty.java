package com.rl.geye.ui.aty;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.edwintech.vdp.jni.ApiMgrV2;
import com.nicky.framework.tableview.CustomTableItem;
import com.nicky.framework.tableview.SwitchTableItem;
import com.nicky.framework.tableview.UITableView;
import com.nicky.framework.tableview.ViewItem;
import com.rl.commons.bean.EdwinItem;
import com.rl.geye.R;
import com.rl.geye.base.BaseP2PAty;
import com.rl.geye.constants.Constants;
import com.rl.geye.ui.dlg.ChooseDataDialog;
import com.rl.geye.ui.dlg.ChooseTimeDialog;
import com.rl.p2plib.bean.DetectInfo;
import com.rl.p2plib.bean.RecordTime;
import com.rl.p2plib.bean.SysInfo;
import com.rl.p2plib.bean.WakeUpData;
import com.rl.p2plib.interf.IDetectTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.rl.geye.constants.Constants.BundleKey.KEY_SYS_INFO;


/**
 * Created by Nicky on 2016/9/21.
 * 移动侦测设置页
 */
public class DetectSetAty extends BaseP2PAty implements UITableView.TableClickListener {


    private static final int ID_START = 1001;
    private static final int ID_END = 1002;
    private static final int ID_RECORD_TIME = 1003;
    /**
     * id for Switch items
     */
    private final int nightId = 1;
    private final int dayId = 2;
    private final int customId = 3;
    private final int audioId = 4;
    private final int recordId = 5;
    private final int pirId = 6;
    private final int alarmRemoveId = 7;
    private final int alarmWakeUpId = 8;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tb_00)
    UITableView tb00;
    @BindView(R.id.tb_01)
    UITableView tb01;
    @BindView(R.id.tb_02)
    UITableView tb02;
    @BindView(R.id.ly_all)
    View lyAll;
    @BindView(R.id.btn_save)
    Button btnSave;
    private CustomTableItem itemRecordTime;
    private SwitchTableItem itemDay;
    private SwitchTableItem itemNight;
    private SwitchTableItem itemCustom;
    private SwitchTableItem itemAudio; //IPC
    private SwitchTableItem itemRecord; //IPC
    private SwitchTableItem itemPir; //pir
    private SwitchTableItem itemAlarmRemove; //防拆
    private SwitchTableItem itemWakeUpRemove; //远程唤醒
    private CustomTableItem itemFrequency; //灵敏度
    private CustomTableItem itemStartTime;   // 开始时间
    private CustomTableItem itemEndTime;     // 结束时间
    private DetectInfo mDetect;
//    private DetectInfo mOrigDetect;

    private RecordTime mRecordTime;
    //    private RecordTime mOrigRecordTime;
    private WakeUpData mWakeData;
    private boolean isDetect = true;//是移动侦测还是自动录像

    private SysInfo mSysInfo;
    private List<EdwinItem> timeList = new ArrayList<>(); //时间列表
    private List<EdwinItem> freqList = new ArrayList<>(); //灵敏度列表
    private boolean flag = false;     //pir报警开关
    private Handler mHandler;
    /**
     * switch item click and check changed callback
     */
    private SwitchTableItem.OnSwitchListener mOnSwitchListener = new SwitchTableItem.OnSwitchListener() {

        @Override
        public void onSwitchClick(int switchId) {
//            modeChanged();
        }

        @Override
        public void onSwitchChanged(int switchId, boolean isChecked) {

            switch (switchId) {

                case nightId:
                    if (isChecked) {
//                        tvModeDesc.setVisibility( isChecked ? View.VISIBLE:View.GONE);
                        itemDay.setChecked(false);
                        itemCustom.setChecked(false);

                        if (isDetect) {
                            mDetect.setNightMode();
                            itemStartTime.setValue(mDetect.getStartTimeStr());
                            itemEndTime.setValue(mDetect.getEndTimeStr());
                        } else {
                            mRecordTime.setNightMode();
                            itemStartTime.setValue(mRecordTime.getStartTimeStr());
                            itemEndTime.setValue(mRecordTime.getEndTimeStr());
                        }
                    }
                    modeChanged();
                    break;
                case dayId:
                    if (isChecked) {
                        itemNight.setChecked(false);
                        itemCustom.setChecked(false);
                        if (isDetect) {
                            mDetect.setDayMode();
                            itemStartTime.setValue(mDetect.getStartTimeStr());
                            itemEndTime.setValue(mDetect.getEndTimeStr());
                        } else {
                            mRecordTime.setDayMode();
                            itemStartTime.setValue(mRecordTime.getStartTimeStr());
                            itemEndTime.setValue(mRecordTime.getEndTimeStr());
                        }
                    }
                    modeChanged();
                    break;
                case customId:
                    if (isChecked) {
                        itemDay.setChecked(false);
                        itemNight.setChecked(false);

                    }
                    tb02.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    modeChanged();
                    break;

                case recordId:
                    mDetect.setRecord(isChecked ? 1 : 0);
                    break;

                case audioId:
                    mDetect.setAudio(isChecked ? 1 : 0);
                    break;
                case pirId:
                    flag = isChecked;
                    if (isChecked) {
                        tb01.setVisibility(itemFrequency, View.VISIBLE);
                    } else {
                        tb01.setVisibility(itemFrequency, View.GONE);
                    }
                    break;
                case alarmRemoveId:
                    mDetect.setRemoveAlarm(isChecked ? 1 : 0);
                    break;
                case alarmWakeUpId:
                    if (mWakeData == null) {
                        mWakeData.setEnable(isChecked ? 1 : 0);
                    }
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.aty_motion_detect;
    }

    @Override
    protected void onP2PStatusChanged() {

    }

    @Override
    protected boolean initPrepareData() {
        super.initPrepareData();
        if (fromIntent != null) {
            isDetect = fromIntent.getBooleanExtra(Constants.BundleKey.KEY_IS_DETECT, true);
            mSysInfo = fromIntent.getParcelableExtra(KEY_SYS_INFO);
            if (mSysInfo.getSupportWakeUpControl() == 1) {
                getWakeUp();
            }
            if (isDetect) {
                mDetect = fromIntent.getParcelableExtra(Constants.BundleKey.KEY_DETECT_INFO);
                Log.i("频率", mDetect.getEnablePir() + "");
                return mDetect != null && mSysInfo != null && super.initPrepareData();
            } else {
                mRecordTime = fromIntent.getParcelableExtra(Constants.BundleKey.KEY_DETECT_INFO);//布防时间
                return mRecordTime != null && mSysInfo != null && super.initPrepareData();
            }
        }
        return false;
    }

    private void getWakeUp() {
        if (mDevice != null) {
            ApiMgrV2.getWakeUpReq(mDevice.getDevId());
        }
    }

    @Override
    public View getVaryTargetView() {
        return lyAll;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
    }

    @Override
    protected void initViewsAndEvents() {
//        BridgeService.addGetSetCallback(this);

        timeList.clear();
        timeList.add(new EdwinItem(String.valueOf(10), 10));
        timeList.add(new EdwinItem(String.valueOf(15), 15));
        timeList.add(new EdwinItem(String.valueOf(20), 20));
        timeList.add(new EdwinItem(String.valueOf(30), 30));
        freqList.clear();
        for (int i = 0; i <= 100; i++) {
            freqList.add(new EdwinItem(String.valueOf(i), i));
        }
        IDetectTime detectTime;
        if (isDetect) {
            detectTime = mDetect;
            if (mDevice.isIpc()) {
                tvTitle.setText(R.string.deployment);
            } else {
                tvTitle.setText(R.string.motion_detect);
            }
        } else {
            detectTime = mRecordTime;
            tvTitle.setText(R.string.auto_record);
            itemRecordTime = new CustomTableItem(this, CustomTableItem.ITEM_TYPE_COMMON);
            itemRecordTime.setName(getString(R.string.mode_record_time));
            itemRecordTime.setIconVisibility(View.GONE);
            itemRecordTime.setIconRightVisibility(View.VISIBLE);
            itemRecordTime.setValue(getString(R.string.record_time_desc, mRecordTime.getVideo_lens()));
        }

        itemDay = new SwitchTableItem(this);
        itemNight = new SwitchTableItem(this);
        itemCustom = new SwitchTableItem(this);

        itemStartTime = new CustomTableItem(this, CustomTableItem.ITEM_TYPE_COMMON);
        itemEndTime = new CustomTableItem(this, CustomTableItem.ITEM_TYPE_COMMON);


        itemDay.setName(getString(R.string.mode_day));
        itemDay.setIconVisibility(View.GONE);
        itemDay.setOnSwitchListener(dayId, mOnSwitchListener);
        itemDay.setChecked(detectTime.isDayMode());

        itemNight.setName(getString(R.string.mode_night));
        itemNight.setIconVisibility(View.GONE);
        itemNight.setOnSwitchListener(nightId, mOnSwitchListener);
        itemNight.setChecked(detectTime.isNightMode());

        itemCustom.setName(getString(R.string.mode_custom));
        itemCustom.setIconVisibility(View.GONE);
        itemCustom.setOnSwitchListener(customId, mOnSwitchListener);
        itemCustom.setChecked(detectTime.isCustomMode());


        itemStartTime.setName(getString(R.string.start_time));
        itemStartTime.setIconVisibility(View.GONE);
        itemStartTime.setIconRightVisibility(View.GONE);
        itemStartTime.setValue(detectTime.getStartTimeStr());

        itemEndTime.setName(getString(R.string.end_time));
        itemEndTime.setIconVisibility(View.GONE);
        itemEndTime.setIconRightVisibility(View.GONE);
        itemEndTime.setValue(detectTime.getEndTimeStr());


        if (isDetect) {

            itemAudio = new SwitchTableItem(this);
            itemRecord = new SwitchTableItem(this);
            itemPir = new SwitchTableItem(this);
            itemAlarmRemove = new SwitchTableItem(this);
            itemWakeUpRemove = new SwitchTableItem(this);
            itemFrequency = new CustomTableItem(this, CustomTableItem.ITEM_TYPE_COMMON);

            itemAudio.setName(getString(R.string.mode_audio));
            itemAudio.setIconVisibility(View.GONE);
            itemAudio.setOnSwitchListener(audioId, mOnSwitchListener);
            itemAudio.setChecked(mDetect.getAudio() == 1);

            itemRecord.setName(getString(R.string.mode_record));
            itemRecord.setIconVisibility(View.GONE);
            itemRecord.setOnSwitchListener(recordId, mOnSwitchListener);
            itemRecord.setChecked(mDetect.getRecord() == 1);


            itemPir.setName(getString(R.string.mode_pir));
            itemPir.setIconVisibility(View.GONE);
            itemPir.setOnSwitchListener(pirId, mOnSwitchListener);
            itemPir.setChecked(!(mDetect.getEnablePir() == 0));

            itemAlarmRemove.setName(getString(R.string.mode_alarm));
            itemAlarmRemove.setIconVisibility(View.GONE);
            itemAlarmRemove.setOnSwitchListener(alarmRemoveId, mOnSwitchListener);
            itemAlarmRemove.setChecked(mDetect.getEnableRemoveAlaram() == 1);

            itemWakeUpRemove.setName(getString(R.string.Item_wake_up));
            itemWakeUpRemove.setIconVisibility(View.GONE);
            itemWakeUpRemove.setOnSwitchListener(alarmWakeUpId, mOnSwitchListener);

            itemFrequency.setName(getString(R.string.pir));
            itemFrequency.setIconRightVisibility(View.VISIBLE);
            itemFrequency.setValue(mDetect.getEnablePir() + "");
        }
        if (!isDetect) {//自动录像
            tb00.clear();
            tb00.addViewItem(new ViewItem(itemRecordTime, ID_RECORD_TIME));
            tb00.commit();
//            tb01.clear();
//            tb01.addViewItem(new ViewItem(itemDay));
//            tb01.addViewItem(new ViewItem(itemNight));
//            tb01.addViewItem(new ViewItem(itemCustom));
//            tb01.commit();
            tb02.clear();
            tb02.addViewItem(new ViewItem(itemStartTime, ID_START));
            tb02.addViewItem(new ViewItem(itemEndTime, ID_END));
            tb02.commit();
        } else {//布防
            if (mDevice.isIpc()) {
                tb01.clear();
                tb01.addViewItem(new ViewItem(itemDay));
                tb01.addViewItem(new ViewItem(itemNight));
                tb01.addViewItem(new ViewItem(itemCustom));
                tb01.addViewItem(new ViewItem(itemRecord));
                tb01.addViewItem(new ViewItem(itemAudio));
                tb01.commit();

                tb02.clear();
                tb02.addViewItem(new ViewItem(itemStartTime, ID_START));
                tb02.addViewItem(new ViewItem(itemEndTime, ID_END));
                tb02.commit();
            } else {
                tb01.clear();
                if (isDetect && mSysInfo.getSupportPIR() == 1) {
                    tb01.addViewItem(new ViewItem(itemPir));
                    tb01.addViewItem(new ViewItem(itemFrequency, R.id.tb_set_pir));
                    if (mDetect.getEnablePir() == 0) {
                        itemPir.setChecked(false);
                        tb01.setVisibility(itemFrequency, View.GONE);
                    } else {
                        itemPir.setChecked(true);
                        tb01.setVisibility(itemFrequency, View.VISIBLE);
                    }
                }
                if (isDetect && mSysInfo.getSupportRemoveAlarm() == 1) {
                    tb01.addViewItem(new ViewItem(itemAlarmRemove));
                }
                if (mSysInfo.getSupportWakeUpControl() == 1) {
                    tb01.addViewItem(new ViewItem(itemWakeUpRemove));
                }
                tb01.commit();
            }
        }

        tb02.setVisibility(detectTime.isCustomMode() ? View.VISIBLE : View.GONE);
        tb01.setClickable(itemDay, false);
        tb01.setClickable(itemNight, false);
        tb01.setClickable(itemCustom, false);
        tb00.setTableClickListener(this);
        tb02.setTableClickListener(this);
        tb01.setTableClickListener(this);
        btnSave.setOnClickListener(this);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case R.id.msg_update_wakeup:
                        if (mWakeData.getEnable() == 0) {
                            itemWakeUpRemove.setChecked(false);
                        } else {
                            itemWakeUpRemove.setChecked(true);
                        }
                        break;
                }
            }
        };
    }

    @Override
    protected MyP2PCallBack getP2PCallBack() {
        return new MyP2PCallBack() {
            @Override
            public void onGetWakeUp(String did, int msgType, WakeUpData wakeUpData) {
                if (wakeUpData != null && isSameDevice(did)) {
                    mWakeData = wakeUpData;
                    mHandler.sendEmptyMessage(R.id.msg_update_wakeup);
                }
            }
        };
    }

    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if (mDetect != null || mRecordTime != null) { //&& mDetect.equals(mOrigDetect)
                    if (!flag && mDetect != null)
                        mDetect.setEnablePir(0);
                    save();
                }
                finish();
                break;
        }
    }

    @Override
    public void onTableClick(ViewItem view) {
        int startH, startM, endH, endM;
        if (isDetect) {
            startH = mDetect.getStart_hour();
            startM = mDetect.getStart_mins();
            endH = mDetect.getClose_hour();
            endM = mDetect.getClose_mins();
        } else {
            startH = mRecordTime.getStart_hour();
            startM = mRecordTime.getStart_mins();
            endH = mRecordTime.getClose_hour();
            endM = mRecordTime.getClose_mins();
        }

        switch (view.getViewId()) {
            case ID_RECORD_TIME:
                ChooseDataDialog dlg = new ChooseDataDialog();
                dlg.setTitle(getString(R.string.mode_record_time)).setUnit(getString(R.string.unit_sec))
                        .setCurData(new EdwinItem(String.valueOf(mRecordTime.getVideo_lens()),
                                mRecordTime.getVideo_lens()))
                        .setDatas(timeList)
                        .setOkStr(getString(R.string.str_ok)).setCancelStr(getString(R.string.str_cancel))
                        .setOnDataChooseListener(new ChooseDataDialog.OnDataChooseListener() {
                            @Override
                            public void onDataChoose(EdwinItem data) {

                                mRecordTime.setVideo_lens(data.getVal());
                                itemRecordTime.setValue(getString(R.string.record_time_desc, mRecordTime.getVideo_lens()));
                            }
                        })
                        .show(getSupportFragmentManager(), "__choose_record_time__");
                break;
            case ID_START:
                ChooseTimeDialog dialog = new ChooseTimeDialog();
                dialog.setTitle(getString(R.string.start_time)).setMinuteType(ChooseTimeDialog.MinuteType.TYPE_PER_15)
                        .setCurTime(startH, startM)
                        .setOkStr(getString(R.string.str_ok)).setCancelStr(getString(R.string.str_cancel))
                        .setOnTimeChooseListener(new ChooseTimeDialog.OnTimeChooseListener() {

                            @Override
                            public void onTimeChoose(int hour, int minute) {
                                if (isDetect) {
                                    mDetect.setStart_hour(hour);
                                    mDetect.setStart_mins(minute);
                                    itemStartTime.setValue(mDetect.getStartTimeStr());
                                } else {
                                    mRecordTime.setStart_hour(hour);
                                    mRecordTime.setStart_mins(minute);
                                    itemStartTime.setValue(mRecordTime.getStartTimeStr());
                                }
                            }
                        }).show(getSupportFragmentManager(), "__choose_start_time__");
                break;

            case ID_END:
                ChooseTimeDialog dialog2 = new ChooseTimeDialog();
                dialog2.setTitle(getString(R.string.end_time)).setMinuteType(ChooseTimeDialog.MinuteType.TYPE_PER_15)
                        .setCurTime(endH, endM)
                        .setOkStr(getString(R.string.str_ok)).setCancelStr(getString(R.string.str_cancel))
                        .setOnTimeChooseListener(new ChooseTimeDialog.OnTimeChooseListener() {

                            @Override
                            public void onTimeChoose(int hour, int minute) {
                                if (isDetect) {
                                    mDetect.setClose_hour(hour);
                                    mDetect.setClose_mins(minute);
                                    itemEndTime.setValue(mDetect.getEndTimeStr());
                                } else {
                                    mRecordTime.setClose_hour(hour);
                                    mRecordTime.setClose_mins(minute);
                                    itemEndTime.setValue(mRecordTime.getEndTimeStr());
                                }
                            }
                        }).show(getSupportFragmentManager(), "__choose_end_time__");
                break;
            case R.id.tb_set_pir:
                ChooseDataDialog dlgPir = new ChooseDataDialog();
                dlgPir.setTitle(getString(R.string.pir))
                        .setCurData(new EdwinItem(String.valueOf(mDetect.getEnablePir()),
                                mDetect.getEnablePir()))
                        .setDatas(freqList)
                        .setOkStr(getString(R.string.str_ok)).setCancelStr(getString(R.string.str_cancel))
                        .setOnDataChooseListener(new ChooseDataDialog.OnDataChooseListener() {
                            @Override
                            public void onDataChoose(EdwinItem data) {
                                mDetect.setEnablePir(data.getVal());
                                itemFrequency.setValue(mDetect.getEnablePir() + "");
                            }
                        }).show(getSupportFragmentManager(), "__set_pir__");
                break;

        }


    }

    /**
     * 布防模式改变
     */
    private void modeChanged() {
        if (!itemNight.isChecked() && !itemDay.isChecked() && !itemCustom.isChecked()) {
            if (isDetect)
                mDetect.setEnable(0);
            else
                mRecordTime.setType(0);
        } else {
            if (isDetect)
                mDetect.setEnable(1);
            else
                mRecordTime.setType(4);
        }


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_save, menu);
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                final View v = findViewById(R.id.item_save);
//                if (v != null) {
//                    v.setOnLongClickListener(mMenuItemLongClickListener);
//                }
//            }
//        });
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.item_save:
//                if (ClickUtil.isFastClick(getActivity(), toolbar))
//                    return super.onOptionsItemSelected(item);
//                if (mDetect != null || mRecordTime != null) { //&& mDetect.equals(mOrigDetect)
//                    if (!flag && mDetect!=null)
//                        mDetect.setEnablePir(0);
//                    save();
//                }
//                finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * 保存移动侦测设置(自动录像)
     */
    private void save() {
        if (isDetect) {
            ApiMgrV2.saveDetect(mDevice.getDevId(), mDetect, mDevice.isIpc());
            if (!mDevice.isIpc() && mWakeData != null) {
                ApiMgrV2.saveWakeUp(mDevice.getDevId(), mWakeData);
            }
            setResult(RESULT_OK, fromIntent);
            fromIntent.putExtra(Constants.BundleKey.KEY_DETECT_INFO, mDetect);
        } else {
            ApiMgrV2.saveAutoRecord(mDevice.getDevId(), mRecordTime);
            setResult(RESULT_OK, fromIntent);
            fromIntent.putExtra(Constants.BundleKey.KEY_DETECT_INFO, mRecordTime);
        }
    }

}
