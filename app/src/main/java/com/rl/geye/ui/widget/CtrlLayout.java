package com.rl.geye.ui.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rl.geye.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 2017/5/3.
 * 监测页 顶部与底部的控制栏 (带隐藏显示动画)
 */
public class CtrlLayout extends RelativeLayout {

    private final static int DIRECTION_LEFT = 0;
    private final static int DIRECTION_RIGHT = 1; //
    private final static int DIRECTION_UP = 2; //
    private final static int DIRECTION_DOWN = 3; //
    private final String TAG = "CtrlLayout";
    private boolean isAnimationRunning = false;
    private Thread autoHideThread = null; //自动隐藏线程
    private volatile boolean autoHideRunFlag = true;
    private boolean isCtrlShow = true; //两端控制区是否显示
    private List<Integer> childIdList = new ArrayList<>();//保存 child id
    private int mOffset = 100; //隐藏动画移动值
    //    private boolean isBottom = true;
    private int moveDirection = 0;
    private Handler mHandler = new Handler();
    private AnimationListener mListener;
    private Drawable mBgDrawable;

    public CtrlLayout(Context context) {
        super(context);
        init(context);
    }

    public CtrlLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CtrlLayout);
//        isBottom = typedArray.getBoolean(R.styleable.CtrlLayout_isBottom,true);
        moveDirection = typedArray.getInt(R.styleable.CtrlLayout_move_direction, DIRECTION_UP);
        mOffset = typedArray.getDimensionPixelSize(R.styleable.CtrlLayout_moveDistance, 100);

