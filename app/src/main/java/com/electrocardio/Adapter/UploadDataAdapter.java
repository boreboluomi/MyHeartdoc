package com.electrocardio.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.Bean.UploadDataBean;
import com.electrocardio.Bean.UploadDataState;
import com.electrocardio.R;
import com.electrocardio.custom.elec.UploadDataView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/3/9.
 */
public class UploadDataAdapter extends BaseAdapter {

    private Context mContext;
    private UploadDataBean currUploadDataBean;
    private ArrayList<UploadDataBean> arrayList;
    private final int HEAD = 0;// 头部条目
    private final int FIRSTITEM = 1;// 第一个条目
    private final int SECONDITEM = 2;// 第二个条目
    private int CURRENTITEM = 0;// 当前条目
    private DecimalFormat decimalFormat = new DecimalFormat("##0.00");
    private UploadDataListener mUploadDataListener;// 上传数据的监听

    public UploadDataAdapter(Context context) {
        mContext = context;
    }

    public void setUploadDataList(ArrayList<UploadDataBean> arr) {
        arrayList = arr;
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
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEAD;
        else {
            return arrayList.get(position).isYearAndMonth() ? FIRSTITEM : SECONDITEM;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        currUploadDataBean = arrayList.get(position);
        CURRENTITEM = getItemViewType(position);
        ViewHolderOne viewHolderOne = null;
        ViewHolderTwo viewHolderTwo = null;
        TextView tvYearAndMonth = null;

        if (convertView == null) {
            switch (CURRENTITEM) {
                case HEAD:
                    convertView = View.inflate(mContext, R.layout.uploaddata_headitem, null);
                    viewHolderOne = new ViewHolderOne();
                    viewHolderOne.tvDayAndMonth = (TextView) convertView.findViewById(R.id.tv_yearAndMonth);
                    viewHolderOne.tvAllOrNot = (TextView) convertView.findViewById(R.id.btn_allOrNot);
                    convertView.setTag(viewHolderOne);
                    break;
                case FIRSTITEM:
                    convertView = View.inflate(mContext, R.layout.handclear_itemone, null);
                    tvYearAndMonth = (TextView) convertView.findViewById(R.id.tv_yearOrMonth);
                    convertView.setTag(tvYearAndMonth);
                    break;
                case SECONDITEM:
                    viewHolderTwo = new ViewHolderTwo();
                    convertView = View.inflate(mContext, R.layout.uploaddata_content_item, null);
                    viewHolderTwo.tv_dayOfMonth = (TextView) convertView.findViewById(R.id.tv_dayOfMonth);
                    viewHolderTwo.tv_timeSpace = (TextView) convertView.findViewById(R.id.tv_timeSpace);
                    viewHolderTwo.rl_timeAndSize = (RelativeLayout) convertView.findViewById(R.id.rl_timeAndSize);
                    viewHolderTwo.tv_timeLength = (TextView) convertView.findViewById(R.id.tv_timeLength);
                    viewHolderTwo.tv_dataSize = (TextView) convertView.findViewById(R.id.tv_size);
                    viewHolderTwo.rl_uploadProgress = (RelativeLayout) convertView.findViewById(R.id.rl_uploadProgress);
                    viewHolderTwo.pb_uploadProgress = (ProgressBar) convertView.findViewById(R.id.pb_uploadProgress);
                    viewHolderTwo.tv_upDataSta = (TextView) convertView.findViewById(R.id.tv_uploadDataState);
                    viewHolderTwo.udv = (UploadDataView) convertView.findViewById(R.id.udv);
                    convertView.setTag(viewHolderTwo);
                    break;
            }
        } else {
            switch (CURRENTITEM) {
                case HEAD:
                    viewHolderOne = (ViewHolderOne) convertView.getTag();
                    break;
                case FIRSTITEM:
                    tvYearAndMonth = (TextView) convertView.getTag();
                    break;
                case SECONDITEM:
                    viewHolderTwo = (ViewHolderTwo) convertView.getTag();
                    break;
            }
        }
        switch (CURRENTITEM) {
            case HEAD:
                String month = currUploadDataBean.getMonth() < 10 ? "0" +
                        currUploadDataBean.getMonth() : currUploadDataBean.getMonth() + "";
                viewHolderOne.tvDayAndMonth.setText(currUploadDataBean.getYear() + "年" + month + "月");
                viewHolderOne.tvAllOrNot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUploadDataListener != null)
                            mUploadDataListener.allUploadClicked();
                    }
                });
                break;
            case FIRSTITEM:
                String monthTwo = currUploadDataBean.getMonth() < 10 ? "0" +
                        currUploadDataBean.getMonth() : currUploadDataBean.getMonth() + "";
                tvYearAndMonth.setText(currUploadDataBean.getYear() + "年" + monthTwo + "月");
                break;
            case SECONDITEM:
                convertView.findViewById(R.id.v_topLine).setVisibility(View.VISIBLE);
                if (position == (arrayList.size() - 1)) {
                    convertView.findViewById(R.id.v_botLineShort).setVisibility(View.GONE);
                    convertView.findViewById(R.id.v_botLineLong).setVisibility(View.VISIBLE);
                } else {
                    if (getItemViewType(position + 1) == SECONDITEM) {
                        convertView.findViewById(R.id.v_botLineLong).setVisibility(View.GONE);
                        convertView.findViewById(R.id.v_botLineShort).setVisibility(View.VISIBLE);
                    } else {
                        convertView.findViewById(R.id.v_botLineShort).setVisibility(View.GONE);
                        convertView.findViewById(R.id.v_botLineLong).setVisibility(View.VISIBLE);
                    }
                }
                if (getItemViewType(position - 1) == SECONDITEM)
                    convertView.findViewById(R.id.v_topLine).setVisibility(View.GONE);

                viewHolderTwo.tv_dayOfMonth.setText(currUploadDataBean.getDayOfMonth() + "");
                viewHolderTwo.tv_timeSpace.setText(currUploadDataBean.getTimeSpace());
                viewHolderTwo.tv_timeLength.setText(currUploadDataBean.getTimeLength());
                viewHolderTwo.tv_dataSize.setText(currUploadDataBean.getDataSize() + "M");
                viewHolderTwo.udv.setUploadState(currUploadDataBean.getUploadState());
                viewHolderTwo.pb_uploadProgress.setProgress((int) (currUploadDataBean.getUploadDataSize()
                        * 100 / currUploadDataBean.getDataSize()));
