package com.electrocardio.Bean;

/**
 * Created by ZhangBo on 2016/3/7.
 */
public class SixSecondMeasureBean {

    private boolean isDayOfMonth = false;// 数据类型是否为日期

    private int dayOfMonth = 0;// 日期

    private String timeSpace;// 时间间隔

    private int bmp;// 心率

    private long timeStamp;// 时间戳

    private String healthDescrip;// 健康描述

    private String dataFileSrc;// 数据文件存储路径

    private int healthNumber;// 健康指数

    public boolean isDayOfMonth() {
        return isDayOfMonth;
    }

    public void setIsDayOfMonth(boolean isDayOfMonth) {
        this.isDayOfMonth = isDayOfMonth;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getTimeSpace() {
        return timeSpace;
    }

    public void setTimeSpace(String timeSpace) {
        this.timeSpace = timeSpace;
    }

    public int getBmp() {
        return bmp;
    }

    public void setBmp(int bmp) {
        this.bmp = bmp;
    }

    public String getDataFileSrc() {
        return dataFileSrc;
    }

    public void setDataFileSrc(String dataFileSrc) {
        this.dataFileSrc = dataFileSrc;
    }

    public String getHealthDescrip() {
        return healthDescrip;
    }

    public void setHealthDescrip(String healthDescrip) {
        this.healthDescrip = healthDescrip;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getHealthNumber() {
        return healthNumber;
    }

    public void setHealthNumber(int healthNumber) {
        this.healthNumber = healthNumber;
    }

    public void SixSecondMeasureBean() {

    }

    @Override
    public String toString() {
        return "SixSecondMeasureBean{" +
                "isDayOfMonth=" + isDayOfMonth +
                ", dayOfMonth=" + dayOfMonth +
                ", timeSpace='" + timeSpace + '\'' +
                ", bmp=" + bmp +
                ", timeStamp=" + timeStamp +
                ", healthDescrip='" + healthDescrip + '\'' +
                ", dataFileSrc='" + dataFileSrc + '\'' +
                ", healthNumber=" + healthNumber +
                '}';
    }
}
