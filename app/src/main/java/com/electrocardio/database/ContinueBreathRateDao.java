package com.electrocardio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.electrocardio.databasebean.ContinueBreathRate;
import com.electrocardio.util.ConstantUtils;

/**
 * Created by ZhangBo on 2016/03/24.
 */
public class ContinueBreathRateDao {

    private Context mContext;
    // 创建数据库的类
    private ECGDatabaseHelper helper;
    // 单例设计模式
    private static ContinueBreathRateDao instance;

    /**
     * ContinueBreathRateDao的构造函数
     *
     * @param context
     */
    private ContinueBreathRateDao(Context context) {
        mContext = context;
        helper = new ECGDatabaseHelper(mContext, ConstantUtils.APPNAME, 1);
    }

    /**
     * 获取ContinueHeartRateDao的实例
     *
     * @param context
     * @return
     */
    public synchronized static ContinueBreathRateDao getInstance(Context context) {
        if (instance == null) {
            instance = new ContinueBreathRateDao(context);
        }
        return instance;
    }

    // 存放ContinueBreathRateDao的table的名称
    private static String TABLENAME = ConstantUtils.CONHEARTRATE;
    // 用户名
    private String USERNAME = "";

    /**
     * 添加一条连续测量呼吸率的记录
     *
     * @param continueBreathRate
     * @return
     */
    public boolean addOneContinueBreathRateRecord(ContinueBreathRate continueBreathRate) {
        USERNAME = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", USERNAME);
        values.put("timestamp", continueBreathRate.getTimeStamp());
        values.put("rate", continueBreathRate.getRate());
        values.put("averagerate", continueBreathRate.getAverageBreathRate());
        values.put("datafilesrc", continueBreathRate.getDataFileSrc());
        return database.insert(TABLENAME, null, values) > 0;
    }

    /**
     * 更新一条连续测量呼吸率的记录
     *
     * @param continueBreathRate
     * @return
     */
    public boolean updateOneContinueBreathRateRecord(ContinueBreathRate continueBreathRate) {
        USERNAME = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("rate", continueBreathRate.getRate());
        values.put("averagerate", continueBreathRate.getAverageBreathRate());
        return database.update(TABLENAME, values, "username = ? and timestamp = ?", new String[]
                {USERNAME, continueBreathRate.getTimeStamp() + ""}) > 0;
    }

    /**
     * 根据时间戳获取一条连续测量的呼吸率的记录
     *
     * @param timeStamp
     * @return
     */
    public ContinueBreathRate getOneContinueBreathRate(long timeStamp) {
        USERNAME = UserDao.getInstance(mContext).getUserID();
        ContinueBreathRate continueBreathRate = null;
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and timestamp = ?",
                new String[]{USERNAME, timeStamp + ""}, null, null, null);
        if (cursor.getCount() > 0) {
            continueBreathRate = new ContinueBreathRate();
            continueBreathRate.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
            continueBreathRate.setAverageBreathRate(cursor.getInt(cursor.getColumnIndex("averagerate")));
            continueBreathRate.setRate(cursor.getInt(cursor.getColumnIndex("rate")));
            continueBreathRate.setDataFileSrc(cursor.getString(cursor.getColumnIndex("datafilesrc")));
        }
        cursor.close();
        return continueBreathRate;
    }

    /**
     * 根据时间戳删除一条记录
     *
     * @param timeStamp
     * @return
     */
    public boolean deleteOneRecordByTimeStamp(long timeStamp) {
        USERNAME = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        return database.delete(TABLENAME, "username = ? and timestamp = ?",
                new String[]{USERNAME, timeStamp + ""}) > 0;
    }

}
