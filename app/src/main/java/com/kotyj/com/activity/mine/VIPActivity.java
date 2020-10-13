package com.kotyj.com.activity.mine;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.activity.login.LoginNewActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.callback.StringCallback;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.PayResult;
import com.kotyj.com.model.VipModel;
import com.kotyj.com.model.WeiXinModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.GlideUtils;
import com.kotyj.com.utils.LogUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.utils.wechat.PayListener;
import com.kotyj.com.utils.wechat.WechatPay;
import com.kotyj.com.viewone.dialog.VipBottomDialog;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VIPActivity extends BaseActivity {
    private static final int SDK_PAY_FLAG = 1;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private String level;
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
    private List<VipModel> stringList = new ArrayList<>();
    private MyAdapter myAdapter;


    @Override
    public int initLayout() {
        return R.layout.fragment_new_vip;
    }

    @Override
    public void initData() {
        tvTitle.setText("会员升级");
        level = StorageCustomerInfo02Util.getInfo("level", context);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        myAdapter = new MyAdapter(R.layout.item_vip, stringList);
        myAdapter.bindToRecyclerView(recyclerView);
        initListener();
        loadHeaderData();

    }

    private void initListener() {
        myAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VipModel vipModel = stringList.get(position);
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        gotoUp(vipModel);
                        break;
                    case 2:
                        gotoUp(vipModel);
                        break;
                }
            }
        });
    }

    void gotoUp(VipModel vipModel) {
        if (Integer.parseInt(vipModel.getLevel()) <= Integer.parseInt(level)) {
            ViewUtils.makeToast(context, "您已升级", 500);
            return;
        }
        showVipDialog(vipModel.getLevel(), vipModel.getPrice());
    }


    private void loadHeaderData() {
        HttpParams httpParams = OkClient.getParamsInstance().getParams();
        httpParams.put("3", "390006");
        httpParams.put("42", getMerId());
        httpParams.put("43", "10F");
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onSuccess(Response<BaseEntity> response) {
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    stringList = JSONArray.parseArray(model.getStr57(), VipModel.class);
                    myAdapter.setNewData(stringList);
                }
            }
        });
    }

    private void showVipDialog(final String level, final String money) {

        VipBottomDialog intence = VipBottomDialog.getIntence(money, "M", level);
        intence.setStringCallback(new StringCallback() {
            @Override
            public void callback(String value) {
                loadPayData(value, level, CommonUtils.formatNewFen(money));

            }
        });
        intence.show(getSupportFragmentManager(), "");

    }

    private void loadPayData(final String type, String level, String money) {
        // : 2019/4/30 金额
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "890001");
        httpParams.put("5", money);
        httpParams.put("8", type);
        httpParams.put("43", level);
        httpParams.put("41", "M");
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
                        WeiXinModel wechatModel = JSONObject.parseObject(orderInfo, WeiXinModel.class);
                        WechatPay.getInstance().startWeChatPay(wechatModel.getAppid(), wechatModel.getPartnerid(), wechatModel.getPrepayid(), wechatModel.getNoncestr(), wechatModel.getTimestamp(), wechatModel.getSign(), new PayListener() {
                            @Override
                            public void onPaySuccess() {
                                ViewUtils.makeToast2(context, "支付成功,请重新登录", 500, LoginNewActivity.class, "PAY");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }


    class MyAdapter extends BaseQuickAdapter<VipModel, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<VipModel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, VipModel item) {
            GlideUtils.loadImage(context, item.getImage(), (ImageView) helper.getView(R.id.image));
        }
    }
}
