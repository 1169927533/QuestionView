package com.example.haipingguo.questionview.view.dra;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.haipingguo.questionview.R;
import com.example.haipingguo.questionview.utils.ScreenUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by luoxuwei on 2017/9/20.
 */
//TODO:将view和数据绑定相关的代码抽离成一个adapter
public class DragLayout extends RelativeLayout {

    public static final int DRAG_STATE_IDLE = 1;

    public static final int DRAG_STATE_DRAGGING = 2;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DRAG_STATE_IDLE, DRAG_STATE_DRAGGING})
    public @interface DragState {
    }

    private boolean mDispatchToAlertWindow;
    private final int[] mTempLocation = new int[2];

    private CheckForDrag mCheckForDrag;
    private CheckForRelease mCheckForRelease;

    private volatile boolean mCancelled;

    private AlertWindowHelper mWindomHelper;

    private AutoBreakLayout mSourceView;
    private AutoBreakLayout mTargetView;
    private AutoBreakLayout mCurrentView;
    private View mDivide;

    private boolean mRequestedDisallowIntercept;
    private boolean mPendingDrag;

    private GestureDetectorCompat mGestureDetector;
    private volatile View mTouchChild;

    private DragLayoutAdapter mAdapter;

    @DragState
    int mDragState = DRAG_STATE_IDLE;

    private int mItemLayoutId;
    private DragListener mDragListener;
    boolean mDragable=true;


    private final Cacher<View,Void> mCacher = new Cacher<View, Void>() {
        @Override
        public View create(Void aVoid) {
            return LayoutInflater.from(DragLayout.this.getContext()).inflate(mItemLayoutId,
                    mCurrentView, false);
        }

        @Override
        public View obtain() {
            final View view = super.obtain();
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(ScreenUtil.dp2px(getContext(), 25),
                    ScreenUtil.dp2px(getContext(), 25),
                    ScreenUtil.dp2px(getContext(), 25),
                    ScreenUtil.dp2px(getContext(), 25));
            view.setLayoutParams(lp);
            if(view.getParent()!=null){
                return  obtain();
            }
            return view;
        }

        @Override
        protected void onRecycleSuccess(View view) {
            removeFromParent(view);
        }
    };

    private void removeFromParent(View child) {
        final ViewParent parent = child.getParent();
        if(parent !=null && parent instanceof ViewGroup){
            ((ViewGroup) parent).removeView(child);
        }
    }

    public void setData(List<String> data) {
        if (data==null || data.size()==0) {
            return;
        }
        for(int i=0;i<data.size();i++) {
            addItem(mSourceView, i, new DataBean(i, data.get(i)));
        }
    }

    public void setDragable(boolean dragable) {
        mDragable = dragable;
    }

    public void setAdapter(DragLayoutAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException();
        }
        mAdapter = adapter;
    }

    public void setDragListener(DragListener dragListener) {
        mDragListener = dragListener;
    }

    private final AlertWindowHelper.ICallback mWindowCallback = new AlertWindowHelper.ICallback() {
        @Override
        public void onCancel(View view, MotionEvent event) {
            releaseDragInternal();
            if(mDragListener!=null) {
                mDragListener.finishDrage();
            }
        }

        @Override
        public boolean onMove(View view, MotionEvent event) {
            return processOverlap(view, event);
        }
    };

    public View findTopChildUnder(ViewGroup parent, int x, int y) {
        final int childCount = parent.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = parent.getChildAt(i);
            if (ViewUtils.isViewIntersect(child, x, y))
                return child;
        }
        return null;
    }

    private boolean isViewUnderInScreen(View view, int x, int y, boolean log) {
        if (view == null) {
            return false;
        }
        int w = view.getWidth();
        int h = view.getHeight();
        view.getLocationOnScreen(mTempLocation);
        int viewX = mTempLocation[0];
        int viewY = mTempLocation[1];
        return x >= viewX && x < viewX + w
                && y >= viewY && y < viewY + h;
    }

    public void hideDivideLine() {
        mDivide.setVisibility(GONE);
    }

    /**
     * 根据指定的view,处理重叠事件
     *
     * @param view the target view
     * @return true 如果处理重叠成功。
     */
    private boolean processOverlap(View view, MotionEvent event) {
        final List<AutoBreakLayout.Item> mItems = mCurrentView.getItems();
        AutoBreakLayout.Item item = null;
        int centerX, centerY;
        boolean found = false;
        AutoBreakLayout tempView = getCurrentView(event);

        for (int i = 0, size = mItems.size(); i < size; i++) {
            item = mItems.get(i);
            item.view.getLocationOnScreen(mTempLocation);
            centerX = mTempLocation[0] + item.view.getWidth() / 2;
            centerY = mTempLocation[1] + item.view.getHeight() / 2;
            if (isViewUnderInScreen(view, centerX, centerY, false) && item != mCurrentView.getDragItem()) {
                /**
                 * Drag到target目标的center时，判断有没有已经hold item, 有的话，先删除旧的,
                 */
                found = true;
                break;
            }
        }
        if (found || (mCurrentView != tempView)) {
            //the really index to add
            int index = 0;

            if (mCurrentView != tempView) {
                index = tempView.getChildCount();
            } else {
                index = item.index;
            }
            AutoBreakLayout.Item dragItem = mCurrentView.getDragItem();
            // remove old
            if (tempView!=mCurrentView) {
                mCurrentView.removeView(mCurrentView.getDragItem().view);
                mCurrentView = tempView;
            } else {
                mCurrentView.removeView(dragItem.view);
            }

            View hold = createChildView(dragItem.view);
            hold.setVisibility(View.INVISIBLE);  //隐藏
            mCurrentView.addView(hold, index);
            mCurrentView.findDragItem(hold);
            //目前不需要，两边都是同样的view
//            setWindowViewByChild(mWindomHelper.getView(), mCurrentView.getDragItem().view);
        }
        return found;
    }

    public View createChildView(View child) {
        TextView view = (TextView) mCacher.obtain();
        mAdapter.onBindData(view, child.getTag());
        return view;
    }

    private void setWindowViewByChild(View target, View source) {
        mAdapter.onBindData(target, (DataBean) source.getTag());
    }

    private void releaseDragInternal() {
        if (mCurrentView.getDragItem() != null) {
            mCurrentView.getDragItem().view.setVisibility(View.VISIBLE);
        }
        mWindomHelper.releaseView();
        mDispatchToAlertWindow = false;
        mTouchChild = null;

        mRequestedDisallowIntercept = false;
    }

    public DragLayout(Context context) {
        super(context);
        init(context,null, 0);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DragLayout);
            mItemLayoutId = a.getResourceId(R.styleable.DragLayout_dragLayout_item_layout, 0);
            if (0 == mItemLayoutId) {
                throw new ExceptionInInitializerError(getResources().getString(R.string.spell_test_view_no_item_layout_error));
            }
            a.recycle();
        }
        mWindomHelper = new AlertWindowHelper(context);
        mGestureDetector = new GestureDetectorCompat(context, new GestureListenerImpl());
        mCacher.setMaxPoolSize(10);
        mCacher.prepare();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSourceView = (AutoBreakLayout) findViewById(R.id.rl_source_view);
        mTargetView = (AutoBreakLayout) findViewById(R.id.rl_target_view);
        mDivide = findViewById(R.id.spell_div);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mCheckForDrag);
        removeCallbacks(mCheckForRelease);
    }

    private void addItem(ViewGroup parent, int index, DataBean item) {
        if (index < -1) {
            throw new IllegalArgumentException("index can't < -1.");
        }
        final TextView view = (TextView) mCacher.obtain();
        mAdapter.onBindData(view, item);
        parent.addView(view, index);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mDragable) {
            return true;
        }
        try {
            if(mGestureDetector == null) {
                return super.onTouchEvent(event);
            }
            final boolean handled = mGestureDetector.onTouchEvent(event);
            mCancelled = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
            //解决ScrollView嵌套时，引起的事件冲突
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(mRequestedDisallowIntercept || mDragState != DRAG_STATE_IDLE);
            }
            if (mDispatchToAlertWindow) {
                mWindomHelper.getView().dispatchTouchEvent(event);
                if (mCancelled) {
                    mDispatchToAlertWindow = false;
                }
            }
            return handled;
        } catch (Exception e){
            return super.onTouchEvent(event);
        }

    }

    private void beginDragInternal(@DragState int state) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
            mRequestedDisallowIntercept = true;
        }
        checkForDrag(0, false);
    }

    private void checkForRelease() {
        if (mCheckForRelease == null) {
            mCheckForRelease = new CheckForRelease();
        }
        postDelayed(mCheckForRelease, 100);
    }

    private void checkForDrag(long delay, boolean checkRelease) {
        if (mCheckForDrag == null) {
            mCheckForDrag = new CheckForDrag();
        }
        postDelayed(mCheckForDrag, delay);
        if (checkRelease) {
            checkForRelease();
        }
    }

    private void beginDragImpl(View childView) {
        //impl
        childView.setVisibility(View.INVISIBLE);
        mDispatchToAlertWindow = true;
        mCurrentView.findDragItem(childView);
        childView.getLocationInWindow(mTempLocation);
        mWindomHelper.showView(childView, childView.getWidth(), childView.getHeight(), mTempLocation[0],
                mTempLocation[1], true, mWindowCallback);
        mDragState = DRAG_STATE_DRAGGING;
        if (mDragListener!=null) {
            mDragListener.beginDrage();
        }
    }

    private class CheckForDrag implements Runnable {
        @Override
        public void run() {
            if (mTouchChild != null) {
                beginDragImpl(mTouchChild);
            }
        }
    }

    private class CheckForRelease implements Runnable {
        @Override
        public void run() {
            if (mCancelled) {
                releaseDragInternal();
            }
        }
    }

    private AutoBreakLayout getCurrentView(MotionEvent e) {
        if (e.getY()<mDivide.getTop()) {
            return mTargetView;
        } else if (e.getY()>mDivide.getBottom()) {
            return mSourceView;
        } else {
            return mCurrentView;
        }
    }

    private class GestureListenerImpl extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!mDispatchToAlertWindow && !mPendingDrag && mDragState != DRAG_STATE_IDLE) {
                mPendingDrag = true;
                checkForDrag(0, false);
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            mPendingDrag = false;
            removeCallbacks(mCheckForDrag);
            mCurrentView = getCurrentView(e);
            if (mCurrentView == null) {
                return false;
            }
            mTouchChild = findTopChildUnder(mCurrentView, (int) e.getX()-mCurrentView.getLeft(), (int) e.getY()-mCurrentView.getTop());
            if (mTouchChild != null) {
                mWindomHelper.setTouchDownPosition((int) e.getRawX(), (int) e.getRawY());
                if (mDragState != DRAG_STATE_DRAGGING) {
                    sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED);
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    beginDragInternal(DRAG_STATE_DRAGGING);
                }
            }
            return mTouchChild != null;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            playSoundEffect(SoundEffectConstants.CLICK);
            if (mTouchChild!=null) {
                AutoBreakLayout target = (mCurrentView == mSourceView)?mTargetView:mSourceView;
                mCurrentView.removeView(mTouchChild);
                View hold = createChildView(mTouchChild);
                target.addView(hold, target.getChildCount());
                if (mDragListener!=null) {
                    mDragListener.finishDrage();
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }
    }

    static public class DataBean {
        int index;
        String data;

        public DataBean(int index, String data) {
            this.index = index;
            this.data = data;
        }
    }

    static public interface DragListener {
        void beginDrage();
        void finishDrage();
    }

}
