package com.electrocardio.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.electrocardio.R;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/2/29.
 */
public class BluetoothDeviceAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<BluetoothDevice> arrayList;
    private int selectNumber = -1;

    public BluetoothDeviceAdapter(Context context) {
        mContext = context;
    }

    public void setSelectedPosition(int position) {
        selectNumber = position;
    }

    public int getSelectedPosition() {
        return selectNumber;
    }

    public void setDeviceList(ArrayList<BluetoothDevice> list) {
        arrayList = list;
    }

    @Override
    public int getCount() {
        if (arrayList == null)
            arrayList = new ArrayList<>();
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.adapter_bluetoothdevice, null);
            TextView tv_deviceAddress = (TextView) convertView.findViewById(R.id.tv_deviceAddress);
            TextView tv_deviceName = (TextView) convertView.findViewById(R.id.tv_deviceName);
            TextView tv_connecting = (TextView) convertView.findViewById(R.id.tv_connecting);
            vh = new ViewHolder();
            vh.tv_deviceAddress = tv_deviceAddress;
            vh.tv_deviceName = tv_deviceName;
            vh.tv_connecting = tv_connecting;
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (selectNumber == position)
            vh.tv_connecting.setVisibility(View.VISIBLE);
        else
            vh.tv_connecting.setVisibility(View.INVISIBLE);
        vh.tv_deviceName.setText(arrayList.get(position).getName());
        vh.tv_deviceAddress.setText(arrayList.get(position).getAddress());
        return convertView;
    }

    class ViewHolder {
        private TextView tv_deviceAddress;
        private TextView tv_deviceName;
        private TextView tv_connecting;
    }
}
