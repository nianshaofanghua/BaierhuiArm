package com.tsminfo.android.baierhuiarm.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.tsminfo.android.baierhuiarm.R;
import com.tsminfo.android.baierhuiarm.activity.banner.Banner;
import com.tsminfo.android.baierhuiarm.activity.dialog.EditDialog;
import com.tsminfo.android.baierhuiarm.activity.login.LoginActivity;
import com.tsminfo.android.baierhuiarm.application.MyApplication;
import com.tsminfo.android.baierhuiarm.config.ArmConstants;
import com.tsminfo.android.baierhuiarm.config.UrlConfig;
import com.tsminfo.android.baierhuiarm.helper.SerialPortHelper;
import com.tsminfo.android.baierhuiarm.helper.SocketClientHelper;
import com.tsminfo.android.baierhuiarm.model.BannerListModel;
import com.tsminfo.android.baierhuiarm.model.MachineLoginModel;
import com.tsminfo.android.baierhuiarm.model.OrderRespModel;
import com.tsminfo.android.baierhuiarm.model.RepertoryModel;
import com.tsminfo.android.baierhuiarm.model.ServiceTelModel;
import com.tsminfo.android.baierhuiarm.model.ShipmentModel;
import com.tsminfo.android.baierhuiarm.model.BannerModel;
import com.tsminfo.android.baierhuiarm.service.MainService;
import com.tsminfo.android.baierhuiarm.utils.DataUtils;
import com.tsminfo.android.baierhuiarm.utils.NetUtils;
import com.tsminfo.android.baierhuiarm.utils.SerialPortUtil;
import com.tsminfo.android.baierhuiarm.utils.TimeUtil;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IndexFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "IndexFragment";

    private static final double TICKETPRICE = 1.5; // 彩票单价
    private static final double CHARGINGPRICE = 1.5; // 充电单价

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

    private ImageView mPaperScanImageView;

    private CountDownTimer mCountDownTimer; // 倒计时

    private Button mReturnBtn; // 返回按mScanPayQrTextView钮

    private Integer mSelectType; // 1: 纸巾 2: 彩票; 3: 充电
    private Integer mCurrentPage; // 1: 根页面 2: 扫码付款; 3: 等待出货 4: 付款成功
    private String mGoodsId;

    private Integer mSelectGoodsNum; // 商品数量

    private String deviceNo;
    private String showMachineNo; // 展示的售货机编号

    private Gson mGson; // 解析数据

    private SerialPortUtil mSerialPortUtil; // 串口通讯

    private Banner mBanner; // 轮播图
    //    private Banner mBannerSecond;
    private VideoView mBannerSecond;

    private EditDialog mMachineNoDialog; // 输入框

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGson = new Gson();
        mHandler=new Handler();
        mSerialPortUtil = new SerialPortUtil();
        listenSocketResponse();


        Intent intent = new Intent(getContext(), MainService.class);
        getContext().startService(intent);

        mProxyCacheServer = MyApplication.getProxy(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_index, container, false);

        configIndexView(v);

        count = 3;
        setLottery(ticketType);

        machineIsRegister();    // 判断售货机是否注册

        return v;
    }


    private void configIndexView(View v) {

        mDeviceNoTextView = v.findViewById(R.id.device_no_text_view);

        mPaperTowelBtn = v.findViewById(R.id.index_paper_towel_button);
        mPaperTowelBtn.setOnClickListener(this);
        mLotteryTicketBtn = v.findViewById(R.id.index_lottery_ticket_button);
        mLotteryTicketBtn.setOnClickListener(this);
        mChargingBtn = v.findViewById(R.id.index_charging_button);
        mChargingBtn.setOnClickListener(this);
        mReplenishmentBtn = v.findViewById(R.id.index_replenishment_button);
        mReplenishmentBtn.setOnClickListener(this);

        mIndexContainer = v.findViewById(R.id.index_container);
        mCardContainer = v.findViewById(R.id.card_container);
        mPaperScanContainer = v.findViewById(R.id.paper_scan_container);
        mPaperTradeResultContainer = v.findViewById(R.id.paper_trade_result_container);
        mSelectGoodsNumContainer = v.findViewById(R.id.select_goods_num_container);
        mScanPayContainer = v.findViewById(R.id.scan_pay_container);
        mScanPayContainerRight = v.findViewById(R.id.scan_pay_container_right);
        mScanPayContainerCharging = v.findViewById(R.id.scan_pay_container_charging);
        mChoosePayWayContainer = v.findViewById(R.id.choose_pay_way_container);

        mSelectNumImageView = v.findViewById(R.id.select_num_img);
        mGoodsNumAddBtn = v.findViewById(R.id.goods_num_add_btn);
        mGoodsNumAddBtn.setOnClickListener(this);
        mGoodsNumMinusBtn = v.findViewById(R.id.goods_num_minus_btn);
        mGoodsNumMinusBtn.setOnClickListener(this);
        mGoodsNumTextView = v.findViewById(R.id.goods_num_text_view);
        mGoodsAmountTextView = v.findViewById(R.id.goods_amount_text_view);
        mAliPayBtn = v.findViewById(R.id.alipay_image_btn);
        mAliPayBtn.setOnClickListener(this);
        mWeChatBtn = v.findViewById(R.id.wechat_image_btn);
        mWeChatBtn.setOnClickListener(this);

        mScanPayImageView = v.findViewById(R.id.scan_pay_image);
        mScanPayGoodsNumTextView = v.findViewById(R.id.scan_pay_total_text_view);
        mScanPayAmountTextView = v.findViewById(R.id.scan_pay_amount_text_view);
        mScanPayQrTextView = v.findViewById(R.id.scan_pay_qr_text_view);
        mScanPayQrImageView = v.findViewById(R.id.scan_pay_qr_image_view);

        mPaperScanImageView = v.findViewById(R.id.paper_scan_image_view);

        mTradeSuceessTextView = v.findViewById(R.id.trade_success_text_view);
        mTimeTextView = v.findViewById(R.id.time_text_view);

        mReturnBtn = v.findViewById(R.id.return_btn);
        mReturnBtn.setOnClickListener(this);

        mBanner = v.findViewById(R.id.index_banner);
        mBannerSecond = v.findViewById(R.id.index_banner2);

        mRepertory = v.findViewById(R.id.goods_repertory_text_view);
        mHintText = v.findViewById(R.id.hint_success_text);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.index_paper_towel_button:
                Log.i(TAG, "纸巾......");
                mGoodsId = "3";
                paperQrRequest();
                break;

            case R.id.index_lottery_ticket_button:
                Log.i(TAG, "彩票......");
                indexSelectGoodsNumClick(2);
                break;
            case R.id.index_charging_button:
                Log.i(TAG, "充电......");
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
        currentTime = mBannerSecond.getCurrentPosition();
        mBannerSecond.pause();
        mBannerSecond.setVisibility(View.GONE);
        mIndexContainer.setVisibility(View.GONE);
        mReturnBtn.setVisibility(View.VISIBLE);
        mCardContainer.setVisibility(View.VISIBLE);
        mPaperScanContainer.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(qrUrl)
                .into(mPaperScanImageView);

    }

    // 进入彩票/充电页面
    private void indexSelectGoodsNumClick(Integer selectType) {
        mSelectGoodsNum = 1;
        mSelectType = selectType;
        mCurrentPage = 1;
        if (selectType == 2) {
            mSelectNumImageView.setImageResource(R.drawable.lottery_ticket_img);
            configGoodsNumView();
            mGoodsId = "16";
            checkRepertoryRequest();
//            mRepertory.setVisibility(View.VISIBLE);
//            mRepertory.setText(Html.fromHtml("剩余 <font color='#FF7739'><big>" + lotteryNum + "</big></font> 张"));
        } else {
            mSelectNumImageView.setImageResource(R.drawable.charging_img);
            configGoodsNumView();
            mGoodsId = "17";
            mRepertory.setVisibility(View.GONE);
        }
        currentTime = mBannerSecond.getCurrentPosition();
        mBannerSecond.pause();
        mBannerSecond.setVisibility(View.GONE);
        mIndexContainer.setVisibility(View.GONE);
        mCardContainer.setVisibility(View.VISIBLE);
        mChoosePayWayContainer.setVisibility(View.VISIBLE);
        mSelectGoodsNumContainer.setVisibility(View.VISIBLE);
        mReturnBtn.setVisibility(View.VISIBLE);
    }

    // 补货
    private void replenishClick() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    // 增加/减少商品数量
    private void optGoodsNum(Boolean add) {
        if (add) {
            mSelectGoodsNum++;
            configGoodsNumView();
        } else {
            if (mSelectGoodsNum == 1) {
                Toast.makeText(this.getActivity(), R.string.goods_num_not_minue, Toast.LENGTH_SHORT).show();
            } else {
                mSelectGoodsNum--;
                configGoodsNumView();
            }
        }
    }

    // 选择支付方式 1: 微信支付; 2:支付宝支付
    private void selectPayWay(String payWay) {

        commitOrderRequest(payWay);
    }


    // 扫码支付界面
    private void configScanPayView(String payWay, Bitmap bitmap) {
        mCurrentPage = 2;
        mSelectGoodsNumContainer.setVisibility(View.GONE);
        mChoosePayWayContainer.setVisibility(View.GONE);
        if (mSelectType == 2) {
            mScanPayImageView.setImageResource(R.drawable.lottery_ticket_img);
            mScanPayGoodsNumTextView.setText(Html.fromHtml("共 <font color='#FF7739'><big>" + mSelectGoodsNum.toString() + "</big></font> 张"));
        } else {
            mScanPayImageView.setImageResource(R.drawable.charging_img);
            mScanPayGoodsNumTextView.setText(Html.fromHtml("共 <font color='#FF7739'><big>" + mSelectGoodsNum.toString() + "</big></font> 小时"));
        }


        double amount = mSelectType == 2 ? mSelectGoodsNum * TICKETPRICE : mSelectGoodsNum * CHARGINGPRICE;
        mScanPayAmountTextView.setText(Html.fromHtml("总金额 <font color='#FF7739'><big>" + String.format("%.1f", amount) + "</big></font> 元"));

        if (payWay.equals("weixin")) {
            mScanPayQrTextView.setText(R.string.place_use_wechat_pay);
        } else {
            mScanPayQrTextView.setText(R.string.place_use_alipay_pay);
        }

//        mScanPayQrImageView.setImageResource(R.drawable.qr_placeholder);
        mBannerSecond.setVisibility(View.GONE);
        mIndexContainer.setVisibility(View.GONE);
        mReturnBtn.setVisibility(View.VISIBLE);
        mScanPayQrImageView.setImageBitmap(bitmap);
        mScanPayContainerRight.setVisibility(View.VISIBLE);
        mScanPayContainer.setVisibility(View.VISIBLE);
    }


    // 纸巾支付成功
    private void configPaperPaySuccess() {
        mCurrentPage = 2;
        currentTime = mBannerSecond.getCurrentPosition();
        mBannerSecond.pause();
        mBannerSecond.setVisibility(View.GONE);
        mIndexContainer.setVisibility(View.GONE);
        mPaperScanContainer.setVisibility(View.GONE);
        mTradeSuceessTextView.setText(R.string.shipment_success);
        mHintText.setText("请从取纸口取走纸巾");
        mPaperTradeResultContainer.setVisibility(View.VISIBLE);
        mReturnBtn.setVisibility(View.VISIBLE);
        mHandler.postDelayed(autoSkip,3000);
    }

    // 彩票支付成功
    private void configTicketPaySuccess() {
        mCurrentPage = 3;
        mScanPayContainer.setVisibility(View.GONE);
        mScanPayContainerRight.setVisibility(View.GONE);
        mTradeSuceessTextView.setText(R.string.wish_you_a_lottery);
        mHintText.setText("请从取票口取票");
        mPaperTradeResultContainer.setVisibility(View.VISIBLE);
        mHandler.postDelayed(autoSkip,3000);
    }


    // 开始充电
    private void startCharging() {
        mCurrentPage = 3;
        mScanPayContainerRight.setVisibility(View.GONE);
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
                returnClick();
            }
        };

        mCountDownTimer.start();

    }


    // 配置选择商品数量界面显示
    private void configGoodsNumView() {
        mGoodsNumTextView.setText(mSelectGoodsNum.toString());
        double amount = mSelectType == 2 ? mSelectGoodsNum * TICKETPRICE : mSelectGoodsNum * CHARGINGPRICE;
        setGoodsAmount(amount);
    }

    // 商品总价
    private void setGoodsAmount(double amount) {
        mGoodsAmountTextView.setText(Html.fromHtml("总金额 <font color='#FF7739'><big>" + String.format("%.1f", amount) + "</big></font> 元"));
    }


    // 返回按钮点击事件
    private void returnClick() {
        if (mCurrentPage == 1) {
            mHandler.removeCallbacks(autoSkip);
            if (mSelectType == 1) {
                mPaperScanContainer.setVisibility(View.GONE);
            } else if (mSelectType == 2 || mSelectType == 3) {
                mSelectGoodsNumContainer.setVisibility(View.GONE);
            }

            mBannerSecond.setVisibility(View.VISIBLE);
            mBannerSecond.start();
            mBannerSecond.seekTo(currentTime);
            mIndexContainer.setVisibility(View.VISIBLE);
            mReturnBtn.setVisibility(View.GONE);
            mChoosePayWayContainer.setVisibility(View.GONE);
            mCardContainer.setVisibility(View.GONE);
            mSelectGoodsNum = 0;

        } else if (mCurrentPage == 2) {
            if (mSelectType == 1) {
                mPaperTradeResultContainer.setVisibility(View.GONE);
                mIndexContainer.setVisibility(View.VISIBLE);
                mReturnBtn.setVisibility(View.GONE);
                mSelectGoodsNum = 0;
            } else if (mSelectType == 2 || mSelectType == 3) {
                mScanPayContainer.setVisibility(View.GONE);
                mScanPayContainerRight.setVisibility(View.GONE);
                mChoosePayWayContainer.setVisibility(View.VISIBLE);
                mSelectGoodsNumContainer.setVisibility(View.VISIBLE);
                mCurrentPage = 1;
            }
        } else if (mCurrentPage == 3) {
            if (mSelectType == 2) {
                mPaperTradeResultContainer.setVisibility(View.GONE);
                mScanPayContainer.setVisibility(View.GONE);
            } else if (mSelectType == 3) {
                mScanPayContainerCharging.setVisibility(View.GONE);
                mTimeTextView.setVisibility(View.GONE);
                mScanPayContainer.setVisibility(View.GONE);
            }
            mCardContainer.setVisibility(View.GONE);
            mBannerSecond.setVisibility(View.VISIBLE);
            mBannerSecond.start();
            mBannerSecond.seekTo(currentTime);
            mIndexContainer.setVisibility(View.VISIBLE);
            mReturnBtn.setVisibility(View.GONE);
            mSelectGoodsNum = 0;
        }
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
                BannerListModel bannerListModel = mGson.fromJson(result, BannerListModel.class);
                List<BannerModel> modelImages = bannerListModel.getImages();
                final List<BannerModel> modelVideos = bannerListModel.getVedio();

                mBanner.setDataList(modelImages);
                mBanner.startBanner();
                mBanner.startAutoPlay();

//                mBannerSecond.setDataList(modelVideos);
//                mBannerSecond.startBanner();
//                mBannerSecond.startAutoPlay();

                if (!modelVideos.isEmpty()) {
                    String url = modelVideos.get(index).getImg();
                    url = mProxyCacheServer.getProxyUrl(url);
                    mBannerSecond.setVideoURI(Uri.parse(url));
                    mBannerSecond.start();
                    mBannerSecond.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (index<(modelVideos.size()-1)){
                                index += 1;
                                String url = modelVideos.get(index).getImg();
                                url = mProxyCacheServer.getProxyUrl(url);
                                mBannerSecond.setVideoURI(Uri.parse(url));
                                mBannerSecond.start();
                            }else if (index==(modelVideos.size()-1)){
                                index = 0;
                                String url = modelVideos.get(index).getImg();
                                url = mProxyCacheServer.getProxyUrl(url);
                                mBannerSecond.setVideoURI(Uri.parse(url));
                                mBannerSecond.start();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                showToast(errorMsg);
            }
        });
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
                configIndexPaperView(result);
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                showToast(errorMsg);
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
                Log.i(TAG, "下单: " + result);
                OrderRespModel respModel = mGson.fromJson(result, OrderRespModel.class);
                Log.i(TAG, "订单编号: " + respModel.getOrder_sn());
                payRequest(channel, respModel.getOrder_sn());
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                showToast(errorMsg);
            }
        });
    }
    // 查询库存
    private void checkRepertoryRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("dev_no", deviceNo);
        params.put("goods_id", mGoodsId);

        NetUtils.sendGet(UrlConfig.ATM_CHECK_REPERTORY_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "查库存: " + result);
                RepertoryModel repertoryModel = mGson.fromJson(result, RepertoryModel.class);
                Log.i(TAG, "库存数量: " + repertoryModel.getNum());
                if (mGoodsId.equals("3")){
                    paperNum = repertoryModel.getNum();
                }else if(mGoodsId.equals("16")){
                    lotteryNum = repertoryModel.getNum();
                    mRepertory.setVisibility(View.VISIBLE);
                    mRepertory.setText(Html.fromHtml("剩余 <font color='#FF7739'><big>" + lotteryNum + "</big></font> 张"));
                }
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                showToast(errorMsg);
            }
        });
    }



    // 支付
    private void payRequest(final String channel, final String orderNo) {
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
                configScanPayView(channel, result);
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                showToast(errorMsg);
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
                if (getActivity().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }


    /*****  socket *****/
    private void registerDevice(String machineNo) {

        String serialNumber = android.os.Build.SERIAL;

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


    public Handler handler = new Handler() {
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
                byte status = data.getByte("status");
                sendShipmentHandle(goodsId, status);

            }
        }
    };


    /**
     * 出票结果处理
     *
     * @param goodsId
     * @param status
     */
    private void sendShipmentHandle(String goodsId, byte status) {

        if (goodsId.equals(ArmConstants.GOODS_ID_PAPER)) {
            if (status == 0x00) {
//            if (status == 0x02) {
                showToast("出纸巾成功");
                Log.i(TAG, "出纸巾成功");
                configPaperPaySuccess();
            } else if (status == 0x01) {
                showToast("纸巾不足");
                Log.i(TAG, "纸巾不足");
            } else if (status == 0x02) {
//            } else if (status == 0x00) {
                showToast("出错了");
                Log.i(TAG, "出纸巾失败");
            }
        } else if (goodsId.equals(ArmConstants.GOODS_ID_TICKET)) {
            if (status == 0x00) {
//            if (status == 0x02) {
                showToast("出票成功");
                Log.i(TAG, "出票成功");
                configTicketPaySuccess();
            } else if (status == 0x01) {
                showToast("彩票不足");
            } else if (status == 0x02) {
//            } else if (status == 0x00) {
                showToast("出错了....");
                Log.i(TAG, "出票失败");
            }
        } else if (goodsId.equals(ArmConstants.GOODS_ID_CHARGING)) {
            showToast("开始充电");
            Log.i(TAG, "开始充电");
            startCharging();
        }
    }


    /**
     * 处理串口通讯返回数据
     */
    private void handleSerialPortResponse(byte[] receiveData) {

        if (receiveData != null && receiveData.length > 0){
            byte b = receiveData[2]; // 命令字 据此判断命令
            if (b == (byte) 0x81) { // 出彩票
                sendShipmentResultMsg(ArmConstants.GOODS_ID_TICKET, receiveData[3]);

            } else if (b == (byte) 0x82) {  // 出纸巾

                sendShipmentResultMsg(ArmConstants.GOODS_ID_PAPER, receiveData[3]);
            } else if (b == (byte) 0x83) { // 充电

                sendShipmentResultMsg(ArmConstants.GOODS_ID_CHARGING, receiveData[3]);
            }
        }

    }


    /**
     * 出货结果
     *
     * @param goodsId 商品ID
     * @param status  状态 0: 成功; 1: 不足; 2: 出错了
     */
    private void sendShipmentResultMsg(String goodsId, byte status) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("key", 2);
        data.putString("goodsId", goodsId);
        data.putByte("status", status);
        msg.setData(data);
        handler.sendMessage(msg);
    }


    // socket数据
    private void listenSocketResponse() {
        SocketClientHelper.getInstance().setOnDataReceiveListener(new SocketClientHelper.OnSocketDataReceiveListener() {
            @Override
            public void onDataReceive(Integer code, String receiveData) {
                Log.i(TAG, receiveData);

                if (receiveData.contains(ArmConstants.SOCKET_DELIVERY)) {

                    ShipmentModel shipmentModel = mGson.fromJson(receiveData, ShipmentModel.class);
                    if (shipmentModel.getGoods_id().equals(ArmConstants.GOODS_ID_PAPER)) {
                        // 出纸巾
                        byte[] sendData = SerialPortHelper.outPaper(Integer.valueOf(shipmentModel.getNum()));
                        byte[] receive = mSerialPortUtil.sendData(sendData);
                        handleSerialPortResponse(receive);
                    } else if (shipmentModel.getGoods_id().equals(ArmConstants.GOODS_ID_TICKET)) {
                        // 出彩票
                        byte[] sendData = SerialPortHelper.outLotteryTicket(Integer.valueOf(shipmentModel.getNum()));
                        byte[] receive = mSerialPortUtil.sendData(sendData);
                        handleSerialPortResponse(receive);
                    } else if (shipmentModel.getGoods_id().equals(ArmConstants.GOODS_ID_CHARGING)) {
                        // 充电
//                        byte[] sendData = SerialPortHelper.usbCharging(ArmConstants.USB_CHARGING_PORT, Integer.valueOf(shipmentModel.getNum()) * 10);
                        byte[] sendData = SerialPortHelper.usbCharging(ArmConstants.USB_CHARGING_PORT, Integer.valueOf(shipmentModel.getNum()) * 60);
                        byte[] receive = mSerialPortUtil.sendData(sendData);
                        handleSerialPortResponse(receive);
                    }
                } else if (receiveData.contains(ArmConstants.SOCKET_LOGIN)) {

                    if (mMachineNoDialog != null) {
                        mMachineNoDialog.dismiss();
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
                    }

                }

            }
        });
    }


    /**
     * 展示售货机主界面
     */
    private void showMachineView() {
        mDeviceNoTextView.setText("货机编号：" + showMachineNo);
        mIndexContainer.setVisibility(View.VISIBLE);
        mBanner.setVisibility(View.VISIBLE);
        mBannerSecond.setVisibility(View.VISIBLE);
        mCardContainer.setVisibility(View.GONE);
        mReturnBtn.setVisibility(View.GONE);
        mChoosePayWayContainer.setVisibility(View.GONE);

        requestAtmSlideshow();  // 请求轮播图
    }


    /**
     * 售货机是否注册
     */
    private void machineIsRegister() {

        String serialNumber = android.os.Build.SERIAL;
        deviceNo = DataUtils.str2HexStr(serialNumber);
        showMachineNo = serialNumber;
        MainService.mMachineNo = deviceNo;
        setMachineNo(deviceNo, serialNumber);
        new Thread(registerDevice).start();
        showMachineView();

    }


    // 设置售货机编号
    private void setMachineNo(String machineNo, String showMachineNo) {
        MyApplication application = (MyApplication) getActivity().getApplication();
        application.setMachineNo(machineNo);
        application.setShowMachineNo(showMachineNo);
    }


    // toast提示
    private void showToast(String msg) {
        Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_LONG);
