package com.sty.bugly.demo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Process;

import com.sty.bugly.demo.R;
import com.sty.bugly.demo.iinterface.ICallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @Author: tian
 * @UpdateDate: 2021/1/13 4:16 PM
 */
public class DownloadApkUtils {

    /**
     * 下载-带有成功或失败结果回调的方法
     * @param context
     * @param apkUrl
     * @param apkFile
     */
    public static void downloadWithCallback(final Context context, final String apkUrl, File apkFile, final ICallback<Boolean> callback) {
        final ProgressDialog progressDialog = getProgressDialog(context);
        new AsyncTask<File, Integer, File>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected File doInBackground(File... params) {
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    File apkFile = params[0];
                    URL url = new URL(apkUrl);
                    HttpURLConnection conn;
                    if(url.getProtocol().toLowerCase().equals("https")) {
                        trustAllHosts();
                        conn = (HttpsURLConnection) url.openConnection();
                        ((HttpsURLConnection)conn).setHostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        });
                    }else {
                        conn = (HttpURLConnection) url.openConnection();
                    }
                    conn.setConnectTimeout(15 * 1000);
                    conn.setReadTimeout(15 * 1000);
                    conn.connect();
                    int length = conn.getContentLength();
                    in = conn.getInputStream();
                    File file = null;
                    if(apkFile.isDirectory()){
                        file = new File(apkFile.getAbsolutePath(),System.currentTimeMillis()+".apk");
                        file.createNewFile();
                    }else{
                        file = apkFile;
                    }
                    file.createNewFile();
                    out = new FileOutputStream(file);

                    byte[] buf = new byte[1024 * 2];
                    int len = 0;
                    long total = 0;

                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                        total += len;
                        int progress = (int) (((float) total / length) * 100);
                        publishProgress(progress);
                    }
                    in.close();
                    out.close();
                    return file;
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    //BuglyUtils.sendCrashManually(ExceptionUtils.newInstance(ex));
                    try{ if (in!=null){in.close();}if (out!=null){out.close();}}catch (Exception ex1){};
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                if (values != null && values.length != 0)
                    progressDialog.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(File file) {
                if (progressDialog!=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                if (file==null){
                    //SuperToastUtils.showError("下载安装包出错！");
                    if(callback != null) {
                        callback.onResult(false);
                    }
                    return;
                }
                if(callback != null) {
                    callback.onResult(true);
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(file);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setDataAndType(uri, "application/vnd.android.package-archive");
                context.startActivity(i);
                Process.killProcess(Process.myPid());
                System.exit(0); // 退出

            }
        }.execute(apkFile);
    }

    private static void trustAllHosts() {
        //create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(tls.getSocketFactory());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ProgressDialog getProgressDialog(Context context) {
        // 创建ProgressDialog对象
        ProgressDialog pdDialog = new ProgressDialog(context);

        // 设置进度条风格，风格为长形
        pdDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        // 设置ProgressDialog 标题
        pdDialog.setTitle("下载新版本");

        // 设置ProgressDialog
        pdDialog.setMessage("正在下载中……");

        // 设置ProgressDialog 标题图标
        pdDialog.setIcon(R.mipmap.download);

        // 设置ProgressDialog
        pdDialog.setMax(100);

        pdDialog.setIndeterminate(false);

        pdDialog.setCancelable(true);

        pdDialog.setCanceledOnTouchOutside(false);

        return pdDialog;

    }

}
