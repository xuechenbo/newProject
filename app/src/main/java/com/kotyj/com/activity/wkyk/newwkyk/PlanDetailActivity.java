package com.kotyj.com.activity.wkyk.newwkyk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.kotyj.com.MyApplication;
import com.kotyj.com.R;
import com.kotyj.com.adapter.PlanDetailAdapter;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.BankCardEvent;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.BindCard;
import com.kotyj.com.model.CardPlanList;
import com.kotyj.com.model.PlanItem;
import com.kotyj.com.model.PlanSmallDetailModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.LogUtils;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.Utils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 计划详情页
 */
public class PlanDetailActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_bank_icon)
    ImageView ivBankIcon;
    @BindView(R.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R.id.ll_bank_name)
    LinearLayout llBankName;
    @BindView(R.id.tv_pay_title)
    TextView tvPayTitle;
    @BindView(R.id.tv_bank_account)
    TextView tvBankAccount;
    @BindView(R.id.tv_repayCycle)
    TextView tvRepayCycle;
    @BindView(R.id.tv_channel_name)
    TextView tvChannelName;
    @BindView(R.id.tv_limit)
    TextView tvLimit;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_hNum)
    TextView tvHNum;
    @BindView(R.id.tv_total_service_fee)
    TextView tvTotalServiceFee;
    @BindView(R.id.delete_btn)
    TextView deleteBtn;
    @BindView(R.id.btn_stop_start)
    TextView btnStopStart;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_showMore)
    TextView tvShowMore;
    private PlanItem mPlanItem;
    private BindCard mBindCard;
    private List<CardPlanList> mList = new ArrayList<>();
    private String status;
    private String type;
    private PlanDetailAdapter planDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_plan_detail;
    }

    @Override
    public void initData() {
        tvTitle.setText("计划详情");
        mPlanItem = (PlanItem) getIntent().getSerializableExtra("planItem");
        type = mPlanItem.getType();
        mBindCard = (BindCard) getIntent().getSerializableExtra("bindCard");
        status = mPlanItem.getPlanStatus();

        String bankAccount = mBindCard.getBANK_ACCOUNT();
        if (!StringUtil.isEmpty(bankAccount) && bankAccount.length() > 4) {
            String bankNum1 = bankAccount.substring(0, 4);
            String bankNum2 = bankAccount.substring(bankAccount.length() - 4, bankAccount.length());
            tvBankAccount.setText(bankNum1 + " **** **** " + bankNum2);
        }
        tvBankName.setText(MyApplication.bankCodeList.get(mBindCard.getBANK_NAME()));


        Utils.initBankCodeColorIcon(mBindCard.getBANK_NAME(), ivBankIcon, this);

        tvRepayCycle.setText(mPlanItem.getPlanCycle());

        BigDecimal fee = CommonUtils.formatNewWithScale(mPlanItem.getPrePayFee(), 2);
        BigDecimal timesFee = CommonUtils.formatNewWithScale(mPlanItem.getPreTimesAmount(), 2);
        BigDecimal workingFund = CommonUtils.formatNewWithScale(mPlanItem.getWorkingFund(), 2);

        tvLimit.setText(mPlanItem.getShouldPayNow());//"还款总额"

        tvHNum.setText(mPlanItem.getPaymentNum());
        if (type.equals("10C")) {
            tvTotalPrice.setText(String.valueOf(timesFee.add(fee)));//"预留额度"
        } else {
            tvTotalPrice.setText(workingFund.toString());//"预留额度"
        }
        tvTotalServiceFee.setText(String.valueOf(timesFee.add(fee)));//手续费


//        tvTotalPrice.setText(String.valueOf(workingFund.add(fee).add(timesFee)));//"预留额度"
//        tvTotalServiceFee.setText(mPlanItem.getPrePayFee());//手续费


        tvChannelName.setText("通道名称:" + mPlanItem.getACQ_NAME());


        if ("10C".equals(status) || "10D".equals(status)) {
            btnStopStart.setText("启用");
        } else {
            btnStopStart.setText("暂停");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        planDetailAdapter = new PlanDetailAdapter(R.layout.item_plan_detail, mList, type.equals("10C"));
        planDetailAdapter.bindToRecyclerView(recyclerView);

        loadSmallPlan();
    }

    private void refreshData() {
        mList.clear();
        loadSmallPlan();
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.btn_stop_start
            , R.id.tv_showMore, R.id.delete_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ViewUtils.overridePendingTransitionBack(context);
                break;
            case R.id.tv_right:
                break;
            case R.id.btn_stop_start:
                if (!"10C".equals(status) && !"10D".equals(status)) {
                    cancelPlanRequest();
                } else {
                    cancelPlanRequest();
                }
                break;
            case R.id.delete_btn:
                showDeleteDialog();
                break;
            case R.id.tv_showMore:
                if (tvShowMore.getText().toString().equals("查看更多")) {
                    tvShowMore.setText("收起更多");
                    planDetailAdapter.setShowCount(false);
                } else {
                    tvShowMore.setText("查看更多");
                    planDetailAdapter.setShowCount(true);
                }
                break;
        }
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("是否删除计划，计划删除将无法恢复")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ("10A".equals(status) || "10B".equals(status)) {
                            ViewUtils.makeToast(context, "请先取消计划再删除！", 1000);
                            return;
                        }
                        deletePlan();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 删除计划
     */
    private void deletePlan() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "390002");
        httpParams.put("8", mPlanItem.getPlanId());
        httpParams.put("42", mPlanItem.getMerNo());
        httpParams.put("43", mBindCard.getBANK_ACCOUNT());
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
                    ViewUtils.makeToast(context, "删除成功", 1500);
                    EventBus.getDefault().post(new BankCardEvent());
                    finish();
                }
            }


        });
    }

    /**
     * 启动/取消计划
     */
    private void cancelPlanRequest() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "190214");
        httpParams.put("7", "10C".equals(status) || "10D".equals(status) ? "1" : "0");
        httpParams.put("8", mPlanItem.getPlanId());
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onSuccess(Response<BaseEntity> response) {
                super.onSuccess(response);
                loadingDialog.dismiss();
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    if ("10C".equals(status) || "10D".equals(status)) {
                        ViewUtils.makeToast(context, "启用成功", 500);
                        btnStopStart.setText("暂停");
                        status = "10B";
                    } else {
                        ViewUtils.makeToast(context, "取消成功", 500);
                        btnStopStart.setText("启用");
                        status = "10D";
                    }
                    EventBus.getDefault().post(new BankCardEvent());
                    refreshData();
                }
            }

            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
            }
        });
    }


    /**
     * 获取小计划
     */
    private void loadSmallPlan() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "190213");
        httpParams.put("8", mPlanItem.getPlanId());
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
                    LogUtils.i("57=" + model.getStr57());
                    List<PlanSmallDetailModel> list = JSONArray.parseArray(model.getStr57(), PlanSmallDetailModel.class);
                    for (PlanSmallDetailModel plan :
                            list) {
                        CardPlanList cardPlanList = new CardPlanList();
                        BigDecimal money = CommonUtils.formatNewWithScale(String.valueOf(plan.getMoney()), 2);
                        BigDecimal toMoney = CommonUtils.formatNewWithScale(String.valueOf(plan.getToMoney()), 2);
                        cardPlanList.setFromMoney(money);
                        cardPlanList.setToMoney(toMoney);
                        cardPlanList.setPayFee(money.subtract(toMoney));
                        cardPlanList.setFromCard(plan.getFromIncreaseId());
                        cardPlanList.setToCard(plan.getToIncreaseId());
                        if (plan.getPlanTime() != null) {
                            cardPlanList.setPlanTime(plan.getPlanTime().getTime());
                        }
                        cardPlanList.setStatus(plan.getStatus());
                        cardPlanList.setPlanItemId(plan.getId());
                        cardPlanList.setType(plan.getType());
                        cardPlanList.setArea(plan.getCustomizeCity());
                        cardPlanList.setIndustry(plan.getCityIndustry());
                        cardPlanList.setMessage(plan.getMessage());
                        cardPlanList.setFlag(plan.isFlag());
                        mList.add(cardPlanList);
                    }
                    planDetailAdapter.setNewData(mList);
                    if (mList.size() > 5) {
                        planDetailAdapter.setShowCount(true);
                    } else {
                        tvShowMore.setVisibility(View.GONE);
                    }

                }
            }
        });
    }


}
