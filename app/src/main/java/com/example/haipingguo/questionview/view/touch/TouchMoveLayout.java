package com.example.haipingguo.questionview.view.touch;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.haipingguo.questionview.R;
import com.example.haipingguo.questionview.utils.ScreenUtil;
import com.example.haipingguo.questionview.view.touch.bean.ModulePosition;
import com.example.haipingguo.questionview.view.touch.bean.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TouchMoveLayout extends RelativeLayout implements TouchMoveView.OnTouchMoveListener {
    private Context mContext;
    public static int YOFFSETY = 0;
    public static int YOFFSETX = 0;
    private OnTouchMoveListener mOnTouchMoveListener;
    int ints[] = new int[2];
    private LinearLayout mQuestionLlyt, mOptionLlyt;
    //题目热区集合
    private List<ModulePosition> HotQuestionList = new ArrayList<>();
    //选项位置集合
    private List<ModulePosition> mOptionList = new ArrayList<>();
    //选项集合
    private List<TouchMoveView> touchMoveViewList = new ArrayList<>();
    public static Map<Integer,ItemButtonView> resultMap=new HashMap<>();

    public TouchMoveLayout(Context context) {
        this(context, null);
    }

    public TouchMoveLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchMoveLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mContext = context;
        inflate(context, R.layout.touch_view_layout, this);
    }

    public void setOnTouchMoveListener(OnTouchMoveListener onTouchMoveListener){
        mOnTouchMoveListener=onTouchMoveListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mQuestionLlyt = findViewById(R.id.touch_question_llyt);
        mOptionLlyt = findViewById(R.id.touch_option_llyt);
    }

    /**
     * 初始化数据
     *
     * @param questionList 题目
     * @param options      选项
     */
    public void initialData(List<Integer> questionList, List<String> options) {
        initQuestionList(questionList);
        initOptionList(options);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                getLocationOnScreen(ints);
                YOFFSETY =ints[1];
                YOFFSETX=ints[0];
            }
        });

    }

    private void initQuestionList(List<Integer> questionList) {
        mQuestionLlyt.removeAllViews();
        for (int i = 0; i < questionList.size(); i++) {
            View questionLayout = View.inflate(mContext, R.layout.touch_question_view, null);
            mQuestionLlyt.addView(questionLayout);
        }
        //布局发生变化监听
        mQuestionLlyt.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                initQuestionHotList();
            }
        });
    }

    private void initOptionList(List<String> optionList) {
        mOptionLlyt.removeAllViews();
        for (int i = 0; i < optionList.size(); i++) {
            TouchMoveView touchMoveView= new TouchMoveView(mContext);
            touchMoveView.setParentLayout(this);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin=ScreenUtil.px2dp(mContext,20);
            layoutParams.rightMargin=ScreenUtil.px2dp(mContext,20);
            touchMoveView.setLayoutParams(layoutParams);
            getOptionView(touchMoveView,i);
            touchMoveView.setIndex(i);
            touchMoveView.setOnTouchMoveListener(this);
            mOptionLlyt.addView(touchMoveView);
            touchMoveViewList.add(touchMoveView);
        }
        mOptionLlyt.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                initOptionList();
            }
        });
    }

    private void initOptionList() {
        for (int i = 0; i < mOptionLlyt.getChildCount(); i++) {
            TouchMoveView optionItem = (TouchMoveView) mOptionLlyt.getChildAt(i);
            int[] location = new int[2];
            optionItem.getLocationOnScreen(location);
            ModulePosition modulePosition=new ModulePosition();
            modulePosition.setCenterPosition(new Position(location[0]-YOFFSETX+(optionItem.getWidth()/2),
                    location[1]-YOFFSETY+(optionItem.getHeight()/2)));
            mOptionList.add(modulePosition);
        }
        for (int i = 0; i < mOptionLlyt.getChildCount(); i++) {
            TouchMoveView optionItem = (TouchMoveView) mOptionLlyt.getChildAt(i);
            optionItem.setOptionList(mOptionList);
        }
    }

    /**
     * 获取到对应题目对应的热区范围
     */
    private void initQuestionHotList() {
        for (int i = 0; i < mQuestionLlyt.getChildCount(); i++) {
            View questionItem = mQuestionLlyt.getChildAt(i);
            View resultText = questionItem.findViewById(R.id.question_result_text);
            int[] questionLayoutLocation = new int[2];
            //以屏幕为原点questionItem的坐标
            questionItem.getLocationOnScreen(questionLayoutLocation);
            ModulePosition  modulePosition = new ModulePosition(i,
                    new Position(questionLayoutLocation[0] , questionLayoutLocation[1]-YOFFSETY),
                    new Position(questionLayoutLocation[0] + questionItem.getWidth(),
                            questionLayoutLocation[1] + questionItem.getHeight()-YOFFSETY));
            int[] location = new int[2];
            resultText.getLocationOnScreen(location);
            int xPos = location[0];
            int yPos = location[1];
            int centerX = xPos + (resultText.getWidth() / 2)-YOFFSETX;
            int centerY = yPos + (resultText.getHeight() / 2) -YOFFSETY;
            modulePosition.setCenterPosition(new Position(centerX, centerY));
            HotQuestionList.add(modulePosition);
        }

        for (TouchMoveView touchMoveView : touchMoveViewList) {
            touchMoveView.setHotList(HotQuestionList);
        }
    }

    public void getOptionView(AppCompatTextView textView,int index) {
        textView.setBackgroundResource(R.drawable.live_common_touch_view_background);
        textView.setPadding(ScreenUtil.dp2px(mContext, 20), ScreenUtil.dp2px(mContext, 7),
                ScreenUtil.dp2px(mContext, 20), ScreenUtil.dp2px(mContext, 10));
        textView.setText(String.valueOf(index+1));
    }

    @Override
    public void result(List<Integer> resultList) {
        mOnTouchMoveListener.result(resultList);
    }

    public interface OnTouchMoveListener {
       void result(List<Integer> resultList);
    }
}
