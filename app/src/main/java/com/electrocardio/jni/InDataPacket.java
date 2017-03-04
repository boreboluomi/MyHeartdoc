package com.electrocardio.jni;

/**
 * Created by yangzheng on 2016/2/20.
 */
public class InDataPacket {
    public int[] ecgData;

    public int[] getEcgData() {
        return ecgData;
    }
    public void setEcgData(int[] ecgData) {
        this.ecgData = ecgData;
    }
}
