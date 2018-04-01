package com.nan.zhbj.pager;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nan.zhbj.MainActivity;
import com.nan.zhbj.R;

/**
 * Created by nan on 2018/3/3.
 */

public class BasePager {
    Activity mActivity;
    public View mRootView;
    //通过公共的framelayout来替换自己独有的布局
    FrameLayout mFrameContent;
    //公共标题栏，内容有实现类决定
    ImageButton mIBMenu;
    TextView mTVTitle;
    ImageView mIvPhotosStyle;

    public BasePager(Activity activity) {
        mActivity = activity;
        mRootView = initView();
        //initData();  //创建时初始化数据过于耗费资源流量，故在选中页面时再进行数据的初始化
    }

    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_content_base, null);
        mFrameContent = view.findViewById(R.id.fl_content);
        mIBMenu = view.findViewById(R.id.ib_menu);
        mIBMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) mActivity;
                mainActivity.getSlidingMenu().toggle();//slidingmenu打开关闭
            }
        });
        mTVTitle = view.findViewById(R.id.tv_title);
        mIvPhotosStyle = view.findViewById(R.id.iv_photos_style);
        return view;
    }

    public void initData() {
    }
}
