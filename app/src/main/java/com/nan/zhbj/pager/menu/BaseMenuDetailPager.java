package com.nan.zhbj.pager.menu;

import android.app.Activity;
import android.view.View;

/**
 * Created by nan on 2018/3/7.
 */

public abstract class BaseMenuDetailPager {
    Activity mActivity;
    public View mRootView;

    public BaseMenuDetailPager(Activity activity) {
        mActivity = activity;
        mRootView = initView();
    }

    protected abstract View initView();

    public void initData() {

    }
}
