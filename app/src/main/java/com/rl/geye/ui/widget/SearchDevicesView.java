package com.rl.geye.ui.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.rl.geye.R;


/**
 * @author NickyHuang
 * @ClassName: SearchDevicesView
 * @Description: 雷达扫描View
 * @date 2016-3-14
 */
public class SearchDevicesView extends RelativeLayout {

    public static final String TAG = "SearchDevicesView";
    public Context context;

//	private long TIME_DIFF = 50;

    int[] lineColor = new int[]{0x7B, 0x7B, 0x7B};
    int[] innerCircle0 = new int[]{0xb9, 0xff, 0xFF};
    int[] innerCircle1 = new int[]{0xdf, 0xff, 0xFF};
    int[] innerCircle2 = new int[]{0xec, 0xff, 0xFF};

    int[] argColor = new int[]{0xF3, 0xf3, 0xfa};

    private float offsetArgs = 0;
    private boolean isSearching = false;
    private Bitmap bitmap;
    //	private Bitmap bitmap1;
    private Bitmap bitmap2;


    private int leftPadding = 0;


    public SearchDevicesView(Context context) {
        super(context);
        this.context = context;
        initBitmap();
    }

    public SearchDevicesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initBitmap();
    }

    public SearchDevicesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initBitmap();
    }

    public boolean isSearching() {
        return isSearching;
    }

    public void setSearching(boolean isSearching) {
        this.isSearching = isSearching;
        offsetArgs = 0;
        invalidate();
    }

    private void initBitmap() {
        leftPadding = getResources().getDimensionPixelSize(R.dimen.search_padding_left);

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.gplus_search_bg));
        }
//		if(bitmap1 == null){
//			bitmap1 = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.locus_round_click));
//		}
        if (bitmap2 == null) {
            bitmap2 = Bitmap.createBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.gplus_search_args));
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2, getHeight() / 2 - bitmap.getHeight() / 2, null);

        if (isSearching) {
//			Rect rMoon = new Rect(getWidth()/2-bitmap2.getWidth(),getHeight()/2,getWidth()/2,getHeight()/2+bitmap2.getHeight()); 
            Rect rMoon = new Rect(getWidth() / 2 - bitmap.getWidth() / 2 + leftPadding, getHeight() / 2, getWidth() / 2 - bitmap.getWidth() / 2 + bitmap2.getWidth(), getHeight() / 2 + bitmap2.getHeight());
            canvas.rotate(offsetArgs, getWidth() / 2, getHeight() / 2);
            canvas.drawBitmap(bitmap2, null, rMoon, null);
            offsetArgs = offsetArgs + 3;
        } else {

            canvas.drawBitmap(bitmap2, getWidth() / 2 - bitmap2.getWidth(), getHeight() / 2, null);
        }

//		canvas.drawBitmap(bitmap1,  getWidth() / 2 - bitmap1.getWidth() / 2, getHeight() / 2 - bitmap1.getHeight() / 2, null);

        if (isSearching)
            invalidate();
//			postInvalidateDelayed(TIME_DIFF);

    }

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {	
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:		
//			handleActionDownEvenet(event);
//			return true;
//		case MotionEvent.ACTION_MOVE: 
//			return true;
//		case MotionEvent.ACTION_UP:
//			return true;
//		}
//		return super.onTouchEvent(event);
//	}
//	
//	private void handleActionDownEvenet(MotionEvent event){
//		RectF rectF = new RectF(getWidth() / 2 - bitmap1.getWidth() / 2, 
//								getHeight() / 2 - bitmap1.getHeight() / 2, 
//								getWidth() / 2 + bitmap1.getWidth() / 2, 
//								getHeight() / 2 + bitmap1.getHeight() / 2);
//		
//		if(rectF.contains(event.getX(), event.getY())){
//			CWLogger.d(TAG, "click search device button");
//			if(!isSearching()) {
//				setSearching(true);
//			}else{
//				setSearching(false);
//			}
//		}
//	}

}