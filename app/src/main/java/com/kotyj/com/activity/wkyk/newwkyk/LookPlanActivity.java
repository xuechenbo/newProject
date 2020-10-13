package com.kotyj.com.activity.wkyk.newwkyk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kotyj.com.MyApplication;
import com.kotyj.com.R;
import com.kotyj.com.adapter.LookPlanAdapter;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.BankCardEvent;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.BindCard;
import com.kotyj.com.model.PlanAllModel;
import com.kotyj.com.model.PlanItem;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.DateUtil;
import com.kotyj.com.utils.LogUtils;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.Utils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LookPlanActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_bank_icon)
    ImageView ivBankIcon;
    @BindView(R.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R.id.tv_bank_account)
    TextView tvBankAccount;
    @BindView(R.id.tv_limit)
    TextView tvLimit;
    @BindView(R.id.tv_DayNum)
    TextView tvDayNum;
    @BindView(R.id.tv_bill_day)
    TextView tvBillDay;
    @BindView(R.id.tv_pay_day)
    TextView tvPayDay;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.srl_refresh)
    SmartRefreshLayout srlRefresh;
    @BindView(R.id.ll1)
    LinearLayout ll1;
    private BindCard mBindCard;
    private LookPlanAdapter mLookPlanAdapter;
    private List<PlanItem> mList = new ArrayList<>();

    private String bankAccount;
    private boolean isMed;
    private int page = 1;
    private List<PlanItem> oList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int initLayout() {
        return R.layout.activity_look_plan;
    }

    @Override
    public void initData() {
        tvTitle.setText("查看计划");



        isMed = getIntent().getBooleanExtra("isMed", false);
        if (isMed) {
            ll1.setVisibility(View.GONE);
            srlRefresh.setEnableLoadmore(true);
        } else {
            srlRefresh.setEnableLoadmore(false);
            mBindCard = (BindCard) getIntent().getSerializableExtra("bindCard");
            bankAccount = mBindCard.getBANK_ACCOUNT();
            String bankAccount = mBindCard.getBANK_ACCOUNT();
            if (!StringUtil.isEmpty(bankAccount) && bankAccount.length() > 4) {
                String bankNum1 = bankAccount.substring(0, 4);
                String bankNum2 = bankAccount.substring(bankAccount.length() - 4, bankAccount.length());
                tvBankAccount.setText(bankNum1 + " **** **** " + bankNum2);
            }
            tvBankName.setText(MyApplication.bankCodeList.get(mBindCard.getBANK_NAME()));
            Utils.initBankCodeColorIcon(mBindCard.getBANK_NAME(), ivBankIcon, this);
            tvLimit.setText(mBindCard.getLIMIT_MONEY());
            tvBillDay.setText(mBindCard.getBILL_DAY());
            tvPayDay.setText("-" + mBindCard.getREPAYMENT_DAY() + "");

            tvDayNum.setText(mBindCard.getDay());
        }


        rvList.setLayoutManager(new LinearLayoutManager(context));
        mLookPlanAdapter = new LookPlanAdapter(mList, isMed);
        mLookPlanAdapter.bindToRecyclerView(rvList);
        mLookPlanAdapter.setEmptyView(R.layout.layout_bank_empty, srlRefresh);
        srlRefresh.setRefreshHeader(new ClassicsHeader(context));

        initListener();
    }

    private void initListener() {
        srlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mList.clear();
                page = 1;
                loadData(page);
            }
        });
        srlRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                loadData(page);
            }
        });
        mLookPlanAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                gotoDetails(mList.get(position));
            }
        });

        mLookPlanAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.btn_detail:
                        gotoDetails(mList.get(position));
                        break;
                }
            }
        });
    }


    public void gotoDetails(PlanItem planItem) {
        Intent intent = new Intent(context, PlanDetailActivity.class);
        intent.putExtra("planItem", planItem);

        if (isMed) {
            BindCard bindCard = new BindCard();
            bindCard.setBANK_ACCOUNT(planItem.getBankAccount());
            bindCard.setBANK_NAME(planItem.getBankName());
            intent.putExtra("bindCard", bindCard);
        } else {
            intent.putExtra("bindCard", mBindCard);
        }


        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mList.clear();
        page = 1;
        loadData(page);
    }

    private void loadData(int page) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", isMed ? "390016" : "190212");
        if (isMed) {
            httpParams.put("5", page);
            httpParams.put("6", "10");
        } else {
            httpParams.put("43", bankAccount);
        }
        httpParams.put("42", isMed ? getMerId() : mBindCard.getMERCHANT_NO());

        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
                srlRefresh.finishRefresh();
                srlRefresh.finishLoadmore();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                super.onSuccess(response);
                loadingDialog.dismiss();
                srlRefresh.finishRefresh();
                srlRefresh.finishLoadmore();
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    oList = new ArrayList<>();
                    LogUtils.i("57=" + model.getStr57());
                    if (StringUtil.isEmpty(model.getStr57()) || model.getStr57().equals("[]") || model.getStr57().equals("null")) {
                        mLookPlanAdapter.setNewData(mList);
                        return;
                    }
                    // : 2019/4/2 数据返回太乱，需要做处理
                    List<PlanAllModel> list = JSONArray.parseArray(model.getStr57(), PlanAllModel.class);

                    for (PlanAllModel allmodel : list) {
                        if (isMed) {
                            onPlanList(allmodel);
                        } else {
                            if (bankAccount.equals(allmodel.getBANK_ACCOUNT())) {
                                onPlanList(allmodel);
                            }
                        }
                    }

                    mList.addAll(oList);
                    Collections.sort(mList);
                    mLookPlanAdapter.setNewData(mList);
                }
            }
        });
    }

    public void onPlanList(PlanAllModel allmodel) {

        PlanItem planItem = new PlanItem();
        planItem.setPrePayFee(CommonUtils.formatNewWithScale(String.valueOf(allmodel.getSALE_FREE()), 2).toString());
        planItem.setFrozenAmount(CommonUtils.formatNewWithScale(String.valueOf(allmodel.getTHAW_TRX()), 2).toString());
        planItem.setWorkingFund(String.valueOf(allmodel.getCB_AMT()));
        planItem.setPayType("还款形式：" + allmodel.getEVERY_NUM() + "笔/日");
        if (allmodel.getCREATE_TIME() != null) {
            planItem.setCreateTime(DateUtil.formatDateToHMS(allmodel.getCREATE_TIME().getTime()));
        }
        if (allmodel.getSTART_TIME() != null && allmodel.getEND_TIME() != null) {
//            planItem.setPlanCycle(DateUtil.formateDateTOYMD(allmodel.getSTART_TIME().getTime()) + "至" + DateUtil.formateDateTOYMD(allmodel.getEND_TIME().getTime()));
            planItem.setPlanCycle(DateUtil.formatDateToHMD3(allmodel.getSTART_TIME().getTime()) + "至" + DateUtil.formatDateToHMD3(allmodel.getEND_TIME().getTime()));
        }
        planItem.setDeductFee(CommonUtils.formatNewWithScale(String.valueOf(allmodel.getFred()), 2).toString());
        planItem.setDeductTimesFee(CommonUtils.formatNewWithScale(String.valueOf(allmodel.getNumed()), 2).toString());
        planItem.setPaidAmountNow(allmodel.getPayed());
        planItem.setShouldPayNow(CommonUtils.formatNewWithScale(String.valueOf(allmodel.getPLAN_AMT()), 2).toString());
        planItem.setPlanId(allmodel.getID());
        planItem.setPlanStatus(allmodel.getSTATUS());
        planItem.setConsumed(CommonUtils.formatNewWithScale(String.valueOf(allmodel.getSaled()), 2).toString());
        planItem.setType(allmodel.getTYPE());
        planItem.setACQ_NAME(allmodel.getACQ_NAME());
        planItem.setDISCOUNTS_MONEY(allmodel.getDISCOUNTS_MONEY());
        planItem.setPlanProgress(allmodel.getProgress());
        planItem.setPreTimesAmount(CommonUtils.formatNewWithScale(String.valueOf(allmodel.getPAY_FREE()), 2).toString());
        planItem.setLevel(allmodel.getLevel());
        planItem.setPaymentNum(allmodel.getPaymentNum());


        //TODO 中介信息
        planItem.setBankName(allmodel.getBank_code());
        planItem.setBankAccount(allmodel.getBANK_ACCOUNT());

        if (isMed) {
            planItem.setMERCHANT_CN_NAME(allmodel.getMERCHANT_CN_NAME());
        }

        planItem.setMerNo(isMed ? allmodel.getMERCHANT_NO() : getMerNo());

        if (!oList.contains(planItem)) {
            oList.add(planItem);
        }

    }

    /**
     * @param event
     */
    @Subscribe
    public void onEvent(BankCardEvent event) {
        srlRefresh.autoRefresh();
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        ViewUtils.overridePendingTransitionBack(context);
    }
}
