package com.electrocardio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.electrocardio.databasebean.SixtySecondsHeartRate;
import com.electrocardio.util.ConstantUtils;

/**
 * Created by ZhangBo on 2016/03/22.记录60秒测量心率的表
 */
public class SixtySecondsHeartRateDao {

    private Context mContext;
    // 创建数据库的类
    private ECGDatabaseHelper helper;
    // 单利设计模式
    private static SixtySecondsHeartRateDao instance;

    /**
     * SixtySecondsHeartRateDao的构造函数
     *
     * @param context
     */
    private SixtySecondsHeartRateDao(Context context) {
        mContext = context;
        helper = new ECGDatabaseHelper(mContext, ConstantUtils.APPNAME, 1);
    }

    /**
     * 获取SixtySecondsHeartRateDao的实例
     *
     * @param context
     * @return
     */
    public synchronized static SixtySecondsHeartRateDao getInstance(Context context) {
        if (instance == null) {
            instance = new SixtySecondsHeartRateDao(context);
        }
        return instance;
    }

    // 存放SixtySecondsHeartRateDao的table的名称
    private static String TABLENAME = ConstantUtils.SIXTYSECONDSHEARTRATE;
    // 用户名
    private String USERID = "";

    /**
     * 添加一条60秒测量的心率记录
     *
     * @param sixtySecondsHeartRate
     * @return
     */
    public boolean addOneSixtySecondsHeartRate(SixtySecondsHeartRate sixtySecondsHeartRate) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", USERID);
        values.put("timeStamp", sixtySecondsHeartRate.getTimeStamp());
        values.put("heartRate", sixtySecondsHeartRate.getRate());
        values.put("average", sixtySecondsHeartRate.getAverageHeartRate());
        values.put("fast", sixtySecondsHeartRate.getFastestHeartRate());
        values.put("slow", sixtySecondsHeartRate.getSlowestHeartRate());
        return database.insert(TABLENAME, null, values) != -1l;
    }

    /**
     * 获取一条60秒测量的心率记录
     *
     * @param timeStamp
     * @return
     */
    public SixtySecondsHeartRate getSixtySecondsHeartRate(long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SixtySecondsHeartRate sixtySecondsHeartRate = new SixtySecondsHeartRate();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and timeStamp = ?",
                new String[]{USERID, timeStamp + ""}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            sixtySecondsHeartRate.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timeStamp")));
            sixtySecondsHeartRate.setRate(cursor.getInt(cursor.getColumnIndex("heartRate")));
            sixtySecondsHeartRate.setFastestHeartRate(cursor.getInt(cursor.getColumnIndex("fast")));
            sixtySecondsHeartRate.setAverageHeartRate(cursor.getInt(cursor.getColumnIndex("average")));
            sixtySecondsHeartRate.setSlowestHeartRate(cursor.getInt(cursor.getColumnIndex("slow")));
        }
        cursor.close();
        return sixtySecondsHeartRate;
    }

}
