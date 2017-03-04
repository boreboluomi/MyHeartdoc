package com.electrocardio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.electrocardio.databasebean.SixtySecondsBreathRate;
import com.electrocardio.util.ConstantUtils;

/**
 * Created by ZhangBo on 2016/03/22.记录60秒测量呼吸率的表
 */
public class SixtySecondsBreathRateDao {

    private Context mContext;
    // 创建数据库的类
    private ECGDatabaseHelper helper;
    // 单利设计模式
    private static SixtySecondsBreathRateDao instance;

    /**
     * SixtySecondsBreathRateDao的构造函数
     *
     * @param context
     */
    private SixtySecondsBreathRateDao(Context context) {
        mContext = context;
        helper = new ECGDatabaseHelper(mContext, ConstantUtils.APPNAME, 1);
    }

    /**
     * 获取SixtySecondsBreathRateDao的实例
     *
     * @param context
     * @return
     */
    public synchronized static SixtySecondsBreathRateDao getInstance(Context context) {
        if (instance == null) {
            instance = new SixtySecondsBreathRateDao(context);
        }
        return instance;
    }

    // 存放SixtySecondsBreathRateDao的table的名称
    private static String TABLENAME = ConstantUtils.SIXTYSECONDSBREATHRATE;
    // 用户名
    private String USERID = "";

    /**
     * 添加一条60秒测量的呼吸率的记录
     *
     * @param sixtySecondsBreathRate
     * @return
     */
    public boolean addOneSixtySecondsBreathRate(SixtySecondsBreathRate sixtySecondsBreathRate) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", USERID);
        values.put("timeStamp", sixtySecondsBreathRate.getTimeStamp());
        values.put("breathRate", sixtySecondsBreathRate.getRate());
        values.put("average", sixtySecondsBreathRate.getAverageBreathRate());
        return database.insert(TABLENAME, null, values) != -1l;
    }

    /**
     * 获取一条60秒测量的呼吸率的记录
     *
     * @param timeStamp
     * @return
     */
    public SixtySecondsBreathRate getOneSixtySecondsBreathRate(long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SixtySecondsBreathRate sixtySecondsBreathRate = new SixtySecondsBreathRate();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and timeStamp = ?",
                new String[]{USERID, timeStamp + ""}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            sixtySecondsBreathRate.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timeStamp")));
            sixtySecondsBreathRate.setRate(cursor.getInt(cursor.getColumnIndex("breathRate")));
            sixtySecondsBreathRate.setAverageBreathRate(cursor.getInt(cursor.getColumnIndex("average")));
        }
        cursor.close();
        return sixtySecondsBreathRate;
    }

}
