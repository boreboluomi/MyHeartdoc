package com.electrocardio.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.Bean.UserInformation;
import com.electrocardio.Bean.UserNumberBean;
import com.electrocardio.Bean.UserRegisterBean;
import com.electrocardio.Callback.UserNumberCallBack;
import com.electrocardio.Callback.UserRegisterCallBack;
import com.electrocardio.R;
import com.electrocardio.base.BaseActivity;
import com.electrocardio.database.UserDao;
import com.electrocardio.util.CountDownTimerUtils;
import com.electrocardio.util.Md5;
import com.electrocardio.util.MobileNo;
import com.electrocardio.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;

/**
 * Created by yangzheng on 2016/5/16.
 * Retrieve password
 */
public class ChangephoneActivity extends BaseActivity implements View.OnClickListener,CountDownTimerUtils.onDownTimerListener{
    private RelativeLayout mTitleState1;
    private RelativeLayout mTitleState2;
    private RelativeLayout mcontentstate1;
    private RelativeLayout mcontentstate2;
    private RelativeLayout mStatefinish;
    private EditText mPassword;
    private LinearLayout mCancle;
    private Button mButton;
    private UserInformation userInformation;
    private EditText mphone;
    private EditText code;
    private Button mSendCode;
    private Button mbutton;
    private Button mSuccess;
    private LinearLayout back;

    public static void startActivity(Activity activity, int code) {
        Intent intent = new Intent(activity, ChangephoneActivity.class);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.change_phone_layout;
    }

    @Override
    protected void Initialize() {
            initView();
            initdata();

    }



    private void initView() {
        TextView  mTitleText = (TextView) findViewById(R.id.title_text);
        mTitleText.setText("绑定手机");
        TextView  mstateText = (TextView) findViewById(R.id.text_password);
        mstateText.setText("绑定手机");
        TextView  mfinish = (TextView) findViewById(R.id.finish_text);
        mfinish.setText("绑定手机");
        mCancle = (LinearLayout) findViewById(R.id.edit_button_cancle);
        mButton = (Button) findViewById(R.id.btn_test);
        mPassword = (EditText) findViewById(R.id.new_password);

        mTitleState1 = (RelativeLayout) findViewById(R.id.title_state1);
        mTitleState2 = (RelativeLayout) findViewById(R.id.title_state2);
        mcontentstate1 = (RelativeLayout) findViewById(R.id.contetn_state1);
        mcontentstate2 = (RelativeLayout) findViewById(R.id.contetn_state2);
        mStatefinish = (RelativeLayout) findViewById(R.id.activity_finish);

        mphone = (EditText) findViewById(R.id.edit_text_phone);
        code = (EditText) findViewById(R.id.edit_text_code);
        mSendCode = (Button) findViewById(R.id.btn_sendcode);
        mbutton = (Button) findViewById(R.id.button_login);

        mSuccess = (Button) findViewById(R.id.button_success);
        back = (LinearLayout) findViewById(R.id.back);



    }

    private void initdata() {
        userInformation = UserDao.getInstance(this).getUser();
        mCancle.setOnClickListener(this);
        mButton.setOnClickListener(this);
        mSendCode.setOnClickListener(this);
        mbutton.setOnClickListener(this);
        mSuccess.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_button_cancle:
                mPassword.setText("");
                break;
            case R.id.btn_test:
                String password = mPassword.getText().toString();
                String phone = userInformation.getPhoneNumber();
                SendcodeMessages(phone, password);
                break;
            case R.id.btn_sendcode:
                String phonenumber = mphone.getText().toString();
                SendcodeMessages(phonenumber);
                break;

            case R.id.button_login:
                String mphoneNumber = mphone.getText().toString();
                String mcode=code.getText().toString();
                if (mphoneNumber.equals("")) {
                    ToastUtils.ToastMessage(this, "手机号不能为空");
                    break;
                }
                if (mcode.equals("")) {
                    ToastUtils.ToastMessage(this, "验证码不能为空");
                    break;
                }

                if (!MobileNo.isMobileNO(mphoneNumber)) {
                    ToastUtils.ToastMessage(this, "请填写正确手机号");
                    break;
                }

                if (!MobileNo.isMobileCode(mcode)) {
                    ToastUtils.ToastMessage(this, "请填写正确验证码");
                    break;
                }


                RegisterRequest(mphoneNumber,mcode);

                break;

            case R.id.button_success:
                startActivity(new Intent(ChangephoneActivity.this,LoginRegisterActivity.class));
                finish();
                break;
            case R.id.back:
                finish();
                break;
        }

    }

    private void RegisterRequest(String mphoneNumber, String mcode) {

        String url = "http://realtime.ddduo.com/realtime/AppServiceController/appLoginNum";
        Map<String, String> params = new HashMap<>();
        params.put("phone",  userInformation.getPhoneNumber());
        params.put("new_phone",mphoneNumber);
        params.put("userCode",mcode);

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
                        String statusCode = userRegisterBean.getStatusCode();
                        if(statusCode.equals("010")){
                            mStatefinish.setVisibility(View.VISIBLE);
                            mTitleState2.setVisibility(View.GONE);
                            mcontentstate2.setVisibility(View.GONE);
                            back.setVisibility(View.GONE);
                        }
                        if(statusCode.equals("020")){

                        }
                        if(statusCode.equals("090")){

                        }
                        if(statusCode.equals("011")){

                        }
                    }
                });
    }


    private void SendcodeMessages(String phone,String password) {
        if (!password.equals("")) {
            postLoginUser(phone, password);

        } else {
            ToastUtils.ToastMessage(this, "密码不能为空");
        }
    }
    private void SendcodeMessages(String phone) {
        if (MobileNo.isMobileNO(phone)) {
            postPhoneCade(phone);

        } else {
            ToastUtils.ToastMessage(this, "请填写正确手机号");
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



    private class mUserNumberCallBack extends UserNumberCallBack {


        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(UserNumberBean userNumberBean) {
            String Code = userNumberBean.getStatusCode();
            if(Code.equals("1")){
                mTitleState1.setVisibility(View.GONE);
                mcontentstate1.setVisibility(View.GONE);
                mTitleState2.setVisibility(View.VISIBLE);
                mcontentstate2.setVisibility(View.VISIBLE);
            }
            if (Code.equals("000"))
                ToastUtils.ToastMessage(ChangephoneActivity.this, "登录失败");
            if (Code.equals("010"))
                ToastUtils.ToastMessage(ChangephoneActivity.this, "用户不存在 ");
            if (Code.equals("020"))
                ToastUtils.ToastMessage(ChangephoneActivity.this, "密码错误");

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
                        System.out.println("s:"+s);
                    }
                });*/

    }



    private class mCodeCallBack extends UserRegisterCallBack {


        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(UserRegisterBean userRegisterBean) {
            String Code = userRegisterBean.getStatusCode();

            if (Code.equals("010")) {
                System.out.println("验证码" + userRegisterBean.getCodeVal());
                DownTimeButton();
            }
            if (Code.equals("020"))
                ToastUtils.ToastMessage(ChangephoneActivity.this, "允许一分钟一次 ");
            if (Code.equals("011"))
                ToastUtils.ToastMessage(ChangephoneActivity.this, "手机号已经存在 ");
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
}
