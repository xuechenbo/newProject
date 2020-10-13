package com.kotyj.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kotyj.com.R;
import com.kotyj.com.activity.shop.AddressListActivity;
import com.kotyj.com.activity.shop.ItemDetailActivity;
import com.kotyj.com.activity.shop.MyOrderActivity;
import com.kotyj.com.adapter.ShoppingMalAdapter;
import com.kotyj.com.base.BaseFragment;
import com.kotyj.com.model.BaseEntity;
import com.kotyj.com.model.ShoppingMalModel;
import com.kotyj.com.utils.CommonUtils;
import com.kotyj.com.utils.FastJsonUtils;
import com.kotyj.com.utils.ViewUtils;
import com.kotyj.com.utils.okgo.OkClient;
import com.kotyj.com.viewone.GridDivideItemDecoration;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShopFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.srl_refresh)
    SmartRefreshLayout srlRefresh;
    Unbinder unbinder;

    private List<ShoppingMalModel> mList = new ArrayList<>();
    private ShoppingMalAdapter adapter;
    private Integer pageIndex = 1;

    public static ShopFragment newInstance() {
        return new ShopFragment();
    }

    @Override
    public int initLayout() {
        return R.layout.fragment_shop;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initData(View v) {
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        recyclerView.addItemDecoration(new GridDivideItemDecoration(context, CommonUtils.dp2px(context, 10), context.getResources().getColor(R.color.themeColor1)));
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ShoppingMalAdapter(mList);
        adapter.bindToRecyclerView(recyclerView);
        srlRefresh.setRefreshHeader(new ClassicsHeader(context));
        srlRefresh.setRefreshFooter(new ClassicsFooter(context));

        initListener();
        loadData();
    }

    @OnClick({R.id.tv_order, R.id.tv_address_manager})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_order:
                startActivity(new Intent(context, MyOrderActivity.class));
                break;
            case R.id.tv_address_manager:
                startActivity(new Intent(context, AddressListActivity.class));
                break;
        }
    }

    private void initListener() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                goItemDetail(position);
            }
        });
        srlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshData();
            }
        });


        srlRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                pageIndex++;
                loadData();
            }
        });

    }

    private void refreshData() {
        pageIndex = 1;
        mList.clear();
        loadData();
    }

    private void goItemDetail(int position) {
        Intent intent = new Intent(context, ItemDetailActivity.class);
        intent.putExtra("item", mList.get(position));
        startActivity(intent);
    }



    private void loadData() {
        loadingDialog.show();
        HttpParams map = OkClient.getParamsInstance().getParams();
        map.put("3", "790101");
        map.put("22", pageIndex + "");
        map.put("23", "10");
        loadingDialog.show();
        OkClient.getInstance().post(map, new OkClient.EntityCallBack<BaseEntity>(context, BaseEntity.class) {
            @Override
            public void onError(Response<BaseEntity> response) {
                super.onError(response);
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
                String result = model.getStr39();
                if ("00".equals(result)) {
                    List<ShoppingMalModel> list = FastJsonUtils.toList(model.getStr41(), ShoppingMalModel.class);
                    mList.addAll(list);
                    adapter.setNewData(mList);
                } else {
                    ViewUtils.makeToast(context, result, 500);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
