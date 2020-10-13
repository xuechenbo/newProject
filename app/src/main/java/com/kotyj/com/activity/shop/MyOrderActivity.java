package com.kotyj.com.activity.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kotyj.com.R;
import com.kotyj.com.activity.mine.ServiceCenterActivity;
import com.kotyj.com.adapter.MyOrderAdapter;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.event.OrderPayEvent;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.OrderModel;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyOrderActivity extends BaseActivity {

    private static final String TAG = "MyOrderActivity";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.id_img)
    ImageView idImg;
    @BindView(R.id.relative_defaultx)
    RelativeLayout relativeDefaultx;
    @BindView(R.id.srl_refresh)
    SmartRefreshLayout srlRefresh;

    private List<OrderModel> mList = new ArrayList<>();
    private MyOrderAdapter orderAdapter;
    private Integer pageIndex = 1;

    @OnClick(R.id.iv_back)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

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
        return R.layout.act_my_order;
    }

    @Override
    public void initData() {
        tvTitle.setText("我的订单");
        EventBus.getDefault().register(this);
        rvList.setLayoutManager(new LinearLayoutManager(context));
        orderAdapter = new MyOrderAdapter(mList, context);
        orderAdapter.bindToRecyclerView(rvList);
        srlRefresh.setRefreshHeader(new ClassicsHeader(context));
        loadData();
        initListener();
    }

    private void initListener() {
        orderAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.my_service:
                        OrderModel orderModel = mList.get(position);
                        if (orderModel.getStatus().equals("10A")) {
                            Intent intent = new Intent(context, OrderDetailActivity.class);
                            intent.putExtra("orderId", mList.get(position).getId());
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(context, ServiceCenterActivity.class));
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        orderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("orderId", mList.get(position).getId());
                startActivity(intent);
            }
        });


        srlRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageIndex++;
                loadData();
            }
        });
        srlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshData();
            }
        });
    }

    private void refreshData() {
        mList.clear();
        pageIndex = 1;
        loadData();
    }

    /**
     * 获取订单
     */
    private void loadData() {
        final HttpParams map = OkClient.getParamsInstance().getParams();
        map.put("3", "790105");
        map.put("21", getMerId());
        map.put("22", pageIndex + "");
        map.put("23", "10");
        loadingDialog.show();
        OkClient.getInstance().post(map, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                Log.i(TAG, "onError" + response.toString());
                loadingDialog.dismiss();
                srlRefresh.finishRefresh();
                srlRefresh.finishLoadmore();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                super.onSuccess(response);
                loadingDialog.dismiss();
                srlRefresh.finishRefresh();
                srlRefresh.finishLoadmore();
                BaseEntity model = response.body();
                Log.i(TAG, "onSuccess" + model.toString());
                String result = model.getStr39();
                if ("00".equals(result)) {
                    List<OrderModel> list = JSONArray.parseArray(model.getStr40(), OrderModel.class);
                    if (list != null) {
                        mList.addAll(list);
                        orderAdapter.setNewData(mList);
                    }

                }
            }
        });
    }

    @Subscribe
    public void onEvent(OrderPayEvent orderPayEvent) {
        refreshData();
    }


}
