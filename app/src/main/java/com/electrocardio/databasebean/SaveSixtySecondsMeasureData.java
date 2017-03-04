package com.electrocardio.databasebean;

import android.content.Context;
import android.os.Environment;

import com.electrocardio.database.SixtySecondsBreathRateDao;
import com.electrocardio.database.SixtySecondsHeartRateDao;
import com.electrocardio.database.SixtySecondsRecordDao;
import com.electrocardio.database.UserDao;
import com.electrocardio.util.ThreadUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by ZhangBo on 2016/03/22.保存60测量的数据
 */
public class SaveSixtySecondsMeasureData {

    private Context mContext;// 上下文变量

    private long mStartTimeMillis;// 开始时的毫秒数
    private long mEndTimeMillis;// 停止时的毫秒数

    private int averageHeartRate;// 平均心率
    private int heartRateCount;// 心率个数
    private int fastestHeartRate;// 最快心率
    private int slowestHeartRate;// 最慢心率
    private int fastHeartRateCount;// 过快心率次数
    private int norFastHeartRateCount;// 稍快心率次数
    private int normalHeartRateCount;// 正常心率次数
    private int norSlowHeartRateCount;// 稍慢心率次数
    private int slowHeartRateCount;// 过慢心率次数

    private int averageBreathRate;// 平均呼吸率
    private int breathRateCount;// 呼吸率个数
    private int fastBreathRateCount;// 过快呼吸率次数
    private int normalBreathRateCount;// 正常呼吸率次数
    private int slowBreathRateCount;// 过慢呼吸率次数

    private HeartRateStandard heartRateStandard;// 判断心率状况的类
    private BreathRateStandard breathRateStandard;// 判断呼吸率状况的类
    private ArrayList<Float> ecgArray;// 存放ECG数据的集合

    private SaveOverListener mSaveOverListener;// 存储完毕的事件监听者

    public SaveSixtySecondsMeasureData(Context context) {
        mContext = context;
        heartRateStandard = new HeartRateStandard();
        breathRateStandard = new BreathRateStandard();
        ecgArray = new ArrayList<>();

    }

    /**
     * 开始保存数据
     */
    public void saveStart() {
        averageHeartRate = 0;// 平均心率
        heartRateCount = 0;// 心率个数
        fastestHeartRate = 0;// 最快心率
        slowestHeartRate = 255;// 最慢心率
        fastHeartRateCount = 0;// 过快心率次数
        norFastHeartRateCount = 0;// 稍快心率次数
        normalHeartRateCount = 0;// 正常心率次数
        norSlowHeartRateCount = 0;// 稍慢心率次数
        slowHeartRateCount = 0;// 过慢心率次数

        averageBreathRate = 0;// 平均呼吸率
        breathRateCount = 0;// 呼吸率个数
        fastBreathRateCount = 0;// 过快呼吸率次数
        normalBreathRateCount = 0;// 正常呼吸率次数
        slowBreathRateCount = 0;// 过慢呼吸率次数
        mStartTimeMillis = System.currentTimeMillis();
        ecgArray.clear();
    }

    /**
     * 增加一次心率
     *
     * @param heartRate
     */
    public void addHeartRate(int heartRate) {
        if (heartRate < 0)
            return;
        averageHeartRate += heartRate;
        heartRateCount++;
        switch (heartRateStandard.judgeState(heartRate)) {
            case HeartRateStandard.FAST:
                fastHeartRateCount++;
                break;
            case HeartRateStandard.FASTNORMAL:
                norFastHeartRateCount++;
                break;
            case HeartRateStandard.NORMAL:
                normalHeartRateCount++;
                break;
            case HeartRateStandard.SLOWNORMAL:
                norSlowHeartRateCount++;
                break;
            case HeartRateStandard.SLOW:
                slowHeartRateCount++;
                break;
        }
        slowestHeartRate = slowestHeartRate > heartRate ? heartRate : slowestHeartRate;
        fastestHeartRate = fastestHeartRate < heartRate ? heartRate : fastestHeartRate;
    }

    /**
     * 增加一次呼吸率
     *
     * @param breathRate
     */
    public void addBreathRate(int breathRate) {
        averageBreathRate += breathRate;
        breathRateCount++;
        switch (breathRateStandard.judgeState(breathRate)) {
            case BreathRateStandard.FAST:
                fastBreathRateCount++;
                break;
            case BreathRateStandard.NORMAL:
                normalBreathRateCount++;
                break;
            case BreathRateStandard.SLOW:
                slowBreathRateCount++;
                break;
        }
    }

    /**
     * 添加ECG数据
     *
     * @param ecgData
     */
    public void addECGData(float[] ecgData) {
        for (float data : ecgData)
            addECGData(data);
    }

    /**
     * 添加ECG数据
     *
     * @param ecgData
     */
    public void addECGData(float ecgData) {
        ecgArray.add(ecgData);
    }

