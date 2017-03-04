package com.electrocardio.util;

import android.os.CountDownTimer;

/**
 * Created by yangzheng on 2016/3/4.
 */
public class CountDownTimerUtils extends CountDownTimer {


    public CountDownTimerUtils(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long l) {
        mlistener.onDownTimerTick(l);
    }

    @Override
    public void onFinish() {
        mlistener.onDownFinish();
    }



    public onDownTimerListener mlistener = null;
    public interface onDownTimerListener{
        void onDownTimerTick(long l);
        void onDownFinish();
    }

    public void setonDownTimerListener(onDownTimerListener listener)
    {
        mlistener = listener;
    }
}
