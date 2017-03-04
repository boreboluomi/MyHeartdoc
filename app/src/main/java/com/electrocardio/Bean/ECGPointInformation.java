package com.electrocardio.Bean;

/**
 * Created by ZhangBo on 2016/05/03.
 */
public class ECGPointInformation {

    private boolean isRPoint = false;// 是否为R波
    private int BEATSTYLE = -1;// 心搏类型
    private int RRINTERVAL = 0;// RR间期
    private float pointY = 0;// 纵坐标
    public static final int DOUXING = 0;// 窦性
    public static final int FANGZAO = 1;// 房早
    public static final int SHIZAO = 2;// 室早
    public static final int WEICHA = 3;// 尾差

    public float getPointY() {
        return pointY;
    }

    public void setPointY(float pointY) {
        this.pointY = pointY;
    }

    public boolean isRPoint() {
        return isRPoint;
    }

    public void setIsRPoint(boolean isRPoint) {
        this.isRPoint = isRPoint;
    }

    public int getBEATSTYLE() {
        return BEATSTYLE;
    }

    public void setBEATSTYLE(int BEATSTYLE) {
        this.BEATSTYLE = BEATSTYLE;
    }

    public int getRRINTERVAL() {
        return RRINTERVAL;
    }

    public void setRRINTERVAL(int RRINTERVAL) {
        this.RRINTERVAL = RRINTERVAL;
    }

    @Override
    public String toString() {
        if (isRPoint)
            return pointY + "_b" + BEATSTYLE + "_r" + RRINTERVAL;
        else
            return pointY + "";
    }

    public ECGPointInformation() {
    }
}
