package com.tsminfo.android.baierhuiarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.tsminfo.android.baierhuiarm.utils.RxTimerUtil;

/**
 * Created by syj on 2019/4/15.
 * 定时返回目标activity
 */

public class BaseDispatchTouchActivity extends AppCompatActivity implements RxTimerUtil.IRxNext {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     * 主要的方法，重写dispatchTouchEvent
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            //获取触摸动作，如果ACTION_UP，计时开始。
            case MotionEvent.ACTION_UP:
                RxTimerUtil.timer(10000,this);
                break;
            //否则其他动作计时取消
            default:RxTimerUtil.cancel();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    protected void onPause() {
        super.onPause();
        RxTimerUtil.cancel();
    }
    @Override
    protected void onResume() {

        super.onResume();
        RxTimerUtil.timer(10000,this);
    }


    @Override
    public void doIntent() {
        Intent intent = new Intent(this,IndexFragmentActivity.class);
        startActivity(intent);
    }
}
