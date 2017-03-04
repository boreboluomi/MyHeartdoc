package com.electrocardio.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.electrocardio.Bean.SixSecondMeasureBean;
import com.electrocardio.R;
import com.electrocardio.custom.elec.PointView;
import com.electrocardio.custom.elec.SixSecondRecordLabel;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/3/7.
 */
public class SixSecondMeasureAdapter extends BaseAdapter {

    private Context mContext;// 环境变量
    private final int isDayofMonth = 0;// 是日期
    private final int notDayOfMonth = 1;// 不是日期
    private int currentType = 1;// 当前类型
    private SixSecondMeasureBean ssmb;
    private ArrayList<SixSecondMeasureBean> arrayList;

    public SixSecondMeasureAdapter(Context context) {
        mContext = context;
    }

    /**
     * 设置集合列表
     *
     * @param measureList
     */
    public void setSixSecondMeasureList(ArrayList<SixSecondMeasureBean> measureList) {
        arrayList = measureList;
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
    public int getItemViewType(int position) {
        return arrayList.get(position).isDayOfMonth() ?
                isDayofMonth : notDayOfMonth;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ssmb = arrayList.get(position);
        ViewHolderOne vhOne = null;
        ViewHolderTwo vhTwo = null;
        currentType = getItemViewType(position);
        if (convertView == null) {
            if (currentType == isDayofMonth) {
                convertView = View.inflate(mContext, R.layout.secound_typeone_item, null);
                TextView tv = (TextView) convertView.findViewById(R.id.tv_dayLabel);
                View v_top = convertView.findViewById(R.id.v_topLine);
                vhOne = new ViewHolderOne();
                vhOne.v_topLine = v_top;
                vhOne.tv_content = tv;
                convertView.setTag(vhOne);
            } else {
                vhTwo = new ViewHolderTwo();
                convertView = View.inflate(mContext, R.layout.secound_typetwo_item, null);
                PointView point = (PointView) convertView.findViewById(R.id.iv_point);
                TextView timeSpace = (TextView) convertView.findViewById(R.id.tv_timeSpace);
                TextView bpm = (TextView) convertView.findViewById(R.id.tv_bpm);
                TextView health = (TextView) convertView.findViewById(R.id.tv_health);
                SixSecondRecordLabel sixSecondReLabel = (SixSecondRecordLabel) convertView.
                        findViewById(R.id.ssrl_label);
                vhTwo.iv_point = point;
                vhTwo.tv_timeSpace = timeSpace;
                vhTwo.tv_bpm = bpm;
                vhTwo.tv_health = health;
                vhTwo.sixSecondRecord = sixSecondReLabel;
                convertView.setTag(vhTwo);
            }
        } else {
            if (currentType == isDayofMonth) {
                vhOne = (ViewHolderOne) convertView.getTag();
            } else {
                vhTwo = (ViewHolderTwo) convertView.getTag();
            }
        }
        if (currentType == isDayofMonth) {
            if (position == 0)
                vhOne.v_topLine.setVisibility(View.INVISIBLE);
            else
                vhOne.v_topLine.setVisibility(View.VISIBLE);
            vhOne.tv_content.setText(ssmb.getDayOfMonth() + "");
        } else {
            vhTwo.sixSecondRecord.setNumber(ssmb.getHealthNumber());
            vhTwo.iv_point.setNumber(ssmb.getHealthNumber());
            vhTwo.tv_timeSpace.setText(ssmb.getTimeSpace());
            vhTwo.tv_bpm.setText(ssmb.getBmp() + "");
            vhTwo.tv_health.setText(ssmb.getHealthDescrip());
        }
        return convertView;
    }

    class ViewHolderOne {
        private View v_topLine;// 上半部分线
        private TextView tv_content;// 内容
    }

    class ViewHolderTwo {
        private PointView iv_point;// 左边的点
        private TextView tv_timeSpace;// 时间间隔
        private TextView tv_bpm;// 心率
        private TextView tv_health;// 健康描述
        private SixSecondRecordLabel sixSecondRecord;// 60秒测量记录label
    }
}
