package com.electrocardio.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/2/24.
 */
public class SuggestFeedBackActivity extends Activity {

    private ImageView iv_back;
    private TextView tv_title;
    private TextView tv_commit;
    private EditText et_suggestion;
    private TextView tv_curNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestfeedback);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_titleLeft);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_commit = (TextView) findViewById(R.id.tv_titleRight);
        et_suggestion = (EditText) findViewById(R.id.et_suggest);
        tv_curNumber = (TextView) findViewById(R.id.tv_curNumber);
        tv_commit.setVisibility(View.VISIBLE);

        initData();

        setOnButtonClick();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        tv_title.setText("意见反馈");
        tv_commit.setText("提交");
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
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SuggestFeedBackActivity.this, "尚未开发", Toast.LENGTH_SHORT).show();
            }
        });
        et_suggestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_curNumber.setText(s.length() + "/300");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_enter, R.anim.activity_back_exit);
    }
}
