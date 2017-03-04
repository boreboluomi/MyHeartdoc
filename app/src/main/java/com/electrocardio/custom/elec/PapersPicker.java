package com.electrocardio.custom.elec;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.electrocardio.R;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/2/24.
 */
public class PapersPicker extends LinearLayout {

    private int itemNumber = 3;// 条目数量
    private int lineColor = Color.parseColor("#b3b3b3");// 线的颜色
    private float msakHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45,
            getResources().getDisplayMetrics());
    private boolean noEmpty = true;
    private int normalTextColor = Color.parseColor("#999999");
    private float normalTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
            getResources().getDisplayMetrics());
    private int selectTextColor = Color.parseColor("#333333");
    private float selectTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20,
            getResources().getDisplayMetrics());
    private int unitHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45,
            getResources().getDisplayMetrics());
    private boolean isEnable = true;

    public PapersPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        // this.context = context;
        LayoutInflater.from(context).inflate(R.layout.papers_picker, this, true);
        // View.inflate(context, R.layout.papers_picker, this);
        papersPicker = (ScrollerNumberPicker) findViewById(R.id.papers);

        TypedArray attribute = context.obtainStyledAttributes(attrs,
                R.styleable.PapersPicker);
        unitHeight = (int) attribute.getDimension(
                R.styleable.PapersPicker_unitHight, 32);
        normalTextSize = (int) attribute.getDimension(
                R.styleable.PapersPicker_normalTextSize, 14.0f);
        selectTextSize = attribute.getDimension(
                R.styleable.PapersPicker_selecredTextSize, 22.0f);
        itemNumber = attribute.getInt(R.styleable.PapersPicker_itemNumber, 7);
        normalTextColor = attribute.getColor(
                R.styleable.PapersPicker_normalTextColor, 0xff000000);
        selectTextColor = attribute.getColor(
                R.styleable.PapersPicker_selecredTextColor, 0xffff0000);
        lineColor = attribute.getColor(R.styleable.PapersPicker_lineColor,
                0xff000000);
        msakHeight = attribute.getDimension(R.styleable.PapersPicker_maskHight,
                48.0f);
        noEmpty = attribute.getBoolean(R.styleable.PapersPicker_noEmpty, false);
        isEnable = attribute
                .getBoolean(R.styleable.PapersPicker_isEnable, true);
        attribute.recycle();

        papersPicker.isEnable(isEnable);
        papersPicker.isNoEmpty(noEmpty);
        papersPicker.setMaskHight(msakHeight);
        papersPicker.setLineColor(lineColor);
        papersPicker.setSelectedColor(selectTextColor);
        papersPicker.setNormalColor(normalTextColor);
        papersPicker.setItemNumber(itemNumber);
        papersPicker.setSelectedFont(selectTextSize);
        papersPicker.setNormalFont(normalTextSize);
        papersPicker.setUnitHeight(unitHeight);

        papersPicker.init();
    }

    public PapersPicker(Context context) {
        this(context, null);
    }

    private PapersPickerTextChangeListener mPapersPickerTextChangeListener;
    private ScrollerNumberPicker papersPicker;
    private int tempPapersIndex = 1;
    // private Context context;

    public ArrayList<String> list;

    public void setPapersPickerTextChangeListener(
            PapersPickerTextChangeListener papersPickerTextChangeListener) {
        mPapersPickerTextChangeListener = papersPickerTextChangeListener;
    }

    public void setList(ArrayList<String> list, int selectId) {
        tempPapersIndex = selectId;
        this.list = list;
        init(selectId);
    }

    public void init(int selectId) {

        papersPicker.setData(list);
        papersPicker.setDefault(selectId);
        papersPicker.setOnSelectListener(new ScrollerNumberPicker.OnSelectListener() {
            @Override
            public void selecting(int id, String text) {
            }

            @Override
            public void endSelect(int id, String text) {
                if (mPapersPickerTextChangeListener != null)
                    mPapersPickerTextChangeListener.onTextChanged(text);
                tempPapersIndex = id;
            }
        });
    }

    /**
     * 获取当前PapersIndex
     *
     * @return
     */
    public int getCurrentPapersIndex() {
        return tempPapersIndex;
    }

    public String getPapers() {
        return papersPicker.getSelectedText();
    }

    public interface PapersPickerTextChangeListener {
        public void onTextChanged(String text);
    }
}
