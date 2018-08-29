package com.example.haipingguo.questionview.view.dra;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.haipingguo.questionview.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by luoxuwei on 2017/9/20.
 */
public class AutoBreakLayout extends ViewGroup {

    private static final String TAG = "AutoBreakLayout";

    private static final boolean sDebug = false;

    public static final int INVALID_INDXE = -1;

    private static final Comparator<Item> sComparator = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return compareImpl(lhs.index, rhs.index);
        }

        public int compareImpl(int lhs, int rhs) {
            return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        }
    };

    private int mMaxLine = Integer.MAX_VALUE;
    private int mHorizontalSpace;
    private int mVerticalSpace;

    public AutoBreakLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoBreakLayout(Context context) {
        super(context);
        init(context, null);
    }

    public AutoBreakLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public AutoBreakLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoBreakLayout);
            this.mMaxLine = a.getInt(R.styleable.AutoBreakLayout_autoBreakLayout_maxLine, Integer.MAX_VALUE);
            this.mHorizontalSpace = a.getDimensionPixelSize(R.styleable.AutoBreakLayout_autoBreakLayout_horizontal_space, 0);
            this.mVerticalSpace = a.getDimensionPixelSize(R.styleable.AutoBreakLayout_autoBreakLayout_vertical_space, 0);
            a.recycle();
        }
    }

    /**
     * 负责设置子控件的测量模式和大小 根据所有子控件设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // 如果是warp_content情况下，记录宽和高
        int width = 0;
        int height = 0;
        /**
         * 记录每一行的宽度，width不断取最大宽度
         */
        int lineWidth = 0;
        /**
         * 每一行的高度，累加至height
         */
        int lineHeight = 0;

        int cCount = getChildCount();

        int rows = 1;
        // 遍历每个子元素
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            // 测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 当前子空间实际占据的宽度
            int childWidth = 0;
            if (i == 0) {
                childWidth = child.getMeasuredWidth();
            } else {
                childWidth = child.getMeasuredWidth() + mHorizontalSpace;
            }
            // 当前子空间实际占据的高度
            int childHeight = child.getMeasuredHeight();

            /**
             * 如果加入当前child，则超出最大宽度，则的到目前最大宽度给width，类加height 然后开启新行
             */
            if (lineWidth + childWidth > sizeWidth) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
                break;
            } else {
                //否则累加值lineWidth,lineHeight取最大高度
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);//5
            }
            // 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
            if (i == cCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }
        setMeasuredDimension(
                (modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width,
                (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);

    }

    /**
     * 存储所有的View，按行记录
     */
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    /**
     * 记录每一行的最大高度
     */
    private List<Integer> mLineHeights = new ArrayList<Integer>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeights.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;
        // 存储每一行所有的childView
        List<View> lineViews = new ArrayList<View>();
        int cCount = getChildCount();

        // 遍历所有的孩子
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            // 当前子空间实际占据的宽度
            int childWidth = 0;
            if (i == 0) {
                childWidth = child.getMeasuredWidth();
            } else {
                childWidth = child.getMeasuredWidth() + mHorizontalSpace;
            }
            // 当前子空间实际占据的高度
            int childHeight = child.getMeasuredHeight();

            // 如果已经需要换行
            if (childWidth + lineWidth > width) {
                childHeight += mVerticalSpace;

                // 记录这一行所有的View以及最大高度
                mLineHeights.add(lineHeight);
                // 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
                mAllViews.add(lineViews);
                lineWidth = 0;// 重置行宽
                lineViews = new ArrayList<View>();
            }
            /**
             * 如果不需要换行，则累加
             */
            lineWidth += childWidth;
            lineHeight = Math.max(lineHeight, childHeight);
            lineViews.add(child);
        }
        // 记录最后一行
        mLineHeights.add(lineHeight);
        mAllViews.add(lineViews);

        int left = 0;
        int top = 0;
        // 得到总行数
        int lineNums = mAllViews.size();
        for (int i = 0; i < lineNums; i++) {
            // 每一行的所有的views
            lineViews = mAllViews.get(i);
            // 当前行的最大高度
            lineHeight = mLineHeights.get(i);

            if (sDebug) {
                Log.e(TAG, "第" + i + "行 ：" + lineViews.size() + " , " + lineViews);
                Log.e(TAG, "第" + i + "行， ：" + lineHeight);
            }

            // 遍历当前行所有的View
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                //计算childView的left,top,right,bottom
                int lc = 0;
                if (j == 0) {
                    lc = left;
                } else {
                    lc = left + mHorizontalSpace;
                }
                int tc = 0;
                if (i == 0) {
                    tc = top;
                } else {
                    tc = top + mVerticalSpace;
                }
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                if (sDebug)
                    Log.e(TAG, child + " , l = " + lc + " , t = " + t + " , r ="
                            + rc + " , b = " + bc);

                child.layout(lc, tc, rc, bc);

                if (j == 0) {
                    left += child.getMeasuredWidth();
                } else {
                    left += child.getMeasuredWidth() + mHorizontalSpace;
                }
            }
            left = 0;
            top += lineHeight;
        }

    }

    private final InternalItemHelper mItemManager = new InternalItemHelper();

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        mItemManager.onAddView(child, index, params);
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
        mItemManager.onRemoveViewAt(index);
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        mItemManager.onRemoveView(view);
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        mItemManager.onRemoveAllViews();
    }

    public static class Item {
        public int index;
        public View view;

        @Override
        public String toString() {
            return "Item{" +
                    "index=" + index +
                    '}';
        }
    }

    public List<Item> getItems() {
        return mItemManager.mItems;
    }

    public Item getDragItem() {
        return mItemManager.mDragItem;
    }

    public void findDragItem(View view) {
        mItemManager.findDragItem(view);
    }

    private static class InternalItemHelper {
        final List<Item> mItems = new ArrayList<>();
        /**
         * 对应的拖拽item
         */
        Item mDragItem = null;

        public void onAddView(View child, int index, LayoutParams params) {
            index = index != -1 ? index : mItems.size();
            Item item;
            for (int i = 0, size = mItems.size(); i < size; i++) {
                item = mItems.get(i);
                if (item.index >= index) {
                    item.index++;
                }
            }
            //add
            item = new Item();
            item.index = index;
            item.view = child;
            mItems.add(item);
            Collections.sort(mItems, sComparator);
        }

        public void onRemoveViewAt(int index) {
            Item item;
            for (int i = 0, size = mItems.size(); i < size; i++) {
                item = mItems.get(i);
                if (item.index > index) {
                    item.index--;
                }
            }
            item = mItems.remove(index);
            Collections.sort(mItems, sComparator);
        }

        public void onRemoveView(View view) {
            Item item;
            int targetIndex = INVALID_INDXE;
            for (int i = 0, size = mItems.size(); i < size; i++) {
                item = mItems.get(i);
                if (item.view == view) {
                    targetIndex = item.index;
                    break;
                }
            }
            if (targetIndex == -1) {
                throw new IllegalStateException("caused by targetIndex == -1");
            }
            // -- index if need
            for (int i = 0, size = mItems.size(); i < size; i++) {
                item = mItems.get(i);
                if (item.index > targetIndex) {
                    item.index--;
                }
            }
            mItems.remove(targetIndex);
            Collections.sort(mItems, sComparator);
        }

        public void onRemoveAllViews() {
            mItems.clear();
        }

        public void findDragItem(View touchView) {
            Item item;
            for (int i = 0, size = mItems.size(); i < size; i++) {
                item = mItems.get(i);
                if (item.view == touchView) {
                    mDragItem = item;
                    break;
                }
            }
        }
    }
}
