package com.electrocardio.fragment.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.electrocardio.R;
import com.electrocardio.activity.DistoryContentActivity;
import com.electrocardio.activity.ScanActivity;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.custom.elec.DrawBackground;
import com.electrocardio.custom.elec.ECGDrawView;
import com.electrocardio.custom.elec.Renderer;
import com.electrocardio.fragment.base.AbsBaseFragment;
import com.electrocardio.jni.JniCallbackData;
import com.electrocardio.popup.CommentPopup;
import com.electrocardio.popup.DialogPopup;
import com.electrocardio.popup.MenuPopup;
import com.electrocardio.popup.SlideFromBottomPopup;
import com.electrocardio.shared.STUtil;
import com.electrocardio.terminalIO.TIOManager;
import com.electrocardio.terminalIO.TIOPeripheral;
import com.electrocardio.terminalIO.TIOPeripheralCallback;
import com.electrocardio.util.CountDownTimerUtils;
import com.electrocardio.util.ToastUtils;

import java.util.ArrayList;


/**
 * Created by yangzheng on 2016/1/4.
 */
public class CHomeFragment extends AbsBaseFragment implements TIOPeripheralCallback {
    public static final int Request = 1001;
    private Button scanButton;
    private static final int RSSI_INTERVAL = 1670;
    private TIOPeripheral _peripheral;
    private Runnable _rssiRunnable;
    private Handler _rssiHandler = new Handler();
    private Boolean Start = false;
    private ECGDrawView viewCopy;
    private CheckBox mcheckBoxRa;
    private CheckBox mCheckBoxRl;
    private CheckBox mStartAndStopState;
    private SlideFromBottomPopup mSlideButton;
    private String mContent;
    private int AHR = 0;
    private Boolean INIT = false;
    private JniCallbackData jniCallback = new JniCallbackData();

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mcheckBoxRa.setChecked(true);
                    mCheckBoxRl.setChecked(true);
                    break;
                case 2:
                    mcheckBoxRa.setChecked(false);
                    mCheckBoxRl.setChecked(false);
                    break;
                case 3:
                    int electricity = (Integer) msg.obj;
                    mElectricitytext.setText(electricity + "%");
                    if (electricity >= 80) {
                        mElectricity.setBackgroundResource(R.mipmap.ic_battery_1);
                    } else if (electricity < 80 && electricity > 60) {
                        mElectricity.setBackgroundResource(R.mipmap.ic_battery_2);
                    } else if (electricity <= 60 && electricity > 40) {
                        mElectricity.setBackgroundResource(R.mipmap.ic_battery_3);
                    } else if (electricity <= 40 && electricity > 20) {
                        mElectricity.setBackgroundResource(R.mipmap.ic_battery_4);
                    } else if (electricity <= 20 && electricity > 10) {
                        mElectricity.setBackgroundResource(R.mipmap.ic_battery_5);
                    } else if (electricity <= 10) {
                        mElectricity.setBackgroundResource(R.mipmap.ic_battery_6);
                    }
                    break;
                case 4:
                    int hr = (Integer) msg.obj;
                    mHeartRate.setText(hr + "");

                    break;

