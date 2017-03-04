package com.electrocardio.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/2/24.
 */
public class AboutUsActivity extends Activity {

    private ImageView iv_back;// 返回按钮
    private TextView tv_title;// 标题
    private TextView tv_version;
    private TextView versionRemind;// 版本提示
    private ImageView iv_new;// 新版本提醒
    private RelativeLayout rl_suggestion;// 意见反馈
    private RelativeLayout rl_use;// 使用帮助
    private RelativeLayout rl_checkAndUpdate;// 检查更新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_titleLeft);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_version = (TextView) findViewById(R.id.tv_version);
        iv_new = (ImageView) findViewById(R.id.iv_new);
        versionRemind = (TextView) findViewById(R.id.versionRemind);
        rl_suggestion = (RelativeLayout) findViewById(R.id.rl_suggestion);
        rl_checkAndUpdate = (RelativeLayout) findViewById(R.id.rl_checkAndUpdate);
        rl_use = (RelativeLayout) findViewById(R.id.rl_use);
        tv_title.setText("关于我们");

        setOnButtonClick();
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClick() {
        // 返回按钮的点击事件
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // 意见反馈按钮的点击事件
        rl_suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity(SuggestFeedBackActivity.class);
            }
        });
        // 使用帮助按钮的点击事件
        rl_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutUsActivity.this, "尚未开发", Toast.LENGTH_SHORT).show();
            }
        });
        // 检查更新按钮的点击事件
        rl_checkAndUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutUsActivity.this, "尚未开发", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 开启新的界面
     *
     * @param newClass
     */
    private void startNewActivity(Class newClass) {
        Intent intent = new Intent(this, newClass);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_enter, R.anim.activity_back_exit);
    }
}
