package com.sty.bugly.demo.model;

/**
 * @Author: tian
 * @UpdateDate: 2021/1/13 6:45 PM
 */
public class ApiCancelEvent {
    private String tag;

    public ApiCancelEvent(String tag) {

        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
