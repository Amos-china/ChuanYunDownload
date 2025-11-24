package com.chuanyun.downloader.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.chuanyun.downloader.R;

public class DraggableImageView extends androidx.appcompat.widget.AppCompatImageView {
    private float downX, downY; // 记录手指按下时的坐标
    private float viewX, viewY; // 记录View的原始坐标
    private int screenWidth;   // 屏幕宽度
    private OnClickListener clickListener; // 点击事件监听器

    public DraggableImageView(Context context) {
        super(context);
        init(context);
    }

    public DraggableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DraggableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // 获取屏幕宽度
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = windowManager.getDefaultDisplay().getWidth();

        // 设置默认图片资源（可以自定义）
        setImageResource(R.mipmap.add_torrent_ic);

        // 设置触摸监听，实现拖动逻辑
        setOnTouchListener(new OnTouchListener() {
            private long touchStartTime;
            private static final long CLICK_DURATION = 200; // 定义点击的最大持续时间

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 记录按下时的坐标和时间
                        downX = event.getRawX();
                        downY = event.getRawY();
                        viewX = getX();
                        viewY = getY();
                        touchStartTime = System.currentTimeMillis();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // 计算手指移动的距离并更新View的位置
                        float moveX = event.getRawX() - downX;
                        float moveY = event.getRawY() - downY;
                        setX(viewX + moveX);
                        setY(viewY + moveY);
                        return true;

                    case MotionEvent.ACTION_UP:
                        // 判断是否为点击事件
                        long touchDuration = System.currentTimeMillis() - touchStartTime;
                        if (touchDuration < CLICK_DURATION && Math.abs(event.getRawX() - downX) < 10 && Math.abs(event.getRawY() - downY) < 10) {
                            if (clickListener != null) {
                                clickListener.onClick(DraggableImageView.this);
                            }
                        } else {
                            // 拖动结束后，根据最终X坐标选择靠左或靠右
                            float finalX = getX();
                            if (finalX + getWidth() / 2 < screenWidth / 2) {
                                // 靠左
                                animate().x(20).setDuration(300).start();
                            } else {
                                // 靠右
                                animate().x(screenWidth - getWidth() - 20).setDuration(300).start();
                            }
                        }
                        return true;
                }
                return false;
            }
        });
    }

    // 设置点击事件监听器
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        this.clickListener = l;
    }
}
