package com.rl.geye.ui.frag;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;

import com.nicky.framework.widget.XViewPager;
import com.rl.geye.R;
import com.rl.geye.adapter.MyFragPageAdapter;
import com.rl.geye.base.BaseMyFrag;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/9/13.
 * 首页 -- 记录(图片、视频)
 */
public class HomeRecordFrag extends BaseMyFrag {

    @BindView(R.id.vp_picvideo)
    XViewPager mViewPager;

    @BindView(R.id.root_view)
    View rootView;


    private boolean firstShow = true; //页面首次加载

    private MyFragPageAdapter mAdapter;

    private TabPhotoFrag fragPhoto;
    private TabVideoFrag fragVideo;

    private OnRecordListener mListener;

    /*
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
        //do something
        if (context instanceof OnRecordListener) {
            mListener = (OnRecordListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecordListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_home_record;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViewsAndEvents() {

        mViewPager.setEnableScroll(true);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mListener.onRecordTabChanged(position == 0);
//                selectTab(position);
//                ivDelete.setVisibility(mAdapter.deleteMenuEnable(position)? View.VISIBLE:View.GONE);
                //TODO
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void lazyLoad() {
        //默认加载的数据
        if (rootView.getHeight() > 100 && firstShow) {
            firstShow = false;
            initData();
        } else {
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    if (firstShow) {
                        firstShow = false;
                        initData();
                    }
                }
            });
        }
    }

    private void initData() {

        if (fragPhoto == null)
            fragPhoto = new TabPhotoFrag();
        if (fragVideo == null)
            fragVideo = new TabVideoFrag();

        Fragment[] frags = new Fragment[]{fragPhoto, fragVideo};
        CharSequence[] titles = new CharSequence[]{getString(R.string.str_photo), getString(R.string.str_video)};

        mAdapter = new MyFragPageAdapter(getChildFragmentManager(), frags, titles);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);
        mListener.onRecordTabChanged(true);

    }

    public void selectTabPhoto() {
        if (mViewPager != null && mAdapter != null)
            mViewPager.setCurrentItem(0);
    }

    public void selectTabVideo() {
        if (mViewPager != null && mAdapter != null)
            mViewPager.setCurrentItem(1);
    }

    public void deletePhoto() {
        if (fragPhoto != null)
            fragPhoto.gotoDelete();
    }

    public void deleteVideo() {
        if (fragVideo != null)
            fragVideo.gotoDelete();
    }

    /**
     * 设备列表个数发生改变 或者 设备刷新状态变化
     */
    public interface OnRecordListener {

        void onRecordTabChanged(boolean isPhotoSelected);

    }


}
