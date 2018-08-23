package com.example.haipingguo.questionview.view.touch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import com.example.haipingguo.questionview.R;
import com.example.haipingguo.questionview.utils.ScreenUtil;
import com.example.haipingguo.questionview.view.touch.bean.ModulePosition;
import com.example.haipingguo.questionview.view.touch.bean.Position;

import java.util.ArrayList;
import java.util.List;

import static com.example.haipingguo.questionview.view.touch.TouchMoveLayout.YOFFSETX;
import static com.example.haipingguo.questionview.view.touch.TouchMoveLayout.YOFFSETY;

public class ItemButtonView extends android.support.v7.widget.AppCompatTextView {
    public List<ModulePosition> resultPositionList = new ArrayList<>();
    private ModulePosition optionOrginLocation;
    public OnChangeEventListener mOnChangeEventListener;
    private boolean isMove;
    public int index;
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

    public void setIndex(int aindex) {
        this.index = aindex;
    }


    public void setOptionOrginLocation(ModulePosition optionOrginLocation) {
        this.optionOrginLocation = optionOrginLocation;
    }

    public void setOnChangeEventListener(OnChangeEventListener listener) {
        this.mOnChangeEventListener = listener;
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

    public void moveTo(ModulePosition toPosition) {
        setX(toPosition.leftTop.x);
        setY(toPosition.leftTop.y);
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
                isMove = Math.abs(moveX - downX) > 20 || Math.abs(moveY - downY) > 20;
                if (isMove) {
                    setX(moveX - YOFFSETX - getWidth() / 2);
                    setY(moveY - YOFFSETY - getHeight() / 2);
                }
                break;
            case MotionEvent.ACTION_UP:
                float x = event.getRawX() - YOFFSETX;
                float y = event.getRawY() - YOFFSETY;
                if (!isMove) {
                    return false;
                }
                ModulePosition check = check(x, y);
                if (check != null) {
                    mOnChangeEventListener.moveToHotQueue(new Position(x - getWidth() / 2, y - getHeight() / 2), check);
                } else {
                    mOnChangeEventListener.moveToInitial(new Position(x, y), optionOrginLocation,this);
                }
                break;
        }
        return true;
    }

    private ModulePosition check(float x, float y) {
        ModulePosition position;
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
        void moveToInitial(Position startPosition, ModulePosition endPosition, ItemButtonView itemButtonView);

        void moveToHotQueue(Position startPosition, ModulePosition endPosition);
    }
}
