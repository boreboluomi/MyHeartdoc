package com.electrocardio.fragment.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.electrocardio.Bean.UserInformation;
import com.electrocardio.Bean.UserNumberBean;
import com.electrocardio.Callback.UserNumberCallBack;
import com.electrocardio.R;
import com.electrocardio.activity.LoginRegisterActivity;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.database.UserDao;
import com.electrocardio.util.Md5;
import com.electrocardio.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by yangzheng on 2016/5/24.
 * 填写密码
 */
public abstract class FragmentThreePassword extends Fragment implements View.OnClickListener {


    private RelativeLayout mStatefinish;
    private Button mbutton;
    protected BackHandledInterface mBackHandledInterface;

    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
     */
    public abstract boolean onBackPressed();

    public interface BackHandledInterface {

        void setSelectedFragment(FragmentThreePassword selectedFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        if(!(getActivity() instanceof BackHandledInterface)){
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        }else{
            this.mBackHandledInterface = (BackHandledInterface)getActivity();
        }

        View view = inflater.inflate(R.layout.change_password_layout3, container, false);

        mStatefinish = (RelativeLayout) view.findViewById(R.id.activity_finish);
        mbutton = (Button) view.findViewById(R.id.button_success);
        getActivity().findViewById(R.id.back).setVisibility(View.GONE);
        initdata();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //告诉FragmentActivity，当前Fragment在栈顶
        mBackHandledInterface.setSelectedFragment(this);
    }
    private void initdata() {
        mbutton.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_success:
                startActivity(new Intent(getActivity(),LoginRegisterActivity.class));
                BaseApplication.getInstance().exit(false);
                break;
        }
    }

}
