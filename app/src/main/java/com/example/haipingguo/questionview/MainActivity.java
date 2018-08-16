package com.example.haipingguo.questionview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        findViewById(R.id.touchquestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,TouchQuestionActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.locaton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder  builder=new AlertDialog.Builder(MainActivity.this);
                final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.location_dialog_view, null);
                final TextView locationWindowTv=view.findViewById(R.id.locationWindow);
                final TextView locationScreenTv=view.findViewById(R.id.locationScreen);
                builder.setView(view);
                builder.create().show();
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int[] locWindow=new int[2];
                        int[] locScreen=new int[2];
                        view.getLocationInWindow(locWindow);
                        view.getLocationOnScreen(locScreen);
                        locationWindowTv.setText("相对于窗口--x="+locWindow[0]+"---y="+locWindow[1]);
                        locationScreenTv.setText("相对于屏幕左上角--x="+locScreen[0]+"---y="+locScreen[1]);
                    }
                },500);
            }
        });
    }

}
