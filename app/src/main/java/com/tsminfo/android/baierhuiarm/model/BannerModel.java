package com.tsminfo.android.baierhuiarm.model;

public class BannerModel {

    /**
     * 广告名
     */
    private String name;

    /**
     * 类型 1:图片; 2: 视频
     */
    private String type;

    /**
     * 地址
     */
    private String img;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
