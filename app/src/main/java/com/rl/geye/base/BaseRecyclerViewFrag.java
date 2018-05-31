package com.rl.geye.base;


import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.rl.geye.R;

import butterknife.BindView;


/**
 * Created by Nicky on 2017/8/19.
 */
public abstract class BaseRecyclerViewFrag extends BaseMyFrag implements BaseQuickAdapter.RequestLoadMoreListener {

    public final static int PAGE_COUNT = 10;//每次加载条数
    public static final int STATE_NONE = 0;     // 初始状态
    public static final int STATE_REFRESH = 1;  // 下拉刷新
    public static final int STATE_LOADMORE = 2; // 上滑刷新
    public static final int STATE_PRESSNONE = 3;// 更新中
    public static int mState = STATE_NONE;
    @BindView(R.id.rv)
    public RecyclerView rv;
    @BindView(R.id.swipe)
    public SwipeRefreshLayout swipe;
    @BindView(R.id.root_view)
    public View rootView;
    public View emptyView;
    public TextView tvEmptyTips;
    public ImageView ivEmpty;
    private boolean firstShow = true; //页面首次加载
    private AsyncTask<Void, Void, Void> refreshTask;
    private AsyncTask<Void, Void, Void> loadmoreTask;

    private int groupSize = 0;

    private BaseMultiItemQuickAdapter mAdapter;

    /*************** RecyclerView 适配器 **************/
    protected abstract BaseMultiItemQuickAdapter getAdapter();

    /*************** RecyclerView 初始化 设置 LayoutManager、ItemAnimator等 **************/
    protected abstract RecyclerView.LayoutManager getLayoutManager();

    protected RecyclerView.ItemAnimator getItemAnimator() {
        return new DefaultItemAnimator();
    }


    /*************** EmptyView 设置 **************/
    protected void initEmptyView() {
    }


    /*************** 刷新、加在更多 **************/
    protected abstract void executeRefresh();

    protected abstract void executeLoadMore();


    @Override
    protected int getLayoutId() {
        return R.layout.frag_tab_photo;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViewsAndEvents() {
        emptyView = getActivity().getLayoutInflater().inflate(R.layout.comm_message, (ViewGroup) rv.getParent(), false);
        tvEmptyTips = (TextView) emptyView.findViewById(R.id.message_info);
        ivEmpty = (ImageView) emptyView.findViewById(R.id.message_icon);
        initEmptyView();

        swipe.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipe.setProgressBackgroundColorSchemeResource(android.R.color.white);


        mAdapter = getAdapter();
        mAdapter.setEmptyView(emptyView);

        mAdapter.setOnLoadMoreListener(this, rv);
//        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        mAdapter.setPreLoadNumber(3);

        rv.setLayoutManager(getLayoutManager());
        rv.setItemAnimator(getItemAnimator());
        rv.setAdapter(mAdapter);


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mState == STATE_REFRESH) {
                    return;
                }
                if (rv == null)
                    return;
//              rvDev.setSelection(0);
                mState = STATE_REFRESH;
                executeRefresh();
            }
        });

    }

    @Override
    protected void lazyLoad() {
        //默认加载的数据
        if (rootView.getHeight() > 100 && firstShow) {
            firstShow = false;
            prepareRefresh();
            executeRefresh();
        } else {
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    if (firstShow) {
                        firstShow = false;
                        prepareRefresh();
                        executeRefresh();
                    }
                }
            });
        }
    }


    @Override
    public void onLoadMoreRequested() {
        executeLoadMore();
    }


    protected void prepareRefresh() {
        if (swipe != null && !swipe.isRefreshing()) {
            swipe.setRefreshing(true);
        }
    }
}
