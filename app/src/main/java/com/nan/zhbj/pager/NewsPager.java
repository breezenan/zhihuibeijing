package com.nan.zhbj.pager;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.nan.zhbj.MainActivity;
import com.nan.zhbj.domain.NewsMenu;
import com.nan.zhbj.fragment.LeftMenuFragment;
import com.nan.zhbj.global.GlobalConstants;
import com.nan.zhbj.pager.menu.BaseMenuDetailPager;
import com.nan.zhbj.pager.menu.InteractMenuDetailPager;
import com.nan.zhbj.pager.menu.NewsMenuDetailPager;
import com.nan.zhbj.pager.menu.PhotosMenuDetailPager;
import com.nan.zhbj.pager.menu.TopicMenuDetailPager;
import com.nan.zhbj.utils.CacheUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nan on 2018/3/3.
 */

public class NewsPager extends BasePager {
    public static final String TAG = "NewsPager";

    public NewsPager(Activity activity) {
        super(activity);
    }

    private List<BaseMenuDetailPager> mMenuDetailPagers = new ArrayList<BaseMenuDetailPager>();
    private int mDetailPosition;

    /**
     * 服务器请求的数据对象封装
     */
    private NewsMenu mNewsMenu;

    @Override
    public void initData() {
        mTVTitle.setText("新闻");

        /*TextView textView = new TextView(mActivity);
        textView.setText("新闻");
        textView.setTextColor(Color.RED);
        textView.setGravity(Gravity.CENTER);
        mFrameContent.addView(textView);*/

        //请求服务器，请求数据  xUtils
        String json = CacheUtil.getCache(mActivity, GlobalConstants.CATEGORY_URL);
        if (!TextUtils.isEmpty(json)) {
            Log.i(TAG, "initData: has cache data, data:" + json);
            processData(json);//将json数据转化为对象
        }
        //从服务器请求新的数据
        getDataFromServer();
    }

    private void showDetailPager() {
        //得到侧边栏对应详情页
        BaseMenuDetailPager detailPager = mMenuDetailPagers.get(mDetailPosition);
        View view = detailPager.mRootView;
        //通过framlayout替换新的menu pager
        mFrameContent.removeAllViews();
        mFrameContent.addView(view);
        //根据menu选择显示对应标题
        mTVTitle.setText(mNewsMenu.data.get(mDetailPosition).title);

        //如果是组图页 标题栏显示浏览图片形式按钮
        if (detailPager instanceof PhotosMenuDetailPager) {
            mIvPhotosStyle.setVisibility(View.VISIBLE);
        } else {
            mIvPhotosStyle.setVisibility(View.GONE);
        }
        detailPager.initData();
    }

    public void setCurrentMenuDetailPager(int position) {
        mDetailPosition = position;
        showDetailPager();
    }

    /**
     * 设置侧边栏菜单分类数据
     */
    private void fillLeftMenuData() {
        MainActivity mainActivity = (MainActivity) mActivity;
        //获取到侧边栏fragment
        LeftMenuFragment leftMenuFragment = (LeftMenuFragment) mainActivity.getFragmentByTag(MainActivity.TAG_LEFT_MENU_FRAGMENT);
        //将解析的数据传入侧边栏中
        leftMenuFragment.setMenuData(mNewsMenu.data);
    }

    /**
     * 请求数据从服务器
     */
    private void getDataFromServer() {
        RequestParams params = new RequestParams(GlobalConstants.CATEGORY_URL);
        Log.i(TAG, "getDataFromServer: url:" + params.getUri());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //result为请求的json数据
                String data = result;
                Log.i(TAG, "onSuccess: data:" + data);
                //Gson解析json
                processData(data);
                //缓存json数据
                CacheUtil.setCache(mActivity, GlobalConstants.CATEGORY_URL, data);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Log.i(TAG, "onError: isOnCallback:" + isOnCallback);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "onCancelled");
            }

            @Override
            public void onFinished() {
                Log.i(TAG, "onFinished");
            }
        });
    }

    /**
     * 将json数据通过google的 Gson框架进行解析为NewsMenu对象
     */
    public void processData(String json) {
        Gson gson = new Gson();
        //请求的json数据转化后的对象
        mNewsMenu = gson.fromJson(json, NewsMenu.class);
        Log.i(TAG, "processData: newsMenu:" + mNewsMenu);
        fillLeftMenuData();
        mDetailPosition = 0;
        mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity, mNewsMenu.data.get(0).children));
        mMenuDetailPagers.add(new TopicMenuDetailPager(mActivity));
        mMenuDetailPagers.add(new PhotosMenuDetailPager(mActivity,mIvPhotosStyle));
        mMenuDetailPagers.add(new InteractMenuDetailPager(mActivity));
        showDetailPager();
    }
}
