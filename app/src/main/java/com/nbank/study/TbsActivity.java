package com.nbank.study;

import android.os.Bundle;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;

import butterknife.OnClick;
import ico.ico.ico.BaseFragActivity;
import ico.ico.util.log;

public class TbsActivity extends BaseFragActivity {

    Boolean init = false;
    Runnable waitTask;

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
                } else {
                    synchronized (init) {
                        init = true;
                        if (waitTask != null) {
                            mHandler.post(waitTask);
                        }
                    }
                }
            }
        });
    }

    @OnClick(R.id.btn_pdf)
    public void onClickPDF() {
        final String path = "/storage/emulated/0/tencent/MicroMsg/Download/陈方毅劳动合同.pdf";
        Runnable _task = new Runnable() {
            @Override
            public void run() {
                QbSdk.openFileReader(mActivity, path, null, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        log.w("onReceiveValue：" + s);
                    }
                });
            }
        };
        synchronized (init) {
            if (!init) {
                waitTask = _task;
            } else {
                mHandler.post(_task);
            }
        }
    }
}
