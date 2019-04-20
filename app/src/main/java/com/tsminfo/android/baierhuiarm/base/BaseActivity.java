package com.tsminfo.android.baierhuiarm.base;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.tsminfo.android.baierhuiarm.application.MyApplication;
import com.tsminfo.android.baierhuiarm.receiver.NetBroadcastReceiver;

public abstract class BaseActivity extends AppCompatActivity implements NetBroadcastReceiver.NetStatusMonitor {

    private static final String TAG = "BaseActivity";

    private NetBroadcastReceiver netBroadcastReceiver;

    public Button btn_floatView;
    public boolean wmTag;
    public WindowManager wm;
    private WindowManager.LayoutParams params;

    @Override
    protected void onStart() {
        super.onStart();

        initWindowManager();
        // 注册网络状态监听的广播
        registerBroadcastReceiver();
    }


    /**
     * 注册网络状态广播
     */
    private void registerBroadcastReceiver() {
        //注册广播
        if (netBroadcastReceiver == null) {
            netBroadcastReceiver = new NetBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netBroadcastReceiver, filter);
            netBroadcastReceiver.setStatusMonitor(this);
        }
    }


    @Override
    public void onNetChange(boolean netStatus) {
        Log.i(TAG, "网络状态变化了: " + netStatus);
        if (!netStatus){
            createFloatView("无法连接服务器,请联系工作人员检查网络设置");
        }else {
            removeFloatView();
        }
        MyApplication.mNetStatus = netStatus;
    }


    /**
     * 初始化提示窗
     */
    public void initWindowManager() {
        wm = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        /*
         * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
         *
         */
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;


        // 设置悬浮窗的长得宽
        params.width = wm.getDefaultDisplay().getWidth();
        params.height = 100;
        params.gravity = Gravity.LEFT | Gravity.TOP;
    }

    /**
     * 创建悬浮窗
     *
     * @param msg
     */
    public void createFloatView(String msg) {
        if (btn_floatView == null) {
            btn_floatView = new Button(getApplicationContext());
            wmTag = true;
        }

        btn_floatView.setText(msg);

        if (wmTag) {
            wm.addView(btn_floatView, params);
            wmTag = false;
        } else {
            wm.updateViewLayout(btn_floatView, params);
        }
    }

    /**
     * 移除悬浮窗
     */
    public void removeFloatView() {
        if (btn_floatView != null){
            wm.removeViewImmediate(btn_floatView);
            btn_floatView = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (netBroadcastReceiver != null) {
            //注销广播
            unregisterReceiver(netBroadcastReceiver);
        }
    }
}
