package com.electrocardio.Bean;

/**
 * Created by ZhangBo on 2016/3/8.
 */
public class HandClearBean {

    private boolean isYearMonth = false;// 是否是年和月
    private int year;// 年
    private int month;// 月
    private int dayOfMonth;// 日
    private String timeSpace;// 时间间隔
    private String timeLength;// 测量时长
    private float dataSize;// 数据大小
    private boolean submitState = true;// 上传状态
    private boolean checked = false;// 是否已经被选中
    private long timeStamp = -1;// 时间戳

    public boolean isYearMonth() {
        return isYearMonth;
    }

    public void setIsYearMonth(boolean isYearMonth) {
        this.isYearMonth = isYearMonth;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
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

    public String getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(String timeLength) {
        this.timeLength = timeLength;
    }

    public float getDataSize() {
        return dataSize;
    }

    public void setDataSize(float dataSize) {
        this.dataSize = dataSize;
    }

    public boolean isSubmitState() {
        return submitState;
    }

    public void setSubmitState(boolean submitState) {
        this.submitState = submitState;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public HandClearBean() {
    }

    @Override
    public String toString() {
        return "HandClearBean{" +
                "isYearMonth=" + isYearMonth +
                ", year=" + year +
                ", month=" + month +
                ", dayOfMonth=" + dayOfMonth +
                ", timeSpace='" + timeSpace + '\'' +
                ", timeLength='" + timeLength + '\'' +
                ", dataSize=" + dataSize +
                ", submitState=" + submitState +
                ", checked=" + checked +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
