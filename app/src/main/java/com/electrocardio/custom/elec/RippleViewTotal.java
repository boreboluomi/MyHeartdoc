package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/3/12.
 */
public class RippleViewTotal extends RelativeLayout {

    private TextView tv_search;
    private TextView tv_connect;
    private ImageView imageView;
    private AnimationDrawable frame;
    private boolean SEARCHED = false;

    public RippleViewTotal(Context context) {
        super(context);
    }

    public RippleViewTotal(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.rippleviewtotal, this,
                true);
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_connect = (TextView) findViewById(R.id.tv_connect);
        imageView = (ImageView) findViewById(R.id.iv_rippleView);
    }

    public RippleViewTotal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFrameAnimation() {
        imageView.setImageResource(R.drawable.frameanimation);
        frame = (AnimationDrawable) imageView.getDrawable();
        frame.start();
    }

    public void closeFrameAnimation() {
        SEARCHED = false;
        if (frame != null)
            frame.stop();
        imageView.clearAnimation();
        frame = null;
    }

    /**
     * 正在搜索设备
     */
    public void Searching() {
        if (frame != null && frame.isRunning())
            frame.stop();
        tv_connect.setVisibility(View.GONE);
        tv_search.setText("正在搜索设备...");
        if (frame != null && !frame.isRunning())
            frame.start();
    }

    /**
     * 查找到设备，请连接设备
     */
    public void Searched() {
        if (!SEARCHED) {
            SEARCHED = true;
            if (frame != null && frame.isRunning())
                frame.stop();
            tv_search.setText("请连接设备");
            if (frame != null && !frame.isRunning())
                frame.start();
        }
    }

    /**
     * 正在连接设备
     */
    public void Connect() {
        if (frame != null && frame.isRunning())
            frame.stop();
        //tv_connect.setVisibility(View.GONE);
        tv_search.setText("正在连接设备");
        if (frame != null && !frame.isRunning())
            frame.start();
    }
}
