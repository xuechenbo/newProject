package com.kotyj.com.activity.wkyk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kotyj.com.MyApplication;
import com.kotyj.com.R;
import com.kotyj.com.adapter.ChooseCardAccountAdapter;
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
import com.kotyj.com.utils.LogUtils;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.Utils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.viewone.NewSimpleCalendarDialogFragment;
import com.kotyj.com.viewone.dialog.PlanTotalPriceDialog;
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

public class MakeNewDesignActivity extends BaseActivity {
    private static final int REQUSRT_CHANNEL = 0x02;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.iv_bank_icon)
    ImageView ivBankIcon;
    @BindView(R.id.tv_bank_name)
    TextView tvBankName;
    @BindView(R.id.ll_bank_name)
    LinearLayout llBankName;
    @BindView(R.id.tv_bank_account)
    TextView tvBankAccount;
    @BindView(R.id.tv_userName)
    TextView tvUserName;
    @BindView(R.id.tv_limit)
    TextView tvLimit;
    @BindView(R.id.tv_billDay)
    TextView tvBillDay;
    @BindView(R.id.tv_payDay)
    TextView tvPayDay;
    @BindView(R.id.bind_item)
    LinearLayout bindItem;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.rl_channel)
    RelativeLayout rlChannel;
    @BindView(R.id.rbt_channel_small)
    RadioButton rbtChannelSmall;
    @BindView(R.id.rbt_channel_big)
    RadioButton rbtChannelBig;
    @BindView(R.id.rbt_channel_all)
    RadioButton rbtChannelAll;
    @BindView(R.id.rg_channel)
    RadioGroup rgChannel;

    @BindView(R.id.view_close)
    View viewClose;
    @BindView(R.id.et_inputPayAmount)
    EditText etInputPayAmount;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.spinner_pay)
    Spinner spinnerPay;
    @BindView(R.id.ll_pay_model)
    LinearLayout llPayModel;
    @BindView(R.id.spinner_pay_number)
    Spinner spinnerPayNumber;
    @BindView(R.id.ll_pay_number)
    LinearLayout llPayNumber;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_total_title)
    TextView tvTotalTitle;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.iv_total_price_tip)
    ImageView ivTotalPriceTip;
    @BindView(R.id.btn_calculate)
    TextView btnCalculate;
    @BindView(R.id.btn_previewPlan)
    Button btnPreviewPlan;
    @BindView(R.id.ll_make_design)
    LinearLayout llMakeDesign;
    private boolean zhia;
    private BindCard mBindCard;
    private String bankAccount, limit, billDay, payDay, acqcode, isLuodi, isZiXuan;
    private String bindId;
    private ChooseCardAccountAdapter mChooseCardAccountAdapter;
    private List<ChannelBean.Channel> mChannelList = new ArrayList<>();
    private ChannelBean.Channel mSelChannel, oldSelChannel;
    private int selPosition = -1;
    private boolean channels;
    private String channelType = "10B";
    private List<String> list = new ArrayList<>();
    private List<String> salePayList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> salePayAdapter;
    private NewSimpleCalendarDialogFragment mNewSimpleCalendarDialogFragment;
    private List<Date> selectedDates = new ArrayList<>();
    private String cityId, oldCityId;
    private int payAmountPerDay = 1, oldPayAmountPerDay;
    private int salePayModel = 1, oldSalePayModel;
    private boolean isCalculate = false;
    private HashMap<String, Area> area;
    private PreviewPlanModel previewPlanModel = new PreviewPlanModel();
    private String startDate, endDate, dayPeriod, oldDayPeriod, oldAmt;
    private String industy_Json;
    private JSONArray industyInfos;

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
        return R.layout.activity_make_design_2;
    }

    @Override
    public void initData() {
        tvTitle.setText("制定计划000");
        zhia = getIntent().getBooleanExtra("zhia", false);
        mBindCard = (BindCard) getIntent().getSerializableExtra("bindCard");
        //TODO 多通道
        channels = getIntent().getBooleanExtra("channels", false);
        bankAccount = mBindCard.getBANK_ACCOUNT();
        limit = mBindCard.getLIMIT_MONEY();
        billDay = mBindCard.getBILL_DAY();
        payDay = String.valueOf(mBindCard.getREPAYMENT_DAY());
        bindId = mBindCard.getID();

        String name = StorageCustomerInfo02Util.getInfo("bankAccountName", this);
        tvUserName.setText(name);

        String bankAccount = mBindCard.getBANK_ACCOUNT();
        if (!StringUtil.isEmpty(bankAccount) && bankAccount.length() > 4) {
            String bankNum1 = bankAccount.substring(0, 4);
            String bankNum2 = bankAccount.substring(bankAccount.length() - 4, bankAccount.length());
            tvBankAccount.setText(bankNum1 + " **** **** " + bankNum2);
        }
        if (channels) {
            rgChannel.setVisibility(View.VISIBLE);
        }
        tvBankName.setText(MyApplication.bankCodeList.get(mBindCard.getBANK_NAME()));
        Utils.initBankCodeColorIcon(mBindCard.getBANK_NAME(), ivBankIcon, this);
        tvLimit.setText(limit);
        tvBillDay.setText(billDay);
        tvPayDay.setText(payDay);
        initDesign();
        initAccount();
        initListener();
    }

    private void initDesign() {
        tvDate.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_MOVE) {
                    //按下或滑动时请求父节点不拦截子节点
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //抬起时请求父节点拦截子节点
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });

        list = new ArrayList<>();
        list.add("1次/日");
        list.add("2次/日");
        list.add("3次/日");

        adapter = new ArrayAdapter<>(context, R.layout.spinner_layout, list);
        adapter.setDropDownViewResource(R.layout.spiner_drop_down_style);
        spinnerPayNumber.setAdapter(adapter);

        salePayList = new ArrayList<>();
        salePayList.add("消一还一");
        salePayList.add("消二还一");
        salePayAdapter = new ArrayAdapter<>(context, R.layout.spinner_layout, salePayList);
        salePayAdapter.setDropDownViewResource(R.layout.spiner_drop_down_style);
        spinnerPay.setAdapter(salePayAdapter);


        if (zhia) {
            llPayModel.setVisibility(View.GONE);
            llPayNumber.setVisibility(View.GONE);
            tvTotalTitle.setText("手续费小计");
            btnCalculate.setText("计算手续费");
        }
    }

    private void initListener() {
        mChooseCardAccountAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.chb_select:
                        if (channels) {
                            mChannelList.get(position).setCheck(!mChannelList.get(position).isCheck());
                            mChooseCardAccountAdapter.notifyDataSetChanged();
                        } else {
                            if (selPosition != position) {
                                selPosition = position;
                                for (int i = 0; i < mChannelList.size(); i++) {
                                    mChannelList.get(i).setCheck(false);
                                }
                                mChannelList.get(position).setCheck(true);
                                mChooseCardAccountAdapter.notifyDataSetChanged();
                                mSelChannel = mChannelList.get(position);
                            }
                        }
                        break;
                    case R.id.btn_report:
                        // TODO: 2019/10/12 去绑卡
                        Intent intent1 = new Intent(context, BindCardActivity.class);
                        intent1.putExtra("bindCard", mBindCard);
                        intent1.putExtra("type", mChannelList.get(position).getAcqcode());
                        intent1.putExtra("category", "1");
                        startActivityForResult(intent1, REQUSRT_CHANNEL);
                        break;
                    case R.id.tv_limit_des:
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putExtra("title", mChannelList.get(position).getChannelName());
                        intent.putExtra("url", "http://120.78.81.147/icon/icon_channel_" + mChannelList.get(position).getAcqcode() + ".png");
                        startActivity(intent);
                        break;
                }
            }
        });
        rbtChannelSmall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    channelType = "10B";
                    mChannelList.clear();
                    loadChannelData();
                }
            }
        });

        rbtChannelBig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    channelType = "10A";
                    mChannelList.clear();
                    loadChannelData();
                }
            }
        });

        rbtChannelAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    channelType = "";
                    mChannelList.clear();
                    loadChannelData();
                }
            }
        });
        spinnerPayNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                payAmountPerDay = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerPay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                salePayModel = i + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        etInputPayAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkInfoIfComplete();
            }
        });
    }

    /**
     * 实时判断数据是否输入完整
     *
     * @return
     */
    private boolean checkInfoIfComplete() {
        btnCalculate.setClickable(false);
        String inputPayAmount = etInputPayAmount.getText().toString().trim();
        if (TextUtils.isEmpty(inputPayAmount)) {
            btnCalculate.setBackgroundResource(R.drawable.shape_solid_gray_right_corner_5);
            btnCalculate.setTextColor(ContextCompat.getColor(context, R.color.text_color));
            return false;
        }
        if (selectedDates.size() == 0) {
            btnCalculate.setBackgroundResource(R.drawable.shape_solid_gray_right_corner_5);
            btnCalculate.setTextColor(ContextCompat.getColor(context, R.color.text_color));
            return false;
        }
        if (area == null) {
            btnCalculate.setBackgroundResource(R.drawable.shape_solid_gray_right_corner_5);
            btnCalculate.setTextColor(ContextCompat.getColor(context, R.color.text_color));
            return false;
        }
        btnCalculate.setClickable(true);
//        btnCalculate.setBackgroundResource(R.drawable.shape_solid_themecolor_25);
        btnCalculate.setTextColor(ContextCompat.getColor(context, R.color.btn_text_color));
        return true;
    }

    private void initAccount() {
        mChooseCardAccountAdapter = new ChooseCardAccountAdapter(mChannelList);
        rvList.setLayoutManager(new LinearLayoutManager(context));
        mChooseCardAccountAdapter.bindToRecyclerView(rvList);
        mChooseCardAccountAdapter.addFooterView(getFooterView());
        mChooseCardAccountAdapter.setEmptyView(R.layout.layout_empty, rlChannel);
        loadChannelData();
    }

    private View getFooterView() {

        TextView tvAdd = new TextView(context);
        tvAdd.setText("提交计划预览");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 120);
        params.setMargins(10, 50, 10, 0);
        tvAdd.setGravity(Gravity.CENTER);
        tvAdd.setBackgroundResource(R.drawable.shape_solid_background_corner_5);
        tvAdd.setTextColor(ContextCompat.getColor(context, R.color.btn_text_color));
        tvAdd.setLayoutParams(params);
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2019/10/12 显示弹框
                if (channels) {
                    StringBuilder channelNameBuilder = new StringBuilder();
                    StringBuilder acqCodeBuilder = new StringBuilder();
                    for (ChannelBean.Channel channel : mChannelList) {
                        if (channel.isCheck()) {
                            if (channelNameBuilder.length() > 1) {
                                channelNameBuilder.append(",");
                            }
                            channelNameBuilder.append(channel.getChannelName());
                            if (acqCodeBuilder.length() > 1) {
                                acqCodeBuilder.append(",");
                            }
                            acqCodeBuilder.append(channel.getAcqcode());
                        }
                    }
                    String[] chanel = channelNameBuilder.toString().split(",");
                    if (chanel.length < 2) {
                        ViewUtils.makeToast(context, "请至少选择两条通道", 500);
                        return;
                    }
                    if (mSelChannel == null) {
                        mSelChannel = new ChannelBean.Channel();
                    }
                    mSelChannel.setChannelName(channelNameBuilder.toString());
                    mSelChannel.setAcqcode(acqCodeBuilder.toString());
                    mSelChannel.setChannels(true);
                    mSelChannel.setChannelType(StringUtil.isEmpty(channelType) ? "10B" : "10A");
                } else {
                    if (mSelChannel == null) {
                        ViewUtils.makeToast(context, "请至少选择一个通道", 500);
                        return;
                    }
                }
                llMakeDesign.setVisibility(View.VISIBLE);
            }
        });
        return tvAdd;
    }

    private void loadChannelData() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", channels ? "390014" : "390013");
        httpParams.put("42", channels ? getMerNo() : mBindCard.getMERCHANT_NO());
        if (channels) {
            httpParams.put("41", "10A");
            if (!StringUtil.isEmpty(channelType)) {
                httpParams.put("45", channelType);
            }
        }
        httpParams.put("43", zhia ? "QYK" : "YK");
        httpParams.put("44", mBindCard.getID());
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
                    ChannelBean channelBean = JSONObject.parseObject(model.getStr57(), ChannelBean.class);
                    List<ChannelBean.Channel> list = channelBean.getAcqData();
                    mChannelList.addAll(list);
                    mChooseCardAccountAdapter.setNewData(mChannelList);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (llMakeDesign.getVisibility() == View.VISIBLE) {
                llMakeDesign.setVisibility(View.GONE);
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return false;
    }

    /**
     * 提交计划后，自动关闭页面
     *
     * @param event
     */
    @Subscribe
    public void onEvent(PlanCloseEvent event) {
        ViewUtils.overridePendingTransitionBack(context);
    }

    @OnClick({R.id.iv_back, R.id.view_close, R.id.ll_dialog_make_design, R.id.tv_date, R.id.tv_city, R.id.btn_calculate, R.id.btn_previewPlan, R.id.iv_total_price_tip})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.iv_back:
                ViewUtils.overridePendingTransitionBack(context);
                break;
            case R.id.view_close:

                llMakeDesign.setVisibility(View.GONE);
                break;
            case R.id.ll_dialog_make_design:
                break;
            case R.id.tv_date:
                if (CommonUtils.isFastDoubleClick2()) {
                    return;
                }
                KeyBoardUtils.hideKeyboard(etInputPayAmount);
                mNewSimpleCalendarDialogFragment = NewSimpleCalendarDialogFragment.getInstance(zhia);
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
                        checkInfoIfComplete();
                    }
                });

                mNewSimpleCalendarDialogFragment.show(getSupportFragmentManager(), "start");
                break;
            case R.id.tv_city:
                if (CommonUtils.isFastDoubleClick2()) {
                    return;
                }
                startActivityForResult(new Intent(context, ChoiceAreaActivity.class)
                                .putExtra("acqCode", mSelChannel.getAcqcode())
                                .putExtra("onlyP_C", true)
                                .putExtra("zhia", zhia)
                                .putExtra("bindid", mBindCard.getID())
                        , 1);
                break;

            case R.id.btn_calculate:
                //TODO 计算周转金
                if (CommonUtils.isFastDoubleClick2()) {
                    return;
                }
                if (!checkInfoIfComplete()) {
                    return;
                }
                long interval = DateUtil.getDayBetweenTWodDate(startDate, endDate);
                if (interval > 25) {
                    ViewUtils.makeToast(context, "还款周期不能超过25天", 1000);
                    return;
                }
                if (zhia) {
                    calculateQYKMoney();
                } else {
                    calculateYKMoney();
                }
                break;
            case R.id.btn_previewPlan:
                if (CommonUtils.isFastDoubleClick2()) {
                    return;
                }
                goPreviewPlan();
                break;
            case R.id.iv_total_price_tip:
                if (CommonUtils.isFastDoubleClick2()) {
                    return;
                }
                // : 2019/10/14 周转金提示
                PlanTotalPriceDialog planTotalPriceDialog = PlanTotalPriceDialog.getInstance(previewPlanModel, zhia ? false : true);
                planTotalPriceDialog.show(getSupportFragmentManager(), "price");
                break;
            default:
                break;
        }
    }

    /**
     * 计算全额还手续费
     */
    private void calculateQYKMoney() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "390048");
        httpParams.put("7", payAmountPerDay + "");
        httpParams.put("8", etInputPayAmount.getText().toString());

        httpParams.put("10", dayPeriod);
        httpParams.put("11", mBindCard.getBANK_ACCOUNT());
        httpParams.put("12", mBindCard.getID());
        httpParams.put("43", mSelChannel.getAcqcode());
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
                    ivTotalPriceTip.setVisibility(View.VISIBLE);
                    oldCityId = cityId;
                    oldSelChannel = mSelChannel;
                    btnPreviewPlan.setVisibility(View.VISIBLE);
                    oldAmt = etInputPayAmount.getText().toString();
                    oldDayPeriod = dayPeriod;
                    oldPayAmountPerDay = payAmountPerDay;
                    oldSalePayModel = salePayModel;
                    isCalculate = true;
                    BigDecimal fee = CommonUtils.formatNewWithScale(model.getStr9(), 2);
                    BigDecimal timesFee = CommonUtils.formatNewWithScale(model.getStr7(), 2);

                    String dates = model.getStr10();
                    if (dates.contains(",")) {
                        //多日
                        startDate = dates.substring(0, dates.indexOf(","));
                        endDate = dates.substring(dates.lastIndexOf(",") + 1);
                    } else {//单日
                        startDate = dates;
                        endDate = dates;
                    }
                    BigDecimal totalFee = fee.add(timesFee);
