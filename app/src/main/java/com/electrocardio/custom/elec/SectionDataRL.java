package com.electrocardio.custom.elec;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/04/08.
 */
public class SectionDataRL extends RelativeLayout {

    private int bigNumber = 7;// 大格的数量

    public SectionDataRL(Context context) {
        super(context);
    }

    public SectionDataRL(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionDataRL(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BigNumber, defStyleAttr, 0);
        bigNumber = array.getInteger(R.styleable.BigNumber_bigNumber, 7);
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, bigNumber * 5 * (int) TypedValue.applyDimension(TypedValue.
                COMPLEX_UNIT_MM, 1, getResources().getDisplayMetrics()) + 2);
    }
}
