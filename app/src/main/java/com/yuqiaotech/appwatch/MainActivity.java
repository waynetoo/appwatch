package com.yuqiaotech.appwatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yuqiaotech.constants.WatchConstants;
import com.yuqiaotech.service.WatchDogService;
import com.yuqiaotech.utils.AppUtil;

public class MainActivity extends AppCompatActivity {
    private Button btn_validate_packageName;
    private TextInputLayout til_package_name;
    private SharedPreferences spWatchDog;

    private boolean isServerRunning;

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
        til_package_name = findViewById(R.id.til_package_name);
        btn_validate_packageName = findViewById(R.id.btn_validate_packageName);
        btn_validate_packageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String packageName = til_package_name.getEditText().getText().toString();

                til_package_name.setErrorEnabled(false);

                //验证包名
                if (validatePackageName(packageName)) {
                    isServerRunning = true;
                    spWatchDog.edit().putString(WatchConstants.SP_PACKAGE_NAME, packageName).commit();
                    Intent intent = new Intent(MainActivity.this, WatchDogService.class);
                    startService(intent);
                    Toast.makeText(MainActivity.this, "开启监控成功", Toast.LENGTH_LONG).show();
                } else {
                    isServerRunning = false;
                    Intent intent = new Intent(MainActivity.this, WatchDogService.class);
                    stopService(intent);
                }
                setBtntxt();
            }
        });
    }

    private void initData() {
        spWatchDog = getSharedPreferences(WatchConstants.SP_WATCH, Context.MODE_PRIVATE);
        String watchpackageName = spWatchDog.getString(WatchConstants.SP_PACKAGE_NAME, "");
        isServerRunning = AppUtil.isServiceRunning(this, WatchDogService.class.getName());
        if (!TextUtils.isEmpty(watchpackageName)) {
            til_package_name.getEditText().setText(watchpackageName);
            setBtntxt();
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

    /**
     * 验证包名
     *
     * @return
     */
    private boolean validatePackageName(String packagename) {
        boolean isExit = AppUtil.isPkgInstalled(this, packagename);
        if (!isExit) {
            showError(til_package_name, "app 包名不存在，请重新输入！");
        }
        return isExit;
    }

    /**
     * 显示错误提示，并获取焦点
     *
     * @param textInputLayout
     * @param error
     */
    private void showError(TextInputLayout textInputLayout, String error) {
        textInputLayout.setError(error);
        textInputLayout.getEditText().setFocusable(true);
        textInputLayout.getEditText().setFocusableInTouchMode(true);
        textInputLayout.getEditText().requestFocus();
    }

}
