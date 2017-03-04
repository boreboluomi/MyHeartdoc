package com.electrocardio.bluetoothdata;

import java.util.ArrayList;

/**
 * Created by yangzheng on 2016/3/4.
 */
public class EcgDataAnalysis{



    public static final byte ECGSTATE = 0x01;
    public static final byte ECGWAVE = 0x02;
    public static final byte RESWAVE = 0x05;
    public static final byte CommandCallBack = 0x07;
    private int Defaultpackage=-1;
    private final int STATE = 0;// 蓝牙状态确认ID
    private final int ECGID = 1;// ECG波形ID
    private final int RESID = 2;// RESP(呼吸)波形ID
    private final int BREAKEID = 3;// 命令回应ID
    private ArrayList<Byte> blueStateConfList=new ArrayList<Byte>();// 蓝牙状态确认数据集合
    private ArrayList<Byte> ecgWaveList= new ArrayList<Byte>();// ECG波形数据集合
    private ArrayList<Byte> respWaveList= new ArrayList<Byte>();// 呼吸波形数据集合
    private ArrayList<Byte> cmBackList=new ArrayList<Byte>();// 命令回应数据集合
    private static int BluetoothLength =5;//根据解析文档得知

    public  void receivedData(byte[] alldata) {
        for (byte data : alldata) {
            switch (data) {
                case ECGSTATE:
                    Defaultpackage=STATE;
                    break;
                case ECGWAVE:
                    Defaultpackage=ECGID;
                    if (ecgWaveList.size() == BluetoothLength) {

                        int datacotent = DataResolve(ecgWaveList);


                        icallBack.onDataCoallBack(datacotent);
                        ecgWaveList.clear();
                    }
                    break;
                case RESWAVE:
                    Defaultpackage=RESID;
                    break;
                case CommandCallBack:
                    Defaultpackage=BREAKEID;
                    break;
                default:
                    addECGDataToArray(data);
                    break;
            }
        }
    }

    private void addECGDataToArray(byte data) {
        switch (Defaultpackage){
            case STATE:
                int BatteryState = data & 0xFF;
                int Electricity = BatteryState & 0x7f;
                icallBack.onStateCoallBack(BatteryState,Electricity);
                break;
            case ECGID:
                ecgWaveList.add(data);

                break;
            case RESID:
                respWaveList.add(data);
                break;
            case BREAKEID:
                cmBackList.add(data);
                break;
        }
    }


    public  int DataResolve(ArrayList<Byte> mlist) {
        if (mlist.size() != BluetoothLength) {
            mlist.clear();
            return 0;
        }
        byte standard = mlist.get(0);
        int result;
        int[] array = new int[mlist.size() - 2];
        for (int i = 1; i < mlist.size() - 1; i++) {
            if ((standard & (0x01 << (i - 1))) == (0x01 << (i - 1)))
                array[i - 1] = (mlist.get(i) & 0xff);
            else
                array[i - 1] = (mlist.get(i) & 0x7f);
        }
        result = (array[0] << 16) + (array[1] << 8) + array[2];
        if (result > 8388608)
            result -= 8388608 * 2;
        return result;
    }

    public DataCoallBack icallBack = null;
    public interface DataCoallBack {
        void onDataCoallBack(int s);
        void onStateCoallBack(int batteryState, int s);
    }
    public void setonDataClick(DataCoallBack iBack)
    {
        icallBack = iBack;
    }


}
