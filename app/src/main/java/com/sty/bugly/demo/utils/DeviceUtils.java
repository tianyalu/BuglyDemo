package com.sty.bugly.demo.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * @Author: tian
 * @UpdateDate: 2021/1/13 5:31 PM
 */
public class DeviceUtils {
    public static String getDeviceId(Context context) {
        String uniqueID = "";
        final String androidId = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("androidId", androidId);
        if (TextUtils.isEmpty(uniqueID)) {
            try {
                if (!"9774d56d682e549c".equals(androidId)) {
                    uniqueID = UUID.nameUUIDFromBytes(androidId
                            .getBytes("utf8")).toString();
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return uniqueID;
    }
}
