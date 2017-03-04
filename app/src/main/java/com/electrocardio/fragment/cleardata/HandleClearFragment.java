package com.electrocardio.fragment.cleardata;

import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.Adapter.HandClearAdapter;
import com.electrocardio.Bean.HandClearBean;
import com.electrocardio.R;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.database.AbnormalListDao;
import com.electrocardio.database.ContinueMeasureRecordDao;
import com.electrocardio.databasebean.AbnormalListBean;
import com.electrocardio.databasebean.ContinueMeasureRecord;
import com.electrocardio.popup.SavaDataPop;
import com.electrocardio.util.DeleteFileUtil;
import com.electrocardio.util.ThreadUtils;
import com.electrocardio.util.ToastUtils;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/2/24.
 */
public class HandleClearFragment extends BaseFragment {

    private ImageView iv_titleLeft;// 返回图片的点击事件
    private TextView tv_title;// 标题
    private TextView tv_right;// 全选按钮
    private RelativeLayout rl_clear;// 清理按钮
    private TextView tv_clear;// 清理数量
    private BackClickListener mBackClickListener;// 返回按钮的点击事件
    private ArrayList<HandClearBean> arrayList = new ArrayList<>();// 存储数据的集合
    private HandClearBean handClearBean;
    private ListView listView;// 列表
    private int checkedCount = 0;// 选中的数量
    private HandClearAdapter handClearAdapter;// 适配器
    private final int CLEARECLICKED = 1;// 清理按钮点击了的信号
    private final int DELETEFILE = 2;// 删除文件信号
    private final int DISMISSPOP = 3;// 关闭pop的信号
    private boolean STARTCLEAR = false;// 开始清理的标记
    private boolean TIMEOVER = false;// 时间已到
    private SavaDataPop savaDataPop;// 保存数据的pop

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLEARECLICKED:
                    clearButtonClicked();
                    break;
                case DELETEFILE:
                    String path = "";
                    path = (String) msg.obj;
                    if (!path.equals(""))
                        deleteFile(path);
                    break;
                case DISMISSPOP:
                    break;
            }
        }
    };

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_handclear, null);
        iv_titleLeft = (ImageView) view.findViewById(R.id.iv_titleLeft);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_right = (TextView) view.findViewById(R.id.tv_titleRight);
        rl_clear = (RelativeLayout) view.findViewById(R.id.rl_clear);
        listView = (ListView) view.findViewById(R.id.listView);
        tv_clear = (TextView) view.findViewById(R.id.tv_clear);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        tv_title.setText("手动清理");
        tv_right.setVisibility(View.VISIBLE);

        handClearAdapter = new HandClearAdapter(mActivity);
        // 获取Adapter所要显示的数据
        obtainAdapterData();
        // 为适配器设置数据集合
        handClearAdapter.setDataList(arrayList);
        listView.setAdapter(handClearAdapter);
        // 清理按钮
        rl_clear.setClickable(true);
        rl_clear.setEnabled(true);
        // 全选按钮
        tv_right.setClickable(true);
        tv_right.setEnabled(true);

        setOnButtonClick();
    }

    /**
     * 获取Adapter所要显示的数据
     */
    private void obtainAdapterData() {
        arrayList.clear();
        ArrayList<ContinueMeasureRecord> recordList = ContinueMeasureRecordDao.getInstance(mActivity).
                getContinueMeasureRecord();
        if (recordList.size() > 0) {
            int measureYM = recordList.get(0).getMeasureYM();
            handClearBean = new HandClearBean();
            handClearBean.setIsYearMonth(true);
            handClearBean.setYear((measureYM >> 8 & 0xff) + 1970);
            handClearBean.setMonth(measureYM & 0xff);
            arrayList.add(handClearBean);
            for (ContinueMeasureRecord record : recordList) {
                if (measureYM != record.getMeasureYM()) {
                    measureYM = record.getMeasureYM();
                    handClearBean = new HandClearBean();
                    handClearBean.setIsYearMonth(true);
                    handClearBean.setYear((measureYM >> 8 & 0xff) + 1970);
                    handClearBean.setMonth(measureYM & 0xff);
                    arrayList.add(handClearBean);
                } else {
                    handClearBean = new HandClearBean();
                    handClearBean.setIsYearMonth(false);
                    handClearBean.setYear((measureYM >> 8 & 0xff) + 1970);
                    handClearBean.setMonth(measureYM & 0xff);
                    handClearBean.setDayOfMonth(record.getStartDay());
                    handClearBean.setDataSize(record.getDataSize());
                    handClearBean.setSubmitState(record.getDataSize() <= record.getSubmitedSize());
                    handClearBean.setTimeSpace(getTimeLabel(record.getStartTime(), record.getEndMD(), record.getEndTime()));
                    handClearBean.setTimeLength(getTimeLengthLabel(record.getTimeLength()));
                    handClearBean.setTimeStamp(record.getTimeStamp());
                    arrayList.add(handClearBean);
                }
            }
        }
    }

    /**
     * 清理按钮点击了
     */
    private void clearButtonClicked() {
        if (arrayList.size() > 0) {
            for (HandClearBean bean : arrayList) {
                if (!bean.isYearMonth())
                    if (bean.isChecked()) {
                        ArrayList<AbnormalListBean> beanList = AbnormalListDao.getInstance(mActivity).
                                getAllSubmitedAbnormalListBean(bean.getTimeStamp());
                        String path = "";
                        if (beanList.size() > 0) {
                            path = beanList.get(0).getDataFileSrc().substring(0, beanList.get(0).getDataFileSrc().lastIndexOf("/"));
                            path = path.substring(0, path.lastIndexOf("/"));
                        }
                        ContinueMeasureRecordDao.getInstance(mActivity).deleteOneContinueMeasureRecord(bean.getTimeStamp());
                        mHandler.obtainMessage(DELETEFILE, path).sendToTarget();
                    }
            }
        }
        tv_clear.setText("清理");
        // 重新获取Adapter要显示的数据
        obtainAdapterData();
        handClearAdapter.notifyDataSetInvalidated();
        // 清理按钮
        rl_clear.setClickable(true);
        rl_clear.setEnabled(true);
        // 全选按钮
        tv_right.setClickable(true);
        tv_right.setEnabled(true);
        STARTCLEAR = false;
        clearOver();
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    private void deleteFile(final String filePath) {
        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                new DeleteFileUtil().deleteDirectory(filePath);
            }
        });
    }

    /**
     * 获取时间长度的lebel
     *
     * @param timeLength
     * @return
     */
    private String getTimeLengthLabel(long timeLength) {
        int hourLength = (int) (timeLength / 3600000);
        timeLength %= 3600000;
        int minuteLength = (int) (timeLength / 60000);
        timeLength %= 60000;
        int secondLength = (int) (timeLength / 1000);
        String hourStr = hourLength < 10 ? "0" + hourLength : hourLength + "";
        String minuteStr = minuteLength < 10 ? "0" + minuteLength : minuteLength + "";
        String secondStr = secondLength < 10 ? "0" + secondLength : secondLength + "";
        return hourStr + ":" + minuteStr + ":" + secondStr;
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
        iv_titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBackClickListener != null)
                    mBackClickListener.onBackClicked();
            }
        });
        // 全选按钮的点击事件
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handAllChooseButtonClick();
            }
        });
        // 清理按钮的点击事件
        rl_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedCount == 0) {
                    ToastUtils.ToastMessage(mActivity, "无清理项");
                    return;
                }
                STARTCLEAR = true;
                TIMEOVER = false;
                // 清理按钮
                rl_clear.setClickable(false);
                rl_clear.setEnabled(false);
                // 全选按钮
                tv_right.setClickable(false);
                tv_right.setEnabled(false);
                if (savaDataPop == null) {
                    savaDataPop = new SavaDataPop(mActivity);
                    savaDataPop.setAlertContent("正在清理数据...");
                }
                savaDataPop.showAtLocation(tv_right, Gravity.CENTER, 0, 0);
                mHandler.obtainMessage(CLEARECLICKED).sendToTarget();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TIMEOVER = true;
                        clearOver();
                    }
                }, 3000);
            }
        });
        // 适配器的选择事件
        handClearAdapter.setOnCheckedListener(new HandClearAdapter.OnCheckedListener() {
            @Override
            public void onChecked(int position, boolean isChecked) {
                arrayList.get(position).setChecked(isChecked);
                checkedCount = 0;
                for (HandClearBean data : arrayList) {
                    if (!data.isYearMonth())
                        if (data.isChecked())
                            checkedCount++;
                }
                if (checkedCount > 0)
                    tv_clear.setText("清理(" + checkedCount + ")");
                else
                    tv_clear.setText("清理");
            }
        });

    }

    /**
     * 清理完毕
     */
    private void clearOver() {
        if (TIMEOVER && !STARTCLEAR) {
            TIMEOVER = false;
            if (savaDataPop != null && savaDataPop.isShowing()) {
                savaDataPop.dismiss();
            }
        }
    }

    /**
     * 处理全选按钮的点击事件
     */
    private void handAllChooseButtonClick() {
        if (tv_right.getText().toString().trim().equals("全选")) {
            tv_right.setText("全不选");
            for (HandClearBean data : arrayList) {
                if (!data.isYearMonth())
                    data.setChecked(true);
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handClearAdapter.notifyDataSetInvalidated();
                }
            });
        } else {
            tv_right.setText("全选");
            for (HandClearBean data : arrayList) {
                if (!data.isYearMonth())
                    data.setChecked(false);
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handClearAdapter.notifyDataSetInvalidated();
                }
            });
        }
    }

    /**
     * 返回按钮的点击事件
     *
     * @param backClickListener
     */
    public void setOnBackClickListener(BackClickListener backClickListener) {
        mBackClickListener = backClickListener;
    }

    public interface BackClickListener {
        public void onBackClicked();
    }
}
