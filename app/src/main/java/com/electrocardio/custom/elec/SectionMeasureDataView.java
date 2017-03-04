package com.electrocardio.custom.elec;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.electrocardio.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ZhangBo on 2016/04/08.
 */
public class SectionMeasureDataView extends View {

    private int bigNumber = 7;// 大格的数量
    private boolean hasData = false;// 是否有数据
    private int secondNumber = 20;// 秒数
    private int smallWidth;// 小格的宽度
    private ArrayList<Float> dataList;// 数据集合
    private ArrayList<Float> pointList;// 纵坐标集合
    private ArrayList<String> labelArray;// 时间标签集合
    private Paint linePaint;// 绘制线的画笔
    private Paint waveLinePaint;// 绘制心电波形图的画笔
    private Paint mTextPaint;// 字体画笔
    private float gainRatio = 0.5f;// 增益系数
    private int bottomLineTY = 0;// 底部线顶点Y坐标
    private int bottomLineBY = 0;// 底部线低点Y坐标
    private int dataWidth = 0;// 数据宽度
    private int moveX = 0;// 移动的横坐标
    private int currentX = 0;// 当前横坐标
    private ProcessRatioListener mProcessRatioListener;// 进度监听器
    private int currRatio = 0;// 当前进度

    public SectionMeasureDataView(Context context) {
        this(context, null);
    }

    public SectionMeasureDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionMeasureDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BigNumber, defStyleAttr, 0);
        bigNumber = array.getInteger(R.styleable.BigNumber_bigNumber, 7);
        array.recycle();
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        pointList = new ArrayList<>();
        smallWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1,
                getResources().getDisplayMetrics());
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(3);
        linePaint.setColor(Color.RED);
        waveLinePaint = new Paint();
        waveLinePaint.setAntiAlias(true);
        waveLinePaint.setStrokeWidth(2);
        waveLinePaint.setColor(Color.BLACK);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#333333"));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13,
                getResources().getDisplayMetrics()));
    }

    /**
     * 为底部的时间标签赋值
     *
     * @param startTime
     * @param timeLength
     */
    public void setTimeStamp(long startTime, long timeLength) {
        if (labelArray == null)
            labelArray = new ArrayList<>();
        long mTimeLength = (timeLength + 500) / 1000 * 1000;
        Calendar calendar = Calendar.getInstance();
        int hour;
        int minute;
        int second;
        String hourStr;
        String minuteStr;
        String secondStr;
        for (int i = 0; i <= mTimeLength / 1000; i++) {
            calendar.setTimeInMillis(startTime);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            second = calendar.get(Calendar.SECOND);
            hourStr = hour < 10 ? "0" + hour : hour + "";
            minuteStr = minute < 10 ? "0" + minute : minute + "";
            secondStr = second < 10 ? "0" + second : second + "";
            labelArray.add(hourStr + ":" + minuteStr + ":" + secondStr);
            startTime += 1000;
        }
        secondNumber = (int) (mTimeLength / 1000);
    }

    /**
     * 设置数据集合
     *
     * @param array
     */
    public void setDataList(ArrayList<Float> array) {
        dataList = array;
        pointList.clear();
        for (float data : dataList)
            pointList.add(getMeasuredHeight() / 2 - data * gainRatio);
        hasData = true;
        invalidate();
    }

    /**
     * 调节增益系数
     *
     * @param ratio
     */
    public void setGainRatio(float ratio) {
        gainRatio = ratio;
        pointList.clear();
        for (float data : dataList)
            pointList.add(getMeasuredHeight() / 2 - data * gainRatio);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hasData) {
            drawWaveLine(canvas);
            drawTimeLabel(canvas);
            drawBottomLine(canvas);
        }
    }

    /**
     * 绘制底部的时间标签
     *
     * @param canvas
     */
    private void drawTimeLabel(Canvas canvas) {
//        for (int i = 1; i < labelArray.size(); i++)
//            canvas.drawText(labelArray.get(i), moveX + 25 * smallWidth * i, bottomLineBY + 2 * smallWidth, mTextPaint);

        for (int i = 1; i < labelArray.size(); i++)
            canvas.drawText(labelArray.get(i), 25 * smallWidth * i, bottomLineBY + 2 * smallWidth, mTextPaint);
    }

    /**
     * 绘制底部的线段
     *
     * @param canvas
     */
    private void drawBottomLine(Canvas canvas) {
//        canvas.drawLine(0, bottomLineBY, dataWidth + moveX, bottomLineBY, linePaint);
//        for (int i = 0; i <= secondNumber; i++)
//            canvas.drawLine(25 * smallWidth * i + moveX, bottomLineTY, 25 * smallWidth * i + moveX,
//                    bottomLineBY, linePaint);

        canvas.drawLine(0, bottomLineBY, dataWidth, bottomLineBY, linePaint);
        for (int i = 0; i <= secondNumber; i++)
            canvas.drawLine(25 * smallWidth * i, bottomLineTY, 25 * smallWidth * i, bottomLineBY, linePaint);
    }


    /**
     * 绘制心电波形图
     *
     * @param canvas
     */
    private void drawWaveLine(Canvas canvas) {
        // int pointX = moveX;
        int pointX = 0;
        for (int i = 0; i < pointList.size() - 1; i++)
            canvas.drawLine(pointX, pointList.get(i), ++pointX, pointList.get(i + 1), waveLinePaint);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                currentX = (int) event.getX();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int x = (int) event.getX();
//                moveX += (x - currentX) * 3;
//                if ((moveX - getMeasuredWidth() / 2 + dataWidth) < 0)
//                    moveX = getMeasuredWidth() / 2 - dataWidth;
//                if (moveX > 0)
//                    moveX = 0;
//                if (dataWidth <= getMeasuredWidth())
//                    moveX = 0;
//                currentX = x;
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//        }
//        invalidate();
//        calculateCurrentRatio();
//        return true;
//    }

    /**
     * 设置比例
     *
     * @param ratio
     */
    public void setCurrRatio(int ratio) {
        if (dataWidth <= getMeasuredWidth()) {
            moveX = 0;
        } else {
            if (currRatio != ratio) {
                currRatio = ratio;
                moveX = -(dataWidth - getMeasuredWidth() / 2) * currRatio / 100;
                invalidate();
            }
        }
    }

    /**
     * 计算当前进度
     */
    private void calculateCurrentRatio() {
        if (dataWidth <= getMeasuredWidth())
            currRatio = -1;
        else
            currRatio = -moveX * 100 / (dataWidth - getMeasuredWidth() / 2);
        if (mProcessRatioListener != null)
            mProcessRatioListener.ratio(currRatio);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(secondNumber * 25 * smallWidth, bigNumber * 5 * smallWidth + 1);
        dataWidth = secondNumber * 25 * smallWidth;

        bottomLineTY = (int) (getMeasuredHeight() - smallWidth * 3.5);
        bottomLineBY = getMeasuredHeight() - smallWidth * 3;
    }

    /**
     * 设置秒数
     *
     * @param number
     */
    public void setSecondNumber(int number) {
        secondNumber = number;
    }

    /**
     * 设置进度监听器
     *
     * @param processRatioListener
     */
    public void setProcessRatioListener(ProcessRatioListener processRatioListener) {
        mProcessRatioListener = processRatioListener;
    }

    public interface ProcessRatioListener {
        public void ratio(int ratio);
    }
}
