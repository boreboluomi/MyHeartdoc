package com.electrocardio.custom.elec;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.electrocardio.R;
import com.electrocardio.activity.DataExceptionActivity;
import com.electrocardio.bluetoothdata.BluetoothStateConfirmClass;

/**
 * Created by yangzheng on 2016/3/16.
 */
public class ScrollHorizontalView extends HorizontalScrollView {

    private LinearLayout mContainer;
    private View mInflater;
    private LinearLayout mGallery;
    private View mView;

    private Context context;

    public ScrollHorizontalView(Context context) {
        super(context);
        this.context = context;

    }

    public ScrollHorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollHorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void initdata1(Activity mainActivity, float[] con) {
        mContainer = (LinearLayout) getChildAt(0);
        mGallery = (LinearLayout) mContainer.findViewById(R.id.id_gallery);
        LayoutInflater mInflater = LayoutInflater.from(mainActivity);
        mView = mInflater.inflate(R.layout.activity_index_gallery_item, mGallery, false);
        RelativeLayout mEcgView = (RelativeLayout) mView.findViewById(R.id.id_index_gallery_item_image);
        // Random random=new Random();
        // int mImgId = mImgIds[random.nextInt(10)];
        CustomHorizontal customHorizontal = new CustomHorizontal(mainActivity, con);
        mEcgView.addView(customHorizontal);
        mGallery.addView(mView);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mContainer = (LinearLayout) getChildAt(0);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        iCallBack.onScrollChangedData(l, t, oldl, oldt);
    }


    public onScrollChangedDataListener iCallBack = null;

    public interface onScrollChangedDataListener {
        void onScrollChangedData(int l, int t, int oldl, int s);

    }

    public void setonScrollChangedBlackListener(onScrollChangedDataListener Back) {
        iCallBack = Back;
    }


}
