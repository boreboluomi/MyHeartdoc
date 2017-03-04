package com.electrocardio.databasebean;

/**
 * Created by ZhangBo on 2016/03/22.
 */
public class SixtySecondsRecord {

    private long timeStamp = -1l;// 时间戳
    private int startTime = -1;// 开始时间
    private int endTime = -1;// 结束时间
    private int averageHeartRate = -1;// 平均心率
    private String healthState = "";// 健康状况
    private int healthIndex = -1;// 健康指数
    private String dataFileSrc = "";// 数据文件存储路径

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setStartTime(int hour, int minute, int second) {
        startTime = (hour << 16) + (minute << 8) + second;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public void setEndTime(int hour, int minute, int second) {
        endTime = (hour << 16) + (minute << 8) + second;
    }

    public int getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(int averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public String getHealthState() {
        return healthState;
    }

    public void setHealthState(String healthState) {
        this.healthState = healthState;
    }

    public int getHealthIndex() {
        return healthIndex;
    }

    public void setHealthIndex(int healthIndex) {
        this.healthIndex = healthIndex;
    }

    public String getDataFileSrc() {
        return dataFileSrc;
    }

    public void setDataFileSrc(String dataFileSrc) {
        this.dataFileSrc = dataFileSrc;
    }

    @Override
    public String toString() {
        return "SixtySecondsRecord{" +
                "timeStamp=" + timeStamp +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", averageHeartRate=" + averageHeartRate +
                ", healthState='" + healthState + '\'' +
                ", healthIndex=" + healthIndex +
                ", dataFileSrc='" + dataFileSrc + '\'' +
                '}';
    }

    public SixtySecondsRecord() {
    }
}
