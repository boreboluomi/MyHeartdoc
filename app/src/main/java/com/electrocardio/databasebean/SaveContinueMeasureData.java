package com.electrocardio.databasebean;

import android.app.Activity;
import android.os.Environment;

import com.electrocardio.base.BaseApplication;
import com.electrocardio.database.ContinueBreathRateDao;
import com.electrocardio.database.ContinueHeartRateDao;
import com.electrocardio.database.ContinueMeasureRecordDao;
import com.electrocardio.database.UserDao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ZhangBo on 2016/03/25.
 */
public abstract class SaveContinueMeasureData {

    private WeakReference<Activity> mActivity;
    private ContinueMeasureRecord continueMeasureRecord;// 存储连续测量的javaBean的类
    private long timeStamp = -1;// 时间戳
    private String src = "";// 每次连续测量信息数据存储文件根目录
    private ArrayList<Float> cache;// 缓存数据
    private ArrayList<Integer> heartRateCache;// 心率缓存数据
    private int heartRateCapacity = 20;// 心率缓存容量
    private int capacity = 1625;// 缓存容量
    private SaveAbnormalData saveAbnormalData;// 将异常信息分别手机并保存的类
    private SaveContinueHeartRate saveContinueHeartRate;// 存储连续测量心率的类
    private SaveContinueBreathRate saveContinueBreathRate;// 存储连续测量的呼吸率的类

    public SaveContinueMeasureData(WeakReference<Activity> activity) {
        mActivity = activity;
    }

    /**
     * 保存连续测量数据初始化
     */
    public void saveStart() {
        Activity activity = mActivity.get();
        if (activity == null)
            return;
        continueMeasureRecord = new ContinueMeasureRecord();
        timeStamp = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int measureYm = ((calendar.get(Calendar.YEAR) - 1970) << 8) + calendar.get(Calendar.MONTH) + 1;
        int startTime = (calendar.get(Calendar.HOUR_OF_DAY) << 16) + (calendar.get(Calendar.MINUTE)
                << 8) + calendar.get(Calendar.SECOND);
        src = mActivity.get().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/"
                + UserDao.getInstance(activity).getUserID() + "/continue/" + calendar.get(Calendar.YEAR)
                + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + day + "/" + timeStamp;
        continueMeasureRecord.setTimeStamp(timeStamp);
        continueMeasureRecord.setStartDay(day);
        continueMeasureRecord.setMeasureYM(measureYm);
        continueMeasureRecord.setStartTime(startTime);
        continueMeasureRecord.setMeasureOver(ContinueMeasureRecord.MEASURENOTOVER);
        ContinueMeasureRecordDao.getInstance(activity).addOneContinueMeasureRecord(continueMeasureRecord);
        if (saveAbnormalData != null) {
            saveAbnormalData.close();
        }
        saveAbnormalData = new SaveAbnormalData(mActivity, timeStamp, src) {
            @Override
            public void saveComplete(AbnormalDataCacheBean abnormalDataCacheBean) {
                oneAbnormalInfoSaved(abnormalDataCacheBean);
            }
        };
        if (cache != null)
            cache.clear();
        else
            cache = new ArrayList<>();
        if (heartRateCache != null)
            heartRateCache.clear();
        else
            heartRateCache = new ArrayList<>();
        saveContinueHeartRate = new SaveContinueHeartRate(src, src + "/heartRate.txt", timeStamp);
        saveContinueBreathRate = new SaveContinueBreathRate(src, src + "/breathRate.txt", timeStamp);
    }

    /**
     * 接收到异常信号
     *
     * @param sign
     */
    public void receiveAbnormalSign(int sign) {
        if (saveAbnormalData != null) {
            ArrayList<Float> array = new ArrayList<>();
            ArrayList<Integer> heartArr = new ArrayList<>();
            int size = AbnormalSignInfor.getCacheLength(sign);
            int heartRateSize = size / AbnormalSignInfor.UNIT_LENGTH;
            // 获取缓存的心电波形
            if (cache.size() <= size) {
                for (float data : cache)
                    array.add(data);
            } else {
                for (int i = (cache.size() - size); i < cache.size(); i++)
                    array.add(cache.get(i));
            }
            // 获取缓存的心率数据
            if (heartRateCache.size() <= heartRateSize) {
                for (int data : heartRateCache)
                    heartArr.add(data);
            } else {
                for (int i = (heartRateCache.size() - heartRateSize); i < heartRateCache.size(); i++)
                    heartArr.add(heartRateCache.get(i));
            }
            saveAbnormalData.setAbnormalSignWithData(sign, array, heartArr);
        }
    }

