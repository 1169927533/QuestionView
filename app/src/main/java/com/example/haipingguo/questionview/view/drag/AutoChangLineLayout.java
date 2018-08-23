package com.example.haipingguo.questionview.view.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class AutoChangLineLayout extends ViewGroup {

    public AutoChangLineLayout(Context context) {
        this(context, null);
    }

    public AutoChangLineLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoChangLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new MarginLayoutParams(lp);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth = 0;
        int totalHeight = 0;
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //measureChildren(widthMeasureSpec,heightMeasureSpec);
        for (int i = 0; i < getChildCount(); ++i) {
            View child = getChildAt(i);
            if(child.getVisibility()!=View.GONE){
                measureChild(child,widthMeasureSpec,heightMeasureSpec);
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            totalWidth += child.getMeasuredWidth()+lp.leftMargin+lp.rightMargin;
            if(totalHeight<child.getMeasuredHeight()+lp.topMargin+lp.bottomMargin){
                totalHeight = child.getMeasuredHeight()+lp.topMargin+lp.bottomMargin;
            }
            Log.i("ghppp","onMeasure child width=="+child.getMeasuredWidth());
        }
        int width = modeWidth==MeasureSpec.AT_MOST?totalWidth:sizeWidth;
        int height = modeHeight==MeasureSpec.AT_MOST?totalHeight:sizeHeight;
        setMeasuredDimension(width,height);
      /*  int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int width = modeWidth==MeasureSpec.EXACTLY?
                getMeasureWidthSize(sizeWidth):sizeWidth;
        int height = modeHeight==MeasureSpec.EXACTLY?
                getMeasureHeightSize(width>sizeWidth,sizeHeight):sizeHeight;
        setMeasuredDimension(width,height);*/
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0, top = 0,right=0,rightMargin=0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            top=lp.topMargin;
            left =i==0?lp.leftMargin:left+child.getMeasuredWidth()+lp.leftMargin+rightMargin;
            right=left + child.getMeasuredWidth();
            child.layout(left, top, right,
                    top + child.getMeasuredHeight());
            rightMargin=lp.rightMargin;
        }
    }

    private int getMeasureHeightSize(boolean isChangeLine, int aSize) {
        if (isChangeLine) {

        }
        return 0;
    }

    private int getMeasureWidthSize(int aSize) {
        int size = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            if (i == 0) {
                size = child.getMeasuredWidth();
            } else {
                size = child.getMeasuredWidth() + layoutParams.leftMargin;
            }
        }
        return size;
    }

    /*Left position, relative to parent
     * @param t Top position, relative to parent
     * @param r Right position, relative to parent
     * @param b Bottom position, relative to parent*/


}
