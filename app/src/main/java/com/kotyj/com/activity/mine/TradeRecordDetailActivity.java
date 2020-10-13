package com.kotyj.com.activity.mine;

import android.os.Bundle;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.QueryModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.DateUtil;
import com.kotyj.com.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TradeRecordDetailActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.tv_trade_type)
    TextView tvTradeType;
    @BindView(R.id.tv_trade_time)
    TextView tvTradeTime;
    @BindView(R.id.tv_trade_account)
    TextView tvTradeAccount;
    @BindView(R.id.tv_trade_no)
    TextView tvTradeNo;
    @BindView(R.id.tv_trade_status)
    TextView tvTradeStatus;
    private QueryModel mQueryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.act_trade_detail;
    }

    @Override
    public void initData() {
        tvTitle.setText("交易详情");
        mQueryModel = (QueryModel) getIntent().getSerializableExtra("detail");
        updateData();
    }

    private void updateData() {
        if (mQueryModel == null) {
            return;
        }
        tvMoney.setText(CommonUtils.format00(mQueryModel.getTRX_AMT()) + "元");

        if (mQueryModel.getRate().equals("无")) {
            tvMerchantName.setText(mQueryModel.getRate());
        } else {
            tvMerchantName.setText(CommonUtils.RidofZero(mQueryModel.getRate()));
        }

        tvTradeType.setText(mQueryModel.getCde_name());
        tvTradeTime.setText(DateUtil.formatDateToHM_points_s(mQueryModel.getCREATE_TIME().getTime()));
        tvTradeAccount.setText(mQueryModel.getCardNo());
        tvTradeNo.setText(mQueryModel.getID());
        tvTradeStatus.setText(mQueryModel.getStatus());
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        ViewUtils.overridePendingTransitionBack(context);
    }
}
