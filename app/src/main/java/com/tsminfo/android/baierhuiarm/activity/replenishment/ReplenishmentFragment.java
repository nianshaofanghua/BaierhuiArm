package com.tsminfo.android.baierhuiarm.activity.replenishment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tsminfo.android.baierhuiarm.R;
import com.tsminfo.android.baierhuiarm.activity.IndexFragmentActivity;
import com.tsminfo.android.baierhuiarm.activity.dialog.EditDialog;
import com.tsminfo.android.baierhuiarm.application.MyApplication;
import com.tsminfo.android.baierhuiarm.config.ArmConstants;
import com.tsminfo.android.baierhuiarm.config.UrlConfig;
import com.tsminfo.android.baierhuiarm.helper.SerialPortHelper;
import com.tsminfo.android.baierhuiarm.model.AppVersionModel;
import com.tsminfo.android.baierhuiarm.model.RepertoryModel;
import com.tsminfo.android.baierhuiarm.model.ServiceTelModel;
import com.tsminfo.android.baierhuiarm.utils.AppInfoUtil;
import com.tsminfo.android.baierhuiarm.utils.DownloadUtils;
import com.tsminfo.android.baierhuiarm.utils.NetUtils;
import com.tsminfo.android.baierhuiarm.utils.SerialPortUtil;

import java.util.HashMap;
import java.util.Map;

import me.jessyan.progressmanager.body.ProgressInfo;


