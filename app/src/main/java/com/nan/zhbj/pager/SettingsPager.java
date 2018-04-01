package com.nan.zhbj.pager;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by nan on 2018/3/3.
 */

public class SettingsPager extends BasePager {
    public SettingsPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        mTVTitle.setText("设置");

        TextView textView = new TextView(mActivity);
        textView.setText("设置");
        textView.setTextSize(25);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        mFrameContent.addView(textView);

        mIBMenu.setVisibility(View.GONE);
    }
}