                case 5:
                    long Dowmtime = (long) msg.obj;
                    mDownTimer.setText(Dowmtime+"");
                    break;

            }

        }
    };
    private ImageView mElectricity;
    private TextView mElectricitytext;
    private ImageView mTitleButtom;
    private TextView mClearTextView;
    private ImageView mClearView;
    private MenuPopup menuPopup;
    private LinearLayout mFrequency;
    private TextView mHeartRate;
    private RelativeLayout mHomeLoading;
    private LinearLayout mTestduration;
    private Chronometer mDownTimer;
    private DialogPopup dialogPopup;

    @Override
    protected void init(Bundle savedInstanceState, View contentView) {
        setFragmentStatus(FRAGMENT_STATUS_SUCCESS);
        Initialize(contentView);
    }

    private void Initialize(View contentView) {
        mHomeLoading = (RelativeLayout) contentView.findViewById(R.id.homefragment_loading);
        mTitleButtom = (ImageView) contentView.findViewById(R.id.title_bar_right_menu);//历史记录
        mClearTextView = (TextView) contentView.findViewById(R.id.ClearAllButtonPressed);//断开连接
        mClearView = (ImageView) contentView.findViewById(R.id.ClearImageView);
        mElectricitytext = (TextView) contentView.findViewById(R.id.Electricity_text);
        mElectricity = (ImageView) contentView.findViewById(R.id.mElectricity);//电量
        mFrequency = (LinearLayout) contentView.findViewById(R.id.frequency);//波律
        mHeartRate = (TextView) contentView.findViewById(R.id.heart_rate);
        mTestduration = (LinearLayout) contentView.findViewById(R.id.test_duration);
        mDownTimer = (Chronometer) contentView.findViewById(R.id.down_timer);
        RelativeLayout mRelayout = (RelativeLayout) contentView.findViewById(R.id.root);
        mcheckBoxRa = (CheckBox) contentView.findViewById(R.id.checkbox_RA);
        mCheckBoxRl = (CheckBox) contentView.findViewById(R.id.checkbox_RL);
        scanButton = (Button) contentView.findViewById(R.id.scanButton);
        mStartAndStopState = (CheckBox) contentView.findViewById(R.id.start_stop_button);

        Renderer renderer = new Renderer();
        renderer.setSiatShowLabel(true);
        renderer.setSiatLineStep(3);
        renderer.setSiatScrollable(true);
        renderer.getSiatBackgroundColor();
        DrawBackground drawBackground = new DrawBackground(getContext());
        viewCopy = new ECGDrawView(getContext(), renderer);
        mRelayout.addView(drawBackground);
        mRelayout.addView(viewCopy);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ScanActivity.class);
                startActivityForResult(intent, Request);
            }
        });
        mTitleButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DistoryContentActivity.class));
              //  getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        mClearTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!mClearTextView.getText().equals("无连接设备")) {

                    menuPopup = new MenuPopup(getActivity());
                    menuPopup.showPopupWindow(view);
                    menuPopup.setOnMenuListener(new MenuPopup.OnMenuClickListener() {
                        @Override
                        public void OnClickConfirm() {
                            scanButton.setVisibility(View.VISIBLE);
                            mStartAndStopState.setVisibility(View.GONE);
                            mClearView.setVisibility(View.GONE);//无连接设备图片
                            mClearTextView.setText("无连接设备");//无连接设备文本
                            mStartAndStopState.setChecked(false);
                            Start = false;
                            menuPopup.dismiss();
                        }
                    });
                }
            }
        });

        mFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentPopup commentPopup = new CommentPopup(getActivity());
                commentPopup.showPopupWindow(view);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHomeFragment.Request && resultCode == Activity.RESULT_OK) {

            mContent = data.getStringExtra(BaseApplication.PERIPHERAL_ID_NAME);
            scanButton.setVisibility(View.GONE);
            mStartAndStopState.setVisibility(View.VISIBLE);
            mClearView.setVisibility(View.VISIBLE);
            mClearTextView.setText(mContent);
            mStartAndStopState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getContext(),"选择测量方式",Toast.LENGTH_SHORT).show();
                    if (mStartAndStopState.isChecked()) {
                        if (mSlideButton == null) {
                            mSlideButton = new SlideFromBottomPopup(getActivity());
                        }
                        mStartAndStopState.setChecked(false);
                        mSlideButton.showPopupWindow();
                    } else {
                        mStartAndStopState.setChecked(true);
                        dialogPopup = new DialogPopup(getActivity()) /*{
                            @Override
                            protected void cancelButton() {
                                dialogPopup.dismiss();
                            }

                            @Override
                            protected void okButton() {
                                mDownTimer.stop();
                                ToastUtils.ToastMessage(getContext(), "停止测量");
                                onDisconnectButtonPressed();
                                dialogPopup.dismiss();
                            }
                        }*/;
                        dialogPopup.showPopupWindow();

                    }
                    if (mSlideButton != null) {
                        mSlideButton.setOnConfirmListener(new SlideFromBottomPopup.OnConfirmClickListener() {
                            @Override
                            public void OnClickConfirm(int STATE) {
                                Start = false;
                                connectPeripheral(mContent);
                                mStartAndStopState.setChecked(true);
                                mSlideButton.dismiss();
                                mTestduration.setVisibility(View.VISIBLE);
                                if (STATE ==1001) {
                                    ToastUtils.ToastMessage(getContext(), "单次测量");
                                    CountDownTimerUtils countDownTimerUtils = new CountDownTimerUtils(60000, 1000) /*{
                                        @Override
                                        protected void onDownTimerTick(long l) {
                                            long timelong = l / 1000;
                                            mHandler.obtainMessage(5, timelong).sendToTarget();
                                        }

                                        @Override
                                        protected void onDownFinish() {
                                            mStartAndStopState.setChecked(false);
                                            mTestduration.setVisibility(View.GONE);
                                            ToastUtils.ToastMessage(getContext(),"测量完成");

                                        }
                                    }*/;
                                    countDownTimerUtils.start();
                                }
                                if(STATE==1002){
                                    ToastUtils.ToastMessage(getContext(), "连续测量");
                                    mDownTimer.setBase(SystemClock.elapsedRealtime());
                                    // 开始记时
                                    mDownTimer.start();
                                 /*   mDownTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                                        @Override
                                        public void onChronometerTick(Chronometer chronometer) {
                                            System.out.println("chronometer------------"+chronometer);
                                        }
                                    });*/

                                }
                            }

                            @Override
                            public void OnClickCancel() {

                            }
                        });
                    }
                }
            });
        }
        if (requestCode == CHomeFragment.Request && resultCode == Activity.RESULT_CANCELED) {
            scanButton.setVisibility(View.VISIBLE);
            mStartAndStopState.setVisibility(View.GONE);
           // Toast.makeText(getContext(), "取消连接", Toast.LENGTH_SHORT).show();
        }
    }

    private void connectPeripheral(String address) {
        this._peripheral = TIOManager.sharedInstance().findPeripheralByAddress(
                address);
        this._peripheral.setListener(this);
        this._peripheral.connect();
        if (_peripheral.isConnected()) {
            byte[] data = new byte[]{0x51, (byte) 0x81, 0x0d, 0x0a};
            _peripheral.writeUARTData(data);
        }
    }


    private void onDisconnectButtonPressed() {
        byte[] data = new byte[]{0x52, (byte) 0x82, 0x0d, 0x0a};
        _peripheral.writeUARTData(data);
        this._peripheral.disconnect();
        System.out.println("------结束------");
        //  FileUtils.writeFileToSD(output);
    }

    @Override
    public void setFragmentType(int fragmentType) {

    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public void onPause() {
        super.onPause();
        // _peripheral.disconnect();
       // mHomeLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHomeLoading.setVisibility(View.VISIBLE);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHomeLoading.setVisibility(View.GONE);
            }
        },2000);
        System.out.println("onResume");
    }

    @Override
    public void tioPeripheralDidConnect(TIOPeripheral peripheral) {
        this.startRSSITimer();
        if (!this._peripheral.shallBeSaved()) {
            // save if connected for the first time
            this._peripheral.setShallBeSaved(true);
            TIOManager.sharedInstance().savePeripherals();
        }
        if (!Start) {
            byte[] data = new byte[]{0x51, (byte) 0x81, 0x0d, 0x0a};
            _peripheral.writeUARTData(data);
            Start = true;
            System.out.println("------开始------");
        }
    }

    private void startRSSITimer() {
        if (this._rssiRunnable == null) {
            this._rssiRunnable = new Runnable() {
                @Override
                public void run() {
                    CHomeFragment.this._peripheral.readRSSI();
                    CHomeFragment.this._rssiHandler.postDelayed(
                            CHomeFragment.this._rssiRunnable,
                            CHomeFragment.RSSI_INTERVAL);
                }
            };
        }
        this._peripheral.readRSSI();
        this._rssiHandler.postDelayed(this._rssiRunnable,
                CHomeFragment.RSSI_INTERVAL);
    }

    private void stopRSSITimer() {
        this._rssiHandler.removeCallbacks(this._rssiRunnable);
    }

    @Override
    public void tioPeripheralDidFailToConnect(TIOPeripheral peripheral, String errorMessage) {
        if (errorMessage.length() > 0) {
            STUtil.showErrorAlert("Failed to connect with error message: "
                    + errorMessage, getContext());
        }
    }

    @Override
    public void tioPeripheralDidDisconnect(TIOPeripheral peripheral, String errorMessage) {
        this.stopRSSITimer();
    }

    @Override
    public void tioPeripheralDidReceiveUARTData(TIOPeripheral peripheral, byte[] data) {
        receiveData(data);
    }


    @Override
    public void tioPeripheralDidWriteNumberOfUARTBytes(TIOPeripheral peripheral, int bytesWritten) {

    }

    @Override
    public void tioPeripheralUARTWriteBufferEmpty(TIOPeripheral peripheral) {

    }

    @Override
    public void tioPeripheralDidUpdateAdvertisement(TIOPeripheral peripheral) {

    }

    @Override
    public void tioPeripheralDidUpdateRSSI(TIOPeripheral peripheral, int rssi) {

    }

    @Override
    public void tioPeripheralDidUpdateLocalUARTCreditsCount(TIOPeripheral peripheral, int creditsCount) {

    }

    @Override
    public void tioPeripheralDidUpdateRemoteUARTCreditsCount(TIOPeripheral peripheral, int creditsCount) {

    }

    private int state;
    private ArrayList<Byte> dataList = new ArrayList<Byte>();
    private ArrayList<Byte> ECGState = new ArrayList<Byte>();

    private int FirstArray;
    private int SecondArray;
    private int ThirdArray;
    private int mResult = 0;
    private int i = 0;
    private int j = 0;
    private int number = 0;
    private int[] ArrayNum = new int[10];
    private float[] DrawArray = new float[13];
    private Float[] output = new Float[10000];

    //private ArrayList<Float> DrawViewList= new ArrayList<Float>();
    private void receiveData(byte[] array) {

        for (byte data : array) {
            switch (data) {
                case 0x01:
                    // 蓝牙确认状态
                    state = 1;

                    break;
                case 0x02:
                    state = 2;
                    // 解析心电数据
               /*
                    if(j==1){
                        Timestamp now = new Timestamp(System.currentTimeMillis());//获取系统当前时间
                        System.out.println("开始接受数据："+now);
                    }

                    if(j==10000){
                        Timestamp now = new Timestamp(System.currentTimeMillis());
                        System.out.println("已经接受到10000包的数据："+now);
                    }*/
                    if (dataList.size() == 5) {
                        // String bytesToHexString = bytesToHexString(array);
                        InterpretingData();
                    }
                    break;
                case 0x05:
                    // RESP波形数据
                    //System.out.println("得到  0x05 数据");

                    state = 5;
                case 0x07:
                    // 发送命令回复
                    //   System.out.println("得到  0x07 数据");

                    state = 7;
                    break;
                default:
                    addData(data);

                    break;
            }
        }
    }


    public void addData(byte data) {

        switch (state) {
            case 1:
                int BatteryState = data & 0xFF;

                if (BatteryState == 128) {
                    mHandler.obtainMessage(1).sendToTarget();
                } else if (BatteryState == 129) {
                    mHandler.obtainMessage(2).sendToTarget();
                } else {
                    int Electricity = BatteryState & 0x7f;
                    mHandler.obtainMessage(3, Electricity).sendToTarget();
                }

                break;
            case 2:
                dataList.add(data);
                break;
            default:
                break;
        }

    }


    private void InterpretingData() {
        if (dataList.size() != 5) {
            dataList.clear();
            return;
        }
        int FirstData = dataList.get(0) & 1;
        int SecondData = dataList.get(0) & 2;
        int ThirdData = dataList.get(0) & 4;

        if (FirstData == 1) {
            FirstArray = dataList.get(1) & 0xff;
        } else {
            FirstArray = dataList.get(1) & 0x7f;
        }
        if (SecondData == 2) {
            SecondArray = dataList.get(2) & 0xff;
        } else {
            SecondArray = dataList.get(2) & 0x7f;
        }
        if (ThirdData == 4) {
            ThirdArray = dataList.get(3) & 0xff;

        } else {
            ThirdArray = dataList.get(3) & 0x7f;
        }

        Transformation(FirstArray, SecondArray, ThirdArray);


        dataList.clear();
    }

    private void Transformation(int firstArray, int secondArray, int thirdArray) {
        int num = ((firstArray << 16) + (secondArray << 8) + thirdArray);
        if (num > 8388607) {
            mResult = num - 16777216;
        } else {
            mResult = num;
        }
        // mResult /= 26;

        ArrayNum[i] = mResult;
        // System.out.println("---------------:" + ArrayNum[i]);
        AHR++;
        i++;

        if (i == 10) {
           /* OutObjectResultPacket objectFromJni = jniCallback.getObjectFromJni(ArrayNum, 1);
            Float[] ecgData = objectFromJni.getEcgData();*/
            if (!INIT) {
                jniCallback.getInitFromJni();
                INIT = true;
            }

            float[] stringFromJni = jniCallback.getFloatFromJni(ArrayNum);

            for (int z = 0; z < 13; z++) {
                DrawArray[z] = stringFromJni[z] / 26;
                //System.out.println(z+"homeFragment:" + DrawArray[z]);
            }
            //viewCopy.setDrawArray(DrawArray);

            // System.out.println("----------------------------"+j);

            // j++;
            i = 0;
        }
        if (AHR == 250) {
            int i = jniCallback.getHeartRate();
            mHandler.obtainMessage(4, i).sendToTarget();
            AHR = 0;
        }


    }

    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }


}
