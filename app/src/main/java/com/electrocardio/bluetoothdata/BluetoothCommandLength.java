package com.electrocardio.bluetoothdata;

/**
 * Created by ZhangBo on 2016/3/1.蓝牙指令长度
 */
public class BluetoothCommandLength {

    /**
     * 蓝牙状态确认长度
     */
    public static int BluetoothStateConfirm = 3;

    /**
     * ECG波形数据长度
     */
    public static int ECGWave = 16;

    /**
     * RESP波形数据长度
     */
    public static int RESPWave = 6;

    /**
     * 命令回应长度
     */
    public static int CommandCallBack = 2;
}
