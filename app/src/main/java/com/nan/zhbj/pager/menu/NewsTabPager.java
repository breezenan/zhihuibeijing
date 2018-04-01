package com.nan.zhbj.pager.menu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nan.zhbj.NewsDisplayActivity;
import com.nan.zhbj.R;
import com.nan.zhbj.domain.NewsMenu;
import com.nan.zhbj.domain.NewsTabDataDeatil;
import com.nan.zhbj.global.GlobalConstants;
import com.nan.zhbj.utils.CacheUtil;
import com.nan.zhbj.utils.PrefUtil;
import com.nan.zhbj.view.PullToRefreshListView;
import com.viewpagerindicator.CirclePageIndicator;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 新闻中心侧边栏"新闻"页的tab页
 * Created by nan on 2018/3/9.
 */

public class NewsTabPager extends BaseMenuDetailPager {
    public static final String TAG = "NewsTabPager";
    /**
     * 新闻tab页数据
     */
    private NewsTabDataDeatil mNewsTabDataDeatil;
    private NewsMenu.NewsTabData mNewsTabData;
    private ArrayList<NewsTabDataDeatil.TabTopNews> mTabTopNewsList;
    private ArrayList<NewsTabDataDeatil.TabNews> mTabNewsList;
    private ArrayList<NewsTabDataDeatil.TabNews> mMoreDataList;
    private ViewPager mTopNewsPager;
    private CirclePageIndicator mPageIndicator;
    private PullToRefreshListView mNewsListView;
    private NewsListAdapter mNewsListAdapter;
    private TextView mTopTitle;
    private String mTabNewsUrl;
    private String mLoadingMoreUrl;

    //实现轮播图
    private Handler mHandler;

    public NewsTabPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        super(activity);
        mNewsTabData = newsTabData;
        mTabNewsUrl = GlobalConstants.SERVER_URL + mNewsTabData.url;
    }

    /**
     * 不能再initView中使用mNewsTabData,会引起空指针，因为该方法会在父类构造方法中先于mNewsTabData赋值前被调用
     */
    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.tab_news, null);
        mNewsListView = view.findViewById(R.id.lv_list_news);
        //mNewsList的headerview,注意headerview的高度不取决与布局的顶层view，而是它的子view，故需要嵌套空的view容器
        View headerView = View.inflate(mActivity, R.layout.list_header_topnews, null);
        mTopNewsPager = headerView.findViewById(R.id.vp_top_news);
        mTopTitle = headerView.findViewById(R.id.tv_top_title);
        mTopTitle.setVisibility(View.INVISIBLE);
        mPageIndicator = headerView.findViewById(R.id.indicator);
        mNewsListView.addHeaderView(headerView);
        mNewsListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉ListView刷新时请求数据
                getDataFromServer();
            }

            @Override
            public void onLoadingMore() {
                //加载更多时请求更多数据
                getMoreDataFromServer();
            }
        });
        return view;
    }

    private void getMoreDataFromServer() {
        Log.i(TAG, "getMoreDataFromServer: mLoadingMoreUrl=" + mLoadingMoreUrl);
        if (TextUtils.isEmpty(mLoadingMoreUrl)) {
            Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_SHORT).show();
            mNewsListView.onLoadingComplete();
            return;
        }
        RequestParams params = new RequestParams(mLoadingMoreUrl);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = result;
                Log.i(TAG, "onSuccess: 加载更多数据成功");
                processData(json, true);
                mNewsListView.onLoadingComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "onError: " + ex.getMessage());
                mNewsListView.onLoadingComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        String cacheJson = CacheUtil.getCache(mActivity, mTabNewsUrl);
        if (!TextUtils.isEmpty(cacheJson)) {
            processData(cacheJson, false);
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        RequestParams params = new RequestParams(mTabNewsUrl);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result, false);
                CacheUtil.setCache(mActivity, mTabNewsUrl, result);
                mNewsListView.onRefreshComplete(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mNewsListView.onRefreshComplete(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void processData(String json, final boolean moreData) {
        Gson gson = new Gson();
        mNewsTabDataDeatil = gson.fromJson(json, NewsTabDataDeatil.class);

        mLoadingMoreUrl = mNewsTabDataDeatil.data.more;
        if (!TextUtils.isEmpty(mLoadingMoreUrl)) {
            mLoadingMoreUrl = GlobalConstants.SERVER_URL + mLoadingMoreUrl;
        } else {
            mLoadingMoreUrl = null;
        }
        //是否是更多内容中的数据
        if (moreData) {
            //分页加载
            //采用ArrayList保存新数据生成总数据
            mMoreDataList = mNewsTabDataDeatil.data.news;
            mTabNewsList.addAll(mMoreDataList);
            mNewsListAdapter.notifyDataSetChanged();
        } else {
            mTabTopNewsList = mNewsTabDataDeatil.data.topnews;
            mTopTitle.setVisibility(View.VISIBLE);
            mTopTitle.setText(mTabTopNewsList.get(0).title);
            mTopNewsPager.setAdapter(new TopNewsPagerAdapter());
            mPageIndicator.setViewPager(mTopNewsPager);
            //mPageIndicator.setSnap(true); indicator快照方式滑动
            mPageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //更新Topnews的标题
                    mTopTitle.setText(mTabTopNewsList.get(position).title);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            mTabNewsList = mNewsTabDataDeatil.data.news;
            mNewsListAdapter = new NewsListAdapter();
            mNewsListView.setAdapter(mNewsListAdapter);
            mNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    position = position - mNewsListView.getHeaderViewsCount();
                    NewsTabDataDeatil.TabNews news = mTabNewsList.get(position);
                    String readIds = PrefUtil.getString(mActivity, "read_ids", "");
                    //如果当前新闻已读没有缓存则进行缓存
                    if (!readIds.contains(news.id + "")) {
                        readIds = readIds + news.id + ",";
                        Log.i(TAG, "onItemClick: readIds=" + readIds);
                        TextView textView = view.findViewById(R.id.tv_title);
                        textView.setTextColor(Color.GRAY);
                        PrefUtil.putString(mActivity, "read_ids", readIds);
                        // mNewsAdapter.notifyDataSetChanged();//全局刷新, 浪费性能
                    }
                    Intent intent = new Intent(mActivity, NewsDisplayActivity.class);
                    String newUrl = news.url.replace(GlobalConstants.OLD_IP, GlobalConstants.NEW_IP);
                    intent.putExtra("news_url", newUrl);
                    mActivity.startActivity(intent);
                }
            });
            if (mHandler == null) {
                //Top 新闻轮播图
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            int currentItem = mTopNewsPager.getCurrentItem();
                            currentItem++;
                            if (currentItem > mTabTopNewsList.size() - 1) {
                                currentItem = 0;
                            }
                            mTopNewsPager.setCurrentItem(currentItem);
                            mHandler.sendEmptyMessageDelayed(1, 2000);
                        }
                        super.handleMessage(msg);
                    }
                };
                mHandler.sendEmptyMessageDelayed(1, 2000);
                mTopNewsPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                //停止轮播，传入null表示移除所有回调和消息
                                mHandler.removeCallbacksAndMessages(null);
                                break;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL://一个事件被打断时调用(例如，从Viewparger按下后又滑动listview，导致viewpager不会执行up事件，但会执行cancel事件)
                                mHandler.sendEmptyMessageDelayed(1, 2000);
                                break;
                        }
                        return false;
                    }
                });
            }
        }
    }

    class TopNewsPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mTabTopNewsList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //TODO 采用inflate布局形式，找不到子view
