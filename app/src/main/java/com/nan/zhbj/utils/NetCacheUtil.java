package com.nan.zhbj.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.nan.zhbj.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 根据url从网络下载图片
 * Created by nan on 2018/3/27.
 */

public class NetCacheUtil {
    public static final String TAG = "breeze";
    private ImageView imageView;
    private String url;

    private LocalCacheUtil localCacheUtil;
    private MemoryCacheUtil memoryCacheUtil;

    public NetCacheUtil(LocalCacheUtil localCacheUtil, MemoryCacheUtil memoryCacheUtil) {
        this.localCacheUtil = localCacheUtil;
        this.memoryCacheUtil = memoryCacheUtil;
    }

    public void getBitmapFromInternet(ImageView imageView, String url) {
        this.imageView = imageView;
        this.url = url;
        //AsyncTask异步下载bitmap
        new BitmapAsyncTask().execute(url);
    }

    class BitmapAsyncTask extends AsyncTask<String, Integer, Bitmap> {
        /**
         * 运行在主线程，可做下载前初始化工作
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "onPreExecute: 读取网络缓存");
        }

        /**
         * 运行在子线程，用于执行耗时操作
         */
        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bitmap = downloadBitmap(url);
            // 用于标识ImageView已实现唯一性,因为listview为了性能会重用ImageView，有可能导致其他item的图片设置到本item
            imageView.setTag(url);
            return bitmap;
        }

        /**
         * 运行在主线程，用于子线程执行中与主线程交互
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        /**
         * 运行在主线程，用于子线程执行完毕完成最后的界面操作
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // 校验imageview与url是否匹配
            if (!url.equals(imageView.getTag())) {
                Log.i(TAG, "onPostExecute: url与imageview不匹配");
                return;
            }
            // 下载不成功则设置默认图片
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                //写本地缓存
                localCacheUtil.setLocalCache(url, bitmap);
                //写内存缓存
                memoryCacheUtil.setMemoryCache(url, bitmap);
            } else {
                imageView.setImageResource(R.drawable.pic_item_list_default);
            }
        }
    }

    /**
     * 网络下载图片
     */
    private Bitmap downloadBitmap(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);// 连接超时  等待连接服务器5s
            connection.setReadTimeout(5000);// 读取超时  连接上了服务器，但服务器5s没有返回数据
            connection.connect();

            int responseCode = connection.getResponseCode();
            // 连接成功
            if (responseCode == 200) {
                InputStream inputStream = connection.getInputStream();
                // 根据输入流解析bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 注意断开连接
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
