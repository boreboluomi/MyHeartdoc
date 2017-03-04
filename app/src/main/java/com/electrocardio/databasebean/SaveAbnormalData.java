package com.electrocardio.databasebean;

import android.app.Activity;
import android.os.Message;

import com.electrocardio.base.BaseApplication;
import com.electrocardio.custom.elec.HandleAbnormalInfor;
import com.electrocardio.database.ContinueMeasureRecordDao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/03/28.
 */
public abstract class SaveAbnormalData {

    private WeakReference<Activity> mWeakActivity;// 弱引用的Activity
    private AbnormalDataCacheBean abnormalDataCacheBean;
    private ArrayList<AbnormalDataCacheBean> cacheArray;
    private String dataFileSrc;// 文件存储路径
    private long ymTimeStamp = -1l;// 区分每次连续测量的时间戳
    private HandleAbnormalInfor handleAbnormalInfor;// 处理异常信息的线程
    private final int RECEICESIGNWITHDATA = 0;// 接收到异常信息信号和数据
    private final int ADDECGDATA = 1;// 添加ECG数据
    private final int ABNORMALINFORFILL = 2;// 一条异常信息数据采集完毕
    private final int ADDHEARTRATEDATA = 3;// 添加心率数据

    /**
     * 处理handler信号
     *
     * @param msg
     */
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case RECEICESIGNWITHDATA:// 接收到异常信号和缓存数据
                Object[] objects = (Object[]) msg.obj;
                receiveAbnormalSignWithData(msg.arg1, (ArrayList<Float>) objects[0], (ArrayList<Integer>) objects[1]);
                break;
            case ADDECGDATA:// 添加ecg数据
                receiveECGData((Float) msg.obj);
                break;
            case ABNORMALINFORFILL:// 一条信息数据采集完毕
                OneAbnormalInforFill((AbnormalDataCacheBean) msg.obj);
                break;
            case ADDHEARTRATEDATA:// 添加心率数据
                receiveHeartRateDate((Integer) msg.obj);
                break;
        }
    }

    /**
     * 构造函数
     *
     * @param activity
     * @param timeStamp
     * @param src
     */
    public SaveAbnormalData(WeakReference<Activity> activity, long timeStamp, String src) {
        mWeakActivity = activity;
        if (mWeakActivity.get() == null)
            return;
        ymTimeStamp = timeStamp;
        dataFileSrc = src;
        if (cacheArray == null)
            cacheArray = new ArrayList<>();
        if (handleAbnormalInfor == null) {
            handleAbnormalInfor = new HandleAbnormalInfor(mWeakActivity) {
                @Override
                public void handleAbnormalMessage(Message message) {
                    handleMessage(message);
                }
            };
            handleAbnormalInfor.start();
        }
    }

    /**
     * @param type
     * @param abnormalData
     */
    public void setAbnormalSignWithData(int type, ArrayList<Float> abnormalData, ArrayList<Integer> heartRateArr) {
        if (handleAbnormalInfor != null)
            if (handleAbnormalInfor.getHandler() != null) {
                Object[] object = new Object[]{abnormalData, heartRateArr};
                handleAbnormalInfor.getHandler().obtainMessage(RECEICESIGNWITHDATA, type, 0, object).sendToTarget();
            }
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void addData(float data) {
        if (cacheArray.size() > 0)
            if (handleAbnormalInfor != null)
                if (handleAbnormalInfor.getHandler() != null)
                    handleAbnormalInfor.getHandler().obtainMessage(ADDECGDATA, data).sendToTarget();
    }

    /**
     * 添加心率数据
     *
     * @param heartRate
     */
    public void addHeartRateData(int heartRate) {
        if (cacheArray.size() > 0)
            if (handleAbnormalInfor != null)
                if (handleAbnormalInfor.getHandler() != null)
                    handleAbnormalInfor.getHandler().obtainMessage(ADDHEARTRATEDATA, heartRate).sendToTarget();
    }

    /**
     * 接收到ECG数据
     *
     * @param data
     */
    private void receiveECGData(float data) {
        for (AbnormalDataCacheBean bean : cacheArray) {
            if (!bean.isFulled()) {
                bean.addData(data);
                if (bean.isFulled()) {
                    handleAbnormalInfor.getHandler().obtainMessage(ABNORMALINFORFILL, bean).sendToTarget();
                }
            }
        }
    }

    /**
     * 接收到心率数据
     *
     * @param heartRate
     */
    private void receiveHeartRateDate(int heartRate) {
        for (AbnormalDataCacheBean bean : cacheArray) {
            if (!bean.isFulled())
                bean.setHeartRateArr(heartRate);
        }
    }

    /**
     * 接收到异常信息信号和数据
     *
     * @param type
     * @param arrayList
     */
    private void receiveAbnormalSignWithData(int type, ArrayList<Float> arrayList, ArrayList<Integer> heartRateArr) {
        if (cacheArray.size() < 3) {
            abnormalDataCacheBean = new AbnormalDataCacheBean(mWeakActivity.get(), ymTimeStamp) {
                @Override
                public void saveComplete(AbnormalDataCacheBean abnormalDataCacheBean) {
                    SaveAbnormalData.this.saveComplete(abnormalDataCacheBean);
                    if (cacheArray.size() == 0)
                        ContinueMeasureRecordDao.getInstance(BaseApplication.getInstance()).
                                updateContinueMeasureRecordDataSize(ymTimeStamp);
                }
            };
            abnormalDataCacheBean.addData(arrayList);
            abnormalDataCacheBean.setHeartRateArr(heartRateArr);
            abnormalDataCacheBean.getAbnormalListBean().setTYPE(type);
            abnormalDataCacheBean.getAbnormalListBean().setSubmited(AbnormalListBean.NOTSUBMITED);
            abnormalDataCacheBean.getAbnormalListBean().setDataFileDir(dataFileSrc + "/" + "abnormal");
            abnormalDataCacheBean.setFileSrc(dataFileSrc + "/" + "abnormal/" + abnormalDataCacheBean.
                    getAbnormalListBean().getTimeStamp() + ".txt");
            cacheArray.add(abnormalDataCacheBean);
        }
    }

    /**
     * 一条异常信息数据采集完毕
     *
     * @param abnormalDataCacheBean
     */
    private void OneAbnormalInforFill(AbnormalDataCacheBean abnormalDataCacheBean) {
        if (cacheArray.contains(abnormalDataCacheBean))
            cacheArray.remove(abnormalDataCacheBean);
        abnormalDataCacheBean.save();

    }

    /**
     * 关闭处理异常数据的handleAbnormalInfor
     */
    public void close() {
        if (cacheArray.size() > 0) {
            for (AbnormalDataCacheBean bean : cacheArray)
                handleAbnormalInfor.getHandler().obtainMessage(ABNORMALINFORFILL, bean).sendToTarget();
        }
        if (handleAbnormalInfor != null)
            handleAbnormalInfor.out();
    }

    /**
     * 保存完毕
     *
     * @param abnormalDataCacheBean
     */
    public abstract void saveComplete(AbnormalDataCacheBean abnormalDataCacheBean);
}
