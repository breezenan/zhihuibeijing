package com.nan.zhbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by nan on 2018/3/19.
 */

public class TopNewsViewPager extends ViewPager {
    float startX = 0;
    float startY = 0;

    public TopNewsViewPager(Context context) {
        super(context);
    }

    public TopNewsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 1. 默认禁止父控件拦截事件
     * 2. 上下滑动不禁止拦截事件
     * 3. 向左滑动且当前页是最后一个页面不禁止拦截
     * 4. 向右滑动且当前页是第一个页面不禁止拦截
     *
     * 解决bug tab页切换卡顿(由于startX和startY在dispatchTouchEvent中声明原因,move事件会多次触发dispatchTouchEvent，故导致startX/Y初始值失效)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = ev.getX();
                float endY = ev.getY();
                float dx = endX - startX;
                float dy = endY - startY;
                if (Math.abs(dx) > Math.abs(dy)) {//左右滑动
                    if (dx > 0) {//手右滑
                        if (getCurrentItem() == 0) {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    } else {
                        if (getCurrentItem() == getAdapter().getCount() - 1) {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
