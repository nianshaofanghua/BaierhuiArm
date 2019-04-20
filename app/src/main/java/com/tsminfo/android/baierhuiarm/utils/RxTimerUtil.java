package com.tsminfo.android.baierhuiarm.utils;

import android.util.Log;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by syj on 2019/4/15.
 * rx定时器
 *
 */

public class RxTimerUtil {
    private static Disposable mDisposable;

    /** milliseconds毫秒后执行next操作
     *
     * @param milliseconds
     * @param iRxNext
     */
    public static void timer(long milliseconds,final IRxNext iRxNext) {
        java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
        String     s = format2.format(new Date());
        Log.e("logzz",""+s);
        Observable.timer(milliseconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable=disposable;
                    }
                    @Override
                    public void onNext(@NonNull Long number) {
                        java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
                        String     s = format2.format(new Date());
                        Log.e("logzz",""+s);
                        if(iRxNext!=null){
                            iRxNext.doIntent();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        //取消订阅
                        cancel();
                    }

                    @Override
                    public void onComplete() {
                        //取消订阅
                        cancel();
                    }
                });
    }




    /**
     * 取消订阅
     */
    public static void cancel(){
        if(mDisposable!=null&&!mDisposable.isDisposed()){
            mDisposable.dispose();
            Log.e("logzz","====定时器取消======");
        }
    }

    public interface IRxNext{
        void doIntent();
    }
}
