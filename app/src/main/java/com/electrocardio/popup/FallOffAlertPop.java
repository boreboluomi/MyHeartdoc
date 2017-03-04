package com.electrocardio.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/04/01.
 */
public class FallOffAlertPop extends PopupWindow {

    public FallOffAlertPop(Context context) {
        this(context, null);
    }

    public FallOffAlertPop(Context context, AttributeSet attrs) {
        super(context, attrs);
        View mView = LayoutInflater.from(context).inflate(R.layout.popup_falloffalert, null);
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
}
