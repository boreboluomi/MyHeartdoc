package com.electrocardio.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.Bean.BluetoothCommunicateClass;
import com.electrocardio.Bean.BluetoothCommunicateListener;
import com.electrocardio.Bean.HandleDisconnectState;
import com.electrocardio.Bean.MeasureBatteryDisplay;
import com.electrocardio.Bean.SixSecondMeasureBean;
import com.electrocardio.R;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.bluetoothdata.BluetoothStateConfirmClass;
import com.electrocardio.custom.elec.EcgWaveView;
import com.electrocardio.custom.elec.MeasureBackground;
import com.electrocardio.custom.elec.MeasureButton;
import com.electrocardio.database.SixtySecondsRecordDao;
import com.electrocardio.jni.CompleteECGResultBean;
import com.electrocardio.popup.AdjustScopePop;
import com.electrocardio.popup.FallOffAlertPop;
import com.electrocardio.popup.SavaDataPop;
import com.electrocardio.util.ConstantUtils;
import com.electrocardio.util.ToastUtils;

/**
 * Created by ZhangBo on 2016/04/29.
 */
public class RowMeasureActivity extends Activity {


    private MeasureBackground MB;// 测量背景
    private TextView tv_range;// 增益label
    private RelativeLayout rl_gain;// 增益按钮背景
    private TextView tv_gain;// 增益按钮
    private ImageView iv_out;// 退出全屏的按钮
    private TextView tv_respRate;// 呼吸率label
    private TextView tv_meaTime;// 测量计时
    private RelativeLayout rl_measureTime;//
    private TextView tv_heartRate;// 心率label
    private RelativeLayout rl_battery;// 电量提醒
    private RelativeLayout rl_dataTrans;// 数据传输提醒

