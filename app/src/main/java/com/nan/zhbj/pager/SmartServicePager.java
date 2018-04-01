package com.nan.zhbj.pager;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by nan on 2018/3/3.
 */

public class SmartServicePager extends BasePager {
    public SmartServicePager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        mTVTitle.setText("生活");

        TextView textView = new TextView(mActivity);
        textView.setText("智慧服务");
        textView.setTextSize(25);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        mFrameContent.addView(textView);
    }
}
