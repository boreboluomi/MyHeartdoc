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
 * Created by ZhangBo on 2016/2/27.
 */
public class HeartRateLabel extends View {

    private Paint mBorderPaint;
    private int mRate = 50;
    private int centerX;
    private int centerY;
    private int radius;
    private String white = "#ffffff";
    private String bgColor = "#cccccc";
    private String normalColor = "#6dbd18";
    private String quickColor = "#d62002";
    private String quickNorColor = "#e58308";
    private String slowColor = "#028bca";
    private String slowNorColor = "#21d0c6";

    public HeartRateLabel(Context context) {
        super(context);
    }

    public HeartRateLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeartRateLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                getResources().getDisplayMetrics());
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setColor(Color.parseColor(bgColor));
    }

    /**
     * 设置内容
     *
     * @param rate
     */
    private void setRate(int rate) {
        mRate = rate;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(new RectF(0, 0, getWidth(), getHeight()), 0, (100 - mRate) * 360 / 100,
                true, mBorderPaint);
        mBorderPaint.setColor(Color.parseColor(normalColor));
        canvas.drawArc(new RectF(0, 0, getWidth(), getHeight()), (100 - mRate) * 360 / 100, 360,
                true, mBorderPaint);
        mBorderPaint.setColor(Color.parseColor(white));
        canvas.drawArc(new RectF(0, 0, getWidth(), getHeight()), 0, 360, true, mBorderPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
    }
}
