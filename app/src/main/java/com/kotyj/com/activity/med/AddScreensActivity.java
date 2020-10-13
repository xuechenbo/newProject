package com.kotyj.com.activity.med;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.kotyj.com.R;
import com.kotyj.com.activity.other.QrCodePayActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.PayResult;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.LogUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;

import java.math.BigDecimal;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddScreensActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_num)
    EditText etNum;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.zhifubao_choose)
    ImageView zhifubaoChoose;
    @BindView(R.id.wechat_choose)
    ImageView wechatChoose;
    private String singCardMoney;
    private String type = "z";
    private String totalMoney;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.act_addscreens;
    }

    @Override
    public void initData() {
        singCardMoney = StorageCustomerInfo02Util.getInfo("SingCardMoney", context);
        tvTitle.setText("增加卡位");

        initListener();
    }

    private void initListener() {
        etNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e("afterTextChanged", editable.toString());
                String stringNum = editable.toString();
                if (stringNum.equals("0")) {
                    etNum.setText("");
                }
                if (stringNum.isEmpty()) {
                    tvMoney.setText("0元");
                } else {
                    totalMoney = new BigDecimal(singCardMoney).multiply(new BigDecimal(stringNum)).setScale(2).toString();
                    Log.e("sssss", totalMoney);
                    tvMoney.setText(totalMoney + "元");
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.ll_alipay, R.id.ll_weixin, R.id.bt_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_alipay:
                type = "z";
                zhifubaoChoose.setImageResource(R.drawable.check_circle_sel);
                wechatChoose.setImageResource(R.drawable.check_circle);
                break;
            case R.id.ll_weixin:
                type = "w";
                wechatChoose.setImageResource(R.drawable.check_circle_sel);
                zhifubaoChoose.setImageResource(R.drawable.check_circle);
                break;
            case R.id.bt_submit:
                if (etNum.getText().toString().trim().isEmpty()) {
                    ViewUtils.makeToast(context, "请输入数量", 1200);
                    return;
                }
                //gotuCode(type);
                loadPayData();
                break;
        }
    }

    private void loadPayData() {
        // : 2019/4/30 金额
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "890001");
        httpParams.put("5", CommonUtils.formatNewFen(totalMoney));
        httpParams.put("8", type);
        httpParams.put("1", etNum.getText().toString().trim());
        httpParams.put("41", "CP");
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
                    if (isAlipay()) {
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
                    if (isWeixin()) {
                        ViewUtils.makeToast(context, "暂未开放", 1200);
//                        WeiXinModel wechatModel = JSONObject.parseObject(orderInfo, WeiXinModel.class);
//                        WechatPay.getInstance().startWeChatPay(wechatModel.getAppid(), wechatModel.getPartnerid(), wechatModel.getPrepayid(), wechatModel.getNoncestr(), wechatModel.getTimestamp(), wechatModel.getSign(), new PayListener() {
//                            @Override
//                            public void onPaySuccess() {
//                                ViewUtils.makeToast(context, "支付成功", 500);
//                            }
//
//                            @Override
//                            public void onPayError(String resultStatus) {
//                                ViewUtils.makeToast(context, "支付失败" + "\n" + resultStatus, 1000);
//                            }
//
//                            @Override
//                            public void onPayCancel() {
//                                ViewUtils.makeToast(context, "支付取消", 1000);
//                            }
//                        });
                    }
                }
            }
        });
    }

    private boolean isAlipay() {
        return "z".equals(type);
    }

    private boolean isWeixin() {
        return "w".equals(type);
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


    void gotuCode(String type) {
        startActivity(new Intent(context, QrCodePayActivity.class)
                .putExtra("paytype", type)
                .putExtra("money", totalMoney)
                .putExtra("level", "5")
                .putExtra("title", type.equals("z") ? "支付宝扫码支付" : "微信扫码支付")
                .putExtra("type", "S"));
    }
}
