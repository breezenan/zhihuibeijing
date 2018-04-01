package com.nan.zhbj.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by nan on 2018/3/27.
 */

public class LocalCacheUtil {
    public static final String TAG = "LocalCacheUtil";
    private static final String LOCAL_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/zhbj/cache/";

    public void setLocalCache(String url,Bitmap bitmap) {
        if (bitmap == null || url == null) {
            return;
        }
        File dir = new File(LOCAL_CACHE_PATH);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }
        try {
            String fileName = MD5Encoder.encode(url);
            File file = new File(LOCAL_CACHE_PATH, fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getLocalCache(String url) {
        try {
            File file = new File(LOCAL_CACHE_PATH, MD5Encoder.encode(url));
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
