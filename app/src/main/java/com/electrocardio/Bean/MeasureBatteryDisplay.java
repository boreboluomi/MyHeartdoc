package com.electrocardio.Bean;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.bluetoothdata.BluetoothStateConfirmClass;

/**
 * Created by ZhangBo on 2016/03/31.
 */
public class MeasureBatteryDisplay {

    private TextView number;// 当前电量的数字显示
    private ImageView imageView;// 显示电量的图片
    private TextView chargeState;// 充电状态
    private RelativeLayout battValAlert;// 电池电量警报
    private boolean isFallOff = true;// 是否脱落
    private long chargeTime = -1l;

    private ImageView cb_RA;// RA状态
    private ImageView cb_LA;// LA状态
    private int battery = 90;// 电池电量

    private FallOffChangeListener mFallOffChangeListener;// 脱落状态改变的事件见监听

    /**
     * 构造函数
     *
     * @param number
     * @param imageView
     * @param chargeState
     */
    public MeasureBatteryDisplay(TextView number, ImageView imageView, TextView chargeState) {
        this.number = number;
        this.imageView = imageView;
        this.chargeState = chargeState;
    }

    /**
     * 判断是否脱落
     *
     * @return
     */
    public boolean isFallOff() {
        return isFallOff;
    }

    public ImageView getRA() {
        return cb_RA;
    }

    /**
     * 设置是否脱落
     *
     * @param fallOff
     */
    public void setFallOff(boolean fallOff) {
        isFallOff = fallOff;
    }

    /**
     * 设置RA和LA灯
     *
     * @param ra
     * @param la
     */
    public void setRAandLA(ImageView ra, ImageView la) {
        cb_RA = ra;
        cb_LA = la;
    }

    /**
     * 设置显示充电状态
     *
     * @param chargeState
     */
    public void setChargeState(TextView chargeState) {
        this.chargeState = chargeState;
    }

    /**
     * 设置电池电量警报
     *
     * @param batteryAlert
     */
    public void setBatteryAlert(RelativeLayout batteryAlert) {
        battValAlert = batteryAlert;
    }

    /**
     * 更新当前电量状态
     *
     * @param stateConfirmClass
     */
    public void updataState(BluetoothStateConfirmClass stateConfirmClass) {
        updataFallOffSate(stateConfirmClass.isFallOff());
        updateNumber(stateConfirmClass.getBatteryValue());
        updateImageView(stateConfirmClass.getBatteryValue());
        updataRAandLA(stateConfirmClass.isFallOff());
        setBatteryValAlert(stateConfirmClass.getBatteryValue());
        updateChargeState(stateConfirmClass.isCharging(), stateConfirmClass.getBatteryValue());
    }

    /**
     * 设置电池电量警报
     *
     * @param value
     */
    public void setBatteryValAlert(int value) {
        battery = value;
        if (battValAlert != null)
            if (value < 10 && value >= 0) {
                if (battValAlert.getVisibility() == View.INVISIBLE)
                    battValAlert.setVisibility(View.VISIBLE);
            } else if (battValAlert.getVisibility() == View.VISIBLE)
                battValAlert.setVisibility(View.INVISIBLE);
    }

    /**
     * 获取电池电量
     *
     * @return
     */
    public int getBattery() {
        return battery;
    }

    /**
     * 更新导联脱落状态
     *
     * @param fallOff
     */
    private void updataFallOffSate(boolean fallOff) {
        if (isFallOff != fallOff) {
            if (mFallOffChangeListener != null)
                mFallOffChangeListener.fallOffChanged(fallOff);
        }
        isFallOff = fallOff;
    }

    /**
     * 更新数字电量显示
     *
     * @param num
     */
    private void updateNumber(int num) {
        if (number != null) {
            if (num != -1)
                number.setText(num + "%");
            else
                number.setText("- -%");
        }
    }

    /**
     * 更新显示电池电量的背景图片
     *
     * @param battryValue
     */
    private void updateImageView(int battryValue) {
        if (imageView != null)
            if (battryValue >= 80)
                imageView.setBackgroundResource(R.mipmap.ic_battery_1);
            else if (battryValue >= 60)
                imageView.setBackgroundResource(R.mipmap.ic_battery_2);
            else if (battryValue >= 40)
                imageView.setBackgroundResource(R.mipmap.ic_battery_3);
            else if (battryValue >= 20)
                imageView.setBackgroundResource(R.mipmap.ic_battery_4);
            else if (battryValue >= 10)
                imageView.setBackgroundResource(R.mipmap.ic_battery_5);
            else if (battryValue >= 0)
                imageView.setBackgroundResource(R.mipmap.ic_battery_6);
            else
                imageView.setBackgroundResource(R.mipmap.ic_battery_7);
    }

    /**
     * 更新RA和LA灯的状态
     *
     * @param fallOff
     */
    public void updataRAandLA(boolean fallOff) {
        if (cb_RA != null)
            cb_RA.setBackgroundResource(fallOff ? R.mipmap.lights_ra_off : R.mipmap.lights_ra_on);
        if (cb_LA != null)
            cb_LA.setBackgroundResource(fallOff ? R.mipmap.lights_la_off : R.mipmap.lights_la_on);
    }

    /**
     * 更新充电状态
     *
     * @param charging
     */
    private void updateChargeState(boolean charging, int batteryValue) {
        if (charging) {
            chargeTime = System.currentTimeMillis();
            if (batteryValue >= 98)
                chargeState.setText("已充满");
            else
                chargeState.setText("正在充电");
            if (chargeState.getVisibility() == View.INVISIBLE)
                chargeState.setVisibility(View.VISIBLE);
        } else {
            if (chargeTime != -1 && !((System.currentTimeMillis() - chargeTime) < 2000)) {
                chargeState.setText("充电中断");
                if (chargeState.getVisibility() == View.INVISIBLE)
                    chargeState.setVisibility(View.VISIBLE);
            } else if (chargeState.getVisibility() == View.VISIBLE)
                chargeState.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 断开连接调用的方法
     */
    public void disConnected() {
        if (cb_RA != null)
            cb_RA.setBackgroundResource(R.mipmap.lights_ra_off);
        if (cb_LA != null)
            cb_LA.setBackgroundResource(R.mipmap.lights_la_off);
    }

    /**
     * 设置导联脱落状态改变的事件监听
     *
     * @param fallOffChangeListener
     */
    public void setFallOffChangeListener(FallOffChangeListener fallOffChangeListener) {
        mFallOffChangeListener = fallOffChangeListener;
    }

    public interface FallOffChangeListener {
        public void fallOffChanged(boolean fallOff);
    }

}
