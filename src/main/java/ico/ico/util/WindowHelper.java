package ico.ico.util;

import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 关于窗口的工具类
 * 对顶部状态栏，底部导航栏，整体窗口显示状态进行一些设置和更改
 */
public class WindowHelper {

    Window mWin;

    public WindowHelper(Window win) {
        this.mWin = win;
    }

    //region 底部导航栏相关

    /** 设置底部导航栏背景透明 */
    public WindowHelper setNaviTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWin.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        return this;
    }

    /** 设置底部导航栏隐藏,这会覆盖导航栏透明的设置 */
    public WindowHelper setNaviHide() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = mWin.getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            WindowManager.LayoutParams params = mWin.getAttributes();
            params.systemUiVisibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            params.systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            mWin.setAttributes(params);
        }
        return this;
    }
    //endregion

    //region 顶部状态栏相关

    /** 设置顶部状态栏透明 */
    public WindowHelper setStatusTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWin.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        return this;
    }

    /**
     * 获取当前手机顶部状态栏的高度
     *
     * @return int
     */
    public int getStatusHeight() {
        int statusHeight = 0;
        Rect localRect = new Rect();
        mWin.getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = mWin.getDecorView().getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 获取当前手机顶部状态栏的高度
     *
     * @return
     */
    public int getStatusHeight1() {
        int resourceId = mWin.getDecorView().getResources().getIdentifier("status_bar_height", "dimen", "android");
        return mWin.getDecorView().getResources().getDimensionPixelSize(resourceId);
    }
    //endregion

    //region 窗口相关

    /** 设置窗口没有标题栏 */
    public WindowHelper setWindowNoTitle() {
        mWin.requestFeature(Window.FEATURE_NO_TITLE);
        return this;
    }

    /** 保持屏幕常量 */
    public WindowHelper setKeepScreenOn() {
        mWin.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return this;
    }

    /** 设置窗口全屏幕 */
    public WindowHelper setWindowFull() {
        mWin.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return this;
    }

    /** 设置窗口为沉浸模式,这将同时隐藏状态栏和导航栏 */
    public WindowHelper setWindowImmersive() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = mWin.getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            WindowManager.LayoutParams params = mWin.getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            mWin.setAttributes(params);
        }
        return this;
    }
    //endregion

    /** 开启硬件加速 */
    public void openHardwareAccelerated(){
        mWin.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
    }

    /** 关闭硬件加速 */
    public void closeHardwareAccelerated(){
        mWin.clearFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
    }
}