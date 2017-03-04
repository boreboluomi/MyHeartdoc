package com.electrocardio.Bean;

/**
 * Created by ZhangBo on 2016/3/9.
 */
public enum UploadDataState {
    UPLOADING(0),// 正在上传
    WAITUPLOAD(1),// 等待上传
    UPLOADPAUSE(2),// 暂停上传
    UPLOADAGINE(3),// 重新上传
    NOTUPLOAD(4),// 上传
    UPLOADOVER(5);// 上传完毕

    private int mState;

    private UploadDataState(int state) {
        mState = state;
    }

    public int getState() {
        return mState;
    }

}