//            View view = View.inflate(mActivity, R.layout.tab_news, null);
//            ImageView imageView = view.findViewById(R.id.iv_top_image);
//            imageView.setImageResource(R.drawable.topnews_item_default);
            ImageView view = new ImageView(mActivity);
            //下载图片
            Log.i(TAG, "instantiateItem: ");
            ImageOptions options = new ImageOptions.Builder()
                    .setIgnoreGif(false)
                    //设置图片填充ImageView
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)
                    //未加载好时显示的图片
                    .setLoadingDrawableId(R.drawable.topnews_item_default)
                    //图片下载失败显示的图片
                    .setFailureDrawableId(R.mipmap.ic_launcher_round)
                    .build();
            //如果不关心下载失败成功，则可以使用不带CommonCallback参数的方法
            String newUrl = mTabTopNewsList.get(position).topimage.replace(GlobalConstants.OLD_IP, GlobalConstants.NEW_IP);
            x.image().bind(view, newUrl, options, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable result) {
                    Log.i(TAG, "onSuccess: dowloading top image sucess");
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i(TAG, "onError: dowloading top image failure");
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    class NewsListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTabNewsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTabNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.list_item_tab_news, null);
                holder.ivListImage = convertView.findViewById(R.id.iv_listimage);
                holder.tvTitle = convertView.findViewById(R.id.tv_title);
                holder.tvPubdate = convertView.findViewById(R.id.tv_pubdate);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            NewsTabDataDeatil.TabNews tabNews = mTabNewsList.get(position);//得到当前页list中新闻对象
            String newUrl = tabNews.listimage.replace(GlobalConstants.OLD_IP, GlobalConstants.NEW_IP);
            x.image().bind(holder.ivListImage, newUrl);//xutil3下载网络图片
            holder.tvTitle.setText(tabNews.title);
            holder.tvPubdate.setText(tabNews.pubdate);
            String readIds = PrefUtil.getString(mActivity, "read_ids", "");
            if (readIds.contains(tabNews.id + "")) {
                holder.tvTitle.setTextColor(Color.GRAY);
            } else {
                holder.tvTitle.setTextColor(Color.BLACK);
            }
            return convertView;
        }
    }

    class ViewHolder {
        ImageView ivListImage;
        TextView tvTitle;
        TextView tvPubdate;

    }
}
