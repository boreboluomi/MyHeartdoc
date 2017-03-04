package com.electrocardio.bluetoothdata;

/**
 * Created by yangzheng on 2016/3/8.
 */
public abstract class AbsEcgData {


    protected abstract void onEcgdataContent(float content);
    protected abstract void onEcgStateContent(int batteryState, int electricity);
}
