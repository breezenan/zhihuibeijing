package com.nan.zhbj.pager.menu;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nan.zhbj.MainActivity;
import com.nan.zhbj.R;
import com.nan.zhbj.domain.NewsMenu;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻中心的侧边栏"新闻"对应页
 * Created by nan on 2018/3/7.
 */

public class NewsMenuDetailPager extends BaseMenuDetailPager {
    private ViewPager mViewPager;
    private TabPageIndicator mPageIndicator;
    private ArrayList<NewsMenu.NewsTabData> mChildren;
    private ImageButton mIBNextTab;
    private int mCurrentItem;
    /**
     * Tab页的标题
     */

    List<BaseMenuDetailPager> mTabs = new ArrayList<BaseMenuDetailPager>();

    public NewsMenuDetailPager(Activity activity, ArrayList<NewsMenu.NewsTabData> children) {
        super(activity);
        mChildren = children;
    }

    @Override
    protected View initView() {
        /**
         * 新闻页使用viewpager实现
         */
        View view = View.inflate(mActivity, R.layout.left_menu_pager_news, null);
        mViewPager = view.findViewById(R.id.vp_news_detail);
        mPageIndicator = view.findViewById(R.id.vp_indicator);
        mIBNextTab = view.findViewById(R.id.ib_next_tab);
        mIBNextTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换写一个tab页
                mCurrentItem = mViewPager.getCurrentItem();
                mViewPager.setCurrentItem(++mCurrentItem);

            }
        });
        return view;
    }

    @Override
    public void initData() {
        mTabs.clear();
        for (int i = 0; i < mChildren.size(); i++) {
            mTabs.add(new NewsTabPager(mActivity, mChildren.get(i)));
            mTabs.get(i).initData();
        }
        mViewPager.setAdapter(new NewsDetailPagerAdapter());
        mPageIndicator.setViewPager(mViewPager);//必须在viewpager设置adaptor之后调用
        //pageIndicator会缓存当前页下标，故每次初始化需重置当前下标
        mPageIndicator.setCurrentItem(0);
        //如果有PageIndicator则设置OnPageChangeListener监听器必须设给indicator
        mPageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {//第0页允许slidingmenu的滑动
                    setSlidingMenuEnabled(true);
                } else {
                    //响应tab之间的滑动,不允许slidingmenu滑动
                    setSlidingMenuEnabled(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setSlidingMenuEnabled(boolean enabled) {
        MainActivity activity = (MainActivity) mActivity;
        if (enabled) {
            activity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            activity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    class NewsDetailPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mTabs.size();
        }

        //返回pager的tab头部文字内容
        @Override
        public CharSequence getPageTitle(int position) {
            return mChildren.get(position).title;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mTabs.get(position).mRootView;
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
