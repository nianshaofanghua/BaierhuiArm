package com.tsminfo.android.baierhuiarm.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetStatusUtil {

    public static boolean isConn(Context context){
        boolean isConnect = false;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null) {
            isConnect = networkInfo.isAvailable();
        }
        return isConnect;
    }
}