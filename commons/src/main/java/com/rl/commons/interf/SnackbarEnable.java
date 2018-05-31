package com.rl.commons.interf;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Nicky on 2016/9/13.
 */
public interface SnackbarEnable {

    void showSnackbar(@NonNull CharSequence tipsText, int duration);

    void showSnackbarWithAction(@NonNull CharSequence tipsText, int duration,
                                CharSequence actionText, final View.OnClickListener actionListener);
}
