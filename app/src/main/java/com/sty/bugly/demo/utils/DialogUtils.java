package com.sty.bugly.demo.utils;

import android.content.Context;

import com.sty.bugly.demo.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @Author: tian
 * @UpdateDate: 2021/1/13 4:01 PM
 */
public class DialogUtils {
    public static final int NORMAL_TYPE = 0;
    public static final int ERROR_TYPE = 1;
    public static final int SUCCESS_TYPE = 2;
    public static final int WARNING_TYPE = 3;
    public static final int CUSTOM_IMAGE_TYPE = 4;

    public static SweetAlertDialog showdialog(Context context, int type, String title, String content, String ConfirmText,
                                              String CancelText, int CustomImage, final dialogCallBack callBack) {
        try {
            if (context==null){
                return null;
            }
            final SweetAlertDialog showdialog = new BaseSweetAlertDialog(context, type);
            showdialog.setTitleText(title)
                    .setContentText(content)
                    .setConfirmText(ConfirmText);
            if (!CancelText.equals("")) {
                showdialog.setCancelText(CancelText);
            } else {
                showdialog.showCancelButton(false);
            }

            showdialog.setCustomImage(CustomImage)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            if (showdialog!=null && showdialog.isShowing()){
                                showdialog.cancel();
                            }
                            if (callBack != null) {
                                callBack.ConfirmClick();
                            }
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            if (showdialog!=null && showdialog.isShowing()){
                                showdialog.cancel();
                            }
                            if (callBack != null) {
                                callBack.CancelClick();
                            }
                        }
                    })
                    .show();
            return showdialog;

        }catch (Throwable ex){
            //BuglyUtils.sendCrashManually(ExceptionUtils.newInstance(ex));
            ex.printStackTrace();
        }
        return null;


    }

    public static SweetAlertDialog showdialog(Context context, int type, String title, String content, String ConfirmText,
                                              String CancelText, final dialogCallBack callBack) {
        return showdialog(context, type, title, content, ConfirmText, CancelText, R.drawable.selected, callBack);
    }

    public static SweetAlertDialog showdialog(Context context, int type, String title, String content, String ConfirmText,
                                              int CustomImage, final dialogCallBack callBack) {
        return showdialog(context, type, title, content, ConfirmText, "", CustomImage, callBack);
    }

    public static SweetAlertDialog showdialog(Context context, int type, String title, String content, String ConfirmText,
                                              final dialogCallBack callBack) {
        return showdialog(context, type, title, content, ConfirmText, "", callBack);
    }

    public static SweetAlertDialog showWarningConfirmDialog(Context context,String content)
    {
        return showdialog(context,WARNING_TYPE,"警告",content,"确定",null);
    }

    public static SweetAlertDialog showSuccessConfirmDialog(Context context, String content, dialogCallBack callBack) {
        return showdialog(context, SUCCESS_TYPE, "提示", content, "我知道了", callBack);
    }
    public interface dialogCallBack {
        void ConfirmClick();

        void CancelClick();
    }
}
