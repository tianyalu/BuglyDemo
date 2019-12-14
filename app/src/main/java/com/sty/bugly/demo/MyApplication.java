package com.sty.bugly.demo;

import android.app.Application;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by tian on 2019/12/12.
 */

public class MyApplication extends TinkerApplication {

    /**
     * tinkerFlags: 表示Tinker支持的类型 dex only, library only or all support. default: TINKER_ENABLE_ALL
     * delegateClassName: Application代理类，这里填写自己自定义的ApplicationLike
     * loaderClassName: Tinker的加载器，使用默认即可
     * tinkerLoadVerifyFlag: 加载dex或者lib是否验证md5，默认为false
     */
    public MyApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.sty.bugly.demo.ApplicationLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}
