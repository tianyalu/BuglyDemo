package com.sty.bugly.demo.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: tian
 * @UpdateDate: 2021/1/13 4:12 PM
 */
public class SynchronizeData {
    private static volatile ExecutorService mThreadExecutor;
    public static ExecutorService getThreadExecutor() {
        if (mThreadExecutor == null) {
            synchronized (SynchronizeData.class) {
                if(mThreadExecutor == null) {
                    mThreadExecutor = Executors.newScheduledThreadPool(10);
                }
            }
        }
        return mThreadExecutor;
    }
}
