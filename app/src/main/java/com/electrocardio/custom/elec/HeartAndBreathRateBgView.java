package com.electrocardio.custom.elec;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.electrocardio.R;
import com.electrocardio.util.SysinfoHelper;

/**
 * Created by ZhangBo on 2016/3/15.
 */
public class HeartAndBreathRateBgView extends View {

    private boolean isHeartRate = true;
    private Paint mPaint;// 背景画笔
    private Paint mTextPaint;// 字体画笔
    private Paint dotsLinePaint;// 虚线画笔
    private int itemHeight;// 条目高度
    private int lineLength;// 横杠长度

    public HeartAndBreathRateBgView(Context context) {
        this(context, null);
    }

    public HeartAndBreathRateBgView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartAndBreathRateBgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HeartAndBreathRateBgView);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.HeartAndBreathRateBgView_isHeartRate:
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
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
        // mPaint.setAlpha(51);
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11,
                getResources().getDisplayMetrics()));
        mTextPaint.setColor(Color.parseColor("#666666"));

        dotsLinePaint = new Paint();
        dotsLinePaint.setStyle(Paint.Style.FILL);
        dotsLinePaint.setStrokeWidth(1);
        dotsLinePaint.setAntiAlias(true);
        dotsLinePaint.setColor(Color.parseColor("#666666"));
        float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                getResources().getDisplayMetrics());
        PathEffect pathEffect = new DashPathEffect(new float[]{width, width, width, width}, 1);
        dotsLinePaint.setPathEffect(pathEffect);

        if (SysinfoHelper.getInstance().supportHardwareAccelerated())
            // 是否开启了硬件加速，如开启将其禁掉
            if (!isHardwareAccelerated()) {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);

        drawLeftLine(canvas);

        drawLeftText(canvas);

        drawDotsLine(canvas);

    }

    /**
     * 绘制虚线
     *
     * @param canvas
     */
    private void drawDotsLine(Canvas canvas) {
        float textLen = mTextPaint.measureText("过快");
        for (int i = 0; i < 8; i++) {
            if (isHeartRate)
                if (i == 1 || i == 4 || i == 7)
                    canvas.drawLine(itemHeight * 2, itemHeight * (2 + i), getMeasuredWidth() -
                            itemHeight - textLen - lineLength, itemHeight * (2 + i), dotsLinePaint);
                else
                    canvas.drawLine(itemHeight * 2, itemHeight * (2 + i), getMeasuredWidth() - itemHeight,
                            itemHeight * (2 + i), dotsLinePaint);
            else if (i == 1 || i == 4 || i == 7)
                canvas.drawLine(itemHeight * 2, itemHeight * (2 + i), getMeasuredWidth() -
                        itemHeight - textLen - lineLength, itemHeight * (2 + i), dotsLinePaint);
            else
                canvas.drawLine(itemHeight * 2, itemHeight * (2 + i), getMeasuredWidth() - itemHeight,
                        itemHeight * (2 + i), dotsLinePaint);
        }
    }

    /**
     * 画左边的文字
     *
     * @param canvas
     */
    private void drawLeftText(Canvas canvas) {

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        int number = isHeartRate ? 160 : 32;
        for (int i = 0; i < 8; i++) {
            mTextPaint.measureText(number + "");
            canvas.drawText(number + "", itemHeight * 2 - lineLength * 3, itemHeight * (2 + i)
                    + fontMetrics.descent, mTextPaint);
            number -= isHeartRate ? 20 : 4;
            if (i == 1) {
                canvas.drawText("过快", getMeasuredWidth() - itemHeight, itemHeight * (2 + i)
                        + fontMetrics.descent, mTextPaint);
            }
            if (i == 4) {
                canvas.drawText("正常", getMeasuredWidth() - itemHeight, itemHeight * (2 + i)
                        + fontMetrics.descent, mTextPaint);
            }
            if (i == 7) {
                canvas.drawText("过慢", getMeasuredWidth() - itemHeight, itemHeight * (2 + i)
                        + fontMetrics.descent, mTextPaint);
            }
        }
        if (isHeartRate) {
            canvas.drawText("稍快", getMeasuredWidth() - itemHeight, itemHeight * 4.5f
                    + fontMetrics.descent, mTextPaint);
            canvas.drawText("稍慢", getMeasuredWidth() - itemHeight, itemHeight * 7.5f
                    + fontMetrics.descent, mTextPaint);
        }
    }

    /**
     * 画左边的线段
     *
     * @param canvas
     */
    private void drawLeftLine(Canvas canvas) {
        for (int i = 0; i < 8; i++)
            canvas.drawLine(itemHeight * 2 - lineLength * 2, itemHeight * (2 + i), itemHeight * 2 -
                    lineLength, itemHeight * (2 + i), mTextPaint);
    }

    /**
     * 画背景条目图
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        if (isHeartRate) {
            mPaint.setARGB(51, 214, 32, 2);
            canvas.drawRect(0, itemHeight * 1, getMeasuredWidth(), itemHeight * 4, mPaint);
            mPaint.setARGB(51, 229, 131, 8);
            // mPaint.setColor(Color.parseColor("#33e58308"));
            canvas.drawRect(0, itemHeight * 4, getMeasuredWidth(), itemHeight * 5, mPaint);
            mPaint.setARGB(51, 109, 189, 24);
            // mPaint.setColor(Color.parseColor("#336dbd18"));
            canvas.drawRect(0, itemHeight * 5, getMeasuredWidth(), itemHeight * 7, mPaint);
            mPaint.setARGB(51, 33, 208, 198);
            // mPaint.setColor(Color.parseColor("#3321d0c6"));
            canvas.drawRect(0, itemHeight * 7, getMeasuredWidth(), itemHeight * 8, mPaint);
            mPaint.setARGB(51, 2, 139, 202);
            // mPaint.setColor(Color.parseColor("#33028bca"));
            canvas.drawRect(0, itemHeight * 8, getMeasuredWidth(), itemHeight * 10, mPaint);
        } else {
            mPaint.setARGB(51, 229, 131, 8);
            // mPaint.setColor(Color.parseColor("#33e58308"));
            canvas.drawRect(0, itemHeight * 1, getMeasuredWidth(), itemHeight * 4, mPaint);
            mPaint.setARGB(51, 109, 189, 24);
            // mPaint.setColor(Color.parseColor("#336dbd18"));
            canvas.drawRect(0, itemHeight * 4, getMeasuredWidth(), itemHeight * 7, mPaint);
            mPaint.setARGB(51, 214, 32, 2);
            // mPaint.setColor(Color.parseColor("#3321d0c6"));
            canvas.drawRect(0, itemHeight * 7, getMeasuredWidth(), itemHeight * 10, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 220, getResources().getDisplayMetrics()));
        itemHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                getResources().getDisplayMetrics());
        lineLength = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                getResources().getDisplayMetrics());
    }

}
