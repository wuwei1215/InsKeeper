package com.practice.wuwei.inskeeper;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * @author by wuwei
 * @date on 2018/3/31 下午8:20
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
    }
}
