package com.electrocardio.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import com.electrocardio.Bean.AccessNetWork;
import com.electrocardio.Bean.UploadDataState;
import com.electrocardio.database.AbnormalListDao;
import com.electrocardio.database.ContinueMeasureRecordDao;
import com.electrocardio.database.UploadMeasureRecordDao;
import com.electrocardio.databasebean.AbnormalListBean;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/05/10.
 */
public class UploadConMeaRecordBean {

    private Context mContext;// 环境变量
    private long mTimeStamp;// 时间戳
    private ArrayList<AbnormalListBean> abnormalList;// 异常信息列表
    private AccessNetWork accessNetWork;// 上传文件的类
    private boolean ISONLYWIFI = false;// 是否仅在wifi下上传
    private final int UPLOADSUCCESSONEFILE = 1;// 上传成功一份文件
    private final int UPLOADONEFILEAFTER = 2;// 一份文件上传结束(不管上传成功或者失败)
    private UploadOverOnConRecordListener mUploadOverOnConRecordListener;// 上传完毕的监听事件
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case UPLOADSUCCESSONEFILE:
                    // 一份文件上传成功后的回调接口
                    if (mUploadOverOnConRecordListener != null)
                        mUploadOverOnConRecordListener.uploadOneFile((Long) msg.obj);
                    break;
                case UPLOADONEFILEAFTER:
                    // 一份异常文件上传结束后接着上传下一份异常文件(不论这份文件上传成功或者失败)
                    position++;
                    uploadAbnormalData();
                    break;
            }
        }
    };
    private int position = 0;

    public UploadConMeaRecordBean(Context context, long timeStamp) {
        mContext = context;
        mTimeStamp = timeStamp;
        // 获取一次连续测量中所有的异常数据文件
        abnormalList = AbnormalListDao.getInstance(mContext).getAllNotSubmitedAbnormalListBean(mTimeStamp);
        accessNetWork = new AccessNetWork(mContext);
        accessNetWork.setUploadProgressListener(new AccessNetWork.UploadProgressListener() {
            @Override
            public void uploadSuccess(long timeStamp) {
                // 一份文件上传成功
                handler.obtainMessage(UPLOADSUCCESSONEFILE, timeStamp).sendToTarget();
            }

            @Override
            public void uploadAfter() {
                // 一份异常文件上传结束后接着上传下一份异常文件(不论这份文件上传成功或者失败)
                System.out.println("一份异常文件上传结束后。。。。。。。。。。。。。");
                handler.obtainMessage(UPLOADONEFILEAFTER).sendToTarget();
            }
        });
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public long getTimeStamp() {
        return mTimeStamp;
    }

    /**
     * 设置是否仅在wifi下上传
     *
     * @param isOnlyWifi
     */
    public void isONLYWIFI(boolean isOnlyWifi) {
        ISONLYWIFI = isOnlyWifi;
    }

    /**
     * 上传异常数据
     */
    public synchronized void uploadAbnormalData() {
        // 上传异常数据
        final long timeStamp = abnormalList.get(abnormalList.size() - 1).getYmTimeStamp();
        // 所有的异常文件都已经经过上传操作后的情况(这种情况并不能确保所有的文件已经全部上传到服务器)
//        if (position == abnormalList.size() && position != 0) {
//            // 所有的异常文件都已经成功上传,则将本条数据的状态更改为上传完毕,否则将状态更改为重新上传
//            if (ContinueMeasureRecordDao.getInstance(mContext).judgeConMeaSubimtedOver(timeStamp)) {
//                UploadMeasureRecordDao.getInstance(mContext).updateOneRecordState(timeStamp,
//                        UploadDataState.UPLOADOVER.getState());
//            } else
//                UploadMeasureRecordDao.getInstance(mContext).updateOneRecordState(timeStamp,
//                        UploadDataState.UPLOADAGINE.getState());
//            // 所有的异常文件都已经经过上传操作后的回调接口
//            if (mUploadOverOnConRecordListener != null)
//                mUploadOverOnConRecordListener.uploadOver(timeStamp);
//            return;
//        }
//        System.out.println("abnormalList.size():。。。。。。。。" + abnormalList.size());
//        System.out.println("position:。。。。。。。。。。。。。。。。。。" + position);

        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        // 如果当前网络未处于拦截状态
        if (activeInfo == null) {
            if (position < abnormalList.size()) {
                handler.obtainMessage(UPLOADONEFILEAFTER).sendToTarget();
                if (mUploadOverOnConRecordListener != null)
                    mUploadOverOnConRecordListener.uploadOneFile(timeStamp);
            } else {
                // 所有的异常文件都已经成功上传,则将本条数据的状态更改为上传完毕,否则将状态更改为重新上传
                if (ContinueMeasureRecordDao.getInstance(mContext).judgeConMeaSubimtedOver(timeStamp)) {
                    UploadMeasureRecordDao.getInstance(mContext).updateOneRecordState(timeStamp,
                            UploadDataState.UPLOADOVER.getState());
                } else
                    UploadMeasureRecordDao.getInstance(mContext).updateOneRecordState(timeStamp,
                            UploadDataState.UPLOADAGINE.getState());
                // 所有的异常文件都已经经过上传操作后的回调接口
                if (mUploadOverOnConRecordListener != null)
                    mUploadOverOnConRecordListener.uploadOver(UploadConMeaRecordBean.this);
            }
            return;
        }

        System.out.println("ISONLYWIFI:" + ISONLYWIFI + ";activeInfo.getType() == ConnectivityManager.TYPE_WIFI"
                + (activeInfo.getType() == ConnectivityManager.TYPE_WIFI));
        // 如果仅在wifi状态下上传同时当前连接状态不是wifi状态
        if (ISONLYWIFI && activeInfo.getType() != ConnectivityManager.TYPE_WIFI) {
            if (position  < abnormalList.size()) {
                handler.obtainMessage(UPLOADONEFILEAFTER).sendToTarget();
                if (mUploadOverOnConRecordListener != null)
                    mUploadOverOnConRecordListener.uploadOneFile(timeStamp);
            } else {
                // 所有的异常文件都已经成功上传,则将本条数据的状态更改为上传完毕,否则将状态更改为重新上传
                if (ContinueMeasureRecordDao.getInstance(mContext).judgeConMeaSubimtedOver(timeStamp)) {
                    UploadMeasureRecordDao.getInstance(mContext).updateOneRecordState(timeStamp,
                            UploadDataState.UPLOADOVER.getState());
                } else
                    UploadMeasureRecordDao.getInstance(mContext).updateOneRecordState(timeStamp,
                            UploadDataState.UPLOADAGINE.getState());
                // 所有的异常文件都已经经过上传操作后的回调接口
                if (mUploadOverOnConRecordListener != null)
                    mUploadOverOnConRecordListener.uploadOver(UploadConMeaRecordBean.this);
            }
            return;
        }

        // 如果当前记录具备上传文件资格才可以上传文件
        if (UploadMeasureRecordDao.getInstance(mContext).judgeHasAuthToUpload(timeStamp)) {
            ThreadUtils.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    if (position == abnormalList.size() && position != 0) {
                        // 所有的异常文件都已经成功上传,则将本条数据的状态更改为上传完毕,否则将状态更改为重新上传
                        if (ContinueMeasureRecordDao.getInstance(mContext).judgeConMeaSubimtedOver(timeStamp)) {
                            UploadMeasureRecordDao.getInstance(mContext).updateOneRecordState(timeStamp,
                                    UploadDataState.UPLOADOVER.getState());
                        } else
                            UploadMeasureRecordDao.getInstance(mContext).updateOneRecordState(timeStamp,
                                    UploadDataState.UPLOADAGINE.getState());
                        // 所有的异常文件都已经经过上传操作后的回调接口
                        if (mUploadOverOnConRecordListener != null)
                            mUploadOverOnConRecordListener.uploadOver(UploadConMeaRecordBean.this);
                        return;
                    }
//                    System.out.println("position == abnormalList.size():" + (position == abnormalList.size()));
                    if (position < abnormalList.size())
                        accessNetWork.uploadFile(abnormalList.get(position));
                }
            });
        } else {
//            System.out.println("当前文件不具备上传资格。。。。。。");
            handler.obtainMessage(UPLOADONEFILEAFTER).sendToTarget();
            if (mUploadOverOnConRecordListener != null)
                mUploadOverOnConRecordListener.uploadOneFile(timeStamp);
        }
    }

    /**
     * 设置上传完毕的监听事件
     *
     * @param uploadOverOnConRecord
     */
    public void setUploadOverOnConRecordListener(UploadOverOnConRecordListener uploadOverOnConRecord) {
        mUploadOverOnConRecordListener = uploadOverOnConRecord;
    }

    public interface UploadOverOnConRecordListener {
        public void uploadOver(UploadConMeaRecordBean uploadConMeaRecordBean);

        public void uploadOneFile(long timeStamp);
    }
}
