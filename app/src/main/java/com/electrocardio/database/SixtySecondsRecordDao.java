package com.electrocardio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.electrocardio.Bean.SixSecondMeasureBean;
import com.electrocardio.databasebean.SixtySecondsRecord;
import com.electrocardio.util.ConstantUtils;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/03/22.记录60秒测量概况的表
 */
public class SixtySecondsRecordDao {

    private Context mContext;
    // 创建数据库的类
    private ECGDatabaseHelper helper;
    // 单利设计模式
    private static SixtySecondsRecordDao instance;

    /**
     * SixtySecondsRecordDao的构造函数
     *
     * @param context
     */
    private SixtySecondsRecordDao(Context context) {
        mContext = context;
        helper = new ECGDatabaseHelper(mContext, ConstantUtils.APPNAME, 1);
    }

    /**
     * 获取SixtySecondsRecordDao的实例
     *
     * @param context
     * @return
     */
    public synchronized static SixtySecondsRecordDao getInstance(Context context) {
        if (instance == null) {
            instance = new SixtySecondsRecordDao(context);
        }
        return instance;
    }

    // 存放SixtySecondsRecordDao的table的名称
    private static String TABLENAME = ConstantUtils.SIXTYSECONDSRECORD;
    // 用户名
    private String USERID = "";

