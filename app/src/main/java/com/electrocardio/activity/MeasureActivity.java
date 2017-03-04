package com.electrocardio.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.electrocardio.Bean.UserInformation;
import com.electrocardio.R;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.custom.elec.CircleImageView;
import com.electrocardio.custom.elec.MeasureButton;
import com.electrocardio.database.ContinueMeasureRecordDao;
import com.electrocardio.database.UploadMeasureRecordDao;
import com.electrocardio.database.UserDao;
import com.electrocardio.databasebean.ContinueMeasureRecord;
import com.electrocardio.fragment.activity.MeasureFragment;
import com.electrocardio.reside.ResideMenu;
import com.electrocardio.util.DeleteFileUtil;
import com.electrocardio.util.TimeUtils;
import com.electrocardio.util.ToastUtils;

/**
 * Created by ZhangBo on 2016/3/2.
 */
public class MeasureActivity extends FragmentActivity {

    private long exitTime = 0;
    private ImageView iv_sex;// 性别图片
    private TextView userame;// 用户名
    private TextView age;// 年龄
    private TextView height;// 身高
    private TextView weight;// 体重
    private ResideMenu resideMenu;

    //----------------左边菜单项------------------//
    private RelativeLayout rl_head;// 个人中心
    private RelativeLayout tl_ring;// 铃音报警
    private RelativeLayout rl_clear;// 数据清理
    private RelativeLayout rl_news;// 消息通知
    private RelativeLayout rl_safe;// 账号安全
    private RelativeLayout rl_us;// 关于我们
    private RelativeLayout rl_submitData;// 上传数据
    private TextView uploadDataTip;// 上传数据的提示
    private UserInformation userInformation;
    private TextView tv_ringContent;
    private TextView tv_clearContent;// 数据清理内容提示
    private TextView tv_newsTip;
    //--------------------------------------//

    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;
    private MeasureFragment measureFragment;
    private CircleImageView mCircleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        BaseApplication.getInstance().addActivity(this);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        // MethodUtils.getInstance().setStateBarColor(this, "#26bfa6");
        setUpMenu();
        InitUpMenu();

        ContinueMeasureRecord continueMeasureRecord = ContinueMeasureRecordDao.getInstance(this).getCurrentMeasureRecord();
        if (continueMeasureRecord != null) {
            continueMeasureRecord.setMeasureOver(ContinueMeasureRecord.MEASUREOVER);
            continueMeasureRecord.setEndTimeStamp(System.currentTimeMillis());
            ContinueMeasureRecordDao.getInstance(this).updateContinueRecord(continueMeasureRecord);
        }

        if (measureFragment == null)
            measureFragment = new MeasureFragment();
        changeFragment(measureFragment);
        // BaseApplication.getInstance().setMeasureActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();

        UserInformation user = UserDao.getInstance(this).getUser();
        userame.setText(user.getUserName());
        if (user.getSEX() == 2) {
            iv_sex.setImageResource(R.mipmap.gender_female);
        } else {
            iv_sex.setImageResource(R.mipmap.gender_male);
        }

        String nowtime = TimeUtils.getTime();
        String beforetiem = user.getBirthdayLabel();
        int year = Integer.valueOf(nowtime.substring(0, 4)) - Integer.valueOf(beforetiem.substring(0, 4));
        age.setText(year + "岁");

        String weight = user.getWeight() + "kg";
        this.weight.setText(weight);
        String height = user.getHeight() + "cm";
        this.height.setText(height);
        String logoSrc = user.getLogoSrc();


        if (!logoSrc.equals("")) {
            Bitmap photo = BitmapFactory.decodeFile(logoSrc);
            mCircleImageView.setImageBitmap(photo);
        } else {
            if (UserDao.getInstance(this).getUser().getSEX() == 2) {
                mCircleImageView.setImageResource(R.mipmap.woman);
            } else {
                mCircleImageView.setImageResource(R.mipmap.man);
            }
        }
  /*      userInformation = UserDao.getInstance(this).getUser();
        if (userInformation.getSEX() == UserInformation.MALE)
            iv_sex.setImageResource(R.mipmap.gender_male);
        else
            iv_sex.setImageResource(R.mipmap.gender_female);
        userame.setText(userInformation.getUserName());
        age.setText(userInformation.getAge() + "岁");
        height.setText(userInformation.getHeight() + " cm");
        weight.setText(userInformation.getWeight() + " kg");*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    /**
     * 设置左边的菜单
     */
    private void setUpMenu() {
        resideMenu = new ResideMenu(this, R.layout.custom_left_layout, -1);
        resideMenu.setUse3D(false);
        resideMenu.setBackground(R.mipmap.menu_background);
        resideMenu.attachToActivity(MeasureActivity.this);
        resideMenu.setMenuListener(menuListener);
        resideMenu.setScaleValue(0.6f);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    }

