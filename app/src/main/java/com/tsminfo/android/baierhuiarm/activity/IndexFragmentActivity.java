package com.tsminfo.android.baierhuiarm.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.serialport.SerialPortFinder;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tsminfo.android.baierhuiarm.R;
import com.tsminfo.android.baierhuiarm.activity.banner.Banner;
import com.tsminfo.android.baierhuiarm.activity.login.LoginFragmentActivity;
import com.tsminfo.android.baierhuiarm.application.MyApplication;
import com.tsminfo.android.baierhuiarm.base.BaseActivity;
import com.tsminfo.android.baierhuiarm.config.ArmConstants;
import com.tsminfo.android.baierhuiarm.config.UrlConfig;
import com.tsminfo.android.baierhuiarm.helper.PropertiesHelper;
import com.tsminfo.android.baierhuiarm.helper.SerialPortHelper;
import com.tsminfo.android.baierhuiarm.helper.SocketClientHelper;
import com.tsminfo.android.baierhuiarm.model.AdModel;
import com.tsminfo.android.baierhuiarm.model.BannerListModel;
import com.tsminfo.android.baierhuiarm.model.BannerModel;
import com.tsminfo.android.baierhuiarm.model.MachineLoginModel;
import com.tsminfo.android.baierhuiarm.model.OrderGoods;
import com.tsminfo.android.baierhuiarm.model.OrderRespModel;
import com.tsminfo.android.baierhuiarm.model.RepertoryModel;
import com.tsminfo.android.baierhuiarm.model.ShipmentModel;
import com.tsminfo.android.baierhuiarm.service.MainService;
import com.tsminfo.android.baierhuiarm.utils.DataUtils;
import com.tsminfo.android.baierhuiarm.utils.NetUtils;
import com.tsminfo.android.baierhuiarm.utils.RefundDataUtil;
import com.tsminfo.android.baierhuiarm.utils.SerialPortUtil;
import com.tsminfo.android.baierhuiarm.utils.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class IndexFragmentActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "IndexFragment";

    private static double TICKETPRICE = 0d; // 彩票单价
    private static double CHARGINGPRICE = 0d; // 充电单价
    private static double PAPERPRICE = 0d; // 纸巾单价
    private final static long DELAY_TIME = 20000;   //自动跳转时间
    private final static long REFRESH_TIME = 60 * 60 * 1000;   //重新获取广告间隔时间

    private TextView mDeviceNoTextView;
    private ImageButton mPaperTowelBtn;
    private ImageButton mLotteryTicketBtn;
    private ImageButton mChargingBtn;
    private ImageButton mReplenishmentBtn;
    private LinearLayout mIndexContainer;    // 首页
    private CardView mCardContainer;    // card
    private LinearLayout mPaperScanContainer;    // 扫码领纸巾
    private LinearLayout mPaperTradeResultContainer; // 纸巾领取结果
    private LinearLayout mSelectGoodsNumContainer; // 选择商品数量界面
    private LinearLayout mScanPayContainer; // 扫码支付界面
    private LinearLayout mScanPayContainerRight; // 扫码支付界面右半部分
    private LinearLayout mScanPayContainerCharging;  // 开始充电
    private LinearLayout mChoosePayWayContainer; // 选择支付方式

    private ImageView mSelectNumImageView; // 选择商品数量界面图片
    private ImageButton mGoodsNumAddBtn; // 商品数量增加
    private ImageButton mGoodsNumMinusBtn; // 商品数量减少
    private TextView mGoodsNumTextView; // 商品数量
    private TextView mGoodsAmountTextView; // 商品总价
    private ImageButton mAliPayBtn; // 支付宝支付
    private ImageButton mWeChatBtn; // 微信支付

    private ImageView mScanPayImageView; // 扫码支付界面图片
    private TextView mScanPayGoodsNumTextView; // 扫码支付界面商品数量
    private TextView mScanPayAmountTextView; // 扫码支付界面商品总价
    private TextView mScanPayQrTextView; // 扫码支付界面描述
    private ImageView mScanPayQrImageView; // 扫码支付界面二维码

    private TextView mTradeSuceessTextView;
    private TextView mTimeTextView;
    private LinearLayout mGoodsNumView;
    private TextView mChargingInfoText;

    private ImageView mPaperScanImageView;

    private CountDownTimer mCountDownTimer; // 倒计时

    private Button mReturnBtn; // 返回按mScanPayQrTextView钮

    private Integer mSelectType; // 1: 纸巾 2: 彩票; 3: 充电
    private Integer mCurrentPage = 0; // 1: 根页面 2: 扫码付款; 3: 等待出货 4: 付款成功
    private String mGoodsId;

    private Integer mSelectGoodsNum = 0; // 商品数量

    private String deviceNo;
    private String showMachineNo; // 展示的售货机编号

    private Gson mGson; // 解析数据

    private SerialPortUtil mSerialPortUtil; // 串口通讯

    private Banner mBanner; // 轮播图

    private VideoView mBannerSecond;

    private HttpProxyCacheServer mProxyCacheServer;

    private int index = 0;

    private String lotteryNum;

    private String paperNum;

    private TextView mRepertory;

    private Handler mHandler;

    private TextView mHintText;

    private int count = 3;

    private int currentTime = 0;

    private int ticketType = 6;

    private TextView mPaperPrice;

    private static List<String> orderNoes = new ArrayList<>();     //需要退款的订单号
    private boolean isSlideshowOn = false;
    private static String lastOrderNo;
    //网络请求重试次数：
    private int paperQrRequestCount = 3;
    private int initPaperQrCount = 3;
    private int requestAtmSlideshowCount = 3;
    private int commitOrderRequestCount = 3;
    private int checkRepertoryRequestCount = 3;
    private int weixinRefundCount = 3;
    private boolean isMain = true;  //当前是否主界面
    private int adTaskType = 0;     //广告任务模式，0：无任务；1：天数模式；2：次数模式
    List<BannerModel> modelImages;
    List<BannerModel> modelVideos;
    private boolean hasImageTask = false;
    private boolean hasVideoTask = false;
    public static int imageCount = 0;   //图片剩余次数
    public static int videoCount = 0;   //视频剩余次数
    private static long startTime = 0;
    //    private Calendar startCalendar = Calendar.getInstance();
    //    private Calendar endCalendar = Calendar.getInstance();
    private Calendar finishCalendar = Calendar.getInstance();
    private List<BannerModel> taskImages;
    private List<BannerModel> taskVideos;
    private int startHour = 0;
    private int startMinute = 0;
    private int endHour = 0;
    private int endMinute = 0;
    private String backOrderNo = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_index);

        mGson = new Gson();
        mHandler = new Handler();
        mSerialPortUtil = new SerialPortUtil();




        configIndexView();

        getStoragePermissions();    //请求权限

        machineIsRegister();    // 判断售货机是否注册

        listenSocketResponse();

        count = 3;
        setLottery(ticketType);

        checkPrice();   //查询价格

        initPaperQr();  //初始化纸巾二维码

        Intent intent = new Intent(this, MainService.class);
        startService(intent);

        //        IntentFilter intentFilter = new IntentFilter();
        //        intentFilter.addAction(ArmConstants.START_TASK);
        //        intentFilter.addAction(ArmConstants.END_TASK);
        //        intentFilter.addAction(ArmConstants.FINISH_TASK);
        //
        //        TimingReceiver timingReceiver = new TimingReceiver();
        //        registerReceiver(timingReceiver, intentFilter);

        checkAdTask();
        //        mHandler.postDelayed(reFund, 5 * 60 * 1000);      //延时处理未成功的退款

        mProxyCacheServer = MyApplication.getProxy(this);
    }

    private void configIndexView() {
        mDeviceNoTextView = findViewById(R.id.device_no_text_view);
        mPaperTowelBtn = findViewById(R.id.index_paper_towel_button);
        mPaperTowelBtn.setOnClickListener(this);
        mLotteryTicketBtn = findViewById(R.id.index_lottery_ticket_button);
        mLotteryTicketBtn.setOnClickListener(this);
        mChargingBtn = findViewById(R.id.index_charging_button);
        mChargingBtn.setOnClickListener(this);
        mReplenishmentBtn = findViewById(R.id.index_replenishment_button);
        mReplenishmentBtn.setOnClickListener(this);
        mIndexContainer = findViewById(R.id.index_container);
        mCardContainer = findViewById(R.id.card_container);
        mPaperScanContainer = findViewById(R.id.paper_scan_container);
        mPaperTradeResultContainer = findViewById(R.id.paper_trade_result_container);
        mSelectGoodsNumContainer = findViewById(R.id.select_goods_num_container);
        mScanPayContainer = findViewById(R.id.scan_pay_container);
        mScanPayContainerRight = findViewById(R.id.scan_pay_container_right);
        mScanPayContainerCharging = findViewById(R.id.scan_pay_container_charging);
        mChoosePayWayContainer = findViewById(R.id.choose_pay_way_container);
        mSelectNumImageView = findViewById(R.id.select_num_img);
        mGoodsNumAddBtn = findViewById(R.id.goods_num_add_btn);
        mGoodsNumAddBtn.setOnClickListener(this);
        mGoodsNumMinusBtn = findViewById(R.id.goods_num_minus_btn);
        mGoodsNumMinusBtn.setOnClickListener(this);
        mGoodsNumTextView = findViewById(R.id.goods_num_text_view);
        mGoodsAmountTextView = findViewById(R.id.goods_amount_text_view);
        mAliPayBtn = findViewById(R.id.alipay_image_btn);
        mAliPayBtn.setOnClickListener(this);
        mWeChatBtn = findViewById(R.id.wechat_image_btn);
        mWeChatBtn.setOnClickListener(this);
        mScanPayImageView = findViewById(R.id.scan_pay_image);
        mScanPayGoodsNumTextView = findViewById(R.id.scan_pay_total_text_view);
        mScanPayAmountTextView = findViewById(R.id.scan_pay_amount_text_view);
        mScanPayQrTextView = findViewById(R.id.scan_pay_qr_text_view);
        mScanPayQrImageView = findViewById(R.id.scan_pay_qr_image_view);
        mPaperScanImageView = findViewById(R.id.paper_scan_image_view);
        mTradeSuceessTextView = findViewById(R.id.trade_success_text_view);
        mTimeTextView = findViewById(R.id.time_text_view);
        mReturnBtn = findViewById(R.id.return_btn);
        mReturnBtn.setOnClickListener(this);
        mBanner = findViewById(R.id.index_banner);
        mBannerSecond = findViewById(R.id.index_banner2);
        mRepertory = findViewById(R.id.goods_repertory_text_view);
        mHintText = findViewById(R.id.hint_success_text);
        mPaperPrice = findViewById(R.id.paper_price_text);
        mGoodsNumView = findViewById(R.id.goods_num_view);
        mChargingInfoText = findViewById(R.id.charging_info_text);
    }


    @Override
    public void onClick(View view) {
        mHandler.removeCallbacks(autoSkip);
        mHandler.postDelayed(autoSkip, DELAY_TIME);
        switch (view.getId()) {
            case R.id.index_paper_towel_button:
                Log.i(TAG, "纸巾......");
                mGoodsId = "3";
                if (PAPERPRICE < 0.01d) {
                    checkRepertoryRequestCount = 3;
                    checkRepertoryRequest("3");
                } else {
                    mPaperPrice.setText(Html.fromHtml(
                            "第一包 <font color='#FF7739'><big>免费</big></font>，第二包<font color='#FF7739'><big>" + PAPERPRICE + "</big></font> 元"));
                }
                paperQrRequestCount = 3;
                paperQrRequest();
                break;

            case R.id.index_lottery_ticket_button:
                Log.i(TAG, "彩票......");
                checkRepertoryRequestCount = 3;
                checkRepertoryRequest("16");
                indexSelectGoodsNumClick(2);
                break;
            case R.id.index_charging_button:
                Log.i(TAG, "充电......");
                if (CHARGINGPRICE < 0.01d) {
                    checkRepertoryRequestCount = 3;
                    checkRepertoryRequest("17");
                }
                indexSelectGoodsNumClick(3);
                break;
            case R.id.index_replenishment_button:
                Log.i(TAG, "补货......");
                replenishClick();
                break;
            case R.id.goods_num_add_btn:
                optGoodsNum(true);
                break;
            case R.id.goods_num_minus_btn:
                optGoodsNum(false);
                break;
            case R.id.alipay_image_btn:
                selectPayWay("alipay");
                break;
            case R.id.wechat_image_btn:
                selectPayWay("weixin");
                break;
            case R.id.return_btn:
                returnClick();
                break;
            default:
                break;
        }
    }

    /********** 首页点击事件 **********/
    // 进入免费领纸巾界面
    private void configIndexPaperView(String qrUrl) {
        mSelectType = 1;
        mCurrentPage = 1;
        mHandler.removeCallbacks(autoSkip);
        mHandler.postDelayed(autoSkip, DELAY_TIME);
        setAllGone();
        mReturnBtn.setVisibility(View.VISIBLE);
        mCardContainer.setVisibility(View.VISIBLE);
        mPaperScanContainer.setVisibility(View.VISIBLE);
        Glide.with(this).load(qrUrl).into(mPaperScanImageView);
    }

    // 进入彩票/充电页面
    private void indexSelectGoodsNumClick(Integer selectType) {
        mSelectGoodsNum = 1;
        mSelectType = selectType;
        mCurrentPage = 1;
        setAllGone();
        if (selectType == 2) {
            mSelectNumImageView.setImageResource(R.drawable.lottery_ticket_img);
            configGoodsNumView();
            mGoodsId = "16";
            mRepertory.setVisibility(View.VISIBLE);
            mGoodsNumView.setVisibility(View.VISIBLE);
        } else {
            mSelectNumImageView.setImageResource(R.drawable.charging_img);
            configGoodsNumView();
            mGoodsId = "17";
            mChargingInfoText.setVisibility(View.VISIBLE);
        }
        mCardContainer.setVisibility(View.VISIBLE);
        mChoosePayWayContainer.setVisibility(View.VISIBLE);
        mSelectGoodsNumContainer.setVisibility(View.VISIBLE);
        mReturnBtn.setVisibility(View.VISIBLE);
    }

    // 补货
    private void replenishClick() {
        Intent intent = new Intent(this, AdvertActivity.class);
        isMain = false;
        startActivity(intent);
    }

    // 增加/减少商品数量
    private void optGoodsNum(Boolean add) {
        if (add) {
            mSelectGoodsNum++;
            configGoodsNumView();
        } else {
            if (mSelectGoodsNum == 1) {
                Toast.makeText(this, R.string.goods_num_not_minue, Toast.LENGTH_SHORT).show();
            } else {
                mSelectGoodsNum--;
                configGoodsNumView();
            }
        }
    }

    // 选择支付方式 1: 微信支付; 2:支付宝支付
    private void selectPayWay(String payWay) {
        commitOrderRequestCount = 3;
        commitOrderRequest(payWay);
    }


    // 扫码支付界面
    private void configScanPayView(String payWay, Bitmap bitmap, String mGoodsId) {
        mCurrentPage = 2;
        mHandler.removeCallbacks(autoSkip);
        mHandler.postDelayed(autoSkip, DELAY_TIME);
        setAllGone();
        mCardContainer.setVisibility(View.VISIBLE);
        //        if (mSelectType == 2) {
        if (mGoodsId.equals("16")) {
            mSelectType = 2;
            mScanPayImageView.setImageResource(R.drawable.lottery_ticket_img);
            mScanPayGoodsNumTextView.setText(Html.fromHtml("共 <font color='#FF7739'><big>" + mSelectGoodsNum.toString() + "</big></font> 张"));
        } else {
            mSelectType = 3;
            mScanPayImageView.setImageResource(R.drawable.charging_img);
            mScanPayGoodsNumTextView.setText(Html.fromHtml("共 <font color='#FF7739'><big>" + mSelectGoodsNum.toString() + "</big></font> 小时"));
        }

        double amount = mSelectType == 2 ? mSelectGoodsNum * TICKETPRICE : mSelectGoodsNum * CHARGINGPRICE;
        mScanPayAmountTextView.setText(Html.fromHtml("总金额 <font color='#FF7739'><big>" + amount + "</big></font> 元"));

        if (payWay.equals("weixin")) {
            mScanPayQrTextView.setText(R.string.place_use_wechat_pay);
        } else {
            mScanPayQrTextView.setText(R.string.place_use_alipay_pay);
        }
        mReturnBtn.setVisibility(View.VISIBLE);
        mScanPayQrImageView.setImageBitmap(bitmap);
        mScanPayContainerRight.setVisibility(View.VISIBLE);
        mScanPayContainer.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(autoSkip);
        mHandler.postDelayed(autoSkip, DELAY_TIME);
    }

    // 纸巾支付成功
    private void configPaperPaySuccess() {
        mCurrentPage = 2;
        currentTime = mBannerSecond.getCurrentPosition();
        mBannerSecond.pause();
        setAllGone();
        mTradeSuceessTextView.setText(R.string.shipment_success);
        mHintText.setText("请从取纸口取走纸巾");
        mCardContainer.setVisibility(View.VISIBLE);
        mPaperTradeResultContainer.setVisibility(View.VISIBLE);
        mReturnBtn.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(autoSkip);
        mHandler.postDelayed(autoSkip, 3000);
    }

    // 彩票支付成功
    private void configTicketPaySuccess() {
        mCurrentPage = 3;
        setAllGone();
        mTradeSuceessTextView.setText(R.string.wish_you_a_lottery);
        mHintText.setText("请从取票口取票");
        checkRepertoryRequest("16");
        mCardContainer.setVisibility(View.VISIBLE);
        mPaperTradeResultContainer.setVisibility(View.VISIBLE);
        mReturnBtn.setVisibility(View.VISIBLE);
        mHandler.removeCallbacks(autoSkip);
        mHandler.postDelayed(autoSkip, 3000);
    }


    // 开始充电
    private void startCharging() {
        mCurrentPage = 3;
        mHandler.removeCallbacks(autoSkip);
        mHandler.postDelayed(autoSkip, DELAY_TIME);
        setAllGone();
        mCardContainer.setVisibility(View.VISIBLE);
        mReturnBtn.setVisibility(View.VISIBLE);
        mScanPayContainer.setVisibility(View.VISIBLE);
        mTimeTextView.setVisibility(View.VISIBLE);
        mScanPayContainerCharging.setVisibility(View.VISIBLE);

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(mSelectGoodsNum * 60 * 60 * 1000, 1000) {
            @Override
            public void onTick(long l) {

                String timeStr = TimeUtil.getTime((int) (l / 1000));
                mTimeTextView.setText(timeStr);
            }

            @Override
            public void onFinish() {
                mTimeTextView.setText("充电完成");
                mHandler.removeCallbacks(autoSkip);
                mHandler.postDelayed(autoSkip, 3000);
            }
        };

        mCountDownTimer.start();


    }

    // 配置选择商品数量界面显示
    private void configGoodsNumView() {
        mGoodsNumTextView.setText(mSelectGoodsNum.toString());
        //        double amount = mSelectType == 2 ? mSelectGoodsNum * TICKETPRICE : mSelectGoodsNum * CHARGINGPRICE;
        double amount = mSelectType == 2 ? mSelectGoodsNum * TICKETPRICE : CHARGINGPRICE;
        setGoodsAmount(amount);
    }

    // 商品总价
    private void setGoodsAmount(double amount) {
        mGoodsAmountTextView.setText(Html.fromHtml("总金额 <font color='#FF7739'><big>" + amount + "</big></font> 元"));
    }


    // 返回按钮点击事件
    private void returnClick() {
        mReturnBtn.setClickable(false);
        if (mCurrentPage == 1) {
            setAllGone();
            mBannerSecond.setVisibility(View.VISIBLE);
            mBannerSecond.start();
            mBannerSecond.seekTo(currentTime);
            mIndexContainer.setVisibility(View.VISIBLE);
            //mSelectGoodsNum = 0;
            mCurrentPage = 0;
            mHandler.removeCallbacks(autoSkip);
            //        } else if (mCurrentPage == 2) {
        } else {
            if (mSelectType == 1) {
                //                mPaperTradeResultContainer.setVisibility(View.GONE);
                //                mIndexContainer.setVisibility(View.VISIBLE);
                //                mReturnBtn.setVisibility(View.GONE);
                //                mSelectGoodsNum = 0;
                setAllGone();
                mReturnBtn.setVisibility(View.VISIBLE);
                mCardContainer.setVisibility(View.VISIBLE);
                mPaperScanContainer.setVisibility(View.VISIBLE);
                mCurrentPage = 1;
            } else if (mSelectType == 2) {
                setAllGone();
                mSelectNumImageView.setImageResource(R.drawable.lottery_ticket_img);
                configGoodsNumView();
                mGoodsId = "16";
                mRepertory.setVisibility(View.VISIBLE);
                mGoodsNumView.setVisibility(View.VISIBLE);
                mCardContainer.setVisibility(View.VISIBLE);
                mChoosePayWayContainer.setVisibility(View.VISIBLE);
                mSelectGoodsNumContainer.setVisibility(View.VISIBLE);
                mReturnBtn.setVisibility(View.VISIBLE);
                mCurrentPage = 1;
            } else {
                setAllGone();
                mSelectNumImageView.setImageResource(R.drawable.charging_img);
                configGoodsNumView();
                mGoodsId = "17";
                mChargingInfoText.setVisibility(View.VISIBLE);
                mCardContainer.setVisibility(View.VISIBLE);
                mChoosePayWayContainer.setVisibility(View.VISIBLE);
                mSelectGoodsNumContainer.setVisibility(View.VISIBLE);
                mReturnBtn.setVisibility(View.VISIBLE);
                mCurrentPage = 1;
            }
        }
        //        else if (mCurrentPage == 3) {
        //            if (mSelectType == 2) {
        //                mPaperTradeResultContainer.setVisibility(View.GONE);
        //                mScanPayContainer.setVisibility(View.GONE);
        //            } else if (mSelectType == 3) {
        //                mScanPayContainerCharging.setVisibility(View.GONE);
        //                mTimeTextView.setVisibility(View.GONE);
        //                mScanPayContainer.setVisibility(View.GONE);
        //            }
        //            mCardContainer.setVisibility(View.GONE);
        //            mBannerSecond.setVisibility(View.VISIBLE);
        //            mBannerSecond.start();
        //            mBannerSecond.seekTo(currentTime);
        //            mIndexContainer.setVisibility(View.VISIBLE);
        //            mReturnBtn.setVisibility(View.GONE);
        //            //            mSelectGoodsNum = 0;
        //            mCurrentPage = 0;
        //            mHandler.removeCallbacks(autoSkip);
        //        }
        mReturnBtn.setClickable(true);
    }

    /******* 网络请求 ******/

    // 请求轮播图
    private void requestAtmSlideshow() {
        Map<String, String> params = new HashMap<>();
        params.put("dev_no", deviceNo);
        NetUtils.sendGet(UrlConfig.ATM_SLIDESHOW_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, result);
                //                mHandler.removeCallbacks(refresh);
                //                mHandler.postDelayed(refresh, REFRESH_TIME);
                BannerListModel bannerListModel = mGson.fromJson(result, BannerListModel.class);
                modelImages = bannerListModel.getImages();
                modelVideos = bannerListModel.getVedio();
                if (isMain) {
                    startBanner(modelImages);

                    startBannerSecond(modelVideos);
                }
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                Log.i(TAG, "获取轮播图错误：errCode:" + errCode + "，errorMsg:" + errorMsg);
                if (errCode != -1 && errCode != -2) {
                    showToast(errorMsg);
                } else if (errorMsg.contains("reponse's code is : 466")) {
                    showToast("该设备未注册或补货");
                } else {
                    if (requestAtmSlideshowCount > 0) {
                        requestAtmSlideshowCount--;
                        showToast("获取广告信息超时，正在重新获取");
                        requestAtmSlideshow();
                    } else {
                        mHandler.postDelayed(reTry, 120000);
                    }
                }
            }
        });
    }

    private void startBanner(List<BannerModel> modelImages) {
        if (!modelImages.isEmpty() && !hasImageTask) {
            if (isSlideshowOn) {
                mBanner.dataChange(modelImages);
            } else {
                mBanner.setDataList(modelImages);
                mBanner.startBanner();
                mBanner.startAutoPlay();
            }
            isSlideshowOn = true;
        }
    }

    private void startBannerSecond(final List<BannerModel> modelVideos) {
        if (!modelVideos.isEmpty() && !hasVideoTask) {
            String url = modelVideos.get(index).getImg();
            url = mProxyCacheServer.getProxyUrl(url);
            mBannerSecond.setVideoURI(Uri.parse(url));
            mBannerSecond.start();
            mBannerSecond.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (index < (modelVideos.size() - 1)) {
                        index += 1;
                        String url = modelVideos.get(index).getImg();
                        url = mProxyCacheServer.getProxyUrl(url);
                        mBannerSecond.setVideoURI(Uri.parse(url));
                        mBannerSecond.start();
                    } else if (index == (modelVideos.size() - 1)) {
                        index = 0;
                        if (videoCount > 0)
                            videoCount--;
                        String url = modelVideos.get(index).getImg();
                        url = mProxyCacheServer.getProxyUrl(url);
                        mBannerSecond.setVideoURI(Uri.parse(url));
                        mBannerSecond.start();
                    }
                }
            });
        }
        if (!mBannerSecond.isPlaying()) mBannerSecond.start();
    }

    // 纸巾二维码
    private void paperQrRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("dev_no", deviceNo);
        params.put("goods_id", mGoodsId);

        NetUtils.sendGet(UrlConfig.ATM_SUB_QR_CODE_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "纸巾二维码: " + result);
                if (isMain) {
                    configIndexPaperView(result);
                }
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                Log.i(TAG, "获取纸巾二维码错误：errCode:" + errCode + "，errorMsg:" + errorMsg);
                if (errCode != -1 && errCode != -2) {
                    showToast(errorMsg);
                } else if (errorMsg.contains("reponse's code is : 466")) {
                    showToast("该设备未注册或补货");
                } else {
                    if (paperQrRequestCount > 0) {
                        paperQrRequestCount--;
                        showToast("请求二维码超时，正在重新请求");
                        paperQrRequest();
                    } else {
                        showToast("网络异常，请稍后再试");
                    }
                }
            }
        });
    }

    // 初始化纸巾二维码
    private void initPaperQr() {
        Map<String, String> params = new HashMap<>();
        params.put("dev_no", deviceNo);
        params.put("goods_id", "3");

        NetUtils.sendGet(UrlConfig.ATM_SUB_QR_CODE_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "纸巾二维码: " + result);
                if (isMain) {
                    Glide.with(IndexFragmentActivity.this).load(result).into(mPaperScanImageView);
                }
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                Log.i(TAG, "获取纸巾二维码错误：errCode:" + errCode + "，errorMsg:" + errorMsg);
                if (errCode != -1 && errCode != -2) {
                    showToast(errorMsg);
                } else {
                    if (initPaperQrCount > 0) {
                        initPaperQrCount--;
                        initPaperQr();
                    }
                }
            }
        });
    }

    // 下单
    private void commitOrderRequest(final String channel) {
        Map<String, String> params = new HashMap<>();
        params.put("dev_no", deviceNo);
        params.put("goods_id", mGoodsId);
        params.put("num", String.valueOf(mSelectGoodsNum));
        params.put("payment_channel", channel);

        NetUtils.sendPost(UrlConfig.ATM_COMMIT_ORDER_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
                if (isMain) {
                    Log.i(TAG, "下单: " + result);
                    OrderRespModel respModel = mGson.fromJson(result, OrderRespModel.class);
                    Log.i(TAG, "订单编号: " + respModel.getOrder_sn());
                    payRequest(channel, respModel.getOrder_sn(), mGoodsId);
                }
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                Log.i(TAG, "获取订单错误：errCode:" + errCode + "，errorMsg:" + errorMsg);
                if (errCode != -1 && errCode != -2) {
                    showToast(errorMsg);
                } else if (errorMsg.contains("reponse's code is : 466")) {
                    showToast("库存不足，我们会尽快补货");
                } else {
                    if (commitOrderRequestCount > 0) {
                        commitOrderRequestCount--;
                        showToast("获取订单超时，正在重新获取");
                        commitOrderRequest(channel);
                    } else {
                        showToast("网络异常，请稍后再试");
                    }
                }
            }
        });
    }

    // 查询库存
    private void checkRepertoryRequest(final String goodsId) {
        Map<String, String> params = new HashMap<>();
        params.put("dev_no", deviceNo);
        params.put("goods_id", goodsId);

        NetUtils.sendGet(UrlConfig.ATM_CHECK_REPERTORY_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
                mHandler.removeCallbacks(reTry);
                Log.i(TAG, "查库存和价格: " + result);
                RepertoryModel repertoryModel = mGson.fromJson(result, RepertoryModel.class);
                if (isMain) {
                    if (goodsId.equals("3")) {
                        paperNum = repertoryModel.getNum();
                        PAPERPRICE = Double.parseDouble(repertoryModel.getPrice());
                        Log.i(TAG, "纸巾库存: " + paperNum + ",价格: " + PAPERPRICE);
                        mPaperPrice.setText(Html.fromHtml(
                                "第一包 <font color='#FF7739'><big>免费</big></font>，第二包<font color='#FF7739'><big>" + PAPERPRICE + "</big></font> 元"));
                    } else if (goodsId.equals("16")) {
                        lotteryNum = repertoryModel.getNum();
                        mRepertory.setVisibility(View.VISIBLE);
                        TICKETPRICE = Double.parseDouble(repertoryModel.getPrice());
                        Log.i(TAG, "彩票库存: " + lotteryNum + ",价格: " + TICKETPRICE);
                        mRepertory.setText(Html.fromHtml("剩余 <font color='#FF7739'><big>" + lotteryNum + "</big></font> 张"));
                    } else if (goodsId.equals("17")) {
                        CHARGINGPRICE = Double.parseDouble(repertoryModel.getPrice());
                        Log.i(TAG, "充电: " + repertoryModel.getNum() + ",价格: " + CHARGINGPRICE);
                    }
                }
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                Log.i(TAG, "查询库存错误：errCode:" + errCode + "，errorMsg:" + errorMsg);
                if (errCode != -1 && errCode != -2) {
                    showToast(errorMsg);
                } else if (errorMsg.contains("reponse's code is : 466")) {
                    showToast("该设备未注册或补货");
                } else {
                    if (checkRepertoryRequestCount > 0) {
                        checkRepertoryRequestCount--;
                        showToast("获取商品信息超时，正在重新获取");
                        checkRepertoryRequest(goodsId);
                    } else {
                        showToast("网络连接异常，2分钟后重试");
                        mHandler.postDelayed(reTry, 120000);
                    }
                }
            }
        });
    }

    public void checkPrice() {
        checkRepertoryRequestCount = 5;
        if (PAPERPRICE < 0.01d) {
            checkRepertoryRequest("3");
        }
        if (TICKETPRICE < 0.01d) {
            checkRepertoryRequest("16");
        }
        if (CHARGINGPRICE < 0.01d) {
            checkRepertoryRequest("17");
        }
    }

    // 支付
    private void payRequest(final String channel, final String orderNo, final String mGoodsId) {
        Map<String, String> params = new HashMap<>();
        String type = "1";
        if (channel.equals("alipay")) {
            type = "2";
        }
        params.put("type", type);
        params.put("order_sn", orderNo);

        NetUtils.sendImgPost(UrlConfig.ATM_GET_NATIVE_PAY_URL, params, new NetUtils.WGBitmapCallback() {
            @Override
            public void onSuccess(Bitmap result) {
                if (isMain) {
                    configScanPayView(channel, result, mGoodsId);
                }
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                Log.i(TAG, "获取支付二维码错误：errCode:" + errCode + "，errorMsg:" + errorMsg);
                //                showToast(errorMsg);
                showToast("获取支付二维码失败，请稍后重试");
            }
        });
    }

    // 微信退款
    private void weixinRefund(final String orderNo) {
        Map<String, String> params = new HashMap<>();
        params.put("order_sn", orderNo);

        NetUtils.sendGet(UrlConfig.ATM_WEIXIN_REFUND_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "自动退款: " + result);
                RefundDataUtil.removeOrder(getApplicationContext(), orderNo);
                showToast(result);
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                Log.i(TAG, "退款失败：errCode:" + errCode + "，errorMsg:" + errorMsg);
                if (errCode != -1 && errCode != -2) {
                    showToast(errorMsg);
                } else {
                    if (weixinRefundCount > 0) {
                        weixinRefundCount--;
                        showToast("退款失败，正在重试");
                        weixinRefund(orderNo);
                    } else {
                        showToast("退款失败，请联系客服");
                    }
                }
            }
        });
    }

    // 申请权限
    private void getStoragePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }

    private Runnable reTry = new Runnable() {
        @Override
        public void run() {
            if (!isSlideshowOn) {
                requestAtmSlideshowCount = 3;
                requestAtmSlideshow();
            }
            checkPrice();
        }
    };

    /*****  socket 设备注册  *****/
    private void registerDevice(String machineNo) {

        String serialNumber = Build.SERIAL;

        Map<String, String> params = new HashMap<>();
        params.put("from", "client");
        params.put("cmd", "login");
        params.put("dev_no", machineNo);
        params.put("ccid", serialNumber);// "3839383630374238313031373730323133323331"

        String jsonStr = mGson.toJson(params);
        try {
            SocketClientHelper.getInstance().sendMsg(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Runnable registerDevice = new Runnable() {
        @Override
        public void run() {
            // 设备注册
            registerDevice(deviceNo);
        }
    };

    /*****  socket 出货结果上报  *****/
    private void appearOrderStatus(final String orderNo, final String status, OrderGoods orderGoods) {

        Map<String, Object> params = new HashMap<>();
        params.put("from", "client");
        params.put("cmd", "delivery");
        params.put("dev_no", deviceNo);
        params.put("status", status);// "1"代表出货成功，"0"代表出货失败
        params.put("order_sn", orderNo);
        if (orderGoods != null) {
            params.put("order_goods", orderGoods);
        } else {
            params.put("order_goods", "");
        }

        final String jsonStr = mGson.toJson(params);
        Log.i(TAG, "上报出货结果：" + jsonStr);
        final Timer timer = new Timer();
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt("key", 5);
                data.putString("jsonStr", jsonStr);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                timer.schedule(task, 0, 10000);
                while (!backOrderNo.equals(orderNo))
                    ;
                timer.cancel();
            }
        }).start();
        //        try {
        //            SocketClientHelper.getInstance().sendMsg(jsonStr);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            int key = data.getInt("key");
            if (key == 1) {
                boolean success = data.getBoolean("success");
                if (success) {
                    showToast("设备登录注册成功");
                    showMachineView();  //
                } else {
                    showToast("设备登录注册失败");
                }
            } else if (key == 2) {    // 购物成功
                String goodsId = data.getString("goodsId");
                String orderNo = data.getString("orderNo");
                byte status = data.getByte("status");
                Integer goodsNum = data.getInt("goodsNum");
                sendShipmentHandle(goodsId, status, orderNo, goodsNum);

            } else if (key == -1) { // socket超时
                boolean status = data.getBoolean("status");
                if (status) {
                    removeFloatView();
                    if (!SocketClientHelper.SOCKET_CONNECT) {
                        machineIsRegister();
                        SocketClientHelper.SOCKET_CONNECT = true;
                    }
                } else {
                    createFloatView("服务器连接超时,请联系工作人员检查网络设置");
                }
            } else if (key == ArmConstants.DELIVERY_LOTTERY_MSG_KEY) {    // 彩票出货
                String goodsId = data.getString("goodsId");
                String orderNo = data.getString("orderNo");
                Integer goodsNum = data.getInt("goodsNum");
                deliveryLotterys(orderNo, goodsNum);
            } else if (key == 5) {
                String jsonStr = data.getString("jsonStr");
                try {
                    SocketClientHelper.getInstance().sendMsg(jsonStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 出票结果处理
     *
     * @param goodsId
     * @param status
     */
    private void sendShipmentHandle(String goodsId, byte status, String orderNo, Integer goodsNum) {

        if (goodsId.equals(ArmConstants.GOODS_ID_PAPER)) {
            Log.d(TAG, "出纸巾状态: " + status);
            if (status == 0x00) {
                showToast("出纸巾成功");
                appearOrderStatus(orderNo, "1", null);
                if (isMain) {
                    configPaperPaySuccess();
                }
            } else {
                if (status == 0x01) {
                    showToast("纸巾不足,正在为您退款");
                } else {
                    showToast("出纸巾失败,正在为您退款");
                }
                //                RefundDataUtil.deliveryFail(getApplicationContext(), orderNo, goodsNum); // 出票失败,修改订单状态
                //                weixinRefundCount = 3;
                //                weixinRefund(orderNo);
                OrderGoods orderGoods = new OrderGoods("3", 1);
                appearOrderStatus(orderNo, "0", orderGoods);
            }
        } else if (goodsId.equals(ArmConstants.GOODS_ID_CHARGING)) {
            showToast("开始充电");
            Log.i(TAG, "开始充电");
            appearOrderStatus(orderNo, "1", null);
            if (isMain) {
                startCharging();
            }
        }
    }

    /**
     * 处理串口通讯返回数据
     */
    private void handleSerialPortResponse(byte[] receiveData, String orderNo, Integer goodsNum) {
        if (receiveData != null && receiveData.length > 0) {
            byte b = receiveData[2]; // 命令字 据此判断命令
            if (b == (byte) 0x82) {  // 出纸巾

                sendShipmentResultMsg(ArmConstants.GOODS_ID_PAPER, receiveData[3], orderNo, goodsNum);
            } else if (b == (byte) 0x83) { // 充电

                sendShipmentResultMsg(ArmConstants.GOODS_ID_CHARGING, receiveData[3], orderNo, goodsNum);
            }
        }

    }

    /**
     * 出货结果
     *
     * @param goodsId 商品ID
     * @param status  状态 0: 成功; 1: 不足; 2: 出错了
     */
    private void sendShipmentResultMsg(String goodsId, byte status, String orderNo, Integer goodsNum) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("key", 2);
        data.putString("goodsId", goodsId);
        data.putString("orderNo", orderNo);
        data.putInt("goodsNum", goodsNum);
        data.putByte("status", status);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    // socket数据
    private void listenSocketResponse() {
        SocketClientHelper.getInstance().setOnDataReceiveListener(new SocketClientHelper.OnSocketDataReceiveListener() {
            @Override
            public void onDataReceive(Integer code, String receiveData) {
                if (receiveData == null)
                    return;
                Log.i(TAG, receiveData);
                if (code < 0) {
                    socketConnStatus(false);
                    return;
                }
                socketConnStatus(true);

                if (receiveData.contains("}{")) {
                    String[] strs = receiveData.split("\\}\\{");
                    Log.i(TAG, "Socket捕获连体婴儿！");
                    for (int i = 0; i < strs.length; i++) {
                        if (i == 0) {
                            strs[i] = strs[i] + "}";
                        } else if (i == (strs.length - 1)) {
                            strs[i] = "{" + strs[i];
                        } else {
                            strs[i] = "{" + strs[i] + "}";
                        }
                    }
                    for (String str : strs) {
                        analysisSocketData(str);
                    }
                } else {
                    analysisSocketData(receiveData);
                }

                if (receiveData.contains("\"\\n\"")) {
                    String str = receiveData.substring(0, receiveData.length() - 4);
                    System.out.println(str);
                    analysisSocketData(str);
                    return;
                }
                if (receiveData.contains("'")) {
                    String str = receiveData.substring(0, receiveData.length() - 1);
                    System.out.println(str);
                    analysisSocketData(str);
                }
            }
        });
    }

    /**
     * 解析socket传回的数据
     */
    private void analysisSocketData(String receiveData) {
        if (receiveData.contains(ArmConstants.SOCKET_AD_PUSH)) {
            finishTask();
            mHandler.removeCallbacks(checkAdTime);
            imageCount = 0;
            videoCount = 0;
            startTime = 0;
            PropertiesHelper.writeAdTask(receiveData, 0, 0, IndexFragmentActivity.this);
            startAdTask(receiveData);
        } else if (receiveData.contains(ArmConstants.SOCKET_DELIVERY)) {

            ShipmentModel shipmentModel = null;
            try {
                shipmentModel = mGson.fromJson(receiveData, ShipmentModel.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                showToast("订单信息数据异常");
                return;
            }
            String orderNo = shipmentModel.getOrder_sn();
            if (shipmentModel.getNum() == null
                    || (Integer.valueOf(shipmentModel.getNum()) <= 0)
                    || (lastOrderNo != null && orderNo.equals(lastOrderNo))) { // 数量为空 | 数量<=0 | 订单号与上次相同 返回
                return;
            }
            lastOrderNo = orderNo;
            Integer goodsNum = Integer.valueOf(shipmentModel.getNum()); // 出货数量
            //                    if (!shipmentModel.getGoods_id().equals(ArmConstants.GOODS_ID_CHARGING)) { //出纸巾或者彩票时, 保存订单号
            //                        RefundDataUtil.receiveDeliveryOrder(getApplicationContext(), orderNo, goodsNum);
            //                    }
            if (shipmentModel.getGoods_id().equals(ArmConstants.GOODS_ID_PAPER)) {
                // 出纸巾
                byte[] sendData = SerialPortHelper.outPaper(goodsNum);
                byte[] receive = mSerialPortUtil.sendData(sendData);
                handleSerialPortResponse(receive, orderNo, goodsNum);
            } else if (shipmentModel.getGoods_id().equals(ArmConstants.GOODS_ID_TICKET)) {
                // 出彩票
                sendDeliveryLotteryMsg(shipmentModel);

            } else if (shipmentModel.getGoods_id().equals(ArmConstants.GOODS_ID_CHARGING)) {
                // 充电
                byte[] sendData = SerialPortHelper.usbCharging(ArmConstants.USB_CHARGING_PORT, goodsNum * 60);
                byte[] receive = mSerialPortUtil.sendData(sendData);
                handleSerialPortResponse(receive, orderNo, goodsNum);
            }
        } else if (receiveData.contains(ArmConstants.SOCKET_LOGIN)) {

            String[] strArr = receiveData.split("[{]");
            if (strArr.length > 1) {
                receiveData = receiveData.replace(strArr[0], "");
            }
            MachineLoginModel loginModel = mGson.fromJson(receiveData, MachineLoginModel.class);

            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("key", 1);

            if (loginModel.getRes().equals("success")) {
                deviceNo = loginModel.getDev_no();
                data.putBoolean("success", true);
            } else {
                data.putBoolean("success", false);
            }
            msg.setData(data);
            handler.sendMessage(msg);
        } else if (receiveData.contains(ArmConstants.SOCKET_FEED_BACK)) {
            JSONObject json;
            try {
                json = new JSONObject(receiveData);
                backOrderNo = json.getString("order_sn");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查是否有保存的广告任务
     */
    private void checkAdTask() {
        Map<String, Object> task = PropertiesHelper.readAdTask(this);
        String adTask = (String) task.get("adTask");
        Log.i(TAG, adTask);
        if (adTask != null && !adTask.equals("")) {
            AdModel adModel = null;
            try {
                adModel = mGson.fromJson(adTask, AdModel.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                showToast("广告推送数据异常");
                return;
            }
            adTaskType = Integer.parseInt(adModel.getRelease_type());
            if (adTaskType != 2) {
                startTime = (long) task.get("timePush");
                //                imageCount = (int) task.get("imageCount");
                //                videoCount = (int) task.get("videoCount");
                startAdTask(adTask);
            } else {
                PropertiesHelper.deleteAdTask(this);
            }
        }
    }

    /**
     * 解析发布的广告数据，开始广告任务
     *
     * @param receiveData
     */
    private void startAdTask(String receiveData) {
        AdModel adModel = null;
        try {
            adModel = mGson.fromJson(receiveData, AdModel.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            showToast("广告推送数据异常");
            return;
        }
        adTaskType = Integer.parseInt(adModel.getRelease_type());
        taskImages = adModel.getImages();
        taskVideos = adModel.getVedio();
        if (adTaskType == 2) {
            if (taskImages != null && !taskImages.isEmpty()) {
                imageCount = Integer.parseInt(adModel.getNum());
            }
            if (taskVideos != null && !taskVideos.isEmpty()) {
                videoCount = Integer.parseInt(adModel.getNum());
            }
            if (imageCount > 0) {
                if (isMain) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            startBanner(taskImages);
                            hasImageTask = true;
                        }
                    });
//                    startBanner(taskImages);
//                    hasImageTask = true;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (imageCount > 0)
                            ;
                        hasImageTask = false;
                        if (!hasVideoTask) {
                            adTaskType = 0;
                            PropertiesHelper.deleteAdTask(IndexFragmentActivity.this);
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                requestAtmSlideshow();
                            }
                        });
                    }
                }).start();
            }
            if (videoCount > 0) {
                if (isMain) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            startBannerSecond(taskVideos);
                            hasVideoTask = true;
                        }
                    });
                    //                    startBannerSecond(taskVideos);
//                    hasVideoTask = true;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (videoCount > 0)
                            ;
                        hasVideoTask = false;
                        if (!hasImageTask) {
                            adTaskType = 0;
                            PropertiesHelper.deleteAdTask(IndexFragmentActivity.this);
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                requestAtmSlideshow();
                            }
                        });
                    }
                }).start();
            }
        } else if (adTaskType == 1) {
            int days = Integer.parseInt(adModel.getDay());
            String[] times = adModel.getTime().split("--");
            String[] start = times[0].split(":");
            startHour = Integer.parseInt(start[0]);
            startMinute = Integer.parseInt(start[1]);
            //            startCalendar.set(Calendar.HOUR_OF_DAY, startHour);
            //            startCalendar.set(Calendar.MINUTE, startMinute);
            //            startCalendar.set(Calendar.SECOND, 0);
            String[] end = times[1].split(":");
            endHour = Integer.parseInt(end[0]);
            endMinute = Integer.parseInt(end[1]);
            //            endCalendar.set(Calendar.HOUR_OF_DAY, endHour);
            //            endCalendar.set(Calendar.MINUTE, endMinute);
            //            endCalendar.set(Calendar.SECOND, 0);
            //            Calendar current = Calendar.getInstance();
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            finishCalendar.setTimeInMillis(startTime);
            finishCalendar.add(Calendar.DAY_OF_MONTH, days);
            mHandler.post(checkAdTime);
            //            if (current.after(finishCalendar)){
            //                PropertiesHelper.deleteAdTask(this);
            //                return;
            //            } else if (current.after(endCalendar)) {
            //                startCalendar.add(Calendar.DAY_OF_MONTH, 1);
            //                endCalendar.add(Calendar.DAY_OF_MONTH, 1);
            //            } else if (current.before(endCalendar) && current.after(startCalendar)) {
            //                startCalendar.add(Calendar.DAY_OF_MONTH, 1);
            //                startTask(taskImages,taskVideos);
            //            } else {
            //                requestAtmSlideshow();
            //            }
            //            startTimingTask(startCalendar,ArmConstants.START_TASK,0);
            //            startTimingTask(endCalendar,ArmConstants.END_TASK,1);
            //            startTimingTask(finishCalendar,ArmConstants.FINISH_TASK,2);
        }
    }

    Runnable checkAdTime = new Runnable() {
        @Override
        public void run() {
            if (Calendar.getInstance().after(finishCalendar)) {
                finishTask();
                return;
            }
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.set(Calendar.HOUR_OF_DAY, startHour);
            startCalendar.set(Calendar.MINUTE, startMinute);
            startCalendar.set(Calendar.SECOND, 0);
            Calendar current = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.set(Calendar.HOUR_OF_DAY, endHour);
            endCalendar.set(Calendar.MINUTE, endMinute);
            endCalendar.set(Calendar.SECOND, 0);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
            Log.i(TAG, "开始播放时间：" + dateFormat.format(startCalendar.getTime()));
            Log.i(TAG, "结束播放时间：" + dateFormat.format(endCalendar.getTime()));
            Log.i(TAG, "当前时间：" + dateFormat.format(current.getTime()));
            if (current.after(startCalendar) && current.before(endCalendar)) {
                if (!hasImageTask && !hasVideoTask) {
                    startTask(taskImages, taskVideos);
                }
            } else {
                if (hasVideoTask || hasImageTask) {
                    endTask();
                }
            }
            mHandler.postDelayed(this, 60 * 1000);
        }
    };

    private void startTask(List<BannerModel> taskImages, List<BannerModel> taskVideos) {
        Log.i(TAG, "开始播放推送的广告");
        if (taskImages != null && !taskImages.isEmpty() && isMain) {
            startBanner(taskImages);
            hasImageTask = true;
        }
        if (taskVideos != null && !taskVideos.isEmpty() && isMain) {
            startBannerSecond(taskVideos);
            hasVideoTask = true;
        }
    }

    private void endTask() {
        Log.i(TAG, "结束播放推送的广告");
        hasVideoTask = false;
        hasImageTask = false;
        requestAtmSlideshow();
    }

    private void finishTask() {
        Log.i(TAG, "取消播放推送的广告");
        PropertiesHelper.deleteAdTask(this);
        if (hasVideoTask || hasImageTask) {
            hasVideoTask = false;
            hasImageTask = false;
            requestAtmSlideshow();
        }
        if (adTaskType == 2) {
            //            cancelTimingTask(ArmConstants.START_TASK, 0);
            //            cancelTimingTask(ArmConstants.END_TASK, 1);
            //            cancelTimingTask(ArmConstants.FINISH_TASK, 2);
        }
        mHandler.removeCallbacks(checkAdTime);
        adTaskType = 0;
    }

    //    private void startTimingTask(Calendar startCalendar,String action,int requestCode) {
    //        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日hh时mm分ss秒");
    //        String[] strs = {"开启定时任务-定时开启","开启定时任务-定时关闭","开启定时任务-定时完成并销毁"};
    //        if (requestCode >= 0 && requestCode <= 2)
    //        Log.i(TAG,strs[requestCode]);
    //        Log.i(TAG,"执行时间" + dateFormat.format(startCalendar.getTime()));
    //        Intent intent = new Intent(action);
    //        PendingIntent pi = PendingIntent.getBroadcast(this, requestCode, intent, 0);
    //        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
    ////        am.setWindow(AlarmManager.RTC_WAKEUP, startCalendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pi);
    //        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, startCalendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pi);
    //    }
    //
    //    private void cancelTimingTask(String action,int requestCode) {
    //        Intent intent = new Intent(action);
    //        PendingIntent pi = PendingIntent.getBroadcast(this, requestCode, intent, 0);
    //        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
    //        am.cancel(pi);
    //    }
    //
    //    public class TimingReceiver extends BroadcastReceiver {
    //
    //        private static final String TAG = "TimingReceiver";
    //
    //        @Override
    //        public void onReceive(Context context, Intent intent) {
    //            String action = intent.getAction();
    //            if (action != null && action.equals(ArmConstants.START_TASK)) {
    //                Log.i(TAG, "开始执行广告开始的定时任务");
    //                // 开始播放推送广告
    //                startTask(taskImages,taskVideos);
    //            } else if (action != null && action.equals(ArmConstants.END_TASK)) {
    //                Log.i(TAG, "开始执行广告结束的定时任务");
    //                // 结束播放推送广告
    //                endTask();
    //            } else if (action != null && action.equals(ArmConstants.FINISH_TASK)) {
    //                Log.i(TAG, "开始执行任务完成的定时任务");
    //                // 完成任务
    //                finishTask();
    //            }
    //        }
    //    }

    private void socketConnStatus(boolean status) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("key", -1);
        data.putBoolean("status", status);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 展示售货机主界面
     */
    private void showMachineView() {
        mDeviceNoTextView.setText("货机编号：" + showMachineNo);
        setAllGone();
        mIndexContainer.setVisibility(View.VISIBLE);
        mBanner.setVisibility(View.VISIBLE);
        mBannerSecond.setVisibility(View.VISIBLE);
        requestAtmSlideshowCount = 3;
        requestAtmSlideshow();  // 请求轮播图
    }


    /**
     * 售货机是否注册
     */
    private void machineIsRegister() {

        String serialNumber = Build.SERIAL;
        deviceNo = DataUtils.str2HexStr(serialNumber);
        showMachineNo = serialNumber;
        MainService.mMachineNo = deviceNo;
        setMachineNo(deviceNo, serialNumber);
        new Thread(registerDevice).start();
        showMachineView();
    }

    // 设置售货机编号
    private void setMachineNo(String machineNo, String showMachineNo) {
        MyApplication application = (MyApplication) this.getApplication();
        application.setMachineNo(machineNo);
        application.setShowMachineNo(showMachineNo);
    }

    // toast提示
    private void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mBanner.destroy();
        isMain = false;
        isSlideshowOn = false;
        super.onDestroy();
    }

    @Override
    public void onStart() {
        isMain = true;
        requestAtmSlideshowCount = 3;
        requestAtmSlideshow();
        super.onStart();
    }

    Runnable autoSkip = new Runnable() {

        @Override
        public void run() {
            if (mCurrentPage != 0) {
                setAllGone();
                mBannerSecond.setVisibility(View.VISIBLE);
                mBannerSecond.start();
                mBannerSecond.seekTo(currentTime);
                mIndexContainer.setVisibility(View.VISIBLE);
                mCurrentPage = 0;
            }
        }
    };

    private void setAllGone() {
        mIndexContainer.setVisibility(View.GONE);               //主页按钮界面
        mCardContainer.setVisibility(View.GONE);                //购物、二维码外界面
        mPaperScanContainer.setVisibility(View.GONE);           //纸巾二维码
        mPaperTradeResultContainer.setVisibility(View.GONE);    //出货成功界面
        mSelectGoodsNumContainer.setVisibility(View.GONE);      //商品和数量界面
        mScanPayContainer.setVisibility(View.GONE);             //商品总价展示-扫码界面
        mScanPayContainerRight.setVisibility(View.GONE);        //支付二维码
        mScanPayContainerCharging.setVisibility(View.GONE);     //充电支付成功
        mChoosePayWayContainer.setVisibility(View.GONE);        //选择支付方式
        mReturnBtn.setVisibility(View.GONE);                    //返回按钮
        currentTime = mBannerSecond.getCurrentPosition();       //获取当前视频播放位置
        mBannerSecond.pause();                                  //暂停视频播放
        mBannerSecond.setVisibility(View.GONE);                 //视频轮播
        mRepertory.setVisibility(View.GONE);                    //剩余库存显示
        mGoodsNumView.setVisibility(View.GONE);                 //商品数量设置
        mChargingInfoText.setVisibility(View.GONE);             //充电说明
    }

    private void setLottery(int ticketType) {
        byte[] sendData = SerialPortHelper.setTicketType(ticketType);
        byte[] receive = mSerialPortUtil.sendData(sendData);
        if ((receive != null) && (receive[2] == (byte) 0x88) && (receive[3] == 0x00)) {
            Log.i(TAG, "设置彩票类型成功");
        } else {
            if (count > 0) {
                count--;
                Log.i(TAG, "设置彩票失败，进行第" + (3 - count) + "次重试");
                setLottery(ticketType);
            } else {
                Log.i(TAG, "设置彩票失败!");
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeCallbacks(autoSkip);
                mHandler.postDelayed(autoSkip, DELAY_TIME);
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(autoSkip);
                mHandler.postDelayed(autoSkip, DELAY_TIME);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onNetChange(boolean netStatus) {
        super.onNetChange(netStatus);
        if (netStatus) {
            // 有网络
            mHandler.post(reTry);
            machineIsRegister();
        }
    }


    /**
     * 退款
     */
    private Runnable reFund = new Runnable() {
        @Override
        public void run() {
            Map<String, Integer> refundMap = RefundDataUtil.getRefundInfo(getApplicationContext());
            if (refundMap.size() > 0) {
                for (Map.Entry<String, Integer> entry : refundMap.entrySet()) {
                    String orderNo = entry.getKey();
                    weixinRefundCount = 3;
                    weixinRefund(orderNo);
                }
            }
        }
    };


    /**
     * 接收到彩票出票信息
     *
     * @param shipmentModel
     */
    private void sendDeliveryLotteryMsg(ShipmentModel shipmentModel) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("key", ArmConstants.DELIVERY_LOTTERY_MSG_KEY);
        data.putString("goodsId", shipmentModel.getGoods_id());
        data.putString("orderNo", shipmentModel.getOrder_sn());
        data.putInt("goodsNum", Integer.valueOf(shipmentModel.getNum()));
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 出彩票
     *
     * @param orderNo
     * @param goodsNum
     */
    @SuppressWarnings("JavaDoc")
    private void deliveryLotterys(String orderNo, Integer goodsNum) {

        for (int i = 0; i < goodsNum; i++) {
            Integer result = shipment(1);
            if (result != 1) { // 出票失败
                Integer residue = goodsNum - i; // 未出票彩票数量
                offerLotteryFailure(result, orderNo, goodsNum, residue);
                break;
            } else {
                int residue = goodsNum - i - 1;
                if (residue > 0) {
                    //                    RefundDataUtil.updateDeliveryNum(getApplicationContext(), orderNo, residue); // 更新数量
                    showToast("出票成功，剩余" + residue + "张");
                } else {
                    // 出票成功
                    appearOrderStatus(orderNo, "1", null);
                    offerLotterySuccess(orderNo);
                }
            }

        }//for
    }

    /**
     * 出货
     *
     * @param num 出货数量
     * @return -2:出票失败; -1: 彩票不足; 1:出彩票成功;
     */
    private Integer shipment(Integer num) {
        Integer result = -2;
        byte[] sendData = SerialPortHelper.outLotteryTicket(num);
        byte[] receiveData = mSerialPortUtil.sendData(sendData);

        if (receiveData != null && receiveData.length > 0) {

            byte status = receiveData[3];
            if (status == 0x00) {
                //            if (status == 0x02) {
                // 出彩票成功
                result = 1;
            } else if (status == 0x01) {
                // 彩票不足
                result = -1;
            } else {
                // 出票失败
                result = -2;
            }
        }
        return result;
    }


    /**
     * 出票成功
     *
     * @param orderNo
     */
    private void offerLotterySuccess(String orderNo) {
        showToast("出票成功");
        //        RefundDataUtil.removeOrder(getApplicationContext(), orderNo); // 移除订单
        if (isMain) {
            configTicketPaySuccess();
        }
    }


    /**
     * 出彩票失败 退款
     *
     * @param errorCode  -1: 彩票不足； -2: 出票失败
     * @param orderNo    订单编号
     * @param goodsNum   应出票数量
     * @param residueNum 未出票数量
     */
    private void offerLotteryFailure(Integer errorCode, String orderNo, Integer goodsNum, Integer residueNum) {

        if (residueNum < goodsNum) { // 部分出票
            //            RefundDataUtil.removeOrder(getApplicationContext(), orderNo); // 部分失败, 移除订单
            showToast("出票部分失败，失败数量" + residueNum + "张，正在为您退款");
        } else if (errorCode == -1) {
            showToast("彩票不足，正在为您退款");
        } else {
            showToast("出票失败，正在为您退款");
        }

        OrderGoods orderGoods = new OrderGoods("16", residueNum);
        //        OrderGoods orderGoods = new OrderGoods("16", 2);
        appearOrderStatus(orderNo, "0", orderGoods);
        //        RefundDataUtil.deliveryFail(getApplicationContext(), orderNo, residueNum);
        //        weixinRefundCount = 3;
        //        weixinRefund(orderNo);
    }

    //刷新轮播广告
    private Runnable refresh = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "开始检查广告更新");
            requestAtmSlideshowCount = 3;
            requestAtmSlideshow();
            machineIsRegister();
        }
    };
}