package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/2/23.
 */
public class SwitchButtonView extends View {

    private Bitmap slideButtonBackground;// 滑动块背景
    private boolean currentState = false;// 开关当前的状态
    private boolean isSliding = false;// 当前是否处于滑动状态
    // private int currentX = 0;// 触摸的那个点的X轴值

    private OnSwitchStateChangeListener mOnSwitchStateChangeListener;

    public void setOnSwitchStateChangeListener(
            OnSwitchStateChangeListener mOnSwitchStateChangeListener) {
        this.mOnSwitchStateChangeListener = mOnSwitchStateChangeListener;
    }

    public SwitchButtonView(Context context) {
        super(context);
    }

    public void setState(boolean state) {
        if (currentState != state) {
            currentState = state;
            invalidate();
        }
    }

    public boolean isChecked() {
        return currentState;
    }

    public SwitchButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        String namespace = "http://schemas.android.com/apk/res-auto";
        // 取出滑动块的id
        int slideButtonBackgroundID = attrs.getAttributeResourceValue(
                namespace, "slideButtonBackground", -1);
        // 取出当前的状态
        currentState = attrs.getAttributeBooleanValue(namespace, "switchState",
                false);
        setSlideButtonBackgroundResource(slideButtonBackgroundID);
    }

    public SwitchButtonView(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackgroundBitmap();
        if (isSliding) {
//            int left = currentX - getMeasuredHeight();
//            int rightAlign = getWidth() - getMeasuredHeight();
//            if (left < 0) {
//                left = 0;
//            } else if (left > rightAlign) {
//                left = rightAlign;
//            }
//            canvas.drawBitmap(slideButtonBackground, left, 0, null);
        } else {
            if (currentState) {
                canvas.drawBitmap(slideButtonBackground, 0, 0, null);
            } else {
                int left = getWidth() - getMeasuredHeight();
                canvas.drawBitmap(slideButtonBackground, left + TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()), 0, null);
            }
        }
    }

    /**
     * 绘制背景图片
     */
    private void drawBackgroundBitmap() {
        if (currentState)
            setBackgroundResource(R.drawable.switch_bg_on);
        else
            setBackgroundResource(R.drawable.switch_bg_off);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // isSliding = true;
                // currentX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                // currentX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                // isSliding = false;
                // currentX = (int) event.getX();
                // int center = getWidth() / 2;
                // 如果当前的x的值大于center,应该处于打开的状态
                currentState = !currentState;
                //boolean state = currentX < center;
                // 触发activity那边注册的回调事件
                if (mOnSwitchStateChangeListener != null) {
                    mOnSwitchStateChangeListener.onSwitchStateChange(currentState);
                }
                //currentState = state;
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 设置滑动块的背景图片
     *
     * @param slideButtonBackgroundID
     */
    public void setSlideButtonBackgroundResource(int slideButtonBackgroundID) {
        slideButtonBackground = BitmapFactory.decodeResource(getResources(),
                slideButtonBackgroundID);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec),
                slideButtonBackground.getHeight());
    }

    private int measureWidth(int measureSpec) {
        int result = 100;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    public interface OnSwitchStateChangeListener {
        public void onSwitchStateChange(boolean state);
    }
}
