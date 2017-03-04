package com.electrocardio.bluetoothdata;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.electrocardio.terminalIO.TIOManager;
import com.electrocardio.terminalIO.TIOPeripheral;
import com.electrocardio.terminalIO.TIOPeripheralCallback;
import java.lang.ref.WeakReference;

/**
 * Created by yangzheng on 2016/3/4.
 * 斯图曼蓝牙接口回调类
 */
//,EcgDataAnalysis.DataCoallBack
public class TerminalBluetooth implements TIOPeripheralCallback, Analysis.ReceivedDataListener {
    public static byte[] ECGSTART = new byte[]{0x51, (byte) 0x81, 0x0d, 0x0a};//蓝牙开始
    public static byte[] ECGSPAUSE = new byte[]{0x51, (byte) 0x82, 0x0d, 0x0a};//蓝牙暂停
    public static byte[] ECGSTOP = new byte[]{0x53, (byte) 0x81, 0x0d, 0x0a};//蓝牙停止
    public static byte[] ECGCONNECT = new byte[]{0x56, (byte) 0x81, 0x0d, 0x0a};//蓝牙连接
    private static final int RSSI_INTERVAL = 1670;
    private WeakReference<Activity> activity;
    private Context con;
    private Handler rssiHandler = new Handler();
    private Runnable rssiRunnable;
    private Analysis analysis;
    private TIOPeripheral peripheral;
    private Handler mHanler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1 :
                    break;
                case 2:
                    break;
            }
        }
    };
    public TerminalBluetooth(Context context, TIOPeripheral peripheralByAddress,
                             WeakReference<Activity> activity) {
        this.con = context;
        this.activity = activity;
        peripheral = peripheralByAddress;
        //analysis = new Analysis(activity);
        analysis.setReceivedDataListener(this);
        connectPeripheral();
    }


    public void connectPeripheral() {
        peripheral.setListener(this);
       // Connectstate(true);
    }

    public void sendStartEcg() {

        peripheral.writeUARTData(ECGSTART);
    }
    public void sendPauseEcg() {
        peripheral.writeUARTData(ECGSPAUSE);
    }
    public void sendStopEcg() {
        peripheral.writeUARTData(ECGSTOP);
        peripheral.disconnect();
    }

    public void Connectstate(Boolean b) {
        if (b) {
            peripheral.writeUARTData(ECGCONNECT);
        }
    }



  /*  public void disConnected(){
        peripheral.writeUARTData(ECGSPAUSE);
        peripheral.disconnect();
    }*/

    /**
     * 开启PSSSITimer
     */
    private void startRSSITimer() {
        if (this.rssiRunnable == null) {
            this.rssiRunnable = new Runnable() {
                @Override
                public void run() {
                    peripheral.readRSSI();
                    rssiHandler.postDelayed(rssiRunnable, RSSI_INTERVAL);
                }
            };
        }
        this.peripheral.readRSSI();
        this.rssiHandler.postDelayed(this.rssiRunnable, RSSI_INTERVAL);
    }

    /**
     * 停止PSSSITimer
     */
    private void stopRSSITimer() {

        this.rssiHandler.removeCallbacks(this.rssiRunnable);
    }

    @Override
    public void tioPeripheralDidConnect(TIOPeripheral peripheral) {
        startRSSITimer();
        if (!peripheral.shallBeSaved()) {
            peripheral.setShallBeSaved(true);
            TIOManager.sharedInstance().savePeripherals();

        }


    }

    @Override
    public void tioPeripheralDidFailToConnect(TIOPeripheral peripheral, String errorMessage) {
        if (errorMessage.length() > 0) {
            /*STUtil.showErrorAlert("Failed to connect with error message: "
                    + errorMessage, con);*/
            System.out.println("Failed to connect with error message: " + errorMessage);
        }

    }

    @Override
    public void tioPeripheralDidDisconnect(TIOPeripheral peripheral, String errorMessage) {
        stopRSSITimer();
    }

    @Override
    public void tioPeripheralDidReceiveUARTData(TIOPeripheral peripheral, byte[] data) {

            analysis.receivedData(data);



    }

    @Override
    public void tioPeripheralDidWriteNumberOfUARTBytes(TIOPeripheral peripheral, int bytesWritten) {

    }

    @Override
    public void tioPeripheralUARTWriteBufferEmpty(TIOPeripheral peripheral) {

    }

    @Override
    public void tioPeripheralDidUpdateAdvertisement(TIOPeripheral peripheral) {

    }

    @Override
    public void tioPeripheralDidUpdateRSSI(TIOPeripheral peripheral, int rssi) {

    }

    @Override
    public void tioPeripheralDidUpdateLocalUARTCreditsCount(TIOPeripheral peripheral, int creditsCount) {

    }

    @Override
    public void tioPeripheralDidUpdateRemoteUARTCreditsCount(TIOPeripheral peripheral, int creditsCount) {

    }

    @Override
    public void reBluetoothState(BluetoothStateConfirmClass blueStateConfirm) {
        if (icallBack != null) {
            icallBack.onBluetoothState(blueStateConfirm);
        }
    }

    @Override
    public void reEcgWaveData(int data) {


    }

    @Override
    public void reEcgWaveDataBate(int[] dataArray) {
        for (int i = 0; i < dataArray.length; i++) {
            if (icallBack != null) {
                icallBack.onEcgData(dataArray[i]);
            }
        }
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

    }

    @Override
    public void disConnectCallBack() {

    }

    @Override
    public void blueConnCommandCallBack() {
        icallBack.onConnectState();
    }

    @Override
    public void othersCallBack(byte data) {
        switch (data) {
            case (byte) 0x89:
                icallBack.onMarkCallBack();
                break;
        }
    }


    @Override
    public void losePackageRate(int rate) {
        if (icallBack != null) {
            icallBack.onLosedata(rate);
        }
    }


    //回调的接口
    public onDataBlackListener icallBack = null;

    public interface onDataBlackListener {
        void onEcgData(int s);

        void onMarkCallBack();

        void onLosedata(int rate);

        void onConnectState();

        void onBluetoothState(BluetoothStateConfirmClass blueStateConfirm);
    }

    public void setonBlackListener(onDataBlackListener iBack) {
        icallBack = iBack;
    }
}
