package com.electrocardio.popup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/04/01.
 */
public class StopMeasureAlertPop extends Dialog {

    private TextView alertContent;// 提示内容
    private TextView cancel;// 取消按钮
    private TextView confirm;// 确定按钮
    private String mContent;// 提示内容
    private ConfirmClickedListener mConfirmClickedListener;// 确认按钮的监听事件

    public StopMeasureAlertPop(Context context) {
        this(context, R.style.RemindDialogStyle);
    }

    public StopMeasureAlertPop(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_stopmeasurealert);
        alertContent = (TextView) findViewById(R.id.alertContent);
        cancel = (TextView) findViewById(R.id.cancel);
        confirm = (TextView) findViewById(R.id.confirm);
        // 取消按钮的点击事件
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // 确定按钮的点击事件
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConfirmClickedListener != null)
                    mConfirmClickedListener.onConfirmClicked();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        alertContent.setText(mContent);
    }

    /**
     * 设置警告内容
     *
     * @param content
     */
    public void setAlertContent(String content) {
        mContent = content;
    }

    /**
     * 设置确定按钮的点击事件
     *
     * @param confirmClickedListener
     */
    public void setConfirmClickedListener(ConfirmClickedListener confirmClickedListener) {
        mConfirmClickedListener = confirmClickedListener;
    }

    public interface ConfirmClickedListener {
        public void onConfirmClicked();
    }

}
