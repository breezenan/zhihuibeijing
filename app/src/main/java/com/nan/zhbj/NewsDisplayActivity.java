package com.nan.zhbj;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by nan on 2018/3/25.
 */
@ContentView(R.layout.activity_news_display)
public class NewsDisplayActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "breeze";
    @ViewInject(R.id.ib_menu)
    ImageButton mIBMenu;
    @ViewInject(R.id.ib_back)
    ImageButton mIBBack;
    @ViewInject(R.id.tv_title)
    TextView mTVTitle;
    @ViewInject(R.id.ll_control)
    LinearLayout mLLControler;
    @ViewInject(R.id.wv_browser)
    WebView mWebView;
    @ViewInject(R.id.pb_progress)
    ProgressBar mPBProgress;
    @ViewInject(R.id.ib_text_size)
    ImageButton mIBTextSize;
    @ViewInject(R.id.ib_share)
    ImageButton mIBShare;
    private String mNewsUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        init();
    }

    private void init() {
        mIBMenu.setVisibility(View.INVISIBLE);
        mIBBack.setVisibility(View.VISIBLE);
        mTVTitle.setVisibility(View.INVISIBLE);
        mLLControler.setVisibility(View.VISIBLE);
        mIBBack.setOnClickListener(this);
        mIBTextSize.setOnClickListener(this);
        mIBShare.setOnClickListener(this);
        mNewsUrl = getIntent().getStringExtra("news_url");
        Log.i(TAG, "init: mNewsUrl:" + mNewsUrl);
        //加载新闻详情页
        //loadWebPage(mNewsUrl);
        loadWebPage(mNewsUrl);
    }

    private void loadWebPage(String url) {
        //webview加载网页
        mWebView.loadUrl(url);
        //配置网页加载设置项
        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(true);// 显示缩放按钮(wap网页不支持，因为wap页经排版好了，无需缩放)
        settings.setUseWideViewPort(true);//支持双击缩放(wap网页不支持)
        settings.setJavaScriptEnabled(true);//支持javascript
        mWebView.setWebViewClient(new NewsWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.i(TAG, "onProgressChanged: newProgress=" + newProgress);
                mPBProgress.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.i(TAG, "onReceivedTitle: title=" + title);
            }
        });

    }

    class NewsWebViewClient extends WebViewClient {
        //开始加载网页时调用
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i(TAG, "onPageStarted: ");
            mPBProgress.setVisibility(View.VISIBLE);
        }

        //网页加载结束调用
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i(TAG, "onPageFinished: ");
            mPBProgress.setVisibility(View.INVISIBLE);
        }

        //每个链接的跳转都会走到该方法
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.i(TAG, "shouldOverrideUrlLoading: ");
            //会自动决定在当前webview中打开网页还是启动浏览器来打开
            return super.shouldOverrideUrlLoading(view, request);
        }
    }


    /**
     * TODO: 优化字体选择逻辑
     */
    private String[] mTextSizeItems = new String[]{"超大号字体", "大号字体", "正常字体", "小号字体",
            "超小号字体"};
    private int[] mTextSizeZooms = new int[]{130, 115, 100, 85, 70};

    private int mCurrentZoomIndex = 2;
    private int tempIndex = 0;

    private void showTextSizeSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("字体设置")
                .setSingleChoiceItems(mTextSizeItems, mCurrentZoomIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempIndex = which;
                        Log.i(TAG, "SingleChoiceItems onClick: mCurrentZoomIndex=" + mCurrentZoomIndex);
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WebSettings settings = mWebView.getSettings();
                        Log.i(TAG, "PositiveButton onClick: mCurrentZoomIndex=" + mCurrentZoomIndex);
                        //text大小缩放,默认为100
                        mCurrentZoomIndex = tempIndex;
                        settings.setTextZoom(mTextSizeZooms[mCurrentZoomIndex]);
                    }
                }).setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //监听返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //如果webview打开多级网页，则返回键做返回上一个网页的操作，否则按照正常返回键逻辑走
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.ib_text_size:
                showTextSizeSettingDialog();
                break;
            case R.id.ib_share:
                showShare();
                break;
            default:
                break;
        }
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
//关闭sso授权
        oks.disableSSOWhenAuthorize();
// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("标题");
// titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
// text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
// url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
// comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
// site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
// siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
// 启动分享GUI
        oks.show(this);
    }
}
