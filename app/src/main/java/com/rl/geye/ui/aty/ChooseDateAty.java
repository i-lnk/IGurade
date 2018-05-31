package com.rl.geye.ui.aty;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.edwintech.vdp.jni.ApiMgrV2;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.utils.DateUtil;
import com.rl.geye.R;
import com.rl.geye.base.BaseP2PAty;
import com.rl.geye.constants.Constants;
import com.rl.geye.decorators.DisableDecorator;
import com.rl.geye.decorators.EventDecorator;
import com.rl.geye.decorators.OneDayDecorator;
import com.rl.geye.ui.dlg.ChooseYearMonthDialog;
import com.rl.p2plib.bean.MonthRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/11/7.
 * 页面
 */
public class ChooseDateAty extends BaseP2PAty implements OnDateSelectedListener, OnMonthChangedListener {

    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.calendarView)
    MaterialCalendarView widget;
    private MonthRecord mMonthRecord;
    private int curYear;
    private int curMonth;
    private int dotRadius;


    @Override
    protected int getLayoutId() {
        return R.layout.aty_choose_date;

    }


    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.title_choose_date);
    }

    @Override
    protected void onP2PStatusChanged() {

    }


    @Override
    protected void initViewsAndEvents() {

        dotRadius = getResources().getDimensionPixelSize(R.dimen.dot_radius);


        widget.setHeaderTextAppearance(R.style.My_TextAppearance_MaterialCalendarWidget_Header);
        widget.setDateTextAppearance(R.style.My_TextAppearance_MaterialCalendarWidget_Date);
        widget.setWeekDayTextAppearance(R.style.My_TextAppearance_MaterialCalendarWidget_WeekDay);
//        widget.setLeftArrowMask( ContextCompat.getDrawable(this,R.mipmap.ic_pre) );
//        widget.setRightArrowMask( ContextCompat.getDrawable(this,R.mipmap.ic_cal_next));

        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);
//        widget.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
//        widget.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);

        Calendar instance = Calendar.getInstance();
        widget.setSelectedDate(instance);


//        widget.addDecorators(
//                new EnableOneToTenDecorator()
//                new MySelectorDecorator(this),
//                oneDayDecorator
//        );
//        new HighlightWeekendsDecorator()

        widget.addDecorator(new DisableDecorator(true));


        Calendar instance2 = Calendar.getInstance();
        instance2.set(Calendar.DAY_OF_MONTH, instance2.getActualMaximum(Calendar.DAY_OF_MONTH));

        widget.state().edit()
                .setMaximumDate(instance2)
                .commit();

        widget.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                return String.format("%04d.%02d", day.getYear(), day.getMonth() + 1);
            }
        });

        widget.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));


        widget.setOnTitleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChooseYearMonthDialog dialog = new ChooseYearMonthDialog();
                dialog.setTitle(getString(R.string.start_time))
                        .setCurTime(widget.getCurrentDate().getYear(), widget.getCurrentDate().getMonth() + 1)
                        .setOkStr(getString(R.string.str_ok)).setCancelStr(getString(R.string.str_cancel))
                        .setOnTimeChooseListener(new ChooseYearMonthDialog.OnTimeChooseListener() {

                            @Override
                            public void onTimeChoose(int year, int month) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, month - 1);
                                widget.setCurrentDate(CalendarDay.from(cal), true);
                            }
                        }).show(getSupportFragmentManager(), "__choose_start_time__");
            }
        });

    }

    @Override
    protected void onClickView(View v) {

    }


    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull final CalendarDay date, boolean selected) {
//        oneDayDecorator.setDate(date.getDate());
//        widget.invalidateDecorators();
        widget.clearSelection();
        widget.setSelectedDate(CalendarDay.today());

        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, mDevice);
        bundle.putParcelable(Constants.BundleKey.KEY_CAL_DATE, date);
        gotoActivity(FolderVideoAty.class, bundle);


    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {


        curYear = date.getYear();
        curMonth = date.getMonth() + 1;

        ApiMgrV2.getMonthRecord(mDevice.getDevId(), DateUtil.getDateStr5(date.getCalendar().getTimeInMillis()));//
    }


    @Override
    protected MyP2PCallBack getP2PCallBack() {
        return new MyP2PCallBack() {
            @Override
            public void onGetMonthRecord(String did, int msgType, MonthRecord monthRecord) {
                super.onGetMonthRecord(did, msgType, monthRecord);
                if (monthRecord != null && isSameDevice(did)) {
                    mMonthRecord = monthRecord;
                    new ApiSimulator().executeOnExecutor(ThreadPoolMgr.getCustomThreadPool());
                }
            }
        };
    }

    /**
     * Simulate an API call to show how to add decorators
     */
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {

            Calendar calendar = Calendar.getInstance();

            calendar.set(curYear, curMonth - 1, 1);

            ArrayList<CalendarDay> dates = new ArrayList<>();
            for (int i = 1; i < 32; i++) {
                CalendarDay day = CalendarDay.from(calendar);
                if (mMonthRecord.getDatas()[i]) {
                    dates.add(day);
                }
                calendar.add(Calendar.DATE, 1);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            if (isFinishing()) {
                return;
            }

            widget.addDecorators(new EventDecorator(ContextCompat.getColor(getActivity(), R.color.home_color), dotRadius, calendarDays),
                    new DisableDecorator(calendarDays));
        }
    }


}
