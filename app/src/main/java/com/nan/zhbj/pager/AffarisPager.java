package com.nan.zhbj.pager;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by nan on 2018/3/3.
 */

public class AffarisPager extends BasePager {
    public AffarisPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        mTVTitle.setText("人口管理");

        TextView textView = new TextView(mActivity);
        textView.setText("政务");
        textView.setTextSize(25);
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        mFrameContent.addView(textView);
    }
}
