package com.example.haipingguo.questionview.view.drag;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.haipingguo.questionview.R;

/**
 * Created by luoxuwei on 2017/9/20.
 */
public class DragWindowHelper {
    private Context mContext;
    private float mTouchSlop;
    private View mItemView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;

    public DragWindowHelper(Context context) {
        mContext = context;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mParams = createParams();
    }

    private WindowManager.LayoutParams createParams() {
        mParams = new WindowManager.LayoutParams();
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        return mParams;
    }

    public void showDragView() {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(R.mipmap.ic_launcher);
        imageView.setOnTouchListener(new DragTouchListener());
        mItemView=imageView;
        mWindowManager.addView(imageView, mParams);
    }

    private class DragTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float downX = event.getRawX();
            float downY = event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getRawX();
                    downY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveX = event.getRawX() - downX;
                    float moveY = event.getRawY() - downY;
                    if (Math.abs(moveX) > mTouchSlop || Math.abs(moveY) > mTouchSlop) {
                        moveDragView(event.getRawX(),event.getRawY());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    return false;
            }
            return false;
        }
    }

    private void moveDragView(float rawX, float rawY) {
        mParams.x= (int) rawX;
        mParams.y= (int) rawY;
        mWindowManager.updateViewLayout(mItemView, mParams);
    }
}
