package com.tsminfo.android.baierhuiarm.helper;

import android.content.Context;
import android.icu.text.DateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.tsminfo.android.baierhuiarm.utils.PropertiesUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读写文件
 */
public class PropertiesHelper {

    private final static String TAG = "PropertiesHelper";
    private final static String ORDERNUM = "orderNum";

    /**
     * 写入订单号
     */
    public static void writeOrderNo(List<String> orderNoes, Context context){
        PropertiesUtil mProp = PropertiesUtil.getInstance(context).init();
        mProp.open();
        mProp.clearItems();
        int orderNum = orderNoes.size();
        if (orderNum != 0){
            mProp.writeInt(ORDERNUM,orderNum);
            for (int i = 0; i < orderNum; i++) {
                String orderNo = orderNoes.get(i);
                mProp.writeString(""+i,orderNo);
            }
        }
        mProp.commit();
    }
    /**
     * 读取订单号
     */
    public static List<String> readOrderNo(Context context){
        PropertiesUtil mProp = PropertiesUtil.getInstance(context).init();
        mProp.open();
        int orderNum = mProp.readInt(ORDERNUM);
        List<String> orderNoes = new ArrayList<>();
        if (orderNum != 0){
            for (int i = 0; i < orderNum; i++) {
                String orderNo = mProp.readString(""+i);
                orderNoes.add(orderNo);
            }
        }
        mProp.commit();
        return orderNoes;
    }
    /**
     * 写入广告任务
     */
    public static void writeAdTask(String adTask, int imageCount, int videoCount, Context context){
        PropertiesUtil mProp = PropertiesUtil.getInstance(context).init();
        mProp.open();
        mProp.clearItems();
        mProp.writeString("adTask",adTask);
        mProp.writeLong("timePush",System.currentTimeMillis());
        mProp.writeInt("imageCount",imageCount);
        mProp.writeInt("videoCount",videoCount);
        mProp.commit();
    }
    /**
     * 读取广告任务
     */
    public static Map<String,Object> readAdTask(Context context){
        Map<String,Object> map = new HashMap<>();
        PropertiesUtil mProp = PropertiesUtil.getInstance(context).init();
        mProp.open();
        map.put("adTask",mProp.readString("adTask"));
        map.put("timePush",mProp.readLong("timePush"));
        map.put("imageCount",mProp.readInt("imageCount"));
        map.put("videoCount",mProp.readInt("videoCount"));
        mProp.commit();
        return map;
    }
    /**
     * 删除广告任务
     */
    public static void deleteAdTask(Context context){
        PropertiesUtil mProp = PropertiesUtil.getInstance(context).init();
        mProp.open();
        mProp.clearItems();
        mProp.commit();
    }
}