//                    tvPendingPayAmount.setText(String.valueOf(totalFee));
                    tvTotalPrice.setText(String.valueOf(totalFee));

                    previewPlanModel.setWorkingFund("0");
                    previewPlanModel.setTimesFee(timesFee.toString());
                    previewPlanModel.setFee(fee.toString());
                    previewPlanModel.setStartDate(startDate);
                    previewPlanModel.setEndDate(endDate);
                    previewPlanModel.setDayTimes(payAmountPerDay + "");
                    previewPlanModel.setAcqcode(mSelChannel.getAcqcode());
                    previewPlanModel.setF57(model.getStr57());
                    previewPlanModel.setRepayAmount(CommonUtils.formatNewWithScale(etInputPayAmount.getText().toString(), 2).toString());
                    previewPlanModel.setTotalServiceFee(totalFee.toString());
                }
            }
        });
    }

    /**
     * 计算周转金
     */
    private void calculateYKMoney() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", mSelChannel.isChannels() ? "393000" : "193000");
        httpParams.put("7", payAmountPerDay + "");
        httpParams.put("8", etInputPayAmount.getText().toString());
        httpParams.put("9", salePayModel);
        httpParams.put("10", dayPeriod);
        httpParams.put("11", mBindCard.getBANK_ACCOUNT());
        httpParams.put("35", area.get("city").getId());
        httpParams.put("36", area.get("province").getId());
        if (mSelChannel.isChannels()) {
            httpParams.put("44", mSelChannel.getChannelType());
        }
        httpParams.put("12", mBindCard.getID());
        httpParams.put("43", mSelChannel.getAcqcode());
        httpParams.put("42", channels ? getMerNo() : mBindCard.getMERCHANT_NO());
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
                    ivTotalPriceTip.setVisibility(View.VISIBLE);
                    oldCityId = cityId;
                    oldSelChannel = mSelChannel;
                    btnPreviewPlan.setVisibility(View.VISIBLE);
                    oldAmt = etInputPayAmount.getText().toString();
                    oldDayPeriod = dayPeriod;
                    oldPayAmountPerDay = payAmountPerDay;
                    oldSalePayModel = salePayModel;
                    isCalculate = true;
                    BigDecimal fee = CommonUtils.formatNewWithScale(model.getStr9(), 2);
                    BigDecimal timesFee = CommonUtils.formatNewWithScale(model.getStr7(), 2);
                    //需要预留的金额
                    BigDecimal workingFund = CommonUtils.formatNewWithScale(model.getStr40(), 2);
                    String dates = model.getStr10();
                    if (dates.contains(",")) {
                        //多日
                        startDate = dates.substring(0, dates.indexOf(","));
                        endDate = dates.substring(dates.lastIndexOf(",") + 1);
                    } else {//单日
                        startDate = dates;
                        endDate = dates;
                    }
                    BigDecimal totalFee = workingFund.add(fee).add(timesFee);
                    tvTotalPrice.setText(String.valueOf(totalFee));
                    previewPlanModel.setWorkingFund(workingFund.toString());
                    previewPlanModel.setTimesFee(timesFee.toString());
                    previewPlanModel.setFee(fee.toString());
                    previewPlanModel.setStartDate(startDate);
                    previewPlanModel.setEndDate(endDate);
                    previewPlanModel.setDayTimes(payAmountPerDay + "");
                    previewPlanModel.setAcqcode(mSelChannel.getAcqcode());
                    previewPlanModel.setF57(model.getStr57());
                    previewPlanModel.setRepayAmount(CommonUtils.formatNewWithScale(etInputPayAmount.getText().toString(), 2).toString());
                    previewPlanModel.setTotalFee(totalFee.toString());
                    previewPlanModel.setTotalServiceFee(fee.add(timesFee).toString());
                }
            }
        });
    }

    /**
     * 进入计划预览页面
     */
    private void goPreviewPlan() {
        if (!checkInfoIfComplete() || !isCalculate) {
            ViewUtils.makeToast(context, zhia ? "请先计算手续费" : "请先计算周转金", 1500);
            return;
        }
        if (oldSelChannel != null) {
            if (!oldSelChannel.getAcqcode().equals(mSelChannel.getAcqcode())) {
                ViewUtils.makeToast(context, "通道已修改，请重新计算" + (zhia ? "手续费" : "周转金"), 1500);
                return;
            }
        }
        if (!checkDateChange()) {
            ViewUtils.makeToast(context, "数据已修改，请重新计算" + (zhia ? "手续费" : "周转金"), 1500);
            return;
        }
        if (!StringUtil.isEmpty(cityId) && !cityId.equals(oldCityId)) {
            ViewUtils.makeToast(context, "数据已修改，" + (zhia ? "请先计算手续费" : "请先计算周转金"), 1500);
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
        previewPlanModel.setChannelName(mSelChannel.getChannelName());
        previewPlanModel.setZhia(zhia);
        previewPlanModel.setChannels(mSelChannel.isChannels());
        previewPlanModel.setChannelType(mSelChannel.getChannelType());
//        Intent intent = new Intent(context, PreviewPlanActivity.class);
//        intent.putExtra("previewModel", previewPlanModel);
//        intent.putExtra("bindCard", mBindCard);
//        startActivity(intent);

    }

    /**
     * 检查内容是否有变动
     */
    private boolean checkDateChange() {

        if (TextUtils.equals(oldAmt, etInputPayAmount.getText().toString())
                && TextUtils.equals(oldDayPeriod, dayPeriod)
                ) {
            if (!zhia) {
                isCalculate = (oldPayAmountPerDay == payAmountPerDay && oldSalePayModel == salePayModel);
                return isCalculate;
            } else {
                return true;
            }
        }
        isCalculate = false;
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUSRT_CHANNEL && resultCode == RESULT_OK) {
            mChannelList.clear();
            loadChannelData();
        }

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            area = (HashMap<String, Area>) data.getSerializableExtra("data");
            industy_Json = data.getStringExtra("industry_JSON");
            if (!TextUtils.isEmpty(industy_Json)) {
                industyInfos = JSONArray.parseArray(industy_Json);
            }
            cityId = area.get("city").getId();
            tvCity.setText(String.format("%s-%s", area.get("province").getDivisionName(), area.get("city").getDivisionName()));
            checkInfoIfComplete();
        }
    }


}
