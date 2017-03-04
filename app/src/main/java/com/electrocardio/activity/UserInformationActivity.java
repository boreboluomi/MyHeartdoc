package com.electrocardio.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.electrocardio.Bean.UploadImageBean;
import com.electrocardio.Bean.UserInformation;
import com.electrocardio.Bean.UserInformationBean;
import com.electrocardio.Bean.UserInformationdataBean;
import com.electrocardio.Callback.UploadImageback;
import com.electrocardio.Callback.UserInformationCallback;
import com.electrocardio.R;
import com.electrocardio.base.BaseActivity;
import com.electrocardio.clipimage.ClipImageActivity;
import com.electrocardio.custom.elec.CircleImageView;
import com.electrocardio.custom.elec.SelectBirthdayPopupWindow;
import com.electrocardio.custom.elec.SelectSingleRowPopupWindow;
import com.electrocardio.database.UserDao;
import com.electrocardio.imageselector.ImageConfig;
import com.electrocardio.imageselector.ImageSelector;
import com.electrocardio.imageselector.ImageSelectorActivity;
import com.electrocardio.popup.HeadPortraitPopup;
import com.electrocardio.util.ConstantUtils;
import com.electrocardio.util.ShareData;
import com.electrocardio.util.ToastUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by yangzheng on 2016/3/29.
 * 用户信息
 */
public class UserInformationActivity extends BaseActivity {

