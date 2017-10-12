package com.xiaodong.mywidget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xiaodong.mwidget.SuccessFaildProgress;


public class ProgreTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progre_test);
        SuccessFaildProgress progress = (SuccessFaildProgress) findViewById(R.id.pro);
//        progress.loadFaild();
    }
}
