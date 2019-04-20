package com.tsminfo.android.baierhuiarm.model;

import java.util.List;

public class BannerListModel {

    /**
     * images
     */
    List<BannerModel> images;

    /**
     * videos
     */
    List<BannerModel> vedio;

    public List<BannerModel> getImages() {
        return images;
    }

    public void setImages(List<BannerModel> images) {
        this.images = images;
    }

    public List<BannerModel> getVedio() {
        return vedio;
    }

    public void setVedio(List<BannerModel> vedio) {
        this.vedio = vedio;
    }
}
