package com.tsminfo.android.baierhuiarm.model;

/**
 * Created by Administrator on 2019/4/19.
 */

public class AdvertListModel {


    /**
     * back_img : http://qn.lianshangatm.com/3cacaf2797676781afa711b31f68a018
     * total : {"one_img":"http://qn.lianshangatm.com/532e5b6d33e234e1b615e9ddc3227cd8","two_img":"http://qn.lianshangatm.com/faac70a2ea4897ae665a79ae65db2c7b","three_img":"http://qn.lianshangatm.com/f045d3144a6d43f6e586ce52a0517ae1","four_img":"http://qn.lianshangatm.com/f309d1fba07a8df1071c7608e17b4cdf"}
     */

    private String back_img;
    private TotalBean total;

    public String getBack_img() {
        return back_img;
    }

    public void setBack_img(String back_img) {
        this.back_img = back_img;
    }

    public TotalBean getTotal() {
        return total;
    }

    public void setTotal(TotalBean total) {
        this.total = total;
    }

    public static class TotalBean {
        /**
         * one_img : http://qn.lianshangatm.com/532e5b6d33e234e1b615e9ddc3227cd8
         * two_img : http://qn.lianshangatm.com/faac70a2ea4897ae665a79ae65db2c7b
         * three_img : http://qn.lianshangatm.com/f045d3144a6d43f6e586ce52a0517ae1
         * four_img : http://qn.lianshangatm.com/f309d1fba07a8df1071c7608e17b4cdf
         */

        private String one_img;
        private String two_img;
        private String three_img;
        private String four_img;

        public String getOne_img() {
            return one_img;
        }

        public void setOne_img(String one_img) {
            this.one_img = one_img;
        }

        public String getTwo_img() {
            return two_img;
        }

        public void setTwo_img(String two_img) {
            this.two_img = two_img;
        }

        public String getThree_img() {
            return three_img;
        }

        public void setThree_img(String three_img) {
            this.three_img = three_img;
        }

        public String getFour_img() {
            return four_img;
        }

        public void setFour_img(String four_img) {
            this.four_img = four_img;
        }
    }
}
