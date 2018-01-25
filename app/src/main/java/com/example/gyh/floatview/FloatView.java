package com.example.gyh.floatview;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class FloatView {


    private Context mContext;//上下文
    private WindowManager mWindowManager;//窗口管理器
    private View mView; //悬浮窗的view
    private View mPreView;
    private WindowManager.LayoutParams mParams;//view在悬浮窗内的参数
    private int mScreenWidth;//屏幕的宽度
    private int mScaledDoubleTapSlop;//判定拖动和点击的距离
    private int measuredWidth;//屏幕的宽度
    private View.OnClickListener mListener;//悬浮控件的点击事件
    private static FloatView mInstance;

    public static FloatView getInstance(Context context) {
        if (mInstance == null) {
            synchronized (FloatView.class) {
                if (mInstance == null) {
                    mInstance = new FloatView(context);
                }
            }
        }
        return mInstance;
    }

    private FloatView(Context mContext) {
        this.mContext = mContext;
        initViewParams();
    }

    /**
     * 初始化tView相关参数
     */
    public void initViewParams() {
        mScaledDoubleTapSlop = ViewConfiguration.get(mContext).getScaledDoubleTapSlop();
        mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = mWindowManager.getDefaultDisplay().getWidth();
        mParams = new WindowManager.LayoutParams();
//        mParams.windowAnimations = R.style.dialog_anim;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT; // 显示效果
        mParams.gravity = Gravity.START;
        if (Build.VERSION.SDK_INT >= 26) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                |WindowManager.LayoutParams. FLAG_WATCH_OUTSIDE_TOUCH;
        //不允许获得焦点,toast的通性
        //  WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        //不允许接收触摸事件
        //  | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mView = View.inflate(mContext, R.layout.view_float, null);
        mView.setLayoutParams(mParams);
        handleViewTouch();
        mWindowManager.addView(mView, mParams);
    }

    public FloatView setView(View view) {
        if (view != null) {
            mPreView = mView;
            mView = view;
            handleViewTouch();
        }
        return this;
    }

    public FloatView setOnClickListener(View.OnClickListener clickListener) {
        mListener = clickListener;
        return this;
    }

    /**
     * 处理view的Touch事件
     */
    private void handleViewTouch() {
        mView.setOnTouchListener(new View.OnTouchListener() {
            int lastX, lastY;
            int paramX, paramY;
            int startTouchX, startTouchY;
            int unTouchX, unTouchY;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        startTouchX = (int) event.getRawX();
                        startTouchY = (int) event.getRawY();
                        paramX = mParams.x;
                        paramY = mParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        mParams.x = paramX + dx;
                        mParams.y = paramY + dy;
                        // 更新悬浮窗位置
                        mWindowManager.updateViewLayout(mView, mParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        //手指抬起的时候 判断离那边比较近
                        unTouchX = (int) event.getRawX();
                        unTouchY = (int) event.getRawY();
                        aliveBorder();
                        break;
                }
                int finalX = Math.abs(unTouchX - startTouchX);
                int finalY = Math.abs(unTouchY - startTouchY);
                if (finalX < mScaledDoubleTapSlop && finalY < mScaledDoubleTapSlop) {
                    return false;
                } else {
                    return true;
                }
            }
        });
    }

    /**
     * 显示悬浮窗
     */
    public FloatView show() {
        mView.setOnClickListener(mListener);
        if (mView != mPreView) {
            if (mPreView != null) {
                mWindowManager.removeView(mPreView);
            }
            mWindowManager.addView(mView, mParams);
        }
        return this;
    }

    /**
     * 做一个靠边的动画
     */
    private void aliveBorder() {
        measuredWidth = mView.getMeasuredWidth() / 2;
        if (mParams.x + measuredWidth > mScreenWidth / 2) {
            executeAnimator(mParams.x, mScreenWidth);
        } else {
            executeAnimator(mParams.x, 0);
        }
    }

    private void executeAnimator(int start, int end) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int) animation.getAnimatedValue();
                mParams.x = curValue;
                mWindowManager.updateViewLayout(mView, mParams);
            }
        });
        valueAnimator.setEvaluator(new IntEvaluator());
        valueAnimator.start();
    }


    /**
     * 隐藏悬浮控件
     */
    public void hide() {
        if (mView != null) {
            if (mView.getParent() != null) {
                mWindowManager.removeView(mView);
            }
            mView = null;
        }
    }
}  