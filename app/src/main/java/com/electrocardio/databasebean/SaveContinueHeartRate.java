package com.electrocardio.databasebean;

import com.electrocardio.base.BaseApplication;
import com.electrocardio.database.ContinueHeartRateDao;
import com.electrocardio.util.ThreadUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/03/29.保存连续测量的心率
 */
public class SaveContinueHeartRate {

    private boolean SAVETOFIRST = true;// 保存到第一个数组
    private ArrayList<Integer> heartRateArrFir;// 第一个存储60秒的心率的数组
    private ArrayList<Integer> heartRateArrSec;// 第二个存储60秒的心率的数组
    private ContinueHeartRate continueHeartRate;// 连续测量的心率
    private String fileDir = "";// 存储数据的文件目录
    private String fileSrc = "";// 存储数据的文件路径

    /**
     * 添加心率数据
     *
     * @param heartRate
     */
    public void addHeartRate(int heartRate) {
        if (SAVETOFIRST) {// 如果要存储到第一个集合中
            heartRateArrFir.add(heartRate);
            if (heartRateArrFir.size() >= 60) {
                SAVETOFIRST = false;
                int sum = 0;
                for (int data : heartRateArrFir)
                    sum += data;
                sum /= heartRateArrFir.size();
                heartRateArrFir.clear();
                saveData(sum);
            }
        }else{
            heartRateArrSec.add(heartRate);
            if (heartRateArrSec.size() >= 60) {
                SAVETOFIRST = true;
                int sum = 0;
                for (int data : heartRateArrSec)
                    sum += data;
                sum /= heartRateArrSec.size();
                heartRateArrSec.clear();
                saveData(sum);
            }
        }
    }

    /**
     * 设置时间戳
     *
     * @param timeStamp
     */
    private void setTimeStamp(long timeStamp) {
        continueHeartRate.setTimeStamp(timeStamp);
    }

    /**
     * 设置存储数据的文件的路径
     *
     * @param src
     */
    private void setFileSrc(String src) {
        fileSrc = src;
        continueHeartRate.setDataFileSrc(fileSrc);
    }

    /**
     * 设置存储数据的文件的目录
     *
     * @param dir
     */
    private void setFileDir(String dir) {
        fileDir = dir;
    }

    /**
     * 获取continueHeartRate
     *
     * @return
     */
    public ContinueHeartRate getContinueHeartRate() {
        return continueHeartRate;
    }

    /**
     * 构造函数
     *
     * @param src
     * @param timeStamp
     */
    public SaveContinueHeartRate(String dir, String src, long timeStamp) {
        heartRateArrFir = new ArrayList<>();
        continueHeartRate = new ContinueHeartRate();
        setTimeStamp(timeStamp);
        setFileDir(dir);
        setFileSrc(src);
        ContinueHeartRateDao.getInstance(BaseApplication.getInstance()).
                addOneContinueHeartRateRecord(continueHeartRate);
    }

    /**
     * 存储数据到文件中
     *
     * @param heartRate
     */
    private void saveData(final int heartRate) {
        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                saveDataToFile(heartRate);
            }
        });
    }

    /**
     * 测量结束
     */
    public void measureEnd() {
        int sum = 0;
        for (int data : heartRateArrFir)
            sum += data;
        if (heartRateArrFir.size() != 0)
            sum /= heartRateArrFir.size();
        heartRateArrFir.clear();
        saveData(sum);
    }

    /**
     * 存储数据到文件中
     *
     * @param heartrate
     */
    private synchronized void saveDataToFile(int heartrate) {
        File file = new File(fileDir);
        if (!file.exists())
            file.mkdirs();
        File srcFile = new File(fileSrc);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(srcFile, true);
            fileOutputStream.write((heartrate + "\t").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null)
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

}
