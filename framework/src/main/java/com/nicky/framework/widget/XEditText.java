package com.nicky.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import com.nicky.framework.R;
import com.rl.commons.filter.EditEmojiFilter;


/**
 * 
 * @ClassName: CWEditText
 * @Description: 自定义编辑框(1、可包含清空按钮; 2、可设置是否过滤表情; 3、可包含复制、粘贴操作菜单)
 * @author NickyHuang
 * @date 2015-11-2
 *
 */
public class XEditText extends AppCompatEditText implements MenuItem.OnMenuItemClickListener{

	private final static boolean DEFAULT_NO_EMOJI = false;//默认不屏蔽表情
	private final static boolean DEFAULT_WITH_OP_MENU = false;//默认不需要操作菜单

	private final static int RIGHT_TYPE_NONE = 0;
	private final static int RIGHT_TYPE_CLEAN_CONTENT = 1; //清空
	private final static int RIGHT_TYPE_SHOW_PWD = 2; //显示密码

	private int mRightIconType = RIGHT_TYPE_NONE;
	private boolean mNoEmoji = DEFAULT_NO_EMOJI;//是否屏蔽输入表情
	private boolean mWithOpMenu = DEFAULT_WITH_OP_MENU;//是否包含操作菜单(复制、粘贴、清空等)
	
	private boolean isInited = false;//是否已经初始化完毕
	
//	private Context mActContent;

	private int rightIconExtraSize = 50;//右侧图标额外点击区域
	private int rightIconMaxSize = 80;//右侧图标最大尺寸


	private int errorUnderlineColor; // 错误颜色
	private boolean isErrorStateEnabled = false;
	private boolean mHasReconstructedEditTextBackground;

 
    public XEditText(Context context) {
    	this(context, null);
    } 
 
    public XEditText(Context context, AttributeSet attrs) {
    	this(context, attrs,android.R.attr.editTextStyle);
    } 
    
