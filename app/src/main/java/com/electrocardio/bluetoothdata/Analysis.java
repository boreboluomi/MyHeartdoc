package com.electrocardio.bluetoothdata;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.electrocardio.util.ThreadWithTime;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/2/29.
 */
public class Analysis {

    private int CURRENTPACKAGEID = -1;// 当前数据包头
    private final int BLUETOOTHSTATECONFIRM = 0;// 蓝牙状态确认ID
    private final int ECGWAVEID = 1;// ECG波形ID
    private final int RESPWAVEIDID = 2;// RESP(呼吸)波形ID
    private final int CMDACKID = 3;// 命令回应ID
    private final int LOASEPACKAGERATE = 4;

    private Context mContext;
    private ArrayList<Byte> blueStateConfList;// 蓝牙状态确认数据集合
    private ArrayList<Byte> ecgWaveList;// ECG波形数据集合
    private ArrayList<Byte> respWaveList;// 呼吸波形数据集合
    private ArrayList<Byte> cmBackList;// 命令回应数据集合
    private BluetoothStateConfirmClass blueStateConfirm;
    private byte switchByte;// 用于Byte与byte转化的数据
    private ReceivedDataListener mReceivedDataListener;// 解析完数据的监听
    private long currentTime = 0;
    private int mCount = 0;
    private boolean isFirstEcG = true;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOASEPACKAGERATE:
                    int number = (int) msg.obj;
                    number = number > 250 ? 250 : number;
                    if (number >= 5 && !isFirstEcG)
                        if (mReceivedDataListener != null)
                            mReceivedDataListener.losePackageRate(number);
                    break;
            }
        }
    };

    public Analysis(Context context) {
        blueStateConfList = new ArrayList<>();
        ecgWaveList = new ArrayList<>();
        respWaveList = new ArrayList<>();
        cmBackList = new ArrayList<>();
        blueStateConfirm = new BluetoothStateConfirmClass();
        mContext = context;
    }

    public void reset() {
        blueStateConfList.clear();
        ecgWaveList.clear();
        respWaveList.clear();
        cmBackList.clear();
        isFirstEcG = true;
    }

    /**
     * 计算丢包率
     */
    public void computeLosePackageRate() {
        new ThreadWithTime(currentTime) {
            @Override
            public void run() {
                while (getCurrentTime() == currentTime) {
                    mCount = 0;
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (getCurrentTime() == currentTime)
                        handler.obtainMessage(LOASEPACKAGERATE, mCount).sendToTarget();
                }
            }
        }.start();
    }

    public void receivedData(byte[] dataArr) {
        for (byte data : dataArr) {
            switch (data) {
                case BluetoothCommand.ReBluetoothState:// 检测到蓝牙状态确认数据头
                    CURRENTPACKAGEID = BLUETOOTHSTATECONFIRM;
                    analBlueStateConfirmData(blueStateConfList);
                    System.out.println("检测到蓝牙状态确认数据头:" +
                            (BluetoothCommand.ReBluetoothState & 0xff));
                    break;
                case BluetoothCommand.ReEcgWave:// 检测到ECG波形数据头
                    if (isFirstEcG) {
                        computeLosePackageRate();
                        isFirstEcG = false;
                    } else {
                        mCount++;
                    }
                    CURRENTPACKAGEID = ECGWAVEID;
                    analysisBluetoothData(ecgWaveList, BluetoothCommand.ReEcgWave,
                            BluetoothCommandLength.ECGWave);
                    System.out.println("检测到ECG波形数据头:" +
                            (BluetoothCommand.ReEcgWave & 0xff));
                    break;
                case BluetoothCommand.ReRESPWave:// 检测到呼吸波形数据头
                    CURRENTPACKAGEID = RESPWAVEIDID;
                    analysisBluetoothData(respWaveList, BluetoothCommand.ReRESPWave,
                            BluetoothCommandLength.RESPWave);
//                    System.out.println("检测到呼吸波形数据头:" +
//                            (BluetoothCommand.ReRESPWave & 0xff));
                    break;
                case BluetoothCommand.ReCommandCallBack:// 检测到命令回应数据头
                    CURRENTPACKAGEID = CMDACKID;
                    commandCallBack(cmBackList);
                    System.out.println("检测到命令回应数据头:" +
                            (BluetoothCommand.ReCommandCallBack & 0xff));
                    break;
                default:
                    addData(data);
                    break;
            }
        }
    }

    /**
     * 在对应集合中添加数据
     */
    private void addData(byte data) {
        switch (CURRENTPACKAGEID) {
            case BLUETOOTHSTATECONFIRM:// 蓝牙状态确认ID
                blueStateConfList.add(data);
                if (blueStateConfList.size() == (BluetoothCommandLength.BluetoothStateConfirm - 1))
                    analBlueStateConfirmData(blueStateConfList);
                break;
            case ECGWAVEID:// ECG波形ID
                ecgWaveList.add(data);
                if (ecgWaveList.size() == (BluetoothCommandLength.ECGWave - 1))
                    analysisBluetoothData(ecgWaveList, BluetoothCommand.ReEcgWave,
                            BluetoothCommandLength.ECGWave);
                break;
            case RESPWAVEIDID:// 呼吸波形ID
                respWaveList.add(data);
                if (respWaveList.size() == (BluetoothCommandLength.RESPWave - 1))
                    analysisBluetoothData(respWaveList, BluetoothCommand.ReRESPWave,
                            BluetoothCommandLength.RESPWave);
                break;
            case CMDACKID:// 命令回应ID
                cmBackList.add(data);
                if (cmBackList.size() == (BluetoothCommandLength.CommandCallBack - 1))
                    commandCallBack(cmBackList);
                break;
            default:
                break;
        }
    }

    /**
     * 设置receivedDataListener
     *
     * @param receivedDataListener
     */
    public void setReceivedDataListener(ReceivedDataListener receivedDataListener) {
        mReceivedDataListener = receivedDataListener;
    }

    /**
     * 解析蓝牙数据
     *
     * @param packageID
     * @param packageLength
     */
    private void analysisBluetoothData(ArrayList<Byte> arrayList, byte packageID, int packageLength) {
        if (arrayList == null || arrayList.size() == 0)
            return;
        if (arrayList.size() == (packageLength - 1)) {
            switch (packageID) {
                case BluetoothCommand.ReEcgWave:
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.reEcgWaveDataBate(analysisECGData(arrayList));
                    break;
                case BluetoothCommand.ReRESPWave:
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.reRespWaveData(analysisBluetoothData(arrayList));
                    break;
            }
        }
        arrayList.clear();
    }


    /**
     * 解析蓝牙数据
     *
     * @param arrayList
     */
    private int analysisBluetoothData(ArrayList<Byte> arrayList) {
        byte standard = arrayList.get(0);
        int result;
        int[] array = new int[arrayList.size() - 2];
        for (int i = 1; i < arrayList.size() - 1; i++) {
            if ((standard & (0x01 << (i - 1))) == (0x01 << (i - 1)))
                array[i - 1] = (arrayList.get(i) & 0xff);
            else
                array[i - 1] = (arrayList.get(i) & 0x7f);
        }
        result = (array[0] << 16) + (array[1] << 8) + array[2];
        if (result > 8388608)
            result -= 8388608 * 2;
        return result;
    }

    /**
     * 解析ECG波形数据
     *
     * @param arrayList
     * @return
     */
    private int[] analysisECGData(ArrayList<Byte> arrayList) {
        byte standardFirst = arrayList.get(0);
        byte standardSecond = arrayList.get(1);
        int[] result = new int[4];
        int[] array = new int[12];
        for (int i = 2; i < 8; i++) {
            if ((standardFirst & (0x01 << (i - 2))) == (0x01 << (i - 1)))
                array[i - 2] = (arrayList.get(i) & 0xff);
            else
                array[i - 2] = (arrayList.get(i) & 0x7f);
        }
        for (int i = 8; i < arrayList.size() - 1; i++) {
            if ((standardSecond & (0x01 << (i - 8))) == (0x01 << (i - 1)))
                array[i - 2] = (arrayList.get(i) & 0xff);
            else
                array[i - 2] = (arrayList.get(i) & 0x7f);
        }
        for (int i = 0; i < 4; i++) {
            result[i] = (array[i * 3] << 16) + (array[i * 3 + 1] << 8) + array[i * 3 + 2];
            if (result[i] > 8388608)
                result[i] -= 8388608 * 2;
        }
        return result;
    }

    /**
     * 解析蓝牙状态确认数据
     *
     * @param arrayList
     */
    private void analBlueStateConfirmData(ArrayList<Byte> arrayList) {
        if (arrayList == null || arrayList.size() == 0)
            return;
        if (arrayList.size() == (BluetoothCommandLength.BluetoothStateConfirm - 1)) {
            switchByte = arrayList.get(0);
            if (switchByte == (byte) 0x81)
                blueStateConfirm.setFallOff(true);
            if (switchByte == (byte) 0x80)
                blueStateConfirm.setFallOff(false);
            switchByte = arrayList.get(1);
            blueStateConfirm.setBatteryValue(switchByte & 0x7f);
//            switchByte = arrayList.get(2);
//            if (switchByte == (byte) 0x81)
//                blueStateConfirm.setCharging(true);
//            if (switchByte == (byte) 0x80)
//                blueStateConfirm.setCharging(false);
            if (mReceivedDataListener != null)
                mReceivedDataListener.reBluetoothState(blueStateConfirm);
        }
        arrayList.clear();
    }

    /**
     * 解析发送命令回应数据
     *
     * @param arrayList
     */
    private void commandCallBack(ArrayList<Byte> arrayList) {
        if (arrayList == null || arrayList.size() == 0)
            return;
        if (arrayList.size() == (BluetoothCommandLength.CommandCallBack - 1)) {
            switchByte = arrayList.get(0);
            switch (switchByte) {
                case (byte) 0x81:// 时间校准正确
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.timeCorrectCallBack(true);
                    break;
                case (byte) 0x82:// 时间效准错误
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.timeCorrectCallBack(false);
                    break;
                case (byte) 0x83:// 1mv校准开始
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.oneMVCorrectCallBack(true);
                    break;
                case (byte) 0x84:// 1mv校准结束
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.oneMVCorrectCallBack(false);
                    break;
                case (byte) 0x85:// 测量开始命令回复
                    isFirstEcG = true;
                    currentTime = System.currentTimeMillis();
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.measureCommandCallBack(true);
                    break;
                case (byte) 0x86:// 测量结束命令回复
                    if (mReceivedDataListener != null) {
                        isFirstEcG = true;
                        mReceivedDataListener.measureCommandCallBack(false);
                    }
                    break;
                case (byte) 0x87:// 断开蓝牙命令回复
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.disConnectCallBack();
                    break;
                case (byte) 0x89:// Mark按键按下
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.othersCallBack(switchByte);
                    break;
                case (byte) 0x8a:// 低电量关机命令回复
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.othersCallBack(switchByte);
                    break;
                case (byte) 0x8b:// 结束服务并关机命令回复
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.othersCallBack(switchByte);
                    break;
                case (byte) 0x8c:// 设备重新启动命令回复
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.othersCallBack(switchByte);
                    break;
                case (byte) 0x8d:// 蓝牙连接命令回复
                    if (mReceivedDataListener != null)
                        mReceivedDataListener.blueConnCommandCallBack();
                    break;
            }
        }
        arrayList.clear();
    }


    public interface ReceivedDataListener {
        void reBluetoothState(BluetoothStateConfirmClass blueStateConfirm);

        void reEcgWaveData(int data);

        void reEcgWaveDataBate(int[] dataArray);

        void reRespWaveData(int data);

        void timeCorrectCallBack(boolean right);

        void oneMVCorrectCallBack(boolean start);

        void measureCommandCallBack(boolean measureComm);

        void disConnectCallBack();

        void blueConnCommandCallBack();

        void othersCallBack(byte data);

        void losePackageRate(int rate);
    }

}
