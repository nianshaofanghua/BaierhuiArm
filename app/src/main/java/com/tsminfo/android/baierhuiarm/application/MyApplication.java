package com.tsminfo.android.baierhuiarm.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.danikula.videocache.HttpProxyCacheServer;
import com.tencent.bugly.crashreport.CrashReport;
import com.tsminfo.android.baierhuiarm.activity.banner.MyFileNameGenerator;
import com.tsminfo.android.baierhuiarm.config.ArmConstants;
import com.tsminfo.android.baierhuiarm.config.UrlConfig;
import com.tsminfo.android.baierhuiarm.utils.NetUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {


    public String mMachineNo;   // 售货机编号 16进制
    public String mShowMachineNo; // 售货机编号
    public static String mobile;
    public static boolean mNetStatus; // 网络状态

    private HttpProxyCacheServer proxy;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();




        CrashReport.initCrashReport(getApplicationContext(), ArmConstants.BUGLY_LOGIN_APP_ID, true);
        MyApplication.mNetStatus = true;
        initOkHttpClient(); // okHttp初始化
        context = getApplicationContext();
    }

    private void initOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new LoggerInterceptor("TAG"))
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }


    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication app = (MyApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.getProxy()) : app.proxy;
    }

    private HttpProxyCacheServer getProxy() {
        HttpProxyCacheServer proxy = new HttpProxyCacheServer.Builder(this)
                .fileNameGenerator(new MyFileNameGenerator())
                .build();
        return proxy;
    }

    public static Context getContext() {
        return MyApplication.context;
    }

    public String getMachineNo() {
        return mMachineNo;
    }

    public void setMachineNo(String machineNo) {
        mMachineNo = machineNo;
    }

    public String getShowMachineNo() {
        return mShowMachineNo;
    }

    public void setShowMachineNo(String showMachineNo) {
        mShowMachineNo = showMachineNo;
    }

}
