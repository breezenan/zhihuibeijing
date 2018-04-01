package com.nan.zhbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nan.zhbj.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nan on 2018/3/20.
 */

public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener {
    public static final String TAG = "PullToRefreshListView";
    /**
     * ListView 下拉刷新的三种状态
     */
    public static final int PULL_TO_REFRESH = 0;
    public static final int RELEASE_TO_REFRESH = 1;
    public static final int REFRESHING = 2;

    private int mCurrentRefreshState = PULL_TO_REFRESH;

    private View mListHeader;
    private View mListFooter;
    private ProgressBar mPBProgress;
    private ImageView mIVArrow;
    private TextView mTVState;
    private TextView mTVTime;
    private RotateAnimation releaseToPullAnim;
    private RotateAnimation pullToReleaseAnim;
    private float startY = -1;
    private int mListFooterHeight;
    private int mListHeaderHeight;
    private boolean mLoadingMore;

    public PullToRefreshListView(Context context) {
        super(context);
        init();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initRefreshHeadView();
        initFooterView();
    }

    private void initFooterView() {
        mListFooter = View.inflate(getContext(), R.layout.list_footer_news_more, null);
        mListFooter.measure(0, 0);
        mListFooterHeight = mListFooter.getMeasuredHeight();
        Log.i(TAG, "initFooterView: mListFooterHeight:" + mListFooterHeight);
        mListFooter.setPadding(0, -mListFooterHeight, 0, 0);
        addFooterView(mListFooter);
        setOnScrollListener(this);
    }

    private void initRefreshAnimation() {
        releaseToPullAnim = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        releaseToPullAnim.setDuration(400);
        releaseToPullAnim.setFillAfter(true);
        pullToReleaseAnim = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        pullToReleaseAnim.setDuration(400);
        pullToReleaseAnim.setFillAfter(true);

    }

    /**
     * 初始化刷新头部界面
     */
    private void initRefreshHeadView() {
        mListHeader = View.inflate(getContext(), R.layout.list_header_news_refresh, null);
        //view的宽高在onmeasure()之后才可以确定，onmeasure在onresume()之后调用,故此处使用需要先进行measure
        mListHeader.measure(0, 0);
        mListHeaderHeight = mListHeader.getMeasuredHeight();
        addHeaderView(mListHeader);
        mListHeader.setPadding(0, -mListHeaderHeight, 0, 0);

        mPBProgress = mListHeader.findViewById(R.id.pb_loading);
        mIVArrow = mListHeader.findViewById(R.id.iv_image);
        mTVState = mListHeader.findViewById(R.id.tv_state);
        mTVTime = mListHeader.findViewById(R.id.tv_refresh_time);
        mTVTime.setText(getCurrentTime());
        initRefreshAnimation();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //如果startY是-1，则重新赋值，防止listview头部的viewpager消费了down事件而导致startY没有初始值
                if (startY == -1) {
                    Log.i(TAG, "onTouchEvent: case ACTION_MOVE startY=" + startY);
                    startY = ev.getY();
                }
                /**
                 * 如果正在刷新，则不响应下拉刷新
                 */
                if (mCurrentRefreshState == REFRESHING) {
                    break;
                }
                float endY = ev.getY();
                int dy = (int) (endY - startY);
                if (dy > 0 && getFirstVisiblePosition() == 0) {
                    int padding = dy - mListHeaderHeight;
                    //非正在刷新的状态下向下滑动都需要实时更新header 的paddingTop
                    mListHeader.setPadding(0, padding, 0, 0);
                    if (padding > 0 && mCurrentRefreshState != RELEASE_TO_REFRESH) {
                        mCurrentRefreshState = RELEASE_TO_REFRESH;
                        refreshState();
                    } else if (padding < 0 && mCurrentRefreshState != PULL_TO_REFRESH) {
                        mCurrentRefreshState = PULL_TO_REFRESH;
                        refreshState();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                startY = -1; //重置startY为无效值
                //如果状态是释放刷新，则要切换到正在刷新状态
                if (mCurrentRefreshState == RELEASE_TO_REFRESH) {
                    mCurrentRefreshState = REFRESHING;
                    refreshState();
                    mListHeader.setPadding(0, 0, 0, 0);
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onRefresh();
                    }
                    //如果是下拉刷新状态，手指松开后，header此时应该隐藏
                } else if (mCurrentRefreshState == PULL_TO_REFRESH) {
                    mListHeader.setPadding(0, -mListHeaderHeight, 0, 0);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void refreshState() {
        mIVArrow.clearAnimation();//清除动画才可以设置透明度
        switch (mCurrentRefreshState) {
            case PULL_TO_REFRESH:
                updateToPullToRefresh();
                break;
            case RELEASE_TO_REFRESH:
                updateToRelaseToRefresh();
                break;
            case REFRESHING:
                updateToRefreshing();
                break;
            default:
                break;
        }
    }


    private void updateToPullToRefresh() {
        mIVArrow.setAnimation(releaseToPullAnim);
        releaseToPullAnim.start();
        mTVState.setText("下拉刷新");
        mIVArrow.setVisibility(VISIBLE);
        mPBProgress.setVisibility(INVISIBLE);
    }

    private void updateToRelaseToRefresh() {
        mIVArrow.setAnimation(pullToReleaseAnim);
        pullToReleaseAnim.start();
        mTVState.setText("释放刷新");
        mIVArrow.setVisibility(VISIBLE);
        mPBProgress.setVisibility(INVISIBLE);
    }

    private void updateToRefreshing() {
        mTVState.setText("正在刷新...");
        mIVArrow.setVisibility(INVISIBLE);
        mPBProgress.setVisibility(VISIBLE);
    }


    private String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        return time;
    }

    public void onRefreshComplete(boolean success) {
        if (success) {
            mTVTime.setText(getCurrentTime());
        }
        resetState();
    }

    //每次数据加载完成都需隐藏footer并置加载更多标志位false
    public void onLoadingComplete() {
        mListFooter.setPadding(0, -mListFooterHeight, 0, 0);
        mLoadingMore = false;
    }

    /**
     * 重置下拉刷新中控件的状态
     */
    private void resetState() {
        mCurrentRefreshState = PULL_TO_REFRESH;
        mIVArrow.setVisibility(VISIBLE);
        mPBProgress.setVisibility(INVISIBLE);
        mTVState.setText("下拉刷新");
        mListHeader.setPadding(0, -mListHeaderHeight, 0, 0);

    }

    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        mOnRefreshListener = refreshListener;
    }

    public interface OnRefreshListener {
        //下拉刷新
        void onRefresh();

        //加载更多
        void onLoadingMore();
    }

    /**
     * TODO:加载逻辑优化
     * 滑动状态发生变化时调用
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        /**
         *scrollState有三种状态，分别是SCROLL_STATE_IDLE、SCROLL_STATE_TOUCH_SCROLL、SCROLL_STATE_FLING
         *SCROLL_STATE_IDLE是当屏幕停止滚动时
         *SCROLL_STATE_TOUCH_SCROLL是当用户在以触屏方式滚动屏幕并且手指仍然还在屏幕上时（The user is scrolling using touch, and their finger is still on the screen）
         *SCROLL_STATE_FLING是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时（The user had previously been scrolling using touch and had performed a fling）
         */
        if (scrollState == SCROLL_STATE_IDLE) {
            //当最后一个item可见时，此时显示footer
            if (getLastVisiblePosition() == getCount() - 1 && !mLoadingMore) {
                mListFooter.setPadding(0, 0, 0, 0);
                //TODO: ListView footer出现时会跳一下
                setSelection(getCount() - 1);//使当前listview的最后一个item选中，则footer就可以直接显示出来
                Log.i(TAG, "onScrollStateChanged: loading more");
                mOnRefreshListener.onLoadingMore();
                mLoadingMore = true;
            }
        }
    }

    /**
     * 滑动过程中调用
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        /**
         * firstVisibleItem 表示在当前屏幕显示的第一个listItem在整个listView里面的位置（下标从0开始）
         * visibleItemCount表示在现时屏幕可以见到的ListItem(部分显示的ListItem也算)总数
         * totalItemCount表示ListView的ListItem总数
         * listView.getLastVisiblePosition()表示在现时屏幕最后一个ListItem
         * (最后ListItem显示出来一点都算)在整个ListView的位置（下标从0开始）
         */
    }
}
