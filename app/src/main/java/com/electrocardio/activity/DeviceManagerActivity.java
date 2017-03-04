package com.electrocardio.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.electrocardio.R;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.fragment.devicemanager.AdvanceDebugFragment;
import com.electrocardio.fragment.devicemanager.DeviceManagerFragment;

/**
 * Created by ZhangBo on 2016/2/23.设备管理界面
 */
public class DeviceManagerActivity extends FragmentActivity {

    private FragmentManager fragmentManager;// Fragment管理器
    private FragmentTransaction fragmentTransaction;
    private DeviceManagerFragment deviceManager;// 设备管理Fragment
    private AdvanceDebugFragment advanceDebug;//高级调试Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicemanager);
        fragmentManager = getSupportFragmentManager();

        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        if (deviceManager == null) {
            deviceManager = new DeviceManagerFragment();
            deviceManager.setHideViewEnableListener(new DeviceManagerFragment.HideViewEnableListener() {
                @Override
                public void hideViewEnable() {
                    if (advanceDebug == null) {
                        advanceDebug = new AdvanceDebugFragment();
                        advanceDebug.setOnBackClickedListener(new AdvanceDebugFragment.OnBackClickedListener() {
                            @Override
                            public void onBackClicked() {
                                switchFrament(deviceManager);
                            }
                        });
                    }
                    switchFrament(advanceDebug);
                }
            });
        }
        switchFrament(deviceManager);
    }

    /**
     * 切换Fragment
     *
     * @param fragment
     */
    private void switchFrament(BaseFragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_enter, R.anim.activity_back_exit);
    }
}
