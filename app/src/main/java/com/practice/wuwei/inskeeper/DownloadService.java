package com.practice.wuwei.inskeeper;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.practice.wuwei.inskeeper.utils.DownloadUtil;
import com.practice.wuwei.inskeeper.utils.NotificationUtil;

import static android.content.ContentValues.TAG;

/**
 * @author by wuwei
 * @date on 2018/2/8 下午8:51
 */

public class DownloadService extends Service {
    private final IBinder mBinder = new LocalBinder();

    private ClipboardManager clipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            if (clipboardManager.hasPrimaryClip() && clipboardManager.getPrimaryClip().getItemCount() > 0) {
                String url = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                if (url != null && url.startsWith("https://www.instagram.com/")) {
                    Log.e(TAG, "copied text: " + url);
                    //Toast.makeText(DownloadService.this,"截获Instagram分享地址："+url,Toast.LENGTH_SHORT).show();
                    DownloadUtil.downloadXMLFormInstagram(DownloadService.this, url);
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

//                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //setForeground();
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        //setForeground();
//        return mBinder;
    }

    private void setForeground() {
        NotificationUtil.init(this);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        registerClipEvents();
        Notification notification = NotificationUtil.getNotification(this, "Instagram下载服务", "服务成功运行，在Instagram中点击\"复制链接\"即可自动下载", Color.CYAN);
        notification.contentIntent = PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        /**发起通知**/
        startForeground(1, notification);
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        unregisterClipEvents();
//        stopForeground(true);
//        NotificationUtil.destroy();
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setForeground();
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        unregisterClipEvents();
        stopForeground(true);
        NotificationUtil.destroy();
        super.onDestroy();
    }

    private void registerClipEvents() {
        clipboardManager.addPrimaryClipChangedListener(clipChangedListener);
    }

    private void unregisterClipEvents() {
        clipboardManager.removePrimaryClipChangedListener(clipChangedListener);
    }

    public class LocalBinder extends Binder {
        DownloadService getService() {
// 返回本service的实例到客户端，于是客户端可以调用本service的公开方法
            return DownloadService.this;
        }
    }
}
