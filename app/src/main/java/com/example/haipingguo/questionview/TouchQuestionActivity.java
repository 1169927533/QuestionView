package com.example.haipingguo.questionview;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
        List<Integer> questionList=new ArrayList<>();
        questionList.add(1);
        questionList.add(2);
        questionList.add(3);
        List<String> optionList=new ArrayList<>();
        optionList.add("1");
        optionList.add("2");
        optionList.add("3");
        touchMoveLayout.initialData(questionList,optionList);
    }

}
