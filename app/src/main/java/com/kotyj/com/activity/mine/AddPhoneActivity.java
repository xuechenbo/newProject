package com.kotyj.com.activity.mine;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.activity.login.LoginNewActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.utils.CheckOutMessage;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPhoneActivity extends BaseActivity {
    private final long COUNT_DOWN_TOTAL = 60000;
    private final long COUNT_DOWN_INTERVAL = 1000;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_get_code)
    TextView tvGetCode;
    private final CountDownTimer countDownTimer = new CountDownTimer(COUNT_DOWN_TOTAL, COUNT_DOWN_INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvGetCode.setText("重新获取(" + String.format("%ds", millisUntilFinished / COUNT_DOWN_INTERVAL) + ")");
            tvGetCode.setEnabled(false);
        }

        @Override
        public void onFinish() {
            tvGetCode.setText("获取验证码");
            tvGetCode.setEnabled(true);
        }
    };

    @Override
    public int initLayout() {
        return R.layout.activity_addphone;
    }

    @Override
    public void initData() {

        tvTitle.setText("修改手机号");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_back, R.id.tv_get_code, R.id.bt_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
            case R.id.bt_register:
                String code = etCode.getText().toString();
                String phone = etPhone.getText().toString();
                if (code.isEmpty()) {
                    ViewUtils.makeToast(context, "验证码为空", 1200);
                    return;
                }
                submit(phone, code);
                break;
        }
    }

    private void submit(String phone, String code) {

        loadingDialog.show();
        HttpParams map = OkClient.getParamsInstance().getParams();
        map.put("3", "990003");
        map.put("1", phone);//手机号
        map.put("6", code);//验证码
        map.put("7", "10B");
        map.put("42", getMerId());
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
                    ViewUtils.makeToast2(context, "修改成功", 500, LoginNewActivity.class, "MOD_PWD");
                }
            }
        });

    }

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
        countDownTimer.start();
    }
}
