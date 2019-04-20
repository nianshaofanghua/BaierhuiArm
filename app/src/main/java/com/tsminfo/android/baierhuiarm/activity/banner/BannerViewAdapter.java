package com.tsminfo.android.baierhuiarm.activity.banner;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class BannerViewAdapter extends PagerAdapter {

    private List<View> mViewList;

    public BannerViewAdapter(List<View> views) {
        if (views == null) {
            views = new ArrayList<>();
        }
        this.mViewList = views;
    }

    public void setDataList(List<View> list) {
        if (list != null && list.size() > 0) {
            this.mViewList = list;
        }
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = mViewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }


    @Override
    public int getCount() {
        return mViewList.size();
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
