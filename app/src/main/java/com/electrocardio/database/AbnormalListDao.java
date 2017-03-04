package com.electrocardio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.electrocardio.databasebean.AbnormalListBean;
import com.electrocardio.util.ConstantUtils;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/03/23.
 */
public class AbnormalListDao {

    private Context mContext;
    // 创建数据库的类
    private ECGDatabaseHelper helper;
    // 单利设计模式
    private static AbnormalListDao instance;

    /**
     * AbnormalListDao的构造函数
     *
     * @param context
     */
    private AbnormalListDao(Context context) {
        mContext = context;
        helper = new ECGDatabaseHelper(mContext, ConstantUtils.APPNAME, 1);
    }

    /**
     * 获取AbnormalListDao的实例
     *
     * @param context
     * @return
     */
    public synchronized static AbnormalListDao getInstance(Context context) {
        if (instance == null) {
            instance = new AbnormalListDao(context);
        }
        return instance;
    }

    // 存放AbnormalListDao的table的名称
    private static String TABLENAME = ConstantUtils.CONABNORMALLIST;
    // 用户名
    private String USERID = "";

    /**
     * 增加一条异常信息记录
     *
     * @param abnormalListBean
     */
    public boolean addOneAbnormalRecord(AbnormalListBean abnormalListBean) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", USERID);
        values.put("ymtimestamp", abnormalListBean.getYmTimeStamp());
        values.put("timestamp", abnormalListBean.getTimeStamp());
        values.put("timelabel", abnormalListBean.getTimeLabel());
        values.put("description", abnormalListBean.getDescription());
        values.put("submited", abnormalListBean.getSubmited());
        values.put("filepath", abnormalListBean.getDataFileSrc());
        values.put("type", abnormalListBean.getDETAILTYPE());
        values.put("fileSize", abnormalListBean.getFileSize());
        values.put("heartRate", abnormalListBean.getHeartRate());
        return database.insert(TABLENAME, null, values) > 0;
    }

    /**
     * 通过时间戳更新心率
     *
     * @param timeStamp
     * @param heartRate
     * @return
     */
    public boolean updateHeartRateByTimeStamp(long timeStamp, int heartRate) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("heartRate", heartRate);
        return database.update(TABLENAME, values, "username = ? and timestamp = ?", new String[]
                {USERID, timeStamp + ""}) > 0;
    }

    public int getHeartRateByTimeStamp(long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username =? and timeStamp = ?",
                new String[]{USERID, timeStamp + ""}, null, null, null);
        int rate = -1;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            rate = cursor.getInt(cursor.getColumnIndex("heartRate"));
        }
        cursor.close();
        return rate;
    }

    /**
     * 通过时间戳删除一次连续测量中的所有异常信息记录
     *
     * @param timeStamp
     * @return
     */
    public boolean deleteOneAbnormalRecordByTime(long timeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        return database.delete(TABLENAME, "username = ? and timestamp = ?", new String[]{USERID,
                timeStamp + ""}) > 0;
    }

    /**
     * 通过连续测量的时间戳删除整次测量的异常信息
     *
     * @param ymTimeStamp
     * @return
     */
    public boolean deleteOneDayAbnormalRecord(long ymTimeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        return database.delete(TABLENAME, "username = ? and ymtimestamp = ?", new String[]{USERID,
                ymTimeStamp + ""}) > 0;
    }

    /**
     * 获取一次连续测量中的所有数据的大小
     *
     * @param ymTimeStamp
     * @return
     */
    public float getAllDataSize(long ymTimeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and ymtimestamp = ?",
                new String[]{USERID, ymTimeStamp + ""}, null, null, null);
        float numberSize = 0;
        while (cursor.moveToNext()) {
            numberSize += cursor.getFloat(cursor.getColumnIndex("fileSize"));
        }
        cursor.close();
        return numberSize;
    }

    /**
     * 获取一次连续测量中的所有已经提交的数据的大小
     *
     * @param ymTimeStamp
     * @return
     */
    public float getAllSubmitedDataSize(long ymTimeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and ymtimestamp = ? and submited = ?",
                new String[]{USERID, ymTimeStamp + "", AbnormalListBean.SUBMITED + ""}, null, null, null);
        float numberSize = 0;
        while (cursor.moveToNext()) {
            numberSize += cursor.getFloat(cursor.getColumnIndex("fileSize"));
        }
        cursor.close();
        return numberSize;
    }

    /**
     * 更新异常数据提交状态
     *
     * @param timeStamp
     * @param state
     * @return
     */
    public boolean updateAbnormalRecordSubmitState(long ymtimestamp, long timeStamp, int state) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("submited", state);
        return database.update(TABLENAME, values, "username = ? and ymtimestamp = ? and " +
                "timestamp = ?", new String[]{USERID, ymtimestamp + "", timeStamp + ""}) > 0;
    }

    /**
     * 更新异常数据大小
     *
     * @param ymtimestamp
     * @param timeStamp
     * @param datasize
     * @return
     */
    public boolean updateAbnormalRecordDataSize(long ymtimestamp, long timeStamp, float datasize) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fileSize", datasize);
        return database.update(TABLENAME, values, "username = ? and ymtimestamp = ? and " +
                "timestamp = ?", new String[]{USERID, ymtimestamp + "", timeStamp + ""}) > 0;
    }

    /**
     * 更新异常数据大小
     *
     * @param ymtimestamp
     * @param timeStamp
     * @param src
     * @return
     */
    public boolean updateAbnormalRecordFileSrc(long ymtimestamp, long timeStamp, String src) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("filepath", src);
        return database.update(TABLENAME, values, "username = ? and ymtimestamp = ? and " +
                "timestamp = ?", new String[]{USERID, ymtimestamp + "", timeStamp + ""}) > 0;
    }

    /**
     * 获取一次连续测量中所有的异常信息
     *
     * @param ymTimeStamp
     * @return
     */
    public ArrayList<AbnormalListBean> getAllAbnormalListBean(long ymTimeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        AbnormalListBean abnormalListBean;
        ArrayList<AbnormalListBean> array = new ArrayList<>();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and ymtimestamp = ?",
                new String[]{USERID, ymTimeStamp + ""}, null, null, "timestamp ASC");
        while (cursor.moveToNext()) {
            abnormalListBean = new AbnormalListBean();
            abnormalListBean.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
            abnormalListBean.setTimeLabel(cursor.getString(cursor.getColumnIndex("timelabel")));
            abnormalListBean.setTYPE(cursor.getInt(cursor.getColumnIndex("type")));
            abnormalListBean.setDataFileSrc(cursor.getString(cursor.getColumnIndex("filepath")));
            abnormalListBean.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            abnormalListBean.setYmTimeStamp(cursor.getLong(cursor.getColumnIndex("ymtimestamp")));
            abnormalListBean.setFileSize(cursor.getFloat(cursor.getColumnIndex("fileSize")));
            abnormalListBean.setSubmited(cursor.getInt(cursor.getColumnIndex("submited")));
            abnormalListBean.setHeartRate(cursor.getInt(cursor.getColumnIndex("heartRate")));
            array.add(abnormalListBean);
        }
        cursor.close();
        return array;
    }

    /**
     * 获取一次连续测量中所有的已经提交的异常信息
     *
     * @param ymTimeStamp
     * @return
     */
    public ArrayList<AbnormalListBean> getAllSubmitedAbnormalListBean(long ymTimeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        AbnormalListBean abnormalListBean = null;
        ArrayList<AbnormalListBean> array = new ArrayList<>();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and ymtimestamp = ? and" +
                " submited = ?", new String[]{USERID, ymTimeStamp + "", AbnormalListBean.SUBMITED
                + ""}, null, null, "timestamp ASC");
        while (cursor.moveToNext()) {
            abnormalListBean = new AbnormalListBean();
            abnormalListBean.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
            abnormalListBean.setTimeLabel(cursor.getString(cursor.getColumnIndex("timelabel")));
            abnormalListBean.setTYPE(cursor.getInt(cursor.getColumnIndex("type")));
            abnormalListBean.setDataFileSrc(cursor.getString(cursor.getColumnIndex("filepath")));
            abnormalListBean.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            abnormalListBean.setYmTimeStamp(cursor.getLong(cursor.getColumnIndex("ymtimestamp")));
            abnormalListBean.setFileSize(cursor.getFloat(cursor.getColumnIndex("fileSize")));
            abnormalListBean.setHeartRate(cursor.getInt(cursor.getColumnIndex("heartRate")));
            abnormalListBean.setSubmited(cursor.getInt(cursor.getColumnIndex("submited")));
            array.add(abnormalListBean);
        }
        cursor.close();
        return array;
    }

    /**
     * 获取一次连续测量中所有的未提交的异常信息
     *
     * @param ymTimeStamp
     * @return
     */
    public ArrayList<AbnormalListBean> getAllNotSubmitedAbnormalListBean(long ymTimeStamp) {
        USERID = UserDao.getInstance(mContext).getUserID();
        AbnormalListBean abnormalListBean = null;
        ArrayList<AbnormalListBean> array = new ArrayList<>();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and ymtimestamp = ? and" +
                        " submited = ?", new String[]{USERID, ymTimeStamp + "", AbnormalListBean.NOTSUBMITED
                        + ""}, null, null, "timestamp ASC"
        );
        while (cursor.moveToNext()) {
            abnormalListBean = new AbnormalListBean();
            abnormalListBean.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
            abnormalListBean.setTimeLabel(cursor.getString(cursor.getColumnIndex("timelabel")));
            abnormalListBean.setTYPE(cursor.getInt(cursor.getColumnIndex("type")));
            abnormalListBean.setDataFileSrc(cursor.getString(cursor.getColumnIndex("filepath")));
            abnormalListBean.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            abnormalListBean.setYmTimeStamp(cursor.getLong(cursor.getColumnIndex("ymtimestamp")));
            abnormalListBean.setFileSize(cursor.getFloat(cursor.getColumnIndex("fileSize")));
            abnormalListBean.setHeartRate(cursor.getInt(cursor.getColumnIndex("heartRate")));
            abnormalListBean.setSubmited(cursor.getInt(cursor.getColumnIndex("submited")));
            array.add(abnormalListBean);
        }
        cursor.close();
        return array;
    }

}
