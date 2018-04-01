package com.nan.zhbj;

import com.mob.MobApplication;
import com.nan.zhbj.utils.Logger;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by nan on 2018/3/5.
 */

public class MyApplication extends MobApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化XUtils3
        x.Ext.init(this);
        x.Ext.setDebug(false); //输出debug日志，开启会影响性能

        // 初始化Jpush
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        Logger.d("breeze","myapplication");
    }
}
