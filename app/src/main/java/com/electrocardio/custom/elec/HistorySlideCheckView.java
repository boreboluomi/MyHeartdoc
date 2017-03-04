package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.electrocardio.util.SysinfoHelper;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ZhangBo on 2016/3/17.
 */
public class HistorySlideCheckView extends View {

    private Paint mLinePaint;// 线段画笔
    private Paint mTextPaint;// 字体画笔
    private Paint mCurvePaint;// 曲线画笔
    private ArrayList<String> labelName;// 小时标签集合
    private ArrayList<Float> pointYList;// 纵坐标集合
    private ArrayList<Integer> arrayList;// 心率数据集合
    private int sectionHeight;// 每段的高度
    private int sectionWidth;// 每段的宽度
    private int smallTagHeight;// 小的分段线高度
    private int bigTagHeight;// 大的分段线高度
    private int startMeasureX;
    private int currentX;// 当前横坐标
    private int scrolledPosition;
    private boolean hasData = false;// 是否有数据
    // private int initX;// 初始化的横坐标
    private ScrollListener mScrollListener;
    private long mTimelength = 3600000;// 测量时长
    private int sectionNumber = 30;// 段数

    public HistorySlideCheckView(Context context) {
        this(context, null);
    }

    public HistorySlideCheckView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistorySlideCheckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(3);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11,
                getResources().getDisplayMetrics()));

        mCurvePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCurvePaint.setStrokeWidth(3);
        mCurvePaint.setAntiAlias(true);
        mCurvePaint.setColor(Color.parseColor("#ed145b"));
        mCurvePaint.setStyle(Paint.Style.STROKE);
        PathEffect pathEffect = new CornerPathEffect(5);
        mCurvePaint.setPathEffect(pathEffect);

        labelName = new ArrayList<>();
        arrayList = new ArrayList<>();

        sectionHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                getResources().getDisplayMetrics());
        smallTagHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());
        bigTagHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7,
                getResources().getDisplayMetrics());
        scrolledPosition = sectionHeight * 2;
        pointYList = new ArrayList<>();

        if (SysinfoHelper.getInstance().supportHardwareAccelerated())
            // 是否开启了硬件加速，如开启将其禁掉
            if (!isHardwareAccelerated()) {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
    }

    /**
     * 设置时间标签
     *
     * @param startTime
     * @param timeLength
     */
    public void setTimeStampLabel(long startTime, long timeLength) {
        mTimelength = (timeLength + 60000) / 120000 * 120000;
        if (mTimelength < 3600000)
            mTimelength = 3600000;
        Calendar calendar = Calendar.getInstance();
        int hour;
        int minute;
        String hourStr;
        String minuteStr;
        for (long i = 0; i <= mTimelength; ) {
            calendar.setTimeInMillis(startTime + i);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            hourStr = hour < 10 ? "0" + hour : hour + "";
            minuteStr = minute < 10 ? "0" + minute : minute + "";
            labelName.add(hourStr + ":" + minuteStr);
            i += 600000;
        }
    }

    /**
     * 设置纵坐标集合
     *
     * @param arrayList
     */
    public void setPointYList(ArrayList<Integer> arrayList) {
        hasData = true;
        pointYList.clear();
        for (int data : arrayList)
            this.arrayList.add(data);
        for (int data : arrayList)
            pointYList.add((10 - data / 20f) * sectionHeight);
        invalidate();
    }

    /**
     * 设置曲线颜色
     *
     * @param curveLineColor
     */
    public void setCurveLineColor(int curveLineColor) {
        mCurvePaint.setColor(curveLineColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mLinePaint.setColor(Color.parseColor("#b2b2b2"));
        canvas.drawLine(0, sectionHeight * 10, getMeasuredWidth(), sectionHeight * 10, mLinePaint);
        drawSectionLine(canvas);
        drawBottomTimeLabel(canvas);
        if (hasData)
            drawCurvaLine(canvas);
        drawLongLine(canvas);
    }

    /**
     * 绘制竖着的长线
     *
     * @param canvas
     */
    private void drawLongLine(Canvas canvas) {
        mLinePaint.setColor(Color.parseColor("#333333"));
        canvas.drawLine(scrolledPosition, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 31,
                getResources().getDisplayMetrics()), scrolledPosition, sectionHeight * 10, mLinePaint);
        mLinePaint.setColor(Color.parseColor("#666666"));
    }

    /**
     * 绘制曲线
     *
     * @param canvas
     */
    private void drawCurvaLine(Canvas canvas) {
        int startX = startMeasureX;
        Path areaPath = new Path();
        for (int i = 0; i < pointYList.size() - 1; i++) {
            if (i == 0)
                areaPath.moveTo(startX, pointYList.get(i));
            else
                areaPath.lineTo(startX, pointYList.get(i));
            startX += sectionWidth / 2;
        }
        // areaPath.close();
        canvas.drawPath(areaPath, mCurvePaint);
        areaPath.reset();
    }

    /**
     * 绘制底部的时间标签
     *
     * @param canvas
     */
    private void drawBottomTimeLabel(Canvas canvas) {
        int textX = startMeasureX;
        int textY = (int) (sectionHeight * 10 - (mTextPaint.getFontMetrics().ascent - 0.5f));
        mTextPaint.setColor(Color.parseColor("#666666"));
        for (String hour : labelName) {
            canvas.drawText(hour, textX, textY, mTextPaint);
            textX += sectionWidth * 5;
        }
    }

    /**
     * 画分断线
     *
     * @param canvas
     */
    private void drawSectionLine(Canvas canvas) {
        int startX = startMeasureX;
        int startY = sectionHeight * 10;
        for (int i = 0; i <= sectionNumber; ) {
            if (i % 30 == 0)
                canvas.drawLine(startX, startY, startX, startY - bigTagHeight, mLinePaint);
            else
                canvas.drawLine(startX, startY, startX, startY - smallTagHeight, mLinePaint);
            startX += sectionWidth * 5;
            i += 5;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getX();
                startMeasureX -= (currentX - x);
                currentX = x;
                if (startMeasureX > sectionHeight * 2)
                    startMeasureX = sectionHeight * 2;
                if (startMeasureX < (getMeasuredWidth() / 2 + sectionHeight / 2 - sectionNumber * sectionWidth))
                    startMeasureX = getMeasuredWidth() / 2 + sectionHeight / 2 - sectionNumber * sectionWidth;
                scrolledPosition = (int) (sectionHeight * 2 + Math.abs(startMeasureX - sectionHeight
                        * 2) / (float) (sectionNumber * sectionWidth) * (getMeasuredWidth() - sectionHeight * 3));
                if (mScrollListener != null)
                    mScrollListener.onScrolled(scrolledPosition, getArrayListData((scrolledPosition -
                            startMeasureX) / sectionWidth * 2));
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 获取对应的数据
     *
     * @param position
     * @return
     */
    private int getArrayListData(int position) {
        if (arrayList != null && arrayList.size() > position)
            return arrayList.get(position);
        else
            return 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, sectionHeight * 11);
        sectionNumber = (int) (mTimelength / 120000);
        sectionWidth = (getMeasuredWidth() - sectionHeight * 3) / 30;
        startMeasureX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
                getResources().getDisplayMetrics());
    }

    public void setScrollListener(ScrollListener scrollListener) {
        mScrollListener = scrollListener;
    }

    public interface ScrollListener {
        public void onScrolled(int position, int number);
    }

}
