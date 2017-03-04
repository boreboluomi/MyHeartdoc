package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class DrawMainBackground extends View {

    private Paint mPaint;
    private int smallWidth;
    private int width = 0;
    private int height = 0;
    private int moveY = 0;// Y周轴移动距离
    private int topY = 0;// 顶部Y轴坐标
    private int bottomY = 0;// 底部Y轴坐标
    private int startY = 0;// 按下时的纵坐标

    public DrawMainBackground(Context context) {
        super(context);
        initView();
    }
    public DrawMainBackground(Context context, Renderer renderer) {
        super(context);
        initView();
    }


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

        mPaint.setStrokeWidth(5);
        mPaint.setAlpha(255);
        canvas.drawLine(0, bottomY, smallWidth / 2, bottomY, mPaint);
        canvas.drawLine(smallWidth / 2, bottomY + 2, smallWidth / 2, topY - 2, mPaint);
        canvas.drawLine(smallWidth / 2, topY, smallWidth / 2 + smallWidth,
                topY, mPaint);
        canvas.drawLine(smallWidth / 2 + smallWidth, topY - 2, smallWidth / 2 + smallWidth,
                bottomY + 2, mPaint);
        canvas.drawLine(smallWidth / 2 + smallWidth, bottomY, smallWidth + smallWidth,
                bottomY, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
//                moveY = (int) event.getY() - startY;
//                bottomY -= moveY;
//                topY -= moveY;
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = (int) event.getY() - startY;
                startY = (int) event.getY();
                bottomY += moveY;
                topY += moveY;
                if (topY < 0) {
                    topY = 0;
                    bottomY = topY + smallWidth * 10;
                }
                if (bottomY > height) {
                    bottomY = height;
                    topY = bottomY - smallWidth * 10;
                }
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = (getMeasuredWidth() / smallWidth + 1) * smallWidth;
        height = (getMeasuredHeight() / smallWidth + 1) * smallWidth;
        bottomY = smallWidth * 25;
        topY = smallWidth * 15;
    }
}
