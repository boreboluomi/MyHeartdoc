package com.electrocardio.jni;

import java.util.Arrays;

/**
 * Created by ZhangBo on 2016/04/01.
 */
public class CompleteECGResultBean {

    private int nRNum;// 心搏总数
    private int nHRAvg;// 平均心率
    private int nHRMax;// 最快心率
    private int nHRMin;// 最慢心率
    private int[] arynHRPercent;// 正常异常心率比例

    public int getnRNum() {
        return nRNum;
    }

    public void setnRNum(int nRNum) {
        this.nRNum = nRNum;
    }

    public int getnHRAvg() {
        return nHRAvg;
    }

    public void setnHRAvg(int nHRAvg) {
        this.nHRAvg = nHRAvg;
    }

    public int getnHRMax() {
        return nHRMax;
    }

    public void setnHRMax(int nHRMax) {
        this.nHRMax = nHRMax;
    }

    public int getnHRMin() {
        return nHRMin;
    }

    public void setnHRMin(int nHRMin) {
        this.nHRMin = nHRMin;
    }

    public int[] getArynHRPercent() {
        return arynHRPercent;
    }

    public void setArynHRPercent(int[] arynHRPercent) {
        this.arynHRPercent = arynHRPercent;
    }

    @Override
    public String toString() {
        return "CompleteECGResultBean{" +
                "nRNum=" + nRNum +
                ", nHRAvg=" + nHRAvg +
                ", nHRMax=" + nHRMax +
                ", nHRMin=" + nHRMin +
                ", arynHRPercent=" + Arrays.toString(arynHRPercent) +
                '}';
    }

    public CompleteECGResultBean() {
    }
}
