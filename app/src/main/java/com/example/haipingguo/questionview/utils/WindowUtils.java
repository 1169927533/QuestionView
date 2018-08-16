package com.example.haipingguo.questionview.utils;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class WindowUtils {

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        if(context instanceof AppCompatActivity){
            ActionBar supportActionBar = ((AppCompatActivity) context).getSupportActionBar();
            if(supportActionBar!=null){
                result=+supportActionBar.getHeight()+result;
            }
        }
        return result;
    }

    /*public static int getWindowActionBarHeight(Activity activity) {
        if(activity instanceof AppCompatActivity){
            ((AppCompatActivity)activity).getActionBar();
            result=+.getHeight();
        }
    }*/
}
