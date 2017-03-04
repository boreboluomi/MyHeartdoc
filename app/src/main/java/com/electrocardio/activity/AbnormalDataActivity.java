package com.electrocardio.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.electrocardio.custom.elec.SectionMeasureDataView;
import com.electrocardio.popup.AdjustScopePop;
import com.electrocardio.util.ThreadUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ZhangBo on 2016/04/08.
 */
public class AbnormalDataActivity extends Activity {

    private ImageView ivBack;// 返回按钮
    private TextView tvTimeLabel;// 时间标签
    private TextView tvTimeLength;// 时间长度
    private TextView abNormalInfor;// 异常情况描述
    private TextView tv_gain;// 增益按钮
    private HorizontalScrollView HSL;// 水平ScrollView
    private RelativeLayout rl_gain;// 增益背景
    private SectionMeasureDataView sectionMeasureDataView;// 片段数据展示
    private AdjustScopePop adjustScopePop;// 调整增益的pop

    private int moveX = 0;// 移动的横坐标
    private int currentX = 0;// 当前横坐标

    private long timeStamp = -1;// 时间戳
    private String abnormalDec = "";// 异常描述
    private String filePath = "";// 文件路径
    private ArrayList<Float> dataArray;
    private GainStandardView gainSV;// 1mv对照尺
    private TextView tv_range;// 增益显示器
    private CustomProgressBarWithLabel customProgressBarWithLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_abnormaldata);

        initView();

        setOnButtonClick();

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        timeStamp = intent.getLongExtra("timeStamp", -1);
        abnormalDec = intent.getStringExtra("abnormalDes");
        filePath = intent.getStringExtra("filePath");
        if (timeStamp != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeStamp);
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
            tvTimeLabel.setText(monthStr + "/" + dayStr + " " + hourStr + ":" + minuteStr + ":" + secondStr);
        }

        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                getDataFromFile();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sectionMeasureDataView.setDataList(dataArray);
                    }
                });
            }
        });

        customProgressBarWithLabel.setTimeStamp(timeStamp, 20000);
        sectionMeasureDataView.setTimeStamp(timeStamp, 20000);
        sectionMeasureDataView.setSecondNumber(20);

        customProgressBarWithLabel.setProgressRatioListener(new CustomProgressBarWithLabel.ProgressRatioListener() {
            @Override
            public void currentRatio(int ration) {
                HSL.smoothScrollTo((sectionMeasureDataView.getMeasuredWidth() - HSL.getMeasuredWidth())
                        * ration / 100, 0);
            }
        });

        tvTimeLength.setText("00:00:20");
        abNormalInfor.setText("疑似症状 " + abnormalDec);
    }

    /**
     * 从文件中获取数据
     */
    private void getDataFromFile() {
        dataArray = new ArrayList<>();
        File dataFile = new File(filePath);
        if (dataFile.exists()) {
            InputStream input = null;
            try {
                input = new FileInputStream(dataFile);
                InputStreamReader reader = new InputStreamReader(input);
                BufferedReader bufReader = new BufferedReader(reader);
                String line;
                while ((line = bufReader.readLine()) != null) {
                    for (String str : line.split(","))
                        dataArray.add(Float.parseFloat(str));
                }
                bufReader.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (input != null)
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClick() {
        // 返回按钮的点击事件
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // 调节增益按钮的点击事件
        tv_gain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustScopePop.showAsDropDown(rl_gain, (int) TypedValue.applyDimension(TypedValue.
                        COMPLEX_UNIT_DIP, -166, getResources().getDisplayMetrics()), (int)
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -86, getResources().
                                getDisplayMetrics()));
            }
        });
        // 调节增益的选择框
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
                        moveX += (currentX - x);
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
        customProgressBarWithLabel.setRatio(moveX * 100 / (sectionMeasureDataView.getMeasuredWidth()
                - HSL.getMeasuredWidth()));
    }

    /**
     * 初始化界面
     */
    private void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTimeLabel = (TextView) findViewById(R.id.tv_timeLabel);
        tvTimeLength = (TextView) findViewById(R.id.tv_timeLength);
        abNormalInfor = (TextView) findViewById(R.id.tv_abNormalInfor);
        sectionMeasureDataView = (SectionMeasureDataView) findViewById(R.id.sectionDataDis);
        HSL = (HorizontalScrollView) findViewById(R.id.HSL);
        tv_gain = (TextView) findViewById(R.id.tv_gain);
        gainSV = (GainStandardView) findViewById(R.id.gainSV);
        tv_range = (TextView) findViewById(R.id.tv_range);
        rl_gain = (RelativeLayout) findViewById(R.id.rl_gain);
        customProgressBarWithLabel = (CustomProgressBarWithLabel) findViewById(R.id.CPBWL);
        adjustScopePop = new AdjustScopePop(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_enter, R.anim.activity_back_exit);
    }
}
