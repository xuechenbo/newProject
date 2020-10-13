package com.kotyj.com.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kotyj.com.R;
import com.kotyj.com.base.BaseActivity;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.WithdrawYueModel;
import com.kotyj.com.utils.DateUtil;
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

public class WithdrawYueListActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    private List<WithdrawYueModel> mList = new ArrayList<>();
    private MyAdapter myAdapter;
    private int page = 1;

    @Override
    public int initLayout() {
        return R.layout.general_mlist;
    }

    @Override
    public void initData() {

        tvTitle.setText("转入记录");
        myAdapter = new MyAdapter(R.layout.item_withdraw_list, mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, OrientationHelper.VERTICAL));
        myAdapter.bindToRecyclerView(recyclerView);
        myAdapter.setEmptyView(R.layout.layout_bank_empty, recyclerView);

        initListener();

        loadData(page);

    }

    private void initListener() {
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                mList.clear();
                loadData(page);
            }
        });
        smartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                loadData(page);
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


    class MyAdapter extends BaseQuickAdapter<WithdrawYueModel, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<WithdrawYueModel> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, WithdrawYueModel item) {
            helper.setGone(R.id.tv_status, false)
                    .setText(R.id.tv_date_start, DateUtil.formatDateToHM_points_s(item.getCreate_time().getTime()))
                    .setText(R.id.tv_money, "-" + item.getMoney());


        }
    }

    private void loadData(int page) {
        loadingDialog.show();
        HttpParams httpParams = new HttpParams();
        httpParams.put("3", "990000");
        httpParams.put("42", getMerId());
        httpParams.put("40", page + "");
        httpParams.put("41", "10");
        OkClient.getInstance().post(httpParams, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
                loadingDialog.dismiss();
                smartRefreshLayout.finishRefresh();
                smartRefreshLayout.finishLoadmore();

            }

            @Override
            public void onSuccess(Response<BaseEntity> response) {
                super.onSuccess(response);
                loadingDialog.dismiss();
                smartRefreshLayout.finishRefresh();
                smartRefreshLayout.finishLoadmore();
                BaseEntity model = response.body();
                if (model == null) {
                    return;
                }

                if ("00".equals(model.getStr39())) {
                    if (model.getStr57().isEmpty() || model.getStr57().equals("null")) {
                        return;
                    }
                    List<WithdrawYueModel> list = new Gson().fromJson(model.getStr57(), new TypeToken<List<WithdrawYueModel>>() {
                    }.getType());
                    mList.addAll(list);
                    myAdapter.setNewData(mList);
                }
            }
        });
    }
}
