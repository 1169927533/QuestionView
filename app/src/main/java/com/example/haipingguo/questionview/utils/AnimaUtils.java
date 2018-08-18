package com.example.haipingguo.questionview.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import com.example.haipingguo.questionview.view.ItemButtonView;
import com.example.haipingguo.questionview.view.bean.ModulePosition;
import com.example.haipingguo.questionview.view.bean.Position;

public class AnimaUtils {

    public static void moveToHotQuestion(Position startPosition,ModulePosition endPosition, final ItemButtonView itemView){
        final Position position1=endPosition.centerPosition;
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(itemView, "translationX", startPosition.x, position1.x-itemView.getWidth()/2);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(itemView, "translationY", startPosition.y, position1.y-itemView.getHeight()/2);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
        animatorSet.setDuration(400);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                itemView.clearAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    public static void moveToOriginal(Position startPosition,Position endPosition, final ItemButtonView itemView){
        ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(itemView, "translationX", startPosition.x, endPosition.x);
        ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(itemView, "translationY", startPosition.y, endPosition.y);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(objectAnimatorX,objectAnimatorY);
        animatorSet.setDuration(400);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                itemView.clearAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }
}
