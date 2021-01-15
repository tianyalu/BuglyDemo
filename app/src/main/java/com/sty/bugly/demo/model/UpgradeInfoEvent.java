package com.sty.bugly.demo.model;

import com.tencent.bugly.beta.UpgradeInfo;

/**
 * UpgradeInfo信息：
 * public String id = "";//唯一标识
 * public String title = "";//升级提示标题
 * public String newFeature = "";//升级特性描述
 * public long publishTime = 0;//升级发布时间,ms
 * public int publishType = 0;//升级类型 0测试 1正式
 * public int upgradeType = 1;//升级策略 1建议 2强制 3手工
 * public int popTimes = 0;//提醒次数
 * public long popInterval = 0;//提醒间隔
 * public int versionCode;
 * public String versionName = "";
 * public String apkMd5;//包md5值
 * public String apkUrl;//APK的CDN外网下载地址
 * public long fileSize;//APK文件的大小
 * pubilc String imageUrl; // 图片url
 *
 * @Author: tian
 * @UpdateDate: 2021/1/13 2:59 PM
 */
public class UpgradeInfoEvent {
    private UpgradeInfo upgradeInfo;

    public UpgradeInfoEvent(UpgradeInfo upgradeInfo) {
        this.upgradeInfo = upgradeInfo;
    }

    public UpgradeInfo getUpgradeInfo() {
        return upgradeInfo;
    }

    public void setUpgradeInfo(UpgradeInfo upgradeInfo) {
        this.upgradeInfo = upgradeInfo;
    }
}
