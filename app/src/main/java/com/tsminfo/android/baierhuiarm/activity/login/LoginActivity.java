package com.tsminfo.android.baierhuiarm.activity.login;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;

import com.tsminfo.android.baierhuiarm.activity.IndexActivity;
import com.tsminfo.android.baierhuiarm.activity.IndexFragmentActivity;
import com.tsminfo.android.baierhuiarm.base.SingleFragmentActivity;

public class LoginActivity extends SingleFragmentActivity {

    private Handler mHandler = new Handler();

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    public void onBackPressed() {
//            Intent intent= new Intent(this, IndexActivity.class);
            Intent intent= new Intent(this, IndexFragmentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent me) {
//        if (me.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (isShouldHideKeyboard(v, me)) {
//                hideKeyboard(v.getWindowToken());
//            }
//        }
//        return super.dispatchTouchEvent(me);
//    }
//
//
//    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
//        if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
//            int[] l = {0, 0};
//            v.getLocationInWindow(l);
//            int left = l[0],    //得到输入框在屏幕中上下左右的位置
//                    top = l[1],
//                    bottom = top + v.getHeight(),
//                    right = left + v.getWidth();
//            if (event.getX() > left && event.getX() < right
//                    && event.getY() > top && event.getY() < bottom) {
//                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
//                return false;
//            } else {
//                return true;
//            }
//        }
//        // 如果焦点不是EditText则忽略
//        return false;
//    }
//
//
//    private void hideKeyboard(IBinder token) {
//        if (token != null) {
//            InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }
}
