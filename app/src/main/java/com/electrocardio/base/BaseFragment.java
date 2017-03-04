package com.electrocardio.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 基础的Fragment类 Create By ZhangBo 2016-2-22
 */
public abstract class BaseFragment extends Fragment {
    public Activity mActivity;// 上下文对象

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 初始化数据，自雷覆盖此方法，来实现自己的数据初始化
     */
    public void initData() {
    }

    /**
     * 初始化Fragment布局
     *
     * @return
     */
    public abstract View initView();
}
