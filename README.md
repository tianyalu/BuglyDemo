# BuglyDemo 集成腾讯Bugly热更新示例
## 一、概念
腾讯Bugly，为移动开发者提供专业的异常上报和运营统计方案，帮助开发者快速发现并解决异常，同时掌握产品运营动态，及时
跟进用户反馈。  
简单的说Bugly帮助开发者维护apk版本，包括apk的托管、升级（热更新）、异常上报以及运营统计等工作。
可以只集成异常上报；也可以只集成应用升级（自动包含异常上报）；也可以只集成热更新（自动包含应用升级和异常上报）。  
官方推出的Bugly demo的GuiHub地址为：[https://github.com/BuglyDevTeam/Bugly-Android-Demo](https://github.com/BuglyDevTeam/Bugly-Android-Demo)  

## 二、热更新
热更新能力是Bugly为解决开发者紧急修复线上bug，而无需重新发版让用户无感知就能把问题修复的一项能力。
热更新基于微信的Tinker开源方案。
### 2.1 Tinker 
Tinker is a hot-fix solution library for Android, it supports dex, library and resources update 
without reinstall apk.  
其GitHub地址为：[https://github.com/Tencent/tinker](https://github.com/Tencent/tinker)  
Tinker原理如下图所示：  
![image](https://github.com/tianyalu/BuglyDemo/blob/master/show/tinker.png)  

### 2.2 热更新集成步骤（暂未考虑NDK）
详情可参考：[Bugly Android热更新使用指南](https://bugly.qq.com/docs/user-guide/instruction-manual-android-hotfix/?v=20180709165613#_1)
#### 2.2.1 注册并获取APP_ID 
到[Bugly官网](https://bugly.qq.com/v2/index)注册并新建项目，随后在“产品设置”下可以看到“产品信息”中的App ID，
这个ID非常重要，后续需要添加到项目配置中的。  
![image](https://github.com/tianyalu/BuglyDemo/blob/master/show/bugly_appid.png)  
#### 2.2.2 添加依赖插件
工程根目录下`build.gradle`文件中添加：  
```groovy 
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.0'
        
        classpath 'com.tencent.bugly:tinker-support:1.2.0'
    }
}
```
#### 2.2.3 集成SDK
* 在`app module`的`build.gradle`文件中添加内容如下： 
```groovy
android {
    compileSdkVersion 26
    buildToolsVersion '28.0.3'
    defaultConfig {
        applicationId "com.sty.bugly.demo"
        minSdkVersion 19
        targetSdkVersion 26
        versionCode 4
        versionName "1.1.2"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"

        //开启multidex
        multiDexEnabled true
    }

    //签名配置
    signingConfigs {
        release {
            try {
                storeFile file("./keystore/bugly_demo_keystore")
                storePassword "123456"
                keyAlias "key0"
                keyPassword "123456"
            } catch (ex) {

            }
        }

        debug {
            try {
                storeFile file("./keystore/bugly_demo_keystore")
                storePassword "123456"
                keyAlias "key0"
                keyPassword "123456"
            } catch (ex) {

            }
        }
    }

    //构建类型
    buildTypes {
        release {
            minifyEnabled true
            signingConfig signingConfigs.release
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
        debug {
            minifyEnabled false
            signingConfig signingConfigs.debug
            debuggable true
        }
    }

}

dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    implementation 'com.android.support:support-v4:26.1.0'
    implementation 'com.android.support:appcompat-v7:26.1.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    
    implementation 'com.android.support:multidex:1.0.3'
    implementation 'com.tencent.bugly:crashreport_upgrade:1.4.2'
    implementation 'com.tencent.tinker:tinker-android-lib:1.9.14.3'
}

// 依赖插件脚本
apply from: 'tinker-support.gradle'
```

* 在与`app module`的`build.gradle`文件同级目录下新建`tinker-support.gradle`文件，其内容如下：  
```groovy
apply plugin: 'com.tencent.bugly.tinker-support'

def bakPath = file("${buildDir}/bakApk/")

/**
 * 此处填写每次构建生成的基准包目录
 */
def baseApkDir = "app-1214-10-27-49"

/**
 * 对于插件各参数的详细解析请参考
 */
tinkerSupport {

    // 开启tinker-support插件，默认值true
    enable = true

    // 指定归档目录，默认值当前module的子目录tinker
    autoBackupApkDir = "${bakPath}"

    // 是否启用覆盖tinkerPatch配置功能，默认值false
    // 开启后tinkerPatch配置不生效，即无需添加tinkerPatch
    overrideTinkerPatchConfiguration = true

    // 编译补丁包时，必需指定基线版本的apk，默认值为空
    // 如果为空，则表示不是进行补丁包的编译
    // @{link tinkerPatch.oldApk }
    baseApk = "${bakPath}/${baseApkDir}/app-release.apk"

    // 对应tinker插件applyMapping
    baseApkProguardMapping = "${bakPath}/${baseApkDir}/app-release-mapping.txt"

    // 对应tinker插件applyResourceMapping
    baseApkResourceMapping = "${bakPath}/${baseApkDir}/app-release-R.txt"

    // 构建基准包和补丁包都要指定不同的tinkerId，并且必须保证唯一性
    tinkerId = "base-1.1.2"

    // 构建多渠道补丁时使用
    // buildAllFlavorsDir = "${bakPath}/${baseApkDir}"

    // 是否启用加固模式，默认为false.(tinker-spport 1.0.7起支持）
    // isProtectedApp = true

    // 是否开启反射Application模式
    enableProxyApplication = false

    // 是否支持新增非export的Activity（注意：设置为true才能修改AndroidManifest文件）
    supportHotplugComponent = true

}

/**
 * 一般来说,我们无需对下面的参数做任何的修改
 * 对于各参数的详细介绍请参考:
 * https://github.com/Tencent/tinker/wiki/Tinker-%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97
 */
tinkerPatch {
    //oldApk ="${bakPath}/${appName}/app-release.apk"
    ignoreWarning = false
    useSign = true
    dex {
        dexMode = "jar"
        pattern = ["classes*.dex"]
        loader = []
    }
    lib {
        pattern = ["lib/*/*.so"]
    }

    res {
        pattern = ["res/*", "r/*", "assets/*", "resources.arsc", "AndroidManifest.xml"]
        ignoreChange = []
        largeModSize = 100
    }

    packageConfig {
    }
    sevenZip {
        zipArtifact = "com.tencent.mm:SevenZip:1.1.10"
//        path = "/usr/local/bin/7za"
    }
    buildConfig {
        keepDexApply = false
        //tinkerId = "1.0.1-base"
        //applyMapping = "${bakPath}/${appName}/app-release-mapping.txt" //  可选，设置mapping文件，建议保持旧apk的proguard混淆方式
        //applyResourceMapping = "${bakPath}/${appName}/app-release-R.txt" // 可选，设置R.txt文件，通过旧apk文件保持ResId的分配
    }
}
```
更详细的配置可参考：[tinker-support配置说明](https://bugly.qq.com/docs/utility-tools/plugin-gradle-hotfix/)  

#### 2.2.4 初始化SDK
自定义`Application(MyApplication)`：  
```java
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
```
**注意：**这个类集成TinkerApplication类，这里面不做任何操作，所有Application的代码都会放到ApplicationLike
继承类当中。  
`ApplicationLike`类中内容如下：  
```java
public class ApplicationLike extends DefaultApplicationLike {
    public static final String TAG = "Tinker.SampleApplicationLike";

    public ApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                           long applicationStartElapsedTime, long applicationStartMillisTime,
                           Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime,
                applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //这里实现SDK初始化
        //第三个参数为SDK调试模式开关，调试模式的行为特征如下：
        //输出详细的Bugly SDK的log
        //每一条Crash都会被立即上报
        //自定义日志将会在Logcat中输出
        //建议在测试阶段设置成true，发布时设置为false
//        CrashReport.initCrashReport(getApplicationContext(), "b2f9f0f3d6", BuildConfig.DEBUG);
        Bugly.init(getApplication(), "b2f9f0f3d6", BuildConfig.DEBUG);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
        // 安装tinker
        Beta.installTinker(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }
}
```
**注意：**  
Tinker需要开启MultiDex，需要在dependencies中配置`implementation 'com.android.support:multidex:1.0.3'`才
可以使用`MultiDex.install`方法；  
`ApplicationLike`这个类是`Application`的代理类，以前所有在`Application`的实现必须全部拷贝到这里，在`onCreate`
方法调用SDK的初始化方法，在`onBaseContextAttached`中调用`Beta.installTinker(this);`。  


#### 2.2.5 `AndroidManifest.xml`配置
`AndroidManifest.xml` 文件内容如下：  
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.sty.bugly.demo">

    <!--权限配置-->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <permission android:name="android.permission.READ_LOGS" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />

    <application
        android:name=".MyApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!--Activity配置-->
        <activity
            android:name="com.tencent.bugly.beta.ui.BetaActivity"
            android:configChanges="keyboardHidden|orientation|screenSize|locale"
            android:theme="@android:style/Theme.Translucent" />

        <!--FileProvider配置-->
        <!--这里要注意一下，FileProvider类是在support-v4包中的，检查你的工程是否引入该类库。-->
        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.fileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths"/>
        </provider>
    </application>

</manifest>
```
在res目录下新建xml文件夹，创建`provider_paths.xml`，文件内容如下：  
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- /storage/emulated/0/Download/${applicationId}/.beta/apk-->
    <external-path name="beta_external_path" path="Download/"/>
    <!--/storage/emulated/0/Android/data/${applicationId}/files/apk/-->
    <external-path name="beta_external_files_path" path="Android/data/"/>
</paths>
```

#### 2.2.6 混淆配置 
在`proguard-rules.pro`文件中添加如下内容：  
```proguard
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}
# tinker混淆规则
-dontwarn com.tencent.tinker.**
-keep class com.tencent.tinker.** { *; }
```


### 2.3 热更新操作步骤
详情可参考：[热更新使用范例](https://bugly.qq.com/docs/user-guide/instruction-manual-android-hotfix-demo/)  
* 打基准安装包并上报联网：安装包要上传到Bugly管理后台，本地安装此apk并启动。（注：填写唯一的tinkerId，
注释掉`tinker-support.gradle`中的`baseApk = "${bakPath}/${baseApkDir}/app-release.apk"`）  
* 对基准包的bug修复（可以是Java代码变更，资源的变更）  
* 修改基准包路径[替换为bakApk下基于哪个基准版本要修复的文件夹名称]、修改补丁包tinkerId、mapping文件路径
（如果开启了混淆需要配置）、resId文件路径。（打开`tinker-support.gradle`中的
`baseApk = "${bakPath}/${baseApkDir}/app-release.apk"`注释）  
* 执行`buildTinkerPatchRelease`打Release版本补丁包  
* 选择app/build/outputs/patch目录下的补丁包并上传。（注：不要选择tinkerPatch目录下的补丁包，不然上传会有问题）  
* 编辑下发补丁规则，点击“立即下发”  
* 杀死进程并重启基准包，请求补丁策略（SDK会自动下载补丁包并合成）  
* 再次重启基准包，检验补丁应用结果  
* 查看页面，查看激活数据的变化  
