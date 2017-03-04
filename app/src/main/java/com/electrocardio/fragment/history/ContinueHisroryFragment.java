package com.electrocardio.fragment.history;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.activity.ContinueMeasureRasultActivity;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.custom.elec.HeartAndBreathRingLabelView;
import com.electrocardio.custom.elec.HistorySlideCheckLayout;
import com.electrocardio.database.ContinueHeartRateDao;
import com.electrocardio.databasebean.ContinueHeartRate;
import com.electrocardio.util.ThreadUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ZhangBo on 2016/3/15.
 */
public class ContinueHisroryFragment extends BaseFragment {

    private ImageView iv_back;// 返回按钮
    private TextView tv_timeSpace;// 时间间隔
    private TextView tv_timeLength;// 时长
    private ImageView iv_abnormalList;// 异常列表按钮
    private ContinueHeartRate continueHeartRate;// 连续测量的心率
    private HeartAndBreathRingLabelView heartRingLabel;// 心率情况环图
    private TextView tv_averageRate;// 平均心率
    private TextView tv_fastRate;// 最快心率
    private TextView tv_slowestRate;// 最慢心率
    private TextView tv_heartBeat;// 心搏总数
    private HeartAndBreathRingLabelView breathRingLabel;// 呼吸率情况环图
    private TextView tv_averageHeartBeat;// 平均呼吸率
    private ArrayList<Integer> dataArray;// 心率数据集合
    private AbnormalListClickListener mAbnormalListClickListener;

    private HistorySlideCheckLayout historySlideCheckLayout;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_continumearesult, null);
        iv_back = (ImageView) view.findViewById(R.id.iv_back);// 返回按钮
        tv_timeSpace = (TextView) view.findViewById(R.id.tv_timeSpace);// 时间间隔
        tv_timeLength = (TextView) view.findViewById(R.id.tv_timeLength);// 时长
        iv_abnormalList = (ImageView) view.findViewById(R.id.iv_unusalList);// 异常列表按钮
        heartRingLabel = (HeartAndBreathRingLabelView) view.findViewById(R.id.heartRingLabel);// 心率情况环图
        tv_averageRate = (TextView) view.findViewById(R.id.tv_averageRate);// 平均心率
        tv_fastRate = (TextView) view.findViewById(R.id.tv_fastRate);// 最快心率
        tv_slowestRate = (TextView) view.findViewById(R.id.tv_slowestRate);// 最慢心率
        tv_heartBeat = (TextView) view.findViewById(R.id.tv_heartBeat);// 心搏总数
        breathRingLabel = (HeartAndBreathRingLabelView) view.findViewById(R.id.breathRingLabel);// 呼吸率情况环图
        tv_averageHeartBeat = (TextView) view.findViewById(R.id.tv_averageHeartBeat);// 平均呼吸率
        historySlideCheckLayout = (HistorySlideCheckLayout) view.findViewById(R.id.bpm_HSCL);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        long timeStamp = ((ContinueMeasureRasultActivity) mActivity).getTimeStamp();
        long timeLength = ((ContinueMeasureRasultActivity) mActivity).getTimeLength();
        historySlideCheckLayout.setTimeStampLabel(timeStamp, timeLength);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        int startHour = calendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = calendar.get(Calendar.MINUTE);
        int startSecond = calendar.get(Calendar.SECOND);
        String startHourStr = startHour < 10 ? "0" + startHour : startHour + "";
        String startMinuteStr = startMinute < 10 ? "0" + startMinute : startMinute + "";
        String startSecondStr = startSecond < 10 ? "0" + startSecond : startSecond + "";
        calendar.setTimeInMillis(timeStamp + timeLength);
        int endHour = calendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = calendar.get(Calendar.MINUTE);
        int endSecond = calendar.get(Calendar.SECOND);
        String endHourStr = endHour < 10 ? "0" + endHour : endHour + "";
        String endMinuteStr = endMinute < 10 ? "0" + endMinute : endMinute + "";
        String endSecondStr = endSecond < 10 ? "0" + endSecond : endSecond + "";
        tv_timeSpace.setText(startHourStr + ":" + startMinuteStr + ":" + startSecondStr + "~" +
                endHourStr + ":" + endMinuteStr + ":" + endSecondStr);
        int hourLength = (int) (timeLength / 3600000);
        timeLength %= 3600000;
        int minuteLength = (int) (timeLength / 60000);
        timeLength %= 60000;
        int secondLength = (int) (timeLength / 1000);
        String hourStr = hourLength < 10 ? "0" + hourLength : hourLength + "";
        String minuteStr = minuteLength < 10 ? "0" + minuteLength : minuteLength + "";
        String secondStr = secondLength < 10 ? "0" + secondLength : secondLength + "";
        tv_timeLength.setText("时长 " + hourStr + ":" + minuteStr + ":" + secondStr);

        continueHeartRate = ContinueHeartRateDao.getInstance(mActivity).getOneContinueHeartRateRecord(timeStamp);

        heartRingLabel.setRate(continueHeartRate.getRateArray());
        tv_averageRate.setText(continueHeartRate.getAverageHeartRate() + "");// 平均心率
        tv_fastRate.setText(continueHeartRate.getFastestHeartRate() + "");// 最快心率
        tv_slowestRate.setText(continueHeartRate.getSlowestHeartRate() + "");// 最慢心率
        tv_heartBeat.setText(continueHeartRate.getBeatCount() + "");// 心搏总数
        dataArray = new ArrayList<>();

        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                getHeartRateData(continueHeartRate.getDataFileSrc());
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        historySlideCheckLayout.setDataArray(dataArray);
                    }
                });
            }
        });

        setOnButtonClickListener();
    }

    /**
     * 获取心率数据
     *
     * @param filePath
     */
    private void getHeartRateData(String filePath) {
        File dataFile = new File(filePath);
        if (!dataFile.exists())
            return;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(dataFile);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                for (String str : line.split("\t"))
                    dataArray.add(Integer.parseInt(str));
            }
            bufferedReader.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClickListener() {
        // 返回按钮的点击事件
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
        // 异常列表按钮的点击事件
        iv_abnormalList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAbnormalListClickListener != null)
                    mAbnormalListClickListener.abnormalListClicked();
            }
        });
    }

    public void setAbnormalListClickListener(AbnormalListClickListener abnormalListClickListener) {
        mAbnormalListClickListener = abnormalListClickListener;
    }

    public interface AbnormalListClickListener {
        void abnormalListClicked();
    }

}
