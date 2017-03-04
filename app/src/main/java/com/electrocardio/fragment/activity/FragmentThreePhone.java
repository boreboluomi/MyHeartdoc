package com.electrocardio.fragment.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.activity.LoginRegisterActivity;
import com.electrocardio.base.BaseApplication;

/**
 * Created by yangzheng on 2016/5/24.
 * 填写密码
 */
public abstract class FragmentThreePhone extends Fragment implements View.OnClickListener {


    private  String phone;
    private RelativeLayout mStatefinish;
    private Button mbutton;
    protected BackHandledInterface mBackHandledInterface;
    private TextView successTitle;
    private TextView successText;

    public FragmentThreePhone(String phoneNumber) {
        this.phone=phoneNumber;
    }

    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
     */
    public abstract boolean onBackPressed();

    public interface BackHandledInterface {

        void setSelectedFragment(FragmentThreePhone selectedFragment);
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
        successTitle = (TextView) view.findViewById(R.id.success_title);
        successText = (TextView) view.findViewById(R.id.success_text);
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
        successTitle.setText("手机绑定成功");
        String mphone=phone.substring(0,3)+"****"+phone.substring(7,phone.length());
        successText.setText("您绑定的手机为:" +mphone);

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
