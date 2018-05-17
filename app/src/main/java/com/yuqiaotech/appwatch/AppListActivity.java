package com.yuqiaotech.appwatch;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yuqiaotech.appwatch.adapter.AppInfoRecyclerAdapter;
import com.yuqiaotech.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * (用一句话描述类的主要功能)
 * Created by yun on 2018/5/17.
 */
public class AppListActivity extends Activity {

    private RecyclerView rv;
    private AppInfoRecyclerAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    List<ResolveInfo> list = new ArrayList<ResolveInfo>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        initViews();
    }

    private void initViews() {
        rv = findViewById(R.id.rv);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        //创建并设置Adapter
        adapter = new AppInfoRecyclerAdapter(AppUtil.getThirdAppList(this));
        adapter.setOnItemClickListener(new AppInfoRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, ApplicationInfo info) {
                if (null != info) {
                    String packageName = info.packageName;
                    Intent mIntent = new Intent();
                    mIntent.putExtra("packageName", packageName);
                    // 设置结果，并进行传送
                    AppListActivity.this.setResult(AppListActivity.RESULT_OK, mIntent);
                    AppListActivity.this.finish();

                }
            }
        });
        rv.setAdapter(adapter);
    }


}
