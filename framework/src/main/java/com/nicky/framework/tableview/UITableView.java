package com.nicky.framework.tableview;



import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nicky.framework.R;
import com.rl.commons.utils.ClickUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class UITableView extends LinearLayout {

	private int mIndexController = 0;
	private LayoutInflater mInflater;
	private LinearLayout mMainContainer;
	private LinearLayout mListContainer;
	private List<IListItem> mItemList;
	private ClickListener mClickListener;
	private TableClickListener mTableClickListener;


	public UITableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mItemList = new ArrayList<>();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMainContainer = (LinearLayout)  mInflater.inflate(R.layout.list_container, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mMainContainer, params);
		mListContainer = (LinearLayout) mMainContainer.findViewById(R.id.buttonsContainer);
	}

	/**
	 *
	 * @param title
	 */
	public void addBasicItem(String title) {
		mItemList.add(new BasicItem(title));
	}

	/**
	 *
	 * @param title
	 * @param summary
	 */
	public void addBasicItem(String title, String summary) {
		mItemList.add(new BasicItem(title, summary));
	}

	/**
	 *
	 * @param title
	 * @param summary
	 * @param color
	 */
	public void addBasicItem(String title, String summary, int color) {
		mItemList.add(new BasicItem(title, summary, color));
	}

	/**
	 *
	 * @param drawable
	 * @param title
	 * @param summary
	 */
	public void addBasicItem(int drawable, String title, String summary) {
		mItemList.add(new BasicItem(drawable, title, summary));
	}



	public void addBasicItem(int drawableLeft, String title, String value,int drawableRight, String remider) {
		mItemList.add(new BasicItem(drawableLeft, title, value,drawableRight,remider));
	}



	/**
	 *
	 * @param drawable
	 * @param title
	 * @param summary
	 */
	public void addBasicItem(int drawable, String title, String summary, int color) {
		mItemList.add(new BasicItem(drawable, title, summary, color));
	}

	/**
	 *
	 * @param item
	 */
	public void addBasicItem(BasicItem item) {
		mItemList.add(item);
	}

	/**
	 *
	 * @param itemView
	 */
	public void addViewItem(ViewItem itemView) {
		mItemList.add(itemView);
	}

	private void addDivider() {
//		View divider = new View(getContext());
//		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) getContext().getResources().getDimension(R.dimen.table_padding_left),
//				(int) getContext().getResources().getDimension(R.dimen.table_divider_height));
//		dividerLayout.addView(divider);
//
//		divider.setLayoutParams(lp);
//		divider.setBackgroundResource(R.color.base_start_color_default);
//		mListContainer.addView(divider);

		RelativeLayout dividerLayout  = new RelativeLayout(getContext());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( LayoutParams.MATCH_PARENT ,
				(int) getContext().getResources().getDimension(R.dimen.table_divider_height));
		dividerLayout.setLayoutParams(lp);

		View leftPadding = new View(getContext());
		RelativeLayout.LayoutParams leftLp = new RelativeLayout.LayoutParams( (int) getContext().getResources().getDimension(R.dimen.table_padding_left)
				,LayoutParams.MATCH_PARENT);
		leftLp.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
		leftPadding.setBackgroundResource(R.color.base_start_color_default);
		leftPadding.setLayoutParams(leftLp);

		View rightPadding = new View(getContext());
		RelativeLayout.LayoutParams rightLp = new RelativeLayout.LayoutParams( (int) getContext().getResources().getDimension(R.dimen.table_padding_right)
				,LayoutParams.MATCH_PARENT);
		rightLp.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
		rightPadding.setBackgroundResource(R.color.base_start_color_default);
		rightPadding.setLayoutParams(rightLp);

		dividerLayout.addView( leftPadding );
		dividerLayout.addView( rightPadding );

		mListContainer.addView( dividerLayout );

	}

	public void commit() {
		mIndexController = 0;

		if(mItemList.size() > 1) {
			//when the list has more than one item
			for(IListItem obj : mItemList) {
				View tempItemView;
				if(mIndexController == 0) {
					tempItemView = mInflater.inflate(R.layout.list_item_top, null);
				}
				else if(mIndexController == mItemList.size()-1) {
					tempItemView = mInflater.inflate(R.layout.list_item_bottom, null);
				}
				else {
					tempItemView = mInflater.inflate(R.layout.list_item_middle, null);
				}
				setupItem(tempItemView, obj, mIndexController);
				tempItemView.setClickable(obj.isClickable());
				mListContainer.addView(tempItemView);
				if(mIndexController < mItemList.size() - 1)
					addDivider();
				mIndexController++;
			}
		}
		else if(mItemList.size() == 1) {
			//when the list has only one item
			View tempItemView = mInflater.inflate(R.layout.list_item_single, null);
			IListItem obj = mItemList.get(0);
			setupItem(tempItemView, obj, mIndexController);
			tempItemView.setClickable(obj.isClickable());
			mListContainer.addView(tempItemView);
		}
	}

	private void setupItem(View view, IListItem item, int index) {
		if(item instanceof BasicItem) {
			BasicItem tempItem = (BasicItem) item;
			setupBasicItem(view, tempItem, mIndexController);
		}
		else if(item instanceof ViewItem) {
			ViewItem tempItem = (ViewItem) item;
			setupViewItem(view, tempItem, mIndexController);
		}
	}

	/**
	 *
	 * @param view
	 * @param item
	 * @param index
	 */
	private void setupBasicItem(View view, BasicItem item, int index) {
		if(item.getDrawable() > -1) {
			((ImageView) view.findViewById(R.id.image)).setBackgroundResource(item.getDrawable());
		}
		if(item.getmDrawRightable() > -1) {
			((ImageView) view.findViewById(R.id.chevron)).setImageResource(item.getmDrawRightable());
		}
		if(item.getSubtitle() != null) {
			((TextView) view.findViewById(R.id.subtitle)).setText(item.getSubtitle());
		}
		else {
			((TextView) view.findViewById(R.id.subtitle)).setVisibility(View.GONE);
		}
		((TextView) view.findViewById(R.id.title)).setText(item.getTitle());
		if(item.getColor() > -1) {
			((TextView) view.findViewById(R.id.title)).setTextColor(item.getColor());
		}
		view.setTag(index);
		if(item.isClickable()) {
			view.setOnClickListener( new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					if(mClickListener != null)
					{
						if (ClickUtil.isFastClick(getContext(),view))
							return;
						mClickListener.onClick((Integer) view.getTag());
					}
				}

			});
		}
		else {
			((ImageView) view.findViewById(R.id.chevron)).setVisibility(View.GONE);
		}

	}

	/**
	 *
	 * @param view
	 * @param itemView
	 * @param index
	 */
	private void setupViewItem(View view, final ViewItem itemView, int index) {
		if(itemView.getView() != null) {
			LinearLayout itemContainer = (LinearLayout) view.findViewById(R.id.itemContainer);
			itemContainer.removeAllViews();
			itemContainer.addView(itemView.getView());

			if(itemView.isClickable()) {
				itemContainer.setTag(index);
				itemContainer.setOnClickListener( new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if(mClickListener != null)
						{
//		                    XLog.e("itemView: "+itemView.getView()  );
							if ( ClickUtil.isFastClick(getContext(),view) )
								return;
							mClickListener.onClick((Integer) view.getTag());
						}
						if( mTableClickListener !=null ){
							if ( ClickUtil.isFastClick(getContext(),view) )
								return;
							mTableClickListener.onTableClick( itemView );
						}
					}
				});
			}
		}
	}

	public interface ClickListener {
		void onClick(int index);
	}

	public interface TableClickListener {
		void onTableClick(ViewItem view);
	}



	/**
	 *
	 * @return
	 */
	public int getCount() {
		return mItemList.size();
	}

	/**
	 *
	 */
	public void clear() {
		mItemList.clear();
		mListContainer.removeAllViews();
	}


	public void setClickListener(ClickListener listener) {
		this.mClickListener = listener;
	}

	public void setTableClickListener(TableClickListener listener) {
		this.mTableClickListener = listener;
	}





	public void removeClickListener() {
		this.mClickListener = null;
	}

	public void removeTableClickListener() {
		this.mTableClickListener = null;
	}



	public void setClickable(View view,boolean clickable) {
		for(int i=0;i<mListContainer.getChildCount();i++){
			View child = mListContainer.getChildAt(i);

			LinearLayout itemContainer = (LinearLayout) child.findViewById(R.id.itemContainer);
			if( itemContainer!=null)
			{
				if(itemContainer.indexOfChild(view)!=-1)
				{
					child.setClickable(clickable);
					break;
				}
			}

		}
	}

	public void setEnabled(View view,boolean enabled) {
		for(int i=0;i<mListContainer.getChildCount();i++){
			View child = mListContainer.getChildAt(i);

			LinearLayout itemContainer = (LinearLayout) child.findViewById(R.id.itemContainer);
			if( itemContainer!=null)
			{
				if(itemContainer.indexOfChild(view)!=-1)
				{
					child.setEnabled(enabled);
					break;
				}
			}
		}
	}

	public void setVisibility(View view,int visibility) {
		for(int i=0;i<mListContainer.getChildCount();i++){
			View child = mListContainer.getChildAt(i);

			LinearLayout itemContainer = (LinearLayout) child.findViewById(R.id.itemContainer);
			if( itemContainer!=null)
			{
				if(itemContainer.indexOfChild(view)!=-1)
				{
					child.setVisibility(visibility);
					break;
				}
			}
		}
	}

}
