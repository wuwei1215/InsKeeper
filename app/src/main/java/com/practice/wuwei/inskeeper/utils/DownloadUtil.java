package com.practice.wuwei.inskeeper.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.practice.wuwei.inskeeper.InsSource;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author by wuwei
 * @date on 2018/2/9 下午4:44
 */

public class DownloadUtil {

    private static ThreadFactory threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r);
        }
    };
    private static ExecutorService getResourceThreadPoll =
            new ThreadPoolExecutor(3, 10,
                    1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(128), threadFactory);

    private static ExecutorService downloadFileThreadPool = Executors.newSingleThreadExecutor(threadFactory);


    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    public static void downloadXMLFormInstagram(final Context context, final String url) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int notify_id = NotificationUtil.sendDownloadNotification(context, url);
                try {

                    Request request = new Request.Builder().url(url).build();
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String xml = response.body().string();
                        Log.e("get", xml);
                        InsSource insSource = DataParserUtil.praseSourceXML(xml);
                        if (insSource.is_video) {
                            getFileByUrl(context, notify_id, true, insSource.video_url);
                        } else {
                            if (insSource.urls == null) {
                                getFileByUrl(context, notify_id, false, insSource.display_url);
                            } else {
                                getFileByUrl(context, notify_id, false, insSource.urls);
                            }

                        }
                    } else {
                        //Toast.makeText(DownloadService.this, "抱歉，好像遇到了点问题，稍后再试一下", Toast.LENGTH_SHORT).show();
                        NotificationUtil.sendErrorNotification(context, notify_id, "数据获取失败(Network Error)");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    NotificationUtil.sendErrorNotification(context, notify_id, "数据获取失败(IOException)");
                }
            }
        };
        getResourceThreadPoll.execute(runnable);
    }

    private static void getVideoByUrl(String url) {

    }


    private static void getFileByUrl(final Context context, final int notify_id, final boolean is_video, final String... urls) {
        for (int i = 0; i < urls.length; i++) {
            final int finalI = i;
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Request request = new Request.Builder().get()
                            .url(urls[finalI])
                            .build();
                    try {
                        Response response = okHttpClient.newCall(request).execute();
                        if (response.isSuccessful()) {
                            String[] s = urls[finalI].split("/");
                            String fileName = s[s.length - 1];
                            FileUtil.saveFile(context, notify_id * 10 + finalI, response.body().bytes(), fileName, is_video);
                        } else {
                            NotificationUtil.sendErrorNotification(context, notify_id * 10 + finalI, "资源获取失败(Network Error)");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        NotificationUtil.sendErrorNotification(context, notify_id * 10 + finalI, "资源获取失败(IOException)");
                    }


                }
            };
            downloadFileThreadPool.execute(runnable);
        }
    }
}
