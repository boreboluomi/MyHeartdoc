package com.electrocardio.activity;

import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import com.electrocardio.R;
import com.electrocardio.base.BaseActivity;
import com.electrocardio.custom.elec.DrawBackground;
import com.electrocardio.custom.elec.ScrollHorizontalView;
import com.electrocardio.custom.elec.SeekBarHint;
import com.electrocardio.util.ThreadUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by yanghzeng on 2016/3/17.
 */
public class DataExceptionActivity extends BaseActivity implements SeekBarHint.OnSeekBarHintProgressChangeListener, View.OnClickListener
        , ScrollHorizontalView.onScrollChangedDataListener {
    private RelativeLayout mBackground;
    private ScrollHorizontalView mroot;
    private InputStream is;
    private String content = ""; //文件内容字符串
    private int z = 0;
    private int s = 0;
    private float[] data = new float[15000];
    private float[] con = new float[1240];
    private RelativeLayout mLoading;
    private Handler mHandler = new Handler();
    private SeekBarHint mSeekBar;
    private RelativeLayout mTitle;
    private String filePath = "";

    @Override
    protected int getLayoutID() {
        return R.layout.activity_exception;
    }

    @Override
    protected void Initialize() {
        initView();
        initdata();
    }

    private void initView() {
        mTitle = (RelativeLayout) findViewById(R.id.exce_title);
        mLoading = (RelativeLayout) findViewById(R.id.exception_loading);
        mroot = (ScrollHorizontalView) findViewById(R.id.root);
        mBackground = (RelativeLayout) findViewById(R.id.exce_ecgbackground);
        mSeekBar = (SeekBarHint) findViewById(R.id.exce_seekbar);


    }

    private void initdata() {
        mLoading.setVisibility(View.VISIBLE);
        DrawBackground bgView = new DrawBackground(this);
        mSeekBar.setOnProgressChangeListener(this);
        mTitle.setOnClickListener(this);
        mroot.setonScrollChangedBlackListener(this);
        filePath = getIntent().getStringExtra("filePath");

        mBackground.addView(bgView);
        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                getCoustomDareData();
            }
        });
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    getCoustomDareData();
//                    mLoading.setVisibility(View.GONE);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 2000);
    }

    public void getCoustomDareData() {
//        is = new FileInputStream(new File(filePath));
//        if (is != null) {
//            InputStreamReader inputreader = new InputStreamReader(is);
//            BufferedReader buffreader = new BufferedReader(inputreader);
//            String line;
//
//            while ((line = buffreader.readLine()) != null) {
//                content += line;
//                if (line != null && !line.equals("")) {
//                    float nData = Float.parseFloat(content);
//                    data[z] = nData;
//                    content = "";
//                    z++;
//                }
//            }
//        }
//
//        for (int d = 0; d < data.length; d++) {
//            con[s] = data[d];
//            s++;
//            if (s == 1239) {
//                mroot.initdata1(DataExceptionActivity.this, con);
//                s = 0;
//            }
//        }
    }

    @Override
    public String onHintTextChanged(SeekBarHint seekBarHint, int progress) {
        mroot.scrollTo(progress, 0);
        mroot.invalidate();
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exce_title:
                finish();
                break;
        }
    }

    @Override
    public void onScrollChangedData(int l, int t, int oldl, int s) {
        mSeekBar.setProgress(l);
        mSeekBar.invalidate();
    }
}
