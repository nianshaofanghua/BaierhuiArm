package com.tsminfo.android.baierhuiarm.activity.banner;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.danikula.videocache.HttpProxyCacheServer;
import com.tsminfo.android.baierhuiarm.activity.IndexFragmentActivity;
import com.tsminfo.android.baierhuiarm.application.MyApplication;
import com.tsminfo.android.baierhuiarm.model.BannerModel;

import java.util.ArrayList;
import java.util.List;


/**
 * banner
 */
public class Banner extends RelativeLayout {

    private static final String TAG = "Banner";

    private static final String banner_type_video = "2"; // 视频

    private ViewPager mViewPager;
    private final int UPDATE_VIEWPAGER = 100;
    // 图片默认时间间隔
    private int mImgInterval = 5000;
    // 每个位置默认时间间隔
    private int defaultInterval = 2000;
    // 默认显示位置
    private int mCurrentIndex;
    // 是否自动播放
    private Boolean isAutoPlay = false;
    private Time mTime;
    private List<View> mViewList;
    private BannerViewAdapter mAdapter;

    private HttpProxyCacheServer mProxyCacheServer;


    public Banner(Context context) {
        super(context);
        init();
    }

    public Banner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Banner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        mProxyCacheServer = MyApplication.getProxy(getContext());
        mTime = new Time();
        mViewPager = new ViewPager(getContext());
        LinearLayout.LayoutParams vp_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mViewPager.setLayoutParams(vp_params);
        this.addView(mViewPager);
    }


    // 设置数据, 数据处理
    public void setDataList(List<BannerModel> dataList) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        // 用于显示数组
        if (mViewList == null) {
            mViewList = new ArrayList<>();
        } else {
            mViewList.clear();
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RequestOptions options = new RequestOptions();
        options = options.centerCrop();
        if (dataList.size() > 1) {
            mCurrentIndex = 1;
            // 循环数组, 将首位各加一条数据
            for (int i = 0; i < dataList.size() + 2; i++) {
                int index = i-1;
                if (i == 0) {
                    index = dataList.size() - 1;
                } else if (i == dataList.size() + 1) {
                    index = 0;
                }
                BannerModel bannerModel = dataList.get(index);
                String url = bannerModel.getImg();
                if (bannerModel.getType().equals(banner_type_video)) {
                    url = mProxyCacheServer.getProxyUrl(url);
                    BannerVideoView videoView = new BannerVideoView(getContext());
                    videoView.setLayoutParams(lp);
                    videoView.setVideoURI(Uri.parse(url));
                    videoView.start();
                    mViewList.add(videoView);
                } else {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setLayoutParams(lp);
//                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(getContext()).load(url).apply(options).into(imageView);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    mViewList.add(imageView);
                }
            }
        } else if (dataList.size() == 1) {
            mCurrentIndex = 0;
            BannerModel bannerModel= dataList.get(0);
            String url = bannerModel.getImg();
            if (bannerModel.getType().equals(banner_type_video)) {
                BannerVideoView videoView = new BannerVideoView(getContext());
                videoView.setLayoutParams(lp);
                videoView.setVideoURI(Uri.parse(url));
                videoView.start();
                //监听视频播放完的代码
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mPlayer) {
                        mPlayer.start();
                        mPlayer.setLooping(true);
                    }
                });
                mViewList.add(videoView);
            } else {
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(lp);
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(getContext()).load(url).apply(options).into(imageView);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                mViewList.add(imageView);
            }
        }
    }

    // 设置图片播放间隔
    public void setImgInterval(int imgInterval) {
        this.mImgInterval = imgInterval;
    }


    public void startBanner() {

        mAdapter = new BannerViewAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(mCurrentIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.d(TAG, "onPageSelected: " + i);
                mCurrentIndex = i;
                getDelayedTime(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

                Log.d(TAG, "onPageScrollStateChanged: " + i);
                // 移除自动计时
                mHandler.removeCallbacks(mRunnable);
                // ViewPager跳转
                int pageIndex = mCurrentIndex;
                if (mCurrentIndex == 0) {
                    pageIndex = mViewList.size() - 2;
                } else if (mCurrentIndex == mViewList.size() - 1) {
                    pageIndex = 1;
                    IndexFragmentActivity.imageCount--;
                }
                if (pageIndex != mCurrentIndex) {
                    mViewPager.setCurrentItem(pageIndex, false);
                }

                // 停止滑动, 重新自动倒计时
                if (i == 0 && isAutoPlay && mViewList.size() > 1) {
                    View v = mViewList.get(pageIndex);
                    if (v instanceof VideoView) {
                        final VideoView videoView = (VideoView) v;
                        int current = videoView.getCurrentPosition();
                        int duration = videoView.getDuration();
                        defaultInterval = duration - current;
                        // 某些时候，某些视频，获取的时间无效，就延时10秒，重新获取
                        if (defaultInterval <= 0) {
                            mTime.getIntervalTime(videoView, mRunnable);
                            mHandler.postDelayed(mTime, mImgInterval);
                        } else {
                            mHandler.postDelayed(mRunnable, defaultInterval);
                        }
                    } else {
                        defaultInterval = mImgInterval;
                        mHandler.postDelayed(mRunnable, defaultInterval);
                    }
                }
            }
        });
    }

    /**
     * 开启自动循环
     */
    public void startAutoPlay() {
        isAutoPlay = true;
        if (mViewList.size() > 1) {
            getDelayedTime(mCurrentIndex);
            if (defaultInterval <= 0) {
                mHandler.postDelayed(mTime, mImgInterval);
            } else {
                mHandler.postDelayed(mRunnable, defaultInterval);
            }
        }
    }

    /**
     * 发消息, 进行循环
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(UPDATE_VIEWPAGER);
        }
    };

    /**
     * 获取视频长度及已经播放的时间
     */
    private class Time implements Runnable {

        private VideoView mVideoView;
        private Runnable mRunnable;

        public void getIntervalTime(VideoView videoView, Runnable runnable) {
            this.mVideoView = videoView;
            this.mRunnable = runnable;
        }

        @Override
        public void run() {

            int current = mVideoView.getCurrentPosition();
            int duration = mVideoView.getDuration();
            int delayed = duration - current;

            mHandler.postDelayed(mRunnable, delayed);
        }
    }


    // 接收消息实现轮播
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VIEWPAGER:
                    mViewPager.setCurrentItem(mCurrentIndex + 1);
                    break;
            }
        }
    };


    public void dataChange(List<BannerModel> list) {
        if (list != null && list.size() > 0) {
            // 重新开启循环, 重新获取interval
            mHandler.removeCallbacks(mRunnable);
            setDataList(list);
            mAdapter.setDataList(mViewList);
            mAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(mCurrentIndex, false);
            // 开启循环
            if (isAutoPlay && mViewList.size() > 1) {
                getDelayedTime(mCurrentIndex);
                if (defaultInterval <= 0) {
                    mHandler.postDelayed(mTime, mImgInterval);
                } else {
                    mHandler.postDelayed(mRunnable, defaultInterval);
                }
            }
        }
    }

    /**
     * 获取延时时间
     *
     * @param position 当前位置
     */
    private void getDelayedTime(int position) {

        View v = mViewList.get(position);
        if (v instanceof VideoView) {
            VideoView videoView = (VideoView) v;
            videoView.start();
            videoView.seekTo(0);
            defaultInterval = videoView.getDuration();
            mTime.getIntervalTime(videoView, mRunnable);
        } else {
            defaultInterval = mImgInterval;
        }
    }

    public void destroy() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mTime = null;
        mRunnable = null;
        if (mViewList != null && mViewList.size() > 0){
            mViewList.clear();
        }
        mViewList = null;
        mViewPager = null;
        mAdapter = null;
    }
}
