package com.electrocardio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.electrocardio.Bean.UserInformation;
import com.electrocardio.util.ConstantUtils;

import java.util.Calendar;

/**
 * Created by ZhangBo on 2016/03/21.
 */
public class UserDao {
    private Context mContext;
    // 创建数据库的类
    private ECGDatabaseHelper helper;
    // 单利设计模式
    private static UserDao instance;

    /**
     * UserDao的构造函数
     *
     * @param context
     */
    private UserDao(Context context) {
        mContext = context;
        helper = new ECGDatabaseHelper(mContext, ConstantUtils.APPNAME, 1);
    }

    /**
     * 获取UserDao的实例
     *
     * @param context
     * @return
     */
    public synchronized static UserDao getInstance(Context context) {
        if (instance == null) {
            instance = new UserDao(context);
        }
        return instance;
    }

    // 存放User的table的名称
    private static String TABLENAME = ConstantUtils.USERTABLE;

    /**
     * 增加用户
     *
     * @param userID
     * @param phoneNumber
     * @param password
     * @return
     */
    public boolean addUser(String userID, String phoneNumber, String password) {
        return addUser(userID, phoneNumber, password, "");
    }

    /**
     * 增加用户
     *
     * @param userId
     * @param phoneNumber
     * @param password
     * @param token
     * @return
     */
    public boolean addUser(String userId, String phoneNumber, String password, String token) {
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("phonenumber", phoneNumber);
        values.put("password", password);
        values.put("token", token);
        values.put("username", "");
        values.put("sex", UserInformation.MALE);
        values.put("birthday", 0);
        values.put("height", 0);
        values.put("weight", 0);
        values.put("deviceID", "");
        values.put("logoSrc", "");
        values.put("endTime", "");
        if (hasUserName()) {
            return dataBase.update(TABLENAME, values, "id = ?", new String[]{"1"}) > 0;
        } else {
            return dataBase.insert(TABLENAME, null, values) != -1l;
        }
    }

    /**
     * 更新结束时间
     *
     * @param endTime
     * @return
     */
    public boolean updateEndTime(long endTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String monthStr = month < 10 ? "0" + month : month + "";
        String dayStr = day < 10 ? "0" + day : day + "";
        String hourStr = hour < 10 ? "0" + hour : hour + "";
        String minuteStr = minute < 10 ? "0" + minute : minute + "";
        String secondStr = second < 10 ? "0" + second : second + "";
        String endTimeStr = year + "-" + monthStr + "-" + dayStr + " " + hourStr + ":" + minuteStr + ":" + secondStr;
        return updateEndTime(endTimeStr);
    }

    /**
     * 更新结束时间
     *
     * @param endTime
     * @return
     */
    public boolean updateEndTime(String endTime) {
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("endTime", endTime);
        int num = dataBase.update(TABLENAME, values, "id = ?", new String[]{"1"});
        return num > 0;
    }

    /**
     * 更新头像路径
     *
     * @param logoPath
     * @return
     */
    public boolean updateLogoPath(String logoPath) {
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("logoSrc", logoPath);
        int num = dataBase.update(TABLENAME, values, "id = ?", new String[]{"1"});
        return num > 0;
    }

