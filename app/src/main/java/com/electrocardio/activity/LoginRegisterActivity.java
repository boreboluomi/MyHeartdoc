package com.electrocardio.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.electrocardio.Bean.UserNumberBean;
import com.electrocardio.Callback.UserNumberCallBack;
import com.electrocardio.R;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.base.ProfileActivity;
import com.electrocardio.clipimage.ClipImageActivity;
import com.electrocardio.database.UserDao;
import com.electrocardio.popup.LoginDataPop;
import com.electrocardio.popup.SavaDataPop;
import com.electrocardio.util.ConstantUtils;
import com.electrocardio.util.CountDownTimerUtils;
import com.electrocardio.util.Md5;
import com.electrocardio.util.MobileNo;
import com.electrocardio.util.ShareData;
import com.electrocardio.util.TimeUtils;
import com.electrocardio.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;

/**
 * Created by yangzheng on 2015/12/31.
 *
 */
public class LoginRegisterActivity extends ProfileActivity implements View.OnClickListener, CountDownTimerUtils.onDownTimerListener  {
    public static LoginRegisterActivity LoginRegisterActivity;
    private TextView registerButton;
    private Button loginButton;
    private long exitTime = 0;
    private EditText mUserName;
    private EditText mPassWord;
    private CheckBox mVisibbility;
    private UserDao instance;
    private String phone;
    private Boolean isRegister=false;
    private TextView rpassword;
    private LoginDataPop saveDataPop;
 /*   private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (saveDataPop.isShowing()) {
                saveDataPop.dismiss();
                ToastUtils.ToastMessage(LoginRegisterActivity.this, "连接超时请重新连接");
            }
        }
    };*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginRegisterActivity = this;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_login_register;
    }

    @Override
    protected void onInitialize() {
        saveDataPop = new LoginDataPop(getContext());
        mUserName = (EditText) findViewById(R.id.loginregisterui_username);
        mPassWord = (EditText) findViewById(R.id.loginregister_pwd);
        registerButton = (TextView) findViewById(R.id.tv_quickregister);
        loginButton = (Button) findViewById(R.id.btn_login);
        mVisibbility = (CheckBox) findViewById(R.id.loginregisterui_eye);
        rpassword = (TextView) findViewById(R.id.loginregusterui_forgetpwd);
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        rpassword.setOnClickListener(this);
        mVisibbility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mPassWord.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
                } else {
                    mPassWord.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        goHomeActivity();//
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPassWord.setText("");
        if (saveDataPop.isShowing())
            saveDataPop.dismiss();
    }

    private void goHomeActivity() {
       /* if (User.getInstance().getUserId() > 0 || getIntent().getBooleanExtra("over_step", false)) {
            startActivityForResult(new Intent(LoginRegisterActivity.this, HomeActivity.class), 1000);
            finish();
        }*/
    }

    @Override
    protected boolean isHideActionBar() {
        return true;
    }

    @Override
    protected boolean isOpenActionBar() {
        return true;
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.tv_quickregister:
                intent.setClass(this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                break;
            case R.id.btn_login:

                String mUserName = this.mUserName.getText().toString();
                String mpassWord = mPassWord.getText().toString();
                if(!checkNetWork(this)){
                    ToastUtils.ToastMessage(getContext(), "请链接网络");
                    break;
                }
                if (mUserName.equals("") || mpassWord.equals("")) {
                    ToastUtils.ToastMessage(getContext(), "用户名或密码不能为空");
                    break;
                }
                if (!MobileNo.isMobileNO(mUserName)) {
                    ToastUtils.ToastMessage(getContext(), "用户名应为手机号");
                    break;
                } else {
                    postLogin(mUserName, mpassWord.toLowerCase());
                    loadingView();

                   /* intent.setClass(this, MeasureActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                    finish();*/
                }



                break;

          case R.id.loginregisterui_eye:
                break;

            case R.id.loginregusterui_forgetpwd:
                RetrievepasswordActivity.startActivity(this,1001);
                break;
            default:
                break;
        }
    }
    private void DownTimeButton() {

            CountDownTimerUtils mCountDownTimerUtils  =new CountDownTimerUtils(30000,1000);
            mCountDownTimerUtils.setonDownTimerListener(this);
            mCountDownTimerUtils.start();



    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && (this.getClass().equals(LoginRegisterActivity.class)
                || this.getClass().equals(LoginRegisterActivity.class))) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), "再次点击退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void postLogin(String name, String mPassWord) {

        String url = ConstantUtils.HOST+ "appLogin";
        System.out.println("name" + name);
        Map<String, String> params = new HashMap<>();
        params.put("phone", name);
        params.put("password", Md5.getMD5Str(mPassWord));
        //  params.put("password", mPassWord);
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                 .execute(new mUserNumberCallBack(name));
 /*  .execute(new StringCallback() {
       @Override
       public void onError(Call call, Exception e) {
           System.out.println("e:" + e);
       }

       @Override
       public void onResponse(String s) {
           System.out.println("s:" + s);
       }
   });*/
    }

    @Override
    public void onDownTimerTick(long l) {

    }

    @Override
    public void onDownFinish() {
        if (saveDataPop.isShowing()) {
            saveDataPop.dismiss();
            ToastUtils.ToastMessage(LoginRegisterActivity.this, "连接超时请重新连接");
        }
    }

    private class mUserNumberCallBack extends UserNumberCallBack {

        private String UserName;

        public mUserNumberCallBack(String name) {
            this.UserName = name;
        }

        @Override
        public void onError(Call call, Exception e) {
            DownTimeButton();
        }

        @Override
        public void onResponse(UserNumberBean userNumberBean) {
                if(userNumberBean.getStatusCode()!=null){
                    instance = UserDao.getInstance(LoginRegisterActivity.this);

                    if (userNumberBean.getStatusCode().equals("1")) {

                        ShareData.saveStartGuide(LoginRegisterActivity.this,ShareData.SHARE_FILE_START_GUIDE,1);
                        phone = userNumberBean.getPhone();
                        if (userNumberBean.getName() == null) {
                            Intent intent = new Intent(LoginRegisterActivity.this, UserInformationActivity.class);
                            instance.addUser(userNumberBean.getNum(), phone, "");
                            intent.putExtra("phone", UserName);

                            if(userNumberBean.getLogo()!=null){
                                String path = Environment.getExternalStorageDirectory() + "/yxkg"+"/"
                                        +  userNumberBean.getPhone()+".png";
                                File file = new File(Environment.getExternalStorageDirectory()+"/yxkg"+"/", userNumberBean.getPhone()+".png");
                                if (!file.exists()) {
                                    System.out.println("----------网络获取1----------");
                                    isRegister=false;
                                    getImage(userNumberBean.getLogo());
                                    instance.updateLogoPath(path);
                                }else{
                                    System.out.println("----------本地存储1----------");
                                    instance.updateLogoPath(path);
                                    startActivity(intent);
                                }

                            }else{
                                instance.updateLogoPath("");
                                startActivity(intent);

                            }

                        }else{
                            instance.addUser(userNumberBean.getNum(), phone, "");
                            instance.updateUserName(userNumberBean.getName());
                            instance.updateHeight(userNumberBean.getHeight());
                            instance.updateWeight(userNumberBean.getWeight());
                            instance.updateDeviceId(userNumberBean.getDevice_id());
                            instance.updateEndTime(userNumberBean.getEnd_time());
                            instance.updateToken(userNumberBean.getUsertoken());
                            System.out.println("token："+userNumberBean.getUsertoken());
                            if (userNumberBean.getSex().equals("1")) {
                                instance.updateSex(1);
                            } else {
                                instance.updateSex(2);
                            }
                            String mbirthday = userNumberBean.getBirthdate();
                            String substring = mbirthday.substring(0, 4);
                            String substring1 = mbirthday.substring(5, 7);
                            String substring2 = mbirthday.substring(8, 10);
                            Integer integer = Integer.valueOf(substring);
                            Integer integer1 = Integer.valueOf(substring1);
                            Integer integer2 = Integer.valueOf(substring2);
                            instance.updateBirthday(integer, integer1, integer2);

                            if(userNumberBean.getLogo()!=null){
                                String path = Environment.getExternalStorageDirectory()+"/yxkg"+ "/"
                                        +  userNumberBean.getPhone()+".png";
                                File file = new File(Environment.getExternalStorageDirectory()+"/yxkg"+ "/", userNumberBean.getPhone()+".png");
                                if (!file.exists()) {
                                    System.out.println("----------网络获取1----------");
                                    isRegister=true;
                                    getImage(userNumberBean.getLogo());
                                    System.out.println("logo:"+userNumberBean.getLogo());
                                    instance.updateLogoPath(path);
                                }else{
                                    System.out.println("----------本地存储2----------");
                                    instance.updateLogoPath(path);
                                    startActivity(new Intent(LoginRegisterActivity.this, MeasureActivity.class));
                                    finish();
                                    ToastUtils.ToastMessage(getContext(), "登录成功");
                                }

                            }else{
                                instance.updateLogoPath("");
                                startActivity(new Intent(LoginRegisterActivity.this, MeasureActivity.class));
                                finish();
                                ToastUtils.ToastMessage(getContext(), "登录成功");
                            }


                        }

                    }
                    // startActivity(new Intent(LoginRegisterActivity.this, MeasureActivity.class));
                    else{
                        if (saveDataPop.isShowing())
                            saveDataPop.dismiss();
                        if (userNumberBean.getStatusCode().equals("000"))
                            ToastUtils.ToastMessage(getContext(), "登录失败");
                        if (userNumberBean.getStatusCode().equals("010"))
                            ToastUtils.ToastMessage(getContext(), "用户不存在 ");
                        if (userNumberBean.getStatusCode().equals("020"))
                            ToastUtils.ToastMessage(getContext(), "密码错误");
                    }
                }else{
                    ToastUtils.ToastMessage(getContext(), "服务器异常请好后重试");
                }

        }

    }

    private void loadingView() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                saveDataPop.showAtLocation(loginButton, Gravity.CENTER, 0, 0);
            }
        });
    }


    private  boolean checkNetWork(Context context) {
        boolean flag = false;


        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null)
            flag = cwjManager.getActiveNetworkInfo().isAvailable();
        return flag;
    }

    public void getImage(String murl)
    {
        String url = murl;
        OkHttpUtils
                .get()//
                .url(url)//
                .tag(this)//
                .build()//
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
               .execute(new BitmapCallback() {
                   @Override
                   public void onError(Call call, Exception e) {

                   }

                   @Override
                   public void onResponse(Bitmap bitmap) {
                       String path = Environment.getExternalStorageDirectory() + "/yxkg" + "/"
                               + phone + ".png";
                       saveBitmap(bitmap, path);
                       if (isRegister) {
                          /* if (saveDataPop.isShowing())
                               saveDataPop.dismiss();*/
                           startActivity(new Intent(LoginRegisterActivity.this, MeasureActivity.class));
                           finish();
                           ToastUtils.ToastMessage(getContext(), "登录成功");
                       } else {
                           startActivity(new Intent(LoginRegisterActivity.this, UserInformationActivity.class));
                           finish();
                       }

                   }
               });
    }


    private void saveBitmap(Bitmap bitmap, String path) {
        File pathfile = new File(Environment.getExternalStorageDirectory() + "/yxkg" + "/");
        File file = new File(path);

        if( !pathfile.exists()) {
            Log.d("TestFile", "Create the path:" + pathfile);
            pathfile.mkdir();
        }
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fOut = null;
        try {
          //  f.createNewFile();
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (fOut != null)
                    fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
