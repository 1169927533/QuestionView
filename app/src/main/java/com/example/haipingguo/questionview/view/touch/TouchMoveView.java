package com.example.haipingguo.questionview.view.touch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.haipingguo.questionview.utils.AnimaUtils;
import com.example.haipingguo.questionview.view.touch.bean.ModulePosition;
import com.example.haipingguo.questionview.view.touch.bean.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.haipingguo.questionview.view.touch.TouchMoveLayout.YOFFSETX;
import static com.example.haipingguo.questionview.view.touch.TouchMoveLayout.YOFFSETY;

public class TouchMoveView extends android.support.v7.widget.AppCompatTextView
        implements ItemButtonView.OnChangeEventListener {
    private Context mContext;
    private float downX;
    private float downY;
    private ItemButtonView itemView;
    private TouchMoveLayout touchMoveLayout;
    private ModulePosition optionOrginLocation;
    private OnTouchMoveListener mOnTouchMoveListener;
    private List<ModulePosition> mHotList = new ArrayList<>();
    private List<ModulePosition> mOptionList = new ArrayList<>();
    private boolean isMove;

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

    public void setOnTouchMoveListener(OnTouchMoveListener onTouchMoveListener){
        mOnTouchMoveListener=onTouchMoveListener;
    }

    public void setHotList(List<ModulePosition> centerList) {
        mHotList.clear();
        mHotList.addAll(centerList);
    }

    public void setOptionList(List<ModulePosition> optionList) {
        mOptionList.clear();
        mOptionList.addAll(optionList);
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
                    itemView = getItemButtonView(index);
                    itemView.initLayout(mContext);
                    itemView.setOnChangeEventListener(this);
                    int[] ints = new int[2];
                    getLocationOnScreen(ints);
                    itemView.setResultPositionList(mHotList);
                    optionOrginLocation = new ModulePosition();
                    optionOrginLocation.setCenterPosition(new Position(ints[0] - YOFFSETX + getWidth() / 2, ints[1] - YOFFSETY + getHeight() / 2));
                    optionOrginLocation.setLeftTop(new Position(ints[0] - YOFFSETX, ints[1] - YOFFSETY));
                    optionOrginLocation.setRightBottom(new Position(ints[0] - YOFFSETX + getWidth(), ints[1] - YOFFSETY + getHeight()));
                    itemView.setOptionOrginLocation(optionOrginLocation);
                    itemView.moveTo(optionOrginLocation);
                    touchMoveLayout.addView(itemView);
                    setVisibility(INVISIBLE);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getRawX();
                float moveY = event.getRawY();
                isMove = Math.abs(moveX - downX) > 20 || Math.abs(moveY - downY) > 20;
                if (isMove) {
                    itemView.setX(moveX - YOFFSETX - getWidth() / 2);
                    itemView.setY(moveY - YOFFSETY - getHeight() / 2);
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
                    moveToHotQueue(new Position(x, y), check);
                } else {
                    moveToHotQueue(new Position(x, y), optionOrginLocation);
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

    public ItemButtonView getItemButtonView(int index) {
        ItemButtonView itemButtonView = new ItemButtonView(mContext);
        itemButtonView.setIndex(index);
        touchMoveLayout.getOptionView(itemButtonView, index);
        return itemButtonView;
    }

    @Override
    public void moveToInitial(Position startPosition, ModulePosition endPosition, ItemButtonView itemButtonView) {
        AnimaUtils.moveToHotQuestion(new Position(startPosition.x, startPosition.y), endPosition, itemButtonView);
        Map.Entry<Integer, ItemButtonView> entry1 = null;
        for (Map.Entry<Integer, ItemButtonView> entry : TouchMoveLayout.resultMap.entrySet()) {
            if(itemButtonView.equals(entry.getValue())){
                entry1=entry;
            }
        }
        if(entry1!=null){
            TouchMoveLayout.resultMap.remove(entry1.getKey());
        }
        mOnTouchMoveListener.result(getList(TouchMoveLayout.resultMap));
    }

    @Override
    public void moveToHotQueue(Position startPosition, ModulePosition endPosition) {
        if (TouchMoveLayout.resultMap.containsKey(endPosition.index)) {
            ItemButtonView itemButtonView = TouchMoveLayout.resultMap.get(endPosition.index);
            //一个在热区，一个不在
            if (!TouchMoveLayout.resultMap.containsValue(itemView)) {
                moveToInitial(mHotList.get(endPosition.index).centerPosition, mOptionList.get(itemButtonView.index), itemButtonView);
                TouchMoveLayout.resultMap.remove(endPosition.index);
                TouchMoveLayout.resultMap.put(endPosition.index, itemView);
            } else {
                //选项都在热区，互换
                Map.Entry<Integer, ItemButtonView> entry1 = null;
                for (Map.Entry<Integer, ItemButtonView> entry : TouchMoveLayout.resultMap.entrySet()) {
                    if(itemView.equals(entry.getValue())){
                        entry1=entry;
                    }
                }
                if(entry1!=null){
                    ModulePosition modulePosition1 = mHotList.get(entry1.getKey());
                    AnimaUtils.moveToHotQuestion(endPosition.centerPosition,
                            modulePosition1, itemButtonView);
                    TouchMoveLayout.resultMap.remove(endPosition.index);
                    TouchMoveLayout.resultMap.remove(modulePosition1.index);
                    TouchMoveLayout.resultMap.put(endPosition.index, itemView);
                    TouchMoveLayout.resultMap.put(modulePosition1.index, itemButtonView);
                }
            }
        } else {
            //已经在热区的位置，跳到另一个热区
            if (TouchMoveLayout.resultMap.containsValue(itemView)) {
                for (Map.Entry<Integer, ItemButtonView> entry : TouchMoveLayout.resultMap.entrySet()) {
                    if(itemView.equals(entry.getValue())){
                        TouchMoveLayout.resultMap.remove(entry.getKey());
                    }
                }
            }
            //从开始位置到热区
            TouchMoveLayout.resultMap.put(endPosition.index, itemView);
        }
        mOnTouchMoveListener.result(getList(TouchMoveLayout.resultMap));
        AnimaUtils.moveToHotQuestion(new Position(startPosition.x, startPosition.y),
                endPosition, itemView);
    }

    private List<Integer> getList(Map<Integer, ItemButtonView> resultMap) {
        List<Integer> resultList = new ArrayList<>();
        for (int i = 0; i < mOptionList.size(); i++) {
            ItemButtonView itemButtonView = resultMap.get(i);
            if(itemButtonView!=null){
                resultList.add(itemButtonView.index);
            }
        }
        return resultList;
    }

    public interface OnTouchMoveListener {
        void result(List<Integer> resultList);
    }
}
