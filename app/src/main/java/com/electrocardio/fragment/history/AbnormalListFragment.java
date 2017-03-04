package com.electrocardio.fragment.history;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.electrocardio.Adapter.AbnormalListAdapter;
import com.electrocardio.R;
import com.electrocardio.activity.AbnormalDataActivity;
import com.electrocardio.activity.ContinueMeasureRasultActivity;
import com.electrocardio.activity.DataExceptionActivity;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.database.AbnormalListDao;
import com.electrocardio.databasebean.AbnormalListBean;

import java.util.ArrayList;

/**
 * Created by ZhangBo on 2016/3/16.
 */
public class AbnormalListFragment extends BaseFragment {

    private ImageView iv_back;// 返回按钮的点击事件
    private ListView lv_list;// 异常症状列表
    private ArrayList<AbnormalListBean> abnorListBeanArr;// 异常信息列表
    private AbnormalListAdapter abnormalListAdapter;// 异常信息列表适配器
    // private AbnormalListBean abnormalListBean;
    private OnBackClickListener mOnBackClickListener;
    private long timeStamp = -1;//时间戳

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_abnormallist, null);
        iv_back = (ImageView) view.findViewById(R.id.iv_back);
        lv_list = (ListView) view.findViewById(R.id.lv_list);
        timeStamp = ((ContinueMeasureRasultActivity) mActivity).getTimeStamp();
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        abnormalListAdapter = new AbnormalListAdapter(mActivity);
        if (timeStamp != -1)
            abnorListBeanArr = AbnormalListDao.getInstance(mActivity).getAllAbnormalListBean(timeStamp);
        abnormalListAdapter.setDataList(abnorListBeanArr);
        lv_list.setAdapter(abnormalListAdapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getContext(), AbnormalDataActivity.class);
                intent.putExtra("timeStamp", abnorListBeanArr.get(position).getTimeStamp());
                intent.putExtra("abnormalDes", abnorListBeanArr.get(position).getDescription());
                intent.putExtra("filePath", abnorListBeanArr.get(position).getDataFileSrc());
                startActivity(intent);
            }
        });
        setOnButtonClickListener();
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClickListener() {
        // 返回按钮的点击事件
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBackClickListener != null)
                    mOnBackClickListener.backButtonClicked();
            }
        });
    }

    public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
        mOnBackClickListener = onBackClickListener;
    }

    public interface OnBackClickListener {
        public void backButtonClicked();
    }
}
