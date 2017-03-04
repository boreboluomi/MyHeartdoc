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
 * Created by ZhangBo on 2016/3/8.
 */
public class PointView extends View {

    private Paint mPaint;// 背景画笔
    private int mNumber = 86;// 健康指数
    private String color = "#2f9726";// 背景颜色
    private boolean first = true;

    public PointView(Context context) {
        super(context);
    }

    public PointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 设置数字
     *
     * @param number
     */
    public void setNumber(int number) {
        mNumber = number;
        if (mNumber >= 85)
            color = "#2f9726";
        else if (mNumber >= 70)
            color = "#ff7200";
        else
            color = "#c9151e";
        first = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!first) {
            mPaint.setColor(Color.parseColor(color));
            canvas.drawArc(new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight()), 0, 360, true, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11,
                getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 11, getResources().getDisplayMetrics()));
    }
}
