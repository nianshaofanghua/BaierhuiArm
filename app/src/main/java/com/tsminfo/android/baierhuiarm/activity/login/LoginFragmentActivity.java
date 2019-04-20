package com.tsminfo.android.baierhuiarm.activity.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tsminfo.android.baierhuiarm.R;
import com.tsminfo.android.baierhuiarm.activity.IndexFragmentActivity;
import com.tsminfo.android.baierhuiarm.activity.replenishment.ReplenishmentActivity;
import com.tsminfo.android.baierhuiarm.application.MyApplication;
import com.tsminfo.android.baierhuiarm.base.BaseActivity;
import com.tsminfo.android.baierhuiarm.config.UrlConfig;
import com.tsminfo.android.baierhuiarm.model.ServiceTelModel;
import com.tsminfo.android.baierhuiarm.utils.NetUtils;
import com.tsminfo.android.baierhuiarm.utils.QRCodeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginFragmentActivity extends BaseActivity{

    private static final String TAG = "LoginFragment";
    private final static long DELAY_TIME = 20000;   //自动跳转时间

    private TextView mDeviceNoTextView;
    private ImageButton mReturnImageButton;
    private EditText mMobileEditText;
    private EditText mPwdEditText;
    private Button mLoginBtn;
    private ImageView mActivateQrImageView;

    private String mDeviceNo;
    private String mShowDeviceNo;
    private String serviceTel;
    private TextView mServiceTel;
    private Gson mGson;
//    InputMethodManager imm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        MyApplication application = (MyApplication) getApplication();
        mDeviceNo = application.getMachineNo();
        mShowDeviceNo = application.getShowMachineNo();
        mServiceTel = findViewById(R.id.service_tel_text);
        mGson = new Gson();
        configLoginView();
        getServiceTel();

        mHandler.postDelayed(runnable, DELAY_TIME);
    }

    private void configLoginView() {

        mDeviceNoTextView = findViewById(R.id.device_no_text_view);
        mDeviceNoTextView.setText("货机编号：" + mShowDeviceNo);
        mReturnImageButton = findViewById(R.id.login_return_btn);
        mReturnImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mMobileEditText = findViewById(R.id.mobile_edit_text);
        mPwdEditText = findViewById(R.id.password_edit_text);
        mLoginBtn = findViewById(R.id.login_button);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginClick();
            }
        });
        mMobileEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHandler.removeCallbacks(runnable);
                mHandler.postDelayed(runnable, DELAY_TIME);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHandler.removeCallbacks(runnable);
                mHandler.postDelayed(runnable, DELAY_TIME);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 激活二维码
        mActivateQrImageView = findViewById(R.id.activate_qr_image_view);
        Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(UrlConfig.BASE_URL + UrlConfig.ACTIVATE_QR_URL + mShowDeviceNo, 400, 400);
        mActivateQrImageView.setImageBitmap(bitmap);
    }


    private void loginClick() {
        mHandler.removeCallbacks(runnable);
        mHandler.postDelayed(runnable, DELAY_TIME);
        Log.i(TAG, "点击登录");
        if (TextUtils.isEmpty(mMobileEditText.getText())) {
            showToast("请输入手机号");
        } else if (TextUtils.isEmpty(mPwdEditText.getText())) {
            showToast("请输入密码");
        } else {
            loginRequest(String.valueOf(mMobileEditText.getText()), String.valueOf(mPwdEditText.getText()));
        }
    }


    private void loginRequest(final String mobile, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("dev_no", mDeviceNo);
        params.put("phone", mobile);
        params.put("password", password);
        NetUtils.sendPost(UrlConfig.ATM_FEEDER_LOGIN_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
//                Log.i(TAG, "登录成功");
                Log.i(TAG, result);
                showToast("登录成功");
                MyApplication.mobile = mobile;
                mHandler.removeCallbacks(runnable);
                Intent intent = new Intent(LoginFragmentActivity.this, ReplenishmentActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                if (errorMsg.contains("reponse's code is : 466")) {
                    Log.i(TAG, "错误代码" + errCode + "内容：" + errorMsg);
                    showToast("账号或密码错误");
                } else {
                    Log.i(TAG, errorMsg);
                    showToast(errorMsg);
                }
            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private Handler mHandler = new Handler();

    @Override
    public void onBackPressed() {
        //            Intent intent= new Intent(this, IndexActivity.class);
        Intent intent = new Intent(this, IndexFragmentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouch();
                mHandler.removeCallbacks(runnable);
                mHandler.postDelayed(runnable, DELAY_TIME);
            case MotionEvent.ACTION_UP:
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            Intent intent = new Intent(LoginFragmentActivity.this,IndexFragmentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
//            onBackPressed();
        }
    };
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (Build.VERSION.SDK_INT >= 19){
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
//    }

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

    public void onTouch() {
        Log.i(TAG,"空白触摸事件");
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
