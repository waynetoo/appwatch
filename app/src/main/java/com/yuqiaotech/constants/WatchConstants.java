package com.yuqiaotech.constants;

/**
 * (用一句话描述类的主要功能)
 * Created by yun on 2018/5/16.
 */
public class WatchConstants {
    public static final String HANDLERTHREAD_NAME = "HANDLERTHREAD_NAME";
    public static final int SELECTED_APP_REQUESTCODE = 0x1;

    public static final String DEFALUT_PACKAGENAME = "com.wangdie.advertisement";
//    public static final String  DEFALUT_PACKAGENAME="com.yuqiaotech.uncatch";

    // 最上层activity 获取此段活动时间内的状态， 因为只能获取至今13秒之类的 activity 切换状态，如果没有切换，则会切到广告app
    public static final long GET_TOP_ACTIVITY_STATUS_DELTA_TIME = 13 * 1000;

    //  service的刷新时间
    public static final long WATCH_DOG_SERVICE_FRESH_TIME = 8 * 1000;
}
