package com.electrocardio.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.electrocardio.R;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.fragment.cleardata.AutoClearFragment;
import com.electrocardio.fragment.cleardata.HandleClearFragment;

/**
 * Created by ZhangBo on 2016/2/24.
 */
public class ClearDataActivity extends FragmentActivity {

    private FragmentManager fm;
    private AutoClearFragment autoClear;// 自动清理界面
    private HandleClearFragment handClear;// 手动清理界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleardata);

        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        fm = getSupportFragmentManager();
        autoClear = new AutoClearFragment();
        handClear = new HandleClearFragment();
        autoClear.setOnHandClearClick(new AutoClearFragment.HandClearClick() {
            @Override
            public void onHandCliearClicked() {
                switchFragment(handClear);
            }
        });
        handClear.setOnBackClickListener(new HandleClearFragment.BackClickListener() {
            @Override
            public void onBackClicked() {
                switchFragment(autoClear);
            }
        });
        switchFragment(autoClear);
    }

    /**
     * 切换Fragment
     *
     * @param baseFragment
     */
    private void switchFragment(BaseFragment baseFragment) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_container, baseFragment);
        ft.commit();
    }
}
