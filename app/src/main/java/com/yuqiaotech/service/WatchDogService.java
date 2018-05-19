package com.yuqiaotech.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.yuqiaotech.constants.WatchConstants;
import com.yuqiaotech.preferences.AppInfoPreferences;
import com.yuqiaotech.utils.AppUtil;

public class WatchDogService extends Service {
    private final int WHAT = 1;
    private Handler childHandler;
    private HandlerThread handlerThread;
    private volatile String mCurrenOtherPackageName;
    private volatile  int i=0;

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
        String watchpackageName =  new AppInfoPreferences().getPackageName(WatchConstants.DEFALUT_PACKAGENAME);
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
            // foreground app 20s no change ,maybe crash
            // 在子线程中
            if (!AppUtil.isAppRunning(getApplication(), (String) msg.obj)) {
                // background
                String currOtherPackageName = AppUtil.getForegroundAppPageName(getApplication());
                if (!TextUtils.isEmpty(currOtherPackageName) && currOtherPackageName.equals(mCurrenOtherPackageName)) {
                    // same 20s open app
//                    if (++i >= 2) {
                        AppUtil.openApp(getApplication(), (String) msg.obj);
                        mCurrenOtherPackageName = null;
//                    }
                } else {
                    // different
                    mCurrenOtherPackageName = currOtherPackageName;
//                    i = 0;
                }

            }
            Message msg2 = Message.obtain(msg);
            childHandler.sendMessageDelayed(msg2, 10 * 1000); //10s refresh again
            return false;


        }
    }
}
