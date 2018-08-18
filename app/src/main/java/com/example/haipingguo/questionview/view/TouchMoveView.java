package com.example.haipingguo.questionview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.example.haipingguo.questionview.utils.AnimaUtils;
import com.example.haipingguo.questionview.view.bean.ModulePosition;
import com.example.haipingguo.questionview.view.bean.Position;

import java.util.ArrayList;
import java.util.List;

import static com.example.haipingguo.questionview.view.TouchMoveLayout.YOFFSETX;
import static com.example.haipingguo.questionview.view.TouchMoveLayout.YOFFSETY;

public class TouchMoveView extends android.support.v7.widget.AppCompatTextView
        implements ItemButtonView.OnChangeEventListener {
    private Context mContext;
    private float downX;
    private float downY;
    private ItemButtonView itemView;
    private TouchMoveLayout touchMoveLayout;
    private ModulePosition optionOrginLocation;
    private List<ModulePosition> mHotList = new ArrayList<>();

    private int index;

    public TouchMoveView(Context context) {
        this(context, null);
    }

    public TouchMoveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchMoveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void setHotList(List<ModulePosition> centerList) {
        mHotList.clear();
        mHotList.addAll(centerList);
    }

    public void setParentLayout(TouchMoveLayout parentLayout) {
        this.touchMoveLayout = parentLayout;
    }

    public void setIndex(int aindex) {
        index = aindex;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                if (itemView == null) {
                    itemView = getItemButtonView();
                    itemView.initLayout(mContext);
                    int[] ints = new int[2];
                    getLocationOnScreen(ints);
                    itemView.setResultPositionList(mHotList);
                    optionOrginLocation = new ModulePosition();
                    optionOrginLocation.setCenterPosition(new Position(ints[0]-YOFFSETX+getWidth()/2,ints[1]-YOFFSETY+getHeight()/2));
                    optionOrginLocation.setLeftTop(new Position(ints[0]-YOFFSETX, ints[1]-YOFFSETY));
                    optionOrginLocation.setRightBottom(new Position(ints[0]-YOFFSETX + getWidth(), ints[1]-YOFFSETY + getHeight()));
                    itemView.setOptionOrginLocation(optionOrginLocation);
                    itemView.moveTo(optionOrginLocation);
                    touchMoveLayout.addView(itemView);
                    setVisibility(INVISIBLE);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getRawX();
                float moveY = event.getRawY();
                if (Math.abs(moveX - downX) > 20 || Math.abs(moveY - downY) > 20) {
                    itemView.setX(moveX - YOFFSETX - getWidth() / 2);
                    itemView.setY(moveY - YOFFSETY - getHeight() / 2);
                }
                break;
            case MotionEvent.ACTION_UP:
                float x = event.getRawX() - YOFFSETX;
                float y = event.getRawY() - YOFFSETY;
                if (isShowAnimat(x, y)) {
                    return false;
                }
                ModulePosition check = check(x, y);
                if (check != null) {
                    AnimaUtils.moveToHotQuestion(new Position(x, y), check, itemView);
                } else {
                    AnimaUtils.moveToHotQuestion(new Position(x - YOFFSETX, y - YOFFSETY), optionOrginLocation, itemView);
                }
                break;
        }
        return true;
    }

    private ModulePosition check(float x, float y) {
        for (int i = 0; i < mHotList.size(); i++) {
            ModulePosition position = mHotList.get(i);
            if (position != null) {
                if (x > position.leftTop.x && x < position.rightBottom.x && y > position.leftTop.y && y < position.rightBottom.y) {
                    return position;
                }
            }
        }
        return null;
    }

    @Override
    public void moveToFailed(ModulePosition currentPosition, ItemButtonView itemButtonView) {

    }

    @Override
    public void moveToInitial(ItemButtonView itemButtonView) {

    }

    @Override
    public void moveToOther(ItemButtonView itemButtonView, ModulePosition check) {

    }

    public ItemButtonView getItemButtonView() {
        ItemButtonView itemButtonView = new ItemButtonView(mContext);
        touchMoveLayout.getOptionView(itemButtonView, index);
        return itemButtonView;
    }

    public boolean isShowAnimat(float x, float y) {
        if (x > optionOrginLocation.leftTop.x && x < optionOrginLocation.rightBottom.x
                && y > optionOrginLocation.leftTop.y && y > optionOrginLocation.rightBottom.y) {
            return true;
        }
        return false;
    }
}
