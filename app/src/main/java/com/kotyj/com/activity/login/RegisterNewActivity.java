package com.kotyj.com.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.activity.fun.X5WebViewActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.utils.CheckOutMessage;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.Constant;
import com.kotyj.com.utils.KeyBoardUtils;
import com.kotyj.com.utils.RegexUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterNewActivity extends BaseActivity {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_showpass)
    ImageView ivShowpass1;
    @BindView(R.id.et_repassword)
    EditText etRepassword;
    @BindView(R.id.iv_showpass1)
    ImageView ivShowpass2;

    @BindView(R.id.bt_register)
    Button btRegister;

    private boolean isPassclose1;
    private boolean isPassclose2;
    @BindView(R.id.tv_registration_protocol)
    TextView tvRegistrationProtocol;
    private Timer timer;
    private int time = 60;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (time == 0) {
                    timer.cancel();
                    tvGetCode.setText("获取验证码");
                } else {
                    tvGetCode.setText(time + "秒后可重发");
                    time--;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_new_register;
    }

    @Override
    public void initData() {
        tvTitle.setText("注  册");
    }

    @OnClick({R.id.iv_back, R.id.tv_get_code, R.id.tv_registration_protocol, R.id.bt_register, R.id.iv_showpass, R.id.iv_showpass1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_showpass:
                String password1 = etPassword.getText().toString().trim();
                if (StringUtil.isEmpty(password1)) {
                    return;
                }
                if (isPassclose1) {
                    isPassclose1 = false;
                    ivShowpass1.setImageResource(R.drawable.pass_show);
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    isPassclose1 = true;
                    ivShowpass1.setImageResource(R.drawable.pass_close);
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etPassword.setSelection(password1.length());
                break;
            case R.id.iv_showpass1:
                String password2 = etRepassword.getText().toString().trim();
                if (StringUtil.isEmpty(password2)) {
                    return;
                }
                if (isPassclose2) {
                    isPassclose2 = false;
                    ivShowpass2.setImageResource(R.drawable.pass_show);
                    etRepassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    isPassclose2 = true;
                    ivShowpass2.setImageResource(R.drawable.pass_close);
                    etRepassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etRepassword.setSelection(password2.length());
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_get_code:
                if ("获取验证码".equals(tvGetCode.getText().toString())) {
                    String phoneValue = etPhone.getText().toString();
                    if (CheckOutMessage.isEmpty(context, "手机号", phoneValue)) return;
                    if (phoneValue.length() != 11) {
                        ViewUtils.makeToast(context, "请输入正确的手机号", 1000);
                        return;
                    }
                    etCode.setText("");

                    sendCheckCode(phoneValue);
                }
                break;
            case R.id.tv_registration_protocol:
                Intent intent3 = new Intent(context, X5WebViewActivity.class);
                intent3.putExtra("title", "用户注册协议");
                intent3.putExtra("url", Constant.register_url);
                startActivity(intent3);
                break;
            case R.id.bt_register:
                KeyBoardUtils.hideKeyboard(view);
                register();
                break;
        }
    }

    private void register() {
        String pwdValue = etPassword.getText().toString();
        String confirmPwdValue = etRepassword.getText().toString();
        final String phoneNum = etPhone.getText().toString();
        String check_codeValue = etCode.getText().toString();
        if (CheckOutMessage.isEmpty(context, "手机号", phoneNum)) return;

        if (!RegexUtil.isPassWordLegal(context, pwdValue)) return;
        if (!RegexUtil.isPassWordLegal(context, confirmPwdValue)) return;
        if (CheckOutMessage.isEmpty(context, "验证码", check_codeValue)) return;

        if (pwdValue.length() < 8) {
            ViewUtils.makeToast(context, "密码位数不能小于8位数", 1000);
            return;
        }
        if (pwdValue.length() > 16) {
            ViewUtils.makeToast(context, "密码位数超限，请重新录入", 1000);
            return;
        }
        if (!pwdValue.equals(confirmPwdValue)) {
            ViewUtils.makeToast(context, "两次新密码输入不一致", 1000);
            return;
        }
        if (check_codeValue.length() != 6) {
            ViewUtils.makeToast(context, "请输入6位验证码", 1000);
            return;
        }

        loadingDialog.show();
        HttpParams map = OkClient.getParamsInstance().getParams();
        map.put("1", phoneNum);
        map.put("3", "190918");
        map.put("8", CommonUtils.Md5(pwdValue));
        map.put("44", Constant.AGENCY_CODE44);
        map.put("52", check_codeValue);
        OkClient.getInstance().post(map, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                super.onSuccess(response);
                BaseEntity model = response.body();
                loadingDialog.dismiss();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    ViewUtils.makeToast(context, "注册成功", 500);
                    StorageCustomerInfo02Util.putInfo(context, "phoneNum", phoneNum);
                    Intent intent = new Intent();
                    intent.putExtra("phone", phoneNum);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    /**
     * 发送验证码
     *
     * @param phoneValue
     */
    private void sendCheckCode(final String phoneValue) {
        loadingDialog.show();
        HttpParams map = OkClient.getParamsInstance().getParams();
        map.put("1", phoneValue);
        map.put("3", "190919");
        OkClient.getInstance().post(map, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                super.onSuccess(response);
                BaseEntity model = response.body();
                loadingDialog.dismiss();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    ViewUtils.makeToast(context, "验证码已发送，请注意查收", 500);
                    time();
                }
            }
        });
    }

    void time() {
        timer = new Timer();
        time = 60;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                timing();
            }
        };
        timer.schedule(task, 0, 1000);
    }

    void timing() {
        Message message = Message.obtain();
        message.what = 1;
        handler.sendMessage(message);
    }


}
