package com.electrocardio.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.electrocardio.database.UploadMeasureRecordDao;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/05/09.
 */
public abstract class HandleUploadData extends Thread {

    private Context mContext;// 环境变量
    private Handler handler;
    private Looper looper;// 轮询器
    private boolean ISONLYWIFI = false;// 是否仅在wifi下上传
    public final static int DATABASECHANGED = 1;// 数据库改变信号
    private final int UPLOADOVER = 2;// 上传完信号
    private UploadOverOneRecordListener mUploadOverOneRecordListener;// 上传完一条连续测量记录的事件监听器
    private ArrayList<Long> timeStampArr = new ArrayList<>();// 存储连续测量时间戳的集合
    private ArrayList<UploadConMeaRecordBean> uploading = new ArrayList<>();// 正在上传的信息的集合

    /**
     * 构造函数
     *
     * @param context
     */
    public HandleUploadData(Context context) {
        mContext = context;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case DATABASECHANGED:// 数据库改变时要调用的方法(主要是有新的上传数据加入到数据库中)
                        handleUploadMessage();
                        break;
                    case UPLOADOVER:// 上传完一份连续测量记录后，将这条记录从正在进行上传操作的集合中删除
                        UploadConMeaRecordBean uploadConMeaRecordBean = (UploadConMeaRecordBean) msg.obj;
                        if (timeStampArr.contains(uploadConMeaRecordBean.getTimeStamp()))
                            timeStampArr.remove(uploadConMeaRecordBean.getTimeStamp());
                        if (uploading.contains(uploadConMeaRecordBean))
                            uploading.remove(uploadConMeaRecordBean);
                        break;
                }
            }
        };
        // 获取数据库中所有处于上传状态的数据
        timeStampArr = UploadMeasureRecordDao.getInstance(mContext).getAllUploadingRecord();
    }

    /**
     * 是否仅在wifi下上传
     *
     * @param isOnlyWifi
     */
    public void isONLYWIFI(boolean isOnlyWifi) {
        if (ISONLYWIFI != isOnlyWifi) {
            ISONLYWIFI = isOnlyWifi;
            for (UploadConMeaRecordBean bean : uploading)
                bean.isONLYWIFI(ISONLYWIFI);
        }
    }

    @Override
    public void run() {
        Looper.prepare();
        looper = Looper.myLooper();
        Looper.loop();
    }

    @Override
    public synchronized void start() {
        super.start();
        // 线程开始运行时，先判断当前集合中是否有需要上传的数据，如果有，则先对这些数据进行上传操作
        for (long timeStamp : timeStampArr) {
            UploadConMeaRecordBean uploadConMea = new UploadConMeaRecordBean(mContext, timeStamp);
            uploadConMea.isONLYWIFI(ISONLYWIFI);
            uploading.add(uploadConMea);
            uploadConMea.setUploadOverOnConRecordListener(new UploadConMeaRecordBean.UploadOverOnConRecordListener() {
                @Override
                public void uploadOver(UploadConMeaRecordBean uploadConMeaRecordBean) {
                    handler.obtainMessage(UPLOADOVER, uploadConMeaRecordBean).sendToTarget();
                    if (mUploadOverOneRecordListener != null)
                        mUploadOverOneRecordListener.uploadOverOneRecord(uploadConMeaRecordBean.getTimeStamp());
                }

                @Override
                public void uploadOneFile(long timeStamp) {
                    if (mUploadOverOneRecordListener != null)
                        mUploadOverOneRecordListener.uploadOneFile(timeStamp);
                }
            });
            // 开始上传异常数据
            uploadConMea.uploadAbnormalData();
        }
    }

    /**
     * 获取handler
     *
     * @return
     */
    public Handler getHandler() {
        return handler;
    }

    /**
     * 退出线程
     */
    public void out() {
        UploadMeasureRecordDao.getInstance(mContext).deleteAllData();
        threadOut();
        if (looper != null)
            looper.quit();
    }

    /**
     * 退出线程
     */
    public abstract void threadOut();

    /**
     * 处理上传信号
     */
    public void handleUploadMessage() {
        System.out.println("接收到信号.........");
        // 先获取所有状态为上传的连续测量记录，然后从这些数据中获取没有进行上传操作的记录，然后对其进行上传操作
        ArrayList<Long> arrayList = UploadMeasureRecordDao.getInstance(mContext).getAllUploadingRecord();
        for (long timeStamp : arrayList) {
            if (!timeStampArr.contains(timeStamp)) {
                timeStampArr.add(timeStamp);
                UploadConMeaRecordBean uploadConMea = new UploadConMeaRecordBean(mContext, timeStamp);
                uploadConMea.isONLYWIFI(ISONLYWIFI);
                uploading.add(uploadConMea);
                uploadConMea.setUploadOverOnConRecordListener(new UploadConMeaRecordBean.UploadOverOnConRecordListener() {

                    @Override
                    public void uploadOver(UploadConMeaRecordBean uploadConMeaRecordBean) {
                        handler.obtainMessage(UPLOADOVER, uploadConMeaRecordBean).sendToTarget();
                        if (mUploadOverOneRecordListener != null)
                            mUploadOverOneRecordListener.uploadOverOneRecord(uploadConMeaRecordBean.getTimeStamp());
                    }

                    @Override
                    public void uploadOneFile(long timeStamp) {
                        if (mUploadOverOneRecordListener != null)
                            mUploadOverOneRecordListener.uploadOneFile(timeStamp);
                    }
                });
                // 开始上传异常数据
                uploadConMea.uploadAbnormalData();
            }
        }
    }

    /**
     * 设置上传完一条连续测量记录的事件监听器
     *
     * @param uploadOverOneRecordListener
     */
    public void setUploadOverOneRecordListener(UploadOverOneRecordListener uploadOverOneRecordListener) {
        mUploadOverOneRecordListener = uploadOverOneRecordListener;
    }

    public interface UploadOverOneRecordListener {
        public void uploadOverOneRecord(long timeStamp);

        public void uploadOneFile(long timeStamp);
    }
}
