package com.electrocardio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.electrocardio.Bean.UploadDataState;
import com.electrocardio.util.ConstantUtils;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/05/10.操作存储要上传的连续测量的记录的表
 */
public class UploadMeasureRecordDao {

    private Context mContext;
    // 创建数据库的类
    private ECGDatabaseHelper helper;
    // 单利设计模式
    private static UploadMeasureRecordDao instance;

    private UploadMeasureRecordDao(Context context) {
        mContext = context;
        helper = new ECGDatabaseHelper(mContext, ConstantUtils.APPNAME, 1);
    }

    /**
     * 获取UploadMeasureRecordDao的实例
     *
     * @param context
     * @return
     */
    public synchronized static UploadMeasureRecordDao getInstance(Context context) {
        if (instance == null) {
            instance = new UploadMeasureRecordDao(context);
        }
        return instance;
    }

    // 存放SixtySecondsYMDDao的table的名称
    private static String TABLENAME = ConstantUtils.UPLOADCONMEASURE;
    // 用户名
    private String USERID = "";

    /**
     * 添加一条记录
     *
     * @param timeStamp
     */
    public boolean addOneConMeasureRecord(long timeStamp, int state) {
        USERID = UserDao.getInstance(mContext).getUserID();
        if (judgeHasConMeasureRecord(timeStamp))
            return true;
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("timestamp", timeStamp);
        contentValues.put("username", USERID);
        contentValues.put("uploadState", state);
        return database.insert(TABLENAME, null, contentValues) != -1l;
    }

    /**
     * 判断是否有某条记录
     *
     * @param timeStamp
     * @return
     */
    public boolean judgeHasConMeasureRecord(long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and timestamp = ?",
                new String[]{USERID, timeStamp + ""}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        return false;
    }

    /**
     * 删除所有数据
     *
     * @return
     */
    public boolean deleteAllData() {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase dataBase = helper.getWritableDatabase();
        return dataBase.delete(TABLENAME, "username = ?", new String[]{USERID}) != -1;
    }

    /**
     * 获取某条记录的状态
     *
     * @param timeStamp
     * @return
     */
    public int getOnRecordState(long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and timestamp = ?",
                new String[]{USERID, timeStamp + ""}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int state = cursor.getInt(cursor.getColumnIndex("uploadState"));
            cursor.close();
            return state;
        }
        return -1;
    }

    /**
     * 判断是否有正在上传和等待上传的数据
     *
     * @return
     */
    public boolean hasUploadingAndWaitRecord() {
        return hasUploadingRecord() || hasWaitingRecord();
    }

    /**
     * 判断是否有正在上传的数据
     *
     * @return
     */
    public boolean hasUploadingRecord() {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and uploadState = ?",
                new String[]{USERID, UploadDataState.UPLOADING.getState() + ""}, null, null, null);
        boolean flag = cursor.getCount() > 0;
        cursor.close();
        return flag;
    }

    /**
     * 判断是否有正在上传的数据
     *
     * @return
     */
    public boolean hasWaitingRecord() {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and uploadState = ?",
                new String[]{USERID, UploadDataState.WAITUPLOAD.getState() + ""}, null, null, null);
        boolean flag = cursor.getCount() > 0;
        cursor.close();
        return flag;
    }

    /**
     * 更新一条记录
     *
     * @param timeStamp
     * @param state
     * @return
     */
    public boolean updateOneRecordState(long timeStamp, int state) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uploadState", state);
        return database.update(TABLENAME, contentValues, "username = ? and timestamp = ?",
                new String[]{USERID, timeStamp + ""}) > 0;
    }

    /**
     * 获取下一条等待上传的记录
     *
     * @return
     */
    public long getNextWaitRecord() {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        long record = -1l;
        Cursor cursor = database.query(TABLENAME, null, "username = ? and uploadState = ?",
                new String[]{USERID, UploadDataState.WAITUPLOAD.getState() + ""}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            record = cursor.getLong(cursor.getColumnIndex("timestamp"));
            cursor.close();
        }
        return record;
    }

    /**
     * 获取所有的上传记录
     *
     * @return
     */
    public ArrayList<Long> getAllUploadRecord() {
        USERID = UserDao.getInstance(mContext).getUserID();
        ArrayList<Long> recordArray = new ArrayList<>();
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ?", new String[]{USERID}, null, null, null);
        while (cursor.moveToNext()) {
            recordArray.add(cursor.getLong(cursor.getColumnIndex("timestamp")));
        }
        cursor.close();
        return recordArray;
    }

    /**
     * 获取所有的处于上传状态的记录
     *
     * @return
     */
    public ArrayList<Long> getAllUploadingRecord() {
        USERID = UserDao.getInstance(mContext).getUserID();
        ArrayList<Long> recordArray = new ArrayList<>();
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and uploadState = ?",
                new String[]{USERID, UploadDataState.UPLOADING.getState() + ""}, null, null, null);
        while (cursor.moveToNext()) {
            recordArray.add(cursor.getLong(cursor.getColumnIndex("timestamp")));
        }
        cursor.close();
        return recordArray;
    }

    /**
     * 判断一条连续测量记录是否具有上传资格
     *
     * @param timeStamp
     * @return
     */
    public boolean judgeHasAuthToUpload(long timeStamp) {
        int state = -1;
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and timestamp = ?",
                new String[]{USERID, timeStamp + ""}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            state = cursor.getInt(cursor.getColumnIndex("uploadState"));
            cursor.close();
        }
        return state == UploadDataState.UPLOADING.getState();
    }

    /**
     * 删除一条记录
     *
     * @param timeStamp
     * @return
     */
    public boolean deleteOneRecord(long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        return database.delete(TABLENAME, "timestamp = ?", new String[]{timeStamp + ""}) > 0;
    }

    /**
     * 获取正在上传的记录的条数
     *
     * @return
     */
    public int getUploadingCount() {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and uploadState = ?", new String[]
                {USERID, UploadDataState.UPLOADING.getState() + ""}, null, null, null);
        int num = cursor.getCount();
        cursor.close();
        return num;
    }
}
