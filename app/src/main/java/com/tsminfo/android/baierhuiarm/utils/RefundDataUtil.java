package com.tsminfo.android.baierhuiarm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据持久化存储
 */
public class RefundDataUtil {

    private static final String REFUND_DATA = "refund_data";
    private static final String REFUND_INFO = "refund_info";

    private static final String UNDERLINE = "_";

    private static final String DELIVERY_STATUS_DELIVERY = "1"; // 已出票
    private static final String REFUND_STATUS_REFUNDED = "1"; // 退款成功


    private static SharedPreferences share(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(REFUND_DATA, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    /**
     * 存储数据
     */
    private static Boolean putString(Context context, String name, String value) {

        SharedPreferences.Editor editor = share(context).edit();
        editor.putString(name, value);
        Boolean result = editor.commit(); // 同步写入
        return result;
    }


    /**
     * 读取数据
     *
     * @param context
     * @param name
     * @return
     */
    private static String getString(Context context, String name) {

        return share(context).getString(name, "");
    }


    /**
     * 存储退款信息
     *
     * @param context
     * @param value   订单编号:出票状态_未出票数量_退款状态;
     * @return
     */
    private static Boolean putRefundString(Context context, String value) {

        return putString(context, REFUND_INFO, value);
    }


    /**
     * 读取退款信息
     *
     * @param context
     * @return
     */
    private static String getRefundInfoStr(Context context) {

        return getString(context, REFUND_INFO);
    }


    /**
     * 获取订单Map
     *
     * @param context
     * @return
     */
    private static Map<String, String> getRefundMap(Context context) {

        String refundInfoStr = getRefundInfoStr(context);
        Map<String, String> refundMap = new HashMap<>();
        if (!TextUtils.isEmpty(refundInfoStr)) {   // 不为空
            refundMap = new Gson().fromJson(refundInfoStr, Map.class);
        }

        return refundMap;
    }


    /**
     * 更新订单状态
     *
     * @param context
     * @param orderNo        订单编号
     * @param deliveryStatus 出票状态 0：未出票 1：出票完成
     * @param unDeliveryNum  未出票数量
     * @param refundStatus   退款状态
     * @return
     */
    private static Boolean updateRefundInfo(Context context, String orderNo, Integer deliveryStatus, Integer unDeliveryNum, Integer refundStatus) {

        Map<String, String> refundMap = getRefundMap(context);
        String value = deliveryStatus + UNDERLINE + unDeliveryNum + UNDERLINE + refundStatus;
        refundMap.put(orderNo, value);

        String jsonStr = new Gson().toJson(refundMap);

        Boolean b = putRefundString(context, jsonStr);

        return b;
    }


    /**
     * 获取退款订单信息
     *
     * @param context
     * @return 订单编号:未出货数量
     */
    public static Map<String, Integer> getRefundInfo(Context context) {

        Map<String, Integer> needRefundMap = new HashMap<>();

        Map<String, String> refundMap = getRefundMap(context);
        for (Map.Entry<String, String> entry : refundMap.entrySet()) {
            String valueStr = entry.getValue();
            String[] valueStrs = valueStr.split("[_]");
            Integer unDeliveryNum = Integer.valueOf(valueStrs[1]); // 未出票数量
            if (unDeliveryNum > 0 && !valueStrs[2].equals(REFUND_STATUS_REFUNDED)) {
                // 未出票数量>0且未退款
                needRefundMap.put(entry.getKey(), unDeliveryNum);
            }
        }

        return needRefundMap;
    }


    /**
     * 接收到出货指令
     *
     * @param context
     * @param orderNo     订单编号
     * @param deliveryNum 出票数量
     * @return
     */
    public static Boolean receiveDeliveryOrder(Context context, String orderNo, Integer deliveryNum) {

        return updateRefundInfo(context, orderNo, 0, deliveryNum, 0);
    }


    /**
     * 出票失败
     *
     * @param context
     * @param orderNo
     * @param unDeliveryNum
     * @return
     */
    public static Boolean deliveryFail(Context context, String orderNo, Integer unDeliveryNum) {
        return updateRefundInfo(context, orderNo, 1, unDeliveryNum, 0);
    }


    /**
     * 更新未出票数量
     *
     * @param context
     * @param orderNo
     * @param unDeliveryNum
     * @return
     */
    public static Boolean updateDeliveryNum(Context context, String orderNo, Integer unDeliveryNum) {
        return updateRefundInfo(context, orderNo, 1, unDeliveryNum, 0);
    }


    /**
     * 移除订单
     *
     * @param context
     * @param orderNo
     * @return
     */
    public static Boolean removeOrder(Context context, String orderNo) {

        Map<String, String> refundMap = getRefundMap(context);

        refundMap.remove(orderNo);

        String jsonStr = new Gson().toJson(refundMap);

        Boolean b = putRefundString(context, jsonStr);

        return b;
    }


    public static void main(String[] args) throws Exception {


    }

}
