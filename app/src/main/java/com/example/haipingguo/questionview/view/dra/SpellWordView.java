package com.example.haipingguo.questionview.view.dra;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.example.haipingguo.questionview.R;
import com.example.haipingguo.questionview.utils.ScreenUtil;


/**
 * Created by luoxuwei on 2017/9/26.
 */

public class SpellWordView extends android.support.v7.widget.AppCompatTextView {
    private Bitmap mBackground, mBubble;
    private int mWidth;
    private int mHeight;
    private String mText;
    private Context mContext;
    public SpellWordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpellWordView(Context context) {
        super(context);
        init(context, null);
    }

    public SpellWordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        mContext=context;
        mBubble = BitmapFactory.decodeResource(context.getResources(), R.mipmap.live_common_view_question_peail);
        mHeight = mBubble.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        if (!TextUtils.isEmpty(getText()) && !TextUtils.equals(mText, getText())) {
            mText = getText().toString();
            mWidth=0;
        }
        if (mWidth!=0) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mWidth==0) {
            recalculateWidth();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void recalculateWidth() {
        if (mBackground!=null) {
            setBackgroundDrawable(null);
            mBackground.recycle();
            mBackground=null;
        }
        double width;
        int btmapWidth;
        width = getMeasuredWidth();
        btmapWidth = mBubble.getWidth();
        Paint mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);


        //1个 宽度90 应90 实际90 90*1-(1-1)*10
        //2个 宽度90 应180 ,实170 90*2-((2-1)*10);
        //3个 宽度90 应270 ,实250 90*3-(3-1)*30
        float xOffset = btmapWidth - ScreenUtil.dp2px(mContext,10);;

        int num = (int) ((width+xOffset-1) / xOffset);
//        Log.e("houxiukai_11", (bm.getWidth() * num - ((num - 1) * ScreenUtil.dp2px(context, 10))) + "");
        //宽度
        mWidth = (int) (btmapWidth * num - ((num - 1) * ScreenUtil.dp2px(mContext,10)));
//        view.setWidth(i1);
        mBackground = Bitmap.createBitmap(mWidth, (int) mHeight, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < num; i++) {
            Canvas canvas = new Canvas(mBackground);
            canvas.drawBitmap(mBubble, mWidth-((i+1)*btmapWidth-i * ScreenUtil.dp2px(mContext,10)), 0, null);
        }
        setBackgroundDrawable(new BitmapDrawable(mBackground));
    }


}
