package com.electrocardio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.electrocardio.databasebean.ContinueMeasureRecord;
import com.electrocardio.util.ConstantUtils;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/03/24.
 */
public class ContinueMeasureRecordDao {

    private Context mContext;
    // 创建数据库的类
    private ECGDatabaseHelper helper;
    // 单例设计模式
    private static ContinueMeasureRecordDao instance;

    /**
     * ContinueMeasureRecordDao的构造函数
     *
     * @param context
     */
    private ContinueMeasureRecordDao(Context context) {
        mContext = context;
        helper = new ECGDatabaseHelper(mContext, ConstantUtils.APPNAME, 1);
    }

    /**
     * 获取ContinueMeasureRecordDao的实例
     *
     * @param context
     * @return
     */
    public synchronized static ContinueMeasureRecordDao getInstance(Context context) {
        if (instance == null) {
            instance = new ContinueMeasureRecordDao(context);
        }
        return instance;
    }

    // 存放ContinueMeasureRecordDao的table的名称
    private static String TABLENAME = ConstantUtils.CONMEASURERECORD;
    // 用户名
    private String USERID = "";

    /**
     * 添加一条连续测量记录
     *
     * @param continueMeasureRecord
     * @return
     */
    public boolean addOneContinueMeasureRecord(ContinueMeasureRecord continueMeasureRecord) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", USERID);
        values.put("measureym", continueMeasureRecord.getMeasureYM());
        values.put("timestamp", continueMeasureRecord.getTimeStamp());
        values.put("startday", continueMeasureRecord.getStartDay());
        values.put("starttime", continueMeasureRecord.getStartTime());
        values.put("endmd", continueMeasureRecord.getEndMD());
        values.put("endtime", continueMeasureRecord.getEndTime());
        values.put("timelength", continueMeasureRecord.getTimeLength());
        values.put("datasize", continueMeasureRecord.getDataSize());
        values.put("averagerate", continueMeasureRecord.getAverageRate());
        values.put("normalrate", continueMeasureRecord.getRate());
        values.put("measureover", continueMeasureRecord.getMeasureOver());
        values.put("submitedsize", continueMeasureRecord.getSubmitedSize());
        boolean flagOne = database.insert(TABLENAME, null, values) > 0l;
        boolean flagTwo = ContinueMeasureYMDao.getInstance(mContext).addContinueMeasureYM(
                continueMeasureRecord.getMeasureYM());
        return flagOne && flagTwo;
    }

    /**
     * 更新一条记录
     *
     * @param continueMeasureRecord
     * @return
     */
    public boolean updateContinueRecord(ContinueMeasureRecord continueMeasureRecord) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timelength", continueMeasureRecord.getTimeLength());
        values.put("datasize", continueMeasureRecord.getDataSize());
        values.put("submitedsize", continueMeasureRecord.getSubmitedSize());
        values.put("endmd", continueMeasureRecord.getEndMD());
        values.put("endtime", continueMeasureRecord.getEndTime());
        values.put("averagerate", continueMeasureRecord.getAverageRate());
        values.put("normalrate", continueMeasureRecord.getRate());
        values.put("measureover", continueMeasureRecord.getMeasureOver());
        return database.update(TABLENAME, values, "username = ? and timestamp = ?", new String[]{
                USERID, continueMeasureRecord.getTimeStamp() + ""}) > 0;
    }

    /**
     * 更新一条记录(不包括文件大小)
     *
     * @param continueMeasureRecord
     * @return
     */
    public boolean updateContinueRecordNoSize(ContinueMeasureRecord continueMeasureRecord) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timelength", continueMeasureRecord.getTimeLength());
        values.put("endmd", continueMeasureRecord.getEndMD());
        values.put("endtime", continueMeasureRecord.getEndTime());
        values.put("averagerate", continueMeasureRecord.getAverageRate());
        values.put("normalrate", continueMeasureRecord.getRate());
        values.put("measureover", continueMeasureRecord.getMeasureOver());
        return database.update(TABLENAME, values, "username = ? and timestamp = ?", new String[]{
                USERID, continueMeasureRecord.getTimeStamp() + ""}) > 0;
    }

    /**
     * 添加一份未上传文件的大小
     *
     * @param dataSize
     * @param timeStamp
     * @return
     */
    public boolean updataContinueDataSizeByAddOneFile(float dataSize, long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and timestamp = ?",
                new String[]{USERID, timeStamp + ""}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            float ds = cursor.getFloat(cursor.getColumnIndex("submitedsize"));
            cursor.close();
            ContentValues values = new ContentValues();
            values.put("submitedsize", ds + dataSize);
            return database.update(TABLENAME, values, "username = ? and timestamp = ?",
                    new String[]{USERID, timeStamp + ""}) > 0;
        }
        return false;
    }

    /**
     * 获取当前正在测量的记录
     *
     * @return
     */
    public ContinueMeasureRecord getCurrentMeasureRecord() {
        USERID = UserDao.getInstance(mContext).getUserID();
        ContinueMeasureRecord continueMeasureRecord = null;
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ?", new String[]{USERID}, null, null, "id");
        if (cursor.moveToLast()) {
            if (cursor.getInt(cursor.getColumnIndex("measureover")) == ContinueMeasureRecord.MEASURENOTOVER) {
                continueMeasureRecord = new ContinueMeasureRecord();
                continueMeasureRecord.setMeasureYM(cursor.getInt(cursor.getColumnIndex("measureym")));
                continueMeasureRecord.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
                continueMeasureRecord.setStartDay(cursor.getInt(cursor.getColumnIndex("startday")));
                continueMeasureRecord.setStartTime(cursor.getInt(cursor.getColumnIndex("starttime")));
                continueMeasureRecord.setEndMD(cursor.getInt(cursor.getColumnIndex("endmd")));
                continueMeasureRecord.setEndTime(cursor.getInt(cursor.getColumnIndex("endtime")));
                continueMeasureRecord.setTimeLength(cursor.getLong(cursor.getColumnIndex("timelength")));
                continueMeasureRecord.setDataSize(cursor.getFloat(cursor.getColumnIndex("datasize")));
                continueMeasureRecord.setAverageRate(cursor.getInt(cursor.getColumnIndex("averagerate")));
                continueMeasureRecord.setRate(cursor.getInt(cursor.getColumnIndex("normalrate")));
                continueMeasureRecord.setMeasureOver(cursor.getInt(cursor.getColumnIndex("measureover")));
                continueMeasureRecord.setSubmitedSize(cursor.getFloat(cursor.getColumnIndex("submitedsize")));
            }
        }
        cursor.close();
        return continueMeasureRecord;
    }

    /**
     * 获取连续测量记录
     *
     * @return
     */
    public ArrayList<ContinueMeasureRecord> getContinueMeasureRecord() {
        USERID = UserDao.getInstance(mContext).getUserID();
        ContinueMeasureRecord continueMeasureRecord;
        ArrayList<ContinueMeasureRecord> array = new ArrayList<>();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and measureover = ?",
                new String[]{USERID, ContinueMeasureRecord.MEASUREOVER + ""}, null, null, "timestamp DESC");
        while (cursor.moveToNext()) {
            continueMeasureRecord = new ContinueMeasureRecord();
            continueMeasureRecord.setMeasureYM(cursor.getInt(cursor.getColumnIndex("measureym")));
            continueMeasureRecord.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
            continueMeasureRecord.setStartDay(cursor.getInt(cursor.getColumnIndex("startday")));
            continueMeasureRecord.setStartTime(cursor.getInt(cursor.getColumnIndex("starttime")));
            continueMeasureRecord.setEndMD(cursor.getInt(cursor.getColumnIndex("endmd")));
            continueMeasureRecord.setEndTime(cursor.getInt(cursor.getColumnIndex("endtime")));
            continueMeasureRecord.setTimeLength(cursor.getLong(cursor.getColumnIndex("timelength")));
            continueMeasureRecord.setDataSize(cursor.getFloat(cursor.getColumnIndex("datasize")));
            continueMeasureRecord.setAverageRate(cursor.getInt(cursor.getColumnIndex("averagerate")));
            continueMeasureRecord.setRate(cursor.getInt(cursor.getColumnIndex("normalrate")));
            continueMeasureRecord.setMeasureOver(cursor.getInt(cursor.getColumnIndex("measureover")));
            continueMeasureRecord.setSubmitedSize(cursor.getFloat(cursor.getColumnIndex("submitedsize")));
            array.add(continueMeasureRecord);
        }
        cursor.close();
        return array;
    }


    /**
     * 获取没有完全提交的连续测量记录
     *
     * @return
     */
    public ArrayList<ContinueMeasureRecord> getNotSubmitedConMeaRecord() {
        USERID = UserDao.getInstance(mContext).getUserID();
        ContinueMeasureRecord continueMeasureRecord;
        ArrayList<ContinueMeasureRecord> array = new ArrayList<>();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and measureover = ?",
                new String[]{USERID, ContinueMeasureRecord.MEASUREOVER + ""}, null, null, "timestamp DESC");
        while (cursor.moveToNext()) {
            continueMeasureRecord = new ContinueMeasureRecord();
            continueMeasureRecord.setDataSize(cursor.getFloat(cursor.getColumnIndex("datasize")));
            continueMeasureRecord.setSubmitedSize(cursor.getFloat(cursor.getColumnIndex("submitedsize")));
            if (continueMeasureRecord.getDataSize() != continueMeasureRecord.getSubmitedSize()) {
                continueMeasureRecord.setMeasureYM(cursor.getInt(cursor.getColumnIndex("measureym")));
                continueMeasureRecord.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
                continueMeasureRecord.setStartDay(cursor.getInt(cursor.getColumnIndex("startday")));
                continueMeasureRecord.setStartTime(cursor.getInt(cursor.getColumnIndex("starttime")));
                continueMeasureRecord.setEndMD(cursor.getInt(cursor.getColumnIndex("endmd")));
                continueMeasureRecord.setEndTime(cursor.getInt(cursor.getColumnIndex("endtime")));
                continueMeasureRecord.setTimeLength(cursor.getLong(cursor.getColumnIndex("timelength")));
                continueMeasureRecord.setAverageRate(cursor.getInt(cursor.getColumnIndex("averagerate")));
                continueMeasureRecord.setRate(cursor.getInt(cursor.getColumnIndex("normalrate")));
                continueMeasureRecord.setMeasureOver(cursor.getInt(cursor.getColumnIndex("measureover")));
                array.add(continueMeasureRecord);
            }
        }
        cursor.close();
        return array;
    }

    /**
     * 获取完全提交的连续测量记录
     *
     * @return
     */
    public ArrayList<ContinueMeasureRecord> getSubmitedConMeaRecord() {
        USERID = UserDao.getInstance(mContext).getUserID();
        ContinueMeasureRecord continueMeasureRecord;
        ArrayList<ContinueMeasureRecord> array = new ArrayList<>();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and measureover = ?",
                new String[]{USERID, ContinueMeasureRecord.MEASUREOVER + ""}, null, null, "timestamp DESC");
        while (cursor.moveToNext()) {
            continueMeasureRecord = new ContinueMeasureRecord();
            continueMeasureRecord.setDataSize(cursor.getFloat(cursor.getColumnIndex("datasize")));
            continueMeasureRecord.setSubmitedSize(cursor.getFloat(cursor.getColumnIndex("submitedsize")));
            if (continueMeasureRecord.getDataSize() <= continueMeasureRecord.getSubmitedSize()) {
                continueMeasureRecord.setMeasureYM(cursor.getInt(cursor.getColumnIndex("measureym")));
                continueMeasureRecord.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
                continueMeasureRecord.setStartDay(cursor.getInt(cursor.getColumnIndex("startday")));
                continueMeasureRecord.setStartTime(cursor.getInt(cursor.getColumnIndex("starttime")));
                continueMeasureRecord.setEndMD(cursor.getInt(cursor.getColumnIndex("endmd")));
                continueMeasureRecord.setEndTime(cursor.getInt(cursor.getColumnIndex("endtime")));
                continueMeasureRecord.setTimeLength(cursor.getLong(cursor.getColumnIndex("timelength")));
                continueMeasureRecord.setAverageRate(cursor.getInt(cursor.getColumnIndex("averagerate")));
                continueMeasureRecord.setRate(cursor.getInt(cursor.getColumnIndex("normalrate")));
                continueMeasureRecord.setMeasureOver(cursor.getInt(cursor.getColumnIndex("measureover")));
                array.add(continueMeasureRecord);
            }
        }
        cursor.close();
        return array;
    }

    /**
     * 根据测量年月获取连续测量记录的集合
     *
     * @param ym
     * @return
     */
    public ArrayList<ContinueMeasureRecord> getContinueMeasureRecord(int ym) {
        USERID = UserDao.getInstance(mContext).getUserID();
        ContinueMeasureRecord continueMeasureRecord;
        ArrayList<ContinueMeasureRecord> array = new ArrayList<>();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and measureym = ? and measureover = ?",
                new String[]{USERID, ym + "", ContinueMeasureRecord.MEASUREOVER + ""}, null, null, "id DESC");
        while (cursor.moveToNext()) {
            continueMeasureRecord = new ContinueMeasureRecord();
            continueMeasureRecord.setMeasureYM(cursor.getInt(cursor.getColumnIndex("measureym")));
            continueMeasureRecord.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
            continueMeasureRecord.setStartDay(cursor.getInt(cursor.getColumnIndex("startday")));
            continueMeasureRecord.setStartTime(cursor.getInt(cursor.getColumnIndex("starttime")));
            continueMeasureRecord.setEndMD(cursor.getInt(cursor.getColumnIndex("endmd")));
            continueMeasureRecord.setEndTime(cursor.getInt(cursor.getColumnIndex("endtime")));
            continueMeasureRecord.setTimeLength(cursor.getLong(cursor.getColumnIndex("timelength")));
            continueMeasureRecord.setDataSize(cursor.getFloat(cursor.getColumnIndex("datasize")));
            continueMeasureRecord.setAverageRate(cursor.getInt(cursor.getColumnIndex("averagerate")));
            continueMeasureRecord.setRate(cursor.getInt(cursor.getColumnIndex("normalrate")));
            continueMeasureRecord.setMeasureOver(cursor.getInt(cursor.getColumnIndex("measureover")));
            continueMeasureRecord.setSubmitedSize(cursor.getFloat(cursor.getColumnIndex("submitedsize")));
            array.add(continueMeasureRecord);
        }
        cursor.close();
        return array;
    }

    /**
     * 在一定年月下是否有记录
     *
     * @param ym
     * @return
     */
    public boolean hasRecordInYm(int ym) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and measureym = ?",
                new String[]{USERID, ym + ""}, null, null, null);
        int num = cursor.getCount();
        cursor.close();
        return num > 0;
    }

    /**
     * 删除一条连续测量记录
     *
     * @param timeStamp
     * @return
     */
    public boolean deleteOneContinueMeasureRecord(long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and timestamp = ?",
                new String[]{USERID, timeStamp + ""}, null, null, null);
        int measureYm = 0;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            measureYm = cursor.getInt(cursor.getColumnIndex("measureym"));
        }
        cursor.close();
        boolean flagOne = database.delete(TABLENAME, "username = ? and timestamp = ?",
                new String[]{USERID, timeStamp + ""}) > 0;
        if (!hasRecordInYm(measureYm))
            ContinueMeasureYMDao.getInstance(mContext).deleteMeasureYM(measureYm);
        if (flagOne) {
            AbnormalListDao.getInstance(mContext).deleteOneAbnormalRecordByTime(timeStamp);
            ContinueHeartRateDao.getInstance(mContext).deleteOneContinueHeartRateRecord(timeStamp);
            ContinueBreathRateDao.getInstance(mContext).deleteOneRecordByTimeStamp(timeStamp);
        }
        return flagOne;
    }

    /**
     * 更新一次连续测量的数据信息
     *
     * @param timeStamp
     * @return
     */
    public boolean updateContinueMeasureRecordDataSize(long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        float totalSize = AbnormalListDao.getInstance(mContext).getAllDataSize(timeStamp);
        float submitedSize = AbnormalListDao.getInstance(mContext).getAllSubmitedDataSize(timeStamp);
        return updateContinueMeasureRecordDataSize(timeStamp, totalSize, submitedSize);
    }

    /**
     * 更新一次连续测量数据的文件的大小的信息
     *
     * @param timeStamp
     * @param totalSize
     * @param submitedSize
     * @return
     */
    public boolean updateContinueMeasureRecordDataSize(long timeStamp, float totalSize,
                                                       float submitedSize) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("datasize", totalSize);
        values.put("submitedsize", submitedSize);
        return database.update(TABLENAME, values, "username = ? and timestamp = ?",
                new String[]{USERID, timeStamp + ""}) != -1;
    }

    /**
     * 更新一次连续测量数据已经上传的文件的大小的信息
     *
     * @param timeStamp
     * @param addedSize
     * @return
     */
    public synchronized boolean updataContinueMeasureRecordDataSize(long timeStamp, float addedSize) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and timestamp = ?",
                new String[]{USERID, timeStamp + ""}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            float submitedSize = cursor.getFloat(cursor.getColumnIndex("submitedsize"));
            cursor.close();
            System.out.println("原submitedSize:" + submitedSize);
            submitedSize += addedSize;
            System.out.println("后submitedSize:" + submitedSize);
            ContentValues values = new ContentValues();
            values.put("submitedsize", submitedSize);
            boolean flag = database.update(TABLENAME, values, "username = ? and timestamp = ?",
                    new String[]{USERID, timeStamp + ""}) != -1;
            System.out.println("flag:" + flag);
            return flag;
        }
        return false;
    }

    /**
     * 获取所有的数据大小
     *
     * @return
     */
    public float getAllDataSize() {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        float allDataSize = 0;
        Cursor cursor = database.query(TABLENAME, null, "username = ? and measureover = ?",
                new String[]{USERID, ContinueMeasureRecord.MEASUREOVER + ""}, null, null, null);
        while (cursor.moveToNext()) {
            allDataSize += cursor.getFloat(cursor.getColumnIndex("datasize"));
        }
        cursor.close();
        return allDataSize;
    }

    /**
     * 获取所有已经提交完毕的数据大小
     *
     * @return
     */
    public float getAllSubimtedDataSize() {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        float allDataSize = 0;
        Cursor cursor = database.query(TABLENAME, null, "username = ? and measureover = ?",
                new String[]{USERID, ContinueMeasureRecord.MEASUREOVER + ""}, null, null, null);
        float submitedSize;
        float allSize;
        while (cursor.moveToNext()) {
            submitedSize = cursor.getFloat(cursor.getColumnIndex("submitedsize"));
            allSize = cursor.getFloat(cursor.getColumnIndex("datasize"));
            if (allSize <= submitedSize) {
                allDataSize += allSize;
            }
        }
        cursor.close();
        return allDataSize;
    }

    /**
     * 判断一次连续测量是否已经上传完毕
     *
     * @param timeStamp
     * @return
     */
    public boolean judgeConMeaSubimtedOver(long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and timestamp = ?",
                new String[]{USERID, timeStamp + ""}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            float submitedSize = cursor.getFloat(cursor.getColumnIndex("submitedsize"));
            float dataSize = cursor.getFloat(cursor.getColumnIndex("datasize"));
            cursor.close();
            return submitedSize >= dataSize;
        }
        return false;
    }

}
