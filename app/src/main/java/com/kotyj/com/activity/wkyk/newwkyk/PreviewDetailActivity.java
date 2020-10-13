package com.kotyj.com.activity.wkyk.newwkyk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kotyj.com.MyApplication;
import com.kotyj.com.R;
import com.kotyj.com.activity.login.PasswordChangeActivity;
import com.kotyj.com.activity.wkyk.ChoiceAreaActivity;
import com.kotyj.com.adapter.PreviewDetailPlanAdapter;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.BankCardEvent;
import com.kotyj.com.event.PlanCloseEvent;
import com.kotyj.com.model.Area;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.BindCard;
import com.kotyj.com.model.CardPlanList;
import com.kotyj.com.model.IndustryModel;
import com.kotyj.com.model.PlanModel;
import com.kotyj.com.model.PreviewPlanModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.DateUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.Utils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.viewone.dialog.PayPswDialog;
import com.kotyj.com.viewone.dialog.PlanTotalPriceDialog;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PreviewDetailActivity extends BaseActivity {


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
    @BindView(R.id.bind_item)
    LinearLayout bindItem;
    @BindView(R.id.tv_channel_name)
    TextView tvChannelName;
    @BindView(R.id.tv_limit)
    TextView tvLimit;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_total_service_fee)
    TextView tvTotalServiceFee;
    @BindView(R.id.lv_plan_detail)
    RecyclerView lvPlanDetail;
    @BindView(R.id.tv_hNum)
    TextView tvHNum;
    private PreviewPlanModel mPreviewPlanModel;
    private List<CardPlanList> mList = new ArrayList<>();
    private PreviewDetailPlanAdapter adapter;
    private BindCard mBindCard;
    private int mPosition;
    private int randomMax = 0;
    private boolean zhia;
    private boolean Ischannels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.act_preview_detail;
    }

    @Override
    public void initData() {
        tvTitle.setText("预览计划");
        initMsg();
        initListener();
    }


    private void initMsg() {
        Ischannels = getIntent().getBooleanExtra("isChannels", false);
        mPreviewPlanModel = (PreviewPlanModel) getIntent().getSerializableExtra("previewModel");
        mBindCard = (BindCard) getIntent().getSerializableExtra("bindCard");
        List<PlanModel> list = JSONArray.parseArray(mPreviewPlanModel.getF57(), PlanModel.class);
        zhia = mPreviewPlanModel.isZhia();
        lvPlanDetail.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PreviewDetailPlanAdapter(mList, zhia);
        adapter.bindToRecyclerView(lvPlanDetail);


        //计划金额
        tvLimit.setText(mPreviewPlanModel.getRepayAmount());


        //预留金额
        tvTotalPrice.setText(zhia ? mPreviewPlanModel.getTotalFee() : mPreviewPlanModel.getWorkingFund());

        //手续费
        tvTotalServiceFee.setText(mPreviewPlanModel.getTotalServiceFee());

        //还款笔数
        tvHNum.setText(mPreviewPlanModel.getHkBs());

        String bankAccount = mBindCard.getBANK_ACCOUNT();
        if (!StringUtil.isEmpty(bankAccount) && bankAccount.length() > 4) {
            String bankNum1 = bankAccount.substring(0, 4);
            String bankNum2 = bankAccount.substring(bankAccount.length() - 4, bankAccount.length());
            tvBankAccount.setText(bankNum1 + " **** **** " + bankNum2);
        }
        Utils.initBankCodeColorIcon(mBindCard.getBANK_NAME(), ivBankIcon, this);
        tvBankName.setText(MyApplication.bankCodeList.get(mBindCard.getBANK_NAME()));
        tvChannelName.setText(mPreviewPlanModel.getChannelName());
        tvRepayCycle.setText(mPreviewPlanModel.getStartDate() + "至" + mPreviewPlanModel.getEndDate());

        initPlanList(list);
    }

    private void initPlanList(List<PlanModel> list) {
        mList.clear();
        tvLimit.setText(mPreviewPlanModel.getRepayAmount());
        //预留金额
        tvTotalPrice.setText(zhia ? mPreviewPlanModel.getTotalFee() : mPreviewPlanModel.getWorkingFund());
//        tvTotalPrice.setText(mPreviewPlanModel.getWorkingFund());
        //还款笔数
        tvHNum.setText(mPreviewPlanModel.getHkBs());
        //手续费
        tvTotalServiceFee.setText(mPreviewPlanModel.getTotalServiceFee());

        for (PlanModel model : list) {
            CardPlanList cardPlanList = new CardPlanList();
            cardPlanList.setPlanTime(DateUtil.parseDateToLong(model.getTime()));
            cardPlanList.setFromCard(String.valueOf(model.getCardNo()));
            cardPlanList.setToCard(model.getCardNo());
            cardPlanList.setFromBindId(model.getBindID());
            cardPlanList.setToBindId(model.getBindID());
            cardPlanList.setToMoney(CommonUtils.formatNewWithScale(String.valueOf(model.getMoney()), 2));
            cardPlanList.setFromMoney(CommonUtils.formatNewWithScale(String.valueOf(model.getMoney()), 2));
            cardPlanList.setType(model.getType());
            cardPlanList.setStatus(model.getStatus());
            cardPlanList.setGroupNum(String.valueOf(model.getGroupNum()));
            cardPlanList.setAcqCode(model.getAcqCode());
            //全额还
            if (mPreviewPlanModel.isZhia() && !TextUtils.isEmpty(mPreviewPlanModel.getIndustryJson())) {
                List<IndustryModel> industyList = JSONArray.parseArray(mPreviewPlanModel.getIndustryJson(), IndustryModel.class);
                randomMax = industyList.size();
                if (randomMax >= 1) {
                    Random random = new Random();
                    int randomInt = random.nextInt(randomMax);
                    IndustryModel industy = industyList.get(randomInt);
                    Area industyArea = new Area();
                    if (industy != null) {
                        industyArea.setId(industy.getAcqMerchantNo());
                        industyArea.setDivisionName(industy.getAcqMerchantName());
                        HashMap<String, Area> diquRandom = new HashMap<>();
                        diquRandom.put("province", mPreviewPlanModel.getArea().get("province"));
                        diquRandom.put("city", mPreviewPlanModel.getArea().get("city"));
                        diquRandom.put("mer", industyArea);
                        if (("sale".equals(cardPlanList.getType()) || "pay".equals(cardPlanList.getType())) && !"-1".equals(diquRandom.get("province").getId())) {
                            cardPlanList.setDiqu(diquRandom);
                        }
                    }
                }
            } else {
                Area industyArea = new Area();
                industyArea.setId(model.getCityIndustry());
                industyArea.setDivisionName(model.getIndustryName());
                HashMap<String, Area> diquRandom = new HashMap<>();
                diquRandom.put("province", mPreviewPlanModel.getArea().get("province"));
                diquRandom.put("city", mPreviewPlanModel.getArea().get("city"));
                diquRandom.put("mer", industyArea);
                if ("sale".equals(cardPlanList.getType()) && !"-1".equals(diquRandom.get("province").getId())) {
                    cardPlanList.setDiqu(diquRandom);
                }
            }
            mList.add(cardPlanList);
        }

        adapter.setNewData(mList);
    }

    private void initListener() {
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                CardPlanList plan = mList.get(position);
                switch (view.getId()) {
                    case R.id.ll_area:
                        if (zhia) {
                            Intent areaIntent = new Intent(context, ChoiceAreaActivity.class);
                            areaIntent.putExtra("bindid", mBindCard.getID());
                            areaIntent.putExtra("acqCode", mPreviewPlanModel.getAcqcode());
                            startActivityForResult(areaIntent, 997);
                        }
                        break;
                    case R.id.industry:
                        Intent intent = new Intent(context, ChoiceAreaActivity.class);
                        intent.putExtra("onlyMer", true);
                        intent.putExtra("area", plan.getDiqu());
                        intent.putExtra("bindid", mBindCard.getID());
                        intent.putExtra("acqCode", zhia ? mPreviewPlanModel.getAcqcode() : plan.getAcqCode());
                        startActivityForResult(intent, 998);
                        break;
                    default:
                        break;
                }
                mPosition = position;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 998 && resultCode == RESULT_OK) {
            HashMap<String, Area> newMer = (HashMap<String, Area>) data.getSerializableExtra("data");
            List<CardPlanList> mData = adapter.getData();
            mData.get(mPosition).setDiqu(newMer);
            TextView tv = (TextView) adapter.getViewByPosition(mPosition, R.id.industry);
            tv.setText(newMer.get("mer").getDivisionName());
            adapter.notifyItemChanged(mPosition);
        }
        if (requestCode == 997 && resultCode == RESULT_OK) {
            HashMap<String, Area> newArea = (HashMap<String, Area>) data.getSerializableExtra("data");
            List<CardPlanList> mData = adapter.getData();
            mData.get(mPosition).setDiqu(newArea);
            TextView area = (TextView) adapter.getViewByPosition(mPosition, R.id.tv_area);
            TextView industry = (TextView) adapter.getViewByPosition(mPosition, R.id.industry);
            industry.setText(newArea.get("mer").getDivisionName());
            area.setText(newArea.get("province").getDivisionName() + "-" + newArea.get("city").getDivisionName());
            adapter.notifyItemChanged(mPosition);
        }
    }

    @OnClick({R.id.iv_back, R.id.bt_submit_plan, R.id.bt_changePlan, R.id.tv_total_service_fee})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_changePlan:
                goPreviewPlan();
                break;
            case R.id.bt_submit_plan:
                if (CommonUtils.isFastDoubleClick2())
                    return;

                ShowPlanDetailsDialog intence = ShowPlanDetailsDialog.getIntence(mPreviewPlanModel);
                intence.setOnShowSuccessListener(new ShowPlanDetailsDialog.OnShowSuccess() {
                    @Override
                    public void onShowMsg() {
                        String payPsd = StorageCustomerInfo02Util.getInfo("payPsd", context);
                        if (payPsd.isEmpty() || payPsd.equals("null")) {
                            ViewUtils.showDialogStandard3(context, "您还未设置支付密码", "取消", "去设置", new ViewUtils.OnshowDialogStandard() {
                                @Override
                                public void clickOk() {
                                    startActivity(new Intent(context, PasswordChangeActivity.class)
                                            .putExtra("isLogin", false)
                                            .putExtra("title", "支付密码"));
                                }

                                @Override
                                public void clickCancel() {

                                }
                            }).show();
                        } else {
                            shoPwdDialog();
                        }
                    }
                });
                intence.show(getSupportFragmentManager(), "");

                break;
            case R.id.tv_total_service_fee:
                PlanTotalPriceDialog planTotalPriceDialog = PlanTotalPriceDialog.getInstance(mPreviewPlanModel, zhia ? false : true);
                planTotalPriceDialog.show(getSupportFragmentManager(), "totalfee");
                break;
        }
    }


    void shoPwdDialog() {
        final PayPswDialog intence1 = PayPswDialog.getIntence();
        intence1.setOnCorrectListener(new PayPswDialog.OnCorrectListener() {
            @Override
            public void onCorrect(String pwd) {
                //验证密码,之后提交计划
                if (zhia) {
                    submitQYKPlan(pwd);
                } else {
                    submitYKPlan(pwd);
                }
                intence1.dismiss();
            }
        });
        intence1.show(getSupportFragmentManager(), "");
    }

    /**
     * 提交全额还计划
     *
     * @param pwd
     */
    private void submitQYKPlan(String pwd) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "390049");
        httpParams.put("8", mPreviewPlanModel.getRepayAmount());
        httpParams.put("10", DateUtil.parseYMDToLong(mPreviewPlanModel.getStartDate()));
        httpParams.put("11", DateUtil.parseYMDToLong(mPreviewPlanModel.getEndDate()));
        httpParams.put("12", mPreviewPlanModel.getFee());
        httpParams.put("13", mPreviewPlanModel.getTimesFee());
        httpParams.put("42", mBindCard.getMERCHANT_NO());
        httpParams.put("44", mPreviewPlanModel.getChannelName());
        httpParams.put("45", pwd);
        httpParams.put("43", mPreviewPlanModel.getAcqcode());
        httpParams.put("57", mList.toString());
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
                    ViewUtils.makeToast(context, "提交成功", 500);
                    Intent intent = new Intent(context, LookPlanActivity.class);
                    intent.putExtra("bindCard", mBindCard);
                    startActivity(intent);
                    EventBus.getDefault().post(new PlanCloseEvent());
                    EventBus.getDefault().post(new BankCardEvent());
                    finish();
                }

            }
        });
    }

    /**
     * 提交养卡计划
     *
     * @param pwd
     */
    private void submitYKPlan(String pwd) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "190210");
        httpParams.put("8", mPreviewPlanModel.getRepayAmount());//计划金额
        httpParams.put("9", mPreviewPlanModel.getWorkingFund());//周周转金
        httpParams.put("10", DateUtil.parseYMDToLong(mPreviewPlanModel.getStartDate()));
        httpParams.put("11", DateUtil.parseYMDToLong(mPreviewPlanModel.getEndDate()));
        httpParams.put("12", mPreviewPlanModel.getFee());       //手续费
        httpParams.put("13", mPreviewPlanModel.getTimesFee());   //笔数费
        httpParams.put("14", mPreviewPlanModel.getDayTimes());  //每日几笔
        httpParams.put("16", mPreviewPlanModel.isGround() ? "2" : "1");
        httpParams.put("42", mPreviewPlanModel.isChannels() ? getMerNo() : mBindCard.getMERCHANT_NO());
        httpParams.put("44", mPreviewPlanModel.getChannelName());//通道名
        httpParams.put("43", mPreviewPlanModel.getAcqcode()); //通道号
        httpParams.put("45", pwd);
        httpParams.put("57", mList.toString());
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
                    ViewUtils.makeToast(context, "提交成功", 500);
                    Intent intent = new Intent(context, LookPlanActivity.class);
                    intent.putExtra("bindCard", mBindCard);
                    startActivity(intent);
                    EventBus.getDefault().post(new PlanCloseEvent());
                    EventBus.getDefault().post(new BankCardEvent());
                    finish();
                }

            }
        });
    }


    //TODO 换个方案
    private void goPreviewPlan() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", zhia ? "390048" : (Ischannels ? "393000" : "193000"));
        httpParams.put("7", mPreviewPlanModel.getDayTimes());
        String string = new BigDecimal(mPreviewPlanModel.getRepayAmount()).stripTrailingZeros().toPlainString();
        httpParams.put("8", string);
        httpParams.put("9", "1");
        httpParams.put("10", mPreviewPlanModel.getPlanTime());//时间
        httpParams.put("11", mBindCard.getBANK_ACCOUNT());
        httpParams.put("35", mPreviewPlanModel.getArea().get("city").getId());
        httpParams.put("36", mPreviewPlanModel.getArea().get("province").getId());
        httpParams.put("12", mBindCard.getID());
        httpParams.put("43", mPreviewPlanModel.getAcqcode());

        httpParams.put("42", mBindCard.getMERCHANT_NO());

        if (Ischannels) {
            httpParams.put("13", "10B");
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

                    //消费笔数
                    mPreviewPlanModel.setTotalXfNum(model.getStr45());

                    //消费金额
                    mPreviewPlanModel.setTotalXfMoney(model.getStr46());
                    //还款笔数
                    mPreviewPlanModel.setHkBs(model.getStr44());
                    //周转金
                    mPreviewPlanModel.setWorkingFund(model.getStr40());
                    //笔数费
                    mPreviewPlanModel.setTimesFee(model.getStr7());
                    //消费手续费
                    mPreviewPlanModel.setFee(model.getStr9());
                    //总费用
                    mPreviewPlanModel.setTotalFee(model.getStr41());

                    mPreviewPlanModel.setTotalServiceFee(model.getStr41());

                    List<PlanModel> list = JSONArray.parseArray(model.getStr57(), PlanModel.class);

                    initPlanList(list);

                }
            }
        });
    }
}
