package com.yuqiaotech.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.yuqiaotech.appwatch.MainActivity;

/**
 * (用一句话描述类的主要功能)
 * Created by yun on 2018/5/17.
 */
public class AppWatchApplication extends Application implements Thread.UncaughtExceptionHandler {
    private static Context comtext;

    @Override
    public void onCreate() {
        super.onCreate();
        comtext = this;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static AppWatchApplication getApplication() {
        return (AppWatchApplication) comtext;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());

    }
}
