package com.electrocardio.databasebean;

import android.content.Context;

import com.electrocardio.database.AbnormalListDao;
import com.electrocardio.database.ContinueMeasureRecordDao;
import com.electrocardio.database.UserDao;
import com.electrocardio.util.DeleteFileUtil;
import com.electrocardio.util.Md5;
import com.electrocardio.util.ThreadUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ZhangBo on 2016/03/28.// 缓存异常数据的javaBean
 */
public abstract class AbnormalDataCacheBean {

    private Context mContext;// 上下文
    private long ymTimeStamp = -1l;// 区分连续测量的时间戳
    private AbnormalListBean abnormalListBean;
    // private String fileSrc = "";// 数据文件存储路径
    private ArrayList<Float> dataArray;
    private ArrayList<Integer> heartRateArr;

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public AbnormalListBean getAbnormalListBean() {
        return abnormalListBean;
    }

    public ArrayList<Float> getDataArray() {
        return dataArray;
    }

    public ArrayList<Integer> getHeartRateArr() {
        return heartRateArr;
    }

    /**
     * 设置心率数据集合
     *
     * @param heartRateArr
     */
    public void setHeartRateArr(ArrayList<Integer> heartRateArr) {
        for (int data : heartRateArr)
            setHeartRateArr(data);
    }

    /**
     * 设置心率数据
     *
     * @param heartRate
     */
    public void setHeartRateArr(int heartRate) {
        heartRateArr.add(heartRate);
    }

    /**
     * 判断是否已经满了
     *
     * @return
     */
    public boolean isFulled() {
        return dataArray.size() >= AbnormalSignInfor.getTotalLength(abnormalListBean.getDETAILTYPE());
    }

    /**
     * 设置数据
     *
     * @param dataArray
     */
    public void setDataArray(ArrayList<Float> dataArray) {
        for (float data : dataArray)
            setDataArray(data);
    }

    public void setDataArray(float data) {
        dataArray.add(data);
    }

    public String getFileSrc() {
        return abnormalListBean.getDataFileSrc();
    }

    public void setFileSrc(String fileSrc) {
        abnormalListBean.setDataFileSrc(fileSrc);
    }

    /**
     * 添加数据
     *
     * @param array
     */
    public void addData(ArrayList<Float> array) {
//        for (float data : array)
//            addData(data);
        dataArray = array;
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void addData(float data) {
        dataArray.add(data);
    }

    @Override
    public String toString() {
        return "SaveAbnormalData{" +
                "abnormalListBean=" + abnormalListBean +
                ", dataArray=" + dataArray +
                '}';
    }

    /**
     * 构造函数
     *
     * @param context
     */
    public AbnormalDataCacheBean(Context context, long timeStamp) {
        mContext = context;
        ymTimeStamp = timeStamp;
        abnormalListBean = new AbnormalListBean();
        abnormalListBean.setYmTimeStamp(ymTimeStamp);
        abnormalListBean.setTimeStamp(System.currentTimeMillis());
        dataArray = new ArrayList<>();
        heartRateArr = new ArrayList<>();
    }

    /**
     * 保存数据
     */
    public void save() {
        int sum = 0;
        if (heartRateArr.size() != 0) {
            for (int data : heartRateArr)
                sum += data;
            sum /= heartRateArr.size();
        }
        abnormalListBean.setHeartRate(sum);
        saveToDataBase();
        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (saveDataToFile()) {
                    saveComplete(AbnormalDataCacheBean.this);
                }
            }
        });
    }

    /**
     * @param abnormalDataCacheBean
     */
    public abstract void saveComplete(AbnormalDataCacheBean abnormalDataCacheBean);

    /**
     * 将数据保存到数据库
     */
    private void saveToDataBase() {
        AbnormalListDao.getInstance(mContext).addOneAbnormalRecord(abnormalListBean);
    }

    private String getTimeStampLabel(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String monthStr = month < 10 ? "0" + month : month + "";
        String dayStr = day < 10 ? "0" + day : day + "";
        String hourStr = hour < 10 ? "0" + hour : hour + "";
        String minuteStr = minute < 10 ? "0" + minute : minute + "";
        String secondStr = second < 10 ? "0" + second : second + "";

        String label = year + "-" + monthStr + "-" + dayStr + " " + hourStr + ":" + minuteStr + ":"
                + secondStr;
        return label;
    }

    /**
     * 成功提交到服务器
     */
    public void submited() {
        abnormalListBean.setSubmited(AbnormalListBean.SUBMITED);
        AbnormalListDao.getInstance(mContext).updateAbnormalRecordSubmitState(abnormalListBean.
                getYmTimeStamp(), abnormalListBean.getTimeStamp(), abnormalListBean.getSubmited());
    }

    /**
     * 将数据保存到文件
     *
     * @return
     */
    private boolean saveDataToFile() {
        if (dataArray == null)
            return false;
        File file = new File(abnormalListBean.getDataFileDir());
        if (!file.exists())
            file.mkdirs();
        File dataFile = new File(abnormalListBean.getDataFileSrc());
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(dataFile);
            for (float data : dataArray) {
                outputStream.write((data + ",").getBytes());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        long fileSize = DeleteFileUtil.getFileSizeByByte(abnormalListBean.getDataFileSrc());
        abnormalListBean.setFileSize(DeleteFileUtil.getFileSize(abnormalListBean.getDataFileSrc()));
        String path = abnormalListBean.getDataFileDir();
        String toName = Md5.MD5(UserDao.getInstance(mContext).getUserID() + abnormalListBean.getTYPE() + getTimeStampLabel(
                abnormalListBean.getTimeStamp()) + fileSize) + ".txt";
        DeleteFileUtil.renameFile(path, abnormalListBean.getTimeStamp() + ".txt", toName);
        abnormalListBean.setDataFileSrc(path + "/" + toName);
        AbnormalListDao.getInstance(mContext).updateAbnormalRecordDataSize(abnormalListBean.
                getYmTimeStamp(), abnormalListBean.getTimeStamp(), DeleteFileUtil.
                getFileSize(abnormalListBean.getDataFileSrc()));
        AbnormalListDao.getInstance(mContext).updateAbnormalRecordFileSrc(abnormalListBean.
                getYmTimeStamp(), abnormalListBean.getTimeStamp(), abnormalListBean.getDataFileSrc());
        ContinueMeasureRecordDao.getInstance(mContext).updataContinueDataSizeByAddOneFile(
                abnormalListBean.getFileSize(), abnormalListBean.getYmTimeStamp());
        return true;
    }
}
