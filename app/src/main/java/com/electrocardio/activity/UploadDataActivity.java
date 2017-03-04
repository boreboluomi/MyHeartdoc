package com.electrocardio.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.electrocardio.Adapter.UploadDataAdapter;
import com.electrocardio.Bean.UploadDataBean;
import com.electrocardio.Bean.UploadDataState;
import com.electrocardio.R;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.custom.elec.SwitchButtonView;
import com.electrocardio.custom.elec.UploadDataView;
import com.electrocardio.database.ContinueMeasureRecordDao;
import com.electrocardio.database.UploadMeasureRecordDao;
import com.electrocardio.databasebean.ContinueMeasureRecord;
import com.electrocardio.util.ConstantUtils;
import com.electrocardio.util.HandleUploadData;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/3/9.
 */
public class UploadDataActivity extends Activity {

    private ImageView iv_back;// 返回按钮
    private TextView tv_title;// 标题
    private ListView listView;
    private UploadDataAdapter uploadDataAdapter;// 上传数据的适配器
    private SwitchButtonView switchButtonView;// 切换按钮
    private ArrayList<UploadDataBean> arrayList;
    private UploadDataBean uploadDataBean;
    private FrameLayout fl;
    private SharedPreferences sharePrefer;// sp文件操作类
    private HandleUploadData mHandleUploadData;// 处理上传数据的类

    private final int UPLOADONEFILE = 1;// 上传完一份文件
    private final int UPLOADOVERONERECORD = 2;// 上传完一次连续测量记录
    private final int UPLOADALLRECORD = 3;// 上传所有连续测量记录

