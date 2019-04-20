package com.tsminfo.android.baierhuiarm.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tsminfo.android.baierhuiarm.R;

public class EditDialog extends Dialog {

    private Button mYesButton, mNoButton;
    private TextView mTextView;
    private EditText mEditText;
    private String mTitle;
    private String mMessage;
    private int mInputType;
    private String mPositiveName, mNegativeName;


    private onNoClickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesClickListener yesOnclickListener;//确定按钮被点击了的监听器

    public EditDialog(@NonNull Context context) {

        super(context, R.style.dialog_input);
    }

    public EditDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected EditDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public EditDialog(Context context, String title, String placeholder, int inputType, onYesClickListener sureListener, onNoClickListener cancelListener) {
        super(context, R.style.dialog_input);
        this.mTitle = title;
        this.mMessage = placeholder;
        this.yesOnclickListener = sureListener;
        this.noOnclickListener = cancelListener;
        mInputType = inputType;
    }

    /**
     * 设置取消按钮内容和监听事件
     *
     * @param negativeName
     * @param noOnclickListener
     */
    public void setNoOnclickListener(String negativeName, onNoClickListener noOnclickListener) {
        if (negativeName != null){
            mNegativeName = negativeName;
        }
        this.noOnclickListener = noOnclickListener;
    }

    /**
     * 设置确定按钮内容和监听事件
     * @param positiveName
     * @param yesOnclickListener
     */
    public void setYesOnclickListener(String positiveName, onYesClickListener yesOnclickListener) {
        this.yesOnclickListener = yesOnclickListener;
    }

    public void setTitle(String title){
        this.mTitle = title;
    }

    public void setMessage(String message){
        this.mMessage = message;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit);
        setCanceledOnTouchOutside(false);

        initView();
        initData();
        initEvent();

    }


    private void initView(){
        mYesButton = findViewById(R.id.dialog_sure_btn);
        mNoButton = findViewById(R.id.dialog_cancel_btn);
        mTextView = findViewById(R.id.dialog_title_text_view);
        mEditText = findViewById(R.id.dialog_input_edit_text);

        mEditText.setInputType(mInputType);

    }

    private void initData(){
        if (mTitle != null) {
            mTextView.setText(mTitle);
        }
        if (mMessage != null) {
            mEditText.setHint(mMessage);
        }
        if (mPositiveName != null) {
            mYesButton.setText(mPositiveName);
        }
        if (mNegativeName != null) {
            mNoButton.setText(mNegativeName);
        }
    }

    private void initEvent(){
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick(mEditText.getText().toString());
                }
            }
        });

        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noOnclickListener.onNoClick();
            }
        });
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesClickListener {
        public void onYesClick(String phone);
    }

    public interface onNoClickListener {
        public void onNoClick();
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height= ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(150, 0, 150, 0);
        getWindow().setAttributes(layoutParams);
    }
}
