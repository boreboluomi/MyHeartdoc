package com.electrocardio.Bean;

/**
 * Created by ZhangBo on 2016/3/9.
 */
public class UploadDataBean {
    private boolean IsYearAndMonth = true;// 是否是年和月
    private int year = 0;// 年
    private int month = 0;// 月
    private int dayOfMonth = 0;// 日
    private String timeSpace;// 时间区域
    private long timeStamp;// 时间戳
    private UploadDataState uploadState = UploadDataState.NOTUPLOAD;// 上传状态
    private String timeLength;// 时长
    private float dataSize;// 数据大小
    private float uploadDataSize;// 已经上传数据大小

    public boolean isYearAndMonth() {
        return IsYearAndMonth;
    }

    public void setIsYearAndMonth(boolean isYearAndMonth) {
        IsYearAndMonth = isYearAndMonth;
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

    public UploadDataState getUploadState() {
        return uploadState;
    }

    public void setUploadState(UploadDataState uploadState) {
        this.uploadState = uploadState;
    }

    public void setUploadState(int state) {
        switch (state) {
            case 0:
                uploadState = UploadDataState.UPLOADING;
                break;
            case 1:
                uploadState = UploadDataState.WAITUPLOAD;
                break;
            case 2:
                uploadState = UploadDataState.UPLOADPAUSE;
                break;
            case 3:
                uploadState = UploadDataState.UPLOADAGINE;
                break;
            case 4:
                uploadState = UploadDataState.NOTUPLOAD;
                break;
            case 5:
                uploadState = UploadDataState.UPLOADOVER;
                break;

        }
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

    public float getUploadDataSize() {
        return uploadDataSize;
    }

    public void setUploadDataSize(float uploadDataSize) {
        this.uploadDataSize = uploadDataSize;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public UploadDataBean() {
    }

    @Override
    public String toString() {
        return "UploadDataBean{" +
                "IsYearAndMonth=" + IsYearAndMonth +
                ", year=" + year +
                ", month=" + month +
                ", dayOfMonth=" + dayOfMonth +
                ", timeSpace='" + timeSpace + '\'' +
                ", timeStamp=" + timeStamp +
                ", uploadState=" + uploadState +
                ", timeLength='" + timeLength + '\'' +
                ", dataSize=" + dataSize +
                ", uploadDataSize=" + uploadDataSize +
                '}';
    }
}
