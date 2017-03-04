package com.electrocardio.fragment.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.electrocardio.Bean.UserInformation;
import com.electrocardio.Bean.UserNumberBean;
import com.electrocardio.Callback.UserNumberCallBack;
import com.electrocardio.R;
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
public class FragmentOnePhone extends Fragment implements View.OnClickListener {


    private LinearLayout mCancle;
    private Button mButton;
    private EditText mPassword;
    private UserInformation userInformation;
    private TextView mTitletext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.change_password_layout, container, false);

        mCancle = (LinearLayout)view.findViewById(R.id.edit_button_cancle);
        mButton = (Button)view.findViewById(R.id.btn_test);
        mPassword = (EditText)view.findViewById(R.id.new_password);
        mTitletext = (TextView) view.findViewById(R.id.text_password);

        initdata();

        return view;
    }

    private void initdata() {
        mTitletext.setText("绑定手机");
        userInformation = UserDao.getInstance(getActivity()).getUser();
        mCancle.setOnClickListener(this);
        mButton.setOnClickListener(this);
        mPassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.edit_button_cancle:
                mPassword.setText("");
                break;
            case R.id.btn_test:
                if(!checkNetWork(getActivity())){
                    ToastUtils.ToastMessage(getActivity(), "网络不可用");

                }else{
                    String password = mPassword.getText().toString();
                    String phone = userInformation.getPhoneNumber();
                    SendcodeMessages(phone,password.toLowerCase());
                }
                break;
        }
    }


    private void SendcodeMessages(String phone,String password) {
        if (!password.equals("")) {
            postLoginUser(phone, password);

        } else {
            ToastUtils.ToastMessage(getActivity(), "密码不能为空");
        }
    }

    private void postLoginUser(String phone, String mPassWord) {
        String url = "http://realtime.ddduo.com/realtime/AppServiceController/appLogin";
        System.out.println("name" + phone);
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("password", Md5.getMD5Str(mPassWord));
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new mUserNumberCallBack());


    }
    private  boolean checkNetWork(Context context) {
        boolean flag = false;


        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null)
            flag = cwjManager.getActiveNetworkInfo().isAvailable();
        return flag;
    }
    private class mUserNumberCallBack extends UserNumberCallBack {


        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(UserNumberBean userNumberBean) {
            String Code = userNumberBean.getStatusCode();
            if(Code.equals("1")){
                FragmentTwoPhone fTwo = new FragmentTwoPhone();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction tx = fm.beginTransaction();
                tx.replace(R.id.id_content, fTwo, "TWOPhone");
               // tx.addToBackStack(null);
                tx.commit();
            }
            if (Code.equals("000"))
                ToastUtils.ToastMessage(getActivity(), "登录失败");
            if (Code.equals("010"))
                ToastUtils.ToastMessage(getActivity(), "用户不存在 ");
            if (Code.equals("020"))
                ToastUtils.ToastMessage(getActivity(), "密码错误");

        }
    }


}
