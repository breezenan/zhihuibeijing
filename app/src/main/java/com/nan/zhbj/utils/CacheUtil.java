package com.nan.zhbj.utils;

import android.content.Context;

import com.nan.zhbj.domain.NewsMenu;

/**
 * 缓存网络数据,该类是通过SharedPreference来进行缓存
 * 有需要时可通过数据库或者文件进行缓存  url MD5后作为文件名，json数据作为文件内容
 * Created by nan on 2018/3/5.
 */
public class CacheUtil {
    /**
     * @param url  缓存数据key
     * @param json 缓存的数据
     */
    public static void setCache(Context ctx, String url, String json) {
        PrefUtil.putString(ctx, url, json);
    }

    public static String getCache(Context ctx, String url) {
        return PrefUtil.getString(ctx, url, "");
    }
}
