package com.nicky.framework.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.nicky.framework.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 2017/6/17.
 */

public class IPEditText extends LinearLayout implements TextWatcher {

    private static final String TAG = "IPEditText";
    private static final int DEFAULT_TEXT_MAX_LENGTH = 3;
    private static final int DEFAULT_TEXT_SIZE = 16;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_BORDER_COLOR = Color.argb(60, 0, 0, 0);
    private static final int DEFAULT_BORDER_WIDTH = 2;
    private static final int DEFAULT_POINT_COLOR = Color.BLACK;
    private static final int DEFAULT_POINT_WIDTH = 2;
    private static final int DEFAULT_IP_EDITTEXT_LENGTH = 4;
    public EditText mEditText;
    private int width;
    private int height;
    private Paint paint;
    private int textLength;
    private int textSize;
    private int textColor;
    private int borderColor;
    private int borderWidth;
    private int pointColor;
    private int pointWidth;
    private int editNumber;
    private int default_height = px2dp(20);
    private int default_width = px2dp(60);
    private List<EditText> data = new ArrayList<>();//TODO
    private SuperTextWatcher listener;

    public IPEditText(Context context) {

        this(context, null);

    }

    public IPEditText(Context context, AttributeSet attrs) {

        this(context, attrs, 0);

    }

    public IPEditText(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IPEditText, defStyleAttr, 0);

        textLength = ta.getInt(R.styleable.IPEditText_textLength, DEFAULT_TEXT_MAX_LENGTH);

        textSize = (int) ta.getDimension(R.styleable.IPEditText_IPtextSize, DEFAULT_TEXT_SIZE);

        textColor = ta.getColor(R.styleable.IPEditText_IPtextColor, DEFAULT_TEXT_COLOR);

        borderColor = ta.getColor(R.styleable.IPEditText_borderColor, DEFAULT_BORDER_COLOR);

        borderWidth = (int) ta.getDimension(R.styleable.IPEditText_borderWidth, DEFAULT_BORDER_WIDTH);

        pointColor = ta.getColor(R.styleable.IPEditText_pointColor, DEFAULT_POINT_COLOR);

        pointWidth = (int) ta.getDimension(R.styleable.IPEditText_pointWidth, DEFAULT_POINT_WIDTH);

        editNumber = ta.getInt(R.styleable.IPEditText_editNumber, DEFAULT_IP_EDITTEXT_LENGTH);

        init(context);

        initPaint();

    }

    public float getTextSize() {

        return data.get(0).getTextSize();

    }

    private void initPaint() {

        paint = new Paint();

        paint.setAntiAlias(true);

        paint.setStrokeWidth(2);

        paint.setStyle(Paint.Style.FILL);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init(Context context) {

        for (int i = 0; i < editNumber; i++) {

            EditText edit = new EditText(context);
            edit.setBackground(null);

            edit.setFilters(new InputFilter[]{new InputFilterMinMax("0", "255", "3")});

            edit.setTextSize(textSize);

            edit.setTextColor(textColor);

            edit.setGravity(Gravity.CENTER);

            edit.setInputType(InputType.TYPE_CLASS_NUMBER);

            edit.setMinHeight(default_height);

            edit.setMinWidth(default_width);

            edit.setTag(i);

            edit.setMaxLines(1);

            edit.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));

            edit.addTextChangedListener(this);

            edit.setOnTouchListener(new OnTouchListener() {

                @Override

                public boolean onTouch(View v, MotionEvent event) {

                    mEditText = (EditText) v;

                    return false;

                }

            });

            edit.setOnKeyListener(new OnKeyListener() {

                @Override

                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if (event.getKeyCode() == KeyEvent.KEYCODE_DEL & event.getAction() == KeyEvent.ACTION_DOWN) {

//doSomething();

                        int a = data.indexOf(mEditText);

                        if (a > 0 & mEditText.getSelectionStart() == 0) {

                            data.get(a - 1).requestFocus();

                            mEditText = data.get(a - 1);

                            mEditText.setSelection(mEditText.length());

                        }

                    }

                    return false;

                }

            });

            addView(edit);

            data.add(edit);

        }

        setDividerDrawable(getResources().getDrawable(android.R.drawable.divider_horizontal_textfield));

        setOrientation(LinearLayout.HORIZONTAL);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

