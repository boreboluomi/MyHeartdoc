package com.electrocardio.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.electrocardio.Bean.UploadImageBean;
import com.electrocardio.Bean.UserInformation;
import com.electrocardio.Callback.UploadImageback;
import com.electrocardio.R;
import com.electrocardio.base.BaseApplication;
import com.electrocardio.clipimage.ClipImageActivity;
import com.electrocardio.custom.elec.CircleImageView;
import com.electrocardio.database.UserDao;
import com.electrocardio.imageselector.ImagEntryActivity;
import com.electrocardio.imageselector.ImageConfig;
import com.electrocardio.imageselector.ImageSelector;
import com.electrocardio.imageselector.ImageSelectorActivity;
import com.electrocardio.popup.HeadPortraitPopup;
import com.electrocardio.util.ConstantUtils;
import com.electrocardio.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by ZhangBo on 2016/2/24.
 */
public class PersonInformationActivity extends FragmentActivity implements View.OnClickListener {

    private final int CAMERA_WITH_DATA = 6;
    private final int CROP_RESULT_CODE = 7;
    private ImageView iv_back;// 返回图片按钮
    private TextView tv_title;// 标题
    private RelativeLayout rl_userID;// 用户ID按钮
    private RelativeLayout rl_username;// 姓名按钮
    private RelativeLayout rl_sex;// 性别按钮
    private RelativeLayout rl_birthday;// 出生日期按钮
    private RelativeLayout rl_height;// 身高按钮
    private RelativeLayout rl_weight;// 体重按钮
    private RelativeLayout rl_back;// 退出登录按钮

    private TextView userID;// 用户ID
    private TextView tv_username;// 用户名
    private TextView tv_sex;// 性别
    private TextView tv_birthday;// 出生日期
    private TextView tv_height;// 身高
    private TextView tv_weight;// 体重

