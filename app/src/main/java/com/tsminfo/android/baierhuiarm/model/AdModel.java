package com.tsminfo.android.baierhuiarm.model;

import java.util.List;
import java.util.Map;

/**
 * @Package: com.tsminfo.android.baierhuiarm.model
 * @Description: 描述
 * @Creator: zzt
 * @Date: Creat in 17:55 2019/4/7 0007
 */
public class AdModel {

    /**
     * from : backend
     * cmd : delivery
     * adv_group : {"images":[{"name":"3221312312321","type":"1","img":"http://qn.lianshangatm.com/ca5fb94675a04007614b40c1f359a825"}]}
     * release_type : 2
     * time : 5:00--10:00
     * day : 0
     * num : 20
     */

    private String from;
    private String cmd;
    private List<BannerModel> images;
    private List<BannerModel> vedio;
    private String release_type;
    private String time;
    private String day;
    private String num;
    private Map<String,List<BannerModel>> adv_group;

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

    public String getRelease_type() {
        return release_type;
    }

    public void setRelease_type(String release_type) {
        this.release_type = release_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public List<BannerModel> getImages() {
        return adv_group.get("images");
    }

    public void setImages(List<BannerModel> images) {
        this.images = images;
    }

    public List<BannerModel> getVedio() {
        return adv_group.get("vedio");
    }

    public void setVedio(List<BannerModel> vedio) {
        this.vedio = vedio;
    }
}
