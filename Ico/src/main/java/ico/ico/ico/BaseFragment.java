package ico.ico.ico;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by admin on 2015/3/11.
 */
public abstract class BaseFragment extends Fragment {
    public BaseFragActivity mActivity;
    public BaseFragment mFragment;
    public View mContentView;
    public Handler mHandler = new Handler();
    private OnHideChangeListener onHideChangeListener;
    private boolean saveStateFlag = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFragment = this;
        mActivity = (BaseFragActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContentView == null || !isSaveStateFlag()) {
            onCreateView(inflater, container, savedInstanceState, isSaveStateFlag());
        }
        return mContentView;
    }

    public abstract void onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, boolean isSaveStateFlag);

    public View getContentView() {
        return mContentView;
    }

    public void setContentView(@LayoutRes int layoutResId) {
        mContentView = LayoutInflater.from(getContext()).inflate(layoutResId, null);
    }

    /**
     * 重写函数
     * 根据隐藏显示状态手动调用来模拟Activity启动、播放，暂停，停止的生命周期
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (onHideChangeListener != null) {
            onHideChangeListener.onHideChanged(hidden);
        }
        if (!hidden) {
            onStart();
            onResume();
        } else {
            onPause();
            onStop();
        }
    }

    @Override
    public void onDestroyView() {
        if (mContentView != null && mContentView.getParent() != null && isSaveStateFlag()) {
            ((ViewGroup) mContentView.getParent()).removeView(mContentView);
        }
        super.onDestroyView();
    }

    /**
     * 设置状态保存标记
     * 若改标记为true，则该碎片所表示的视图在onDestoryView函数中不会被销毁，在下一次onCreateView中可以复用
     * 默认为true
     *
     * @return
     */
    public boolean isSaveStateFlag() {
        return saveStateFlag;
    }

    /**
     * 设置状态保存标记
     * 若改标记为true，则该碎片所表示的视图在onDestoryView函数中不会被销毁，在下一次onCreateView中可以复用
     * 默认为true
     *
     * @param saveStateFlag
     */
    public void setSaveStateFlag(boolean saveStateFlag) {
        this.saveStateFlag = saveStateFlag;
    }

    /**
     * 在contentView视图中，根据ID获取组件
     *
     * @param id
     * @return
     */
    public View findViewById(int id) {
        return mContentView.findViewById(id);
    }

    //region

    /**
     * 弹出土司
     */
    public void showToast(@StringRes final int stringResId) {
        mActivity.showToast(stringResId);
    }

    /**
     * 弹出土司
     */
    public void showToast(final CharSequence text) {
        mActivity.showToast(text);
    }

    /**
     * 弹出土司
     */
    public void showToasts(@StringRes final int stringResId) {
        mActivity.showToasts(stringResId);
    }

    /**
     * 弹出土司
     */
    public void showToasts(final CharSequence text) {
        mActivity.showToasts(text);
    }

    /**
     * 关闭当前对话框，显示输入参数所表示的对话框
     *
     * @param _dialog
     */
    public void showDialog(Dialog _dialog) {
        mActivity.showDialog(_dialog);
    }

    /**
     * 关闭对话框
     */
    public void dismissDialog() {
        mActivity.dismissDialog();
    }

    /**
     * 关闭当前对话框，显示输入参数所表示的对话框
     *
     * @param _dialog
     */
    public void showDialog(Dialog _dialog, String key) {
        mActivity.showDialog(_dialog, key);
    }

    /**
     * 关闭对话框
     */
    public void dismissDialog(String key) {
        mActivity.dismissDialog(key);
    }

    /**
     * 关闭全部的对话框
     */
    public void dismissDialogs() {
        mActivity.dismissDialogs();
    }

    /**
     * 显示对话框碎片
     *
     * @param dialogFragment
     */
    public void showDialogFrag(DialogFragment dialogFragment, String tag) {
        mActivity.showDialogFrag(dialogFragment, tag, getChildFragmentManager());
    }

    /**
     * 关闭对话框碎片
     */
    public void dismissDialogFrag() {
        mActivity.dismissDialogFrag();
    }

    public BaseFragment setOnHideChangeListener(OnHideChangeListener onHideChangeListener) {
        this.onHideChangeListener = onHideChangeListener;
        return this;
    }

    public interface OnHideChangeListener {
        void onHideChanged(boolean isHide);
    }

    //endregion
}