    public static final int USERNAME = 0;
    public static final int SEX = 1;
    public static final int BIRTHDAY = 2;
    public static final int HEIGHT = 3;
    public static final int WEIGHT = 4;
    private BaseApplication instance;
    private UserInformation userInformation;
    private CircleImageView mCircleImageView;
    private HeadPortraitPopup headPortraitPopup;
    private String mphoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personinformatio);
        initView();

        initData();
        setOnClick();
        instance = BaseApplication.getInstance();
        instance.addActivity(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        userInformation = UserDao.getInstance(this).getUser();
        mphoneNumber = userInformation.getPhoneNumber();
        userID.setText(userInformation.getUserId());
        tv_username.setText(userInformation.getUserName());
        tv_sex.setText(userInformation.getSEX() == 2 ? "女" : "男");
        tv_height.setText(userInformation.getHeight() + "cm");
        tv_weight.setText(userInformation.getWeight() + "kg");
        tv_birthday.setText(userInformation.getBirthdayLabel());
        String logoSrc = userInformation.getLogoSrc();
        if( !logoSrc.equals("")){
            Bitmap photo = BitmapFactory.decodeFile(logoSrc);
            mCircleImageView.setImageBitmap(photo);
        }else{
            if(UserDao.getInstance(this).getUser().getSEX()==2){
                mCircleImageView.setImageResource(R.mipmap.woman);
            }else{
                mCircleImageView.setImageResource(R.mipmap.man);
            }
        }


    }

    /**
     * 初始化界面
     */
    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_titleLeft);// 返回图片按钮
        tv_title = (TextView) findViewById(R.id.tv_title);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);// 退出登录按钮
        rl_userID = (RelativeLayout) findViewById(R.id.rl_userID);// 用户ID按钮
        rl_username = (RelativeLayout) findViewById(R.id.rl_username);// 姓名按钮
        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);// 性别按钮
        rl_birthday = (RelativeLayout) findViewById(R.id.rl_birthday);// 出生日期按钮
        rl_height = (RelativeLayout) findViewById(R.id.rl_height);// 身高按钮
        rl_weight = (RelativeLayout) findViewById(R.id.rl_weight);// 体重按钮

        userID = (TextView) findViewById(R.id.tv_userID);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);
        tv_height = (TextView) findViewById(R.id.tv_height);
        tv_weight = (TextView) findViewById(R.id.tv_weight);

        mCircleImageView = (CircleImageView) findViewById(R.id.personinformation_user_head);


        tv_title.setText("个人信息");


    }

    public static final int REQUEST_CODE = 1000;
    private ArrayList<String> path = new ArrayList<>();

    /**
     * 设置点击事件
     */
    private void setOnClick() {
        // 用户ID按钮的点击事件
        rl_userID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkNetWork(PersonInformationActivity.this)){
                    ToastUtils.ToastMessage(PersonInformationActivity.this, "网络不可用");
                }else{
                    headPortraitPopup = new HeadPortraitPopup(PersonInformationActivity.this);
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


                            ImageSelector.open(PersonInformationActivity.this, imageConfig);

                        }
                    });
                    headPortraitPopup.showPopupWindow();
                }

            }
        });
        rl_username.setOnClickListener(this);// 姓名按钮
        rl_sex.setOnClickListener(this);// 性别按钮
        rl_birthday.setOnClickListener(this);// 出生日期按钮
        rl_height.setOnClickListener(this);// 身高按钮
        rl_weight.setOnClickListener(this);// 体重按钮

        // 退出登录按钮的点击事件
        rl_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        PersonInformationActivity.this);
                builder.setMessage("确定要退出吗？");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                setResult(-1);
                                UserDao instance = UserDao.getInstance(PersonInformationActivity.this);
                                instance.updateToken("");
                                finish();
                                // instance.exit(true);
                                // onBackPressed();
                            }
                        });
                builder.setNegativeButton("取消", null);
                builder.create();
                builder.show();
            }
        });

        // 返回按钮的点击事件
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (headPortraitPopup!=null) {
           if(  headPortraitPopup.isShowing()){
               headPortraitPopup.dismiss();
           }
        }


        if (resultCode != RESULT_OK) {
            System.out.println("requestCode");
            return;
        }
        switch (requestCode) {


           case USERNAME:
                if ( resultCode == RESULT_OK && data != null){
                    tv_username.setText(data.getStringExtra("USERNAME"));
                    UserDao.getInstance(this).updateUserName(data.getStringExtra("USERNAME"));
                }

                break;
            case SEX:
                if ( resultCode == RESULT_OK && data != null){
                    tv_sex.setText(data.getStringExtra("SEX"));
                    UserDao.getInstance(this).updateSex(data.getStringExtra("SEX").equals("男") ? 1 : 2);
                }

                break;
            case BIRTHDAY:
                if( resultCode == RESULT_OK && data != null){
                    String birthday = data.getStringExtra("BIRTHDAY");
                    tv_birthday.setText(birthday);
                    int year = Integer.parseInt(birthday.substring(0, 4));
                    int month = Integer.parseInt(birthday.substring(5, 7));
                    int day = Integer.parseInt(birthday.substring(8, 10));
                    UserDao.getInstance(this).updateBirthday(year, month, day);
                }
                break;
            case HEIGHT:
                if( resultCode == RESULT_OK && data != null){
                    tv_height.setText(data.getStringExtra("HEIGHT"));
                    UserDao.getInstance(this).updateHeight(data.getStringExtra("HEIGHT")
                            .substring(0, data.getStringExtra("HEIGHT").length() - 2));
                }

                break;
            case WEIGHT:
                if( resultCode == RESULT_OK && data != null){
                    tv_weight.setText(data.getStringExtra("WEIGHT"));
                    UserDao.getInstance(this).updateWeight(data.getStringExtra("WEIGHT")
                            .substring(0, data.getStringExtra("WEIGHT").length() - 2));
                }

                break;

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
                        + "/" + mphoneNumber+"cup" + ".png");

               // System.out.println("(Environment.getExternalStorageDirectory():"+Environment.getExternalStorageDirectory());
                break;
        }

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String path = data.getStringExtra(ImageSelectorActivity.EXTRA_RESULT);
            Bitmap photo = BitmapFactory.decodeFile(path);
            mCircleImageView.setImageBitmap(photo);
            uploadFile(path);
        }




    }

    @Override
    public void onClick(View v) {
        int requestCode = -1;
        String name = "";
        String value = "";
        switch (v.getId()) {

            case R.id.rl_username:
                name = "USERNAME";
                value = tv_username.getText().toString().trim();
                requestCode = USERNAME;
                break;
            case R.id.rl_sex:
                name = "SEX";
                value = tv_sex.getText().toString().trim();
                requestCode = SEX;
                break;
            case R.id.rl_birthday:
                name = "BIRTHDAY";
                value = tv_birthday.getText().toString().trim();
                requestCode = BIRTHDAY;
                break;
            case R.id.rl_height:
                name = "HEIGHT";
                value = tv_height.getText().toString().trim();
                requestCode = HEIGHT;
                break;
            case R.id.rl_weight:
                name = "WEIGHT";
                value = tv_weight.getText().toString().trim();
                if (value.equals("kg"))
                    value = 55 + "kg";
                requestCode = WEIGHT;
                break;
        }
        Intent intent = new Intent(this, PersonInformationEditActivity.class);
        intent.putExtra(name, value);
        intent.addFlags(requestCode);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }

    @Override
    public void onBackPressed() {
        setResult(0);
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_enter,
                R.anim.activity_back_exit);
    }


    private void startCapture() {
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
              Environment.getExternalStorageDirectory() + "/yxkg", mphoneNumber + "cup" + ".png")));

        startActivityForResult(intent, CAMERA_WITH_DATA);
    }


    private void startCropImageActivity(String path) {
        ClipImageActivity.startActivity(this, path, CROP_RESULT_CODE);
    }

    /**
     * 通过uri获取文件路径
     *
     * @param mUri
     * @return
     */
    public String getFilePath(Uri mUri) {
        try {
            if (mUri.getScheme().equals("file")) {
                return mUri.getPath();
            } else {
                return getFilePathByUri(mUri);
            }
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    // 获取文件路径通过url
    private String getFilePathByUri(Uri mUri) throws FileNotFoundException {
        Cursor cursor = getContentResolver()
                .query(mUri, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(1);
    }


    public void uploadFile(final String path) {
        File file = new File(Environment.getExternalStorageDirectory()+"/yxkg",mphoneNumber+".png");
        if (!file.exists()) {
            Toast.makeText(PersonInformationActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("phone",mphoneNumber);
        String url = ConstantUtils.HOST+"appLogo";

        OkHttpUtils.post()//
                .addFile("headLogo", mphoneNumber+".png", file)//
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
                        // String status = uploadImageBean.getStatus();
                        String statusCode = uploadImageBean.getStatusCode();
                        if (statusCode.equals("1")) {
                            UserDao instance = UserDao.getInstance(PersonInformationActivity.this);
                            instance.updateLogoPath(path);
                        } else {
                            if (uploadImageBean.getStatusCode().equals("000")) {

                            }
                            if (uploadImageBean.getStatusCode().equals("010")) {

                            }
                        }
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
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
