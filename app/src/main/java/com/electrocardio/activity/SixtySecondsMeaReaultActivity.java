package com.electrocardio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.custom.elec.CustomProgressBarWithLabel;
import com.electrocardio.custom.elec.GainStandardView;
import com.electrocardio.custom.elec.HeartAndBreathRingLabelView;
import com.electrocardio.custom.elec.SectionMeasureDataView;
import com.electrocardio.database.SixtySecondsBreathRateDao;
import com.electrocardio.database.SixtySecondsHeartRateDao;
import com.electrocardio.databasebean.SixtySecondsBreathRate;
import com.electrocardio.databasebean.SixtySecondsHeartRate;
import com.electrocardio.popup.AdjustScopePop;
import com.electrocardio.util.ThreadUtils;
import com.electrocardio.util.ToastUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/3/16.
 */
public class SixtySecondsMeaReaultActivity extends FragmentActivity {

    private ImageView iv_back;// 返回按钮
    private ImageView iv_share;// 分享按钮
    private TextView tv_timeSpace;// 测量时间段
    private HeartAndBreathRingLabelView heartRingLabel;// 显示心率的环形图
    private TextView tv_averageRate;// 平均心率
    private TextView tv_fastRate;// 最快心率
    private TextView tv_slowestRate;// 最慢心率
    private HeartAndBreathRingLabelView breathRingLabel;// 显示呼吸率的环形图
    private TextView tv_aveHeartBeat;// 平均呼吸率
    private ArrayList<Float> arrayList;

    private int moveX = 0;// 移动的横坐标
    private int currentX = 0;// 当前横坐标

