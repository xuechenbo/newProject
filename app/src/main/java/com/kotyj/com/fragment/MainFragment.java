package com.kotyj.com.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.kotyj.com.R;
import com.kotyj.com.activity.login.LoginNewActivity;
import com.kotyj.com.activity.main.FriendListActivity;
import com.kotyj.com.activity.main.NewbieGuideActivity;
import com.kotyj.com.activity.main.NoticeListActivity;
import com.kotyj.com.activity.med.MediationCustomerActivity;
import com.kotyj.com.activity.mine.ServiceCenterActivity;
import com.kotyj.com.activity.wkyk.newwkyk.BankCardListActivity;
import com.kotyj.com.activity.wkyk.newwkyk.SwipeCardActivity;
import com.kotyj.com.base.BaseFragment;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.ImageScrollModel;
import com.kotyj.com.model.NoticeModel;
import com.kotyj.com.model.PayResult;
import com.kotyj.com.model.WeiXinModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.LogUtil;
import com.kotyj.com.utils.StorageCustomerInfo02Util;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.Utils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.utils.wechat.PayListener;
import com.kotyj.com.utils.wechat.WechatPay;
import com.kotyj.com.viewone.GlideImageLoader;
import com.kotlin.com.wkyk.web.AgentWebActivity;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.tv_notice)
    TextView tvNotice;

    private List<String> imageViews = new ArrayList<>();
    private List<ImageScrollModel> list = new ArrayList<>();

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int initLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void initData(View v) {
        initListener();
        loadImageData();
        loadNotice("");
    }

    private void initListener() {
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (!StringUtil.isEmpty(list.get(position).getOrderPaymentId().trim()) && list.get(position).getOrderPaymentId().startsWith("http")) {
                    goWebView(list.get(position).getOrderPaymentId(), "详情");
                }
            }
        });
        String planMsg = StorageCustomerInfo02Util.getInfo("planMsg", context);
        String ptrMsg = StorageCustomerInfo02Util.getInfo("PTRMsg", context);

        if (!ptrMsg.isEmpty() && !ptrMsg.equals("null")) {
            NoticeModel noticeModel = Utils.getNoticeModel(ptrMsg);
            if (noticeModel.getHasRead() == 1) {
                return;
            }
            ViewUtils.showNoticeDetail(context, noticeModel, new ViewUtils.OnshowNoticeListener() {
                @Override
                public void clickOk(NoticeModel noticeModel) {
                    loadNotice(noticeModel.getId());
                }

                @Override
                public void clickCancel() {

                }
            }).show();
        }
        //系统
        if (!planMsg.isEmpty() && !planMsg.equals("null")) {
            NoticeModel noticeModel = Utils.getNoticeModel(planMsg);
            if (noticeModel.getHasRead() == 1) {
                return;
            }

            ViewUtils.showNoticeDetail(context, noticeModel, new ViewUtils.OnshowNoticeListener() {
                @Override
                public void clickOk(NoticeModel noticeModel) {
                    loadNotice(noticeModel.getId());
                }

                @Override
                public void clickCancel() {

                }
            }).show();
        }


    }

    @OnClick({R.id.ll_notice, R.id.tv_hk, R.id.tv_sk, R.id.tv_kf, R.id.tv_sqxyk, R.id.tv_nfc, R.id.tv_slsk, R.id.tv_hbsk, R.id.tv_jfdh, R.id.tv_zjhk, R.id.iv_xszn, R.id.iv_sczx})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_notice:
                startActivity(new Intent(context, NoticeListActivity.class));
                break;
            case R.id.tv_hk:
                if (!checkCustomerInfoCompleteShowToast()) {
                    return;
                }
                gotoBankList();
                break;
            case R.id.tv_sk:
                if (!checkCustomerInfoCompleteShowToast()) {
                    return;
                }
                startActivity(new Intent(context, SwipeCardActivity.class)
                        .putExtra("payType", "WK")
                        .putExtra("title", "收款"));
                break;
            case R.id.tv_sqxyk:
                loadUrlData("2", "申卡");
                break;
            case R.id.tv_jfdh:
//                loadUrl();
                ViewUtils.makeToast(context, "暂未开放", 1200);

                break;
            case R.id.tv_nfc:
                ViewUtils.makeToast(context, "暂未开放", 1200);
                break;
            case R.id.tv_slsk:
                ViewUtils.makeToast(context, "暂未开放", 1200);
                break;
            case R.id.tv_hbsk:
                if (!checkCustomerInfoCompleteShowToast()) {
                    return;
                }
                startActivity(new Intent(context, SwipeCardActivity.class)
                        .putExtra("payType", "HB")
                        .putExtra("title", "花呗收款"));
                break;
            case R.id.tv_zjhk:
                ViewUtils.makeToast(context, "暂未开放", 1200);


//                String isIntermediary = StorageCustomerInfo02Util.getInfo("isIntermediary", context);
//                if (isIntermediary.equals("1")) {
//                    startActivity(new Intent(context, MediationCustomerActivity.class));
//                } else {
//                    final String medMoney = StorageCustomerInfo02Util.getInfo("medMoney", context);
//                    ViewUtils.showDialogStandard3(context, "付费" + medMoney + "元即可开通中介功能", "支付宝支付", "微信支付", new ViewUtils.OnshowDialogStandard() {
//                        @Override
//                        public void clickOk() {
//                            loadPayData("w", "1", CommonUtils.formatNewFen(medMoney));
//                        }
//
//                        @Override
//                        public void clickCancel() {
//                            loadPayData("z", "1", CommonUtils.formatNewFen(medMoney));
//                        }
//                    }).show();
//                }

                break;
            case R.id.iv_xszn:
                startActivity(new Intent(context, NewbieGuideActivity.class));
                break;
            case R.id.iv_sczx:
                startActivity(new Intent(context, FriendListActivity.class));
                break;
            case R.id.tv_kf:
                startActivity(new Intent(context, ServiceCenterActivity.class));
                break;
        }
    }


    private void goWebView(String url, String title) {
        Intent webIntent = new Intent(context, AgentWebActivity.class);
        webIntent.putExtra("title", title);
        webIntent.putExtra("url", url);
        startActivity(webIntent);
    }


    void gotoBankList() {
        Intent intent = new Intent();
        intent.setClass(context, BankCardListActivity.class);
        intent.putExtra("title", "还款");
        startActivity(intent);
    }

    private void loadNotice(final String id) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "190103");
        httpParams.put("42", getMerNo());
        httpParams.put("43", "10A");
        httpParams.put("44", "1");
        if (!id.isEmpty()) {
            httpParams.put("21", id);
        }
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    if (!id.isEmpty()) {
                        return;
                    }
                    if (StringUtil.isEmpty(model.getStr60())) {
                        return;
                    }
                    if (model.getStr60().isEmpty()) {
                        return;
                    }
                    List<NoticeModel> list = JSONArray.parseArray(model.getStr60(), NoticeModel.class);
                    if (list != null && list.size() > 0) {
                        NoticeModel model1 = list.get(0);
                        tvNotice.setSelected(true);
                        tvNotice.setText(model1.getTitle());
                    }
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (banner != null) {
                banner.startAutoPlay();
            }
            loadNotice("");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (banner != null) {
            banner.stopAutoPlay();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void loadImageData() {
        HttpParams httpParams = OkClient.getParamsInstance().getParams();
        httpParams.put("3", "190108");
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {

            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }
                if ("00".equals(model.getStr39())) {
                    if (StringUtil.isEmpty(model.getStr57())) {
                        return;
                    }
                    list = JSONArray.parseArray(model.getStr57(), ImageScrollModel.class);
                    for (ImageScrollModel imageModel : list) {
                        imageViews.add(imageModel.getSingleNo());
                    }
                    banner.setImageLoader(new GlideImageLoader());
                    banner.setImages(imageViews);
                    banner.setDelayTime(4000);
                    banner.start();
                }
            }
        });
    }


    /**
     * 获取链接
     */
    private void loadUrlData(final String type, final String title) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "898907");
        httpParams.put("41", type);
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
                    goWebView(model.getStr38(), title);
                }
            }
        });
    }

    private void loadUrl() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "990010");
        httpParams.put("42", getMerId());
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
                    String str57 = model.getStr57();
                    goWebView(str57, "积分兑换");
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

    //开通中介
    private void loadPayData(final String type, String level, String money) {
        // : 2019/4/30 金额
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "890001");
        httpParams.put("5", money);
        httpParams.put("8", type);
        httpParams.put("43", level);
        httpParams.put("41", "I");
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

    private static final int SDK_PAY_FLAG = 1;

}