    /**
     * 添加ecg数据
     *
     * @param ecgData
     */
    public void addECGWaveData(float[] ecgData) {
        for (float data : ecgData)
            addECGWaveData(data);
    }

    /**
     * 添加ecg数据
     *
     * @param ecgData
     */
    public void addECGWaveData(float ecgData) {
        cache.add(ecgData);
        if (cache.size() > capacity)
            cache.remove(0);
        saveAbnormalData.addData(ecgData);
    }

    /**
     * 接收心率数据
     *
     * @param heartRate
     */
    public void addHeartRate(int heartRate) {
        if (saveContinueHeartRate != null)
            saveContinueHeartRate.addHeartRate(heartRate);
        heartRateCache.add(heartRate);
        if (heartRateCache.size() > heartRateCapacity)
            heartRateCache.remove(0);
        saveAbnormalData.addHeartRateData(heartRate);
    }

    /**
     * 接收呼吸率数据
     *
     * @param breathRate
     */
    public void addBreathRate(int breathRate) {
        if (saveContinueBreathRate != null)
            saveContinueBreathRate.addBreathRate(breathRate);
    }

    /**
     * 接收心率的所有结果数据
     *
     * @param nNum
     * @param nHRAvg
     * @param nHRMax
     * @param nHRMin
     * @param arydbHRPercent
     */
    public void addHeartRateResult(int nNum, int nHRAvg, int nHRMax, int nHRMin, int[] arydbHRPercent) {
        saveContinueHeartRate.getContinueHeartRate().setBeatCount(nNum);
        saveContinueHeartRate.getContinueHeartRate().setAverageHeartRate(nHRAvg);
        saveContinueHeartRate.getContinueHeartRate().setFastestHeartRate(nHRMax);
        saveContinueHeartRate.getContinueHeartRate().setSlowestHeartRate(nHRMin);
        saveContinueHeartRate.getContinueHeartRate().setRate(arydbHRPercent[0], arydbHRPercent[1],
                arydbHRPercent[2], arydbHRPercent[3], arydbHRPercent[4]);
        continueMeasureRecord.setRate(arydbHRPercent[2]);
        continueMeasureRecord.setAverageRate(nHRAvg);
        ContinueHeartRateDao.getInstance(BaseApplication.getInstance()).
                updateOneContinueHeartRateRecord(saveContinueHeartRate.getContinueHeartRate());
    }

    /**
     * 接收呼吸率的所有结果数据
     *
     * @param nBRAvg
     * @param arydbBRPercent
     */
    public void addBreathRateResult(int nBRAvg, int[] arydbBRPercent) {
        saveContinueBreathRate.getContinueBreathRate().setAverageBreathRate(nBRAvg);
        saveContinueBreathRate.getContinueBreathRate().setRate(arydbBRPercent[0], arydbBRPercent[1],
                arydbBRPercent[2]);
        ContinueBreathRateDao.getInstance(BaseApplication.getInstance()).
                updateOneContinueBreathRateRecord(saveContinueBreathRate.getContinueBreathRate());
    }

    /**
     * 一条异常信息数据存储完毕
     *
     * @param abnormalDataCacheBean
     */
    public abstract void oneAbnormalInfoSaved(AbnormalDataCacheBean abnormalDataCacheBean);

    /**
     * 结束保存
     */
    public void saveStop() {
        if (saveAbnormalData != null) {
            saveAbnormalData.close();
            saveAbnormalData = null;
        }
        continueMeasureRecord.setMeasureOver(ContinueMeasureRecord.MEASUREOVER);
        continueMeasureRecord.setEndTimeStamp(System.currentTimeMillis());
        ContinueMeasureRecordDao.getInstance(BaseApplication.getInstance()).
                updateContinueRecordNoSize(continueMeasureRecord);
        saveContinueHeartRate.measureEnd();
        saveContinueBreathRate.measureEnd();
    }

}
