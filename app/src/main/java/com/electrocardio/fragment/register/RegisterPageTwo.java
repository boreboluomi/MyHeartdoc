package com.electrocardio.fragment.register;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.activity.HomeActivity;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.custom.elec.SelectBirthdayPopupWindow;
import com.electrocardio.custom.elec.SelectSingleRowPopupWindow;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/2/22.
 */
public class RegisterPageTwo extends BaseFragment {
    private TextView tv_title;
    private TextView tv_left;
    private TextView tv_right;
    private CancelClick mCancelClick;// 取消按钮接口
    private RadioButton rb_man;
    private RadioButton rb_woman;
    private TextView et_birthday;
    private TextView et_height;
    private TextView et_weight;

    private SelectSingleRowPopupWindow selectSingleRowPopupWindow;// 身高
    private SelectSingleRowPopupWindow selectSingleRowPopupWindow2;// 体重
    private SelectBirthdayPopupWindow selectBirthdayPopupWindow;// 生日

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_registerpagetwo, null);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_left = (TextView) view.findViewById(R.id.tv_left);
        tv_right = (TextView) view.findViewById(R.id.tv_right);

        rb_man = (RadioButton) view.findViewById(R.id.rb_man);
        rb_woman = (RadioButton) view.findViewById(R.id.rb_woman);
        et_birthday = (TextView) view.findViewById(R.id.et_birthday);
        et_height = (TextView) view.findViewById(R.id.et_height);
        et_weight = (TextView) view.findViewById(R.id.et_weight);

        tv_title.setText("完善个人信息");
        tv_left.setVisibility(View.VISIBLE);
        tv_right.setText("提交");
        return view;
    }

    @Override
    public void initData() {
        super.initData();

        setOnButtonClick();
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClick() {
        // 取消按钮的点击事件
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCancelClick != null)
                    mCancelClick.onCancelClicked();
            }
        });

        // 提交按钮的点击事件
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, HomeActivity.class);
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                mActivity.finish();
            }
        });

        rb_man.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rb_woman.setChecked(false);
                }
            }
        });

        rb_woman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rb_man.setChecked(false);
                }
            }
        });

        et_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHeightPopWindow();
            }
        });

        et_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWeightPopWindow();
            }
        });

        et_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBirthdayPopWindow();
            }
        });
    }

    /**
     * 显示出生日期popWindow
     */
    private void showBirthdayPopWindow() {
        selectBirthdayPopupWindow = new SelectBirthdayPopupWindow(mActivity);
        selectBirthdayPopupWindow
                .setSelectListener(new SelectBirthdayPopupWindow.SelectListener() {
                    @Override
                    public void onSelectItem(String str) {
                        et_birthday.setText(str);
                    }
                });
        // 显示窗口
        selectBirthdayPopupWindow.showAtLocation(mActivity.getWindow()
                        .getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                0); // 设置layout在PopupWindow中显示的位置
    }

    /**
     * 显示体重popWindow
     */
    private void showWeightPopWindow() {
        // 实例化SelectPicPopupWindow
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 20; i < 250; i++) {
            arrayList.add(i + "kg");
        }
        selectSingleRowPopupWindow2 = new SelectSingleRowPopupWindow(mActivity,
                "体重设置", arrayList, 30);
        selectSingleRowPopupWindow2.setSelectListener(new SelectSingleRowPopupWindow.SelectListener() {
            @Override
            public void onSelectItem(String str) {
                et_weight.setText(str.subSequence(0, str.length() - 2));
            }
        });
        // 显示窗口
        selectSingleRowPopupWindow2.showAtLocation(mActivity.getWindow()
                        .getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                0); // 设置layout在PopupWindow中显示的位置
    }

    /**
     * 显示身高popWindow
     */
    private void showHeightPopWindow() {
        // 实例化SelectPicPopupWindow
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 40; i < 250; i++) {
            arrayList.add(i + "cm");
        }
        selectSingleRowPopupWindow = new SelectSingleRowPopupWindow(mActivity,
                "身高设置", arrayList, 120);
        selectSingleRowPopupWindow.setSelectListener(new SelectSingleRowPopupWindow.SelectListener() {
            @Override
            public void onSelectItem(String str) {
                et_height.setText(str.subSequence(0, str.length() - 2));
            }
        });
        // 显示窗口
        selectSingleRowPopupWindow.showAtLocation(mActivity.getWindow()
                        .getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                0); // 设置layout在PopupWindow中显示的位置
    }

    public void setCancelClick(CancelClick cancelClick) {
        mCancelClick = cancelClick;
    }

    public interface CancelClick {
         void onCancelClicked();
    }
}
