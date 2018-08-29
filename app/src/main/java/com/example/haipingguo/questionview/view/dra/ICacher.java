package com.example.haipingguo.questionview.view.dra;

/**
 * Created by luoxuwei on 2017/9/19.
 */

public interface ICacher<T, P> {

    void prepare(P p);

    T obtain(P p);

    void clear();

    T create(P p);

    void recycle(T t);

}

