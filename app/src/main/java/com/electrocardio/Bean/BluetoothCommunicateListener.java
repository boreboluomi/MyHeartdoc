package com.electrocardio.Bean;

import android.bluetooth.BluetoothDevice;

import com.electrocardio.bluetoothdata.BluetoothStateConfirmClass;

/**
 * Created by ZhangBo on 2016/04/16.
 */
public interface BluetoothCommunicateListener {

    /**
     * 断开蓝牙连接
     */
    public void disConnected();

    /**
     * 获取服务完毕
     */
    public void stateReady();

    /**
     * 接收到蓝牙状态
     *
     * @param blueStateConfirm
     */
    public void reBluetoothState(BluetoothStateConfirmClass blueStateConfirm);

    /**
     * 测量命令回复
     *
     * @param measureComm
     */
    public void measureCommandCallBack(boolean measureComm);

    /**
     * 断开连接命令回复
     */
    public void disConnectCallBack();

    /**
     * 丢包率解析完毕
     *
     * @param rate
     */
    public void lostPackageRate(int rate);

    /**
     * 测量时间
     *
     * @param timeLabel
     */
    public void measureTime(String timeLabel);

    /**
     * 计算后的心电波形数据
     *
     * @param dataArray
     */
    public void calculatedMeasreData(float[] dataArray);

    /**
     * 心率
     *
     * @param heartRate
     */
    public void heartRate(int heartRate);

    /**
     * 存储完毕
     */
    public void saveOver();

}
