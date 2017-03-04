package com.electrocardio.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/04/05.
 */
public class LoginDataPop extends PopupWindow {

    private ImageView circle;// 图片
    private TextView content;// 内容
    private Animation animation;// 动画

    public LoginDataPop(Context context) {
        this(context, null);
    }

    public LoginDataPop(Context context, AttributeSet attrs) {
        super(context, attrs);
        View mView = LayoutInflater.from(context).inflate(R.layout.popup_logindata, null);
        // 设置view
        setContentView(mView);
        // 设置窗体弹出的宽
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置窗体弹出的高
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置窗体可以点击
        setFocusable(true);
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable drawable = new ColorDrawable(0x00000000);
        // 设置窗体的背景
        setBackgroundDrawable(drawable);

        circle = (ImageView) mView.findViewById(R.id.cicle);
        content = (TextView) mView.findViewById(R.id.content);
        animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(1000);
        animation.setRepeatCount(-1);
        animation.setFillAfter(true);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        circle.setAnimation(animation);
        animation.start();
    }

    /**
     * 设置提醒内容
     *
     * @param con
     */
    public void setAlertContent(String con) {
        content.setText(con);
    }

    @Override
    public void dismiss() {
        if (animation != null) {
            animation.cancel();
            circle.clearAnimation();
        }
        super.dismiss();
    }
}
