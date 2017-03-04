package com.electrocardio.databasebean;

import java.util.Calendar;

/**
 * Created by ZhangBo on 2016/03/24.
 */
public class ContinueMeasureRecord {

    private long timeStamp = -1;// 时间戳
    private int startDay = -1;// 开始日期
    private int startTime = -1;// 开始时间(时分秒)
    private int endMD = -1;// 结束日期(月日)
    private int endTime = -1;// 结束时间(时分秒)
    private long timeLength = -1;// 时间长度
    private float dataSize = 0;// 数据大小
    private float submitedSize = 0;// 已经提交的数据的大小
    private int averageRate = 0;// 平均心率
    private int rate = 0;// 心率比例
    private int measureOver = 0;// 测量是否已经结束
    private int measureYM = -1;// 连续测量年月
    public static final int MEASURENOTOVER = 0;// 测量没有结束
    public static final int MEASUREOVER = 1;// 测量已经结束

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndMD() {
        return endMD;
    }

    public void setEndMD(int endMD) {
        this.endMD = endMD;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public long getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(long timeLength) {
        this.timeLength = timeLength;
    }

    public float getDataSize() {
        return dataSize;
    }

    public void setDataSize(float dataSize) {
        this.dataSize = dataSize;
    }

    public float getSubmitedSize() {
        return submitedSize;
    }

    public void setSubmitedSize(float submitedSize) {
        this.submitedSize = submitedSize;
    }

    public int getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(int averageRate) {
        this.averageRate = averageRate;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getMeasureOver() {
        return measureOver;
    }

    public void setMeasureOver(int measureOver) {
        this.measureOver = measureOver;
    }

    public int getMeasureYM() {
        return measureYM;
    }

    public void setMeasureYM(int measureYM) {
        this.measureYM = measureYM;
    }

    public void setMeasureYM(int year, int month) {
        measureYM = ((year - 1970) << 8) + month;
    }

    public void setEndTimeStamp(long endTimeStamp) {
        timeLength = endTimeStamp - timeStamp;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTimeStamp);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        endMD = (month << 8) + day;
        endTime = (hour << 16) + (minute << 8) + second;
    }

    @Override
    public String toString() {
        return "ContinueMeasureRecord{" +
                "timeStamp=" + timeStamp +
                ", startDay=" + startDay +
                ", startTime=" + startTime +
                ", endMD=" + endMD +
                ", endTime=" + endTime +
                ", timeLength=" + timeLength +
                ", dataSize=" + dataSize +
                ", submitedSize=" + submitedSize +
                ", averageRate=" + averageRate +
                ", rate=" + rate +
                ", measureOver=" + measureOver +
                ", measureYM=" + measureYM +
                '}';
    }

    public ContinueMeasureRecord() {
    }
}