    /**
     * 初始化左边的菜单
     */
    private void InitUpMenu() {
        rl_head = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_head);
        tl_ring = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_ring);
        rl_clear = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_dataClear);
        rl_news = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_news);
        rl_safe = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_accountSafe);
        rl_us = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_aboutUs);
        rl_submitData = (RelativeLayout) resideMenu.getLeftMenuView().findViewById(R.id.rl_submitData);
        uploadDataTip = (TextView) resideMenu.getLeftMenuView().findViewById(R.id.tv_uploadDataTip);
        tv_ringContent = (TextView) resideMenu.getLeftMenuView().findViewById(R.id.tv_ringContent);
        tv_clearContent = (TextView) resideMenu.getLeftMenuView().findViewById(R.id.tv_clearContent);
        tv_newsTip = (TextView) resideMenu.getLeftMenuView().findViewById(R.id.tv_newsTip);
        mCircleImageView = (CircleImageView) resideMenu.getLeftMenuView().findViewById(R.id.iv_icon);

        iv_sex = (ImageView) resideMenu.getLeftMenuView().findViewById(R.id.iv_sex);
        userame = (TextView) resideMenu.getLeftMenuView().findViewById(R.id.tv_name);
        height = (TextView) resideMenu.getLeftMenuView().findViewById(R.id.tv_height);
        weight = (TextView) resideMenu.getLeftMenuView().findViewById(R.id.tv_weight);
        age = (TextView) resideMenu.getLeftMenuView().findViewById(R.id.tv_age);

        userame.setText(UserDao.getInstance(this).getUser().getUserName());
        if (UserDao.getInstance(this).getUser().getSEX() == 2) {
            iv_sex.setImageResource(R.mipmap.gender_female);
        } else {
            iv_sex.setImageResource(R.mipmap.gender_male);
        }
        String nowtime = TimeUtils.getTime();
        String beforetiem = UserDao.getInstance(this).getUser().getBirthdayLabel();
        int year = Integer.valueOf(nowtime.substring(0, 4)) - Integer.valueOf(beforetiem.substring(0, 4));
        age.setText(year + "岁");
        System.out.println(year);
        String weight = UserDao.getInstance(this).getUser().getWeight() + "kg";
        this.weight.setText(weight);
        String height = UserDao.getInstance(this).getUser().getHeight() + "cm";
        this.height.setText(height);

        setOnLeftMenuClick();
    }

    /**
     * 为左侧菜单项设置点击事件
     */
    private void setOnLeftMenuClick() {
        rl_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeasureActivity.this, PersonInformationActivity.class);
                startActivityForResult(intent, 1);
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
                if (BaseApplication.getInstance().getBlueCommuCls() != null) {
                    if (BaseApplication.getInstance().getBlueCommuCls().getMEASUREBUTTONSTATE() == MeasureButton.STOP) {
                        ToastUtils.ToastMessage(MeasureActivity.this, "正在测量,此操作被禁止");
                        return;
                    }
                }
                startNewActivity(AccountSecureActivity.class);
            }
        });
        rl_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(AboutUsActivity.class);
            }
        });
        rl_submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(UploadDataActivity.class);
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
            int num = ContinueMeasureRecordDao.getInstance(MeasureActivity.this).
                    getNotSubmitedConMeaRecord().size();
            if (num == 0)
                uploadDataTip.setVisibility(View.INVISIBLE);
            else
                uploadDataTip.setText(num + "");
        }

        @Override
        public void closeMenu() {
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        int num = ContinueMeasureRecordDao.getInstance(MeasureActivity.this).
                getNotSubmitedConMeaRecord().size();
        if (num == 0)
            uploadDataTip.setVisibility(View.INVISIBLE);
        else
            uploadDataTip.setText(num + "");
        float usedSize = ContinueMeasureRecordDao.getInstance(this).getAllDataSize();
        String usedStr = DeleteFileUtil.getOccupySize(usedSize);
        tv_clearContent.setText(usedStr);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == -1) {
                exitHandleThing();
                startActivity(new Intent(MeasureActivity.this, LoginRegisterActivity.class));
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (System.currentTimeMillis() - exitTime > 2000) {
//                Toast.makeText(getApplicationContext(), "再次点击退出", Toast.LENGTH_SHORT).show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                exitHandleThing();
//            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(intent);
            return true;
        }
        return true;
    }

    /**
     * 退出时需要处理的事情
     */
    private void exitHandleThing() {
        UploadMeasureRecordDao.getInstance(this).deleteAllData();
        if (BaseApplication.getInstance().getHandleUploadData() != null) {
            BaseApplication.getInstance().getHandleUploadData().out();
            BaseApplication.getInstance().setHandleUploadData(null);
        }
        // 清理处理蓝牙传输数据的类
        if (BaseApplication.getInstance().getBlueCommuCls() != null) {
            if (BaseApplication.getInstance().getBlueCommuCls().getAUBlutoothHolder().connectedDevice() != null)
                BaseApplication.getInstance().getBlueCommuCls().getAUBlutoothHolder().disconnect();
            while (BaseApplication.getInstance().getBlueCommuCls().getAUBlutoothHolder().connectedDevice() != null) {
            }
            BaseApplication.getInstance().setBlueCommuCls(null);
        }
        finish();
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mCurrentOrientation = getResources().getConfiguration().orientation;
//        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
//            if (measureFragment == null) {
//                measureFragment = new MeasureFragment();
//            }
//            changeFragment(measureFragment);
//            resideMenu.setSwipeDirectionEnable(ResideMenu.DIRECTION_LEFT);
//        } else {
//            if (rowMeasureFragment == null) {
//                rowMeasureFragment = new RowMeasureFragment();
//            }
//            changeFragment(rowMeasureFragment);
//            resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
//        }
//    }

    /**
     * 切换Fragment
     *
     * @param targetFragment
     */
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
}
