package com.example.haipingguo.questionview.view.dra;

import android.view.View;

/**
 * Created by luoxuwei on 2017/9/23.
 */

public interface DragLayoutAdapter<T> {
    public abstract void onBindData(View itemView, T data);
}
