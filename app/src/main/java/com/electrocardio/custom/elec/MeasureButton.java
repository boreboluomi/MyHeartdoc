package com.electrocardio.custom.elec;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/03/31.
 */
public class MeasureButton extends FrameLayout {

    private int currentState = 0;// 当前按钮状态
    public static final int SCAN = 0;// 搜索蓝牙状态
    public static final int START = 1;// 开始测量状态
    public static final int STOP = 2;// 停止测量状态
    private Button btn_scan;// 搜索蓝牙按钮
    private Button btn_start;// 开始测量按钮
    private Button btn_stop;// 停止测量按钮

    private ClickedListener mClickedListener;// 点击时间监听

    public MeasureButton(Context context) {
        this(context, null);
    }

    public MeasureButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_measurebutton, this, true);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        // 浏览按钮的点击事件
        btn_scan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickedListener != null)
                    mClickedListener.scanClicked();
            }
        });
        // 开始测量按钮的点击事件
        btn_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickedListener != null)
                    mClickedListener.startClicked();
            }
        });
        // 停止测量按钮的点击事件
        btn_stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickedListener != null)
                    mClickedListener.stopClicked();
            }
        });
    }

    /**
     * 获取当前按钮的状态
     *
     * @return
     */
    public int getCurrentBtnState() {
        return currentState;
    }

    /**
     * 设置当前状态
     *
     * @param state
     */
    public void setCurrentState(int state) {
        currentState = state;
        switch (state) {
            case SCAN:
                setScanState(true);
                setStartState(false);
                setStopState(false);
                break;
            case START:
                setStartState(true);
                setScanState(false);
                setStopState(false);
                break;
            case STOP:
                setStopState(true);
                setScanState(false);
                setStartState(false);
                break;
        }
    }

    /**
     * 设置搜索蓝牙按钮的状态
     *
     * @param visible
     */
    private void setScanState(boolean visible) {
        if (visible)
            btn_scan.setVisibility(View.VISIBLE);
        else
            btn_scan.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置开始测量按钮的状态
     *
     * @param visible
     */
    private void setStartState(boolean visible) {
        if (visible)
            btn_start.setVisibility(View.VISIBLE);
        else
            btn_start.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置停止测量按钮的状态
     *
     * @param visible
     */
    private void setStopState(boolean visible) {
        if (visible)
            btn_stop.setVisibility(View.VISIBLE);
        else
            btn_stop.setVisibility(View.INVISIBLE);
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
        public void scanClicked();

        public void startClicked();

        public void stopClicked();
    }

}
