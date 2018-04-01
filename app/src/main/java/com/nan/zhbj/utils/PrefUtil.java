package com.nan.zhbj.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by nan on 2018/3/3.
 */

public class PrefUtil {
    public static boolean getBoolean(Context ctx, String key, Boolean defValue) {
        SharedPreferences config = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        return config.getBoolean(key, defValue);
    }

    public static void putBoolean(Context ctx, String key, Boolean value) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value).commit();//注意commit
    }

    public static String getString(Context ctx, String key, String defValue) {
        SharedPreferences config = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        return config.getString(key, defValue);
    }

    public static void putString(Context ctx, String key, String value) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
        editor.putString(key, value).commit();//注意commit
    }
}