//        int l = data.get(0).getLeft();
//        int t = data.get(0).getTop() - getPaddingTop();
//        int r = width - getPaddingRight();
//        int b = height;

         float textY = (height + data.get(0).getTextSize())/2  ;
        float y = textY + 5;

        paint.setColor(borderColor);
        paint.setStrokeWidth(borderWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(0, textY + 5, y+10, y+10, paint);

        int x = width / editNumber;
//        paint.setStrokeWidth(pointWidth);
        paint.setColor(pointColor);
        paint.setStyle(Paint.Style.FILL);

        for (int i = 1; i < data.size(); i++) {
//            canvas.drawPoint(x * i, y, paint);
            canvas.drawCircle(x * i, y, pointWidth, paint);

        }

    }

    @Override

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        width = w;

        height = h;

    }

    @Override

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override

    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (listener != null) {

            listener.afterTextChanged(getSuperEditTextValue());

        }

        if (s.length() == 3) {

            int a = data.indexOf(mEditText);

            if (a < 3) {

                data.get(a + 1).requestFocus();

                mEditText = data.get(a + 1);

            }

        } else if (s.length() == 2) {

            int a = data.indexOf(mEditText);

            if (a < 3 & (Integer.parseInt(s.toString()) > 25)) {

                data.get(a + 1).requestFocus();

                mEditText = data.get(a + 1);

            }

        }

    }

    public String[] getSuperEditTextValue() {
        String[] val = new String[editNumber];
        for (int i = 0; i < editNumber; i++) {
            val[i] = data.get(i).getText().toString();
        }
        return val;
    }


    public void setSuperEditTextValue(String[] strs) {
        int i=0;
        for (String s:strs) {
            data.get(i).setText(s);
            i++;
            if( i==editNumber)
                break;
        }
    }

    public boolean getSuperCompile() {

        for (int i = 0; i < editNumber; i++) {

            String str = data.get(i).getText().toString();

            if (Integer.parseInt(str) <= 255) {

                return true;

            }

        }

        return false;

    }

    public int px2dp(int val) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getResources().getDisplayMetrics());

    }

    public int px2sp(int val) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, val, getResources().getDisplayMetrics());

    }

    public void setSuperTextWatcher(SuperTextWatcher listener) {

        this.listener = listener;

    }

    public interface SuperTextWatcher {

        public void afterTextChanged(String[] s);

    }

    public class InputFilterMinMax implements InputFilter {

        private Double min, max;

        private int maxCount;

        public InputFilterMinMax(Double min, Double max) {

            this.min = min;

            this.max = max;

        }

        public InputFilterMinMax(String min, String max, String maxCount) {
            this.min = Double.parseDouble(min);
            this.max = Double.parseDouble(max);
            this.maxCount = Integer.parseInt(maxCount);
        }

        @Override

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            try {

                if (source.equals("-") & dest.toString().equals("")) {
                    return "-";
                } else if (!dest.toString().contains("-") & source.toString().equals("-")) {
                    String strInut = source.toString() + dest.toString();
                    if (strInut.replace(".", "").replace("-", "").length() <= maxCount) {
                        Double input = Double.parseDouble(strInut);
                        if (isInRange(min, max, input))
                            return null;
                    } else {
                        return "";
                    }

                } else {
                    String strInut = dest.toString() + source.toString();
                    if (strInut.replace(".", "").replace("-", "").length() <= maxCount) {
                        Double input = Double.parseDouble(strInut);
                        if (isInRange(min, max, input))
                            return null;
                    } else {
                        return "";
                    }

                }

            } catch (NumberFormatException nfe) {

            }
            return "";
        }

        private boolean isInRange(Double a, Double b, Double c) {

            return b > a ? c >= a && c <= b : c >= b && c <= a;

        }

    }

}