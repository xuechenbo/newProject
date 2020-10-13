package com.kotyj.com.activity.shop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.callback.StringCallback;
import com.kotyj.com.event.AddressEvent;
import com.kotyj.com.event.OrderPayEvent;
import com.kotyj.com.model.AddressModel;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.ItemModel;
import com.kotyj.com.model.OrderModel;
import com.kotyj.com.model.PayResult;
import com.kotyj.com.model.WeiXinModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.LogUtil;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.Utils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.utils.wechat.PayListener;
import com.kotyj.com.utils.wechat.WechatPay;
import com.kotyj.com.viewone.dialog.VipBottomDialog;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderConfirmActivity extends BaseActivity {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.iv_right_array)
    ImageView ivRightArray;
    @BindView(R.id.cl_address)
    ConstraintLayout clAddress;
    @BindView(R.id.rl_address_add)
    RelativeLayout rlAddressAdd;
    @BindView(R.id.iv_product)
    ImageView ivProduct;
    @BindView(R.id.tv_product_name)
    TextView tvProductName;
    @BindView(R.id.tv_specification)
    TextView tvSpecification;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_stock)
    TextView tvStock;
    @BindView(R.id.tv_remark)
    EditText tvRemark;
    @BindView(R.id.tv_money_2)
    TextView tvMoney2;
    @BindView(R.id.btn_purchase)
    Button btnPurchase;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    private ItemModel mItemModel;
    private AddressModel mAddressModel;
    private String addressId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int initLayout() {
        return R.layout.act_order_confirm;
    }

    @Override
    public void initData() {
        tvTitle.setText("订单支付");
        EventBus.getDefault().register(this);
        mItemModel = (ItemModel) getIntent().getSerializableExtra("goods");
        GlideUtils.loadImage(context, mItemModel.getImage(), ivProduct);
        tvProductName.setText(mItemModel.getName());


        tvPrice.setText(Utils.getShopPayType(mItemModel.getPrice() + "", mItemModel.getPoint() + ""));

        tvSpecification.setText("规格：" + mItemModel.getSize());
        tvStock.setText("x" + mItemModel.getGoodsCount());

        tvMoney2.setText(Utils.getShopTotalMoney(mItemModel));

        requestData();
    }

    /**
     * 收货地址列表 获取默认地址
     */
    private void requestData() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "590012");
        httpParams.put("9", getMerId());
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
                    clAddress.setVisibility(View.VISIBLE);
                    rlAddressAdd.setVisibility(View.GONE);
                    String addressListStr = model.getStr40();
                    List<AddressModel> modelList = JSONArray.parseArray(addressListStr, AddressModel.class);
                    for (AddressModel address : modelList) {
                        if ("1".equals(address.getStatus())) {
                            mAddressModel = address;
                            updateData();
                        }
                    }
                    if (mAddressModel == null && modelList.size() > 0) {
                        mAddressModel = modelList.get(0);
                        updateData();
                    }
                } else if (!StringUtil.isEmpty(model.getStr39()) && model.getStr39().contains("暂无地址")) {
                    clAddress.setVisibility(View.GONE);
                    rlAddressAdd.setVisibility(View.VISIBLE);


                }
            }
        });
    }

    private void updateData() {
        if (mAddressModel == null) {
            return;
        }
        tvName.setText(mAddressModel.getName());
        tvPhone.setText(CommonUtils.translateShortNumber(mAddressModel.getPhone(), 3, 4));
        tvAddress.setText(mAddressModel.getAddress());
        addressId = mAddressModel.getId();
    }

    @Subscribe
    public void onEvent(AddressEvent addressEvent) {
        requestData();
    }

    @OnClick({R.id.iv_back, R.id.cl_address, R.id.btn_purchase, R.id.rl_address_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ViewUtils.overridePendingTransitionBack(context);
                break;
            case R.id.cl_address:
                startActivityForResult(new Intent(context, AddressListActivity.class).putExtra("formOrder", true), 1);
                break;
            case R.id.btn_purchase:
                if (StringUtil.isEmpty(addressId)) {
                    ViewUtils.makeToast(context, "请选择收货地址", 1000);
                    return;
                }
                generateOrders();

                break;
            case R.id.rl_address_add:
                startActivity(new Intent(context, AddressAddActivity.class));
                break;
        }
    }

    /**
     * 生成订单
     */
    private void generateOrders() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "790103");
        httpParams.put("6", getMerId());
        httpParams.put("7", mItemModel.getId());
        httpParams.put("9", addressId);
        httpParams.put("10", mItemModel.getSize());
        httpParams.put("11", mItemModel.getGoodsCount());
        httpParams.put("12", tvRemark.getText().toString());

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

                    if (new BigDecimal(mItemModel.getPrice()).compareTo(BigDecimal.ZERO) == 0) {
                        startActivity(new Intent(context, MyOrderActivity.class));
                        finish();
                    } else {
                        OrderModel orderModel = JSONObject.parseObject(model.getStr57(), OrderModel.class);
                        shoDialog(tvMoney2.getText().toString().trim(), orderModel);
                    }
                }
            }

            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mAddressModel = (AddressModel) data.getSerializableExtra("data");
            updateData();
        }
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
        EventBus.getDefault().post(new OrderPayEvent());
        finish();
    }


}
