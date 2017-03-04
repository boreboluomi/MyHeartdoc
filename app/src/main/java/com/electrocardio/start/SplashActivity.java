package com.electrocardio.start;

import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;

import com.electrocardio.R;
import com.electrocardio.TestActivity;
import com.electrocardio.base.ProfileActivity;
import com.electrocardio.consts.Config;
import com.electrocardio.database.UserDao;

/**
 * Created by yangzheng on 2015/12/30.
 */
public class SplashActivity extends ProfileActivity {
    @Override
    protected int getLayoutID() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onInitialize() {
       // UserDao.getInstance(this).addUser("U2229", "18811061109", "张三");
       // UserDao.getInstance(this).updateUserName("张三");
      //  UserDao.getInstance(this).updateBirthday(2016, 6, 6);
      // UserDao.getInstance(this).updateHeight(167);
      //  UserDao.getInstance(this).updateSex(0);
      // UserDao.getInstance(this).updateWeight(55);
       // intent2Index(1000);
        startActivity(new Intent(SplashActivity.this, StartGuideActivity.class));
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
        finish();
    }

    private void intent2Index(int delayTime) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (Config.RELEASE) {
                    startActivity(new Intent(SplashActivity.this, StartGuideActivity.class));
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
                } else {
                    // startActivity(new Intent(SplashActivity.this, DeBugActivity.class));
                }
                finish();
            }
        }, delayTime);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean isHideActionBar() {
        return true;
    }

    @Override
    protected boolean isOpenActionBar() {
        return true;
    }
}
