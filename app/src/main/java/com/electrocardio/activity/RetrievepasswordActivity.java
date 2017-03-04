package com.electrocardio.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.electrocardio.Bean.UserRegisterBean;
import com.electrocardio.Callback.UserRegisterCallBack;
import com.electrocardio.ObServen.SmsObserver;
import com.electrocardio.R;
import com.electrocardio.base.BaseActivity;
import com.electrocardio.database.UserDao;
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
 * Created by yangzheng on 2016/5/16.
 * Retrieve password
 */
public class RetrievepasswordActivity extends BaseActivity implements View.OnClickListener,CountDownTimerUtils.onDownTimerListener{
    private static int mcode;
    private EditText mPhone;
    private EditText mCode;
    private LinearLayout mCancle;
    private Button mSendcode;
    private Button mSendtext;
    private RelativeLayout mTitleStatestart;
    private RelativeLayout mTitleStatestop;
    private RelativeLayout mContentStart;
    private RelativeLayout mContentStop;
    private Boolean isLogin=false;
    public static final int MSG_RECEIVED_CODE = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_RECEIVED_CODE) {
                String code = (String) msg.obj;
                //update the UI
                mCode.setText(code);
            }
            if(msg.what ==2){
                ToastUtils.ToastMessage(RetrievepasswordActivity.this, "手机号已注册");
            }
            if(msg.what ==3){
                ToastUtils.ToastMessage(RetrievepasswordActivity.this,"验证码已发送");
            }
            if(msg.what ==5){
                ToastUtils.ToastMessage(RetrievepasswordActivity.this,"验证码不一致");
            }
            if(msg.what ==6){
                ToastUtils.ToastMessage(RetrievepasswordActivity.this,"注册失败");
            }
            if(msg.what ==7){
                ToastUtils.ToastMessage(RetrievepasswordActivity.this,"找不到该手机号的验证码");
            }
            if(msg.what ==8){
                ToastUtils.ToastMessage(RetrievepasswordActivity.this,"验证码超时");
            }
            if(msg.what ==9){
                ToastUtils.ToastMessage(RetrievepasswordActivity.this,"请勿重复发送（1分钟每条）");
            }
            if(msg.what ==10){
                ToastUtils.ToastMessage(RetrievepasswordActivity.this,"用户不存在");
            }
        }
    };
    private String codeVal;
    private EditText password;
    private EditText mpassword;
    private Button mButtonLogin;
    private String mphone;
    private RelativeLayout mContentfinish;
    private Button mButtonSuccess;
    private LinearLayout back;
    private SmsObserver mObserver;
    public static void startActivity(Activity activity, int code) {
        mcode = code;
        Intent intent = new Intent(activity, RetrievepasswordActivity.class);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.retrieve_password_layout;
    }

    @Override
    protected void Initialize() {
            initView();
            initdata();

    }



    private void initView() {
        back = (LinearLayout) findViewById(R.id.back);
        mTitleStatestart = (RelativeLayout) findViewById(R.id.activity_title_state1);
        mTitleStatestop = (RelativeLayout) findViewById(R.id.activity_title_state2);
        mContentStart = (RelativeLayout) findViewById(R.id.activity_content1);
        mContentStop = (RelativeLayout) findViewById(R.id.activity_content2);
        mContentfinish = (RelativeLayout) findViewById(R.id.activity_finish);
        password = (EditText) findViewById(R.id.password);
        mpassword = (EditText) findViewById(R.id.mpassword);
        mPhone = (EditText) findViewById(R.id.edit_text_phone);
        mCode = (EditText) findViewById(R.id.edit_text_code);
        mCancle = (LinearLayout) findViewById(R.id.edit_button_cancle);
        mSendcode = (Button) findViewById(R.id.btn_sendcode);
        mSendtext = (Button) findViewById(R.id.btn_test);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mButtonSuccess = (Button) findViewById(R.id.button_success);
    }

    private void initdata() {
        mObserver = new SmsObserver(RetrievepasswordActivity.this, mHandler);
        Uri uri = Uri.parse("content://sms");
        getContentResolver().registerContentObserver(uri, true, mObserver);

        mCancle.setOnClickListener(this);
        mSendcode.setOnClickListener(this);
        mSendtext.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);
        mButtonSuccess.setOnClickListener(this);
        back.setOnClickListener(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mObserver);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_button_cancle:
                mPhone.setText("");
                break;
            case R.id.btn_sendcode:
                String phone = mPhone.getText().toString();
                SendcodeMessages(phone);
                break;
            case R.id.btn_test:
                mphone = mPhone.getText().toString();
                String mcode=mCode.getText().toString();
                if (mphone.equals("")) {
                    ToastUtils.ToastMessage(this, "手机号不能为空");
                    break;
                }
                if (mcode.equals("")) {
                    ToastUtils.ToastMessage(this, "验证码不能为空");
                    break;
                }

                if (!MobileNo.isMobileNO(mphone)) {
                    ToastUtils.ToastMessage(this, "请填写正确手机号");
                    break;
                }

                if (!MobileNo.isMobileCode(mcode)) {
                    ToastUtils.ToastMessage(this, "请填写正确验证码");
                    break;
                }


                RegisterRequest(mphone,mcode);
                break;

            case R.id.button_login:

                String spassword = password.getText().toString();
                String sspassword = mpassword.getText().toString();
                if (spassword.equals("") || sspassword.equals("")) {
                    ToastUtils.ToastMessage(this, "请填写密码");
                    break;
                }
                if(spassword.length()<6||sspassword.length()<6){
                    ToastUtils.ToastMessage(this, "密码必须大于6位");
                    break;
                }
                if(!getPassword(spassword)||!getPassword(sspassword)){
                    ToastUtils.ToastMessage(this, "密码格式不正确");
                    break;
                }

                if (!spassword.equals(sspassword)) {
                    ToastUtils.ToastMessage(this, "两次密码输入不一致");
                    break;
                }

                Registerfinish(mphone,spassword.toLowerCase());
                break;

            case R.id.button_success:
               //
               //
               // startActivity(new Intent(this,LoginRegisterActivity.class));
                finish();
                break;

            case R.id.back:
                finish();
                break;

        }
    }

  /*  private boolean getPassword(String userName){
        String regEx = "^[a-zA-Z][a-zA-Z0-9_]{5,19}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(userName);
        return matcher.matches();
    }*/
 /* private boolean getPassword(String userName) {
      String regEx = "^[a-zA-Z0-9]{6,19}$";
      Pattern pattern = Pattern.compile(regEx);
      Matcher matcher = pattern.matcher(userName);
      return matcher.matches();
  }*/

    public static boolean getPassword(String password) {
        String string = "^[Za-z0-9`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]+$";
        Pattern mPattern = Pattern.compile(string);
        Matcher matcher = mPattern.matcher(password);
        return matcher.matches();
    }

    private void SendcodeMessages(String phone) {
        if (MobileNo.isMobileNO(phone)) {
            postPhoneCade(phone);

        } else {
            ToastUtils.ToastMessage(this, "请填写正确手机号");
        }
    }

    private void postPhoneCade(String phone) {
        String url = "http://realtime.ddduo.com/realtime/AppServiceController/sendMessageCode";

        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("codeType", "02");
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new mUserNumberCallBack());
           /*    .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String s) {
                        System.out.println("s:"+s);
                    }
                });*/

    }




    private class mUserNumberCallBack extends UserRegisterCallBack {

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(UserRegisterBean userRegisterBean) {
            String statusCode = userRegisterBean.getStatusCode();

            if(statusCode.equals("010")){
                //倒计时
                codeVal = userRegisterBean.getCodeVal();
                DownTimeButton(codeVal);
                System.out.println("验证码已发送:" + codeVal);
                mHandler.sendEmptyMessage(3);
            }
            if(statusCode.equals("020")){
                mHandler.sendEmptyMessage(9);
            }
            if(statusCode.equals("030")){
                System.out.println("验证码已发送3:");
            }
            if(statusCode.equals("050")){
                System.out.println("验证码已发送5:");
            }
            if(statusCode.equals("090")){
                mHandler.sendEmptyMessage(2);
            }
            if(statusCode.equals("100")){
                mHandler.sendEmptyMessage(10);
            }

        }


    }

    private void RegisterRequest(String phone, String code)
    {
        System.out.println("用户填写验证码:" +code+","+phone);
        isLogin=false;
        String url = "http://realtime.ddduo.com/realtime/AppServiceController/findPasswordVerification";
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("userCode",code);
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
               .execute(new RegisterResult());


    }
    private class RegisterResult extends UserRegisterCallBack {

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(UserRegisterBean userRegisterBean) {

            String statusCode = userRegisterBean.getStatusCode();
            System.out.println("statusCode:"+statusCode);

            if(statusCode.equals("010")){
               //GO
                if(isLogin){
                    mContentfinish.setVisibility(View.VISIBLE);
                    mContentStop.setVisibility(View.GONE);

                }else{
                    mTitleStatestart.setVisibility(View.GONE);
                    mContentStart.setVisibility(View.GONE);
                    mTitleStatestop.setVisibility(View.VISIBLE);
                    mContentStop.setVisibility(View.VISIBLE);
                }

            }
            if(statusCode.equals("020")){
                mHandler.sendEmptyMessage(5);
            }
            if(statusCode.equals("030")){
                mHandler.sendEmptyMessage(8);
            }
            if(statusCode.equals("040")){
                mHandler.sendEmptyMessage(7);
            }


        }


    }

    private void Registerfinish(String phone, String password)
    {
        System.out.println("用户手机号:" +phone);
        isLogin=true;
        String url = "http://realtime.ddduo.com/realtime/AppServiceController/appLoginPassword";
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("password",Md5.getMD5Str(password));
        params.put("findType","01");
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                 .execute(new RegisterResult());


    }

    private void DownTimeButton(String codeVal) {
        if(!codeVal.equals("")){
            CountDownTimerUtils mCountDownTimerUtils  =new CountDownTimerUtils(60000,1000);
            mCountDownTimerUtils.setonDownTimerListener(this);
            mCountDownTimerUtils.start();
            mSendcode.setEnabled(false);
        }
    }

    @Override
    public void onDownTimerTick(long l) {
        long timelong = l / 1000;
        mSendcode.setText(timelong + "秒后重新发送");
    }

    @Override
    public void onDownFinish() {
        mSendcode.setEnabled(true);
        mSendcode.setText(" 重新发送 ");
    }
}
