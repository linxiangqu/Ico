package com.nbank.study;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
    }

    public void onClickCamera(View v) {
        startActivity(new Intent(this, CameraActivity.class));
    }
    public void onClickTBS(View v) {
        startActivity(new Intent(this, TbsActivity.class));
    }
}
