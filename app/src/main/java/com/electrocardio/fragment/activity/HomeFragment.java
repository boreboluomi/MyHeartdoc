package com.electrocardio.fragment.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Time;
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
import com.electrocardio.bluetoothdata.BluetoothStateConfirmClass;
import com.electrocardio.bluetoothdata.TerminalBluetooth;
import com.electrocardio.custom.elec.CircleImageView;
import com.electrocardio.custom.elec.DrawBackground;
import com.electrocardio.custom.elec.DrawMainBackground;
import com.electrocardio.custom.elec.ECGDrawView;
import com.electrocardio.custom.elec.Renderer;
import com.electrocardio.fragment.base.AbsBaseFragment;
import com.electrocardio.jni.JniCallbackData;
import com.electrocardio.jni.OutObjectResultPacket;
import com.electrocardio.popup.CommentPopup;
import com.electrocardio.popup.DialogPopup;
import com.electrocardio.popup.MenuPopup;
import com.electrocardio.popup.SlideFromBottomPopup;
import com.electrocardio.reside.ResideMenu;
import com.electrocardio.util.CountDownTimerUtils;
import com.electrocardio.util.FileUtils;
import com.electrocardio.util.ToastUtils;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yangzheng on 2016/1/4.
 * 显示斯图曼蓝牙的主界面
 */


