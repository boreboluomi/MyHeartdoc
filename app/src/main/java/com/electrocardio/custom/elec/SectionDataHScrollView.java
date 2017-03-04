package com.electrocardio.custom.elec;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by ZhangBo on 2016/04/08.
 */
public class SectionDataHScrollView extends ScrollView {

    private int CurrentX;
    private int CurrentY;

    public SectionDataHScrollView(Context context) {
        this(context, null);
    }

    public SectionDataHScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionDataHScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                if (Math.abs(x - CurrentX) >= Math.abs(y - CurrentY))
                    return false;
                else if (y > getMeasuredHeight() / 3 * 2)
                    return false;
                CurrentX = x;
                CurrentY = y;
                break;
            case MotionEvent.ACTION_DOWN:
                CurrentX = (int) ev.getX();
                CurrentY = (int) ev.getY();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
