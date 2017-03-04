package com.electrocardio.Bean;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.aceuni.uart.sdk.AUBlutoothHolder;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.bluetoothdata.AUStateChangeCallbackIml;
import com.electrocardio.bluetoothdata.Analysis;
import com.electrocardio.bluetoothdata.BluetoothStateConfirmClass;
import com.electrocardio.custom.elec.MeasureButton;
import com.electrocardio.databasebean.AbnormalSignInfor;
import com.electrocardio.databasebean.SaveContinueMeasureData;
import com.electrocardio.databasebean.SaveSixtySecondsMeasureData;
import com.electrocardio.jni.ECGResultBean;
import com.electrocardio.jni.JniCallbackData;
import com.electrocardio.jni.RealBeatVal;
import com.electrocardio.popup.AdjustScopePop;
import com.electrocardio.popup.FallOffAlertPop;
import com.electrocardio.popup.SavaDataPop;
import com.electrocardio.util.ThreadUtils;
import com.electrocardio.util.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/04/18.
 */
public class BluetoothCommunicateClass {

    private final int SCAN_BLUETOOTH_DEVICE = 1;// 浏览蓝牙设备
    private final int HEART_RATE = 2;// 心率
    private final int BLUETOOTH_STATE = 3;// 检查导联是否脱落
    private final int MEASURE_TIME = 4;// 测量时长
    private final int LOST_PACKAGE = 5;// 丢包率

    private Context mContext;// 上下文环境变量
    private AUBlutoothHolder mHolder;// 加安模块封装好的蓝牙类
    private AUStateChangeCallbackIml mCallBack;// 蓝牙事件回调

    private Analysis analysis;// 解析数据的类
    private boolean DISCONNECTCALLBACK = false;// 断开连接命令是否返回了标记
    private boolean ISSIXTYSECONDSMEASURE = false;// 是否为60秒测量标记

    private int ecgCount = 0;// ecg数据个数
    private int heartRateCount = 0;// 心跳次数
    private int HEARTRATE = 0;// 心率
    private int[] ecgArray = new int[10];//ECG原始数据
    private float[] ecgResultArray = new float[13];// ECG解析之后的数据
    private boolean init = false;// 是否已经进行初始化的标记
    private MeasureBatteryDisplay batteryDisplay;// 电池电量的显示
    private JniCallbackData jniCallback = new JniCallbackData();
    private SaveSixtySecondsMeasureData saveSixtySecondsData;// 保存60秒测量数据的类
    private SaveContinueMeasureData saveContinueMeasureData;// 保存连续测量数据的类
    private int stardandGain = 26;// 标准增益
    private long startMeaTime = 0;// 开始测量毫秒数
    private int measureHour = 0;// 测量时间小时数
    private int measureMinute = 0;// 测量时间分钟数
    private int measureSecond = 0;// 测量时间秒数
    private float gain = 0.5f;// 增益
    private boolean isFallOff = true;// 是否脱落
    private boolean mvFlag = false;// 是否为1mv测试标记
    private int batteryValue = 90;// 电池电量
    private boolean ISMARKED = false;// 手动截取
    private boolean ISSTART = false;// 开始测量
    private boolean TIMECUT = false;// 定时截取
    private TimeRunn timeRunnable;// 定时信号
    private ArrayList<Integer> resourceData = new ArrayList<>();

    private int MEASUREBUTTONSTATE = MeasureButton.SCAN;// 测量按钮的状态

    private FallOffAlertPop fallOffAlertPop;// 导联脱落pop
    private SavaDataPop saveDataPop;// 存储数据的pop
    private AdjustScopePop adjustScopePop;// 调整增益的pop
    private ECGResultBean ecgResultBean;// ecg数据解析结果
    private HandleDisconnectState handleDisconnectState;// 处理断开连接事件
    private boolean ISONLYWIFI = false;// 是否仅在WiFi状态下上传

