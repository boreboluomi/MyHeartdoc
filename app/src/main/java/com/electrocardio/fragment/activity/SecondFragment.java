package com.electrocardio.fragment.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.electrocardio.Adapter.SixSecondMeasureAdapter;
import com.electrocardio.Bean.SixSecondMeasureBean;
import com.electrocardio.R;
import com.electrocardio.activity.DistoryContentActivity;
import com.electrocardio.activity.SixtySecondsMeaReaultActivity;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.database.SixtySecondsRecordDao;
import com.electrocardio.database.SixtySecondsYMDDao;
import com.electrocardio.databasebean.SixtySecondsYMD;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/2/22.
 */
public class SecondFragment extends BaseFragment {
    private ArrayList<SixSecondMeasureBean> mDatas = new ArrayList<>();
    private ListView mListView;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.second_fragment, null);
        mListView = (ListView) view.findViewById(R.id.listView);
        final SixSecondMeasureAdapter adapter = new SixSecondMeasureAdapter(getActivity());
        initDatas();
        ((DistoryContentActivity) getActivity()).setYearAndMonthChanged(new DistoryContentActivity.YearAndMonthChanged() {
            @Override
            public void yearAndMonthChanged(int[] array) {
                updataContentData(((DistoryContentActivity) getActivity()).getYear(),
                        ((DistoryContentActivity) getActivity()).getMonth());
                adapter.setSixSecondMeasureList(mDatas);
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setSixSecondMeasureList(mDatas);
        mListView.setAdapter(adapter);
        setOnButtonClickListener();
        return view;
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mDatas.get(position).isDayOfMonth()) {
                    Intent intent = new Intent(getActivity(), SixtySecondsMeaReaultActivity.class);
                    intent.putExtra("timeStamp", mDatas.get(position).getTimeStamp());
                    intent.putExtra("dataFileSrc", mDatas.get(position).getDataFileSrc());
                    intent.putExtra("timeSpace", mDatas.get(position).getTimeSpace());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        updataContentData(((DistoryContentActivity) getActivity()).getYear(),
                ((DistoryContentActivity) getActivity()).getMonth());
    }

    /**
     * 更新数据内容
     *
     * @param year
     * @param month
     */
    private void updataContentData(int year, int month) {
        mDatas.clear();
        SixtySecondsYMDDao sixtySecondsYMD = SixtySecondsYMDDao.getInstance(getActivity());
        SixtySecondsRecordDao sixtySecondsRecordDao = SixtySecondsRecordDao.getInstance(getActivity());
        ArrayList<SixtySecondsYMD> array = sixtySecondsYMD.getAllDayOnOneMonth(year, month);
        for (SixtySecondsYMD data : array) {
            for (SixSecondMeasureBean bean : sixtySecondsRecordDao.getRecoredBeanByYMD(
                    data.getYearAndMonth(), data.getDay())) {
                mDatas.add(bean);
            }
        }
    }
}
