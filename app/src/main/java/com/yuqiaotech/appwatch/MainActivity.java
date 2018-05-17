package com.yuqiaotech.appwatch;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuqiaotech.app.AppWatchApplication;
import com.yuqiaotech.preferences.AppInfoPreferences;
import com.yuqiaotech.service.WatchDogService;
import com.yuqiaotech.utils.AppUtil;

import static com.yuqiaotech.constants.WatchConstants.SELECTED_APP_REQUESTCODE;

public class MainActivity extends AppCompatActivity {
    private Button btn_validate_packageName;
    private ImageView icon;
    private TextView appName;


    private boolean isServerRunning;

    private String appPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initviews();
        initData();

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
                } else {
                    stopWatchDogServer();
                }
                setBtntxt();
            }
        });
    }


    private void initData() {
        appPackageName = new AppInfoPreferences().getPackageName();
        isServerRunning = AppUtil.isServiceRunning(this, WatchDogService.class.getName());
        if (!TextUtils.isEmpty(appPackageName)) {
            ResolveInfo resolveInfo = AppUtil.findAppByPackageName(this, appPackageName);
            setAppInfo(resolveInfo);
            setBtntxt();
        }
    }

    private void setAppInfo(ResolveInfo resolveInfo) {
        if (null == resolveInfo) {
            return;
        }
        PackageManager pm = AppWatchApplication.getApplication().getPackageManager(); // 获得PackageManager对象
        icon.setImageDrawable(resolveInfo.loadIcon(pm));
        appName.setText(resolveInfo.loadLabel(pm));
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
        String packagename = data.getStringExtra("packageName");
        if (TextUtils.isEmpty(packagename)) {
            return;
        }
        // 根据上面发送过去的请求吗来区别
        switch (requestCode) {
            case SELECTED_APP_REQUESTCODE:
                appPackageName = packagename;
                new AppInfoPreferences().putPackageName(appPackageName);
                ResolveInfo resolveInfo = AppUtil.findAppByPackageName(this, appPackageName);
                setAppInfo(resolveInfo);
                startWatchDogServer();
                setBtntxt();
                break;
            default:
                break;
        }
    }
}
