package com.practice.wuwei.inskeeper.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.practice.wuwei.inskeeper.R;

/**
 * @author by wuwei
 * @date on 2018/2/9 下午4:44
 */

public class NotificationUtil {

    // 通知渠道的id
    private static String ID = "my_channel_01";
    private static NotificationManager notificationManager;
    private static int notify_id = 10;

    public static void init(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = NotificationUtil.getNotificationChannel();
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.cancelAll();
    }

    public static void destory() {
        notificationManager = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static NotificationChannel getNotificationChannel() {
        NotificationChannel mChannel = new NotificationChannel(ID, "通知测试", NotificationManager.IMPORTANCE_HIGH);
// 配置通知渠道的属性
        mChannel.setDescription("通知不可描述");
// 设置通知出现时的闪灯（如果 android 设备支持的话）
        mChannel.enableLights(false);
        //mChannel.setLightColor(Color.RED);
// 设置通知出现时的震动（如果 android 设备支持的话）
        mChannel.enableVibration(false);
        //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});


        return mChannel;
    }

    /**
     * 显示一个普通的通知
     *
     * @param context 上下文
     */
    public static Notification getNotification(Context context, String title, String content) {
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new NotificationCompat.Builder(context, ID)
                /**设置通知左边的大图标**/
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                /**设置通知右边的小图标**/
                .setSmallIcon(R.mipmap.ic_launcher)
                /**通知首次出现在通知栏，带上升动画效果的**/
                .setTicker("通知来了")
                /**设置通知的标题**/
                .setContentTitle(title)
                /**设置通知的内容**/
                .setContentText(content)
                /**通知产生的时间，会在通知信息里显示**/
                .setWhen(System.currentTimeMillis())
                /**设置该通知优先级**/
                .setPriority(NotificationCompat.PRIORITY_MAX)
                /**设置这个标志当用户单击面板就可以让通知将自动取消**/
                .setAutoCancel(true)
                /**设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)**/
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                /**向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：**/
                //.setDefaults(Notification.DEFAULT_SOUND)
//                .setVibrate(new long[]{0})
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setTimeoutAfter(15000l)
//                .setFullScreenIntent(contentIntent,true)
//                .setContentIntent(contentIntent)
//                .setContentIntent(PendingIntent.getActivity(context, 1, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        return notification;

    }

    public static int sendDownloadNotification(Context context, String url) {
        notify_id++;
        if (notify_id > 100) {
            notify_id = 10;
        }
        String title = "开始下载";
        String content = url + "资源开始下载，请稍候";
        Notification notification = getNotification(context, title, content);
        notificationManager.notify(notify_id, notification);
//        Toast.makeText(context,"notify_id = " + String.valueOf(notify_id),Toast.LENGTH_SHORT).show();
        Log.e("开始下载", "notify_id = " + String.valueOf(notify_id));
        return notify_id;
    }

    public static void sendFinishNotification(Context context, int id, String fileName) {
        String title = "下载完成";
        String content = fileName + "下载完成，请在资源库查看";
        Notification notification = getNotification(context, title, content);
        notificationManager.cancel(id);
        notificationManager.notify(id, notification);
//        Toast.makeText(context,fileName + "下载完成，请在资源库查看",Toast.LENGTH_SHORT).show();
        Log.e("下载完成", "文件名：" + fileName);
    }

    public static void sendErrorNotification(Context context, int id, String content) {
        String title = "下载失败";
        Notification notification = getNotification(context, title, content);
        notificationManager.cancel(id);
        notificationManager.notify(id, notification);
//        Toast.makeText(context,"下载失败，错误类型：" + content,Toast.LENGTH_SHORT).show();
        Log.e("下载失败", "错误类型：" + content);
    }
}
