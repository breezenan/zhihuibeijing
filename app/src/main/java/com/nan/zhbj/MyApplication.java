package com.nan.zhbj;

import com.mob.MobApplication;

import org.xutils.x;

/**
 * Created by nan on 2018/3/5.
 */

public class MyApplication extends MobApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(false); //输出debug日志，开启会影响性能
    }
}
