package com.kotyj.com.activity.wkyk.newwkyk;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kotyj.com.MyApplication;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.BankCardEvent;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.BindCard;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.Utils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BankCreditDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.iv_bank_icon)
    ImageView ivBankIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_bank_account)
    TextView tvBankAccount;
    @BindView(R.id.cl_bank_head)
    ConstraintLayout clBankHead;
    @BindView(R.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R.id.tv_bank_account_2)
    TextView tvBankAccount2;
    @BindView(R.id.tv_name_2)
    TextView tvName2;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_id_card)
    TextView tvIdCard;
    @BindView(R.id.tv_limit)
    TextView tvLimit;
    @BindView(R.id.tv_bill_day)
    TextView tvBillDay;
    @BindView(R.id.tv_pay_day)
    TextView tvPayDay;
    @BindView(R.id.tv_dead_line)
    TextView tvDeadLine;
    @BindView(R.id.tv_cvv)
    TextView tvCvv;
    @BindView(R.id.ll_bank_info)
    LinearLayout llBankInfo;
    @BindView(R.id.rl)
    LinearLayout rl;
    private BindCard mBindCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_bank_credit_detail;
    }

    @Override
    public void initData() {
        tvTitle.setText("信用卡信息");
        mBindCard = (BindCard) getIntent().getSerializableExtra("bindCard");

        Utils.initBankCodeColorIcon(mBindCard.getBANK_NAME(), ivBankIcon, context);
        tvName.setText(MyApplication.getBankName(mBindCard.getBANK_NAME()));
        String banNum = mBindCard.getBANK_ACCOUNT();
        if (banNum.length() > 4) {
            String bankNum2 = banNum.substring(banNum.length() - 4, banNum.length());
            tvBankAccount.setText("尾号 " + bankNum2);
        }
        rl.setVisibility(View.VISIBLE);
        tvBankName.setText(MyApplication.getBankName(mBindCard.getBANK_NAME()));
        tvBankAccount2.setText(mBindCard.getBANK_ACCOUNT());
        tvName2.setText(mBindCard.getBANK_ACCOUNT_NAME());
        tvPhone.setText(mBindCard.getBANK_PHONE());
        tvIdCard.setText(mBindCard.getID_CARD_NUMBER());
        tvLimit.setText(mBindCard.getLIMIT_MONEY());
        tvBillDay.setText(mBindCard.getBILL_DAY());
        tvPayDay.setText(mBindCard.getREPAYMENT_DAY() + "");

        tvDeadLine.setText(mBindCard.getExpdate());
        tvCvv.setText(mBindCard.getCvn());
    }

    @OnClick({R.id.iv_back, R.id.btn_del, R.id.btn_change})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ViewUtils.overridePendingTransitionBack(context);
                break;
            case R.id.btn_del:
                showDelDialog();
                break;
            case R.id.btn_change:
                // TODO: 2019/9/4
                startActivityForResult(new Intent(context, BankCreditDetailChangeActivity.class).putExtra("bindCard", mBindCard), 1);
                break;
        }
    }

    private void showDelDialog() {
        ViewUtils.showDialogStandard3(context, "解绑信用卡", "", "", new ViewUtils.OnshowDialogStandard() {
            @Override
            public void clickOk() {
                delCreditCard();
            }

            @Override
            public void clickCancel() {

            }
        });
    }

    /**
     * 解绑信用卡
     */
    private void delCreditCard() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "190520");
        httpParams.put("8", mBindCard.getBANK_ACCOUNT());
        httpParams.put("42", mBindCard.getMERCHANT_NO());
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
                    ViewUtils.makeToast(context, "解绑成功", 1500);
                    EventBus.getDefault().post(new BankCardEvent());
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && RESULT_OK == resultCode) {
            finish();
        }
    }
}
