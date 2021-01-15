package com.sty.bugly.demo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.sty.bugly.demo.core.BuglyManager;
import com.sty.bugly.demo.core.BuglyUtils;
import com.sty.bugly.demo.utils.LogUtils;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;
import com.tencent.tinker.entry.DefaultApplicationLike;

import java.util.Locale;

/**
 * Created by tian on 2019/12/13.
 */

public class ApplicationLike extends DefaultApplicationLike {
    public static final String TAG = "Tinker.ApplicationLike";

    public ApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                           long applicationStartElapsedTime, long applicationStartMillisTime,
                           Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime,
                applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BuglyUtils.init(getApplication());
       // initBugly();
    }

    private void initBugly() {
        // 设置是否开启热更新能力，默认为true
        Beta.enableHotfix = true;
        // 设置是否自动下载补丁
        Beta.canAutoDownloadPatch = true;
        // 设置是否提示用户重启
        Beta.canNotifyUserRestart = false;
        // 设置是否自动合成补丁
        Beta.canAutoPatch = true;

        /**
         *  全量升级状态回调
         */
        Beta.upgradeStateListener = new UpgradeStateListener() {
            @Override
            public void onUpgradeFailed(boolean b) {
                LogUtils.d(TAG, "upgradeStateListener.onUpgradeFailed: " + b);
            }

            @Override
            public void onUpgradeSuccess(boolean b) {
                LogUtils.d(TAG, "upgradeStateListener.onUpgradeSuccess: " + b);
            }

            @Override
            public void onUpgradeNoVersion(boolean b) {
                //Toast.makeText(getApplication(), "最新版本", Toast.LENGTH_SHORT).show();
                LogUtils.d(TAG, "upgradeStateListener.onUpgradeNoVersion: " + b);
            }

            @Override
            public void onUpgrading(boolean b) {
                //Toast.makeText(getApplication(), "onUpgrading", Toast.LENGTH_SHORT).show();
                LogUtils.d(TAG, "upgradeStateListener.onUpgrading: " + b);
            }

            @Override
            public void onDownloadCompleted(boolean b) {
                LogUtils.d(TAG, "upgradeStateListener.onDownloadCompleted: " + b);
            }
        };

        /**
         * 补丁回调接口，可以监听补丁接收、下载、合成的回调
         */
        Beta.betaPatchListener = new BetaPatchListener() {
            @Override
            public void onPatchReceived(String patchFileUrl) {
                //Toast.makeText(getApplication(), patchFileUrl, Toast.LENGTH_SHORT).show();
                LogUtils.d(TAG, "betaPatchListener.onPatchReceived: " + patchFileUrl);
            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {
                String text = String.format(Locale.getDefault(),
                        "%s %d%%",
                        Beta.strNotificationDownloading,
                        (int) (totalLength == 0 ? 0 : savedLength * 100 / totalLength));
                //Toast.makeText(getApplication(), text, Toast.LENGTH_SHORT).show();
                LogUtils.d(TAG, "betaPatchListener.onDownloadReceived: " + text);
            }

            @Override
            public void onDownloadSuccess(String patchFilePath) {
                //Toast.makeText(getApplication(), patchFilePath, Toast.LENGTH_SHORT).show();
//                Beta.applyDownloadedPatch();
                LogUtils.d(TAG, "betaPatchListener.onDownloadSuccess: " + patchFilePath);
            }

            @Override
            public void onDownloadFailure(String msg) {
                //Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
                LogUtils.d(TAG, "betaPatchListener.onDownloadFailure: " + msg);
            }

            @Override
            public void onApplySuccess(String msg) {
                //Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
                LogUtils.d(TAG, "betaPatchListener.onApplySuccess: " + msg);
            }

            @Override
            public void onApplyFailure(String msg) {
                //Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
                LogUtils.d(TAG, "betaPatchListener.onApplyFailure: " + msg);
            }

            @Override
            public void onPatchRollback() {
                //Toast.makeText(getApplication(), "onPatchRollback", Toast.LENGTH_SHORT).show();
                LogUtils.d(TAG, "betaPatchListener.onPatchRollback: ");
            }
        };

        long start = System.currentTimeMillis();
        Bugly.setUserId(getApplication(), "falue");
        Bugly.setUserTag(getApplication(), 123456);
        Bugly.putUserData(getApplication(), "key1", "123");
        Bugly.setAppChannel(getApplication(), "bugly");
        Bugly.setIsDevelopmentDevice(getApplication(), true); //todo

        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId,调试时将第三个参数设置为true
        //这里实现SDK初始化
        //第三个参数为SDK调试模式开关，调试模式的行为特征如下：
        //输出详细的Bugly SDK的log
        //每一条Crash都会被立即上报
        //自定义日志将会在Logcat中输出
        //建议在测试阶段设置成true，发布时设置为false
//        CrashReport.initCrashReport(getApplicationContext(), "b2f9f0f3d6", BuildConfig.DEBUG);
        Bugly.init(getApplication(), "b2f9f0f3d6", true);
        long end = System.currentTimeMillis();
        LogUtils.e("init time--->", end - start + "ms");
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
