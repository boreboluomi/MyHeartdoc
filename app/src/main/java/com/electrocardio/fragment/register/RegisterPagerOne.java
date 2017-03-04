package com.electrocardio.fragment.register;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.base.BaseFragment;

/**
 * Created by ZhangBo on 2016/2/22.
 */
public class RegisterPagerOne extends BaseFragment {

    private View view;
    private TextView tv_cancel;// 取消按钮的点击事件
    private Button btn_register;// 注册按钮
    private RegisterClick mRegisterClick;

    @Override
    public View initView() {
        view = View.inflate(mActivity, R.layout.fragment_registerpageone, null);
        tv_cancel = (TextView) view.findViewById(R.id.tv_right);
        btn_register = (Button) view.findViewById(R.id.btn_register);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        setOnButtonClick();
    }

    private void setOnButtonClick() {
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
                mActivity.overridePendingTransition(R.anim.activity_back_enter, R.anim.activity_back_exit);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRegisterClick != null)
                    mRegisterClick.onRegisterClicked();
            }
        });
    }

    public void setOnRegisterClick(RegisterClick registerClick) {
        mRegisterClick = registerClick;
    }

    public interface RegisterClick {
        public void onRegisterClicked();
    }
}
