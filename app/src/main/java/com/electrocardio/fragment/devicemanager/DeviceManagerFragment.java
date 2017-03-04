package com.electrocardio.fragment.devicemanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.activity.DeviceManagerActivity;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.custom.elec.SwitchButtonView;
import com.electrocardio.database.UserDao;
import com.electrocardio.util.ConstantUtils;
import com.electrocardio.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ZhangBo on 2016/04/27.
 */
public class DeviceManagerFragment extends BaseFragment {

    private ImageView ivBack;// 返回按钮
    private TextView tv_title;// 标题
    private TextView deviceLabel;// 设备标记
    private SwitchButtonView switchButton;// 切换按钮
    private RadioButton rb_big;// 大
    private RadioButton rb_middle;// 中
    private RadioButton rb_small;// 小
    private TextView hideView;// 彩蛋
    private long currentTime = 0;// 当前时间
    private int clickCount = 0;// 点击次数
    private HideViewEnableListener mHideViewEnableListener;// 彩蛋点击完毕

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_devicemanager, null);
        ivBack = (ImageView) view.findViewById(R.id.iv_titleLeft);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        deviceLabel = (TextView) view.findViewById(R.id.deviceLabel);
        switchButton = (SwitchButtonView) view.findViewById(R.id.alertButton);
        rb_big = (RadioButton) view.findViewById(R.id.rb_big);
        rb_middle = (RadioButton) view.findViewById(R.id.rb_middle);
        rb_small = (RadioButton) view.findViewById(R.id.rb_small);
        hideView = (TextView) view.findViewById(R.id.hideView);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        tv_title.setText("设备管理");
        deviceLabel.setText(UserDao.getInstance(mActivity).getDeviceId());
        setOnButtonClick();

        ConnectivityManager manager = (ConnectivityManager) mActivity.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if (activeInfo == null) {
            ToastUtils.ToastMessage(mActivity, "网络未连接,不能获取设备号");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("num", UserDao.getInstance(mActivity).getUserID());
        OkHttpUtils.post().url(ConstantUtils.HOST + ConstantUtils.OBTAINDEVICEIDANDTIME).
                params(params).
                build().
                execute(
                new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws Exception {
                        String result = response.body().string();
                        JSONObject jsonObject = new JSONObject(result);
                        final String deviceId = jsonObject.getString("device_id");
                        long endTime = jsonObject.getLong("end_time");
                        UserDao.getInstance(mActivity).updateDeviceId(deviceId);
                        UserDao.getInstance(mActivity).updateEndTime(endTime);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                deviceLabel.setText(deviceId);
                            }
                        });
                        return null;
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(Object o) {

                    }
                }
        );
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClick() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DeviceManagerActivity) mActivity).onBackPressed();
            }
        });
        // 选择按钮的选中事件
        switchButton.setOnSwitchStateChangeListener(new SwitchButtonView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean state) {

            }
        });
        // 声音大的选取事件
        rb_big.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        // 声音中的选取事件
        rb_middle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        // 声音小的选取事件
        rb_small.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        // 彩蛋的点击事件
        hideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((System.currentTimeMillis() - currentTime) < 300) {
                    clickCount += 1;
                    if (clickCount == 10)
                        if (mHideViewEnableListener != null)
                            mHideViewEnableListener.hideViewEnable();
                } else {
                    clickCount = 0;
                }
                currentTime = System.currentTimeMillis();
            }
        });
    }

    /**
     * 设置彩蛋点击完毕
     *
     * @param hideViewEnableListener
     */
    public void setHideViewEnableListener(HideViewEnableListener hideViewEnableListener) {
        mHideViewEnableListener = hideViewEnableListener;
    }

    public interface HideViewEnableListener {
        public void hideViewEnable();
    }
}