public class ReplenishmentFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ReplenishmentFragment";

    private TextView mDeviceNoTextView;
    private TextView mMobileTextView;
    private ImageButton mReturnImageButton;
    private CardView mPaperReplenishCardView;
    private CardView mOpenLockCardView;
    private CardView mTicketReplenishCardView;
    private String mGoodsId;
    private String mDeviceNo;
    private String mShowDeviceNo;
    private TextView mLotteryNum;
    private TextView mPaperNum;
    private Button mClearLotteryBtn;
    private Button mClearPaperButton;

    private String lotteryNum;

    private String paperNum;

    private Gson mGson;

    private EditDialog mEditDialog;
    private PopupWindow mPopupWindow;

    private SerialPortUtil mSerialPortUtil; // 串口通讯
    private byte[] mBuffer; // 串口通讯返回数据
    private static Handler handler = new Handler();
    private String serviceTel;
    private TextView mServiceTel;
    private final int MAX_PROGRESS = 100;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGson = new Gson();

        MyApplication application = (MyApplication) getActivity().getApplication();
        mDeviceNo = application.getMachineNo();
        mShowDeviceNo = application.getShowMachineNo();

        mSerialPortUtil = new SerialPortUtil();
        requestAppVersion();
        listenSerialPortResponse();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_replenishment, container, false);

        configReplenishView(v);

        checkRepertoryRequest("3");
        checkRepertoryRequest("16");

        return v;
    }


    private void configReplenishView(View v) {
        mMobileTextView = v.findViewById(R.id.mobile_text_view);
        mMobileTextView.setText(MyApplication.mobile);
        mDeviceNoTextView = v.findViewById(R.id.device_no_text_view);
        mDeviceNoTextView.setText("货机编号：" + mShowDeviceNo);
        mPaperReplenishCardView = v.findViewById(R.id.paper_replenish_card_view);
        mPaperReplenishCardView.setOnClickListener(this);
        mOpenLockCardView = v.findViewById(R.id.open_lock_card_view);
        mOpenLockCardView.setOnClickListener(this);
        mTicketReplenishCardView = v.findViewById(R.id.ticket_replenish_card_view);
        mTicketReplenishCardView.setOnClickListener(this);
        mReturnImageButton = v.findViewById(R.id.replenish_return_btn);
        mReturnImageButton.setOnClickListener(this);
        mLotteryNum = v.findViewById(R.id.lottery_num_text);
        mPaperNum = v.findViewById(R.id.paper_num_text);
        mServiceTel = v.findViewById(R.id.service_tel_text);
        mClearLotteryBtn = v.findViewById(R.id.clear_lottery_inventory_btn);
        mClearLotteryBtn.setOnClickListener(this);
        mClearPaperButton = v.findViewById(R.id.clear_paper_inventory_btn);
        mClearPaperButton.setOnClickListener(this);
        getServiceTel();
    }


    @Override
    public void onClick(View view) {
        dismissLoading();
        switch (view.getId()) {
            case R.id.open_lock_card_view:
                Log.i(TAG, "开锁");
                openLock();
                break;
            case R.id.paper_replenish_card_view:
                Log.i(TAG, "纸巾点击事件");
                mGoodsId = "3";
                showDialog("纸巾补货");
                break;
            case R.id.ticket_replenish_card_view:
                Log.i(TAG, "彩票点击事件");
                mGoodsId = "16";
                showDialog("彩票补货");
                break;
            case R.id.clear_lottery_inventory_btn:
                Log.d(TAG, "清空彩票库存");
                clearInventory("16");
                break;
            case R.id.clear_paper_inventory_btn:
                Log.d(TAG, "清空纸巾库存");
                clearInventory("3");
                break;
            case R.id.replenish_return_btn:
                returnClick();
                break;
            default:
                break;
        }
    }


    // 开门
    private void openLock() {

        byte[] sendData = SerialPortHelper.lockOpen();
        byte[] receive = mSerialPortUtil.sendData(sendData);
        if (receive != null) {
            if (receive[3] == 0x00) {
                showToast("开锁成功");
            } else {
                showToast("开锁失败");
            }

        } else {
            showToast("开锁请求发送失败");
        }
    }


    /**
     * 补货请求
     *
     * @param goodsNum 补货数量
     */
    private void replenishRequest(String goodsNum) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("dev_no", mDeviceNo);
        params.put("goods_id", mGoodsId);
        params.put("num", goodsNum);

        NetUtils.sendPost(UrlConfig.ATM_FEED_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
                mEditDialog.dismiss();
                dismissLoading();
                Toast.makeText(getContext(), "补货成功", Toast.LENGTH_LONG).show();
                checkRepertoryRequest(mGoodsId);
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                //                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                dismissLoading();
                if (errorMsg.contains("reponse's code is : 466")) {
                    showToast("补货失败");
                } else {
                    Log.i(TAG, errorMsg);
                    showToast(errorMsg);
                }
            }
        });
    }


    /**
     * 清空库存点击
     *
     * @param goodsId
     */
    @SuppressWarnings("JavaDoc")
    private void clearInventory(String goodsId) {

        if (goodsId.equals(ArmConstants.GOODS_ID_PAPER)) {
            if (!TextUtils.isEmpty(paperNum) && !paperNum.equals("0")) {
                clearInventoryRequest(goodsId);
            } else {
                checkRepertoryRequest(goodsId);
            }
        } else if (goodsId.equals(ArmConstants.GOODS_ID_TICKET)) {
            if (!TextUtils.isEmpty(lotteryNum) && !lotteryNum.equals("0")) {
                clearInventoryRequest(goodsId);
            } else {
                checkRepertoryRequest(goodsId);
            }
        }
    }


    /**
     * 清空库存
     *
     * @param goodsId 商品ID
     */
    private void clearInventoryRequest(final String goodsId) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("dev_no", mDeviceNo);
        params.put("goods_id", goodsId);

        NetUtils.sendGet(UrlConfig.ATM_ELIMINATE_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
                dismissLoading();
                Toast.makeText(getContext(), "清空库存成功", Toast.LENGTH_LONG).show();
                checkRepertoryRequest(goodsId);
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                Log.i(TAG, errorMsg);
                dismissLoading();
                showToast(errorMsg);
            }
        });
    }

    // 获取设备版本
    private void requestAppVersion() {
        Map<String, String> params = new HashMap<>();

        NetUtils.sendGet(UrlConfig.GET_APP_VERSION_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "设备版本: " + result);
                AppVersionModel machineInfoModel = mGson.fromJson(result, AppVersionModel.class);
                String currentV = AppInfoUtil.getLocalVersionName(MyApplication.getContext());
                Log.i(TAG, currentV);
                if (machineInfoModel.getApk_name() == null) {
                    showToast("当前版本是最新的");
                } else if (machineInfoModel.getApk_name().compareTo(currentV) > 0) {
                    showToast("app有新版本，即将更新");
                    Log.i(TAG, "app有新版本");
                    showProgressDialog();
                    downloadApk(machineInfoModel.getApk_address());
                }
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                showToast("获取更新版本失败");
            }
        });
    }

    // 下载apk
    private void downloadApk(String apkUrl) {
        DownloadUtils.getInstance().download(apkUrl, ArmConstants.APK_DOWNLOAD_FILE_PATH, ArmConstants.APK_DOWNLOAD_FILE_NAME,
                new DownloadUtils.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess() {
                        Log.i(TAG, "安装包下载成功");
                        showToast("安装包下载成功, 即将开始安装");
                        progressDialog.cancel();
                        String apkPath = ArmConstants.APK_DOWNLOAD_FILE_PATH + ArmConstants.APK_DOWNLOAD_FILE_NAME;
                        installApk(apkPath);
                    }

                    @Override
                    public void onDownloading(ProgressInfo progress) {
                        Log.i(TAG, "下载进度: " + progress.getPercent());
                        showProgressDialog();
                        progressDialog.setProgress(progress.getPercent());
                    }

                    @Override
                    public void onDownloadFailed() {
                        Log.i(TAG, "apk下载失败");
                        showToast("安装包下载失败");
                        progressDialog.cancel();
                    }
                });
    }

    // 安装apk
    private void installApk(String apkPath) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //是否有安装位置来源的权限
            boolean haveInstallPermission = MyApplication.getContext().getPackageManager().canRequestPackageInstalls();
            if (haveInstallPermission) {
                AppInfoUtil.installApk(MyApplication.getContext(), apkPath);
            } else {
                showToast("安装应用需要打开安装未知来源应用权限，请去设置中开启权限");
            }
        } else {
            AppInfoUtil.installApk(MyApplication.getContext(), apkPath);
        }
    }

    // 串口通讯监听
    private void listenSerialPortResponse() {

        //        mSerialPortUtil.setOnDataReceiveListener(new SerialPortUtil.OnDataReceiveListener() {
        //            @Override
        //            public void onDataReceive(byte[] buffer, int size) {
        //                mBuffer = buffer;
        //                handler.post(runnable);
        //            }
        //
        //            //开线程更新UI
        //            Runnable runnable = new Runnable() {
        //                @Override
        //                public void run() {
        //
        //                    handleSerialPortResponse();
        //                }
        //            };
        //        });
    }

    /**
     * 处理串口通讯返回数据
     */
    private void handleSerialPortResponse() {
        byte b = mBuffer[2]; // 命令字 据此判断命令
        if (b == (byte) 0x84) {
            if (mBuffer[3] == 0x00) {
                Toast.makeText(getContext(), "开锁成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "开锁失败", Toast.LENGTH_LONG).show();
            }
        }
    }


    // 弹出输入框
    private void showDialog(String title) {
        mEditDialog = new EditDialog(getContext(), title, "请输入补货数量", InputType.TYPE_CLASS_NUMBER, new EditDialog.onYesClickListener() {

            @Override
            public void onYesClick(String num) {
                Log.d(TAG, num);
                if (!TextUtils.isEmpty(num)) {
                    replenishRequest(num);
                } else {
                    showToast("请输入补货数量");
                }

            }
        }, new EditDialog.onNoClickListener() {
            @Override
            public void onNoClick() {
                mEditDialog.dismiss();
            }
        });
        mEditDialog.show();
    }


    // 返回上一页
    private void returnClick() {
        //        Intent intent= new Intent(getContext(), IndexActivity.class);
        Intent intent = new Intent(getContext(), IndexFragmentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    // toast提示
    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    // 查询库存
    private void checkRepertoryRequest(final String goodsId) {
        Map<String, String> params = new HashMap<>();
        params.put("dev_no", mDeviceNo);
        params.put("goods_id", goodsId);

        //        NetUtils.sendPost(UrlConfig.ATM_CHECK_REPERTORY_URL, params, new NetUtils.WGNetCallBack() {
        NetUtils.sendGet(UrlConfig.ATM_CHECK_REPERTORY_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "查库存: " + result);
                RepertoryModel repertoryModel = mGson.fromJson(result, RepertoryModel.class);
                Log.i(TAG, "库存数量: " + repertoryModel.getNum());
                if (goodsId.equals("3")) {
                    paperNum = repertoryModel.getNum();
                    mPaperNum.setText(Html.fromHtml("纸巾 <font color='#FF7739'><big>" + paperNum + "</big></font> 包"));
                } else if (goodsId.equals("16")) {
                    lotteryNum = repertoryModel.getNum();
                    mLotteryNum.setText(Html.fromHtml("彩票 <font color='#FF7739'><big>" + lotteryNum + "</big></font> 张"));
                }
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                showToast(errorMsg);
            }
        });
    }

    // 查询客服电话
    private void getServiceTel() {
        Map<String, String> params = new HashMap<>();
        //                params.put("dev_no", deviceNo);
        //                params.put("goods_id", mGoodsId);

        NetUtils.sendGet(UrlConfig.ATM_GET_SERVICE_TEL, params, new NetUtils.WGNetCallBack() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "查客服电话: " + result);
                ServiceTelModel serviceTelModel = mGson.fromJson(result, ServiceTelModel.class);
                Log.i(TAG, "客服电话: " + serviceTelModel.getServicePhone());
                serviceTel = serviceTelModel.getServicePhone();
                mServiceTel.setText("客服电话: " + serviceTel);
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                showToast(errorMsg);
            }
        });
    }

    /**
     * 加载
     */
    private void showLoading() {
        dismissLoading();
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow();
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setFocusable(false);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
            View view = LayoutInflater.from(getContext()).inflate(R.layout.loading_popup, null);
            mPopupWindow.setContentView(view);
        }
        mPopupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 40);

    }

    /**
     * 加载dismiss
     */
    private void dismissLoading() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    private void showProgressDialog() {
        try {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getContext());
            }else if (!progressDialog.isShowing()) {
                progressDialog.setProgress(0);
                progressDialog.setTitle("正在下载更新包");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(MAX_PROGRESS);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