    private GainStandardView gainSV;// 标尺
    private TextView tv_range;
    private TextView tv_gain;// 增益按钮
    private RelativeLayout rl_gain;// 增益背景
    private HorizontalScrollView HSL;// 横向滚动条
    private SectionMeasureDataView sectionMeasureDataView;// 片段数据展示
    private AdjustScopePop adjustScopePop;// 调整增益的pop
    private CustomProgressBarWithLabel CPBWL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sixtysecondsmearesult);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        tv_timeSpace = (TextView) findViewById(R.id.tv_timeSpace);
        heartRingLabel = (HeartAndBreathRingLabelView) findViewById(R.id.heartRingLabel);// 显示心率的环形图
        tv_averageRate = (TextView) findViewById(R.id.tv_averageRate);// 平均心率
        tv_fastRate = (TextView) findViewById(R.id.tv_fastRate);// 最快心率
        tv_slowestRate = (TextView) findViewById(R.id.tv_slowestRate);// 最慢心率
        breathRingLabel = (HeartAndBreathRingLabelView) findViewById(R.id.breathRingLabel);// 显示呼吸率的环形图
        tv_aveHeartBeat = (TextView) findViewById(R.id.tv_averageHeartBeat);// 平均呼吸率

        gainSV = (GainStandardView) findViewById(R.id.gainSV);
        tv_range = (TextView) findViewById(R.id.tv_range);
        tv_gain = (TextView) findViewById(R.id.tv_gain);// 增益按钮
        rl_gain = (RelativeLayout) findViewById(R.id.rl_gain);// 增益背景
        HSL = (HorizontalScrollView) findViewById(R.id.HSL);// 横向滚动条
        sectionMeasureDataView = (SectionMeasureDataView) findViewById(R.id.sectionDataDis);// 片段数据展示
        CPBWL = (CustomProgressBarWithLabel) findViewById(R.id.CPBWL);
        adjustScopePop = new AdjustScopePop(this);// 调整增益的pop

        initData();

        setOnButtonClickListener();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        long time = intent.getLongExtra("timeStamp", -1l);
        final String fileSrc = intent.getStringExtra("dataFileSrc");
        tv_timeSpace.setText(intent.getCharSequenceExtra("timeSpace"));
        if (time != -1) {
            SixtySecondsHeartRate sixtySecondsHeartRate = SixtySecondsHeartRateDao.getInstance(this).
                    getSixtySecondsHeartRate(time);
            SixtySecondsBreathRate sixtySecondsBreathRate = SixtySecondsBreathRateDao.
                    getInstance(this).getOneSixtySecondsBreathRate(time);

            heartRingLabel.setRate(sixtySecondsHeartRate.getRateArray());
            tv_averageRate.setText(sixtySecondsHeartRate.getAverageHeartRate() + "");
            tv_fastRate.setText(sixtySecondsHeartRate.getFastestHeartRate() + "");
            tv_slowestRate.setText(sixtySecondsHeartRate.getSlowestHeartRate() + "");

            breathRingLabel.setRate(sixtySecondsBreathRate.getRateArray());
            tv_aveHeartBeat.setText(sixtySecondsBreathRate.getAverageBreathRate() + "");
        }
        sectionMeasureDataView.setTimeStamp(time, 60000);
        CPBWL.setTimeStamp(time, 60000);

        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                getDataFromFile(fileSrc);
            }
        });
    }

    /**
     * 从文件中获取数据
     *
     * @param fileSrc
     */
    private void getDataFromFile(String fileSrc) {
        arrayList = new ArrayList<>();
        File file = new File(fileSrc);
        if (!file.exists())
            return;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String string;
            while ((string = reader.readLine()) != null) {
                for (String str : string.split("\t"))
                    arrayList.add(Float.parseFloat(str));
            }
            reader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sectionMeasureDataView.setDataList(arrayList);
            }
        });
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClickListener() {
        // 返回按钮的点击事件
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // 分享按钮的点击事件
        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.ToastMessage(SixtySecondsMeaReaultActivity.this, "尚未开发");
            }
        });
        // 增益按钮的点击事件
        tv_gain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustScopePop.showAsDropDown(rl_gain, (int) TypedValue.applyDimension(TypedValue.
                        COMPLEX_UNIT_DIP, -166, getResources().getDisplayMetrics()), (int)
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -86, getResources().
                                getDisplayMetrics()));
            }
        });
        // 增益框的选取事件
        adjustScopePop.setSelectListener(new AdjustScopePop.SelectListener() {
            @Override
            public void onSelectedItem(int position) {
                switch (position) {
                    case 0:
                        sectionMeasureDataView.setGainRatio(0.25f);
                        gainSV.setGain(0.25f);
                        tv_gain.setText("x0.25");
                        tv_range.setText("25mm/s 2.5mm/mv");
                        break;
                    case 1:
                        sectionMeasureDataView.setGainRatio(0.5f);
                        gainSV.setGain(0.5f);
                        tv_gain.setText("x0.5");
                        tv_range.setText("25mm/s 5mm/mv");
                        break;
                    case 2:
                        sectionMeasureDataView.setGainRatio(1f);
                        gainSV.setGain(1f);
                        tv_gain.setText("x1.0");
                        tv_range.setText("25mm/s 10mm/mv");
                        break;
                    case 3:
                        sectionMeasureDataView.setGainRatio(2f);
                        gainSV.setGain(2f);
                        tv_gain.setText("x2.0");
                        tv_range.setText("25mm/s 20mm/mv");
                        break;
                }
            }
        });
        // 展示数据的控件
        CPBWL.setProgressRatioListener(new CustomProgressBarWithLabel.ProgressRatioListener() {
            @Override
            public void currentRatio(int ration) {
                HSL.smoothScrollTo((sectionMeasureDataView.getMeasuredWidth() - HSL.getMeasuredWidth()) * ration / 100, 0);
            }
        });

        // 横向滚动条的触摸事件
        HSL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentX = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x = (int) event.getX();
                        moveX += (currentX - x) * 3;
                        if (moveX > (sectionMeasureDataView.getMeasuredWidth() - HSL.getMeasuredWidth()))
                            moveX = sectionMeasureDataView.getMeasuredWidth() - HSL.getMeasuredWidth();
                        if (moveX < 0)
                            moveX = 0;
                        currentX = x;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                calculateCurrentRatio();
                return true;
            }
        });
    }

    /**
     * 计算进度比
     */
    private void calculateCurrentRatio() {
        HSL.smoothScrollTo(moveX, 0);
        CPBWL.setRatio(moveX * 100 / (sectionMeasureDataView.getMeasuredWidth() - HSL.getMeasuredWidth()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_enter, R.anim.activity_back_exit);
    }
}