//        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mBanner.destroy();
        super.onDestroy();
    }
    @Override
    public void onStart() {
        requestAtmSlideshow();
        super.onStart();
    }

    Runnable autoSkip=new Runnable(){

        @Override
        public void run() {
            mCardContainer.setVisibility(View.GONE);
            mPaperScanContainer.setVisibility(View.GONE);
            mPaperTradeResultContainer.setVisibility(View.GONE);
            mSelectGoodsNumContainer.setVisibility(View.GONE);
            mScanPayContainerCharging.setVisibility(View.GONE);
            mScanPayContainer.setVisibility(View.GONE);
            mChoosePayWayContainer.setVisibility(View.GONE);
            mScanPayContainerRight.setVisibility(View.GONE);
            mReturnBtn.setVisibility(View.GONE);
            mBannerSecond.setVisibility(View.VISIBLE);
            mBannerSecond.start();
            mBannerSecond.seekTo(currentTime);
            mIndexContainer.setVisibility(View.VISIBLE);
        }
    };

    private void setLottery(int ticketType){
        byte[] sendData = SerialPortHelper.setTicketType(ticketType);
        byte[] receive = mSerialPortUtil.sendData(sendData);
        if ((receive != null) && (receive[2] == (byte) 0x88) && (receive[3] == 0x00)){
            Log.i(TAG, "设置彩票类型成功");
        }else{
            if (count > 0) {
                count --;
                Log.i(TAG, "设置彩票失败，进行第" + (3 - count) + "次重试");
                setLottery(ticketType);
            }else {
                Log.i(TAG, "设置彩票失败!");
//                showToast("设置彩票长度失败");
            }
        }
    }
}
