package com.electrocardio.custom.elec;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.electrocardio.R;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/03/18.
 */
public class HistorySlideCheckLayout extends RelativeLayout {

    private HistorySlideCheckView hscv;
    private PointerContentView contentView;
    private String unitLabel = "bpm";// 单位
    private int mCurvaColor = Color.parseColor("#ed145b");
    private int contentWidth;

    public HistorySlideCheckLayout(Context context) {
        this(context, null);
    }

    public HistorySlideCheckLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistorySlideCheckLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.relativalayout_historyslidecheck, this, true);
        hscv = (HistorySlideCheckView) this.findViewById(R.id.hscv);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HistorySlideCheckLayout);
        for (int i = 0; i < array.getIndexCount(); i++) {
            int a = array.getIndex(i);
            switch (a) {
                case R.styleable.HistorySlideCheckLayout_HSCL_UnitLabel:
                    unitLabel = array.getString(a);
                    break;
                case R.styleable.HistorySlideCheckLayout_curveLineColor:
                    mCurvaColor = array.getColor(a, Color.parseColor("#ed145b"));
                    break;
            }
        }
        array.recycle();
        initView(context);
    }

    /**
     * 设置时间
     *
     * @param startTime
     * @param timeLength
     */
    public void setTimeStampLabel(long startTime, long timeLength) {
        hscv.setTimeStampLabel(startTime, timeLength);
    }

    /**
     * 设置数据
     *
     * @param dataArray
     */
    public void setDataArray(ArrayList<Integer> dataArray) {
        if (dataArray != null && dataArray.size() > 0) {
            if (hscv != null)
                hscv.setPointYList(dataArray);
            contentView.setText(dataArray.get(0) + "");
        }
    }

    /**
     * 初始化控件
     */
    private void initView(Context context) {
        contentWidth = (int) TypedValue.applyDimension(TypedValue.
                COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());
        contentView = new PointerContentView(context);
        contentView.setUnitLabel(unitLabel);
        this.addView(contentView);
        LayoutParams params = new LayoutParams(contentWidth, (int) TypedValue.
                applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()));
        params.setMargins((int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
                getResources().getDisplayMetrics()) - contentWidth / 2) - 1, 0, 0, 0);
        contentView.setLayoutParams(params);
        hscv.setCurveLineColor(mCurvaColor);
        hscv.setScrollListener(new HistorySlideCheckView.ScrollListener() {
            @Override
            public void onScrolled(int position, int number) {
                contentView.setLeft(position - contentView.getMeasuredWidth() / 2);
                LayoutParams params = new LayoutParams((int) TypedValue.applyDimension(TypedValue.
                        COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics()), (int) TypedValue.
                        applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()));
                params.setMargins(position - contentView.getMeasuredWidth() / 2 - 1, 0, 0, 0);
                contentView.setLayoutParams(params);
                contentView.setText(number + "");
            }
        });
    }
}
