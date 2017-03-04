package com.electrocardio.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/04/01.
 */
public class DisConnectPop extends PopupWindow {

    private View view;
    private TextView tv_disConnect;// 断开链接按钮
    private ClickedListener mClickedListener;// 点击事件的监听事件

    public DisConnectPop(Context context) {
        this(context, null);
    }

    public DisConnectPop(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(context).inflate(R.layout.popup_disconnect, null);
        tv_disConnect = (TextView) view.findViewById(R.id.tv_disConnect);
        // 设置view
        setContentView(view);
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
        // 设置断开连接的点击事件
        tv_disConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickedListener != null)
                    mClickedListener.onClickedListener();
                dismiss();
            }
        });
    }

    /**
     * 设置点击事件的监听器
     *
     * @param clickedListener
     */
    public void setClickedListener(ClickedListener clickedListener) {
        mClickedListener = clickedListener;
    }

    public interface ClickedListener {
        public void onClickedListener();
    }
}
