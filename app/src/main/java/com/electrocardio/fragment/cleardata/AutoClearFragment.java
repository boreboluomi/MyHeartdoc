package com.electrocardio.fragment.cleardata;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.database.AbnormalListDao;
import com.electrocardio.database.ContinueMeasureRecordDao;
import com.electrocardio.databasebean.AbnormalListBean;
import com.electrocardio.databasebean.ContinueMeasureRecord;
import com.electrocardio.util.DeleteFileUtil;
import com.electrocardio.util.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ZhangBo on 2016/2/24.
 */
public class AutoClearFragment extends BaseFragment {

    private ImageView iv_back;// 返回按钮
    private TextView tv_title;// 标题
    private RelativeLayout rl_center;
    private ImageView iv_broom_df;// 清理动画背景
    private ImageView iv_broom_clear;// 清理动画清理图片
    private TextView tv_clearState;// 清理状态
    private TextView tv_clearTip;// 清理建议
    private Button btn_clear;// 自动清理按钮
    private int needClearItem = 0;// 需要清理的异常条目
    private RelativeLayout rl_handClear;// 手动清理
    private RotateAnimation rotateAnimation;// 清理动画
    private HandClearClick mHandClearClick;// 手动清理点击事件的回调接口
    private final int STARTCLEARDATA = 1;// 开始清理数据信号
    private final int UPDATECLEARSTATE = 2;// 更新清理状态
    private final int DATACLEAROVER = 3;// 数据清理完毕
    private final int DELETEFILE = 4;// 删除文件
    private MyHandler myHandler;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_autoclear, null);
        iv_back = (ImageView) view.findViewById(R.id.iv_titleLeft);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        rl_center = (RelativeLayout) view.findViewById(R.id.rl_center);
        iv_broom_df = (ImageView) view.findViewById(R.id.iv_broom_bg_default);
        iv_broom_clear = (ImageView) view.findViewById(R.id.iv_broom_bg_rotation);
        tv_clearState = (TextView) view.findViewById(R.id.tv_clearState);
        tv_clearTip = (TextView) view.findViewById(R.id.tv_clearTip);
        btn_clear = (Button) view.findViewById(R.id.btn_clear);
        rl_handClear = (RelativeLayout) view.findViewById(R.id.rl_handClear);
        return view;
    }

    @Override
    public void initData() {
        tv_title.setText("数据清理");
        myHandler = new MyHandler(this) {
            @Override
            protected void switchCase(Message message) {
                switch (message.what) {
                    case STARTCLEARDATA:// 开始清理数据
                        clearDataAnimation();
                        break;
                    case UPDATECLEARSTATE:// 更新清理状态
                        updateClearState((Integer) message.obj);
                        break;
                    case DATACLEAROVER:// 数据清理完毕
                        dataClearOver();
                        break;
                    case DELETEFILE:// 删除文件
                        String filePath = (String) message.obj;
                        if (!filePath.equals(""))
                            deleteFile(filePath);
                        break;
                    default:
                        break;
                }
            }
        };
        needClearItem = 0;
        ArrayList<ContinueMeasureRecord> arrayList = ContinueMeasureRecordDao.getInstance(mActivity).
                getSubmitedConMeaRecord();
        ArrayList<AbnormalListBean> beanList;
        for (ContinueMeasureRecord record : arrayList) {
            beanList = AbnormalListDao.getInstance(mActivity).
                    getAllSubmitedAbnormalListBean(record.getTimeStamp());
            needClearItem += beanList.size();
        }

        setOnButtonClick();
    }

    @Override
    public void onStart() {
        super.onStart();
        float usedSize = ContinueMeasureRecordDao.getInstance(mActivity).getAllSubimtedDataSize();
        String usedStr = DeleteFileUtil.getOccupySize(usedSize);
        tv_clearState.setText(needClearItem + "个清理项,共" + usedStr);
        if (usedSize == 0f || needClearItem == 0) {
            rl_center.setBackgroundColor(Color.parseColor("#2dc021"));
            btn_clear.setEnabled(true);
            btn_clear.setClickable(true);
            tv_clearState.setText("非常干净");
            tv_clearTip.setText("仅保留本月未上传的异常数据");
            tv_clearTip.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    /**
     * 设置清理状态的背景色
     *
     * @param progress
     */
    public void setClearBg(int progress) {
        if (progress >= 80)
            rl_center.setBackgroundColor(Color.parseColor("#f98c71"));
        else if (progress >= 50)
            rl_center.setBackgroundColor(Color.parseColor("#b59db0"));
        else if (progress >= 25)
            rl_center.setBackgroundColor(Color.parseColor("#7aaeef"));
        else rl_center.setBackgroundColor(Color.parseColor("#2dc021"));
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClick() {
        // 返回按钮的点击事件
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
                mActivity.overridePendingTransition(R.anim.activity_back_enter,
                        R.anim.activity_back_exit);
            }
        });
        // 清理按钮的点击事件
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_clearState.getText().toString().equals("清理完毕") || tv_clearState.getText().
                        toString().equals("非常干净")) {
                    ToastUtils.ToastMessage(mActivity, "不需要清理");
                    return;
                }
                btn_clear.setClickable(false);
                btn_clear.setEnabled(false);
                myHandler.obtainMessage(STARTCLEARDATA).sendToTarget();
            }
        });
        // 手动清理的点击事件
        rl_handClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHandClearClick != null)
                    mHandClearClick.onHandCliearClicked();
            }
        });
    }

    private ExecutorService executorService = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    /**
     * 启动清理数据的动画
     */
    protected void clearDataAnimation() {
        final ArrayList<ContinueMeasureRecord> arrayList = ContinueMeasureRecordDao.getInstance(mActivity).
                getSubmitedConMeaRecord();
        iv_broom_clear.setVisibility(View.VISIBLE);
        iv_broom_df.setVisibility(View.INVISIBLE);
        rotateAnimation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setDuration(500);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        iv_broom_clear.setAnimation(rotateAnimation);
        rotateAnimation.start();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                for (ContinueMeasureRecord record : arrayList) {
                    ArrayList<AbnormalListBean> beanList = AbnormalListDao.getInstance(mActivity).
                            getAllSubmitedAbnormalListBean(record.getTimeStamp());
                    String path = "";
                    if (beanList.size() > 0) {
                        path = beanList.get(0).getDataFileSrc().substring(0, beanList.get(0).getDataFileSrc().lastIndexOf("/"));
                        path = path.substring(0, path.lastIndexOf("/"));
                    }
                    ContinueMeasureRecordDao.getInstance(mActivity).deleteOneContinueMeasureRecord(record.getTimeStamp());
                    myHandler.obtainMessage(DELETEFILE, path).sendToTarget();
                }
                myHandler.obtainMessage(DATACLEAROVER).sendToTarget();
            }
        });
    }

    /**
     * 根据文件路径删除文件
     *
     * @param filePath
     */
    private void deleteFile(final String filePath) {
        // 删除文件
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                new DeleteFileUtil().deleteDirectory(filePath);
            }
        });
    }

    /**
     * 垃圾清理完毕
     */
    private void dataClearOver() {
        if (rotateAnimation != null) {
            rotateAnimation.cancel();
        }
        rl_center.setBackgroundColor(Color.parseColor("#2dc021"));
        iv_broom_clear.clearAnimation();
        iv_broom_clear.setVisibility(View.INVISIBLE);
        iv_broom_df.setVisibility(View.VISIBLE);
        btn_clear.setEnabled(true);
        btn_clear.setClickable(true);
        tv_clearState.setText("清理完毕");
        tv_clearTip.setText("继续保持清理好习惯哦");
        tv_clearTip.setTextColor(Color.parseColor("#ffffff"));
    }

    /**
     * 更新清理状态
     *
     * @param progress
     */
    private void updateClearState(int progress) {
        setClearBg(100 - progress);
    }

    public void setOnHandClearClick(HandClearClick handClearClick) {
        mHandClearClick = handClearClick;
    }

    public interface HandClearClick {
        public void onHandCliearClicked();
    }

    static abstract class MyHandler extends Handler {
        private WeakReference<Fragment> mFragmentReference;

        MyHandler(Fragment fragment) {
            mFragmentReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            Fragment fragment = mFragmentReference.get();
            if (fragment != null)
                switchCase(msg);
        }

        protected abstract void switchCase(Message message);
    }
}
