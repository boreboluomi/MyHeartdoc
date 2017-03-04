package com.electrocardio.custom.elec;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by ZhangBo on 2016/3/4.
 */
public class RateLabel extends View {

    private Paint mPaint;// 背景画笔
    private Paint mTextPaint;// 字体画笔
    private int mRate = 50;// 心率
    private int mRateScale = 0;//比例
    private boolean setRate = false;// 是否已经设置了心率
    private int raidusWidth;// 线条宽度
    private String color = "#2f9726";

    public RateLabel(Context context) {
        super(context);
        initView();
    }

    public RateLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RateLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 控件初始化
     */
    private void initView() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.parseColor("#cccccc"));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11,
                getResources().getDisplayMetrics()));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        raidusWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (setRate) {
            mPaint.setColor(Color.parseColor("#cccccc"));
            RectF rect = new RectF(raidusWidth, raidusWidth, getMeasuredWidth() - raidusWidth,
                    getMeasuredWidth() - raidusWidth);
            canvas.drawArc(rect, -90, mRateScale, true, mPaint);
            mPaint.setColor(Color.parseColor(color));
            canvas.drawArc(rect, mRateScale - 90, 360 - mRateScale, true, mPaint);
            mPaint.setColor(Color.parseColor("#ffffff"));
            rect = new RectF(raidusWidth * 2, raidusWidth * 2, getMeasuredWidth() - raidusWidth * 2,
                    getMeasuredWidth() - raidusWidth * 2);
            canvas.drawArc(rect, 0, 360, true, mPaint);
            mTextPaint.setColor(Color.parseColor(color));
            mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20,
                    getResources().getDisplayMetrics()));
            canvas.drawText(mRate + "", getWidth() / 2, (getWidth() - mTextPaint.
                    getFontMetrics().descent - mTextPaint.getFontMetrics().ascent) / 2, mTextPaint);
            mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11,
                    getResources().getDisplayMetrics()));
            canvas.drawText("正常心率比", getWidth() / 2, (getHeight() + getWidth() - mTextPaint.
                    getFontMetrics().descent - mTextPaint.getFontMetrics().ascent) / 2, mTextPaint);
        }
    }

    /**
     * 设置心率
     *
     * @param rate
     */
    public void setRate(int rate) {
        mRate = rate;
        setRate = true;
        if (mRate >= 85)
            color = "#6dbd18";
        else if (mRate >= 70)
            color = "#e58308";
        else
            color = "#d62002";
        mRateScale = (100 - mRate) * 36 / 10;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(), measureHeight());
    }

    private int measureWidth() {
        int result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68,
                getResources().getDisplayMetrics());
        return result;
    }

    private int measureHeight() {
        int result = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68,
                getResources().getDisplayMetrics()) + mTextPaint.getFontMetrics().descent -
                mTextPaint.getFontMetrics().ascent);
        return result;
    }
}
