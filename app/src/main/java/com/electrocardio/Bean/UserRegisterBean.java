package com.electrocardio.Bean;

/**
 * Created by yangzhegn on 2016/3/29.
 * 注册接口
 */
public class UserRegisterBean {

    private String statusCode ;
    private String codeVal  ;
    private String phoneVal;
    private String num;
    private String timeVal;
    private String usertoken;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getCodeVal() {
        return codeVal;
    }

    public void setCodeVal(String codeVal) {
        this.codeVal = codeVal;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPhoneVal() {
        return phoneVal;
    }

    public void setPhoneVal(String phoneVal) {
        this.phoneVal = phoneVal;
    }

    public String getTimeVal() {
        return timeVal;
    }

    public void setTimeVal(String timeVal) {
        this.timeVal = timeVal;
    }

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }
}
