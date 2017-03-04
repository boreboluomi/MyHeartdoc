package com.electrocardio.fragment.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.Bean.AccessNetWork;
import com.electrocardio.Bean.BluetoothCommunicateClass;
import com.electrocardio.Bean.BluetoothCommunicateListener;
import com.electrocardio.Bean.HandleDisconnectState;
import com.electrocardio.Bean.MeasureBatteryDisplay;
import com.electrocardio.Bean.SixSecondMeasureBean;
import com.electrocardio.Bean.UserInformation;
import com.electrocardio.R;
import com.electrocardio.activity.BluetoothScanActivity;
import com.electrocardio.activity.DistoryContentActivity;
import com.electrocardio.activity.MeasureActivity;
import com.electrocardio.activity.RowMeasureActivity;
import com.electrocardio.activity.SixtySecondsMeaReaultActivity;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.bluetoothdata.BluetoothCommand;
import com.electrocardio.bluetoothdata.BluetoothStateConfirmClass;
import com.electrocardio.custom.elec.CircleImageView;
import com.electrocardio.custom.elec.EcgWaveView;
import com.electrocardio.custom.elec.MeasureBackground;
import com.electrocardio.custom.elec.MeasureButton;
import com.electrocardio.database.ContinueMeasureRecordDao;
import com.electrocardio.database.SixtySecondsRecordDao;
import com.electrocardio.database.UserDao;
import com.electrocardio.databasebean.AbnormalDataCacheBean;
import com.electrocardio.databasebean.SaveContinueMeasureData;
import com.electrocardio.databasebean.SaveSixtySecondsMeasureData;
import com.electrocardio.jni.CompleteECGResultBean;
import com.electrocardio.popup.AdjustScopePop;
import com.electrocardio.popup.DisConnectPop;
import com.electrocardio.popup.FallOffAlertPop;
import com.electrocardio.popup.SavaDataPop;
import com.electrocardio.popup.SlideFromBottomPopup;
import com.electrocardio.popup.StopMeasureAlertPop;
import com.electrocardio.reside.ResideMenu;
import com.electrocardio.util.ConstantUtils;
import com.electrocardio.util.DeleteFileUtil;
import com.electrocardio.util.SaveLosePackageRateUtil;
import com.electrocardio.util.ThreadUtils;
import com.electrocardio.util.ToastUtils;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

/**
 * Created by ZhangBo on 2016/3/2.
 */
public class MeasureFragment extends BaseFragment {
    private final int Request = 1001;
    private final int SCAN_BLUETOOTH_DEVICE = 1;// 浏览蓝牙设备
    private MeasureBatteryDisplay batteryDisplay;// 电池电量的显示
    private EcgWaveView ecgWaveView;// 绘制ECG波形图

    private CircleImageView head;// 头像
    private ImageView mHistory;// 历史记录按钮
    private TextView heartRate;// 显示心率的label
    private TextView tvDisconnect;//断开连接
    private ImageView mDropDown;// 下拉箭头
    private TextView lostPacRate;// 丢包率
    private TextView mvCorrect;// 1mv校准
    private MeasureButton measureBtn;// 测量按钮
    private SlideFromBottomPopup chooseMeasureMode;// 选择测量模式
    private TextView tv_space;// 剩余空间提示
    private ProgressBar pbProgress;// 进度条
    private TextView tv_range;// 幅度

    private RelativeLayout rl_measureTime;// 存放测量时长的ViewGroup
    private TextView tv_meaTime;// 测量时长
    private RelativeLayout rl_dataTransAlert;// 数据传输警报
    private RelativeLayout rl_gain;// 调节增益的Relativelayout
    private TextView tv_gain;// 显示增益的label

    private BluetoothCommunicateClass blueCommClass;// 蓝牙通信类
    private DecimalFormat decimalFormat = new DecimalFormat("##0.00");
    private DisConnectPop disConnectPop;// 断开连接的pop
    private FallOffAlertPop fallOffAlertPop;// 导联脱落pop
    private StopMeasureAlertPop stopMeasureAlertPop;// 停止测量的pop
    private SavaDataPop saveDataPop;// 存储数据的pop
    private HandleDisconnectState handleDisconnectState;// 处理断开连接事件
    private MeasureBackground MB;// 测量背景
    private ImageView iv_full;// 全屏按钮

