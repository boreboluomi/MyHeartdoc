package com.electrocardio.activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.electrocardio.Bean.UserRegisterBean;
import com.electrocardio.Callback.UserRegisterCallBack;
import com.electrocardio.ObServen.SmsObserver;
import com.electrocardio.R;
import com.electrocardio.base.BaseActivity;
import com.electrocardio.database.UserDao;
import com.electrocardio.util.ConstantUtils;
import com.electrocardio.util.CountDownTimerUtils;
import com.electrocardio.util.Md5;
import com.electrocardio.util.MobileNo;
import com.electrocardio.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;


/**
 * Created by yangzheng on 2015/12/31.
 * Register UI
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, CountDownTimerUtils.onDownTimerListener {

    private EditText mPhone;
    private EditText mCode = null;
    private Button mSendCode;
    private Button mLoginButton;
    public static final int MSG_RECEIVED_CODE = 1;
    private EditText mpassword;
    private EditText mSpassword;
    private TextView mPhoneState;
    private CheckBox mCheckBox;
    private SmsObserver mObserver;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_RECEIVED_CODE) {
                String code = (String) msg.obj;
                //update the UI
                mCode.setText(code);
            }
            if(msg.what ==2){
                ToastUtils.ToastMessage(RegisterActivity.this, "手机号已注册");
            }
            if(msg.what ==3){
                ToastUtils.ToastMessage(RegisterActivity.this,"验证码已发送");
            }
            if(msg.what ==5){
                ToastUtils.ToastMessage(RegisterActivity.this,"验证码不一致");
            }
            if(msg.what ==6){
                ToastUtils.ToastMessage(RegisterActivity.this,"注册失败");
            }
            if(msg.what ==7){
                ToastUtils.ToastMessage(RegisterActivity.this,"找不到该手机号的验证码");
            }
            if(msg.what ==8){
                ToastUtils.ToastMessage(RegisterActivity.this,"验证码超时");
            }
            if(msg.what ==9){
                ToastUtils.ToastMessage(RegisterActivity.this,"服务端异常稍候重试");
            }
        }
    };
   private String codeVal;
    @Override
    protected int getLayoutID() {
        return R.layout.activity_register;
    }

    @Override
    protected void Initialize() {
        initView();
        initData();
    }


    private void initView() {

        mPhone = (EditText) findViewById(R.id.et_phoneOrAddress);
        mCode = (EditText) findViewById(R.id.et_authCode);
        mSendCode = (Button) findViewById(R.id.btn_obtainAuthCode);
        mLoginButton = (Button) findViewById(R.id.btn_register);
        mpassword = (EditText) findViewById(R.id.et_password);
        mSpassword = (EditText) findViewById(R.id.et_confirmPwd);
        mPhoneState = (TextView) findViewById(R.id.phoneState);
        TextView mright= (TextView) findViewById(R.id.tv_right);
        mCheckBox = (CheckBox) findViewById(R.id.checkBox);
        mright.setOnClickListener(this);
        mSendCode.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
       // mPhone.addTextChangedListener(textWatcher);

     /*   mpassword.addTextChangedListener(new TextWatcher()
        {
            int index = 0;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                System.out.println("b:" + s.toString());
               // TODO Auto-generated method stub
                mpassword.removeTextChangedListener(this);//解除文字改变事件
                index = mpassword.getSelectionStart();//获取光标位置
                mpassword.setText(s.toString().toLowerCase());//转换
                mpassword.setSelection(index);//重新设置光标位置
                mpassword.addTextChangedListener(this);//重新绑定事件
                System.out.println("A:" + s.toString());
               // Log.i("mylog", s.toString());

            }
        });*/

    }

    private void initData() {
        mObserver = new SmsObserver(RegisterActivity.this, mHandler);
        Uri uri = Uri.parse("content://sms");
        getContentResolver().registerContentObserver(uri, true, mObserver);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mObserver);
    }

    @Override
    public void onClick(View view) {
        String phone = mPhone.getText().toString();
        switch (view.getId()) {
            case R.id.btn_obtainAuthCode:
                if(!checkNetWork(this)){
                    ToastUtils.ToastMessage(this, "请链接网络");
                    break;
                }
                if (MobileNo.isMobileNO(phone)) {
                    postPhoneCade(phone);

                } else {
                    ToastUtils.ToastMessage(this, "请填写正确手机号");
                }
                break;

            case R.id.btn_register:
                String password = mpassword.getText().toString();
                String spassword = mSpassword.getText().toString();
                String code = mCode.getText().toString();


            //   ToastUtils.ToastMessage(this, "是否："+ getPassword(password)+","+getPasswordnull(password));
                if(!checkNetWork(this)){
                    ToastUtils.ToastMessage(this, "请链接网络");
                    break;
                }

                if (phone.equals("")) {
                    ToastUtils.ToastMessage(this, "手机号不能为空");
                    break;
                }
               if (code.equals("")) {
                    ToastUtils.ToastMessage(this, "验证码不能为空");
                    break;
                }
                if (password.equals("") || spassword.equals("")) {
                    ToastUtils.ToastMessage(this, "请填写密码");
                    break;
                }
                if(password.length()<6||spassword.length()<6){
                    ToastUtils.ToastMessage(this, "密码必须大于6位");
                    break;
                }
                if(!getPassword(password)||!getPassword(spassword)){
                    ToastUtils.ToastMessage(this, "密码格式不正确");
                    break;
                }

                if (!password.toLowerCase().equals(spassword.toLowerCase())) {
                    ToastUtils.ToastMessage(this, "两次密码输入不一致");
                    break;
                }

                if (!MobileNo.isMobileNO(phone)) {
                    ToastUtils.ToastMessage(this, "请填写正确手机号");
                    break;
                }
                if (!mCheckBox.isChecked()) {
                    ToastUtils.ToastMessage(this, "请同意服务条款");
                    break;
                }
                if (!MobileNo.isMobileCode(code)) {
                    ToastUtils.ToastMessage(this, "请填写正确验证码");
                    break;
                }

                if(mPhoneState.getText().toString().equals("已注册")){
                    ToastUtils.ToastMessage(this, "手机号已注册");
                    break;
                }


                RegisterRequest(phone,password.toLowerCase(),code);
                break;

            case R.id.tv_right:
                finish();
                break;

        }

    }
  private void RegisterRequest(String phone, String password, String code)
    {
        System.out.println("用户填写验证码:" +code);

        String url = "http://realtime.ddduo.com/realtime/AppServiceController/appRegister";
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("password",Md5.getMD5Str(password));
        params.put("userCode", code);
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new RegisterResult());
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

    public void postPhoneCade(String phone)
    {

        String url = ConstantUtils.HOST+"sendMessageCode";

        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("codeType", "01");
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                 .execute(new mUserNumberCallBack());
               /* .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String s) {
                        System.out.println("s:" + s);
                    }
                });*/
    }



    private class mUserNumberCallBack extends UserRegisterCallBack {

        @Override
        public void onError(Call call, Exception e) {
            mHandler.sendEmptyMessage(9);
        }

        @Override
        public void onResponse(UserRegisterBean userRegisterBean) {
            if (userRegisterBean==null){
                mHandler.sendEmptyMessage(9);
            }else{
                String statusCode = userRegisterBean.getStatusCode();

                if(statusCode.equals("010")){
                    //倒计时
                    codeVal = userRegisterBean.getCodeVal();
                    DownTimeButton(codeVal);
                    System.out.println("验证码已发送:" + codeVal);
                    mHandler.sendEmptyMessage(3);
                }
                if(statusCode.equals("090")){
                    mHandler.sendEmptyMessage(2);
                }
            }


        }


        }

    private class RegisterResult extends UserRegisterCallBack {

        @Override
        public void onError(Call call, Exception e) {
            mHandler.sendEmptyMessage(9);
        }

        @Override
        public void onResponse(UserRegisterBean userRegisterBean) {
            String statusCode = userRegisterBean.getStatusCode();
            String mToken=userRegisterBean.getUsertoken();
            System.out.println("statusCode:"+statusCode);
            System.out.println("token:"+mToken);
            if (statusCode.equals("000")){
                System.out.println("系统错误");
                mHandler.sendEmptyMessage(6);
            }
            if(statusCode.equals("010")){
                System.out.println("手机号已注册");
                mHandler.sendEmptyMessage(2);
            }
            if(statusCode.equals("020")){
                mHandler.sendEmptyMessage(5);
            }
            if(statusCode.equals("030")){
                String phone = mPhone.getText().toString();
                UserDao.getInstance(RegisterActivity.this).addUser(userRegisterBean.getNum(), phone, "");
                UserDao.getInstance(RegisterActivity.this).updateToken(mToken);
                Intent intent = new Intent(RegisterActivity.this, UserInformationActivity.class);
                intent.putExtra("username",phone);
                System.out.println("注册成功:"+phone);
                startActivity(intent);
                finish();
            }
            if(statusCode.equals("040")){
                mHandler.sendEmptyMessage(7);
            }

            if(statusCode.equals("050")){
                mHandler.sendEmptyMessage(8);
            }

        }


    }
   /* private boolean getPassword(String userName){
        String regEx = "^[0-9_][a-zA-Z][a-zA-Z0-9_]{5,19}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(userName);
        return matcher.matches();
    }*/

