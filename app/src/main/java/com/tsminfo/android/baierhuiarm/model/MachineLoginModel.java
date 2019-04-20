package com.tsminfo.android.baierhuiarm.model;

public class MachineLoginModel {

    /**
     * 来源
     */
    private String from;

    /**
     * 指令
     */
    private String cmd;

    /**
     * 设备号
     */
    private String dev_no;

    /**
     * 结果
     */
    private String res;

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

    public String getDev_no() {
        return dev_no;
    }

    public void setDev_no(String dev_no) {
        this.dev_no = dev_no;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }
}
