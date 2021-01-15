package com.sty.bugly.demo.utils;

import android.content.Context;

import com.sty.bugly.demo.model.ApiCancelEvent;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;

/**
 * @Author: tian
 * @UpdateDate: 2021/1/13 4:02 PM
 */
public class BaseSweetAlertDialog extends SweetAlertDialog {
    private boolean activityDestroy = false;//记录这个dialog绑定的activity有没有销毁

    public BaseSweetAlertDialog(Context context) {
        super(context);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public BaseSweetAlertDialog(Context context, int alertType) {
        super(context, alertType);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }



    @Override
    public void cancel() {
        try {
            super.cancel();
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        }catch (Throwable ex){

        }
    }

    /**
     * 收到销毁某个activity的event
     *
     * @param event
     */
    public void onEvent(ApiCancelEvent event) {
        if (event.getTag().equals(getContext().getClass().getName())) {
            activityDestroy = true;
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        }
    }

    @Override
    public void dismiss() {
        try {
            if (!activityDestroy) {
                super.dismiss();
            }
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        }catch (Throwable ex){

        }
    }
}