public class HomeFragment extends AbsBaseFragment implements TerminalBluetooth.onDataBlackListener,
        View.OnClickListener, SlideFromBottomPopup.OnConfirmClickListener, DialogPopup.OnDialogListener,
        CountDownTimerUtils.onDownTimerListener,CommentPopup.OnCommentPopupClickListener {
    public static final int Request = 1001;
    private Button scanButton;
    private int Waverate=26;
    private Boolean Start = false;
    private ECGDrawView viewCopy;
    private CheckBox mcheckBoxRa;
    private CheckBox mCheckBoxRl;
    private CheckBox mStartAndStopState;
    private SlideFromBottomPopup mSlideButton;
    private String mContent;
    private int AHR = 0;
    private Boolean INIT = false;
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
    private TerminalBluetooth terminalBluetooth;
    private RelativeLayout mRelayout;
    private Boolean mark = false;
    private int i = 0;
    private int[] ArrayNum = new int[10];
    private float[] DrawArray = new float[13];
    private float[] InputFile = new float[15000];
    private List mlist=new ArrayList<Integer>();
    private final int HEART_RATE = 2;// 心率
    private final int BLUETOOTH_STATE = 3;//
    private JniCallbackData jniCallback = new JniCallbackData();
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case HEART_RATE:
                    mHeartRate.setText((int) msg.obj + "");
                    break;
                case BLUETOOTH_STATE:
                    Boolean state= (Boolean) msg.obj;
                    if(!state){
                        mcheckBoxRa.setChecked(true);
                        mCheckBoxRl.setChecked(true);
                    }else{
                        mcheckBoxRa.setChecked(false);
                        mCheckBoxRl.setChecked(false);
                    }

                    break;
                case 4:
                    setBatteryValue((int) msg.obj);
                    break;
                case 5:
                    countDownTimerUtils = new CountDownTimerUtils(30000, 1000);
                    countDownTimerUtils.setonDownTimerListener(HomeFragment.this);
                    countDownTimerUtils.start();
                    ToastUtils.ToastMessage(getContext(),"正在手动截取");
                    break;

                case 6:
                    int rate = (int) msg.obj;
                    float ratef = (1 - rate / 250.0f) * 100;
                    mrate.setText("丢包率：" + decimalFormat.format(ratef) + "%");
                    break;

            }
        }
    };
    private CountDownTimerUtils countDownTimerUtils;
    private LinearLayout alertPowr;
    private DecimalFormat decimalFormat = new DecimalFormat("##0.00");
    private TextView mrate;
    private LinearLayout mEleLinearLayout;
    private TextView mfrequency_text;
    private CircleImageView mCircleImageView;

    @Override
    protected void init(Bundle savedInstanceState, View contentView) {
        setFragmentStatus(FRAGMENT_STATUS_SUCCESS);
        Initialize(contentView);
        initdata();
    }

    private void initdata() {

    }

    private void Initialize(View contentView) {

        mCircleImageView = (CircleImageView) contentView.findViewById(R.id.title_bar_left_menu);
        mHomeLoading = (RelativeLayout) contentView.findViewById(R.id.homefragment_loading);
        mTitleButtom = (ImageView) contentView.findViewById(R.id.title_bar_right_menu);//历史记录
        mClearTextView = (TextView) contentView.findViewById(R.id.ClearAllButtonPressed);//断开连接
        mClearView = (ImageView) contentView.findViewById(R.id.ClearImageView);
        mElectricitytext = (TextView) contentView.findViewById(R.id.Electricity_text);
        mEleLinearLayout = (LinearLayout) contentView.findViewById(R.id.electricity_lin);
        mElectricity = (ImageView) contentView.findViewById(R.id.mElectricity);//电量
        mFrequency = (LinearLayout) contentView.findViewById(R.id.frequency);//波律
        mfrequency_text = (TextView) contentView.findViewById(R.id.frequency_text);
        mHeartRate = (TextView) contentView.findViewById(R.id.heart_rate);
        mTestduration = (LinearLayout) contentView.findViewById(R.id.test_duration);
        mDownTimer = (Chronometer) contentView.findViewById(R.id.down_timer);
        mRelayout = (RelativeLayout) contentView.findViewById(R.id.root);
        mcheckBoxRa = (CheckBox) contentView.findViewById(R.id.checkbox_RA);
        mCheckBoxRl = (CheckBox) contentView.findViewById(R.id.checkbox_RL);
        scanButton = (Button) contentView.findViewById(R.id.scanButton);
        mStartAndStopState = (CheckBox) contentView.findViewById(R.id.start_stop_button);
        alertPowr = (LinearLayout) contentView.findViewById(R.id.alert_power);
        mrate = (TextView) contentView.findViewById(R.id.alert_bluetooth_rate);
        Renderer renderer = new Renderer();
        renderer.setSiatShowLabel(true);
        renderer.setSiatLineStep(3);
        renderer.setSiatScrollable(true);
        renderer.getSiatBackgroundColor();
        DrawMainBackground drawBackground = new DrawMainBackground(getContext());
        viewCopy = new ECGDrawView(getContext(), renderer);
        mRelayout.addView(drawBackground);
        mRelayout.addView(viewCopy);
        mCircleImageView.setOnClickListener(HomeFragment.this);
        mStartAndStopState.setOnClickListener(HomeFragment.this);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanActivity.onClearAllButtonPressed();
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

                            offBluetoothReplyState();
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
                commentPopup.setOnCommentPopupClickListener(HomeFragment.this);
                commentPopup.showPopupWindow(view);

            }
        });
    }

    private void offBluetoothReplyState() {
        terminalBluetooth.sendStopEcg();
        mStartAndStopState.setChecked(true);
        scanButton.setVisibility(View.VISIBLE);
        mStartAndStopState.setVisibility(View.GONE);
        mClearView.setVisibility(View.GONE);//无连接设备图片
        mClearTextView.setText("无连接设备");//无连接设备文本
        mHeartRate.setText("- -");
        mEleLinearLayout.setVisibility(View.GONE);
        mTestduration.setVisibility(View.GONE);
        mStartAndStopState.setChecked(false);
        mcheckBoxRa.setChecked(false);
        mCheckBoxRl.setChecked(false);
        mfrequency_text.setText("x 1.00");
        Waverate=26;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HomeFragment.Request && resultCode == Activity.RESULT_OK) {
            terminalBluetooth = new TerminalBluetooth(getContext(),
                    BaseApplication.getInstance().getTIOPeripheral(), new WeakReference<Activity>(getActivity()));
            terminalBluetooth.setonBlackListener(this);

            GoneDefaultState();//隐藏默认状态 显示索索状态


        }
        if (requestCode == HomeFragment.Request && resultCode == Activity.RESULT_CANCELED) {
            scanButton.setVisibility(View.VISIBLE);
            mStartAndStopState.setVisibility(View.GONE);
        }
    }

    private void GoneDefaultState() {
        mEleLinearLayout.setVisibility(View.VISIBLE);
        scanButton.setVisibility(View.GONE);
        mStartAndStopState.setVisibility(View.VISIBLE);
        mClearView.setVisibility(View.VISIBLE);
        mClearTextView.setText(mContent);
        mClearView.setVisibility(View.VISIBLE);
        mClearTextView.setText(BaseApplication.getInstance().getTIOPeripheral().getName());
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

    }

    @Override
    public void onResume() {
        super.onResume();
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

    private int k = 0;

    @Override
    public void onEcgData(int s) {

        mlist.add(s);
        if (mlist.size()>=2500)
            mlist.remove(0);
        ArrayNum[i] = s;
        AHR++;
        i++;
        if (i == 10) {
            if (!INIT) {
                jniCallback.getInitFromJni();
                INIT = true;
            }

            //float[] stringFromJni = jniCallback.getFloatFromJni(ArrayNum);
            OutObjectResultPacket algorithmFrom = jniCallback.getAlgorithmFrom(ArrayNum);
            float[] stringFromJni = algorithmFrom.getEcgData();
            int[] arynRhythmMark = algorithmFrom.getArynRhythmMark();
           // System.out.println("arynRhythmMark:"+arynRhythmMark.length);
            for (int z = 0; z < 13; z++) {
                DrawArray[z] = stringFromJni[z] / Waverate;
                if (mark) {
                    InputFile[k * 13 + z] = stringFromJni[z] / Waverate;
                }

            }
            if (mark) {
                k++;
            }
            viewCopy.setDrawArray(DrawArray);
            i = 0;
        }
        if (AHR == 250) {
            int i = jniCallback.getHeartRate();
            mHandler.obtainMessage(HEART_RATE, i).sendToTarget();
            AHR = 0;
        }
    }

    @Override
    public void onMarkCallBack() {
        System.out.println("检测到了mark信息");
        mark = true;

        mHandler.obtainMessage(5).sendToTarget();
    }

    @Override
    public void onLosedata(int rate) {
        mHandler.obtainMessage(6, rate).sendToTarget();
    }

    @Override
    public void onConnectState() {

        System.out.println("---------连接成功---------");
    }

    @Override
    public void onBluetoothState( BluetoothStateConfirmClass blueStateConfirm) {
        int batteryValue = blueStateConfirm.getBatteryValue();
        boolean fallOff = blueStateConfirm.isFallOff();

        mHandler.obtainMessage(BLUETOOTH_STATE,fallOff).sendToTarget();

        mHandler.obtainMessage(4, batteryValue).sendToTarget();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_stop_button:
                if (mStartAndStopState.isChecked()) {
                    mStartAndStopState.setChecked(false);
                    if (mSlideButton == null) {
                        mSlideButton = new SlideFromBottomPopup(getActivity());
                    }
                    mSlideButton.showPopupWindow();
                    mSlideButton.setOnConfirmListener(this);
                } else {
                    mStartAndStopState.setChecked(true);
                    if(dialogPopup==null){
                        dialogPopup =new DialogPopup(getActivity());
                    }
                    dialogPopup.showPopupWindow();
                    dialogPopup.setOnDialogListener(this);
                }
                break;
            case R.id.title_bar_left_menu:
               // ToastUtils.ToastMessage(getContext(),"点击了");
                BaseApplication.getInstance().getResideMenu().openMenu(0);
                break;
        }

    }

    /**
     * 选择测量模式
     *
     * @param STATE 1001单次  1002连续
     */
    @Override
    public void OnClickConfirm(int STATE) {
        if (STATE == 1001) {
            ToastUtils.ToastMessage(getContext(), "单次测量");
            if (!mark) {
                countDownTimerUtils = new CountDownTimerUtils(60000, 1000);
                countDownTimerUtils.setonDownTimerListener(HomeFragment.this);
                countDownTimerUtils.start();
            }

        }
        if (STATE == 1002) {
            ToastUtils.ToastMessage(getContext(), "连续测量");
            mDownTimer.setBase(SystemClock.elapsedRealtime());
            mDownTimer.start();
        }
        terminalBluetooth.sendStartEcg();
        terminalBluetooth.setonBlackListener(HomeFragment.this);
        mStartAndStopState.setChecked(true);
        mSlideButton.dismiss();
        mTestduration.setVisibility(View.VISIBLE);
    }


    @Override
    public void OnClickCancel() {
        mStartAndStopState.setChecked(false);
        mSlideButton.dismiss();
    }

    /**
     * 是否结束Dialog
     */
    @Override
    public void cancelButton() {
        dialogPopup.dismiss();
    }

    @Override
    public void okButton() {
        mDownTimer.stop();
        dialogPopup.dismiss();
        terminalBluetooth.sendPauseEcg();
        mStartAndStopState.setChecked(false);
        FileUtils.writeArrayToSD(mlist,"abc");
        System.out.println("发送测量暂停指令");

    }

    /**
     * 时间倒计时的类
     *
     * @param l
     */
    @Override
    public void onDownTimerTick(long l) {
        if (!mark && mStartAndStopState.isChecked()) {
            long timelong = l / 1000;
            mDownTimer.setText(timelong + "");
        }
    }

    @Override
    public void onDownFinish() {
        if (!mark) {
            terminalBluetooth.sendPauseEcg();
            mStartAndStopState.setChecked(false);
            mTestduration.setVisibility(View.GONE);
        }
        if (mark) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");

            Date curDate = new Date(System.currentTimeMillis());//获取当前时间

            String str = formatter.format(curDate);
           // System.out.println(str+"-------------------");
            FileUtils.writeFileToSD(InputFile,str);
            ToastUtils.ToastMessage(getContext(), "储存成功");
            k=0;
        }
        mark = false;
    }

    /**
     * 设置电池电量
     *
     * @param battryValue
     */
    private void setBatteryValue(int battryValue) {
        mElectricitytext.setText(battryValue + "%");
        if (battryValue <= 10) {
            alertPowr.setVisibility(View.VISIBLE);
        } else {
            alertPowr.setVisibility(View.INVISIBLE);

        }
        if (battryValue >= 80)
            mElectricity.setBackgroundResource(R.mipmap.ic_battery_1);
        else if (battryValue >= 60)
            mElectricity.setBackgroundResource(R.mipmap.ic_battery_2);
        else if (battryValue >= 40)
            mElectricity.setBackgroundResource(R.mipmap.ic_battery_3);
        else if (battryValue >= 20)
            mElectricity.setBackgroundResource(R.mipmap.ic_battery_4);
        else if (battryValue >= 10)
            mElectricity.setBackgroundResource(R.mipmap.ic_battery_5);

        else
            mElectricity.setBackgroundResource(R.mipmap.ic_battery_6);

    }
//波形
    @Override
    public void onItem1(View v) {
       // ToastUtils.ToastMessage(getContext(),"点击了");
        mfrequency_text.setText("x 0.25");
        Waverate=104;
    }

    @Override
    public void onItem2(View v) {
        mfrequency_text.setText("x 0.50");
        Waverate=52;
    }

    @Override
    public void onItem3(View v) {
        mfrequency_text.setText("x 1.00");
        Waverate=26;
    }

    @Override
    public void onItem4(View v) {
        mfrequency_text.setText("x 2.00");
        Waverate=13;
    }
}
