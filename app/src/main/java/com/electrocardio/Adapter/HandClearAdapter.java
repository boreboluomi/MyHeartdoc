package com.electrocardio.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.electrocardio.Bean.HandClearBean;
import com.electrocardio.R;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/3/8.
 */
public class HandClearAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<HandClearBean> arrayList;
    private HandClearBean currHandClearBean;// 当前对象
    private final int ISYEARANDMONTH = 0;// 是年和月的标签
    private final int NOTYEARANDMONTH = 1;// 不是年和月的标签
    private OnCheckedListener mOnCheckedListener;
    private int currentType;

    public HandClearAdapter(Context context) {
        mContext = context;
    }

    public void setDataList(ArrayList<HandClearBean> arrList) {
        arrayList = arrList;
    }

    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        mOnCheckedListener = onCheckedListener;
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
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return arrayList.get(position).isYearMonth() ? ISYEARANDMONTH : NOTYEARANDMONTH;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        currentType = getItemViewType(position);
        currHandClearBean = arrayList.get(position);
        ViewHolder vh = null;
        TextView tv = null;
        if (convertView == null) {
            switch (currentType) {
                case ISYEARANDMONTH:
                    convertView = View.inflate(mContext, R.layout.handclear_itemone, null);
                    tv = (TextView) convertView.findViewById(R.id.tv_yearOrMonth);
                    convertView.setTag(tv);
                    break;
                case NOTYEARANDMONTH:
                    convertView = View.inflate(mContext, R.layout.handclear_itemtwo, null);
                    vh = new ViewHolder();
                    vh.tv_dayOfMonth = (TextView) convertView.findViewById(R.id.tv_dayOfMonth);
                    vh.tv_timeSpace = (TextView) convertView.findViewById(R.id.tv_timeSpace);
                    vh.tv_timeLength = (TextView) convertView.findViewById(R.id.tv_timeLength);
                    vh.tv_dataSize = (TextView) convertView.findViewById(R.id.tv_size);
                    vh.tv_subMitState = (TextView) convertView.findViewById(R.id.tv_submitState);
                    vh.cb_check = (CheckBox) convertView.findViewById(R.id.cb_check);
                    convertView.setTag(vh);
                    break;
            }
        } else {
            switch (currentType) {
                case ISYEARANDMONTH:
                    tv = (TextView) convertView.getTag();
                    break;
                case NOTYEARANDMONTH:
                    vh = (ViewHolder) convertView.getTag();
                    break;
            }
        }
        switch (currentType) {
            case ISYEARANDMONTH:
                String month = currHandClearBean.getMonth() < 10 ? "0" +
                        currHandClearBean.getMonth() : currHandClearBean.getMonth() + "";
                tv.setText(currHandClearBean.getYear() + "年" + month + "月");
                break;
            case NOTYEARANDMONTH:
                convertView.findViewById(R.id.v_topLine).setVisibility(View.VISIBLE);
                if (position == (arrayList.size() - 1)) {
                    convertView.findViewById(R.id.v_botLineShort).setVisibility(View.GONE);
                    convertView.findViewById(R.id.v_botLineLong).setVisibility(View.VISIBLE);
                } else {
                    if (getItemViewType(position + 1) == NOTYEARANDMONTH) {
                        convertView.findViewById(R.id.v_botLineLong).setVisibility(View.GONE);
                        convertView.findViewById(R.id.v_botLineShort).setVisibility(View.VISIBLE);
                    } else {
                        convertView.findViewById(R.id.v_botLineShort).setVisibility(View.GONE);
                        convertView.findViewById(R.id.v_botLineLong).setVisibility(View.VISIBLE);
                    }
                }
                if (getItemViewType(position - 1) == NOTYEARANDMONTH)
                    convertView.findViewById(R.id.v_topLine).setVisibility(View.GONE);
                vh.tv_dayOfMonth.setText(currHandClearBean.getDayOfMonth() + "");
                vh.tv_timeSpace.setText(currHandClearBean.getTimeSpace());
                vh.tv_timeLength.setText(currHandClearBean.getTimeLength());
                vh.tv_dataSize.setText(currHandClearBean.getDataSize() + "K");
                vh.tv_subMitState.setVisibility(currHandClearBean.isSubmitState() ?
                        View.INVISIBLE : View.VISIBLE);
                vh.cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (mOnCheckedListener != null)
                            mOnCheckedListener.onChecked(position, isChecked);
                    }
                });
                vh.cb_check.setChecked(currHandClearBean.isChecked() ? true : false);
                break;
        }
        return convertView;
    }

    public interface OnCheckedListener {
        public void onChecked(int position, boolean isChecked);
    }

    class ViewHolder {
        private TextView tv_dayOfMonth;// 测量日
        private TextView tv_timeSpace;// 测量间隔
        private TextView tv_timeLength;// 测量时长
        private TextView tv_dataSize;// 数据大小
        private TextView tv_subMitState;// 上传状态
        private CheckBox cb_check;// 是否选中
    }
}
