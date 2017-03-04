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

/**
 * Created by ZhangBo on 2016/04/12.
 */
public class HistoryMeasureBackground extends View {

    private Paint mPaint;// 画笔
    private int smallWidth;// 最小格的宽度
    private int width = 0;// 宽
    private int height = 0;// 高
    private int bigNumber = 7;// 大格的数量

    public HistoryMeasureBackground(Context context) {
        this(context, null);
    }

    public HistoryMeasureBackground(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistoryMeasureBackground(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(2);
        // mPaint.setAlpha(100);
        smallWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAlpha(100);
        for (int i = 0; i < width / smallWidth; i++) {
            if (i % 5 == 0) {
                mPaint.setStrokeWidth(2);
            } else {
                mPaint.setStrokeWidth(1);
            }
            canvas.drawLine(smallWidth * i, 0, smallWidth * i, height, mPaint);
        }
        for (int i = 0; i < height / smallWidth; i++) {
            if (i % 5 == 0) {
                mPaint.setStrokeWidth(2);
            } else {
                mPaint.setStrokeWidth(1);
            }
            canvas.drawLine(0, smallWidth * i, width, smallWidth * i, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, bigNumber * 5 * (int) TypedValue.applyDimension(TypedValue.
                COMPLEX_UNIT_MM, 1, getResources().getDisplayMetrics()) + 2);
        width = (getMeasuredWidth() / smallWidth + 1) * smallWidth;
        height = (getMeasuredHeight() / smallWidth + 1) * smallWidth;
    }
}
