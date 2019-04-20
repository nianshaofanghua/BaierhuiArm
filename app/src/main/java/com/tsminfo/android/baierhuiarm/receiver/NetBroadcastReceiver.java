package com.tsminfo.android.baierhuiarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.tsminfo.android.baierhuiarm.utils.NetStatusUtil;

public class NetBroadcastReceiver extends BroadcastReceiver {

    //网络状态监听接口
    private NetStatusMonitor netStatusMonitor;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //获取网络状态的类型
            boolean netStatus = NetStatusUtil.isConn(context);

            System.out.println("当前网络连接状态: " + netStatus);
            if (netStatusMonitor != null)
                // 接口传递网络状态的类型到注册广播的页面
                netStatusMonitor.onNetChange(netStatus);
        }
    }


    /**
     * 网络状态类型改变的监听接口
     */
    public interface NetStatusMonitor {
        void onNetChange(boolean netStatus);

    }

    /**
     * 设置网络状态监听接口
     */
    public void setStatusMonitor(NetStatusMonitor netStatusMonitor) {
        this.netStatusMonitor = netStatusMonitor;
    }
}
