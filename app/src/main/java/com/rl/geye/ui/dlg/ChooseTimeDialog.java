package com.rl.geye.ui.dlg;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.nicky.framework.base.BaseDialog;
import com.rl.geye.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 时间选择框
 *
 * @author NickyHuang
 */
public class ChooseTimeDialog extends BaseDialog {

    private static final int INVALID_VAL = -1;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.time_h)
    WheelPicker wheelH;
    @BindView(R.id.time_m)
    WheelPicker wheelM;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_ok)
    Button btnOk;
    private int curHour = INVALID_VAL;
    private int curMinute = INVALID_VAL;


    private String okStr;
    private String cancelStr;
    private String title;


    private List<String> HourList = new ArrayList<>();
    private List<String> MinuteList = new ArrayList<>();

    private int mMinType = MinuteType.TYPE_PER_1;
    private OnTimeChooseListener mOnTimeChooseListener;
    private WheelPicker.OnItemSelectedListener mItemSelectedListener = new WheelPicker.OnItemSelectedListener() {

        @Override
        public void onItemSelected(WheelPicker picker, Object data, int position) {

            switch (picker.getId()) {
                case R.id.time_h:
                    curHour = getIntVal((String) data);
                    break;
                case R.id.time_m:
                    curMinute = getIntVal((String) data);
                    break;
            }

        }
    };

    public ChooseTimeDialog setOnTimeChooseListener(OnTimeChooseListener listener) {
        this.mOnTimeChooseListener = listener;
        return this;
    }

    @Override
    protected void initDialog(Dialog dialog) {
        setDialogGravity(Gravity.CENTER);
        View view = inflateContentView(R.layout.dialog_choose_time);
        ButterKnife.bind(this, view);
        if (okStr != null) {
            btnOk.setText(okStr);
        }

        if (cancelStr != null) {
            btnCancel.setText(cancelStr);
        }

        if (title != null) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }

        initCurTime();


        if (HourList.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                HourList.add(String.format("%02d", i));
            }
        }
        if (MinuteList.isEmpty()) {
            int perMinute = 1;
            switch (mMinType) {
                case MinuteType.TYPE_PER_1:
                    perMinute = 1;
                    break;
                case MinuteType.TYPE_PER_5:
                    perMinute = 5;
                    break;
                case MinuteType.TYPE_PER_10:
                    perMinute = 10;
                    break;
                case MinuteType.TYPE_PER_15:
                    perMinute = 15;
                    break;
                case MinuteType.TYPE_PER_20:
                    perMinute = 20;
                    break;
                case MinuteType.TYPE_PER_30:
                    perMinute = 30;
                    break;
                default:
                    perMinute = 1;
                    break;
            }

            for (int i = 0; i < 60; i = i + perMinute) {
                MinuteList.add(String.format("%02d", i));
            }
        }
        formatCurTime();

        wheelH.setData(HourList);
        wheelM.setData(MinuteList);
        wheelH.setSelectedItemPosition(HourList.indexOf(String.format("%02d", curHour)));
        wheelM.setSelectedItemPosition(MinuteList.indexOf(String.format("%02d", curMinute)));
        wheelH.setOnItemSelectedListener(mItemSelectedListener);
        wheelM.setOnItemSelectedListener(mItemSelectedListener);

        dialog.setContentView(view);
    }

    private void initCurTime() {
        if (curHour < 0 || curHour > 23
                || curMinute < 0 || curMinute > 59) {
            Calendar cal = Calendar.getInstance();
            curHour = cal.get(Calendar.HOUR_OF_DAY);
            curMinute = cal.get(Calendar.MINUTE);
        }
    }

    private void formatCurTime() {
        int perMinute = 1;
        switch (mMinType) {
            case MinuteType.TYPE_PER_1:
                perMinute = 1;
                break;
            case MinuteType.TYPE_PER_5:
                perMinute = 5;
                break;
            case MinuteType.TYPE_PER_10:
                perMinute = 10;
                break;
            case MinuteType.TYPE_PER_15:
                perMinute = 15;
                break;
            case MinuteType.TYPE_PER_20:
                perMinute = 20;
                break;
            case MinuteType.TYPE_PER_30:
                perMinute = 30;
                break;
            default:
                perMinute = 1;
                break;
        }
        if (curMinute % perMinute > 0) {
            curMinute = curMinute - curMinute % perMinute;
        }
        if (curMinute < 0) {
            curMinute = 0;
        }
    }

    public ChooseTimeDialog setOkStr(String okStr) {
        this.okStr = okStr;
        return this;
    }

    public ChooseTimeDialog setCancelStr(String cancelStr) {
        this.cancelStr = cancelStr;
        return this;
    }

    public ChooseTimeDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public ChooseTimeDialog setCurTime(int hour, int minute) {
        curHour = hour;
        curMinute = minute;
        return this;
    }

    public ChooseTimeDialog setMinuteType(int type) {
        mMinType = type;
        return this;
    }

    @OnClick({R.id.btn_ok, R.id.btn_cancel})
    void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (mOnTimeChooseListener != null) {
                    mOnTimeChooseListener.onTimeChoose(curHour, curMinute);
                }
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    private int getIntVal(String str) {
        int val = 0;
        try {
            val = Integer.parseInt(str);
        } catch (Exception e) {
        }
        return val;
    }


    public interface OnTimeChooseListener {
        void onTimeChoose(int hour, int minute);
    }

    public class MinuteType {
        public static final int TYPE_PER_1 = 0; //间隔1分
        public static final int TYPE_PER_5 = 1; //间隔5分
        public static final int TYPE_PER_10 = 2; //间隔10分
        public static final int TYPE_PER_15 = 3; //间隔15分
        public static final int TYPE_PER_20 = 4;
        public static final int TYPE_PER_30 = 5;
    }


}
