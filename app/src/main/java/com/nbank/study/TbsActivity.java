package com.nbank.study;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.tencent.smtt.sdk.TbsReaderView;
import com.tencent.smtt.sdk.ValueCallback;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ico.ico.ico.BaseFragActivity;
import ico.ico.util.Common;
import ico.ico.util.log;
import pub.devrel.easypermissions.EasyPermissions;

public class TbsActivity extends BaseFragActivity {

    Boolean init = false;
    Runnable waitTask;

    @BindView(R.id.layout_readview)
    RelativeLayout layoutReadview;
    @BindView(R.id.et_file)
    EditText et_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tbs);
        ButterKnife.bind(this);


        if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)) {
            EasyPermissions.requestPermissions(mActivity, "需要权限才可以读取本地文件哦", 0, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE);
        }

        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                log.w("onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
                log.w("onInstallFinish");
            }

            @Override
            public void onDownloadProgress(int i) {
                log.w("onDownloadProgress");
            }
        });

        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                log.w("onCoreInitFinished");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                log.w("onViewInitFinished:" + b + "|" + QbSdk.getTBSInstalling());
                if (!b) {
                    if (QbSdk.getTBSInstalling())
                        QbSdk.initX5Environment(TbsActivity.this, this);
                } else {
                    synchronized (init) {
                        init = true;
                        if (waitTask != null) {
                            mHandler.postDelayed(waitTask, 2000);
                        }
                    }
                }
            }
        });
    }


    @OnClick(R.id.btn_reset)
    public void reset() {
        et_file.setText(Environment.getExternalStorageDirectory() + "");
    }

    final String filePath = "/storage/emulated/0/tencent/MicroMsg/Download/陈方毅劳动合同.pdf";
//    final String filePath = "/storage/emulated/0/tencent/MicroMsg/Download/订餐服务.docx";

    /* 使用sdk打开本地文件，需要手机安装有QQ浏览器，然后加载pdf插件总是加载失败 */
    @OnClick(R.id.btn_pdf)
    public void onClickPDF() {
        if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)) {
            EasyPermissions.requestPermissions(mActivity, "需要权限才可以读取本地文件哦", 0, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE);
            return;
        }
        String filePath = obtainFilePath();
        if (TextUtils.isEmpty(filePath)) {
            mActivity.showToast("文件不存在");
            return;
        }

        Runnable _task = new Runnable() {
            @Override
            public void run() {
                log.w("onClickPDF Runnable is run");
                QbSdk.openFileReader(mActivity, TbsActivity.this.filePath, null, new ValueCallback<String>() {
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

    /** 应用内打开本地文件需要使用TbsReaderView，由于构造函数原因只能这code创建然后addView */
    TbsReaderView tbsReaderView;

    /** 应用内打开本地文件 */
    @OnClick(R.id.btn_pdf_inner)
    public void onClickPDFInner() {
        if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)) {
            EasyPermissions.requestPermissions(mActivity, "需要权限才可以读取本地文件哦", 0, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE);
            return;
        }
        String filePath = obtainFilePath();
        if (TextUtils.isEmpty(filePath)) {
            mActivity.showToast("文件不存在");
            return;
        }
        Runnable _task = new Runnable() {
            @Override
            public void run() {
                log.w("onClickPDFInner Runnable is run");
                //初始化控件
                if (tbsReaderView == null) {
                    TbsReaderView _tbsReaderView = new TbsReaderView(mActivity, new TbsReaderView.ReaderCallback() {
                        @Override
                        public void onCallBackAction(Integer integer, Object o, Object o1) {
                            log.w("onCallBackAction");
                        }
                    });
                    layoutReadview.addView(_tbsReaderView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    tbsReaderView = _tbsReaderView;
                }
                //构造数据包
                Bundle data = new Bundle();
                data.putString(TbsReaderView.KEY_FILE_PATH, filePath);
                data.putString(TbsReaderView.KEY_TEMP_PATH, Environment.getExternalStorageDirectory() + "/temp");
                //检查文件是否受到支持
                boolean flag = tbsReaderView.preOpen(Common.getSuffix(filePath), false);
                //如果支持则显示文件
                if (flag) tbsReaderView.openFile(data);
                //java.io.FileNotFoundException: /data/user/0/com.nbank.study/cache/optlist.ser (No such file or directory)
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

    /** 获取文本输入框中的文件路径 */
    private String obtainFilePath() {
        String _filePath = et_file.getText().toString();
        File file = new File(_filePath);
        if (!file.exists()) return null;
        return _filePath;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tbsReaderView != null) {
            tbsReaderView.onStop();
        }
    }
}
