package com.sty.bugly.demo.core;

import android.content.Context;

import com.sty.bugly.demo.BuildConfig;
import com.sty.bugly.demo.model.UpgradeInfoEvent;
import com.sty.bugly.demo.utils.DeviceUtils;
import com.sty.bugly.demo.utils.LogUtils;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * @Author: tian
 * @UpdateDate: 2021/1/13 2:52 PM
 */
public class BuglyManager implements ThrowableCatcher {
    private static final String TAG = BuglyManager.class.getSimpleName();

    private Context context;

    public static final String HGS_DEBUG_APP_ID = "b2f9f0f3d6"; //仅sty测试 测试版 使用
    public static final String HGS_BETA_APP_ID = "b2f9f0f3d6";

    public boolean isDevDevice() {
        if("eb3a4da0-3e88-31dd-a07a-467d1ff0483e".equals(DeviceUtils.getDeviceId(context)) || BuildConfig.DEBUG) {
            return true;
        }
        return false;
    }

    public String getAppId(){
        if(BuildConfig.DEBUG) {
            return HGS_DEBUG_APP_ID;
        }else{
            return HGS_BETA_APP_ID;
        }
    }

    @Override
    public void init(Context context){
        this.context = context;
        initBugly(context);
    }

    // 升级：https://bugly.qq.com/docs/user-guide/advance-features-android-beta/?v=20200622202242
    // 热更新：https://bugly.qq.com/docs/user-guide/api-hotfix/?v=20200622202242
    // 其它参数：https://bugly.qq.com/docs/utility-tools/plugin-gradle-bugly/?v=20200622202242
    private void initBugly(Context context) {
        //延迟初始化1S
        Beta.initDelay = 1 * 1000;
        //添加可显示弹窗的Activity
        //Beta.canShowUpgradeActs.add(MainActivity.class);
        //设置自定义升级对话框布局UI
        //Beta.upgradeDialogLayoutId = R.layout.upgrade_dialog;
        // 设置是否开启热更新能力，默认为true
        Beta.enableHotfix = true;
        // 设置是否自动下载补丁
        Beta.canAutoDownloadPatch = true;
        // 设置是否提示用户重启
        Beta.canNotifyUserRestart = false;
        // 设置是否自动合成补丁
        Beta.canAutoPatch = true;
        //不需要自动检查更新
        Beta.autoCheckUpgrade = false;
        if(isDevDevice()) {  //测试设备
            Bugly.setIsDevelopmentDevice(context, true);
        }else {
            Bugly.setIsDevelopmentDevice(context, false);
        }

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
//        Bugly.setUserId(context, "falue");
//        Bugly.setUserTag(context, 123456);
//        Bugly.putUserData(context, "key1", "123");
//        Bugly.setAppChannel(context, "bugly");

        //异常上报
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            public Map<String, String> onCrashHandleStart(int crashType, String errorType,
                                                          String errorMessage, String errorStack) {
                String msg = "onCrashHandleStart: " + errorType + "\\n" + errorMessage + "\\n" + errorStack;
                LogUtils.e(TAG, "CrashUtils.sendCrashManually: " + msg);

                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                String storeGid = "store.getGid()";
                String storeName = "store.getName()";
                map.put("storeGid", storeGid);
                map.put("storeName", storeName);
                // map.put("Key", "Value");
                return map;
            }
            @Override
            public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType,
                                                           String errorMessage, String errorStack) {
                return null;
            }
        });

        strategy.setAppChannel(BuildConfig.DEBUG ? "debug" : "release");
        Beta.autoCheckUpgrade = false;//不需要自动检查更新
        Beta.upgradeListener = new UpgradeListener() {
            @Override
            public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean isSilence) {
                LogUtils.d(TAG, "Beta.upgradeListener: onUpgrade");
                if (strategy != null) {
                    UpgradeInfoEvent event = new UpgradeInfoEvent(strategy);
                    EventBus.getDefault().post(event);
                }
            }
        };

        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId,调试时将第三个参数设置为true
        //这里实现SDK初始化
        //第三个参数为SDK调试模式开关，调试模式的行为特征如下：
        //输出详细的Bugly SDK的log
        //每一条Crash都会被立即上报
        //自定义日志将会在Logcat中输出
        //建议在测试阶段设置成true，发布时设置为false
//        CrashReport.initCrashReport(getApplicationContext(), "b2f9f0f3d6", BuildConfig.DEBUG);
        Bugly.init(context, getAppId(),false, strategy);
        long end = System.currentTimeMillis();
        LogUtils.e("init time--->", end - start + "ms");
    }

    @Override
    public void sendCrashManually(Throwable ex) {
        CrashReport.postCatchedException(ex);
    }

    @Override
    public void removeCustomizeValue(String key) {
        CrashReport.removeUserData(context,key);
    }

    @Override
    public void removeAllCustomizeValue() {
        Set<String> keySet = CrashReport.getAllUserDataKeys(context);
        if (keySet!=null){
            for (String key:keySet) {
                CrashReport.removeUserData(context,key);
            }
        }
    }

    @Override
    public void addCustomizeValue(String key, String value) {
        CrashReport.putUserData(context, key, value);
    }
    @Override
    public void setUserId(String userId){
        CrashReport.setUserId(userId);
    }
}