//                if (currUploadDataBean.getUploadState().equals(UploadDataState.NOTUPLOAD) ||
//                        currUploadDataBean.getUploadState().equals(UploadDataState.WAITUPLOAD)) {
//                    viewHolderTwo.rl_uploadProgress.setVisibility(View.GONE);
//                    viewHolderTwo.rl_timeAndSize.setVisibility(View.VISIBLE);
//                } else {
                viewHolderTwo.rl_timeAndSize.setVisibility(View.GONE);
                viewHolderTwo.rl_uploadProgress.setVisibility(View.VISIBLE);
                if (currUploadDataBean.getUploadState().equals(UploadDataState.UPLOADAGINE)) {
                    viewHolderTwo.tv_upDataSta.setText("失败");
                    viewHolderTwo.tv_upDataSta.setTextColor(Color.parseColor("#c9151e"));
                } else {
                    if (currUploadDataBean.getDataSize() < 1000)
                        viewHolderTwo.tv_upDataSta.setText(currUploadDataBean.getUploadDataSize() +
                                "/" + currUploadDataBean.getDataSize() + "K");
                    else
                        viewHolderTwo.tv_upDataSta.setText(decimalFormat.format(currUploadDataBean.
                                getUploadDataSize() / 1024) + "/" + decimalFormat.format(
                                currUploadDataBean.getDataSize() / 1024) + "M");
                    viewHolderTwo.tv_upDataSta.setTextColor(Color.parseColor("#666666"));
                }
//                }
                // 上传状态的点击事件
                viewHolderTwo.udv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mUploadDataListener != null)
                            mUploadDataListener.uploadStateClicked(position, view);
                    }
                });
                break;
        }
        return convertView;
    }

    class ViewHolderOne {
        private TextView tvDayAndMonth;// 年和月
        private TextView tvAllOrNot;// 全选按钮
    }

    class ViewHolderTwo {
        private TextView tv_dayOfMonth;// 日
        private TextView tv_timeSpace;// 时间间隔
        private RelativeLayout rl_timeAndSize;// 时间和大小
        private TextView tv_timeLength;// 时长
        private TextView tv_dataSize;// 数据大小

        private RelativeLayout rl_uploadProgress;// 上传状态
        private TextView tv_upDataSta;// 上传状态
        private ProgressBar pb_uploadProgress;// 进度条
        private UploadDataView udv;// 上传状态图片
    }

    /**
     * 设置上传数据的监听
     *
     * @param uploadDataListener
     */
    public void setUploadDataListener(UploadDataListener uploadDataListener) {
        mUploadDataListener = uploadDataListener;
    }

    /**
     * 上传数据的监听
     */
    public interface UploadDataListener {
        public void allUploadClicked();

        public void uploadStateClicked(int position, View view);
    }
}
