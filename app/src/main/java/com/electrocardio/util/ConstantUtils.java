package com.electrocardio.util;

/**
 * Created by ZhangBo on 2016/2/24.
 */
public class ConstantUtils {

    public static String HOST = "http://realtime.ddduo.com/realtime/AppServiceController/";// 主机地址
    public static String REGISTER = "appRegister";// 注册接口
    public static String OBTAINDEVICEIDANDTIME = "userService";//获取设备ID的接口
    public static String UPDATEINFOR = "appUpdateInfo";//修改个人信息
    public static String LOGIN = "appLogin";// 用户登录
    public static String UPLOADEECG = "UploadHandleServlet";// 上传心电图
    public static String NOTIFYPASSWORD = "appLoginPassword";// 修改登录密码
    public static String CHANGEPASSWORD = "appLoginNum";// 修改登录密码

    public static String APPNAME = "ECG";// app名称
    public static String USERNAMEINSP = "spusername";// sp文件中存储的username的key值
    public static String ISONLYWIFI = "isonlywifi";// 是否仅在wifi状态下上传

    public static String USERTABLE = "usertable";// 存储用户名的表
    public static String SIXTYSECONDSYMD = "sixtysecondsymd";// 存储60秒记录年月日的表
    public static String SIXTYSECONDSRECORD = "sixtysecondsrecord";// 存储60秒记录的表
    public static String SIXTYSECONDSHEARTRATE = "sixtysecondsheartrate";//存储60秒记录心率情况的表
    public static String SIXTYSECONDSBREATHRATE = "sixtysecondsbreathrate";// 存储60秒记录呼吸率情况的表

    public static String CONABNORMALLIST = "conabnormallist";// 存储连续测量异常信息的表
    public static String CONHEARTRATE = "conheartrate";// 存储连续测量心率信息的表
    public static String CONBREATHRATE = "conbreathrate";// 存储连续测量呼吸率信息的表
    public static String CONMEASUREYM = "conmeasureym";// 存储连续测量年月的表
    public static String CONMEASURERECORD = "conmeasurerecord";// 存储连续测量基本秒数的表
    public static String UPLOADCONMEASURE = "uploadconmeasure";// 存储将要上传的连续测量的记录的表

    public static String DIUBAO = "diubao";// 丢包
    public static String ONEMV = "onemv";// 1mv校准

    /**
     * 判断某一年是否为闰年
     *
     * @param year
     * @return
     */
    public static boolean isRunNian(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }
}
