package com.electrocardio.Bean;

import android.app.Activity;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aceuni.uart.sdk.AUBlutoothHolder;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.bluetoothdata.Analysis;
import com.electrocardio.bluetoothdata.BluetoothCommand;
import com.electrocardio.bluetoothdata.BluetoothStateConfirmClass;
import com.electrocardio.custom.elec.MeasureButton;
import com.electrocardio.popup.SavaDataPop;

/**
 * Created by ZhangBo on 2016/04/06.
 */
public class HandleDisconnectState {

    private Activity mActivity;// 上下文环境
    private SavaDataPop savaDataPop;// 提示pop
    private MeasureButton measureButton;// 测量按钮
    private ImageView mDropDown;// 下拉按钮
    private TextView tvDisconnect;// 断开连接按钮
    private TextView heartRate;// 心率label
    private MeasureBatteryDisplay batteryDisplay;// 处理电池状态的类
    private Analysis analysis;// 解析数据的类
    private AUBlutoothHolder holder;// 处理蓝牙状态的类
    private StateListener mStateListener;// 状态监听器
    private long disConMini = -1;// 断开连接的时间

    private Handler handler = new Handler();

    /**
     * 构造函数
     *
     * @param activity
     */
    public HandleDisconnectState(Activity activity) {
        mActivity = activity;
        savaDataPop = new SavaDataPop(mActivity);
        savaDataPop.setFocusable(true);
        savaDataPop.setTouchable(true);
        savaDataPop.setOutsideTouchable(false);
    }

    /**
     * 竖屏中init
     *
     * @param meaButton
     * @param dropDown
     * @param disConnect
     */
    public void initOnMeasure(MeasureBatteryDisplay batDisplay, MeasureButton meaButton,
                              ImageView dropDown, TextView disConnect, TextView heartRateLabel) {
        batteryDisplay = batDisplay;// 处理电池状态的类
        measureButton = meaButton;// 测量按钮
        mDropDown = dropDown;// 下拉按钮
        tvDisconnect = disConnect;// 断开连接按钮
        heartRate = heartRateLabel;// 心率label
    }

    /**
     * 初始化操作
     *
     * @param analysis
     * @param holder
     */
    public void init(Analysis analysis, AUBlutoothHolder holder) {
        this.analysis = analysis;// 解析数据的类
        this.holder = holder;// 处理蓝牙状态的类
    }

    /**
     * 断开连接
     *
     * @param callBack
     * @param isSixtyMeasure
     */
    public void disConnected(boolean callBack, boolean isSixtyMeasure) {
        if (callBack) {// 如果是手动断开连接
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (measureButton != null) {
                        measureButton.setCurrentState(MeasureButton.SCAN);
                        mDropDown.setVisibility(View.INVISIBLE);
                        tvDisconnect.setText("无连接设备");
                        BluetoothStateConfirmClass blueConFirm = new BluetoothStateConfirmClass();
                        blueConFirm.setCharging(false);
                        blueConFirm.setFallOff(true);
                        blueConFirm.setBatteryValue(-1);
                        batteryDisplay.updataState(blueConFirm);
                    }
                    heartRate.setText("- -");// 显示心率的label
                    if (BaseApplication.getInstance().getBlueCommuCls() != null) {
                        BaseApplication.getInstance().getBlueCommuCls().setMEASUREBUTTONSTATE(MeasureButton.SCAN);
                        BaseApplication.getInstance().getBlueCommuCls().setIsFallOff(true);
                        batteryDisplay.disConnected();
                    }
                }
            });
        } else {
            disConMini = System.currentTimeMillis();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if ((System.currentTimeMillis() - disConMini) >= 599000) {
                        if (holder.connectedDevice() == null) {
                            holder.stopScan();
                            if (mStateListener != null)
                                mStateListener.SaveContinueMeasureData();
                            if (savaDataPop.isShowing())
                                savaDataPop.dismiss();
                            // holder.disconnect();
                        }
                    }
                }
            }, 600000);
            if (isSixtyMeasure) {// 60秒测量下的异常断开
                if (measureButton != null) {
                    measureButton.setCurrentState(MeasureButton.SCAN);
                    mDropDown.setVisibility(View.INVISIBLE);
                    tvDisconnect.setText("无连接设备");
                    BluetoothStateConfirmClass blueConFirm = new BluetoothStateConfirmClass();
                    blueConFirm.setCharging(false);
                    blueConFirm.setFallOff(true);
                    blueConFirm.setBatteryValue(-1);
                    heartRate.setText("- -");// 显示心率的label
                    batteryDisplay.updataState(blueConFirm);
                }
                if (BaseApplication.getInstance().getBlueCommuCls() != null) {
                    BaseApplication.getInstance().getBlueCommuCls().setMEASUREBUTTONSTATE(MeasureButton.SCAN);
                    BaseApplication.getInstance().getBlueCommuCls().setIsFallOff(true);
                    batteryDisplay.disConnected();
                    if (mStateListener != null)
                        mStateListener.SaveSixtySecondsMeasureData();
                }
            } else {// 连续测量下的异常断开
                if (BaseApplication.getInstance().getBlueCommuCls() != null) {
                    if (savaDataPop != null) {
                        savaDataPop.setAlertContent("正在重连设备");
                        savaDataPop.showAtLocation(measureButton, Gravity.CENTER, 0, 0);
                    }
                    analysis.reset();
                    holder.startLeScan();
                }
            }
        }
    }

    /**
     * 重新连接成功
     */
    public void reConnected() {
        if (savaDataPop != null && savaDataPop.isShowing())
            savaDataPop.dismiss();
        if (holder != null)
            for (int i = 0; i < 3; i++) {
                holder.sendData(BluetoothCommand.getSEBluetoothState());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        disConMini = System.currentTimeMillis();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (holder != null) {
                    holder.sendData(BluetoothCommand.getSEStartMeasure());
                }
                if (savaDataPop.isShowing())
                    savaDataPop.dismiss();
            }
        }, 1000);
    }

    /**
     * 设置状态监听器
     *
     * @param stateListener
     */
    public void setStateListener(StateListener stateListener) {
        mStateListener = stateListener;
    }

    public interface StateListener {
        public void SaveSixtySecondsMeasureData();// 存储60秒的测量数据

        public void SaveContinueMeasureData();// 存储连续测量的数据
    }

}
