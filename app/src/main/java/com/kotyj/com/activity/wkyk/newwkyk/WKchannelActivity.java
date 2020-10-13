package com.kotyj.com.activity.wkyk.newwkyk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.activity.fun.X5WebViewActivity;
import com.kotyj.com.activity.wkyk.BindCardActivity;
import com.kotyj.com.activity.wkyk.ImageActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.RefreshCard;
import com.kotyj.com.event.SwipeCloseEvent;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.BindCard;
import com.kotyj.com.model.ChannelBean;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.LogUtils;
import com.kotyj.com.utils.StringUtil;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WKchannelActivity extends BaseActivity {
    private static final int REWUEST_FINISH = 0x20;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_right)
    TextView tvRight;
    private List<ChannelBean.Channel> mList = new ArrayList<>();
    private MyAdapter myAdapter;
    private BindCard mBindCard;
    private String payType;
    private String money;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public int initLayout() {
        return R.layout.act_ykchannel;
    }

    @Override
    public void initData() {
        tvTitle.setText("选择通道");
        payType = getIntent().getStringExtra("payType");
        money = getIntent().getStringExtra("money");

        mBindCard = (BindCard) getIntent().getSerializableExtra("BindCard");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        myAdapter = new MyAdapter(R.layout.item_yk_channel, mList);
        myAdapter.bindToRecyclerView(recyclerView);

        loadData();

        myAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.tv_remark:
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putExtra("title", mList.get(position).getChannelName());
                        intent.putExtra("url", "http://xinjuejia.llyzf.cn/image/acqCode/" + mList.get(position).getAcqcode() + ".png");
                        startActivity(intent);
                        break;
                    case R.id.bt_chose:
                        ChannelBean.Channel channel = mList.get(position);
                        requestBindStatus(channel);
                        break;
                }
            }
        });


        myAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ChannelBean.Channel channel = mList.get(position);
                requestBindStatus(channel);
            }
        });
    }


    //是否开通通道
    private void requestBindStatus(final ChannelBean.Channel channel) {
        if (channel.getStatus().equals("未开通")) {
            ViewUtils.makeToast(context, "未绑卡，请先绑卡", 500);
            Intent intent = new Intent(context, BindCardActivity.class);
            intent.putExtra("bindCard", mBindCard);
            intent.putExtra("type", channel.getAcqcode());
            startActivity(intent);
        } else {
            placeAnOrder(channel);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void loadData() {
        mList.clear();
        loadingDialog.show();
        HttpParams httpParams = OkClient.getParamsInstance().getParams();
        httpParams.put("3", "390013");
        httpParams.put("42", getMerNo());
        if (payType.equals("WK")) {
            httpParams.put("44", mBindCard.getID());
        }
        httpParams.put("43", payType);
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
                    mList.addAll(list);
                    myAdapter.setNewData(mList);
                }
            }
        });
    }


    private void placeAnOrder(ChannelBean.Channel channel) {
        loadingDialog.show();
        String moneyFen = CommonUtils.toFenNot00(money);
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "190959");
        httpParams.put("5", moneyFen);
        if (!StringUtil.isEmpty(channel.getAcqMerchantNo())) {
            httpParams.put("40", channel.getAcqMerchantNo());
        }
        if (payType.equals("WK")) {
            httpParams.put("41", mBindCard.getBANK_ACCOUNT());
            httpParams.put("45", mBindCard.getCvn());
            httpParams.put("46", mBindCard.getExpdate().replace("/", ""));
        }
        httpParams.put("42", getMerNo());
        httpParams.put("43", channel.getAcqcode());
        httpParams.put("44", channel.getRate());
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
                    String url = model.getStr30();
                    if ("00".equals(url)) {
                        ViewUtils.makeToast(context, "下单成功请稍等，交易是否成功以您信用卡的扣款为准", 1500);
                        setResult(Activity.RESULT_OK);
                        finish();
                        return;
                    }
                    Intent intent = new Intent(context, X5WebViewActivity.class);
                    intent.putExtra("title", "无卡支付");
                    intent.putExtra("url", url);
                    intent.putExtra("rightTitle", "首页");
                    intent.putExtra("back", true);
                    startActivityForResult(intent, REWUEST_FINISH);
                    ViewUtils.makeToast(context, "下单成功请稍等", 1500);
                    ViewUtils.overridePendingTransitionCome(context);

                }
            }

            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }


    class MyAdapter extends BaseQuickAdapter<ChannelBean.Channel, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<ChannelBean.Channel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ChannelBean.Channel item) {

            helper.addOnClickListener(R.id.bt_chose)
                    .addOnClickListener(R.id.tv_remark);

            helper.setText(R.id.tv_channelName, item.getChannelName())
                    .setText(R.id.tv_dealRate, item.getRate())
                    .setText(R.id.tv_limitPerTimes, "单笔限额：" + item.getLimit())
                    .setText(R.id.tv_dealTimes, "交易时间：" + item.getT0date())
                    .setText(R.id.item_content, item.getRemark())
                    .setText(R.id.bt_chose, item.getStatus().equals("未开通") ? "点击激活" : "已激活");


            helper.setTextColor(R.id.bt_chose, item.getStatus().equals("未开通") ? getResources().getColor(R.color.white) : getResources().getColor(R.color.theme_fontColor));

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REWUEST_FINISH && resultCode == RESULT_OK) {
            EventBus.getDefault().post(new SwipeCloseEvent());
            finish();
        }
    }


    @Subscribe
    public void onEvent(RefreshCard refreshCard) {
        loadData();
    }


}
