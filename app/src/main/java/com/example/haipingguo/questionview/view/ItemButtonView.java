package com.example.haipingguo.questionview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.example.haipingguo.questionview.R;
import com.example.haipingguo.questionview.utils.ScreenUtil;
import com.example.haipingguo.questionview.view.bean.ModulePosition;
import com.example.haipingguo.questionview.view.bean.Position;

import java.util.ArrayList;
import java.util.List;

import static com.example.haipingguo.questionview.view.TouchMoveLayout.YOFFSETX;
import static com.example.haipingguo.questionview.view.TouchMoveLayout.YOFFSETY;

public class ItemButtonView extends android.support.v7.widget.AppCompatTextView {
    //答案的位置范围数组
    public List<ModulePosition> resultPositionList = new ArrayList<>();
    public OnChangeEventListener listener;
    private float downX;
    private float downY;

    public ItemButtonView(Context context) {
        this(context, null);
    }

    public ItemButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnChangeEvent(OnChangeEventListener listener) {
        this.listener = listener;
    }

    public void setResultPositionList(List<ModulePosition> positionList) {
        if (resultPositionList != null) {
            resultPositionList.clear();
            resultPositionList.addAll(positionList);
        }
    }

    public void initLayout(final Context context) {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                setBackgroundResource(R.drawable.live_common_touch_view_background);
                setTextColor(getResources().getColor(R.color.live_common_gray_1));
                setPadding(ScreenUtil.dp2px(context, 20), ScreenUtil.dp2px(context, 7),
                        ScreenUtil.dp2px(context, 20), ScreenUtil.dp2px(context, 10));
            }
        });
    }

    //已处理偏移问题,待处理动画
    public void moveTo(Position toPosition) {
        setX(toPosition.x);
        setY(toPosition.y);
        Log.i("ghppp","time 2=="+System.currentTimeMillis());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getRawX();
                float moveY = event.getRawY();
                if (Math.abs(moveX - downX) > 20 || Math.abs( moveY - downX) > 20) {
                    setX(moveX  - YOFFSETX-getWidth()/2);
                    setY(moveY  - YOFFSETY-getHeight()/2);
                }
                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                float x = event.getRawX()- YOFFSETX;
                float y = event.getRawY() - YOFFSETY;
                ModulePosition check = check(x, y);
                if(check!=null){
                    moveToOther(check);
                    //listener.moveToOther(this, check);
                }else{

                }
                break;
        }
        return true;
    }

    public void moveToOther(ModulePosition modulePosition){
        final Position position1=modulePosition.centerPosition;
        TranslateAnimation animation = new TranslateAnimation(0, position1.x - getX(), 0, position1.y - getY());
        animation.setFillBefore(false);
        animation.setDuration(200);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setClickable(true);
                setX(position1.x-getWidth()/2);
                setY(position1.y-getHeight()/2);
                clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animation);
    }

    private ModulePosition check(float x, float y) {
        ModulePosition position ;
        for (int i = 0; i < resultPositionList.size(); i++) {
            position = resultPositionList.get(i);
            if (position != null) {
                if (x > position.leftTop.x && x < position.rightBottom.x && y > position.leftTop.y && y < position.rightBottom.y) {
                    return position;
                }
            }
        }
        return null;
    }

    interface OnChangeEventListener {
        void moveToFailed(ModulePosition currentPosition, ItemButtonView itemButtonView);

        void moveToInitial(ItemButtonView itemButtonView);

        void moveToOther(ItemButtonView itemButtonView, ModulePosition check);
    }
}
