package com.nan.zhbj.pager.menu;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nan.zhbj.R;
import com.nan.zhbj.domain.Photos;
import com.nan.zhbj.global.GlobalConstants;
import com.nan.zhbj.utils.CacheUtil;
import com.nan.zhbj.utils.MyBitmapUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 新闻中心的侧边栏"组图"栏目对应页
 * Created by nan on 2018/3/7.
 */

public class PhotosMenuDetailPager extends BaseMenuDetailPager {
    public static final String TAG = "feifei";
    //列表或网格显示
    private ListView mLvPhotos;
    private GridView mGvPhotos;
    //组图bean
    private Photos mPhotos;
    //新闻列表，每个新闻中包含图片，标题
    private ArrayList<Photos.PhotosNews> mPhotosNewsList;
    private ImageView mIvPhotosStyle;
    //组图url
    private String mUrl = GlobalConstants.PHOTOS_URL;
    private MyBitmapUtil mBitmapUtil;
    private boolean listStyle = true;

    public PhotosMenuDetailPager(Activity activity, ImageView photosStyle) {
        super(activity);
        mIvPhotosStyle = photosStyle;
    }

    @Override
    protected View initView() {
        View view = View.inflate(mActivity, R.layout.left_menu_pager_photos, null);
        mLvPhotos = view.findViewById(R.id.lv_photos);
        mGvPhotos = view.findViewById(R.id.gv_photos);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        Log.i(TAG, "initData: ");
        String json = CacheUtil.getCache(mActivity, mUrl);
        if (!TextUtils.isEmpty(json)) {
            processData(json);
        }
        getDataFromServer();
        mIvPhotosStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listStyle) {
                    mIvPhotosStyle.setImageResource(R.drawable.icon_pic_list_type);
                    mGvPhotos.setVisibility(View.VISIBLE);
                    mLvPhotos.setVisibility(View.GONE);
                    listStyle = false;
                } else {
                    mIvPhotosStyle.setImageResource(R.drawable.icon_pic_grid_type);
                    mGvPhotos.setVisibility(View.GONE);
                    mLvPhotos.setVisibility(View.VISIBLE);
                    listStyle = true;
                }
            }
        });
        mBitmapUtil = new MyBitmapUtil();
    }

    /**
     * 根据url读取网络数据
     */
    private void getDataFromServer() {
        RequestParams params = new RequestParams(mUrl);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result);
                CacheUtil.setCache(mActivity, mUrl, result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Toast.makeText(mActivity, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 根据从服务器请求的json数据转化为对应的数据bean
     *
     * @param json
     */
    private void processData(String json) {
        Gson gson = new Gson();
        mPhotos = gson.fromJson(json, Photos.class);
        mPhotosNewsList = mPhotos.data.news;
        PhotosAdapter adapter = new PhotosAdapter();
        mLvPhotos.setAdapter(adapter);
        mGvPhotos.setAdapter(adapter);
    }

    /**
     * listview,gridview公用的adapter
     */
    class PhotosAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mPhotosNewsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mPhotosNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(mActivity, R.layout.list_item_photos_news, null);
                holder.imageView = convertView.findViewById(R.id.iv_image);
                holder.textView = convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Photos.PhotosNews news = mPhotosNewsList.get(position);
            //1. xutil网络下载图片，内部已实现三级缓存
            //x.image().bind(holder.imageView, news.listimage);
            //2. 自定义三级缓存
            mBitmapUtil.display(holder.imageView, news.listimage.replace(GlobalConstants.OLD_IP, GlobalConstants.NEW_IP));
            holder.textView.setText(news.title);
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
