package com.electrocardio.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.electrocardio.util.ConstantUtils;

/**
 * Created by ZhangBo on 2016/03/21.
 */
public class ECGDatabaseHelper extends SQLiteOpenHelper {

    public ECGDatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建存储用户的表
        db.execSQL("create table " + ConstantUtils.USERTABLE + "(id integer primary key " +
                "autoincrement,userId varchar(20),username varchar(20),phonenumber varchar(20),sex " +
                "integer(4),birthday integer(4),height varchar(20),weight varchar(20),password " +
                "varchar(20),endTime varchar(20),deviceID varchar(20),logoSrc varchar(20),token varchar(64));");
        // 创建存储单次测量年月日的表
        db.execSQL("create table " + ConstantUtils.SIXTYSECONDSYMD + "(id integer primary key " +
                "autoincrement,username varchar(20),yearAndMonth integer(4),day integer(4));");
        // 创建存储单次测量记录的表
        db.execSQL("create table " + ConstantUtils.SIXTYSECONDSRECORD + "(id integer primary key " +
                "autoincrement,username varchar(20),ymd integer(4),timeStamp long(8),startTime " +
                "integer(4),endTime integer(4),averHeartRate integer(4),healthState varchar(20)," +
                "healthIndex integer(4),dataFileSrc varchar(100));");
        // 创建存储单次测量心率的表
        db.execSQL("create table " + ConstantUtils.SIXTYSECONDSHEARTRATE + "(id integer primary key " +
                "autoincrement,username varchar(20),timeStamp long(8),heartRate integer(4),average " +
                "integer(4),fast integer(4),slow integer(4));");
        // 创建存储单次测量呼吸率的表
        db.execSQL("create table " + ConstantUtils.SIXTYSECONDSBREATHRATE + "(id integer primary key " +
                "autoincrement,username varchar(20),timeStamp long(8),breathRate integer(4),average " +
                "integer(4));");
        // 创建存储连续测量异常信息的表
        db.execSQL("create table " + ConstantUtils.CONABNORMALLIST + "(id integer primary key " +
                "autoincrement,username varchar(20),ymtimestamp long(8),timestamp long(8)," +
                "timelabel varchar(30),description varchar(30),heartRate integer(4),submited integer(4)," +
                "filepath varchar(70),type integer(4),fileSize float(4));");
        // 创建存储连续测量心率的表
        db.execSQL("create table " + ConstantUtils.CONHEARTRATE + "(id integer primary key " +
                "autoincrement,username varchar(20),timestamp long(8),rate integer(4),fastrate " +
                "integer(4),averagerate integer(4),slowrate integer(4),beat integer(4),datafilesrc " +
                "varchar(70));");
        // 创建存储连续测量呼吸率的表
        db.execSQL("create table " + ConstantUtils.CONBREATHRATE + "(id integer primary key " +
                "autoincrement,username varchar(20),timestamp long(8),rate integer(4)," +
                "averagerate integer(4),datafilesrc varchar(70));");
        // 创建存储连续测量记录的表
        db.execSQL("create table " + ConstantUtils.CONMEASURERECORD + "(id integer primary key " +
                "autoincrement,username varchar(20),measureym integer(4),timestamp long(8)," +
                "startday integer(4),starttime integer(4),endmd integer(4),endtime integer(4)," +
                "timelength long(8),datasize float(4),averagerate integer(4),normalrate integer(4)," +
                "measureover integer(4),submitedsize float(4));");
        // 创建存储测量年月的表
        db.execSQL("create table " + ConstantUtils.CONMEASUREYM + "(id integer primary key" +
                " autoincrement,username varchar(20),measureYM integer(4));");
        // 创建存储将要上传的连续测量的记录的表
        db.execSQL("create table " + ConstantUtils.UPLOADCONMEASURE + "(id integer primary key " +
                "autoincrement,username varchar(20),timestamp long(8),uploadState integer(4));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
