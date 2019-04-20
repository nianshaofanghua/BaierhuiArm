package com.tsminfo.android.baierhuiarm.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.tsminfo.android.baierhuiarm.helper.SocketClientHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainService extends Service {

    private static final String TAG = "MainService";

    private static final int HEARTBEAT = 60000;

    private Gson mGson;

    public static String mMachineNo;

    private Handler handler = new Handler();
    ExecutorService executor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // 心跳
        mGson = new Gson();
//        executor = Executors.newCachedThreadPool();
//        handler.post(initHeart);
        new Heartbeat().start();
        super.onCreate();
    }

//    Runnable initHeart = new Runnable() {
//
//        @Override
//        public void run() {
//            if (mMachineNo != null) {
////                executor.submit(new Runnable() {
//                executor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        heartbeat();
//                    }
//                });
//            }
//            handler.removeCallbacks(this);
//            handler.postDelayed(this, HEARTBEAT);
//        }
//    };
    private class Heartbeat extends Thread{
        @Override
        public void run() {
            while (true) {
                if (mMachineNo != null) {
                    heartbeat();
                }
                try {
                    sleep(HEARTBEAT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 心跳
    public void heartbeat() {
        Map<String, String> params = new HashMap<>();
        params.put("from", "client");
        params.put("cmd", "heartbeat");
        params.put("dev_no", mMachineNo);

        String jsonStr = mGson.toJson(params);
        SocketClientHelper.getInstance().sendMsg(jsonStr);
    }
}
