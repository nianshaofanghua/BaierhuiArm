package com.tsminfo.android.baierhuiarm.config;

public class UrlConfig {

    /**
     * 客服电话
     */
    public static final String ATM_GET_SERVICE_TEL = "/v1/atm/service_phone";
//    public static String BASE_URL = "http://meila.yeshitou.com";          //测试环境
    public static String BASE_URL = "https://deploy.lianshangatm.com";      //正式环境
    /**
     * 轮播图
     */
    public static final String ATM_SLIDESHOW_URL = "/v1/atm/home";

    /**
     * 补货员登录
     */
    public static final String ATM_FEEDER_LOGIN_URL = "/v1/atm/feeder_login";

    /**
     * 补货
     */
    public static final String ATM_FEED_URL = "/v1/atm/feed";

    /**
     * 纸巾二维码
     */
    public static final String ATM_SUB_QR_CODE_URL = "/v1/atm/sub_qr_code";

    /**
     * 下单
     */
    public static final String ATM_COMMIT_ORDER_URL = "/v1/atm/commit_order";

    /**
     * 查库存
     */
    public static final String ATM_CHECK_REPERTORY_URL = "/v1/atm/get_inventory";

    /**
     * 检查更新
     */
    public static final String GET_APP_VERSION_URL = "/v1/home/is_apk";

    /**
     * 支付
     */
//    public static final String ATM_GET_NATIVE_PAY_URL = "/v1/atm/get_native_pay";
    public static final String ATM_GET_NATIVE_PAY_URL = "/v2/atm/get_native_pay";

    /**
     * 清空库存
     */
    public static final String ATM_ELIMINATE_URL = "/v1/atm/eliminate";

    /**
     * 登录界面激活二维码
     */
    public static final String ACTIVATE_QR_URL = "/index/index/dev_act?dev_no=";

    /**
     * 微信退款接口
     */
    public static final String ATM_WEIXIN_REFUND_URL = "/v1/pay/weixin_refund";


    /**
     * 补货今日爆款
     */
    public static final String ATM_ADVERT_URL = "/v1/atm/rep_img";

    /**
     * 补货今日爆款详情
     */
    public static final String ATM_ADVERT_DETAIL_URL = "/v1/atm/details_img";

}
