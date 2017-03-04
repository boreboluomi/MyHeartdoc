package com.electrocardio.bluetoothdata;

/**
 * Created by ZhangBo on 2016/2/29.
 */
public class BluetoothCommand {

    /**
     * 发送复位
     */
    public static byte SendReset = 0x01;

    /**
     * 发送ECG波形
     */
    public static byte SendEcgWave = 0x02;

    /**
     * 发送导联脱落状态
     */
    public static byte SendLeadSts = 0x03;

    /**
     * 发送呼吸波形
     */
    public static byte SendRespWave = 0x05;

    /**
     * 发送电池状态
     */
    public static byte SendBatValue = 0x07;

    /**
     * 发送命令回应
     */
    public static byte SendCmdAck = 0x08;

    /**
     * 发送蓝牙连接
     */
    public static byte SendBluetoothConn = 0x56;

    /**
     * 接收蓝牙状态
     */
    public static final byte ReBluetoothState = 0x01;

    /**
     * 接收ECG波形
     */
    public static final byte ReEcgWave = 0x02;

    /**
     * 接收RESP波形
     */
    public static final byte ReRESPWave = 0x05;

    /**
     * 接收命令回应
     */
    public static final byte ReCommandCallBack = 0x07;

    /**
     * 测量开始指令
     *
     * @return
     */
    public static byte[] getSEStartMeasure() {
        return new byte[]{0x51, (byte) 0x81, 0x0d, 0x0a};// 正式版命令
    }

    /**
     * 测量停止指令
     *
     * @return
     */
    public static byte[] getSEStopMeasure() {
        return new byte[]{0x51, (byte) 0x82, 0x0d, 0x0a};
    }

    /**
     * 启动1mV校准指令
     *
     * @return
     */
    public static byte[] getSEStart1mV() {
        return new byte[]{0x52, (byte) 0x81, 0x0d, 0x0a};
    }

    /**
     * 断开蓝牙连接指令
     *
     * @return
     */
    public static byte[] getSEDisConnectBluetooth() {
        return new byte[]{0x53, (byte) 0x81, 0x0d, 0x0a};
    }

    /**
     * 蓝牙状态确认指令
     *
     * @return
     */
    public static byte[] getSEConfirmBluetoothState() {
        return new byte[]{0x54, (byte) 0x81, 0x0d, 0x0a};
    }

    /**
     * 电量校准指令
     *
     * @return
     */
    public static byte[] getSEBatteryCorrect() {
        return new byte[]{0x55, (byte) 0x81, 0x0d, 0x0a};
    }

    /**
     * 蓝牙状态指令
     * @return
     */
    public static byte[] getSEBluetoothState() {
        return new byte[]{0x56, (byte) 0x81, 0x0d, 0x0a};
    }
}
