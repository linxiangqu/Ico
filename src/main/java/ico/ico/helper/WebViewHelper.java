package ico.ico.helper;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ico.ico.util.Common;
import ico.ico.util.log;

/**
 * WebView的帮助类
 * <p>
 * 可以用他对webview基本的初始化工作
 * <p>
 * {@link CommonWebChromeClient}实现了一些对于html中一些功能的安卓实现,如控制台打印,对话框弹出,选择图片功能
 * <p>
 * {@link CommonWebViewClient}实现对链接以TEL开头的电话拨打功能
 */
public class WebViewHelper {
    public static void init(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        //设置页面缩放
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        //设置支持js
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        //设置允许打开alert
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //允许h5获取地理位置
        webSettings.setGeolocationEnabled(true);

        //设置允许打开图片上传对话框
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    /**
     * 执行js函数
     *
     * @param webView webview对象
     * @param method  要执行的js函数名
     * @param params  要传入的参数列表
     */
    public static void executeJsMethod(WebView webView, String method, Object... params) {
        String param = "";
        for (int i = 0; i < params.length; i++) {
            if (!TextUtils.isEmpty(params[i].toString())) {
                if (i == 0) {
                    param += "'" + params[i].toString() + "'";
                } else {

                    param += ",'" + params[i].toString() + "'";
                }
            }
        }
        log.w("========" + String.format("javascript:%s(%s);", method, param));
        webView.loadUrl(String.format("javascript:%s(%s);", method, param));
    }


    /**
     * 加载asset中的html文件
     *
     * @param webView  webview对象
     * @param htmlName html文件的路径名,开头不用加/
     */
    public static void loadAssetsHtml(WebView webView, String htmlName) {
        webView.loadUrl(String.format("file:///android_asset/%s", htmlName));
    }

    /**
     * 加载sd卡中的html文件
     *
     * @param webView  webview对象
     * @param htmlName html文件的路径名,开头不用加/
     */
    public static void loadSDHtml(WebView webView, String htmlName) {
        webView.loadUrl(String.format("content:///com.android.htmlfileprovider/sdcard/%s", htmlName));
    }

    /**
     * 加载网络上的html文件
     *
     * @param webView webview对象
     * @param url     html文件的路径名,开头不用加/
     */
    public static void loadNetHtml(WebView webView, String url) {
        webView.loadUrl(url);
    }


    public static abstract class CommonWebChromeClient extends WebChromeClient {

        ValueCallback<Uri> mUploadMsg;
        ValueCallback<Uri[]> mUploadMsgs;

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            log.w(String.format("onJsAlert==url：%s；messgae：%s；", url, message));
            return false;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            log.d(cm.message() + " -- From line "
                    + cm.lineNumber() + " of "
                    + cm.sourceId());
            return true;
        }

        //For Android >=5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            log.w("onShowFileChooser==");
            mUploadMsgs = filePathCallback;
            onImageSelected();
            return true;
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            log.w("onShowFileChooser1==");
            mUploadMsg = uploadMsg;
            onImageSelected();
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            log.w("onShowFileChooser2==");
            mUploadMsg = uploadMsg;
            onImageSelected();
        }

        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            log.w("openFileChooser3==");
            mUploadMsg = uploadMsg;
            onImageSelected();
        }

        /**
         * 执行图片选择的具体逻辑操作
         * 操作成功后，调用mUploadMsg或者mUploadMsgs的onReceiveValue函数进行处理
         */
        public abstract void onImageSelected();
    }

    public static class CommonWebViewClient extends WebViewClient {

        Object mObject;


        public CommonWebViewClient(Object object) {
            this.mObject = object;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            log.w("shouldOverrideUrlLoading=" + request.toString());
            return super.shouldOverrideUrlLoading(view, request);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            log.w("shouldOverrideUrlLoading=" + url);
            if (url.toUpperCase().startsWith("TEL")) {
                if (mObject instanceof Activity) {
                    ((Activity) mObject).startActivity(Common.getIntentByDial(url.substring(4)));
                } else if (mObject instanceof Fragment) {
                    ((Fragment) mObject).startActivity(Common.getIntentByDial(url.substring(4)));
                }
                return true;
            }
            WebViewHelper.loadNetHtml(view, url);
            return super.shouldOverrideUrlLoading(view, url);
        }


    }
}
