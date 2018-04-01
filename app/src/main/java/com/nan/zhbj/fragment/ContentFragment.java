package com.nan.zhbj.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nan.zhbj.MainActivity;
import com.nan.zhbj.R;
import com.nan.zhbj.pager.AffarisPager;
import com.nan.zhbj.pager.BasePager;
import com.nan.zhbj.pager.HomePager;
import com.nan.zhbj.pager.NewsPager;
import com.nan.zhbj.pager.SettingsPager;
import com.nan.zhbj.pager.SmartServicePager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nan on 2018/3/3.
 */
@ContentView(R.layout.fragment_content)
public class ContentFragment extends BaseFragment {
    @ViewInject(R.id.cvp_content)
    private ViewPager mPagerContent;
    @ViewInject(R.id.rg_tabs)
    private RadioGroup mRadioGroup;
    private List<BasePager> mPagers = new ArrayList<>();

    /*@Override
    public View initView() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_content, null);
        mPagerContent = view.findViewById(R.id.cvp_content);
        mRadioGroup = view.findViewById(R.id.rg_tabs);
        return view;
    }*/

    @Override
    public void initData() {
        mPagers.add(new HomePager(mActivity));
        mPagers.add(new NewsPager(mActivity));
        mPagers.add(new SmartServicePager(mActivity));
        mPagers.add(new AffarisPager(mActivity));
        mPagers.add(new SettingsPager(mActivity));

        mPagerContent.setAdapter(new ContentPagerAdapter());
        mPagerContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPagers.get(position).initData();//初始化选中页数据
                //主页和设置页禁用侧边栏
                if (position == 0 || position == 4) {
                    setSlidingMenuEnabled(false);
                } else {
                    //其他页开启侧边栏
                    setSlidingMenuEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        mPagerContent.setCurrentItem(0);
                        break;
                    case R.id.rb_news:
                        mPagerContent.setCurrentItem(1);
                        break;
                    case R.id.rb_smart:
                        mPagerContent.setCurrentItem(2);
                        break;
                    case R.id.rb_affairs:
                        mPagerContent.setCurrentItem(3);
                        break;
                    case R.id.rb_settings:
                        mPagerContent.setCurrentItem(4);
                        break;
                    default:
                        break;
                }
            }
        });
        mPagers.get(0).initData();//初始化第一页数据
        setSlidingMenuEnabled(false);
    }

    public void setSlidingMenuEnabled(boolean enabled) {
        MainActivity activity = (MainActivity) mActivity;
        if (enabled) {
            activity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            activity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    class ContentPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager basePager = mPagers.get(position);
            //由于viewpager默认会加载下一页数据，为了性能和节省流量，初始化数据放在选中当前界面
            //basePager.initData();
            View view = basePager.mRootView;
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public BasePager getContentPager(int position) {
        if (position < 0 || position > mPagers.size()) {
            return null;
        }
        return mPagers.get(position);
    }
}
