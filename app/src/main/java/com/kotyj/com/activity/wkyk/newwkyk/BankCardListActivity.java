package com.kotyj.com.activity.wkyk.newwkyk;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kotyj.com.R;
import com.kotyj.com.activity.med.maode.MediationBean;
import com.kotyj.com.adapter.ManagerCreditAdapter;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.BankCardEvent;
import com.kotyj.com.event.SwipeCloseEvent;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.BindCard;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BankCardListActivity extends BaseActivity {

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.srl_refresh)
    SmartRefreshLayout srlRefresh;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.cl_container)
    RelativeLayout clContainer;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_cardNum)
    TextView tvCardNum;
    @BindView(R.id.med_top)
    ConstraintLayout medTop;
    private ManagerCreditAdapter mBankCardListAdapter;
    private List<BindCard> mList = new ArrayList<>();
    private boolean isPay;
    private String money;
    private String payType;
    private boolean med;
    private MediationBean mMedBean;
    private BindCard chooseBindCard;

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
        return R.layout.activity_bank_card_list;
    }

    @Override
    public void initData() {
        String title = getIntent().getStringExtra("title");
        tvTitle.setText(!StringUtil.isEmpty(title) ? title : "卡包");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("添加信用卡");
        Drawable drawable = getResources().getDrawable(R.drawable.right_add);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvRight.setCompoundDrawables(drawable, null, null, null);

        //TODO 无卡
        isPay = getIntent().getBooleanExtra("is2Pay", false);
        money = getIntent().getStringExtra("money");
        payType = getIntent().getStringExtra("payType");

        //TODO 中介
        mMedBean = (MediationBean) getIntent().getSerializableExtra("mMedBean");
        med = getIntent().getBooleanExtra("med", false);

        srlRefresh.setRefreshHeader(new ClassicsHeader(context));
        mBankCardListAdapter = new ManagerCreditAdapter(mList);
        mBankCardListAdapter.setIs2Pay(isPay);
        mBankCardListAdapter.setMakeType(isPay ? "立即收款" : "立即还款");
        rvList.setLayoutManager(new LinearLayoutManager(context));
        mBankCardListAdapter.bindToRecyclerView(rvList);
        mBankCardListAdapter.setEmptyView(R.layout.layout_bank_empty, rvList);
        if (med) {
            initMedTop();
        }
        initListener();
        srlRefresh.setEnableLoadmore(false);
        requestData();
    }

    void initMedTop() {
        GlideUtils.loadAvatar(context, "", ivHead);
        tvName.setText(mMedBean.getMERCHANT_CN_NAME());
        medTop.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onEvent(SwipeCloseEvent event) {
        ViewUtils.overridePendingTransitionBack(context);
    }

    BindCard loadBind(BindCard bindCard) {
        if (med) {
            //TODO 中介制定计划
            bindCard.setMERCHANT_NO(mMedBean.getMERCHANT_NO());
            bindCard.setMedId(mMedBean.getID());
        } else {
            bindCard.setMERCHANT_NO(getMerNo());
            bindCard.setMedId(getMerId());
        }
        return bindCard;
    }

    private void initListener() {
        srlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                requestData();
            }
        });

        srlRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {

            }
        });

        mBankCardListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BindCard bindCard = mList.get(position);
                if (isPay) {
                    Intent swipeIntent = new Intent(context, WKchannelActivity.class);
                    swipeIntent.putExtra("BindCard", bindCard);
                    swipeIntent.putExtra("money", money);
                    swipeIntent.putExtra("payType", payType);
                    startActivity(swipeIntent);
                } else {
                    //TODO 判断是是否中介，中介10A
                    bindCard.setMakeType(med);
                    BankCardDialog bankCardDialog = BankCardDialog.getIntence(loadBind(bindCard));
                    bankCardDialog.show(getSupportFragmentManager(), "");
                }
            }
        });
        mBankCardListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (CommonUtils.isFastDoubleClick2()) {
                    return;
                }
                chooseBindCard = mList.get(position);
                switch (view.getId()) {
                    case R.id.tv_unbind:
                        showDelDialog(loadBind(chooseBindCard));
                        break;
                    case R.id.btn_make:
                        if (isPay) {
                            Intent swipeIntent = new Intent(context, WKchannelActivity.class);
                            swipeIntent.putExtra("BindCard", chooseBindCard);
                            swipeIntent.putExtra("money", money);
                            swipeIntent.putExtra("payType", payType);
                            startActivity(swipeIntent);
                            return;
                        }
                        if (chooseBindCard.getPlanCount() > 0) {
                            final Dialog dialog = new Dialog(context, R.style.MyProgressDialog);
                            dialog.setContentView(R.layout.dialog_plan_tip);
                            TextView bt_known = (TextView) dialog.findViewById(R.id.btn);
                            dialog.setCanceledOnTouchOutside(true);
                            bt_known.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                            return;
                        }

                        chooseBindCard.setMakeType(med);
                        BankCardDialog bankCardDialog = BankCardDialog.getIntence(loadBind(chooseBindCard));
                        bankCardDialog.show(getSupportFragmentManager(), "");


                        break;
                    default:
                        break;
                }
            }
        });
    }


    private void showDelDialog(final BindCard bindCard) {
        ViewUtils.showDialogStandard3(context, "解绑信用卡", "", "",
                new ViewUtils.OnshowDialogStandard() {
                    @Override
                    public void clickOk() {
                        delCreditCard(bindCard);
                    }

                    @Override
                    public void clickCancel() {

                    }
                });
    }


    private void delCreditCard(BindCard bindCard) {
        HttpParams httpParams = OkClient.getParamsInstance().getParams();
        httpParams.put("3", "190520");
        httpParams.put("8", bindCard.getBANK_ACCOUNT());
        httpParams.put("42", bindCard.getMERCHANT_NO());
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
                    srlRefresh.autoRefresh();
                }
            }
        });
    }


    /**
     * 获取信用卡列表
     */
    private void requestData() {
        loadingDialog.show();
        HttpParams httpParams = OkClient.getParamsInstance().getParams();
        httpParams.put("3", "190932");
        httpParams.put("42", med ? mMedBean.getMERCHANT_NO() : getMerNo());
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
                srlRefresh.finishRefresh();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                super.onSuccess(response);
                loadingDialog.dismiss();
                srlRefresh.finishRefresh();
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    mList = JSONArray.parseArray(model.getStr57(), BindCard.class);
                    mBankCardListAdapter.setNewData(mList);

                    if (med) {
                        tvCardNum.setText("信用卡张数：" + mList.size());
                    }


                }
            }
        });
    }


    @Subscribe
    public void onEvent(BankCardEvent event) {
        srlRefresh.autoRefresh();
    }

    @OnClick({R.id.iv_back, R.id.iv_right, R.id.btn_add, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ViewUtils.overridePendingTransitionBack(context);
                break;
            case R.id.tv_right:
                String merchantCnName = StorageCustomerInfo02Util.getInfo("merchantCnName", context);
                String idCardNumber = StorageCustomerInfo02Util.getInfo("idCardNumber", context);
                Intent intent = new Intent(context, AddBankCardActivity.class);
                intent.putExtra("name", med ? mMedBean.getMERCHANT_CN_NAME() : merchantCnName);
                intent.putExtra("idCard", med ? mMedBean.getID_CARD_NUMBER() : idCardNumber);
                intent.putExtra("merchantId", med ? mMedBean.getID() : getMerId());
                startActivity(intent);
                break;
        }
    }
}
