package com.example.haipingguo.questionview.view.dra;

import java.util.List;

/**
 * Created by luoxuwei on 2017/9/23.
 */

public interface CourseExerciseView<T> {
    /**
     * @param exercise
     */
    void setTestData(List<String>  exercise);
    void showResult();
    void setEnable(boolean enable);
    List<T> getResult();
    void setResultListener(ExerciseViewListener listener);

    public interface ExerciseViewListener {
        void onAnswerChange();
    }
}
