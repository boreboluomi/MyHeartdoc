package com.electrocardio.custom.elec;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.R;

import java.util.Calendar;

/**
 * Created by ZhangBo on 2016/04/14.
 */
public class CustomProgressBarWithLabel extends RelativeLayout {

    private CustomProgressBar CPB;
    private TextView tv_startTimeLabel;// 开始时间标签
    private TextView tv_endTimeLabel;// 结束时间标签
    private String startTimeLebel;// 开始时间
    private String endTimeLebel;// 结束时间
    private ProgressRatioListener mProgressRatioListener;// 进度条监听器

    public CustomProgressBarWithLabel(Context context) {
        this(context, null);
    }

    public CustomProgressBarWithLabel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressBarWithLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.progressbarwithlabel, this);
        CPB = (CustomProgressBar) findViewById(R.id.CPB);
        tv_startTimeLabel = (TextView) findViewById(R.id.tv_startTimeLabel);
        tv_endTimeLabel = (TextView) findViewById(R.id.tv_endTimeLabel);
        // 设置进度条的监听事件
        CPB.setProgressBarListener(new CustomProgressBar.ProgressBarListener() {
            @Override
            public void currentRatio(int ratio) {
                if (mProgressRatioListener != null)
                    mProgressRatioListener.currentRatio(ratio);
            }
        });
        if (startTimeLebel != null) {
            tv_startTimeLabel.setText(startTimeLebel);
            tv_endTimeLabel.setText(endTimeLebel);
        }
    }

    /**
     * 设置时间
     *
     * @param startTime
     * @param timeLength
     */
    public void setTimeStamp(long startTime, long timeLength) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String hourStr = hour < 10 ? "0" + hour : hour + "";
        String minuteStr = minute < 10 ? "0" + minute : minute + "";
        String secondStr = second < 10 ? "0" + second : second + "";
        startTimeLebel = hourStr + ":" + minuteStr + ":" + secondStr;
        calendar.setTimeInMillis(startTime + timeLength);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        hourStr = hour < 10 ? "0" + hour : hour + "";
        minuteStr = minute < 10 ? "0" + minute : minute + "";
        secondStr = second < 10 ? "0" + second : second + "";
        endTimeLebel = hourStr + ":" + minuteStr + ":" + secondStr;
        if (tv_startTimeLabel != null) {
            tv_startTimeLabel.setText(startTimeLebel);
            tv_endTimeLabel.setText(endTimeLebel);
        }
    }

    /**
     * 设置进度
     *
     * @param ratio
     */
    public void setRatio(int ratio) {
        CPB.setRatio(ratio);
    }

    /**
     * 设置进度条监听器
     *
     * @param progressRatioListener
     */
    public void setProgressRatioListener(ProgressRatioListener progressRatioListener) {
        mProgressRatioListener = progressRatioListener;
    }

    public interface ProgressRatioListener {
        public void currentRatio(int ration);
    }
}
