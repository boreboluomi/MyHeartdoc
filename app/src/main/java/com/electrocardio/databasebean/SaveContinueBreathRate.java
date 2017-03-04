package com.electrocardio.databasebean;

import com.electrocardio.base.BaseApplication;
import com.electrocardio.database.ContinueBreathRateDao;
import com.electrocardio.util.ThreadUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/03/29.保存连续测量的呼吸率
 */
public class SaveContinueBreathRate {

    private ArrayList<Integer> breathRateArray;// 存储60秒的呼吸率
    private ContinueBreathRate continueBreathRate;// 连续测量的呼吸率
    private String fileDir = "";// 存储数据的文件目录
    private String fileSrc = "";// 存储数据的文件路径

    /**
     * 添加心率数据
     *
     * @param heartRate
     */
    public void addBreathRate(int heartRate) {
        breathRateArray.add(heartRate);
        if (breathRateArray.size() >= 60) {
            int sum = 0;
            for (int data : breathRateArray)
                sum += data;
            sum /= breathRateArray.size();
            breathRateArray.clear();
            saveData(sum);
        }
    }

    /**
     * 设置时间戳
     *
     * @param timeStamp
     */
    private void setTimeStamp(long timeStamp) {
        continueBreathRate.setTimeStamp(timeStamp);
    }

    /**
     * 设置存储数据的文件的路径
     *
     * @param src
     */
    private void setFileSrc(String src) {
        fileSrc = src;
        continueBreathRate.setDataFileSrc(fileSrc);
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
    public ContinueBreathRate getContinueBreathRate() {
        return continueBreathRate;
    }

    /**
     * 构造函数
     *
     * @param src
     * @param timeStamp
     */
    public SaveContinueBreathRate(String dir, String src, long timeStamp) {
        breathRateArray = new ArrayList<>();
        continueBreathRate = new ContinueBreathRate();
        setTimeStamp(timeStamp);
        setFileDir(dir);
        setFileSrc(src);
        ContinueBreathRateDao.getInstance(BaseApplication.getInstance()).
                addOneContinueBreathRateRecord(continueBreathRate);
    }

    /**
     * 存储数据到文件中
     *
     * @param breathRate
     */
    private void saveData(final int breathRate) {
        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                saveDataToFile(breathRate);
            }
        });
    }

    /**
     * 测量结束
     */
    public void measureEnd() {
        int sum = 0;
        for (int data : breathRateArray)
            sum += data;
        if (breathRateArray.size() != 0)
            sum /= breathRateArray.size();
        breathRateArray.clear();
        saveData(sum);
    }

    /**
     * 存储数据到文件中
     *
     * @param breathRate
     */
    private synchronized void saveDataToFile(int breathRate) {
        File file = new File(fileDir);
        if (!file.exists())
            file.mkdirs();
        File srcFile = new File(fileSrc);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(srcFile, true);
            fileOutputStream.write((breathRate + "\t").getBytes());
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
