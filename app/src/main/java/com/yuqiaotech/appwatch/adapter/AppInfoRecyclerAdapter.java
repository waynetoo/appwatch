package com.yuqiaotech.appwatch.adapter;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuqiaotech.app.AppWatchApplication;
import com.yuqiaotech.appwatch.R;

import java.util.List;

/**
 * (用一句话描述类的主要功能)
 * Created by yun on 2018/5/17.
 */
public class AppInfoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<ApplicationInfo> list;
    private OnItemClickListener mClickListener;
    private PackageManager pm = AppWatchApplication.getApplication().getPackageManager(); // 获得PackageManager对象

    public AppInfoRecyclerAdapter(List<ApplicationInfo> list) {
        this.list = list;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //如果viewType是普通item返回普通的布局，否则是底部布局并返回
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .item_app_info_list, viewGroup, false);
        final NormalItmeViewHolder vh = new NormalItmeViewHolder(view);
        if (mClickListener != null) {
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(vh.itemView, list.get(vh.getLayoutPosition()));
                }
            });
        }
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ApplicationInfo info = list.get(position);
        if (null == info) {
            return;
        }
        ((NormalItmeViewHolder) viewHolder).label.setText(pm.getApplicationLabel(info).toString());
        ((NormalItmeViewHolder) viewHolder).iv.setImageDrawable(info.loadIcon(pm));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public int getItemViewType(int position) {
        // 如果position+1等于整个布局所有数总和就是底部布局
        return super.getItemViewType(position);
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalItmeViewHolder extends RecyclerView.ViewHolder {
        public TextView label;
        public ImageView iv;

        public NormalItmeViewHolder(View view) {
            super(view);
            label = (TextView) view.findViewById(R.id.item_title_tv);
            iv = (ImageView) view.findViewById(R.id.item_iv);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View itemView, ApplicationInfo info);
    }
}
