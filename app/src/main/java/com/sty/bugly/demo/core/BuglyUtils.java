package com.sty.bugly.demo.core;

import android.content.Context;

import com.sty.bugly.demo.utils.SynchronizeData;

import java.net.SocketTimeoutException;

/**
 * @Author: tian
 * @UpdateDate: 2021/1/13 3:37 PM
 */
public class BuglyUtils {

    public static void init(final Context context){
        SynchronizeData.getThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                getThrowableCatcher().init(context);
            }
        });
    }

    public static ThrowableCatcher getThrowableCatcher(){
        //需要修改上传方式，请改这里
        return new BuglyManager();
    }

    public static void sendCrash2HgsLog(Throwable ex) {
        StringBuilder exceptions = new StringBuilder(ex.toString()).append("\n");
//        Store store = Store.getCurrentStore();
//        if (store != null) {
//            exceptions.append(store.getGid()).append(",").append(store.getName());
//        }
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        if (stackTraceElements != null && stackTraceElements.length > 0) {
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                exceptions.append(stackTraceElement.toString()).append("\n");
            }
        }

        sendLog2Server(exceptions.toString());
    }

    private static void sendLog2Server(String log) {
//        ApiClient.getHgsLogApi().sendCrashLog(log, new Callback<Result>() {
//            @Override
//            public void success(Result result, Response response) {
//                StringBuffer a = new StringBuffer();
//                a.append("a");
//            }
//
//            @Override
//            public void failure(RetrofitError retrofitError) {
//
//            }
//        });
    }

    public static void sendCrashManually(Throwable ex){
        sendCrash2HgsLog(ex);
//        if (ex==null){
//            return;
//        }
//
//        if (ex instanceof PayError){
//            return;
//        }
//
//        if (ex instanceof PayCloseError){
//            return;
//        }
//
//        if (ex instanceof SocketTimeoutException){
//            return;
//        }
//
//        if (ex instanceof PayQueryError || ex instanceof RetailUploadError){
//            getThrowableCatcher().sendCrashManually(ex);
//        }

    }

    public static void removeCustomizeValue(String key){
        getThrowableCatcher().removeCustomizeValue(key);
    }

    public static void removeAllCustomizeValue(){
        getThrowableCatcher().removeAllCustomizeValue();
    }

    public static void addCustomizeValue(String key,String value){
        getThrowableCatcher().addCustomizeValue(key, value);
    }

    public static void setUserId(String userId){
        getThrowableCatcher().setUserId(userId);
    }

    public static void sendCustomLog(String errStr) {
        //sendLog2Server(errStr +  ExceptionUtils.getCustomMessage());
    }

}
