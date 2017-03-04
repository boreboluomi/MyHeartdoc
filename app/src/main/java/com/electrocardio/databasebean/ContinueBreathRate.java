package com.electrocardio.databasebean;

/**
 * Created by ZhangBo on 2016/03/24.
 */
public class ContinueBreathRate {

    private long timeStamp = -1;// 时间戳
    private int fastRate = 23;// 过快比例
    private int normalRate = 56;// 正常比例
    private int slowRate = 21;// 过慢比例
    private int rate = -1;// 比例集合
    private int averageBreathRate = -1;// 平均呼吸率
    private String dataFileSrc = "";// 存储数据文件路径

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getFastRate() {
        return fastRate;
    }

    public int getNormalRate() {
        return normalRate;
    }

    public int getSlowRate() {
        return slowRate;
    }

    public int getRate() {
        return rate;
    }

    public int[] getRateArray() {
        return new int[]{fastRate, normalRate, slowRate};
    }

    public void setRate(int fastRate, int normalRate, int slowRate) {
        this.fastRate = fastRate;
        this.normalRate = normalRate;
        this.slowRate = slowRate;
        rate = (fastRate << 8) + normalRate;
    }

    public void setRate(int rate) {
        this.rate = rate;
        normalRate = rate & 0xff;
        fastRate = rate >>> 8 & 0xff;
        slowRate = 100 - normalRate - fastRate;
    }

    public int getAverageBreathRate() {
        return averageBreathRate;
    }

    public void setAverageBreathRate(int averageBreathRate) {
        this.averageBreathRate = averageBreathRate;
    }

    public String getDataFileSrc() {
        return dataFileSrc;
    }

    public void setDataFileSrc(String dataFileSrc) {
        this.dataFileSrc = dataFileSrc;
    }

    @Override
    public String toString() {
        return "ContinueBreathRate{" +
                "timeStamp=" + timeStamp +
                ", fastRate=" + fastRate +
                ", normalRate=" + normalRate +
                ", slowRate=" + slowRate +
                ", rate=" + rate +
                ", averageBreathRate=" + averageBreathRate +
                ", dataFileSrc='" + dataFileSrc + '\'' +
                '}';
    }
}
