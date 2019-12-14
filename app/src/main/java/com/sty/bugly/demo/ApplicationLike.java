package com.sty.bugly.demo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.tinker.entry.DefaultApplicationLike;

/**
 * Created by tian on 2019/12/13.
 */

public class ApplicationLike extends DefaultApplicationLike {
    public static final String TAG = "Tinker.SampleApplicationLike";

    public ApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                           long applicationStartElapsedTime, long applicationStartMillisTime,
                           Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime,
                applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //这里实现SDK初始化
        //第三个参数为SDK调试模式开关，调试模式的行为特征如下：
        //输出详细的Bugly SDK的log
        //每一条Crash都会被立即上报
        //自定义日志将会在Logcat中输出
        //建议在测试阶段设置成true，发布时设置为false
//        CrashReport.initCrashReport(getApplicationContext(), "b2f9f0f3d6", BuildConfig.DEBUG);
        Bugly.init(getApplication(), "b2f9f0f3d6", BuildConfig.DEBUG);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
        // 安装tinker
        Beta.installTinker(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }
}