    private BluetoothCommunicateListener mBluetoothCommunicateListener;// 蓝牙通信接口

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCAN_BLUETOOTH_DEVICE:
                    scanBluetoothDevice();
                    break;
                case HEART_RATE:
                    if (mBluetoothCommunicateListener != null)
                        mBluetoothCommunicateListener.heartRate((int) msg.obj);
                    break;
                case BLUETOOTH_STATE:
                    BluetoothStateConfirmClass communicateClass = (BluetoothStateConfirmClass) msg.obj;
                    setIsFallOff(communicateClass.isFallOff());
                    if (mBluetoothCommunicateListener != null)
                        mBluetoothCommunicateListener.reBluetoothState(communicateClass);
                    break;
                case MEASURE_TIME:
                    if (mBluetoothCommunicateListener != null)
                        mBluetoothCommunicateListener.measureTime((String) msg.obj);
                    break;
                case LOST_PACKAGE:
                    if (mBluetoothCommunicateListener != null)
                        mBluetoothCommunicateListener.lostPackageRate((int) msg.obj);
                    break;
            }
        }
    };

    public boolean getISONLYWIFI() {
        return ISONLYWIFI;
    }

    public void setISONLYWIFI(boolean ISONLYWIFI) {
        this.ISONLYWIFI = ISONLYWIFI;
    }

    /**
     * 构造函数
     *
     * @param context
     */
    public BluetoothCommunicateClass(Context context) {
        mContext = context;
        adjustScopePop = new AdjustScopePop(mContext);
        mCallBack = new AUStateChangeCallbackIml(mContext);
        mHolder = new AUBlutoothHolder(mContext, mCallBack);
        analysis = new Analysis(mContext);
        saveSixtySecondsData = new SaveSixtySecondsMeasureData(mContext);
        saveSixtySecondsData.setSaveOverListener(new SaveSixtySecondsMeasureData.SaveOverListener() {
            @Override
            public void saveOver() {
                if (mBluetoothCommunicateListener != null)
                    mBluetoothCommunicateListener.saveOver();
            }
        });
        setAnalysisListener();
    }

    /**
     * 获取处理断开连接事件的类
     *
     * @return
     */
    public HandleDisconnectState getHandleDisconnectState() {
        return handleDisconnectState;
    }

    /**
     * 定时截取
     */
    public void handleTimeCut(TimeRunn timeRunn) {
        if (MEASUREBUTTONSTATE != MeasureButton.STOP) {
            TIMECUT = false;
            return;
        }
        if (timeRunn.getTimeStamp() == startMeaTime)
            myHandler.postDelayed(timeRunnable, 600000);
    }

    /**
     * 设置处理断开连接事件的类
     *
     * @param handleDisconnectState
     */
    public void setHandleDisconnectState(HandleDisconnectState handleDisconnectState) {
        this.handleDisconnectState = handleDisconnectState;
    }

    /**
     * 获取调整增益的pop
     *
     * @return
     */
    public AdjustScopePop getAdjustScopePop() {
        return adjustScopePop;
    }

    /**
     * 开始寻找蓝牙设备
     */
    private void scanBluetoothDevice() {

    }

    /**
     * 设置加安蓝牙的事件监听器
     */
    public void setBluetoothListener() {
        mCallBack.setCallBackStateListener(new AUStateChangeCallbackIml.CallBackStateListener() {
            @Override
            public void foundNewDevice(BluetoothDevice bluetoothDevice) {
                if (bluetoothDevice.getAddress().equals(BaseApplication.getInstance().
                        getBluetoothDevice().getAddress())) {
                    mHolder.stopScan();
                    mHolder.connect(bluetoothDevice, mContext);
                }
            }

            @Override
            public void connected() {

            }

            @Override
            public void disConnected() {
                if (mBluetoothCommunicateListener != null)
                    mBluetoothCommunicateListener.disConnected();
            }

            @Override
            public void stateReady() {
                if (mBluetoothCommunicateListener != null)
                    mBluetoothCommunicateListener.stateReady();
            }

            @Override
            public void onReceivedData(byte[] bytes) {
                if (analysis != null)
                    analysis.receivedData(bytes);
            }
        });
    }

    /**
     * 获取电池电量
     *
     * @return
     */
    public int getBatteryValue() {
        return batteryValue;
    }

    /**
     * 设置电池电量
     *
     * @param batteryValue
     */
    public void setBatteryValue(int batteryValue) {
        this.batteryValue = batteryValue;
    }

    /**
     * 是否脱落
     *
     * @return
     */
    public boolean isFallOff() {
        return isFallOff;
    }

    /**
     * 设置是否脱落
     *
     * @param isFallOff
     */
    public void setIsFallOff(boolean isFallOff) {
        this.isFallOff = isFallOff;
    }

    /**
     * 获取增益
     *
     * @return
     */
    public float getGain() {
        return gain;
    }

    /**
     * 设置增益
     *
     * @param gain
     */
    public void setGain(float gain) {
        this.gain = gain;
    }

    /**
     * 获取测量按钮的状态
     *
     * @return
     */
    public int getMEASUREBUTTONSTATE() {
        return MEASUREBUTTONSTATE;
    }

    /**
     * 设置测量按钮的状态
     *
     * @param MEASUREBUTTONSTATE
     */
    public void setMEASUREBUTTONSTATE(int MEASUREBUTTONSTATE) {
        this.MEASUREBUTTONSTATE = MEASUREBUTTONSTATE;
    }

    /**
     * 获取解析数据的类
     *
     * @return
     */
    public Analysis getAnalysis() {
        return analysis;
    }

    /**
     * 设置解析数据类的监听器
     */
    private void setAnalysisListener() {
        analysis.setReceivedDataListener(new Analysis.ReceivedDataListener() {
            @Override
            public void reBluetoothState(BluetoothStateConfirmClass blueStateConfirm) {
                myHandler.obtainMessage(BLUETOOTH_STATE, blueStateConfirm).sendToTarget();
            }

            @Override
            public void reEcgWaveData(int data) {
            }

            @Override
            public void reEcgWaveDataBate(int[] dataArray) {
                handleReEcgWaveData(dataArray);
            }

            @Override
            public void reRespWaveData(int data) {
            }

            @Override
            public void timeCorrectCallBack(boolean right) {
            }

            @Override
            public void oneMVCorrectCallBack(boolean start) {
            }

            @Override
            public void measureCommandCallBack(boolean measureComm) {
//                mvFlag = true;
//                resourceData.clear();
                if (measureComm) {
                    if (MEASUREBUTTONSTATE == MeasureButton.START) {
                        ISSTART = true;
//                    if (timeRunnable == null)// 定时截取功能
//                        timeRunnable = new() {
//                            @Override
//                            public void run() {
//                                TIMECUT = true;
//                                handleTimeCut();
//                            }
//                        };
                        timeRunnable = new TimeRunn();
                        if (mBluetoothCommunicateListener != null)
                            mBluetoothCommunicateListener.measureCommandCallBack(measureComm);
                    }
                } else {
                    myHandler.removeCallbacks(timeRunnable);
                    if (mBluetoothCommunicateListener != null)
                        mBluetoothCommunicateListener.measureCommandCallBack(measureComm);
                }
            }

            @Override
            public void disConnectCallBack() {
                DISCONNECTCALLBACK = true;
                mHolder.disconnect();
            }

            @Override
            public void blueConnCommandCallBack() {
            }

            @Override
            public void othersCallBack(byte data) {
                switch (data) {
                    case (byte) 0x89:
                        ISMARKED = true;
                        break;
                }
            }

            @Override
            public void losePackageRate(int rate) {
                myHandler.obtainMessage(LOST_PACKAGE, rate).sendToTarget();
            }
        });
    }

    /**
     * 处理接收到ecg数据
     *
     * @param dataArray
     */
    private void handleReEcgWaveData(int[] dataArray) {
        for (int data : dataArray) {
//            if (mvFlag) {
//                if (resourceData.size() < 18000)
//                    resourceData.add(data);
//                else {
//                    mvFlag = false;
//                    outPut();
//                }
//            }
            ecgArray[ecgCount] = data;
            ecgCount++;
            heartRateCount++;
            if (ecgCount == 10) {
                ecgCount = 0;
                if (!init) {
                    jniCallback.getInitFromJni();
                    init = true;
                }
                ecgResultBean = jniCallback.AnalysisECGWave(ecgArray, isFallOff ? 1 : 0);
                if (!ISSIXTYSECONDSMEASURE) {
                    if (ISSTART) {
                        ISSTART = false;
                        saveContinueMeasureData.receiveAbnormalSign(AbnormalSignInfor.START);
                    }
                    if (TIMECUT) {
                        TIMECUT = false;
                        saveContinueMeasureData.receiveAbnormalSign(AbnormalSignInfor.TIMERCUT);
                    }
                    if (ISMARKED) {
                        ISMARKED = false;
                        saveContinueMeasureData.receiveAbnormalSign(AbnormalSignInfor.MARK);
                    }
                    if (ecgResultBean.getbOverLoad() != ECGResultBean.OVERLOAD) {

                        for (int i = 0; i < ecgResultBean.getArynRhythmMark().length; i++) {
                            if (ecgResultBean.getArynRhythmMark()[i] == AbnormalSignInfor.SIN_TACHY ||
                                    ecgResultBean.getArynRhythmMark()[i] == AbnormalSignInfor.SIN_BRADY ||
                                    ecgResultBean.getArynRhythmMark()[i] == AbnormalSignInfor.VEB_TRI ||
                                    ecgResultBean.getArynRhythmMark()[i] == AbnormalSignInfor.APB_BI)
//                          if (ecgResultBean.getArynRhythmMark()[i] != 0)
                                saveContinueMeasureData.receiveAbnormalSign(ecgResultBean.getArynRhythmMark()[i]);
                        }
                    }
                }
                ecgResultArray = ecgResultBean.getAryfDataShow();
                for (int i = 0; i < 13; i++)
                    ecgResultArray[i] /= stardandGain;
                if (ISSIXTYSECONDSMEASURE)
                    saveSixtySecondsData.addECGData(ecgResultArray);
                else
                    saveContinueMeasureData.addECGWaveData(ecgResultArray);
                if (mBluetoothCommunicateListener != null)
                    mBluetoothCommunicateListener.calculatedMeasreData(ecgResultArray);
            }
            if (heartRateCount >= 250) {
                heartRateCount = 0;
                setMeasureTime();
                HEARTRATE = jniCallback.getHeartRate();
                RealBeatVal realBeatVal = jniCallback.EcgGetRealBeatVal();
                if (HEARTRATE > 10)
                    if (ISSIXTYSECONDSMEASURE)
                        saveSixtySecondsData.addHeartRate(HEARTRATE);
                    else
                        saveContinueMeasureData.addHeartRate(HEARTRATE);
                if (HEARTRATE >= 0)
                    myHandler.obtainMessage(HEART_RATE, HEARTRATE).sendToTarget();
            }
        }
    }

    /**
     * 断开连接是否返回
     *
     * @return
     */
    public boolean isDISCONNECTCALLBACK() {
        return DISCONNECTCALLBACK;
    }

    /**
     * 设置断开连接回调
     *
     * @param callBack
     */
    public void setDISCONNECTCALLBACK(boolean callBack) {
        DISCONNECTCALLBACK = callBack;
    }

    /**
     * 设置测量时间
     */
    private void setMeasureTime() {
        if (startMeaTime != -1) {
            long dif = System.currentTimeMillis() - startMeaTime;
            dif /= 1000;
            measureSecond = (int) dif % 60;
            dif /= 60;
            measureMinute = (int) (dif % 60);
            measureHour = (int) (dif / 60);
            myHandler.obtainMessage(MEASURE_TIME, (measureHour < 10 ? "0" + measureHour : measureHour)
                    + ":" + (measureMinute < 10 ? "0" + measureMinute : measureMinute) + ":" +
                    (measureSecond < 10 ? "0" + measureSecond : measureSecond)).sendToTarget();
        }
    }

    /**
     * 设置开始测量时间
     *
     * @param timeInMini
     */
    public void setStartMeaTime(long timeInMini) {
        startMeaTime = timeInMini;
    }

    /**
     * 设置存储连续测量数据的类
     *
     * @param saveContinueMeasureData
     */
    public void setSaveContinueMeasureData(SaveContinueMeasureData saveContinueMeasureData) {
        this.saveContinueMeasureData = saveContinueMeasureData;
    }

    /**
     * 获取存储连续测量数据的对象
     *
     * @return
     */
    public SaveContinueMeasureData getSaveContinueMeasureData() {
        return saveContinueMeasureData;
    }

    /**
     * 设置存储60秒测量数据的类
     *
     * @param saveSixtySecondsData
     */
    public void setSaveSixtySecondsData(SaveSixtySecondsMeasureData saveSixtySecondsData) {
        this.saveSixtySecondsData = saveSixtySecondsData;
    }

    /**
     * 获取存储60测量的数据的对象
     *
     * @return
     */
    public SaveSixtySecondsMeasureData getSaveSixtySecondsData() {
        return saveSixtySecondsData;
    }

    /**
     * 获取开始测量时间
     *
     * @return
     */
    public long getStartMeaTime() {
        return startMeaTime;
    }

    /**
     * 设置测量初始化
     *
     * @param flag
     */
    public void setMeasureInit(boolean flag) {
        init = flag;
    }

    /**
     * 是否已经初始化
     *
     * @return
     */
    public boolean isMeasureInit() {
        return init;
    }

    /**
     * 设置是否为60秒测量
     *
     * @param isSixty
     */
    public void setISSIXTYSECONDSMEASURE(boolean isSixty) {
        ISSIXTYSECONDSMEASURE = isSixty;
    }

    /**
     * 判断是否为60秒测量
     *
     * @return
     */
    public boolean isSIXTYSECONDSMEASURE() {
        return ISSIXTYSECONDSMEASURE;
    }

    /**
     * 获取mHolder
     *
     * @return
     */
    public AUBlutoothHolder getAUBlutoothHolder() {
        return mHolder;
    }

    /**
     * 获取mCallBack
     *
     * @return
     */
    public AUStateChangeCallbackIml getAUStateChangeCallbackIml() {
        return mCallBack;
    }

    /**
     * 设置蓝牙通信状态的监听
     *
     * @param blueCommListener
     */
    public void setBluetoothCommunicateListener(BluetoothCommunicateListener blueCommListener) {
        mBluetoothCommunicateListener = blueCommListener;
    }

    private void outPut() {
        if (ThreadUtils.getInstance().getExecutorService() != null) {
            ThreadUtils.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    writeDataToFile();
                }
            });
        }
    }

    /**
     * 将数据写入到文件中
     */
    private void writeDataToFile() {
        String dataFileName = System.currentTimeMillis() + "heartdoc.txt";
        String path = Environment.getExternalStorageDirectory() + "/AHeartDoc";
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        File dataFile = new File(path, dataFileName);
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(dataFile);
            for (int data : resourceData)
                fileOutputStream.write((data + "\n\t").getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null)
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        resourceData.clear();
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.ToastMessage(mContext, "存储完毕");
            }
        });
    }

    /**
     * 获取调用算法的类
     *
     * @return
     */
    public JniCallbackData getJniCallback() {
        return jniCallback;
    }

    /**
     * 清除蓝牙通信状态的监听
     */
    public void removeBluetoothCommunicateListener() {
        mBluetoothCommunicateListener = null;
    }

    public MeasureBatteryDisplay getBatteryDisplay() {
        return batteryDisplay;
    }

    public void setBatteryDisplay(MeasureBatteryDisplay batteryDisplay) {
        this.batteryDisplay = batteryDisplay;
    }

    public TimeRunn getTimeRunnable() {
        return timeRunnable;
    }

    public class TimeRunn implements Runnable {

        private long mTimeStamp = -1;

        public void setTimeStamp(long timeStamp) {
            mTimeStamp = timeStamp;
        }

        public long getTimeStamp() {
            return mTimeStamp;
        }

        @Override
        public void run() {
            TIMECUT = true;
            handleTimeCut(this);
        }
    }

}
