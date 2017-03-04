package com.electrocardio.custom.elec;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/03/18.
 */
public class PointerContentView extends RelativeLayout {

    private TextView tv_bpm;// 显示内容
    private TextView tv_bpmLabel;// 显示单位

    public PointerContentView(Context context) {
        this(context, null);
    }

    public PointerContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointerContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.pointer_content, this, true);
        tv_bpm = (TextView) findViewById(R.id.tv_bpmNumber);
        tv_bpmLabel = (TextView) findViewById(R.id.tv_bpmLabel);
    }

    /**
     * 设置内容
     *
     * @param content
     */
    public void setText(String content) {
        tv_bpm.setText(content);
    }

    /**
     * 设置单位
     *
     * @param unitLabel
     */
    public void setUnitLabel(String unitLabel) {
        tv_bpmLabel.setText(unitLabel);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()));
    }
}
