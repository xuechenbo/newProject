package com.kotyj.com.activity.shop;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.kotyj.com.R;
import com.kotyj.com.activity.mine.ServiceCenterActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.callback.StringCallback;
import com.kotyj.com.event.OrderPayEvent;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.OrderModel;
import com.kotyj.com.model.PayResult;
import com.kotyj.com.model.WeiXinModel;
import com.kotyj.com.utils.DateUtil;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.LogUtil;
import com.kotyj.com.utils.LogUtils;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.TimeUtils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.utils.wechat.PayListener;
import com.kotyj.com.utils.wechat.WechatPay;
import com.kotyj.com.viewone.CustomCountDownTimerView;
import com.kotyj.com.viewone.dialog.VipBottomDialog;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetailActivity extends BaseActivity {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_order_status)
    TextView tvOrderStatus;
    @BindView(R.id.iv_order_status)
    ImageView ivOrderStatus;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.iv_product)
    ImageView ivProduct;
    @BindView(R.id.tv_product_name)
    TextView tvProductName;
    @BindView(R.id.tv_specification)
    TextView tvSpecification;
    @BindView(R.id.tv_single_price)
    TextView tvSinglePrice;
    @BindView(R.id.tv_stock)
    TextView tvStock;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_delivery)
    TextView tvDelivery;
    @BindView(R.id.tv_order_no)
    TextView tvOrderNo;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.tv_money_2)
    TextView tvMoney2;
    @BindView(R.id.btn_purchase)
    Button btnPurchase;
    @BindView(R.id.custom_tv_count_down)
    CustomCountDownTimerView customTvCountDown;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    @BindView(R.id.tv_wul_name)
    TextView tvWulName;
    @BindView(R.id.tv_wuliu_id)
    TextView tvWuliuId;
    @BindView(R.id.tv_wl_copy)
    TextView tvWlCopy;
    @BindView(R.id.rl_wl)
    RelativeLayout rlWl;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.tv_dd_copy)
    TextView tvDdCopy;
    private OrderModel orderModel;
    private String orderId;

    @Override
    public int initLayout() {
        return R.layout.act_order_detail;
    }

    @Override
    public void initData() {
        tvTitle.setText("订单详情");
        orderId = getIntent().getStringExtra("orderId");

        loadDetail();
    }


    private void loadDetail() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "790107");
        httpParams.put("35", orderId);
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
                    tvName.setText(model.getStr31());
                    tvPhone.setText(model.getStr32());
                    tvAddress.setText(model.getStr30());
                    if (!StringUtil.isEmpty(model.getStr57())) {
                        orderModel = JSONObject.parseObject(model.getStr57(), OrderModel.class);
                        fillData();
                    }
                }
            }
        });
    }

    private void fillData() {


        GlideUtils.loadImage(context, orderModel.getGoodsImage(), ivProduct);
        tvOrderStatus.setText(orderModel.getStatusString());
        tvProductName.setText(orderModel.getGoodsName());
        tvSpecification.setText("规格：" + orderModel.getGoodsSpecification());
        tvSinglePrice.setText("￥" + orderModel.getGoodsPrice() + "+" + orderModel.getGoodPoint());
        tvStock.setText("x" + orderModel.getGoodsCount());
        tvTotalPrice.setText("商品合计:" + orderModel.getPay() + "元" + "+" + orderModel.getPoint() + "积分");
        if (StringUtil.isEqual(orderModel.getStatus(), "10A") || StringUtil.isEqual(orderModel.getStatus(), "10B") || StringUtil.isEqual(orderModel.getStatus(), "10C") || StringUtil.isEqual(orderModel.getStatus(), "10D")) {
            ivOrderStatus.setImageResource(R.drawable.order_paying);
        } else if (StringUtil.isEqual(orderModel.getStatus(), "10F") || StringUtil.isEqual(orderModel.getStatus(), "70A")) {
            ivOrderStatus.setImageResource(R.drawable.order_qx);
        } else {
            ivOrderStatus.setImageResource(R.drawable.order_payed);
        }

        if (orderModel.getId().length() >= 16) {
            tvOrderNo.setText("订单编号:" + orderModel.getId().substring(0, 16));
        } else {
            tvOrderNo.setText("订单编号:" + orderModel.getId());
        }
        tvOrderTime.setText("提交时间:" + DateUtil.formatDateToHMS(orderModel.getCreateTime().getTime()));
        tvMoney2.setText("应付:" + orderModel.getPay() + "元" + "+" + orderModel.getPoint() + "积分");


        tvWulName.setText("物流公司:" + orderModel.getPostName());
        tvWuliuId.setText("快递单号:" + orderModel.getPostNumber());
        String status = orderModel.getStatus();
        if (status.equals("10A")) {
            //等待付款
            btnPurchase.setText("立即支付");
        } else if (status.equals("10C")) {
            //等待发货
            tvMoney2.setVisibility(View.GONE);
            btnPurchase.setText("联系客服");
        } else if (status.equals("10D")) {
            //等待收货
            btnPurchase.setText("确认收货");
            rlWl.setVisibility(View.VISIBLE);
        } else if (status.equals("10E")) {
            //交易完成
            btnPurchase.setText("联系客服");
            rlWl.setVisibility(View.VISIBLE);
        } else if (status.equals("10F")) {
            //交易关闭
            btnPurchase.setText("删除订单");
        } else if (status.equals("70A")) {
            //支付失败
            tvMoney2.setVisibility(View.GONE);
            btnPurchase.setText("联系客服");
        } else if (status.equals("10B")) {
            tvMoney2.setVisibility(View.GONE);
            btnPurchase.setText("联系客服");
        }


        checkCountDown(orderModel.getCreateTime().getTime());
        initListener();
    }

    @OnClick({R.id.iv_back, R.id.btn_purchase, R.id.tv_dd_copy, R.id.tv_wl_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ViewUtils.overridePendingTransitionBack(context);
                break;
            case R.id.tv_dd_copy:
                copyString(orderModel.getId());
                break;
            case R.id.tv_wl_copy:
                copyString(orderModel.getPostNumber());
                break;
            case R.id.btn_purchase:
                // TODO: 2019/10/18 支付
                String string = btnPurchase.getText().toString();
                if (string.equals("立即支付")) {
                    shoDialog(orderModel.getPay() + "元" + "+" + orderModel.getPoint() + "积分", orderModel);
                } else if (string.equals("联系客服")) {
                    startActivity(new Intent(context, ServiceCenterActivity.class));
                } else if (string.equals("确认收货")) {
                    loadDataSH();
                } else if (string.equals("删除订单")) {
                    loadData();
                }
                break;
            default:
                break;
        }
    }

    private void loadDataSH() {
        final HttpParams map = OkClient.getParamsInstance().getParams();
        map.put("3", "590019");
        map.put("9", orderModel.getId());
        loadingDialog.show();
        OkClient.getInstance().post(map, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
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
                String result = model.getStr39();
                if ("00".equals(result)) {
                    ViewUtils.makeToast(context, "收货成功", 1200);
                    EventBus.getDefault().post(new OrderPayEvent());
                    finish();
                }
            }
        });
    }

    private void loadData() {
        final HttpParams map = OkClient.getParamsInstance().getParams();
        map.put("3", "790106");
        map.put("41", orderModel.getId());
        loadingDialog.show();
        OkClient.getInstance().post(map, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
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
                String result = model.getStr39();
                if ("00".equals(result)) {
                    ViewUtils.makeToast(context, "删除成功", 1200);
                    EventBus.getDefault().post(new OrderPayEvent());
                    finish();
                }
            }
        });
    }


    void copyString(String str) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", str);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ViewUtils.makeToast(context, "复制成功", 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    /**
     * 判断订单未付款，订单超过一个小时未付款，自动关闭订单
     *
     * @param createTime
     */
    private void checkCountDown(long createTime) {
        LogUtils.i("TimeUtils.string2Millis(createTime)=" + createTime);
        long pastTime = TimeUtils.getTimeSpan(System.currentTimeMillis(), createTime, TimeUtils.MSEC);
        long msc = TimeUtils.getTimeSpan(orderModel.getUpdateTime().getTime(), orderModel.getCreateTime().getTime(), TimeUtils.MSEC);
        if (pastTime >= msc) {
            //订单关闭
            shutDownCountDown();
        } else {
            long remainTime = msc - pastTime;
            customTvCountDown.start(remainTime);
        }
    }

    private void shutDownCountDown() {
        customTvCountDown.setVisibility(View.GONE);
        tvMoney2.setVisibility(View.GONE);
        llTime.setVisibility(View.GONE);
        btnPurchase.setText("联系客服");
    }

    private void initListener() {
        customTvCountDown.setTimerFinishListener(new CustomCountDownTimerView.TimerFinishListener() {
            @Override
            public void onTimerFinish() {
                shutDownCountDown();
                // TODO: 2019/10/23 订单关闭
                customTvCountDown.destroy();

            }
        });
    }


    private void shoDialog(String trim, final OrderModel orderModel) {
        VipBottomDialog intence = VipBottomDialog.getIntence(trim, "0", "0");
        intence.setStringCallback(new StringCallback() {
            @Override
            public void callback(String value) {
                loadPayData(orderModel, value);
            }
        });
        intence.show(getSupportFragmentManager(), "");
    }

    private void loadPayData(OrderModel orderModel, final String value) {
        // : 2019/4/30 金额
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "790104");
        httpParams.put("21", orderModel.getId());
        httpParams.put("22", getMerId());
        httpParams.put("23", value);
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
                    if (value.equals("z")) {
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
                    }
                    if (value.equals("w")) {
                        WeiXinModel wechatModel = JSONObject.parseObject(orderInfo, WeiXinModel.class);
                        WechatPay.getInstance().startWeChatPay(wechatModel.getAppid(), wechatModel.getPartnerid(), wechatModel.getPrepayid(), wechatModel.getNoncestr(), wechatModel.getTimestamp(), wechatModel.getSign(), new PayListener() {
                            @Override
                            public void onPaySuccess() {
                                ViewUtils.makeToast(context, "支付成功", 500);
                                paySuccess();
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


    private static final int SDK_PAY_FLAG = 1;
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
                        ViewUtils.makeToast(context, "支付成功", 1000);
                        paySuccess();
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

    private void paySuccess() {
        finish();
    }

}
