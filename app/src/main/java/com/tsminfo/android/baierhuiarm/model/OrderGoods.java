package com.tsminfo.android.baierhuiarm.model;

/**
 * @Package: com.tsminfo.android.baierhuiarm.activity
 * @Description: 出货失败商品信息
 * @Creator: zzt
 * @Date: Creat in 16:57 2019/4/7 0007
 */
public class OrderGoods {
//    private String goods_id;
    private int goods_id;
    private int num;

    public OrderGoods(String goods_id, int num) {
//        this.goods_id = goods_id;
        this.goods_id = Integer.parseInt(goods_id);
        this.num = num;
    }
}
