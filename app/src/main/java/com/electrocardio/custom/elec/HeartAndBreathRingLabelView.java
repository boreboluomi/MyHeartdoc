package com.electrocardio.custom.elec;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.electrocardio.R;

import java.util.Random;

/**
 * Created by ZhangBo on 2016/3/15.
 */
public class HeartAndBreathRingLabelView extends View {

    private int normal = 53;
    private int ringWidth;// 圆环宽度
    private int fastRate = 53;// 过快比例
    private int normalFast = 72;// 稍快比例
    private int normalRate = 188;// 正常比例
    private int normalSlow = 31;// 稍慢比例
    private int slowRate = 11;// 过慢比例
    private boolean isHeartRate = true;// 是否为心率
    private int startAngle = 0;
    private int zeroNumber = 0;// 数据为零的个数
    private Paint mPaint;// 画笔
    private Paint mTextPaint;// 字体画笔

    public HeartAndBreathRingLabelView(Context context) {
        this(context, null);
    }

    public HeartAndBreathRingLabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartAndBreathRingLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HeartAndBreathRingLabelView);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.HeartAndBreathRingLabelView_isHeartRateLabel:
                    isHeartRate = array.getBoolean(attr, true);
                    break;
            }
        }
        array.recycle();
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ringWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                getResources().getDisplayMetrics());
        startAngle = new Random().nextInt(100) - 90;
        mPaint = new Paint();
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.parseColor("#6dbd18"));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        if (!isHeartRate) {
            normal = 63;
            fastRate = 97;// 过快比例
            normalRate = 215;// 正常比例
            slowRate = 45;// 过慢比例
        } else {
            normal = 52;
            fastRate = 53;// 过快比例
            normalFast = 72;// 稍快比例
            normalRate = 188;// 正常比例
            normalSlow = 31;// 稍慢比例
            slowRate = 11;// 过慢比例
        }
    }

    /**
     * 设置比例
     *
     * @param array
     */
    public void setRate(int[] array) {
        for (int data : array)
            if (data == 0)
                zeroNumber++;
        if (array.length == 5) {
            normal = array[2];
            if (zeroNumber < 4) {
                fastRate = (int) ((3.55f + 0.01 * zeroNumber) * array[0]);// 过快比例
                normalFast = (int) ((3.55f + 0.01 * zeroNumber) * array[1]);// 稍快比例
                normalRate = (int) ((3.55f + 0.01 * zeroNumber) * array[2]);// 正常比例
                normalSlow = (int) ((3.55f + 0.01 * zeroNumber) * array[3]);// 稍慢比例
                if (array[4] == 0)
                    slowRate = 0;
                else
                    slowRate = 355 - (fastRate == 0 ? -1 : fastRate) - (normalFast == 0 ? -1 : normalFast)
                            - (normalRate == 0 ? -1 : normalRate) - (normalSlow == 0 ? -1 : normalSlow);// 过慢比例
            } else {
                fastRate = (int) (3.6f * array[0]);// 过快比例
                normalFast = (int) (3.6f * array[1]);// 稍快比例
                normalRate = (int) (3.6f * array[2]);// 正常比例
                normalSlow = (int) (3.6f * array[3]);// 稍慢比例
                slowRate = (int) (3.6f * array[4]);// 过慢比例
            }
            invalidate();
        }
        if (array.length == 3) {
            normal = array[1];
            if (zeroNumber < 2) {
                fastRate = (int) ((3.57f + 0.01 * zeroNumber) * array[0]);// 过快比例
                normalRate = (int) ((3.57f + 0.01 * zeroNumber) * array[1]);// 正常比例
                if (array[2] == 0)
                    slowRate = 0;
                else
                    slowRate = 357 - (fastRate == 0 ? -1 : fastRate) - (normalRate == 0 ? -1 : normalRate);// 过慢比例
            } else {
                fastRate = (int) (3.6f * array[0]);// 过快比例
                normalRate = (int) (3.6f * array[1]);// 正常比例
                slowRate = (int) (3.6f * array[2]);// 过慢比例
            }
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isHeartRate) {
            mPaint.setColor(Color.parseColor("#d62002"));
            canvas.drawArc(new RectF(ringWidth, ringWidth, getMeasuredWidth() - ringWidth,
                    getMeasuredHeight() - ringWidth), startAngle, fastRate, true, mPaint);
            startAngle += (fastRate == 0 ? 0 : fastRate + 1);

            mPaint.setColor(Color.parseColor("#e58308"));
            canvas.drawArc(new RectF(ringWidth, ringWidth, getMeasuredWidth() - ringWidth,
                    getMeasuredHeight() - ringWidth), startAngle, normalFast, true, mPaint);
            startAngle += (normalFast == 0 ? 0 : normalFast + 1);

            mPaint.setColor(Color.parseColor("#6dbd18"));
            canvas.drawArc(new RectF(ringWidth, ringWidth, getMeasuredWidth() - ringWidth,
                    getMeasuredHeight() - ringWidth), startAngle, normalRate, true, mPaint);
            startAngle += (normalRate == 0 ? 0 : normalRate + 1);

            mPaint.setColor(Color.parseColor("#21d0c6"));
            canvas.drawArc(new RectF(ringWidth, ringWidth, getMeasuredWidth() - ringWidth,
                    getMeasuredHeight() - ringWidth), startAngle, normalSlow, true, mPaint);
            startAngle += (normalSlow == 0 ? 0 : normalSlow + 1);

            mPaint.setColor(Color.parseColor("#028bca"));
            canvas.drawArc(new RectF(ringWidth, ringWidth, getMeasuredWidth() - ringWidth,
                    getMeasuredHeight() - ringWidth), startAngle, slowRate, true, mPaint);
        } else {
            mPaint.setColor(Color.parseColor("#e58308"));
            canvas.drawArc(new RectF(ringWidth, ringWidth, getMeasuredWidth() - ringWidth,
                    getMeasuredHeight() - ringWidth), startAngle, fastRate, true, mPaint);
            startAngle += (fastRate == 0 ? 0 : fastRate + 1);

            mPaint.setColor(Color.parseColor("#6dbd18"));
            canvas.drawArc(new RectF(ringWidth, ringWidth, getMeasuredWidth() - ringWidth,
                    getMeasuredHeight() - ringWidth), startAngle, normalRate, true, mPaint);
            startAngle += (normalRate == 0 ? 0 : normalRate + 1);

            mPaint.setColor(Color.parseColor("#d62002"));
            canvas.drawArc(new RectF(ringWidth, ringWidth, getMeasuredWidth() - ringWidth,
                    getMeasuredHeight() - ringWidth), startAngle, slowRate, true, mPaint);
        }
        mPaint.setColor(Color.parseColor("#ffffff"));
        canvas.drawArc(new RectF(ringWidth * 2, ringWidth * 2, getMeasuredWidth() - ringWidth * 2,
                getMeasuredHeight() - ringWidth * 2), 0, 360, true, mPaint);
        drawText(canvas);
    }

    /**
     * 绘制字体
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20,
                getResources().getDisplayMetrics()));
        canvas.drawText(normal + "%", getMeasuredWidth() / 2, getMeasuredHeight() / 2, mTextPaint);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13,
                getResources().getDisplayMetrics()));
        canvas.drawText("正常", getMeasuredWidth() / 2, getMeasuredHeight() / 2 -
                mTextPaint.getFontMetrics().ascent + mTextPaint.getFontMetrics().descent, mTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80,
                getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()));
    }
}
