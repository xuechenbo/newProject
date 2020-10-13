package com.kotyj.com.activity.wkyk.newwkyk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kotyj.com.R;
import com.kotyj.com.activity.wkyk.BindCardActivity;
import com.kotyj.com.activity.wkyk.ImageActivity;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.PlanCloseEvent;
import com.kotyj.com.event.RefreshCard;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.BindCard;
import com.kotyj.com.model.ChannelBean;
import com.kotyj.com.utils.LogUtils;
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

public class YKchannelActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.bt_sup)
    Button btSup;
    private boolean isQYK;
    private List<ChannelBean.Channel> mList = new ArrayList<>();
    //TODO 多通道通道号和名称
    ChannelBean.Channel mSelChannel = new ChannelBean.Channel();
    private MyAdapter myAdapter;
    private BindCard mBindCard;
    private boolean isChannels;


    @Override
    public int initLayout() {
        return R.layout.act_ykchannel;
    }

    @Override
    public void initData() {
        tvTitle.setText("选择通道");
        isChannels = getIntent().getBooleanExtra("isChannels", false);
        isQYK = getIntent().getBooleanExtra("isQYK", false);
        mBindCard = (BindCard) getIntent().getSerializableExtra("bindCard");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        myAdapter = new MyAdapter(R.layout.item_yk_channel, mList);
        myAdapter.bindToRecyclerView(recyclerView);
        btSup.setVisibility(isChannels ? View.VISIBLE : View.GONE);
        initListener();
        loadData();

    }

    private void initListener() {
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
                if (isChannels) {
                    if (mList.get(position).getStatus().equals("未开通")) {
                        ViewUtils.makeToast(context, "未绑卡，请先绑卡", 500);
                        ChannelBean.Channel channel = mList.get(position);
                        Intent intent = new Intent(context, BindCardActivity.class);
                        intent.putExtra("bindCard", mBindCard);
                        intent.putExtra("type", channel.getAcqcode());
                        startActivity(intent);
                    } else {
                        mList.get(position).setCheck(!mList.get(position).isCheck());
                        myAdapter.notifyDataSetChanged();
                    }
                } else {
                    ChannelBean.Channel channel = mList.get(position);
                    requestBindStatus(channel);
                }

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
            gotoMakeDesignActivity(channel);
        }
    }


    public void gotoMakeDesignActivity(final ChannelBean.Channel channel) {
        Intent intent = new Intent(context, MakeDesignActivity.class);
        intent.putExtra("isQYK", isQYK);
        intent.putExtra("bindCard", mBindCard);
        intent.putExtra("isChannels", isChannels);
        intent.putExtra("channel", channel);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
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
        httpParams.put("3", isChannels ? "390014" : "390013");
        httpParams.put("42", mBindCard.getMERCHANT_NO());
        httpParams.put("43", isQYK ? "QYK" : "YK");
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
                    mList.addAll(list);
                    myAdapter.setNewData(mList);
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_right, R.id.bt_sup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_sup:
                StringBuilder channelNameBuilder = new StringBuilder();
                StringBuilder acqCodeBuilder = new StringBuilder();
                for (ChannelBean.Channel channel : mList) {
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
                gotoMakeDesignActivity(mSelChannel);
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
                    .setText(R.id.bt_chose, item.getStatus().equals("未开通") ? "点击激活" : "已激活")
                    .setTextColor(R.id.bt_chose, item.getStatus().equals("未开通") ? getResources().getColor(R.color.white) : getResources().getColor(R.color.theme_fontColor));
            if (isChannels) {
                helper.setGone(R.id.bt_chose, item.getStatus().equals("未开通") ? true : false)
                        .setGone(R.id.iv_check, item.getStatus().equals("未开通") ? false : true);
                helper.setImageResource(R.id.iv_check, item.isCheck() ? R.drawable.check_select : R.drawable.check_unselect);
            }

        }
    }


    @Subscribe
    public void onEvent(RefreshCard refreshCard) {
        loadData();
    }

    @Subscribe
    public void onEvent(PlanCloseEvent event) {
        ViewUtils.overridePendingTransitionBack(context);
    }
}
