package com.electrocardio.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.electrocardio.Bean.UserInformation;
import com.electrocardio.Bean.UserInformationBean;
import com.electrocardio.Callback.UserInformationCallback;
import com.electrocardio.R;
import com.electrocardio.custom.elec.SelectBirthdayPopupWindow;
import com.electrocardio.custom.elec.SelectSingleRowPopupWindow;
import com.electrocardio.database.UserDao;
import com.electrocardio.util.ConstantUtils;
import com.electrocardio.util.CussorUtils;
import com.electrocardio.util.TestNetWork;
import com.electrocardio.util.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;


import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by ZhangBo on 2016/2/24.
 */
public class PersonInformationEditActivity extends Activity {
    private TextView tv_cancel;// 取消按钮
    private TextView tv_title;// 标题
    private TextView tv_submit;// 保存按钮
    private RelativeLayout rl_username;// 修改姓名
    private EditText et_username;// 姓名编辑框

    private LinearLayout ll_sex;// 修改性别
    private RadioButton rb_man;// 男人单选按钮
    private RadioButton rb_woman;// 女人单选按钮

    private RelativeLayout rl_birthday;// 修改生日
    private TextView tv_birthday;// 生日

    private RelativeLayout rl_height;// 修改身高
    private TextView tv_height;// 身高

    private RelativeLayout rl_weight;// 修改体重
    private TextView tv_weight;// 体重
    private int requestCode;// 编辑类型
    Intent data = new Intent();
    private boolean isFirst = true;

    private int year;
    private int month;
    private int day;

