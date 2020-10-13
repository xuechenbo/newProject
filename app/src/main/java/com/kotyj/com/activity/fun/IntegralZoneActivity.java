package com.kotyj.com.activity.fun;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.RefreshIntegral;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.UserInfoModel;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.Utils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntegralZoneActivity extends BaseActivity {
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.iv_level)
    TextView ivLevel;
    @BindView(R.id.tv_integral)
    TextView tvIntegral;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_tvmx)
    TextView tvTvmx;


    @Override
    public int initLayout() {
        return R.layout.act_integral_zone;
    }

    @Override
    public void initData() {
        tvTitle.setText("积分兑换");
        tvTvmx.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        requestDate();
    }

    private void requestDate() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "190112");
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
                    GlideUtils.loadAvatar(context, model.getStr48(), ivAvatar);
                    List<UserInfoModel> list = JSONArray.parseArray(model.getStr57(), UserInfoModel.class);
                    if (list.size() == 0) {
                        return;
                    }
                    UserInfoModel userInfoModel = list.get(0);
                    String level = userInfoModel.getLevel();
                    StorageCustomerInfo02Util.putInfo(context, "level", level);
                    if (Integer.parseInt(level) >= 6) {
                        ivLevel.setVisibility(View.GONE);
                    }
                    ivLevel.setText(Utils.GetLeveName(level));

                    SpannableString spannableString = new SpannableString("积分:" + userInfoModel.getScore());
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#F9D22B"));
                    spannableString.setSpan(foregroundColorSpan, 3, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvIntegral.setText(spannableString);

                } else {
                    ViewUtils.makeToast(context, model.getStr39(), 1200);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @OnClick({R.id.iv_back, R.id.iv_inegral_shop, R.id.iv_jindan,
            R.id.tv_tvmx, R.id.iv_dh, R.id.ll_qianbao1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_tvmx:
                startActivity(new Intent(context, YuEDetailActivity.class)
                        .putExtra("title", "积分明细"));
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_inegral_shop:
                break;
            case R.id.iv_jindan:
                //TODO 积分转让
                startActivity(new Intent(context, TransferIntegraActivity.class));
                break;
            case R.id.iv_dh:
                if (StorageCustomerInfo02Util.getIntInfo(context, "jf", -1) == 0) {
                    ViewUtils.makeToast(context, "暂未开放", 500);
                    return;
                }

                loadUrlData("积分兑换");
                break;
        }
    }

    private void loadUrlData(final String title) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "081430");
        httpParams.put("42", getMerNo());
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
                    goWebView(model.getStr38(), title);
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


    @Subscribe
    public void onEvent(RefreshIntegral event) {
        requestDate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
