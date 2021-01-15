package com.sty.bugly.demo.utils;

import android.util.Log;

import com.sty.bugly.demo.BuildConfig;

/**
 * @Author: tian
 * @UpdateDate: 2021/1/12 5:02 PM
 */
public class LogUtils {
    private static final String DefTag = "DEF_TAG";
    private static final boolean forceOpenLog = true;
    private static final boolean isShowLog = forceOpenLog || BuildConfig.DEBUG;

    public static void v(String msg) {
        if(isShowLog) {
            Log.v(DefTag, msg);
        }
    }
    public static void v(String tag, String msg) {
        if(isShowLog) {
            Log.v(tag, msg);
        }
    }

    public static void d(String msg) {
        if(isShowLog) {
            Log.d(DefTag, msg);
        }
    }
    public static void d(String tag, String msg) {
        if(isShowLog) {
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        if(isShowLog) {
            Log.i(DefTag, msg);
        }
    }
    public static void i(String tag, String msg) {
        if(isShowLog) {
            Log.i(tag, msg);
        }
    }

    public static void w(String msg) {
        if(isShowLog) {
            Log.w(DefTag, msg);
        }
    }
    public static void w(String tag, String msg) {
        if(isShowLog) {
            Log.w(tag, msg);
        }
    }

    public static void e(String msg) {
        if(isShowLog) {
            Log.e(DefTag, msg);
        }
    }
    public static void e(String tag, String msg) {
        if(isShowLog) {
            Log.e(tag, msg);
        }
    }
}
