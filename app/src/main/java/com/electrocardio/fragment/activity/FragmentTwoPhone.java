package com.electrocardio.fragment.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.electrocardio.Bean.UserInformation;
import com.electrocardio.Bean.UserRegisterBean;
import com.electrocardio.Callback.UserRegisterCallBack;
import com.electrocardio.ObServen.SmsObserver;
import com.electrocardio.R;
import com.electrocardio.database.UserDao;
import com.electrocardio.util.CountDownTimerUtils;
import com.electrocardio.util.MobileNo;
import com.electrocardio.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by yangzheng on 2016/5/24.
 * 填写密码
 */
public class FragmentTwoPhone extends Fragment implements View.OnClickListener,CountDownTimerUtils.onDownTimerListener {

    private LinearLayout mCancle;
    private EditText mphone;
    private EditText code;
    private Button mSendCode;
    private Button mLogin;
    private UserInformation userInformation;
    public static final int MSG_RECEIVED_CODE = 1;
    private SmsObserver mObserver;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_RECEIVED_CODE) {
                String mcode = (String) msg.obj;
                //update the UI
                code.setText(mcode);
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.change_phone_layout2, container, false);
        initView(view);
        initdata();

        return view;
    }

    private void initView(View view) {
        TextView mfinish = (TextView)view.findViewById(R.id.finish_text);
        mfinish.setText("绑定手机");
        mCancle = (LinearLayout)view.findViewById(R.id.edit_button_cancle);
        mphone = (EditText)view.findViewById(R.id.edit_text_phone);
        code = (EditText)view.findViewById(R.id.edit_text_code);
        mSendCode = (Button)view.findViewById(R.id.btn_sendcode);
        mLogin = (Button)view.findViewById(R.id.button_login);
    }

    private void initdata() {
        initsms();
        mphone.setFocusable(true);
        mphone.setFocusableInTouchMode(true);
        mphone.requestFocus();

        userInformation = UserDao.getInstance(getActivity()).getUser();
        mCancle.setOnClickListener(this);
        mSendCode.setOnClickListener(this);
        mLogin.setOnClickListener(this);

    }

    private void initsms() {
        mObserver = new SmsObserver(getActivity(), mHandler);
        Uri uri = Uri.parse("content://sms");
        getActivity().getContentResolver().registerContentObserver(uri, true, mObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_button_cancle:
                mphone.setText("");
                break;
            case R.id.btn_sendcode:
                String phonenumber = mphone.getText().toString();
                SendcodeMessages(phonenumber);
                break;
            case R.id.button_login:
                String mphoneNumber = mphone.getText().toString();
                String mcode=code.getText().toString();
                if (mphoneNumber.equals("")) {
                    ToastUtils.ToastMessage(getActivity(), "手机号不能为空");
                    break;
                }
                if (mcode.equals("")) {
                    ToastUtils.ToastMessage(getActivity(), "验证码不能为空");
                    break;
                }

                if (!MobileNo.isMobileNO(mphoneNumber)) {
                    ToastUtils.ToastMessage(getActivity(), "请填写正确手机号");
                    break;
                }

                if (!MobileNo.isMobileCode(mcode)) {
                    ToastUtils.ToastMessage(getActivity(), "请填写正确验证码");
                    break;
                }


                RegisterRequest(mphoneNumber,mcode);
                break;
        }

    }

    private void SendcodeMessages(String phone) {
        if (MobileNo.isMobileNO(phone)) {
            postPhoneCade(phone);

        } else {
            ToastUtils.ToastMessage(getActivity(), "请填写正确手机号");
        }
    }

    private void postPhoneCade(String phone) {
        String url = "http://realtime.ddduo.com/realtime/AppServiceController/sendMessageCode";

        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("codeType", "03");
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                 .execute(new mCodeCallBack());
             /*   .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String s) {
                        System.out.println("s:" + s);
                    }
                });*/

    }

    private class mCodeCallBack extends UserRegisterCallBack {


        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(UserRegisterBean userRegisterBean) {
            if(userRegisterBean==null){
                ToastUtils.ToastMessage(getActivity(), "服务端异常稍候重试");
            }else{
                String Code = userRegisterBean.getStatusCode();
                if (Code.equals("010")) {
                    System.out.println("验证码" + userRegisterBean.getCodeVal());
                    DownTimeButton();
                }
                if (Code.equals("020"))
                    ToastUtils.ToastMessage(getActivity(), "允许一分钟一次 ");
                if (Code.equals("011"))
                    ToastUtils.ToastMessage(getActivity(), "手机号已经存在 ");
            }
            }



    }

    private void DownTimeButton() {

        CountDownTimerUtils mCountDownTimerUtils  =new CountDownTimerUtils(60000,1000);
        mCountDownTimerUtils.setonDownTimerListener(this);
        mCountDownTimerUtils.start();
        mSendCode.setEnabled(false);

    }
    @Override
    public void onDownTimerTick(long l) {
        long timelong = l / 1000;
        mSendCode.setText(timelong + "秒后重新发送");
    }

    @Override
    public void onDownFinish() {
        mSendCode.setEnabled(true);
        mSendCode.setText(" 重新发送 ");
    }

    private void RegisterRequest(String mphoneNumber, String mcode) {

        String url = "http://realtime.ddduo.com/realtime/AppServiceController/appLoginNum";
        Map<String, String> params = new HashMap<>();
        params.put("phone",  userInformation.getPhoneNumber());
        params.put("new_phone",mphoneNumber);
        params.put("userCode", mcode);

        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new UserRegisterCallBack() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(UserRegisterBean userRegisterBean) {

                        if(userRegisterBean==null){
                            ToastUtils.ToastMessage(getActivity(), "服务器异常稍后重试");

                        }else{
                            String statusCode = userRegisterBean.getStatusCode();
                            System.out.println("statusCode:"+statusCode);
                            if (statusCode.equals("010")) {
                                System.out.println("成功");
                                FragmentThreePhone fThree = new FragmentThreePhone(mphone.getText().toString()) {
                                    @Override
                                    public boolean onBackPressed() {
                                        return true;
                                    }
                                };

                                FragmentManager fm = getFragmentManager();
                                FragmentTransaction tx = fm.beginTransaction();
                                tx.replace(R.id.id_content, fThree, "Threephone");
                                // tx.addToBackStack(null);
                                tx.commit();

                            }
                            if (statusCode.equals("020")) {
                                ToastUtils.ToastMessage(getActivity(), "输入的验证码不正确");
                            }
                            if(statusCode.equals("060")){
                                ToastUtils.ToastMessage(getActivity(), "没有该手机号的验证码 ");
                            }
                            if (statusCode.equals("090")) {

                            }
                            if (statusCode.equals("011")) {

                            }
                        }


                    }
                });
    }

}
