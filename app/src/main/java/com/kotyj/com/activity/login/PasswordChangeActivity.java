package com.kotyj.com.activity.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.utils.CheckOutMessage;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.KeyBoardUtils;
import com.kotyj.com.utils.RegexUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordChangeActivity extends BaseActivity {

    private final long COUNT_DOWN_TOTAL = 60000;
    private final long COUNT_DOWN_INTERVAL = 1000;

    @BindView(R.id.ll_container)
    LinearLayout llContainer;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_phone)
    TextView etPhone;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R.id.iv_showpass)
    ImageView ivShowpass;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    private boolean islogin;


    private final CountDownTimer countDownTimer = new CountDownTimer(COUNT_DOWN_TOTAL, COUNT_DOWN_INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvGetCode.setText(String.format("%ds", millisUntilFinished / COUNT_DOWN_INTERVAL));
            tvGetCode.setEnabled(false);
        }

        @Override
        public void onFinish() {
            tvGetCode.setText("发送验证码");
            tvGetCode.setEnabled(true);
        }
    };
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_password_change;
    }

    @Override
    public void initData() {
        phone = StorageCustomerInfo02Util.getInfo("phone", context);
        etPhone.setText(CommonUtils.translateShortNumber(phone, 3, 4));
        islogin = getIntent().getBooleanExtra("isLogin", false);
        tvTitle.setText(getIntent().getStringExtra("title"));
        tvTip.setText(islogin ? "温馨提示：密码为8-16位数字+字母组合" : "温馨提示：密码为6位数字");
        if (!islogin) {
            etPassword.setInputType(InputType.TYPE_CLASS_NUMBER); //输入类型
            etPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)}); //最大输入长度
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); //设置为密码输入框

            etPassword.setHint("请输入新的支付密码");
        }

    }

    @OnClick({R.id.iv_back, R.id.bt_determine, R.id.tv_get_code, R.id.iv_showpass})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ViewUtils.overridePendingTransitionBack(context);
                break;
            case R.id.bt_determine:
                KeyBoardUtils.hideKeyboard(llContainer);
                String check_codeValue = etCode.getText().toString();
                String new_pwd = etPassword.getText().toString();
                if (CheckOutMessage.isEmpty(context, "验证码", check_codeValue)) return;
                if (CheckOutMessage.isEmpty(context, "密码", new_pwd)) return;
                if (check_codeValue.length() != 6) {
                    ViewUtils.makeToast(context, "请输入6位验证码", 1000);
                    return;
                }
                if (islogin) {
                    if (!RegexUtil.isPassWordLegal(context, new_pwd)) return;
                } else {
                    if (new_pwd.length() != 6) {
                        ViewUtils.makeToast(context, "请输入6位数字密码", 1200);
                    }
                }

                sendToModifyPwd(check_codeValue, new_pwd);

                break;
            case R.id.tv_get_code:
                if ("发送验证码".equals(tvGetCode.getText().toString())) {
                    etCode.setText("");
                    sendCheckCode();
                }
                break;
            case R.id.iv_showpass:
                String password1 = etPassword.getText().toString().trim();
                if (StringUtil.isEmpty(password1)) {
                    return;
                }
                if (isPassclose1) {
                    isPassclose1 = false;
                    ivShowpass.setImageResource(R.drawable.pass_show);
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    isPassclose1 = true;
                    ivShowpass.setImageResource(R.drawable.pass_close);
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etPassword.setSelection(password1.length());
                break;
        }
    }

    private boolean isPassclose1;

    private void sendToModifyPwd(String check_codeValue, String new_pwd) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", islogin ? "190929" : "990002");
        httpParams.put("1", StorageCustomerInfo02Util.getInfo("phone", context));
        httpParams.put("6", check_codeValue);
        if (islogin) {
            httpParams.put("9", CommonUtils.Md5(new_pwd));
        } else {
            httpParams.put("9", new_pwd);
        }

        httpParams.put("42", getMerId());
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                super.onSuccess(response);
                loadingDialog.dismiss();
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    StorageCustomerInfo02Util.removeKey("passwd", context);
                    if (islogin) {
                        ViewUtils.makeToast2(context, "修改成功", 500, LoginNewActivity.class, "MOD_PWD");
                    } else {
                        StorageCustomerInfo02Util.putInfo(context, "payPsd", "1");
                        ViewUtils.makeToast(context, "修改成功", 500);
                        finish();
                    }
                }
            }
        });
    }

    private void sendCheckCode() {
        loadingDialog.show();
        HttpParams map = OkClient.getParamsInstance().getParams();
        map.put("1", phone);
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
        countDownTimer.start();
    }

}
