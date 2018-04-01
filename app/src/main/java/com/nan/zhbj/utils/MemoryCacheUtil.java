package com.nan.zhbj.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by nan on 2018/3/28.
 */

public class MemoryCacheUtil {
    public static final String TAG = "breeze MemoryCacheUtil";
    /**
     * 1. 强引用java虚拟机宁愿抛出OOM，也不会进行回收
     */
    //private HashMap<String, Bitmap> map = new HashMap<>();
    /**
     * 2. 使用软引用使对象即使有引用的情况下也一定概率可以被回收 ,如果是弱引用或是虚引用，其回收的概率更大
     */
    //private HashMap<String, SoftReference<Bitmap>> map = new HashMap<String, SoftReference<Bitmap>>();
    /**
     * Android2.3后 java垃圾回收期更倾向于回收非强引用， 故使用google提供的缓存api LruCache(least recently used)
     */
    private LruCache<String, Bitmap> mBitmapCache;

    public MemoryCacheUtil() {
        long maxSize = Runtime.getRuntime().maxMemory();
        Log.i(TAG, "MemoryCacheUtil: maxSize=" + maxSize);
        // 一般设置大小为单个应用分配内存的1/8
        mBitmapCache = new LruCache<String, Bitmap>((int) (maxSize / 8));
    }

    /**
     * 写入内存
     */
    public void setMemoryCache(String url, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        //map.put(url, new SoftReference<Bitmap>(bitmap));
        mBitmapCache.put(url, bitmap);
    }

    /**
     * 从内存读取
     */
    public Bitmap getMemoryCache(String url) {
//        SoftReference<Bitmap> soft = map.get(url);
//        if (soft != null) {
//            return soft.get();
//        }
        return mBitmapCache.get(url);
    }
}