    private TextView tv_title;
    private TextView tv_left;
    private TextView tv_right;
    private RadioButton rb_man;
    private RadioButton rb_woman;
    private TextView et_birthday;
    private TextView et_height;
    private TextView et_weight;
    private SelectSingleRowPopupWindow selectSingleRowPopupWindow;// 身高
    private SelectSingleRowPopupWindow selectSingleRowPopupWindow2;// 体重
    private SelectBirthdayPopupWindow selectBirthdayPopupWindow;// 生日
    private TextView mName;
    private int isgender = 1;
    private String mphone;
    private String userphone;
    private CircleImageView mCircleImageView;
    private HeadPortraitPopup headPortraitPopup;
    public static final int REQUEST_CODE = 1000;
    private final int CAMERA_WITH_DATA = 6;
    private final int CROP_RESULT_CODE = 7;
    private ArrayList<String> path = new ArrayList<>();
    private String logoSrc;

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_registerpagetwo;
    }

    @Override
    protected void Initialize() {
        initView();

        initData();
    }


    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_left = (TextView) findViewById(R.id.tv_left);
        tv_right = (TextView) findViewById(R.id.tv_right);

        rb_man = (RadioButton) findViewById(R.id.rb_man);
        rb_woman = (RadioButton) findViewById(R.id.rb_woman);
        et_birthday = (TextView) findViewById(R.id.et_birthday);
        et_height = (TextView) findViewById(R.id.et_height);
        et_weight = (TextView) findViewById(R.id.et_weight);
        mCircleImageView = (CircleImageView) findViewById(R.id.headImage);


        mName = (TextView) findViewById(R.id.et_name);

        tv_title.setText("完善个人信息");
        tv_left.setVisibility(View.VISIBLE);
        tv_right.setText("提交");
    }

    private void initData() {
        UserInformation userInformation = UserDao.getInstance(this).getUser();
        logoSrc = userInformation.getLogoSrc();
        if( !logoSrc.equals("")){
            Bitmap photo = BitmapFactory.decodeFile(logoSrc);
            mCircleImageView.setImageBitmap(photo);
        }else{
            if(userInformation.getSEX()==2){
                mCircleImageView.setImageResource(R.mipmap.woman);
            }else{
                mCircleImageView.setImageResource(R.mipmap.man);
            }
        }
        if (getIntent().getStringExtra("phone") == null) {
            userphone = getIntent().getStringExtra("username");
        } else {
            userphone = getIntent().getStringExtra("phone");
        }

     //   System.out.println("userphone:" + userphone + "," + userInformation.getPhoneNumber());
        setOnButtonClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClick() {

        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mUserName = mName.getText().toString();
                String mbirthday = et_birthday.getText().toString();
                String mHeight = et_height.getText().toString();
                String mWeight = et_weight.getText().toString();
                if (mUserName.equals("")) {
                    ToastUtils.ToastMessage(UserInformationActivity.this, "用户信息不能为空");
                } else if (mbirthday.equals("")) {
                    ToastUtils.ToastMessage(UserInformationActivity.this, "生日不能为空");
                } else if (mHeight.equals("")) {
                    ToastUtils.ToastMessage(UserInformationActivity.this, "身高不能为空");
                } else if (mWeight.equals("")) {
                    ToastUtils.ToastMessage(UserInformationActivity.this, "体重不能为空");
                } else {
                    postString(mUserName, isgender + "", mbirthday, mHeight, mWeight);

                }
            }
        });

        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rb_man.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rb_woman.setChecked(false);
                    if(logoSrc.equals("")) {
                        mCircleImageView.setImageResource(R.mipmap.man);
                    }
                    isgender = 1;
                }
            }
        });

        rb_woman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rb_man.setChecked(false);
                    if(logoSrc.equals("")) {
                        mCircleImageView.setImageResource(R.mipmap.woman);
                    }
                    isgender = 2;
                }
            }
        });

        et_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHeightPopWindow();
            }
        });

        et_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWeightPopWindow();
            }
        });

        et_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBirthdayPopWindow();
            }
        });

        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        if(checkNetWork(UserInformationActivity.this)){
                            OpenPopupWindow();
                        }else{
                            ToastUtils.ToastMessage(UserInformationActivity.this,"请链接网络");
                        }
            }
        });
    }




    private void startCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                Environment.getExternalStorageDirectory() + "/yxkg" + "/", userphone + "cup" + ".png")));
        startActivityForResult(intent, CAMERA_WITH_DATA);
    }

    private void OpenPopupWindow() {
        headPortraitPopup = new HeadPortraitPopup(UserInformationActivity.this);
        headPortraitPopup.setOnConfirmListener(new HeadPortraitPopup.OnConfirmClickListener() {
            @Override
            public void OnClickCancel() {
                headPortraitPopup.dismiss();


            }

            @Override
            public void Onphotograph() {
                startCapture();
            }

            @Override
            public void Onphoto() {
                ImageConfig imageConfig
                        = new ImageConfig.Builder(
                        // GlideLoader 可用自己用的缓存库
                        new GlideLoader())
                        .steepToolBarColor(getResources().getColor(R.color.black))
                        .titleTextColor(getResources().getColor(R.color.white))
                        .singleSelect()
                        .crop()
                        .pathList(path)
                        .filePath("/ImageSelector/Pictures")
                        .requestCode(REQUEST_CODE)
                        .build();


                ImageSelector.open(UserInformationActivity.this, imageConfig);

            }
        });
        headPortraitPopup.showPopupWindow();
    }
    private  boolean checkNetWork(Context context) {
        boolean flag = false;


        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null)
            flag = cwjManager.getActiveNetworkInfo().isAvailable();
        return flag;
    }




    private void startCropImageActivity(String path) {
        ClipImageActivity.startActivity(this, path, CROP_RESULT_CODE);
    }
    /**
     * 显示出生日期popWindow
     */
    private void showBirthdayPopWindow() {
        selectBirthdayPopupWindow = new SelectBirthdayPopupWindow(this);
        selectBirthdayPopupWindow
                .setSelectListener(new SelectBirthdayPopupWindow.SelectListener() {
                    @Override
                    public void onSelectItem(String str) {
                        et_birthday.setText(str);
                    }
                });
        // 显示窗口
        selectBirthdayPopupWindow.showAtLocation(this.getWindow()
                        .getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    /**
     * 显示体重popWindow
     */
    private void showWeightPopWindow() {
        // 实例化SelectPicPopupWindow
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 20; i <=150; i++) {
            arrayList.add(i + "kg");
        }
        selectSingleRowPopupWindow2 = new SelectSingleRowPopupWindow(this,
                "体重设置", arrayList, 30);
        selectSingleRowPopupWindow2.setSelectListener(new SelectSingleRowPopupWindow.SelectListener() {
            @Override
            public void onSelectItem(String str) {
                String mWeight = str.subSequence(0, str.length() - 2) + "";
                et_weight.setText(mWeight);
            }
        });
        // 显示窗口
        selectSingleRowPopupWindow2.showAtLocation(this.getWindow()
                        .getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                0); // 设置layout在PopupWindow中显示的位置
    }

    /**
     * 显示身高popWindow
     */
    private void showHeightPopWindow() {
        // 实例化SelectPicPopupWindow
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 100; i <= 200; i++) {
            arrayList.add(i + "cm");
        }
        selectSingleRowPopupWindow = new SelectSingleRowPopupWindow(this,
                "身高设置", arrayList, 60);
        selectSingleRowPopupWindow.setSelectListener(new SelectSingleRowPopupWindow.SelectListener() {
            @Override
            public void onSelectItem(String str) {
                String mHeight = str.subSequence(0, str.length() - 2) + "";
                et_height.setText(mHeight);
            }
        });

        // 显示窗口
        selectSingleRowPopupWindow.showAtLocation(this.getWindow()
                        .getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                0); // 设置layout在PopupWindow中显示的位置
    }

    public void postString(String mUserName, String gender, String mbirthday, String mHeight, String mWeight) {
        String url = ConstantUtils.HOST+"appUpdateInfo";
        // System.out.println("name:" + phone + "," + gender + "," + mUserName + "," + mbirthday + "," + mHeight + "," + mWeight);

        Map<String, String> params = new HashMap<>();

    /*    if (getIntent().getStringExtra("phone") == null) {
            userphone = getIntent().getStringExtra("username");
        } else {
            userphone = getIntent().getStringExtra("phone");
        }*/
        params.put("phone", userphone);
        params.put("name", mUserName);
        params.put("sex", gender);
        params.put("birthdate", mbirthday);
        params.put("weight", mWeight);
        params.put("height", mHeight);
        params.put("emergency_contact", "");
        params.put("emergency_contact_tel", "");
        OkHttpUtils
                .post()
                .url(url)
                .params(params)
                .build()
                .execute(new mInformation());
/*       .execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(String s) {
                System.out.println("s----------------:"+s);
            }
        });*/
    }


    public class mInformation extends UserInformationCallback {

        @Override
        public void onError(Call call, Exception e) {
          //  System.out.println("emcode:" + e);
        }

        @Override
        public void onResponse(UserInformationBean userInformationBean) {

            //String status = userInformationBean.getStatus();
            String status = userInformationBean.getStatusCode();
            if (status.equals("1")) {
                ToastUtils.ToastMessage(UserInformationActivity.this, "提交成功");
                String mUserName = mName.getText().toString();
                String mbirthday = et_birthday.getText().toString();
                String mHeight = et_height.getText().toString();
                String mWeight = et_weight.getText().toString();
                String substring = mbirthday.substring(0, 4);
                String substring1 = mbirthday.substring(5, 7);
                String substring2 = mbirthday.substring(8, 10);
                Integer integer = Integer.valueOf(substring);
                Integer integer1 = Integer.valueOf(substring1);
                Integer integer2 = Integer.valueOf(substring2);
                UserDao.getInstance(UserInformationActivity.this).updateUserName(mUserName);
                UserDao.getInstance(UserInformationActivity.this).updateBirthday(integer, integer1, integer2);
                UserDao.getInstance(UserInformationActivity.this).updateHeight(mHeight);
                UserDao.getInstance(UserInformationActivity.this).updateWeight(mWeight);
                UserDao.getInstance(UserInformationActivity.this).updateSex(isgender);
                startActivity(new Intent(UserInformationActivity.this, MeasureActivity.class));
                finish();
            } else {
                ToastUtils.ToastMessage(UserInformationActivity.this, "用户不存在");
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      if (headPortraitPopup!=null) {
          if(headPortraitPopup.isShowing()){
              headPortraitPopup.dismiss();
          }
        }
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode){
            case CROP_RESULT_CODE:
                if (requestCode == CROP_RESULT_CODE && resultCode == RESULT_OK && data != null) {
                    String path = data.getStringExtra(ImageSelectorActivity.EXTRA_RESULT);
                    Bitmap photo = BitmapFactory.decodeFile(path);
                    mCircleImageView.setImageBitmap(photo);
                    uploadFile(path);
                }

                break;
            case CAMERA_WITH_DATA:
                // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                startCropImageActivity(Environment.getExternalStorageDirectory()+"/yxkg"
                        + "/" + userphone+"cup" + ".png");
                break;
        }

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String path = data.getStringExtra(ImageSelectorActivity.EXTRA_RESULT);
            Bitmap photo = BitmapFactory.decodeFile(path);
            mCircleImageView.setImageBitmap(photo);
            uploadFile(path);
        }




    }


    public void uploadFile(final String path) {
        File file = new File(Environment.getExternalStorageDirectory()+"/yxkg"
                + "/",userphone+".png");
        if (!file.exists()) {
            Toast.makeText(UserInformationActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("phone", userphone);


     /* Map<String, String> headers = new HashMap<>();
        headers.put("APP-Key", "APP-Secret222");
        headers.put("APP-Secret", "APP-Secret111");*/

        String url = ConstantUtils.HOST+"appLogo";

        OkHttpUtils.post()//
                .addFile("headLogo", userphone+".png", file)//
                .url(url)//
                .params(params)//
                        // .headers(headers)//
                .build()//
                .execute(new UploadImageback() {


                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(UploadImageBean uploadImageBean) {
                        String status = uploadImageBean.getStatusCode();
                        if (status.equals("1")) {
                            UserDao instance = UserDao.getInstance(UserInformationActivity.this);
                            instance.updateLogoPath(path);
                            System.out.println("上传成功");
                        } else {
                            if (status.equals("000")) {

                            }
                            if (status.equals("010")) {

                            }
                        }
                    }


                });
    }
}
