package com.electrocardio.base;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;

import com.electrocardio.Bean.BluetoothCommunicateClass;
import com.electrocardio.reside.ResideMenu;
import com.electrocardio.terminalIO.TIOPeripheral;
import com.electrocardio.util.HandleUploadData;

import java.util.Stack;

/**
 * Created by yangzheng on 2015/12/28.
 */
public class BaseApplication extends Application {
    public static final String PERIPHERAL_ID_NAME = "com.stollmann.tiov2sample.peripheralId";
    private BluetoothDevice blueDevcie = null;
    private static Stack<FragmentActivity> mActivities;
    private static BaseApplication instance;
    private TIOPeripheral peripheralByAddress;
    private ResideMenu mResideMenu;
    private BluetoothCommunicateClass mBlueCommClass;// 蓝牙通信的类
    private HandleUploadData handleUploadData;// 处理上传数据的类

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mActivities = new Stack<>();
        // SMSSDK.initSDK(this, "10fb7a0462330", "1cc6580b92d19bf67c018d4db912e453");
        // TIOManager.initialize(this.getApplicationContext());
    }

    /**
     * 获取处理上传数据的类
     *
     * @return
     */
    public HandleUploadData getHandleUploadData() {
        return handleUploadData;
    }

    /**
     * 设置处理上传数据的类
     *
     * @param handleUploadData
     */
    public void setHandleUploadData(HandleUploadData handleUploadData) {
        this.handleUploadData = handleUploadData;
    }

    /**
     * 设置蓝牙通信类
     *
     * @param blueCommClass
     */
    public void setBlueCommuCls(BluetoothCommunicateClass blueCommClass) {
        mBlueCommClass = blueCommClass;
    }

    /**
     * 获取蓝牙通信类
     *
     * @return
     */
    public BluetoothCommunicateClass getBlueCommuCls() {
        return mBlueCommClass;
    }

    public void setTIOPeripheral(TIOPeripheral tIOPeripheral) {
        peripheralByAddress = tIOPeripheral;
    }

    public TIOPeripheral getTIOPeripheral() {
        return peripheralByAddress;
    }

    public void setResideMenu(ResideMenu re) {
        this.mResideMenu = re;
    }

    public ResideMenu getResideMenu() {
        return mResideMenu;
    }

    public void addActivity(FragmentActivity activity) {
        if (activity == null) {
            return;
        }
        mActivities.push(activity);
    }

    public void setBluetoothDevice(BluetoothDevice device) {
        blueDevcie = device;
    }

    public BluetoothDevice getBluetoothDevice() {
        return blueDevcie;
    }

    /**
     * 获取BaseApplication的实例
     *
     * @return
     */
    public static BaseApplication getInstance() {
        return instance;
    }

    protected Stack<FragmentActivity> getActivityStack() {
        return mActivities;
    }

    protected FragmentActivity getTopActivity() {
        if (mActivities.empty()) {
            return null;
        } else {
            return mActivities.peek();
        }
    }

    public void removeActivity(Activity activity) {
        if (activity == null) {
            return;
        }

        if (mActivities.contains(activity)) {
            mActivities.remove(activity);
        }
    }

    public void exit(boolean isAppExit) {
        if (!mActivities.empty()) {
            for (Activity activity : mActivities) {
                if (activity != null && !activity.isFinishing())
                    activity.finish();
            }
            mActivities.clear();
        }
        if (isAppExit) {
            System.exit(0);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
