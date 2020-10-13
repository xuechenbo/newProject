package com.kotyj.com.activity.fun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.UrlModel;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoreActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    public int initLayout() {
        return R.layout.act_more;
    }

    @Override
    public void initData() {
        tvTitle.setText("更多功能");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_back, R.id.img1, R.id.img2, R.id.img3, R.id.img4, R.id.img5, R.id.img6})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.img1:
                //TODO 征信
                ViewUtils.makeToast(context, "暂未开放", 1200);
                //startActivity(new Intent(context, CardHonorActivity.class));
                break;
            case R.id.img2:
                //我的积分

                break;
            case R.id.img3:
                //京东白条
                ViewUtils.makeToast(context, "暂未开放", 1200);
                break;
            case R.id.img4:
                //办卡
                if (StorageCustomerInfo02Util.getIntInfo(context, "bk", -1) == 0) {
                    ViewUtils.makeToast(context, "暂未开放", 500);
                    return;
                }
                loadUrlData("2", "信用卡办理");
                break;
            case R.id.img5:
                //贷款
                if (StorageCustomerInfo02Util.getIntInfo(context, "dk", -1) == 0) {
                    ViewUtils.makeToast(context, "暂未开放", 500);
                    return;
                }
                loadUrlData("1", "贷款申请");
                break;
            case R.id.img6:
                //卡测评
                if (StorageCustomerInfo02Util.getIntInfo(context, "kcp", -1) == 0) {
                    ViewUtils.makeToast(context, "暂未开放", 500);
                    return;
                }
                startActivity(new Intent(context, CardScoreActivity.class));
                break;
        }
    }


    /**
     * 获取链接
     */
    private void loadUrlData(final String type, final String title) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "690034");
        httpParams.put("41", getMerId());
        httpParams.put("43", type);//1 贷款 2 办卡
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
                    UrlModel urlModel = JSONObject.parseObject(model.getStr42(), UrlModel.class);
                    if ("2".equals(type)) {
                        goWebView(urlModel.getBK(), title);
                    } else if ("1".equals(type)) {
                        goWebView(urlModel.getDK(), title);
                    }
                }
            }
        });
    }

    private void goWebView(String url, String title) {
        Intent webIntent = new Intent(context, X5WebViewActivity.class);
        webIntent.putExtra("title", title);
        webIntent.putExtra("url", url);
        startActivity(webIntent);
    }
}
