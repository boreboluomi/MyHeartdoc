package com.electrocardio.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.databasebean.AbnormalListBean;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/3/16.
 */
public class AbnormalListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<AbnormalListBean> arrayList;

    public AbnormalListAdapter(Context context) {
        mContext = context;
    }

    public void setDataList(ArrayList<AbnormalListBean> array) {
        arrayList = array;
    }

    @Override
    public int getCount() {
        if (arrayList == null)
            arrayList = new ArrayList<>();
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.abnormallist_item, null);
            viewHolder = new ViewHolder();
            viewHolder.ll_abnormallist = (LinearLayout) convertView.findViewById(R.id.ll_abnormallist);
            viewHolder.tv_serialNumber = (TextView) convertView.findViewById(R.id.tv_serialNumber);
            viewHolder.tv_timeLabel = (TextView) convertView.findViewById(R.id.tv_timeLabel);
            viewHolder.tv_symptom = (TextView) convertView.findViewById(R.id.tv_symptom);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            viewHolder.ll_abnormallist.setBackgroundColor(Color.parseColor("#f2f2f5"));
        } else {
            viewHolder.ll_abnormallist.setBackgroundColor(Color.parseColor("#ebebf0"));
        }
        viewHolder.tv_serialNumber.setText((position + 1) + "");
        viewHolder.tv_timeLabel.setText(arrayList.get(position).getTimeLabel());
        viewHolder.tv_symptom.setText(arrayList.get(position).getDescription());
        if (arrayList.get(position).getTYPE() == AbnormalListBean.MARK) {
            viewHolder.tv_serialNumber.setTextColor(Color.parseColor("#ff7200"));
            viewHolder.tv_timeLabel.setTextColor(Color.parseColor("#ff7200"));
            viewHolder.tv_symptom.setTextColor(Color.parseColor("#ff7200"));
        } else {
            viewHolder.tv_serialNumber.setTextColor(Color.parseColor("#333333"));
            viewHolder.tv_timeLabel.setTextColor(Color.parseColor("#333333"));
            viewHolder.tv_symptom.setTextColor(Color.parseColor("#333333"));
        }
        return convertView;
    }

    class ViewHolder {
        private LinearLayout ll_abnormallist;
        private TextView tv_serialNumber;
        private TextView tv_timeLabel;
        private TextView tv_symptom;
    }
}
