package com.nicky.framework.tableview;


import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nicky.framework.R;
import com.nicky.framework.widget.XEditText;
import com.nicky.framework.widget.XLinearLayout;
import com.rl.commons.bean.EdwinItem;
import com.rl.commons.filter.EditLenInputFilter;


/**
 *
 * 自定义table item
 * @author NickyHuang
 *
 */
public class CustomTableItem extends XLinearLayout {

	protected ImageView iconLeft; //item 图标
	protected ImageView iconRight; //item右侧图标
	protected TextView tvName; //item名称
	protected TextView tvDesc; //item描述
	protected TextView tvValue; //item值
	protected ImageView ivValue; //item值
	protected XEditText etItem; //item Edit
//	protected CheckBox switchItem; //item switch
	protected SwitchCompat switchItem; //item switch
	protected View bgEnable; //不可选的背景
	protected LinearLayout lyItem;

	private Context mCurContext;

	public static final int ITEM_TYPE_COMMON = 0;//单行
	public static final int ITEM_TYPE_EDIT = 1; //带编辑框
	public static final int ITEM_TYPE_DESC = 2; //2行
	public static final int ITEM_TYPE_SWITCH = 3; //带开关


	public interface OnXCheckedChangeListener{
		void onXCheckedChanged(boolean isChecked);
	}
	private OnXCheckedChangeListener mOnXCheckedChangeListener;


	public CustomTableItem(Context context, int viewType) {
		super(context,viewType);
	}

