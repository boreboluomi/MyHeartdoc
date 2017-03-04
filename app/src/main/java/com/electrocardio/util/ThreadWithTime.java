package com.electrocardio.util;

/**
 * Created by ZhangBo on 2016/3/12.
 */
public class ThreadWithTime extends Thread {

    private long currentTime = 0;// 当前时间戳

    public ThreadWithTime(long currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * 获取时间戳
     *
     * @return
     */
    public long getCurrentTime() {
        return currentTime;
    }
}
