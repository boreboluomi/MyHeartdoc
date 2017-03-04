package com.electrocardio.Bean;

import java.util.Calendar;

/**
 * Created by ZhangBo on 2016/04/22.
 */
public class UserInformation {

    private String userId;//用户ID
    private String userName;// 用户名
    public static int MALE = 1;// 男性
    public static int FEMALE = 0;// 女性
    private int age = 0;// 年龄
    private int SEX = 0;//性别
    private int birthday = 0;// 出生日期
    private String height;// 身高
    private String weight;// 体重
    private String phoneNumber;// 电话号码
    private String logoSrc;// 头像路径
    private String deviceId;// 设备ID
    private String userToken;// 用户免登录令牌
    private String endTime;// 服务结束时间

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSEX() {
        return SEX;
    }

    public void setSEX(int SEX) {
        this.SEX = SEX;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getBirthday() {
        return birthday;
    }

    public String getLogoSrc() {
        return logoSrc;
    }

    public void setLogoSrc(String logoSrc) {
        this.logoSrc = logoSrc;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getBirthdayLabel() {
        int year = (birthday >>> 16) + 1900;
        int month = (birthday >>> 8) & 0xff;
        int day = birthday & 0xff;
        String monthStr = month < 10 ? "0" + month : month + "";
        String dayStr = day < 10 ? "0" + day : day + "";
        return year + "年" + monthStr + "月" + dayStr + "日";
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
        age = Calendar.getInstance().get(Calendar.YEAR) - ((birthday >>> 16) + 1900) + 1;
    }

    public int getAge() {
        return age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "UserInformation{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", SEX=" + SEX +
                ", birthday=" + birthday +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", logoSrc='" + logoSrc + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", userToken='" + userToken + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