    private TextView mElectricitytext;// 电池电量文字显示
    private ImageView mElectricity;// 电池图片
    private TextView chargeState;// 电池电量
    private ImageView mcheckBoxRa;// RA状态
    private ImageView mCheckBoxRl;// RL状态
    private RelativeLayout batteryAlert;// 电池电量警报

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCAN_BLUETOOTH_DEVICE:
                    scanBluetoothDevice();
                    break;
            }
        }
    };

    @Override
    public View initView() {

        View view = View.inflate(getActivity(), R.layout.fragment_measure, null);
        head = (CircleImageView) view.findViewById(R.id.title_bar_left_menu);
        mHistory = (ImageView) view.findViewById(R.id.title_bar_right_menu);//历史记录
        tvDisconnect = (TextView) view.findViewById(R.id.ClearAllButtonPressed);//断开连接
        lostPacRate = (TextView) view.findViewById(R.id.tv_lostPackageRate);//丢包率
        mDropDown = (ImageView) view.findViewById(R.id.ClearImageView);
        iv_full = (ImageView) view.findViewById(R.id.iv_full);

        mElectricitytext = (TextView) view.findViewById(R.id.Electricity_text);// 电量文字
        mElectricity = (ImageView) view.findViewById(R.id.mElectricity);//电量图片
        chargeState = (TextView) view.findViewById(R.id.tv_chargeState);// 充电状态
        mcheckBoxRa = (ImageView) view.findViewById(R.id.checkbox_RA);// RA灯
        mCheckBoxRl = (ImageView) view.findViewById(R.id.checkbox_RL);// LA灯
        batteryAlert = (RelativeLayout) view.findViewById(R.id.rl_battery);// 设备电量警报

        heartRate = (TextView) view.findViewById(R.id.tv_heartRate);// 显示心率的label
        RelativeLayout mRelayout = (RelativeLayout) view.findViewById(R.id.root);

        mvCorrect = (TextView) view.findViewById(R.id.btn_Correct);
        rl_measureTime = (RelativeLayout) view.findViewById(R.id.rl_measureTime);// 存放测量时长的ViewGroup
        tv_meaTime = (TextView) view.findViewById(R.id.tv_meaTime);// 测量时长
        rl_dataTransAlert = (RelativeLayout) view.findViewById(R.id.rl_dataTrans);// 数据传输警报
        rl_gain = (RelativeLayout) view.findViewById(R.id.rl_gain);// 调节增益的button
        tv_gain = (TextView) view.findViewById(R.id.tv_gain);// 显示当前增益的textView
        tv_space = (TextView) view.findViewById(R.id.tv_space);// 显示剩余空间的view
        pbProgress = (ProgressBar) view.findViewById(R.id.pbProgress);// 显示剩余空间的进度条
        tv_range = (TextView) view.findViewById(R.id.tv_range);// 幅度显示
        MB = (MeasureBackground) view.findViewById(R.id.MB);// 测量背景

        measureBtn = (MeasureButton) view.findViewById(R.id.measureBtn);// 测量按钮
        ecgWaveView = new EcgWaveView(getContext());
        disConnectPop = new DisConnectPop(getContext());
        fallOffAlertPop = new FallOffAlertPop(getContext());
        stopMeasureAlertPop = new StopMeasureAlertPop(getContext());
        saveDataPop = new SavaDataPop(getContext());

        mRelayout.addView(ecgWaveView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (BaseApplication.getInstance().getBlueCommuCls() == null) {
            BaseApplication.getInstance().setBlueCommuCls(new BluetoothCommunicateClass(BaseApplication.getInstance()));
        }
        blueCommClass = BaseApplication.getInstance().getBlueCommuCls();

        MeasureBatteryDisplay display = new MeasureBatteryDisplay(mElectricitytext, mElectricity, chargeState);
        display.setFallOff(blueCommClass.isFallOff());
        blueCommClass.setBatteryDisplay(display);
        batteryDisplay = blueCommClass.getBatteryDisplay();
        batteryDisplay.setRAandLA(mcheckBoxRa, mCheckBoxRl);
        batteryDisplay.setChargeState(chargeState);
        batteryDisplay.setBatteryAlert(batteryAlert);

        batteryDisplay.setFallOff(blueCommClass.isFallOff());
        batteryDisplay.updataRAandLA(blueCommClass.isFallOff());
        batteryDisplay.setBatteryValAlert(blueCommClass.getBatteryValue());

        setStopMeasureAlertListener();

        if (blueCommClass.getAUBlutoothHolder().connectedDevice() != null) {
            tvDisconnect.setText(BaseApplication.getInstance().getBluetoothDevice().getName());
            mDropDown.setVisibility(View.VISIBLE);
        }
        measureBtn.setCurrentState(blueCommClass.getMEASUREBUTTONSTATE());


        // 设置加安蓝牙通信的事件监听器
        setBluetoothCommunicateListener();

        if (mActivity.getSharedPreferences(ConstantUtils.APPNAME, Context.MODE_PRIVATE).getBoolean
                (ConstantUtils.DIUBAO, false))
            lostPacRate.setVisibility(View.VISIBLE);
        else
            lostPacRate.setVisibility(View.INVISIBLE);
        if (mActivity.getSharedPreferences(ConstantUtils.APPNAME, Context.MODE_PRIVATE).getBoolean
                (ConstantUtils.ONEMV, false))
            mvCorrect.setVisibility(View.VISIBLE);
        else
            mvCorrect.setVisibility(View.INVISIBLE);
        newInitData();

        float avaiableSize = DeleteFileUtil.getAvaiableSizeByF();
        float usedSize = ContinueMeasureRecordDao.getInstance(mActivity).getAllDataSize();
        String avaiableStr = DeleteFileUtil.getAvaiableSize();
        String usedStr = DeleteFileUtil.getOccupySize(usedSize);
        tv_space.setText("已占用" + usedStr + ",剩余" + avaiableStr + "可用");
        pbProgress.setProgress((int) usedSize * 100 / (int) (avaiableSize + usedSize));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (blueCommClass != null) {
            SharedPreferences sharedPreferences = mActivity.getSharedPreferences(ConstantUtils.APPNAME, Context.MODE_PRIVATE);
            blueCommClass.setISONLYWIFI(sharedPreferences.getBoolean(ConstantUtils.ISONLYWIFI, false));
        }
        UserInformation userInformation = UserDao.getInstance(getActivity()).getUser();
        String logoSrc = userInformation.getLogoSrc();
        System.out.println("logoSrc:" + logoSrc);
        if (!logoSrc.equals("")) {
            Bitmap photo = BitmapFactory.decodeFile(logoSrc);
            head.setImageBitmap(photo);
        } else {
            if (UserDao.getInstance(getActivity()).getUser().getSEX() == 2) {
                head.setImageResource(R.mipmap.woman);
            } else {
                head.setImageResource(R.mipmap.man);
            }
        }
    }

    @Override
    public void initData() {
    }

    private void newInitData() {
        if (blueCommClass.getHandleDisconnectState() == null) {
            blueCommClass.setHandleDisconnectState(new HandleDisconnectState(mActivity));
            blueCommClass.getHandleDisconnectState().init(blueCommClass.getAnalysis(),
                    blueCommClass.getAUBlutoothHolder());
        }
        handleDisconnectState = blueCommClass.getHandleDisconnectState();
        handleDisconnectState.initOnMeasure(batteryDisplay, measureBtn, mDropDown, tvDisconnect, heartRate);
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
                measureBtn.setCurrentState(MeasureButton.SCAN);
                mDropDown.setVisibility(View.INVISIBLE);
                tvDisconnect.setText("无连接设备");
                BluetoothStateConfirmClass blueConFirm = new BluetoothStateConfirmClass();
                blueConFirm.setCharging(false);
                blueConFirm.setFallOff(true);
                blueConFirm.setBatteryValue(-1);
                batteryDisplay.updataState(blueConFirm);
                heartRate.setText("- -");// 显示心率的label
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
                rl_dataTransAlert.setVisibility(View.INVISIBLE);
                batteryAlert.setVisibility(View.INVISIBLE);
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
//        stardandGain = (int) (8388607 / 2420 / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM,
//                10, getResources().getDisplayMetrics()));
        // 设置按钮的点击事件
        setOnClickListener();
    }

    /**
     * 设置蓝牙通信的监听
     */
    private void setBluetoothCommunicateListener() {
        blueCommClass.setBluetoothCommunicateListener(
                new BluetoothCommunicateListener() {
                    @Override
                    public void disConnected() {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (blueCommClass.isDISCONNECTCALLBACK()) {
                                    ecgWaveView.reSet();
                                    tv_meaTime.setText("00:00:00");
                                    rl_measureTime.setVisibility(View.INVISIBLE);
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
                                    myHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            rl_dataTransAlert.setVisibility(View.INVISIBLE);
                                            batteryAlert.setVisibility(View.INVISIBLE);
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
                        mActivity.runOnUiThread(new Runnable() {
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
                        if (rl_measureTime.getVisibility() == View.INVISIBLE)
                            rl_measureTime.setVisibility(View.VISIBLE);
                        tv_meaTime.setText(timeLabel);
                    }

                    @Override
                    public void calculatedMeasreData(float[] dataArray) {
                        handleReEcgWaveData(dataArray);
                    }

                    @Override
                    public void heartRate(int rate) {
                        heartRate.setText(rate + "");
                    }

                    @Override
                    public void saveOver() {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.ToastMessage(mActivity, "保存完毕");
                                SixSecondMeasureBean sixSecondMeasureBean = SixtySecondsRecordDao.
                                        getInstance(mActivity).getRecentHeartRateRecord();
                                if (saveDataPop.isShowing())
                                    saveDataPop.dismiss();
                                if (sixSecondMeasureBean != null) {
                                    Intent intent = new Intent(mActivity, SixtySecondsMeaReaultActivity.class);
                                    intent.putExtra("timeStamp", sixSecondMeasureBean.getTimeStamp());
                                    intent.putExtra("dataFileSrc", sixSecondMeasureBean.getDataFileSrc());
                                    intent.putExtra("timeSpace", sixSecondMeasureBean.getTimeSpace());
                                    startActivity(intent);
                                    getActivity().overridePendingTransition(R.anim.activity_enter,
                                            R.anim.activity_exit);
                                }
                            }
                        });
                    }
                });
    }

    /**
     * 处理丢包率数据
     *
     * @param rate
     */
    private void handleLostPackageData(int rate) {
        float ratef = (1 - rate / 250.0f) * 100;
        lostPacRate.setText("丢包率：" + decimalFormat.format(ratef) + "%");
        SaveLosePackageRateUtil.getInstance().addNumber(decimalFormat.format(ratef));
        if (ratef > 5) {
            rl_dataTransAlert.setVisibility(View.VISIBLE);
        } else
            rl_dataTransAlert.setVisibility(View.INVISIBLE);
    }

    /**
     * 处理接收到ecg数据
     *
     * @param dataArray
     */
    private void handleReEcgWaveData(final float[] dataArray) {
        if (measureBtn.getCurrentBtnState() != MeasureButton.STOP)
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    measureBtn.setCurrentState(MeasureButton.STOP);
                    blueCommClass.setMEASUREBUTTONSTATE(MeasureButton.STOP);
                }
            });
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ecgWaveView.setDrawArray(dataArray);
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
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (blueCommClass.isSIXTYSECONDSMEASURE())
                        ToastUtils.ToastMessage(mActivity, "60秒测量");
                    else
                        ToastUtils.ToastMessage(mActivity, "连续测量");
                    measureBtn.setCurrentState(MeasureButton.STOP);
                    blueCommClass.setMEASUREBUTTONSTATE(MeasureButton.STOP);
                    if (!blueCommClass.isSIXTYSECONDSMEASURE()) {
                        blueCommClass.getTimeRunnable().setTimeStamp(blueCommClass.getStartMeaTime());
                        blueCommClass.handleTimeCut(blueCommClass.getTimeRunnable());
                    }
                }
            });
        } else {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    measureBtn.setCurrentState(MeasureButton.START);
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
     * 存储60秒测量的数据
     */
    private void saveSixtySecondsMeasureData() {
        if (blueCommClass.isSIXTYSECONDSMEASURE()) {
            if ((System.currentTimeMillis() - blueCommClass.getStartMeaTime()) > 15000) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveDataPop.showAtLocation(measureBtn, Gravity.CENTER, 0, 0);
                    }
                });
                // outPut();
                blueCommClass.getSaveSixtySecondsData().saveStop();
            }
        }
        blueCommClass.setStartMeaTime(-1);
    }

    /**
     * 设置停止测量警报确认事件的监听器
     */
    private void setStopMeasureAlertListener() {
        stopMeasureAlertPop.setConfirmClickedListener(new StopMeasureAlertPop.ConfirmClickedListener() {
            @Override
            public void onConfirmClicked() {
                blueCommClass.getAUBlutoothHolder().sendData(BluetoothCommand.getSEStopMeasure());
                stopMeasureAlertPop.dismiss();
            }
        });
    }

    /**
     * 显示停止测量警告的pop
     */
    private void ShowStopMeasureAlertPop() {
        if (blueCommClass.isSIXTYSECONDSMEASURE()) {
            if ((System.currentTimeMillis() - blueCommClass.getStartMeaTime()) < 15000)
                stopMeasureAlertPop.setAlertContent("测量时间过短,数据不会保存,是否停止本次测量");
            else
                stopMeasureAlertPop.setAlertContent("是否停止本次测量");
        } else {
            stopMeasureAlertPop.setAlertContent("是否停止本次测量");
        }
        stopMeasureAlertPop.show();
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
     * 设置按钮的点击事件
     */
    private void setOnClickListener() {

        // 头像的点击事件
       head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MeasureActivity) mActivity).getResideMenu().openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        // 脱落警告pop的消失监听器
        fallOffAlertPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                batteryDisplay.setFallOff(false);
            }
        });
        // 调节增益按钮的点击事件
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
        // 1mv校准按钮的点击事件
        mvCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mvFlag = true;
                blueCommClass.getAUBlutoothHolder().sendData(BluetoothCommand.getSEStart1mV());
