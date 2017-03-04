package com.electrocardio.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ZhangBo on 2016/3/7.
 */
public class ThreadUtils {

    private ExecutorService executorService;
    private static ThreadUtils threadUtils;

    private ThreadUtils() {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executorService.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    public static ThreadUtils getInstance() {
        if (threadUtils == null)
            threadUtils = new ThreadUtils();
        return threadUtils;
    }

    /**
     * 执行任务
     *
     * @param runnable
     */
    public void execute(Runnable runnable) {
        getExecutorService().execute(runnable);
    }

    /**
     * 获取线程池
     *
     * @return
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }
}
