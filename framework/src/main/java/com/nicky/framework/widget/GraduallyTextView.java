package com.nicky.framework.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2016/3/30.
 * 渐显( 用于显示 加载中... 这种 )
 */
public class GraduallyTextView extends AppCompatEditText {

    private CharSequence text;
    private int startY = 0;
    private float progress;
    private boolean isLoading;
    private Paint mPaint;
    private int textLength;
    private boolean isStop = true;
    private float scaleX;
    private int duration = 3000;
    private float sigleDuration;

    private ValueAnimator valueAnimator;


    public GraduallyTextView(Context context) {
        super(context);
        init();
    }


    public GraduallyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public GraduallyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        setBackground(null);
        setCursorVisible(false);
        setFocusable(false);
        setEnabled(false);
        setFocusableInTouchMode(false);

        valueAnimator = ValueAnimator.ofFloat(0, 100).setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setRepeatCount(Animation.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        progress = (Float) animation.getAnimatedValue();
                        GraduallyTextView.this.invalidate();
                    }
                });
    }


    public void changeText(CharSequence text) {
        stopLoading();
//        Log.w("changeText","==========================>getGravity() : "+getGravity());
        setText(text);
        startLoading();
    }


    public void startLoading() {
        if (!isStop) {
            return;
        }
        textLength = getText().length();
        if (TextUtils.isEmpty(getText().toString())) {
            return;
        }
        isLoading = true;
        isStop = false;
        text = getText();

//        scaleX = getTextScaleX() * 10;
//        startY = 50;

        Rect bounds = new Rect();

        mPaint.setColor(getCurrentTextColor());
        mPaint.setTextSize(getTextSize());
        mPaint.getTextBounds(String.valueOf(text), 0, String.valueOf(text).length(), bounds);


        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        measure(0,w);


        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        Log.w("startLoading","=======================getMeasuredHeight()  : "+getMeasuredHeight() );
        Log.w("startLoading","=======================fontMetrics  : "+fontMetrics );

        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;

        scaleX = getMeasuredWidth() / 2 - bounds.width() / 2;
        startY = baseline;

//        Log.w("startLoading","=======================getWidth()  : "+getWidth() );
//        Log.w("startLoading","=======================getMeasuredWidth()  : "+getMeasuredWidth() );
        setMinWidth(getMeasuredWidth());
        setText("");
        setHint("");
        valueAnimator.start();
        sigleDuration = 100f / textLength;
    }


    public void stopLoading() {
        isLoading = false;
        valueAnimator.end();
        valueAnimator.cancel();
        isStop = true;
        setText(text);
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (!isLoading) {
            return;
        }
        if (visibility == View.VISIBLE) {
//            valueAnimator.resume();
        	valueAnimator.start();
        }else {
//            valueAnimator.pause();
        	valueAnimator.end();
        }
    }


    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isStop) {
            mPaint.setAlpha(255);
            if (progress / sigleDuration >= 1) {
                canvas.drawText(String.valueOf(text), 0,
                        (int) (progress / sigleDuration), scaleX, startY,
                        mPaint);
            }
            mPaint.setAlpha(
                    (int) (255 * ((progress % sigleDuration) / sigleDuration)));
            int lastOne = (int) (progress / sigleDuration);
            if (lastOne < textLength) {
                canvas.drawText(String.valueOf(text.charAt(lastOne)), 0, 1,
                        scaleX + getPaint().measureText(
                                text.subSequence(0, lastOne).toString()),
                        startY, mPaint);
            }
        }
    }
}
