package com.electrocardio.fragment.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.Adapter.ContiHisAdapter;
import com.electrocardio.R;
import com.electrocardio.activity.ContinueMeasureRasultActivity;
import com.electrocardio.activity.DistoryContentActivity;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.database.ContinueMeasureRecordDao;
import com.electrocardio.databasebean.ContinueMeasureRecord;
import com.electrocardio.util.ToastUtils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ZhangBo on 2016/04/07.
 * 连续记录界面
 */
public class ContinuousFragment extends BaseFragment {

    private ListView listView;
    public static int HISTORY = 1;// 历史记录信号
    public static int ABNORMAL = 2;// 异常列表信号
    private ArrayList<ContinueMeasureRecord> arrayList = new ArrayList<>();// 连续测量数据集合
    private RelativeLayout rl_currentMeasure;// 当前测量
    private ContinueMeasureRecord currentMeasureRecord;// 当前正在测量的记录
    private TextView tvStartTime;// 当前测量开始时间
    private ContiHisAdapter contiHisAdapter;// 适配器

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.profile_fragment, null);
        listView = (ListView) view.findViewById(R.id.listView);
        tvStartTime = (TextView) view.findViewById(R.id.tv_startTime);
        rl_currentMeasure = (RelativeLayout) view.findViewById(R.id.rl_currentMeasure);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int ym = ((year - 1970) << 8) + month;
        currentMeasureRecord = ContinueMeasureRecordDao.getInstance(mActivity).getCurrentMeasureRecord();
        arrayList = ContinueMeasureRecordDao.getInstance(mActivity).getContinueMeasureRecord(ym);
        contiHisAdapter = new ContiHisAdapter(mActivity);
        contiHisAdapter.setArrayList(arrayList);
        listView.setAdapter(contiHisAdapter);

        setOnClickListener();
        initCurrentMeasure();
    }

    /**
     * 初始化当前正在测量的记录
     */
    private void initCurrentMeasure() {
        if (currentMeasureRecord != null) {
            long startTimeStamp = currentMeasureRecord.getTimeStamp();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTimeStamp);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            String monthStr = month < 10 ? "0" + month : month + "";
            String dayStr = day < 10 ? "0" + day : day + "";
            String hourStr = hour < 10 ? "0" + hour : hour + "";
            String minuteStr = minute < 10 ? "0" + minute : minute + "";
            String secondStr = second < 10 ? "0" + second : second + "";
            tvStartTime.setText("开始时间 " + year + "/" + monthStr + "/" + dayStr + " " + hourStr +
                    ":" + minuteStr + ":" + secondStr);
        } else {
            rl_currentMeasure.setVisibility(View.GONE);
        }
    }

    /**
     * 设置各种类型的点击事件
     */
    private void setOnClickListener() {
        // listView的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mActivity, ContinueMeasureRasultActivity.class);
                intent.putExtra("sign", HISTORY);
                intent.putExtra("timeStamp", arrayList.get(position).getTimeStamp());
                intent.putExtra("timeLength", arrayList.get(position).getTimeLength());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
            }
        });
        // 当前测量按钮的点击事件
        rl_currentMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ContinueMeasureRasultActivity.class);
                intent.putExtra("sign", ABNORMAL);
                if (currentMeasureRecord != null) {
                    intent.putExtra("timeStamp", currentMeasureRecord.getTimeStamp());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                } else {
                    ToastUtils.ToastMessage(mActivity, "当前没有测量计划");
                }
            }
        });

        // 年月日改变事件的监听
        ((DistoryContentActivity) getActivity()).setYearAndMonthChanged(new DistoryContentActivity.
                YearAndMonthChanged() {
            @Override
            public void yearAndMonthChanged(int[] array) {
                int ym = ((array[0] - 1970) << 8) + array[1];
                arrayList = ContinueMeasureRecordDao.getInstance(mActivity).getContinueMeasureRecord(ym);
                contiHisAdapter.setArrayList(arrayList);
                contiHisAdapter.notifyDataSetInvalidated();
            }
        });
    }
}