//
  /*  private boolean getPassword(String userName) {
    String regEx = "^[a-zA-Z0-9]{6,19}$";
    Pattern pattern = Pattern.compile(regEx);
    Matcher matcher = pattern.matcher(userName);
    return matcher.matches();
    }*/
/*
    public static boolean getPassword(String password) {
        String string = "^[\u4E00-\u9FA5A-Za-z0-9`~!@#$%^&*()+=|{}':;',.<>/?~@#￥%……&;*——+|{}【】‘；：”“’。，、？|-]+$";
        Pattern mPattern = Pattern.compile(string);
        Matcher matcher = mPattern.matcher(password);
        return matcher.matches();
    }*/
    public static boolean getPassword(String password) {
    String string = "^[Za-z0-9`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]+$";
    Pattern mPattern = Pattern.compile(string);
    Matcher matcher = mPattern.matcher(password);
    return matcher.matches();
}

    private void DownTimeButton(String codeVal) {

        if(!codeVal.equals("")){
            CountDownTimerUtils mCountDownTimerUtils  =new CountDownTimerUtils(60000,1000);
            mCountDownTimerUtils.setonDownTimerListener(this);
            mCountDownTimerUtils.start();
            mSendCode.setEnabled(false);
        }


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

    private  boolean checkNetWork(Context context) {
        boolean flag = false;


        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null)
            flag = cwjManager.getActiveNetworkInfo().isAvailable();
        return flag;
    }
}




