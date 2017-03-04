package com.electrocardio.start;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.electrocardio.Bean.UserInformation;
import com.electrocardio.Bean.UserNumberBean;
import com.electrocardio.Callback.UserNumberCallBack;
import com.electrocardio.R;
import com.electrocardio.activity.LoginRegisterActivity;
import com.electrocardio.activity.MeasureActivity;
import com.electrocardio.activity.UserInformationActivity;
import com.electrocardio.base.BaseActivity;
import com.electrocardio.base.ProfileActivity;
import com.electrocardio.database.UserDao;
import com.electrocardio.util.Md5;
import com.electrocardio.util.ShareData;
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
 * Created by yangzheng on 2015/12/30.
 */
public class StartGuideActivity extends BaseActivity {

    private ViewPager mViewPager;
    private TextView start_btn;
    private UserDao instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.startguide_viewpaper;
    }

    @Override
    protected void Initialize() {
        Integer count = ShareData.getStartGuide(this, ShareData.SHARE_FILE_START_GUIDE);
        if (count > 0) {
            UserInformation userInformation = UserDao.getInstance(this).getUser();
            String phoneNumber = userInformation.getPhoneNumber();
            String userToken = userInformation.getUserToken();
            // startContent();
            if (userToken.equals("")) {
                startContent();
            } else {
                if(!checkNetWork(this)){
                    startActivity(new Intent(StartGuideActivity.this, MeasureActivity.class));
                    finish();
                }else{
                    PostUserToken(phoneNumber, userToken);
                }

            }
        } else {
            startContent();
        }

    }

    private  boolean checkNetWork(Context context) {
        boolean flag = false;


        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null)
            flag = cwjManager.getActiveNetworkInfo().isAvailable();
        return flag;
    }
    private void startContent() {

        startActivity(new Intent(StartGuideActivity.this, LoginRegisterActivity.class));
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
        finish();
    }

    private void PostUserToken(String phone, String token) {
        System.out.println("token1:" + token);
        String url = "http://realtime.ddduo.com/realtime/AppServiceController/userToken";
        Map<String, String> params = new HashMap<>();
        System.out.println("phone:" + phone);
        params.put("phone", phone);
        params.put("usertoken", token);
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new mUserNumberCallBack());
       /*     .execute(new StringCallback() {
                  @Override
                  public void onError(Call call, Exception e) {

                  }

                  @Override
                  public void onResponse(String s) {
                        System.out.println("s:"+s);
                  }
              });*/


    }


    private class mUserNumberCallBack extends UserNumberCallBack {


        @Override
        public void onError(Call call, Exception e) {
            startContent();
        }

        @Override
        public void onResponse(UserNumberBean userNumberBean) {
            if(userNumberBean!=null){
                String statusCode = userNumberBean.getStatusCode();
                if (statusCode.equals("-1")) {
                    startContent();
                } else {
                    startHomeActivity(userNumberBean);
                }
            }else{
                startContent();
            }


        }

    }

    private void startHomeActivity(UserNumberBean userNumberBean) {
        if(userNumberBean.getName()!=null){
            instance = UserDao.getInstance(StartGuideActivity.this);
            instance.updateUserName(userNumberBean.getName());
            instance.updateHeight(userNumberBean.getHeight());
            instance.updateWeight(userNumberBean.getWeight());
            instance.updateDeviceId(userNumberBean.getDevice_id());
            instance.updateEndTime(userNumberBean.getEnd_time());
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
            if (userNumberBean.getLogo() != null) {
                getImage(userNumberBean.getLogo());
            }
            System.out.println("进入到homeActivity页面");

            startActivity(new Intent(StartGuideActivity.this, MeasureActivity.class));
            finish();
        }else{
            startActivity(new Intent(StartGuideActivity.this, LoginRegisterActivity.class));
            finish();
        }

    }


    public void getImage(String murl) {
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
                        String phoneNumber = instance.getPhoneNumber();
                        String path = Environment.getExternalStorageDirectory() + "/yxkg" + "/"
                                + phoneNumber + ".png";
                        saveBitmap(bitmap, path);

                    }
                });
    }

    private void saveBitmap(Bitmap bitmap, String path) {
        File pathfile = new File(Environment.getExternalStorageDirectory() + "/yxkg" + "/");
        File file = new File(path);

        if (!pathfile.exists()) {
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