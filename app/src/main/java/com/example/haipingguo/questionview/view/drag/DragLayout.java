package com.example.haipingguo.questionview.view.drag;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.haipingguo.questionview.R;
import com.example.haipingguo.questionview.view.dra.Cacher;

import java.util.ArrayList;
import java.util.List;

public class DragLayout extends RelativeLayout {

    private Context mContext;
    private DragWindowHelper mWindowHelper;
    private AutoChangLineLayout mChangLineLayout;
    private List<String> mDataList=new ArrayList<>();
    private int mItemLayoutId;

    public DragLayout(Context context) {
        this(context,null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("ghpppp","DragLayout");
        inflate(context, R.layout.drag_layout, this);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DragLayout);
            mItemLayoutId = a.getResourceId(R.styleable.DragLayout_dragLayout_item_layout, 0);
            if (0 == mItemLayoutId) {
                throw new ExceptionInInitializerError(getResources().getString(R.string.spell_test_view_no_item_layout_error));
            }
            a.recycle();
        }
        mContext=context;
        mWindowHelper=new DragWindowHelper(mContext);
    }

    //设置数据
    public void setData(List<String> data) {
        if (data==null || data.size()==0) {
            return;
        }
        if(mDataList.size()>0){
            mDataList.clear();
        }
        mDataList.addAll(data);
        for(int i=0;i<data.size();i++) {
            addItem(i);
        }
    }

    public void addItem(int index){
        TextView textView = new TextView(mContext);
        textView.setText(mDataList.get(index));
        mChangLineLayout.addView(textView, index);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mChangLineLayout= findViewById(R.id.auto_disorder_view);
        mWindowHelper.showDragView();
    }
}
