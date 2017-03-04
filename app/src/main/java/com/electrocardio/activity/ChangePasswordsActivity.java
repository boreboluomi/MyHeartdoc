package com.electrocardio.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.electrocardio.R;
import com.electrocardio.base.BaseActivity;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.fragment.activity.FragmentOnePassword;
import com.electrocardio.fragment.activity.FragmentThreePassword;

/**
 * Created by yangzheng on 2016/5/16.
 * Retrieve password
 */
public class ChangePasswordsActivity extends BaseActivity implements FragmentThreePassword.BackHandledInterface {


    private LinearLayout black;
    private FragmentThreePassword mBackHandedFragment;

    public static void startActivity(Activity activity, int code) {
        Intent intent = new Intent(activity, ChangePasswordsActivity.class);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.change_fragmentlayout;
    }

    @Override
    protected void Initialize() {
            BaseApplication.getInstance().addActivity(this);
            initView();
            initdata();


    }



    private void initView() {
        TextView  mTitleText = (TextView) findViewById(R.id.title_text);
        black = (LinearLayout) findViewById(R.id.back);
        mTitleText.setText("重置密码");
    }

    private void initdata() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.add(R.id.id_content, new FragmentOnePassword(),"ONE");
        tx.commit();
        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void setSelectedFragment(FragmentThreePassword selectedFragment) {
        this.mBackHandedFragment = selectedFragment;

    }
/*
    public  void setVisibleBlack(){
        black.setVisibility(View.GONE);
    }*/
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

}
