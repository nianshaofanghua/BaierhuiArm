package com.tsminfo.android.baierhuiarm.utils;

import android.content.Context;

/**
 * Created by Administrator on 2019/4/19.
 */

public class SystemUtils {

    /**
     * px转换成dp
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue * (scale + 0.5f));
    }

    // 将dip或dp值转换为px值
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
