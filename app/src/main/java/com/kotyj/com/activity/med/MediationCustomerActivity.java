package com.kotyj.com.activity.med;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kotyj.com.R;
import com.kotyj.com.activity.login.LoginNewActivity;
import com.kotyj.com.activity.med.maode.MediationBean;
import com.kotyj.com.activity.real.MedRealNameFirstActivity;
import com.kotyj.com.activity.wkyk.newwkyk.BankCardListActivity;
import com.kotyj.com.activity.wkyk.newwkyk.LookPlanActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.callback.StringCallback;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.PayResult;
import com.kotyj.com.model.WeiXinModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.LogUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.utils.wechat.PayListener;
import com.kotyj.com.utils.wechat.WechatPay;
import com.kotyj.com.viewone.dialog.VipBottomDialog;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.Integer.parseInt;

public class MediationCustomerActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.tv_sousuo)
    TextView tvSousuo;
    @BindView(R.id.tv_totalNum)
    TextView tvTotalNum;
    @BindView(R.id.tv_syNum)
    TextView tvSyNum;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    private LinearLayoutManager linearLayoutManager;
    List<MediationBean> mList;
    private MyAdapter myAdapter;


    @Override
    public int initLayout() {
        return R.layout.act_mediationcustomer;
    }


    @Override
    public void initData() {
        mList = new ArrayList<>();
        tvTitle.setText("中介还款");
        smartRefreshLayout.setEnableLoadmore(false);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        myAdapter = new MyAdapter(R.layout.item_customer, mList);
        myAdapter.bindToRecyclerView(recyclerView);
        Listener();
        requestData("");

    }

    @Override
    protected void onResume() {
        super.onResume();
        etSearch.setFocusable(false);
    }

    private void Listener() {
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                etSearch.setText("");
                etSearch.setFocusable(false);
                requestData("");
            }
        });


        myAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MediationBean mediationBean = mList.get(position);
                String freeze_status = mediationBean.getFREEZE_STATUS();
                if (freeze_status.equals("10B")) {
                    startActivity(new Intent(context, BankCardListActivity.class)
                            .putExtra("mMedBean", mList.get(position))
                            .putExtra("med", true)
                            .putExtra("title", "信用卡管理"));
                } else if (freeze_status.equals("10F")) {
                    ViewUtils.makeToast(context, "实名审核中", 1200);
                } else if (freeze_status.equals("10A") && !mediationBean.getBANK_ACCOUNT().isEmpty()) {
                    ViewUtils.makeToast(context, "实名审核中", 1200);
                } else if (freeze_status.equals("10C") || freeze_status.equals("10A")) {
                    ViewUtils.makeToast(context, "重新认证", 1200);
                    startActivity(new Intent(context, MedRealNameFirstActivity.class)
                            .putExtra("MediationBean", mediationBean)
                            .putExtra("update", true));
                } else if (freeze_status.equals("10D")) {
                    ViewUtils.makeToast(context, "重新审核中", 1200);
                } else {
                    ViewUtils.makeToast(context, "实名认证未通过", 1200);
                }
            }
        });

        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    tvSousuo.setVisibility(View.VISIBLE);
                } else {
                    // 此处为失去焦点时的处理内容
                    tvSousuo.setVisibility(View.GONE);
                    etSearch.setText("");
                }
            }
        });
        etSearch.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                etSearch.setFocusable(true);
                etSearch.setFocusableInTouchMode(true);
                etSearch.requestFocus();
                return false;
            }
        });
    }


    //获取列表
    private void requestData(String name) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "390012");
        if (!name.isEmpty()) {
            httpParams.put("43", name);  //搜索名字
        }
        httpParams.put("42", getMerId());
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onSuccess(Response<BaseEntity> response) {
                loadingDialog.dismiss();
                smartRefreshLayout.finishRefresh();
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    String str57 = model.getStr37();
                    mList = new Gson().fromJson(str57, new TypeToken<List<MediationBean>>() {
                    }.getType());
                    myAdapter.setNewData(mList);

                    //单个卡位
                    String str35 = model.getStr35();
                    StorageCustomerInfo02Util.putInfo(context, "singCardMoney", str35);

                    String str36 = model.getStr36();
                    try {
                        JSONArray jsonArray = new JSONArray(str36);
                        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                        String screensAllNumber = jsonObject.getString("screensAllNumber");
                        String screensNumber = jsonObject.getString("screensNumber");
                        tvTotalNum.setText("已使用卡位:" + (parseInt(screensAllNumber) - parseInt(screensNumber)));
                        tvSyNum.setText("剩余可绑卡位:" + screensNumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ViewUtils.makeToast(context, model.getStr39(), 1200);
                }
            }

            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
                smartRefreshLayout.finishRefresh();
            }
        });
    }


    @OnClick({R.id.iv_back, R.id.tv_sousuo, R.id.tv_add, R.id.tv_addAdmin, R.id.tv_totalPlan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_sousuo:
                String trim = etSearch.getText().toString().trim();
                if (trim.isEmpty()) {
                    ViewUtils.makeToast(context, "请输入内容", 1200);
                } else {
                    requestData(trim);
                }
                break;
            case R.id.tv_add:
                String singCardMoney = StorageCustomerInfo02Util.getInfo("singCardMoney", context);
                AddScreensDialog intence = AddScreensDialog.getIntence(singCardMoney);
                intence.setOnPayCardListener(new AddScreensDialog.OnPayCardListener() {
                    @Override
                    public void payNum(String num) {
                        showBottomDialog(num);
                    }
                });
                intence.show(getSupportFragmentManager(), "");

                break;
            case R.id.tv_addAdmin:
                startActivity(new Intent(context, MedRealNameFirstActivity.class));
                break;
            case R.id.tv_totalPlan:
                startActivity(new Intent(context, LookPlanActivity.class)
                        .putExtra("isMed", true));
                break;
        }
    }

    private void showBottomDialog(final String num) {
        final String singCardMoney = StorageCustomerInfo02Util.getInfo("singCardMoney", context);
        VipBottomDialog intence = VipBottomDialog.getIntence(CommonUtils.BigDeciMul(num, singCardMoney), "S", "1");
        intence.setStringCallback(new StringCallback() {
            @Override
            public void callback(String value) {
                loadPayData(value, "1", CommonUtils.BigDeciMul(num, singCardMoney));
            }
        });
        intence.show(getSupportFragmentManager(), "");
    }


    class MyAdapter extends BaseQuickAdapter<MediationBean, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<MediationBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, MediationBean item) {
            helper.setText(R.id.tv_name, item.getMERCHANT_CN_NAME())
                    .setText(R.id.tv_cardNum, "信用卡张数:" + item.getCardCount())
                    .setText(R.id.tv_planNum, "进行的计划:" + item.getPlanCount());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String mes) {
        if (mes.equals("clo_MeRealName")) {
            smartRefreshLayout.autoRefresh();
        }
    }

    private void loadPayData(final String type, String level, String money) {
        // : 2019/4/30 金额
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "890001");
        httpParams.put("5", CommonUtils.formatNewFen(money));
        httpParams.put("8", type);
        httpParams.put("43", level);
        httpParams.put("41", "S");
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
                    final String orderInfo = model.getStr42();
                    if ("z".equals(type)) {
                        Runnable payRunnable = new Runnable() {
                            @Override
                            public void run() {
                                PayTask alipay = new PayTask(context);
                                Map<String, String> result = alipay.payV2(orderInfo, true);
                                Message msg = new Message();
                                msg.what = SDK_PAY_FLAG;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            }
                        };
                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                    } else if ("w".equals(type)) {
                        WeiXinModel wechatModel = com.alibaba.fastjson.JSONObject.parseObject(orderInfo, WeiXinModel.class);
                        WechatPay.getInstance().startWeChatPay(wechatModel.getAppid(), wechatModel.getPartnerid(), wechatModel.getPrepayid(), wechatModel.getNoncestr(), wechatModel.getTimestamp(), wechatModel.getSign(), new PayListener() {
                            @Override
                            public void onPaySuccess() {
                                ViewUtils.makeToast2(context,
                                        "支付成功,请重新登录", 500, LoginNewActivity.class,
                                        "PAY");
                            }

                            @Override
                            public void onPayError(String resultStatus) {
                                ViewUtils.makeToast(context, "支付失败" + "\n" + resultStatus, 1000);
                            }

                            @Override
                            public void onPayCancel() {
                                ViewUtils.makeToast(context, "支付取消", 1000);
                            }
                        });
                    }
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    LogUtil.e("clx", payResult.toString());
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ViewUtils.makeToast2(context,
                                "支付成功,请重新登录", 500, LoginNewActivity.class,
                                "PAY");
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ViewUtils.makeToast(context, "支付取消", 500);
                    } else {
                        ViewUtils.makeToast(context, "支付失败", 500);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
    private static final int SDK_PAY_FLAG = 1;
}
