package com.electrocardio.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.electrocardio.R;
import com.electrocardio.base.AbstractActivity;
import com.electrocardio.fragment.api.ResponseData;

/**
 * Created by yangzheng on 2016/1/4.
 */
public abstract class AbsBaseFragment extends Fragment {
    //fragment 展示状态
    public static final int FRAGMENT_STATUS_LOADING = 0XFF01;//加载
    private int mFragmentStatus = FRAGMENT_STATUS_LOADING;
    public static final int FRAGMENT_STATUS_EMPTY = 0XFF02;//空数据
    public static final int FRAGMENT_STATUS_SUCCESS = 0XFF03;//加载数据成功
    public static final int FRAGMENT_STATUS_ERROR = 0XFF04;//加载数据出错

    public static final int FORCE_LONG_OUT = 800; //强制用户登出
    public static final int REFUSE_TYPE_REFUSE = 8000;//拒绝用户使用
    public static final int REFUSE_TYPE_ABNORMAL = 8001;//拒绝用户非正常使用
    public static final int PWD_CHANGED = 888;//密码修改
    private View mRootView, mContentView;
    private ViewAnimator mViewAnimator;

    public void setFragmentStatus(int fragmentStatus) {
        if (mViewAnimator == null) return;
        int childCount = mViewAnimator.getChildCount();
        mFragmentStatus = fragmentStatus;
        switch (fragmentStatus) {
            case FRAGMENT_STATUS_LOADING:
                if (childCount == 0) return;
                mViewAnimator.setDisplayedChild(0);
                break;
            case FRAGMENT_STATUS_SUCCESS:
                if (childCount < 1) return;
                mViewAnimator.setDisplayedChild(1);
                break;
            case FRAGMENT_STATUS_EMPTY:
                if (childCount < 2) return;
                mViewAnimator.setDisplayedChild(2);
                break;
            case FRAGMENT_STATUS_ERROR:
                if (childCount < 3) return;
                mViewAnimator.setDisplayedChild(3);
                break;
        }
    }

    protected boolean defaultErrorHandle(int error, String msg) {
        if (error == 0) {
            setFragmentStatus(FRAGMENT_STATUS_SUCCESS);
            return true;
        }
        if (msg == null) {
            return false;
        }
        switch (error) {
            case FORCE_LONG_OUT:
                return true;
            case REFUSE_TYPE_REFUSE:
                return true;
            case REFUSE_TYPE_ABNORMAL:
                return true;
            default:
                setFragmentStatus(FRAGMENT_STATUS_ERROR);
                TextView tvError = (TextView) mViewAnimator.findViewById(R.id.error);
                if (tvError != null) {
                    tvError.setText(msg);
                }
                return true;
        }
    }

    /**
     * 默认错误处理
     *
     * @param response
     * @return
     */
    protected boolean defaultErrorHandle(ResponseData response) {
        int error = response.errorCode;
        String msg = response.msg;
        return defaultErrorHandle(error, msg);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_animator, container, false);
        mViewAnimator = (ViewAnimator) mRootView.findViewById(R.id.va_view_animator);
        //loading布局初始化
        View view = inflater.inflate(getLoadingLayout(), null, false);
        mViewAnimator.addView(view);

        //内容布局
        mContentView = inflater.inflate(getContentLayout(), null, false);
        onContentLayoutInit(mContentView);
        mViewAnimator.addView(mContentView);

        //错误布局初始化
        view = inflater.inflate(getErrorLayout(), null, false);
        onErrorLayoutInit(view);
        mViewAnimator.addView(view);

        return mRootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init(savedInstanceState, mContentView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInVisible();
        }
    }

    protected abstract void init(Bundle savedInstanceState, View contentView);

    protected void onRefresh() {
        setFragmentStatus(AbsBaseFragment.FRAGMENT_STATUS_LOADING);
    }

    public Context getContext() {
        return ((AbstractActivity) getActivity()).getContext();
    }

    public abstract void setFragmentType(int fragmentType);

    /**
     * 获取Fragment 当前展示状态
     *
     * @return
     */
    protected int getFragmentStatus() {
        return mFragmentStatus;
    }

    protected int getLoadingLayout() {
        return R.layout.fragment_state_loading;
    }

    protected void onLoadingLayoutInit(View view) {
    }

    protected int getContentLayout() {
        return R.layout.fragment_state_content;
    }

    protected void onContentLayoutInit(View view) {
    }

    protected int getErrorLayout() {
        return R.layout.fragment_state_error;
    }

    protected void onErrorLayoutInit(View view) {
        View errorView = view.findViewById(R.id.error_layout);
        if (errorView != null) {
            errorView.setOnClickListener(new ErrorClickListener());
        }
    }

    private class ErrorClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            onRefresh();
        }
    }

    /**
     * Fragment不可见时被调用
     */
    protected void onInVisible() {
    }

    /**
     * Fragment可见时被调用
     */
    protected void onVisible() {
    }
}
