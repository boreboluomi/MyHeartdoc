package com.electrocardio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.electrocardio.databasebean.SixtySecondsYMD;
import com.electrocardio.util.ConstantUtils;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/03/22.记录60秒测量年月日的表
 */
public class SixtySecondsYMDDao {

    private Context mContext;
    // 创建数据库的类
    private ECGDatabaseHelper helper;
    // 单利设计模式
    private static SixtySecondsYMDDao instance;

    /**
     * SixtySecondsYMDDao的构造函数
     *
     * @param context
     */
    private SixtySecondsYMDDao(Context context) {
        mContext = context;
        helper = new ECGDatabaseHelper(mContext, ConstantUtils.APPNAME, 1);
    }

    /**
     * 获取SixtySecondsYMDDao的实例
     *
     * @param context
     * @return
     */
    public synchronized static SixtySecondsYMDDao getInstance(Context context) {
        if (instance == null) {
            instance = new SixtySecondsYMDDao(context);
        }
        return instance;
    }

    // 存放SixtySecondsYMDDao的table的名称
    private static String TABLENAME = ConstantUtils.SIXTYSECONDSYMD;
    // 用户名
    private String USERID = "";

    /**
     * 判断是否有相同的年月日
     *
     * @param yearAndMonth
     * @param day
     * @return
     */
    public boolean hasSameYMD(int yearAndMonth, int day) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase dataBase = helper.getReadableDatabase();
        Cursor cursor = dataBase.query(TABLENAME, null, "username = ? and yearAndMonth = ? and " +
                "day = ?", new String[]{USERID, yearAndMonth + "", day + ""}, null, null, null);
        boolean flag = cursor.getCount() > 0;
        cursor.close();
        return flag;
    }

    /**
     * 添加一条年月日记录
     *
     * @param year
     * @param month
     * @param day
     */
    public boolean addYearAndMonthAndDay(int year, int month, int day) {
        USERID = UserDao.getInstance(mContext).getUserID();
        int y = year - 1970;
        if (!hasSameYMD((y << 8) + month, day)) {
            SQLiteDatabase dataBase = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", USERID);
            values.put("yearAndMonth", (y << 8) + month);
            values.put("day", day);
            return dataBase.insert(TABLENAME, null, values) != -1l;
        } else {
            return true;
        }
    }

    /**
     * 获取一个月中所有的天
     *
     * @param year
     * @param month
     * @return
     */
    public ArrayList<SixtySecondsYMD> getAllDayOnOneMonth(int year, int month) {
        USERID = UserDao.getInstance(mContext).getUserID();
        int yearAndMonth = ((year - 1970) << 8) + month;
        ArrayList<SixtySecondsYMD> array = new ArrayList<>();
        SixtySecondsYMD sixtySecondsYMD;
        SQLiteDatabase dataBase = helper.getReadableDatabase();
        Cursor cursor = dataBase.query(TABLENAME, null, "username = ? and yearAndMonth = ?",
                new String[]{USERID, yearAndMonth + ""}, null, null, "day desc");
        while (cursor.moveToNext()) {
            sixtySecondsYMD = new SixtySecondsYMD();
            sixtySecondsYMD.setYearAndMonth(yearAndMonth);
            sixtySecondsYMD.setDay(cursor.getInt(cursor.getColumnIndex("day")));
            array.add(sixtySecondsYMD);
        }
        cursor.close();
        return array;
    }

    /**
     * 获取所有的年月日信息
     *
     * @return
     */
    public ArrayList<SixtySecondsYMD> getAllData() {
        USERID = UserDao.getInstance(mContext).getUserID();
        ArrayList<SixtySecondsYMD> array = new ArrayList<>();
        SixtySecondsYMD ymd;
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ?", new String[]{USERID}, null, null, null);
        while (cursor.moveToNext()) {
            ymd = new SixtySecondsYMD();
            ymd.setYearAndMonth(cursor.getInt(cursor.getColumnIndex("yearAndMonth")));
            ymd.setDay(cursor.getInt(cursor.getColumnIndex("day")));
            array.add(ymd);
        }
        cursor.close();
        return array;
    }

}
