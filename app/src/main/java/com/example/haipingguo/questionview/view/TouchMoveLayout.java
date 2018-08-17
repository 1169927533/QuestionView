package com.example.haipingguo.questionview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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
import com.example.haipingguo.questionview.view.bean.ModulePosition;
import com.example.haipingguo.questionview.view.bean.Position;

import java.util.ArrayList;
import java.util.List;

public class TouchMoveLayout extends RelativeLayout implements ViewTreeObserver.OnGlobalLayoutListener {
    private Context mContext;
    public static int YOFFSETY = 0;
    public static int YOFFSETX = 0;
    private ModulePosition modulePosition;
    int ints[] = new int[2];
    private LinearLayout mQuestionLlyt, mOptionLlyt;
    private Paint paint;
    //题目热区集合
    private List<ModulePosition> HotQuestionList = new ArrayList<>();
    //选项集合
    private List<TouchMoveView> touchMoveViewList = new ArrayList<>();

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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mQuestionLlyt = findViewById(R.id.touch_question_llyt);
        mOptionLlyt = findViewById(R.id.touch_option_llyt);
        paint = new Paint();
        paint.setStrokeWidth(20);
        paint.setColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
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
        mQuestionLlyt.getViewTreeObserver().addOnGlobalLayoutListener(this);
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
            mOptionLlyt.addView(touchMoveView);
            touchMoveViewList.add(touchMoveView);
        }
    }

    @Override
    public void onGlobalLayout() {
        if (Build.VERSION.SDK_INT < 16) {
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        } else {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        initQuestionHotList();
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
            modulePosition = new ModulePosition(0 + 1,
                    new Position(questionLayoutLocation[0] , questionLayoutLocation[1]-YOFFSETY),
                    new Position(questionLayoutLocation[0] + questionItem.getWidth(),
                            questionLayoutLocation[1] + questionItem.getHeight()-YOFFSETY));
            int[] location = new int[2];
            resultText.getLocationOnScreen(location);
            location[0] = Math.abs(location[0]) % ScreenUtil.getScreenWidth(mContext);
            int xPos = location[0];
            int yPos = location[1];
            int centerX = xPos + (resultText.getWidth() / 2)-YOFFSETX;
            int centerY = yPos + (resultText.getHeight() / 2) -YOFFSETY;
            modulePosition.setCenterPosition(new Position(centerX, centerY));
            HotQuestionList.add(modulePosition);
        }

        for (TouchMoveView touchMoveView : touchMoveViewList) {
            touchMoveView.setCenterList(HotQuestionList);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ModulePosition modulePosition = HotQuestionList.get(1);
        Rect rect = new Rect((int)modulePosition.leftTop.x, (int)modulePosition.leftTop.y,
                (int)modulePosition.rightBottom.x, (int)modulePosition.rightBottom.y);

       /* canvas.drawPoint(ints[0],ints[1],paint);*/
        canvas.drawRect(rect,paint);
    }

    public void getOptionView(AppCompatTextView textView,int index) {
        textView.setBackgroundResource(R.drawable.live_common_touch_view_background);
        textView.setPadding(ScreenUtil.dp2px(mContext, 20), ScreenUtil.dp2px(mContext, 7),
                ScreenUtil.dp2px(mContext, 20), ScreenUtil.dp2px(mContext, 10));
        textView.setText(String.valueOf(index+1));
    }
}
