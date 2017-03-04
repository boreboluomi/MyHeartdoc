package com.electrocardio.custom.elec;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.electrocardio.util.WeakReferenceHandler;

import java.lang.ref.WeakReference;

/**
 * Created by ZhangBo on 2016/03/29.处理异常信息的线程
 */
public abstract class HandleAbnormalInfor extends Thread {

    private WeakReference<Activity> mWeakActivity;
    private WeakReferenceHandler handler;
    private Looper looper;

    public HandleAbnormalInfor(WeakReference<Activity> weakActivity) {
        mWeakActivity = weakActivity;
        handler = new WeakReferenceHandler(mWeakActivity) {
            @Override
            public void handleWeakMessage(Message message) {
                handleAbnormalMessage(message);
            }
        };
    }

    @Override
    public void run() {
        Looper.prepare();
        looper = Looper.myLooper();
        Looper.loop();
    }

    /**
     * 获取handler
     *
     * @return
     */
    public Handler getHandler() {
        return handler;
    }

    /**
     * 退出线程
     */
    public void out() {
        if (looper != null)
            looper.quit();
    }

    public abstract void handleAbnormalMessage(Message message);

}
