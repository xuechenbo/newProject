package com.kotyj.com.activity.wkyk.newwkyk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kotyj.com.R;
import com.kotyj.com.activity.wkyk.ChoiceAreaActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.callback.CalendarSelectCallback;
import com.kotyj.com.event.PlanCloseEvent;
import com.kotyj.com.model.Area;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.BindCard;
import com.kotyj.com.model.ChannelBean;
import com.kotyj.com.model.PreviewPlanModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.DateUtil;
import com.kotyj.com.utils.KeyBoardUtils;
import com.kotyj.com.utils.Utils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.viewone.NewSimpleCalendarDialogFragment;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeDesignActivity extends BaseActivity {
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
    @BindView(R.id.et_inputPayAmount)
    EditText etInputPayAmount;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.spinner_pay)
    Spinner spinnerPay;
    @BindView(R.id.spinner_pay_number)
    Spinner spinnerPayNumber;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_total_title)
    TextView tvTotalTitle;
    private boolean isQYK;
    private BindCard mBindCard;
    private boolean Ischannels;
    private List<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private NewSimpleCalendarDialogFragment mNewSimpleCalendarDialogFragment;
    private List<Date> selectedDates = new ArrayList<>();
    private String startDate, endDate, dayPeriod = "";
    private int payAmountPerDay = 1;
    private ChannelBean.Channel channel;
    private String industy_Json;
    private HashMap<String, Area> area;
    private PreviewPlanModel previewPlanModel = new PreviewPlanModel();
    private int typ1 = -1;

    private boolean isCosFlag = false;

    @Override
    public int initLayout() {
        return R.layout.activity_make_design1;
    }

    @Override
    public void initData() {
        tvTitle.setText("制定计划");

        isQYK = getIntent().getBooleanExtra("isQYK", false);
        mBindCard = (BindCard) getIntent().getSerializableExtra("bindCard");
        channel = (ChannelBean.Channel) getIntent().getSerializableExtra("channel");
        //TODO 多通道
        Ischannels = getIntent().getBooleanExtra("isChannels", false);

        tvTotalTitle.setText(isQYK ? "手续费" : "预留金额");

        tvDayNum.setText(mBindCard.getDay());

        Utils.initBankCodeColorIcon(mBindCard.getBANK_NAME(), ivBankIcon, context);
        tvBankName.setText(mBindCard.getShort_cn_name());
        String banNum = mBindCard.getBANK_ACCOUNT();
        if (banNum.length() > 4) {
            String bankNum1 = banNum.substring(0, 4);
            String bankNum2 = banNum.substring(banNum.length() - 4, banNum.length());
            tvBankAccount.setText(bankNum1 + " **** **** " + bankNum2);
        }
        tvLimit.setText(mBindCard.getLIMIT_MONEY());
        tvPayDay.setText("-" + mBindCard.getREPAYMENT_DAY());
        tvBillDay.setText(mBindCard.getBILL_DAY());

        list = new ArrayList<>();
        list.add("每天一笔");
        list.add("每天二笔");
        list.add("每天三笔");
        adapter = new ArrayAdapter<>(context, R.layout.spinner_layout, list);
        adapter.setDropDownViewResource(R.layout.spiner_drop_down_style);
        spinnerPayNumber.setAdapter(adapter);


        initListener();
    }

    private void initListener() {
        spinnerPayNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(R.color.theme_font_white));
                payAmountPerDay = position + 1;
                typ1++;
                if (typ1 != 0) {
                    makePlan();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etInputPayAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();

                if (!TextUtils.isEmpty(string)) {
                    if (Integer.parseInt(string) >= 500) {
                        makePlan();
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        EventBus.getDefault().register(context);
    }

    @OnClick({R.id.iv_back, R.id.tv_date, R.id.tv_city, R.id.btn_previewPlan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_date:
                if (CommonUtils.isFastDoubleClick2()) {
                    return;
                }
                KeyBoardUtils.hideKeyboard(etInputPayAmount);
                mNewSimpleCalendarDialogFragment = NewSimpleCalendarDialogFragment.getInstance(isQYK);
                mNewSimpleCalendarDialogFragment.setCalendarSelectCallback(new CalendarSelectCallback() {
                    @Override
                    public void success(List<Date> dateList, String day) {
                        selectedDates = dateList;
                        if (selectedDates.size() > 0) {
                            startDate = DateUtil.formateDateTOYMD(selectedDates.get(0));
                            endDate = DateUtil.formateDateTOYMD(selectedDates.get(selectedDates.size() - 1));
                        }
                        StringBuilder sb_days = new StringBuilder();
                        for (Date date : selectedDates) {
                            sb_days.append(DateUtil.formateDateTOYMD(date)).append(",");
                        }
                        dayPeriod = sb_days.substring(0, sb_days.length() - 1);
                        tvDate.setText(day);
                        makePlan();
                    }
                });
                mNewSimpleCalendarDialogFragment.show(getSupportFragmentManager(), "start");
                break;
            case R.id.tv_city:
                if (CommonUtils.isFastDoubleClick2()) {
                    return;
                }
                //选中的商户号
                startActivityForResult(new Intent(context, ChoiceAreaActivity.class)
                                .putExtra("acqCode", channel.getAcqcode())
                                .putExtra("onlyP_C", true)
                                .putExtra("isQYK", isQYK)
                                .putExtra("bindid", mBindCard.getID())
                        , 1);
                break;
            case R.id.btn_previewPlan:
                if (CommonUtils.isFastDoubleClick2()) {
                    return;
                }
                if (!isCosFlag) {
                    ViewUtils.makeToast(context, "请重新计算", 1200);
                    return;
                }
                if (area == null) {
                    HashMap<String, Area> map = new HashMap<>();
                    Area area = new Area();
                    area.setId("-1");
                    area.setDivisionName("");
                    map.put("province", area);
                    previewPlanModel.setArea(map);
                } else {
                    previewPlanModel.setArea(area);
                    previewPlanModel.setGround(true);
                }
                previewPlanModel.setIndustryJson(industy_Json);
                previewPlanModel.setIsLuodi("1");
                previewPlanModel.setIsZiXuan("1");
                previewPlanModel.setChannelName(channel.getChannelName());
                previewPlanModel.setZhia(isQYK);

                Intent intent = new Intent(context, PreviewDetailActivity.class);
                intent.putExtra("previewModel", previewPlanModel);
                intent.putExtra("bindCard", mBindCard);
                intent.putExtra("isChannels", Ischannels);
                startActivity(intent);
                break;
        }
    }


    void makePlan() {
        String etMoney = etInputPayAmount.getText().toString();
        if (etMoney.isEmpty()) {
            ViewUtils.makeToast(context, "账单金额为空", 1200);
            return;
        }
        long payAmount = Long.parseLong(etMoney);
        if (payAmount < 500) {
            ViewUtils.makeToast(context, "还款金额不得小于500", 1000);
            return;
        }
        if (selectedDates.size() == 0) {
            ViewUtils.makeToast(context, "请选择时间", 1200);
            return;
        }
        if (area == null) {
            ViewUtils.makeToast(context, "请选择地区", 1200);
            return;
        }
        goPreviewPlan();
    }


    private void goPreviewPlan() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", isQYK ? "390048" : (Ischannels ? "393000" : "193000"));
        httpParams.put("7", payAmountPerDay + "");
        httpParams.put("8", etInputPayAmount.getText().toString());
        httpParams.put("9", "1");
        httpParams.put("10", dayPeriod);
        httpParams.put("11", mBindCard.getBANK_ACCOUNT());
        httpParams.put("35", area.get("city").getId());
        httpParams.put("36", area.get("province").getId());
        httpParams.put("12", mBindCard.getID());
        httpParams.put("43", channel.getAcqcode());
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
                    previewPlanModel.setTotalXfNum(model.getStr45());
                    //消费金额
                    previewPlanModel.setTotalXfMoney(model.getStr46());
                    //费率
                    previewPlanModel.setRate(model.getStr47());
                    //单笔费用
                    previewPlanModel.setSingMoney(model.getStr48());

                    //还款笔数
                    previewPlanModel.setHkBs(model.getStr44());


                    BigDecimal workingfund = CommonUtils.formatNewWithScale(model.getStr40(), 2);
                    //周转金
                    previewPlanModel.setWorkingFund(workingfund.toString());


                    //笔数费
                    previewPlanModel.setTimesFee(model.getStr7());

                    //消费手续费
                    previewPlanModel.setFee(model.getStr9());

                    BigDecimal totalFee = CommonUtils.formatNewWithScale(model.getStr41(), 2);

                    //总费用
                    previewPlanModel.setTotalFee(totalFee.toString());
                    previewPlanModel.setTotalServiceFee(totalFee.toString());


                    if (isQYK) {
                        tvTotalPrice.setText(model.getStr41());
                    } else {
                        tvTotalPrice.setText(model.getStr40());
                    }

                    //开始结束时间
                    String dates = model.getStr10();
                    if (dates.contains(",")) {
                        startDate = dates.substring(0, dates.indexOf(","));
                        endDate = dates.substring(dates.lastIndexOf(",") + 1);
                    } else {
                        startDate = dates;
                        endDate = dates;
                    }
                    previewPlanModel.setStartDate(startDate);
                    previewPlanModel.setEndDate(endDate);

                    //每日几笔
                    previewPlanModel.setDayTimes(payAmountPerDay + "");
                    //通道号
                    previewPlanModel.setAcqcode(channel.getAcqcode());
                    previewPlanModel.setF57(model.getStr57());//计划列表

                    BigDecimal repayMoney = CommonUtils.formatNewWithScale(etInputPayAmount.getText().toString(), 2);

                    //计划金额
                    previewPlanModel.setRepayAmount(repayMoney.toString());
                    //计划周期
                    previewPlanModel.setPlanTime(model.getStr10());
                    //TODO 是否是计算成功
                    isCosFlag = true;

                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            area = (HashMap<String, Area>) data.getSerializableExtra("data");
            industy_Json = data.getStringExtra("industry_JSON");

            tvCity.setText(String.format("%s-%s", area.get("province").getDivisionName(), area.get("city").getDivisionName()));
            makePlan();
        }
    }


    @Subscribe
    public void onEvent(PlanCloseEvent event) {
        ViewUtils.overridePendingTransitionBack(context);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
