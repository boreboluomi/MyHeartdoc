package com.electrocardio.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by ZhangBo on 2016/3/16.
 */
public abstract class WeakReferenceHandler extends Handler {

    private WeakReference<Activity> mActivity;

    public WeakReferenceHandler(WeakReference<Activity> activity) {
        mActivity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        if (mActivity.get() != null)
            handleWeakMessage(msg);
    }

    public abstract void handleWeakMessage(Message message);
}
