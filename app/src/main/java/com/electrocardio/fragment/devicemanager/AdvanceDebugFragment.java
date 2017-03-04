package com.electrocardio.fragment.devicemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.base.BaseFragment;
import com.electrocardio.custom.elec.SwitchButtonView;
import com.electrocardio.util.ConstantUtils;

/**
 * Created by ZhangBo on 2016/04/27.
 */
public class AdvanceDebugFragment extends BaseFragment {

    private OnBackClickedListener mOnBackClickedListener;// 返回按钮点击的监听事件
    private ImageView ivBack;// 返回按钮
    private TextView tv_title;// 标题
    private RelativeLayout rl_debug;// 调试
    private EditText et_password;// 密码输入框
    private Button confirm;// 验证密码
    private RelativeLayout rl_content;// 内容
    private SwitchButtonView mVButton;// 1mv校准button
    private SwitchButtonView statistical;// 丢包统计
    private SharedPreferences sharedPreferences;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_advancedebug, null);
        ivBack = (ImageView) view.findViewById(R.id.iv_titleLeft);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        rl_debug = (RelativeLayout) view.findViewById(R.id.rl_debug);
        et_password = (EditText) view.findViewById(R.id.et_password);
        confirm = (Button) view.findViewById(R.id.confirm);
        rl_content = (RelativeLayout) view.findViewById(R.id.rl_content);
        mVButton = (SwitchButtonView) view.findViewById(R.id.mVButton);
        statistical = (SwitchButtonView) view.findViewById(R.id.statistical);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        tv_title.setText("高级调试");
        sharedPreferences = mActivity.getSharedPreferences(ConstantUtils.APPNAME, Context.MODE_PRIVATE);
        mVButton.setState(sharedPreferences.getBoolean(ConstantUtils.ONEMV, false));
        statistical.setState(sharedPreferences.getBoolean(ConstantUtils.DIUBAO, false));
        setOnButtonClick();
    }

    @Override
    public void onStart() {
        super.onStart();
        et_password.setText("");
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClick() {
        // 返回按钮的点击事件
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBackClickedListener != null)
                    mOnBackClickedListener.onBackClicked();
            }
        });

        // 验证按钮的点击事件
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_password.getText().toString().trim().equals("12345678")) {
                    rl_debug.setVisibility(View.INVISIBLE);
                    rl_content.setVisibility(View.VISIBLE);
                }
            }
        });
        // 1mv校准按钮的选取事件
        mVButton.setOnSwitchStateChangeListener(new SwitchButtonView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean state) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(ConstantUtils.ONEMV, state);
                editor.commit();
            }
        });
        // 丢包统计按钮的选取事件
        statistical.setOnSwitchStateChangeListener(new SwitchButtonView.OnSwitchStateChangeListener() {
            @Override
            public void onSwitchStateChange(boolean state) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(ConstantUtils.DIUBAO, state);
                editor.commit();
            }
        });
    }

    /**
     * 设置返回按钮的点击事件的监听器
     *
     * @param onBackClickedListener
     */
    public void setOnBackClickedListener(OnBackClickedListener onBackClickedListener) {
        mOnBackClickedListener = onBackClickedListener;
    }

    public interface OnBackClickedListener {
        public void onBackClicked();
    }

}
