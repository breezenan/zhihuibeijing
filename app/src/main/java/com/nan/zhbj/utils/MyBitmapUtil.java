package com.nan.zhbj.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

/**
 * 实现图片的三级缓存
 * Created by nan on 2018/3/27.
 */

public class MyBitmapUtil {
    public static final String TAG = "breeze";
    private final LocalCacheUtil localCacheUtil;
    private final MemoryCacheUtil memoryCacheUtil;
    private final NetCacheUtil netCacheUtil;

    public MyBitmapUtil() {
        localCacheUtil = new LocalCacheUtil();
        memoryCacheUtil = new MemoryCacheUtil();
        netCacheUtil = new NetCacheUtil(localCacheUtil, memoryCacheUtil);
    }

    public void display(ImageView imageView, String url) {
        // 1. 内存中读取缓存
        Bitmap bitmap;
        bitmap = memoryCacheUtil.getMemoryCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "display: 读取内存缓存");
            return;
        }
        // 2. 从本地读取缓存
        bitmap = localCacheUtil.getLocalCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            Log.i(TAG, "display: 读取本地缓存");
            //写入内存缓存
            memoryCacheUtil.setMemoryCache(url,bitmap);
            return;
        }

        // 3. 从网络下载图片
        netCacheUtil.getBitmapFromInternet(imageView, url);
    }
}
