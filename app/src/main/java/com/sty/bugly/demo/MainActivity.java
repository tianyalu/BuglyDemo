package com.sty.bugly.demo;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sty.bugly.demo.iinterface.ICallback;
import com.sty.bugly.demo.model.UpgradeInfoEvent;
import com.sty.bugly.demo.utils.DialogUtils;
import com.sty.bugly.demo.utils.DownloadApkUtils;
import com.sty.bugly.demo.utils.FileUtil;
import com.sty.bugly.demo.utils.SynchronizeData;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {
    private TextView tvText;
    private TextView tvText2;
    private Button btnCheckUpgrade;
    private Button btnCheckHotFix;
    private Button btnCrash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        tvText = findViewById(R.id.tv_text);
        tvText2 = findViewById(R.id.tv_text2);
        tvText.setText("Hotfix V" + BuildConfig.VERSION_NAME);
//        tvText2.setText("392哈哈哈哈 -- 热更新第二次");
//        tvText2.setText("基线版本 414");
        tvText2.setText("bug is fixed bug已经被修复了, 热更新哈哈哈 ---> 414-1");

        btnCheckUpgrade = findViewById(R.id.btn_check_upgrade);
        btnCheckHotFix = findViewById(R.id.btn_check_hotfix);
        btnCrash = findViewById(R.id.btn_crash);
        btnCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashReport.testJavaCrash();
//                UpgradeInfo info = new UpgradeInfo(null);
//                info.apkUrl = "111";
//                info.newFeature = "这是要测试content是否被截断了这是要测试content是否被截断了这是要测试content是否被截断了";
//                info.title = "title";
//                info.upgradeType = 1;
//                showUpgradeDialog(info);
            }
        });
        btnCheckUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Beta.checkUpgrade();
            }
        });
        btnCheckHotFix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Beta.checkHotFix();
            }
        });
    }

    public void onEventMainThread(UpgradeInfoEvent upgradeInfoEvent) {
        if(upgradeInfoEvent != null) {
            UpgradeInfo upgradeInfo = upgradeInfoEvent.getUpgradeInfo();
            showUpgradeDialog(upgradeInfo);
        }
    }

    private void showUpgradeDialog(final UpgradeInfo upgradeInfo) {
        boolean isForceUpgrade = upgradeInfo.upgradeType == 2; //升级策略 1建议 2强制 3手工
        String cancelText = "下次更新";
        if(isForceUpgrade) {
            cancelText = "";
        }
        final SweetAlertDialog dialog = DialogUtils.showdialog(this, DialogUtils.WARNING_TYPE, "发现新版本",
                upgradeInfo.newFeature, "现在更新", cancelText, new DialogUtils.dialogCallBack() {
                    @Override
                    public void ConfirmClick() {
                        String apkUrl = upgradeInfo.apkUrl;
                        String fileName = "HGS_POS" + upgradeInfo.versionCode + ".apk";

                        downloadApkTask(apkUrl, fileName);
                    }

                    @Override
                    public void CancelClick() {
                    }
                });
        //对话框不能取消，要强制更新
        if(dialog != null) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_F12) {
                        dialog.dismiss();
                    }
                    if (keyCode == KeyEvent.KEYCODE_L) {
                        dialog.dismiss();
                    }
                    return true;
                }
            });
        }
    }

    /**
     *
     * @param apkUrl  下载apk的URL
     * @param fileName 本地生成apk文件的全路径名称
     */
    private void downloadApkTask(final String apkUrl, final String fileName) {
        downloadApk(apkUrl, fileName, new ICallback<Integer>() {
            @Override
            public void onResult(Integer result) {
                if(result == -2) { //第一次失败，其它原因，重试(存储空间不足的情况下就不重试了)
                    downloadApk(apkUrl, fileName, new ICallback<Integer>() {
                        @Override
                        public void onResult(Integer ret) {
                            if(ret == -2) { //第二次失败，切换自己维护的apk下载地址下载(存储空间不足的情况下就不重试了)
                                //checkNewestVersion();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     *
     * @param apkUrl  要下载的apk文件URL
     * @param fileName 本地生成apk文件全路径名称
     * @param callback  1:成功 -1：存储空间不足 -2：其它失败
     */
    private void downloadApk(final String apkUrl, final String fileName, final ICallback<Integer> callback) {
        SynchronizeData.getThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //删除完之后重新生成下载文件
                    final File apkFile = FileUtil.createNewFile(FileUtil.APK_DOWNLOAD_PATH, fileName);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DownloadApkUtils.downloadWithCallback(MainActivity.this, apkUrl, apkFile, new ICallback<Boolean>() {
                                @Override
                                public void onResult(Boolean aBoolean) {
                                    if(callback != null) {
                                        callback.onResult(aBoolean ? 1 : -2);
                                    }
                                }
                            });
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                    if(callback != null) {
                        callback.onResult(-2);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
