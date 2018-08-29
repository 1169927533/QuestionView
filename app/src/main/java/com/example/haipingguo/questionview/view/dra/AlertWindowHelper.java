package com.example.haipingguo.questionview.view.dra;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by luoxuwei on 2017/9/20.
 */
public class AlertWindowHelper {

    private final WindowManager mWm;
    private final WindowManager.LayoutParams mParams;
    private final float mTouchSlop;
    private final int mStateBarHeight;
    private final boolean mFullScreen;

    private View mView;
    private ICallback mCallback;

    private int mInitLeft;
    private int mInitTop;
    private int mTouchDownX;
    private int mTouchDownY;
    private AnimatorSet mBegin;
    public interface ICallback {

        void onCancel(View v, MotionEvent event);

        boolean onMove(View view, MotionEvent event);
    }

    public AlertWindowHelper(Context context) {
        this.mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.mParams = createWindowParams();
        this.mStateBarHeight = ViewUtils.getStatusHeight(context);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mFullScreen = context instanceof Activity && (((Activity) context).getWindow()
                .getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN ) != 0 ;
    }

    public View getView() {
        return mView;
    }

    public void setTouchDownPosition(int x, int y) {
        this.mTouchDownX = x;
        this.mTouchDownY = y ;
    }

    private WindowManager.LayoutParams createWindowParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.LEFT | Gravity.TOP; //这是窗体的原点位置,如果设置为CENTER默认,远点会在屏幕中间
        params.x = 0;
        params.y = 0;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;      //focusable不能去,如果去了后面的窗口就都不能操作了
        // params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//需要权限SYSTEM_ALERT_WINDOW; 6.0以后弹出窗体会异常
        return params;
    }

    public void showView(View view, int width, int height, int initLeft, int initTop, boolean useInternalDrag, ICallback callback) {
        releaseView();
        this.mInitLeft = initLeft;
        this.mInitTop = initTop;
        if (callback != null) {
            mCallback = callback;
        }
        view.setDrawingCacheEnabled(true);
        ImageView dragView = new ImageView(view.getContext());
        dragView.setImageBitmap(Bitmap.createBitmap(view.getDrawingCache()));
        dragView.setScaleType(ImageView.ScaleType.CENTER);
        view.destroyDrawingCache();

        mView = dragView;
        if (useInternalDrag) {
            mView.setOnTouchListener(new DragTouchListener());
        }
        mParams.x = initLeft;
        mParams.y = adjustY(initTop);
        mParams.width = (int) (width*1.2);
        mParams.height = (int) (height*1.2);

        mWm.addView(mView, mParams);
        ObjectAnimator beginX = ObjectAnimator.ofFloat(mView, "scaleX", 1f, 1.2f);
        beginX.setDuration(200);
        ObjectAnimator beginY = ObjectAnimator.ofFloat(mView, "scaleY", 1f, 1.2f);
        beginY.setDuration(200);
        mBegin = new AnimatorSet();
        mBegin.playTogether(beginX,beginY);
        mBegin.start();
    }

    public void updateViewLayout2(int dx, int dy) {
        if (mView == null) {
            throw new IllegalStateException("must call #showView first");
        }
        mParams.x = mInitLeft + dx;
        mParams.y = adjustY(mInitTop + dy);
        mWm.updateViewLayout(mView, mParams);
    }

    public void releaseView() {
        if (mView != null) {
            mView.setOnTouchListener(null);
            mBegin.cancel();
            mWm.removeView(mView);
            mView = null;
        }
    }

    private int adjustY(int top) {
        return mFullScreen ? top : top - mStateBarHeight;
    }

    private boolean checkTouchSlop(float dx, float dy) {
        return Math.abs(dx) > mTouchSlop || Math.abs(dy) > mTouchSlop;
    }

    private class DragTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final ICallback mCallback = AlertWindowHelper.this.mCallback;
            if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
                if (mCallback != null) {
                    mCallback.onCancel(v, event);
                }
                return false;
            }


            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                int dx = (int) event.getRawX() - mTouchDownX;
                int dy = (int) event.getRawY() - mTouchDownY;
                if(!checkTouchSlop(dx , dy)) {
                    return false;
                }
                updateViewLayout2(dx, dy);
                return mCallback != null && mCallback.onMove(v, event);
            }
            return false;
        }
    }

}
