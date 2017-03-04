package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/04/08.
 */
public class CustomProgressBar extends View {

    private int currentX;
    private int moveX = 0;
    private Paint mPaint;// 画笔
    private Paint mPicPaint;// 图片画笔
    private int currentRadio = 0;//当前进度
    private int topY = 0;// 进度条顶点纵坐标
    private int bottomY = 0;// 进度条底端纵坐标
    private Bitmap bitmap;// 图片按钮
    private ProgressBarListener mProgressBarListener;// 进度监听器

    public CustomProgressBar(Context context) {
        this(context, null);
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        mPicPaint = new Paint();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.progress_focus);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgressBar(canvas);
        canvas.drawBitmap(bitmap, (getMeasuredWidth() - bitmap.getWidth()) * currentRadio / 100, 0, mPicPaint);
    }

    /**
     * 设置进度
     *
     * @param ratio
     */
    public void setRatio(int ratio) {
        if (ratio == -1)
            return;
        if (ratio == currentRadio)
            return;
        currentRadio = ratio;
        moveX = currentRadio * (getMeasuredWidth() - bitmap.getWidth()) / 100;
        invalidate();
    }

    /**
     * 计算当前进度
     */
    private void calculateCurrentRatio() {
        currentRadio = moveX * 100 / (getMeasuredWidth() - bitmap.getWidth());
        if (mProgressBarListener != null)
            mProgressBarListener.currentRatio(currentRadio);
    }

    /**
     * 绘制进度条
     *
     * @param canvas
     */
    private void drawProgressBar(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#26bfa6"));
        Rect rectF = new Rect(0, topY, getMeasuredWidth() * currentRadio / 100, bottomY);
        canvas.drawRect(rectF, mPaint);
        mPaint.setColor(Color.parseColor("#b2b2b2"));
        Rect rectS = new Rect(getMeasuredWidth() * currentRadio / 100, topY, getMeasuredWidth(), bottomY);
        canvas.drawRect(rectS, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                int xF = (int) event.getX();
                moveX = xF;
                if (moveX < 0)
                    moveX = 0;
                if (moveX > (getMeasuredWidth() - bitmap.getWidth()))
                    moveX = getMeasuredWidth() - bitmap.getWidth();
                currentX = xF;
                break;
            case MotionEvent.ACTION_MOVE:
                int xS = (int) event.getX();
                moveX += (xS - currentX);
                if (moveX < 0)
                    moveX = 0;
                if (moveX > (getMeasuredWidth() - bitmap.getWidth()))
                    moveX = getMeasuredWidth() - bitmap.getWidth();
                currentX = xS;
                break;
            case MotionEvent.ACTION_DOWN:
                currentX = (int) event.getX();
                break;
        }
        calculateCurrentRatio();
        invalidate();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, bitmap.getHeight());
        topY = (int) ((getMeasuredHeight() - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5, getResources().getDisplayMetrics())) / 2);
        bottomY = (int) ((getMeasuredHeight() + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5, getResources().getDisplayMetrics())) / 2);
    }

    /**
     * 设置进度监听器
     *
     * @param progressBarListener
     */
    public void setProgressBarListener(ProgressBarListener progressBarListener) {
        mProgressBarListener = progressBarListener;
    }

    public interface ProgressBarListener {
        public void currentRatio(int ratio);
    }
}
