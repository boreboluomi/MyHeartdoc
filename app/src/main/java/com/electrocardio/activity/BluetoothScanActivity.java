package com.electrocardio.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.aceuni.uart.sdk.AUBlutoothHolder;
import com.electrocardio.Adapter.BluetoothDeviceAdapter;
import com.electrocardio.R;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.bluetoothdata.AUStateChangeCallbackIml;
import com.electrocardio.custom.elec.RippleViewTotal;
import com.electrocardio.database.UserDao;
import com.electrocardio.util.WeakReferenceHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/3/2.
 */
public class BluetoothScanActivity extends Activity {

    private ListView deviceList;// 蓝牙设备列表
    private ImageView mCancel;// 取消按钮
    private BluetoothDevice device;// 正在连接的设备
    private RippleViewTotal rvt;// 搜索蓝牙设备的动画
    private final int STOP_SCAN = 0;// 停止扫描
    private final int START_SCAN = 1;// 开始扫描
    private BluetoothDeviceAdapter adapter;
    private ArrayList<BluetoothDevice> deviceNameList;// 存储蓝牙设备的列表
    private String deivceId = "";// 设备ID
    private AUBlutoothHolder mHolder;
    private WeakReferenceHandler handler = new WeakReferenceHandler(new WeakReference<Activity>(this)) {
        @Override
        public void handleWeakMessage(Message message) {
            switch (message.what) {
                case STOP_SCAN:
                    if (mHolder != null) {
                        mHolder.stopScan();
                        handler.sendEmptyMessageDelayed(START_SCAN, 1000);
                    }
                    break;
                case START_SCAN:
                    if (mHolder != null) {
                        mHolder.startLeScan();
                        handler.sendEmptyMessageDelayed(STOP_SCAN, 10000);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothscan);

        initView();

        initData();

        setOnButtonClickListener();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        rvt.setFrameAnimation();
        rvt.Searching();
        AUStateChangeCallbackIml aUStateChangeCallbackIml = BaseApplication.getInstance().
                getBlueCommuCls().getAUStateChangeCallbackIml();
        aUStateChangeCallbackIml.setCallBackStateListener(new AUStateChangeCallbackIml.
                CallBackStateListener() {
            @Override
            public void foundNewDevice(BluetoothDevice bluetoothDevice) {
                if (!deviceNameList.contains(bluetoothDevice)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rvt.Searched();
                        }
                    });
                    deviceNameList.add(bluetoothDevice);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void connected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvt.Connect();
                    }
                });
            }

            @Override
            public void disConnected() {
            }

            @Override
            public void stateReady() {
                BaseApplication.getInstance().setBluetoothDevice(device);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvt.closeFrameAnimation();
                    }
                });
                Intent intent = new Intent();
                intent.putExtra(BaseApplication.PERIPHERAL_ID_NAME, deviceNameList.get(
                        adapter.getSelectedPosition()).getName());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onReceivedData(byte[] bytes) {

            }
        });
        mHolder.startLeScan();
        handler.sendEmptyMessageDelayed(STOP_SCAN, 10000);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        mCancel = (ImageView) findViewById(R.id.mScan_cancel);
        deviceList = (ListView) findViewById(R.id.peripheralsListView);
        rvt = (RippleViewTotal) findViewById(R.id.rvt);
    }

    @Override
    public void onBackPressed() {
        handler.removeMessages(START_SCAN);
        handler.removeMessages(STOP_SCAN);
        mHolder.stopScan();
        if (mHolder.connectedDevice() != null)
            mHolder.disconnect();
        while (mHolder.connectedDevice() != null) {
        }
        // mHolder.disconnect();
        rvt.closeFrameAnimation();
        super.onBackPressed();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        deviceNameList = new ArrayList<>();
        adapter = new BluetoothDeviceAdapter(this);
        adapter.setDeviceList(deviceNameList);
        deviceList.setAdapter(adapter);
        mHolder = BaseApplication.getInstance().getBlueCommuCls().getAUBlutoothHolder();
        deivceId = UserDao.getInstance(this).getDeviceId();
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClickListener() {
        // 取消按钮的点击事件
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // listView的条目点击事件
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();
                handler.removeMessages(START_SCAN);
                handler.removeMessages(STOP_SCAN);
                mHolder.stopScan();
                device = deviceNameList.get(position);
                mHolder.connect(device, BaseApplication.getInstance());
            }
        });
    }

}
