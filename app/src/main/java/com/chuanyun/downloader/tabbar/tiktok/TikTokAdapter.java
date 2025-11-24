package com.chuanyun.downloader.tabbar.tiktok;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chuanyun.downloader.R;

import java.util.ArrayList;
import java.util.List;

public class TikTokAdapter extends PagerAdapter {

    public interface ImActionCallBack {
        void downloadImAction(TikTokRandomDataModel dataModel);
        void userHomeImAction(TikTokRandomDataModel dataModel);
    }

    private ImActionCallBack imActionCallBack;

    public void setImActionCallBack(ImActionCallBack imActionCallBack) {
        this.imActionCallBack = imActionCallBack;
    }

    private List<View> mViewPool = new ArrayList<>();
    private List<TikTokRandomDataModel> mVideoBeans;

    public TikTokAdapter(List<TikTokRandomDataModel> data) {
        this.mVideoBeans = data;
    }

    @Override
    public int getCount() {
        return mVideoBeans == null ? 0 : mVideoBeans.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Context context = container.getContext();
        View view = null;
        if (mViewPool.size() > 0) {//取第一个进行复用
            view = mViewPool.get(0);
            mViewPool.remove(0);
        }

        ViewHolder viewHolder;
        if (view == null) {

            view = LayoutInflater.from(context).inflate(R.layout.item_tik_tok, container, false);
            viewHolder = new ViewHolder(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        TikTokRandomDataModel item = mVideoBeans.get(position);
        //开始预加载
        PreloadManager.getInstance(context).addPreloadTask(item.getUrl(), position);
        RequestOptions options = new RequestOptions()
                .frame(0); // 获取第一帧
        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(item.getUrl())
                .placeholder(android.R.color.black)
                .into(viewHolder.mThumb);
        viewHolder.mTitle.setText(item.getVideoDesc());
        viewHolder.mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.downloadIm.setOnClickListener(imView -> {
            if (imActionCallBack != null) {
                imActionCallBack.downloadImAction(item);
            }
        });

        viewHolder.homeIm.setOnClickListener(homeView -> {
            if (imActionCallBack != null) {
                imActionCallBack.userHomeImAction(item);
            }
        });

        viewHolder.mPosition = position;
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View itemView = (View) object;
        container.removeView(itemView);
        TikTokRandomDataModel item = mVideoBeans.get(position);
        //取消预加载
        PreloadManager.getInstance(container.getContext()).removePreloadTask(item.getUrl());
        //保存起来用来复用
        mViewPool.add(itemView);
    }


    /**
     * 借鉴ListView item复用方法
     */
    public static class ViewHolder {

        public int mPosition;
        public TextView mTitle;//标题
        public ImageView mThumb;//封面图
        public TikTokView mTikTokView;
        public FrameLayout mPlayerContainer;
        public ImageView downloadIm, homeIm;

        ViewHolder(View itemView) {
            mTikTokView = itemView.findViewById(R.id.tiktok_view);
            mTitle = mTikTokView.findViewById(R.id.tv_title);
            mThumb = mTikTokView.findViewById(R.id.iv_thumb);
            downloadIm = mTikTokView.findViewById(R.id.im_download);
            homeIm = mTikTokView.findViewById(R.id.im_home);
            mPlayerContainer = itemView.findViewById(R.id.container);
            itemView.setTag(this);
        }
    }
}
