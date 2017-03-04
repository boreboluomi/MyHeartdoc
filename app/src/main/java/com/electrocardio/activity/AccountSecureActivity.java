package com.electrocardio.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.Bean.UserInformation;
import com.electrocardio.R;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.database.UserDao;

/**
 * Created by ZhangBo on 2016/2/24.
 */
public class AccountSecureActivity extends FragmentActivity {

    private ImageView iv_back;
    private TextView tv_title;
    private RelativeLayout rl_bindPhone;
    private RelativeLayout rl_modifyPwd;
    private TextView phoneNumber;
    private UserInformation userInformation;
    private ImageView mblack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsecure);
        BaseApplication.getInstance().addActivity(this);
        initView();
        initData();
    }



    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_titleLeft);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rl_bindPhone = (RelativeLayout) findViewById(R.id.rl_bindphone);
        rl_modifyPwd = (RelativeLayout) findViewById(R.id.rl_changepassword);
        phoneNumber = (TextView) findViewById(R.id.tv_bindphonenumber);
        tv_title.setText("账号安全");
        setOnButtonClick();
    }
    private void initData() {

        userInformation = UserDao.getInstance(this).getUser();
        String phone = userInformation.getPhoneNumber();
        String maskNumber = phone.substring(0,3)+"****"+phone.substring(7,phone.length());
        phoneNumber.setText(maskNumber);
    }
    private void setOnButtonClick() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rl_bindPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(AccountSecureActivity.this, "尚未开发", Toast.LENGTH_SHORT).show();
                ChangephoneActivity1.startActivity(AccountSecureActivity.this, 1001);
            }
        });
        rl_modifyPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordsActivity.startActivity(AccountSecureActivity.this, 1002);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_enter, R.anim.activity_back_exit);
    }
}
