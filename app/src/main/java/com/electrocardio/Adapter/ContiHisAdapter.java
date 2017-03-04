package com.electrocardio.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.custom.elec.RateLabel;
import com.electrocardio.databasebean.ContinueMeasureRecord;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/04/07.
 */
public class ContiHisAdapter extends BaseAdapter {

    private Context mContext;// 上下文变量
    private ArrayList<ContinueMeasureRecord> arrayList;// 存储连续测量记录的集合

    /**
     * 构造函数
     */
    public ContiHisAdapter(Context context) {
        mContext = context;
    }

    public void setArrayList(ArrayList<ContinueMeasureRecord> array) {
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
            convertView = View.inflate(mContext, R.layout.continuous_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvDayOfMonth = (TextView) convertView.findViewById(R.id.tv_dayOfMonth);
            viewHolder.tvTimeSpaceTitle = (TextView) convertView.findViewById(R.id.tv_timeSpaceTitle);
            viewHolder.tvTimeSpace = (TextView) convertView.findViewById(R.id.tv_timeSpace);
            viewHolder.tvContentSize = (TextView) convertView.findViewById(R.id.tv_contentSize);
            viewHolder.tvHeartRate = (TextView) convertView.findViewById(R.id.tv_heartRate);
            viewHolder.rateLabel = (RateLabel) convertView.findViewById(R.id.rateLabel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvDayOfMonth.setText(arrayList.get(position).getStartDay() + "");
        int startHour = arrayList.get(position).getStartTime() >>> 16 & 0xff;
        int startMinute = arrayList.get(position).getStartTime() >>> 8 & 0xff;
        int startSecond = arrayList.get(position).getStartTime() & 0xff;
        int endMonth = arrayList.get(position).getEndMD() >>> 8 & 0xff;
        int endDay = arrayList.get(position).getEndMD() & 0xff;
        int endHour = arrayList.get(position).getEndTime() >>> 16 & 0xff;
        int endMinute = arrayList.get(position).getEndTime() >>> 8 & 0xff;
        int endSecond = arrayList.get(position).getEndTime() & 0xff;
        long timeLength = arrayList.get(position).getTimeLength();
        int hourLength = (int) (timeLength / 3600000);
        timeLength %= 3600000;
        int minuteLength = (int) (timeLength / 60000);
        timeLength /= 1000;
        int secondLength = (int) (timeLength % 60);
        viewHolder.tvTimeSpaceTitle.setText((startHour < 10 ? "0" + startHour : startHour) + ":"
                + (startMinute < 10 ? "0" + startMinute : startMinute) + ":" + (startSecond < 10 ?
                "0" + startSecond : startSecond) + "~" + (endMonth < 10 ? "0" + endMonth + "月" :
                endMonth + "月") + (endDay < 10 ? "0" + endDay : endDay) + "日" + " " + (endHour <
                10 ? "0" + endHour : endHour) + ":" + (endMinute < 10 ? "0" + endMinute : endMinute)
                + ":" + (endSecond < 10 ? "0" + endSecond : endSecond));
        viewHolder.tvTimeSpace.setText((hourLength < 10 ? "0" + hourLength : hourLength) + ":" +
                (minuteLength < 10 ? "0" + minuteLength : minuteLength) + ":" + (secondLength < 10 ?
                "0" + secondLength : secondLength));
        viewHolder.tvContentSize.setText(arrayList.get(position).getDataSize() + "k");
        viewHolder.tvHeartRate.setText(arrayList.get(position).getAverageRate() + "");
        viewHolder.rateLabel.setRate(arrayList.get(position).getRate());
        return convertView;
    }

    class ViewHolder {
        private TextView tvDayOfMonth;// 日
        private TextView tvTimeSpaceTitle;// 时间区域标题
        private TextView tvTimeSpace;// 时长
        private TextView tvContentSize;// 数据大小
        private TextView tvHeartRate;// 平均心率
        private RateLabel rateLabel;// 正常心率比
    }
}
