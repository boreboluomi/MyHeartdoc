package com.electrocardio.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/03/30.
 */
public class AdjustScopePop extends PopupWindow {

    private View mView;
    private int position = 1;
    private RadioButton rb_first;// 第一个单选框
    private RadioButton rb_second;// 第二个单选框
    private RadioButton rb_third;// 第三个单选框
    private RadioButton rb_forth;// 第四个单选框
    private SelectListener mSelectListener;// 选择按钮监听器

    public AdjustScopePop(Context context) {
        this(context, null);
    }

    public AdjustScopePop(Context context, AttributeSet attrs) {
        super(context, attrs);
        mView = LayoutInflater.from(context).inflate(R.layout.popup_adjustscope, null);
        rb_first = (RadioButton) mView.findViewById(R.id.rb_first);
        rb_second = (RadioButton) mView.findViewById(R.id.rb_second);
        rb_third = (RadioButton) mView.findViewById(R.id.rb_third);
        rb_forth = (RadioButton) mView.findViewById(R.id.rb_forth);
        // 设置view
        setContentView(mView);
        // 设置窗体弹出的宽
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置窗体弹出的高
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置窗体可以点击
        setFocusable(true);
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable drawable = new ColorDrawable(0x00000000);
        // 设置窗体的背景
        setBackgroundDrawable(drawable);

    }

    public void setCheckecItem(int position) {
        switch (position) {
            case 0:
                rb_first.setChecked(true);
                break;
            case 1:
                rb_second.setChecked(true);
                break;
            case 2:
                rb_third.setChecked(true);
                break;
            case 3:
                rb_forth.setChecked(true);
                break;
        }
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        setCheckecItem(position);
        rb_first.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mSelectListener != null)
                    mSelectListener.onSelectedItem(0);
                position = 0;
                dismiss();
            }
        });
        rb_second.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mSelectListener != null)
                    mSelectListener.onSelectedItem(1);
                position = 1;
                dismiss();
            }
        });
        rb_third.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mSelectListener != null)
                    mSelectListener.onSelectedItem(2);
                position = 2;
                dismiss();
            }
        });
        rb_forth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mSelectListener != null)
                    mSelectListener.onSelectedItem(3);
                position = 3;
                dismiss();
            }
        });
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        rb_first.setOnCheckedChangeListener(null);
        rb_second.setOnCheckedChangeListener(null);
        rb_third.setOnCheckedChangeListener(null);
        rb_forth.setOnCheckedChangeListener(null);
    }

    /**
     * 设置选择事件的监听者
     *
     * @param selectListener
     */
    public void setSelectListener(SelectListener selectListener) {
        mSelectListener = selectListener;
    }

    public interface SelectListener {
        public void onSelectedItem(int position);
    }

}