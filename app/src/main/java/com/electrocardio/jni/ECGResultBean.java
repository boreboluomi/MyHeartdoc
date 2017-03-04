package com.electrocardio.jni;

import java.util.Arrays;

/**
 * Created by ZhangBo on 2016/04/01.
 */
public class ECGResultBean {

    private float[] aryfDataShow;// 算法返回的点
    private int nRhythmNum;// 心搏检测
    private int[] arynRhythmMark;// 节律诊断
    public static int OVERLOAD = 1;// 过载
    private int bOverLoad = 0;// 是否过载

    public float[] getAryfDataShow() {
        return aryfDataShow;
    }

    public void setAryfDataShow(float[] aryfDataShow) {
        this.aryfDataShow = aryfDataShow;
    }

    public int getnRhythmNum() {
        return nRhythmNum;
    }

    public void setnRhythmNum(int nRhythmNum) {
        this.nRhythmNum = nRhythmNum;
    }

    public int[] getArynRhythmMark() {
        return arynRhythmMark;
    }

    public void setArynRhythmMark(int[] arynRhythmMark) {
        this.arynRhythmMark = arynRhythmMark;
    }

    public int getbOverLoad() {
        return bOverLoad;
    }

    public void setbOverLoad(int bOverLoad) {
        this.bOverLoad = bOverLoad;
    }

    @Override
    public String toString() {
        return "ECGResultBean{" +
                "aryfDataShow=" + Arrays.toString(aryfDataShow) +
                ", nRhythmNum=" + nRhythmNum +
                ", arynRhythmMark=" + Arrays.toString(arynRhythmMark) +
                ", bOverLoad=" + bOverLoad +
                '}';
    }

    public ECGResultBean() {
    }
}
