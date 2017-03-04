package com.electrocardio.databasebean;

/**
 * Created by ZhangBo on 2016/03/22.
 */
public class SixtySecondsHeartRate {
    private long timeStamp = -1l;// 时间戳
    private int fastRate = 9;// 过快比例
    private int normalFast = 11;// 稍快比例
    private int normalRate = 62;// 正常比例
    private int normalSlow = 7;// 稍慢比例
    private int slowRate = 11;// 过慢比例
    private int rate = -1;// 比例
    private int averageHeartRate = -1;// 平均心率
    private int fastestHeartRate = -1;// 最快心率
    private int slowestHeartRate = -1;// 最慢心率

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getFastRate() {
        return fastRate;
    }

    public int getNormalFast() {
        return normalFast;
    }

    public int getNormalRate() {
        return normalRate;
    }

    public int getNormalSlow() {
        return normalSlow;
    }

    public int getSlowRate() {
        return slowRate;
    }

    public int getRate() {
        return rate;
    }

    public int[] getRateArray() {
        return new int[]{fastRate, normalFast, normalRate, normalSlow, slowRate};
    }

    public void setRate(int rate) {
        this.rate = rate;
        normalSlow = rate & 0xff;
        normalRate = rate >>> 8 & 0xff;
        normalFast = rate >>> 16 & 0xff;
        fastRate = rate >>> 24 & 0xff;
        slowRate = 100 - normalFast - normalRate - normalSlow - fastRate;
    }

    public void setRate(int fastRate, int normalFast, int normalRate, int normalSlow, int slowRate) {
        this.fastRate = fastRate;
        this.normalFast = normalFast;
        this.normalRate = normalRate;
        this.normalSlow = normalSlow;
        this.slowRate = slowRate;
        this.rate = (fastRate << 24) + (normalFast << 16) + (normalRate << 8) + normalSlow;
    }

    public int getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(int averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public int getFastestHeartRate() {
        return fastestHeartRate;
    }

    public void setFastestHeartRate(int fastestHeartRate) {
        this.fastestHeartRate = fastestHeartRate;
    }

    public int getSlowestHeartRate() {
        return slowestHeartRate;
    }

    public void setSlowestHeartRate(int slowestHeartRate) {
        this.slowestHeartRate = slowestHeartRate;
    }

    @Override
    public String toString() {
        return "SixtySecondsHeartRate{" +
                "timeStamp=" + timeStamp +
                ", fastRate=" + fastRate +
                ", normalFast=" + normalFast +
                ", normalRate=" + normalRate +
                ", normalSlow=" + normalSlow +
                ", slowRate=" + slowRate +
                ", rate=" + rate +
                ", averageHeartRate=" + averageHeartRate +
                ", fastestHeartRate=" + fastestHeartRate +
                ", slowestHeartRate=" + slowestHeartRate +
                '}';
    }

    public SixtySecondsHeartRate() {
    }
}
