package com.example.haipingguo.questionview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DragActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag_activity);
       /* SentenceSpellView sentenceSpellView = findViewById(R.id.drag_sentence_spellview);
        sentenceSpellView.setTestData(getOptionData());*/
    }

    public List<String> getOptionData() {
        List<String> optionList = new ArrayList<>();
        optionList.add("1");
        optionList.add("2");
        optionList.add("3");
        optionList.add("1");
        optionList.add("2");
        optionList.add("3");
        optionList.add("1");
        optionList.add("2");
        optionList.add("3");
        optionList.add("1");
        optionList.add("2");
        optionList.add("3");
        return optionList;
    }
}
