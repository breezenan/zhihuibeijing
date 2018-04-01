package com.nan.zhbj.pager;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by nan on 2018/3/3.
 */

public class HomePager extends BasePager {
    public HomePager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        //页面标题
        mTVTitle.setText("智慧北京");
        //页面内容
        TextView textView = new TextView(mActivity);
        textView.setText("首页");
        textView.setTextSize(25);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        mFrameContent.addView(textView);
        //主页不显示menu
        mIBMenu.setVisibility(View.GONE);
    }
}