    /**
     * 是否有用户
     *
     * @return
     */
    private boolean hasUserName() {
        SQLiteDatabase dataBase = helper.getReadableDatabase();
        Cursor cursor = dataBase.query(TABLENAME, null, null, null, null, null, null);
        int num = cursor.getCount();
        cursor.close();
        return num > 0;
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public String getUserID() {
        String username = "";
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        Cursor cursor = dataBase.query(TABLENAME, null, "id = ?", new String[]{"1"}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            username = cursor.getString(cursor.getColumnIndex("userId"));
        }
        cursor.close();
        return username;
    }

    /**
     * 获取phonenumber
     *
     * @return
     */
    public String getPhoneNumber() {
        String phonenumber = "";
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        Cursor cursor = dataBase.query(TABLENAME, null, "id = ?", new String[]{"1"}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            phonenumber = cursor.getString(cursor.getColumnIndex("phonenumber"));
        }
        cursor.close();
        return phonenumber;
    }

    /**
     * 获取用户
     *
     * @return
     */
    public UserInformation getUser() {
        UserInformation userInformation = null;
        SQLiteDatabase dataBase = helper.getReadableDatabase();
        Cursor cursor = dataBase.query(TABLENAME, null, "id = ?", new String[]{"1"}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            userInformation = new UserInformation();
            userInformation.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
            userInformation.setUserName(cursor.getString(cursor.getColumnIndex("username")));
            userInformation.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phonenumber")));
            userInformation.setSEX(cursor.getInt(cursor.getColumnIndex("sex")));
            userInformation.setBirthday(cursor.getInt(cursor.getColumnIndex("birthday")));
            userInformation.setHeight(cursor.getString(cursor.getColumnIndex("height")) + "");
            userInformation.setWeight(cursor.getString(cursor.getColumnIndex("weight")) + "");
            userInformation.setDeviceId(cursor.getString(cursor.getColumnIndex("deviceID")));
            userInformation.setEndTime(cursor.getString(cursor.getColumnIndex("endTime")));
            userInformation.setLogoSrc(cursor.getString(cursor.getColumnIndex("logoSrc")));
            userInformation.setUserToken(cursor.getString(cursor.getColumnIndex("token")));
        }
        cursor.close();
        return userInformation;
    }

    /**
     * 更新用户名
     *
     * @param userName
     * @return
     */
    public boolean updateUserName(String userName) {
        if (!hasUserName())
            return false;
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", userName);
        int num = dataBase.update(TABLENAME, values, "id = ?", new String[]{"1"});
        return num > 0;
    }

    /**
     * 更新性别
     *
     * @param sex
     * @return
     */
    public boolean updateSex(int sex) {
        if (!hasUserName())
            return false;
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sex", sex);
        int num = dataBase.update(TABLENAME, values, "id = ?", new String[]{"1"});
        return num > 0;
    }

    /**
     * 更新身高
     *
     * @param height
     * @return
     */
    public boolean updateHeight(String height) {
        if (!hasUserName())
            return false;
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("height", height);
        int num = dataBase.update(TABLENAME, values, "id = ?", new String[]{"1"});
        return num > 0;
    }

    /**
     * 更新体重
     *
     * @param weight
     * @return
     */
    public boolean updateWeight(String weight) {
        if (!hasUserName())
            return false;
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("weight", weight);
        int num = dataBase.update(TABLENAME, values, "id = ?", new String[]{"1"});
        return num > 0;
    }

    /**
     * 更新密码
     *
     * @param password
     * @return
     */
    public boolean updatePassword(String password) {
        if (!hasUserName())
            return false;
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", password);
        int num = dataBase.update(TABLENAME, values, "id = ?", new String[]{"1"});
        return num > 0;
    }

    /**
     * 更新出生日期
     *
     * @param year
     * @param month
     * @param day
     */
    public boolean updateBirthday(int year, int month, int day) {
        if (!hasUserName())
            return false;
        int ymd = ((year - 1900) << 16) + (month << 8) + day;
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("birthday", ymd);
        int num = dataBase.update(TABLENAME, values, "id = ?", new String[]{"1"});
        return num > 0;
    }

    /**
     * 更新token
     *
     * @param token
     * @return
     */
    public boolean updateToken(String token) {
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("token", token);
        int num = dataBase.update(TABLENAME, values, "id = ?", new String[]{"1"});
        return num > 0;
    }

    /**
     * 获取token
     *
     * @return
     */
    public String getToken() {
        String token = null;
        SQLiteDatabase dataBase = helper.getReadableDatabase();
        Cursor cursor = dataBase.query(TABLENAME, null, "id = ?", new String[]{"1"}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            token = cursor.getString(cursor.getColumnIndex("token"));
        }
        cursor.close();
        return token;
    }

    /**
     * 获取头像路径
     *
     * @return
     */
    public String getLogoPath() {
        String logoPath = null;
        SQLiteDatabase dataBase = helper.getReadableDatabase();
        Cursor cursor = dataBase.query(TABLENAME, null, "id = ?", new String[]{"1"}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            logoPath = cursor.getString(cursor.getColumnIndex("logoSrc"));
        }
        cursor.close();
        return logoPath;
    }

    /**
     * 更新deviceId
     *
     * @param deviceId
     * @return
     */
    public boolean updateDeviceId(String deviceId) {
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("deviceID", deviceId);
        int num = dataBase.update(TABLENAME, values, "id = ?", new String[]{"1"});
        return num > 0;
    }

    /**
     * 获取设别ID
     *
     * @return
     */
    public String getDeviceId() {
        String deviceId = null;
        SQLiteDatabase dataBase = helper.getReadableDatabase();
        Cursor cursor = dataBase.query(TABLENAME, null, "id = ?", new String[]{"1"}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            deviceId = cursor.getString(cursor.getColumnIndex("deviceID"));
        }
        cursor.close();
        return deviceId;
    }


    /**
     * 清理用户
     */
    public boolean clearUser() {
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        return dataBase.delete(TABLENAME, null, null) != -1;
    }
}