//        mBgDrawable = getBackground();
//        if(mBgDrawable!=null){
//            int width = mBgDrawable.getIntrinsicWidth();
//            int height = mBgDrawable.getIntrinsicHeight();
//            if(height>mOffset)
//                height = mOffset;
//            mBgDrawable.setBounds(0, 0, width, height );
//        }
    }

    public void setAnimationListener(AnimationListener listener) {
        mListener = listener;
    }

    private void init(Context context) {

    }

    private void initChildIdList() {
        childIdList.clear();
        getChildList(CtrlLayout.this);
    }

    /**
     * 遍历获取child list
     */
    private void getChildList(ViewGroup viewGroup) {
        if (viewGroup == null)
            return;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                getChildList((ViewGroup) child);
            } else {
                childIdList.add(child.getId());
            }
        }
    }

    public boolean isAnimationRunning() {
        return isAnimationRunning;
    }

    /**
     * 显示隐藏两端控制区域
     */
    public void toggleShowHide() {
        if (isAnimationRunning)
            return;
        autoHideStop();
        isCtrlShow = !isCtrlShow;
        isAnimationRunning = true;
        if (mListener != null) {
            mListener.onAnimationStart();
        }
        initChildIdList();
        if (isCtrlShow) {
//            showAnimation(CtrlLayout.this);
            startShow();
        } else {
//            hideAnimation(CtrlLayout.this);
            startHide();
        }
    }

    /**
     * 开始自动隐藏线程
     */
    public synchronized void autoHideStart() {

        if (autoHideThread == null) {
            autoHideRunFlag = true;
            autoHideThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        /** 多次sleep 以便快速中断   */
                        for (int i = 0; i < 50; i++) {
                            Thread.sleep(100);
                            if (!autoHideRunFlag) {
                                break;
                            }
                        }
                        if (autoHideRunFlag) {
                            isCtrlShow = !isCtrlShow;
                            isAnimationRunning = true;
                            if (mListener != null) {
                                mListener.onAnimationStart();
                            }
                            initChildIdList();
//                            hideAnimation(CtrlLayout.this);
                            startHide();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            autoHideThread.start();
        }
    }

    /**
     * 停止自动隐藏线程
     */
    public void autoHideStop() {
        if (autoHideThread == null) {
            autoHideRunFlag = false;
            return;
        }
        if (autoHideThread.isAlive()) {
            try {
                autoHideRunFlag = false;
                autoHideThread.join(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        autoHideThread = null;
    }

    /**
     * 自动隐藏线程重启
     */
    public void autoHideRestart() {
        autoHideStop();
        autoHideStart();
    }

    /**
     * 是否为需要回退动画的View
     */
    protected boolean isIconChild(View child) {
//        return (child instanceof ImageView)||(child instanceof Button);
        return false; //无需回弹动画
    }

    private boolean isChild(View child) {
        return indexOfChild(child) != -1;
    }


    /**
     * child 显示隐藏执行完成
     */
    protected synchronized void onChildShowCompleted(View child, boolean isShow) {
//        XLog.w(TAG, " ");
//        XLog.w(TAG, "=========================");
//        XLog.w(TAG, "=========================isShow: " + isShow + " , child" + child);
        if (childIdList.contains(child.getId())) {
            childIdList.remove(Integer.valueOf(child.getId()));
        }
        if (childIdList.isEmpty()) {
//            XLog.w(TAG, "=========================onAnimationFinish");
            isAnimationRunning = false;
            if (mListener != null) {
                mListener.onAnimationFinish(isShow);
            }
//            if( isShow ){
//                if( autoHideThread==null || !autoHideThread.isAlive() )
//                {
//                    autoHideStart();
//                }
//            }
        }
    }

    private void startShow() {
//        mHandler.post(new ShowViewRunnable(CtrlLayout.this, true));
        mHandler.postDelayed(new ShowViewRunnable(CtrlLayout.this, true), 20);
//        for ( int i=0;i<getChildCount();i++ ){
//            mHandler.postDelayed(new ShowViewRunnable( getChildAt(i) , true), 20 * (i+1) );
//        }
    }

    private void startHide() {
//        mHandler.post(new ShowViewRunnable(CtrlLayout.this, false));
        mHandler.postDelayed(new ShowViewRunnable(CtrlLayout.this, false), 20);
//        for ( int i=0;i<getChildCount();i++ ){
//            mHandler.postDelayed(new ShowViewRunnable( getChildAt(i) , false), 20 * (i+1) );
//        }
    }

    /**
     * 显示动画
     */
    private void showAnimation(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
//			child.setVisibility(View.INVISIBLE);
//            mHandler.post(new ShowViewRunnable(child, true));
            mHandler.postDelayed(new ShowViewRunnable(child, true), i * 20);
        }
    }

    /**
     * 隐藏动画
     */
    private void hideAnimation(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
//            mHandler.post(new ShowViewRunnable(child, false));
            mHandler.postDelayed(new ShowViewRunnable(child, false), (layout.getChildCount() - i) * 20);
        }
    }

    public interface AnimationListener {
        void onAnimationStart();

        void onAnimationFinish(boolean isShow);
    }

    /**
     * child 显示隐藏动画
     */
    public class ShowViewRunnable implements Runnable {
        private View child;
        private boolean isShow = true;

        public ShowViewRunnable(View child, boolean isShow) {
            this.child = child;
            this.isShow = isShow;
        }

        @Override
        public void run() {
            if (child != null) {
                ValueAnimator fadeAnim;
                //isChild(child)
                if (CtrlLayout.this == child || (isIconChild(child) && child.getVisibility() == VISIBLE)) {
                    int yPos = mOffset;
                    int startPos, endPos;
                    if (isShow) {
                        startPos = (DIRECTION_DOWN == moveDirection || DIRECTION_RIGHT == moveDirection) ? yPos : -yPos;
                        endPos = 0;
                    } else {
                        startPos = 0;
                        endPos = (DIRECTION_DOWN == moveDirection || DIRECTION_RIGHT == moveDirection) ? yPos : -yPos;
                    }

                    fadeAnim = ObjectAnimator.ofFloat(child,
                            (DIRECTION_DOWN == moveDirection || DIRECTION_UP == moveDirection) ?
                                    "translationY" : "translationX", startPos, endPos);
                    fadeAnim.setDuration(300);
//                    if( (isIconChild(child ) && child.getVisibility()==VISIBLE) ){
//                        KickBackAnimator kickAnimator = new KickBackAnimator();
//                        kickAnimator.setDuration(200);
//                        fadeAnim.setEvaluator(kickAnimator);
//                    }
                    fadeAnim.start();
                    fadeAnim.addListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            onChildShowCompleted(child, isShow);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }
                    });
                } else {
                    onChildShowCompleted(child, isShow);
                }
                if (child instanceof ViewGroup) {
                    if (isShow)
                        showAnimation((ViewGroup) child);
                    else
                        hideAnimation((ViewGroup) child);
                }
            }
        }
    }


}
