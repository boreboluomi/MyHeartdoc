package com.electrocardio.jni;

import java.util.Arrays;

/**
 * Created by yangzheng on 2016/2/20.
 */
public class OutObjectResultPacket {
    public  int start;
    public int[] startToend;
    public int[] arynRhythmMark;
    public float[] ecgData;

    public OutObjectResultPacket(int start, int[] arynRhythmMark, int[] startToend, float[] ecgData) {
        this.start = start;
        this.arynRhythmMark = arynRhythmMark;
        this.startToend = startToend;
        this.ecgData = ecgData;
    }

    @Override
    public String toString() {
        return "OutObjectResultPacket{" +
                "start=" + start +
                ", startToend=" + Arrays.toString(startToend) +
                ", arynRhythmMark=" + Arrays.toString(arynRhythmMark) +
                ", ecgData=" + Arrays.toString(ecgData) +
                '}';
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public float[] getEcgData() {
        return ecgData;
    }

    public void setEcgData(float[] ecgData) {
        this.ecgData = ecgData;
    }

    public int[] getArynRhythmMark() {
        return arynRhythmMark;
    }

    public void setArynRhythmMark(int[] arynRhythmMark) {
        this.arynRhythmMark = arynRhythmMark;
    }

    public int[] getStartToend() {
        return startToend;
    }

    public void setStartToend(int[] startToend) {
        this.startToend = startToend;
    }
}
