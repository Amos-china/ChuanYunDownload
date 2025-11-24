package com.chuanyun.downloader.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuanyun.downloader.R;


public class CustomItemLinearLayout extends LinearLayout {
    private LinearLayout contentLL;
    private TextView titleTv;
    private TextView subTitleTv;
    private View topView;
    private View bottomLineView;
    private ImageView leftImageView;
    private ImageView rightImageView;

    public CustomItemLinearLayout(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);

        initView(context,attributeSet);
    }

    private void initView(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomItemLinearLayout);
        String title = typedArray.getString(R.styleable.CustomItemLinearLayout_cll_title_str);
        String subTitle = typedArray.getString(R.styleable.CustomItemLinearLayout_cll_sub_title_str);
        int titleColor = typedArray.getColor(R.styleable.CustomItemLinearLayout_cll_title_color,getResources().getColor(R.color.black));
        int subTitleColor = typedArray.getColor(R.styleable.CustomItemLinearLayout_cll_sub_title_color,getResources().getColor(R.color.gray_999));
        int contentViewColor = typedArray.getColor(R.styleable.CustomItemLinearLayout_cll_bg_color,getResources().getColor(R.color.white));
        boolean showRightIm = typedArray.getBoolean(R.styleable.CustomItemLinearLayout_cll_show_right_im,true);
        boolean showTopView = typedArray.getBoolean(R.styleable.CustomItemLinearLayout_cll_show_top_view,true);
        boolean showBottomLineView = typedArray.getBoolean(R.styleable.CustomItemLinearLayout_cll_show_bottom_line_view,false);
        Drawable leftImSrc = typedArray.getDrawable(R.styleable.CustomItemLinearLayout_cll_left_im_src);


        View inflate = LayoutInflater.from(context).inflate(R.layout.linear_layout_custom,this,true);
        contentLL = inflate.findViewById(R.id.content_ll);
        titleTv = inflate.findViewById(R.id.title_tv);
        subTitleTv = inflate.findViewById(R.id.sub_title_tv);
        topView = inflate.findViewById(R.id.top_view);
        bottomLineView = findViewById(R.id.bottom_line_view);
        leftImageView = findViewById(R.id.left_icon_im);
        rightImageView = findViewById(R.id.right_icon_im);

        contentLL.setBackgroundColor(contentViewColor);
        titleTv.setTextColor(titleColor);
        subTitleTv.setTextColor(subTitleColor);

        titleTv.setText(title);
        subTitleTv.setText(subTitle);

        if (leftImSrc != null) {
            leftImageView.setVisibility(VISIBLE);
            leftImageView.setImageDrawable(leftImSrc);
        }else {
            leftImageView.setVisibility(GONE);
        }

        rightImageView.setVisibility(showRightIm ? VISIBLE : GONE);

        topView.setVisibility(showTopView ? VISIBLE : GONE);

        bottomLineView.setVisibility(showBottomLineView ? VISIBLE : GONE);

    }

    public void setTitleText(String text) {
        if (!TextUtils.isEmpty(text)) {
            titleTv.setText(text);
        }
    }

    public void setSubTitleText(String text) {
        subTitleTv.setText(text);
    }

}
