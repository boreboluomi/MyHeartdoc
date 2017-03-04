package com.electrocardio.custom.elec;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/04/08.
 */
public class GainStandardView extends View {

    private int bigNumber = 7;// 大格的数量
    private int startY = 0;// 按下时的纵坐标
    private int moveY = 0;// Y轴移动距离
    private Paint linePaint;// 绘制线的画笔
    private int smallWidth;// 小格的宽度
    private int bottomY = 0;// 校准器底部Y坐标
    private int topY = 0;//校准器顶部Y坐标
    private float gain = 0.5f;// 增益

    public GainStandardView(Context context) {
        this(context, null);
    }

    public GainStandardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GainStandardView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setColor(Color.RED);
        smallWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1,
                getResources().getDisplayMetrics());
    }

    /**
     * 调节增益
     *
     * @param gain
     */
    public void setGain(float gain) {
        this.gain = gain;
        topY = (int) (smallWidth * 25 - smallWidth * 10 * gain);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGainStandard(canvas);
    }

    /**
     * 绘制1mv校准器
     *
     * @param canvas
     */
    private void drawGainStandard(Canvas canvas) {
        linePaint.setStrokeWidth(5);
        canvas.drawLine(0, bottomY, smallWidth / 2, bottomY, linePaint);
        canvas.drawLine(smallWidth / 2, bottomY + 2, smallWidth / 2, topY - 2, linePaint);
        canvas.drawLine(smallWidth / 2, topY, smallWidth / 2 + smallWidth,
                topY, linePaint);
        canvas.drawLine(smallWidth / 2 + smallWidth, topY - 2, smallWidth / 2 + smallWidth,
                bottomY + 2, linePaint);
        canvas.drawLine(smallWidth / 2 + smallWidth, bottomY, smallWidth + smallWidth,
                bottomY, linePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, bigNumber * 5 * (int) TypedValue.applyDimension(TypedValue.
                COMPLEX_UNIT_MM, 1, getResources().getDisplayMetrics()) + 2);
        bottomY = smallWidth * 25;
        topY = (int) (smallWidth * 25 - smallWidth * 10 * gain);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = (int) event.getY() - startY;
                startY = (int) event.getY();
                bottomY += moveY;
                topY += moveY;
                if (topY < 0) {
                    topY = 0;
                    bottomY = (int) (topY + smallWidth * 10 * gain);
                }
                if (bottomY > getMeasuredHeight()) {
                    bottomY = getMeasuredHeight();
                    topY = (int) (bottomY - smallWidth * 10 * gain);
                }
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }
}
