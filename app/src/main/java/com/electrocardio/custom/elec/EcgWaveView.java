package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;

/**
 * Created by ZhangBo on 2016/3/2.
 */
public class EcgWaveView extends View {

    private boolean hasData;// 是否有数据
    private boolean isFirstAdd = true;// 标记是否是第一个栈加数
    private Paint linePaint;// 绘制线的画笔
    private StackL stack = new StackL();// 自定义的存储数据的类
    private float gain = 0.5f;// 增益
    private final int PUSHDATA_TO_STACK = 1;//将数据推送到栈
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PUSHDATA_TO_STACK:
                    pushDataToStack((float[]) msg.obj);
                    break;
            }
        }
    };

    public EcgWaveView(Context context) {
        this(context, null);
    }

    public EcgWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EcgWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 设置增益
     */
    public void setGain(float gain) {
        this.gain = gain;
    }

    /**
     * 栈一减
     */
    private void firstCut() {
        if (stack.getFirstSize() != 0)
            stack.popFirst();
    }

    /**
     * 栈二减
     */
    private void secondCut() {
        if (stack.getSecondSize() != 0)
            stack.popSecond();
    }

    /**
     * 控件初始化
     */
    private void initView() {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(2);
    }

    /**
     * 设置数据
     *
     * @param drawArray
     */
    public void setDrawArray(float[] drawArray) {
        handler.obtainMessage(PUSHDATA_TO_STACK, drawArray).sendToTarget();
    }

    /**
     * 将数据放到栈中
     *
     * @param drawArray
     */
    private void pushDataToStack(float[] drawArray) {
        hasData = true;
        if (isFirstAdd) {
            for (int i = 0; i < drawArray.length; i++) {
                stack.pushFirst(getMeasuredHeight() / 2 - drawArray[i] * gain);
                secondCut();
                if (stack.getFirstSize() > (getWidth() + 1)) {
                    isFirstAdd = false;
                    for (int j = 0; j < 50; j++)
                        firstCut();
                }
            }
            invalidate();
        } else {
            for (int i = 0; i < drawArray.length; i++) {
                stack.pushSecond(getMeasuredHeight() / 2 - drawArray[i] * gain);
                firstCut();
                if (stack.getSecondSize() > (getWidth() + 1)) {
                    isFirstAdd = true;
                    for (int j = 0; j < 50; j++)
                        secondCut();
                }
            }
            invalidate();
        }
    }

    /**
     * 重置
     */
    public void reSet() {
        while (stack.getFirstSize() > 0) {
            stack.popFirst();
        }
        while (stack.getSecondSize() > 0) {
            stack.popSecond();
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hasData) {
            float pointX = 0;
            if (isFirstAdd) {// 先画栈一再画栈二
                for (int i = 0; i < stack.getFirstSize() - 1; i++) {
                    canvas.drawLine(pointX, stack.getFirst(i), pointX + 1,
                            stack.getFirst(i + 1), linePaint);
                    pointX += 1;
                }
                pointX += 50;
                if (stack.getSecondSize() != 0) {
                    for (int i = 0; i < stack.getSecondSize() - 1; i++) {
                        canvas.drawLine(pointX, stack.getSecond(i), pointX + 1,
                                stack.getSecond(i + 1), linePaint);
                        pointX += 1;
                    }
                }
            } else {// 先画栈二再画栈一
                for (int i = 0; i < stack.getSecondSize() - 1; i++) {
                    canvas.drawLine(pointX, stack.getSecond(i), pointX + 1, stack.getSecond(i + 1), linePaint);
                    pointX += 1;
                }
                pointX += 50;
                if (stack.getFirstSize() != 0) {
                    for (int i = 0; i < stack.getFirstSize() - 1; i++) {
                        canvas.drawLine(pointX, stack.getFirst(i), pointX + 1, stack.getFirst(i + 1), linePaint);
                        pointX += 1;
                    }
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stack.clearFirst();
        stack.clearSecond();
        super.onDetachedFromWindow();
    }

    /**
     * 自定义的接收数据的链表
     */
    class StackL {
        private LinkedList<Float> listFirst = new LinkedList<>();
        private LinkedList<Float> listSecond = new LinkedList<>();

        public void pushFirst(float data) {
            listFirst.add(data);
        }

        public float popFirst() {
            return listFirst.removeFirst();
        }

        public void clearFirst() {
            listFirst.clear();
        }

        public void clearSecond() {
            listSecond.clear();
        }

        public float getFirst(int index) {
            return listFirst.get(index);
        }

        public int getFirstSize() {
            return listFirst.size();
        }

        public void pushSecond(float data) {
            listSecond.add(data);
        }

        public float getSecond(int index) {
            return listSecond.get(index);
        }

        public int getSecondSize() {
            return listSecond.size();
        }

        public float popSecond() {
            return listSecond.removeFirst();
        }
    }
}
