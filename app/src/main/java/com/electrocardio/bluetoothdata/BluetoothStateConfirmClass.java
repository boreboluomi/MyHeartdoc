package com.electrocardio.bluetoothdata;

/**
 * Created by Administrator on 2016/3/1.
 */
public class BluetoothStateConfirmClass {

    private boolean fallOff = false;//是否脱落
    private int batteryValue = 0;//电量
    private boolean charging = false;//是否在充电

    public boolean isFallOff() {
        return fallOff;
    }

    public void setFallOff(boolean fallOff) {
        this.fallOff = fallOff;
    }

    public int getBatteryValue() {
        return batteryValue;
    }

    public void setBatteryValue(int batteryValue) {
        this.batteryValue = batteryValue;
    }

    public boolean isCharging() {
        return charging;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }

    @Override
    public String toString() {
        return "BluetoothStateConfirmClass{" +
                "fallOff=" + fallOff +
                ", batteryValue=" + batteryValue +
                ", charging=" + charging +
                '}';
    }

    public BluetoothStateConfirmClass() {
        fallOff = false;
        batteryValue = 0;
        charging = false;
    }

    public BluetoothStateConfirmClass(boolean fallOff, int batteryValue, boolean charging) {
        this.fallOff = fallOff;
        this.batteryValue = batteryValue;
        this.charging = charging;
    }
}
