package com.electrocardio.custom.elec;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.electrocardio.Bean.UploadDataState;
import com.electrocardio.R;

/**
 * Created by ZhangBo on 2016/3/9.
 */
public class UploadDataView extends FrameLayout {

    private ImageView iv_upload;// 上传状态图片
    private TextView tv_uploadLabel;// 上传状态label
    private AnimationDrawable animation;// 帧动画
    private UploadDataState mCurrentState;// 当前上传状态

    public UploadDataView(Context context) {
        super(context);
        initView(context);
    }

    public UploadDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public UploadDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 设置帧动画
     */
    private void setFrameAnimation() {
        iv_upload.setImageResource(R.drawable.frameanimation_upload);
        animation = (AnimationDrawable) iv_upload.getDrawable();
        animation.start();
    }

    /**
     * 取消帧动画
     */
    private void closeFrameAnimation() {
        if (animation != null)
            animation.stop();
        iv_upload.clearAnimation();
        iv_upload.setImageResource(0);
        animation = null;
    }

    /**
     * 对控件进行初始化
     *
     * @param context
     */
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.uploadanimateview, this, true);
        iv_upload = (ImageView) findViewById(R.id.iv_upload);
        tv_uploadLabel = (TextView) findViewById(R.id.tv_uploadStateLabel);
    }

    /**
     * 设置上传状态
     *
     * @param state
     */
    public void setUploadState(UploadDataState state) {
        switch (state) {
            case UPLOADING:// 正在上传
                mCurrentState = UploadDataState.UPLOADING;
                iv_upload.setBackgroundResource(R.mipmap.ic_cloud_2);
                tv_uploadLabel.setText("正在上传");
                setFrameAnimation();
                break;
            case WAITUPLOAD:// 等待上传
                closeFrameAnimation();
                mCurrentState = UploadDataState.WAITUPLOAD;
                iv_upload.setBackgroundResource(R.mipmap.ic_cloud_wait);
                tv_uploadLabel.setText("等待");
                break;
            case UPLOADPAUSE:// 暂停上传
                closeFrameAnimation();
                mCurrentState = UploadDataState.UPLOADPAUSE;
                iv_upload.setBackgroundResource(R.mipmap.ic_cloud_pause);
                tv_uploadLabel.setText("暂停");
                break;
            case UPLOADAGINE:// 重新上传
                closeFrameAnimation();
                mCurrentState = UploadDataState.UPLOADAGINE;
                iv_upload.setBackgroundResource(R.mipmap.ic_cloud_again);
                tv_uploadLabel.setText("重新上传");
                break;
            case NOTUPLOAD:// 上传
                closeFrameAnimation();
                mCurrentState = UploadDataState.NOTUPLOAD;
                iv_upload.setBackgroundResource(R.mipmap.ic_cloud_0);
                tv_uploadLabel.setText("上传");
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        closeFrameAnimation();
        super.onDetachedFromWindow();
    }

    /**
     * 获取当前上传状态
     *
     * @return
     */
    public UploadDataState getCurrentState() {
        return mCurrentState;
    }
}
