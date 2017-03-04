package com.electrocardio.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by yangzheng on 2016/3/17.
 */
public abstract class BaseActivity extends FragmentActivity {
    protected static BaseApplication mApplication;
    public static final int NO_LAYOUT = 0;
    private View contentView=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mApplication == null) {
            mApplication = (BaseApplication) getApplication();
        }
        int id = getLayoutID();

        if (id != NO_LAYOUT) {
            contentView = getLayoutInflater().inflate(id, null);
            setContentView(contentView);

        } else {
            Toast.makeText(this, "contentView is Null!", Toast.LENGTH_SHORT).show();
        }
        Initialize();
    }

    protected abstract int getLayoutID();
    protected abstract void Initialize();
}
