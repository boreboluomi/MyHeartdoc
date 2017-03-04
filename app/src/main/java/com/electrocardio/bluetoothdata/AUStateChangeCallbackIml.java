package com.electrocardio.bluetoothdata;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.aceuni.uart.sdk.AUBlutoothHolder;
import com.aceuni.uart.sdk.AUStateChangedCallback;
import com.aceuni.uart.sdk.AU_BLE_STATE;
import com.aceuni.uart.sdk.AU_CONNECTION_INTERVAL;
import com.aceuni.uart.sdk.AdvertData;
import com.aceuni.uart.sdk.PropertyKeys;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by ZhangBo on 2016/2/29.
 */
public class AUStateChangeCallbackIml implements AUStateChangedCallback {

    private Context mContext;
    private CallBackStateListener mCallBackStateListener;

    public AUStateChangeCallbackIml(Context context) {
        mContext = context;
    }

    /**
     * 设置事件的回调监听
     *
     * @param callBackStateListener
     */
    public void setCallBackStateListener(CallBackStateListener callBackStateListener) {
        removeCallBackStateListener();
        mCallBackStateListener = callBackStateListener;
    }

    /**
     * 取消事件的回调监听
     */
    public void removeCallBackStateListener() {
        if (mCallBackStateListener != null)
            mCallBackStateListener = null;
    }

    @Override
    public void onStateChangedCallback(final AUBlutoothHolder holder, AU_BLE_STATE state,
                                       Properties properties) {
        switch (state) {
            case FOUND_NEW_DEVICE:// 发现新设备
                System.out.println("发现新设备。。。");
                int rssi = (Integer) properties
                        .get(PropertyKeys.PROPERTY_KEY_RSSI);
                byte[] scanRecord = (byte[]) properties
                        .get(PropertyKeys.PROPERTY_KEY_SCAN_RECORD);
                AdvertData advertData = new AdvertData(scanRecord);
                if (isAUCC2640(advertData.getParsedData())) {
                    BluetoothDevice device = (BluetoothDevice) properties
                            .get(PropertyKeys.PROPERTY_KEY_DEVICE);
                    int rssiF = (Integer) properties
                            .get(PropertyKeys.PROPERTY_KEY_RSSI);
                    System.out.println("是我方设备。。。");
                    if (mCallBackStateListener != null)
                        mCallBackStateListener.foundNewDevice(device);
                }
                break;
            case CONNECTED:// 连接成功
                if (mCallBackStateListener != null)
                    mCallBackStateListener.connected();
                break;
            case DISCONNECTED:// 断开连接
                if (mCallBackStateListener != null)
                    mCallBackStateListener.disConnected();
                break;
            case STATE_READY:// 准备就绪
                holder.setConnectionInterval(AU_CONNECTION_INTERVAL._30ms);
                if (mCallBackStateListener != null)
                    mCallBackStateListener.stateReady();
                break;
        }
    }

    @Override
    public void onReceivedData(byte[] array) {
        if (mCallBackStateListener != null)
            mCallBackStateListener.onReceivedData(array);
    }

    private boolean isAUCC2640(Properties parsedData) {
        final byte[] UUID_FILTER = new byte[]{(byte) 0xF0, (byte) 0xFF};
        final byte[] COMPANY_FILTER = new byte[]{(byte) 0xBC, 0x00};
        final String COMPLETE_LOCAL_NAME = "MECG:";
        boolean isAUCC2640ServiceUUID = false;
        byte[] _16BitPartial = (byte[]) parsedData
                .get(PropertyKeys.PROERTY_KEY_SERVICE_UUIDS_16_BIT_PARTIAL);
        isAUCC2640ServiceUUID = Arrays.equals(_16BitPartial, UUID_FILTER);
        boolean isDeviceName = false;
        String deviceName = (String) parsedData
                .get(PropertyKeys.PROERTY_KEY_LOCAL_NAME_COMPLETE);
        if (deviceName != null)
            isDeviceName = deviceName.startsWith(COMPLETE_LOCAL_NAME);
//        else
//            ToastUtils.ToastMessage(mContext, "deviceName == null");
//        if (isAUCC2640ServiceUUID && isDeviceName) {
//            return true;
//        }
        if (isAUCC2640ServiceUUID) {
            return true;
        }
        return false;
    }

    public interface CallBackStateListener {
        /**
         * 发现了新设备
         *
         * @param bluetoothDevice
         */
        public void foundNewDevice(BluetoothDevice bluetoothDevice);

        /**
         * 连接成功
         */
        public void connected();

        /**
         * 断开连接
         */
        public void disConnected();

        /**
         * 准备就绪
         */
        public void stateReady();

        /**
         * 接收到数据
         *
         * @param bytes
         */
        public void onReceivedData(byte[] bytes);
    }
}
