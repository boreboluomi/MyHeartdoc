package com.electrocardio.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.electrocardio.base.BaseApplication;
import com.electrocardio.database.UserDao;
import com.electrocardio.fragment.activity.FragmentOnePassword;
import com.electrocardio.fragment.activity.FragmentOnePhone;
import com.electrocardio.fragment.activity.FragmentThreePassword;
import com.electrocardio.fragment.activity.FragmentThreePhone;
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
public class ChangephoneActivity1 extends BaseActivity implements View.OnClickListener, FragmentThreePhone.BackHandledInterface {

    private LinearLayout back;
    private FragmentThreePhone mBackHandedFragment;

    public static void startActivity(Activity activity, int code) {
        Intent intent = new Intent(activity, ChangephoneActivity1.class);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.change_fragmentlayout;
    }

    @Override
    protected void Initialize() {
            initView();
            initdata();

    }



    private void initView() {
        TextView  mTitleText = (TextView) findViewById(R.id.title_text);
        mTitleText.setText("绑定手机");

        back = (LinearLayout) findViewById(R.id.back);

    }

    private void initdata() {
        BaseApplication.getInstance().addActivity(this);
        back.setOnClickListener(this);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.add(R.id.id_content, new FragmentOnePhone(), "ONE");
        tx.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }

    }



    @Override
    public void onBackPressed() {

        if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                super.onBackPressed();
            }
        }
        if(mBackHandedFragment != null){
            startActivity(new Intent(this,LoginRegisterActivity.class));
            BaseApplication.getInstance().exit(false);
        }
    }

    @Override
    public void setSelectedFragment(FragmentThreePhone selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }
}