    /**
     * 保存停止
     */
    public void saveStop() {
        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                save();
            }
        });
    }

    /**
     * 保存数据
     */
    private synchronized void save() {
        mEndTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mStartTimeMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int startHour = calendar.get(Calendar.HOUR_OF_DAY);
        int startMinte = calendar.get(Calendar.MINUTE);
        int startSecond = calendar.get(Calendar.SECOND);
        calendar.setTimeInMillis(mEndTimeMillis);
        int endHour = calendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = calendar.get(Calendar.MINUTE);
        int endSecond = calendar.get(Calendar.SECOND);

        // 计算各种比例
        computeEveryRate();

        // 存储文件的路径
        String saveSrc = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() + "/" + UserDao.getInstance(mContext).getUserID() + "/sixty/"
                + year + "/" + month + "/" + day;

        // 保存60秒测量的各项信息到数据库中
        saveToDataBase(year, month, day, startHour, startMinte, startSecond, endHour, endMinute,
                endSecond, saveSrc);

        // 将60秒测量的ECG数据存储到文件中
        saveECGDataToFile(saveSrc);

        if (mSaveOverListener != null)
            mSaveOverListener.saveOver();
    }

    /**
     * 将60秒测量的ECG数据存储到文件中
     *
     * @param saveSrc
     */
    private void saveECGDataToFile(String saveSrc) {
        File file = new File(saveSrc);
        if (!file.exists())
            file.mkdirs();
        File ecgFile = new File(saveSrc, mStartTimeMillis + ".txt");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(ecgFile);
            for (float data : ecgArray) {
                fileOutputStream.write((data + "\t").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ecgArray.clear();
    }

    /**
     * 获取存储心率的bean
     *
     * @return
     */
    public HeartRateStandard getHeartRateStandard() {
        return heartRateStandard;
    }

    /**
     * 保存60秒测量的各项信息到数据库中
     *
     * @param year
     * @param month
     * @param day
     * @param startHour
     * @param startMinte
     * @param startSecond
     * @param endHour
     * @param endMinute
     * @param endSecond
     * @param saveSrc
     */
    private void saveToDataBase(int year, int month, int day, int startHour, int startMinte,
                                int startSecond, int endHour, int endMinute, int endSecond,
                                String saveSrc) {
        // 60秒测量记录概况
        SixtySecondsRecord sixtySecondsRecord = new SixtySecondsRecord();
        sixtySecondsRecord.setTimeStamp(mStartTimeMillis);
        sixtySecondsRecord.setStartTime(startHour, startMinte, startSecond);
        sixtySecondsRecord.setEndTime(endHour, endMinute, endSecond);
        sixtySecondsRecord.setAverageHeartRate(averageHeartRate);
        sixtySecondsRecord.setHealthState("亚健康");
        sixtySecondsRecord.setHealthIndex(new Random().nextInt(30) + 60);
        sixtySecondsRecord.setDataFileSrc(saveSrc + "/" + mStartTimeMillis + ".txt");

        // 60秒测量记录的心率情况
        SixtySecondsHeartRate sixtySecondsHeartRate = new SixtySecondsHeartRate();
        sixtySecondsHeartRate.setTimeStamp(mStartTimeMillis);
        sixtySecondsHeartRate.setRate(fastHeartRateCount, norFastHeartRateCount, normalHeartRateCount,
                norSlowHeartRateCount, slowHeartRateCount);
        sixtySecondsHeartRate.setFastestHeartRate(fastestHeartRate);
        sixtySecondsHeartRate.setAverageHeartRate(averageHeartRate);
        sixtySecondsHeartRate.setSlowestHeartRate(slowestHeartRate);

        // 60秒测量记录的呼吸率情况
        SixtySecondsBreathRate sixtySecondsBreathRate = new SixtySecondsBreathRate();
        sixtySecondsBreathRate.setTimeStamp(mStartTimeMillis);
        sixtySecondsBreathRate.setAverageBreathRate(averageBreathRate);
        sixtySecondsBreathRate.setRate(fastBreathRateCount, normalBreathRateCount, slowBreathRateCount);

        // 将sixtySecondsRecord、sixtySecondsBreathRate、sixtySecondsHeartRate存储到数据库中
        SixtySecondsRecordDao.getInstance(mContext).addSixtySecondsRecord(year, month, day, sixtySecondsRecord);
        SixtySecondsBreathRateDao.getInstance(mContext).addOneSixtySecondsBreathRate(sixtySecondsBreathRate);
        SixtySecondsHeartRateDao.getInstance(mContext).addOneSixtySecondsHeartRate(sixtySecondsHeartRate);
    }

    /**
     * 计算各种比例
     */
    private void computeEveryRate() {
        if (heartRateCount != 0) {
            averageHeartRate /= heartRateCount;// 计算平均心率
            fastHeartRateCount = fastHeartRateCount * 100 / heartRateCount;// 过快心率比例
            norFastHeartRateCount = (norFastHeartRateCount * 100 + heartRateCount / 2) / heartRateCount;// 稍快心率比例
            normalHeartRateCount = normalHeartRateCount * 100 / heartRateCount;// 正常心率比例
            norSlowHeartRateCount = norSlowHeartRateCount * 100 / heartRateCount;// 稍慢心率比例
            slowHeartRateCount = 100 - fastHeartRateCount - norFastHeartRateCount - normalHeartRateCount
                    - norSlowHeartRateCount;// 过慢心率比例
        }

        if (breathRateCount != 0) {
            averageBreathRate /= breathRateCount;// 计算平均呼吸率
            fastBreathRateCount = fastBreathRateCount * 100 / breathRateCount;// 计算过快呼吸率比例
            normalBreathRateCount = (normalBreathRateCount * 100 + breathRateCount / 2) /
                    breathRateCount;// 计算正常呼吸率比例
            slowBreathRateCount = 100 - fastBreathRateCount - normalBreathRateCount;
        }
    }

    /**
     * 设置存储完毕的事件监听器
     *
     * @param saveOverListener
     */
    public void setSaveOverListener(SaveOverListener saveOverListener) {
        mSaveOverListener = saveOverListener;
    }

    public interface SaveOverListener {
        public void saveOver();
    }
}