    private MeasureBatteryDisplay batteryDisplay;// 电池电量的显示
    private FallOffAlertPop fallOffAlertPop;// 导联脱落pop
    private SavaDataPop saveDataPop;// 存储数据的pop
    private HandleDisconnectState handleDisconnectState;// 处理断开连接事件

    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;
    private EcgWaveView ecgWaveView;// 绘制ECG波形图
    private BluetoothCommunicateClass blueCommClass;// 蓝牙通信类
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_rowmeasure);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
        if (blueCommClass != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(ConstantUtils.APPNAME, Context.MODE_PRIVATE);
            blueCommClass.setISONLYWIFI(sharedPreferences.getBoolean(ConstantUtils.ISONLYWIFI, false));
        }
    }

    public void initView() {
        TextView Electricity_text = (TextView) findViewById(R.id.Electricity_text);// 电量信息
        ImageView mElectricity = (ImageView) findViewById(R.id.mElectricity);// 电量图片
        TextView tv_chargeState = (TextView) findViewById(R.id.tv_chargeState);// 充电状态
        ImageView checkbox_RA = (ImageView) findViewById(R.id.checkbox_RA);// RA灯
        ImageView checkbox_RL = (ImageView) findViewById(R.id.checkbox_RL);// RL灯
        MB = (MeasureBackground) findViewById(R.id.MB);// 测量背景
        tv_range = (TextView) findViewById(R.id.tv_range);// 增益label
        rl_gain = (RelativeLayout) findViewById(R.id.rl_gain);// 增益按钮背景
        tv_gain = (TextView) findViewById(R.id.tv_gain);// 增益按钮
        iv_out = (ImageView) findViewById(R.id.iv_out);// 退出全屏的按钮
        tv_respRate = (TextView) findViewById(R.id.tv_respRate);// 呼吸率label
        tv_meaTime = (TextView) findViewById(R.id.tv_meaTime);// 测量计时
        rl_measureTime = (RelativeLayout) findViewById(R.id.rl_measureTime);
        tv_heartRate = (TextView) findViewById(R.id.tv_heartRate);// 心率
        rl_battery = (RelativeLayout) findViewById(R.id.rl_battery);// 电量提醒
        rl_dataTrans = (RelativeLayout) findViewById(R.id.rl_dataTrans);// 数据传输提醒
        RelativeLayout batteryAlert = (RelativeLayout) findViewById(R.id.rl_battery);// 设备电量警报
        RelativeLayout mRelayout = (RelativeLayout) findViewById(R.id.root);

        fallOffAlertPop = new FallOffAlertPop(RowMeasureActivity.this);

        ecgWaveView = new EcgWaveView(RowMeasureActivity.this);
        mRelayout.addView(ecgWaveView);
        blueCommClass = BaseApplication.getInstance().getBlueCommuCls();
        MeasureBatteryDisplay display = new MeasureBatteryDisplay(Electricity_text, mElectricity, tv_chargeState);
        display.setFallOff(blueCommClass.isFallOff());
        blueCommClass.setBatteryDisplay(display);
        batteryDisplay = blueCommClass.getBatteryDisplay();
        batteryDisplay.setRAandLA(checkbox_RA, checkbox_RL);
        batteryDisplay.setChargeState(tv_chargeState);
        batteryDisplay.setBatteryAlert(batteryAlert);

        batteryDisplay.setFallOff(blueCommClass.isFallOff());
        batteryDisplay.updataRAandLA(blueCommClass.isFallOff());
        batteryDisplay.setBatteryValAlert(blueCommClass.getBatteryValue());
        setBluetoothCommunicateListener();
    }

    public void initData() {
        fallOffAlertPop = new FallOffAlertPop(RowMeasureActivity.this);// 导联脱落pop
        saveDataPop = new SavaDataPop(RowMeasureActivity.this);// 存储数据的pop
        if (blueCommClass.getHandleDisconnectState() == null) {
            blueCommClass.setHandleDisconnectState(new HandleDisconnectState(RowMeasureActivity.this));
            blueCommClass.getHandleDisconnectState().init(blueCommClass.getAnalysis(), blueCommClass.getAUBlutoothHolder());
        }
        handleDisconnectState = blueCommClass.getHandleDisconnectState();
        handleDisconnectState.initOnMeasure(batteryDisplay, null, null, null, tv_heartRate);
        // 设置状态监听器
        handleDisconnectState.setStateListener(new HandleDisconnectState.StateListener() {
            @Override
            public void SaveSixtySecondsMeasureData() {
                tv_meaTime.setText("00:00:00");
                rl_measureTime.setVisibility(View.INVISIBLE);
                saveSixtySecondsMeasureData();
            }

            @Override
            public void SaveContinueMeasureData() {
                BluetoothStateConfirmClass blueConFirm = new BluetoothStateConfirmClass();
                blueConFirm.setCharging(false);
                blueConFirm.setFallOff(true);
                blueConFirm.setBatteryValue(-1);
                batteryDisplay.updataState(blueConFirm);
                tv_heartRate.setText("- -");// 显示心率的label
                if (BaseApplication.getInstance().getBlueCommuCls() != null) {
                    BaseApplication.getInstance().getBlueCommuCls().setMEASUREBUTTONSTATE(MeasureButton.SCAN);
                    BaseApplication.getInstance().getBlueCommuCls().setIsFallOff(true);
                    batteryDisplay.disConnected();
                }
                tv_meaTime.setText("00:00:00");
                ecgWaveView.reSet();
                rl_measureTime.setVisibility(View.INVISIBLE);
                CompleteECGResultBean resultBean = blueCommClass.getJniCallback().
                        EcgGetStatisitic();
                blueCommClass.getSaveContinueMeasureData().addHeartRateResult(
                        resultBean.getnRNum(), resultBean.getnHRAvg(), resultBean.getnHRMax(),
                        resultBean.getnHRMin(), resultBean.getArynHRPercent());
                blueCommClass.getSaveContinueMeasureData().saveStop();
                rl_dataTrans.setVisibility(View.INVISIBLE);
                rl_battery.setVisibility(View.INVISIBLE);
            }
        });

        ecgWaveView.setGain(blueCommClass.getGain());
        tv_gain.setText("x" + blueCommClass.getGain() + "");
        MB.setRange(blueCommClass.getGain());

        if (blueCommClass.getGain() == 0.25f) {
            tv_range.setText("25mm/s 2.5mm/mv");
            blueCommClass.getAdjustScopePop().setCheckecItem(0);
        } else if (blueCommClass.getGain() == 0.5f) {
            tv_range.setText("25mm/s 5mm/mv");
            blueCommClass.getAdjustScopePop().setCheckecItem(1);
        } else if (blueCommClass.getGain() == 1.0f) {
            tv_range.setText("25mm/s 10mm/mv");
            blueCommClass.getAdjustScopePop().setCheckecItem(2);
        } else {
            tv_range.setText("25mm/s 20mm/mv");
            blueCommClass.getAdjustScopePop().setCheckecItem(3);
        }
        setOnButtonClickListener();
    }

    /**
     * 存储60秒测量的数据
     */
    private void saveSixtySecondsMeasureData() {
        if (blueCommClass.isSIXTYSECONDSMEASURE()) {
            if ((System.currentTimeMillis() - blueCommClass.getStartMeaTime()) > 15000) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveDataPop.showAtLocation(tv_gain, Gravity.CENTER, 0, 0);
                    }
                });
                // outPut();
                blueCommClass.getSaveSixtySecondsData().saveStop();
            }
        }
        blueCommClass.setStartMeaTime(-1);
    }

    /**
     * 设置蓝牙通信的监听
     */
    private void setBluetoothCommunicateListener() {
        blueCommClass.setBluetoothCommunicateListener(new BluetoothCommunicateListener() {
            @Override
            public void disConnected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (blueCommClass.isDISCONNECTCALLBACK()) {
                            ecgWaveView.reSet();
                            tv_meaTime.setText("00:00:00");
                        }
                        if (BluetoothAdapter.getDefaultAdapter().isEnabled() == false) {
                            ecgWaveView.reSet();
                            // 结束本次测量
                            if (blueCommClass.isSIXTYSECONDSMEASURE())
                                saveSixtySecondsMeasureData();
                            else {
                                CompleteECGResultBean resultBean = blueCommClass.getJniCallback().
                                        EcgGetStatisitic();
                                blueCommClass.getSaveContinueMeasureData().addHeartRateResult(
                                        resultBean.getnRNum(), resultBean.getnHRAvg(), resultBean.getnHRMax(),
                                        resultBean.getnHRMin(), resultBean.getArynHRPercent());
                                blueCommClass.getSaveContinueMeasureData().saveStop();
                            }
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    rl_dataTrans.setVisibility(View.INVISIBLE);
                                    rl_battery.setVisibility(View.INVISIBLE);
                                }
                            }, 1000);

                            // 设置为手动断开
                            handleDisconnectState.disConnected(true,
                                    blueCommClass.isSIXTYSECONDSMEASURE());

                        } else
                            handleDisconnectState.disConnected(blueCommClass.isDISCONNECTCALLBACK(),
                                    blueCommClass.isSIXTYSECONDSMEASURE());
                    }
                });
            }

            @Override
            public void stateReady() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleDisconnectState.reConnected();
                    }
                });
            }

            @Override
            public void reBluetoothState(BluetoothStateConfirmClass blueStateConfirm) {
                setBluetoothState(blueStateConfirm);
            }

            @Override
            public void measureCommandCallBack(boolean measureComm) {
                handleMeasureCommandCallBack(measureComm);
            }

            @Override
            public void disConnectCallBack() {

            }

            @Override
            public void lostPackageRate(int rate) {
                handleLostPackageData(rate);
            }

            @Override
            public void measureTime(String timeLabel) {
                tv_meaTime.setText(timeLabel);
            }

            @Override
            public void calculatedMeasreData(float[] dataArray) {
                handleReEcgWaveData(dataArray);
            }

            @Override
            public void heartRate(int rate) {
                tv_heartRate.setText(rate + "");
            }

            @Override
            public void saveOver() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.ToastMessage(RowMeasureActivity.this, "保存完毕");
                        SixSecondMeasureBean sixSecondMeasureBean = SixtySecondsRecordDao.
                                getInstance(RowMeasureActivity.this).getRecentHeartRateRecord();
                        if (saveDataPop.isShowing())
                            saveDataPop.dismiss();
                        if (sixSecondMeasureBean != null) {
                            Intent intent = new Intent(RowMeasureActivity.this, SixtySecondsMeaReaultActivity.class);
                            intent.putExtra("timeStamp", sixSecondMeasureBean.getTimeStamp());
                            intent.putExtra("dataFileSrc", sixSecondMeasureBean.getDataFileSrc());
                            intent.putExtra("timeSpace", sixSecondMeasureBean.getTimeSpace());
                            startActivity(intent);
                            overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                        }
                    }
                });
            }
        });
    }

    /**
     * 处理测量回复指令
     *
     * @param measureComm
     */
    private void handleMeasureCommandCallBack(boolean measureComm) {
        if (measureComm) {
            if (!tv_meaTime.getText().toString().trim().equals("00:00:00"))
                return;
            blueCommClass.setStartMeaTime(System.currentTimeMillis());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (blueCommClass.isSIXTYSECONDSMEASURE())
                        ToastUtils.ToastMessage(RowMeasureActivity.this, "60秒测量");
                    else
                        ToastUtils.ToastMessage(RowMeasureActivity.this, "连续测量");
                    blueCommClass.setMEASUREBUTTONSTATE(MeasureButton.STOP);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    blueCommClass.setMEASUREBUTTONSTATE(MeasureButton.START);
                    tv_meaTime.setText("00:00:00");
                    ecgWaveView.reSet();
                    rl_measureTime.setVisibility(View.INVISIBLE);
                }
            });
            if (blueCommClass.isSIXTYSECONDSMEASURE())
                // outPut();
                saveSixtySecondsMeasureData();
            else {
                CompleteECGResultBean resultBean = blueCommClass.getJniCallback().
                        EcgGetStatisitic();
                blueCommClass.getSaveContinueMeasureData().addHeartRateResult(
                        resultBean.getnRNum(), resultBean.getnHRAvg(), resultBean.getnHRMax(),
                        resultBean.getnHRMin(), resultBean.getArynHRPercent());
                blueCommClass.getSaveContinueMeasureData().saveStop();
            }
        }
    }

    /**
     * 设置导联是否脱落
     *
     * @param bluetoothState
     */
    private void setBluetoothState(BluetoothStateConfirmClass bluetoothState) {
        batteryDisplay.updataState(bluetoothState);
    }

    /**
     * 处理丢包率数据
     *
     * @param rate
     */
    private void handleLostPackageData(int rate) {
        float ratef = (1 - rate / 250.0f) * 100;
        if (ratef > 5)
            rl_dataTrans.setVisibility(View.VISIBLE);
        else
            rl_dataTrans.setVisibility(View.INVISIBLE);
    }

    /**
     * 处理接收到ecg数据
     *
     * @param dataArray
     */
    private void handleReEcgWaveData(final float[] dataArray) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ecgWaveView.setDrawArray(dataArray);
            }
        });
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClickListener() {
        // 脱落警告pop的消失监听器
        fallOffAlertPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                batteryDisplay.setFallOff(false);
            }
        });
        // 设置脱落状态改变的事件监听器
        batteryDisplay.setFallOffChangeListener(new MeasureBatteryDisplay.FallOffChangeListener() {
            @Override
            public void fallOffChanged(boolean fallOff) {
                if (fallOff) {
                    if (blueCommClass.getMEASUREBUTTONSTATE() == MeasureButton.STOP) {
                        if (!fallOffAlertPop.isShowing()) {
                            fallOffAlertPop.showAtLocation(tv_meaTime, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                        }
                    } else {
                        if (fallOffAlertPop.isShowing())
                            fallOffAlertPop.dismiss();
                    }
                } else {
                    if (fallOffAlertPop.isShowing())
                        fallOffAlertPop.dismiss();
                }
            }
        });
        // 退出全屏按钮的点击事件
        iv_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 增益按钮的点击事件
        rl_gain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blueCommClass.getAdjustScopePop().showAsDropDown(rl_gain, (int) TypedValue.applyDimension(TypedValue.
                        COMPLEX_UNIT_DIP, -166, getResources().getDisplayMetrics()), (int)
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -86, getResources().
                                getDisplayMetrics()));
            }
        });

        // 增益pop的选择事件
        blueCommClass.getAdjustScopePop().setSelectListener(new AdjustScopePop.SelectListener() {
            @Override
            public void onSelectedItem(int position) {
                switch (position) {
                    case 0:
                        ecgWaveView.setGain(0.25f);
                        tv_gain.setText("x0.25");
                        tv_range.setText("25mm/s 2.5mm/mv");
                        MB.setRange(0.25f);
                        blueCommClass.setGain(0.25f);
                        break;
                    case 1:
                        ecgWaveView.setGain(0.5f);
                        tv_gain.setText("x0.5");
                        tv_range.setText("25mm/s 5mm/mv");
                        MB.setRange(0.5f);
                        blueCommClass.setGain(0.5f);
                        break;
                    case 2:
                        ecgWaveView.setGain(1.0f);
                        tv_gain.setText("x1.0");
                        tv_range.setText("25mm/s 10mm/mv");
                        MB.setRange(1f);
                        blueCommClass.setGain(1.0f);
                        break;
                    case 3:
                        ecgWaveView.setGain(2.0f);
                        tv_gain.setText("x2.0");
                        tv_range.setText("25mm/s 20mm/mv");
                        MB.setRange(2f);
                        blueCommClass.setGain(2.0f);
                        break;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    @Override
    public void onBackPressed() {
        blueCommClass.setBatteryValue(batteryDisplay.getBattery());
        blueCommClass.setIsFallOff(batteryDisplay.isFallOff());
        super.onBackPressed();
    }
}
