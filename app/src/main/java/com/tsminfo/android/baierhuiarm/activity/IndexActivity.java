package com.tsminfo.android.baierhuiarm.activity;

import android.support.v4.app.Fragment;
import android.view.MotionEvent;

import com.tsminfo.android.baierhuiarm.base.SingleFragmentActivity;

public class IndexActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new IndexFragment();
    }
}
