package com.tsminfo.android.baierhuiarm.utils;

/**
 * @Package: com.tsminfo.android.baierhuiarm.utils
 * @Description: 描述
 * @Creator: zzt
 * @Date: Creat in 1:40 2019/4/12 0012
 */
public class Test {
    public static void main(String[] args) {
        String str = "{\"from\":\"backend\",\"cmd\":\"delivery\",\"order_sn\":\"RE5caf6b22693ff861455850\"," +
                "\"dev_no\":\"3534353037383836383531633438313730353066\",\"goods_id\":\"16\",\"num\":\"1\"}\"\\n\"";
        if (str.contains("\"\\n\"")) {
            String strs = str.substring(0, str.length() - 4);
            System.out.println(strs);
        }
    }
}
