package com.tsminfo.android.baierhuiarm.activity;

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
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.tsminfo.android.baierhuiarm.AdvertDetailActivity;
import com.tsminfo.android.baierhuiarm.R;
import com.tsminfo.android.baierhuiarm.activity.login.LoginFragmentActivity;
import com.tsminfo.android.baierhuiarm.activity.replenishment.ReplenishmentActivity;
import com.tsminfo.android.baierhuiarm.application.MyApplication;
import com.tsminfo.android.baierhuiarm.base.BaseActivity;
import com.tsminfo.android.baierhuiarm.config.UrlConfig;
import com.tsminfo.android.baierhuiarm.model.AdvertListModel;
import com.tsminfo.android.baierhuiarm.utils.ActivitysManager;
import com.tsminfo.android.baierhuiarm.utils.CenterCropRoundCornerTransform;
import com.tsminfo.android.baierhuiarm.utils.NetUtils;
import com.tsminfo.android.baierhuiarm.utils.SystemUtils;

import java.util.HashMap;
import java.util.Map;

public class AdvertActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_bg, iv_one, iv_two, iv_three, iv_four;
    private TextView tv_back, tv_replenishment;


    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert);
        ActivitysManager   activitysManager = ActivitysManager.getInstance();
        activitysManager.addActivity(this);
        NavigationBarStatusBar(this,true);
        initView();

        getAdvert();
    }

    private void initView() {
        int a  = 0;
        a= a+11;
        iv_bg = findViewById(R.id.iv_bg);
        iv_one = findViewById(R.id.iv_one);
        iv_two = findViewById(R.id.iv_two);
        iv_three = findViewById(R.id.iv_three);

        iv_four = findViewById(R.id.iv_four);

        tv_back = findViewById(R.id.tv_back);
        tv_replenishment = findViewById(R.id.tv_replenishment);
        tv_back.setOnClickListener(this);
        tv_replenishment.setOnClickListener(this);

        iv_one.setOnClickListener(this);
        iv_two.setOnClickListener(this);
        iv_three.setOnClickListener(this);
        iv_four.setOnClickListener(this);
        tv_back.setOnClickListener(this);
//     mHandler.postDelayed(runnable, 30000);
    }

    private void getAdvert() {

        HashMap<String, String> params = new HashMap<>();
        NetUtils.sendGet(UrlConfig.ATM_ADVERT_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
//                Log.i(TAG, "登录成功");
                Log.e("logzz", "" + result);
                AdvertListModel model = new Gson().fromJson(result, AdvertListModel.class);
                Glide.with(AdvertActivity.this).load(model.getBack_img()).into(iv_bg);
                RequestOptions myOptions = new RequestOptions();

                myOptions.transform(new CenterCropRoundCornerTransform(SystemUtils.dip2px(AdvertActivity.this, 8)));
                Glide.with(AdvertActivity.this).load(model.getTotal().getOne_img()).apply(myOptions).into(iv_one);
                Glide.with(AdvertActivity.this).load(model.getTotal().getTwo_img()).apply(myOptions).into(iv_two);
                Glide.with(AdvertActivity.this).load(model.getTotal().getThree_img()).apply(myOptions).into(iv_three);
                Glide.with(AdvertActivity.this).load(model.getTotal().getFour_img()).apply(myOptions).into(iv_four);
            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_replenishment:
                 intent = new Intent(this, LoginFragmentActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_one:
                intent  = new Intent(this, AdvertDetailActivity.class);
                intent.putExtra("key","one_img");
                startActivity(intent);
                break;
            case R.id.iv_two:
                intent  = new Intent(this, AdvertDetailActivity.class);
                intent.putExtra("key","two_img");
                startActivity(intent);
                break;
            case R.id.iv_three:
                intent  = new Intent(this, AdvertDetailActivity.class);
                intent.putExtra("key","three_img");
                startActivity(intent);
                break;
            case R.id.iv_four:
                intent  = new Intent(this, AdvertDetailActivity.class);
                intent.putExtra("key","four_img");
                startActivity(intent);
                break;
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
    /**
     * 导航栏，状态栏隐藏
     * @param activity
     */
    public static void NavigationBarStatusBar(Activity activity, boolean hasFocus){
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
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(runnable, 30000);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= 19){
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

}
