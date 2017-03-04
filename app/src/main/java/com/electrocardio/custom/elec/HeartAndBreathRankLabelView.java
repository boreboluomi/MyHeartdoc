package com.electrocardio.custom.elec;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/3/16.
 */
public class HeartAndBreathRankLabelView extends View {

    private int rankColor;// 排行榜颜色
    private String rankLabel = "过快";
    private int rankBorder = 0;//
    private int textColor;// 字体颜色
    private int textSize;// 字体大小
    private int space;
    private Paint mPaint;// 背景画笔
    private Paint mTextPaint;// 字体画笔

    public HeartAndBreathRankLabelView(Context context) {
        this(context, null);
    }

    public HeartAndBreathRankLabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartAndBreathRankLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HeartAndBreathRankLabelView);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.HeartAndBreathRankLabelView_rankLabel:
                    rankLabel = array.getString(attr);
                    break;
                case R.styleable.HeartAndBreathRankLabelView_rankColor:
                    rankColor = array.getColor(attr, Color.parseColor("#d62002"));
                    break;
                case R.styleable.HeartAndBreathRankLabelView_rankBorder:
                    rankBorder = (int) array.getDimension(attr, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.HeartAndBreathRankLabelView_rankTextColor:
                    textColor = array.getColor(attr, Color.parseColor("#666666"));
                    break;
                case R.styleable.HeartAndBreathRankLabelView_rankTextSize:
                    textSize = (int) array.getDimension(attr, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 13, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.HeartAndBreathRankLabelView_space:
                    space = (int) array.getDimension(attr, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()));
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
        mPaint.setColor(rankColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, (getMeasuredHeight() - rankBorder) / 2, rankBorder,
                (getMeasuredHeight() + rankBorder) / 2, mPaint);
        canvas.drawText(rankLabel, rankBorder + space + 2 + mTextPaint.measureText(rankLabel) / 2,
                getMeasuredHeight() / 2 + mTextPaint.getFontMetrics().descent, mTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) (rankBorder + space + mTextPaint.measureText(rankLabel) + 0.5f +
                4), measureHeight());
    }

    /**
     * 测量高度
     *
     * @return
     */
    private int measureHeight() {
        int textHeight = (int) (mTextPaint.getFontMetrics().bottom -
                mTextPaint.getFontMetrics().top + 0.5f);
        return textHeight > rankBorder ? textHeight : rankBorder;
    }
}
