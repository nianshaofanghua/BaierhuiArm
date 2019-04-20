package com.tsminfo.android.baierhuiarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tsminfo.android.baierhuiarm.activity.AdvertActivity;
import com.tsminfo.android.baierhuiarm.activity.IndexFragmentActivity;
import com.tsminfo.android.baierhuiarm.activity.login.LoginFragmentActivity;
import com.tsminfo.android.baierhuiarm.base.BaseActivity;
import com.tsminfo.android.baierhuiarm.config.UrlConfig;
import com.tsminfo.android.baierhuiarm.model.AdvertListModel;
import com.tsminfo.android.baierhuiarm.utils.ActivitysManager;
import com.tsminfo.android.baierhuiarm.utils.NetUtils;

import java.util.HashMap;

public class AdvertDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView iv_bg;
    private TextView tv_back, tv_replenishment;
    private ActivitysManager activitysManager;
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert_detail);
        activitysManager = ActivitysManager.getInstance();
        activitysManager.addActivity(this);



        NavigationBarStatusBar(this,true);
        initView();
        getAdvert(getIntent().getStringExtra("key"));
    }

    private void initView() {
        iv_bg = findViewById(R.id.iv_bg);
        tv_back = findViewById(R.id.tv_back);
        tv_replenishment = findViewById(R.id.tv_replenishment);
        tv_back.setOnClickListener(this);
        tv_replenishment.setOnClickListener(this);


    }

    private void getAdvert(String key) {

        HashMap<String, String> params = new HashMap<>();
        params.put("name", key);
        NetUtils.sendGet(UrlConfig.ATM_ADVERT_DETAIL_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
//                Log.i(TAG, "登录成功");
                Log.e("logzz", "" + result);
//                AdvertListModel model = new Gson().fromJson(result, AdvertListModel.class);
//                Glide.with(AdvertDetailActivity.this).load(model.getBack_img()).into(iv_bg);
                Glide.with(AdvertDetailActivity.this).load(result).into(iv_bg);
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                Log.e("logzz", "" + errorMsg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_replenishment:
                Intent intent = new Intent(this, LoginFragmentActivity.class);
                startActivity(intent);
                break;
        }
    }
    /**
     * 导航栏，状态栏隐藏
     * @param activity
     */
    public static void NavigationBarStatusBar(Activity activity, boolean hasFocus) {
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = activity.getWindow().getDecorView();
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
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeCallbacks(runnable);
                break;
            case MotionEvent.ACTION_UP:
                mHandler.postDelayed(runnable, 30000);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    Runnable runnable = new Runnable(){

        @Override
        public void run() {
            onBackPressed();
        }
    };
    @Override
    public void onBackPressed() {
//            Intent intent= new Intent(this, IndexActivity.class);
        Intent intent= new Intent(this, IndexFragmentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(runnable, 30000);
    }

}
