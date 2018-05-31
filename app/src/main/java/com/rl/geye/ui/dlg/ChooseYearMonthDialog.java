package com.rl.geye.ui.dlg;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.aigestudio.wheelpicker.WheelPicker.OnItemSelectedListener;
import com.nicky.framework.base.BaseDialog;
import com.rl.geye.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 年月选择框
 *
 * @author NickyHuang
 */
public class ChooseYearMonthDialog extends BaseDialog {

    private static final int INVALID_VAL = -1;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.time_h)
    WheelPicker wheelY;
    @BindView(R.id.time_m)
    WheelPicker wheelM;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_ok)
    Button btnOk;
    private int curYear = INVALID_VAL;
    private int curMonth = INVALID_VAL;


    private String okStr;
    private String cancelStr;
    private String title;


    private List<String> YearList = new ArrayList<>();
    private List<String> MonthList = new ArrayList<>();


    private OnTimeChooseListener mOnTimeChooseListener;
    private OnItemSelectedListener mItemSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(WheelPicker picker, Object data, int position) {

            switch (picker.getId()) {
                case R.id.time_h:
                    curYear = getIntVal((String) data);
                    break;
                case R.id.time_m:
                    curMonth = getIntVal((String) data);
                    break;
            }

        }
    };

    public ChooseYearMonthDialog setOnTimeChooseListener(OnTimeChooseListener listener) {
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
        if (YearList.isEmpty()) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR) - 9;
            for (int i = 0; i < 10; i++) {
                YearList.add(String.format("%04d", year + i));

            }
        }
        if (MonthList.isEmpty()) {
            for (int i = 1; i < 13; i++) {
                MonthList.add(String.format("%02d", i));
            }
        }


        wheelY.setData(YearList);
        wheelM.setData(MonthList);
        wheelY.setSelectedItemPosition(YearList.indexOf(String.format("%04d", curYear)));
        wheelM.setSelectedItemPosition(MonthList.indexOf(String.format("%02d", curMonth)));
        wheelY.setOnItemSelectedListener(mItemSelectedListener);
        wheelM.setOnItemSelectedListener(mItemSelectedListener);

        dialog.setContentView(view);
    }


    private void initCurTime() {
        if (curMonth < 0 || curMonth > 12 || curYear < 1970) {
            Calendar cal = Calendar.getInstance();
            curMonth = cal.get(Calendar.MONTH);
            curYear = cal.get(Calendar.YEAR);
        }
    }


    public ChooseYearMonthDialog setOkStr(String okStr) {
        this.okStr = okStr;
        return this;
    }

    public ChooseYearMonthDialog setCancelStr(String cancelStr) {
        this.cancelStr = cancelStr;
        return this;
    }


    public ChooseYearMonthDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public ChooseYearMonthDialog setCurTime(int year, int month) {
        curYear = year;
        curMonth = month;
        return this;
    }


    @OnClick({R.id.btn_ok, R.id.btn_cancel})
    void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (mOnTimeChooseListener != null) {
                    mOnTimeChooseListener.onTimeChoose(curYear, curMonth);
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
        void onTimeChoose(int year, int month);
    }


}
