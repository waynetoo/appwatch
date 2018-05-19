package com.yuqiaotech.appwatch;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuqiaotech.constants.WatchConstants;
import com.yuqiaotech.preferences.AppInfoPreferences;
import com.yuqiaotech.service.WatchDogService;

import static com.yuqiaotech.constants.WatchConstants.SELECTED_APP_REQUESTCODE;

public class MainActivity extends AppCompatActivity {
    private Button btn_validate_packageName;
    private ImageView icon;
    private TextView appName;


    private boolean isServerRunning;

    private String appPackageName;
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initviews();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!hasPermission()) {
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }

    private void initviews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        icon = findViewById(R.id.app_icon);
        appName = findViewById(R.id.app_name);

        btn_validate_packageName = findViewById(R.id.btn_validate_packageName);
        //选择app
        findViewById(R.id.selected_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, AppListActivity.class);
                startActivityForResult(i, SELECTED_APP_REQUESTCODE);

            }
        });
        btn_validate_packageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(appPackageName)) {
                    Toast.makeText(MainActivity.this, "请先选择需要监控的APP", Toast.LENGTH_LONG).show();
                    return;
                }
                //验证包名
                if (!isServerRunning) {
                    startWatchDogServer();
//                    MainActivity.this.finish();
                } else {
                    stopWatchDogServer();
                }
                setBtntxt();
            }
        });
    }


    private void initData() {
        appPackageName = new AppInfoPreferences().getPackageName(WatchConstants.DEFALUT_PACKAGENAME);
//        isServerRunning = AppUtil.isServiceRunning(this, WatchDogService.class.getName());
        if (!TextUtils.isEmpty(appPackageName)) {
            startWatchDogServer();
            setAppInfo(appPackageName);
            setBtntxt();
        }
    }

    private void setAppInfo(String packname) {
        PackageManager pm = getPackageManager(); // 获得PackageManager对象
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            icon.setImageDrawable(info.loadIcon(pm));
            appName.setText(info.loadLabel(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setBtntxt() {
        //服务是否启动
        if (isServerRunning) {
            btn_validate_packageName.setText("取消监控");
        } else {
            btn_validate_packageName.setText("开启监控");
        }
    }


    private void startWatchDogServer() {
        isServerRunning = true;
        Intent intent = new Intent(MainActivity.this, WatchDogService.class);
        startService(intent);
        Toast.makeText(MainActivity.this, "开启监控成功", Toast.LENGTH_LONG).show();

    }

    private void stopWatchDogServer() {
        isServerRunning = false;
        Intent intent = new Intent(MainActivity.this, WatchDogService.class);
        stopService(intent);
    }


    // 回调方法，从第二个页面回来的时候会执行这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data) {
            return;
        }
        String packagename = data.getStringExtra("packageName");
        if (TextUtils.isEmpty(packagename)) {
            return;
        }
        // 根据上面发送过去的请求吗来区别
        switch (requestCode) {
            case SELECTED_APP_REQUESTCODE:
                Log.e("packagename", packagename);
                appPackageName = packagename;
                new AppInfoPreferences().putPackageName(appPackageName);//save
                setAppInfo(appPackageName);
                startWatchDogServer();
                setBtntxt();
                break;
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:
                if (!hasPermission()) {
                    //若用户未开启权限，则引导用户开启“Apps with usage access”权限
                    startActivityForResult(
                            new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                            MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
                }
                break;
            default:
                break;
        }
    }

    //检测用户是否对本app开启了“Apps with usage access”权限
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}
