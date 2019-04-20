package com.tsminfo.android.baierhuiarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tsminfo.android.baierhuiarm.activity.IndexActivity;
import com.tsminfo.android.baierhuiarm.activity.IndexFragmentActivity;

public class BaierhuiReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
//            Intent mainActivityIntent = new Intent(context, IndexActivity.class);  // 要启动的Activity
            Intent mainActivityIntent = new Intent(context, IndexFragmentActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
    }
}
