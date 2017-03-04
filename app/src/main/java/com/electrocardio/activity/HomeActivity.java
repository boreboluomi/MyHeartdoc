package com.electrocardio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aceuni.uart.sdk.AUBlutoothHolder;
import com.electrocardio.R;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.base.ProfileActivity;
import com.electrocardio.bluetoothdata.AUStateChangeCallbackIml;
import com.electrocardio.fragment.activity.HomeFragment;
import com.electrocardio.reside.ResideMenu;

import java.lang.ref.WeakReference;

/**
 * Created by yangzheng on 2015/12/31.
 */
public class HomeActivity extends ProfileActivity {
    private long exitTime = 0;
    private ResideMenu resideMenu;
    private HomeActivity mContext;

    //----------------左边菜单项------------------//
    private RelativeLayout rl_head;
    private RelativeLayout tl_ring;
    private RelativeLayout rl_clear;
    private RelativeLayout rl_news;
    private RelativeLayout rl_safe;
    private RelativeLayout rl_us;
    private TextView tv_ringContent;
    private TextView tv_clearContent;
    private TextView tv_newsTip;
    //--------------------------------------//
    private AUBlutoothHolder mHolder;
    private AUStateChangeCallbackIml mCallBack;

    public static final String TAG = "HomeActivity";
    public static final int SCAN_PERIOD = 10000;
    public static final int STOP_SCAN_FOR_PERIOD = 1001;
    public static final int DEVICE_DISCONNECTED = 1002;
    public static final int DEVICE_STATE_READY = 1003;
    public static final int RECEIVED_DATA_FROM_DEVICE = 1004;
    private MyHandler mHandler = new MyHandler(this) {
        @Override
        public void switchMessage(Message msg) {
            switch (msg.what) {
                case STOP_SCAN_FOR_PERIOD:// 停止浏览
                    if (mHolder != null)
                        mHolder.stopScan();
                    break;
                case DEVICE_DISCONNECTED:// 设备断开连接
                    break;
                case DEVICE_STATE_READY:// 设备就绪
                    break;
                case RECEIVED_DATA_FROM_DEVICE:// 从设备中接收到数据
                    break;
            }
        }
    };
    private RelativeLayout mSubmitData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if (savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();
       /*     Bundle bundle = new Bundle();
            homeFragment.setArguments();*/
            changeFragment(homeFragment);
        }

        InitUpMenu();
        BaseApplication.getInstance().addActivity(this);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_home;
    }

    @Override
    protected void onInitialize() {
        setUpMenu();
        mCallBack = new AUStateChangeCallbackIml(this);
        mHolder = new AUBlutoothHolder(this, mCallBack);
    }

    private void setUpMenu() {
        resideMenu = new ResideMenu(this, R.layout.custom_left_layout, -1);
        BaseApplication.getInstance().setResideMenu(resideMenu);
        resideMenu.setUse3D(false);
        resideMenu.setBackground(R.mipmap.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        resideMenu.setScaleValue(0.6f);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

    }
    private void InitUpMenu() {
        rl_head = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_head);
        tl_ring = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_ring);
        rl_clear = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_dataClear);
        rl_news = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_news);
        rl_safe = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_accountSafe);
        rl_us = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_aboutUs);
        tv_ringContent = (TextView) resideMenu.getLeftMenuView().findViewById(R.id.tv_ringContent);
        tv_clearContent = (TextView) resideMenu.getLeftMenuView().findViewById(R.id.tv_clearContent);
        tv_newsTip = (TextView) resideMenu.getLeftMenuView().findViewById(R.id.tv_newsTip);
        mSubmitData = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_submitData);
        setOnLeftMenuClick();
    }

    /**
     * 获取mHolder
     *
     * @return
     */
    public AUBlutoothHolder getAUBlutoothHolder() {
        return mHolder;
    }

    /**
     * 获取mCallBack
     * @return
     */
    public AUStateChangeCallbackIml getAUStateChangeCallbackIml(){
        return mCallBack;
    }

    /**
     * 为左侧菜单项设置点击事件
     */
    private void setOnLeftMenuClick() {
        rl_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(PersonInformationActivity.class);
            }
        });
        tl_ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(DeviceManagerActivity.class);
            }
        });
        rl_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(ClearDataActivity.class);
            }
        });
        rl_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(MessageInformActivity.class);
            }
        });
        rl_safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(AccountSecureActivity.class);
            }
        });
        rl_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(AboutUsActivity.class);
            }
        });
        mSubmitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToastUtils.ToastMessage(getContext(),"点击了");
                startNewActivity(UploadFileActivity.class);
            }
        });
    }

    /**
     * 开启新的Activity
     *
     * @param newClass
     */
    private void startNewActivity(Class newClass) {
        Intent intent = new Intent(this, newClass);
        startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
        }

        @Override
        public void closeMenu() {
        }
    };

    @Override
    protected boolean isHideActionBar() {
        return true;
    }

    @Override
    protected boolean isOpenActionBar() {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                if (LoginRegisterActivity.LoginRegisterActivity != null) {
                    LoginRegisterActivity.LoginRegisterActivity.finish();
                }
                finish();
            }
        }
        return true;
    }

    private void changeFragment(Fragment targetFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    abstract class MyHandler extends Handler {
        private WeakReference<ProfileActivity> mActivity;

        MyHandler(ProfileActivity activity) {
            mActivity = new WeakReference<ProfileActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ProfileActivity activity = mActivity.get();
            if (activity != null)
                switchMessage(msg);
        }

        public abstract void switchMessage(Message message);
    }



}
