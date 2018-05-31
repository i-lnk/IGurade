package com.rl.geye.util;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.rl.geye.R;


/**
 * Created by Nicky on 2017/5/10.
 */

public class SnackbarUtil {

    public static final int Info = 1;
    public static final int Confirm = 2;
    public static final int Warning = 3;
    public static final int Alert = 4;
    public static final int Default = 0;


    public static int red = 0xfff44336;
    public static int green = 0xff4caf50;
    public static int blue = 0xff2195f3;
    public static int orange = 0xffffc107;

    //    private static int mGravity = Gravity.CENTER_HORIZONTAL|Gravity.TOP;
    private static int mGravity = Gravity.CENTER_HORIZONTAL;


    /**
     * 短显示Snackbar
     */
    public static Snackbar ShortSnackbar(View view, String message) {
        return ShortSnackbar(view, message, Default);
    }

    /**
     * 长显示Snackbar
     */
    public static Snackbar LongSnackbar(View view, String message) {
        return LongSnackbar(view, message, Default);
    }


    /**
     * 短显示Snackbar，自定义颜色
     *
     * @param view
     * @param message
     * @param messageColor
     * @param backgroundColor
     * @return
     */
    public static Snackbar ShortSnackbar(View view, String message, int messageColor, int backgroundColor) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        setSnackbarColor(snackbar, messageColor, backgroundColor);
        setSnackbarTextGravity(snackbar, mGravity);
        return snackbar;
    }

    /**
     * 长显示Snackbar，自定义颜色
     *
     * @param view
     * @param message
     * @param messageColor
     * @param backgroundColor
     * @return
     */
    public static Snackbar LongSnackbar(View view, String message, int messageColor, int backgroundColor) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        setSnackbarColor(snackbar, messageColor, backgroundColor);
        setSnackbarTextGravity(snackbar, mGravity);
        return snackbar;
    }

    /**
     * 自定义时常显示Snackbar，自定义颜色
     *
     * @param view
     * @param message
     * @param messageColor
     * @param backgroundColor
     * @return
     */
    public static Snackbar IndefiniteSnackbar(View view, String message, int duration, int messageColor, int backgroundColor) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setDuration(duration);
        setSnackbarColor(snackbar, messageColor, backgroundColor);
        setSnackbarTextGravity(snackbar, mGravity);
        return snackbar;
    }

    /**
     * 短显示Snackbar，可选预设类型
     *
     * @param view
     * @param message
     * @param type
     * @return
     */
    public static Snackbar ShortSnackbar(View view, String message, int type) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        switchType(snackbar, type);
        setSnackbarTextGravity(snackbar, mGravity);
        return snackbar;
    }

    /**
     * 长显示Snackbar，可选预设类型
     *
     * @param view
     * @param message
     * @param type
     * @return
     */
    public static Snackbar LongSnackbar(View view, String message, int type) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        switchType(snackbar, type);
        setSnackbarTextGravity(snackbar, mGravity);
        return snackbar;
    }

    /**
     * 自定义时常显示Snackbar，可选预设类型
     *
     * @param view
     * @param message
     * @param type
     * @return
     */
    public static Snackbar IndefiniteSnackbar(View view, String message, int duration, int type) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setDuration(duration);
        switchType(snackbar, type);
        setSnackbarTextGravity(snackbar, Gravity.CENTER);
        return snackbar;
    }

    //选择预设类型
    private static void switchType(Snackbar snackbar, int type) {
        switch (type) {
            case Info:
                setSnackbarColor(snackbar, blue);
                break;
            case Confirm:
                setSnackbarColor(snackbar, green);
                break;
            case Warning:
                setSnackbarColor(snackbar, orange);
                break;
            case Alert:
                setSnackbarColor(snackbar, Color.YELLOW, red);
                break;
        }
    }

    /**
     * 设置Snackbar背景颜色
     *
     * @param snackbar
     * @param backgroundColor
     */
    public static void setSnackbarColor(Snackbar snackbar, int backgroundColor) {
        View view = snackbar.getView();
        if (view != null) {
            view.setBackgroundColor(backgroundColor);
        }
    }

    /**
     * 设置Snackbar文字颜色
     *
     * @param snackbar
     * @param messageColor
     */
    public static void setSnackbarTextColor(Snackbar snackbar, int messageColor) {
        View view = snackbar.getView();
        if (view != null) {
            ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);
        }
    }

    /**
     * 设置Snackbar文字和背景颜色
     *
     * @param snackbar
     * @param messageColor
     * @param backgroundColor
     */
    public static void setSnackbarColor(Snackbar snackbar, int messageColor, int backgroundColor) {
        View view = snackbar.getView();
        if (view != null) {
            view.setBackgroundColor(backgroundColor);
            setSnackbarTextColor(snackbar, messageColor);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void setSnackbarTextGravity(Snackbar snackbar, int gravity) {
        View view = snackbar.getView();
        if (view != null) {
            TextView snackTextView = ((TextView) view.findViewById(R.id.snackbar_text));
            snackTextView.setGravity(gravity);
            snackTextView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

//            ViewGroup.LayoutParams vl = view.getLayoutParams();
//            CoordinatorLayout.LayoutParams cl = new CoordinatorLayout.LayoutParams(vl.width,vl.height);
//
//            //设置显示位置居中
//            cl.gravity = Gravity.TOP;
//            view.setLayoutParams(cl);

        }
    }

//    /**
//     * 向Snackbar中添加view
//     * @param snackbar
//     * @param layoutId
//     * @param index 新加布局在Snackbar中的位置
//     */
//    public static void SnackbarAddView( Snackbar snackbar,int layoutId,int index) {
//        View snackbarview = snackbar.getView();
//        Snackbar.SnackbarLayout snackbarLayout=(Snackbar.SnackbarLayout)snackbarview;
//
//        View add_view = LayoutInflater.from(snackbarview.getContext()).inflate(layoutId,null);
//
//        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//        p.gravity= Gravity.CENTER_VERTICAL;
//
//        snackbarLayout.addView(add_view,index,p);
//    }
}
