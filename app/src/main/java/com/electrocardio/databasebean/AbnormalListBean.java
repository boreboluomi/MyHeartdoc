package com.electrocardio.databasebean;

import java.util.Calendar;

/**
 * Created by ZhangBo on 2016/03/23.异常列表的bean类
 */
public class AbnormalListBean {

    private String timeLabel;// 时间
    private String description;// 异常描述
    private int TYPE = 0;// 类型
    private int DETAILTYPE = -1;
    public static final int ABNORMAL = 0;// 异常
    public static final int MARK = 1;// 手动截取
    public static final int START = 2;// 开始
    public static final int TIMECUT = 3;// 定时截取
    private long ymTimeStamp;// 年月的时间戳
    private long timeStamp;// 时间戳
    private int submited = 0;// 是否已经提交
    public static final int SUBMITED = 1;//已经提交
    public static final int NOTSUBMITED = 0;// 没有提交
    private String dataFileDir = "";// 数据文件存储目录
    private String dataFileSrc;// 数据文件存储路径
    private float fileSize;// 数据文件大小
    private int heartRate;// 心率

    public String getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getTYPE() {
        return TYPE;
    }

    public int getDETAILTYPE() {
        return DETAILTYPE;
    }

    public void setTYPE(int TYPE) {
        DETAILTYPE = TYPE;
        switch (TYPE) {
            case AbnormalSignInfor.MARK:
                this.TYPE = MARK;
                break;
            case AbnormalSignInfor.START:
                this.TYPE = START;
                break;
            case AbnormalSignInfor.TIMERCUT:
                this.TYPE = TIMECUT;
                break;
            default:
                this.TYPE = ABNORMAL;
                break;
        }
        description = AbnormalSignInfor.getStateDescription(TYPE);
    }

    public long getYmTimeStamp() {
        return ymTimeStamp;
    }

    public void setYmTimeStamp(long ymTimeStamp) {
        this.ymTimeStamp = ymTimeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String monStr = month < 10 ? "0" + month : month + "";
        String dayStr = day < 10 ? "0" + day : day + "";
        String hourStr = hour < 10 ? "0" + hour : hour + "";
        String minuteStr = minute < 10 ? "0" + minute : minute + "";
        String secondStr = second < 10 ? "0" + second : second + "";
        timeLabel = monStr + "/" + dayStr + " " + hourStr + ":" + minuteStr + ":" + secondStr;
    }

    public String getDataFileDir() {
        return dataFileDir;
    }

    public void setDataFileDir(String dataFileDir) {
        this.dataFileDir = dataFileDir;
    }

    public int getSubmited() {
        return submited;
    }

    public void setSubmited(int submited) {
        this.submited = submited;
    }

    public String getDataFileSrc() {
        return dataFileSrc;
    }

    public void setDataFileSrc(String dataFileSrc) {
        this.dataFileSrc = dataFileSrc;
    }

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "AbnormalListBean{" +
                "timeLabel='" + timeLabel + '\'' +
                ", description='" + description + '\'' +
                ", DETAILTYPE=" + DETAILTYPE +
                ", ymTimeStamp=" + ymTimeStamp +
                ", timeStamp=" + timeStamp +
                ", submited=" + submited +
                ", dataFileDir='" + dataFileDir + '\'' +
                ", dataFileSrc='" + dataFileSrc + '\'' +
                ", fileSize=" + fileSize +
                ", heartRate=" + heartRate +
                ", TYPE=" + TYPE +
                '}';
    }

    public AbnormalListBean() {
    }
}