    private Handler handler;// 处理上传数据中的Handler
    // 本Activity中的Handler
    private Handler uHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOADONEFILE:// 上传完一份文件
                    uploadOneFile((Long) msg.obj);
                    break;
                case UPLOADOVERONERECORD:// 上传完一条连续测量记录
                    uploadOverOneRecord((Long) msg.obj);
                    break;
                case UPLOADALLRECORD:// 上传所有连续测量记录
                    uploadAllConMeaRecord();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaddata);

        initView();

        setOnButtonClick();

    }

    /**
     * 初始化控件
     */
    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_titleLeft);
        tv_title = (TextView) findViewById(R.id.tv_title);
        listView = (ListView) findViewById(R.id.listView);
        fl = (FrameLayout) View.inflate(this, R.layout.wifi, null);
        switchButtonView = (SwitchButtonView) fl.findViewById(R.id.submitData);
        View bootView = new View(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
        bootView.setLayoutParams(params);
        AbsListView.LayoutParams paramsRL = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fl.setLayoutParams(paramsRL);
        listView.addHeaderView(fl);
        listView.addFooterView(bootView);

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        tv_title.setText("数据上传");
        sharePrefer = getSharedPreferences(ConstantUtils.APPNAME, Context.MODE_PRIVATE);
        // 获取是否仅在wifi上传
        boolean isOnlyWifi = sharePrefer.getBoolean(ConstantUtils.ISONLYWIFI, false);
        switchButtonView.setState(isOnlyWifi);

        uploadDataAdapter = new UploadDataAdapter(this);

        arrayList = new ArrayList<>();
        // 初始化Adapter的数据
        initAdapterData();

        uploadDataAdapter.setUploadDataList(arrayList);
        listView.setAdapter(uploadDataAdapter);

        // 判断BaseApplication中的HandleUploadData是否为空，如果为空，为其赋值并初始化
        if (BaseApplication.getInstance().getHandleUploadData() == null) {
            UploadMeasureRecordDao.getInstance(this).deleteAllData();
            BaseApplication.getInstance().setHandleUploadData(new HandleUploadData(BaseApplication.getInstance()) {
                @Override
                public void threadOut() {

                }
            });
            BaseApplication.getInstance().getHandleUploadData().isONLYWIFI(isOnlyWifi);
            BaseApplication.getInstance().getHandleUploadData().start();
        }
        // 将本Activity中的mHandleUploadData指向BaseApplication中的HandleUploadData
        mHandleUploadData = BaseApplication.getInstance().getHandleUploadData();
        mHandleUploadData.setUploadOverOneRecordListener(new HandleUploadData.UploadOverOneRecordListener() {
            @Override
            public void uploadOverOneRecord(long timeStamp) {
                uHandler.obtainMessage(UPLOADOVERONERECORD, timeStamp).sendToTarget();
            }

            @Override
            public void uploadOneFile(long timeStamp) {
                uHandler.obtainMessage(UPLOADONEFILE, timeStamp).sendToTarget();
            }
        });
        // 将handler指向mHandleUploadData中的Handler
        handler = mHandleUploadData.getHandler();
    }

    /**
     * 上传所有的连续测量记录
     */
    private void uploadAllConMeaRecord() {
        // 获取所有没有全部提交的连续测量记录
        ArrayList<ContinueMeasureRecord> conArray = ContinueMeasureRecordDao.getInstance(this).
                getNotSubmitedConMeaRecord();
        // 获取所有上传的连续测量记录
        ArrayList<Long> recordList = UploadMeasureRecordDao.getInstance(this).getAllUploadRecord();

        // 如果上传数据库中有处于重新上传状态的数据，则将其重新上传
        for (long timeStamp : recordList) {
            if (UploadMeasureRecordDao.getInstance(this).getOnRecordState(timeStamp) == UploadDataState.UPLOADAGINE.getState()) {
                if (isFulled())
                    UploadMeasureRecordDao.getInstance(this).updateOneRecordState(
                            timeStamp, UploadDataState.WAITUPLOAD.getState());
                else {
                    UploadMeasureRecordDao.getInstance(this).updateOneRecordState(
                            timeStamp, UploadDataState.UPLOADING.getState());
                    handler.obtainMessage(HandleUploadData.DATABASECHANGED).sendToTarget();
                }
            }
        }

        // 如果有处于未上传状态的连续测量记录，则将其放入上传记录中
        for (ContinueMeasureRecord record : conArray) {
            if (!recordList.contains(record.getTimeStamp())) {
                if (isFulled()) {
                    UploadMeasureRecordDao.getInstance(this).addOneConMeasureRecord(
                            record.getTimeStamp(), UploadDataState.WAITUPLOAD.getState());
                } else {
                    UploadMeasureRecordDao.getInstance(this).addOneConMeasureRecord(
                            record.getTimeStamp(), UploadDataState.UPLOADING.getState());
                    handler.obtainMessage(HandleUploadData.DATABASECHANGED).sendToTarget();
                }
            }
        }

    }

    /**
     * 初始化Adapter的数据
     */
    private void initAdapterData() {
        // 获取所有没有全部提交的连续测量记录
        ArrayList<ContinueMeasureRecord> conArray = ContinueMeasureRecordDao.getInstance(this).
                getNotSubmitedConMeaRecord();
        // 获取所有上传的连续测量记录
        ArrayList<Long> recordList = UploadMeasureRecordDao.getInstance(this).getAllUploadRecord();

//        for (ContinueMeasureRecord record : conArray)
//            ContinueMeasureRecordDao.getInstance(this).updateContinueMeasureRecordDataSize(record.getTimeStamp());
//        ArrayList<ContinueMeasureRecord> list = ContinueMeasureRecordDao.getInstance(this).
//                getNotSubmitedConMeaRecord();
//        for (ContinueMeasureRecord record : conArray)
//            System.out.println("record.toString():。。。。。。。" + record.toString());
        arrayList.clear();
        if (conArray.size() > 0) {
            int currentYm = conArray.get(0).getMeasureYM();
            uploadDataBean = new UploadDataBean();
            uploadDataBean.setIsYearAndMonth(true);
            uploadDataBean.setYear((currentYm >> 8 & 0xff) + 1970);
            uploadDataBean.setMonth(currentYm & 0xff);
            uploadDataBean.setTimeStamp(-1);
            arrayList.add(uploadDataBean);
            for (ContinueMeasureRecord record : conArray) {
                if (currentYm != record.getMeasureYM()) {
                    currentYm = record.getMeasureYM();
                    uploadDataBean = new UploadDataBean();
                    uploadDataBean.setIsYearAndMonth(true);
                    uploadDataBean.setYear((currentYm >> 8 & 0xff) + 1970);
                    uploadDataBean.setMonth(currentYm & 0xff);
                    uploadDataBean.setTimeStamp(-1);
                    arrayList.add(uploadDataBean);
                }
                uploadDataBean = new UploadDataBean();
                uploadDataBean.setIsYearAndMonth(false);
                uploadDataBean.setYear((currentYm >> 8 & 0xff) + 1970);
                uploadDataBean.setMonth(currentYm & 0xff);
                uploadDataBean.setDayOfMonth(record.getStartDay());
                uploadDataBean.setDataSize(record.getDataSize());
                uploadDataBean.setUploadDataSize(record.getSubmitedSize());
                uploadDataBean.setTimeStamp(record.getTimeStamp());
                uploadDataBean.setTimeLength("25:01:02");
                if (recordList.contains(record.getTimeStamp()))
                    uploadDataBean.setUploadState(UploadMeasureRecordDao.getInstance(this).
                            getOnRecordState(record.getTimeStamp()));
                else
                    uploadDataBean.setUploadState(UploadDataState.NOTUPLOAD);
                uploadDataBean.setTimeSpace(getTimeLabel(record.getStartTime(), record.getEndMD(),
                        record.getEndTime()));
                arrayList.add(uploadDataBean);
            }
        }
    }

    /**
     * 上传完一份连续测量记录
     *
     * @param timeStamp
     */
    private void uploadOverOneRecord(long timeStamp) {
        // 更新这条连续测量记录的总数据以及上传数据大小
        ContinueMeasureRecordDao.getInstance(this).updateContinueMeasureRecordDataSize(timeStamp);
        // 如果上传记录中有这条记录同时其状态为上传完毕，则删除这条记录
        if (UploadMeasureRecordDao.getInstance(this).judgeHasConMeasureRecord(timeStamp)) {
            if (UploadMeasureRecordDao.getInstance(this).getOnRecordState(timeStamp) ==
                    UploadDataState.UPLOADOVER.getState())
                UploadMeasureRecordDao.getInstance(this).deleteOneRecord(timeStamp);
        }
        if (!isFulled()) {
            // 获取下一条等待上传的数据
            long nextWait = UploadMeasureRecordDao.getInstance(this).getNextWaitRecord();
            // 如果存在下一条等待上传的数据
            if (nextWait != -1l) {
                UploadMeasureRecordDao.getInstance(this).updateOneRecordState(nextWait,
                        UploadDataState.UPLOADING.getState());
                // 向数据库中添加一条上传记录并向处理上传操作的线程发出信号
                handler.obtainMessage(HandleUploadData.DATABASECHANGED).sendToTarget();
            }
        }
        // 重新初始化Adapter中的数据
        initAdapterData();
        uploadDataAdapter.notifyDataSetChanged();

    }

    /**
     * 上传完一份文件
     *
     * @param timeStamp
     */

    private void uploadOneFile(long timeStamp) {
        initAdapterData();
        uploadDataAdapter.notifyDataSetChanged();
    }

    /**
     * 获取时间label
     *
     * @param startTime
     * @param endMD
     * @param endTime
     * @return
     */
    private String getTimeLabel(int startTime, int endMD, int endTime) {
        int startHour = startTime >> 16 & 0xff;
        int startMinute = startTime >> 8 & 0xff;
        int startSecond = startTime & 0xff;
        int endM = endMD >> 8 & 0xff;
        int endD = endMD & 0xff;
        int endHour = endTime >> 16 & 0xff;
        int endMinute = endTime >> 8 & 0xff;
        int endSecond = endTime & 0xff;
        String startHourStr = startHour < 10 ? "0" + startHour : startHour + "";
        String startMinuteStr = startMinute < 10 ? "0" + startMinute : startMinute + "";
        String startSecondStr = startSecond < 10 ? "0" + startSecond : startSecond + "";
        String endMStr = endM < 10 ? "0" + endM : endM + "";
        String endDStr = endD < 10 ? "0" + endD : endD + "";
        String endHourStr = endHour < 10 ? "0" + endHour : endHour + "";
        String endMinuteStr = endMinute < 10 ? "0" + endMinute : endMinute + "";
        String endSecondStr = endSecond < 10 ? "0" + endSecond : endSecond + "";
        return startHourStr + ":" + startMinuteStr + ":" + startSecondStr + "~" + endMStr + "月" +
                endDStr + "日" + " " + endHourStr + ":" + endMinuteStr + ":" + endSecondStr;
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClick() {
        // 返回按钮的点击事件
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 是否仅在wifi上传
        switchButtonView.setOnSwitchStateChangeListener(new SwitchButtonView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean state) {
                SharedPreferences.Editor editor = sharePrefer.edit();
                editor.putBoolean(ConstantUtils.ISONLYWIFI, state);
                editor.commit();
                if (mHandleUploadData != null)
                    mHandleUploadData.isONLYWIFI(state);
            }
        });

        // 适配器的监听事件
        uploadDataAdapter.setUploadDataListener(new UploadDataAdapter.UploadDataListener() {
            @Override
            public void allUploadClicked() {
                // 上传所有连续测量记录的命令
                uHandler.obtainMessage(UPLOADALLRECORD).sendToTarget();
            }

            @Override
            public void uploadStateClicked(int position, View view) {
                switch (((UploadDataView) view).getCurrentState()) {
                    case UPLOADING:// 正在上传
                        // 如果当前状态为正在上传，则将状态更改为暂停，并在数据库中将这条记录删除
                        ((UploadDataView) view).setUploadState(UploadDataState.UPLOADPAUSE);
                        UploadMeasureRecordDao.getInstance(UploadDataActivity.this).
                                deleteOneRecord(arrayList.get(position).getTimeStamp());
                        break;
                    case WAITUPLOAD:// 等待上传
                        // 如果当前状态为等待上传，则将状态更改为上传，并在数据库中将这条记录删除
                        ((UploadDataView) view).setUploadState(UploadDataState.NOTUPLOAD);
                        UploadMeasureRecordDao.getInstance(UploadDataActivity.this).
                                deleteOneRecord(arrayList.get(position).getTimeStamp());
                        break;
                    case UPLOADPAUSE:// 暂停上传
                        // 如果当前状态为暂停上传，如果正在上传数量已成最大值则将当前状态更新为等待上传并在数据库中添加一条记录
                        if (isFulled()) {
                            ((UploadDataView) view).setUploadState(UploadDataState.WAITUPLOAD);
                            UploadMeasureRecordDao.getInstance(UploadDataActivity.this).
                                    addOneConMeasureRecord(arrayList.get(position).getTimeStamp(),
                                            UploadDataState.WAITUPLOAD.getState());
                        } else {// 如果当前状态为暂停上传，如果正在上传数量未成最大值则将当前状态更新为正在
                            // 上传并在数据库中添加一条记录，同时发出信号
                            ((UploadDataView) view).setUploadState(UploadDataState.UPLOADING);
                            UploadMeasureRecordDao.getInstance(UploadDataActivity.this).
                                    addOneConMeasureRecord(arrayList.get(position).getTimeStamp(),
                                            UploadDataState.UPLOADING.getState());
                            // 向数据库中添加一条上传记录并向处理上传操作的线程发出信号
                            handler.obtainMessage(HandleUploadData.DATABASECHANGED).sendToTarget();
                        }
                        break;
                    case UPLOADAGINE:// 重新上传
                        // 如果当前状态为重新上传，同时正在上传数量已成最大值则将当前状态更新为等待上传，同时在数据库中更新这条记录的状态
                        if (isFulled()) {
                            ((UploadDataView) view).setUploadState(UploadDataState.WAITUPLOAD);
                            UploadMeasureRecordDao.getInstance(UploadDataActivity.this).
                                    updateOneRecordState(arrayList.get(position).getTimeStamp(),
                                            UploadDataState.WAITUPLOAD.getState());
                        } else {// 如果当前状态为重新上传，同时正在上传数量未成最大值则将当前状态更新为正在上传，
                            // 同时在数据库中更新这条记录的状态，并发出信号
                            ((UploadDataView) view).setUploadState(UploadDataState.UPLOADING);
                            UploadMeasureRecordDao.getInstance(UploadDataActivity.this).
                                    updateOneRecordState(arrayList.get(position).getTimeStamp(),
                                            UploadDataState.UPLOADING.getState());
                            // 向数据库中添加一条上传记录并向处理上传操作的线程发出信号
                            handler.obtainMessage(HandleUploadData.DATABASECHANGED).sendToTarget();
                        }
                        break;
                    case NOTUPLOAD:// 上传
                        // 如果当前状态为上传状态，同时正在上传数量已成最大值，则将状态设置为等待上传，同时在数据库中添加此条记录
                        if (isFulled()) {
                            ((UploadDataView) view).setUploadState(UploadDataState.WAITUPLOAD);
                            UploadMeasureRecordDao.getInstance(UploadDataActivity.this).
                                    addOneConMeasureRecord(arrayList.get(position).getTimeStamp(),
                                            UploadDataState.WAITUPLOAD.getState());
                        } else {// 如果当前状态为上传状态，同时正在上传数量未成最大值，则将状态设置为正在上传，
                            // 同时在数据库中添加此条记录，并发出信号
                            ((UploadDataView) view).setUploadState(UploadDataState.UPLOADING);
                            UploadMeasureRecordDao.getInstance(UploadDataActivity.this).
                                    addOneConMeasureRecord(arrayList.get(position).getTimeStamp(),
                                            UploadDataState.UPLOADING.getState());
                            // 向数据库中添加一条上传记录并向处理上传操作的线程发出信号
                            handler.obtainMessage(HandleUploadData.DATABASECHANGED).sendToTarget();
                        }
                        break;
                }
            }
        });
    }

    /**
     * 判断上传的数据库是否已经满了
     *
     * @return
     */
    private boolean isFulled() {
        UploadMeasureRecordDao uploadMeasureRecordDao = UploadMeasureRecordDao.getInstance(this);
        return uploadMeasureRecordDao.getUploadingCount() >= 3;
    }

    @Override
    protected void onDestroy() {
        // 如果上传数据库中没有上传和等待上传的数据，则退出上传数据的线程
        if (!UploadMeasureRecordDao.getInstance(this).hasUploadingAndWaitRecord()) {
            if (mHandleUploadData != null) {
                mHandleUploadData.setUploadOverOneRecordListener(null);
                mHandleUploadData.out();
            }
            BaseApplication.getInstance().setHandleUploadData(null);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_enter, R.anim.activity_back_exit);
    }
}
