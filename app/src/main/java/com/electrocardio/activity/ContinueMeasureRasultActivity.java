package com.electrocardio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.electrocardio.R;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.fragment.activity.ContinuousFragment;
import com.electrocardio.fragment.history.AbnormalListFragment;
import com.electrocardio.fragment.history.ContinueHisroryFragment;

/**
 * Created by ZhangBo on 2016/3/15.
 */
public class ContinueMeasureRasultActivity extends FragmentActivity {

    private FragmentManager fragmentManager;
    private ContinueHisroryFragment contiHistoryFrag;// 连续测量历史记录界面
    private AbnormalListFragment abnormalListFragment;// 异常信息界面
    private long timeStamp = -1;// 时间戳
    private long timeLength = -1;// 测量时长
    private int sign = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continuemeasureresult);

        Intent intent = getIntent();
        sign = intent.getIntExtra("sign", ContinuousFragment.HISTORY);
        timeStamp = intent.getLongExtra("timeStamp", -1);
        timeLength = intent.getLongExtra("timeLength", -1);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        fragmentManager = getSupportFragmentManager();
        contiHistoryFrag = new ContinueHisroryFragment();
        abnormalListFragment = new AbnormalListFragment();
        contiHistoryFrag.setAbnormalListClickListener(new ContinueHisroryFragment.AbnormalListClickListener() {
            @Override
            public void abnormalListClicked() {
                switchFragment(abnormalListFragment);
            }
        });
        abnormalListFragment.setOnBackClickListener(new AbnormalListFragment.OnBackClickListener() {
            @Override
            public void backButtonClicked() {
                if (sign == ContinuousFragment.HISTORY)
                    switchFragment(contiHistoryFrag);
                else
                    onBackPressed();
            }
        });
        if (sign == ContinuousFragment.HISTORY)
            switchFragment(contiHistoryFrag);
        else if (timeStamp == -1)
            switchFragment(contiHistoryFrag);
        else
            switchFragment(abnormalListFragment);
        switchFragment(sign == ContinuousFragment.HISTORY ? contiHistoryFrag : abnormalListFragment);
    }

    /**
     * 切换Fragment
     *
     * @param fragment
     */
    public void switchFragment(BaseFragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_container, fragment);
        fragmentTransaction.commit();
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * 获取测量时长
     *
     * @return
     */
    public long getTimeLength() {
        return timeLength;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_enter, R.anim.activity_back_exit);
    }
}
