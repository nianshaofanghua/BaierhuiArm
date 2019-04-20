package com.tsminfo.android.baierhuiarm.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.tsminfo.android.baierhuiarm.config.ArmConstants;
import com.tsminfo.android.baierhuiarm.config.UrlConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.Call;

public class NetUtils {

    /**
     * get 请求
     *
     * @param url      地址
     * @param params   参数
     * @param callBack 回调
     */
    public static void sendGet(final String url, final Map<String, String> params, final WGNetCallBack callBack) {
        OkHttpUtils.get()
                .url(UrlConfig.BASE_URL + url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (e.getMessage().contains("No address associated with hostname") || e.getMessage().contains("Network is unreachable")) {
                            callBack.onFailed(-1, "网络连接失败");
                        } else {
                            callBack.onFailed(-1, e.getMessage());
                        }

                    }

                    @Override
                    public void onResponse(String response, int id) {

                        handleResponse(response, callBack);
                    }
                });
    }


    /**
     * post 请求
     *
     * @param url      地址
     * @param params   参数
     * @param callBack 回调
     */
    public static void sendPost(final String url, final Map<String, String> params, final WGNetCallBack callBack) {
        OkHttpUtils.post()
                .url(UrlConfig.BASE_URL + url)
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i("Error", "返回错误：代码 -1，信息：" + e.getMessage());
                        if (e.getMessage().contains("No address associated with hostname") || e.getMessage().contains("Network is unreachable")) {
                            callBack.onFailed(-1, "网络连接失败");
                        } else {
                            callBack.onFailed(-1, e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("response", response);
                        handleResponse(response, callBack);
                    }
                });
    }

    /**
     * 响应处理
     *
     * @param response 响应
     * @param callBack 回调
     */
    private static void handleResponse(String response, WGNetCallBack callBack) {
        JSONObject json = null;
        try {
            // 解析获取code
            json = new JSONObject(response);
            Integer code = json.getInt("code");
            if (code == 200) {
                if (response.contains("data")){
                    String data = json.getString("data");
                    callBack.onSuccess(data);
                }else {
                    String data = json.getString("msg");
                    callBack.onSuccess(data);
                }

            } else {
                if (response.contains("data")){
                    String data = json.getString("data");
                    callBack.onFailed(code, data);
                }else {
                    String data = json.getString("msg");
                    callBack.onFailed(code, data);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            callBack.onFailed(-2, e.getLocalizedMessage());
        }
    }


    /**
     * 图片请求
     *
     * @param url      地址
     * @param params   参数
     * @param callback 回调
     */
    public static void sendImgPost(final String url, final Map<String, String> params, final WGBitmapCallback callback) {
        OkHttpUtils.post()
                .url(UrlConfig.BASE_URL + url)
                .params(params)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFailed(-1, e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        callback.onSuccess(response);
                    }
                });
    }


    public interface WGNetCallBack {
        void onSuccess(String result);

        void onFailed(Integer errCode, String errorMsg);
    }

    public interface WGBitmapCallback {
        void onSuccess(Bitmap result);

        void onFailed(Integer errCode, String errorMsg);
    }
}
