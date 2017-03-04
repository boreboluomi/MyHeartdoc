package com.electrocardio.base;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.electrocardio.R;

/**
 * Created by Administrator on 2015/12/28.
 */
public abstract class ProfileActivity extends AbstractActivity {
    private LinearLayout mLayoutProgress;
    private FrameLayout mScreenLayout;
    private static boolean isScreeObserver = true;

    @Override
    protected void onStart() {
        super.onStart();
        if (checkFromOut()) {
            onFromOut();
        }
    }


    public static boolean checkFromOut() {
        return !isScreeObserver;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunningForeground();
    }

    @Override
    protected void onFromOut() {
        super.onFromOut();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunningForeground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      /*  if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
     //   CellularHelper.saveCellularType(this);
    }

    protected void openEventBus() {
       /* mEventBusOpened = true;
        EventBus.getDefault().register(this);*/
    }


    public void isRunningForeground() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        String packageName = getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
            isScreeObserver = true;
        } else {
            isScreeObserver = false;
        }
    }

    @Override
    protected boolean isNetworkConnected() {
        return false;
    }

    @Override
    protected boolean isWifiConnected() {
        return false;
    }

    @Override
    protected boolean isMobileConnected() {
        return false;
    }


    protected FrameLayout getScreenLayout() {
        if (mScreenLayout == null) mScreenLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.new_activity_screen, null);
        return mScreenLayout;
    }
}
