package com.kotyj.com.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kotyj.com.R;
import com.kotyj.com.adapter.QueryNewAdapter;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.QueryModel;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TradeListActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    private int pageIndex = 1;
    private List<QueryModel> mList = new ArrayList<>();
    private QueryNewAdapter mQueryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    public int initLayout() {
        return R.layout.act_trade;
    }

    @Override
    public void initData() {
        tvTitle.setText("交易明细");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(linearLayoutManager);
        mQueryAdapter = new QueryNewAdapter(mList);
        mQueryAdapter.bindToRecyclerView(rvList);
        mQueryAdapter.setEmptyView(R.layout.layout_bank_empty, rvList);
        initListener();
        loadData();
    }

    private void initListener() {
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                pageIndex = 1;
                mList.clear();
                loadData();
            }
        });
        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageIndex++;
                loadData();
            }
        });
        mQueryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(context, TradeRecordDetailActivity.class)
                        .putExtra("detail", mList.get(position)));
            }
        });
    }


    private void loadData() {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "990005");
        httpParams.put("42", getMerId());
        httpParams.put("40", pageIndex + "");
        httpParams.put("41", "10");
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
                smartRefreshLayout.finishLoadmore();
                smartRefreshLayout.finishRefresh();
            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                super.onSuccess(response);
                loadingDialog.dismiss();
                smartRefreshLayout.finishLoadmore();
                smartRefreshLayout.finishRefresh();
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }

                if ("00".equals(model.getStr39())) {

                    List<QueryModel> list = new Gson().fromJson(model.getStr57(), new TypeToken<List<QueryModel>>() {
                    }.getType());
                    mList.addAll(list);
                    mQueryAdapter.setNewData(mList);

                }
            }
        });
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        ViewUtils.overridePendingTransitionBack(context);
    }
}
