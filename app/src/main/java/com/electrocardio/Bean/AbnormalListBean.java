package com.electrocardio.Bean;

/**
 * Created by ZhangBo on 2016/3/16.
 */
public class AbnormalListBean {

    private String timeLabel = "02/28 20:19:21";// 时间
    private String description = "窦性心动过速";// 疑似症状描述
    private boolean isHand = false;// 是否为手动截取

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

    public boolean isHand() {
        return isHand;
    }

    public void setIsHand(boolean isHand) {
        this.isHand = isHand;
    }

    @Override
    public String toString() {
        return "AbnormalListBean{" +
                "timeLabel='" + timeLabel + '\'' +
                ", description='" + description + '\'' +
                ", isHand=" + isHand +
                '}';
    }

    public AbnormalListBean() {
    }
}
