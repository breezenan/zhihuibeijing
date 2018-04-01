package com.nan.zhbj;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nan.zhbj.fragment.ContentFragment;
import com.nan.zhbj.fragment.LeftMenuFragment;

import java.security.Permission;


/**
 * Created by nan on 2018/3/3.
 */

public class MainActivity extends Activity {
    public static final String TAG_CONTENT_FRAGMENT = "tag_content_fragment";
    public static final String TAG_LEFT_MENU_FRAGMENT = "tag_left_menu_fragment";
    private SlidingMenu mSlidingMenu;
    private FragmentTransaction mFragmentTransaction;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        /*
        SlidingMenu使用，用于左侧抽屉内容显示
         */
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setMode(SlidingMenu.LEFT);//设置方向
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//设置全屏滑动，也可设置为边界滑动或者禁止滑动
        //menu.setShadowWidthRes(R.dimen.shadow_width);
        //menu.setShadowDrawable(R.drawable.shadow);
        mSlidingMenu.setBehindOffset(500);//设置划出slidingmenu的剩余宽度
        mSlidingMenu.setFadeDegree(1f);//设置划出渐变度
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mSlidingMenu.setMenu(R.layout.menu);
        initFragment();
        //TODO:动态权限封装
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    private void initFragment() {
        mFragmentManager = getFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.fl_content, new ContentFragment(), TAG_CONTENT_FRAGMENT);
        mFragmentTransaction.replace(R.id.fl_left_menu, new LeftMenuFragment(), TAG_LEFT_MENU_FRAGMENT);
        mFragmentTransaction.commit();
    }

    public SlidingMenu getSlidingMenu() {
        return mSlidingMenu;
    }

    public Fragment getFragmentByTag(String tag) {
        return mFragmentManager.findFragmentByTag(tag);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "需要读写存储设备", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
