package com.tsminfo.android.baierhuiarm.activity.login;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tsminfo.android.baierhuiarm.R;
import com.tsminfo.android.baierhuiarm.activity.replenishment.ReplenishmentActivity;
import com.tsminfo.android.baierhuiarm.application.MyApplication;
import com.tsminfo.android.baierhuiarm.config.UrlConfig;
import com.tsminfo.android.baierhuiarm.utils.NetUtils;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private TextView mDeviceNoTextView;
    private ImageButton mReturnImageButton;
    private EditText mMobileEditText;
    private EditText mPwdEditText;
    private Button mLoginBtn;
    private TextView tv_version;
    private String mDeviceNo;
    private String mShowDeviceNo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, container, false);

        MyApplication application = (MyApplication) getActivity().getApplication();
        mDeviceNo = application.getMachineNo();
        mShowDeviceNo = application.getShowMachineNo();

        configLoginView(v);

        return v;
    }


    private void configLoginView(View v) {

        mDeviceNoTextView = v.findViewById(R.id.device_no_text_view);
        mDeviceNoTextView.setText("货机编号：" + mShowDeviceNo);
        mReturnImageButton = v.findViewById(R.id.login_return_btn);
        mReturnImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        mMobileEditText = v.findViewById(R.id.mobile_edit_text);
        mPwdEditText = v.findViewById(R.id.password_edit_text);
        mLoginBtn = v.findViewById(R.id.login_button);
        tv_version = v.findViewById(R.id.tv_version);
//        try {
//            String versionName = getActivity().getPackageManager().
//                    getPackageInfo(getActivity().getPackageName(), 0).versionName;
//            tv_version.setText("v" + versionName);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginClick();
            }
        });

    }


    private void loginClick() {
        Log.i(TAG, "点击登录");
        if (TextUtils.isEmpty(mMobileEditText.getText())) {
            showToast("请输入手机号");
        } else if (TextUtils.isEmpty(mPwdEditText.getText())) {
            showToast("请输入密码");
        } else {
            loginRequest(String.valueOf(mMobileEditText.getText()), String.valueOf(mPwdEditText.getText()));
        }
    }


    private void loginRequest(final String mobile, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("dev_no", mDeviceNo);
        params.put("phone", mobile);
        params.put("password", password);
        NetUtils.sendPost(UrlConfig.ATM_FEEDER_LOGIN_URL, params, new NetUtils.WGNetCallBack() {
            @Override
            public void onSuccess(String result) {
//                Log.i(TAG, "登录成功");
                Log.i(TAG, result);
                showToast("登录成功");
                MyApplication.mobile = mobile;
                Intent intent = new Intent(getActivity(), ReplenishmentActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);

            }

            @Override
            public void onFailed(Integer errCode, String errorMsg) {
                if (errorMsg.contains("reponse's code is : 466")) {
                    showToast("账号或密码错误");
                } else {
                    Log.i(TAG, errorMsg);
                    showToast(errorMsg);
                }
            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


}
