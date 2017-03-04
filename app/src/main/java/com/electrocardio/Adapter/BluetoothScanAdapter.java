package com.electrocardio.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.terminalIO.TIOPeripheral;

import java.util.ArrayList;

/**
 * Created by yangzheng on 2016/2/29.
 */
public class BluetoothScanAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<TIOPeripheral> arrayList;
    private int selectNumber = -1;

    public BluetoothScanAdapter(Context context) {
        mContext = context;
    }

    public void setSelectedPosition(int position) {
        selectNumber = position;
    }

    public int getSelectedPosition() {
        return selectNumber;
    }

    public void setDeviceList(ArrayList<TIOPeripheral> list) {
        arrayList = list;
    }

    @Override
    public int getCount() {
        if (arrayList == null)
            arrayList = new ArrayList<TIOPeripheral>();
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
            ImageView iv = (ImageView) convertView.findViewById(R.id.iv_checked);
            TextView tv_deviceName = (TextView) convertView.findViewById(R.id.tv_deviceName);
            TextView tv_connecting = (TextView) convertView.findViewById(R.id.tv_connecting);
            vh = new ViewHolder();
            vh.iv_checked = iv;
            vh.tv_deviceName = tv_deviceName;
            vh.tv_connecting = tv_connecting;
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (selectNumber == position) {
            vh.iv_checked.setVisibility(View.VISIBLE);
            vh.tv_connecting.setVisibility(View.VISIBLE);
        } else {
            vh.iv_checked.setVisibility(View.INVISIBLE);
            vh.tv_connecting.setVisibility(View.INVISIBLE);
        }
        vh.tv_deviceName.setText(arrayList.get(position).getAddress());
        return convertView;
    }

    class ViewHolder {
        private ImageView iv_checked;
        private TextView tv_deviceName;
        private TextView tv_connecting;
    }
}
