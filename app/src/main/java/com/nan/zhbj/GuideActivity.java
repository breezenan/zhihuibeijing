package com.nan.zhbj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nan.zhbj.utils.PrefUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nan on 2018/3/3.
 */

public class GuideActivity extends Activity {
    public static final String TAG = "GuideActivity";
    private ViewPager mPageGuide;
    private int[] mGuideRes = {R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
    private List<ImageView> mResList;

    private LinearLayout mIndicator;
    private ImageView mRedPoint;
    private int mPointGap;

    private Button mStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        mIndicator = findViewById(R.id.ll_indicator);
        mRedPoint = findViewById(R.id.iv_current_indicator);
        mPageGuide = findViewById(R.id.vp_guide);
        mStartBtn = findViewById(R.id.btn_start);
        initData();
        mPageGuide.setAdapter(new GuidePagerAdapter());
        mPageGuide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             *
             * @param position 所在页下标
             * @param positionOffset 当前页到下一页的距离的百分比0-1
             * @param positionOffsetPixels
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                //根据两页之间的百分比来计算红点距原位置的距离
                params.leftMargin = (int) (mPointGap * positionOffset + mPointGap * position);
                mRedPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == mResList.size() - 1) {
                    mStartBtn.setVisibility(View.VISIBLE);
                } else {
                    mStartBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化viewpage中的数据
     */
    private void initData() {
        ImageView imageView;
        ImageView grayPoint;
        mResList = new ArrayList<>();
        for (int i = 0; i < mGuideRes.length; i++) {
            imageView = new ImageView(this);
            imageView.setBackgroundResource(mGuideRes[i]);
            mResList.add(imageView);

            //初始化indicator
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                //leftmargin 10dp
                params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            }
            grayPoint = new ImageView(this);
            grayPoint.setImageResource(R.drawable.page_point_gray);
            grayPoint.setLayoutParams(params);
            mIndicator.addView(grayPoint);
        }
        mRedPoint.setImageResource(R.drawable.page_point_red);
        //得到视图树(此处可以是任意view对象调用),添加Layout过程的监听器,onGlobalLayout的方法会在view的layout过程完成后调用
        //view绘制过程  measure(测量大小)-->layout(确定位置)-->draw(view绘制)
        mRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            /**
             * 系统为了确保layout准确，会多次调用onGlobalLyaout()方法;
             */
            @Override
            public void onGlobalLayout() {
                //移除监听，避免多次调用
                mRedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mPointGap = mIndicator.getChildAt(1).getLeft() - mIndicator.getChildAt(0).getLeft();
            }
        });
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                finish();
                PrefUtil.putBoolean(getApplicationContext(), "first_open", false);
            }
        });
    }

    class GuidePagerAdapter extends PagerAdapter {
        /**
         * 获取viewpager中page的个数
         *
         * @return
         */
        @Override
        public int getCount() {
            return mResList != null ? mResList.size() : 0;
        }

        /**
         * 判断每个page中是不是view
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 初始化每个page的view，并加载到container中
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mResList.get(position);
            container.addView(view);
            return view;
        }

        /**
         * 销毁page，通过container移除即可
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
