package com.electrocardio.activity;

import android.app.Activity;
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
public class MessageInformActivity extends Activity {

    private ImageView iv_back;
    private TextView tv_title;
    private RelativeLayout rl_erYue;
    private RelativeLayout rl_feedBack;
    private RelativeLayout rl_other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messageinform);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_titleLeft);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rl_erYue = (RelativeLayout) findViewById(R.id.rl_eryue);
        rl_feedBack = (RelativeLayout) findViewById(R.id.rl_huifu);
        rl_other = (RelativeLayout) findViewById(R.id.rl_other);
        tv_title.setText("消息通知");
        setOnButtonClick();
    }

    /**
     * 设置按钮的点击事件
     */
    private void setOnButtonClick() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rl_erYue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MessageInformActivity.this, "尚未开发", Toast.LENGTH_SHORT).show();
            }
        });
        rl_feedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MessageInformActivity.this, "尚未开发", Toast.LENGTH_SHORT).show();
            }
        });
        rl_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MessageInformActivity.this, "尚未开发", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_enter, R.anim.activity_back_exit);
    }
}
