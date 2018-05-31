package com.rl.geye.decorators;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

/**
 * Decorate
 */
public class DisableDecorator implements DayViewDecorator {


    private HashSet<CalendarDay> dates;

    private boolean allDisable = false;

    public DisableDecorator(Collection<CalendarDay> dates) {
        this.dates = new HashSet<>(dates);
    }

    public DisableDecorator(boolean allDisable) {
        this.allDisable = allDisable;
    }


    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return allDisable || !dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setDaysDisabled(true);
    }

}