    private int height;
    private int weight;
    private boolean isMan = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personinforedit);

        initView();

        setOnClick();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirst) {
            isFirst = false;
            if (hasFocus)
                switch (requestCode) {
                    case PersonInformationActivity.BIRTHDAY:
                        showBirthdayPopWindow();
                        break;
                    case PersonInformationActivity.HEIGHT:
                        showHeightPopWindow();
                        break;
                    case PersonInformationActivity.WEIGHT:
                        showWeightPopWindow();
                        break;
                }
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);// 取消按钮
        tv_title = (TextView) findViewById(R.id.tv_title);// 标题
        tv_submit = (TextView) findViewById(R.id.tv_submit);// 保存按钮
        rl_username = (RelativeLayout) findViewById(R.id.rl_username);// 修改姓名
        et_username = (EditText) findViewById(R.id.et_username);// 姓名编辑框

        ll_sex = (LinearLayout) findViewById(R.id.ll_sex);// 修改性别
        rb_man = (RadioButton) findViewById(R.id.rb_man);// 男人单选按钮
        rb_woman = (RadioButton) findViewById(R.id.rb_woman);// 女人单选按钮

        rl_birthday = (RelativeLayout) findViewById(R.id.rl_birthday);// 修改生日
        tv_birthday = (TextView) findViewById(R.id.tv_birthday);// 生日

        rl_height = (RelativeLayout) findViewById(R.id.rl_height);// 修改身高
        tv_height = (TextView) findViewById(R.id.tv_height);// 身高

        rl_weight = (RelativeLayout) findViewById(R.id.rl_weight);// 修改体重
        tv_weight = (TextView) findViewById(R.id.tv_weight);// 体重

        Intent intent = getIntent();
        requestCode = intent.getFlags();
        String value = "";
        switch (requestCode) {
            case PersonInformationActivity.USERNAME:
                rl_username.setVisibility(View.VISIBLE);
                value = intent.getStringExtra("USERNAME");
                et_username.setText(value);
                CussorUtils.setCussour(et_username);
                tv_title.setText("更改姓名");
                break;
            case PersonInformationActivity.SEX:
                ll_sex.setVisibility(View.VISIBLE);
                value = intent.getStringExtra("SEX");
                if (value.equals("男")) {
                    rb_man.setChecked(true);
                    rb_woman.setChecked(false);
                    isMan = true;
                } else {
                    rb_woman.setChecked(true);
                    rb_man.setChecked(false);
                    isMan = false;
                }
                tv_title.setText("更改性别");
                break;
            case PersonInformationActivity.BIRTHDAY:
                rl_birthday.setVisibility(View.VISIBLE);
                value = intent.getStringExtra("BIRTHDAY");
                tv_birthday.setText(value.substring(0, 4) + "-"
                        + value.substring(5, 7) + "-" + value.substring(8, 10));
                year = Integer.parseInt(value.substring(0, 4));
                month = Integer.parseInt(value.substring(5, 7));
                day = Integer.parseInt(value.substring(8, 10));
                tv_title.setText("更改出生日期");
                break;
            case PersonInformationActivity.HEIGHT:
                rl_height.setVisibility(View.VISIBLE);
                value = intent.getStringExtra("HEIGHT");
                height = Integer.parseInt(value.substring(0, value.length() - 2));
                tv_height.setText(height + "");
                tv_title.setText("更改身高");
                break;
            case PersonInformationActivity.WEIGHT:
                rl_weight.setVisibility(View.VISIBLE);
                value = intent.getStringExtra("WEIGHT");
                weight = Integer.parseInt(value.substring(0, value.length() - 2));
                tv_weight.setText(weight + "");
                tv_title.setText("更改体重");
                break;
        }
    }

    /**
     * 为所有按钮添加点击事件
     */
    private void setOnClick() {

        // 返回图片的点击事件
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_back_enter,
                        R.anim.activity_back_exit);
            }
        });
        // 提交按钮的点击事件
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        // 男人单选按钮
        rb_man.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    rb_woman.setChecked(false);
                    isMan = true;
                }
            }
        });
        // 女人单选按钮
        rb_woman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    rb_man.setChecked(false);
                    isMan = false;
                }
            }
        });
        // 出生日期按钮的点击事件
        tv_birthday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showBirthdayPopWindow();
            }
        });
        // 体重按钮的点击事件
        tv_weight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showWeightPopWindow();
            }
        });
        // 身高按钮的点击事件
        tv_height.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showHeightPopWindow();
            }
        });
    }

    /**
     * 显示出生日期的popWidow
     */
    private void showBirthdayPopWindow() {
        SelectBirthdayPopupWindow birthdayWin = new SelectBirthdayPopupWindow(
                PersonInformationEditActivity.this, year,
                month - 1, day - 1);
        birthdayWin.setChiness(false);
        birthdayWin.setSelectListener(new SelectBirthdayPopupWindow.SelectListener() {

            @Override
            public void onSelectItem(String str) {
                tv_birthday.setText(str);
                year = Integer.parseInt(str.substring(0, 4));
                month = Integer.parseInt(str.substring(5, 7));
                day = Integer.parseInt(str.substring(8, 10));
            }
        });
        // 显示窗口
        birthdayWin.showAtLocation(PersonInformationEditActivity.this
                .getWindow().getDecorView(), Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    /**
     * 显示体重popWindow
     */
    private void showWeightPopWindow() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 20; i < 150; i++) {
            arrayList.add(i + "kg");
        }
        SelectSingleRowPopupWindow weightWin = new SelectSingleRowPopupWindow(
                this, "体重设置", arrayList, weight - 20);
        weightWin.setSelectListener(new SelectSingleRowPopupWindow.SelectListener() {
                    @Override
                    public void onSelectItem(String str) {
                        tv_weight.setText(str.subSequence(0, str.length() - 2));
                    }
                });
        // 显示窗口
        weightWin.showAtLocation(this.getWindow().getDecorView(),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 显示身高popWindow
     */
    private void showHeightPopWindow() {
        // 实例化SelectPicPopupWindow
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 100; i < 200; i++) {
            arrayList.add(i + "cm");
        }
        SelectSingleRowPopupWindow heightWin = new SelectSingleRowPopupWindow(
                this, "身高设置", arrayList, height - 100);
        heightWin
                .setSelectListener(new SelectSingleRowPopupWindow.SelectListener() {
                    @Override
                    public void onSelectItem(String str) {
                        tv_height.setText(str.subSequence(0, str.length() - 2));
                    }
                });
        // 显示窗口
        heightWin.showAtLocation(this.getWindow().getDecorView(),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    /**
     * 保存修改后的数据
     */
    public void save() {
        if (!new TestNetWork(this).checkNetworkState()) {
            ToastUtils.ToastMessage(this, "网络不可用");
            return;
        }
        UserInformation infor = UserDao.getInstance(this).getUser();
        String birthday = infor.getBirthdayLabel();
        String substring = birthday.substring(0, 4);
        String substring1 = birthday.substring(5, 7);
        String substring2 = birthday.substring(8, 10);
        birthday = substring + "-" + substring1 + "-" + substring2;
        HashMap<String, String> params = new HashMap<>();

        switch (requestCode) {
            case PersonInformationActivity.USERNAME:
                params.put("phone", infor.getPhoneNumber());
                params.put("name", et_username.getText().toString().trim());
                params.put("sex", infor.getSEX() + "");
                params.put("weight", infor.getWeight() + "");
                params.put("height", infor.getHeight() + "");
                params.put("birthdate", birthday);
                params.put("emergency_contact", "");
                params.put("emergency_contact_tel", "");
                data.putExtra("USERNAME", et_username.getText().toString().trim());
                System.out.println("jinrequestCode:" + requestCode);
                break;
            case PersonInformationActivity.SEX:
                params.put("phone", infor.getPhoneNumber());
                params.put("name", infor.getUserName());
                params.put("sex", isMan ? "1" : "2");
                params.put("weight", infor.getWeight() + "");
                params.put("height", infor.getHeight() + "");
                params.put("birthdate", birthday);
                params.put("emergency_contact", "张武");
                params.put("emergency_contact_tel", "11111111");
                data.putExtra("SEX", isMan ? "男" : "女");
                System.out.println("jinrequestCode:" + requestCode);
                break;
            case PersonInformationActivity.BIRTHDAY:
                String monthStr = month < 10 ? "0" + month : month + "";
                String dayStr = day < 10 ? "0" + day : day + "";
                params.put("phone", infor.getPhoneNumber());
                params.put("name", infor.getUserName());
                params.put("sex", infor.getSEX() + "");
                params.put("weight", infor.getWeight() + "");
                params.put("height", infor.getHeight() + "");
                params.put("birthdate", year + "-" + monthStr + "-" + dayStr);
                params.put("emergency_contact", "");
                params.put("emergency_contact_tel", "");
                data.putExtra("BIRTHDAY", year + "年" + monthStr + "月" + dayStr + "日");
                break;
            case PersonInformationActivity.HEIGHT:
                params.put("phone", infor.getPhoneNumber());
                params.put("name", infor.getUserName());
                params.put("sex", infor.getSEX() + "");
                params.put("weight", infor.getWeight() + "");
                params.put("height", tv_height.getText().toString().trim());
                params.put("birthdate", birthday);
                params.put("emergency_contact", "");
                params.put("emergency_contact_tel", "");
                data.putExtra("HEIGHT", tv_height.getText().toString().trim()
                        + "cm");
                break;
            case PersonInformationActivity.WEIGHT:
                params.put("phone", infor.getPhoneNumber());
                params.put("name", infor.getUserName());
                params.put("sex", infor.getSEX() + "");
                params.put("weight", tv_weight.getText().toString().trim());
                params.put("height", infor.getHeight() + "");
                params.put("birthdate", birthday);
                params.put("emergency_contact", "");
                params.put("emergency_contact_tel", "");
                data.putExtra("WEIGHT", tv_weight.getText().toString().trim() + "kg");
                break;
        }
        saveToInternet(params);
    }

    private void saveToInternet(HashMap<String, String> params) {

        String url = ConstantUtils.HOST + ConstantUtils.UPDATEINFOR;
        OkHttpUtils.post()
                .url(url)
                .params(params)
                .build()
                .execute(new UserInformationCallback() {
                        @Override
                        public void onError(Call call, Exception e) {

                        }

                        @Override
                        public void onResponse(UserInformationBean userInformationBean) {
                            String statusCode = userInformationBean.getStatusCode();
                            if (statusCode.equals("1")){
                                setResult(RESULT_OK, data);
                                finish();
                                overridePendingTransition(R.anim.activity_back_enter,
                                        R.anim.activity_back_exit);
                            }

                        }
                });
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