	@Override
	protected void init(Context context, LayoutInflater layoutInflater,
						AttributeSet attrs) {
		super.init(context, layoutInflater, attrs);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.getLayoutParams();
		if( layoutParams==null )
			layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
		else
		{
			layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
			layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		}
		this.setLayoutParams(layoutParams);
		switch (mViewType) {
		case ITEM_TYPE_COMMON:
			layoutInflater.inflate(R.layout.table_item_common, this);
			break;
		case ITEM_TYPE_EDIT:
			layoutInflater.inflate(R.layout.table_item_edit, this);
			break;
		case ITEM_TYPE_DESC:
			layoutInflater.inflate(R.layout.table_item_desc, this);
			break;
		case ITEM_TYPE_SWITCH:
			layoutInflater.inflate(R.layout.table_item_switch, this);
			break;
		default:
			break;
		}
		mCurContext = context;
        findViewById();
		if( switchItem!=null && ITEM_TYPE_SWITCH==mViewType )
		{
			switchItem.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (iconLeft != null)
						iconLeft.setSelected(isChecked);
					if(mOnXCheckedChangeListener!=null)
						mOnXCheckedChangeListener.onXCheckedChanged(isChecked);
				}
			});
		}
	}

    private void findViewById()
    {
        iconLeft = (ImageView)findViewById(R.id.icon_item);
        iconRight = (ImageView)findViewById(R.id.iocn_right);
        tvName = (TextView)findViewById(R.id.tv_name);
        tvDesc = (TextView)findViewById(R.id.tv_desc);
        tvValue = (TextView)findViewById(R.id.tv_value);
		ivValue = (ImageView)findViewById(R.id.iv_value);
        etItem = (XEditText) findViewById(R.id.et_item);
        switchItem = (SwitchCompat) findViewById(R.id.switch_item);
//        switchItem = (CheckBox) findViewById(R.id.switch_item);
		bgEnable = findViewById(R.id.bg_enable);

		lyItem = (LinearLayout) findViewById(R.id.layout_item);
    }

	public void setBgEnabled( boolean enabled ){
		if( bgEnable!=null ){
			bgEnable.setVisibility(enabled?View.GONE:View.VISIBLE);
		}
		if(etItem!=null)
			etItem.setFocusableInTouchMode(enabled);
		if(switchItem!=null)
			switchItem.setEnabled(enabled);
	}

	public void setMinHeight( int height ){
		if( lyItem!=null ){
			lyItem.setMinimumHeight(height);
		}
	}

	public void setIconVisibility(int visibility)
	{
		if(iconLeft!=null)
			iconLeft.setVisibility(visibility);
	}

	public void setIconRightVisibility(int visibility) {
		if(iconRight!=null)
			iconRight.setVisibility(visibility);
	}


	public void setIconImageResource(int resId){
		if(iconLeft!=null)
			iconLeft.setImageResource(resId);
	}
	public void setIconRightImageResource(int resId){
		if(iconRight!=null)
			iconRight.setImageResource(resId);
	}


	public void setName(String name) {
		if(tvName!=null)
			tvName.setText(name);
	}

	/**
	 * 必填项(前面有红色*)
	 */
	public void setName(String name , boolean notEmpty) {
		if(tvName==null)
			return;
		if(notEmpty)
		{
			tvName.setText(getFormatStr(mCurContext,name));
		}else
			tvName.setText(name);
	}


	public void setDesc(String desc) {
		if(tvDesc!=null)
		{
			if(!TextUtils.isEmpty(desc))
			{
				tvDesc.setText(desc);
				tvDesc.setVisibility(View.VISIBLE);
			}else{
				tvDesc.setVisibility(View.GONE);
			}
		}
	}

	public void setValue(EdwinItem val) {
		if(tvValue!=null)
		{
			tvValue.setVisibility(View.VISIBLE);
			tvValue.setText(val.getName());
			tvValue.setTag(val);
		}
		if( ivValue!=null ){
			ivValue.setVisibility(View.GONE);
		}
	}

	public void setValue(@NonNull String val) {
		setValue( new EdwinItem( val,val.hashCode() ) );
	}


	public EdwinItem getValue() {
		return tvValue==null?null: (EdwinItem) tvValue.getTag();
	}

	public String getValueStr() {
		return tvValue==null?"":tvValue.getText().toString().trim();
	}


	public void setValueColor(@ColorInt int color) {
		if(tvValue!=null)
		{
			tvValue.setTextColor(color);
		}
	}

	/**
	 * unit sp
	 * @param size
	 */
	public void setValueSize( float size ) {
		if(tvValue!=null)
		{
			tvValue.setTextSize(size);
		}
	}

	public void setImgValue(int resId){
		if(tvValue!=null)
		{
			tvValue.setVisibility(View.GONE);
		}
		if( ivValue!=null ){
			ivValue.setVisibility(View.VISIBLE);
			ivValue.setImageResource(resId);
		}
	}


	public void setEtText(String text){
		if(etItem!=null)
			etItem.setText(text);
	}
	public String getEtText(){
		return etItem==null?"":etItem.getText().toString().trim();
	}
	public void setEtHint(String hint){
		if(etItem!=null)
			etItem.setHint(hint);
	}
	public void setEtMaxLen(int maxLen){
		etItem.setFilters(new InputFilter[]{new EditLenInputFilter(maxLen)});
	}


	public void toggleSwitch(){
		if(switchItem!=null)
		{
			switchItem.toggle();
		}
	}
	public void setChecked(boolean checked)
	{
		if (switchItem != null && switchItem.isChecked() != checked) {
			switchItem.setChecked(checked);
		}
	}
	public boolean isChecked(){
		return switchItem==null?false:switchItem.isChecked();
	}

	public void setOnXCheckedChangeListener(OnXCheckedChangeListener listener){
		mOnXCheckedChangeListener = listener;
	}
	public boolean isXCheckedChangeListenerSet(){
		return mOnXCheckedChangeListener!=null;
	}



	private SpannableStringBuilder getFormatStr(Context context, String origStr)
	{
		if( !TextUtils.isEmpty(origStr) )
		{
			String emptyStr = " ";
			String flagStr = "*";
			String fullStr = origStr+emptyStr+flagStr;
			int flagIndex = origStr.length()+emptyStr.length();//"*"起始位置
			SpannableStringBuilder builder = new SpannableStringBuilder(fullStr);
			ForegroundColorSpan graySpan = new ForegroundColorSpan(context.getResources().getColor(android.R.color.holo_red_light));
			//AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(14,true);
			if(flagIndex!=-1)
				builder.setSpan(graySpan, flagIndex, flagIndex+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			return builder;
		}
		else
			return null;
	}
}
