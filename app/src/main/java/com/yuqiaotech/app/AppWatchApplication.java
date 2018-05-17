package com.yuqiaotech.app;

import android.app.Application;
import android.content.Context;

/**
 * (用一句话描述类的主要功能)
 * Created by yun on 2018/5/17.
 */
public class AppWatchApplication extends Application {
    private static Context comtext;

    @Override
    public void onCreate() {
        super.onCreate();
        comtext = this;
    }

    public static AppWatchApplication getApplication() {
        return (AppWatchApplication) comtext;
    }

}
