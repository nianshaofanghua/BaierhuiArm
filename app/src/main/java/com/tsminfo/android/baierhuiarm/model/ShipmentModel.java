package com.tsminfo.android.baierhuiarm.model;

public class ShipmentModel {

    /**
     * 来源
     */
    private String from;

    /**
     * 指令
     */
    private String cmd;

    /**
     * 订单号
     */
    private String order_sn;

    /**
     * 设备号
     */
    private String dev_no;

    /**
     * 商品ID
     */
    private String goods_id;

    /**
     * 数量
     */
    private String num;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getDev_no() {
        return dev_no;
    }

    public void setDev_no(String dev_no) {
        this.dev_no = dev_no;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