    /**
     * 添加一条60s测量记录
     *
     * @param year
     * @param month
     * @param day
     * @param sixtySecondsRecord
     * @return
     */
    public boolean addSixtySecondsRecord(int year, int month, int day, SixtySecondsRecord sixtySecondsRecord) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SixtySecondsYMDDao.getInstance(mContext).addYearAndMonthAndDay(year, month, day);
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", USERID);
        values.put("ymd", ((year - 1970) << 16) + (month << 8) + day);
        values.put("timeStamp", sixtySecondsRecord.getTimeStamp());
        values.put("startTime", sixtySecondsRecord.getStartTime());
        values.put("endTime", sixtySecondsRecord.getEndTime());
        values.put("averHeartRate", sixtySecondsRecord.getAverageHeartRate());
        values.put("healthState", sixtySecondsRecord.getHealthState());
        values.put("healthIndex", sixtySecondsRecord.getHealthIndex());
        values.put("dataFileSrc", sixtySecondsRecord.getDataFileSrc());
        return database.insert(TABLENAME, null, values) != -1l;
    }

    /**
     * 获取某天中的记录
     *
     * @param yearAndMonth
     * @param day
     * @return
     */
    public ArrayList<SixtySecondsRecord> getRecoredByYMD(int yearAndMonth, int day) {
        USERID = UserDao.getInstance(mContext).getUserID();
        ArrayList<SixtySecondsRecord> array = new ArrayList<>();
        SixtySecondsRecord sixtySecondsRecord;
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and ymd = ?",
                new String[]{USERID, ((yearAndMonth << 8) + day) + ""}, null, null, "timeStamp desc");
        while (cursor.moveToNext()) {
            sixtySecondsRecord = new SixtySecondsRecord();
            sixtySecondsRecord.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timeStamp")));
            sixtySecondsRecord.setStartTime(cursor.getInt(cursor.getColumnIndex("startTime")));
            sixtySecondsRecord.setEndTime(cursor.getInt(cursor.getColumnIndex("endTime")));
            sixtySecondsRecord.setAverageHeartRate(cursor.getInt(cursor.getColumnIndex("averHeartRate")));
            sixtySecondsRecord.setHealthState(cursor.getString(cursor.getColumnIndex("healthState")));
            sixtySecondsRecord.setHealthIndex(cursor.getInt(cursor.getColumnIndex("healthIndex")));
            sixtySecondsRecord.setDataFileSrc(cursor.getString(cursor.getColumnIndex("dataFileSrc")));
            array.add(sixtySecondsRecord);
        }
        cursor.close();
        return array;
    }

    /**
     * 获取最近的一条记录
     *
     * @return
     */
    public SixSecondMeasureBean getRecentHeartRateRecord() {
        USERID = UserDao.getInstance(mContext).getUserID();
        SixSecondMeasureBean sixSecondMeasureBean = null;
        SixtySecondsRecord sixtySecondsRecord = null;
        int number;
        String startHour;
        String startMinute;
        String startSecond;
        String endHour;
        String endMinute;
        String endSecond;
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ?",
                new String[]{USERID}, null, null, null);
        if (cursor.moveToLast()) {
            sixtySecondsRecord = new SixtySecondsRecord();
            sixtySecondsRecord.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timeStamp")));
            sixtySecondsRecord.setStartTime(cursor.getInt(cursor.getColumnIndex("startTime")));
            sixtySecondsRecord.setEndTime(cursor.getInt(cursor.getColumnIndex("endTime")));
            sixtySecondsRecord.setAverageHeartRate(cursor.getInt(cursor.getColumnIndex("averHeartRate")));
            sixtySecondsRecord.setHealthState(cursor.getString(cursor.getColumnIndex("healthState")));
            sixtySecondsRecord.setHealthIndex(cursor.getInt(cursor.getColumnIndex("healthIndex")));
            sixtySecondsRecord.setDataFileSrc(cursor.getString(cursor.getColumnIndex("dataFileSrc")));
        }
        cursor.close();
        if (sixtySecondsRecord != null) {
            sixSecondMeasureBean = new SixSecondMeasureBean();
            sixSecondMeasureBean.setTimeStamp(sixtySecondsRecord.getTimeStamp());
            sixSecondMeasureBean.setBmp(sixtySecondsRecord.getAverageHeartRate());
            sixSecondMeasureBean.setHealthDescrip(sixtySecondsRecord.getHealthState());
            sixSecondMeasureBean.setHealthNumber(sixtySecondsRecord.getHealthIndex());
            number = sixtySecondsRecord.getStartTime() >>> 16 & 0xff;
            startHour = number < 10 ? "0" + number : number + "";
            number = sixtySecondsRecord.getStartTime() >>> 8 & 0xff;
            startMinute = number < 10 ? "0" + number : number + "";
            number = sixtySecondsRecord.getStartTime() & 0xff;
            startSecond = number < 10 ? "0" + number : number + "";

            number = sixtySecondsRecord.getEndTime() >>> 16 & 0xff;
            endHour = number < 10 ? "0" + number : number + "";
            number = sixtySecondsRecord.getEndTime() >>> 8 & 0xff;
            endMinute = number < 10 ? "0" + number : number + "";
            number = sixtySecondsRecord.getEndTime() & 0xff;
            endSecond = number < 10 ? "0" + number : number + "";
            sixSecondMeasureBean.setDataFileSrc(sixtySecondsRecord.getDataFileSrc());
            sixSecondMeasureBean.setTimeSpace(startHour + ":" + startMinute + ":" + startSecond +
                    "~" + endHour + ":" + endMinute + ":" + endSecond);
        }
        return sixSecondMeasureBean;
    }

    /**
     * 获取某天中的记录
     *
     * @param yearAndMonth
     * @param day
     * @return
     */
    public ArrayList<SixSecondMeasureBean> getRecoredBeanByYMD(int yearAndMonth, int day) {
        USERID = UserDao.getInstance(mContext).getUserID();
        ArrayList<SixSecondMeasureBean> array = new ArrayList<>();
        int number;
        String startHour;
        String startMinute;
        String startSecond;
        String endHour;
        String endMinute;
        String endSecond;
        SixSecondMeasureBean sixSecondMeasureBean;
        ArrayList<SixtySecondsRecord> recordArray = getRecoredByYMD(yearAndMonth, day);
        if (recordArray.size() != 0) {
            sixSecondMeasureBean = new SixSecondMeasureBean();
            sixSecondMeasureBean.setIsDayOfMonth(true);
            sixSecondMeasureBean.setDayOfMonth(day);
            array.add(sixSecondMeasureBean);
        }
        for (SixtySecondsRecord sixtySecondsRecord : recordArray) {
            sixSecondMeasureBean = new SixSecondMeasureBean();
            sixSecondMeasureBean.setTimeStamp(sixtySecondsRecord.getTimeStamp());
            sixSecondMeasureBean.setBmp(sixtySecondsRecord.getAverageHeartRate());
            sixSecondMeasureBean.setHealthDescrip(sixtySecondsRecord.getHealthState());
            sixSecondMeasureBean.setHealthNumber(sixtySecondsRecord.getHealthIndex());
            number = sixtySecondsRecord.getStartTime() >>> 16 & 0xff;
            startHour = number < 10 ? "0" + number : number + "";
            number = sixtySecondsRecord.getStartTime() >>> 8 & 0xff;
            startMinute = number < 10 ? "0" + number : number + "";
            number = sixtySecondsRecord.getStartTime() & 0xff;
            startSecond = number < 10 ? "0" + number : number + "";

            number = sixtySecondsRecord.getEndTime() >>> 16 & 0xff;
            endHour = number < 10 ? "0" + number : number + "";
            number = sixtySecondsRecord.getEndTime() >>> 8 & 0xff;
            endMinute = number < 10 ? "0" + number : number + "";
            number = sixtySecondsRecord.getEndTime() & 0xff;
            endSecond = number < 10 ? "0" + number : number + "";
            sixSecondMeasureBean.setDataFileSrc(sixtySecondsRecord.getDataFileSrc());
            sixSecondMeasureBean.setTimeSpace(startHour + ":" + startMinute + ":" + startSecond +
                    "~" + endHour + ":" + endMinute + ":" + endSecond);
            array.add(sixSecondMeasureBean);
        }
        return array;
    }

}
