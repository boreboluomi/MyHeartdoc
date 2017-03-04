package com.electrocardio.custom.elec;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by ZhangBo on 2016/04/08.
 */
public class SectionDataScrollView extends HorizontalScrollView {
    public SectionDataScrollView(Context context) {
        this(context, null);
    }

    public SectionDataScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionDataScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
