package com.electrocardio.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.base.ProfileActivity;
import com.electrocardio.custom.elec.SelectYearOrMonthPopupWindow;
import com.electrocardio.fragment.activity.ContinuousFragment;
import com.electrocardio.fragment.activity.SecondFragment;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by yangzheng on 2016/2/22.
 */
public class DistoryContentActivity extends ProfileActivity implements View.OnClickListener {

    private RelativeLayout rl_date;// 切换年月的RelativeLayout
    private TextView tv_year;// 年标签
    private TextView tv_month;// 月标签
    private SelectYearOrMonthPopupWindow selYeOrMonPopWin;// 切换年和月的popupWindow
    private YearAndMonthChanged mYearAndMonthChanged;
    private ContinuousFragment continuousFragment;// 连续测量记录
    private SecondFragment secondFragment;// 60秒测量记录
    private int currentYear;// 年
    private int currentMonth;// 月

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            changeFragment(new ContinuousFragment());
        }
    }

    /**
     * 获取年份
     *
     * @return
     */
    public int getYear() {
        return currentYear;
    }

    /**
     * 获取月份
     *
     * @return
     */
    public int getMonth() {
        return currentMonth;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_leftmenu;
    }

    @Override
    protected void onInitialize() {
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        RadioButton mRadioButtonContinuous = (RadioButton) findViewById(R.id.radioContinuous);
        RadioButton mRadioButtonRadioMinute = (RadioButton) findViewById(R.id.radioMinute);
        rl_date = (RelativeLayout) findViewById(R.id.rl_date);
        tv_year = (TextView) findViewById(R.id.tv_year);
        tv_month = (TextView) findViewById(R.id.tv_month);
        continuousFragment = new ContinuousFragment();
        secondFragment = new SecondFragment();
        mRadioButtonContinuous.setOnClickListener(this);
        mRadioButtonRadioMinute.setOnClickListener(this);
        rl_date.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;
        tv_year.setText(currentYear + "年");
        tv_month.setText(currentMonth < 10 ? "0" + currentMonth + "月" : currentMonth + "月");
    }

    @Override
    protected boolean isHideActionBar() {
        return true;
    }

    @Override
    protected boolean isOpenActionBar() {
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radioContinuous:
                changeFragment(continuousFragment);
                break;
            case R.id.radioMinute:
                changeFragment(secondFragment);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.rl_date:
                showSelectYearOrMonthPopupWindow();
                break;
        }
    }

    /**
     * 展示选择年和月份的popupwindow
     */
    private void showSelectYearOrMonthPopupWindow() {
        ArrayList<String> yearArray = new ArrayList<>();
        ArrayList<String> monthArray = new ArrayList<>();
        int year = 2010;
        for (int i = 0; i < 20; i++) {
            yearArray.add(year + "");
            year++;
        }
        for (int i = 1; i < 13; i++) {
            if (i < 10)
                monthArray.add("0" + i);
            else
                monthArray.add(i + "");
        }
        selYeOrMonPopWin = new SelectYearOrMonthPopupWindow(this, yearArray, monthArray, 6, 5);
        selYeOrMonPopWin.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM |
                Gravity.CENTER_HORIZONTAL, 0, 0);
        selYeOrMonPopWin.setSelectListener(new SelectYearOrMonthPopupWindow.SelectListener() {
            @Override
            public void onSelectItem(String str) {
                if (mYearAndMonthChanged != null) {
                    String[] date = str.split(":");
                    tv_year.setText(date[0] + "年");
                    tv_month.setText(date[1] + "月");
                    currentYear = Integer.parseInt(date[0]);
                    currentMonth = Integer.parseInt(date[1]);
                    mYearAndMonthChanged.yearAndMonthChanged(new int[]{Integer.parseInt(date[0]),
                            Integer.parseInt(date[1])});
                }
            }
        });
    }

    /**
     * 切换Fragment
     *
     * @param targetFragment
     */
    private void changeFragment(Fragment targetFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FragmentLayout, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_enter, R.anim.activity_back_exit);
    }

    /**
     * 设置年和月变化的监听
     *
     * @param yearAndMonthChanged
     */
    public void setYearAndMonthChanged(YearAndMonthChanged yearAndMonthChanged) {
        mYearAndMonthChanged = yearAndMonthChanged;
    }

    public interface YearAndMonthChanged {
        public void yearAndMonthChanged(int[] array);
    }
}
