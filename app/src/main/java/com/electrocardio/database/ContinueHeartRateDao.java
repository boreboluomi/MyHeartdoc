package com.electrocardio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.electrocardio.databasebean.ContinueHeartRate;
import com.electrocardio.util.ConstantUtils;

/**
 * Created by ZhangBo on 2016/03/24.
 */
public class ContinueHeartRateDao {

    private Context mContext;
    // 创建数据库的类
    private ECGDatabaseHelper helper;
    // 单例设计模式
    private static ContinueHeartRateDao instance;

    /**
     * ContinueHeartRateDao的构造函数
     *
     * @param context
     */
    private ContinueHeartRateDao(Context context) {
        mContext = context;
        helper = new ECGDatabaseHelper(mContext, ConstantUtils.APPNAME, 1);
    }

    /**
     * 获取ContinueHeartRateDao的实例
     *
     * @param context
     * @return
     */
    public synchronized static ContinueHeartRateDao getInstance(Context context) {
        if (instance == null) {
            instance = new ContinueHeartRateDao(context);
        }
        return instance;
    }

    // 存放ContinueHeartRateDao的table的名称
    private static String TABLENAME = ConstantUtils.CONHEARTRATE;
    // 用户名
    private String USERID = "";

    /**
     * 增加一条连续测量心率的记录
     *
     * @param continueHeartRate
     * @return
     */
    public boolean addOneContinueHeartRateRecord(ContinueHeartRate continueHeartRate) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", USERID);
        values.put("timestamp", continueHeartRate.getTimeStamp());
        values.put("rate", continueHeartRate.getRate());
        values.put("fastrate", continueHeartRate.getFastestHeartRate());
        values.put("averagerate", continueHeartRate.getAverageHeartRate());
        values.put("slowrate", continueHeartRate.getSlowestHeartRate());
        values.put("beat", continueHeartRate.getBeatCount());
        values.put("datafilesrc", continueHeartRate.getDataFileSrc());
        return database.insert(TABLENAME, null, values) > 0;
    }

    /**
     * 更新一条连续测量心率的记录
     *
     * @param continueHeartRate
     * @return
     */
    public boolean updateOneContinueHeartRateRecord(ContinueHeartRate continueHeartRate) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("rate", continueHeartRate.getRate());
        values.put("fastrate", continueHeartRate.getFastestHeartRate());
        values.put("averagerate", continueHeartRate.getAverageHeartRate());
        values.put("slowrate", continueHeartRate.getSlowestHeartRate());
        values.put("beat", continueHeartRate.getBeatCount());
        return database.update(TABLENAME, values, "username = ? and timestamp = ?", new String[]
                {USERID, continueHeartRate.getTimeStamp() + ""}) > 0;
    }

    /**
     * 根据时间戳获取一条连续测量心率的记录
     *
     * @param timeStamp
     * @return
     */
    public ContinueHeartRate getOneContinueHeartRateRecord(long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        ContinueHeartRate continueHeartRate = null;
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and timestamp = ?",
                new String[]{USERID, timeStamp + ""}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            continueHeartRate = new ContinueHeartRate();
            continueHeartRate.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
            continueHeartRate.setRate(cursor.getInt(cursor.getColumnIndex("rate")));
            continueHeartRate.setFastestHeartRate(cursor.getInt(cursor.getColumnIndex("fastrate")));
            continueHeartRate.setAverageHeartRate(cursor.getInt(cursor.getColumnIndex("averagerate")));
            continueHeartRate.setSlowestHeartRate(cursor.getInt(cursor.getColumnIndex("slowrate")));
            continueHeartRate.setBeatCount(cursor.getInt(cursor.getColumnIndex("beat")));
            continueHeartRate.setDataFileSrc(cursor.getString(cursor.getColumnIndex("datafilesrc")));
        }
        cursor.close();
        return continueHeartRate;
    }

    /**
     * 根据时间戳删除一条连续测量心率的记录
     *
     * @param timeStamp
     * @return
     */
    public boolean deleteOneContinueHeartRateRecord(long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        return database.delete(TABLENAME, "username = ? and timestamp = ?", new String[]
                {USERID, timeStamp + ""}) > 0;
    }

}
