package com.example.haipingguo.questionview.view.dra;

import android.view.View;
import android.widget.TextView;


/**
 * Created by luoxuwei on 2017/9/25.
 */

public class SpellAdapter implements DragLayoutAdapter<DragLayout.DataBean> {
    @Override
    public void onBindData(View itemView, DragLayout.DataBean data) {
        ((TextView)itemView).setText(data.data);
        itemView.setTag(data);
    }
}
