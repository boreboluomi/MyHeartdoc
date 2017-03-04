package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by ZhangBo on 2016/3/7.
 */
public class SixSecondRecordLabel extends View {

    private Paint mPaint;// 背景画笔
    private Paint mTextPaint;// 字体画笔
    private int borderWidth = 0;// 圆环半径
    private String mainColor = "#2f9726";
    private String bgColor = "#cccccc";
    private int mNumber = 86;// 健康指数
    private boolean first = true;
    private float rate = 50;

    public SixSecondRecordLabel(Context context) {
        super(context);
        initView();
    }

    public SixSecondRecordLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SixSecondRecordLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        borderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                getResources().getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1f);
        mPaint.setStyle(Paint.Style.FILL);
        mTextPaint = new Paint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
    }

    /**
     * 设置指数
     *
     * @param number
     */
    public void setNumber(int number) {
        mNumber = number;
        if (mNumber >= 85)
            mainColor = "#2f9726";
        else if (mNumber >= 70)
            mainColor = "#ff7200";
        else
            mainColor = "#c9151e";
        first = false;
        rate = (100 - number) * 3.6f;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!first) {
            mPaint.setColor(Color.parseColor(bgColor));
            RectF rectF = new RectF(borderWidth, borderWidth, getMeasuredWidth() - borderWidth,
                    getMeasuredWidth() - borderWidth);
            canvas.drawArc(rectF, -90, rate, true, mPaint);
            mPaint.setColor(Color.parseColor(mainColor));
            canvas.drawArc(rectF, -90 + rate, 360 - rate, true, mPaint);
            RectF rectFTwo = new RectF(borderWidth * 2, borderWidth * 2, getMeasuredWidth() -
                    borderWidth * 2, getMeasuredWidth() - borderWidth * 2);
            mPaint.setColor(Color.parseColor("#f7f7fa"));
            canvas.drawArc(rectFTwo, 0, 360, true, mPaint);
            mTextPaint.setColor(Color.parseColor(mainColor));
            mTextPaint.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20,
                    getResources().getDisplayMetrics()));
            Paint.FontMetrics fm = mTextPaint.getFontMetrics();
            canvas.drawText(mNumber + "", getMeasuredWidth() / 2, getMeasuredHeight() / 2, mTextPaint);
            mTextPaint.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11,
                    getResources().getDisplayMetrics()));
            canvas.drawText("健康指数", getMeasuredWidth() / 2, getMeasuredHeight() / 2
                    + fm.descent * 3, mTextPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 62,
                getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 62, getResources().getDisplayMetrics()));
    }
}
