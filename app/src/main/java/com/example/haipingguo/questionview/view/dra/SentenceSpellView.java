package com.example.haipingguo.questionview.view.dra;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.haipingguo.questionview.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by luoxuwei on 2017/9/20.
 */
public class SentenceSpellView extends RelativeLayout implements CourseExerciseView<Integer>, DragLayout.DragListener {
    private TextView mSentenceZh;
    private DragLayout mSentenceView;
    private ViewGroup mTagetView;
    private ExerciseViewListener mListener;
    private RelativeLayout mResultFrame;

    public SentenceSpellView(Context context) {
        this(context,null);
    }

    public SentenceSpellView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SentenceSpellView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSentenceZh =  findViewById(R.id.tv_test_sentence);
        mSentenceView =  findViewById(R.id.rl_testing);
        mTagetView =  findViewById(R.id.rl_target_view);
        mResultFrame = findViewById(R.id.rl_test_result);
        mSentenceView.setDragListener(this);
        mSentenceView.setAdapter(new SpellAdapter());
    }

    @Override
    public void setTestData(List<String>  exercise) {
        mSentenceZh.setText("题目");
        mSentenceView.setData(exercise);
    }

    @Override
    public void showResult() {

    }

    @Override
    public void setEnable(boolean enable) {
        mSentenceView.setDragable(enable);
    }

    @Override
    public List<Integer> getResult() {
        int count = mTagetView.getChildCount();
        List<Integer> result = new LinkedList<>();
        for(int i=0; i<count; i++) {
            result.add(((DragLayout.DataBean)mTagetView.getChildAt(i).getTag()).index);
        }
        return result;
    }

    @Override
    public void setResultListener(ExerciseViewListener listener) {
        mListener = listener;
    }

    @Override
    public void beginDrage() {
        if(mResultFrame.isShown()) {
            mResultFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public void finishDrage() {
        if (mListener!=null) {
            mListener.onAnswerChange();
        }
    }

}
