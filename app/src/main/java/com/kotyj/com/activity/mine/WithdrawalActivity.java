package com.kotyj.com.activity.mine;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kotyj.com.MyApplication;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.utils.CheckOutMessage;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.EditTextUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.Utils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WithdrawalActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.bank_iv)
    ImageView bankIv;
    @BindView(R.id.bank_tv)
    TextView bankTv;
    @BindView(R.id.tv_bank_account)
    TextView tvBankAccount;
    @BindView(R.id.et_money)
    EditText etMoney;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.bank_llt)
    RelativeLayout bankLlt;
    @BindView(R.id.textView01)
    TextView textView01;
    private String loanAmount;
    private String money;
    private boolean type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_withdrawal;
    }

    @Override
    public void initData() {
        Drawable drawable = getResources().getDrawable(R.drawable.look_m);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvRight.setCompoundDrawables(drawable, null, null, null);
        tvTitle.setText(getIntent().getStringExtra("title"));

        String bankName = StorageCustomerInfo02Util.getInfo("bankDetail", context);
        loanAmount = getIntent().getStringExtra("money");
        type = getIntent().getBooleanExtra("type", false);
        tvRight.setVisibility(View.VISIBLE);

        //TODO true 转入余额
        if (type) {
            tvRight.setText("查看记录");
            bankLlt.setVisibility(View.GONE);
            btnSubmit.setText("确认转入");
            tvMoney.setText("可转余额：" + loanAmount);
            textView01.setText("转入金额");
        } else {
            tvRight.setText("提现记录");
            tvMoney.setText("可用余额：" + loanAmount);
        }

        String bankAccounts = StorageCustomerInfo02Util.getInfo("bankAccount", context);
        if (!StringUtil.isEmpty(bankAccounts) && bankAccounts.length() > 0) {
            String str = bankAccounts.substring(bankAccounts.length() - 4, bankAccounts.length());
            tvBankAccount.setText("尾号" + str);
        }

        bankTv.setText(bankName);
        Utils.initBankCodeColorIcon(MyApplication.bankNameList.get(bankName), bankIv, context);

        etMoney.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EditTextUtil.keepTwoDecimals(etMoney, 8);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @OnClick({R.id.iv_back, R.id.btn_submit, R.id.tv_right})
    public void onViewClicked(View view) {
        if (CommonUtils.isFastDoubleClick2()) {
            return;
        }
        switch (view.getId()) {
            case R.id.iv_back:
                ViewUtils.overridePendingTransitionBack(context);
                break;
            case R.id.btn_submit:
                money = etMoney.getText().toString().trim();
                if (CheckOutMessage.isEmpty(context, "金额", money)) {
                    return;
                }
                if (".".equals(money)) {
                    ViewUtils.makeToast(context, "请输入正确的金额", 500);
                    return;
                }
                submit(money);
                break;
            case R.id.tv_right:
                if (type) {
                    startActivity(new Intent(context, WithdrawYueListActivity.class));
                } else {
                    startActivity(new Intent(context, WithdrawListActivity.class));
                }
                break;
        }
    }

    private void submit(String money) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", type ? "990001" : "190888");
        //转入
        if (type) {
            httpParams.put("43", money);
            httpParams.put("42", getMerId());
        } else {
            httpParams.put("5", money);
            httpParams.put("42", getMerNo());
        }
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

                    ViewUtils.makeToast(context, type ? "转入成功" : "提现成功", 500);
                    finish();
                }
            }
        });
    }


}