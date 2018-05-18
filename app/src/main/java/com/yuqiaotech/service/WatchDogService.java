package com.yuqiaotech.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

import com.yuqiaotech.constants.WatchConstants;
import com.yuqiaotech.preferences.AppInfoPreferences;
import com.yuqiaotech.utils.AppUtil;

public class WatchDogService extends Service {
    private final int WHAT = 1;
    private Handler childHandler;
    private HandlerThread handlerThread;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //创建异步HandlerThread
        handlerThread = new HandlerThread(WatchConstants.HANDLERTHREAD_NAME);
        //必须先开启线程
        handlerThread.start();
        //子线程Handler
        childHandler = new Handler(handlerThread.getLooper(), new ChildCallback());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String watchpackageName =  new AppInfoPreferences().getPackageName();
        Message msg = childHandler.obtainMessage();
        msg.what = WHAT;
        msg.obj = watchpackageName;
        childHandler.sendMessage(msg);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        childHandler.removeMessages(WHAT);
        handlerThread.quit();
    }

    /**
     * 该callback运行于子线程
     */
    class ChildCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            //在子线程中
            if(!AppUtil.isRunning(getApplication(),(String) msg.obj)){
                AppUtil.openApp(getApplication(),(String) msg.obj);
            }
            Message msg2=Message.obtain(msg);
            childHandler.sendMessageDelayed(msg2, 5000);
            return false;
        }
    }
}
