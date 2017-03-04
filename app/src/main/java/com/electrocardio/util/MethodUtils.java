package com.electrocardio.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by ZhangBo on 2016/3/2.
 */
public class MethodUtils {

    private static MethodUtils instance;

    private MethodUtils() {

    }

    public static MethodUtils getInstance() {
        if (instance == null)
            instance = new MethodUtils();
        return instance;
    }

    /**
     * 设置状态栏的颜色
     *
     * @param activity
     */
    public void setStateBarColor(Activity activity) {
        setStateBarColor(activity, "#1ebad1");
    }

    /**
     * 设置状态栏的颜色
     *
     * @param activity
     * @param color
     */
    public void setStateBarColor(Activity activity, String color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true, activity);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(Color.parseColor(color));
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on, Context activity) {
        Window win = ((Activity) activity).getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        // winParams.flags |= bits;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
