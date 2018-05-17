package com.yuqiaotech.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 系统操作方法
 */
public class AppUtil {
    /**
     * 打开app
     *
     * @param context
     * @param packageName
     */
    public static void openApp(Context context, String packageName) {
        if (null == context) return;
        if (TextUtils.isEmpty(packageName)) return;

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        try {
            if (intent != null) {
                context.startActivity(intent);
                return;
            }
        } catch (Exception e) {
        }
    }

    /**
     * 启动APP
     *
     * @param context
     * @throws NameNotFoundException
     * @throws Exception
     */
    public static void runApp(Context context) throws NameNotFoundException, Exception {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo appinfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);

        ActivityInfo[] activitys = appinfo.activities;
        if (activitys.length <= 0) {
            return;
        }

        ActivityInfo firstactivity = activitys[0];
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(context.getPackageName(), firstactivity.name);
        context.startActivity(intent);
    }

    /**
     * 激活Activity
     *
     * @param context
     * @param cls     被激活的Activity
     */
    public static void openActivity(Context context, Class<Activity> cls) {
        if (null == context || null == cls) return;

        Intent i = new Intent(context, cls);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(i);
    }

    /**
     * 卸载程序
     *
     * @param context
     * @param packageName 包名
     */
    @Deprecated
    public static void uninstallApk2(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri packageURI = Uri.parse("package:" + packageName);
        intent.setData(packageURI);
        context.startActivity(intent);
    }

    /**
     * 打开并安装文件
     *
     * @param context
     * @param file    apk文件路径
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 应用是否在运行
     *
     * @param ctx
     * @return
     */
    public static boolean isRunning(Context ctx) {
        if (null == ctx) {
            return false;
        }

        String packageName = ctx.getPackageName();
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        if (TextUtils.isEmpty(packageName) || null == am) {
            return false;
        }

        boolean isAppRunning = false;
        List<RunningTaskInfo> list = am.getRunningTasks(50);
        if (null == list || list.isEmpty()) {
            return false;
        }

        for (RunningTaskInfo info : list) {
            if (null == info) continue;

            if ((info.topActivity != null && packageName.equals(info.topActivity.getPackageName()))
                    || (info.baseActivity != null && packageName.equals(info.baseActivity.getPackageName()))) {
                isAppRunning = true;
                break;
            }
        }

        return isAppRunning;
    }

    /**
     * 应用是否在运行
     *
     * @param ctx
     * @return
     */
    public static boolean isRunning(Context ctx, String packageName) {
        if (null == ctx) {
            return false;
        }

        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        if (TextUtils.isEmpty(packageName) || null == am) {
            return false;
        }

        boolean isAppRunning = false;
        List<RunningTaskInfo> list = am.getRunningTasks(50);
        if (null == list || list.isEmpty()) {
            return false;
        }

        for (RunningTaskInfo info : list) {
            if (null == info) continue;

            if ((info.topActivity != null && packageName.equals(info.topActivity.getPackageName()))
                    || (info.baseActivity != null && packageName.equals(info.baseActivity.getPackageName()))) {
                isAppRunning = true;
                break;
            }
        }

        return isAppRunning;
    }

    /**
     * 判断服务是否运行
     *
     * @param ctx
     * @param className 判断的服务名字 "com.xxx.xx..XXXService"
     * @return true 运行中
     */
    public static boolean isServiceRunning(Context ctx, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
        Iterator<RunningServiceInfo> l = servicesList.iterator();
        while (l.hasNext()) {
            RunningServiceInfo si = (RunningServiceInfo) l.next();
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }

        return isRunning;
    }

    /**
     * 停止服务
     *
     * @param ctx
     * @param className 服务名字 "com.xxx.xx..XXXService"
     * @return true, if successful
     */
    public static boolean stopRunningService(Context ctx, String className) {
        Intent intent_service = null;
        boolean ret = false;
        try {
            intent_service = new Intent(ctx, Class.forName(className));
        } catch (Exception e) {
        }
        if (intent_service != null) {
            ret = ctx.stopService(intent_service);
        }
        return ret;
    }


    /**
     * Android判断某个apk是否存在：
     *
     * @param context
     * @param packageName
     * @return
     */

    public static boolean isPkgInstalled(Context context, String packageName) {

        if (packageName == null || "".equals(packageName))
            return false;
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static ResolveInfo findAppByPackageName(Context context, String mPackageName) {
        ResolveInfo newAppInfo = null;
        // 用于存放临时应用程序
        List<ResolveInfo> mTempAllApps;

        PackageManager TempPackageManager = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(mPackageName);
        mTempAllApps = TempPackageManager.queryIntentActivities(mainIntent, 0);
        newAppInfo = mTempAllApps.get(0);

        return newAppInfo;
    }

    // 获得所有启动Activity的信息，类似于Launch界面
    public static List<ResolveInfo> queryAppInfo(Context context) {
        PackageManager pm = context.getPackageManager(); // 获得PackageManager对象
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        for (ResolveInfo reInfo : resolveInfos) {
            String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
            String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
            String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
            Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
            // 为应用程序的启动Activity 准备Intent
            Intent launchIntent = new Intent();
            launchIntent.setComponent(new ComponentName(pkgName,
                    activityName));
            // 创建一个AppInfo对象，并赋值
            System.out.println(appLabel + " activityName---" + activityName
                    + " pkgName---" + pkgName);
        }
        return resolveInfos;
    }


    /**
     * 获取已安装非系统应用
     *
     * @return
     */
    public static List<ApplicationInfo> scanInstallApp(Context mContext) {
        List<ApplicationInfo> appInfos = new ArrayList<>();
        PackageManager pm = mContext.getPackageManager(); // 获得PackageManager对象
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
        for (ApplicationInfo app : listAppcations) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {//非系统程序
                appInfos.add(app);
            }//本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
            else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                appInfos.add(app);
            }
        }
        return appInfos;
    }
    /**
     * 获取已安装非系统应用
     *
     * @return
     */
    public static List<ApplicationInfo> getThirdAppList(Context mContext) {
        List<ApplicationInfo> thirdAPP = new ArrayList<>();
        PackageManager pm = mContext.getPackageManager(); // 获得PackageManager对象
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
        for (ApplicationInfo app : listAppcations) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {//非系统程序
                thirdAPP.add(app);
            }//本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
            else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
//                appInfos.add(app);
            }
        }
        return thirdAPP;
    }

}
