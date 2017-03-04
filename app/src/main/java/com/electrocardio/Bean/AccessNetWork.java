package com.electrocardio.Bean;

import android.content.Context;
import android.widget.Toast;

import com.electrocardio.base.BaseApplication;
import com.electrocardio.database.AbnormalListDao;
import com.electrocardio.database.ContinueMeasureRecordDao;
import com.electrocardio.database.UserDao;
import com.electrocardio.databasebean.AbnormalDataCacheBean;
import com.electrocardio.databasebean.AbnormalSignInfor;
import com.electrocardio.util.ConstantUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import com.electrocardio.databasebean.AbnormalListBean;

import org.json.JSONObject;

/**
 * Created by ZhangBo on 2016/04/23.
 */
public class AccessNetWork {

    private Context mContext;// 上下文环境变量
    private UploadProgressListener mUploadProgressListener;// 上传进度的事件监听器
    private AbnormalListBean mAbnormalListBean;// 异常信息列表

    /**
     * 构造函数
     *
     * @param context
     */
    public AccessNetWork(Context context) {
        mContext = context;
    }

    /**
     * 根据abnormalListBean上传一份文件
     *
     * @param abnormalListBean
     */
    public void uploadFile(AbnormalListBean abnormalListBean) {
        mAbnormalListBean = abnormalListBean;
        File file = new File(abnormalListBean.getDataFileSrc());
        if (!file.exists()) {
            Toast.makeText(BaseApplication.getInstance(), "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("patientId", UserDao.getInstance(BaseApplication.getInstance()).getUserID());
        String dateType = abnormalListBean.getTYPE() + "";
        String ecgStartTime = getTimeStampLabel(abnormalListBean.getYmTimeStamp());
        String startTime = getTimeStampLabel(abnormalListBean.getTimeStamp());
        String endTime = getTimeStampLabel(abnormalListBean.getTimeStamp() + 20000);
        String heartRate = abnormalListBean.getHeartRate() + "";

        params.put("dateTyep", dateType);
        params.put("ecgStartTime", ecgStartTime);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("average_heart_rate", heartRate);
        params.put("doubt", "疑似:" + AbnormalSignInfor.getStateDescription(abnormalListBean.getDETAILTYPE()));
        String url = ConstantUtils.HOST + ConstantUtils.UPLOADEECG;

        OkHttpUtils.post()
                .url(url)
                .params(params)
                .addFile("ecgFile", abnormalListBean.getDataFileSrc(), file)
                .build()
                .execute(new myUserCallback());
    }

    /**
     * 根据abnormalDataCacheBean上传一份文件
     *
     * @param abnormalDataCacheBean
     */
    public void uploadFile(AbnormalDataCacheBean abnormalDataCacheBean) {
        uploadFile(abnormalDataCacheBean.getAbnormalListBean());
    }

    public class myUserCallback extends Callback {

        public myUserCallback() {
        }

        @Override
        public Object parseNetworkResponse(Response response) throws Exception {
            String str = response.body().string();
            System.out.println("response.body().string():" + str);
            if (str != null) {
                JSONObject object = new JSONObject(str);
                String result = object.getString("statusCode");
                if (result.equals("1")) {
                    if (mAbnormalListBean != null) {
                        AbnormalListDao.getInstance(mContext).updateAbnormalRecordSubmitState(
                                mAbnormalListBean.getYmTimeStamp(), mAbnormalListBean.getTimeStamp(),
                                AbnormalListBean.SUBMITED);
                        // 更新连续测量中已经上传的文件的大小
                        ContinueMeasureRecordDao.getInstance(mContext).updataContinueMeasureRecordDataSize(
                                mAbnormalListBean.getYmTimeStamp(), mAbnormalListBean.getFileSize());
                        if (mUploadProgressListener != null)
                            mUploadProgressListener.uploadSuccess(mAbnormalListBean.getYmTimeStamp());
                    }
                }
            }
            return null;
        }

        @Override
        public void onError(Call call, Exception e) {
        }

        @Override
        public void onResponse(Object o) {
        }

        @Override
        public void onBefore(Request request) {
        }

        @Override
        public void onAfter() {
            if (mUploadProgressListener != null)
                mUploadProgressListener.uploadAfter();
        }

        @Override
        public void inProgress(float progress) {
        }

    }

    /**
     * 根据时间戳获取时间标签
     *
     * @param timeStamp
     * @return
     */
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
     * 设置上传进度的事件监听器
     *
     * @param uploadProgressListener
     */
    public void setUploadProgressListener(UploadProgressListener uploadProgressListener) {
        mUploadProgressListener = uploadProgressListener;
    }

    public interface UploadProgressListener {
        public void uploadSuccess(long timeStamp);

        public void uploadAfter();
    }

}
