package com.example.haipingguo.questionview.view.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

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
        int aHeight = 0, aWidth = 0;
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); ++i) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            //换行
            if (totalWidth + childWidth > sizeWidth) {
                aHeight += totalHeight;
                aWidth = Math.max(totalWidth, sizeWidth);
                totalWidth = 0;
                totalHeight = 0;
            }
            totalWidth += childWidth;
            totalHeight = Math.max(totalHeight, childHeight);
        }
        aHeight+=totalHeight;
        aWidth=Math.max(aWidth,totalWidth);
        int width = modeWidth == MeasureSpec.AT_MOST ? aWidth : sizeWidth;
        int height = modeHeight == MeasureSpec.AT_MOST ? aHeight: sizeHeight;
        setMeasuredDimension(width, height);
    }

    //需要知道第几行，第一行有多少view
    List<List<View>> listView = new ArrayList<>();
    List<Integer> lineHeightList = new ArrayList<>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int totalWidth = 0;
        int totalHeight = 0;
        int width = getMeasuredWidth();
        int aHeight = 0;
        int left = 0, top = 0, right = 0;
        List<View> lineListView = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            //换行
            if (totalWidth + childWidth > width) {
                aHeight += totalHeight;
                lineHeightList.add(aHeight);
                listView.add(lineListView);
                lineListView = new ArrayList<>();
                totalWidth = 0;
                totalHeight = 0;
            } else {
                totalHeight = Math.max(totalHeight, childHeight);
            }
            totalWidth += childWidth;
            lineListView.add(child);
        }
        listView.add(lineListView);
        for (int i = 0; i < listView.size(); i++) {
            List<View> views = listView.get(i);
            left = 0;
            right = 0;
            for (int j = 0; j < views.size(); j++) {
                View childView = views.get(j);
                MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                int lineHeight = i == 0 ? 0 : lineHeightList.get(i - 1);
                top = lp.topMargin + lineHeight;
                left = right + lp.leftMargin;
                right = left + childView.getMeasuredWidth();
                childView.layout(left, top, right,
                        top + childView.getMeasuredHeight());
            }
        }
    }
}
