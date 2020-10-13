package com.kotyj.com.activity.fun;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.RefreshIntegral;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransferIntegraActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_integra)
    EditText etIntegra;

    @Override
    public int initLayout() {
        return R.layout.act_trans_integra;
    }

    @Override
    public void initData() {
        tvTitle.setText("积分转让");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_back, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_submit:
                String phone = etPhone.getText().toString().trim();
                String integra = etIntegra.getText().toString().trim();

                if (phone.isEmpty()) {
                    ViewUtils.makeToast(context, "手机号为空", 1200);
                    return;
                }
                if (integra.isEmpty()) {
                    ViewUtils.makeToast(context, "转让积分为空", 1200);
                    return;
                }
                submit(phone, integra);
                break;
        }
    }

    private void submit(String phone, String integra) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "792003");
        httpParams.put("7", phone);//转让的手机号
        httpParams.put("8", integra);//转让积分
        httpParams.put("42", getMerNo());
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onSuccess(Response<BaseEntity> response) {
                loadingDialog.dismiss();
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    ViewUtils.makeToast(context, "转让成功", 1200);
                    EventBus.getDefault().post(new RefreshIntegral());
                    finish();
                } else {
                    ViewUtils.makeToast(context, model.getStr39(), 1200);
                }
            }

            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
            }
        });

    }
}
