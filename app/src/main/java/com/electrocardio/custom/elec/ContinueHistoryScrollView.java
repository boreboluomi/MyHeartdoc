package com.electrocardio.custom.elec;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by ZhangBo on 2016/3/17.
 */
public class ContinueHistoryScrollView extends ScrollView {
    private int CurrentX;
    private int CurrentY;

    public ContinueHistoryScrollView(Context context) {
        super(context);
    }

    public ContinueHistoryScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContinueHistoryScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch(ev.getAction()){
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                if (Math.abs(x - CurrentX) >= Math.abs(y - CurrentY))
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
