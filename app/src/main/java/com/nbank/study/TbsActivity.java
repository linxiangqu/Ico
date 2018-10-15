package com.nbank.study;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tencent.smtt.sdk.QbSdk;

public class TbsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tbs);

        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {
                if (!b) {
                    QbSdk.initX5Environment(TbsActivity.this, this);
                }
            }
        });
    }
}