    public XEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }
    
    
    private void init(AttributeSet attrs) {
		errorUnderlineColor = R.color.text_red;

		rightIconExtraSize = getResources().getDimensionPixelSize(R.dimen.common_size_10);
		rightIconMaxSize = getResources().getDimensionPixelSize(R.dimen.common_size_30);


//    	mActContent = context;
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.XEditText);
		mRightIconType = typedArray.getInt(R.styleable.XEditText_rightIconType,RIGHT_TYPE_NONE);

		mNoEmoji = typedArray.getBoolean(R.styleable.XEditText_noEmoji,
				DEFAULT_NO_EMOJI);
		mWithOpMenu = typedArray.getBoolean(R.styleable.XEditText_withOpMenu,
				DEFAULT_WITH_OP_MENU);
		typedArray.recycle();
		isInited = true;

		if( mRightIconType!= RIGHT_TYPE_NONE ){
			initRightIcon();
		}
		if( mNoEmoji )
			initNoEmoji();
		if( mWithOpMenu )
			initOpMenu();
		
    }
    
    private void initRightIcon()
    {
    	//获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
    	mRightDrawable = getCompoundDrawables()[2];
        if (mRightDrawable == null) {
//        	throw new NullPointerException("You can add drawableRight attribute in XML");
			int iconId = R.drawable.ic_clear_selector;
			switch (mRightIconType){
				case RIGHT_TYPE_CLEAN_CONTENT:
					iconId = R.drawable.ic_clear_selector;
					break;
				case RIGHT_TYPE_SHOW_PWD:
					iconId = R.drawable.ic_show_pwd_selector;
					break;
			}
        	mRightDrawable = getResources().getDrawable(iconId);
        } 

		int width = mRightDrawable.getIntrinsicWidth();
		int height = mRightDrawable.getIntrinsicHeight();
		if (width>rightIconMaxSize || height>rightIconMaxSize ){
			width = rightIconMaxSize;
			height = rightIconMaxSize;
		}

        mRightDrawable.setBounds(0, 0, width, height );
        //默认设置隐藏图标
        setRightIconVisible(false);
        //设置焦点改变的监听
        setOnFocusChangeListener(mOnRightFocusChangeListener);

        //设置输入框里面内容发生改变的监听
        addTextChangedListener(mRightIconTextWatcher);
    }


    private void initNoEmoji()
    {
    	if(mEditEmojiFilter==null)
			mEditEmojiFilter = new EditEmojiFilter();
    	this.setFilters(getFilters());
    	
    }
    
    private void initOpMenu()
    {
//		setInputType(InputType.TYPE_NULL);
		setKeyListener(null);
    }
 
    /*------------------------------------------------------------------------------*
     * 									右侧按钮部分								    *
     *------------------------------------------------------------------------------*/
    
    /**
	 * 右侧按钮的引用
	 */
    private Drawable mRightDrawable;
    /**
     * 控件是否有焦点
     */
    private boolean hasFocus;

	private boolean isPwdShow = false;
    
    /**
     * 当EditText焦点发生变化的时候，判断里面字符串长度设置右侧图标的显示与隐藏
     */
    private OnFocusChangeListener mOnRightFocusChangeListener = new OnFocusChangeListener() {
    	
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			XEditText.this.hasFocus = hasFocus;
	        if (hasFocus) { 
	            setRightIconVisible(getText().length() > 0);
	        } else { 
	            setRightIconVisible(false);
	        } 
		}
	};
	
	 /**
     * 设置右侧图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    private void setRightIconVisible(boolean visible) {
        Drawable right = visible ? mRightDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], 
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
    } 
    
    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
	private TextWatcher mRightIconTextWatcher = new TextWatcher(){

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {
			if(hasFocus){
        		setRightIconVisible(s.length() > 0);
        	}
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
		
	};
    
 
    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP &&
				mRightIconType != RIGHT_TYPE_NONE && isEnabled()) {

			if (getCompoundDrawables()[2] != null) {
//				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
//						&& (event.getX() < ((getWidth() - getPaddingRight())));
				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight() - rightIconExtraSize );
				if (touchable) {

					switch (mRightIconType) {
						case RIGHT_TYPE_CLEAN_CONTENT:
							setShakeAnimation();
							this.setText("");
							break;
						case RIGHT_TYPE_SHOW_PWD:
							if(!isPwdShow){
								//show password
								setTransformationMethod(HideReturnsTransformationMethod.getInstance());
							}else{
								//hide password
								setTransformationMethod(PasswordTransformationMethod.getInstance());
							}
							isPwdShow = !isPwdShow;
							setSelected(isPwdShow);
							break;
					}
				}
			}
		}
		return super.onTouchEvent(event);
	}
 
    
    /**
     * 设置晃动动画
     */
    private void setShakeAnimation(){
    	this.setAnimation(shakeAnimation(3));
    }
    
    
    /**
     * 晃动动画
     * @param counts 1秒钟晃动多少下
     * @return
     */
    private static Animation shakeAnimation(int counts){
    	Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
    	translateAnimation.setInterpolator(new CycleInterpolator(counts));
    	translateAnimation.setDuration(500);
    	return translateAnimation;
    }
 
    
    /*------------------------------------------------------------------------------*
     * 									过滤表情部分								    *
     *------------------------------------------------------------------------------*/
    private EditEmojiFilter mEditEmojiFilter;
    @Override
    public void setFilters(InputFilter[] filters) {
    	if(mEditEmojiFilter==null)
			mEditEmojiFilter = new EditEmojiFilter();
    	if( !isInited )
    	{
    		super.setFilters(filters);
    		return;
    	}
    	if( mNoEmoji )
    	{

	    	if( filters==null|| filters.length==0 )
	    	{
	    		super.setFilters(new InputFilter[]{ mEditEmojiFilter });
	    	}
	    	else
	    	{
	    		InputFilter[] newFilters = new InputFilter[ filters.length+1 ];
	    		newFilters[0] = mEditEmojiFilter;
	    		boolean isContainEmojiFilter = false;
		    	for (int i=0;i<filters.length;i++ ) {
		    		InputFilter inputFilter = filters[i];
		    		if( inputFilter instanceof EditEmojiFilter)
		    			isContainEmojiFilter = true;
		    		newFilters[i+1] = inputFilter;
				}
		    	super.setFilters(isContainEmojiFilter?filters:newFilters);
	    	}
    	}else
    		super.setFilters(filters);
    }
    
 
    /*------------------------------------------------------------------------------*
     * 									操作菜单部分								    *
     *------------------------------------------------------------------------------*/
    private static final int ID_SELECTION_MODE = android.R.id.selectTextMode;
	// Selection context mode
	private static final int ID_SELECT_ALL = android.R.id.selectAll;
	private static final int ID_CUT = android.R.id.cut;
	private static final int ID_COPY = android.R.id.copy;//复制
	private static final int ID_PASTE = android.R.id.paste;//粘贴
	private static final int ID_CLEAR = android.R.id.empty;//清空
	
	
	private EditTextOpListener mEditTextOpListener;

	public void setEditTextOpListener(EditTextOpListener listener)
	{
		mEditTextOpListener = listener;
	}

	public interface EditTextOpListener
	{
//		 void onTextCopy();
		 void onTextPaste();
		 void onTextClear();
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		// 代码效果，有弹出框选择 粘贴，复制，剪切，类似qq效果.....
		if(mWithOpMenu)
		{
			menu.add(0, ID_PASTE, 0, "粘贴").setOnMenuItemClickListener(this);
			menu.add(0, ID_CLEAR, 1, "清空").setOnMenuItemClickListener(this);
	//		menu.add(0, ID_CUT, 2, "剪切").setOnMenuItemClickListener(this);
	//		menu.add(0, ID_COPY, 3, "复制").setOnMenuItemClickListener(this);
	//		menu.add(0, ID_SELECT_ALL, 4, "全选").setOnMenuItemClickListener(this);
			super.onCreateContextMenu(menu);
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return onTextContextMenuItem(item.getItemId());
	}

	@Override
	public boolean onTextContextMenuItem(int id) {

		if(!mWithOpMenu)
			return super.onTextContextMenuItem(id);

		// Do your thing:
		boolean consumed = false;
		// React:
		switch (id) {
		case android.R.id.cut:
			consumed = super.onTextContextMenuItem(id);
			break;
		case android.R.id.paste://先清空在粘贴
			setText("");
			consumed = super.onTextContextMenuItem(id);
			if(mEditTextOpListener!=null)
				mEditTextOpListener.onTextPaste();
			break;
		case android.R.id.copy:
			consumed = super.onTextContextMenuItem(id);
		case android.R.id.empty:
			setText("");
			if(mEditTextOpListener!=null)
				mEditTextOpListener.onTextClear();
		default:
			consumed = super.onTextContextMenuItem(id);
			break;
		}
		return consumed;

	}


	/*------------------------------------------------------------------------------*
     * 									错误颜色部分								    *
     *------------------------------------------------------------------------------*/

	public void setErrorColor() {
		ensureBackgroundDrawableStateWorkaround();
		getBackground().setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(
				ContextCompat.getColor(getContext(), errorUnderlineColor), PorterDuff.Mode.SRC_IN));
	}

	private void ensureBackgroundDrawableStateWorkaround() {
		final Drawable bg = getBackground();
		if (bg == null) {
			return;
		}
		if (!mHasReconstructedEditTextBackground) {
			// This is gross. There is an issue in the platform which affects container Drawables
			// where the first drawable retrieved from resources will propogate any changes
			// (like color filter) to all instances from the cache. We'll try to workaround it...
			final Drawable newBg = bg.getConstantState().newDrawable();
			//if (bg instanceof DrawableContainer) {
			//  // If we have a Drawable container, we can try and set it's constant state via
			//  // reflection from the new Drawable
			//  mHasReconstructedEditTextBackground =
			//      DrawableUtils.setContainerConstantState(
			//          (DrawableContainer) bg, newBg.getConstantState());
			//}
			if (!mHasReconstructedEditTextBackground) {
				// If we reach here then we just need to set a brand new instance of the Drawable
				// as the background. This has the unfortunate side-effect of wiping out any
				// user set padding, but I'd hope that use of custom padding on an EditText
				// is limited.
				setBackgroundDrawable(newBg);
				mHasReconstructedEditTextBackground = true;
			}
		}
	}

	public boolean isErrorStateEnabled() {
		return isErrorStateEnabled;
	}

	public void setErrorState(boolean isErrorStateEnabled) {
		this.isErrorStateEnabled = isErrorStateEnabled;
		if (isErrorStateEnabled) {
			setErrorColor();
			invalidate();
		} else {
			getBackground().mutate().clearColorFilter();
			invalidate();
		}
	}


}
