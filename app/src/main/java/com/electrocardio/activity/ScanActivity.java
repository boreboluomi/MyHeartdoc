package com.electrocardio.activity;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.electrocardio.Adapter.BluetoothScanAdapter;
import com.electrocardio.R;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.base.ProfileActivity;
import com.electrocardio.bluetoothdata.TerminalBluetooth;
import com.electrocardio.custom.elec.RippleViewTotal;
import com.electrocardio.terminalIO.TIOManager;
import com.electrocardio.terminalIO.TIOManagerCallback;
import com.electrocardio.terminalIO.TIOPeripheral;
import com.electrocardio.util.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * Created by yangzheng on 2016/2/3.
 */
public class ScanActivity extends ProfileActivity implements TIOManagerCallback, View.OnClickListener {
    private static final int ENABLE_BT_REQUEST_ID = 1;
    private static final int SCAN_INTERVAL = 8000;
    private ListView _peripheralsListView;
    private Handler _scanHandler = new Handler();
    private ImageView mCancel;
    private Intent intent;
    private BluetoothScanAdapter adapter;
    private ArrayList<TIOPeripheral> deviceNameList;
    private int connect=0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    boolean connected = peripheralByAddress.isConnected();
                    if (connected) {
                        mSendMessage(false);
                        BaseApplication.getInstance().setTIOPeripheral(peripheralByAddress);
                        intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                        ToastUtils.ToastMessage(getContext(), "连接成功");
                    } else {
                        connect++;
                        mSendMessage(true);
                    }
                   /* if(connect>=5){
                        mSendMessage(false);
                        ToastUtils.ToastMessage(getContext(), "下位机异常请重新启动下位机");
                    }*/
                    break;
            }
        }
    };
    private RippleViewTotal animationIV;
    private TIOPeripheral peripheralByAddress;
    private TerminalBluetooth instance;

    // private AnimationDrawable animationDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("ScanActivity-onCreate()");
        TIOManager.sharedInstance().setListener(this);
        if (!TIOManager.sharedInstance().isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
        }
        startTimedScan();
        initView();
        initData();
        setOnButtonClickListener();

    }

    @Override
    protected int getLayoutID() {
        return R.layout.scan_activity;
    }

    @Override
    protected void onInitialize() {
    }

    private void initView() {
        mCancel = (ImageView) this.findViewById(R.id.mScan_cancel);
        mCancel = (ImageView) findViewById(R.id.mScan_cancel);
        animationIV = (RippleViewTotal) findViewById(R.id.animationIV);
        this._peripheralsListView = (ListView) this.findViewById(R.id.peripheralsListView);
        animationIV.setFrameAnimation();
    }

    private void initData() {
        deviceNameList = new ArrayList<TIOPeripheral>();
        mCancel.setOnClickListener(this);
        adapter = new BluetoothScanAdapter(this);
        adapter.setDeviceList(deviceNameList);
        _peripheralsListView.setAdapter(adapter);
    }

    @Override
    public void onAttachedToWindow() {
       /* super.onAttachedToWindow();
        animationIV.setImageResource(R.drawable.m_animation);
        animationDrawable = (AnimationDrawable) animationIV.getDrawable();
        animationDrawable.start();*/
    }

    private void startTimedScan() {
        this._scanHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TIOManager.sharedInstance().stopScan();
            }
        }, ScanActivity.SCAN_INTERVAL);
        TIOManager.sharedInstance().startScan();
    }

    public static void onClearAllButtonPressed() {
        TIOManager.sharedInstance().removeAllPeripherals();
    }

    private void setOnButtonClickListener() {
        _peripheralsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();
                final TIOPeripheral peripheral = TIOManager.sharedInstance().getPeripherals()[position];
                peripheralByAddress = TIOManager.sharedInstance().findPeripheralByAddress(peripheral.getAddress());
                peripheralByAddress.connect();
              /*  mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            intent = new Intent();
                            intent.putExtra(BaseApplication.PERIPHERAL_ID_NAME, peripheral.getAddress());
                            setResult(RESULT_OK, intent);
                            finish();
                    }
                }, 2000);*/
                mSendMessage(true);
            }
        });
    }

    private void mSendMessage(boolean sendconnet) {
        if (sendconnet) {
            mHandler.sendEmptyMessageDelayed(1, 2000);
        }
    }

    @Override
    protected boolean isHideActionBar() {
        return true;
    }

    @Override
    protected boolean isOpenActionBar() {
        return true;
    }

    @Override
    public void tioManagerDidDiscoverPeripheral(TIOPeripheral peripheral) {
        peripheral.setShallBeSaved(false);
        TIOManager.sharedInstance().savePeripherals();
        deviceNameList.add(peripheral);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void tioManagerDidUpdatePeripheral(TIOPeripheral peripheral) {
        deviceNameList.add(peripheral);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mScan_cancel:
                this._scanHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animationIV.Connect();
                        TIOManager.sharedInstance().stopScan();
                    }
                }, ScanActivity.SCAN_INTERVAL);
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimedScan();
        // adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
       /* animationDrawable = (AnimationDrawable) animationIV.getDrawable();
        animationDrawable.stop();*/
        animationIV.closeFrameAnimation();
    }
}
