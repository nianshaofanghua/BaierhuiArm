package com.tsminfo.android.baierhuiarm.activity.replenishment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tsminfo.android.baierhuiarm.activity.IndexActivity;
import com.tsminfo.android.baierhuiarm.activity.IndexFragmentActivity;
import com.tsminfo.android.baierhuiarm.base.SingleFragmentActivity;

public class ReplenishmentActivity extends SingleFragmentActivity {

    @Override
    public void onBackPressed() {
//        Intent intent= new Intent(this, IndexActivity.class);
        Intent intent= new Intent(this, IndexFragmentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected Fragment createFragment() {
        return new ReplenishmentFragment();
    }
}
