package com.nan.zhbj.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nan.zhbj.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by nan on 2018/4/1.
 */

public class PushReceiver extends BroadcastReceiver {
    public static final String TAG = "wangyannan";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Logger.d(TAG, "[PushReceiver] onReceive - " + intent.getAction());

        // 注册成功
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Logger.d(TAG, "[PushReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

            // 收到自定义消息，不会再通知栏显示，由开发者完成
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Logger.d(TAG, "[PushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

            // 收到通知
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Logger.d(TAG, "[PushReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Logger.d(TAG, "[PushReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            String title = intent.getStringExtra(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = intent.getStringExtra(JPushInterface.EXTRA_ALERT);
            Logger.d(TAG, "[PushReceiver] 接收到推送下来的通知 title = " + title + ", content = " + content);

            // 用户点开通知
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Logger.d(TAG, "[PushReceiver] 用户点击打开了通知");
            String extra = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
            try {
                JSONObject jsonObject = new JSONObject(extra);
                String url = jsonObject.getString("url");
                Logger.d(TAG, "[PushReceiver] 用户点击 url = " + url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //打开自定义的Activity
//            Intent i = new Intent(context, TestActivity.class);
//            i.putExtras(bundle);
//            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            context.startActivity(i);
        } else {
            Logger.d(TAG, "unhandled intent - " + intent.getAction());
        }
    }

    private void processCustomMessage(Context context, Bundle bundle) {
    }
}
