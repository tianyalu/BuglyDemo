package com.sty.bugly.demo.utils;

import android.os.Environment;

import java.io.File;

/**
 * @Author: tian
 * @UpdateDate: 2021/1/13 4:14 PM
 */
public class FileUtil {
    public static final String APK_DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/HGS_POS/apk/";

    /**
     * 创建一个file
     *
     * @param fileName 自己命名
     * @return
     */
    public static File createNewFile(String dirPath, String fileName) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dirPath + fileName);
        try {
            file.createNewFile();
            file.setWritable(true);
        } catch (Throwable ex) {
            //BuglyUtils.sendCrashManually(ExceptionUtils.newInstance(ex));
        }
        return file;
    }
}
