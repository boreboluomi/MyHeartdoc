package com.electrocardio.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;

import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/3/10.
 */
public class ContentMeasureDetailActivity extends FragmentActivity {

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contentmeadetail);
        frameLayout = (FrameLayout) findViewById(R.id.fl_container);
    }
}
