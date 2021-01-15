package com.sty.bugly.demo.core;

import android.content.Context;

/**
 * @Author: tian
 * @UpdateDate: 2021/1/13 2:52 PM
 */
public interface ThrowableCatcher {

    void sendCrashManually(Throwable ex);

    void removeCustomizeValue(String key);

    void removeAllCustomizeValue();

    void addCustomizeValue(String key,String value);

    void setUserId(String userId);

    void init(Context context);
}
