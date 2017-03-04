package com.electrocardio.jni;

/**
 * Created by ZhangBo on 2016/05/03.
 */
public class RealBeatVal {

    private int nUpdateRnum;// R波个数
    private int[] arynRPos;// 每个R波位置
    private int[] arynBeatStyle;// 心搏类型
    private int[] arynRR;// RR间期

    public int getnUpdateRnum() {
        return nUpdateRnum;
    }

    public void setnUpdateRnum(int nUpdateRnum) {
        this.nUpdateRnum = nUpdateRnum;
    }

    public int[] getArynRPos() {
        return arynRPos;
    }

    public void setArynRPos(int[] arynRPos) {
        this.arynRPos = arynRPos;
    }

    public int[] getArynBeatStyle() {
        return arynBeatStyle;
    }

    public void setArynBeatStyle(int[] arynBeatStyle) {
        this.arynBeatStyle = arynBeatStyle;
    }

    public int[] getArynRR() {
        return arynRR;
    }

    public void setArynRR(int[] arynRR) {
        this.arynRR = arynRR;
    }
}
