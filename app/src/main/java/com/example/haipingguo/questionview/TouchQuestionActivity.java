package com.example.haipingguo.questionview;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.haipingguo.questionview.view.TouchMoveLayout;

import java.util.ArrayList;
import java.util.List;

/*AppCompatActivity*/
public class TouchQuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_question_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        TouchMoveLayout touchMoveLayout = findViewById(R.id.toucheMoveLayout);
        touchMoveLayout.initialData(getQuestionData(), getOptionData());
        touchMoveLayout.setOnTouchMoveListener(new TouchMoveLayout.OnTouchMoveListener() {
            @Override
            public void result(List<Integer> resultLis) {
            }
        });
    }

    public List<Integer> getQuestionData() {
        List<Integer> questionList = new ArrayList<>();
        questionList.add(1);
        questionList.add(2);
        questionList.add(3);
        return questionList;

    }

    public List<String> getOptionData() {
        List<String> optionList = new ArrayList<>();
        optionList.add("1");
        optionList.add("2");
        optionList.add("3");
        return optionList;
    }
}