//                myHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        outPut();
//                    }
//                }, 20000);
            }
        });
        // 测量按钮的点击事件
        measureBtn.setClickedListener(new MeasureButton.ClickedListener() {
            @Override
            public void scanClicked() {
                myHandler.obtainMessage(SCAN_BLUETOOTH_DEVICE).sendToTarget();
            }

            @Override
            public void startClicked() {
                if (batteryDisplay.isFallOff())
                    ToastUtils.ToastMessage(mActivity, "导联脱落,矫正后再测量");
                blueCommClass.setMeasureInit(false);
                tv_meaTime.setText("00:00:00");
                showChooseMeasureModePop();
            }

            @Override
            public void stopClicked() {
                ShowStopMeasureAlertPop();
            }
        });
        // 设置脱落状态改变的事件监听器
        batteryDisplay.setFallOffChangeListener(new MeasureBatteryDisplay.FallOffChangeListener() {
            @Override
            public void fallOffChanged(boolean fallOff) {
                if (fallOff) {
                    if (measureBtn.getCurrentBtnState() == MeasureButton.STOP) {
                        if (!fallOffAlertPop.isShowing()) {
                            fallOffAlertPop.showAtLocation(measureBtn, Gravity.CENTER_HORIZONTAL |
                                    Gravity.BOTTOM, 0, 0);
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
        // 历史记录按钮的点击事件
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, DistoryContentActivity.class));
                getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        // 断开连接pop的点击事件
        disConnectPop.setClickedListener(new DisConnectPop.ClickedListener() {
            @Override
            public void onClickedListener() {
                if (blueCommClass.getMEASUREBUTTONSTATE() == MeasureButton.STOP) {
                    ToastUtils.ToastMessage(mActivity, "请先停止当前测量");
                    return;
                }
                blueCommClass.setDISCONNECTCALLBACK(true);
                blueCommClass.getAUBlutoothHolder().disconnect();
            }
        });
        // 断开连接按钮的点击事件
        tvDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tvDisconnect.getText().equals("无连接设备")) {
                    disConnectPop.showAsDropDown(tvDisconnect, (int) TypedValue.applyDimension
                            (TypedValue.COMPLEX_UNIT_DIP, -5, getResources().getDisplayMetrics()), 0);
                }
            }
        });
        // 全屏按钮的点击事件
        iv_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blueCommClass.setBatteryValue(batteryDisplay.getBattery());
                blueCommClass.setIsFallOff(batteryDisplay.isFallOff());
                Intent intent = new Intent(mActivity, RowMeasureActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 显示选择状态的PopupWindow
     */
    private void showChooseMeasureModePop() {
        if (chooseMeasureMode == null)
            chooseMeasureMode = new SlideFromBottomPopup(mActivity);
        chooseMeasureMode.setOnConfirmListener(new SlideFromBottomPopup.OnConfirmClickListener() {
            @Override
            public void OnClickConfirm(int STATE) {
                chooseMeasureMode.dismiss();
                blueCommClass.setStartMeaTime(System.currentTimeMillis());
                if (blueCommClass.getAnalysis() != null)
                    blueCommClass.getAnalysis().reset();
                blueCommClass.setDISCONNECTCALLBACK(false);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rl_measureTime.setVisibility(View.VISIBLE);
                        tv_meaTime.setText("00:00:00");
                        ecgWaveView.reSet();
                    }
                });
                if (STATE == 1001) {
                    SixtySecondMeasureStart();
                }
                if (STATE == 1002) {
                    ContinueMeasureStart();
                }
                blueCommClass.setMeasureInit(false);
            }

            @Override
            public void OnClickCancel() {
                chooseMeasureMode.dismiss();
            }
        });
        chooseMeasureMode.showPopupWindow();
    }

    /**
     * 60秒测量开始
     */
    private void SixtySecondMeasureStart() {
        blueCommClass.setISSIXTYSECONDSMEASURE(true);
        if (blueCommClass.getSaveSixtySecondsData() == null)
            blueCommClass.setSaveSixtySecondsData(new SaveSixtySecondsMeasureData(mActivity));
        blueCommClass.getSaveSixtySecondsData().saveStart();
        blueCommClass.getAUBlutoothHolder().sendData(BluetoothCommand.getSEStartMeasure());
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!((System.currentTimeMillis() - blueCommClass.getStartMeaTime()) < 59500) && blueCommClass.
                        getStartMeaTime() != -1) {
                    blueCommClass.getAUBlutoothHolder().sendData(BluetoothCommand.getSEStopMeasure());
                }
            }
        }, 60000);
    }

    /**
     * 连续测量开始了
     */
    private void ContinueMeasureStart() {
        if (blueCommClass.getSaveContinueMeasureData() == null)
            blueCommClass.setSaveContinueMeasureData(new SaveContinueMeasureData(
                    new WeakReference<>(mActivity)) {
                @Override
                public void oneAbnormalInfoSaved(final AbnormalDataCacheBean abnormalDataCacheBean) {
                    ConnectivityManager manager = (ConnectivityManager) mActivity.getSystemService(
                            Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeInfo = manager.getActiveNetworkInfo();
                    if (activeInfo == null)
                        return;
                    if (blueCommClass.getISONLYWIFI() && activeInfo.getType() != 1)
                        return;
                    ThreadUtils.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            new AccessNetWork(BaseApplication.getInstance()).uploadFile(abnormalDataCacheBean);
                        }
                    });
                }
            });
        blueCommClass.getSaveContinueMeasureData().saveStart();
        blueCommClass.setISSIXTYSECONDSMEASURE(false);
        blueCommClass.getAUBlutoothHolder().sendData(BluetoothCommand.getSEStartMeasure());
    }

    private void outPut() {
        if (ThreadUtils.getInstance().getExecutorService() != null) {
            ThreadUtils.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    writeDataToFile();
                }
            });
        }
    }

    /**
     * 将数据写入到文件中
     */
    private void writeDataToFile() {
//        String dataFileName = System.currentTimeMillis() + "heartdoc.txt";
//        String path = Environment.getExternalStorageDirectory() + "/AHeartDoc";
//        File pathFile = new File(path);
//        if (!pathFile.exists()) {
//            pathFile.mkdirs();
//        }
//        File dataFile = new File(path, dataFileName);
//        FileOutputStream fileOutputStream = null;
//
//        try {
//            fileOutputStream = new FileOutputStream(dataFile);
//            for (int data : resourceData)
//                fileOutputStream.write((data + "\n\t").getBytes());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fileOutputStream != null)
//                try {
//                    fileOutputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//        }
//        resourceData.clear();
//        mvFlag = false;
//        mActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ToastUtils.ToastMessage(mActivity, "存储完毕");
//            }
//        });
    }

    /**
     * 开始寻找蓝牙设备
     */
    private void scanBluetoothDevice() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter.isEnabled()) {
            Intent intent = new Intent(mActivity, BluetoothScanActivity.class);
            startActivityForResult(intent, Request);
        } else {
            adapter.enable();
        }
    }

    /**
     * 发送数据
     *
     * @param data
     */
    private void sendData(final byte[] data) {
        if (ThreadUtils.getInstance().getExecutorService() != null)
            ThreadUtils.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    while (count < 3) {
                        blueCommClass.getAUBlutoothHolder().sendData(data);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        count++;
                    }
                }
            });
        else
            ToastUtils.ToastMessage(mActivity, "发送失败");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Request && resultCode == Activity.RESULT_OK) {
            blueCommClass.setBluetoothListener();
            String devAddress = data.getStringExtra(BaseApplication.PERIPHERAL_ID_NAME);
            measureBtn.setCurrentState(MeasureButton.START);
            blueCommClass.setMEASUREBUTTONSTATE(MeasureButton.START);
            mDropDown.setVisibility(View.VISIBLE);
            tvDisconnect.setText(devAddress);
            sendData(BluetoothCommand.getSEBluetoothState());
        }
        if (requestCode == CHomeFragment.Request && resultCode == Activity.RESULT_CANCELED) {
            measureBtn.setCurrentState(MeasureButton.SCAN);
            blueCommClass.setMEASUREBUTTONSTATE(MeasureButton.SCAN);
        }
    }
}
