package com.electrocardio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.electrocardio.util.ConstantUtils;

/**
 * Created by ZhangBo on 2016/03/24.
 */
public class ContinueMeasureYMDao {

    private Context mContext;
    // 创建数据库的类
    private ECGDatabaseHelper helper;
    // 单例设计模式
    private static ContinueMeasureYMDao instance;

    /**
     * ContinueMeasureYMDao的构造函数
     *
     * @param context
     */
    private ContinueMeasureYMDao(Context context) {
        mContext = context;
        helper = new ECGDatabaseHelper(mContext, ConstantUtils.APPNAME, 1);
    }

    /**
     * 获取ContinueMeasureYMDao的实例
     *
     * @param context
     * @return
     */
    public synchronized static ContinueMeasureYMDao getInstance(Context context) {
        if (instance == null) {
            instance = new ContinueMeasureYMDao(context);
        }
        return instance;
    }

    // 存放ContinueMeasureYMDao的table的名称
    private static String TABLENAME = ConstantUtils.CONMEASUREYM;
    // 用户名
    private String USERID = "";

    /**
     * 添加一条测量年月记录
     *
     * @param year
     * @param month
     * @return
     */
    public boolean addContinueMeasureYM(int year, int month) {
        return addContinueMeasureYM(((year - 1970) << 8) + month);
    }

    /**
     * 根据ym添加一条记录
     *
     * @param ym
     * @return
     */
    public boolean addContinueMeasureYM(int ym) {
        USERID = UserDao.getInstance(mContext).getUserID();
        if (hasSameMeasureYM(ym))
            return true;
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", USERID);
        values.put("measureYM", ym);
        return database.insert(TABLENAME, null, values) > 0l;
    }

    /**
     * 判断是否有相同的年月
     *
     * @param year
     * @param month
     * @return
     */
    public boolean hasSameMeasureYM(int year, int month) {
        return hasSameMeasureYM(((year - 1970) << 8) + month);
    }

    /**
     * 根据ym判断时候有相同的年月
     *
     * @param ym
     * @return
     */
    public boolean hasSameMeasureYM(int ym) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(TABLENAME, null, "username = ? and measureYM = ?",
                new String[]{USERID, ym + ""}, null, null, null);
        int num = cursor.getCount();
        cursor.close();
        return num > 0;
    }

    /**
     * 根据年月删除一条数据
     *
     * @param year
     * @param month
     * @return
     */
    public boolean deleteMeasure(int year, int month) {
        return deleteMeasureYM(((year - 1970) << 8) + month);
    }

    /**
     * 根据ym删除一条数据
     *
     * @param ym
     * @return
     */
    public boolean deleteMeasureYM(int ym) {
        USERID = UserDao.getInstance(mContext).getUserID();
        SQLiteDatabase database = helper.getWritableDatabase();
        return database.delete(TABLENAME, "username = ? and measureYM = ?", new String[]{USERID,
                ym + ""}) > 0;
    }

}
