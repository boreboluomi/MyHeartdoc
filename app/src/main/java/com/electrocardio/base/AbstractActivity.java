package com.electrocardio.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.electrocardio.R;
import butterknife.ButterKnife;

/**
 * Created by yangzheng on 2015/12/28.
 */
public abstract class AbstractActivity extends ActionBarActivity {
    public static final String TAG = "BaseActivity";
    public static final int NO_LAYOUT = 0;
    protected static BaseApplication mApplication;
    private ActionBar actionBar;

    View.OnClickListener actoinBarOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            onActionBarRight(view);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mApplication == null) {
            mApplication = (BaseApplication) getApplication();
        }
        mApplication.addActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        int id = getLayoutID();
        View contentView = null;
        if (id != NO_LAYOUT) {
            contentView = getLayoutInflater().inflate(id, null);
            setContentView(contentView);
        } else {
            Toast.makeText(this, "contentView is Null!", Toast.LENGTH_SHORT).show();
        }
        if (isOpenActionBar()) {
            initActionBar();
        }
        onInitialize();
    }

    protected abstract int getLayoutID();

    protected abstract void onInitialize();

    protected abstract boolean isNetworkConnected();


    protected abstract boolean isWifiConnected();

    protected abstract boolean isMobileConnected();


    public Context getContext() {
        return mApplication.getApplicationContext();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void onFromOut() {}

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() { super.onResume();}

    @Override
    protected void onPause() {super.onPause();}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplication.removeActivity(this);

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    protected boolean isOpenActionBar() {return false;}

    protected int getActionBarLayoutID() {
        return R.layout.a_actionbar_title;
    }

    protected void onActionBarRight(View view) {}

    protected void onActionBarLeft(View view) {finish();}

    protected boolean isHideActionBar() {
        return false;
    }

    protected Object getActionBarRightValue() {
        return null;
    }

    protected String getActionBarTitle() {
        return "标题";
    }

    protected void setActionBarTitle(String value) {
        TextView view = ButterKnife.findById(actionBar.getCustomView(), android.R.id.title);
        if (view == null) {
            return;
        }
        view.setText(value);
    }
    protected void initActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setCustomView(getActionBarLayoutID());
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);


        RelativeLayout rl_actionbar = ButterKnife.findById(actionBar.getCustomView(), R.id.rl_actoinbar);
        ButterKnife.findById(actionBar.getCustomView(), android.R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onActionBarLeft(view);
            }
        });
        ((TextView) ButterKnife.findById(actionBar.getCustomView(), android.R.id.title)).setText(getActionBarTitle() != null ? getActionBarTitle() : "无标题");

        if (getActionBarRightValue() != null) {
            if (getActionBarRightValue() instanceof Integer) {
                setActionBarRightValue((Integer)getActionBarRightValue());
            } else if (getActionBarRightValue() instanceof String) {
                TextView textView = new TextView(actionBar.getCustomView().getContext());

                textView.setId(android.R.id.button2);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//                textView.setBackgroundResource(R.drawable.a_shape_actionbar_right_btn);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

                textView.setText((String) getActionBarRightValue());
                int padding = getResources().getDimensionPixelOffset(R.dimen.padding_10);
                int padding_2 = getResources().getDimensionPixelOffset(R.dimen.padding_2);
                layoutParams.setMargins(0, 0, padding, 0);
                textView.setPadding(padding, padding_2, 0, padding_2);

                textView.setTextColor(getResources().getColor(android.R.color.white));
                rl_actionbar.addView(textView, layoutParams);
                textView.setOnClickListener(actoinBarOnClick);
            } else if (getActionBarRightValue() instanceof View) {
                View view = (View) getActionBarRightValue();
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                int padding = getResources().getDimensionPixelOffset(R.dimen.padding_10);
                layoutParams.setMargins(0, 0, padding, 0);
                rl_actionbar.addView(view, layoutParams);
                rl_actionbar.setOnClickListener(actoinBarOnClick);
            }
        }
        if (isHideActionBar()) {
            actionBar.hide();
        }
    }

    public void setActionBarRightValue(Integer imageId){
        RelativeLayout rl_actionbar = ButterKnife.findById(actionBar.getCustomView(), R.id.rl_actoinbar);
        ImageView imageView = new ImageView(actionBar.getCustomView().getContext());
        imageView.setId(android.R.id.button2);
        int padding = getResources().getDimensionPixelOffset(R.dimen.padding_10);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setImageResource(imageId);
        imageView.setPadding(padding, padding, padding, padding);
        rl_actionbar.addView(imageView, layoutParams);
        imageView.setOnClickListener(actoinBarOnClick);
    }

}
